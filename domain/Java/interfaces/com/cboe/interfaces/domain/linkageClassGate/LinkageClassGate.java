package com.cboe.interfaces.domain.linkageClassGate;

/**
 * Created by IntelliJ IDEA.
 * User: woltersm
 * Date: Sep 21, 2005
 * Time: 9:17:43 AM
 * To change this template use File | Settings | File Templates.
 */
import com.cboe.interfaces.domain.TradingProduct;
import com.cboe.interfaces.domain.Order;

import java.util.ArrayList;

/**
 * @author Mark Wolters
 */
public interface LinkageClassGate {
    /**
     * Returns an ArrayList of ExchangeGateIndicatorStructs
     * @return ArrayList
     */
    public ArrayList getExchanges();

    /**
     * Closes the class gate given only an order
     * @param Order
     */
    public void closeGate(Order order);

    /**
     * Returns true if the gate is closed or false if it is open for a given exchange
     * @return boolean
     * @param String Exchange
     */
    public boolean isClosed(String Exchange);

}
