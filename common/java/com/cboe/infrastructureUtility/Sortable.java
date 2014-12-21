// Source file: com/cboe/util/Sortable.java

package com.cboe.infrastructureUtility;

/**
   Defines the methods an object must implement to be sortable
 */
public interface Sortable {
    
    /**
       Returns the sort algorithm
       @return  the sort algorithm
       @roseuid 36CDDC0A01E6
     */
    public SortAlgorithm getSortAlgorithm();
    
    /**
       Assign the sorting algorithm
       @roseuid 36CDDC42010B
     */
    public void setSortAlgorithm(SortAlgorithm sortAlgorithm);
    
    /**
       Does the sorting
       @roseuid 36CDDC9000DB
     */
    public void sort(int sortOrder) throws Exception;
}
