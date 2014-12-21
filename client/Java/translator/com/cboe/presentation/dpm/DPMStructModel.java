//
// -----------------------------------------------------------------------------------
// Source file: DPMStructModel.java
//
// PACKAGE: com.cboe.presentation.commonBusiness;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.dpm;

import java.beans.*;
import java.util.*;
import org.omg.CORBA.UserException;

import com.cboe.idl.cmiUser.DpmStruct;

import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.product.ProductHelper;

import com.cboe.interfaces.presentation.product.ProductClass;
import com.cboe.interfaces.presentation.dpm.DPMModel;

/**
 * Encapsulates a DpmStruct, providing little behaviour
 */
public class DPMStructModel implements DPMModel
{
    private PropertyChangeSupport propertyEventManager = new PropertyChangeSupport(this);
    private boolean isModified = false;
    private DpmStruct struct = null;

    /**
     * DPMModel constructor comment.
     */
    public DPMStructModel(DpmStruct struct)
    {
        super();

        if(struct == null)
        {
            throw new IllegalArgumentException("DPMModel(DpmStruct struct) may not be null.");
        }
        this.struct = struct;

        if(getDpmStruct().dpmAssignedClasses == null)
        {
            getDpmStruct().dpmAssignedClasses = new int[0];
        }

        setModified(false);
    }

    /**
     * Returns the dpmUserId of the DpmStruct as a hashCode.
     * @return dpmUserId String as a hashCode
     */
    public int hashCode()
    {
        return getUserId().hashCode();
    }

    /**
     * Returns the dpmUserId of the DpmStruct
     * @return String
     */
    public String toString()
    {
        return getUserId();
    }

    /**
     * Determines if this object is equal to passed object. Comparison will be done
     * on instance, type, then all other attributes of the containing struct.
     * @param obj to compare this with
     * @return True if all equal, false otherwise
     */
    public boolean equals(Object obj)
    {
        boolean isEqual = super.equals(obj);

        if(!isEqual)
        {
            if(obj != null && obj instanceof DPMModel)
            {
                DPMModel castedObj = (DPMModel)obj;
                isEqual = getUserId().equals(castedObj.getUserId());
            }
        }

        return isEqual;
    }

    /**
     * Compare this DPMStructModel to another based on name.
     */
    public int compareTo(Object obj)
    {
        if(this == obj)
        {
            return 0;
        }
        else
        {
            DPMModel otherDPM = (DPMModel)obj;

            return getUserId().compareTo(otherDPM.getUserId());
        }
    }

    /**
     * Gets the DPM user id from the DpmStruct
     * @return user id
     */
    public String getUserId()
    {
        return getDpmStruct().dpmUserId;
    }

    /**
     * Gets the DpmStruct that this model represents.
     * @return com.cboe.idl.cmiUser.DpmStruct
     */
    public DpmStruct getDpmStruct()
    {
        return struct;
    }

    /**
     * Sets the DpmStruct that this model represents.
     * @param newStruct New struct for this model to represent.
     */
    public void setDpmStruct(DpmStruct newStruct)
    {
        DpmStruct oldStruct = getDpmStruct();

        if(oldStruct != newStruct)
        {
            this.struct = newStruct;

            if(getDpmStruct().dpmAssignedClasses == null)
            {
                getDpmStruct().dpmAssignedClasses = new int[0];
            }

            setModified(false);
            propertyEventManager.firePropertyChange(STRUCT_CHANGE_EVENT, oldStruct, getDpmStruct());
        }
    }

    /**
     * Gets the assigned class keys for the DPM.
     * @return int[] a sequence of user assigned class keys
     */
    public int[] getClassKeys()
    {
        return getDpmStruct().dpmAssignedClasses;
    }

    /**
     * Gets the assigned ProductClass'es for the struct.
     * @return ProductClass sequence
     */
    public ProductClass[] getProductClass()
    {
        int[] classKeys = getClassKeys();
        List<ProductClass> productClasses = new ArrayList<ProductClass>(classKeys.length);

        for(int classKey : classKeys)
        {
            try
            {
                ProductClass pc = ProductHelper.getProductClassCheckInvalid(classKey);
                productClasses.add(pc);
            }
            catch(UserException e)
            {
                DefaultExceptionHandlerHome.find().process(e);
            }
        }

        return productClasses.toArray(new ProductClass[0]);
    }

    /**
     * Determines if the passed classKey is assigned to this DPM. As a side effect of calling this method
     * the class keys in the struct will be sorted.
     * @param classKey to find
     * @return Index into collection of found class. Will be >=0 if found, <0 if not found.
     */
    public int containsClassKey(int classKey)
    {
        Arrays.sort(getClassKeys());
        return Arrays.binarySearch(getClassKeys(), classKey);
    }

    /**
     * Determines if the underlying struct has been modified.
     * @return True if it has been modified, false otherwise.
     */
    public boolean isModified()
    {
        return isModified;
    }

    /**
     * Add the listener for property changes to the User attributes.
     * @param listener PropertyChangeListener to receive a callback when a User
     * property is changed.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        propertyEventManager.addPropertyChangeListener(listener);
    }

    /**
     * Removes the listener for property changes to the User attributes.
     * @param listener PropertyChangeListener to remove from receiving callbacks when a User
     * property is changed.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        propertyEventManager.removePropertyChangeListener(listener);
    }

    /**
     */
    private void setModified(boolean modified)
    {
        this.isModified = modified;
    }
}
