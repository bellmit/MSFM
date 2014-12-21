// Source file: com/cboe/util/SortAlgorithmFactory.java

package com.cboe.infrastructureUtility;

/**
   Creates a SortAlgorithm implementation
 */
public class SortAlgorithmFactory {
    
    /**
       Constructor.  Create a new SortAlgorithmFactory
       @roseuid 36ED737301CB
     */
    public SortAlgorithmFactory() {
    }
    
    /**
       Returns a SortAlgorithm implementation
       @roseuid 36ED6E3C00B3
     */
    public SortAlgorithm newSortAlgorithm() {
		return new QSortAlgorithm();
    }
}
