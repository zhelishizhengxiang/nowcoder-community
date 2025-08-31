package com.simon.community;


import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import com.simon.community.dao.elasticsearch.DiscussPostRepository;
import com.simon.community.dao.mybatis.DiscussPostMapper;
import com.simon.community.pojo.DiscussPost;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;


/**
 * @author zhengx
 * @version 1.0
 */
@SpringBootTest(classes = CommunityApplication.class)
public class elTest {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private DiscussPostRepository discussPostRepository;


    @Autowired
    private ElasticsearchOperations discussOperations;


    @Test
    public void testInsert() {
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(241));
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(242));
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(243));
    }

    @Test
    public void testInsertList() {
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(null,0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(null,100,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(null,200,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(null,300,100));
    }

    @Test
    public void testUpdate() {
        DiscussPost discussPost = discussPostMapper.selectDiscussPostById(231);
        discussPost.setContent("我是新人，使劲灌水");
        //通过覆盖内容来修改数据
        discussPostRepository.save(discussPost);
    }

    @Test
    public void testDelete() {
        discussPostRepository.deleteById(231);
    }

    @Test
    public void testDeleteList() {
        discussPostRepository.deleteAll();
    }
    @Test
    public void searchByRepository(){
        /**查询时全部使用建造器模式*/

        //1.NativeQuery：查询对象构建器，用于将 查询构建器 定义的查询条件，
        // 与分页、排序、过滤、高亮等附加参数组装成一个完整的查询请求。
        //组装出来的对象NativeSearchQuery，即整合之后的可执行的查询对象

        //2.原生查询构建器定义条件:包括范围匹配（RangeQuery）、精确匹配（TermQuery）、布尔匹配用于组装多个查询条件（boolQuery）、分词匹配（matchQuery）等，
        // 对应不同的查询方式

        //一、构建查询条件
        MultiMatchQuery multiMatchQuery =MultiMatchQuery.of(mmp->mmp
                .fields("title","content")
                .query("互联网寒冬")
        );


        //二、使用用 NativeQuery 组装查询（不止是查询条件）
        NativeQuery nativeQuery=NativeQuery.builder()
                .withQuery(multiMatchQuery._toQuery())//查询条件
                .withSort(Sort.by("type").descending())//排序
                .withSort(Sort.by("score").descending())
                .withSort(Sort.by("createTime").descending())
                .withPageable(PageRequest.of(0,10))//第0页开始，每页几条数据
                .build();

        //三、.查询结果
        SearchHits<DiscussPost> searchHits = discussOperations.search(nativeQuery, DiscussPost.class);

        //获得匹配查询的总文档数
        System.out.println(searchHits.getTotalHits());
        //获取所有命中的文档列表
        for (SearchHit searchHit : searchHits.getSearchHits()) {
            //获取命中文档对应的实体对象（
            System.out.println(searchHit.getContent());
        }
    }

    }
