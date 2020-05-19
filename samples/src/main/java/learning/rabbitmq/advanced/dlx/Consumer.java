package learning.rabbitmq.advanced.dlx;

import com.rabbitmq.client.*;
import learning.rabbitmq.utils.ConnectionUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 死信队列设置
 * <p>
 * 首先要设置死信队列的exchange和queue，然后进行绑定：
 * Exchange：dlx.exchange
 * Queue：dlx.queue
 * RoutingKey：#
 * <p>
 * 然后进行正常声明交换机、队列、绑定，只不过需要在队列上加上一个参数：arguments.put("x-dead-letter-exchange","dlx.exchange);
 * 这样消息在过期、requeue、队列达到最大长度时，消息就可以直接路由到死信队列
 */
@Slf4j
public class Consumer {

    private static final String exchange = "test_dlx_exchange";
    private static final String routingKey = "dlx.#";
    private static final String queueName = "test_dlx_queue";

    public static void main(String[] args) throws Exception {

        Connection connection = ConnectionUtils.getConnection();
        Channel channel = connection.createChannel();

        // 死信交换机
        String dlxExchange = "dlx.exchange";
        // 死信队列
        String dlxQueue = "dlx.queue";

        // 2. 声明一个exchange
        channel.exchangeDeclare(exchange, BuiltinExchangeType.TOPIC, true, false, null);

        Map<String, Object> arguments = new HashMap<>();
        // 路由失败，重发到dlx.exchange
        arguments.put("x-dead-letter-exchange", dlxExchange);
        // arguments 要设置到声明队列上
        channel.queueDeclare(queueName, true, false, false, arguments);
        channel.queueBind(queueName, exchange, routingKey);

        // 声明死信交换机
        channel.exchangeDeclare(dlxExchange, BuiltinExchangeType.TOPIC, true, false, null);
        // 声明死信队列
        channel.queueDeclare(dlxQueue, true, false, false, null);
        // 绑定队列和交换机
        channel.queueBind(dlxQueue, dlxExchange, "#");

        channel.basicConsume(queueName, true, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                log.info("——consume message——");
                log.info("body:" + new String(body));
            }
        });

    }
}
