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

    private static boolean dictExtFinished = false;
    private static boolean dictStopFinished = false;

    @RequestMapping(value = {"/dict/clear"})
    @ResponseBody
    public RequestResult dictClear() throws Exception {
        dictExtFinished = false;
        dictStopFinished = false;
        return RequestResult.success();
    }

    @RequestMapping(value = {"/dict/ext"})
    public void dictExt(HttpServletResponse response) throws Exception {
        response.setCharacterEncoding("utf-8");
        response.addHeader("Last-Modified", new Date().toString());
        if(!dictExtFinished) {
            dictExtFinished = true;
            response.getWriter().println(StringUtils.join(getExtDict(), "\n"));
        }else{
            response.setStatus(HttpStatus.NOT_MODIFIED.value());
        }
    }

    @RequestMapping(value = {"/dict/stop"})
    public void dictStop(HttpServletResponse response) throws Exception {
        response.setCharacterEncoding("utf-8");
        response.addHeader("Last-Modified", new Date().toString());
        if(!dictStopFinished) {
            dictStopFinished = true;
            response.getWriter().println(StringUtils.join(getStopDict(), "\n"));
        }else{
            response.setStatus(HttpStatus.NOT_MODIFIED.value());
        }
    }

    public Set<String> getStopDict() throws Exception {
        log.info("getStopDict start..........");
        Set<String> stop = new LinkedHashSet<>();
        Connection conn = null;
        try{
            conn = Pool.getPool().getConnection();

            URL url = ResourceUtils.getURL("classpath:keyword_stop.txt");
            stop.addAll(IOUtils.readLines(url.openStream()));

            //加关键字
            //删除的关键字
            List<String> keyword = JdbcUtils.list(conn, "select name from stk_keyword where status=-1", String.class);
            stop.addAll(keyword);

            log.info("getStopDict end..........");
            return stop;
        }finally{
            Pool.getPool().free(conn);
        }
    }

    public Set<String> getExtDict() throws SQLException {
        log.info("getExtDict start..........");
        Set<String> ext = new LinkedHashSet<>();
        Connection conn = null;
        try{
            conn = Pool.getPool().getConnection();

            //主营业务关键字
            List<String> mainBusinessWords = JdbcUtils.list(conn, "select distinct b.name name from stk_keyword_link a, stk_keyword b where b.status=0 and a.link_type=1 and a.keyword_id=b.id and a.code_type=1", String.class);
            ext.addAll(mainBusinessWords);
            //手动加的关键字
            List<String> words = JdbcUtils.list(conn, "select distinct sk.name from stk_keyword sk,stk_keyword_link skl where sk.status=0 and sk.id=skl.keyword_id and skl.link_type=0", String.class);
            ext.addAll(words);
            //加股票名称
            List<Stk> stks = JdbcUtils.list(conn, "select code,name from stk order by code", Stk.class);
            for(Stk stk : stks){
                ext.add(stk.getCode());
                String name = stk.getName();
                if(name == null) continue;
                if(name.indexOf(" ") > 0){
                    ext.add(StringUtils.replace(name, " ", ""));
                }
                ext.add(name);
            }
            log.info("getExtDict end..........");
            return ext;
        }finally{
            Pool.getPool().free(conn);
        }

    }
}
