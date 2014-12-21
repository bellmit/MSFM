package com.cboe.presentation.common.businessModels;

import java.beans.*;
import java.util.SortedMap;
import java.util.Map;
import java.util.Set;
import java.util.Collection;
import java.util.Comparator;

import com.cboe.interfaces.presentation.common.businessModels.SortedBusinessModelCollection;
import com.cboe.interfaces.presentation.common.businessModels.BusinessModel;
import com.cboe.interfaces.presentation.common.businessModels.BusinessModelCollectionListener;
import com.cboe.interfaces.presentation.common.businessModels.BusinessModelCollectionEvent;
import com.cboe.interfaces.presentation.common.businessModels.MutableBusinessModel;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;

/**
 * Implements BusinessModel interface, providing a little behaviour
 */
public abstract class AbstractSortedBusinessModelCollection implements SortedBusinessModelCollection, PropertyChangeListener
{
    private BusinessModelCollectionListener collectionListener = null;
//  private PropertyChangeSupport propertyEventManager = new PropertyChangeSupport(this);

    abstract public SortedMap getSortedMap();

    /**
     * Following methods implement SortedMap interface
     */
    public Comparator comparator()
    {
        return getSortedMap().comparator();
    }
    public SortedMap subMap(Object fromKey, Object toKey)
    {
        return getSortedMap().subMap(fromKey, toKey);
    }
    public SortedMap headMap(Object toKey)
    {
        return getSortedMap().headMap(toKey);
    }
    public SortedMap tailMap(Object fromKey)
    {
        return getSortedMap().tailMap(fromKey);
    }
    public Object firstKey()
    {
        return getSortedMap().firstKey();
    }
    public Object lastKey()
    {
        return getSortedMap().lastKey();
    }
    public int size()
    {
        return getSortedMap().size();
    }
    public boolean isEmpty()
    {
        return getSortedMap().isEmpty();
    }
    public boolean containsKey(Object key)
    {
        return getSortedMap().containsKey(key);
    }
//    protected boolean containsValue(Object value)
//    {
//        return getSortedMap().containsValue(value);
//    }
    protected Object get(Object key)
    {
        return getSortedMap().get(key);
    }
    protected Object put(Object key, Object value)
    {
        if (value instanceof MutableBusinessModel)
        {
            return getSortedMap().put(key, value);
        }
        else
        {
            throw new IllegalArgumentException("Value must be an instance of MutableBusinessModel");
        }
    }
//    protected Object remove(Object key)
//    {
//        return getSortedMap().remove(key);
//    }
    public void putAll(Map t)
    {
        getSortedMap().putAll(t);
    }
    public void clear()
    {
        getSortedMap().clear();
    }
    public Set keySet()
    {
        return getSortedMap().keySet();
    }
    public Collection values()
    {
        return getSortedMap().values();
    }
    public Set entrySet()
    {
        return getSortedMap().entrySet();
    }
//    public boolean equals(Object o)
//    {
//        return getSortedMap().equals(o);
//    }

    /**
     * Implementation of the BusinessModelCollection interface
     */
    public BusinessModel getElement(Object key)
    {
        return (BusinessModel)get(key);
    }

    public MutableBusinessModel getMutableElement(Object key)
    {
        MutableBusinessModel clonedModel = null;
        try
        {
            MutableBusinessModel model = (MutableBusinessModel)get(key);
            if ( model != null )
            {
                clonedModel = (MutableBusinessModel)model.clone();
                clonedModel.addPropertyChangeListener(this);
            }
        }
        catch(CloneNotSupportedException e )
        {
            com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome.find().process(e, "Programmer Error!!!!");
        }
        return clonedModel;
    }

    public BusinessModel put(Object key, MutableBusinessModel value)
    {
        return put(key, value, true);
    }

    private BusinessModel put(Object key, MutableBusinessModel value, boolean fireEvent)
    {
        BusinessModel oldValue = (BusinessModel)getSortedMap().put(key, value);
        if (fireEvent)
        {
            if ( oldValue == null )
            {
                fireCollectionElementAdded(value);
            }
            else
            {
                fireCollectionElementUpdated(value);
            }
        }
        return oldValue;
    }
    public BusinessModel remove(Object key)
    {
        return (BusinessModel)getSortedMap().remove(key);
    }
    public boolean containsValue(BusinessModel value)
    {
        return getSortedMap().containsValue(value);
    }

    /**
     * Add the listener for BusinessModelCollectionEvents.
     * @param listener BusinessModelCollectionListener to receive a callback.
     */
    public void addListener(BusinessModelCollectionListener listener)
    {
        collectionListener = BusinessModelCollectionListenerEventMulticaster.add(collectionListener, listener);
    }

    /**
     * Removes the listener for BusinessModelCollectionEvents.
     * @param listener BusinessModelCollectionListener to remove from receiving callbacks.
     */
    public void removeListener(BusinessModelCollectionListener listener)
    {
        collectionListener = BusinessModelCollectionListenerEventMulticaster.remove(collectionListener, listener);
    }

    /**
     * Fires BusinessModelCollectionEvent indicating that new element was added to this collection.
     * @param  element - BusinessModel that was added to this collection
     */
    protected void fireCollectionElementAdded(BusinessModel element)
    {
        if (collectionListener != null)
        {
            BusinessModelCollectionEvent event = BusinessModelCollectionEventFactory.create(this, element);
            collectionListener.elementAdded(event);
        }
    }

    /**
     * Fires BusinessModelCollectionEvent indicating that element was updated.
     * @param element - BusinessModel element that was updated
     */
    protected void fireCollectionElementUpdated(BusinessModel element)
    {
        if (collectionListener != null)
        {
            BusinessModelCollectionEvent event = BusinessModelCollectionEventFactory.create(this, element);
            collectionListener.elementUpdated(event);
        }
    }

    /**
     * Fires BusinessModelCollectionEvent indicating that element was removed from this collection.
     * @param element - BusinessModel element that was reoved from this collection
     */
    protected void fireCollectionElementRemoved(BusinessModel element)
    {
        if (collectionListener != null)
        {
            BusinessModelCollectionEvent event = BusinessModelCollectionEventFactory.create(this, element);
            collectionListener.elementRemoved(event);
        }
    }

    public void propertyChange(PropertyChangeEvent evt)
    {
        String property = evt.getPropertyName();
        MutableBusinessModel model = (MutableBusinessModel)evt.getSource();

        if ( property.equals(model.SAVED_EVENT) )
        {
            put(model.getKey(), model);
            model.removePropertyChangeListener(this);
        }
        else if (property.equals(model.RELOADED_EVENT))
        {
            put(model.getKey(), model);
            model.removePropertyChangeListener(this);
        }
        else if (property.equals(model.DELETED_EVENT))
        {
            removeElement(model);
            model.removePropertyChangeListener(this);
        }
    }

    protected void removeElement(MutableBusinessModel model)
    {
        MutableBusinessModel oldValue = (MutableBusinessModel)remove(model.getKey());
        if ( oldValue != null )
        {
            fireCollectionElementRemoved(oldValue);
        }
    }

}
