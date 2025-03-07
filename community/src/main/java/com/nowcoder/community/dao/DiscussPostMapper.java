package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**社区首页显示帖子的DAO层，Mapper接口*/
@Mapper
public interface DiscussPostMapper {

    //做的是分页查询帖子的功能，查到的是多条数据，所以返回的是一个集合
    //这里接口这么定的原因是，传userId是为了到时候显示用户主页有该用户的所发布的贴纸，所以需要userid
    //而挡在首页显示时，就不需要传入userid，具体的实现逻辑在写sql语句的xml文件问体现
    /**
     * @param userId 用户id
     * @param limit  每一页最多显示数据的条数
     * @param offset 每一页起始行的行号
     * @usage 分页查询帖子的功能，查到的是多条数据，所以返回的是一个集合
     * */
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    // @Param注解用于给参数取别名,有时候参数名字会过长
    // 如果在mysql中需要用到动态条件，并且条件里需要用到该参数，并且只有一个参数,并且在<if>（动态条件）里使用,则必须加别名.
    /**
     * @usage 用于返回帖子的数量
     * */
    int selectDiscussPostRows(@Param("userId") int userId);

}
