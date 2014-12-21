//
// -----------------------------------------------------------------------------------
// Source file: FirmStructModel.java
//
// PACKAGE: com.cboe.internalPresentation.firm;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.firm;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;

import com.cboe.idl.firm.FirmStruct;

import com.cboe.interfaces.internalPresentation.firm.FirmModel;

import com.cboe.domain.util.DateWrapper;

/**
 * Encapsulates a FirmStruct
 */
public class FirmStructModel implements FirmModel, Comparable<FirmModel>
{
    private FirmStruct firm;
    private PropertyChangeSupport propertyEventManager = new PropertyChangeSupport(this);
    private DateWrapper dateWrapper;
    private boolean isModified;

    public FirmStructModel(FirmStruct firm)
    {
        if(firm == null)
        {
            throw new IllegalArgumentException("FirmModel(FirmStruct firm) may not be null.");
        }
        this.firm = firm;

        dateWrapper = new DateWrapper();

        setModified(false);
    }

    /**
     * Returns the firmKey as a hashCode.
     * @return firmKey
     */
    public int hashCode()
    {
        return getFirmKey();
    }

    public String toString()
    {
        StringBuffer string = new StringBuffer(100);
        string.append(getAcronym()).append(':').append(getFirmNumber());
        string.append(" (").append(getFullName()).append(')');
        string.append(" (").append(isActive() ? "Active" : "InActive").append(')');
        return string.toString();
    }

    /**
     * Determines if this object is equal to passed object. Comparison will be done
     * on instance, type, firmKey.
     * @param obj to compare this with
     * @return True if all equal, false otherwise
     */
    public boolean equals(Object obj)
    {
        boolean isEqual;

        if(this == obj)
        {
            isEqual = true;
        }
        else if(obj == null)
        {
            isEqual = false;
        }
        else if(obj instanceof FirmModel)
        {
            FirmModel castedObj = (FirmModel)obj;
            isEqual = getFirmKey() == castedObj.getFirmKey();
        }
        else
        {
            isEqual = false;
        }

        return isEqual;
    }

    /**
     * Gets the firm acronym from the FirmStruct
     * @return firm acronym
     */
    public String getAcronym()
    {
        return getFirmStruct().firmAcronym;
    }

    /**
     * Sets the firm acronym for the FirmStruct
     * @param acronym new firm acronym
     */
    public void setAcronym(String acronym)
    {
        if(acronym == null)
        {
            throw new IllegalArgumentException("Acronym must not be null.");
        }
        else
        {
            String oldValue = getAcronym();

            if(!acronym.equals(oldValue))
            {
                getFirmStruct().firmAcronym = acronym;
                setModified(true);
                propertyEventManager.firePropertyChange(ACRONYM_CHANGE_EVENT, oldValue, acronym);
            }
        }
    }

    /**
     * Gets the firm key from the FirmStruct
     * @return firm key
     */
    public int getFirmKey()
    {
        return getFirmStruct().firmKey;
    }

    /**
     * Gets the firm number from the FirmStruct
     * @return firm number
     */
    public String getFirmNumber()
    {
        return getFirmStruct().firmNumber.firmNumber;
    }

    /**
     * Gets the firm exchange from the FirmStruct
     * @return firm exchange
     */
    public String getFirmExchange()
    {
        return getFirmStruct().firmNumber.exchange;
    }

    /**
     * Sets the firm number for the FirmStruct
     * @param number new firm number
     */
    public void setFirmNumber(String number)
    {
        if(number == null)
        {
            throw new IllegalArgumentException("Number must not be null.");
        }
        else
        {
            String oldValue = getFirmNumber();

            if(!number.equals(oldValue))
            {
                getFirmStruct().firmNumber.firmNumber = number;
                setModified(true);
                propertyEventManager.firePropertyChange(FIRM_NUMBER_CHANGE_EVENT, oldValue, number);
            }
        }
    }

    /**
     * Sets the firm exchange for the FirmStruct
     * @param exchange new firm exchange
     */
    public void setFirmExchange(String exchange)
    {
        if(exchange == null)
        {
            throw new IllegalArgumentException("Exchange must not be null.");
        }
        else
        {
            String oldValue = getFirmExchange();

            if(!exchange.equals(oldValue))
            {
                getFirmStruct().firmNumber.exchange = exchange;
                setModified(true);
                propertyEventManager.firePropertyChange(FIRM_EXCHANGE_CHANGE_EVENT, oldValue, exchange);
            }
        }
    }

    /**
     * Gets the firm full name from the FirmStruct
     * @return firm full name
     */
    public String getFullName()
    {
        return getFirmStruct().fullName;
    }

    /**
     * Sets the firm full name for the FirmStruct
     * @param name new firm full name
     */
    public void setFullName(String name)
    {
        if(name == null)
        {
            throw new IllegalArgumentException("Full name must not be null.");
        }
        else
        {
            String oldValue = getFullName();

            if(!name.equals(oldValue))
            {
                getFirmStruct().fullName = name;
                setModified(true);
                propertyEventManager.firePropertyChange(FULL_NAME_CHANGE_EVENT, oldValue, name);
            }
        }
    }

    /**
     * Gets the last modified time for the struct
     * @return Calendar Calendar format of a <code>com.cboe.idl.cmiUtil.DateTimeStruct</code>
     */
    public Calendar getLastModifiedTime()
    {
        if(getFirmStruct().lastModifiedTime != null)
        {
            synchronized(dateWrapper)
            {
                dateWrapper.setDateTime(getFirmStruct().lastModifiedTime);
                return dateWrapper.getNewCalendar();
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * Sets the last modified time for the struct.
     * @param newDateTime last modified time
     */
    public void setLastModifiedTime(Calendar newDateTime)
    {
        if(newDateTime == null)
        {
            throw new IllegalArgumentException("Illegal calendar. Must be a non-null value.");
        }
        else
        {
            Calendar oldValue = getLastModifiedTime();

            if(!newDateTime.equals(oldValue))
            {
                synchronized(dateWrapper)
                {
                    dateWrapper.setDate(newDateTime);
                    getFirmStruct().lastModifiedTime = dateWrapper.toDateTimeStruct();
                }
                setModified(true);
                propertyEventManager.firePropertyChange(LAST_MODIFIED_TIME_CHANGE_EVENT, oldValue, newDateTime);
            }
        }
    }

    /**
     * Gets the membership key from the FirmStruct
     * @return membership key
     */
    public int getMembershipKey()
    {
        return getFirmStruct().membershipKey;
    }

    /**
     * Gets the FirmStruct that this model represents.
     * @return com.cboe.idl.firm.FirmStruct
     */
    public FirmStruct getFirmStruct()
    {
        return firm;
    }

    /**
     * Sets the FirmStruct that this model represents.
     * @param newFirm New struct for this model to represent.
     */
    public void setFirmStruct(FirmStruct newFirm)
    {
        FirmStruct oldFirm = getFirmStruct();

        if(oldFirm != newFirm)
        {
            firm = newFirm;
            setModified(false);
            propertyEventManager.firePropertyChange(STRUCT_CHANGE_EVENT, oldFirm, firm);
        }
    }

    /**
     * Gets the isActive flag
     * @return boolean True if active, false otherwise.
     */
    public boolean isActive()
    {
        return getFirmStruct().isActive;
    }

    /**
     * Sets the isActive flag
     * @param active True if active, false otherwise.
     */
    public void setActive(boolean active)
    {
        if(active != isActive())
        {
            Boolean oldValue = Boolean.valueOf(isActive());
            getFirmStruct().isActive = active;
            setModified(true);
            propertyEventManager.firePropertyChange(ACTIVE_CHANGE_EVENT, oldValue, Boolean.valueOf(active));
        }
    }

    /**
     * Gets the isClearingFirm flag
     * @return boolean True if clearing firm, false otherwise.
     */
    public boolean isClearingFirm()
    {
        return getFirmStruct().isClearingFirm;
    }

    /**
     * Sets the isClearingFirm flag
     * @param clearingFirm True if clearingFirm, false otherwise.
     */
    public void setClearingFirm(boolean clearingFirm)
    {
        if(clearingFirm != isClearingFirm())
        {
            Boolean oldValue = Boolean.valueOf(isClearingFirm());
            getFirmStruct().isClearingFirm = clearingFirm;
            setModified(true);
            propertyEventManager.firePropertyChange(CLEARING_FIRM_CHANGE_EVENT, oldValue, Boolean.valueOf(clearingFirm));
        }
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
     * Add the listener for property changes to the Firm attributes.
     * @param listener PropertyChangeListener to receive a callback when a Firm
     * property is changed.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        propertyEventManager.addPropertyChangeListener(listener);
    }

    /**
     * Removes the listener for property changes to the Firm attributes.
     * @param listener PropertyChangeListener to remove from receiving callbacks when a Firm
     * property is changed.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        propertyEventManager.removePropertyChangeListener(listener);
    }

    public int compareTo(FirmModel o)
    {
        return (getFirmKey() < o.getFirmKey() ? -1 : (getFirmKey() == o.getFirmKey() ? 0 : 1));
    }

    private void setModified(boolean modified)
    {
        isModified = modified;
    }
}