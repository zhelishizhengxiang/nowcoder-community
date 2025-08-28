package com.simon.community.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhengx
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    private String topic;
    //事件触发的人
    private int userId;
    private int entityType;
    private int entityId;
    //实体的作者id
    private int entityUserId;
    //其他未来用到的数据存到map里，可扩展
    private Map<String,Object> data=new HashMap<>();

    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }
    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }
    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Event setData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }
}
