package learning.rabbitmq.utils;

import com.rabbitmq.client.*;
import com.rabbitmq.client.impl.DefaultExceptionHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Slf4j
public class ConnectionUtils {

    public static Connection getConnection() throws IOException, TimeoutException {
        // 创建 ConnectionFactory
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(ConnectionFactory.DEFAULT_HOST);
        connectionFactory.setPort(AMQP.PROTOCOL.PORT);
        connectionFactory.setVirtualHost(ConnectionFactory.DEFAULT_VHOST);
        connectionFactory.setUsername(ConnectionFactory.DEFAULT_USER);
        connectionFactory.setPassword(ConnectionFactory.DEFAULT_PASS);
        // 设置连接超时时间
        connectionFactory.setConnectionTimeout(ConnectionFactory.DEFAULT_CONNECTION_TIMEOUT);
        // 设置心跳间隔
        connectionFactory.setRequestedHeartbeat(ConnectionFactory.DEFAULT_HEARTBEAT);
        // 设置自动恢复
        connectionFactory.setAutomaticRecoveryEnabled(true);
        // 设置连接恢复间隔
        connectionFactory.setNetworkRecoveryInterval(10);

        // 设置异常处理Handler
        connectionFactory.setExceptionHandler(new DefaultExceptionHandler() {
            // 处理连接恢复时的异常.
            @Override
            public void handleConnectionRecoveryException(Connection conn, Throwable exception) {
                super.handleConnectionRecoveryException(conn, exception);
                log.info("连接恢复异常");
            }
        });

        // 建立连接 Connection
        Connection connection = connectionFactory.newConnection();

        //当开启连接自动恢复后,可以通过如下方式,设置监听器(也可以设置在通道处)
        Recoverable recoverable = (Recoverable) connection;
        recoverable.addRecoveryListener(new RecoveryListener() {
            // 当自动恢复完成后调用
            @Override
            public void handleRecovery(Recoverable recoverable) {
                log.info("自动恢复完成");
            }

            // 开始自动恢复前调用.此时未执行任何自动恢复步骤
            @Override
            public void handleRecoveryStarted(Recoverable recoverable) {
                log.info("自动恢复开始");
            }
        });

        return connection;
    }

}
