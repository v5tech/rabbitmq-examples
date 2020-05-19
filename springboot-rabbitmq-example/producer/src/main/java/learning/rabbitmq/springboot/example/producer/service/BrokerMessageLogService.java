package learning.rabbitmq.springboot.example.producer.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import learning.rabbitmq.springboot.example.entity.BrokerMessageLog;
import learning.rabbitmq.springboot.example.producer.mapper.BrokerMessageLogMapper;
import learning.rabbitmq.springboot.example.utils.Constant;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * BrokerMessageLogService
 */
@Service
public class BrokerMessageLogService extends ServiceImpl<BrokerMessageLogMapper, BrokerMessageLog> {

    /**
     * 查询消息状态为0(发送中) 且已经超时的消息集合
     *
     * @return
     */
    public List<BrokerMessageLog> query4StatusAndTimeoutMessage() {
        return lambdaQuery().eq(BrokerMessageLog::getStatus, Constant.ORDER_SENDING)
                .le(BrokerMessageLog::getNextRetry, LocalDateTime.now())
                .list();
    }

    /**
     * 重新发送时更新统计count，发送次数+1
     *
     * @param messageLog
     * @param now
     */
    public void update4ReSend(BrokerMessageLog messageLog, LocalDateTime now) {
        lambdaUpdate().set(BrokerMessageLog::getTryCount, messageLog.getTryCount() + 1)
                .set(BrokerMessageLog::getUpdateTime, now)
                .eq(BrokerMessageLog::getMessageId, messageLog.getMessageId())
                .update();
    }

    /**
     * 更新消息发送结果，成功 or 失败
     *
     * @param messageId
     * @param status
     */
    public void changeBrokerMessageLogStatus(String messageId, String status) {
        lambdaUpdate().set(BrokerMessageLog::getStatus, status)
                .set(BrokerMessageLog::getUpdateTime, LocalDateTime.now())
                .eq(BrokerMessageLog::getMessageId, messageId)
                .update();
    }
}