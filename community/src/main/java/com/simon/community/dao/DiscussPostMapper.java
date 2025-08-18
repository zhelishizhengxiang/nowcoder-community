package com.simon.community.dao;

import com.simon.community.pojo.DiscussPost;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DiscussPostMapper {

    /**
     * @purpose 分页查询帖子
     * @param userId "我发布过"会用，正常页面展示不会用
     * */
    List<DiscussPost> selectDiscussPosts(int userId,int offset,int limit);

    /**
     * @purpose 查询帖子总数
     * @param userId "我发布过"会用，正常页面展示不会用
     *
     * */
//    参数是单个基本类型，但动态 SQL 中需要判断参数是否存在,
//    此时必须要用 @Param 标注，否则动态 SQL 无法识别参数名。
    int selectDiscussPostsCount(@Param("userId") int userId);

    int insertDiscussPost(DiscussPost post);



}