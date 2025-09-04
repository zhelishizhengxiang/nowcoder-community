package com.simon.community.event;

import com.alibaba.fastjson2.JSONObject;
import com.simon.community.pojo.DiscussPost;
import com.simon.community.pojo.Event;
import com.simon.community.pojo.Message;
import com.simon.community.service.DiscussPostService;
import com.simon.community.service.ElasticsearchService;
import com.simon.community.service.MessageService;
import com.simon.community.util.CommunityConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhengx
 * @version 1.0
 */
@Component
@Slf4j
public class EventConsumer implements CommunityConstant {

    @Autowired
    private MessageService messageService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private ElasticsearchService  elasticsearchService;

    /**
     * 消费事件：给用户发送通知，即向message中插入数据
     * */
    @KafkaListener(topics = {TOPIC_COMMENT,TOPIC_FOLLOW,TOPIC_LIKE})
    public void consumeMessage(ConsumerRecord<?,String> record) {
        if(record==null || record.value()==null){
            log.error("消息的内容为空");
            return;
        }
        Event event = JSONObject.parseObject(record.value(), Event.class);
        if(event==null){
            log.error("消息格式错误");
        }

        //发送通知,存入数据库
        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setStatus(0);
        message.setCreateTime(new Date());
        //封装内容数据，用于前端展现
        Map<String,Object> content=new HashMap<>();
        content.put("userId",event.getUserId());
        content.put("entityType",event.getEntityType());
        content.put("entityId",event.getEntityId());
        if(!event.getData().isEmpty()){
            for(Map.Entry<String,Object> entry:event.getData().entrySet()){
                content.put(entry.getKey(),entry.getValue());
            }
        }
        message.setContent(JSONObject.toJSONString(content));

        messageService.addMessage(message);
    }

    /**
     * 消费发帖事件：向es服务器添加或者更新文档
     * */
    @KafkaListener(topics = TOPIC_PUBLISH)
    public void consumePublishMessage(ConsumerRecord<?,String> record) {
        if(record==null || record.value()==null){
            log.error("消息的内容为空");
            return;
        }
        Event event = JSONObject.parseObject(record.value(), Event.class);
        if(event==null){
            log.error("消息格式错误");
        }

        //查询到修改或者新增的帖子,存入es服务器
        DiscussPost post = discussPostService.findDiscussPostById(event.getEntityId());
        elasticsearchService.saveDiscussPost(post);


    }

    /**
     * 消费删贴事件，ex服务器删除该帖子
     * */
    @KafkaListener(topics = TOPIC_DELETE)
    public void consumDeleteMessage(ConsumerRecord<?,String> record) {
        if(record==null || record.value()==null){
            log.error("消息的内容为空");
            return;
        }
        Event event = JSONObject.parseObject(record.value(), Event.class);
        if(event==null){
            log.error("消息格式错误");
        }

        //从服务器中删除该帖子
        elasticsearchService.deleteDiscussPost(event.getEntityId());


    }

}
