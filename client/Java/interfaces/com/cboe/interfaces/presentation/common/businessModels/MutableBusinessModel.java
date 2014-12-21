package com.cboe.interfaces.presentation.common.businessModels;

import java.beans.*;
import org.omg.CORBA.UserException;

/**
 * Describes the contract that a SBT GUI Business object will support.
 */
public interface MutableBusinessModel extends BusinessModel
{
    /**
     * Property name to represent complete change of encapsulated data.
     * This use to be STRUCT_CHANGE_EVENT.
     */
    static final String DATA_CHANGE_EVENT = new String("DATA_CHANGE_EVENT");
    static final String SAVED_EVENT = new String("SAVED_EVENT");
    static final String RELOADED_EVENT = new String("RELOADED_EVENT");
    static final String DELETED_EVENT = new String("DELETED_EVENT");

    /**
     * Supports adding and removing of a listener for event changes to business object.
     * @param listener to add or remove.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener);
    public void removePropertyChangeListener(PropertyChangeListener listener);

    /**
     * Supports adding and removing of a listener for event changes to business object.
     * Listener will only receives events for <code>propertyName</code>
     * @param propertyName to only receive events for.
     * @param listener to add or remove.
     */
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);

    /**
     * Determines if the underlying struct has been modified.
     * @return True if it has been modified, false otherwise.
     */
    public boolean isModified();

}