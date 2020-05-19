package learning.rabbitmq.hello;

import com.rabbitmq.client.*;
import learning.rabbitmq.utils.ConnectionUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * RabbitMq简单模式，消息消费者
 * 没有交换机和 routingkey 绑定，直接消费队列中的消息
 */
@Slf4j
public class Receive {

    private static final String QUEUE_NAME = "sample.*";

    public static void main(String[] args) throws Exception {
        // 获取连接
        Connection connection = ConnectionUtils.getConnection();
        // 创建通道
        Channel channel = connection.createChannel();

        /**
         * 声明队列,该方法是幂等的，只有队列不存在时才会创建
         * 1. String queue 队列名
         * 2. boolean durable 是否持久的
         * 3. boolean exclusive 是否独占的(注册到该connection)
         * 4. boolean autoDelete 是否自动删除(服务器将在不再使用时删除它)
         * 5. Map<String, Object> 该队列的其他属性
         */
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);

        // 创建消费者
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            /**
             * 消费消息
             * @param consumerTag 消费者标签
             * @param envelope 信封类,包含了交付者标签/是否重新交付/路由key等信息
             * @param properties 基本属性
             * @param body 消息
             * @throws IOException
             */
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                log.info("——consume message——");
                String message = new String(body, StandardCharsets.UTF_8);
                log.info("consumerTag = " + message);
                log.info("envelope = " + message);
                log.info("properties = " + message);
                log.info("message = " + message);
            }

            @Override
            public void handleConsumeOk(String consumerTag) {
                log.info("消费者注册成功");
            }

            @Override
            public void handleCancelOk(String consumerTag) {
                log.info("消费者取消注册成功");
            }

            @Override
            public void handleCancel(String consumerTag) throws IOException {
                log.info("消费者取消注册");
            }

            @Override
            public void handleRecoverOk(String consumerTag) {
                log.info("再次获取成功");
            }
        };

        /**
         * 消费消息
         * 1. String queue 队列名称
         * 2. boolean autoAck 是否自动确认
         * 3. Consumer callback 消息消费回调
         */
        channel.basicConsume(QUEUE_NAME, true, consumer);
    }
}
