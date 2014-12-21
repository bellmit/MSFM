package com.cboe.domain.util;

import com.cboe.exceptions.DataValidationException;
import com.cboe.util.ExceptionBuilder;

import java.util.StringTokenizer;

/**
 *  Helper class for OptionalData.
 *  @author Uma Diddi
 *  @author Dmitry Volpyansky
 */
public class OptionalDataHelper
{
    private static final String EXECUTION_INSTRUCTION_PREFIX = "X:";
    private static final String EXECUTION_INSTRUCTION_SUFFIX = ";";
    private static final int EXECUTION_INSTRUCTION_MAX_LENGTH = 24;
    
    private static final String PREFERRED_FIRM_PREFIX             = "P:";
    private static final String PREFERRED_FIRM_EXCHANGE_SEPARATOR = ".";
    private static final String PREFERRED_FIRM_SUFFIX             = ";";

    /*  
    *   Parses out Execution Instruction from optional data 
    *   Execution instruction is prefixed by 'X:' and suffixed by ';'
    *   When only Prefixed by X:, execution instruction is the entire string after X: 
    *   Returns trimmed execution instruction.
    *   Throws exception, if the parsed and trimmed execution instruction string is not of valid length.
    */
    public static String getExecutionInstruction( String parseString ) throws DataValidationException
    {
	    String executionInstruction = "";

        int startIndex = parseString.indexOf(EXECUTION_INSTRUCTION_PREFIX) ;
		if(startIndex == -1) {
			return executionInstruction;
		}
		startIndex = startIndex + EXECUTION_INSTRUCTION_PREFIX.length();

        int endIndex = parseString.indexOf(EXECUTION_INSTRUCTION_SUFFIX, startIndex);
        
        // If the string starts with X: but doesn't end with ';'
        // Take the whole string after X:
        if(endIndex == -1)
        {
            endIndex = parseString.length();
        }
        
        executionInstruction = parseString.substring(startIndex,endIndex).trim();
        if(executionInstruction.length() > OptionalDataHelper.EXECUTION_INSTRUCTION_MAX_LENGTH)
        {
            throw ExceptionBuilder.dataValidationException("Execution Instruction can have a maximum of " + OptionalDataHelper.EXECUTION_INSTRUCTION_MAX_LENGTH + " characters only", 0);
        }
        return executionInstruction;		
    }

    /**
     * decodes optionalData, and extracts the PreferredFirm/FirmExchange, which is in one of two formats: P:EXCH.FIRM; or P:FIRM;
     * 
     * To use this method for validation, the <b>pair</b> parameter can be initialized with two nulls, and checked after the call returns to 
     * determine whether the firm and/or exchange were both set or not.
     * 
     * @param optionalData  -- string containing optional data, possibly containing the PreferredFirm instruction
     * @param pair          -- new String[2] to be filled in with FirmAcronym and FirmExchange
     * 
     * @return String[2], where str[0] is the FirmAcronym and str[1] is the FirmExchange
     */
    public static String[] decodePreferredFirmInstruction(String optionalData, String[] pair)
    {
        if (optionalData          == null ||
            pair                  == null ||
            optionalData.length() == 0)
        {
            return pair;
        }

        int indexStart = optionalData.indexOf(PREFERRED_FIRM_PREFIX);
        if (indexStart < 0)
        {
            return pair;
        }

        int indexEnd = optionalData.indexOf(PREFERRED_FIRM_SUFFIX, indexStart);
        if (indexEnd < 0)
        {
            return pair;
        }

        indexStart += PREFERRED_FIRM_PREFIX.length();
        if (indexEnd - indexStart < 1) // make sure that the end-start gives us at least ONE character for the firm acronym
        {
            return pair;
        }

        optionalData = optionalData.substring(indexStart, indexEnd);
        
        StringTokenizer tokenizer = new StringTokenizer(optionalData, PREFERRED_FIRM_EXCHANGE_SEPARATOR);
        
        if (tokenizer.countTokens() == 1 && !optionalData.endsWith("."))
        {
            if (pair.length >= 1)
            {
                pair[0] = tokenizer.nextToken().trim();
            }                
        }
        else if (tokenizer.countTokens() == 2)
        {
            if (pair.length >= 2)
            {
                pair[1] = tokenizer.nextToken().trim();
                pair[0] = tokenizer.nextToken().trim();
            }                
        }

        return pair;
    }
    
    /**
     * builds a valid PreferredFirm Instruction string from a FirmAcronym and FirmExchange
     * 
     * @param firmAcronym
     * @param firmExchange
     * 
     * @return valid PreferredFirm portion of the optionalData, or an empty string if could not build a valid string
     */
    public static String buildPreferredFirmInstruction(String firmAcronym, String firmExchange)
    {
        if (firmAcronym == null || firmAcronym.length() == 0)
        {
            return "";
        }
    
        if (firmExchange == null || firmExchange.length() == 0)
        {
            return PREFERRED_FIRM_PREFIX + firmAcronym + PREFERRED_FIRM_SUFFIX;
        }
            
        return PREFERRED_FIRM_PREFIX + firmExchange + PREFERRED_FIRM_EXCHANGE_SEPARATOR + firmAcronym + PREFERRED_FIRM_SUFFIX;
    }
}
