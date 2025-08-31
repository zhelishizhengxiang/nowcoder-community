package com.simon.community.service;

import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import com.simon.community.dao.elasticsearch.DiscussPostRepository;
import com.simon.community.pojo.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

/**
 * @author zhengx
 * @version 1.0
 */
@Service
public class ElasticsearchService {

    @Autowired
    private DiscussPostRepository discussPostRepository;

    @Autowired
    private ElasticsearchOperations discussOperations;

    /**
     * 向es服务器中提交（修改）新帖子
     * */
    public void saveDiscussPost(DiscussPost discussPost) {
        discussPostRepository.save(discussPost);
    }

    /**
     * 删除es服务器中的帖子
     * */
    public void deleteDiscussPost(int id) {
        discussPostRepository.deleteById(id);
    }

    /**
     * 查询有对应的内容的帖子
     * */
    public SearchHits<DiscussPost> searchDiscussPost(String keyword, int currentPage, int limit) {
        //一、构建查询条件
        MultiMatchQuery multiMatchQuery =MultiMatchQuery.of(mmp->mmp
                .fields("title","content")
                .query(keyword)
        );

        //二、使用用 NativeQuery 组装查询（不止是查询条件）
        NativeQuery nativeQuery=NativeQuery.builder()
                .withQuery(multiMatchQuery._toQuery())//查询条件
                .withSort(Sort.by("type").descending())//排序
                .withSort(Sort.by("score").descending())
                .withSort(Sort.by("createTime").descending())
                .withPageable(PageRequest.of(currentPage,limit))//第0页开始，每页几条数据
                .build();

        //三、.查询结果
        SearchHits<DiscussPost> searchHits = discussOperations.search(nativeQuery, DiscussPost.class);
        return  searchHits;
    }
}
