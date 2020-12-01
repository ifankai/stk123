package com.stk123.common.util;

import com.sun.net.ssl.HostnameVerifier;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.lang.StringUtils;

import javax.net.ssl.SSLSession;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

@CommonsLog
public class CommonHttpUtils {

    public static int NO_OF_RETRY = 3;

    public static final String POST = "POST";

    protected static boolean useProxy = false;
    protected static String useProxyURL = null;

    protected static List<Proxy> proxys = Collections.synchronizedList(new ArrayList<Proxy>());

    protected static class Proxy{
        public String ip = null;
        public int port = 0;
        public Proxy(String ip, int port){
            this.ip = ip;
            this.port = port;
        }
    }

    static{
        //http://pachong.org/area/short/name/cn.html
        proxys.add(null);
        proxys.add(new CommonHttpUtils.Proxy("116.236.216.116",8080));
        proxys.add(new CommonHttpUtils.Proxy("218.92.227.165",29786));
        proxys.add(new CommonHttpUtils.Proxy("36.250.74.88",8101));
        proxys.add(null);
        proxys.add(new CommonHttpUtils.Proxy("183.207.228.8",80));
        proxys.add(new CommonHttpUtils.Proxy("220.248.41.106",110));
        proxys.add(new CommonHttpUtils.Proxy("218.92.227.165",21571));
        proxys.add(null);
        proxys.add(new CommonHttpUtils.Proxy("113.105.224.87",80));
        proxys.add(new CommonHttpUtils.Proxy("117.167.202.106",8123));
        proxys.add(new CommonHttpUtils.Proxy("183.207.237.11", 83));
        ///

    }
    /**
     * 定义编码格式 UTF-8
     */
    public static final String URL_PARAM_DECODECHARSET_UTF8 = "UTF-8";

    /**
     * 定义编码格式 GBK
     */
    public static final String URL_PARAM_DECODECHARSET_GBK = "GBK";

    protected static final String URL_PARAM_CONNECT_FLAG = "&";
    protected static final String NAME_VALUE_SEPARATOR = "=";

    protected static final String EMPTY = "";

    private static MultiThreadedHttpConnectionManager connectionManager = null;
    private static SimpleHttpConnectionManager simpleConnectionManager = null;

    private static int connectionTimeOut = 60000;   //1分钟
    private static int socketTimeOut = 60000;
    private static int maxConnectionPerHost = 20;
    private static int maxTotalConnections = 20;

    protected static HttpClient client;

    private static String userName = System.getProperty("user.name");


    static{
        connectionManager = new MultiThreadedHttpConnectionManager();
        //simpleConnectionManager = new SimpleHttpConnectionManager();
        connectionManager.getParams().setConnectionTimeout(connectionTimeOut);
        connectionManager.getParams().setSoTimeout(socketTimeOut);
        connectionManager.getParams().setDefaultMaxConnectionsPerHost(maxConnectionPerHost);
        connectionManager.getParams().setMaxTotalConnections(maxTotalConnections);

        ProtocolSocketFactory fcty = new MySecureProtocolSocketFactory();
        Protocol.registerProtocol("https", new Protocol("https", fcty, 443));

        client = new HttpClient(connectionManager);
        //client = new HttpClient(simpleConnectionManager);

        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
        //System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire", "error");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "error");
    }

    public static HttpClient getHttpClient(){
        return client;
    }

    public static void setUseProxy(boolean useProxy, String useProxyURL){
        CommonHttpUtils.useProxy = useProxy;
        CommonHttpUtils.useProxyURL = useProxyURL;
        if(useProxy){
            //connectionTimeOut = connectionTimeOut * 2;
            //NO_OF_RETRY = 4;
        }
    }

    private static int i = 0;
    protected static synchronized Proxy useProxy(HttpState httpState,String url){
    	/*if("kevin.fan.bak".equalsIgnoreCase(userName)){
	        NTCredentials credentials = new NTCredentials("kevin.fan","Ebaotech2008","","");
	        client.getHostConfiguration().setProxy("shtmg.ebaotech.com", 8080);
	        httpState.setProxyCredentials(AuthScope.ANY, credentials);
        }*/
        Proxy proxy = null;
        if(useProxy && url.contains(useProxyURL)){
            proxy = proxys.get(i++);
            if(proxy != null){
                client.getHostConfiguration().setProxy(proxy.ip, proxy.port);
                httpState.setProxyCredentials(AuthScope.ANY, null);
                //System.out.println("use proxy:"+proxy.ip+":"+proxy.port+"-"+url);
            }
            if(i == proxys.size())i=0;
        }
        return proxy;
    }

    /**
     * POST方式提交数据
     * @param url   待请求的URL
     * @param params   要提交的数据
     * @param enc   编码
     * @return   响应结果
     */
    public static String post(String url, Map<String, String> params, String enc){
        return CommonHttpUtils.post(url, params, null,null, enc, null);
    }

    public static String post(String url, String body, String enc){
        return CommonHttpUtils.post(url, null, body,null, enc, null);
    }

    public static String post(String url, Map<String, String> params, String enc, List<Header> respHeaders){
        return CommonHttpUtils.post(url, params, null, null, enc, respHeaders);
    }

    public static String post(String url, Map<String, String> params, String body, String enc, List<Header> respHeaders){
        return CommonHttpUtils.post(url, params, body, null, enc, respHeaders);
    }

    public static String post(String url, Map<String, String> params, Map<String, String> requestHeaders, String enc, List<Header> respHeaders){
        return CommonHttpUtils.post(url, params, null, requestHeaders, enc, respHeaders);
    }

    public static String post(String url, Map<String, String> params,String body, Map<String, String> requestHeaders, String enc, List<Header> respHeaders){
        String response = EMPTY;
        PostMethod postMethod = null;

        HttpState httpState = new HttpState();
        client.setState(httpState);
        useProxy(httpState,url);

        try {
            postMethod = new PostMethod(url);
            postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=" + enc);
            postMethod.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.122 Safari/537.36");
            postMethod.setRequestHeader("Cache-Control","no-cache");
            if(requestHeaders != null){
                for(Map.Entry<String, String> entry : requestHeaders.entrySet()){
                    postMethod.setRequestHeader(entry.getKey(), entry.getValue());
                }
            }
            //postMethod.getParams().setParameter("http.protocol.cookie-policy",CookiePolicy.BROWSER_COMPATIBILITY);
            //将表单的值放入postMethod中
            /*if(params != null){
	            Set<String> keySet = params.keySet();
	            for(String key : keySet){
	                String value = params.get(key);
	                postMethod.addParameter(key, value);
	            }
            }*/
            if(params != null){
                List<NameValuePair> nvps = new ArrayList <NameValuePair>();

                Set<String> keySet = params.keySet();
                for(String key : keySet) {
                    nvps.add(new NameValuePair(key, params.get(key)));
                }
                postMethod.setRequestBody(nvps.toArray(new NameValuePair[nvps.size()]));
            }
            if(body != null){
                postMethod.setRequestBody(body);
            }
            //执行postMethod
            int statusCode = client.executeMethod(postMethod);
            if(statusCode == HttpStatus.SC_OK) {
                if(respHeaders != null){
                    Header[] hds = postMethod.getResponseHeaders();
                    for(Header hd : hds){
                        respHeaders.add(hd);
                    }
                }
                response = postMethod.getResponseBodyAsString();
            }else{
                System.err.println("响应状态码 = " + postMethod.getStatusCode());
            }
        }catch(HttpException e){
            System.err.println("发生致命的异常，可能是协议不对或者返回的内容有问题");
            e.printStackTrace();
        }catch(IOException e){
            System.err.println("发生网络异常");
            e.printStackTrace();
            if(useProxy){

            }
        }finally{
            if(postMethod != null){
                postMethod.releaseConnection();
                postMethod = null;
            }
        }

        return response;
    }

    /**
     * GET方式提交数据
     * @param url  待请求的URL
     * @param params  要提交的数据
     * @param enc  编码
     * @return  响应结果
     */
    public static String get(String url, Map<String, String> params, String enc) throws Exception {
        return CommonHttpUtils.get(url, params,null, enc, NO_OF_RETRY);
    }
    public static String get(String url, Map<String, String> params, Map<String, String> requestHeaders, String enc) throws Exception {
        return CommonHttpUtils.get(url, params, requestHeaders, enc, NO_OF_RETRY);
    }
    public static String get(String url, String enc) throws Exception {
        return CommonHttpUtils.get(url, null, enc);
    }

    public static String get(String url, Map<String, String> reqParams, Map<String, String> requestHeaders, String enc, int tryTimes) throws Exception {
        String exceptionMsg = null;
        while(tryTimes > 0){
            try{
                //System.out.println("tryTimes="+tryTimes);
                return CommonHttpUtils.get0(url, reqParams, requestHeaders, enc);
            }catch(Exception e){
                if(--tryTimes > 0)continue;
                StringWriter aWriter = new StringWriter();
                e.printStackTrace(new PrintWriter(aWriter));
                exceptionMsg = aWriter.getBuffer().toString();
            }
        }
        throw new Exception("http get try n times error."+url+"\n"+exceptionMsg);
    }

    protected final static String ContentType = "Content-Type";
    protected final static String ContentTypeValue = "application/x-www-form-urlencoded;charset=";

    protected final static String UserAgent = "User-Agent";
    protected final static String UserAgentValue = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.122 Safari/537.36";

    protected final static String CacheControl = "Cache-Control";
    protected final static String CacheControlValue = "no-cache";

    protected final static String ProtocolCookiePolicy = "http.protocol.cookie-policy";

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
                br.close();
            }else{
                response = String.valueOf(getMethod.getStatusCode());
            }
            /*for(Header header : getMethod.getResponseHeaders()){
            	System.out.println(header.getName()+"="+header.getValue());
            }*/
            if("400".equals(response) || "404".equals(response)){
                log.info(response+"="+url);
//                Connection conn = null;
//                try {
//                    conn = DBUtil.getConnection();
//                    List<StkErrorLog> errors = ExceptionUtils.queryErrors(conn, ExceptionUtils.ERROR_CODE_999998);
//                    Exception exception = new Exception("url_" + response + ":" + url);
//                    String sException = ExceptionUtils.getExceptionAsString(exception);
//                    boolean hasSimilar = false;
//                    for(StkErrorLog error : errors){
//                        if(StringSimilarUtils.getSimilarRatio(sException, error.getError()) >= 0.95){
//                            hasSimilar = true;
//                            break;
//                        }
//                    }
//                    if(!hasSimilar)
//                        ExceptionUtils.insertLog(conn, ExceptionUtils.ERROR_CODE_999998, exception);
//                }finally {
//                    CloseUtil.close(conn);
//                }
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

    /**
     * 据Map生成URL字符串
     * @param map   Map
     * @param valueEnc  URL编码
     * @return   URL
     */
    protected static String getUrl(Map<String, String> map, String valueEnc) {
        if (null == map || map.keySet().size() == 0) {
            return (EMPTY);
        }
        StringBuffer url = new StringBuffer();
        Set<String> keys = map.keySet();
        for (Iterator<String> it = keys.iterator(); it.hasNext();) {
            String key = it.next();
            if (map.containsKey(key)) {
                String val = map.get(key);
                String str = val != null ? val : EMPTY;
                try {
                    str = URLEncoder.encode(str, valueEnc);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                url.append(key).append(NAME_VALUE_SEPARATOR).append(str).append(URL_PARAM_CONNECT_FLAG);
            }
        }
        String strURL = EMPTY;
        strURL = url.toString();
        if (URL_PARAM_CONNECT_FLAG.equals(EMPTY + strURL.charAt(strURL.length() - 1))) {
            strURL = strURL.substring(0, strURL.length() - 1);
        }
        return (strURL);
    }

    /**
     * @param url
     * @param parameterName 下载文件名是parameter的值
     * @param path 下载文件存放路径
     * @param fileName 指定下载文件名，会忽略parameterName这个参数，不指定则为null
     */
    public static void download(String url, String parameterName, String path, String fileName) throws Exception {
        HttpState httpState = new HttpState();
        client.setState(httpState);
        useProxy(httpState,url);

        GetMethod httpGet = null;
        try {
            httpGet = new GetMethod(url);
            int statusCode = client.executeMethod(httpGet);
            //System.out.println(statusCode);
            InputStream in = httpGet.getResponseBodyAsStream();
            Header header = httpGet.getResponseHeader("Content-Type");
            if(header == null)return;
            String contentType = header.getValue();
            //System.out.println(contentType);
            String fileType = contentType.substring(contentType.lastIndexOf("/") + 1);
            if(fileName == null){
                //System.out.println(httpGet.getResponseCharSet());
                fileName = getFileNameByUrl(url, URL_PARAM_DECODECHARSET_UTF8, fileType, parameterName);
            }
            FileOutputStream out = new FileOutputStream(new File(path + fileName));
            byte[] b = new byte[1024];
            int len = 0;
            while ((len = in.read(b)) != -1) {
                out.write(b, 0, len);
            }
            in.close();
            out.close();
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpGet.releaseConnection();
        }
    }

    public static void download(String url, String path) throws Exception {
        CommonHttpUtils.download(url, null, path, null);
    }

    public static String getFileNameByUrl(String url,String encoding) throws Exception {
        return CommonHttpUtils.getFileNameByUrl(url, encoding, null, null);
    }

    public static String getFileNameByUrl(String url,String encoding, String type, String parameterName) throws Exception {
        if(StringUtils.indexOfIgnoreCase(url,"http") >= 0)
            url = url.substring(7);
        url = url.substring(url.lastIndexOf("/")+1);

        //String type = contentType.substring(contentType.lastIndexOf("/") + 1);
        if(parameterName != null){
            String value = CommonHttpUtils.getParameter(url, encoding, parameterName);
            if(value != null){
                if(StringUtils.indexOfIgnoreCase(value,type) > 0){
                    return value;
                }else{
                    return value + "." + type;
                }
            }
        }
        if(StringUtils.indexOfIgnoreCase(url,"."+type) > 0){
            url = url.substring(0, StringUtils.indexOfIgnoreCase(url,"."+type)+1).replaceAll("[\\?/:*|<>\"]", "_") + type;
            return decode(url, encoding);
        }
        if (StringUtils.indexOfIgnoreCase(type, "html") != -1) {
            url = url.replaceAll("[\\?/:*|<>\"]", "_") + ".html";
            return decode(url, encoding);
        } else {
            if(type != null){
                url = url.replaceAll("[\\?/:*|<>\"]", "_") + "." + type;
                return decode(url, encoding);
            }else{
                url = url.replaceAll("[\\?/:*|<>\"]", "_");
                return decode(url, encoding);
            }
        }
    }

    /*
     * assume each name is unique
     */
    public static String getParameter(final String url, final String encoding,final String name) throws URISyntaxException {
        Map<String, String> mapparams = new HashMap<String, String>();
        List<NameValuePair> params = parse(new URI(url), encoding);
        for (NameValuePair param : params) {
            mapparams.put(param.getName(), param.getValue());
        }
        return mapparams.get(name);
    }

    private static List<NameValuePair> parse(final URI uri, final String encoding) {
        List<NameValuePair> result = Collections.emptyList();
        final String query = uri.getRawQuery();
        if (query != null && query.length() > 0) {
            result = new ArrayList<NameValuePair>();
            parse(result, new Scanner(query), encoding);
        }
        return result;
    }

    private static void parse(final List<NameValuePair> parameters, final Scanner scanner, final String encoding) {
        scanner.useDelimiter(URL_PARAM_CONNECT_FLAG);
        while (scanner.hasNext()) {
            final String[] nameValue = scanner.next().split(
                    NAME_VALUE_SEPARATOR);
            if (nameValue.length == 0 || nameValue.length > 2)
                throw new IllegalArgumentException("bad parameter");

            final String name = decode(nameValue[0], encoding);
            String value = null;
            if (nameValue.length == 2)
                value = decode(nameValue[1], encoding);
            parameters.add(new NameValuePair(name, value));
        }
    }

    private static String decode(final String content, final String encoding) {
        try {
            return URLDecoder.decode(content, encoding != null ? encoding : URL_PARAM_DECODECHARSET_UTF8);
        } catch (UnsupportedEncodingException problem) {
            throw new IllegalArgumentException(problem);
        }
    }

    public static HostnameVerifier hv = new HostnameVerifier() {
        public boolean verify(String urlHostName, SSLSession session) {
            System.out.println("Warning: URL Host: " + urlHostName + " vs. "
                    + session.getPeerHost());
            return true;
        }

        @Override
        public boolean verify(String arg0, String arg1) {
            return true;
        }
    };

    public static void trustAllHttpsCertificates(){
        javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
        javax.net.ssl.TrustManager tm = new miTM();
        trustAllCerts[0] = tm;
        javax.net.ssl.SSLContext sc;
        try {
            sc = javax.net.ssl.SSLContext
                    .getInstance("SSL");
            sc.init(null, trustAllCerts, null);
            javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc
                    .getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class miTM implements javax.net.ssl.TrustManager,
            javax.net.ssl.X509TrustManager {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public boolean isServerTrusted(
                java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(
                java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public void checkServerTrusted(
                java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }

        public void checkClientTrusted(
                java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }
    }


    public static void main(String[] arg) throws Exception {
		/*URL DIR = WebUtils.class.getResource("/");
		System.out.println(DIR.getPath());
		System.out.println(CommonHttpUtils.getFileNameByUrl("http://js.xueqiu.com/images/face/21lol.png","utf-8"));
		CommonHttpUtils.download("http://js.xueqiu.com/images/face/21lol.png", "d:\\share\\download\\");
		File file = new File("d:/share/download/js.xueqiu.com/images/face/21lol.png");
		System.out.println(file.exists());
		FileUtils.copyURLToFile(new URL("http://js.xueqiu.com/images/face/21lol.png"), new File("d:/share/download/js.xueqiu.com/images/face/21lol.png"));*/
        //CommonHttpUtils.useProxy = true;
		/*String page = CommonHttpUtils.get("http://stockapp.finance.qq.com/mstats/menu_childs.php?id=hk_hy", "gb2312");
		Map<String, Map<String, String>> map = JsonUtils.testJson(page);
		for(Map.Entry<String, Map<String, String>> entry : map.entrySet()){
			System.out.println(entry.getValue().get("t"));
			//http://stock.gtimg.cn/data/hk_sector_rank.php?sector=FNS9&metric=price&pageSize=20&reqPage=1&order=0&var_name=list_data
		}
		System.out.println(page);*/

        //copy /b  E:\downloads\mv\file_name\*.ts  E:\downloads\mv\zzz\file_name.ts
        //mediaCoder ts转码mp4

        downloadMV("https://play.520520bo.com/20200821/1Zr6OizJ/700kb/hls/m7zznwv5042001.ts", "极品韩国BJ女主播19禁 性感大奶极品美胸粉红乳头激情直播第一集", 1);
        downloadMV("https://play.168168bo.com/20190124/91Wtyarf/700kb/hls/kWowyfxU6164000.ts", "棒子国主播大秀 你用来自慰的是香肠吗", 0);
        downloadMV("https://play.520520bo.com/20200515/qxtME2Cs/700kb/hls/yrfN4Lv3747000.ts", "韩国高颜值美女 酒店深夜大秀 极品靓乳 非常诱人", 0);








        //CommonHttpUtils.download("http://bp.pep.com.cn/jc/yjcz/czsxjc/202001/P020200210126052860555.pdf", "d:\\share\\义务教育教科书\\");
    }

    public static void downloadMV(String ds, final String toDir, int start) throws Exception{
        System.out.println(toDir);
        //int start = 0;
        String replace = "00"+start+".ts";
        int end = 500;

        final String target = "E:\\downloads\\mv\\"+toDir+"\\";
        File targetDir = new File(target);
        if(!targetDir.exists()){
            targetDir.mkdirs();
        }

        for(int i = start;i <= end;i++){
            String fileName = StringUtils.replace(StringUtils.substringAfterLast(ds, "/"), replace, StringUtils.leftPad(String.valueOf(i), 3, "0")+".ts");
            final String url = StringUtils.substringBeforeLast(ds, "/")+ "/" + fileName;
            //System.out.println(url);

            RetryUtils.retryIfException(3, 30*1000, new Retry(){
                @Override
                public void run() throws Exception {
                    CommonHttpUtils.download2(url, target);
                }
            });

            File file = new File(target + fileName + ".mp2t");
            if(file.exists()){
                file.renameTo(new File(target + fileName));
            }
            file = new File(target + fileName+".html");
            if(file.exists()){
                file.delete();
                break;
            }
        }
        run(toDir);
    }

    public static void run(String fileName){
        try {
            String command = "copy /b E:\\downloads\\mv\\\""+fileName+"\\\"*.ts  E:\\downloads\\mv\\zzz\\\""+fileName+"\".ts \n";
            System.out.println(command);
            //Process process = Runtime.getRuntime().exec("cmd.exe /c "+command);
            /*BufferedInputStream bis = new BufferedInputStream(process.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(bis));
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }*/

            /*process.waitFor();
            if (process.exitValue() != 0) {
                System.out.println("error!");
            }*/

            //bis.close();
            //br.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void download2(String url, String path) throws Exception {
        CommonHttpUtils.download2(url, null, path, null);
    }

    public static void download2(String url, String parameterName, String path, String fileName) throws Exception {
        HttpState httpState = new HttpState();
        client.setState(httpState);
        useProxy(httpState,url);

        GetMethod httpGet = null;
        try {
            httpGet = new GetMethod(url);
            int statusCode = client.executeMethod(httpGet);
            //System.out.println(statusCode);
            InputStream in = httpGet.getResponseBodyAsStream();
            Header header = httpGet.getResponseHeader("Content-Type");
            if(header == null)return;
            String contentType = header.getValue();
            //System.out.println(contentType);
            String fileType = contentType.substring(contentType.lastIndexOf("/") + 1);
            if(fileName == null){
                //System.out.println(httpGet.getResponseCharSet());
                fileName = getFileNameByUrl(url, URL_PARAM_DECODECHARSET_UTF8, fileType, parameterName);
            }
            FileOutputStream out = new FileOutputStream(new File(path + fileName));
            byte[] b = new byte[1024];
            int len = 0;
            while ((len = in.read(b)) != -1) {
                out.write(b, 0, len);
            }
            in.close();
            out.close();
        } finally {
            httpGet.releaseConnection();
        }
    }
}
