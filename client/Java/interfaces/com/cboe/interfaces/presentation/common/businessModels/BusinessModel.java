//
// -----------------------------------------------------------------------------------
// Source file: BusinessModel.java
//
// PACKAGE: com.cboe.interfaces.presentation.common.businessModels;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.common.businessModels;

/**
 * Describes the contract that a SBT GUI Business object will support.
 */
public interface BusinessModel extends Cloneable
{
    /**
     * Returns the key Object to be used as a key in collection.
     * @return Object
     */
    public Object getKey();

    /**
     * Returns the hashCode of the object. To be used as a reminder to return a meaningful
     * hashCode for efficient Map usage.
     * @return hashCode
     */
    public int hashCode();

    /**
     * Determines if this object is equal to passed object. To be used as a reminder to
     * perform a meaninful equals comparison on appropriate struct contents
     * @param obj to compare this with
     * @return True if all equal, false otherwise
     */
    public boolean equals(Object obj);

    /**
     * Implements Cloneable
     */
    public Object clone() throws CloneNotSupportedException;
}