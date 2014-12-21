package com.cboe.presentation.common.comparators;

import java.util.*;

import com.cboe.idl.cmiConstants.OptionTypes;

/**
  Compares OptionType instances

*/
public class OptionTypeComparator implements Comparator
{
    private final String Category = this.getClass().getName();
    /**
      Implements Comparator.
    */
    public int compare(Object optionType1, Object optionType2)
    {
        int result = -1;
        char char1 = ((Character)optionType1).charValue();
        char char2 = ((Character)optionType2).charValue();

        result = compareOptionTypes(char1, char2);
        return result;
    }

    protected int compareOptionTypes(char type1, char type2)
    {
    int result = -1;

        if ( type1 == type2 )
        {
            result = 0;
        }
        else if (type1 == OptionTypes.PUT)
        {
            result = 1;
        }
        return result;

    }
}

