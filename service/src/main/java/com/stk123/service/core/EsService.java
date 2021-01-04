package com.stk123.service.core;

import com.stk123.config.EsProperties;
import lombok.extern.apachecommons.CommonsLog;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.HttpAsyncResponseConsumerFactory;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * Boost:
 * https://www.elastic.co/guide/en/elasticsearch/reference/7.x/mapping-boost.html
 *
 */
@Service
@CommonsLog
public class EsService {

    private final static String[] FETCH_SOURCE_FIELDS_DEFAULT = new String[] {"title", "innerObject.*"};

    @Resource
    protected RestHighLevelClient client;

    @Resource
    private EsProperties esProperties;

    protected static final RequestOptions COMMON_OPTIONS;

    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();

        // 默认缓冲限制为100MB，此处修改为30MB。
        builder.setHttpAsyncResponseConsumerFactory(new HttpAsyncResponseConsumerFactory.HeapBufferedResponseConsumerFactory(30 * 1024 * 1024));
        COMMON_OPTIONS = builder.build();
    }


    public void createIndex(String index) {
        try {
            CreateIndexRequest request = new CreateIndexRequest(index);
            // Settings for this index
            request.settings(Settings.builder().put("index.number_of_shards", esProperties.getIndex().getNumberOfShards()).put("index.number_of_replicas", esProperties.getIndex().getNumberOfReplicas()));
            //request.alias()
            CreateIndexResponse createIndexResponse = client.indices().create(request, COMMON_OPTIONS);

            log.info(" whether all of the nodes have acknowledged the request : " + createIndexResponse.isAcknowledged());
            log.info(" Indicates whether the requisite number of shard copies were started for each shard in the index before timing out :" + createIndexResponse.isShardsAcknowledged());
        } catch (IOException e) {
            throw new RuntimeException("创建索引 {" + index + "} 失败");
        }
    }

    //https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high-search.html
    public SearchResponse searchByIndex(String index) {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.fetchSource(FETCH_SOURCE_FIELDS_DEFAULT, null);
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = null;
        try {
            searchResponse = client.search(searchRequest, COMMON_OPTIONS);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return searchResponse;
    }

}
