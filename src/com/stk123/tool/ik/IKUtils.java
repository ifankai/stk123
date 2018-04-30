package com.stk123.tool.ik;

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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.DateTools;
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
import org.wltea.analyzer.cfg.Configuration;
import org.wltea.analyzer.cfg.DefaultConfig;
import org.wltea.analyzer.dic.Dictionary;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.stk123.bo.Stk;
import com.stk123.bo.StkText;
import com.stk123.task.StkUtils;
import com.stk123.tool.db.TableTools;
import com.stk123.tool.db.connection.Pool;
import com.stk123.tool.db.util.DBUtil;
import com.stk123.tool.search.Search;
import com.stk123.tool.util.ConfigUtils;
import com.stk123.tool.util.JdbcUtils;


public class IKUtils {
	
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
		doc.add(new Field(DocumentField.TIME.value(),StkUtils.formatDate(time,StkUtils.sf_ymd12),Field.Store.YES,Field.Index.ANALYZED));
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
			//search("��е ��Ĥ�� Ĥ��Ƭ");
			//System.out.println(intersection("�ݸ��","��˾���ҹ����ĳ��ݼ��ݸ��������ҵ��ͬʱ��˾���������л��赥��Ϊ�����л���ϵ�в�Ʒ���ǹ����л����Ʒ��������ͷ��ҵ֮һ����˾��Ӫũҩ���л�����ϼ���ϸ��������Ҫ��Ʒ���Գ��ݼ��ݸ��Ϊ����ũҩԭҩ���Ƽ����Ը�Ʒ������Ϊ�����׻���ϵ�в�Ʒ���Լ����л��赥��Ϊ�����л���ϵ�в�Ʒ����˾ӵ��30�����������֪ʶ��Ȩ��ר������������20����Ƽ��ɹ���ù��Һ�ʡ����������˾ӵ����Ӫ������Ȩ����Ʒ����ŷ�������ޡ������ǵȺ��⼸ʮ�����Һ͵�����"));
			//System.out.println(split("��ҵ�û�2010��10��11�ſ�ʼIT����QQ���4M��10M"));
			//System.out.println(split("#��ҵ#"));
			System.out.println(split("����֤ȯ-������װ-300228-���ٱ��棺LNG�ؿ�������͹�ԣ���ͷ��˾������ů-170310"));
		} finally {
			if (conn != null) conn.close();
		}
	}
	
	public static void init() throws Exception {
		Configuration cfg = DefaultConfig.getInstance();  //���شʿ�
        cfg.setUseSmart(true); //�������ִܷ�
        Dictionary.initial(cfg);
        Dictionary dict = Dictionary.getSingleton();
        Connection conn = null;
		try{
			conn = Pool.getPool().getConnection();
			
			URL url = IKUtils.class.getResource("keyword_stop.txt");
			List<String> stopWords = new ArrayList<String>();
			stopWords.addAll(IOUtils.readLines(url.openStream()));
			dict.disableWords(stopWords);
			//System.out.println("stop words:"+stopWords);
			
			//�ӹؼ���
			//ɾ���Ĺؼ���
			List<String> keyword = JdbcUtils.list(conn, "select name from stk_keyword where status=-1", String.class);
			stopWords.addAll(keyword);
			default_excludes.addAll(keyword);
			//System.out.println("add stop query words:"+keywordDeleted);
			
			//��Ӫҵ��ؼ���
			List<String> mainBusinessWords = JdbcUtils.list(conn, "select distinct b.name name from stk_keyword_link a, stk_keyword b where b.status=0 and a.link_type=1 and a.keyword_id=b.id and a.code_type=1", String.class);
			keyword.addAll(mainBusinessWords);
			//�ֶ��ӵĹؼ���
			List<String> words = JdbcUtils.list(conn, "select distinct sk.name from stk_keyword sk,stk_keyword_link skl where sk.status=0 and sk.id=skl.keyword_id and skl.link_type=0", String.class);
			//System.out.println("add words:"+words);
			keyword.addAll(words);
			//�ӹ�Ʊ����
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
			IndexWriterConfig iwConfig = IKUtils.getConfig();
			IndexWriter iwriter = new IndexWriter(directory, iwConfig);
			iwriter.addDocument(IKUtils.getDocument(text));
			iwriter.close();
		}
	}
	
	public static void updateDocument(StkText text) throws Exception{
		Document doc = IKUtils.getDocument(text);
		for(Directory directory : getDirectorys()){
			IndexWriterConfig iwConfig = IKUtils.getConfig();
			IndexWriter iwriter = new IndexWriter(directory, iwConfig);
			iwriter.updateDocument(new Term(DocumentField.ID.value(), text.getId().toString()), doc);
			iwriter.close();
		}
	}
	
	public static void deleteDocument(String id) throws Exception{
		for(Directory directory : getDirectorys()){
			IndexWriterConfig iwConfig = IKUtils.getConfig();
			IndexWriter iwriter = new IndexWriter(directory, iwConfig);
			iwriter.deleteDocuments(new Term(DocumentField.ID.value(), id));
			iwriter.close();
		}
	}
	
	/**
	 * ����
	 */
	public static Set<String> intersection(String src, String des) throws Exception {
		return IKUtils.intersection(src, des, null);
	}
	
	public static Set<String> intersection(String src, String des, List<String> excludes) throws Exception {
		List<String> s1 = IKUtils.split(src);
		//System.out.println("�ؼ��֣�"+s1);
		List<String> s2 = IKUtils.split(des);
		//System.out.println("�ؼ��֣�"+s2);
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
        //�ִ�  
        TokenStream ts = anal.tokenStream(null, reader);
        CharTermAttribute term = ts.getAttribute(CharTermAttribute.class);  
        //�����ִ�����  
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
		// д������
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
		// ʵ����IKAnalyzer�ִ���
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
			
			// �������ƶ���ߵ�5����¼
			TopDocs topDocs = isearcher.search(query, 3);
			System.out.println("���У�" + topDocs.totalHits+", ��������:"+topDocs.getMaxScore());
			// ������
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
	         
		    for(ScoreDoc score:scoreDocs){
		    	//Document d = searcher.doc(sd.doc);
				//ScoreDoc score = scoreDocs[i];
				System.out.println("kkkkk===="+score.doc+",,"+score.doc);
				Document targetDoc = isearcher.doc(score.doc);
				System.out.println("score="+score.score+",id��" + targetDoc.get("id"));
				//System.out.println(isearcher.explain(query, scoreDocs[i].doc).getValue());
				
				//-----------------------keyword������ʾ-------------------//
				String text = targetDoc.get("content");
				if (text != null) {
					SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<font color='red'>", "</font>");
					Highlighter highlighter = new Highlighter(simpleHTMLFormatter,new QueryScorer(query));
					highlighter.setTextFragmenter(new SimpleFragmenter(text.length()));
				
					TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(text));
					String highLightText = highlighter.getBestFragment(tokenStream,text);
					System.out.println("�������ʾ�� " + (score.doc) + " ���������������ʾ��");
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
