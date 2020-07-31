package com.stk123.tool.search;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
import org.apache.lucene.index.MultiReader;
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
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.stk123.bo.Stk;
import com.stk123.bo.StkIndustryType;
import com.stk123.bo.StkText;
import com.stk123.model.Index;
import com.stk123.tool.db.connection.Pool;
import com.stk123.tool.ik.DocumentField;
import com.stk123.tool.ik.DocumentType;
import com.stk123.tool.ik.IKUtils;
import com.stk123.tool.ik.StkSearch;
import com.stk123.tool.tree.Tree;
import com.stk123.tool.tree.TreeNode;
import com.stk123.tool.util.ChineseUtils;
import com.stk123.tool.util.HtmlUtils;
import com.stk123.tool.util.JdbcUtils;
import com.stk123.tool.util.collection.Name2Value;
import com.stk123.web.StkDict;
import com.stk123.web.WebUtils;
import com.stk123.web.bs.IndexService;

public class Search {

	public static DocumentField[] SEARCHFIELDS_DEFAULT = new DocumentField[]{DocumentField.TITLE,DocumentField.CONTENT};
	public static DocumentField[] SEARCHFIELDS_STK_RELATED = new DocumentField[]{DocumentField.F9ZHUYIN,DocumentField.KEYWORD,DocumentField.COMPANYPROFILE};
	public static DocumentField[] SEARCHFIELDS_ALL = new DocumentField[]{DocumentField.TITLE,DocumentField.F9,DocumentField.KEYWORD,DocumentField.COMPANYPROFILE,DocumentField.CONTENT};

	private final static String TOMCAT_TEMP = System.getProperty("java.io.tmpdir");
	private final static String INDEX_PATH = TOMCAT_TEMP + File.separator + "index";

	private int userId;
	private Directory directoryUser;
	private static Directory directoryStkAndIndustry;

	public Search(int userId) throws IOException{
		this.userId = userId;
		this.directoryUser = FSDirectory.open(new File(INDEX_PATH + File.separator + userId));
	}

	public static void initStkAndIndustryAndIndex() throws Exception{
		System.out.println("Search.initStkAndIndustryAndIndex");
		Connection conn = null;
		try{
			conn = Pool.getPool().getConnection();
			directoryStkAndIndustry = new RAMDirectory();
			IndexWriterConfig iwConfig = IKUtils.getConfig();
			iwConfig.setOpenMode(OpenMode.CREATE);
			IndexWriter iwriter = new IndexWriter(directoryStkAndIndustry, iwConfig);

			//stk
			List<Stk> stks = JdbcUtils.list(conn, "select code,name from stk_cn order by code", Stk.class);
			for(Stk stk : stks){
				Index index =  new Index(conn,stk.getCode(),stk.getName());
				Document doc = new Document();
				doc.add(new Field(DocumentField.ID.value(), index.getCode(), Field.Store.YES, Field.Index.NOT_ANALYZED));
				doc.add(new Field(DocumentField.TYPE.value(), DocumentType.STK.value(), Field.Store.YES, Field.Index.NOT_ANALYZED));
				doc.add(new TextField(DocumentField.TITLE.value(), index.getName()+" ["+index.getCode()+"]", Field.Store.YES));

				List<Name2Value> f9 = index.getF9();
				List<Name2Value> zhuyin = Name2Value.containName(f9, "Ӫ");
				String sZhuyin = null;
				for(Name2Value pair : zhuyin){
					sZhuyin += pair.getValue();
				}
				if(sZhuyin != null){
					doc.add(new TextField(DocumentField.F9ZHUYIN.value(), sZhuyin, Field.Store.YES));
				}
				doc.add(new TextField(DocumentField.KEYWORD.value(), StringUtils.join(index.getKeywordAll(),","), Field.Store.YES));
				String profile = index.getStock().getCompanyProfile();
				if(profile != null){
					doc.add(new TextField(DocumentField.COMPANYPROFILE.value(), profile, Field.Store.YES));
				}
				String sf9 = index.getStock().getF9();
				/*if(sf9 != null){
					doc.add(new TextField(DocumentField.F9.value(), sf9, Field.Store.YES));
				}*/
				doc.add(new TextField(DocumentField.CONTENT.value(), StringUtils.join(index.getKeywordAll(),",")+"<br/><br/>"+sf9+"<br/><br/>"+profile, Field.Store.YES));
				iwriter.addDocument(doc);
			}

			//industry
			List<StkIndustryType> inds = JdbcUtils.list(conn, "select * from stk_industry_type a,stk_dictionary b where a.source=b.key and b.type="+StkDict.INDUSTRY_SOURCE, StkIndustryType.class);
			for(StkIndustryType ind : inds){
				Document doc = new Document();
				doc.add(new Field(DocumentField.ID.value(), ind.getId().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED));
				doc.add(new Field(DocumentField.TYPE.value(), DocumentType.INDUSTRY.value(), Field.Store.YES, Field.Index.NOT_ANALYZED));
				doc.add(new TextField(DocumentField.TITLE.value(), ind.getName()+" ["+StkDict.getDict(StkDict.INDUSTRY_SOURCE, ind.getSource())+"]", Field.Store.YES));
				iwriter.addDocument(doc);
			}

			//index data
			IndexService is = new IndexService();
			Tree tree = is.getTree();
			List<TreeNode> nodes = new ArrayList<TreeNode>();
			Tree.getNodeList(tree, nodes);
			for(TreeNode node : nodes){
				if(node.getChildren() == null || node.getChildren().size() == 0){
					Document doc = new Document();
					doc.add(new Field(DocumentField.ID.value(), String.valueOf(node.getNodeId()), Field.Store.YES, Field.Index.NOT_ANALYZED));
					doc.add(new Field(DocumentField.TYPE.value(), DocumentType.INDEX.value(), Field.Store.YES, Field.Index.NOT_ANALYZED));
					if(node.getParent() != null){
						doc.add(new TextField(DocumentField.TITLE.value(), node.getName()+" ["+node.getParent().getName()+"]", Field.Store.YES));
					}else{
						doc.add(new TextField(DocumentField.TITLE.value(), node.getName(), Field.Store.YES));
					}
					iwriter.addDocument(doc);
				}
			}

			iwriter.commit();
			iwriter.close();
		}finally{
			Pool.getPool().free(conn);
		}
	}

	public void initUserText() throws Exception{
		System.out.println("Search.initUserText,userId="+this.userId+",dir="+(INDEX_PATH + File.separator + userId));
		Connection conn = null;
		try{
			IndexWriterConfig iwConfig = IKUtils.getConfig();
			iwConfig.setOpenMode(OpenMode.CREATE);
			IndexWriter iwriter = new IndexWriter(this.directoryUser, iwConfig);

			conn = Pool.getPool().getConnection();
			List params = new ArrayList();
			params.add(this.userId);
			List<StkText> texts = JdbcUtils.list(conn, "select * from stk_text where insert_time>=sysdate-700 and user_id=?", params, StkText.class);
			for(StkText text : texts){
				Document doc = IKUtils.getDocument(text);
				iwriter.addDocument(doc);
			}
			iwriter.commit();
			iwriter.close();
		}finally{
			Pool.getPool().free(conn);
		}
	}

	public List<Document> searchRelatedText(String keyword, String code,boolean sortByTime, int start, int end) throws Exception {
		return Search.search(keyword, DocumentType.TEXT, sortByTime, SEARCHFIELDS_DEFAULT, start,end, null, null, false, code,null, directoryUser);
	}

	public List<Document> searchAll(String keyword,boolean sortByTime,TotalCount totalCount, int start, int end) throws Exception {
		return Search.search(keyword, null, sortByTime, SEARCHFIELDS_ALL, start, end, null, null, true, null,totalCount, directoryUser, directoryStkAndIndustry);
	}

	public static List<Document> searchRelatedStk(String keyword, List<Name2Value> searchWordsWeight, Set<String> defaultExcludes, int start, int end) throws Exception {
		return Search.search(keyword, DocumentType.STK, false, SEARCHFIELDS_STK_RELATED,start, end, searchWordsWeight, defaultExcludes, false, null,null, directoryStkAndIndustry);
	}

	public static List<Document> search(String keyword, DocumentType type, boolean sortByTime, DocumentField[] searchFields, int start, int end, List<Name2Value> searchWordsWeight,Set<String> defaultExcludes, boolean highLight, String code,TotalCount totalCount, Directory... directorys) throws Exception {
		//System.out.println("search=="+keyword);
		BooleanQuery query = new BooleanQuery();
		BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);
		//query.setMinimumNumberShouldMatch(1);
		if(type != null){
			TermQuery tq = new TermQuery(new Term(DocumentField.TYPE.value(), type.value()));
			query.add(tq, BooleanClause.Occur.MUST);
		}
		if(code != null){
			TermQuery tq = new TermQuery(new Term(DocumentField.CODE.value(), code));
			query.add(tq, BooleanClause.Occur.MUST_NOT);
		}
		if(searchFields == null){
			searchFields = SEARCHFIELDS_DEFAULT;
		}
		BooleanQuery query2 = new BooleanQuery();
		query2.setMinimumNumberShouldMatch(1);
		for(int i=0;i<searchFields.length;i++){
			IKSegmenter se = new IKSegmenter(new StringReader(keyword),true);
			Lexeme le = null;
			while((le = se.next()) != null){
				String tKeyWord = le.getLexemeText();
				if(defaultExcludes != null && defaultExcludes.contains(tKeyWord)){
					continue;
				}
				//System.out.print(tKeyWord+",");
				TermQuery tq = new TermQuery(new Term(searchFields[i].value(),tKeyWord));
				if(searchWordsWeight != null){
					List<Name2Value> weights = Name2Value.containName(searchWordsWeight, tKeyWord);
					if(searchWordsWeight != null && weights.size() > 0){
						for(Name2Value<String,Float> weight : weights){
							tq.setBoost(weight.getValue());
						}
					}
				}
				query2.add(tq, BooleanClause.Occur.SHOULD);
			}
		}
		query.add(query2, BooleanClause.Occur.MUST);

		List<IndexReader> readers = new ArrayList<IndexReader>();
		for(Directory directory : directorys){
			readers.add(DirectoryReader.open(directory));
		}
		IndexReader[] ireaders = new IndexReader[]{};
		ireaders = readers.toArray(ireaders);
		MultiReader reader = new MultiReader(ireaders);

		IndexSearcher isearcher = new IndexSearcher(reader);

		TopDocs topDocs = null;
		if(type == DocumentType.TEXT && sortByTime){
			Sort sort = new Sort(new SortField[]{new SortField(DocumentField.TIME.value(), SortField.Type.LONG, true)});
			topDocs = isearcher.search(query, end, sort);
		}else{
			topDocs = isearcher.search(query, end);
		}
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		if(totalCount != null){
			totalCount.totalCount = topDocs.totalHits;
		}
		List<Document> results = new ArrayList<Document>();
		if(highLight){
			Analyzer analyzer = new IKAnalyzer(true);
			for (int i = 0; i < topDocs.totalHits; i++) {
				if(i < start){
					continue;
				}else if(i >= end)break;
				//ScoreDoc score = scoreDocs[i];
				Document targetDoc = isearcher.doc(scoreDocs[i].doc);
				for(DocumentField field : searchFields){
					String text = targetDoc.get(field.value());
					if (text != null && (DocumentField.CONTENT.value().equals(field.value()) || DocumentField.TITLE.value().equals(field.value()))) {
						SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter(HIGH_LIGHT_BEGIN, HIGH_LIGHT_END);
						Highlighter highlighter = new Highlighter(simpleHTMLFormatter,new QueryScorer(query));
						highlighter.setTextFragmenter(new SimpleFragmenter(200));

						String rText = HtmlUtils.removeHTML(text);
						TokenStream tokenStream = analyzer.tokenStream(field.value(), new StringReader(rText));
						String highLightText = highlighter.getBestFragment(tokenStream,rText);
						//System.out.println(highLightText);
						if(highLightText != null){
							targetDoc.removeFields(field.value());
							if(DocumentField.CONTENT.value().equals(field.value())){
								targetDoc.add(new TextField(field.value(), text, Field.Store.YES));
								targetDoc.add(new TextField(DocumentField.SUMMARY.value(), highLightText, Field.Store.YES));
							}else{
								targetDoc.add(new TextField(field.value(), highLightText, Field.Store.YES));
							}
						}
					}
				}
				results.add(targetDoc);
			}
			analyzer.close();
		}else{
			for (int i = 0; i < topDocs.totalHits; i++) {
				if(i < start){
					continue;
				}else if(i >= end)break;
				Document targetDoc = isearcher.doc(scoreDocs[i].doc);
				results.add(targetDoc);
			}
		}
		return results;
	}

	public void addDocument(StkText text) throws Exception{
		IndexWriterConfig iwConfig = IKUtils.getConfig();
		iwConfig.setOpenMode(OpenMode.APPEND);
		IndexWriter iwriter = new IndexWriter(this.directoryUser, iwConfig);
		iwriter.addDocument(IKUtils.getDocument(text));
		iwriter.close();
	}

	public void updateDocument(StkText text) throws Exception{
		Document doc = IKUtils.getDocument(text);
		IndexWriterConfig iwConfig = IKUtils.getConfig();
		iwConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
		IndexWriter iwriter = new IndexWriter(directoryUser, iwConfig);
		iwriter.updateDocument(new Term(DocumentField.ID.value(), text.getId().toString()), doc);
		iwriter.close();
	}

	public void deleteDocument(String id) throws Exception{
		IndexWriterConfig iwConfig = IKUtils.getConfig();
		IndexWriter iwriter = new IndexWriter(directoryUser, iwConfig);
		iwriter.deleteDocuments(new Term(DocumentField.ID.value(), id));
		iwriter.close();
	}

	public void close() throws Exception{
		if(this.directoryUser != null)this.directoryUser.close();
	}

	private final static String HIGH_LIGHT_BEGIN = "<font color='red'>";
	private final static String HIGH_LIGHT_END = "</font>";

	public static class TotalCount{
		public int totalCount;
	}
}
