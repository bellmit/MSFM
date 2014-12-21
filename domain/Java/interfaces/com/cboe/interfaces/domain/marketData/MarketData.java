package com.cboe.interfaces.domain.marketData;

import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
import com.cboe.idl.cmiMarketData.ExchangeIndicatorStruct;
import com.cboe.idl.cmiMarketData.NBBOStruct;
import com.cboe.idl.cmiMarketData.RecapStruct;
import com.cboe.idl.cmiMarketData.TickerStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.interfaces.domain.MarketUpdate;
import com.cboe.idl.marketData.BOStruct;
import com.cboe.idl.quote.ExternalQuoteSideStruct;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.Side;
import com.cboe.interfaces.domain.bestQuote.BestQuote;
import com.cboe.interfaces.domain.bestQuote.CurrentMarket;
import com.cboe.interfaces.domain.linkageClassGate.LinkageClassGate;
import com.cboe.interfaces.domain.optionsLinkage.AllExchangesBBO;

/**
 * A container of trading related data for the current trading day.
 * The last sale summary, NBBO and market best will only be available
 * for products that are traded.  The underlying recap will only
 * be available for products that are used as an underlying product.
 *
 * @author John Wickberg
 */
public interface MarketData
{
/**
 * Gets class key of this market data.
 *
 * @return class key
 */
int getClassKey();
/**
 * Returns a reference for the contingent market best.
 *
 * @return current market of this market data
 */
CurrentMarket getBestMarket();
/**
 * Returns a reference for the non-contingent market best.
 *
 * @return current market of this market data
 */
CurrentMarket getBestLimitMarket();
/**
 * Returns a refence to the NBBO.
 *
 * @return NBBO of this market data
 */
BestQuote getNBBO();

/**
 * @return CBOE Best market this market data
 */
BOStruct getCBOEMarket();

/**
 * @return Top of spread book
 */
BOStruct getTopOfBook();

/**
 * Returns a refence to the BOTR.
 *
 * @return BOTR of this market data
 */
BestQuote getBOTR();

//List getAllExchangesBBO();

AllExchangesBBO getAllExchangesBBO(char quoteSides);

/**
 * Gets product key of this market data.
 *
 * @return product key
 */
int getProductKey();
/**
 * Returns a reference to the recap.
 *
 * @return recap for this market data
 */
Recap getRecap();
///**
// * Converts the contingent market best of this market data to a CORBA struct.
// *
// * @return current market data struct
// */
//MarketDataStructsHolder toCurrentMarketStructs();




/**
 * Converts the NBBO of this market data to a CORBA struct.
 *
 * @return quote struct for NBBO
 */
NBBOStruct toNBBOStruct();
/**
 * Converts the recap of this market data to a CORBA struct.
 *
 * @return recap struct
 */
RecapStruct toRecapStruct();
/**
 * Updates contingent market best.
 *
 * @param currentMarkets new current market values
 */
void updateCurrentMarkets(MarketDataStructsHolder currentMarkets);

/**
 * Unpackaged version of updateCurrentMarkets(MarketDataStructsHolder currentMarkets)
 *
 */
void updateCurrentMarkets(
        CurrentMarketStruct bestMarket,
        CurrentMarketStruct bestLimitMarket,
        CurrentMarketStruct bestPublicMarket,
        CurrentMarketStruct bestPublicMarketAtTop);

/**
 * Unpackaged version of updateCurrentMarkets(MarketDataStructsHolder currentMarkets)
 *
 */
void updateCurrentMarkets(
        MarketUpdate update);

/**
 * Updated a side of BOTR.
 */
void updateBOTR(ExternalQuoteSideStruct botrSide);
/**
 * Updates NBBO.
 *
 * @param newQuote new NBBO quote
 */
void updateNBBO(NBBOStruct newQuote);
/**
 * Updates recap for product.
 *
 * @param newRecap new recap values
 */
void updateRecap(RecapStruct newRecap);
/**
 * Updates recap from ticker values.
 * High/low prices will be maintained.
 * Tick will be calculated.
 * Net change will be calculated.
 *
 * @param ticker last sale values
 */
void updateRecap(TickerStruct ticker);
    
    /**
     * return the best public market
     */ 
    public CurrentMarket getBestPublicMarket();
    public CurrentMarket getBestPublicMarketAtTop();
    
    /**
     * return a CurrentMarketStruct which represents BestMarket
     */ 
    public CurrentMarketStruct toBestMarket();
    
    /**
     * return a CurrentMarketStruct which represents BestLimitMarket
     */ 
    public CurrentMarketStruct toBestLimitMarket();
    
    /**
     * return a CurrentMarketStruct which represents BestPublicMarket
     */ 
    public CurrentMarketStruct toBestPublicMarket();
    
    /**
     * change the inNBBO indicator for current market
     */ 
    public void setBidInNBBOForCurrentMarket(boolean aBoolean);
    public void setAskInNBBOForCurrentMarket(boolean aBoolean);


   
    /**
     * set the market indicators for exchanges for the same class
     * @param indicators
     */
    public void setExchangeIndicators(ExchangeIndicatorStruct[] indicators);

    /**
     * get the market indicators
     * @return
     */

    public LinkageClassGate getLinkageClassGate();
    
    public String getAskSideLocation ();
    
    public String getBidSideLocation ();
    
    public void setAskSideLocation (String location);

    public void setBidSideLocation (String location);
    
    public void setMDHLocation (String location);
    
    public String getMDHLocation ();
  
    public AwayExchangeQuote addOrUpdateQuoteForExchange(char exchangeCode, Price bidPrice, int bidVolume,
                                                         Price askPrice, int askVolume, char marketCondition);
    
    public AwayExchangeQuote updatePriceForSide(char botrExchangeCode, char side, Price updatePrice);
    
    public AwayExchangeQuote updateExchangeQuoteBySide(char botrExchangeCode, char side, 
                                                       Price updatePrice, int updateVolume);
    
    public byte[] getBidBOTRExchanges();
    public void setBidBOTRExchanges(byte[] p_bidBOTRExchanges, int numberOfBidBOTRExchanges);

    public byte[] getAskBOTRExchanges();
    public void setAskBOTRExchanges(byte[] p_askBOTRExchanges, int numberOfAskBOTRExchanges);
    
    public AwayExchangeQuote[] getAwayMarketQuotesByExchange();
    public ExchangeIndicatorStruct[] getExchangeIndicators();
    
    public AwayExchangeQuote findExchangeBBOEntry(String exchangeName);
    
    public void setBotrDirty(boolean p_botrDirty);
    
    public String getSessionName();
    
    public ProductKeysStruct getProductKeys();
    
    public int getBotrQuantityBySide(Side side);

    public boolean getShortSaleTriggeredMode();
    public void setShortSaleTriggeredMode(boolean shortSaleTriggeredMode);
    public void updateExchangeIndicator(char exchangeCode, long productKey, char marketIndicator);

}
