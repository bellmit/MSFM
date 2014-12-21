package com.cboe.domain.util;

import com.cboe.idl.cmiProduct.ClassStruct;

/**
 * Used to compare classes by class symbol.
 * @version 07/22/1999
 * @author Troy Wehrle
 */
public class ClassSymbolComparator implements java.util.Comparator
{
/**
 * ClassComparator constructor comment.
 */
public ClassSymbolComparator()
{
	super();
}
/**
 * Compares two classes based on class symbol.
 */
public int compare(Object arg1, Object arg2)
{
	if(arg1 instanceof ClassStruct && arg2 instanceof ClassStruct)
	{
		ClassStruct class1 = (ClassStruct)arg1;
		ClassStruct class2 = (ClassStruct)arg2;

		int compare;
		if((compare = class1.classSymbol.compareTo(class2.classSymbol)) != 0)
		{
			return compare;
		}
		else
		{
			if(class1.productType == class2.productType)
			{
				return 0;
			}
			else
			{
				if(class1.productType < class2.productType)
				{
					return -1;
				}
				else
				{
					return 1;
				}
			}
		}
	}
	else
	{
		return -1;
	}
}
}
