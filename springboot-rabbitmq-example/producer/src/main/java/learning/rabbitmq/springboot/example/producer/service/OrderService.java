package learning.rabbitmq.springboot.example.producer.service;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import learning.rabbitmq.springboot.example.entity.BrokerMessageLog;
import learning.rabbitmq.springboot.example.entity.Order;
import learning.rabbitmq.springboot.example.producer.mapper.BrokerMessageLogMapper;
import learning.rabbitmq.springboot.example.producer.mapper.OrderMapper;
import learning.rabbitmq.springboot.example.producer.sender.RabbitOrderSender;
import learning.rabbitmq.springboot.example.utils.Constant;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * 订单服务
 */
@Service
public class OrderService extends ServiceImpl<OrderMapper, Order> {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private RabbitOrderSender orderSender;

    @Resource
    private BrokerMessageLogMapper messageLogMapper;

    /**
     * 创建订单
     *
     * @param order 订单对象
     */
    public void createOrder(Order order) {
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        // 业务数据入库
        orderMapper.insert(order);
        // 构建消息日志记录对象
        BrokerMessageLog messageLog = new BrokerMessageLog();
        // 设置消息唯一 id
        messageLog.setMessageId(order.getMessageId())
                // 设置消息体
                .setMessage(JSONUtil.toJsonStr(order))
                // 设置消息状态为 0，发送中
                .setStatus(Constant.ORDER_SENDING)
                // 设置消息未确认超时时间间隔为 1 分钟
                .setNextRetry(now.plusMinutes(Constant.ORDER_TIMEOUT_MINUTES))
                // 设置创建时间
                .setCreateTime(now)
                // 设置修改时间
                .setUpdateTime(now);
        // 消息日志对象入库
        messageLogMapper.insert(messageLog);
        // rabbitmq 发送消息
        orderSender.sendOrder(order);
    }
}