package com.simon.community.service;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.simon.community.dao.mybatis.DiscussPostMapper;
import com.simon.community.pojo.DiscussPost;
import com.simon.community.util.SensitiveFilter;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author zhengx
 * @version 1.0
 */
@Slf4j
@Service
public class DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Value("${caffeine.posts.maxsize}")
    private int maxSize;

    @Value("${caffeine.posts.expire.seconds}")
    private int expireSeconds;

    /**
     Caffeine核心接口：Cache,同步缓存LoadingCache,异步缓存AsyncLoadingCache
     * */
    //贴子列表缓存
    private LoadingCache<String, List<DiscussPost>> postListCache;

    //帖子总数的缓存
    private LoadingCache<Integer,Integer> postRowsCache;

    @PostConstruct
    public void init(){
        //初始化帖子列表和帖子总数缓存
        postListCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                //CacheLoader接口当尝试在缓存中取数据时，发现缓存中不存在某个 key 对应的 value 时，
                //自动触发数据加载逻辑（如从数据库、API 接口获取数据），并将加载的结果自动存入缓存
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    @Override
                    public List<DiscussPost> load(String key) {
                        if(key==null || key.length()==0){
                            throw new IllegalArgumentException("参数错误！");
                        }
                        String[] params=key.split(":");
                        if(params.length!=2){
                            throw new IllegalArgumentException("参数错误");
                        }
                        int offset=Integer.parseInt(params[0]);
                        int limit=Integer.parseInt(params[1]);
                        return discussPostMapper.selectDiscussPosts(0,offset,limit,1);
                    }
                });
        //初始化帖子总数缓存
        postRowsCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Override
                    public Integer load(Integer key) {
                        return discussPostMapper.selectDiscussPostsCount(key);
                    }
                });
    }

    /**
     *  查询某页的数据
     */
    public List<DiscussPost> findDiscussPosts(int userId,int offset,int limit,int orderMode){
        //只有查热门帖子才会走缓存
        if(userId==0 && orderMode==1){
            return postListCache.get(offset+":"+limit);
        }
        log.info("load post list from db");
        return discussPostMapper.selectDiscussPosts(userId,offset,limit,orderMode);
    }

    /**
    *  查询行数
    * */
    public int findDiscussPostsCount(int userId){
        //只有查热门帖子才会走缓存
        if(userId==0){
            return postRowsCache.get(userId);
        }
        log.info("load post rows from db");
        return discussPostMapper.selectDiscussPostsCount(userId);
    }

    /**
     *  添加帖子
     * */
    @Transactional
    public int addDiscussPost(DiscussPost discussPost){
        if (discussPost==null)
                throw new IllegalArgumentException("参数不能为空");
        //转义html标签，防止xss攻击
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));
        //对标题和内容进行敏感词过滤
        discussPost.setTitle(sensitiveFilter.filter(discussPost.getTitle()));
        discussPost.setContent(sensitiveFilter.filter(discussPost.getContent()));

        return discussPostMapper.insertDiscussPost(discussPost);
    }

    /**
     * 根据id查询帖子数据
     * */
    public DiscussPost findDiscussPostById(int id){
        return discussPostMapper.selectDiscussPostById(id);
    }

    /**
     *  修改帖子评论数量
     * */
    @Transactional
    public int updateCommentCount(int id,int commentCount){
        return discussPostMapper.updatePostCommentCount(id,commentCount);
    }

    /**
     * 修改帖子类型
     * */
    @Transactional
    public void updateType(int id,int type){
        discussPostMapper.updateType(id,type);
    }

    /**
     * 修改帖子状态
     * */
    @Transactional
    public void updateStatus(int id,int status){
        discussPostMapper.updateStatus(id,status);
    }

    /**
     * 修改帖子分数
     * */
    @Transactional
    public void updateScore(int id,double score){
        discussPostMapper.updateScore(id, score);
    }
}