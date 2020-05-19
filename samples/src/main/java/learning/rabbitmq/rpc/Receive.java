package learning.rabbitmq.rpc;

import com.rabbitmq.client.*;
import learning.rabbitmq.utils.ConnectionUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 消费者
 */
@Slf4j
public class Receive {

    private static final String QUEUE_NAME = "rpc_queue";

    public static void main(String[] args) throws Exception {
        // 创建连接
        Connection connection = ConnectionUtils.getConnection();
        // 创建通道
        Channel channel = connection.createChannel();
        // 定义临时队列，并返回生成的队列名称
        String replyQueueName = channel.queueDeclare().getQueue();
        // 唯一标志本次请求
        String corrId = UUID.randomUUID().toString();
        // 生成发送消息的属性
        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                // 唯一标志本次请求
                .correlationId(corrId)
                // 设置回调队列
                .replyTo(replyQueueName)
                .build();
        String message = "Hello RabbitMq...";

        // 发送消息，发送到默认交换机
        channel.basicPublish("", QUEUE_NAME, props, message.getBytes(StandardCharsets.UTF_8));
        // 阻塞队列，用于存储回调结果
        final BlockingQueue<String> queue = new ArrayBlockingQueue<String>(1);
        // 定义消息的回退方法
        channel.basicConsume(replyQueueName, true, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                if (properties.getCorrelationId().equals(corrId)) {
                    queue.offer(new String(body, StandardCharsets.UTF_8));
                }
            }
        });

        // 获取回调的结果
        String result = queue.take();
        log.info("接收到消息 '" + result + "'");
    }

}