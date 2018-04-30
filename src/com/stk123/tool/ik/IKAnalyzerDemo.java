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
* Field.Store.COMPRESS:ѹ������,���ڳ��ı������������ 
* Field.Store.YES:����
* Field.Store.NO:������
* 
* Ϊ�����Field.Store.YES��Field.Store.NO������
* ���Կ���ֻҪANALYZED�ͻὨ�����������������ܲ顣
* Store��Ŀ����ͨ��ȫ�ļ����ܷ��ض�Ӧ�����ݡ������Store�������е�Ŀ�ġ���������ͨ��idȥDB�м��ء�
* 
* Index.NO ����������
* Index.ANALYZED �ִʺ�������
* Index.NOT_ANALYZED ���ִʣ�������������Ϊһ���ʽ�������
* 
* Field.Index.NO:���������� 
* Field.Index.TOKENIZED:�ִ�,������
* Field.Index.UN_TOKENIZED:���ִ�,������
* Field.Index.NO_NORMS:���ִ�,������.����Field��ֵ����ͨ�����������棬����ֻȡһ��byte��������Լ�洢�ռ�
* 
* Field.TermVector.NO:������term vectors 
* Field.TermVector.YES:����term vectors
* Field.TermVector.WITH_POSITIONS:����term vectors.(����ֵ��tokenλ����Ϣ) 
* Field.TermVector.WITH_OFFSETS:����term vectors.(����ֵ��Token��offset)
* Field.TermVector.WITH_POSITIONS_OFFSETS:����term vectors.(����ֵ��tokenλ����Ϣ��Token��offset)
* 
* BooleanClause.Occur.MUST  Use this operator for clauses that must appear in the matching documents. 
* BooleanClause.Occur.MUST_NOT  Use this operator for clauses that must not appear in the matching documents. 
* BooleanClause.Occur.SHOULD   Use this operator for clauses that should appear in the matching documents. 
*/

public class IKAnalyzerDemo {
	static String text2="�ƹ�������";
	static String text="��with love ȫ����10��11��Ѷ���� ���޿Ƽ���300017����������������䷢����ҵ��Ԥ�����棬��˾Ԥ��ǰ������ӯ��1.24��Ԫ-1.41��Ԫ��ͬ������130%-160%��ȥ��ͬ�ڹ�˾ӯ��5404.7��Ԫ��������ʾ��2013��ǰ������, ��˾�Ǿ���������Ծ�����Ӱ����Ϊ1388.98��Ԫ.";
	
	public static void main(String[] args) throws Exception {
		Configuration cfg = DefaultConfig.getInstance();  //���شʿ�
        cfg.setUseSmart(true); //�������ִܷ�
        Dictionary.initial(cfg);
        
        Dictionary dictionary = Dictionary.getSingleton();
        List<String> words = new ArrayList<String>();
        words.add("���޿Ƽ�");
        words.add("��������");
        dictionary.addWords(words);  //�Զ�����Զ����
        List<String> stopwords = new ArrayList<String>();
        
        /*stopwords.add("��˾");
        stopwords.add("��");
        stopwords.add("˾");*/
        dictionary.disableWords(stopwords);
        
        System.out.println(cfg.getExtDictionarys());
        System.out.println(cfg.getExtStopWordDictionarys());
        System.out.println(cfg.getMainDictionary()); // ��ȡ���ʵ�·��
        System.out.println(cfg.getQuantifierDicionary());//��ȡ���ʴʵ�·��

        /*Hit hit = dictionary.matchInMainDict("������".toCharArray());
        System.out.println(hit.isMatch());*/
		
		//split();
		System.out.println();
		split2("IT֧����ά��");
		
		search();
		System.out.println();
		
		index();
		
		//System.out.println(highlight(text,"��������"));
	}
	
	public static void split() throws IOException{
        StringReader sr=new StringReader(text);  
        IKSegmenter ik=new IKSegmenter(sr, true);  // true�����ִܷʡ���falseϸ����
        Lexeme lex=null;  
        while((lex=ik.next())!=null){  
            System.out.print(lex.getLexemeText()+"|");  
        }  
	}
	
	public static void split2(String text) throws IOException{
        //�����ִʶ���  
        Analyzer anal=new IKAnalyzer(true);       

        StringReader reader=new StringReader(text);
        //�ִ�  
        TokenStream ts=anal.tokenStream(null, reader);
        CharTermAttribute term=ts.getAttribute(CharTermAttribute.class);  
        //�����ִ�����  
        while(ts.incrementToken()){
        	TermQuery tq = new TermQuery(new Term(null, term.toString()));
        	tq.setBoost(3F);
            System.out.print(term.toString()+"|"); 
        }
        reader.close();
        System.out.println();
	}

	public static void search() throws Exception {
		// ʵ����IKAnalyzer�ִ���
		Analyzer analyzer = new IKAnalyzer(true);
		Directory directory = null;
		IndexWriter iwriter = null;
		IndexReader ireader = null;
		IndexSearcher isearcher = null;
		try {
			// �����ڴ���������
			directory = new RAMDirectory();
			//To store an index on disk, use this instead:
		    //directory = FSDirectory.open("/tmp/testindex");
			
			// ����IndexWriterConfig
			IndexWriterConfig iwConfig = new IndexWriterConfig(Version.LUCENE_45, analyzer);
			iwConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
			iwriter = new IndexWriter(directory, iwConfig);
			// д������
			Document doc = new Document();
			doc.add(new Field("ID", "10000", Field.Store.YES, Field.Index.NOT_ANALYZED));
			doc.add(new TextField("content", text, Field.Store.YES));

			iwriter.addDocument(doc);
			
			doc = new Document();
			doc.add(new Field("ID", "10001", Field.Store.YES, Field.Index.NOT_ANALYZED));
			Field title = new Field("title", "���޿Ƽ�_title", Field.Store.YES,Field.Index.ANALYZED);
			title.setBoost(2F);//��Field����Ȩ��
			doc.add(title);
			doc.add(new Field("time",DateTools.dateToString(new Date(), DateTools.Resolution.SECOND),Field.Store.YES,Field.Index.ANALYZED));
			doc.add(new TextField("content", text2, Field.Store.YES));
			iwriter.addDocument(doc);
			
			iwriter.commit();
			iwriter.close();
			
			
			// ��������**********************************
			// ʵ����������
			ireader = DirectoryReader.open(directory);

			isearcher = new IndexSearcher(ireader);
			String keyword = "���޿Ƽ�,������������������1388.98,�ƹ���";
			System.out.println(keyword);
			split2(keyword);
			String[] fields = { "title", "content" };
			BooleanClause.Occur[] flags = { 
					BooleanClause.Occur.SHOULD,
					BooleanClause.Occur.SHOULD //��
			};
			//Query query = MultiFieldQueryParser.parse(Version.LUCENE_45, keyword, fields, flags, analyzer);// new QueryParser(Version.LUCENE_45, fieldName,analyzer);
			//QueryParser qp = new QueryParser(Version.LUCENE_34,fieldName, analyzer);
			//qp.setDefaultOperator(QueryParser.OR_OPERATOR);
			//Query query = qp.parse(keyword);
			
			
			//�Բ�ѯ������Ȩ��
			BooleanQuery query = new BooleanQuery();
			for(int i=0;i<fields.length;i++){
				IKSegmenter se = new IKSegmenter(new StringReader(keyword),true);
				Lexeme le = null;
				while((le=se.next()) != null){
					String tKeyWord = le.getLexemeText();
					TermQuery tq = new TermQuery(new Term(fields[i],tKeyWord));
					if("��������".equals(tKeyWord)){
						tq.setBoost(10F);
					}
					query.add(tq, BooleanClause.Occur.SHOULD);
				}
			}
			
			Set<Term> terms = new HashSet<Term>();
			query.extractTerms(terms);
			System.out.println("terms="+terms);

			/*****  ����  *****/
			   /*
			    * 1.��������ֶα��뱻������(Indexecd)��������ʱ���� �� Field.Index.TOKENIZED
			    *   (��UN_TOKENIZED��������ʵ��.��NOʱ��ѯ����������������������������)
			    * 2.SortField����
			    *   SCORE��DOC��AUTO��STRING��INT��FLOAT��CUSTOM ��������Ҫ�Ǹ����ֶε�����ѡ��
			    * 3.SortField�ĵ��������������Ƿ��ǽ���true:����  false:����
			    */
			   Sort sort = new Sort(new SortField[]{new SortField("time", SortField.Type.INT, true)});
			  //TopDocs topDocs = isearcher.search(query,3,sort);
			   
			   //�Ȱ���¼�÷�����Ȼ���ٰ���¼����ʱ�䵹��
			   sort = new Sort(new SortField[]{new SortField(null, SortField.Type.SCORE, true),new SortField("time", SortField.Type.INT, true)});
			   /* �費��Ҫ����أ�
			   TopFieldCollector collector = TopFieldCollector.create(sort, 10, false, true, false, false); 
			   isearcher.search(query, collector);
			   TopDocs topDocs = collector.topDocs();*/
			   
					   
			// �������ƶ���ߵ�5����¼
			TopDocs topDocs = isearcher.search(query, 5);
			
			//ͳ�ƹؼ���ƥ����� begin
			long cnt = ireader.totalTermFreq(new Term("content","����"));
			System.out.println("count======"+cnt);//5��
			//ͳ�ƹؼ���ƥ����� end

			System.out.println("���У�" + topDocs.totalHits+", ��������:"+topDocs.getMaxScore());
			// ������
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
			for (int i = 0; i < topDocs.totalHits; i++) {
				ScoreDoc score = scoreDocs[i];
				Document targetDoc = isearcher.doc(scoreDocs[i].doc);
				
				System.out.println("score="+score.score+",���ݣ�" + targetDoc.toString());
				//System.out.println(isearcher.explain(query, scoreDocs[i].doc).getValue());
				
				//-----------------------keyword������ʾ-------------------//
				String text = targetDoc.get("content");
				if (text != null) {
					SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<font color='red'>", "</font>");
					Highlighter highlighter = new Highlighter(simpleHTMLFormatter,new QueryScorer(query));
					highlighter.setTextFragmenter(new SimpleFragmenter(ChineseUtils.length(text)));
				
					TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(text));
					String highLightText = highlighter.getBestFragment(tokenStream,text);
					System.out.println("�������ʾ�� " + (i + 1) + " ���������������ʾ��");
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

		// ��ʾdocument��
		System.out.println(new Date());
		System.out.println(reader + "\t���������� " + reader.numDocs() + "ƪ�ĵ�\n");

		for (int i = 0; i < reader.numDocs(); i++) {
			System.out.println("�ĵ�" + i + "��" + reader.document(i) + "\n");
		}

		// ö��term�����<document, term freq, position* >��Ϣ
		TermsEnum termEnum = reader.terms();
		while (termEnum.next()) {
			System.out.println(termEnum.term().field() + "���г��ֵĴ��"
					+ termEnum.term().text());
			System.out.println(" ���ָĴʵ��ĵ���=" + termEnum.docFreq());

			TermPositions termPositions = reader.termPositions(termEnum.term());
			int i = 0;
			int j = 0;
			while (termPositions.next()) {
				System.out.println((i++) + "->" + "    ���±��:"
						+ termPositions.doc() + ", ���ִ���:"
						+ termPositions.freq() + "    ����λ�ã�");
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
