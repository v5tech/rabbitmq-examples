package learning.rabbitmq.hello;

import cn.hutool.http.ContentType;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import learning.rabbitmq.utils.ConnectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jms.JmsProperties;

import java.nio.charset.StandardCharsets;

/**
 * RabbitMq简单模式，消息生产者
 * 不指定交换机，也没有routingKey，消息直接发送到队列
 */
@Slf4j
public class Send {

    private static final String QUEUE_NAME = "sample.*";

    public static void main(String[] args) throws Exception {
        // 获取连接
        Connection connection = ConnectionUtils.getConnection();

        // 创建 Channel
        Channel channel = connection.createChannel();

        /**
         * 声明队列,该方法是幂等的，只有队列不存在时才会创建
         * 1. String queue 队列名
         * 2. boolean durable 是否持久的
         * 3. boolean exclusive 是否独占的(注册到该connection)
         * 4. boolean autoDelete 是否自动删除(服务器将在不再使用时删除它)
         * 5. Map<String, Object> 该队列的其他属性
         */
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);

        /**
         * 定义发送的属性
         * 包括消息格式/是否持久化等
         * 还有消息id也可以自定义
         */
        AMQP.BasicProperties properties = new AMQP.BasicProperties().builder()
                //内容类型,此处可以使用MediaType
                .contentType(ContentType.JSON.toString(StandardCharsets.UTF_8))
                //消息为持久化(2).或者瞬态(任何其他值)
                .deliveryMode(JmsProperties.DeliveryMode.NON_PERSISTENT.getValue())
                .build();

        String message = "Hello World!";

        /**
         * 发送消息
         * 1. String exchange 交换机名称，将消息发布到... (后面的发布订阅模式)
         * 2. String routingKey routingKey 此处是队列名
         * 3. BasicProperties props 其他属性, route header(路由头)等
         * 4. byte[] body 消息体
         */
        channel.basicPublish("", QUEUE_NAME, properties, message.getBytes());

        log.info("发送消息:{},成功.", message);
    }
}
