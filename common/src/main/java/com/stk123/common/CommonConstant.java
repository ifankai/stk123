package com.stk123.common;

import java.util.Arrays;
import java.util.List;

import com.stk123.common.util.ConfigUtils;

public interface CommonConstant {
	
	//--- sync ----//
	public final static String SYNC_APIKEY = "apikey";

	//----other -----//
	public final static String NULL = "null";
	public final static String NA = "N/A";
	public final static String YES_Y = "Y";
	public final static String YES_N = "N";

	
	//----encoding---//
	public final static String ENCODING_GBK = "GBK";
	public final static String ENCODING_UTF8 = "UTF8";
	public final static String ENCODING_UTF_8 = "UTF-8";
	public final static String ENCODING_ISO_8859_1 = "ISO-8859-1";
	
	
	//----mark ----//
	public final static String MARK_UNDERLINE = "_";
	public final static String MARK_SLASH = "/";
	public final static String MARK_PERCENTAGE = "%";
	public final static String MARK_DOT = ".";
	public final static String MARK_THREE_DOT = "...";
	public final static String MARK_COMMA = ",";
	public final static String MARK_PARENTHESIS_LEFT = "(";
	public final static String MARK_PARENTHESIS_RIGHT = ")";
	public final static String MARK_BRACKET_LEFT = "[";
	public final static String MARK_BRACKET_RIGHT = "]";
	public final static String MARK_BRACE_LEFT = "{";
	public final static String MARK_BRACE_RIGHT = "}";
	public final static String MARK_EMPTY = "";
	public final static String MARK_BLANK_SPACE = " ";
	public final static String MARK_STAR = "*";
	public final static String MARK_VERTICAL = "|";
	public final static String MARK_DOUBLE_QUOTATION = "\"";
	public final static String MARK_QUESTION = "?";
	public final static String MARK_DOUBLE_HYPHEN = "--";
	public final static String MARK_HYPHEN = "-";
	public final static String MARK_NEW_LINE = "\n";
	public final static String MARK_EQUALS = "=";
	public final static String MARK_SEMICOLON = ";";
	public final static String MARK_TILDE = "~";
	
	
	//----stk.properties-----//
	public final static String HOST = ConfigUtils.getProp("host");
	public final static String PORT = ConfigUtils.getProp("port");
	public final static String HOST_PORT = HOST + ((PORT==null||PORT.length()==0)?"":(":"+PORT));
	public final static boolean IS_DEV = YES_Y.equals(ConfigUtils.getProp("is_dev"))?true:false; 
	public final static String VERSION = MARK_QUESTION + ConfigUtils.getProp("version");
	public final static boolean SQL_SELECT_SHOW = "Y".equals(ConfigUtils.getProp("sql_select_show"));

	public final static String PATH_DAILYREPORT_EMAIL_BACKUP = ConfigUtils.getProp("path_dailyreport_email_backup");
	public final static String PATH_TEXT_IMG_DOWNLOAD = ConfigUtils.getProp("path_text_img_download");
	
	public final static int SYS_ARTICLE_LIST_PER_PAGE = Integer.parseInt(ConfigUtils.getProp("article_list_per_page"));
	public final static int SYS_TEST_LIST_PER_PAGE = Integer.parseInt(ConfigUtils.getProp("text_list_per_page"));
	
	public final static List<String> UPLOAD_IMAGE_TYPES = Arrays.asList(ConfigUtils.getProp("upload_image_type").split(","));
	
	//----action mvc ----//
	public final static String ACTION_SUCC = "succ";
	public final static String ACTION_FAIL = "fail";
	public final static String ACTION_404 = "404";
	
	//----request parameter----//
	public final static String PARAMETER_AUTOLOGIN = "autologin";
	public final static String PARAMETER_USERNAME = "username";
	public final static String PARAMETER_PASSWORD = "password";
	public final static String PARAMETER_ID = "id";
	public final static String PARAMETER_LABEL_ID = "labelId";
	public final static String PARAMETER_LABEL = "label";
	public final static String PARAMETER_DATE = "date";
	public final static String PARAMETER_TYPE = "type";
	public final static String PARAMETER_ORDER = "order";
	public final static String PARAMETER_KWCODE = "kwcode";
	public final static String PARAMETER_KWTYPE = "kwtype";
	public final static String PARAMETER_CODE = "code";
	public final static String PARAMETER_CTYPE = "ctype";
	public final static String PARAMETER_TITLE = "title";
	public final static String PARAMETER_TEXT = "text";
	public final static String PARAMETER_COMPANY = "company";
	public final static String PARAMETER_K = "k";
	public final static String PARAMETER_S = "s";
	public final static String PARAMETER_Q = "q";
	public final static String PARAMETER_PAGE = "page";
	public final static String PARAMETER_DATA = "data";
	public final static String PARAMETER_NICKNAME = "nickname";
	public final static String PARAMETER_EMAIL = "email";
	public final static String PARAMETER_PW1 = "pw1";
	public final static String PARAMETER_PW2 = "pw2";
	
	public final static String ATTRIBUTE_ARTICLES = "articles";
	public final static String ATTRIBUTE_LABELS = "labels";
	public final static String ATTRIBUTE_INDUSTRY_SELECT = "industry_select";
	public final static String ATTRIBUTE_DAILY_STK_UP_DOWN = "daily_up_down";//每日涨跌幅
	public final static String ATTRIBUTE_LOGIN_ERROR = "login_error";
	
	//----sql 部分常用sql-----//
	public final static String SQL_SELECT_TEXT_BY_ID = "select * from stk_text where id=? and user_id=?";
	public final static String SQL_SELECT_TITLE_TEXT_BY_TYPE_ORDERBY_ORDER = "select id,title,disp_order from stk_text where type=? and user_id=? order by disp_order desc,insert_time desc";
	public final static String SQL_COUNT_TITLE_TEXT_BY_TYPE_ORDERBY_ORDER = "select count(1) from stk_text where type=? and user_id=?";
	public final static String SQL_SELECT_TITLE_TEXT_BY_LABEL_ID_ORDERBY_ORDER = "select b.id,b.title,b.disp_order from stk_label_text a,stk_text b where a.text_id=b.id and a.label_id=? and b.user_id=? order by b.disp_order desc,b.insert_time desc";
	public final static String SQL_COUNT_TITLE_TEXT_BY_LABEL_ID_ORDERBY_ORDER = "select count(1) from stk_label_text a,stk_text b where a.text_id=b.id and a.label_id=? and b.user_id=?";
	public final static String SQL_INSERT_TEXT = "insert into stk_text(id,type,code,code_type,title,text,insert_time,update_time,disp_order,user_id) values (?,?,?,?,?,?,sysdate,null,?,?)";
	
	
	public final static int KEYWORD_TYPE_STK = 1;
	
	//----session attribute-----//
	public final static String SESSION_CURRENT_USER = "session_current_user";
	
	//----number----//
	public final static String NUMBER_ZERO = "0";
	public final static String NUMBER_ONE = "1";
	public final static String NUMBER_TWO = "2";
	public final static String NUMBER_SIX = "6";
	public final static String NUMBER_NINE = "9";
	public final static String NUMBER_TWELVE = "12";

	//----date format------//
	public final static String DATE_FORMAT_YYYY_MM_DD = "yyyy-MM-dd";
	public final static String DATE_FORMAT_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
	
	//----html tag--------//
	public final static String HTML_TAG_B = "<b>";
	public final static String HTML_TAG_B_END = "</b>";
	public final static String HTML_TAG_BR = "<br/>";
	
	//----json key name---- 此处修改要和stk_dictionary表里type=1000同步//
	public final static String JSON_NAME = "name";
	public final static String JSON_CODE = "code";
	public final static String JSON_PERCENTAGE_UPDOWN = "up";
	public final static String JSON_MARKET_VALUE = "mv";
	public final static String JSON_INDUSTRY = "ind";
	public final static String JSON_PE_TTM = "pettm";
	public final static String JSON_PB = "pb";
	public final static String JSON_PS = "ps";
	public final static String JSON_ROE = "roe";
	public final static String JSON_LAST_QUARTER_NET_PROFIT_MARGIN = "qnpm";
	public final static String JSON_GROSS_MARGIN = "gm";
	public final static String JSON_ID = "id";
	public final static String JSON_VALUE = "value";
	
	
	//----fn type----//
	public final static String FN_TYPE_CN_JLR = "303";
	public final static String FN_TYPE_CN_JLRZZL = "111";
	public final static String FN_TYPE_CN_MGJZC = "102";
	public final static String FN_TYPE_CN_ZYSR = "300";
	public final static String FN_TYPE_CN_YFFY = "206";
	public final static String FN_TYPE_CN_GDQY = "210";
	public final static String FN_TYPE_CN_ZYSRZZL = "110";
	public final static String FN_TYPE_CN_ROE = "109";
	public final static String FN_TYPE_CN_GROSS_MARGIN = "106";
	public final static String FN_TYPE_CN_JYHDXJLLJE = "400";
	public final static String FN_TYPE_CN_JYHDXJL = "123";
	
	//----pageContext----//
	public final static String PAGE_TITLE = "PAGE_TITLE";
	
	//----default user id---//
	public final static int DEFAULT_USER_ID = 1;



	//-----websocket-------//
	public static final String WS_TOPIC = "/topic/greetings";
	public static final String WS_ENDPOINT = "/websocket-endpoint";
	public static final String WS_PREFIX = "/ws";
	public static final String WS_MAPPING = "/query";
}

