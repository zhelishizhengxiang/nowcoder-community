package com.simon.community.dao.mybatis;

import com.simon.community.pojo.Comment;

import java.util.List;

public interface CommentMapper {

    /**
     * 分页查询
     * */
    List<Comment> selectCommentsByEntity(int entityType, int entityId,int offset,int limit);

    /**
     * 查询实体数量
     * */
    int selectCountByEntity(int entityType, int entityId);

    /**
     * 添加评论
     * */
    int insertComment(Comment comment);

    /**
     * 根据id查询评论
     * */
    Comment selectCommentById(int commentId);
}