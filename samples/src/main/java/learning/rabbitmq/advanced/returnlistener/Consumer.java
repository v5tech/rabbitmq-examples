package learning.rabbitmq.advanced.returnlistener;

import com.rabbitmq.client.*;
import learning.rabbitmq.utils.ConnectionUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class Consumer {

    private static final String exchange = "test_return_exchange";
    private static final String routingKey = "return.#";
    private static final String queueName = "test_return_queue";

    public static void main(String[] args) throws Exception {
        //1. 创建一个ConnectionFactory
        Connection connection = ConnectionUtils.getConnection();
        //2. 通过Connection创建一个Channel
        Channel channel = connection.createChannel();
        //3. 声明一个exchange
        channel.exchangeDeclare(exchange, BuiltinExchangeType.TOPIC, true, false, null);
        //4. 声明一个队列
        channel.queueDeclare(queueName, true, false, false, null);
        //5. 绑定
        channel.queueBind(queueName, exchange, routingKey);
        //6. 创建消费者
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
        channel.basicConsume(queueName, true, consumer);
    }


}
