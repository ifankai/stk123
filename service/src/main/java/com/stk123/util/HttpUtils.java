package com.stk123.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.stk123.common.util.HtmlUtils;
import com.stk123.model.core.Stock;
import com.stk123.model.projection.StockBasicProjection;
import com.stk123.util.ik.StringSimilarUtils;
import com.stk123.model.bo.StkErrorLog;
import com.stk123.common.db.util.CloseUtil;
import com.stk123.common.db.util.DBUtil;
import com.stk123.common.util.CommonHttpUtils;
import lombok.SneakyThrows;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.htmlparser.Node;

@CommonsLog
public class HttpUtils extends CommonHttpUtils {

    protected static String get0(String url, Map<String, String> reqParams, Map<String, String> requestHeaders, String enc) throws Exception {
        String response = EMPTY;
        GetMethod getMethod = null;
        StringBuffer strtTotalURL = new StringBuffer(url);

        if (reqParams != null && reqParams.keySet().size() > 0){
            if(strtTotalURL.indexOf("?") == -1) {
                strtTotalURL.append("?").append(getUrl(reqParams, enc));
            } else {
                strtTotalURL.append("&").append(getUrl(reqParams, enc));
            }
        }
        //System.out.println("GET URL =" + strtTotalURL.toString());

        HttpState httpState = new HttpState();
        client.setState(httpState);
        Proxy proxy = useProxy(httpState,url);

        try {
            getMethod = new GetMethod(strtTotalURL.toString());
            client.getParams().setParameter(HttpClientParams.HTTP_CONTENT_CHARSET, enc);
            getMethod.setRequestHeader(ContentType, ContentTypeValue + enc);
            //getMethod.setRequestHeader("Accept","image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/x-shockwave-flash, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, application/x-ms-application, application/x-ms-xbap, application/vnd.ms-xpsdocument, application/xaml+xml, */*");
            //getMethod.setRequestHeader("Accept-Language", "en-us");
            //getMethod.setRequestHeader("Accept-Encoding", "gzip, deflate");
            getMethod.setRequestHeader(UserAgent, UserAgentValue);
            //getMethod.setRequestHeader("Host", "www.chinaclear.cn");
            //getMethod.setRequestHeader("Proxy-Connection", "Keep-Alive");
            //getMethod.setRequestHeader("Cookie", "HAList=a-sz-300272-%u5F00%u80FD%u73AF%u4FDD; emstat_bc_emcount=116670576866698360; emstat_ss_emcount=2_1343740551_1174467560");
            getMethod.setRequestHeader(CacheControl, CacheControlValue);
            if(requestHeaders != null){
                for(Map.Entry<String, String> entry : requestHeaders.entrySet()){
                    getMethod.setRequestHeader(entry.getKey(), entry.getValue());
                }
            }
            /*if(getMethod.getRequestHeaders("Cookie").length > 0)
                System.out.println(getMethod.getRequestHeaders("Cookie")[0]);*/
            client.getParams().setParameter(ProtocolCookiePolicy, CookiePolicy.BROWSER_COMPATIBILITY);
            //执行getMethod
            int statusCode = client.executeMethod(getMethod);
            if(statusCode == HttpStatus.SC_OK) {
                //response = getMethod.getResponseBodyAsString();
                BufferedReader br = new BufferedReader(new InputStreamReader(getMethod.getResponseBodyAsStream(),getMethod.getResponseCharSet()));
                StringBuffer sb = new StringBuffer();
                String str= "";
                while((str = br.readLine()) != null){
                    sb.append(str);
                }
                response = sb.toString();
            }else{
                response = String.valueOf(getMethod.getStatusCode());
            }
            /*for(Header header : getMethod.getResponseHeaders()){
            	System.out.println(header.getName()+"="+header.getValue());
            }*/
            if("400".equals(response) || "404".equals(response)){
                log.info(response+"="+url);
                Connection conn = null;
                try {
                    conn = DBUtil.getConnection();
                    List<StkErrorLog> errors = ExceptionUtils.queryErrors(conn, ExceptionUtils.ERROR_CODE_999998);
                    Exception exception = new Exception("url_" + response + ":" + url);
                    String sException = ExceptionUtils.getExceptionAsString(exception);
                    boolean hasSimilar = false;
                    for(StkErrorLog error : errors){
                        if(StringSimilarUtils.getSimilarRatio(sException, error.getError()) >= 0.95){
                            hasSimilar = true;
                            break;
                        }
                    }
                    if(!hasSimilar)
                        ExceptionUtils.insertLog(conn, ExceptionUtils.ERROR_CODE_999998, exception);
                }finally {
                    CloseUtil.close(conn);
                }
            }
            /*if("502".equals(response)){
            	EmailUtils.send("HTTP代理异常【502】,stop...", url);
            	System.err.println("HTTP代理异常【502】"+url);
            	Thread.currentThread().stop();
            }*/
        }catch(HttpException e){
            System.err.println("发生致命的异常，可能是协议不对或者返回的内容有问题:"+url);
            //e.printStackTrace();
            throw e;
        }catch(IOException e){
            System.err.println("发生网络异常:"+(proxy!=null?(" proxy:"+proxy.ip+":"+proxy.port+", "):"")+url);
            //e.printStackTrace();
            throw e;
        }catch(Exception e){
            System.err.println("发生异常:"+url);
            //e.printStackTrace();
            throw e;
        }finally{
            if(getMethod != null){
                getMethod.releaseConnection();
                getMethod = null;
            }
        }
        return response;
    }

    @SneakyThrows
    //cost:29
    public static void main(String[] args) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("gjc", "润建股份");
        params.put("sslb", "1");
        params.put("sjfw", "24");
        params.put("ys", "1");
        params.put("cxzd", "bt");
        params.put("px", "sj");
        Map<String, String> requestHeaders = new HashMap<String, String>();
        requestHeaders.put("X-Requested-With", "XMLHttpRequest");
        requestHeaders.put("Cookie", "safedog-flow-item=741F60543E3740526436D199331BB62D; UM_distinctid=17e195f8b7c9c-068b01d9c77f0b-3e604809-1fa400-17e195f8b7dcd9; c=; ASPSESSIONIDSSRQTRSQ=PBPNBJJAEAFFICLBNJJCJDPG; Hm_lvt_d554f0f6d738d9e505c72769d450253d=1641103199; robih=2TdX5UjYnX6T1U; MBpermission=0; MBname=ifankai; ASPSESSIONIDAADAACBB=HOLBPAKACINGDIFAJEIHKOHO; CNZZDATA1752123=cnzz_eid%3D927885274-1641095534-%26ntime%3D1641101658; Hm_lpvt_d554f0f6d738d9e505c72769d450253d=1641103887");
        List<Header> respHeaders = new ArrayList<Header>();
        String page = HttpUtils.post("http://www.hibor.com.cn/newweb/HuiSou/sa",params, requestHeaders, "UTF-8", respHeaders);
        System.out.println(page);
        List<Node> items = HtmlUtils.getNodeListByTagNameAndAttribute(page, null, "div", "class", "result-dataitem");
        for(Node node : items){

        }
    }

}
