//
// -----------------------------------------------------------------------------------
// Source file: DPMModel.java
//
// PACKAGE: com.cboe.interfaces.presentation.dpm;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.dpm;

import java.beans.*;
import java.util.*;
import org.omg.CORBA.UserException;

import com.cboe.idl.cmiUser.DpmStruct;

import com.cboe.interfaces.presentation.product.ProductClass;

/**
 * Defines a contract that a DPMModel that represents a DPMStruct should provide.
 */
public interface DPMModel extends Comparable
{
    public static final String STRUCT_CHANGE_EVENT = new String("STRUCT_CHANGE_EVENT");

    /**
     * Returns a hash code that represents this DPM for hashing.
     * @return hash code that should represents this DPM uniquely in some way.
     */
    public int hashCode();

    /**
     * Returns a String representation of this DPM.
     * @return DPM information as a String
     */
    public String toString();

    /**
      * Used as a tag interface method to remind implementors to implement this.
     */
    public boolean equals(Object obj);

    /**
     * Gets the DPM user id
     * @return user id
     */
    public String getUserId();

    /**
     * Gets the DpmStruct that this model represents.
     * @return com.cboe.idl.cmiUser.DpmStruct
     */
    public DpmStruct getDpmStruct();

    /**
     * Sets the DpmStruct that this model represents.
     * @param newStruct New struct for this model to represent.
     */
    public void setDpmStruct(DpmStruct newStruct);

    /**
     * Gets the assigned class keys for the DPM.
     * @return int[] a sequence of user assigned class keys
     */
    public int[] getClassKeys();

    /**
     * Gets the assigned ProductClass'es.
     * @return ProductClass sequence
     */
    public ProductClass[] getProductClass();

    /**
     * Determines if the passed classKey is assigned to this DPM.
     * @param classKey to find
     * @return Index into collection of found class. Will be >=0 if found, <0 if not found.
     */
    public int containsClassKey(int classKey);

    /**
     * Determines if the DPM has been modified.
     * @return True if it has been modified, false otherwise.
     */
    public boolean isModified();

    /**
     * Add the listener for property changes to the DPM attributes.
     * @param listener PropertyChangeListener to receive a callback when a DPM
     * property is changed.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * Removes the listener for property changes to the DPM attributes.
     * @param listener PropertyChangeListener to remove from receiving callbacks when a DPM
     * property is changed.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener);
}