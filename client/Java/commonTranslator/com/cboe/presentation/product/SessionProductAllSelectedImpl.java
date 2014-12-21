//
// -----------------------------------------------------------------------------------
// Source file: SessionProductAllSelectedImpl.java
//
// PACKAGE: com.cboe.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.product;

import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.presentation.product.AllSessionProduct;
import com.cboe.interfaces.presentation.tradingSession.DefaultTradingSession;
import com.cboe.interfaces.domain.SessionKeyWrapper;

import com.cboe.idl.cmiSession.SessionProductStruct;
import com.cboe.idl.cmiConstants.ProductStates;
import com.cboe.domain.util.SessionKeyContainer;

/**
 * SessionProductClass implementation for an All Selected SessionProductClass for the OPTION type.
 */
class SessionProductAllSelectedImpl extends ProductAllSelectedImpl implements AllSessionProduct
{
    private SessionKeyWrapper sessionKeyWrapper;
    private String sessionName;

    protected SessionProductAllSelectedImpl()
    {
        this(DefaultTradingSession.DEFAULT);
    }

    protected SessionProductAllSelectedImpl(String sessionName)
    {
        super();
        setTradingSessionName(sessionName);
    }

    public boolean isDefaultSession()
    {
        return getTradingSessionName().equals(DefaultTradingSession.DEFAULT);
    }

    /**
     * Does nothing for this implementation.
     */
    public void setState(short state)
    {}

    /**
     * Does nothing for this implementation.
     */
    public void setProductStateTransactionSequenceNumber(int sequenceNumber)
    {}

    /**
     * Get the SessionProductStruct that this SessionProduct represents.
     * @return Null is returned
     * @deprecated
     */
    public SessionProductStruct getSessionProductStruct()
    {
        return null;
    }

    /**
     * Get the state for this SessionProduct.
     * @return Returns ProductStates.NO_SESSION
     */
    public short getState()
    {
        return ProductStates.NO_SESSION;
    }

    /**
     * Get the product state transaction sequence number for this SessionProduct.
     * @return zero is returned
     */
    public int getProductStateTransactionSequenceNumber()
    {
        return 0;
    }

    public void setTradingSessionName(String sessionName)
    {
        if( sessionName == null || sessionName.length() == 0 )
        {
            throw new IllegalArgumentException("Invalid sessionName. May not be null or empty string.");
        }
        this.sessionName = sessionName;
    }

    public String getTradingSessionName()
    {
        return sessionName;
    }

    public void updateProduct(Product newProduct)
    {}

    /**
     * Clones this object by returning another instance of this class.
     */
    public Object clone() throws CloneNotSupportedException
    {
        return new SessionProductAllSelectedImpl(getTradingSessionName());
    }

    /**
     * If <code>obj</code> is an instance of this class true is return,
     * false otherwise.
     * @param obj to compare
     * @return true if equal, false if not.
     */
    public boolean equals(Object obj)
    {
        boolean isEqual = super.equals(obj);

        if( isEqual )
        {
            if( obj instanceof SessionProductAllSelectedImpl )
            {
                isEqual = getTradingSessionName().equals(
                        (( SessionProductAllSelectedImpl ) obj).getTradingSessionName());
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
        return ProductHelper.getHashCode(getTradingSessionName(), getProductKey());
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
