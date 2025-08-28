package com.simon.community.event;

import com.alibaba.fastjson2.JSONObject;
import com.simon.community.pojo.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @author zhengx
 * @version 1.0
 */
@Component
public class EventProducer {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    //处理事件，发送消息
    public void fireEvent(Event event) {
        //将事件封装成jSON发送
        this.kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }
}
