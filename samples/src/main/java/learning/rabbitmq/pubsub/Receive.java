package learning.rabbitmq.pubsub;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import learning.rabbitmq.utils.ConnectionUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * 消费者
 */
@Slf4j
public class Receive {

    // 交换机名称
    private static final String EXCHANGE_NAME = "logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 获取连接
        Connection connection = ConnectionUtils.getConnection();
        // 创建通道
        Channel channel = connection.createChannel();
        // 声明一个fanout交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
        // 声明一个临时队列. 非持久的/独占的/自动删除的
        String queueName = channel.queueDeclare().getQueue();
        // 将临时队列绑定到交换机上
        channel.queueBind(queueName, EXCHANGE_NAME, "");
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            log.info("接收到消息:{},consumerTag:{}", message, consumerTag);
        };

        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
        });
    }
}