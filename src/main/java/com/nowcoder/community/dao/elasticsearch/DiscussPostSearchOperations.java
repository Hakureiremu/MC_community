package com.nowcoder.community.dao.elasticsearch;

import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscussPostSearchOperations extends ElasticsearchOperations {
}
