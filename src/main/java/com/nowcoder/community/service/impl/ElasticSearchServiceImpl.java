package com.nowcoder.community.service.impl;

import com.nowcoder.community.dao.elasticsearch.DiscussPostRepository;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.service.ElasticSearchService;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ElasticSearchServiceImpl implements ElasticSearchService {
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private DiscussPostRepository discussPostRepository;

    @Override
    public void saveDiscussPost(DiscussPost post){
        discussPostRepository.save(post);
    }

    @Override
    public void deleteDiscussPost(int id){
        discussPostRepository.deleteById(id);
    }

    @Override
    public Page<DiscussPost> searchDisucssPost(String keyword, int current, int limit) {
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(keyword, "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(current, limit))
                .withHighlightFields(
                        //高亮显示 红色
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();

        SearchHits<DiscussPost> searchHits = elasticsearchRestTemplate.search(searchQuery, DiscussPost.class);
        List<DiscussPost> postList = new ArrayList<>();

        if (searchHits != null) {
            for (SearchHit hit : searchHits) {
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
        }

        SearchPage<DiscussPost> searchPage = SearchHitSupport.searchPageFor(searchHits, searchQuery.getPageable());
        Page<DiscussPost> page = new PageImpl<>(postList, searchPage.getPageable(), searchPage.getTotalElements());

        return page;
    }

}
