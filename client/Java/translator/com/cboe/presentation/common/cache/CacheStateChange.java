package com.cboe.presentation.common.cache;

/**
   An event used to notify listeners of state changes within the cache.
   @author Will McNabb
 */
public class CacheStateChange 
{
///////////////////////////////////////////////////////////////////////////////
// ATTRIBUTES
    
    private Object[] addedElements;
    private Object[] removedElements;
    private Object[] updatedElements;   
    
///////////////////////////////////////////////////////////////////////////////
// CONSTRUCTION
    
    /**
      CacheStateChange constructor.
      @param int stateChange
      @param List affectedElements
    */
    public CacheStateChange(Object[] addedElements, Object[] removedElements, Object[] updatedElements) 
    {
        this.addedElements = addedElements;
        this.removedElements = removedElements;
        this.updatedElements = updatedElements;     
    }
    
///////////////////////////////////////////////////////////////////////////////
// PUBLIC METHODS

    /**
       Gets the elements that were added to the cache.
       @return Object[]
     */
    public Object[] getAddedElements() 
    {
        return this.addedElements;
    }
    /**
       Gets the elements that were "DELETED" in the cache.
       @return Object[]
     */
    public Object[] getRemovedElements() 
    {
        return this.removedElements;
    }
    /**
       Gets the elements that were updated in the cache.
       @return Object[]
     */
    public Object[] getUpdatedElements() 
    {
        return this.updatedElements;
    }
    
}
