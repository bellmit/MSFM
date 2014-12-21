//
// -----------------------------------------------------------------------------------
// Source file: MMClassAssignmentModel.java
//
// PACKAGE: com.cboe.interfaces.presentation.marketMaker;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.marketMaker;

import java.beans.PropertyChangeListener;

import com.cboe.idl.user.MarketMakerClassAssignmentStruct;


public interface MMClassAssignmentModel extends Comparable
{
    public static final String STRUCT_CHANGE_EVENT = new String("STRUCT_CHANGE_EVENT");

    /**
     *
     * @return
     */
    public MarketMakerClassAssignmentStruct getMarketMakerClassAssignmentStruct();

    /**
     *
     * @param newStruct
     */
    public void setMarketMakerClassAssignmentStruct(MarketMakerClassAssignmentStruct newStruct);

    /**
     *
     * @return
     */
    public int getClassKey();

    /**
     *
     * @return
     */
    public short getAssignmentType();

    /**
     *
     * @return
     */
    public String getSessionName();

    /**
     * Returns a hash code that represents this ClassAssignment for hashing.
     * @return hash code that should represents this ClassAssignment uniquely in some way.
     */
    public int hashCode();

    /**
     * Returns a String representation of this ClassAssignment.
     * @return ClassAssignment information as a String
     */
    public String toString();

    /**
      * Used as a tag interface method to remind implementors to implement this.
     */
    public boolean equals(Object obj);

    /**
     * Determines if the ClassAssignment has been modified.
     * @return True if it has been modified, false otherwise.
     */
    public boolean isModified();

    /**
     * Add the listener for property changes to the ClassAssignment attributes.
     * @param listener PropertyChangeListener to receive a callback when a ClassAssignment
     * property is changed.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * Removes the listener for property changes to the ClassAssignment attributes.
     * @param listener PropertyChangeListener to remove from receiving callbacks when a ClassAssignment
     * property is changed.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener);

} // -- end of interface MMClassAssignmentModel
