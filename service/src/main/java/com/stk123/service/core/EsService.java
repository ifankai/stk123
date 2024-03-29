package com.stk123.service.core;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.stk123.common.util.ChineseUtils;
import com.stk123.common.util.PinYin4jUtils;
import com.stk123.config.EsProperties;
import com.stk123.entity.StkTextEntity;
import com.stk123.model.core.Stock;
import com.stk123.model.elasticsearch.EsDocument;
import com.stk123.model.elasticsearch.SearchResult;
import com.stk123.model.projection.IndustryProjection;
import com.stk123.model.projection.StockProjection;
import com.stk123.repository.StkRepository;
import com.stk123.repository.StkTextRepository;
import com.stk123.service.StkConstant;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.HttpAsyncResponseConsumerFactory;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Boost:
 * https://www.elastic.co/guide/en/elasticsearch/reference/7.x/mapping-boost.html
 *
 * matchQuery：会将搜索词分词，再与目标查询字段进行匹配，若分词中的任意一个词与目标字段匹配上，则可查询到。
 * termQuery：不会对搜索词进行分词处理，而是作为一个整体与目标字段进行匹配，若完全匹配，则可查询到。
 *
 * ik_max_word，会对文本做最细粒度的拆分，尽可能拆分出多的词。一个字段的值需要被全文检索式，可以在创建索引时设置字段的分词模式指定为ik_max_word，这样字段内容会被最大化的分词进而生成对应的索引，这样对应的文档能更准确的被检索到。
 * ik_smart，会对文本做最粗粒度的拆分，拆分出的词相对会少些。一般检索时可以设置关键字的分词模式为ik_smart，这样能更准确的检索到预期的结果。
 *
 */
@Service
@CommonsLog
public class EsService {

    public final static String INDEX_STK = "index.stk";

    public final static String FIELD_ID = "id";
    public final static String FIELD_TYPE = "type";
    public final static String FIELD_SUB_TYPE = "subType";
    public final static String FIELD_TITLE = "title";
    public final static String FIELD_DESC = "desc";
    public final static String FIELD_CODE = "code";
    public final static String FIELD_NAME = "name";
    public final static String FIELD_CONTENT = "content";
    public final static String FIELD_INSERT_TIME = "insertTime";
    public final static String FIELD_UPDATE_TIME = "updateTime";


    @Autowired
    private StkTextRepository stkTextRepository;
    @Autowired
    private StkRepository stkRepository;
    @Autowired
    private StockService stockService;


    public final static String[] DEFAULT_SEARCH_FIELDS = new String[] {FIELD_TITLE, FIELD_DESC, FIELD_CONTENT, FIELD_NAME, FIELD_CODE};
    public final static String[] DEFAULT_HIGHLIGHT_FIELDS = new String[] {FIELD_TITLE, FIELD_DESC, FIELD_CONTENT};

    @Resource
    protected RestHighLevelClient client;

    @Resource
    private EsProperties esProperties;

    protected static final RequestOptions COMMON_OPTIONS;

    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();

        // 默认缓冲限制为100MB，此处修改为20MB。
        builder.setHttpAsyncResponseConsumerFactory(new HttpAsyncResponseConsumerFactory.HeapBufferedResponseConsumerFactory(30 * 1024 * 1024));
        COMMON_OPTIONS = builder.build();
    }

    public boolean existingIndex(String index) {
        GetIndexRequest request = new GetIndexRequest(index);
        try {
            return client.indices().exists(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
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

    protected void updateRequest(String index, String id, Object object) {
        try {
            UpdateRequest updateRequest = new UpdateRequest(index, id).doc(BeanUtil.beanToMap(object), XContentType.JSON);
            client.update(updateRequest, COMMON_OPTIONS);
        } catch (IOException e) {
            throw new RuntimeException("更新索引 {" + index + "} 数据 {" + object + "} 失败");
        }
    }


    public void createIndex(String index) {
        try {
            CreateIndexRequest request = new CreateIndexRequest(index);
            // Settings for this index
            request.settings(
                Settings.builder()
                    .put("index.number_of_shards", esProperties.getIndex().getNumberOfShards())
                    .put("index.number_of_replicas", esProperties.getIndex().getNumberOfReplicas())
                    .put("analysis.analyzer.htmlStripAnalyzer.tokenizer", "ik_max_word")
                    .putList("analysis.analyzer.htmlStripAnalyzer.char_filter", "html_strip")
            );
            Alias alias = new Alias(esProperties.getIndex().getAlias());
            request.alias(alias);

            Map<String, Object> keyword = new HashMap<>();
            keyword.put("type", "keyword");
            keyword.put("ignore_above", 256);

            Map<String, Object> properties = new HashMap<>();
            //type (stock, post, industry)
            properties.put(FIELD_TYPE, Collections.singletonMap("type", "keyword"));

            //sub_type
            properties.put(FIELD_SUB_TYPE, Collections.singletonMap("type", "keyword"));

            //id
            properties.put(FIELD_ID, Collections.singletonMap("type", "keyword"));

            //title
            Map<String, Object> title = new HashMap<>();
            title.put("type", "text");
            title.put("boost", 5);
            title.put("analyzer", "ik_smart");
            title.put("fields", Collections.singletonMap("keyword", keyword));
            properties.put(FIELD_TITLE, title);

            //name -> pingyin
            Map<String, Object> name = new HashMap<>();
            name.put("type", "text");
            name.put("boost", 5);
            name.put("analyzer", "ik_smart");
            name.put("fields", Collections.singletonMap("keyword", keyword));
            properties.put(FIELD_NAME, name);

            //desc
            Map<String, Object> desc = new HashMap<>();
            desc.put("type", "text");
            desc.put("analyzer", "ik_max_word");//ik_smart
            properties.put(FIELD_DESC, desc);

            //content
            Map<String, Object> content = new HashMap<>();
            content.put("type", "text");
            content.put("analyzer", "htmlStripAnalyzer");
            properties.put(FIELD_CONTENT, content);

            //code
            properties.put(FIELD_CODE, Collections.singletonMap("type", "text"));

            //insertTime
            properties.put(FIELD_INSERT_TIME, Collections.singletonMap("type", "date"));
            //updateTime
            properties.put(FIELD_UPDATE_TIME, Collections.singletonMap("type", "date"));

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
    public IndexResponse createDocument(String index, EsDocument object) {
        IndexRequest indexRequest = new IndexRequest(index).source(BeanUtil.beanToMap(object), XContentType.JSON);
        try {
            return client.index(indexRequest, COMMON_OPTIONS);
        } catch (IOException e) {
            log.error("createDocument", e);
            throw new RuntimeException("创建文档 {" + index + "} 失败");
        }
    }

    public IndexResponse createDocumentIfNotExisting(StkTextEntity entity) throws IOException {
        EsDocument esDocument = convertStkTextEntityToEsDocument(entity);
        return createDocumentIfNotExisting(esDocument);
    }

    public IndexResponse createDocumentIfNotExisting(EsDocument esDocument) throws IOException {
        SearchResult searchResult = this.searchByTypeAndId(esDocument.getType(), esDocument.getId());
        if(searchResult.getResults().size() > 0)
            return null;
        return createDocument(INDEX_STK, esDocument);
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

    public SearchResult searchByTypeAndId(String type, String id) throws IOException {
        SearchRequest searchRequest = new SearchRequest(INDEX_STK);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        TermsQueryBuilder termsQueryBuilder1 = QueryBuilders.termsQuery(FIELD_TYPE, type);
        TermsQueryBuilder termsQueryBuilder2 = QueryBuilders.termsQuery(FIELD_ID, id);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(termsQueryBuilder1);
        boolQueryBuilder.must(termsQueryBuilder2);
        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, COMMON_OPTIONS);
        RestStatus restStatus = searchResponse.status();
        if (restStatus != RestStatus.OK){
            return SearchResult.failure(restStatus.getStatus());
        }

        SearchHits hits = searchResponse.getHits();
        List list = Arrays.stream(hits.getHits()).map(e -> e.getSourceAsMap()).collect(Collectors.toList());
        return SearchResult.success(list);
    }

    public SearchResult search(String keyword, int page) throws IOException {
        return search(keyword, null, page, false);
    }

    /**
     * @param keyword
     * @param page 从1开始
     * @return
     * @throws IOException
     */
    public SearchResult search(String keyword, Map<String,String> otherKeywords, int page, boolean orderByTime) throws IOException {
        return search(INDEX_STK, page, 10, keyword, otherKeywords, DEFAULT_SEARCH_FIELDS, DEFAULT_HIGHLIGHT_FIELDS, orderByTime);
    }

    public SearchResult search(String keyword, Map<String,String> otherKeywords, int page, int pageSize, boolean orderByTime) throws IOException {
        return search(INDEX_STK, page, pageSize, keyword, otherKeywords, DEFAULT_SEARCH_FIELDS, DEFAULT_HIGHLIGHT_FIELDS, orderByTime);
    }

    public SearchResult search(String keyword, Map<String,String> otherKeywords, int page, int pageSize, String[] searchFields, boolean orderByTime) throws IOException {
        return search(INDEX_STK, page, pageSize, keyword, otherKeywords, searchFields, DEFAULT_HIGHLIGHT_FIELDS, orderByTime);
    }

    //https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high-search.html
    public SearchResult search(String index, int page, int pageSize,
                               String keyword, Map<String,String> otherKeywords,
                               String[] fieldNames, String[] highlightFields, boolean orderByTime) throws IOException {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //fieldNames = Arrays.stream(fieldNames).filter(field -> !StringUtils.endsWithIgnoreCase(field, "code")).toArray(String[]::new);
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(keyword, fieldNames);
        // must 相当于 与 & =
        // must not 相当于 非 ~ ！=
        // should 相当于 或 | or
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.should(multiMatchQueryBuilder);
        queryBuilder.should(QueryBuilders.wildcardQuery("code", "*"+keyword+"*").boost(5));
        queryBuilder.should(QueryBuilders.wildcardQuery("name", "*"+keyword+"*").boost(5));
        queryBuilder.should(QueryBuilders.wildcardQuery("title", "*"+keyword+"*").boost(10));
        boolQueryBuilder.must(queryBuilder);

        if(otherKeywords != null){
            otherKeywords.forEach((key, value) -> {
                String[] array = StringUtils.split(value, ","); //sub_type=300,310
                if (array.length > 1) {
                    BoolQueryBuilder qb = QueryBuilders.boolQuery();
                    for (String s : array) {
                        qb.should(QueryBuilders.termsQuery(key, s));
                    }
                    boolQueryBuilder.must(qb);
                } else {
                    TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery(key, value);
                    boolQueryBuilder.must(termsQueryBuilder);
                }
            });
        }

        searchSourceBuilder.query(boolQueryBuilder);

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        highlightBuilder.fragmentSize(10000); //最大高亮分片数
        highlightBuilder.numOfFragments(0); //从第一个分片获取高亮片段
        Arrays.stream(highlightFields).forEach(e -> {
            HighlightBuilder.Field highlightField = new HighlightBuilder.Field(e);
            highlightBuilder.field(highlightField);
        });

        searchSourceBuilder.highlighter(highlightBuilder);

        searchSourceBuilder.size(pageSize);
        searchSourceBuilder.from((page - 1) * pageSize);
        searchSourceBuilder.timeout(new TimeValue(10, TimeUnit.SECONDS));

        if(orderByTime) {
            searchSourceBuilder.sort(new FieldSortBuilder(FIELD_INSERT_TIME).order(SortOrder.DESC));
        }else{
            searchSourceBuilder.sort(SortBuilders.scoreSort().order(SortOrder.DESC));
        }

        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, COMMON_OPTIONS);
        RestStatus restStatus = searchResponse.status();
        if (restStatus != RestStatus.OK){
            return SearchResult.failure(restStatus.getStatus());
        }

        SearchHits hits = searchResponse.getHits();
        // the total number of hits, must be interpreted in the context of totalHits.relation
        long totalHits = hits.getTotalHits().value;
        float maxScore = hits.getMaxScore();
        log.info("totalHits:"+totalHits+",maxScore:"+maxScore + ", hits:"+hits.getHits().length);

        List<EsDocument> list = new ArrayList<>();
        hits.forEach(item -> {
            Map map = item.getSourceAsMap();
            Map<String, HighlightField> hlFields = item.getHighlightFields();
            hlFields.forEach((k, v) -> map.put(k, v.fragments()==null?null:v.fragments()[0].string()));
            list.add(BeanUtil.toBean(map, EsDocument.class));
        });

        return SearchResult.success(list, totalHits);
    }

    public SearchResult search(String index) throws IOException {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //searchSourceBuilder.fetchSource(FETCH_SOURCE_FIELDS_DEFAULT, Strings.EMPTY_ARRAY);
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, COMMON_OPTIONS);
        RestStatus restStatus = searchResponse.status();
        if (restStatus != RestStatus.OK){
            return SearchResult.failure(restStatus.getStatus());
        }
        SearchHits hits = searchResponse.getHits();
        List<EsDocument> list = new ArrayList<>();
        hits.forEach(item -> list.add(JSON.parseObject(item.getSourceAsString(), EsDocument.class)));
        return SearchResult.success(list);
    }

    public String addDocumentToIndexByBulk(){
        return initIndexByBulk(INDEX_STK, false, DateUtils.addDays(new Date(), -2));
    }

    public String initIndexByBulk(){
        return initIndexByBulk(INDEX_STK, true, DateUtils.addYears(new Date(), -2));
    }

    public String initIndexByBulk(String index){
        return initIndexByBulk(index, true, DateUtils.addYears(new Date(), -2));
    }

    public String initIndexByBulk(String index, boolean deleteAndCreateIndexAtFirst, Date dateAfter) {
        if(deleteAndCreateIndexAtFirst) {
            boolean existing = existingIndex(index);
            if (existing) {
                deleteIndex(index);
            }
            createIndex(index);
        }

        //stk, type=stock
        initStk(index);

        //stk_text
        BulkResponse bulkResponse = initStkText(index, dateAfter);
        if(bulkResponse.hasFailures()){
            return bulkResponse.buildFailureMessage();
        }

        return null;
    }

    private BulkResponse initStk(String index){
        List<StockProjection> list = stkRepository.findAllStk();
        List<Stock> stocks = stockService.buildStocksWithIndustries(list);
        int i=0;
        BulkResponse bulkResponse;
        for(;;) {
            ArrayList documents = new ArrayList();
            int j = i+1000 >= list.size() ? list.size() : i+1000;
            List<Stock> subList = stocks.subList(i, j);
            subList.forEach(e -> {
                EsDocument esDocument = convertStockToEsDocument(e);
                documents.add(esDocument);
            });
            bulkResponse = createDocumentByBulk(index, documents);
            if(bulkResponse.hasFailures()){
                return bulkResponse;
            }
            if(j >= list.size() -1)break;
            i = j;
        }
        return bulkResponse;
    }

    private EsDocument convertStockToEsDocument(Stock stock){
        EsDocument esDocument = new EsDocument();
        esDocument.setType(StkConstant.ES_TYPE_STOCK);
        esDocument.setSubType(stock.getMarket().name());
        //System.out.println(stock.getCode()+","+stock.getName());
        String pinyin = "";
        if(stock.getName() != null && ChineseUtils.isContainChinese(stock.getName())){
            pinyin = String.join("", Arrays.asList(PinYin4jUtils.getHeadByString(StringUtils.replace(stock.getName(), " ", ""))));
        }
        esDocument.setName(pinyin);
        esDocument.setTitle(stock.getName());
        esDocument.setDesc(StringUtils.join(stock.getIndustries().stream().map(ind -> ind.getStkIndustryTypeEntity().getName()).collect(Collectors.toList()), ", "));
        esDocument.setContent((stock.getStock().getF9()==null?"":stock.getStock().getF9()) + "<br/>" + "TODO 十大流通股东,"+stock.getCodeWithPlace());
        esDocument.setId(stock.getCodeWithPlace());
        esDocument.setCode(stock.getCode());
//        esDocument.setInsertTime(e.getInsertTime() == null ? null : e.getInsertTime().getTime());
//        esDocument.setUpdateTime(e.getUpdateTime() == null ? null : e.getUpdateTime().getTime());
        return esDocument;
    }

    private BulkResponse initStkText(String index, Date dateAfter){
        List<StkTextEntity> list = stkTextRepository.findAllByInsertTimeGreaterThanEqual(dateAfter);
        int i=0;
        BulkResponse bulkResponse;
        for(;;) {
            ArrayList documents = new ArrayList();
            int j = i+1000 >= list.size() ? list.size() : i+1000;
            List<StkTextEntity> subList = list.subList(i, j);
            subList.forEach(e -> {
                EsDocument esDocument = convertStkTextEntityToEsDocument(e);
                documents.add(esDocument);
            });
            bulkResponse = createDocumentByBulk(index, documents);
            if(bulkResponse.hasFailures()){
                return bulkResponse;
            }
            if(j >= list.size() -1)break;
            i = j;
        }
        return bulkResponse;
    }

    private EsDocument convertStkTextEntityToEsDocument(StkTextEntity e) {
        EsDocument esDocument = new EsDocument();
        String esType = StkConstant.TEXT_TYPE_MAP_ES_TYPE.get(e.getType());
        if(esType == null){
            throw new RuntimeException("Please setup es type for text type["+e.getType()+"]");
        }
        esDocument.setType(esType);
        esDocument.setSubType(e.getSubType()==null?null:e.getSubType().toString());
        esDocument.setTitle(e.getTitle());
        esDocument.setDesc(e.getTextDesc());
        esDocument.setContent(e.getText());
        esDocument.setId(e.getId().toString());
        esDocument.setCode(e.getCode());
        if(e.getType() == StkConstant.TEXT_TYPE_XUEQIU) {
            if(e.getUserId() == -1) {
                String codeWithPlace = Stock.getCodeWithPlace(e.getCode());
                esDocument.setSource("https://xueqiu.com/S/"+codeWithPlace+'/'+e.getPostId());
            }else{
                esDocument.setSource("https://xueqiu.com/"+e.getUserId()+'/'+e.getPostId());
            }
        }
        esDocument.setInsertTime(e.getUpdateTime() == null ? e.getInsertTime().getTime() : e.getUpdateTime().getTime());
        esDocument.setUpdateTime(e.getCreatedAt() == null ? null : e.getCreatedAt().getTime());
        return esDocument;
    }
}
