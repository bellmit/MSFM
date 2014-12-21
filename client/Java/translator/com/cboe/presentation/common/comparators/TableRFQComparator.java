package com.cboe.presentation.common.comparators;

import com.cboe.interfaces.presentation.rfq.RFQContainer;

import java.util.Comparator;

/**
  This class allows comparison of RFQ when given a RFQTableROwData instance.
*/
public class TableRFQComparator implements Comparator
{

    public TableRFQComparator()
    {
    }

    /**
      Implements Comparator
    */
    public int compare(Object pc1, Object pc2)
    {
        if(pc1 == pc2)
            return 0;
        else
            return compareLongs(((RFQContainer)pc1).getContainedRFQ().getExpireTime(), ((RFQContainer)pc2).getContainedRFQ().getExpireTime());
    }

    private int compareLongs(long val1, long val2)
    {
        int result = -1;
        if(val1 < val2)
            result = -1;
//        else if(val1 == val2)
//            result = 0;
        else
            result = 1;

        return result;
    }
}


