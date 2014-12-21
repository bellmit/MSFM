//
// -----------------------------------------------------------------------------------
// Source file: CBOEIdImpl.java
//
// PACKAGE: com.cboe.presentation.util;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.util;

import com.cboe.idl.cmiUtil.CboeIdStruct;

import com.cboe.interfaces.presentation.util.CBOEId;

import com.cboe.domain.util.CboeId;

/*
 * Implements the contract for a wrapper for the CboeIdStruct
 */
public class CBOEIdImpl implements CBOEId
{
    protected CboeIdStruct struct;

    /**
     * Default constructor
     * @param struct to represent
     */
    public CBOEIdImpl(CboeIdStruct struct)
    {
        this.struct = struct;
    }

    /**
     * clones this object
     * @return Object if of the CBOEId interface.
     */
    public Object clone()
    {
        CboeIdStruct clonedStruct = CboeId.clone(getStruct());
        return new CBOEIdImpl(clonedStruct);
    }

    /**
     * Determines equality between this and another CBOEId
     * @param otherObject to compare this with
     */
    public boolean equals(Object otherObject)
    {
        if (otherObject == this)
        {
            return true;
        }
        else
        {
            if (otherObject instanceof CBOEId)
            {
                CBOEId castedObject = (CBOEId) otherObject;
                if ((castedObject.getHighId() == this.getHighId()) &&
                    (castedObject.getLowId() == this.getLowId()))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public int compareTo(Object otherObject)
    {
        int result = 0;

        if (!this.equals(otherObject))
        {
            CBOEIdImpl otherId = (CBOEIdImpl) otherObject;
            result = this.getHighId() - otherId.getHighId();

            if (result == 0)
            {
                result = this.getLowId() - otherId.getLowId();
            }
        }

        return result;
    }

    /**
     * Produces a unique hash code by OR'ing the high and low together.
     */
    public int hashCode()
    {
        return (getHighId() | getLowId());
    }

    /**
     * Produces a string representation of this object using id's from struct
     */
    public String toString()
    {
        return CboeId.toString(getStruct());
    }

    /**
     * Gets the representing struct
     */
    public CboeIdStruct getStruct()
    {
        return this.struct;
    }

    /**
     * Gets the high id
     */
    public int getHighId()
    {
        return struct.highCboeId;
    }

    /**
     * Gets the low id
     */
    public int getLowId()
    {
        return struct.lowCboeId;
    }
}