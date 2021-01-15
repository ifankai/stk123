package com.stk123.service.core;

import cn.hutool.core.bean.BeanUtil;
import com.stk123.config.EsProperties;
import com.stk123.entity.StkTextEntity;
import com.stk123.model.EsDocument;
import com.stk123.repository.StkTextRepository;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang3.time.DateUtils;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

/**
 * Boost:
 * https://www.elastic.co/guide/en/elasticsearch/reference/7.x/mapping-boost.html
 *
 */
@Service
@CommonsLog
public class EsService {

    @Autowired
    private StkTextRepository stkTextRepository;


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

            Map<String, Object> keyword = new HashMap<>();
            keyword.put("type", "keyword");
            keyword.put("ignore_above", 256);

            Map<String, Object> properties = new HashMap<>();
            //type (stock, post, industry)
            properties.put("type", Collections.singletonMap("type", "keyword"));

            //id
            properties.put("id", Collections.singletonMap("type", "keyword"));

            //title (name)
            Map<String, Object> title = new HashMap<>();
            title.put("type", "text");
            title.put("analyzer", "ik_smart");
            title.put("fields", Collections.singletonMap("keyword", keyword));
            properties.put("title", title);

            //desc
            Map<String, Object> desc = new HashMap<>();
            desc.put("type", "text");
            desc.put("analyzer", "ik_smart");
            properties.put("desc", desc);

            //content
            Map<String, Object> content = new HashMap<>();
            content.put("type", "text");
            content.put("analyzer", "ik_smart");
            properties.put("content", content);

            //code
            properties.put("code", Collections.singletonMap("type", "text"));

            //time
            properties.put("time", Collections.singletonMap("type", "date"));

            Map<String, Object> mapping = new HashMap<>();
            mapping.put("properties", properties);

            request.mapping(mapping);
            CreateIndexResponse createIndexResponse = client.indices().create(request, COMMON_OPTIONS);

            log.info(" whether all of the nodes have acknowledged the request : " + createIndexResponse.isAcknowledged());
            log.info(" Indicates whether the requisite number of shard copies were started for each shard in the index before timing out :" + createIndexResponse.isShardsAcknowledged());
        } catch (IOException e) {
            throw new RuntimeException("创建索引 {" + index + "} 失败");
        }
    }

    //not set id, es will automatically generated id
    public IndexResponse createDocument(String index, Object object) {
        IndexRequest indexRequest = new IndexRequest(index).source(BeanUtil.beanToMap(object), XContentType.JSON);
        try {
            return client.index(indexRequest, COMMON_OPTIONS);
        } catch (IOException e) {
            log.error("createDocument", e);
            throw new RuntimeException("创建文档 {" + index + "} 失败");
        }
    }

    public <T> BulkResponse createDocumentByBulk(String index, List<EsDocument> list) {
        BulkRequest request = new BulkRequest();
        list.forEach(e -> {
            IndexRequest indexRequest = new IndexRequest(index).source(BeanUtil.beanToMap(e), XContentType.JSON);
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


    public BulkResponse initIndexByBulk(String index) {
        boolean existing = existingIndex(index);
        if(existing){
            deleteIndex(index);
        }
        createIndex(index);
        List<StkTextEntity> list = stkTextRepository.findAllByCodeAndCreatedAtGreaterThanOrderByInsertTimeDesc("600600", DateUtils.addYears(new Date(), -1));
        List<EsDocument> documents = new ArrayList();
        list.forEach(e -> {
            EsDocument esDocument = new EsDocument();
            esDocument.setType("post");
            esDocument.setTitle(e.getTitle());
            esDocument.setDesc(e.getTextDesc());
            esDocument.setContent(e.getText());
            esDocument.setId(e.getId().toString());
            esDocument.setCode(e.getCode());
            esDocument.setTime(e.getUpdateTime()==null?e.getInsertTime():e.getUpdateTime());
            documents.add(esDocument);
        });
        return createDocumentByBulk(index, documents);
    }
}
