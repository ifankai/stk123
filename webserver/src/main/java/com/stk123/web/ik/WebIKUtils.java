package com.stk123.web.ik;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.stk123.service.ik.IKUtils;
import com.stk123.common.ik.DocumentField;
import com.stk123.common.ik.DocumentType;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.springframework.util.ResourceUtils;
import org.wltea.analyzer.cfg.Configuration;
import org.wltea.analyzer.cfg.DefaultConfig;
import org.wltea.analyzer.dic.Dictionary;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.stk123.model.bo.Stk;
import com.stk123.model.bo.StkText;
import com.stk123.service.ServiceUtils;
import com.stk123.common.db.TableTools;
import com.stk123.common.db.connection.Pool;
import com.stk123.common.db.util.DBUtil;
import com.stk123.common.util.ConfigUtils;
import com.stk123.common.util.JdbcUtils;


public class WebIKUtils extends IKUtils {
	
	public static Set<String> default_excludes = new HashSet<String>();
	
	public static Document getDocument(StkText text){
		Document doc = new Document();
		doc.add(new Field(DocumentField.ID.value(), text.getId().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED));
		doc.add(new Field(DocumentField.TYPE.value(), DocumentType.TEXT.value(), Field.Store.YES, Field.Index.NOT_ANALYZED));
		if(text.getCode() != null){
			doc.add(new Field(DocumentField.CODE.value(), text.getCode(), Field.Store.YES, Field.Index.NOT_ANALYZED));
		}
		if(text.getTitle() != null){
			doc.add(new TextField(DocumentField.TITLE.value(), text.getTitle(), Field.Store.YES));
		}
		Timestamp time = text.getUpdateTime();
		if(time == null){
			time = text.getInsertTime();
		}
		doc.add(new Field(DocumentField.TIME.value(),ServiceUtils.formatDate(time,ServiceUtils.sf_ymd12),Field.Store.YES,Field.Index.ANALYZED));
		doc.add(new TextField(DocumentField.CONTENT.value(), text.getText(), Field.Store.YES));
		doc.add(new Field(DocumentField.ORDER.value(), text.getDispOrder().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED));
		doc.add(new Field(DocumentField.USERID.value(), text.getUserId().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED));
		return doc;
	}
	
	public static void main(String[] args) throws Exception {
        ConfigUtils.setPropsFromResource(TableTools.class, "db.properties");
		Connection conn = null;
		try {
			init();
			conn = DBUtil.getConnection();
			//createIndex(conn);
			//search("器械 体膜建 膜补片");
			//System.out.println(intersection("草甘膦","公司是我国最大的除草剂草甘膦生产企业，同时公司还生产以有机硅单体为主的有机硅系列产品，是国内有机硅产品的两大龙头企业之一。公司主营农药、有机硅材料及精细化工，主要产品有以除草剂草甘膦为主的农药原药及制剂，以高品质磷酸为主的磷化工系列产品，以及以有机硅单体为主的有机硅系列产品。公司拥有30余项具有自主知识产权的专利技术，并有20余项科技成果获得国家和省级奖励。公司拥有自营进出口权，产品畅销欧美、澳洲、东南亚等海外几十个国家和地区。"));
			//System.out.println(split("行业用户2010年10月11号开始IT申请QQ宽带4M到10M"));
			//System.out.println(split("#行业#"));
			System.out.println(split("中信证券-富瑞特装-300228-跟踪报告：LNG重卡经济性凸显，龙头公司显著回暖-170310"));
		} finally {
			if (conn != null) conn.close();
		}
	}
	
	public static void init() throws Exception {
		Configuration cfg = DefaultConfig.getInstance();  //加载词库
        cfg.setUseSmart(true); //设置智能分词
        Dictionary.initial(cfg);
        Dictionary dict = Dictionary.getSingleton();
        Connection conn = null;
		try{
			conn = Pool.getPool().getConnection();
			
			URL url = ResourceUtils.getURL("classpath:keyword_stop.txt");
			List<String> stopWords = new ArrayList<String>();
			stopWords.addAll(IOUtils.readLines(url.openStream()));
			dict.disableWords(stopWords);
			//System.out.println("stop words:"+stopWords);
			
			//加关键字
			//删除的关键字
			List<String> keyword = JdbcUtils.list(conn, "select name from stk_keyword where status=-1", String.class);
			stopWords.addAll(keyword);
			default_excludes.addAll(keyword);
			//System.out.println("add stop query words:"+keywordDeleted);
			
			//主营业务关键字
			List<String> mainBusinessWords = JdbcUtils.list(conn, "select distinct b.name name from stk_keyword_link a, stk_keyword b where b.status=0 and a.link_type=1 and a.keyword_id=b.id and a.code_type=1", String.class);
			keyword.addAll(mainBusinessWords);
			//手动加的关键字
			List<String> words = JdbcUtils.list(conn, "select distinct sk.name from stk_keyword sk,stk_keyword_link skl where sk.status=0 and sk.id=skl.keyword_id and skl.link_type=0", String.class);
			//System.out.println("add words:"+words);
			keyword.addAll(words);
			//加股票名称
			List<Stk> stks = JdbcUtils.list(conn, "select code,name from stk order by code", Stk.class);
			for(Stk stk : stks){
				keyword.add(stk.getCode());
				String name = stk.getName();
				if(name == null) continue;
				if(name.indexOf(" ") > 0){
					keyword.add(StringUtils.replace(name, " ", ""));
				}
				keyword.add(name);
			}
			dict.addWords(keyword);
		}finally{
			Pool.getPool().free(conn);
		}
		Search.initStkAndIndustryAndIndex();
		Search search = new Search(1);
		search.initUserText();
		search.close();
	}
	
	public static IndexWriterConfig getConfig(){
		return new IndexWriterConfig(Version.LUCENE_45, new IKAnalyzer(true));
	}
	
	private final static List<Directory> Directories = new ArrayList<Directory>();
	
	public static List<Directory> getDirectorys() throws Exception {
		if(Directories.size() == 0){
			Directories.add(StkSearch.getInstance().getDirectory());
			Directories.add(SearchEngine.getInstance().getDirectory());
		}
		return Directories;
	}
	
	public static void addDocument(StkText text) throws Exception{
		for(Directory directory : getDirectorys()){
			IndexWriterConfig iwConfig = WebIKUtils.getConfig();
			IndexWriter iwriter = new IndexWriter(directory, iwConfig);
			iwriter.addDocument(WebIKUtils.getDocument(text));
			iwriter.close();
		}
	}
	
	public static void updateDocument(StkText text) throws Exception{
		Document doc = WebIKUtils.getDocument(text);
		for(Directory directory : getDirectorys()){
			IndexWriterConfig iwConfig = WebIKUtils.getConfig();
			IndexWriter iwriter = new IndexWriter(directory, iwConfig);
			iwriter.updateDocument(new Term(DocumentField.ID.value(), text.getId().toString()), doc);
			iwriter.close();
		}
	}
	
	public static void deleteDocument(String id) throws Exception{
		for(Directory directory : getDirectorys()){
			IndexWriterConfig iwConfig = WebIKUtils.getConfig();
			IndexWriter iwriter = new IndexWriter(directory, iwConfig);
			iwriter.deleteDocuments(new Term(DocumentField.ID.value(), id));
			iwriter.close();
		}
	}
	
	/**
	 * 交集
	 */
	public static Set<String> intersection(String src, String des) throws Exception {
		return WebIKUtils.intersection(src, des, null);
	}
	
	public static Set<String> intersection(String src, String des, List<String> excludes) throws Exception {
		List<String> s1 = WebIKUtils.split(src);
		//System.out.println("关键字："+s1);
		List<String> s2 = WebIKUtils.split(des);
		//System.out.println("关键字："+s2);
		Set<String> set1 = new HashSet<String>();
		set1.addAll(s1);
		Set<String> set2 = new HashSet<String>();
		set2.addAll(s2);
		Set<String> result = new HashSet<String>();
		for(String s : set1){
			if(default_excludes.contains(s))continue;
			if(excludes != null && excludes.contains(s))continue;
			if(set2.contains(s)){
				result.add(s);
			}
		}
		return result;
	}
	
	public static List<String> split(String text) throws Exception{
		List<String> result = new ArrayList<String>();
		Analyzer anal = new IKAnalyzer(true);       
        StringReader reader = new StringReader(text);
        //分词  
        TokenStream ts = anal.tokenStream(null, reader);
        CharTermAttribute term = ts.getAttribute(CharTermAttribute.class);  
        //遍历分词数据  
        while(ts.incrementToken()){
        	//TermQuery tq = new TermQuery(new Term(null, term.toString()));
            result.add(term.toString()); 
        }
        reader.close();
        return result;
	}
	
	public static void createIndex(Connection conn) throws Exception{
		Analyzer analyzer = new IKAnalyzer(true);
		Directory directory = null;
		IndexWriter iwriter = null;
		directory = FSDirectory.open(new File("D:/stock/index"));
		IndexWriterConfig iwConfig = new IndexWriterConfig(Version.LUCENE_45, analyzer);
		iwConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
		iwriter = new IndexWriter(directory, iwConfig);
		// 写入索引
		Document doc = null;
		List<Stk> stks = JdbcUtils.list(conn, "select code,name,company_profile from stk_cn order by code", Stk.class);

		for(Stk stk : stks){
			if(stk.getCompanyProfile() != null){
				doc = new Document();
				doc.add(new Field("id", stk.getCode(), Field.Store.YES, Field.Index.NOT_ANALYZED));
				doc.add(new Field("content", stk.getCompanyProfile(), Field.Store.YES, Field.Index.ANALYZED));
				iwriter.addDocument(doc);
			}
		}
		iwriter.commit();
		iwriter.close();
		directory.close();
	}
	
	public static void search(String keyword){
		// 实例化IKAnalyzer分词器
		Analyzer analyzer = new IKAnalyzer(true);
		Directory directory = null;
		IndexReader ireader = null;
		IndexSearcher isearcher = null;
		try {
			directory = FSDirectory.open(new File("D:/stock/index"));
			
			ireader = DirectoryReader.open(directory);
			isearcher = new IndexSearcher(ireader);
			
			QueryParser qp = new QueryParser(Version.LUCENE_45, "content", analyzer);// new QueryParser(Version.LUCENE_45, fieldName,analyzer);
			qp.setDefaultOperator(QueryParser.OR_OPERATOR);
			Query query = qp.parse(keyword);
			
			// 搜索相似度最高的5条记录
			TopDocs topDocs = isearcher.search(query, 3);
			System.out.println("命中：" + topDocs.totalHits+", 最大的评分:"+topDocs.getMaxScore());
			// 输出结果
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
	         
		    for(ScoreDoc score:scoreDocs){
		    	//Document d = searcher.doc(sd.doc);
				//ScoreDoc score = scoreDocs[i];
				System.out.println("kkkkk===="+score.doc+",,"+score.doc);
				Document targetDoc = isearcher.doc(score.doc);
				System.out.println("score="+score.score+",id：" + targetDoc.get("id"));
				//System.out.println(isearcher.explain(query, scoreDocs[i].doc).getValue());
				
				//-----------------------keyword高亮显示-------------------//
				String text = targetDoc.get("content");
				if (text != null) {
					SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<font color='red'>", "</font>");
					Highlighter highlighter = new Highlighter(simpleHTMLFormatter,new QueryScorer(query));
					highlighter.setTextFragmenter(new SimpleFragmenter(text.length()));
				
					TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(text));
					String highLightText = highlighter.getBestFragment(tokenStream,text);
					System.out.println("★高亮显示第 " + (score.doc) + " 条检索结果如下所示：");
					System.out.println(highLightText);
				}
				System.out.println();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ireader != null) {
				try {
					ireader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (directory != null) {
				try {
					directory.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
