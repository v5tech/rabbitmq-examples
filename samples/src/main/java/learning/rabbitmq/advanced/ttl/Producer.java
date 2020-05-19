package learning.rabbitmq.advanced.ttl;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import learning.rabbitmq.utils.ConnectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jms.JmsProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * TTL 消息
 * RabbitMQ支持消息的过期时间，在消息发送时可以进行指定
 * RabbitMQ支持队列的过期时间，从消息入队列开始计算，只要超过了队列的超时时间配置，那么消息会自动的清除
 * <p>
 * 有两种方式实现：
 * 1、通过队列属性设置，队列中所有消息都有相同的过期时间
 * 2、对消息进行单独设置，每条消息TTL可以不同
 * 两种方法同时使用，则消息的过期时间以两者之间TTL较小的那个数值为准
 */
@Slf4j
public class Producer {

    private static String exchange = "test_ttl_exchange";
    private static String routingKey = "ttl.save";
    private static String queue = "test_ttl.queue";

    public static void main(String[] args) throws Exception {

        Connection connection = ConnectionUtils.getConnection();
        Channel channel = connection.createChannel();
        String message = "Hello ttl Message";

        // 通过队列属性设置消息TTL的方法是在queue.declare方法中加入x-message-ttl参数，单位为ms
        Map<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("x-message-ttl", 6000);
        // 设置队列的过期时间，从消息入队列开始计时，只要超过了队列的超时时间，那么消息会自动的清除
        // arguments.put("x-expires", 1800000); // 30分钟
        channel.queueDeclare(queue, true, false, false, arguments);

        AMQP.BasicProperties props = new AMQP.BasicProperties.
                Builder().
                // 持久化消息
                        deliveryMode(JmsProperties.DeliveryMode.PERSISTENT.getValue()).
                        contentEncoding("UTF-8").
                        expiration("6000").
                        build();

        // 针对每条消息设置TTL的方法是在basic.publish方法中加入expiration的属性参数，单位为ms.
        channel.basicPublish(exchange, routingKey, props, message.getBytes());
    }

}
