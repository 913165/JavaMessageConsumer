package org.example;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class JavaMessageConsumer {

    private final static String QUEUE_NAME = "queue001";
    private final static String HOST_ADDR="20.228.126.239";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        System.out.println("HOST ADDR IS : "+HOST_ADDR);
        factory.setHost(HOST_ADDR);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        System.out.println("connected to host "+HOST_ADDR);
        Map<String, Object> args = new HashMap<String, Object>();
        channel.queueDeclare(QUEUE_NAME, false, false, false, args);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
        while (true) {
            GetResponse response = channel.basicGet(QUEUE_NAME, false);
            if (response == null) {
                TimeUnit.SECONDS.sleep(10);
                System.out.println("No message in queue");
            } else {
                TimeUnit.SECONDS.sleep(1);
                byte[] body = response.getBody();
                String message = new String(body);
                System.out.println("Received message: " + message);
                long deliveryTag = response.getEnvelope().getDeliveryTag();
                channel.basicAck(deliveryTag, false);
            }
        }
    }
}