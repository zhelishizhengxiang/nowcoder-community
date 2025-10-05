package com.simon.community.event;

import com.alibaba.fastjson2.JSONObject;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.simon.community.pojo.DiscussPost;
import com.simon.community.pojo.Event;
import com.simon.community.pojo.Message;
import com.simon.community.service.DiscussPostService;
import com.simon.community.service.ElasticsearchService;
import com.simon.community.service.MessageService;
import com.simon.community.util.CommunityConstant;
import com.simon.community.util.CommunityUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;

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
    private ElasticsearchService elasticsearchService;

    @Value("${wk.image.storage}")
    private String wkImageStorage;

    @Value("${wk.image.command}")
    private String wkImageCmd;

    @Value("${qiniu.key.access}")
    private String accessKey;

    @Value("${qiniu.key.secret}")
    private String secretKey;

    @Value("${qiniu.bucket.share.name}")
    private String shareBucketName;

    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    /**
     * 消费事件：给用户发送通知，即向message中插入数据
     *
     */
    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_FOLLOW, TOPIC_LIKE})
    public void consumeMessage(ConsumerRecord<?, String> record) {
        if (record == null || record.value() == null) {
            log.error("消息的内容为空");
            return;
        }
        Event event = JSONObject.parseObject(record.value(), Event.class);
        if (event == null) {
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
        Map<String, Object> content = new HashMap<>();
        content.put("userId", event.getUserId());
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityId());
        if (!event.getData().isEmpty()) {
            for (Map.Entry<String, Object> entry : event.getData().entrySet()) {
                content.put(entry.getKey(), entry.getValue());
            }
        }
        message.setContent(JSONObject.toJSONString(content));

        messageService.addMessage(message);
    }

    /**
     * 消费发帖事件：向es服务器添加或者更新文档
     *
     */
    @KafkaListener(topics = TOPIC_PUBLISH)
    public void consumePublishMessage(ConsumerRecord<?, String> record) {
        if (record == null || record.value() == null) {
            log.error("消息的内容为空");
            return;
        }
        Event event = JSONObject.parseObject(record.value(), Event.class);
        if (event == null) {
            log.error("消息格式错误");
        }

        //查询到修改或者新增的帖子,存入es服务器
        DiscussPost post = discussPostService.findDiscussPostById(event.getEntityId());
        elasticsearchService.saveDiscussPost(post);


    }

    /**
     * 消费删贴事件，ex服务器删除该帖子
     *
     */
    @KafkaListener(topics = TOPIC_DELETE)
    public void consumDeleteMessage(ConsumerRecord<?, String> record) {
        if (record == null || record.value() == null) {
            log.error("消息的内容为空");
            return;
        }
        Event event = JSONObject.parseObject(record.value(), Event.class);
        if (event == null) {
            log.error("消息格式错误");
        }

        //从服务器中删除该帖子
        elasticsearchService.deleteDiscussPost(event.getEntityId());


    }

    /**
     * 消费分享事件：生成长图存入云服务器
     *
     */
    @KafkaListener(topics = TOPIC_SHARE)
    public void consumShareMessage(ConsumerRecord<?, String> record) {
        if (record == null || record.value() == null) {
            log.error("消息的内容为空");
            return;
        }
        Event event = JSONObject.parseObject(record.value(), Event.class);
        if (event == null) {
            log.error("消息格式错误");
        }

        String htmlUrl = (String) event.getData().get("htmlUrl");
        String fileName = (String) event.getData().get("filename");
        String suffix = (String) event.getData().get("suffix");

        String cmd = wkImageCmd + " --quality 75 "
                + htmlUrl + " " + wkImageStorage + "/" + fileName + suffix;
        //执行该命令
        try {
            //该命令是异步执行
            Runtime.getRuntime().exec(cmd);
            log.info("生成长图成功" + cmd);
        } catch (IOException e) {
            log.error("生成长图失败" + e.getMessage());
        }

        //启动定时器，监视该图片，一旦生成则上传至云服务器
        UploadTask uploadTask = new UploadTask(fileName, suffix);
        ScheduledFuture<?> future = threadPoolTaskScheduler.scheduleAtFixedRate(uploadTask, 500);
        uploadTask.setFuture(future);
    }

    class UploadTask implements Runnable {
        //文件名称
        private String fileName;

        //文件后缀
        private String suffix;

        //启动任务的返回值
        private Future future;

        //开始时间
        private long startTime;

        //上传次数
        private int uploadTimes;

        public UploadTask(String fileName, String suffix) {
            this.fileName = fileName;
            this.suffix = suffix;
            this.startTime = System.currentTimeMillis();
        }

        public void setFuture(Future future) {
            this.future = future;
        }

        @Override
        public void run() {
            //生成图片失败
            if (System.currentTimeMillis() - startTime > 30000) {
                log.error("执行时间过长，终止任务：" + fileName);
                future.cancel(true);
                return;
            }
            //上传失败
            if (uploadTimes >= 3) {
                log.error("上传次数过多，终止任务:" + fileName);
                future.cancel(true);
                return;
            }

            String path = wkImageCmd + "/" + fileName + suffix;
            File filw = new File(path);
            if (filw.exists()) {
                log.info(String.format("开始第%d次上传[%s].", ++uploadTimes, fileName));
                //设置响应信息
                StringMap policy = new StringMap();
                policy.put("returnBody", CommunityUtil.getJSONString(200));
                //生成上传凭证
                Auth auth = Auth.create(accessKey, secretKey);
                String uploadToken = auth.uploadToken(shareBucketName, fileName, 3600, policy);
                //指定上传机房
                UploadManager manager = new UploadManager(new Configuration(Zone.zone2()));
                try{
                    //使用SDK开始上传图片
                    Response response=manager.put(
                            path,fileName,uploadToken,null,"image/"+suffix,false
                    );
                    //处理响应结果
                    JSONObject object = JSONObject.parseObject(response.bodyString());
                    if(object==null || object.get("code")==null || !object.get("code").toString().equals("200")){
                        log.info(String.format("第%d次上传[%s]失败.", uploadTimes, fileName));
                    }else{
                        log.info(String.format("第%d次上传[%s]成功.", uploadTimes, fileName));
                        future.cancel(true);
                    }
                }catch (QiniuException e){
                    log.info(String.format("第%d次上传[%s]失败.", uploadTimes, fileName));
                }
            }else {
                log.info(String.format("等待图片生成["+fileName+"]."));
            }
        }
    }


}
