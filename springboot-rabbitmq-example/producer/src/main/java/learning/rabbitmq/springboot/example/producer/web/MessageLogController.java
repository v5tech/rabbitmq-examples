package learning.rabbitmq.springboot.example.producer.web;

import learning.rabbitmq.springboot.example.entity.BrokerMessageLog;
import learning.rabbitmq.springboot.example.producer.service.BrokerMessageLogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * MessageLogController
 */
@RestController
@RequestMapping("log")
public class MessageLogController {

    @Resource
    private BrokerMessageLogService brokerMessageLogService;

    @GetMapping("list")
    public Object list() {
        return brokerMessageLogService.list();
    }

    @GetMapping("{id}")
    public Object getOne(@PathVariable String id) {
        return brokerMessageLogService.lambdaQuery().eq(BrokerMessageLog::getMessageId, id).one();
    }
}