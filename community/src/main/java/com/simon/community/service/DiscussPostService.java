package com.simon.community.service;

import com.simon.community.dao.DiscussPostMapper;
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
     * @purpose 查询某页的数据
     */
    public List<DiscussPost> findDiscussPosts(int userId,int offset,int limit){
        return discussPostMapper.selectDiscussPosts(userId,offset,limit);
    }

    /**
    * @purpose 查询行数
    * */
    public int findDiscussPostsCount(int userId){
        return discussPostMapper.selectDiscussPostsCount(userId);
    }

    /**
     * @purpose 添加帖子
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
}
