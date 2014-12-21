//
// -----------------------------------------------------------------------------------
// Source file: OperatorFormatter.java
//
// PACKAGE: com.cboe.presentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.formatters;

import com.cboe.idl.alarmConstants.Operators;

import com.cboe.interfaces.instrumentation.common.formatters.OperatorFormatStrategy;

public class OperatorFormatter extends Formatter implements OperatorFormatStrategy
{
    public static final String CONTAINS = Operators.CONTAINS;
    public static final String EQUAL = Operators.EQUAL;
    public static final String GREATER = Operators.GREATER;
    public static final String GREATER_EQUAL = Operators.GREATER_EQUAL;
    public static final String LESS = Operators.LESS;
    public static final String LESS_EQUAL = Operators.LESS_EQUAL;
    public static final String NOT_EQUAL = Operators.NOT_EQUAL;
    public static final String NOT_STARTS_WITH = Operators.NOT_STARTS_WITH;
    public static final String STARTS_WITH = Operators.STARTS_WITH;
    public static final String REGEXP = Operators.REGEXP;

    public static final String CONTAINS_FULL_STRING = "Contains";
    public static final String EQUAL_FULL_STRING = "Equals";
    public static final String GREATER_FULL_STRING = "Greater Than";
    public static final String GREATER_EQUAL_FULL_STRING = "Greater Than/Equal";
    public static final String LESS_FULL_STRING = "Less Than";
    public static final String LESS_EQUAL_FULL_STRING = "Less Than/Equal";
    public static final String NOT_EQUAL_FULL_STRING = "Not Equal";
    public static final String NOT_STARTS_WITH_FULL_STRING = "Not Starts With";
    public static final String STARTS_WITH_FULL_STRING = "Starts With";
    public static final String REGEX_FULL_STRING = "Regular Expression";

    public static final String CONTAINS_LOGICAL_STRING = "*V*";
    public static final String EQUAL_LOGICAL_STRING = "=";
    public static final String GREATER_LOGICAL_STRING = ">";
    public static final String GREATER_EQUAL_LOGICAL_STRING = ">=";
    public static final String LESS_LOGICAL_STRING = "<";
    public static final String LESS_EQUAL_LOGICAL_STRING = "<=";
    public static final String NOT_EQUAL_LOGICAL_STRING = "!=";
    public static final String NOT_STARTS_WITH_LOGICAL_STRING = "!V*";
    public static final String STARTS_WITH_LOGICAL_STRING = "V*";
    public static final String REGEX_LOGICAL_STRING = "*?*";

    public static final String INVALID_STYLE_STRING = "Invalid Style";
    public static final String INVALID_OPERATOR_STRING = "Invalid Operator";

    public OperatorFormatter()
    {
        addStyle(FULL_INFO_NAME, FULL_INFO_DESCRIPTION);
        addStyle(BRIEF_LETTER_INFO_NAME, BRIEF_LETTER_INFO_DESCRIPTION);
        addStyle(BRIEF_LOGICAL_INFO_NAME, BRIEF_LOGICAL_INFO_DESCRIPTION);

        setDefaultStyle(FULL_INFO_NAME);
    }

    public String format(String operator, String style)
    {
        String returnValue;
        if (style.equals(FULL_INFO_NAME))
        {
            returnValue = getFullOperatorString(operator);
        }
        else if (style.equals(BRIEF_LETTER_INFO_NAME))
        {
            returnValue = getOperatorString(operator);
        }
        else if(style.equals(BRIEF_LOGICAL_INFO_NAME))
        {
            returnValue = getLogicalOperatorString(operator);
        }
        else
        {
            returnValue = INVALID_STYLE_STRING + ": " + operator;
        }
        return returnValue;
    }

    public String format(String operator)
    {
        return format(operator, getDefaultStyle());
    }

    private String getFullOperatorString(String operator)
    {
        String retVal;

        if(operator.equals(CONTAINS))
        {
            retVal = CONTAINS_FULL_STRING;
        }
        else if(operator.equals(EQUAL))
        {
            retVal = EQUAL_FULL_STRING;
        }
        else if(operator.equals(GREATER))
        {
            retVal = GREATER_FULL_STRING;
        }
        else if(operator.equals(GREATER_EQUAL))
        {
            retVal = GREATER_EQUAL_FULL_STRING;
        }
        else if(operator.equals(LESS))
        {
            retVal = LESS_FULL_STRING;
        }
        else if(operator.equals(LESS_EQUAL))
        {
            retVal = LESS_EQUAL_FULL_STRING;
        }
        else if(operator.equals(NOT_EQUAL))
        {
            retVal = NOT_EQUAL_FULL_STRING;
        }
        else if(operator.equals(NOT_STARTS_WITH))
        {
            retVal = NOT_STARTS_WITH_FULL_STRING;
        }
        else if(operator.equals(STARTS_WITH))
        {
            retVal = STARTS_WITH_FULL_STRING;
        }
        else if(operator.equals(REGEXP))
        {
            retVal = REGEX_FULL_STRING;
        }
        else
        {
            retVal = INVALID_OPERATOR_STRING + ": " + operator;
        }

        return retVal;
    }

    private String getLogicalOperatorString(String operator)
    {
        String retVal;

        if(operator.equals(CONTAINS))
        {
            retVal = CONTAINS_LOGICAL_STRING;
        }
        else if(operator.equals(EQUAL))
        {
            retVal = EQUAL_LOGICAL_STRING;
        }
        else if(operator.equals(GREATER))
        {
            retVal = GREATER_LOGICAL_STRING;
        }
        else if(operator.equals(GREATER_EQUAL))
        {
            retVal = GREATER_EQUAL_LOGICAL_STRING;
        }
        else if(operator.equals(LESS))
        {
            retVal = LESS_LOGICAL_STRING;
        }
        else if(operator.equals(LESS_EQUAL))
        {
            retVal = LESS_EQUAL_LOGICAL_STRING;
        }
        else if(operator.equals(NOT_EQUAL))
        {
            retVal = NOT_EQUAL_LOGICAL_STRING;
        }
        else if(operator.equals(NOT_STARTS_WITH))
        {
            retVal = NOT_STARTS_WITH_LOGICAL_STRING;
        }
        else if(operator.equals(STARTS_WITH))
        {
            retVal = STARTS_WITH_LOGICAL_STRING;
        }
        else if(operator.equals(REGEXP))
        {
            retVal = REGEX_LOGICAL_STRING;
        }
        else
        {
            retVal = INVALID_OPERATOR_STRING + ": " + operator;
        }

        return retVal;
    }

    private String getOperatorString(String operator)
    {
        String retVal;

        if(operator.equals(CONTAINS) || operator.equals(EQUAL) || operator.equals(GREATER) ||
           operator.equals(GREATER_EQUAL) || operator.equals(LESS) || operator.equals(LESS_EQUAL) ||
           operator.equals(NOT_EQUAL) || operator.equals(NOT_STARTS_WITH) || operator.equals(STARTS_WITH)|| operator.equals(REGEXP))
        {
            retVal = operator;
        }
        else
        {
            retVal = INVALID_OPERATOR_STRING + ": " + operator;
        }

        return retVal;
    }
}
