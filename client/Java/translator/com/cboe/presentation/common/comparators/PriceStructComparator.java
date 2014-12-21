package com.cboe.presentation.common.comparators;

import java.util.*;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.domain.util.ValuedPrice;
import com.cboe.domain.util.ValuedPriceComparator;

/**
  Compares PriceStruct instances
 */
public class PriceStructComparator implements Comparator
{
    ValuedPriceComparator comp = new ValuedPriceComparator();
    /**
      Implements Comparator.
    */
    public int compare(Object priceStruct1, Object priceStruct2)
    {
        int result = -1;
        ValuedPrice vp1 = new ValuedPrice((PriceStruct)priceStruct1);
        ValuedPrice vp2 = new ValuedPrice((PriceStruct)priceStruct2);
        result = comp.compare(vp1, vp2);
        return result;
    }
}

