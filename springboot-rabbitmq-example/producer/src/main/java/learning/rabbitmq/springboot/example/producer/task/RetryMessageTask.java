package learning.rabbitmq.springboot.example.producer.task;

import cn.hutool.json.JSONUtil;
import learning.rabbitmq.springboot.example.entity.BrokerMessageLog;
import learning.rabbitmq.springboot.example.entity.Order;
import learning.rabbitmq.springboot.example.producer.sender.RabbitOrderSender;
import learning.rabbitmq.springboot.example.producer.service.BrokerMessageLogService;
import learning.rabbitmq.springboot.example.utils.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 消息重试、最大努力尝试解决方案
 * RetryMessageTask
 */
@Component
@Slf4j
public class RetryMessageTask {

    @Resource
    private RabbitOrderSender orderSender;

    @Resource
    private BrokerMessageLogService messageLogService;

    @Value("${system-constant.max-try-count}")
    private Integer maxTryCount;

    @Scheduled(initialDelay = 3000, fixedDelay = 10000)
    public void reSend() {

        log.debug("----------------------------定时任务开始----------------------------");

        // 查询status等于 0 且超时的消息
        List<BrokerMessageLog> brokerMessageLogs = messageLogService.query4StatusAndTimeoutMessage();
        log.info(Arrays.toString(brokerMessageLogs.toArray()));
        brokerMessageLogs.forEach(messageLog -> {
            if (messageLog.getTryCount() >= maxTryCount) {
                // 如果失败次数达到3次, 取消重试, 发送警报
                messageLogService.changeBrokerMessageLogStatus(messageLog.getMessageId(), Constant.ORDER_SEND_FAILURE);
                log.error("messageId={}, 失败次数已达到[{}], 不再进行重试. 请排查", messageLog.getMessageId(), messageLog.getTryCount());
            } else {
                // 修改重试次数
                messageLogService.update4ReSend(messageLog, LocalDateTime.now());
                // 消息重试
                Order order = JSONUtil.toBean(messageLog.getMessage(), Order.class);
                orderSender.sendOrder(order);
                log.debug("messageId={}, 进行第{}次重试", messageLog.getMessageId(), messageLog.getTryCount());
            }
        });


    }
}