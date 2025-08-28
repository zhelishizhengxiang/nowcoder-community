package com.simon.community.service;

import com.simon.community.dao.CommentMapper;
import com.simon.community.pojo.Comment;
import com.simon.community.util.CommunityConstant;
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
public class CommentService implements CommunityConstant {
    @Autowired
    private CommentMapper commentMapper;

    @Autowired DiscussPostService discussPostService;

    @Autowired
    private SensitiveFilter  sensitiveFilter;
    /**
     * 查询某一页数据
     */
    public List<Comment> findCommentByEntity(int entityType,int entityId,int offset,int limit) {
        return commentMapper.selectCommentsByEntity(entityType,entityId,offset,limit);
    }

    /**
     * 查询评论总数
     * */
    public int findCommentCountByEntity(int entityType,int entityId) {
        return commentMapper.selectCountByEntity(entityType,entityId);
    }

    /**
     * 新增评论
     * */
    @Transactional
    public int addComment(Comment comment){
        if (comment == null){
            throw new IllegalArgumentException("参数不能为空");
        }
        //进行敏感词和标签过滤
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        int row = commentMapper.insertComment(comment);

        //更新该评论数量，只有评论给帖子的才算数，回复不算
        if(comment.getEntityType()==ENTITY_TYPE_POST){
            int count=commentMapper.selectCountByEntity(comment.getEntityType(),comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(),count);
        }
        return row;
    }

    /**
     * 根据id查询评论
     * */
    public  Comment findCommentById(int commentId){
        return commentMapper.selectCommentById(commentId);
    }
}
