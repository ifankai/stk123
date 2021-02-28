package com.stk123.web.ik;

import java.io.StringReader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.stk123.common.ik.DocumentField;
import com.stk123.common.ik.DocumentType;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import com.stk123.model.bo.Stk;
import com.stk123.model.bo.StkText;
import com.stk123.model.Index;
import com.stk123.common.db.TableTools;
import com.stk123.common.db.connection.Pool;
import com.stk123.common.util.ConfigUtils;
import com.stk123.common.util.JdbcUtils;
import com.stk123.common.util.collection.Name2Value;


public class StkSearch {
	
	private static StkSearch ks = null;
	
	private Directory directory = null;
	
	private StkSearch(Directory directory){
		this.directory = directory;
	}
	
	public static void clear() throws Exception{
		ks = null;
		WebIKUtils.init();
	}
	
	public static synchronized StkSearch getInstance() throws Exception{
		if(ks != null)return ks;
		Connection conn = Pool.getPool().getConnection();
		// 建立内存索引对象
		Directory directory = new RAMDirectory();
		
		// 配置IndexWriterConfig
		IndexWriterConfig iwConfig = WebIKUtils.getConfig();
		iwConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
		IndexWriter iwriter = new IndexWriter(directory, iwConfig);

        FieldType customType1 = new FieldType(TextField.TYPE_STORED);
        FieldType customType2 = new FieldType(TextField.TYPE_STORED);
        customType2.setTokenized(false);

		// 写入索引
		List<Stk> stks = JdbcUtils.list(conn, "select code,name from stk_cn order by code", Stk.class);
		for(Stk stk : stks){
			Index index =  new Index(conn,stk.getCode(),stk.getName());
			Document doc = new Document();
			doc.add(new Field(DocumentField.ID.value(), index.getCode(), customType2));
			doc.add(new Field(DocumentField.TYPE.value(), DocumentType.STK.value(), customType2));
			//content included: main business, keyword, company profile
			List<Name2Value> f9 = index.getF9();
			List<Name2Value> zhuyin = Name2Value.containName(f9, "营");
			String sZhuyin = null;
			for(Name2Value pair : zhuyin){
				sZhuyin += pair.getValue();
			}
			doc.add(new TextField(DocumentField.CONTENT.value(), StringUtils.join(index.getKeywordAll(),",")+","+sZhuyin+","+index.getStock().getCompanyProfile(), Field.Store.YES));
			iwriter.addDocument(doc);
		}
		List<StkText> texts = JdbcUtils.list(conn, "select * from stk_text", StkText.class);
		for(StkText text : texts){
			Document doc = WebIKUtils.getDocument(text);
			iwriter.addDocument(doc);
		}
		
		iwriter.commit();
		iwriter.close();
		
		Pool.getPool().free(conn);
		
		// 搜索过程**********************************
		// 实例化搜索器
		/*ireader = DirectoryReader.open(directory);
		IndexSearcher isearcher = new IndexSearcher(ireader);*/
		ks = new StkSearch(directory);
		return ks;
	}
	
	public Directory getDirectory(){
		return this.directory;
	}
	
	public List<Document> search(String keyword,DocumentType type, int cnt) throws Exception{
		return this.search(keyword,type,new String[]{DocumentField.CONTENT.value()}, cnt, null, null);
	}
	
	public List<Document> search(String keyword,DocumentType type,String[] fields, int cnt) throws Exception{
		return this.search(keyword,type, fields, cnt, null, null);
	}
	
	public List<Document> search(String keyword,DocumentType type, int cnt, List<String> highWeightWords) throws Exception{
		return this.search(keyword,type,new String[]{DocumentField.CONTENT.value()}, cnt, highWeightWords, null);
	}
	
	public List<Document> search(String keyword,DocumentType type, int cnt, List<String> highWeightWords,Set<String> defaultExcludes) throws Exception{
		return this.search(keyword,type,new String[]{DocumentField.CONTENT.value()}, cnt, highWeightWords, defaultExcludes);
	}
	
	public List<Document> search(String keyword, String[] fields, int cnt, List<String> highWeightWords,Set<String> defaultExcludes) throws Exception{
		return this.search(keyword,DocumentType.STK, fields, cnt, highWeightWords, defaultExcludes);
	}
	
	public List<Document> search(String keyword,DocumentType type, String[] fields, int cnt, List<String> highWeightWords,Set<String> defaultExcludes) throws Exception{
		//System.out.println("keyword=="+keyword);
		//System.out.println(IKUtils.split(keyword));
		//System.out.println("high weight words:"+highWeightWords);
		//String[] fields = new String[]{CONTENT};
		//Analyzer analyzer = new IKAnalyzer(true);
		BooleanQuery query = new BooleanQuery();
		/*BooleanClause.Occur[] flags = {
				BooleanClause.Occur.SHOULD,
				BooleanClause.Occur.SHOULD //或
		};*/
		
		/*QueryParser qp = new QueryParser(Version.LUCENE_45,fields, analyzer);
		qp.setDefaultOperator(QueryParser.AND_OPERATOR);
		Query query = qp.parse(keyword);*/
		//Query query = MultiFieldQueryParser.parse(Version.LUCENE_45, keyword, fields, flags, analyzer);

		if(type != null){
			TermQuery tq = new TermQuery(new Term(DocumentField.TYPE.value(), type.value()));
			query.add(tq, BooleanClause.Occur.MUST);
			query.setMinimumNumberShouldMatch(1);//下面should必须有至少一个word匹配
		}
		System.out.println();
		for(int i=0;i<fields.length;i++){
			IKSegmenter se = new IKSegmenter(new StringReader(keyword),true);
			Lexeme le = null;
			while((le = se.next()) != null){
				String tKeyWord = le.getLexemeText();
				//设置查询排除词汇
				if(defaultExcludes != null && defaultExcludes.contains(tKeyWord)){
					continue;
				}
				System.out.print(tKeyWord+",");
				Query tq = new TermQuery(new Term(fields[i],tKeyWord));
				//设置权重
				if(highWeightWords != null && highWeightWords.contains(tKeyWord)){
					tq.setBoost(5F);
                    //tq = new BoostQuery(tq, 5F);
				}
				query.add(tq, BooleanClause.Occur.SHOULD);
			}
		}
		System.out.println("------------");
		IndexReader ireader = DirectoryReader.open(directory);
		IndexSearcher isearcher = new IndexSearcher(ireader);
		TopDocs topDocs = isearcher.search(query, cnt);
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		List<Document> results = new ArrayList<Document>();
		for (int i = 0; i < topDocs.totalHits; i++) {
			if(i >= cnt)break;
			ScoreDoc score = scoreDocs[i];
			Document targetDoc = isearcher.doc(scoreDocs[i].doc);
			//System.out.println("score="+score.score+",内容：" + targetDoc.toString());
			String t = targetDoc.get(DocumentField.TYPE.value());
			results.add(targetDoc);
		}
		return results;
	}
			

	public static void main(String[] args) throws Exception {
		ConfigUtils.setPropsFromResource(TableTools.class, "db.properties");
		StkSearch ks = StkSearch.getInstance();
		List<Document> stks = ks.search("政府的金融能源运营商其他安全产品及第三方产品安全服务",DocumentType.STK, 10, null);
		System.out.println(stks);

	}

}
