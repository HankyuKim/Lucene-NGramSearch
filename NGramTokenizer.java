
import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.util.Version;


public class NGramTokenizer extends Tokenizer
{
	
	private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
	private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);
	
	private final org.apache.lucene.analysis.util.CharacterUtils charUtils;
	private final org.apache.lucene.analysis.util.CharacterUtils.CharacterBuffer iobuffer = org.apache.lucene.analysis.util.CharacterUtils.newCharacterBuffer(4092);
	
	private int dataLength = -1;
	private int startOffset = 0;
	private int position = 0;
	private final int GRAM_SIZE = 3;
	
	public NGramTokenizer(Version version, Reader reader)
	{
		super(reader);
		charUtils = org.apache.lucene.analysis.util.CharacterUtils.getInstance(version);
	}

	@Override
	public boolean incrementToken() throws IOException 
	{
		clearAttributes();
		char[] termBuffer = termAtt.buffer();
		termAtt.setLength(GRAM_SIZE);
		
		startOffset++;							// Values for offset attribute
		offsetAtt.setOffset(startOffset, startOffset+ GRAM_SIZE-1);

//   	 For variable gram sizes...
//    	for(int i=0; i<GRAM_SIZE-1; i++)
//    	{
//    		termBuffer[i] = termBuffer[i+1];
//    	}
    	
    	termBuffer[0] = termBuffer[1];			// Shift characters to left
    	termBuffer[1] = termBuffer[2];
    	
    	// Get next non-whitespace character
		int c = ' '; 
    	while(Character.isWhitespace(c))
    	{
    		if(position >= dataLength) // Read in buffer, if position gets out of bound
    		{
    			if(charUtils.fill(iobuffer, input))
    			{
    				dataLength = iobuffer.getLength();
    				position = 0;
    			}
    			else	// EOF
    				return false;
    		}
    		
			c = charUtils.codePointAt(iobuffer.getBuffer(), position);	// Get next character
        	position++;
    	}
    	
	    Character.toChars(c, termBuffer, GRAM_SIZE-1);

		return true;
	}
	
}
