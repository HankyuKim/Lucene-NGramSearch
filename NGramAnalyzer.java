import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Analyzer.TokenStreamComponents;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
//import org.apache.lucene.analysis.ngram.NGramTokenizer;
import org.apache.lucene.util.Version;


public class NGramAnalyzer  extends Analyzer{

	private final Version matchVersion;
	
	public NGramAnalyzer()
	{
		this(Version.LUCENE_40);
	}
	
	public NGramAnalyzer(Version version)
	{
		super();
		matchVersion = version;
	}

//	@Override
//	public TokenStream tokenStream(String fieldName, Reader reader) 
//	{
//	}

	@Override
	protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
	    NGramTokenizer src = new NGramTokenizer(matchVersion, reader); // My NGramTokenizer
	    TokenStream tok = new LowerCaseFilter(matchVersion, src);
	    return new TokenStreamComponents(src, tok);
	}
}
