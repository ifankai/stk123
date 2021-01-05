package com.stk123.service.core;

import cn.hutool.core.bean.BeanUtil;
import com.stk123.config.EsProperties;
import lombok.extern.apachecommons.CommonsLog;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.HttpAsyncResponseConsumerFactory;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;

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

    public boolean existingIndex(String index) {
        GetIndexRequest request = new GetIndexRequest(index);
        try {
            return client.indices().exists(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("existingIndex", e);
            throw new RuntimeException("索引 {" + index + "} 失败："+e.getMessage());
        }
    }

    public void deleteIndex(String index) {
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(index);
        try {
            client.indices().delete(deleteIndexRequest, COMMON_OPTIONS);
        } catch (IOException e) {
            throw new RuntimeException("删除索引 {" + index + "} 失败");
        }
    }

    public void createIndex(String index) {
        try {
            CreateIndexRequest request = new CreateIndexRequest(index);
            // Settings for this index
            request.settings(Settings.builder().put("index.number_of_shards", esProperties.getIndex().getNumberOfShards()).put("index.number_of_replicas", esProperties.getIndex().getNumberOfReplicas()));
            Alias alias = new Alias(esProperties.getIndex().getAlias());
            request.alias(alias);
            CreateIndexResponse createIndexResponse = client.indices().create(request, COMMON_OPTIONS);

            log.info(" whether all of the nodes have acknowledged the request : " + createIndexResponse.isAcknowledged());
            log.info(" Indicates whether the requisite number of shard copies were started for each shard in the index before timing out :" + createIndexResponse.isShardsAcknowledged());
        } catch (IOException e) {
            throw new RuntimeException("创建索引 {" + index + "} 失败");
        }
    }

    public IndexResponse createDocument(String index, String id, Object object) {
        IndexRequest indexRequest = new IndexRequest(index).id(id).source(BeanUtil.beanToMap(object), XContentType.JSON);
        try {
            return client.index(indexRequest, COMMON_OPTIONS);
        } catch (IOException e) {
            log.error("createDocument", e);
            throw new RuntimeException("创建文档 {" + index + "} 失败, ID {"+ id +"}");
        }
    }

    public <T> BulkResponse createDocumentByBulk(String index, List<T> list, Function<T, String> fun) {
        BulkRequest request = new BulkRequest();
        list.forEach(e -> {
            String id = fun.apply(e);
            IndexRequest indexRequest = new IndexRequest(index).id(id).source(BeanUtil.beanToMap(e), XContentType.JSON);
            request.add(indexRequest);
        });
        try {
            return client.bulk(request, COMMON_OPTIONS);
        } catch (IOException e) {
            log.error("createDocumentByBulk", e);
            throw new RuntimeException("创建文档批量 {" + index + "} 失败");
        }
    }

    //https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high-search.html
    public SearchResponse searchByIndex(String index) {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //searchSourceBuilder.fetchSource(FETCH_SOURCE_FIELDS_DEFAULT, Strings.EMPTY_ARRAY);
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
