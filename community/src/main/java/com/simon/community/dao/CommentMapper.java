package com.simon.community.dao;

import com.simon.community.pojo.Comment;

import java.util.List;

public interface CommentMapper {

    /**
     * 分页查询
     * */
    List<Comment> selectCommentsByEntity(int entityType, int entityId,int offset,int limit);

    int selectCountByEntity(int entityType, int entityId);
}