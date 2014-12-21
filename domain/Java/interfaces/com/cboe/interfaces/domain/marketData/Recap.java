package com.cboe.interfaces.domain.marketData;

import com.cboe.idl.cmiMarketData.*;
import com.cboe.interfaces.domain.Price;
import com.cboe.util.*;

/**
 * A summary of trading for a product.
 *
 * @author John Wickberg
 */
public interface Recap
{
/**
 * Performs instance initialization.
 */
void create();    
/**
 * Performs instance initialization.
 * 
 * @param closePrice close price
 */
void create(Price closePrice);
/**
 * Gets last sale price from recap.
 *
 * @return last sale price
 */
Price getLastSalePrice();
/**
 * Gets last sale quantity from recap.
 *
 * @return last sale volume
 */
int getLastSaleVolume();
/**
 * Gets tick direction from recap.
 *
 * @return tick direction
 */
char getTickDirection();
/**
 * Converts this recap to a CORBA struct.
 *
 * @return base recap struct
 */
RecapStruct toStruct();
/**
 * Updates this recap.
 *
 * @param recap struct containing recap values
 */
void update(RecapStruct recap);
/**
 * Updates this recap using ticker values.
 *
 * @param recap struct containing ticker values
 */
void update(TickerStruct ticker);
/**
 * Get Total Number Of Trades.
 * 
 * @return int
 */
int getNumberOfTrades();

}
