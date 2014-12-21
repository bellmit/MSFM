package com.cboe.interfaces.domain.bestQuote;

import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.orderBook.*;
import com.cboe.interfaces.domain.MarketUpdate;
import com.cboe.interfaces.domain.Price;


/**
 * A summary of the current best price in the market.  This is the equivalent
 * of an internal best quote.
 *
 * @author John Wickberg
 */
public interface CurrentMarket
{
/**
 * Performs instance initialization.
 */
void create();
/**
 * Gets the ask price of the quote.
 */
Price getAskPrice();
/**
 * Gets the ask size of the quote.
 */
int getAskSize();
/**
 * Gets the bid price of the quote.
 */
Price getBidPrice();
/**
 * Gets the bid size of the quote.
 */
int getBidSize();
/**
 * Converts the quote to a CORBA struct.
 */
CurrentMarketStruct toStruct();
/**
 * Updates the quote from a CORBA struct representing the current market
 * best.
 *
 * @param newBest new market best
 */
void update(CurrentMarketStruct newBest);

/**
 * Updates the quote from a CORBA struct representing the current market
 * best.
 *
 * @param newBest new market best
 */
void update(MarketUpdate update, short viewType);

/**
 * Updates the quote from a CORBA struct representing the best
 * prices in the book.
 *
 * @param bestBook best prices from book
 * @param exchange exchange for product
 */
void update(BestBookStruct bestBook, String exchange);
/**
 * Set the NBBO indicator for bid side
 */
void setBidInNBBO(boolean isInNBBO);
/**
 * Set the NBBO indicator for ask side
 */
void setAskInNBBO(boolean isInNBBO);


public String getSessionName();

public void setSessionName(String p_sessionName);

public ProductKeysStruct getProductKeys();

public void setProductKeys(ProductKeysStruct p_productKeys);

void setLegalMarket(boolean isLegalMarket);

void setProductKeys(int key, int classKey, short state, int reportingClsssKey);

void setUpdateTime(long updateTime);

void setExchange(String exchange);

 void setBidPrice(Price price);
 
 void setAskPrice(Price price);
 
 void setBidVolumes(MarketVolumeStruct[] volumes);
 
 void setAskVolumes(MarketVolumeStruct[] volumes);

}
