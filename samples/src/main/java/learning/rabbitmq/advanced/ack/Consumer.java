package learning.rabbitmq.advanced.ack;

import com.rabbitmq.client.*;
import learning.rabbitmq.utils.ConnectionUtils;

/**
 * 消费端ACK与重回队列
 * <p>
 * 消费端进行消费的时候，如果由于业务异常我们可以进行日志的记录，然后进行补偿！
 * 如果由于服务器宕机等严重问题，那我们就需要手工进行ACK保障消费端消费成功！
 * <p>
 * 消费端重回队列是为了对没有处理成功的消息，把消息重新传递给Broker
 */
public class Consumer {

    private static String exchange = "test_ack_exchange";
    private static String routingKey = "ack.#";
    private static String queueName = "test_ack_queue";

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
            long deliveryTag = message.getEnvelope().getDeliveryTag();
            byte[] body = message.getBody();
            AMQP.BasicProperties properties = message.getProperties();
            System.out.println("——consume message——");
            System.out.println("body:" + new String(body));
            System.out.println("num:" + properties.getHeaders().get("num"));
            // num为0的消息会一直回到MQ队列的最尾端，一直无法消费
            if ((Integer) properties.getHeaders().get("num") == 0) {
                // requeue: true表示重新入队（重回队列），重传
                channel.basicNack(deliveryTag, false, true);
            } else {
                channel.basicAck(deliveryTag, false);
            }
        };

        //5. autoAck = false
        channel.basicConsume(queueName, false, deliverCallback, consumerTag -> {
        });
    }
}
