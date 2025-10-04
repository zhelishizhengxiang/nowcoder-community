package com.simon.community.quartz;

import com.simon.community.pojo.DiscussPost;
import com.simon.community.service.DiscussPostService;
import com.simon.community.service.ElasticsearchService;
import com.simon.community.service.LikeService;
import com.simon.community.util.CommunityConstant;
import com.simon.community.util.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author zhengx
 * @version 1.0
 * @purpose 任务：重新计算收到加精、点赞、评论的帖子的分数，这些帖子在redis中
 */
@Slf4j
@Component
public class PostScoreRefreshJob implements Job, CommunityConstant {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private LikeService likeService;
    /**
     * 计算后的结果同步到搜索引擎
     * */
    @Autowired
    private ElasticsearchService  elasticsearchService;

    //牛客纪元：牛客产生的日子
    private static final Date epoch;

    static {
        try {
            epoch = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-08-01 00:00:00");
        } catch (ParseException e) {
           throw new RuntimeException("初始化牛客纪元失败",e);
        }
    }


    @Transactional
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String redisKey= RedisKeyUtil.getPostScoreKey();
        //BoundSetOperations 专门用于对 Redis 中某个固定的 Set类型 key 进行一系列操作（如添加元素、删除元素、查询元素等），
        // 无需在每次操作时重复指定 key，简化了对同一 key 的连续操作
        BoundSetOperations boundSetOperations = redisTemplate.boundSetOps(redisKey);

        //如果没有数据则不用算
        if(boundSetOperations.size()==0){
            log.info("任务取消，没有需要刷新的帖子");
            return;
        }

        log.info("[任务开始] 正在刷新帖子分数："+boundSetOperations.size());
        while (boundSetOperations.size()>0){
            this.refresh((Integer)boundSetOperations.pop());
        }
        log.info("[任务结束] 帖子分数刷新完毕");
    }


    private void refresh(Integer postId){
        DiscussPost post = discussPostService.findDiscussPostById(postId);
        if(post==null){
            log.error("该帖子不存在：id="+postId);
            return;
        }

        //分数计算公式：log(精华分+评论数*10+点赞数*2)+（发布时间-牛客纪元）天数
        //是否加精
        boolean wonder=post.getStatus()==1;
        //评论数
        int commentNum=post.getCommentCount();
        //点赞数
        long likeNum=likeService.findEntityLikeCount(ENTITY_TYPE_POST,postId);

        //权重
        double weigh=(wonder?75:0)+commentNum*10+likeNum*2;
        double score=Math.log10(Math.max(weigh,1))+(post.getCreateTime().getTime() - epoch.getTime())/(1000*3600*24);

        //更新帖子分数
        discussPostService.updateScore(postId,score);

        //同步搜索数据
        post.setScore(score);
        elasticsearchService.saveDiscussPost(post);
    }
}
