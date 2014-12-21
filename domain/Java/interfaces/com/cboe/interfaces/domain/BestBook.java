package com.cboe.interfaces.domain;

import com.cboe.idl.orderBook.BestBookStruct;

/**
 * A holder of the best book values of an order book.
 *
 * @author John Wickberg
 * @since increment 5
 */
public interface BestBook {

	/**
	 * Gets the best ask price even if only contingent orders are at that price.
	 */
	Price getContingentAskPrice();
	/**
	 * Gets all contingenct quantity available at the contingent ask price.
	 */
	int getContingentAskQuantity();
	/**
	 * Gets the best bid price even if only contingent orders are at that price.
	 */
	Price getContingentBidPrice();
	/**
	 * Gets all contingent quantity available at the contingent bid price.
	 */
	int getContingentBidQuantity();
	/**
	 * Gets all contingent quantity available at the contingent price.
	 */
	int getContingentQuantityMax(Side side);
	
	/**
	 * Gets the min contingent quantity available at the contingent price.
	 */
	int getContingentQuantityMin(Side side);
	/**
	 * Gets the best ask price at which there are some non-contingent orders.
	 */
	Price getNonContingentAskPrice();

	/**
	 * Gets the quantity available at the non-contingent ask price.
	 */
	int getNonContingentAskQuantity();

	/**
	 * Gets the best bid price at which there are some non-contingent orders.
	 */
	Price getNonContingentBidPrice();

	/**
	 * Gets the quantity available at the non-contingent bid price.
	 */
	int getNonContingentBidQuantity();
    
    Price getNonContingentBidPriceForOddLots();
    
    Price getNonContingentAskPriceForOddLots();

	/**
	 * Converts the contingent best book values to a CORBA struct.
	 */
	BestBookStruct toContingentStruct();

	/**
	 * Converts the non-contingent best book values to a CORBA struct.
	 */
	BestBookStruct toNonContingentStruct();
    
    Price getContingentPrice(Side side);
    
    Price getNonContingentPrice(Side side);
    
    int getContingentQuantity(Side side);
    
    int getNonContingentQuantity(Side side);
}
