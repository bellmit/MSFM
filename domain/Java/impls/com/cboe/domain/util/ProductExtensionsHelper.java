package com.cboe.domain.util;

import com.cboe.exceptions.DataValidationException;
import com.cboe.util.ExceptionBuilder;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;

import java.util.StringTokenizer;

/**
 *  06/14/05
 *  Helper class for Extensions on ProductClass/ReportingClass/Product.
 *  Format for QPE version in Extensions field
 *      "QPE:1=0;QPE:2=1;QPE:3=1" , where PREFIX = QPE: and SUFFIX = ; and Values [ 0=false, 1= true]
 *  @author Uma Diddi
 */
public class ProductExtensionsHelper
{
    private static final String EXTENSIONS_PREFIX = "QPE:";
    private static final String EXTENSIONS_SUFFIX = ";";
    private static final String QPE_FLAG_VALUE_DELIMITER = "=";

    /**
     *  Returns QPE flag value for the specified version [QPE:1 or QPE:2 or so on ...]
     */
    public static boolean getQpeIndicator(String extensionsString, int qpeVersion) throws DataValidationException
    {
        boolean qpeIndicatorValue = false;
        StringTokenizer tokens = getQpeTokens(extensionsString);

        // Now parse each token [1=0 or 2=1 and so on], to get qpeIndicator value...
        while(tokens.hasMoreElements())
        {
            int count =1;
            String parseToken = tokens.nextToken();
            if(count == qpeVersion) { // get the flag value for each qpe version 1 or 2 etc...
                int startIndex = parseToken.indexOf(EXTENSIONS_PREFIX);
                if(startIndex == -1 && qpeVersion == count) {
                    throw ExceptionBuilder.dataValidationException("QPE Indicator[" + count + "] value not provided in extensions field", DataValidationCodes.INVALID_EXTENSIONS);
                }
                StringTokenizer values = new StringTokenizer(parseToken, QPE_FLAG_VALUE_DELIMITER);
                int flag =0; int cnt =0 ;
                while(values.hasMoreElements()) {
                    String tok = values.nextToken();
                    cnt++;
                    if (cnt == 2) {
                        try {
                            flag = Integer.parseInt(tok);
                        }
                        catch (NumberFormatException ne) {
                            throw ExceptionBuilder.dataValidationException("QPE Indicator[" + count + "] value is neither 0 or 1", DataValidationCodes.INVALID_EXTENSIONS);
                        }
                    }
                }
                if(flag == 1) {
                    return true;
                }
                else if(flag == 0) {
                    return false;
                }
                else {
                    throw ExceptionBuilder.dataValidationException("QPE Indicator[" + count + "] value not provided in extensions field", 0);
                }
            }
            count++;
        }
        return qpeIndicatorValue;
    }

    /**
     *  Returns all the QPE versions flag values in specified in the extensions field.
     *  If any one version is not valid, exception in returned ...
     */
    public static boolean[] getQpeIndicators(String extensionsString) throws DataValidationException
    {
        StringTokenizer tokens = getQpeTokens(extensionsString);
        boolean[] qpeIndicatorValues = new boolean[tokens.countTokens()];
        // Now parse each token [QPE1=0 or QPE2=1 and so on], to get qpeIndicator ...
        while(tokens.hasMoreElements())
        {
            int count =0;
            String parseToken = tokens.nextToken();
            int startIndex = parseToken.indexOf(EXTENSIONS_PREFIX);
            if(startIndex == -1) {
                throw ExceptionBuilder.dataValidationException("QPE Indicator[" + count + "] value not provided in extensions field", DataValidationCodes.INVALID_EXTENSIONS);
            }
            StringTokenizer values = new StringTokenizer(parseToken, QPE_FLAG_VALUE_DELIMITER);
            int flag = 0; int cnt = 0;
            while(values.hasMoreElements()) {
                String tok = values.nextToken();
                cnt++;
                if (cnt == 2) {
                    try {
                        flag = Integer.parseInt(tok);
                        if(flag == 1) {
                            qpeIndicatorValues[count] = true;
                        }
                        else if(flag == 0) {
                            qpeIndicatorValues[count] = false;
                        }
                        else {
                            throw ExceptionBuilder.dataValidationException("QPE Indicator[" + count + "] value not provided in extensions field", 0);
                        }
                    }
                    catch (NumberFormatException ne) {
                        throw ExceptionBuilder.dataValidationException("QPE Indicator[" + count + "] value not in valid format", DataValidationCodes.INVALID_EXTENSIONS);
                    }
                }
            }
            count++;
        }
        return qpeIndicatorValues;
    }

    /*
    *   Validates if QpeIndicators have been provided in the extensions field.
    *   Gets all tokens delimited by EXTENSIONS_SUFFIX which is ";"
    */
    private static StringTokenizer getQpeTokens(String extensionsString) throws DataValidationException
    {
        StringTokenizer tokens = new StringTokenizer(extensionsString, EXTENSIONS_SUFFIX);
        if( tokens.countTokens() == 0) {
            throw ExceptionBuilder.dataValidationException("QPE Indicator Value not provided in extensions field", DataValidationCodes.INVALID_EXTENSIONS);
        }
        return tokens;
    }
}
