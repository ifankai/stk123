package com.stk123.tool.ik;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldCollector;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.cfg.Configuration;
import org.wltea.analyzer.cfg.DefaultConfig;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;
import org.wltea.analyzer.dic.Dictionary;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.stk123.tool.util.ChineseUtils;


/*
* Field.Store.COMPRESS:压缩保存,用于长文本或二进制数据 
* Field.Store.YES:保存
* Field.Store.NO:不保存
* 
* 为了体会Field.Store.YES与Field.Store.NO的区别：
* 可以看到只要ANALYZED就会建索引，建了索引就能查。
* Store的目的是通过全文检查就能返回对应的内容。这就是Store在索引中的目的。而不必在通过id去DB中加载。
* 
* Index.NO 不建立索引
* Index.ANALYZED 分词后建立索引
* Index.NOT_ANALYZED 不分词，把整个内容作为一个词建立索引
* 
* Field.Index.NO:不建立索引 
* Field.Index.TOKENIZED:分词,建索引
* Field.Index.UN_TOKENIZED:不分词,建索引
* Field.Index.NO_NORMS:不分词,建索引.但是Field的值不像通常那样被保存，而是只取一个byte，这样节约存储空间
* 
* Field.TermVector.NO:不保存term vectors 
* Field.TermVector.YES:保存term vectors
* Field.TermVector.WITH_POSITIONS:保存term vectors.(保存值和token位置信息) 
* Field.TermVector.WITH_OFFSETS:保存term vectors.(保存值和Token的offset)
* Field.TermVector.WITH_POSITIONS_OFFSETS:保存term vectors.(保存值和token位置信息和Token的offset)
* 
* BooleanClause.Occur.MUST  Use this operator for clauses that must appear in the matching documents. 
* BooleanClause.Occur.MUST_NOT  Use this operator for clauses that must not appear in the matching documents. 
* BooleanClause.Occur.SHOULD   Use this operator for clauses that should appear in the matching documents. 
*/

public class IKAnalyzerDemo {
	static String text2="制管类周五";
	static String text="我with love 全景网10月11日讯公告 网宿科技（300017）桂林三金周五晚间发布的业绩预增公告，公司预计前三季度盈利1.24亿元-1.41亿元，同比增长130%-160%。去年同期公司盈利5404.7万元。公告显示，2013年前三季度, 公司非经常性损益对净利润影响金额为1388.98万元.";
	
	public static void main(String[] args) throws Exception {
		Configuration cfg = DefaultConfig.getInstance();  //加载词库
        cfg.setUseSmart(true); //设置智能分词
        Dictionary.initial(cfg);
        
        Dictionary dictionary = Dictionary.getSingleton();
        List<String> words = new ArrayList<String>();
        words.add("网宿科技");
        words.add("桂林三金");
        dictionary.addWords(words);  //自动添加自定义词
        List<String> stopwords = new ArrayList<String>();
        
        /*stopwords.add("公司");
        stopwords.add("公");
        stopwords.add("司");*/
        dictionary.disableWords(stopwords);
        
        System.out.println(cfg.getExtDictionarys());
        System.out.println(cfg.getExtStopWordDictionarys());
        System.out.println(cfg.getMainDictionary()); // 获取主词典路径
        System.out.println(cfg.getQuantifierDicionary());//获取量词词典路径

        /*Hit hit = dictionary.matchInMainDict("基础班".toCharArray());
        System.out.println(hit.isMatch());*/
		
		//split();
		System.out.println();
		split2("IT支持与维护");
		
		search();
		System.out.println();
		
		index();
		
		//System.out.println(highlight(text,"桂林三金"));
	}
	
	public static void split() throws IOException{
        StringReader sr=new StringReader(text);  
        IKSegmenter ik=new IKSegmenter(sr, true);  // true用智能分词　，false细粒度
        Lexeme lex=null;  
        while((lex=ik.next())!=null){  
            System.out.print(lex.getLexemeText()+"|");  
        }  
	}
	
	public static void split2(String text) throws IOException{
        //创建分词对象  
        Analyzer anal=new IKAnalyzer(true);       

        StringReader reader=new StringReader(text);
        //分词  
        TokenStream ts=anal.tokenStream(null, reader);
        CharTermAttribute term=ts.getAttribute(CharTermAttribute.class);  
        //遍历分词数据  
        while(ts.incrementToken()){
        	TermQuery tq = new TermQuery(new Term(null, term.toString()));
        	tq.setBoost(3F);
            System.out.print(term.toString()+"|"); 
        }
        reader.close();
        System.out.println();
	}

	public static void search() throws Exception {
		// 实例化IKAnalyzer分词器
		Analyzer analyzer = new IKAnalyzer(true);
		Directory directory = null;
		IndexWriter iwriter = null;
		IndexReader ireader = null;
		IndexSearcher isearcher = null;
		try {
			// 建立内存索引对象
			directory = new RAMDirectory();
			//To store an index on disk, use this instead:
		    //directory = FSDirectory.open("/tmp/testindex");
			
			// 配置IndexWriterConfig
			IndexWriterConfig iwConfig = new IndexWriterConfig(Version.LUCENE_45, analyzer);
			iwConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
			iwriter = new IndexWriter(directory, iwConfig);
			// 写入索引
			Document doc = new Document();
			doc.add(new Field("ID", "10000", Field.Store.YES, Field.Index.NOT_ANALYZED));
			doc.add(new TextField("content", text, Field.Store.YES));

			iwriter.addDocument(doc);
			
			doc = new Document();
			doc.add(new Field("ID", "10001", Field.Store.YES, Field.Index.NOT_ANALYZED));
			Field title = new Field("title", "网宿科技_title", Field.Store.YES,Field.Index.ANALYZED);
			title.setBoost(2F);//对Field设置权重
			doc.add(title);
			doc.add(new Field("time",DateTools.dateToString(new Date(), DateTools.Resolution.SECOND),Field.Store.YES,Field.Index.ANALYZED));
			doc.add(new TextField("content", text2, Field.Store.YES));
			iwriter.addDocument(doc);
			
			iwriter.commit();
			iwriter.close();
			
			
			// 搜索过程**********************************
			// 实例化搜索器
			ireader = DirectoryReader.open(directory);

			isearcher = new IndexSearcher(ireader);
			String keyword = "网宿科技,净利润增长桂林三金1388.98,制管类";
			System.out.println(keyword);
			split2(keyword);
			String[] fields = { "title", "content" };
			BooleanClause.Occur[] flags = { 
					BooleanClause.Occur.SHOULD,
					BooleanClause.Occur.SHOULD //或
			};
			//Query query = MultiFieldQueryParser.parse(Version.LUCENE_45, keyword, fields, flags, analyzer);// new QueryParser(Version.LUCENE_45, fieldName,analyzer);
			//QueryParser qp = new QueryParser(Version.LUCENE_34,fieldName, analyzer);
			//qp.setDefaultOperator(QueryParser.OR_OPERATOR);
			//Query query = qp.parse(keyword);
			
			
			//对查询词设置权重
			BooleanQuery query = new BooleanQuery();
			for(int i=0;i<fields.length;i++){
				IKSegmenter se = new IKSegmenter(new StringReader(keyword),true);
				Lexeme le = null;
				while((le=se.next()) != null){
					String tKeyWord = le.getLexemeText();
					TermQuery tq = new TermQuery(new Term(fields[i],tKeyWord));
					if("桂林三金".equals(tKeyWord)){
						tq.setBoost(10F);
					}
					query.add(tq, BooleanClause.Occur.SHOULD);
				}
			}
			
			Set<Term> terms = new HashSet<Term>();
			query.extractTerms(terms);
			System.out.println("terms="+terms);

			/*****  排序  *****/
			   /*
			    * 1.被排序的字段必须被索引过(Indexecd)，在索引时不能 用 Field.Index.TOKENIZED
			    *   (用UN_TOKENIZED可以正常实现.用NO时查询正常，但排序不能正常设置升降序)
			    * 2.SortField类型
			    *   SCORE、DOC、AUTO、STRING、INT、FLOAT、CUSTOM 此类型主要是根据字段的类型选择
			    * 3.SortField的第三个参数代表是否是降序true:降序  false:升序
			    */
			   Sort sort = new Sort(new SortField[]{new SortField("time", SortField.Type.INT, true)});
			  //TopDocs topDocs = isearcher.search(query,3,sort);
			   
			   //先按记录得分排序，然后再按记录发布时间倒序
			   sort = new Sort(new SortField[]{new SortField(null, SortField.Type.SCORE, true),new SortField("time", SortField.Type.INT, true)});
			   /* 需不需要这段呢？
			   TopFieldCollector collector = TopFieldCollector.create(sort, 10, false, true, false, false); 
			   isearcher.search(query, collector);
			   TopDocs topDocs = collector.topDocs();*/
			   
					   
			// 搜索相似度最高的5条记录
			TopDocs topDocs = isearcher.search(query, 5);
			
			//统计关键词匹配次数 begin
			long cnt = ireader.totalTermFreq(new Term("content","公告"));
			System.out.println("count======"+cnt);//5次
			//统计关键词匹配次数 end

			System.out.println("命中：" + topDocs.totalHits+", 最大的评分:"+topDocs.getMaxScore());
			// 输出结果
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
			for (int i = 0; i < topDocs.totalHits; i++) {
				ScoreDoc score = scoreDocs[i];
				Document targetDoc = isearcher.doc(scoreDocs[i].doc);
				
				System.out.println("score="+score.score+",内容：" + targetDoc.toString());
				//System.out.println(isearcher.explain(query, scoreDocs[i].doc).getValue());
				
				//-----------------------keyword高亮显示-------------------//
				String text = targetDoc.get("content");
				if (text != null) {
					SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<font color='red'>", "</font>");
					Highlighter highlighter = new Highlighter(simpleHTMLFormatter,new QueryScorer(query));
					highlighter.setTextFragmenter(new SimpleFragmenter(ChineseUtils.length(text)));
				
					TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(text));
					String highLightText = highlighter.getBestFragment(tokenStream,text);
					System.out.println("★高亮显示第 " + (i + 1) + " 条检索结果如下所示：");
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
	
	public static String highlight(String text, String key) {
		        if(StringUtils.isBlank(key) || StringUtils.isBlank(text))
		            return text;
		        String result = null;
		        IKAnalyzer analyzer = new IKAnalyzer();
		        Formatter highlighter_formatter = new SimpleHTMLFormatter("<span class=\"highlight\">","</span>");
		        try {
		            QueryScorer scorer = new QueryScorer(new TermQuery(new Term(null,QueryParser.escape(key))));
		            Highlighter hig = new Highlighter(highlighter_formatter, scorer);
		            TokenStream tokens = analyzer.tokenStream(null, new StringReader(text));
		            result = hig.getBestFragment(tokens, text);
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		        return (result != null)?result:text;
		    }
	
	
	public static void index() throws IOException, ParseException{
		Analyzer analyzer = new IKAnalyzer();

	    // Store the index in memory:
	    Directory directory = new RAMDirectory();
	    // To store an index on disk, use this instead:
	    //Directory directory = FSDirectory.open("/tmp/testindex");
	    IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_CURRENT, analyzer);
	    IndexWriter iwriter = new IndexWriter(directory, config);
	    Document doc = new Document();
	    String text = "This is the text to be indexed.";
	    doc.add(new Field("fieldname", text, TextField.TYPE_STORED));
	    iwriter.addDocument(doc);
	    iwriter.close();
	    
	    // Now search the index:
	    DirectoryReader ireader = DirectoryReader.open(directory);
	    IndexSearcher isearcher = new IndexSearcher(ireader);
	    // Parse a simple query that searches for "text":
	    QueryParser parser = new QueryParser(Version.LUCENE_CURRENT, "fieldname", analyzer);
	    Query query = parser.parse("text");
	    ScoreDoc[] hits = isearcher.search(query, null, 1000).scoreDocs;
	    System.out.println(hits.length);
	    // Iterate through the results:
	    for (int i = 0; i < hits.length; i++) {
	      Document hitDoc = isearcher.doc(hits[i].doc);
	      System.out.println(hitDoc.get("fieldname"));
	    }
	    ireader.close();
	    directory.close();
	}
	
	/*public static void printIndex(IndexReader reader) throws Exception {

		// 显示document数
		System.out.println(new Date());
		System.out.println(reader + "\t该索引共含 " + reader.numDocs() + "篇文档\n");

		for (int i = 0; i < reader.numDocs(); i++) {
			System.out.println("文档" + i + "：" + reader.document(i) + "\n");
		}

		// 枚举term，获得<document, term freq, position* >信息
		TermsEnum termEnum = reader.terms();
		while (termEnum.next()) {
			System.out.println(termEnum.term().field() + "域中出现的词语："
					+ termEnum.term().text());
			System.out.println(" 出现改词的文档数=" + termEnum.docFreq());

			TermPositions termPositions = reader.termPositions(termEnum.term());
			int i = 0;
			int j = 0;
			while (termPositions.next()) {
				System.out.println((i++) + "->" + "    文章编号:"
						+ termPositions.doc() + ", 出现次数:"
						+ termPositions.freq() + "    出现位置：");
				for (j = 0; j < termPositions.freq(); j++)
					System.out.print("[" + termPositions.nextPosition() + "]");
				System.out.println("\n");
			}

			
			 * TermDocs termDocs=reader.termDocs(termEnum.term());
			 * while(termDocs.next()){
			 * log.debug((i++)+"->DocNo:"+termDocs.doc()+
			 * ",Freq:"+termDocs.freq()); }
			 
		}

	}*/
}
