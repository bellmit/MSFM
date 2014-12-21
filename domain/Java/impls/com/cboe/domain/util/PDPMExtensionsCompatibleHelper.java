/**
 * 
 */
package com.cboe.domain.util;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.Order;

/**
 * @author misbahud
 *
 */
public class PDPMExtensionsCompatibleHelper
{
    public static final char NEW_FIELD_DELIMITER = '\u0001';
    public static final char OLD_FIELD_DELIMITER = ',';
    public static final String TIERED_PDPM_KEY = "PDPM=";
    public static final String DEFAULT_TAG_DELIMITER = "=";
    public static final int PDPM_KEY_LENGTH = TIERED_PDPM_KEY.length();
    
    public static String getField(String extensions)
    {
        StringBuilder pdpmBuffer = new StringBuilder();       
        if (extensions.length() > PDPM_KEY_LENGTH)
        {
            
            int beginOffset = extensions.indexOf(TIERED_PDPM_KEY);
            if(beginOffset > -1)
            {
            beginOffset += PDPM_KEY_LENGTH;
            String partialExtensions = extensions.substring(beginOffset, extensions.length());
            if(partialExtensions != null && 
               partialExtensions.length() > 0 &&
               !"BARTID=null".equals(extensions))  
            {
                StringCharacterIterator pdpmItr = new StringCharacterIterator(partialExtensions);
                for(char c = pdpmItr.first(); c != CharacterIterator.DONE; c = pdpmItr.next()) 
                {
                    if(c == NEW_FIELD_DELIMITER || c == OLD_FIELD_DELIMITER)
                    {
                        break;
                    }
                    pdpmBuffer.append(c);
                    }
                }
            }
        }
        return pdpmBuffer.toString();
    }
    
    
    /**
     * Fetches the PDPM list from the order extensions
     * @param extensions
     * @return
     */
    public static List<String> deCodePDPMListFromExtensions(String extensions)
    {
        List<String> tieredPdpms = new ArrayList<String>();
                    
        if (extensions != null && extensions.trim().length() > 0)
        {
            String pdpms = getField(extensions);
            
            if (pdpms.length()>0) {
                pdpms = pdpms.trim();
            }

            if(pdpms != null && 
               pdpms.length() > 0)
            {
                int start = 0;
                while ((start + 3) <= pdpms.length())
                {
                    tieredPdpms.add(pdpms.substring(start, start + 3));
                    start += 3;
                }
            }
        }
        return tieredPdpms;
    }

}
