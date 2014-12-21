package com.cboe.internalPresentation.marketMaker;

import java.beans.*;

import com.cboe.interfaces.presentation.marketMaker.MMClassAssignmentModel;
import com.cboe.idl.user.MarketMakerClassAssignmentStruct;
import com.cboe.idl.cmiConstants.SessionNameValues;

//
// -----------------------------------------------------------------------------------
// Source file: MMClassAssignmentModelImpl
//
// PACKAGE: com.cboe.presentation.marketMaker
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

public class MMClassAssignmentModelImpl implements MMClassAssignmentModel
{
    private PropertyChangeSupport propertyEventManager = new PropertyChangeSupport(this);
    private boolean isModified = false;
    private MarketMakerClassAssignmentStruct struct;

    public MMClassAssignmentModelImpl(int classKey, short assignmentType)
    {
        super();

        this.struct = new MarketMakerClassAssignmentStruct();
        this.struct.classKey = classKey;
        this.struct.assignmentType = assignmentType;
        this.struct.sessionName = SessionNameValues.ALL_SESSION_NAME;
        setModified(false);
    }

    public MMClassAssignmentModelImpl(MarketMakerClassAssignmentStruct struct)
    {
        super();

        if(struct == null)
        {
            throw new IllegalArgumentException("MMClassAssignmentModel(MarketMakerClassAssignmentStruct struct) may not be null.");
        }

        this.struct = struct;
        setModified(false);
    }

    public int hashCode()
    {
        return struct.classKey;
    }

    public String toString()
    {
        return String.valueOf(struct.classKey);
    }

    /**
     * Determines if this object is equal to passed object. Comparison will be done
     * on instance, type, then all other attributes of the containing struct.
     * @param obj to compare this with
     * @return True if all equal, false otherwise
     */
    public boolean equals(Object obj)
    {
        if(this == obj)
        {
            return true;
        }
        else if(obj == null)
        {
            return false;
        }
        else if(getClass() == obj.getClass())
        {
            MMClassAssignmentModelImpl castedObj = (MMClassAssignmentModelImpl)obj;

            if(getMarketMakerClassAssignmentStruct() == castedObj.getMarketMakerClassAssignmentStruct())
            {
                return true;
            }
            else
            {
                return (compareTo(castedObj) == 0) ? true : false;
            }
        }

        return false;
    }

    public int compareTo(Object obj)
    {
        if(this == obj)
        {
            return 0;
        }
        else
        {
            MMClassAssignmentModelImpl castedObj = (MMClassAssignmentModelImpl)obj;
            return (castedObj.getClassKey() == getClassKey()) ? 0 : -1;
        }
    }

    public MarketMakerClassAssignmentStruct getMarketMakerClassAssignmentStruct()
    {
        return struct;
    }

    public void setMarketMakerClassAssignmentStruct(MarketMakerClassAssignmentStruct newStruct)
    {
        MarketMakerClassAssignmentStruct oldStruct = getMarketMakerClassAssignmentStruct();

        if(oldStruct != newStruct)
        {
            this.struct = newStruct;

            setModified(false);
            propertyEventManager.firePropertyChange(STRUCT_CHANGE_EVENT, oldStruct, getMarketMakerClassAssignmentStruct());
        }
    }

    public int getClassKey()
    {
        return struct.classKey;
    }

    public short getAssignmentType()
    {
        return struct.assignmentType;
    }

    public String getSessionName()
    {
        return struct.sessionName;
    }

    /**
     * Determines if the ClassAssignment has been modified.
     * @return True if it has been modified, false otherwise.
     */
    public boolean isModified()
    {
        return isModified;
    }

    /**
     * Add the listener for property changes to the ClassAssignment attributes.
     * @param listener PropertyChangeListener to receive a callback when a ClassAssignment
     * property is changed.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        propertyEventManager.addPropertyChangeListener(listener);
    }

    /**
     * Removes the listener for property changes to the ClassAssignment attributes.
     * @param listener PropertyChangeListener to remove from receiving callbacks when a ClassAssignment
     * property is changed.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        propertyEventManager.removePropertyChangeListener(listener);
    }

    private void setModified(boolean b)
    {
        isModified = b;
    }

} // -- end of class MMClassAssignmentModelImpl
