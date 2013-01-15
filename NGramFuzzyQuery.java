
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;

public class NGramFuzzyQuery extends SpanTermQuery
{
	private ArrayList<SpanQuery> clauses;
	private double matchRate;
	
	public NGramFuzzyQuery(Term term, double matchRate) throws IOException {
		super(term);
		this.setMatchRate(matchRate);
		clauses = new ArrayList<SpanQuery>();
		for(Term t: analyze(term))
		{
			clauses.add(new SpanTermQuery(t));
		}
	}

	public Term[] analyze(Term term) throws IOException
	{
//		if(term.text().length() == 2)		// If term's length is 2, do wildcardquery
//		{
//			Term[] singleTerm = new Term[1];
//			singleTerm[0] = new Term(term.field(), term.text().concat("?"));
//			return singleTerm;
//		}
		NGramAnalyzer analyzer = new NGramAnalyzer();
		TokenStream stream = analyzer.tokenStream(null, new StringReader(term.text()));
		CharTermAttribute termAtt = stream.addAttribute(CharTermAttribute.class);

		ArrayList<Term> splitlist = new ArrayList<Term>(4);
		try {
			while(stream.incrementToken())
			{
				splitlist.add(new Term(term.field(), termAtt.toString()));
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return splitlist.toArray(new Term[0]);
	}

	public SpanQuery[] getClauses()
	{
		return clauses.toArray(new SpanQuery[0]);
	}

	  @Override
	  public Weight createWeight(IndexSearcher searcher) throws IOException {
	    return new NGramWeight(this, searcher);
	  }

	public void setMatchRate(double matchRate) {
		this.matchRate = matchRate;
	}

	public double getMatchRate() {
		return matchRate;
	}
}
