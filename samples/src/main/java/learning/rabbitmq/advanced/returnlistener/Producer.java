package learning.rabbitmq.advanced.returnlistener;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import learning.rabbitmq.utils.ConnectionUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * Return Listener用于处理一些不可路由消息
 * 生产者指定Exchange和RoutingKey，将消息投递到某个队列，然后消费者监听队列，进行消息处理，
 * 但在某些情况下，在发送消息时，若当前的exchange不存在或指定的路由key路由失败，
 * 这时，如果需要监听这种不可达的消息，则要使用return listener
 * <p>
 * 主要通过设置channel.basicPublish()方法中的mandatory参数来实现
 * <p>
 * mandatory:
 * true 监听器会接收到不可到达的消息
 * false broker端会自动丢弃该消息
 */
@Slf4j
public class Producer {

    private static final String EXCHANGE_NAME = "test_return_exchange";
    private static final String ROUTING_KEY = "return.save";
    private static final String ROUTING_KEY_ERROR = "abc.save";

    public static void main(String[] args) throws Exception {
        //1. 创建一个ConnectionFactory
        Connection connection = ConnectionUtils.getConnection();
        //2. 通过Connection创建一个Channel
        Channel channel = connection.createChannel();
        //3. 通过Channel发送数据
        String message = "Hello RabbitMQ Return Message";
        // 监听返回消息回调
        channel.addReturnListener((replyCode, replyText, exchange, routingKey, properties, body) -> {
            log.info("——handle return——");
            log.info("replyCode:" + replyCode);
            log.info("replyText:" + replyText);
            log.info("exchange:" + exchange);
            log.info("routingKey:" + routingKey);
            log.info("properties:" + properties);
            log.info("body:" + new String(body));
        });
        /**
         * mandatory设置为true时，无法找到符合条件的queue，会调用basic.return方法将消息返回给生产者；
         * mandatory设置为false时，broker会直接将消息扔掉。
         */
        channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY_ERROR, true, null, message.getBytes());
    }

}
