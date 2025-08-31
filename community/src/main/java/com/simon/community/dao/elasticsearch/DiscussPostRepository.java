package com.simon.community.dao.elasticsearch;

import com.simon.community.pojo.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author zhengx
 * @version 1.0
 */
@Repository
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost, Integer> {
    //会自动生成CRUD方法，有需要的可与根据关键词自己补充
}
