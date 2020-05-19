package learning.rabbitmq.advanced.qos;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import learning.rabbitmq.utils.ConnectionUtils;

public class Producer {

    private static final String exchange = "test_qos_exchange";
    private static final String routingKey = "qos.save";

    public static void main(String[] args) throws Exception {
        //1. 创建一个ConnectionFactory
        Connection connection = ConnectionUtils.getConnection();
        //2. 通过Connection创建一个Channel
        Channel channel = connection.createChannel();
        //3. 发送数据
        String message = "Hello RabbitMQ QOS Message";
        for (int i = 0; i < 5; i++) {
            channel.basicPublish(exchange, routingKey, null, message.getBytes());
        }
    }

}
