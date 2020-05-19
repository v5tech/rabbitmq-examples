package learning.rabbitmq.topic;

import com.rabbitmq.client.*;
import learning.rabbitmq.utils.ConnectionUtils;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Slf4j
public class Receive {

    private static final String EXCHANGE_NAME = "topic_queue";

    private static String[] bindingKeys;

    public static void main(String[] args) throws Exception {
        // 获取连接
        Connection connection = ConnectionUtils.getConnection();
        // 创建通道
        Channel channel = connection.createChannel();

        // 声明一个topic交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        // 声明一个临时队列
        String queueName = channel.queueDeclare().getQueue();

        // 绑定路由，同一个队列可以绑定多个值
        for (String bindingKey : bindingKeys) {
            channel.queueBind(queueName, EXCHANGE_NAME, bindingKey);
        }

        log.info(" [TopicsRecv " + Arrays.toString(bindingKeys) + "] Waiting for messages.");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                String message = new String(body, StandardCharsets.UTF_8);
                log.info(" [TopicsRecv " + Arrays.toString(bindingKeys) + " ] Received '" + envelope.getRoutingKey() + "':'" + message + "'");
            }
        };

        // 接收消息
        channel.basicConsume(queueName, true, consumer);
    }
}