package com.cboe.presentation.common.comparators;

import java.util.Comparator;

import com.cboe.interfaces.presentation.user.ExchangeFirm;

/**
  This class allows comparison of ExchangeFirms
*/
public class ExchangeFirmComparator implements Comparator
{

    public ExchangeFirmComparator()
    {
        super();
    }

    /**
      Implements Comparator
    */
    public int compare(Object arg1, Object arg2)
    {
        if(arg1 == arg2)
        {
            return 0;
        }
        else if(arg1 instanceof ExchangeFirm && arg2 instanceof ExchangeFirm)
        {
            ExchangeFirm firm1 = (ExchangeFirm)arg1;
            ExchangeFirm firm2 = (ExchangeFirm)arg2;

            // ExchangeFirm.toString() is concatenated Exchange and Firm Strings
            return firm1.toString().compareTo(firm2.toString());
        }
        else
        {
            return -1;
        }
    }
}
