package com.cboe.interfaces.domain.marketDataReportService;

import com.cboe.idl.cmiMarketData.TickerStruct;
import com.cboe.idl.cmiUtil.TimeStruct;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.product.OptionType;
import com.cboe.idl.cmiMarketData.RecapStructV5;

/**
 * A summary of trading for a product.
 * 
 * @author Cognizant Technology Solutions.
 */
public interface RecapForReport
{
    /**
     * Gets last sale price from <code>RecapForReport</code>.
     * 
     * @return last sale price
     */
    Price getLastSalePrice();

    /**
     * Gets last sale quantity from <code>RecapForReport</code>.
     * 
     * @return last sale volume
     */
    int getLastSaleVolume();

    /**
     * Gets tick direction from <code>RecapForReport</code>.
     * 
     * @return tick direction
     */
    char getTickDirection();

    /**
     * Getter for product key.
     * 
     * @return productKey
     */
    int getProductKey();

    /**
     * Getter for session name.
     * 
     * @return session Name
     */
    String getSessionName();

    /**
     * Getter for class key.
     * 
     * @return classKey
     */
    int getClassKey();

    /**
     * Gets the net change
     * 
     * @return Net Change.
     */
    Price getNetChange();

    /**
     * Get the total volume of the Recap
     * 
     * @return Total Volume
     */
    int getTotalVolume();

    /**
     * Gets the Opening Price of the product from Recap
     * 
     * @return Open Price
     */
    Price getOpenPrice();

    /**
     * Gets the Option type
     * 
     * @return Option Type
     */
    OptionType getOptionType();

    /**
     * Gets the product type.
     * 
     * @return product Type
     */
    short getProductType();

    /**
     * Gets the Product has been traded or not.
     * 
     * @return boolean whether the product has been traded or not.
     */
    boolean getHasBeenTraded();

    /**
     * Gets the Open Interest for the product.
     * 
     * @return Open Interest.
     */
    int getOpenInterest();

    /**
     * Gets the close price of the Product.
     * 
     * @return <code>Price</code>
     */
    Price getClosePrice();

    /**
     * Gets the total number of trades happened for the product.
     * 
     * @return number of trades.
     */
    int getNumberOfTrades();

    /**
     * Gets the base category type
     * 
     * @return category type.
     */
    int getUnderlyingCategoryType();

    /**
     * Updates this <code>RecapForReport</code> using ticker values.
     * 
     * @param TickerStruct containing ticker values
     */
    public void updateTickerEntry(TimeStruct tradeTime, TickerStruct ticker);

    /**
     * Updates the Open interest.
     * 
     * @param openInterest
     */
    void updateOpenInterest(int openInterest);
    
    /**
     * gets the Last Sale Price Volume 
     * @return LastSalePriceVolume
     */
    int getLastSalePriceVolume();
    
    /**
     * Get Trade Time
     * @return TradeTime
     */
    long getTradeTime();
    
    /**
     * Get Tick
     * @return TickAmount
     */
    Price getTickAmount();
    
    /**
     * Get Net Change Direction
     * @return NetChangeDirection
     */
    char getNetChangeDirection();
    
    /**
     * Get High Price
     * @return HighPrice
     */
    Price getHighPrice();
    
    /**
     * Get HighPriceVolume
     * @return HighPriceVolume
     */
    int getHighPriceVolume();
    
    /**
     * Get High Price Time
     * @return HighPriceTime
     */
    long getHighPriceTime();
    
    /**
     * Get Low price
     * @return LowPrice
     */
    Price getLowPrice();
    
    /**
     * Get low price volume
     * @return LowPriceVolume
     */
    int getLowPriceVolume();
    
    /**
     * Get low price time
     * @return LowPriceTime
     */
    long getLowPriceTime();
    
    /**
     * Get Open Price Volume
     * @return OpenPriceVolume
     */
    int getOpenPriceVolume();
    
    /**
     * Get Open Price Time
     * @return OpenPriceTime
     */
    long getOpeningPriceTime();
    
    /**
     * Create a RecapStruct.
     * 
     */
    public RecapStructV5 updateRecapStructWithLastSale(RecapStructV5 recapStructV5);

}
