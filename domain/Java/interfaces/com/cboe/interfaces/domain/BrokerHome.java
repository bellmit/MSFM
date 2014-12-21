package com.cboe.interfaces.domain;

// ------------------------------------------------------------------------
// Source file: com.cboe.exceptions.BrokerHome.java
//
// PACKAGE: com.cboe.exceptions
//
// ------------------------------------------------------------------------
// Copyright (c) 1999 The Chicago Board Option
import com.cboe.interfaces.domain.Broker;
import com.cboe.interfaces.domain.TradingClass;
import com.cboe.exceptions.BrokerAlreadyExistsException;
/**
 * BrokerHome is an interface which describes the methods that any <code>BrokerHome</code>
 * implementer must provide.  The <code>Home</code> is used to create and find instances
 * of a <code>BrokerHome</code>.  The <code>BrokerHome</code> can be retrieved from the
 * <code>HomeFactory</code> by providing the <code>public static final</code> variable
 * <code>HOME_NAME</code>.
 *
 * @version 1.13
 * @author David Wegener
 */
public interface BrokerHome {
	public static final String HOME_NAME = "Broker"; // name used to retrieve Broker from HomeFactory
	public static final String NULL_HOME_NAME = "NullBrokerHome";

/**
 * Create a new <code>Broker</code> for the specified product class.
 *
 * @param tradingClass		the trading class for this <code>Broker</code>.
 * @return				a new <code>Broker</code> for the specified class.
 * @exception BrokerAlreadyExistsException	if there is already a <code>Broker</code
 *											for the specified product class.
 */
public Broker create(TradingClass tradingClass) throws BrokerAlreadyExistsException;

/**
 * Create a new <code>Broker</code> for the specified product.
 *
 * @param tradingClass		the trading class for this <code>Broker</code>.
 * @return				a new <code>Broker</code> for the specified class.
 * @exception BrokerAlreadyExistsException	if there is already a <code>Broker</code
 *											for the specified product class.
 */
public Broker create(TradingProduct tradingClass) throws BrokerAlreadyExistsException;

/**
 * Find the <code>Broker</code> associated with the specified product class.
 *
 * @param className 	<code>String</code that contains the product class name.
 * @return 				the <code>Broker</code> that handles the product class, or
 *						<code>null</code> if there is no broker for the class.
 */
public Broker findByClass(int classKey);
/**
 * Find the <code>Broker</code> associated with the specified product.
 *
 * @param productKey 	<code>Integer</code that contains the product key.
 * @return 				the <code>Broker</code> that handles the product, or
 *						<code>null</code> if there is no broker for the product.
 */
public Broker findByProduct(int productKey);

/**
 * As part of the STS project we added this for finding the Proxy Product 
 * to find the Broker
 * @param product
 * @return
 */
public Broker findByProxyProduct(TradingProduct product);

/**
 * Find the BrokerSellShortOrderHolder associated with the specified product.
 */
public Object findHolderByClass(int classKey);

}
