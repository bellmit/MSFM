// Source file: com/cboe/util/SortableVector.java

package com.cboe.infrastructureUtility;

import java.util.*;


/**
   This is vector whose components can be sorted by plugging in a SortAlgorithm
 */
public class SortableVector extends Vector implements Sortable {
    private SortAlgorithm sortAlgorithm;
    
    /**
       Constructor.  Create a new SortableVector
       @roseuid 36CDC3E603E6
     */
    public SortableVector() {
		super();
	
    }
    
    /**
       Constructor.  Create a new SortableVector of specified initial capacity
       @param initialCapacity  the initial capacity
       @roseuid 36CDC3E70001
     */
    public SortableVector(int initialCapacity) {
		super(initialCapacity);
	
    }
    
    /**
       Constructor.  Create a new SortableVector of specified initial capacity and with specified specified size increment capacity
       @param initialCapacity    the initial capacity
       @param capacityIncrement  the size increment capacity
       @roseuid 36CDC3E70003
     */
    public SortableVector(int initialCapacity, int capacityIncrement) {
		super(initialCapacity, capacityIncrement);
	
    }
    
    /**
       Returns the current sort algorithm
       @return  the current sort algorithm
       @roseuid 36CDC3E70006
     */
    public SortAlgorithm getSortAlgorithm() {
		return sortAlgorithm;
	
    }
    
    /**
       Sets the sort algorithm property
       @param sortAlgorithm  the sort algorithm property
       @roseuid 36CDC3E70007
     */
    public void setSortAlgorithm(SortAlgorithm sortAlgorithm) {
		this.sortAlgorithm = sortAlgorithm;
	
    }
    
    /**
       Sort the Sortable Vector
       @exception  Exception
       if the set to be sorted is not a sortable object
       collection or the sort order specified is not
       SORT_ASCENDING_ORDER or SORT_DESCENDING_ORDER
       @roseuid 36CDC3E70009
     */
    public final void sort() throws Exception {
		sortAlgorithm.sort(this);
	
    }
    
    /**
       Sort the Sortable Vector in the specified sort order
       @param sortOrder  the sort order
       @exception  Exception
       if the set to be sorted is not a sortable object
       collection or the sort order specified is not
       SORT_ASCENDING_ORDER or SORT_DESCENDING_ORDER
       @roseuid 36CDC3E7000A
     */
    public final void sort(int sortOrder) throws Exception {
		sortAlgorithm.sort(this, sortOrder);
	
    }
}
