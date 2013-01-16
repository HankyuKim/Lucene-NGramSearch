import java.io.IOException;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.WildcardQuery;


public class NGramQueryParser 
{
	public static Query parse(String input, double matchRate) throws IOException
	{
		// Decide between WildcardQuery and NGramFuzzyQuery, depending on the length of query
		if(input.length() <= 2)
		{
			input = input.concat("?");
			Term singleTerm = new Term("contents", input);
			return new WildcardQuery(singleTerm);
		}
		
		return new NGramFuzzyQuery(new Term("contents", input), matchRate);
	}
	
	public static Query parse(String input) throws IOException
	{
		return NGramQueryParser.parse(input, 0.5);
	}


}
