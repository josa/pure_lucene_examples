package com.gfuture.examples.pure_lucene;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

public class RAMDirectoryTest {

	private static RAMDirectory directory = new RAMDirectory();
	private IndexWriter indexWriter;

	@Test
	public void shouldIndexDocuments() throws CorruptIndexException, IOException{
		
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(
				Version.LUCENE_34, new StandardAnalyzer(Version.LUCENE_34));
		
		indexWriter = new IndexWriter(directory, indexWriterConfig);
		
				
		Document josa = new Document();
		josa.add(new Field("id", "akcjdkas01", Store.YES, Field.Index.NOT_ANALYZED));
		josa.add(new Field("name", "Jeosadache Santos Galvão", Store.YES, Field.Index.ANALYZED));
		
		Document caio = new Document();
		caio.add(new Field("id", "akcjdkas02", Store.YES, Field.Index.NOT_ANALYZED));
		caio.add(new Field("name", "Caio Marinho Galvão", Store.YES, Field.Index.ANALYZED));
		
		Document other = new Document();
		other.add(new Field("id", "akcjdkas03", Store.YES, Field.Index.NOT_ANALYZED));
		other.add(new Field("name", "Other da Silva Sauro", Store.YES, Field.Index.ANALYZED));
		
		indexWriter.addDocument(josa);
		indexWriter.addDocument(caio);
		indexWriter.addDocument(other);
		
		indexWriter.close();
	}

	@Test
	public void shouldSearchByFullName() throws CorruptIndexException, IOException, ParseException{
		IndexSearcher searcher = new IndexSearcher(directory);
		
		QueryParser queryParser = new QueryParser(Version.LUCENE_34, "name", new StandardAnalyzer(Version.LUCENE_34));
		Query query = queryParser.parse("name: \"Jeosadache Santos Galvão\"");
		
		TopDocs rs = searcher.search(query, null, 10);
		assertEquals(1, rs.totalHits);
		Document firstHit = searcher.doc(rs.scoreDocs[0].doc);
		assertEquals("Jeosadache Santos Galvão", firstHit.get("name"));
	}

	@Test
	public void shouldSearchByKeyword() throws CorruptIndexException, IOException, ParseException{
		IndexSearcher searcher = new IndexSearcher(directory);
		
		QueryParser queryParser = new QueryParser(Version.LUCENE_34, "name", new StandardAnalyzer(Version.LUCENE_34));
		Query query = queryParser.parse("name: Galvão");
		
		TopDocs rs = searcher.search(query, null, 10);
		assertEquals(2, rs.totalHits);
		assertEquals("Jeosadache Santos Galvão", searcher.doc(rs.scoreDocs[0].doc).get("name"));
		assertEquals("Caio Marinho Galvão", searcher.doc(rs.scoreDocs[1].doc).get("name"));
	}
	
}