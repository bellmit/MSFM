
/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) <p>
 * Company:      <p>
 * @author
 * @version 1.0
 */
package com.cboe.interfaces.presentation.product;
import com.cboe.idl.cmiSession.SessionProductStruct;
import com.cboe.interfaces.domain.SessionKeyWrapper;


public interface SessionProduct extends Product
{
    /**
     * @deprecated Use public getters to get struct contents always
     */
    public SessionProductStruct getSessionProductStruct();

    public short getState();
    public int getProductStateTransactionSequenceNumber();
    public void setState(short state);
    public void setProductStateTransactionSequenceNumber(int sequenceNumber);
    public String getTradingSessionName();
    public void updateProduct(Product newProduct);
    public boolean isInactiveInTradingSession();
    public SessionKeyWrapper getSessionKeyWrapper();

    /**
     * Determines if this trading session is the All Sessions
     */
    public boolean isDefaultSession();
    
    public static final String NOT_IN_TRADING_SESSION = "NOT_IN_TRADING_SESSION";
}
