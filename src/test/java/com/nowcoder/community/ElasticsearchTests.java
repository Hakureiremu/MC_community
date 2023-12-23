package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.elasticsearch.DiscussPostRepository;
import com.nowcoder.community.entity.DiscussPost;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class ElasticsearchTests {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private DiscussPostRepository discussPostRepository;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

//    @Autowired
//    private ElasticsearchTemplate elasticTemplate;

    @Test
    public void testInsert(){
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(281));
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(280));
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(277));
    }

    @Test
    public void testInsertList(){
        //初始化
//        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(101, 0, 100));
//        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(102, 0, 100));
//        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(103, 0, 100));
//        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(111, 0, 100));
//        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(112, 0, 100));
//        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(131, 0, 100));
//        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(132, 0, 100));
//        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(133, 0, 100));
//        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(134, 0, 100));
//        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(231, 0, 100));
    }

    @Test
    public void testUpdate(){
        DiscussPost post = discussPostMapper.selectDiscussPostById(231);
        post.setContent("我是新人，灌水");
        discussPostRepository.save(post);
    }

    @Test
    public void delete(){
        discussPostRepository.deleteById(231);
    }

    @Test
    public void testSearchByTemplate(){
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0,10))
                .withHighlightFields(
                        //高亮显示
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();

        SearchHits<DiscussPost> searchHits = elasticsearchRestTemplate.search(searchQuery, DiscussPost.class);

        if (searchHits != null) {
            List<DiscussPost> postList = new ArrayList<>();
            for (SearchHit hit : searchHits){
                DiscussPost post = (DiscussPost) hit.getContent();
                //处理高亮显示
                Map<String, List<String>> highlightFields = hit.getHighlightFields();
                if (highlightFields.containsKey("title")) {
                    List<String> titleHighlights = highlightFields.get("title");
                    post.setTitle(String.join(" ", titleHighlights));
                }

                if (highlightFields.containsKey("content")) {
                    List<String> contentHighlights = highlightFields.get("content");
                    post.setContent(String.join(" ", contentHighlights));
                }
                postList.add(post);
            }

            SearchPage<DiscussPost> searchPage = SearchHitSupport.searchPageFor(searchHits, searchQuery.getPageable());

            if (searchPage != null) {
                Page<DiscussPost> page = new PageImpl<>(postList, searchPage.getPageable(), searchPage.getTotalElements());

                System.out.println(page.getTotalElements());
                System.out.println(page.getTotalPages());
                System.out.println(page.getNumber());
                System.out.println(page.getSize());

                for (DiscussPost post : page) {
                    System.out.println(post);
                }
            } else {
                System.out.println("SearchPage is null");
            }
        } else {
            System.out.println("SearchHits is null");
        }

    }

}
