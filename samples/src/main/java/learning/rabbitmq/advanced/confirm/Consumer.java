package learning.rabbitmq.advanced.confirm;

import com.rabbitmq.client.*;
import learning.rabbitmq.utils.ConnectionUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class Consumer {

    private static final String exchangeName = "test_confirm_exchange";
    private static final String routingKey = "confirm.#";
    private static final String queueName = "test_confirm_queue";

    public static void main(String[] args) throws Exception {
        Connection connection = ConnectionUtils.getConnection();
        //1. 通过connection创建一个Channel
        Channel channel = connection.createChannel();
        //2. 声明一个exchange
        channel.exchangeDeclare(exchangeName, BuiltinExchangeType.TOPIC, true);
        //3. 声明一个队列
        channel.queueDeclare(queueName, true, false, false, null);
        //4. 绑定
        channel.queueBind(queueName, exchangeName, routingKey);
        //5. 创建消费者
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
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                log.info("——consume message——");
                String message = new String(body, StandardCharsets.UTF_8);
                log.info("consumerTag = " + message);
                log.info("envelope = " + message);
                log.info("properties = " + message);
                log.info("message = " + message);
            }
        };
        //6. 设置Channel
        channel.basicConsume(queueName, true, consumer);

    }
}