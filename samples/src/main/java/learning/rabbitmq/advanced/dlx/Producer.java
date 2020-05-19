package learning.rabbitmq.advanced.dlx;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import learning.rabbitmq.utils.ConnectionUtils;
import org.springframework.boot.autoconfigure.jms.JmsProperties;

/**
 * 死信队列
 * 当消息在一个队列中变成死信（dead message）之后，它能被重新publish到另一个Exchange，这个Exchange就是DLX。
 * <p>
 * 利用DLX，当消息在队列中变成死信(dead message:没有消费者去消费)之后，它能被重新publish到另一个Exchange，这个Exchange就是死信队列
 * DLX是一个正常的Exchange，和一般的Exchange没有区别，它能在任何的队列上被指定
 * 当这个队列中有死信时，RabbitMQ就会自动的将这个消息重新发布到设置的Exchange上，进而被路由到另一个队列
 * <p>
 * 消息变成死信有一下几种情况:
 * <p>
 * 1、消息被拒绝（basic.reject/ basic.nack）并且requeue=false
 * 2、消息TTL过期
 * 3、队列达到最大长度
 */
public class Producer {

    private static final String exchange = "test_dlx_exchange";
    private static final String routingKey = "dlx.save";

    public static void main(String[] args) throws Exception {
        Connection connection = ConnectionUtils.getConnection();
        Channel channel = connection.createChannel();

        String message = "Hello RabbitMQ DLX Message";

        AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                // 2:持久化投递；1:非持久化(未消费的消息重启后就没了)
                .deliveryMode(JmsProperties.DeliveryMode.PERSISTENT.getValue())
                .contentEncoding("UTF-8")
                // 5s后如果没有消费端消费，会变成死信
                .expiration("5000")
                .build();

        for (int i = 0; i < 1; i++) {
            channel.basicPublish(exchange, routingKey, true, properties, message.getBytes());
        }
    }
}
