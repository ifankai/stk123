package com.stk123.controller;

import com.stk123.common.db.connection.Pool;
import com.stk123.common.util.JdbcUtils;
import com.stk123.model.RequestResult;
import com.stk123.model.bo.Stk;
import com.stk123.model.elasticsearch.SearchResult;
import com.stk123.service.core.EsService;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.wltea.analyzer.cfg.DefaultConfig;
import org.wltea.analyzer.dic.Dictionary;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

@Controller
@RequestMapping("/es")  //elasticsearch
@CommonsLog
public class EsController {

    @Autowired
    private EsService esService;
    
    @RequestMapping(value = "/index/{index}", method = RequestMethod.PUT)
    @ResponseBody
    public RequestResult initIndex(@PathVariable("index")String index){
        String errorMsg = esService.initIndexByBulk(index);
        if(errorMsg != null){
            return RequestResult.failure(errorMsg);
        }
        return RequestResult.success();
    }

    @RequestMapping(value = "/index/{index}")
    @ResponseBody
    public RequestResult getIndex(@PathVariable("index")String index){
        try {
            SearchResult esResult = esService.search(index);
            return RequestResult.success(esResult.getResults());
        } catch (IOException e) {
            return RequestResult.failure(e.getMessage());
        }
    }

    @RequestMapping(value = {"/search/{keyword}/{page}", "/search/{keyword}"})
    @ResponseBody
    public RequestResult search(@PathVariable("keyword")String keyword,
                                @PathVariable(value = "page", required = false)Integer page){
        if(page == null){
            page = 1;
        }
        try {
            SearchResult esResult = esService.search(keyword, page);
            return RequestResult.success(esResult.getResults());
        } catch (IOException e) {
            return RequestResult.failure(e.getMessage());
        }
    }

    @RequestMapping(value = {"/searchby/{type}/{id}"})
    @ResponseBody
    public RequestResult searchByTypeAndId(@PathVariable("type")String type,
                                           @PathVariable("id")String id) {
        try {
            SearchResult esResult = esService.searchByTypeAndId(type, id);
            return RequestResult.success(esResult.getResults());
        } catch (IOException e) {
            return RequestResult.failure(e.getMessage());
        }
    }

    private static Date dictExtModified = new Date();
    private static Set<String> dictExt = new LinkedHashSet<>();
    private static Date dictStopModified = new Date();
    private static Set<String> dictStop = new LinkedHashSet<>();

    @RequestMapping(value = {"/dict/clear"})
    @ResponseBody
    public RequestResult dictClear() throws Exception {
        dictExtModified = new Date();
        dictExt.clear();
        dictStopModified = new Date();
        dictStop.clear();
        return RequestResult.success();
    }

    @RequestMapping(value = {"/dict/ext"})
    public void dictExt(HttpServletResponse response) throws Exception {
        response.setCharacterEncoding("utf-8");
        response.setDateHeader("Last-Modified", dictExtModified.getTime());
        response.setHeader("ETag", dictExt.hashCode()+"-"+dictExt.size());
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.getWriter().println(StringUtils.join(getExtDict(), "\n"));
        response.flushBuffer();
    }

    @RequestMapping(value = {"/dict/stop"})
    public void dictStop(HttpServletResponse response) throws Exception {
        response.setCharacterEncoding("utf-8");
        response.setDateHeader("Last-Modified", dictExtModified.getTime());
        response.setHeader("ETag", dictStop.hashCode()+"-"+dictStop.size());
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.getWriter().println(StringUtils.join(getStopDict(), "\n"));
        response.flushBuffer();
    }

    public Set<String> getStopDict() throws Exception {
        if(!dictStop.isEmpty()) return dictStop;
        log.info("getStopDict start..........");
        Connection conn = null;
        try{
            conn = Pool.getPool().getConnection();

            URL url = ResourceUtils.getURL("classpath:keyword_stop.txt");
            dictStop.addAll(IOUtils.readLines(url.openStream()));

            //加关键字
            //删除的关键字
            List<String> keyword = JdbcUtils.list(conn, "select name from stk_keyword where status=-1", String.class);
            dictStop.addAll(keyword);

            log.info("getStopDict end..........");
            return dictStop;
        }finally{
            Pool.getPool().free(conn);
        }
    }

    public Set<String> getExtDict() throws SQLException {
        if(!dictExt.isEmpty()) return dictExt;
        /*if(true){
            dictExt.add("李子园");
            return dictExt;
        }*/
        log.info("getExtDict start..........");
        Connection conn = null;
        try{
            conn = Pool.getPool().getConnection();

            //主营业务关键字
            List<String> mainBusinessWords = JdbcUtils.list(conn, "select distinct b.name name from stk_keyword_link a, stk_keyword b where b.status=0 and a.link_type=1 and a.keyword_id=b.id and a.code_type=1", String.class);
            dictExt.addAll(mainBusinessWords);
            //手动加的关键字
            List<String> words = JdbcUtils.list(conn, "select distinct sk.name from stk_keyword sk,stk_keyword_link skl where sk.status=0 and sk.id=skl.keyword_id and skl.link_type=0", String.class);
            dictExt.addAll(words);
            //加股票名称
            List<Stk> stks = JdbcUtils.list(conn, "select code,name from stk order by code", Stk.class);
            for(Stk stk : stks){
                dictExt.add(stk.getCode());
                String name = stk.getName();
                if(name == null) continue;
                if(name.indexOf(" ") > 0){
                    dictExt.add(StringUtils.replace(name, " ", ""));
                }
                dictExt.add(name);
            }
            log.info("getExtDict end..........");
            return dictExt;
        }finally{
            Pool.getPool().free(conn);
        }

    }
}
