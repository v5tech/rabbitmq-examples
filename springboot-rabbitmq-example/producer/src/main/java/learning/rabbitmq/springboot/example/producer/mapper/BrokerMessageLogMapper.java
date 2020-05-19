package learning.rabbitmq.springboot.example.producer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import learning.rabbitmq.springboot.example.entity.BrokerMessageLog;

/**
 * 消息日志记录对象数据库操作接口
 */
public interface BrokerMessageLogMapper extends BaseMapper<BrokerMessageLog> {
}
