package learning.rabbitmq.routing;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import learning.rabbitmq.utils.ConnectionUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * 消费者
 */
@Slf4j
public class Receive {

    private static final String EXCHANGE_NAME = "direct_exchange";

    /**
     * @param id       消费者线程id
     * @param routings 路由
     * @throws IOException
     * @throws TimeoutException
     */
    public static void receive(int id, String[] routings) throws IOException, TimeoutException {
        // 获取连接
        Connection connection = ConnectionUtils.getConnection();
        // 创建通道
        Channel channel = connection.createChannel();
        // 声明一个direct交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        // 声明一个临时队列. 非持久的/独占的/自动删除的
        String queueName = channel.queueDeclare().getQueue();
        // 绑定路由，同一个队列可以绑定多个值
        for (String routing : routings) {
            // 绑定队列到交换机,并指定路由
            channel.queueBind(queueName, EXCHANGE_NAME, routing);
        }

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            log.info(" [x] 接收到消息 '" + delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };

        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
        });
    }

    /**
     * 开启两个消费者, 分别订阅 error路由和info/warn路由的消息
     *
     * @param args
     */
    public static void main(String[] args) {
        new Thread(() -> {
            try {
                receive(0, new String[]{"error"});
            } catch (IOException | TimeoutException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                receive(1, new String[]{"info", "warn"});
            } catch (IOException | TimeoutException e) {
                e.printStackTrace();
            }
        }).start();
    }
}