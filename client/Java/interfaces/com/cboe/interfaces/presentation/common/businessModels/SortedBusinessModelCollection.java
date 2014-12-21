package com.cboe.interfaces.presentation.common.businessModels;

import java.util.Comparator;
import com.cboe.interfaces.presentation.common.businessModels.BusinessModelCollection;

/**
 * Defines the contract for a Sorted Business Model Collection
 */
public interface SortedBusinessModelCollection extends BusinessModelCollection
{
    public Comparator comparator();
    public Object firstKey();
    public Object lastKey();
    
}

