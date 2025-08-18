package com.simon.community.service;

import com.simon.community.dao.CommentMapper;
import com.simon.community.pojo.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhengx
 * @version 1.0
 */
@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;

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
}
