package learning.rabbitmq.work;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;
import learning.rabbitmq.utils.ConnectionUtils;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * 生产者
 */
@Slf4j
public class Send {

    private static final String QUEUE_NAME = "work_queue*";

    public static void main(String[] args) throws Exception {

        // 获取连接
        Connection connection = ConnectionUtils.getConnection();
        // 创建通道
        Channel channel = connection.createChannel();

        // channel.queueDeclarePassive(String queue) 可以用来检测一个queue是否已经存在

        /**
         *
         * 声明一个持久化队列.该方法是幂等的.只有队列不存在时才会被创建
         * 1.队列名
         * 2.是否持久的
         * 3.是否独占的(注册到该connection)
         * 4. 是否自动删除(服务器将在不再使用时删除它)
         * 5.Map<String, Object> 该队列的其他属性
         */
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);

        /**
         * 将该通道声明为生产者确认模式
         */
        channel.confirmSelect();

        /** 发送消息
         * 1. 将消息发布到... (后面的发布订阅模式)
         * 2. 路由key(此处是队列名)
         * 3. 其他属性, route header(路由头)等
         * 4. 消息
         */
        String message = "Hello RabbitMq...";

        // 获取下一个发送的消息id,可用来在确认回调时判断是哪条消息没有成功
        long nextPublishSeqNo = channel.getNextPublishSeqNo();

        // 开启监听器,异步等待确认结果
        channel.addConfirmListener(new ConfirmListener() {
            // 成功确认结果
            @Override
            public void handleAck(long deliveryTag, boolean multiple) {
                // 此处的multiple和消费者确认中我们自己传递的参数是同一种,表示是否批量
                log.info("发送成功.deliveryTag:{},multiple:{}", deliveryTag, multiple);
            }

            // 未确认结果
            @Override
            public void handleNack(long deliveryTag, boolean multiple) {
                log.info("发送失败.deliveryTag:{},multiple:{}", deliveryTag, multiple);
            }
        });

        // 发送一个持久化消息
        channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes(StandardCharsets.UTF_8));

        /**
         * 如果是持久化的消息队列,需要如下传输消息
         */
        // channel.basicPublish("",QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN,message.getBytes());

        // 同步等待确认结果,可以设置超时时间
        // channel.waitForConfirms();

        // 同步批量等待确认结果,就算只有一个失败了,也会返回false.
        // channel.waitForConfirmsOrDie(5_000);

    }

}