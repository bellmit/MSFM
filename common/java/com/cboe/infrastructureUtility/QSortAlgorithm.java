// Source file: com/cboe/util/QSortAlgorithm.java

package com.cboe.infrastructureUtility;

import java.util.Vector;


/**
   Permission to use, copy, modify, and distribute this software
   and its documentation for NON-COMMERCIAL or
   COMMERCIAL purposes and without fee is hereby granted.
   Please refer to the file http://java.sun.com/copy_trademarks.html
   for further important copyright and trademark information and
   to http://java.sun.com/licensing.html for further important
   licensing information for the Java (tm) Technology.
   SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
   SOFTWARE, EITHER EXPRESS OR IMPLIED,
   INCLUDING BUT NOT LIMITED TO THE IMPLIED
   WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
   PARTICULAR PURPOSE, OR NON-INFRINGEMENT.
   SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
   SUFFERED BY LICENSEE AS A RESULT OF USING,
   MODIFYING OR DISTRIBUTING THIS SOFTWARE OR
   ITS DERIVATIVES.
   THIS SOFTWARE IS NOT DESIGNED OR INTENDED
   FOR USE OR RESALE AS ON-LINE CONTROL
   EQUIPMENT IN HAZARDOUS ENVIRONMENTS
   REQUIRING FAIL-SAFE PERFORMANCE, SUCH AS IN
   THE OPERATION OF NUCLEAR FACILITIES, AIRCRAFT
   NAVIGATION OR COMMUNICATION SYSTEMS, AIR
   TRAFFIC CONTROL, DIRECT LIFE SUPPORT
   MACHINES, OR WEAPONS SYSTEMS, IN WHICH THE
   FAILURE OF THE SOFTWARE COULD LEAD DIRECTLY
   TO DEATH, PERSONAL INJURY, OR SEVERE
   PHYSICAL OR ENVIRONMENTAL DAMAGE ("HIGH RISK
   ACTIVITIES").  SUN SPECIFICALLY DISCLAIMS ANY
   EXPRESS OR IMPLIED WARRANTY OF FITNESS FOR
   HIGH RISK ACTIVITIES.
   @(#)QSortAlgorithm.java	  1.3   29 Feb 1996 James Gosling
   Copyright (c) 1994-1996 Sun Microsystems, Inc. All Rights Reserved.
   @author James Gosling
   @modified by Renzo Zanelli
   @version	 @(#)QSortAlgorithm.java 1.3, 29 Feb 1996
 */
public class QSortAlgorithm implements SortAlgorithm {
    private int sortOrder = SORT_ASCENDING_ORDER;
    
    /**
       Constructor.  Create a new QSortAlgorithm
       @roseuid 36CDC3E6039E
     */
    public QSortAlgorithm() {
	
    }
    
    /**
       The Quicksort algorithm implementation
       @param items      set of items to sort
       @param lowLimit   lower limit of sort
       @param highLimit  upper limit of sort
       @exception        SortAlgorithmException
       if the set to be sorted is not a sortable object collection
       @roseuid 36CDC3E6039F
     */
    private void QuickSort(Object[] items, int lowLimit, int highLimit) throws SortAlgorithmException {
		int low = lowLimit;
		int high = highLimit;
		Object midPoint;				// <- This needs to be different types

		if (highLimit > lowLimit)  {
			midPoint = items[(lowLimit + highLimit) / 2];

			if (sortOrder == SORT_ASCENDING_ORDER)  {
				while (low <= high)  {
					if (midPoint instanceof Integer)  {
						while ((low < highLimit) &&
								(((Integer)items[low]).intValue() <
									((Integer)midPoint).intValue()))
							low++;
						while ((high > lowLimit) &&
								(((Integer)items[high]).intValue() >
									((Integer)midPoint).intValue()))
							high--;
					}
					else if (midPoint instanceof Long)  {
						while ((low < highLimit) &&
								(((Long)items[low]).longValue() <
									((Long)midPoint).longValue()))
							low++;
						while ((high > lowLimit) &&
								(((Long)items[high]).longValue() >
									((Long)midPoint).longValue()))
							high--;
					}
					else if (midPoint instanceof Double)  {
						while ((low < highLimit) &&
								(((Double)items[low]).doubleValue() <
									((Double)midPoint).doubleValue()))
							low++;
						while ((high > lowLimit) &&
								(((Double)items[high]).doubleValue() >
									((Double)midPoint).doubleValue()))
							high--;
					}
					else if (midPoint instanceof Float)  {
						while ((low < highLimit) &&
								(((Float)items[low]).floatValue() <
									((Float)midPoint).floatValue()))
							low++;
						while ((high > lowLimit) &&
								(((Float)items[high]).floatValue() >
									((Float)midPoint).floatValue()))
							high--;
					}
					else if (midPoint instanceof Character)  {
						while ((low < highLimit) &&
								(((Character)items[low]).charValue() <
									((Character)midPoint).charValue()))
							low++;
						while ((high > lowLimit) &&
								(((Character)items[high]).charValue() >
									((Character)midPoint).charValue()))
							high--;
					}
					else if (midPoint instanceof String)  {
						while ((low < highLimit) &&
							(((String)items[low]).compareTo((String)midPoint) <
								0))
							low++;
						while ((high > lowLimit) &&
							(((String)items[high]).compareTo((String)midPoint) >
								0))
							high--;
					}
					else  {
						throw new SortAlgorithmException(
											"Non sortable object collection");
					}

					if (low <= high)  {
						swap(items, low, high);

						low++;
						high--;
					}
				}
			}
			else  {		// sortOrder == SORT_DESCENDING_ORDER
				while (low <= high)  {
					if (midPoint instanceof Integer)  {
						while ((low < highLimit) &&
								(((Integer)items[low]).intValue() >
									((Integer)midPoint).intValue()))
							low++;
						while ((high > lowLimit) &&
								(((Integer)items[high]).intValue() <
									((Integer)midPoint).intValue()))
							high--;
					}
					else if (midPoint instanceof Long)  {
						while ((low < highLimit) &&
								(((Long)items[low]).longValue() >
									((Long)midPoint).longValue()))
							low++;
						while ((high > lowLimit) &&
								(((Long)items[high]).longValue() <
									((Long)midPoint).longValue()))
							high--;
					}
					else if (midPoint instanceof Double)  {
						while ((low < highLimit) &&
								(((Double)items[low]).doubleValue() >
									((Double)midPoint).doubleValue()))
							low++;
						while ((high > lowLimit) &&
								(((Double)items[high]).doubleValue() <
									((Double)midPoint).doubleValue()))
							high--;
					}
					else if (midPoint instanceof Float)  {
						while ((low < highLimit) &&
								(((Float)items[low]).floatValue() >
									((Float)midPoint).floatValue()))
							low++;
						while ((high > lowLimit) &&
								(((Float)items[high]).floatValue() <
									((Float)midPoint).floatValue()))
							high--;
					}
					else if (midPoint instanceof Character)  {
						while ((low < highLimit) &&
								(((Character)items[low]).charValue() >
									((Character)midPoint).charValue()))
							low++;
						while ((high > lowLimit) &&
								(((Character)items[high]).charValue() <
									((Character)midPoint).charValue()))
							high--;
					}
					else if (midPoint instanceof String)  {
						while ((low < highLimit) &&
							(((String)items[low]).compareTo((String)midPoint) >
								0))
							low++;
						while ((high > lowLimit) &&
							(((String)items[high]).compareTo((String)midPoint) <
								0))
							high--;
					}
					else  {
						throw new SortAlgorithmException(
											"Non sortable object collection");
					}

					if (low <= high)  {
						swap(items, low, high);

						low++;
						high--;
					}
				}
			}

			if (lowLimit < high)
				QuickSort(items, lowLimit, high);

			if (low < highLimit)
				QuickSort(items, low, highLimit);
		}
	
    }
    
    /**
       Swap two items
       @param items    array containing the items to swap
       @param index_a  first item to swap
       @param index_b  second item to swap
       @roseuid 36CDC3E603A6
     */
    private void swap(Object[] items, int index_a, int index_b) {
		Object temp;
		temp = items[index_a]; 
		items[index_a] = items[index_b];
		items[index_b] = temp;
	
    }
    
    /**
       Sort an item set
       @param itemSet  set of items to sort
       @exception      SortAlgorithmException
       if the set to be sorted is not a sortable object
       collection or the sort order specified is not
       SORT_ASCENDING_ORDER or SORT_DESCENDING_ORDER
       @roseuid 36CDC3E603AA
     */
    public void sort(Object[] itemSet) throws SortAlgorithmException {
		sortOrder = SORT_ASCENDING_ORDER;

		QuickSort(itemSet, 0, itemSet.length - 1);
	
    }
    
    /**
       Sort an item set in specified direction
       @param itemSet  set of items to sort
       @param order    direction of sort
       @exception      SortAlgorithmException
       if the set to be sorted is not a sortable object
       collection or the sort order specified is not
       SORT_ASCENDING_ORDER or SORT_DESCENDING_ORDER
       @roseuid 36CDC3E603AC
     */
    public void sort(Object[] itemSet, int order) throws SortAlgorithmException {
		if ((order != SORT_ASCENDING_ORDER) &&
				(order != SORT_DESCENDING_ORDER))  {
			throw new SortAlgorithmException("Invalid sort order");
		}

		sortOrder = order;
		QuickSort(itemSet, 0, itemSet.length - 1);
	
    }
    
    /**
       Sort an item set
       @param itemSet  set of items to sort
       @exception      SortAlgorithmException
       if the set to be sorted is not a sortable object
       collection or the sort order specified is not
       SORT_ASCENDING_ORDER or SORT_DESCENDING_ORDER
       @roseuid 36CDC3E603AF
     */
    public void sort(Vector itemSet) throws SortAlgorithmException {
		sort(itemSet, SORT_ASCENDING_ORDER);
	
    }
    
    /**
       Sort an item set in specified direction
       @param itemSet  set of items to sort
       @param order    direction of sort
       @exception      SortAlgorithmException
       if the set to be sorted is not a sortable object
       collection or the sort order specified is not
       SORT_ASCENDING_ORDER or SORT_DESCENDING_ORDER
       @roseuid 36CDC3E603B1
     */
    public void sort(Vector itemSet, int order) throws SortAlgorithmException {
		if ((itemSet == null) || (itemSet.size() < 1))  {
			throw new SortAlgorithmException("Empty object collection");
		}
		if ((order != SORT_ASCENDING_ORDER) &&
				(order != SORT_DESCENDING_ORDER))  {
			throw new SortAlgorithmException("Invalid sort order");
		}

		sortOrder = order;

		Object items[] = new Object[itemSet.size()];
		itemSet.copyInto(items);

		QuickSort(items, 0, items.length - 1);

		itemSet.removeAllElements();
		for (int i = 0; i < items.length; i++)  {
			itemSet.addElement(items[i]);
		}
	
    }
}
