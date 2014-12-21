//
// -----------------------------------------------------------------------------------
// Source file: JointAccount.java
//
// PACKAGE: com.cboe.internalPresentation.user;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.user;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;

import com.cboe.domain.util.DateWrapper;

import com.cboe.idl.user.AccountDefinitionStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;

import com.cboe.interfaces.internalPresentation.user.JointAccount;

import com.cboe.interfaces.presentation.user.ExchangeFirm;
import com.cboe.presentation.user.ExchangeFirmFactory;

import com.cboe.internalPresentation.common.comparators.JointAccountComparator;

/**
 * This class wraps <code>AccountDefinitionStruct</code> and provides various helper methods.
 */
public class JointAccountImpl implements JointAccount
{
    private PropertyChangeSupport propertyEventManager = new PropertyChangeSupport(this);
    private DateWrapper dateWrapper = new DateWrapper();
    private boolean isModified = false;
    private AccountDefinitionStruct account = null;
    private JointAccountComparator comparator = new JointAccountComparator();

    /**
     * Constructor to initialize with struct
     * @param account com.cboe.idl.user.JointAccountDefinitionStruct
     */
    public JointAccountImpl(AccountDefinitionStruct account)
    {
        super();

        if(account == null)
        {
            throw new IllegalArgumentException("JointAccount(AccountDefinitionStruct account) may not be null.");
        }
        this.account = account;

        setModified(false);
    }

    /**
     * Returns the account name as a hashCode.
     * @return account name as hash code
     */
    public int hashCode()
    {
        return getAccountName().hashCode();
    }

    /**
     * Returns the account name as the String
     * @return account name. If inactive text will be appended also.
     */
    public String toString()
    {
        StringBuffer result = new StringBuffer();

        result.append(getAccountName());
        result.append('(').append(getExecutingGiveupFirm()).append(')');

        if(isPrimaryDPMParticipant())
        {
            result.append(" (DPM)");
        }

        if(!isActive())
        {
            result.append(" (Inactive)");
        }
        return result.toString();
    }

    /**
     * Determines if this object is equal to passed object. Comparison will be done
     * on instance, type, userKey, then all other attributes of the containing struct.
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
        else
        {
            return (comparator.compare(this, obj) == 0 ? true : false);
        }
    }

    /**
     * Gets the Account name
     * @return account name
     */
    public String getAccountName()
    {
        return getAccountStruct().account.acronym;
    }

    /**
     * Sets the Account name
     * @param name of account
     */
    public void setAccountName(String name)
    {
        if(name == null)
        {
            throw new IllegalArgumentException("Name must not be null.");
        }
        else
        {
            String oldValue = getAccountName();

            if(!name.equals(oldValue))
            {
                getAccountStruct().account.acronym = name;
                setModified(true);
                propertyEventManager.firePropertyChange(ACCOUNT_NAME_CHANGE_EVENT, oldValue, name);
            }
        }
    }

    /**
     * Gets the Account's Executing or Give Up firm
     * @return executing or give up firm
     */
    public ExchangeFirm getExecutingGiveupFirm()
    {
        return ExchangeFirmFactory.createExchangeFirm(getAccountStruct().executingGiveupFirm);
    }

    /**
     * Sets the Executing or give up firm for this Account
     * @param firm of account
     */
    public void setExecutingGiveupFirm(ExchangeFirm firm)
    {
        if(firm == null)
        {
            throw new IllegalArgumentException("Firm must not be null.");
        }
        else
        {
            ExchangeFirm oldValue = getExecutingGiveupFirm();

            if(!firm.equals(oldValue))
            {
                getAccountStruct().executingGiveupFirm = firm.getExchangeFirmStruct();
                setModified(true);
                propertyEventManager.firePropertyChange(GIVEUP_FIRM_CHANGE_EVENT, oldValue, firm);
            }
        }
    }

    /**
     * Gets the last modified time as a Calendar
     * @return Calendar
     */
    public Calendar getLastModifiedTime()
    {
        if(getAccountStruct().lastModifiedTime != null)
        {
            synchronized(dateWrapper)
            {
                dateWrapper.setDateTime(getAccountStruct().lastModifiedTime);
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
                    getAccountStruct().lastModifiedTime = dateWrapper.toDateTimeStruct();
                }
                setModified(true);
                propertyEventManager.firePropertyChange(LAST_MODIFIED_TIME_CHANGE_EVENT, oldValue, newDateTime);
            }
        }
    }

    /**
     * Gets the active state of the account
     * @return boolean
     */
    public boolean isActive()
    {
        return getAccountStruct().isActive;
    }

    /**
     * Sets the Session isActive flag
     * @param active True if template is enabled to become active, false otherwise.
     */
    public void setActive(boolean active)
    {
        if(active != isActive())
        {
            Boolean oldValue = new Boolean(isActive());
            getAccountStruct().isActive = active;
            setModified(true);
            propertyEventManager.firePropertyChange(ACTIVE_CHANGE_EVENT, oldValue, new Boolean(active));
        }
    }

    /**
     * Gets the Primary DPM participant state of the account
     * @return boolean
     */
    public boolean isPrimaryDPMParticipant()
    {
        return getAccountStruct().isPrimaryDpmParticipant;
    }

    /**
     * Sets the Session isActive flag
     * @param active True if template is enabled to become active, false otherwise.
     */
    public void setPrimaryDPMParticipant(boolean primaryDPM)
    {
        if(primaryDPM != isPrimaryDPMParticipant())
        {
            Boolean oldValue = new Boolean(isPrimaryDPMParticipant());
            getAccountStruct().isPrimaryDpmParticipant = primaryDPM;
            setModified(true);
            propertyEventManager.firePropertyChange(PRIMARY_DPM_CHANGE_EVENT, oldValue, new Boolean(primaryDPM));
        }
    }

    /**
     * Gets the AccountDefinitionStruct that this is wrapping
     * @return AccountDefinitionStruct
     */
    public AccountDefinitionStruct getAccountStruct()
    {
        return account;
    }

    /**
     * Gets the AccountDefinitionStruct that this is wrapping
     * @param newAccount to wrap
     */
    public void setAccountStruct(AccountDefinitionStruct newAccount)
    {
        AccountDefinitionStruct oldAccount = getAccountStruct();

        if(oldAccount != newAccount)
        {
            account = newAccount;
            setModified(false);
            propertyEventManager.firePropertyChange(STRUCT_CHANGE_EVENT, oldAccount, newAccount);
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
