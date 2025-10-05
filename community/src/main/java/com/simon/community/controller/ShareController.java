package com.simon.community.controller;

import com.simon.community.event.EventProducer;
import com.simon.community.pojo.Event;
import com.simon.community.util.CommunityConstant;
import com.simon.community.util.CommunityUtil;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhengx
 * @version 1.0
 */
@Controller
@Slf4j
public class ShareController implements CommunityConstant {

    @Autowired
    private EventProducer  eventProducer;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${wk.image.storage}")
    private String wkImageStorage;


    @Value("${qiniu.bucket.header.url}")
    private String shareBucketUrl;

    /**
     * @param htmlUrl 网页的url
     * 交给kafka来异步生成分享的图片,
     * */
    @RequestMapping(path = "/share",method = RequestMethod.GET)
    @ResponseBody
    public String share(String htmlUrl){
        //文件名
        String filename= CommunityUtil.generateUUID();
        //构建Event
        Event event = new Event()
                .setTopic(TOPIC_SHARE)
                .setData("htmlUrl",htmlUrl)
                .setData("filename",filename)
                .setData("suffix",".png");
        eventProducer.fireEvent(event);
        Map<String,Object> map=new HashMap<>();
        map.put("shareUrl",shareBucketUrl+"/"+filename);

        //返回访问路径
        return CommunityUtil.getJSONString(200,null,map);
    }

    /**
     * 从本地获取长图，已经废弃
     * */
    @Deprecated
    @RequestMapping(path = "/share/image/{fileName}",method = RequestMethod.GET)
    public void getShareImage(@PathVariable("fileName") String fileName, HttpServletResponse response){
        if(StringUtils.isBlank(fileName)){
            throw new IllegalArgumentException("文件名不能为空");
        }
        response.setContentType("image/png");
        File file=new File(wkImageStorage+"/"+fileName+".png");
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            FileInputStream fis=new FileInputStream(file);
            byte[] buf=new byte[1024];
            int b=0;
            while((b=fis.read(buf))!=-1){
                outputStream.write(buf,0,b);
            }
        } catch (IOException e) {
            log.error("获取长图失败"+e.getMessage());
        }

    }
}
