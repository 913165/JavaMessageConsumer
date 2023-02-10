package org.example;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class LongRunningRabbitMQConsumer {
    public static void main1(String[] args) throws IOException, TimeoutException {

        Map<String, String> env = System.getenv();
        String rabbitmqHost = env.get("RABBITMQ_HOST");
        System.out.println("RabbitMQ Host: " + rabbitmqHost);

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(rabbitmqHost);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        String queueName = "long-running-worker-queue";
        channel.queueDeclare(queueName, false, false, false, null);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + message + "'");

            try {
                // simulate a long-running worker
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println(" [x] Done");
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }
        };

        channel.basicConsume(queueName, false, deliverCallback, consumerTag -> {});
    }
}
