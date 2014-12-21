package com.cboe.domain.util;

import java.util.*;
import com.cboe.idl.cmiProduct.ClassStruct;
import com.cboe.interfaces.domain.*;
/**
 * Used to compare ValuePrice.
 * @version 08/04/1999
 * @author Connie Feng
 */
public class ValuedPriceComparator implements Comparator
{
/**
 * ValuePriceDescendingComparator constructor comment.
 */
public ValuedPriceComparator()
{
	super();
}
/**
 * Compares two ValuedPrice.
 */
public int compare(Object arg1, Object arg2)
{
	if(arg1 instanceof ValuedPrice && arg2 instanceof ValuedPrice)
	{
		ValuedPrice firstPrice = (ValuedPrice)arg1;
		ValuedPrice secondPrice = (ValuedPrice)arg2;

		if(firstPrice.lessThan(secondPrice) )
		{
		    return -1;
		}
		else if (firstPrice.equals(secondPrice) )
		{
            return 0;
		}
		else
		{
		    return 1;
		}
	}
	else
	{
		throw new ClassCastException("ValuePriceComparator error: cannot compare objects that are not ValuedPrice");
	}
}

}
