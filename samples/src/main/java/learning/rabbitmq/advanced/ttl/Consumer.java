package learning.rabbitmq.advanced.ttl;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import learning.rabbitmq.utils.ConnectionUtils;

public class Consumer {

    private static String exchange = "test_ttl_exchange";
    private static String routingKey = "ttl.#";
    private static String queueName = "test_ttl_queue";

    public static void main(String[] args) throws Exception {
        Connection connection = ConnectionUtils.getConnection();
        //1. 通过connection创建一个Channel
        Channel channel = connection.createChannel();
        //2. 声明一个exchange
        channel.exchangeDeclare(exchange, BuiltinExchangeType.TOPIC, true, false, null);
        //3. 声明一个队列
        channel.queueDeclare(queueName, true, false, false, null);
        //4. 绑定
        channel.queueBind(queueName, exchange, routingKey);

        DeliverCallback deliverCallback = (consumerTag, message) -> {
            byte[] body = message.getBody();
            System.out.println("——consume message——");
            System.out.println("body:" + new String(body));
            // 手动确认消息
            channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
        };

        //5. autoAck = false
        channel.basicConsume(queueName, false, deliverCallback, consumerTag -> {
        });
    }
}
