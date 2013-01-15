import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.Spans;
import org.apache.lucene.util.Bits;


public class NGramWeight extends Weight 
{
	  protected Similarity similarity;
	  protected Map<Term,TermContext> termContexts;
	  protected SpanQuery[] queries;
	  protected NGramFuzzyQuery query;
//	  protected ArrayList<SpanQuery> clauses;
	  protected Similarity.SimWeight stats;

//	public NGramWeight(ArrayList<SpanQuery> clauses, IndexSearcher searcher) throws IOException
//	{

	public NGramWeight(NGramFuzzyQuery query, IndexSearcher searcher) throws IOException
	{	
	    similarity = searcher.getSimilarity();
//	    this.clauses = clauses;
	    termContexts = new HashMap<Term,TermContext>();
	    TreeSet<Term> terms = new TreeSet<Term>();
	    
	    this.query = query;
	    queries = query.getClauses();
	    
	    for(int i=0; i<queries.length; i++)
	    {
		    queries[i].extractTerms(terms);
	    }
	    final IndexReaderContext context = searcher.getTopReaderContext();
	    final TermStatistics termStats[] = new TermStatistics[terms.size()];
	    int i = 0;
	    for (Term term : terms) {
	      TermContext state = TermContext.build(context, term, true);
	      termStats[i] = searcher.termStatistics(term, state);
	      termContexts.put(term, state);
	      i++;
	    }
	    try
	    {
		    final String field = queries[0].getField();

		    if (field != null) {
		      stats = similarity.computeWeight(queries[0].getBoost(), 
		                                       searcher.collectionStatistics(queries[0].getField()), 
		                                       termStats);
		    }
			
	    }catch(ArrayIndexOutOfBoundsException e)
	    {
	    	System.out.println("error");
	    }
	}

	  @Override
	  public Scorer scorer(AtomicReaderContext context, boolean scoreDocsInOrder,
	      boolean topScorer, Bits acceptDocs) throws IOException {
	    if (stats == null) {
	      return null;
	    } else {
	    	
	    	Spans[] spans = new Spans[queries.length];
	    	for(int i=0; i<spans.length; i++)
	    	{
		    	spans[i] = queries[i].getSpans(context, acceptDocs, termContexts);
	    	}
	    	NGramScorer scorer = new NGramScorer(spans, this, similarity.sloppySimScorer(stats, context));
	    	scorer.setMatchRate(query.getMatchRate());
	      return scorer;
	    }
	  }

	@Override
	public Explanation explain(AtomicReaderContext arg0, int arg1)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Query getQuery() { return query; }

	@Override
	public float getValueForNormalization() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void normalize(float queryNorm, float topLevelBoost) {
	    if (stats != null) {
	        stats.normalize(queryNorm, topLevelBoost);
	      }
	}
}
