package learning.rabbitmq.routing;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import learning.rabbitmq.utils.ConnectionUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 生产者
 */
@Slf4j
public class Send {

    private static final String EXCHANGE_NAME = "direct_exchange";

    public static void main(String[] args) throws Exception {
        // 获取连接
        Connection connection = ConnectionUtils.getConnection();
        // 创建通道
        Channel channel = connection.createChannel();
        // 声明一个direct交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        // 发送消息到 自定义的直接(direct)类型的交易所,指定路由为 error,info和warn
        channel.basicPublish(EXCHANGE_NAME, "error", null, "error".getBytes());
        channel.basicPublish(EXCHANGE_NAME, "info", null, "info".getBytes());
        channel.basicPublish(EXCHANGE_NAME, "warn", null, "warn".getBytes());
    }
}