package com.cboe.presentation.product;

import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiSession.SessionProductStruct;
import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.domain.SessionKeyWrapper;
import com.cboe.presentation.common.formatters.ProductStates;
import com.cboe.domain.util.ClientProductStructBuilder;
import com.cboe.domain.util.SessionKeyContainer;
class InactiveSessionProductImpl extends ProductImpl implements SessionProduct
{
    protected String invalidTradingSession ;
    protected SessionProductStruct sessionProductStruct;
    protected String sessionName;
    private SessionKeyWrapper sessionKeyWrapper;

    public InactiveSessionProductImpl(String sessionName, String inactiveSessionName, ProductStruct productStruct)
    {
        super(productStruct);
        sessionProductStruct = new SessionProductStruct(sessionName, ProductStates.NO_SESSION, productStruct, -1);
        this.invalidTradingSession = inactiveSessionName;
    }

    public boolean isDefaultSession()
    {
        return false;
    }

    public boolean isInactiveInTradingSession()
    {
        return true;
    }

    public void setState(short state)
    {
        if( ProductStates.toString(state).equals(ProductStates.INVALID_TYPE))
        {
            throw new IllegalArgumentException("Invalid state: " + state);
        }
        getSessionProductStruct().productState = state;
    }

    /**
     * Sets the product state transaction sequence number for this SessionProduct.
     * @param sequenceNumber to set in the represented struct
     */
    public void setProductStateTransactionSequenceNumber(int sequenceNumber)
    {
        getSessionProductStruct().productStateTransactionSequenceNumber = sequenceNumber;
    }

    /**
     * Get the SessionProductStruct that this SessionProduct represents.
     * @return SessionProductStruct
     * @deprecated
     */
    public SessionProductStruct getSessionProductStruct()
    {
        return this.sessionProductStruct;
    }

    /**
     * Get the state for this SessionProduct.
     * @return state from represented struct
     */
    public short getState()
    {
        return getSessionProductStruct().productState;
    }

    /**
     * Gets the product state transaction sequence number for this SessionProduct.
     * @return product state transaction sequence number from represented struct
     */
    public int getProductStateTransactionSequenceNumber()
    {
        return getSessionProductStruct().productStateTransactionSequenceNumber;
    }

    /**
     * Get the trading session name for this SessionProduct.
     * @return trading session name from represented struct
     */
    public String getTradingSessionName()
    {
        return getSessionProductStruct().sessionName;
    }

    public void updateProduct(Product newProduct)
    {
        sessionProductStruct.productStruct = newProduct.getProductStruct();
        super.updateProduct(newProduct);
    }
    /**
     * Clones this class by returning another instance that represents a
     * SessionProductStruct that was also cloned.
     */
    public Object clone() throws CloneNotSupportedException
    {
        return new InactiveSessionProductImpl(sessionName, invalidTradingSession, getSessionProductStruct().productStruct);
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
        return ProductHelper.getHashCode(getTradingSessionName(), getProductKey());
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
