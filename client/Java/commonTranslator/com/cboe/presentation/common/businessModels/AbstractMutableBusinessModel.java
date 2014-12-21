package com.cboe.presentation.common.businessModels;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;

import com.cboe.interfaces.presentation.common.businessModels.MutableBusinessModel;

/**
 * Implements BusinessModel interface, providing a little behaviour
 */
public abstract class AbstractMutableBusinessModel<C> extends AbstractBusinessModel implements MutableBusinessModel
{
    private PropertyChangeSupport propertyEventManager;
        private boolean isModified = false;
    private Comparator comparator = null;

    /**
     * AbstructBusinessModel constructor comment.
     */
    public AbstractMutableBusinessModel()
    {
        super();
        setModified(false);
    }

    /**
     * AbstructBusinessModel constructor comment.
     * @param comparator to use for implementing Comparable interface.
     */
    public AbstractMutableBusinessModel(Comparator comparator)
    {
        this();
        this.comparator = comparator;
    }

    /**
     * Compare this object to another based on the passed comparator. If a comparator
     * was not supplied, then a strict equals will be used. If they are not equal,
     * -1 will be returned.
     */
    public int compareTo(C obj)
    {
        if(comparator != null)
        {
            return comparator.compare(this, obj);
        }
        else
        {
            if(this.equals(obj))
            {
                return 0;
            }
            else
            {
                return -1;
            }
        }
    }

    /**
     * Determines if the underlying data has been modified.
     * @return True if it has been modified, false otherwise.
     */
    public boolean isModified()
    {
        return isModified;
    }

    protected PropertyChangeSupport getPropertyEventManager()
    {
        if (propertyEventManager == null)
        {
            propertyEventManager = new PropertyChangeSupport(this);
        }
        return propertyEventManager;
    }

    /**
     * Add the listener for property changes.
     * @param listener PropertyChangeListener to receive a callback.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        getPropertyEventManager().addPropertyChangeListener(listener);
    }

    /**
     * Add the listener for property changes.
     * @param listener PropertyChangeListener to receive a callback.
     */
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
    {
        getPropertyEventManager().addPropertyChangeListener(propertyName, listener);
    }

    /**
     * Removes the listener for property changes.
     * @param listener PropertyChangeListener to remove from receiving callbacks.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        getPropertyEventManager().removePropertyChangeListener(listener);
    }

    /**
     * Removes the listener for property changes.
     * @param listener PropertyChangeListener to remove from receiving callbacks.
     */
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
    {
        getPropertyEventManager().removePropertyChangeListener(propertyName, listener);
    }

    /**
     * Changes the modify flag.
     */
    protected void setModified(boolean modified)
    {
        isModified = modified;
    }

    /**
     * Report a bound property update to any registered listeners. No event is
     * fired if old and new are equal and non-null.
     * @param propertyName The programmatic name of the property that was changed.
     * @param oldValue The old value of the property.
     * @param newValue The new value of the property.
     */
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue)
    {
        getPropertyEventManager().firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * Report an int bound property update to any registered listeners. No event is
     * fired if old and new are equal and non-null. This is merely a convenience wrapper
     * around the more general firePropertyChange method that takes Object values.
     * @param propertyName The programmatic name of the property that was changed.
     * @param oldValue The old value of the property.
     * @param newValue The new value of the property.
     */
    protected void firePropertyChange(String propertyName, int oldValue, int newValue)
    {
        getPropertyEventManager().firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * Report a boolean bound property update to any registered listeners. No event is
     * fired if old and new are equal and non-null. This is merely a convenience wrapper
     * around the more general firePropertyChange method that takes Object values.
     * @param propertyName The programmatic name of the property that was changed.
     * @param oldValue The old value of the property.
     * @param newValue The new value of the property.
     */
    protected void firePropertyChange(String propertyName, boolean oldValue, boolean newValue)
    {
        getPropertyEventManager().firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * Fire an existing PropertyChangeEvent to any registered listeners. No event is
     * fired if the given event's old and new values are equal and non-null.
     * @param event The PropertyChangeEvent object.
     */
    protected void firePropertyChange(PropertyChangeEvent event)
    {
        getPropertyEventManager().firePropertyChange(event);
    }

    /**
     * Check if there are any listeners for a specific property.
     * @param propertyName the property name.
     * @return true if there are one or more listeners for the given property
     */
    protected boolean hasListeners(String propertyName)
    {
        return getPropertyEventManager().hasListeners(propertyName);
    }

    public Comparator getComparator()
    {
        return comparator;
    }

    public void setComparator(Comparator comp)
    {
        this.comparator = comp;
    }
}
