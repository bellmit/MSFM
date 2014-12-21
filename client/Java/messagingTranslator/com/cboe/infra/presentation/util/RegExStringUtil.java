package com.cboe.infra.presentation.util;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExStringUtil
{
    private static Map patternCache = new HashMap();
    
    /**
     * Returns only the patternToRemove portion of fullString, or the entire
     * String if the pattern was not found.
     */
    
    public static String extract( String patternToExtract, String fullString )
    {
        String rv = null;
        Pattern p = (Pattern)patternCache.get( patternToExtract );
        if( p == null )
        {
            p = Pattern.compile( patternToExtract );
            patternCache.put( patternToExtract, p );
        }
        Matcher m = p.matcher( fullString );
        if( m.find() )
        {
            rv = fullString.substring( m.start(), m.end() );
        }
        else
        {
            rv = fullString;
        }
        return rv;
    }
    
    
    /**
     * Returns the fullString, minus the patternToRemove, or the entire
     * fullString if the pattern was not found in the fullString.
     */
    
    public static String strip( String patternToRemove, String fullString )
    {
		String rv = fullString.replaceAll(patternToRemove,"");
		return rv;
//       	StringBuffer rv = new StringBuffer();
//        Pattern p = (Pattern)patternCache.get( patternToRemove );
//        if( p == null )
//        {
//            p = Pattern.compile( patternToRemove );
//            patternCache.put( patternToRemove, p );
//        }
//        Matcher m = p.matcher( fullString );
//        // this loops leaves out the last fragment
//        // e.g. for patternToRemove "is" and fullString Mississippi,
//        // this returns:
//        // Mss" and leaves off the "ippi"
//        int validIdx = 0 ;
//        int matchEnd = 0;
//        while ( m.find() )
//        {
//        	rv.append( fullString.substring(validIdx, m.start()) );
//        	matchEnd = m.end();
//        	validIdx = matchEnd;
//        }
//        // if there was a match, then we need to paste on
//        // the last piece (i.e. "ippi")
//        if ( validIdx < fullString.length() ) 
//        {
//        	rv.append( fullString.substring(matchEnd) );	
//        } else if ( validIdx == 0 )
//        {
//        	// there was no match (validIdx was never incremented),
//        	// so return the full string.
//            rv.append( fullString );
//        }
//        return rv.toString();
    }
}
