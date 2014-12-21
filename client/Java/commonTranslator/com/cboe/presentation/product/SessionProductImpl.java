//
// -----------------------------------------------------------------------------------
// Source file: SessionProductImpl.java
//
// PACKAGE: com.cboe.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.product;

import java.beans.PropertyChangeEvent;

import com.cboe.domain.util.SessionKeyContainer;

import com.cboe.interfaces.presentation.common.businessModels.MutableBusinessModel;
import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.interfaces.domain.SessionKeyWrapper;

import com.cboe.idl.cmiSession.SessionProductStruct;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.formatters.ProductStates;

/**
 * SessionProduct implementation for a SessionProductStruct from the API.
 */
class SessionProductImpl extends ProductImpl implements SessionProduct
{
    private SessionKeyWrapper sessionKeyWrapper;
    private int cachedHash = -1;

    public String sessionName;
    public short productState;
    public int productStateTransactionSequenceNumber;

    /**
     * Constructor
     * @param sessionProductStruct to represent
     */
    public SessionProductImpl(SessionProductStruct sessionProductStruct)
    {
        super(sessionProductStruct.productStruct);

        sessionName = com.cboe.presentation.util.StringCache.get(sessionProductStruct.sessionName);
        productState = sessionProductStruct.productState;
        productStateTransactionSequenceNumber = sessionProductStruct.productStateTransactionSequenceNumber;
    }

    /**
     *  Default constructor.
     */
    protected SessionProductImpl()
    {
        super();
    }

    public boolean isDefaultSession()
    {
        return false;
    }

    /**
     * Sets the state for this SessionProduct.
     * @param state to set in the represented struct
     */
    public void setState(short state)
    {
        if(state != ProductStates.CLOSED && state != ProductStates.ENDING_HOLD && state != ProductStates.FAST_MARKET &&
            state != ProductStates.HALTED && state != ProductStates.NO_SESSION && state != ProductStates.ON_HOLD &&
            state != ProductStates.OPEN && state != ProductStates.OPENING_ROTATION && state != ProductStates.PRE_OPEN &&
            state != ProductStates.SUSPENDED)
        {
            GUILoggerHome.find().alarm("Unknown Product State:"+ProductStates.toString(state)+" Product:"+toString());
        }

        productState = state;
        firePropertyChange(new PropertyChangeEvent(this, MutableBusinessModel.DATA_CHANGE_EVENT, null, state));
    }

    /**
     * Sets the product state transaction sequence number for this SessionProduct.
     * @param sequenceNumber to set in the represented struct
     */
    public void setProductStateTransactionSequenceNumber(int sequenceNumber)
    {
        productStateTransactionSequenceNumber = sequenceNumber;
        firePropertyChange(new PropertyChangeEvent(this, MutableBusinessModel.DATA_CHANGE_EVENT, null, sequenceNumber));
    }

    /**
     * Get the SessionProductStruct that this SessionProduct represents.
     * @return SessionProductStruct
     * @deprecated
     */
    public SessionProductStruct getSessionProductStruct()
    {
        SessionProductStruct retVal = new SessionProductStruct();
        retVal.productState = getState();
        retVal.productStateTransactionSequenceNumber = getProductStateTransactionSequenceNumber();
        retVal.sessionName = getTradingSessionName();
        retVal.productStruct = getProductStruct();
        return retVal;
    }

    /**
     * Get the state for this SessionProduct.
     * @return state from represented struct
     */
    public short getState()
    {
        return productState;
    }

    /**
     * Gets the product state transaction sequence number for this SessionProduct.
     * @return product state transaction sequence number from represented struct
     */
    public int getProductStateTransactionSequenceNumber()
    {
        return productStateTransactionSequenceNumber;
    }

    /**
     * Get the trading session name for this SessionProduct.
     * @return trading session name from represented struct
     */
    public String getTradingSessionName()
    {
        return sessionName;
    }

    /**
     * Clones this class by returning another instance that represents a
     * SessionProductStruct that was also cloned.
     */
    public Object clone() throws CloneNotSupportedException
    {
        return new SessionProductImpl(getSessionProductStruct());
    }

    /**
     * If <code>obj</code> is equal according to my super class, then if it is an
     * instance of SessionProduct and has the same session name true is returned, false otherwise.
     * @param obj to compare
     * @return true if equal, false if not.
     */
    public boolean equals(Object obj)
    {
        boolean isEqual = super.equals(obj);

        if(isEqual)
        {
            if(obj instanceof SessionProduct)
            {
                isEqual = getTradingSessionName().equals(((SessionProduct)obj).getTradingSessionName());
            }
            else
            {
                isEqual = false;
            }
        }

        return isEqual;
    }

    /**
     * Returns a hash code for this Product
     */
    public int hashCode()
    {
        if( cachedHash == -1 )
        {
            cachedHash = ProductHelper.getHashCode(getTradingSessionName(), getProductKey());
        }
        return cachedHash;
    }

    public boolean isInactiveInTradingSession()
    {
        return false;
    }

    public SessionKeyWrapper getSessionKeyWrapper()
    {
        if (sessionKeyWrapper == null)
        {
            sessionKeyWrapper = new SessionKeyContainer(getTradingSessionName(), getProductKey());
        }
        return sessionKeyWrapper;
    }
}

