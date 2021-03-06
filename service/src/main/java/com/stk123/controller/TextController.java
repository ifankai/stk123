package com.stk123.controller;

import com.stk123.common.util.BeanUtils;
import com.stk123.entity.StkTextEntity;
import com.stk123.model.RequestResult;
import com.stk123.model.dto.PageRoot;
import com.stk123.model.dto.TextDto;
import com.stk123.model.elasticsearch.EsDocument;
import com.stk123.model.projection.StockBasicProjection;
import com.stk123.repository.StkRepository;
import com.stk123.repository.StkTextRepository;
import com.stk123.service.core.EsService;
import com.stk123.service.core.TextService;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

    @RequestMapping(value = {"","/{type}"}, method = RequestMethod.GET)
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
        Integer count = null;

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
            esDocument.setStock(stocks.stream().filter(e -> e.getCode().equals(esDocument.getCode())).findFirst().orElse(null));
            results.add(esDocument);
        }

        return RequestResult.success(PageRoot.unPageable(results, count));
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

}
