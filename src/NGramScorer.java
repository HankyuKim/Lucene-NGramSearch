import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;

import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.search.similarities.Similarity.SloppySimScorer;
import org.apache.lucene.search.spans.Spans;


public class NGramScorer extends Scorer {
	protected final Similarity.SloppySimScorer docScorer;
	protected boolean[] more;
	protected int docID;
	protected float freq;
	
	protected Spans[] spans;
	private ArrayList<Integer>	posList = new ArrayList<Integer>();
	private int combo;
	private double matchRate = 0.5;

	protected NGramScorer(Spans[] spans, Weight weight, SloppySimScorer docScorer)throws IOException 
	{
		super(weight);
		this.spans = spans;
		this.docScorer = docScorer;
		this.more = new boolean[spans.length];

		for(int i=0; i<spans.length; i++)
		{
		    if (spans[i].next()) {
		    	docID = -1;
		    	more[i] = true;
		      } else {
		        docID = NO_MORE_DOCS;

		        more[i] = false;
		      }
		}
	}
	
	protected boolean setFreqCurrentDoc() throws IOException 
	{
		docID = spans[0].doc();
		boolean moreDocs = false;
		for(int m=0; m<more.length; m++)
		{
			if(spans[m].doc() < docID && spans[m].doc() != -1)
				docID = spans[m].doc();
			
			if (more[m])
			{
				moreDocs = true;
			}
		}
		if(!moreDocs)
			return false;

//		long time = System.currentTimeMillis();
	    freq = 0.0f;
	    posList.clear();
//		time = System.currentTimeMillis()-time;
//		System.out.println("Time taken to empty list: "+ time);
//	    int existingTrigrams = 0;
//		System.out.println("================="+docID+"===============");
//		time = System.currentTimeMillis();
		for(int i=0; i<spans.length; i++)
		{
			if(spans[i].doc() == docID)
			    do
			    {
//			    	System.out.println(spans[i]);
			    	int matchLength = spans[i].end() - spans[i].start();
			    	posList.add(spans[i].start());
			    	freq += docScorer.computeSlopFactor(matchLength);
			    	more[i] = spans[i].next();
			    }while(more[i] && docID == spans[i].doc());	
		}
//		System.out.println("NGrams to go through: "+posList.size());
//		time = System.currentTimeMillis()-time;
//		System.out.println("Time taken to merge spans: "+ time);
//		time = System.currentTimeMillis();
		Collections.sort(posList);
//		time = System.currentTimeMillis()-time;
//		System.out.println("Time taken to sort array: "+ time);
		combo = 0;

//		time = System.currentTimeMillis();
		int c = 0;
		for(int i=0; i<posList.size()-1; i++)
		{
			int distance = posList.get(i+1) - posList.get(i);
			if(distance < 5)
			{
				c++;
				if(combo<c)
					combo = c;
			}
			else
				c=0;
		}
//		time = System.currentTimeMillis()-time;
//		System.out.println("Time taken to count combo: "+ time);
//		System.out.println(combo +"/"+spans.length/2);
		if(combo < (spans.length-1)*matchRate)
		{
			return setFreqCurrentDoc();
		}
		
	    return true;
	}

	  
	@Override
	public float score() throws IOException {
		float score = docScorer.score(docID, freq);
		score *= (float)(combo+1)/(spans.length);
		
		return score;
	}



	@Override
	public float freq() throws IOException {
	    return freq;
	}



	@Override
	public int advance(int target) throws IOException 
	{
		//Return the docID which is the next matching document
		boolean endOfDocs = true;
	    for(int i=0; i<spans.length; i++)
	    {
	    	if (spans[i].doc() < target)
	    	{
	    		more[i] = spans[i].skipTo(target);
	    	}
	    	
	    	if (more[i]) 
		    {
		    	endOfDocs = false;
		    }
	    }
	    
	    if(endOfDocs)
	    	return docID = NO_MORE_DOCS;
	    
	    if (!setFreqCurrentDoc()) {
	    	docID = NO_MORE_DOCS;
	    }
	    
	    return docID;
	}



	@Override
	public int docID() {
		// TODO Auto-generated method stub
		return docID;
	}



	@Override
	public int nextDoc() throws IOException {
	    if (!setFreqCurrentDoc()) {
	        docID = NO_MORE_DOCS;
	      }
	      return docID;
	}

	public void setMatchRate(double matchRate) {
		this.matchRate = matchRate;
	}

	public double getMatchRate() {
		return matchRate;
	}
}
