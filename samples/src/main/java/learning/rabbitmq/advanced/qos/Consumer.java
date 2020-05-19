package learning.rabbitmq.advanced.qos;

import com.rabbitmq.client.*;
import learning.rabbitmq.utils.ConnectionUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * 消费端限流
 * 使用前提：非自动确认消息的前提下，一定数目的消息未被确认前，不进行消费新的消息。
 * 好处：
 * 1. 实现限速
 * 2. 保证消息确认质量
 * 注意事项：消费确认模式必须是非自动ACK机制（这个是使用baseQos的前提条件，否则会Qos不生效）
 */
@Slf4j
public class Consumer {

    private static final String exchange = "test_qos_exchange";
    private static final String routingKey = "qos.#";
    private static final String queueName = "test_qos_queue";

    public static void main(String[] args) throws Exception {
        //1. 创建一个ConnectionFactory
        Connection connection = ConnectionUtils.getConnection();
        //2. 通过Connection创建一个Channel
        Channel channel = connection.createChannel();
        //2. 声明一个exchange
        channel.exchangeDeclare(exchange, BuiltinExchangeType.TOPIC, true, false, null);
        //3. 声明一个队列
        channel.queueDeclare(queueName, true, false, false, null);
        //4. 绑定
        channel.queueBind(queueName, exchange, routingKey);
        //5. 创建消费者
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                log.info("——consume message——");
                log.info("consumerTag:" + consumerTag);
                log.info("envelope:" + envelope);
                log.info("properties:" + properties);
                log.info("body:" + new String(body));
            }
        };

        //6.限流方式 每次只推1条
        /**
         * prefetchSize 消费单条消息的大小限制，0 不限制，消息的限制大小，消息多少兆。一般不做限制，设置为0
         * prefetchCount 限制Queue每次发送给消费者的消息数，一次最多处理多少条，实际工作中设置为1就好
         * global true 应用于 channel级别，false 应用于 consume 级别，一般设置为false
         */
        channel.basicQos(0, 1, false);

        //7. 设置Channel autoAck一定要设置为false，才能做限流
        channel.basicConsume(queueName, false, consumer);
    }
}
