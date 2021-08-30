package com.stk123.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.stk123.common.CommonUtils;
import com.stk123.common.util.BeanUtils;
import com.stk123.entity.StkTextEntity;
import com.stk123.model.RequestResult;
import com.stk123.model.core.Stock;
import com.stk123.model.dto.PageRoot;
import com.stk123.model.dto.TextDto;
import com.stk123.model.elasticsearch.EsDocument;
import com.stk123.model.projection.StockBasicProjection;
import com.stk123.repository.StkRepository;
import com.stk123.repository.StkTextRepository;
import com.stk123.service.StkConstant;
import com.stk123.service.core.EsService;
import com.stk123.service.core.StockService;
import com.stk123.service.core.TextService;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/text")
@CommonsLog
public class TextController {

    @Autowired
    private StkTextRepository stkTextRepository;
    @Autowired
    private TextService textService;
    @Autowired
    private EsService esService;
    @Autowired
    private StkRepository stkRepository;
    @Autowired
    private StockService stockService;

    @RequestMapping(value = {"","/phone/{type}"}, method = RequestMethod.GET)
    @ResponseBody
    public RequestResult<PageRoot<EsDocument>>
                queryByType(@PathVariable(value = "type", required = false)String type,
                            @RequestParam(value = "createdAtAfter", required = false)Long createdAtAfter,
                            @RequestParam(value = "code", required = false)String code,
                            @RequestParam(value = "keyword", required = false)String keyword,
                            @RequestParam(value = "page", required = false)Integer page,
                            @RequestParam(value = "pageSize", required = false)Integer pageSize,
                            @RequestParam(value = "idBefore", required = false)Long idBefore,
                            @RequestParam(value = "idAfter", required = false)Long idAfter
    ) throws Exception {
        log.info("query....." + type);
        List<StkTextEntity> list = null;
        Long total = null;

        if(createdAtAfter == null) {
            createdAtAfter = DateUtils.addYears(new Date(), -1).getTime();
        }
        Date dateAfter = new Date(createdAtAfter);

        if(type == null || StringUtils.equals(type, "all")) {
            if(idBefore == null && idAfter == null) {
                list = stkTextRepository.findAllByInsertTimeGreaterThanEqualOrderByInsertTimeDescIdDesc(dateAfter, PageRequest.of(0, pageSize));
            }else if(idBefore == null && idAfter != null){
                list = stkTextRepository.findAllByIdGreaterThanOrderByInsertTimeDescIdDesc(idAfter);
            }else if(idBefore != null && idAfter == null){
                list = stkTextRepository.findAllByIdLessThanOrderByInsertTimeDescIdDesc(idBefore, PageRequest.of(0, pageSize));
            }
        }else if(StringUtils.equals(type, "favorite")) {
            if(idBefore == null && idAfter == null) {
                list = stkTextRepository.findAllByFavoriteDateNotNullAndInsertTimeGreaterThanEqualOrderByInsertTimeDescIdDesc(dateAfter, PageRequest.of(0, pageSize));
            }else if(idBefore == null && idAfter != null){
                list = stkTextRepository.findAllByFavoriteDateNotNullAndIdGreaterThanOrderByInsertTimeDescIdDesc(idAfter);
            }else if(idBefore != null && idAfter == null){
                list = stkTextRepository.findAllByFavoriteDateNotNullAndIdLessThanOrderByInsertTimeDescIdDesc(idAfter, PageRequest.of(0, pageSize));
            }
        }/*else if(StringUtils.equals(type, "search")) {
            if(page == null){
                page = 1;
            }
            if (keyword != null) {
                try {
                    keyword = URLDecoder.decode(keyword, "UTF-8");

                    list = new ArrayList<>();
                    SearchResult searchResult = esService.search(keyword, page);
                    for(int i=0; i<searchResult.getResults().size(); i++) {
                        EsDocument esDocument = searchResult.getResults().get(i);
                        StkTextEntity stkTextEntity = new StkTextEntity();
                        stkTextEntity.setId(new Long(esDocument.getId()));
                        stkTextEntity.setCode(esDocument.getCode());
                        stkTextEntity.setTitle(esDocument.getTitle());
                        stkTextEntity.setTextDesc(esDocument.getDesc());
                        stkTextEntity.setText(esDocument.getContent());
                        stkTextEntity.setInsertTime(new Date(esDocument.getInsertTime()));
                        stkTextEntity.setUpdateTime(new Date(esDocument.getUpdateTime()));

                        list.add(stkTextEntity);
                    }
                } catch (Exception e) {
                    log.error("textcontroller.query", e);
                    return RequestResult.failure(PageRoot.unPageable(null, count));
                }
                System.out.println(list);
                return RequestResult.success(PageRoot.pageable(list, page, pageSize, count));
            }
        }*/

        List<StockBasicProjection> stocks = stkRepository.findAllByCodes(list.stream().map(e -> e.getCode()).collect(Collectors.toList()));

        List<EsDocument> results = new ArrayList<>();
        for (StkTextEntity stkTextEntity : list) {
            EsDocument esDocument = BeanUtils.map(stkTextEntity, EsDocument.class, new PropertyMap<StkTextEntity, EsDocument>() {
                @Override
                protected void configure() {
                    map().setDesc(source.getTextDesc());
                    map().setContent(source.getText());
                }
            });
            esDocument.setType("post");
            esDocument.setPost(stkTextEntity);
            //esDocument.setStock(stocks.stream().filter(e -> e.getCode().equals(esDocument.getCode())).findFirst().orElse(null));
            results.add(esDocument);
        }

        return RequestResult.success(PageRoot.unPageable(results, total));
    }

    @RequestMapping("/favorite/{id}/{isFavorite}")
    @ResponseBody
    public RequestResult favorite(@PathVariable("id")Long id, @PathVariable("isFavorite")int isFavorite){
        StkTextEntity post = stkTextRepository.findById(id).get();
        post.setFavoriteDate(isFavorite==1 ? new Date() : null);
        stkTextRepository.save(post);
        return RequestResult.SUCCESS;
    }

    @RequestMapping("/save")
    public RequestResult save(){
        StkTextEntity stkTextEntity = new StkTextEntity();
        stkTextEntity.setCode("100000");
        stkTextEntity.setType(3);
        stkTextEntity.setTitle("title1");
        stkTextEntity.setInsertTime(new Date());
        StkTextEntity entity = stkTextRepository.save(stkTextEntity);
        return RequestResult.success("create new entity id:"+entity.getId());
    }

    @RequestMapping("/{code}/{type}")
    public RequestResult query(@PathVariable String code, @PathVariable Integer type){
//        List<StkTextEntity> result = stkTextRepository.findAllTextByDto(code, type);
        List<TextDto> result = stkTextRepository.findAllByCodeAndTypeOrderByInsertTimeDesc2Dto(code, type);
        return RequestResult.success(result);
    }

    @RequestMapping("/post/{code}")
    public RequestResult post(@PathVariable String code,
                               @RequestParam(value = "start", required = false)String start,
                               @RequestParam(value = "end", required = false)String end){
        Date dateStart = start == null ? CommonUtils.addDay(new Date(), -365) : CommonUtils.parseDate(start);
        Date dateEnd = end == null ? new Date() : CommonUtils.parseDate(end);
        List<StkTextEntity> result = stkTextRepository.findAllByCodeAndTypeAndInsertTimeBetweenOrderByInsertTimeDesc(code, StkConstant.TEXT_TYPE_XUEQIU, dateStart, dateEnd);
        return RequestResult.success(result);
    }

    @RequestMapping("/report/{code}")
    public RequestResult researchReport(@PathVariable String code,
                              @RequestParam(value = "start", required = false)String start,
                              @RequestParam(value = "end", required = false)String end){
        Date dateStart = start == null ? CommonUtils.addDay(new Date(), -365) : CommonUtils.parseDate(start);
        Date dateEnd = end == null ? new Date() : CommonUtils.parseDate(end);
        List<StkTextEntity> result = stkTextRepository.findAllByCodeAndTypeAndSubTypeAndInsertTimeBetweenOrderByInsertTimeDesc(code, StkConstant.TEXT_TYPE_LONG_TEXT,
                StkConstant.TEXT_SUB_TYPE_COMPANY_RESEARCH, dateStart, dateEnd);
        return RequestResult.success(result);
    }

    @RequestMapping({"/notice", "/notice/{createdAtAfter}"})
    @Cached(name = "hot-notice", expire = 600, cacheType = CacheType.LOCAL)
    public RequestResult hotNotice(@PathVariable(value = "createdAtAfter", required = false)Long createdAtAfter){
        log.info("hot notice.......................");
        if(createdAtAfter == null) {
            createdAtAfter = DateUtils.addMonths(new Date(), -1).getTime();
        }
        Date dateAfter = new Date(createdAtAfter);
        List<StkTextEntity> result = stkTextRepository.findAllByTypeAndCodeTypeAndSubTypeAndReplyPositiveAndInsertTimeGreaterThanOrderByInsertTimeDesc(
                StkConstant.TEXT_TYPE_XUEQIU, StkConstant.TEXT_CODE_TYPE_STOCK, StkConstant.TEXT_SUB_TYPE_XUEQIU_NOTICE, 1, dateAfter);
        return RequestResult.success(getNoticeAsMap(result));
    }
    
    private List<Map> getNoticeAsMap(List<StkTextEntity> result){
        Map<String, Stock> stocksMap = stockService.getStocks(result.stream().map(StkTextEntity::getCode).collect(Collectors.toList())).stream().distinct().collect(Collectors.toMap(Stock::getCode, Function.identity()));
        return result.stream().map(text -> {
            Map map = BeanUtil.beanToMap(text);
            Stock stock = stocksMap.get(text.getCode());
            if(stock == null) {
                log.info("stock code not in database:"+text.getCode());
                map.put("nameAndCodeWithLink", text.getCode());
                map.put("reply", text.getReplyCount());
            }else{
                map.put("nameAndCodeWithLink", stock.getNameAndCodeWithLink());
                map.put("reply", CommonUtils.wrapLink(text.getReplyCount()+"", "https://xueqiu.com/S/"+stock.getCodeWithPlace()+"/"+text.getPostId()));
            }
            map.put("createdAt", CommonUtils.formatDate(text.getCreatedAt(), CommonUtils.sf_ymd9));
            map.put("insertTime", CommonUtils.formatDate(text.getUpdateTime() == null ? text.getInsertTime() : text.getUpdateTime(), CommonUtils.sf_ymd9));

            return map;
        }).collect(Collectors.toList());
    }

}
