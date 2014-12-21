package com.cboe.interfaces.domain;

import com.cboe.exceptions.DataValidationException;
import com.cboe.idl.cmiMarketData.ExpectedOpeningPriceStruct;
import com.cboe.idl.cmiMarketData.NBBOStruct;
/**
 * Keeps track of the expected opening trade price
 * if the book is crossed.
 *
 * @author Kevin Park
 */
public interface OpeningPriceIndication {

/**
 * Returns imbalance quantity of market imbalance.
 * Is zero if no market imbalance.
 *
 * @return int	market imbalance quantity.
 */
public int getImbalanceQuantity();
/**
 * Returns tradable opening price.  If no trade is
 * possible, the price is zero.
 *
 * @return Price	opening price
 */
public Price getOpeningPrice();
public short getOpeningType();
/**
 * Returns possible opening trade quantity.
 *
 * @return int		opening trade quantity
 */
public int getOpeningQuantity();
/**
 * Returns true if book is not crossed or maybe taken
 * out of imbalance situation by a trade.
 *
 * @return boolean	openable indicator
 */
public boolean isOpenable();

/**
 * Returns true if we can open immediately.
 *
 * @return boolean  open indicator
 */
public boolean openImmediate();

/**
 * Returns true if an opening trade is possible.
 *
 * @return boolean		tradable indicator
 */
public boolean isTradable();
/**
 * Build and return corba struct for opening price indication.
 *
 * @return ExpectedOpeningPriceStruct
 */
public ExpectedOpeningPriceStruct toExpectedOpeningPriceStruct(short productState);
/**
 * Return a boolean to indicate if there is a need to change sell market orders to limit orders
 */
public boolean needToChangeSellMktOrdersToLimit();

/**
 * return the opening type information
 */
public boolean isOpeningType(short anOpeningType);

public void evalOpeningCondition() throws DataValidationException;

public NBBOStruct getBOTR();

}
