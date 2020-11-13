package com.stk123.web.ik;

import java.io.StringReader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


import com.stk123.common.ik.DocumentField;
import com.stk123.common.ik.DocumentType;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.stk123.model.bo.Stk;
import com.stk123.model.bo.StkIndustryType;
import com.stk123.model.bo.StkText;
import com.stk123.model.Index;
import com.stk123.common.db.connection.ConnectionPool;
import com.stk123.common.util.ChineseUtils;
import com.stk123.common.util.JdbcUtils;
import com.stk123.common.util.collection.Name2Value;


public class SearchEngine {
	
	private static SearchEngine search = null;
	private Directory directory = null;

	private SearchEngine(Directory directory){
		this.directory = directory;
	}
	
	public Directory getDirectory(){
		return this.directory;
	}
	
	public static synchronized SearchEngine getInstance() throws Exception{
		if(search != null)return search;
		Connection conn = ConnectionPool.getInstance().getConnection();
		// 建立内存索引对象
		Directory directory = new RAMDirectory();
		
		// 配置IndexWriterConfig
		IndexWriterConfig iwConfig = StkIKUtils.getConfig();
		iwConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
		IndexWriter iwriter = new IndexWriter(directory, iwConfig);
		// 写入索引
		List<Stk> stks = JdbcUtils.list(conn, "select code,name from stk_cn order by code", Stk.class);
		for(Stk stk : stks){
			Index index =  new Index(conn,stk.getCode(),stk.getName());
			Document doc = new Document();
			doc.add(new Field(DocumentField.ID.value(), index.getCode(), Field.Store.YES, Field.Index.NOT_ANALYZED));
			doc.add(new Field(DocumentField.TYPE.value(), DocumentType.STK.value(), Field.Store.YES, Field.Index.NOT_ANALYZED));
			//content included: main business, keyword, company profile
			String f9 = index.getStock().getF9();
			doc.add(new TextField(DocumentField.CONTENT.value(), StringUtils.join(index.getKeywordAll(),",")+"<br/>"+f9+
					"<br/><br/>"+index.getStock().getCompanyProfile(), Field.Store.YES));
			iwriter.addDocument(doc);
		}
		
		List<StkText> texts = JdbcUtils.list(conn, "select * from stk_text", StkText.class);
		for(StkText text : texts){
			Document doc = StkIKUtils.getDocument(text);
			iwriter.addDocument(doc);
		}
		
		List<StkIndustryType> inds = JdbcUtils.list(conn, "select * from stk_industry_type", StkIndustryType.class);
		for(StkIndustryType ind : inds){
			Document doc = new Document();
			doc.add(new Field(DocumentField.ID.value(), ind.getId().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED));
			doc.add(new Field(DocumentField.TYPE.value(), DocumentType.INDUSTRY.value(), Field.Store.YES, Field.Index.NOT_ANALYZED));
			doc.add(new TextField(DocumentField.CONTENT.value(), ind.getName()+"["+ind.getSource()+"]", Field.Store.YES));
			iwriter.addDocument(doc);
		}
		
		iwriter.commit();
		iwriter.close();
		
		ConnectionPool.getInstance().release(conn);
		
		// 实例化搜索器
		search = new SearchEngine(directory);
		return search;
	}
	
	private static DocumentField[] DefaultFieldsHighLight = new DocumentField[]{DocumentField.TITLE,DocumentField.CONTENT};
	
	/**
	 * 
	 * @param keyword 搜索的关键字
	 * @param type  搜索类型范围,比如stk,text,industry
	 * @param sortByTime 如果type=text，则返回结果可以按时间排序
	 * @param searchFields 在哪些field范围里搜索
	 * @param cnt
	 * @param searchWordsWeight 搜索字的权重
	 * @param defaultExcludes 排除的搜索字
	 * @return
	 * @throws Exception
	 */
	public List<Document> search(String keyword, DocumentType type, boolean sortByTime, DocumentField[] searchFields, int cnt, List<Name2Value> searchWordsWeight,Set<String> defaultExcludes) throws Exception {
		System.out.println("search=="+keyword);
		BooleanQuery query = new BooleanQuery();
		query.setMinimumNumberShouldMatch(1);//下面should必须有至少一个word匹配
		if(type != null){
			TermQuery tq = new TermQuery(new Term(DocumentField.TYPE.value(), type.value()));
			query.add(tq, BooleanClause.Occur.MUST);
		}
		System.out.println();
		if(searchFields == null){
			searchFields = DefaultFieldsHighLight;
		}
		for(int i=0;i<searchFields.length;i++){
			IKSegmenter se = new IKSegmenter(new StringReader(keyword),true);
			Lexeme le = null;
			while((le = se.next()) != null){
				String tKeyWord = le.getLexemeText();
				//设置查询排除词汇
				if(defaultExcludes != null && defaultExcludes.contains(tKeyWord)){
					continue;
				}
				System.out.print(tKeyWord+",");
				TermQuery tq = new TermQuery(new Term(searchFields[i].value(),tKeyWord));
				//设置权重
				if(searchWordsWeight != null){
					List<Name2Value> weights = Name2Value.containName(searchWordsWeight, tKeyWord);
					if(searchWordsWeight != null && weights.size() > 0){
						for(Name2Value weight : weights){
							tq.setBoost(Float.parseFloat(String.valueOf(weight.getValue())));
						}
					}
				}
				query.add(tq, BooleanClause.Occur.SHOULD);
			}
		}
		System.out.println("------------");
		IndexReader ireader = DirectoryReader.open(directory);
		IndexSearcher isearcher = new IndexSearcher(ireader);
		TopDocs topDocs = null;
		if(type == DocumentType.TEXT && sortByTime){
			Sort sort = new Sort(new SortField[]{new SortField(DocumentField.TIME.value(), SortField.Type.INT, true)});
			topDocs = isearcher.search(query, cnt, sort);
		}else{
			topDocs = isearcher.search(query, cnt);
		}
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		List<Document> results = new ArrayList<Document>();
		Analyzer analyzer = new IKAnalyzer(true);
		for (int i = 0; i < topDocs.totalHits; i++) {
			if(i >= cnt)break;
			ScoreDoc score = scoreDocs[i];
			Document targetDoc = isearcher.doc(scoreDocs[i].doc);
			//System.out.println("score="+score.score);
			//-----------------------keyword高亮显示-------------------//
			for(DocumentField field : searchFields){
				String text = targetDoc.get(field.value());
				if (text != null) {
					SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<font color='red'>", "</font>");
					Highlighter highlighter = new Highlighter(simpleHTMLFormatter,new QueryScorer(query));
					highlighter.setTextFragmenter(new SimpleFragmenter(ChineseUtils.length(text)));
				
					TokenStream tokenStream = analyzer.tokenStream(field.value(), new StringReader(text));
					String highLightText = highlighter.getBestFragment(tokenStream,text);
					//System.out.println("★高亮显示第 " + (i + 1) + " 条检索结果如下所示："+field.value()+"="+targetDoc.get(DocumentField.TITLE.value())+","+targetDoc.get(DocumentField.ID.value()));
					//System.out.println(highLightText);
					if(highLightText != null){
						targetDoc.removeFields(field.value());
						targetDoc.add(new TextField(field.value(), highLightText, Field.Store.YES));
					}
				}
			}
			results.add(targetDoc);
		}
		return results;
	}
	
	public List<Document> search(String keyword, int cnt) throws Exception {
		return this.search(keyword, null, false, null, cnt, null, null);
	}
	
	public List<Document> search(String keyword) throws Exception {
		return this.search(keyword, null, false, null, Integer.MAX_VALUE, null, null);
	}
	
	public List<Document> search(String keyword, DocumentType type, boolean sortByTime, int cnt) throws Exception {
		return this.search(keyword, type, sortByTime, null, cnt, null, null);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
