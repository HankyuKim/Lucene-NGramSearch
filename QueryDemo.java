import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;


public class QueryDemo 
{
	public static void main(String[] args) throws IOException
	{
		String input;
		if(args.length == 0)
		{
			System.out.println("Enter search terms");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			input = br.readLine();
		}
		else
			input = args[0];
		
		Query query = NGramQueryParser.parse(input, 1);

		Long time = System.currentTimeMillis();
	    IndexReader reader = IndexReader.open(FSDirectory.open(new File("D:\\result")));
	    IndexSearcher searcher = new IndexSearcher(reader);
		TopDocs docs = searcher.search(query,10);
		
		System.out.println("Number of hits: "+docs.totalHits);
		System.out.println("Time taken: "+ (System.currentTimeMillis()-time));
		
		ScoreDoc[] sd = docs.scoreDocs;
		for(int i=0; i<sd.length; i++)
		{
			System.out.println(searcher.doc(sd[i].doc).toString() + sd[i]);
		}
		System.out.println(query.toString());
	}

}
