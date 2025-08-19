package com.simon.community.dao;

import com.simon.community.pojo.DiscussPost;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DiscussPostMapper {

    /**
     * 分页查询帖子
     * @param userId "我发布过"会用，正常页面展示不会用
     * */
    List<DiscussPost> selectDiscussPosts(int userId,int offset,int limit);

    /**
     *  查询帖子总数
     * @param userId "我发布过"会用，正常页面展示不会用
     *
     * */
    int selectDiscussPostsCount(@Param("userId") int userId);

    int insertDiscussPost(DiscussPost post);

    /**
     * 根据帖子id查询帖子
     * */
    DiscussPost selectDiscussPostById(int id);

    /**
     * 修改帖子评论数量
     * */
    int updatePostCommentCount(int id,int commentCount);


}