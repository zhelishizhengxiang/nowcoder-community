package com.simon.community.dao.mybatis;

import com.simon.community.pojo.DiscussPost;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DiscussPostMapper {

    /**
     * 分页查询帖子
     * @param userId "我发布过"会用，正常页面展示不会用
     * @param orderMode 排序方式，默认为创建时间,为1时是按照分数排序
     * */
    List<DiscussPost> selectDiscussPosts(Integer userId,int offset,int limit,int orderMode);

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

    /**
     * 修改帖子状态
     * */
    int updateStatus(int id,int status);

    /**
     * 修改帖子类型
     * */
    int updateType(int id,int type);

    /**
     * 更新帖子分数
     * */
    int updateScore(int id,double score);


}