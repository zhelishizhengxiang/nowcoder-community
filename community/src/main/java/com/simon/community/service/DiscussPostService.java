package com.simon.community.service;

import com.simon.community.dao.mybatis.DiscussPostMapper;
import com.simon.community.pojo.DiscussPost;
import com.simon.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @author zhengx
 * @version 1.0
 */
@Service
public class DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    /**
     *  查询某页的数据
     */
    public List<DiscussPost> findDiscussPosts(int userId,int offset,int limit){
        return discussPostMapper.selectDiscussPosts(userId,offset,limit);
    }

    /**
    *  查询行数
    * */
    public int findDiscussPostsCount(int userId){
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
    public int updateCommentCount(int id,int commentCount){
        return discussPostMapper.updatePostCommentCount(id,commentCount);
    }

    /**
     * 修改帖子类型
     * */
    public void updateType(int id,int type){
        discussPostMapper.updateType(id,type);
    }

    /**
     * 修改帖子状态
     * */
    public void updateStatus(int id,int status){
        discussPostMapper.updateStatus(id,status);
    }
}
