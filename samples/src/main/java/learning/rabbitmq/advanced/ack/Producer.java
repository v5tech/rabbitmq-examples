package learning.rabbitmq.advanced.ack;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import learning.rabbitmq.utils.ConnectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jms.JmsProperties;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class Producer {

    private static String exchange = "test_ack_exchange";
    private static String routingKey = "ack.save";

    public static void main(String[] args) throws Exception {
        Connection connection = ConnectionUtils.getConnection();
        Channel channel = connection.createChannel();
        String message = "Hello RabbitMQ Send Ack message!";
        for (int i = 0; i < 5; i++) {
            Map<String, Object> headers = new HashMap<>();
            headers.put("num", i);
            AMQP.BasicProperties properties = new AMQP.BasicProperties().builder()
                    // 投递模式，持久化
                    .deliveryMode(JmsProperties.DeliveryMode.PERSISTENT.getValue())
                    .contentEncoding("UTF-8")
                    .headers(headers)
                    .build();
            channel.basicPublish(exchange, routingKey, properties, message.getBytes());
        }
    }

}
