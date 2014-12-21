package com.cboe.interfaces.presentation.common.businessModels;

import java.util.Collection;
import java.util.Set;

public interface BusinessModelCollection
{
    public BusinessModel put(Object key, MutableBusinessModel obj);
    public BusinessModel getElement(Object key);
    public MutableBusinessModel getMutableElement(Object Key);
    public BusinessModel remove(Object key);
    public int size();
    public boolean isEmpty();
    public boolean containsKey(Object key);
    public boolean containsValue(BusinessModel value);
    public Collection values();
    public Set keySet();
    public void clear();
    // Methods for BusinessModelCollectionEvent support
    /**
     * Add the listener for BusinessModelCollectionEvent.
     * @param listener BusinessModelCollectionListener to receive a callback.
     */
    public void addListener(BusinessModelCollectionListener listener);
    /**
     * Removes the listener for BusinessModelCollectionEvents.
     * @param listener BusinessModelCollectionListener to remove from receiving callbacks.
     */
    public void removeListener(BusinessModelCollectionListener listener);
}
