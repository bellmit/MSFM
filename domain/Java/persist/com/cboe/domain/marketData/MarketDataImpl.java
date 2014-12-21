package com.cboe.domain.marketData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.cboe.domain.bestQuote.AwayExchangeBestQuoteImpl;
import com.cboe.domain.bestQuote.CurrentMarketImpl;
import com.cboe.domain.bestQuote.NbboBotrImpl;
import com.cboe.domain.optionsLinkage.AllExchangesBBOImpl;
import com.cboe.domain.optionsLinkage.SweepElementImpl;
import com.cboe.domain.util.MarketDataStructBuilder;
import com.cboe.domain.util.PriceFactory;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.idl.cmiConstants.ExchangeIndicatorTypesOperations;
import com.cboe.idl.cmiConstants.Sides;
import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
import com.cboe.idl.cmiMarketData.ExchangeIndicatorStruct;
import com.cboe.idl.cmiMarketData.ExchangeVolumeStruct;
import com.cboe.idl.cmiMarketData.NBBOStruct;
import com.cboe.idl.cmiMarketData.RecapStruct;
import com.cboe.idl.cmiMarketData.TickerStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.marketData.BOStruct;
import com.cboe.idl.quote.ExternalQuoteSideStruct;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.Side;
import com.cboe.interfaces.domain.TradingClass;
import com.cboe.interfaces.domain.TradingClassHome;
import com.cboe.interfaces.domain.TradingProduct;
import com.cboe.interfaces.domain.TradingProductHome;
import com.cboe.interfaces.domain.bestQuote.BestQuote;
import com.cboe.interfaces.domain.bestQuote.CurrentMarket;
import com.cboe.interfaces.domain.linkageClassGate.LinkageClassGate;
import com.cboe.interfaces.domain.linkageClassGate.LinkageClassGateHome;
import com.cboe.interfaces.domain.marketData.AwayExchangeQuote;
import com.cboe.interfaces.domain.marketData.MarketData;
import com.cboe.interfaces.domain.marketData.MarketDataHome;
import com.cboe.interfaces.domain.marketData.MarketDataStructsHolder;
import com.cboe.interfaces.domain.marketData.Recap;
import com.cboe.interfaces.domain.optionsLinkage.AllExchangesBBO;
import com.cboe.interfaces.domain.optionsLinkage.SweepElement;
import com.cboe.interfaces.domain.tradingProperty.AutoLinkDisqualifiedExchanges;
import com.cboe.interfaces.domain.tradingProperty.AutoLinkPreferredTieExchanges;
import com.cboe.interfaces.domain.tradingProperty.MarketDataAwayExchanges;
import com.cboe.util.ExceptionBuilder;
import com.cboe.idl.cmiConstants.CurrentMarketViewTypes;
import com.cboe.interfaces.domain.MarketUpdate;
import com.cboe.interfaces.domain.tradingProperty.MarketDataAwayExchanges;

/**
 * A persistent implementation of <code>MarketData</code>.
 *
 * @author John Wickberg
 */
public class MarketDataImpl extends BObject implements MarketData
{
    private String askSideLocation = "";
    private String bidSideLocation = "";
    private String mdhLocation = "";
    
    private final String sessionName;
    private final String localExchange;
    
    private final int classKey;

    private final BestQuote nbbo;
    private final BestQuote botr;
    
    private final int productKey;
    private final CurrentMarketImpl bestMarket;
    private final CurrentMarketImpl bestLimitMarket;
    private final CurrentMarketImpl bestPublicMarket;
    private final CurrentMarketImpl bestPublicMarketAtTop;
    
    
    
    private final RecapImpl recap;
    private final boolean autoCalcNBBO;
    private final ExchangeVolumeStruct localExchVol;

    private static final CurrentMarketStruct ZERO_CURRENT_MARKET = MarketDataStructBuilder
            .buildCurrentMarketStruct(null);
    private static final ExchangeIndicatorStruct[] EMPTY_EXCHANGE_INDICATORS = new ExchangeIndicatorStruct[0];
    
    private static final ExchangeVolumeStruct[] EMPTY_VOLS = new ExchangeVolumeStruct[0];
    /* cache TradingClass */
    private TradingClass tradingClass = null;
    
    private static final Integer ONE = new Integer(1);    
    //This is initialized for all the 26 alphabets. This offset is calculated
    //for the exchange by chr - 'A'.
    AwayExchangeQuote[] awayMarketQuotesByExchange = new AwayExchangeQuote[58];

    byte[] bidBOTRExchanges = null;
    byte[] askBOTRExchanges = null;
    private final int BOTR_EXCHANGES_SIZE = 59;

    private Object awayExchangeQuoteLock = new Object();
    private volatile boolean botrDirty = false;

    private boolean shortSaleMode = false;
    
    	// the array list holds the exchange conditions currently in memory for the 
    	// product.  Since we don't know how many exchange up front may quote the product
    	// we use an arrayList to initially keep track of them
    ArrayList<ExchangeIndicatorStruct> exchangeIndicatorList = new ArrayList();
    	// the mapper is a way of keeping track of the exchange indicator struct index in
    	// the exchangeIndicatorList.  The mapper will be indexed by the exchange code offset,
    	// the value stored at that index will be the index into the exchangeIndicatorList
    byte[] exchangeIndicatorListMapper = new byte[58];
    	// This always holds the current set of exchange indicators that will be returned by
    	// the getExchangeIndicators method.
    ExchangeIndicatorStruct[] exchangeIndicators = EMPTY_EXCHANGE_INDICATORS; 
    
    
    /////////////////////////////////////////////////////////////////
    //private transient MarketDataStructsHolder mktDataStructsHolder;
    /////////////////////////////////////////////////////////////////
//    private transient CurrentMarketStruct bestMarketHeld;
//    private transient CurrentMarketStruct bestLimitMarketHeld;
//    private transient CurrentMarketStruct bestPublicMarketHeld;
//    private transient CurrentMarketStruct bestPublicMarketAtTopHeld;
//    private transient int productKeyHeld;
    /////////////////////////////////////////////////////////////////
    
    private final ProductKeysStruct prodKeys;
    private int rptClassKey = -1;

    private MarketDataHome marketDataHome;

    private LinkageClassGate classGate;
    private static LinkageClassGateHome linkageClassGateHome;
    
    
    public MarketDataImpl(String p_sessionName, String p_localExchange, int p_productKey,
            int p_classKey, Price closePrice, boolean autoCalcNBBO)
    {
        initializeBOTRExchanges();
        this.sessionName = p_sessionName;
        this.localExchange = p_localExchange;
        //this.localExchVol = new ExchangeVolumeStruct(p_localExchange, 0);
        this.localExchVol = MarketDataStructBuilder.getExchangeVolumeStruct(p_localExchange, 0);
        this.productKey = p_productKey;
        this.classKey = p_classKey;
        this.nbbo = new AwayExchangeBestQuoteImpl(this);
        this.botr = new AwayExchangeBestQuoteImpl(this);
        this.bestMarket = new CurrentMarketImpl();
        this.bestLimitMarket = new CurrentMarketImpl();
        this.bestPublicMarket = new CurrentMarketImpl();
        this.bestPublicMarketAtTop=new CurrentMarketImpl();
        this.recap = new RecapImpl();
        this.recap.create(closePrice);
        this.prodKeys = new ProductKeysStruct(productKey, classKey, (short) 0, 0);
        this.autoCalcNBBO = autoCalcNBBO;

        initiateMarketDataStructsHolder();
        ZERO_CURRENT_MARKET.sessionName = sessionName;
        ZERO_CURRENT_MARKET.productKeys = getProductKeys();
        setReportingClassKey();
    }

    public MarketDataImpl(String p_sessionName, String p_localExchange, int p_productKey,
            int p_classKey, int reportingClassKey, Price closePrice, boolean autoCalcNBBO)
    {
        initializeBOTRExchanges();
        this.sessionName = p_sessionName;
        this.localExchange = p_localExchange;
        //this.localExchVol = new ExchangeVolumeStruct(p_localExchange, 0);
        this.localExchVol = MarketDataStructBuilder.getExchangeVolumeStruct(p_localExchange, 0);
        this.productKey = p_productKey;
        this.classKey = p_classKey;
        this.nbbo = new AwayExchangeBestQuoteImpl(this);
        this.botr = new AwayExchangeBestQuoteImpl(this);
        this.bestMarket = new CurrentMarketImpl();
        this.bestLimitMarket = new CurrentMarketImpl();
        this.bestPublicMarket = new CurrentMarketImpl();
        this.bestPublicMarketAtTop=new CurrentMarketImpl();
        this.recap = new RecapImpl();
        this.recap.create(closePrice);
        this.prodKeys = new ProductKeysStruct(productKey, classKey, (short) 0, 0);
        this.autoCalcNBBO = autoCalcNBBO;

        initiateMarketDataStructsHolder();
        ZERO_CURRENT_MARKET.sessionName = sessionName;
        ZERO_CURRENT_MARKET.productKeys = getProductKeys();
        if(reportingClassKey < 1) {
            Log.information("received invalid reportingClassKey: " + reportingClassKey + " from the caller for prodKey: " + productKey);
        }
        this.rptClassKey = reportingClassKey;
    }
    

    /**
     * 
     */
    private void initializeBOTRExchanges()
    {
        bidBOTRExchanges = new byte[BOTR_EXCHANGES_SIZE];
        for(int i = 0; i < BOTR_EXCHANGES_SIZE; i++)
        {
            bidBOTRExchanges[i] = '\0';
        }
        askBOTRExchanges = new byte[BOTR_EXCHANGES_SIZE];
        for(int i = 0; i < BOTR_EXCHANGES_SIZE; i++)
        {
            askBOTRExchanges[i] = '\0';
        }        
    }

    private void setReportingClassKey()
    {
        try
        {
            if(rptClassKey < 0)
            {
                TradingProductHome tpHome = (TradingProductHome)HomeFactory.getInstance().findHome(TradingProductHome.HOME_NAME);
                TradingProduct tradingProd = tpHome.findByKey(getProductKey()==0 ? -1 : getProductKey());
                rptClassKey = tradingProd.getProductKeys().reportingClass;
            }
        }
        catch (NotFoundException ex)
        {
            // (ignored)
        }
        catch (Exception ex)
        {
            Log.exception(this, "Failed to find trading product for exchange indicator for prod key " + getProductKey() + ".", ex);
        }
    }

    public ProductKeysStruct getProductKeys()
    {
        return prodKeys;
    }

    /**
 * Getter for sessionName.
 */
    public String getSessionName()
{
    return  sessionName;
}

/**
 * Getter for class key.
 */
    public int getClassKey()
{
    return classKey;
}

/**
 * Getter for contingent market best.
 */
public synchronized CurrentMarket getBestMarket()
{
    return bestMarket;
}

/**
 * Getter for non-contingent market best.
 */
public synchronized CurrentMarket getBestLimitMarket()
{
    return bestLimitMarket;
}

public synchronized CurrentMarket getBestPublicMarket()
{
    return bestPublicMarket;
}

public synchronized CurrentMarket getBestPublicMarketAtTop()
{
    return bestPublicMarketAtTop;
}

public boolean isBotrDirty()
{
    return botrDirty;
}

public synchronized void setBotrDirty(boolean p_botrDirty)
{
    botr.setBidDirty(p_botrDirty);
    botr.setAskDirty(p_botrDirty);
    nbbo.setBidDirty(p_botrDirty);
    nbbo.setAskDirty(p_botrDirty);
}


/**
 * Getter for NBBO.
 *
 */
public synchronized BestQuote getNBBO()
{
//        nbboCalcCallTimer.enter();
        if(autoCalcNBBO)
        {
            if(nbbo.isBidDirty())
            {
                calcNBBOSide(Sides.BID);
               nbbo.setBidDirty(false);
            }
            if(nbbo.isAskDirty())
            {
                calcNBBOSide(Sides.ASK);
                nbbo.setAskDirty(false);
            }
        }
//        nbboCalcCallTimer.exit();
    return nbbo;
}
//
//    static final NanoCallDurationTimer nbboCalcCallTimer = NanoCallDurationTimer
//            .createAndInitializeLoggingCallTimer("NBBO-calc", null, Integer.parseInt(System
//                    .getProperty("callTimerInterval.nbbo", "60000")));

/**
 * Getter for BOTR.
 */
    public synchronized BestQuote getBOTR()
    {
        //Loop through the botr exchanges array
        //for each of it find the awayexchange object
        //create the awayexchangebestquote object from above
        if(botr.isAskDirty() || botr.isBidDirty())
        {
            int bidExchangeCount = 0;
            int askExchangeCount = 0;
            for(int i = 0; i < bidBOTRExchanges.length && bidBOTRExchanges[i] != '\0'; i++)
            {
                bidExchangeCount++;
            }

            for(int i = 0; i < askBOTRExchanges.length && askBOTRExchanges[i] != '\0'; i++)
            {
                askExchangeCount++;
            }
            
            ExchangeVolumeStruct[] updatedBidSideExchangeVolumeStruct = new ExchangeVolumeStruct[bidExchangeCount];
            ExchangeVolumeStruct[] updatedAskSideExchangeVolumeStruct = new ExchangeVolumeStruct[askExchangeCount];

            if(botr.isBidDirty() && bidExchangeCount > 0)
            {
                Price bidPrice = null;
                for(int i = 0; i < bidBOTRExchanges.length && bidBOTRExchanges[i] != '\0'; i++)
                {
                    char bestExchangeI = (char)bidBOTRExchanges[i];
                    int exchangeIndexI = bestExchangeI - 'A';
                    AwayExchangeQuote awayExchangeQuote = awayMarketQuotesByExchange[exchangeIndexI];
                    ExchangeVolumeStruct exchangeVolumeStruct = null;
                    if(awayExchangeQuote != null)
                    {
                        bidPrice = awayExchangeQuote.getBidPrice();
                        exchangeVolumeStruct = awayExchangeQuote.getExchangeBidVolumeStruct();
                    }
                    
                    updatedBidSideExchangeVolumeStruct[i] = exchangeVolumeStruct;
                }

                botr.updateBidSide(bidPrice, updatedBidSideExchangeVolumeStruct);
                botr.setBidDirty(false);

                if(botr.getBidExchangeVolumes() == null || botr.getBidExchangeVolumes().length <= 0 || 
                   botr.getBidPrice() == null || bidBOTRExchanges[0] == '\0')
                {
                    bidPrice = AwayExchangeQuoteImpl.NO_PRICE;
                    updatedBidSideExchangeVolumeStruct = EMPTY_VOLS;
                }   
            }
            
            if(botr.isAskDirty() && askExchangeCount > 0)
            {
                Price askPrice = null;
                for(int i = 0; i < askBOTRExchanges.length && askBOTRExchanges[i] != '\0'; i++)
                {
                    char bestExchangeI = (char)askBOTRExchanges[i];
                    int exchangeIndexI = bestExchangeI - 'A';
                    AwayExchangeQuote awayExchangeQuote = awayMarketQuotesByExchange[exchangeIndexI];
                    ExchangeVolumeStruct exchangeVolumeStruct = null;
                    if(awayExchangeQuote != null)
                    {
                        askPrice = awayExchangeQuote.getAskPrice();
                        exchangeVolumeStruct = awayExchangeQuote.getExchangeAskVolumeStruct();
                    }
                    
                    updatedAskSideExchangeVolumeStruct[i] = exchangeVolumeStruct;
                }
                
                botr.updateAskSide(askPrice, updatedAskSideExchangeVolumeStruct);
                botr.setAskDirty(false);

                if(botr.getAskExchangeVolumes() == null || botr.getAskExchangeVolumes().length <= 0 || 
                   botr.getAskPrice() == null || askBOTRExchanges[0] == '\0')
                {
                  askPrice = AwayExchangeQuoteImpl.NO_PRICE;
                  updatedAskSideExchangeVolumeStruct = EMPTY_VOLS;
                }                
            }
            
            return botr;
        }
        else
        {
            return botr;
        }

    }
    
    public synchronized int getBotrQuantityBySide(Side side)
    {
       return botr.getBotrQuantityBySide(side);
    }
    
public AllExchangesBBO getAllExchangesBBO(char quoteSide)
{
    AllExchangesBBO al = null;
    ArrayList <SweepElement> unFilteredSweepElements = new ArrayList();
       
    /* Get the Preferred Tie Exchange data*/
    Map<String, Integer> preferredTieExchanges = getPreferredTieExchanges();

    ArrayList <NbboBotrImpl> allExchangesBBOCopy = new ArrayList();

    for(int i = 0; i < awayMarketQuotesByExchange.length; i++)
    {
        if(awayMarketQuotesByExchange[i] != null)
        {
            SweepElement thisSweepElement = toSweepElement(quoteSide, awayMarketQuotesByExchange[i], preferredTieExchanges);
            if(thisSweepElement != null)
            {
                unFilteredSweepElements.add(thisSweepElement);
            }
        }
    }

    if(unFilteredSweepElements.size() <= 0)
    {
        return null;
    }

    /*
    // OK, let's go through the disqualified exchanges property and seperate
    // out the qualified exch and disqualified exch.
    if(quoteSide == 'X' || quoteSide == 'Y')  // small hook, remove later
        al = filterExchangesWithNewFormat(unFilteredSweepElements);
    else
        al = filterExchanges(unFilteredSweepElements);
    */
    al = filterExchangesWithNewFormat(unFilteredSweepElements);
    return al;
}

/**
 * Get the PreferredTieExchanges data from Trading Property and return it.
 * 
 * @return
 */
private Map<String, Integer> getPreferredTieExchanges()
{
    Map<String, Integer> preferredTieExchanges = new HashMap<String, Integer>();
    try
    {
        AutoLinkPreferredTieExchanges[] preferredTieExchangeList = getTradingClass(classKey).getNewLinkagePreferredTieExchangeList();
        if(preferredTieExchangeList != null)
        {
            for (AutoLinkPreferredTieExchanges autoLinkPreferredTieExchange : preferredTieExchangeList)
            {
                preferredTieExchanges.put(autoLinkPreferredTieExchange.getAutoLinkTieExchange(), new Integer(autoLinkPreferredTieExchange.getAutoLinkTieExchangePreferredSeq()));
            }
        }
    }
    catch (DataValidationException exp)
    {
        Log.exception(this, exp);
    }
    
    return preferredTieExchanges;
}

/*
 * Filter out disqualified exchanges based on trading property.
 */

private AllExchangesBBO filterExchangesWithNewFormat(List <SweepElement> unFilteredSweepElements)
{
    AllExchangesBBOImpl al = new AllExchangesBBOImpl();
    ArrayList <SweepElement> qualifiedList = new ArrayList <SweepElement> ();
    ArrayList <SweepElement> disqualifiedList = new ArrayList <SweepElement> ();
    
    
    try {
        
        List <String> dqList = 
            getTradingClass(classKey).getAutoLinkOnlyDisqualifiedExchangesList();
    

        ExchangeIndicatorStruct [] ad = getExchangeIndicators();
    
        if((ad == null || ad.length == 0) && (dqList == null || dqList.size() == 0)) {
            // no indicator present, assume everything is qualified.
            al.setQualifiedExchangesBBO(unFilteredSweepElements);
            al.setDisqualifiedExchangesBBO(null);
        } else if(ad == null || ad.length == 0) {
            Iterator iterator = unFilteredSweepElements.iterator();
            while(iterator.hasNext()) {
                SweepElement sweepElement = (SweepElement)iterator.next();
                boolean isQualified = true;
                if(dqList.contains(sweepElement.getExchangeName())) {
                    isQualified = false;
                }   
                if(isQualified) {
                    qualifiedList.add(sweepElement);
                } else {
                    disqualifiedList.add(sweepElement);
                }
            }
            al.setDisqualifiedExchangesBBO(disqualifiedList);
            al.setQualifiedExchangesBBO(qualifiedList);
            
        } else {
            Iterator iterator = unFilteredSweepElements.iterator();
            while(iterator.hasNext()) {
                SweepElement sweepElement = (SweepElement)iterator.next();
                boolean isQualified = true;
                
                // check trading property.
                if(dqList != null && dqList.size() > 0) {
                    if(dqList.contains(sweepElement.getExchangeName())) {
                        isQualified = false;
                    } else {
                        // not in trading property, now check ex indicator.
                        for(int i = 0; i < ad.length; i++) {
                            if(sweepElement.getExchangeName().equalsIgnoreCase(ad[i].exchange)) {
                                if(ad[i].marketCondition != ExchangeIndicatorTypesOperations.CLEAR) {
                                    isQualified = false;
                                } 
                                break;
                            }
                        }
                    }
                } else {
                    // trading property has nothing on this class.
                    for(int i = 0; i < ad.length; i++) {
                        if(sweepElement.getExchangeName().equalsIgnoreCase(ad[i].exchange)) {
                            if(ad[i].marketCondition != ExchangeIndicatorTypesOperations.CLEAR) {
                                isQualified = false;
                            } 
                            break;
                        }
                    }
                }
                    
                if(isQualified) {
                    qualifiedList.add(sweepElement);
                } else {
                    disqualifiedList.add(sweepElement);
                }
            }
                       
            al.setDisqualifiedExchangesBBO(disqualifiedList);
            al.setQualifiedExchangesBBO(qualifiedList);
        }       
    } catch (DataValidationException dx) {
        Log.exception(this, dx);
        return null;
    }
    return al;
}

private AllExchangesBBO filterExchanges(List <SweepElement> unFilteredSweepElements)
{
    AllExchangesBBOImpl al = new AllExchangesBBOImpl();
    ArrayList <SweepElement> qualifiedList = new ArrayList <SweepElement> ();
    ArrayList <SweepElement> disqualifiedList = new ArrayList <SweepElement> ();
    
    try {
    
        AutoLinkDisqualifiedExchanges [] ad = 
            getTradingClass(classKey).getAutoLinkDisqualifiedExchangesList();
        
        // if there is none, that means no exchanges are disqualified.
        if(ad.length == 0) {
            al.setDisqualifiedExchangesBBO(null);
            al.setQualifiedExchangesBBO(unFilteredSweepElements);
        } else {
            Iterator iterator = unFilteredSweepElements.iterator();
            while(iterator.hasNext()) {
                SweepElement sweepElement = (SweepElement)iterator.next();
                boolean inQualifiedList = true;
                for(int i = 0; i < ad.length; i++) {
                    if(sweepElement.getExchangeName().
                            equalsIgnoreCase(ad[i].getAutoLinkDisqualifiedExchanges())) {
                        if(ad[i].isAutoLinkDisqualifiedExchangesFlag()) {
                            disqualifiedList.add(sweepElement);
                            inQualifiedList = false;
                        }
                        break;
                    }
                }
                if(inQualifiedList) {
                    qualifiedList.add(sweepElement);
                }
            }
            al.setDisqualifiedExchangesBBO(disqualifiedList);
            al.setQualifiedExchangesBBO(qualifiedList);
        }
    } catch (DataValidationException dx) {
        Log.exception(this, dx);
        return null;
    }
    return al;
}

    
    private TradingClass getTradingClass(int ck) throws DataValidationException
    {
        if(tradingClass != null)
            return tradingClass;
        
        TradingClassHome tradingClassHome = null;
        try
        {
            tradingClassHome = (TradingClassHome) HomeFactory.getInstance().findHome(TradingClassHome.HOME_NAME);
            tradingClass = tradingClassHome.findByKey(classKey);
        }
        catch (CBOELoggableException e)
        {
            Log.exception(this, e);
        }
        catch (NotFoundException e)
        {
            Log.exception("this.getClass().getName()", e);
            throw ExceptionBuilder.dataValidationException("Trading class could not be found.", 0);
        }
        return tradingClass;
    }
    
/**
 * Getter for product key.
 */
    public int getProductKey()
{
    return productKey;
}

/**
 * Getter for recap.
 */
public synchronized Recap getRecap()
{
    return recap;
}

/**
 * Sets home for self and dependent objects.
 *
 * @author John Wickberg
 */
public synchronized void setBOHome(BOHome newHome)
{
    super.setBOHome(newHome);
    BObject dependent;
    dependent = ((BObject) getBestMarket());
    if (dependent != null)
    {
        newHome.addToContainer(dependent);
    }
    dependent = ((BObject) getBestLimitMarket());
    if (dependent != null)
    {
        newHome.addToContainer(dependent);
    }
    dependent = ((BObject) getBestPublicMarket());
    if (dependent != null)
    {
        newHome.addToContainer(dependent);
    }
    dependent = ((BObject) getRecap());
    if (dependent != null)
    {
        newHome.addToContainer(dependent);
    }
    if (marketDataHome == null ) {
        marketDataHome = (MarketDataHome)getBOHome();
    }
}

///**
//     * access market best structs for contingent market.
// */
//public synchronized MarketDataStructsHolder toCurrentMarketStructs()
//{
//    return getMarketDataStructsHolder();
//}



// commented out because not currently in use
///**
//     * Creates market best struct. Product keys is not completely filled in, don't want to make call
//     * to product service from the domain layer.
// */
//private CurrentMarketStruct toCurrentMarketStruct(CurrentMarket market)
//{
//    CurrentMarketStruct struct = market.toStruct();
//    struct.productKeys = getProductKeys();
//    struct.sessionName = getSessionName();
//    return struct;
//}

/**
 * Converts NBBO of this market data to a CORBA struct.
 *
 * @author John Wickberg
 */
    public NBBOStruct toNBBOStruct()
    {
        NBBOStruct struct = getNBBO().toNBBOStruct();
        struct.productKeys = getProductKeys();
        struct.sessionName = getSessionName();
        return struct;
    }

    /**
     * Converts NBBO of this market data to a CORBA struct.
     *
     */
    public NBBOStruct toBOTRStruct()
    {
        NBBOStruct struct = getBOTR().toNBBOStruct();
        struct.productKeys = getProductKeys();
        struct.sessionName = getSessionName();
        return struct;
    }

/**
 * Converts Best CBOE Market  of this market data to a CORBA struct.
 *
 */
public synchronized BOStruct getCBOEMarket()
{
    BOStruct struct = new BOStruct();
    CurrentMarketStruct cmStruct = getBestMarket().toStruct();
    
    struct.askPrice = cmStruct.askPrice;
    struct.bidPrice = cmStruct.bidPrice;
    struct.askVolume = getBestMarket().getAskSize();
    struct.bidVolume = getBestMarket().getBidSize();
    
    return struct;
}

/**
 * Converts Best CBOE Market  of this market data to a CORBA struct.
 *
 */
public synchronized BOStruct getTopOfBook()
{
    BOStruct struct = new BOStruct();
    CurrentMarketStruct cmStruct = this.getBestPublicMarket().toStruct();
    
    struct.askPrice = cmStruct.askPrice;
    struct.bidPrice = cmStruct.bidPrice;
    struct.askVolume = getBestMarket().getAskSize();
    struct.bidVolume = getBestMarket().getBidSize();
    
    return struct;
}

/**
     * Creates underlying recap struct. Product keys is not completely filled in, don't want to make
     * call to product service from the domain layer.
 */
public synchronized RecapStruct toRecapStruct()
{
    RecapStruct struct = new RecapStruct();
    struct = getRecap().toStruct();
    struct.productKeys = getProductKeys();
    struct.sessionName = getSessionName();
        if (getBestMarket() != null && getBestMarket().getAskPrice() != null)
        {
            struct.askPrice = getBestMarket().getAskPrice().toStruct();
    }
        if (getBestMarket() != null && getBestMarket().getBidPrice() != null)
        {
            struct.bidPrice = getBestMarket().getBidPrice().toStruct();
    }
        if (getBestMarket() != null)
        {
            struct.askTime = getBestMarket().toStruct().sentTime;
            struct.bidTime = getBestMarket().toStruct().sentTime;
    }
    return struct;
}

/**
 * Updates contingent market best.
 */
public synchronized void updateCurrentMarkets(
        CurrentMarketStruct bestMarketStruct,
        CurrentMarketStruct bestLimitMarketStruct,
        CurrentMarketStruct bestPublicMarketStruct,
        CurrentMarketStruct bestPublicMarketAtTopStruct)
{
    //setMarketDataStructsHolder(currentMarkets);

    getBestMarket().update(bestMarketStruct);
    getBestLimitMarket().update(bestLimitMarketStruct);
    getBestPublicMarket().update(bestPublicMarketStruct);
    getBestPublicMarketAtTop().update(bestPublicMarketAtTopStruct);
    nbbo.setBidDirty(true);
    nbbo.setAskDirty(true);
  }

public synchronized void updateCurrentMarkets(
        MarketUpdate update)
{
    getBestMarket().update(update, CurrentMarketViewTypes.BEST_PRICE);
    getBestLimitMarket().update(update, CurrentMarketViewTypes.BEST_LIMIT_PRICE);
    getBestPublicMarket().update(update, CurrentMarketViewTypes.BEST_PUBLIC_ORDER_PRICE);
    if (update.isPublicPriceBest())
        getBestPublicMarketAtTop().update(update, CurrentMarketViewTypes.BEST_PUBLIC_ORDER_PRICE); 
    setBotrDirty(true);
  }

/**
 * Updates contingent market best.
 */
public synchronized void updateCurrentMarkets(
        MarketDataStructsHolder currentMarkets)
{
    //setMarketDataStructsHolder(currentMarkets);
    getBestMarket().update(currentMarkets.getBestMarket());
    getBestLimitMarket().update(currentMarkets.getBestLimitMarket());
    getBestPublicMarket().update(currentMarkets.getBestPublicMarket());
    getBestPublicMarketAtTop().update(currentMarkets.getBestPublicMarketAtTop());
    nbbo.setBidDirty(true);
    nbbo.setAskDirty(true);
  }



    private synchronized void calcNBBOSide(final char p_side)
    {
        final boolean isBid = p_side == Sides.BID;
        final Price localPrice;
        final Price botrPRice;
        if (isBid)
        {
            localPrice = getBestLimitMarket().getBidPrice();
            botrPRice = getBOTR().getBidPrice();
        }
        else
        {
            localPrice = getBestLimitMarket().getAskPrice();
            botrPRice = getBOTR().getAskPrice();
        }
        final Price nbboPrice;
        final ExchangeVolumeStruct[] nbboVols;
        ExchangeVolumeStruct extraVol = null;
        switch (compareTo(p_side, localPrice, botrPRice))
        {
            case -2: // no prices
                nbboPrice = PriceFactory.getNoPrice();
                nbboVols = EMPTY_VOLS;
                break;
            case -1: // BOTR is better
                nbboPrice = botrPRice;
                nbboVols = isBid
                        ? getBOTR().getBidExchangeVolumes()
                        : getBOTR().getAskExchangeVolumes();
                break;
            case 0: // local tied with BOTR
                nbboPrice = botrPRice; // (local == botr, it doesn't matter which price obj ref is
                                       // used)
                ExchangeVolumeStruct[] botrVols = isBid
                        ? getBOTR().getBidExchangeVolumes()
                        : getBOTR().getAskExchangeVolumes();
                localExchVol.volume = isBid
                        ? getBestLimitMarket().getBidSize()
                        : getBestLimitMarket().getAskSize();
                nbboVols = botrVols;
                extraVol = localExchVol;
                break;
            default: // (case 1: local is better)
                nbboPrice = localPrice;
                nbboVols = EMPTY_VOLS;
                localExchVol.volume = isBid
                        ? getBestLimitMarket().getBidSize()
                        : getBestLimitMarket().getAskSize();
                extraVol = localExchVol;
                break;
        }
        if (isBid)
            nbbo.updateBidSide(nbboPrice, nbboVols, extraVol);
        else
            nbbo.updateAskSide(nbboPrice, nbboVols, extraVol);
    }

     
    private ExchangeVolumeStruct[] addVol(final ExchangeVolumeStruct[] p_vols,
            final ExchangeVolumeStruct p_addExchVol)
    {
        final ExchangeVolumeStruct[] newVols = new ExchangeVolumeStruct[p_vols.length + 1];
        System.arraycopy(p_vols, 0, newVols, 0, p_vols.length);
        newVols[p_vols.length] = p_addExchVol;
        return newVols;
    }

    /**
     * @return -2 indicates both prices are effectively absent. -1 means "local is better", 0 means
     * "equality", 1 means "BOTR is better"
     */
    private int compareTo(final char p_side, final Price p_local, final Price p_botr)
    {
        if (p_local.isNoPrice() || p_side == Sides.BID && p_local.toLong() == 0L)
        {
            return p_botr.isNoPrice()
                    ? -2
                    : 0; // -2 indicates 'no prices at all' (including bid side of a 0-bid quote)
        }
        if (p_botr.isNoPrice())
        {
            return 1; // local is better (any value is better than no value)
        }

        final long local = p_local.toLong();
        final long botr = p_botr.toLong();
        // BID:
        if ((p_side == Sides.BID && local < botr) || p_side == Sides.ASK && local > botr)
        {
            return -1; // botr is better
        }
        return p_local.equals(p_botr)
                ? 0
                : 1; // otherwise, local is better
    }

/**
 * Updates NBBO.
 *
 * @author John Wickberg
 */
    public void updateNBBO(NBBOStruct nbboUpdate)
{
    getNBBO().update(nbboUpdate);
}

/**
 * Updates BOTR.
 */
    public void updateBOTR(ExternalQuoteSideStruct botrSide)
    {
        getBOTR().update(botrSide);
        if (botrSide.side == Sides.BID)
        {
            botr.setBidDirty(true);
            nbbo.setBidDirty(true);
        }
        else
        {
            botr.setAskDirty(true);
            nbbo.setAskDirty(true);
        }
    }

/**
 * Updates underlying recap.
 */
public void updateRecap(RecapStruct recapUpdate)
{
    getRecap().update(recapUpdate);
}

/**
 * Updates recap.
 */
public void updateRecap(TickerStruct tickerUpdate)
{
    getRecap().update(tickerUpdate);
}

    /**
     * Everytime when current market is updated, the MarketDataStructsHolder will have be updated.
     * Note: the private data member MarketDataStructsHolder is replaced by its consisting fields
     * --Frank Apr 24 2009
     */
    private void setMarketDataStructsHolder(MarketDataStructsHolder aHolder)
    {
        //mktDataStructsHolder = aHolder;
        getBestMarket().update(aHolder.getBestMarket());
        getBestLimitMarket().update(aHolder.getBestLimitMarket());
        getBestPublicMarket().update(aHolder.getBestPublicMarket());
        getBestPublicMarketAtTop().update(aHolder.getBestPublicMarketAtTop());
        
    }

    /**
     * return the mktDataStructsHolder Note: everytime when current market is updated. The holder
     * will be updated. So mktDataStructs will always hold onto the CurrentMarketStructs
     * representing the current values
     */

    
 //   private void initializeAllExchangesBBO() {
//        allExchangesBBO = new ArrayList<NbboBotrImpl>();
//    }
   
    /**
     * initiate the marketDataStructsHolder
     */
    private void initiateMarketDataStructsHolder()
    {
//        mktDataStructsHolder = new MarketDataStructsHolderImpl(getProductKey(),
//                getStructForBestMarket(), getStructForBestLimitMarket(),
//                getStructForBestPublicMarket(), getStructForBestPublicMarket());
 
        
        getBestMarket().setProductKeys(this.prodKeys);
        getBestMarket().setSessionName(this.sessionName);

        getBestLimitMarket().setProductKeys(this.prodKeys);
        getBestLimitMarket().setSessionName(this.sessionName);
        
        getBestPublicMarket().setProductKeys(this.prodKeys);
        getBestPublicMarket().setSessionName(this.sessionName);
        
        getBestPublicMarketAtTop().setProductKeys(this.prodKeys);
        getBestPublicMarketAtTop().setSessionName(this.sessionName);
        
  
    }

    /**
     * this method recalculate the CurrentMarketStruct from CurrentMarketImpl representing the
     * BestMarket. This is different from public synchronized method toBestMarket, which simply
     * return the cached values.
     */
    private CurrentMarketStruct getStructForBestMarket()
    {
        CurrentMarketStruct aStruct = getBestMarket().toStruct();
        aStruct.sessionName = getSessionName();
        aStruct.productKeys = getProductKeys();
        return aStruct;
    }

    /**
     * this method recalculate the CurrentMarketStruct from CurrentMarketImpl representing the
     * BestLimitMarket. This is different from public synchronized method toBestLimitMarket, which
     * simply return the cached values.
     */
    private CurrentMarketStruct getStructForBestLimitMarket()
    {
        CurrentMarketStruct aStruct = getBestLimitMarket().toStruct();
        aStruct.sessionName = getSessionName();
        aStruct.productKeys = getProductKeys();
        return aStruct;
    }

    /**
     * this method recalculate the CurrentMarketStruct from CurrentMarketImpl representing the
     * BestPublicMarket. This is different from public synchronized method toBestPublicMarket, which
     * simply return the cached values.
     */
    private CurrentMarketStruct getStructForBestPublicMarket()
    {
        CurrentMarketStruct aStruct = getBestPublicMarket().toStruct();
        aStruct.sessionName = getSessionName();
        aStruct.productKeys = getProductKeys();
        return aStruct;
    }

    /**
     * return a CurrentMarketStruct which represents BestMarket
     */
    public synchronized CurrentMarketStruct toBestMarket()
    {
        return bestMarket.toStruct();
    }

    /**
     * return a CurrentMarketStruct which represents BestLimitMarket
     */
    public synchronized CurrentMarketStruct toBestLimitMarket()
    {
        return bestLimitMarket.toStruct();
    }

    /**
     * return a CurrentMarketStruct which represents BestPublicMarket
     */
    public synchronized CurrentMarketStruct toBestPublicMarket()
    {
        CurrentMarketStruct bestPubMarket = bestPublicMarket.toStruct();
        if (bestPubMarket == null)
        {
            return ZERO_CURRENT_MARKET;
        }
        else
        {
            if (bestPubMarket.sessionName == null)
            {
                  bestPubMarket.sessionName = getSessionName(); 
              }
            if (bestPubMarket.productKeys == null)
            {
                  bestPubMarket.productKeys = getProductKeys();
              }
              return bestPubMarket;
        }
    }

    /**
     * change the inNBBO indicator for current market.
     *
     * Note:
     *
     * 1. The inNBBO indicator of current market is calculated based on BestLimitMarket which is the
     * data we publish to the outside world. 2. The inNBBO indicator of bestPublicMarket will be
     * ignored, because a calculation is required, but it is not used anywhere.
     */
    public synchronized void setBidInNBBOForCurrentMarket(boolean isInNBBO)
    {
        getBestMarket().setBidInNBBO(isInNBBO);
        getBestLimitMarket().setBidInNBBO(isInNBBO);
        if(bestPublicMarket!=null)
            bestPublicMarket.setBidInNBBO(isInNBBO);
        if(bestPublicMarketAtTop!=null)
            bestPublicMarketAtTop.setBidInNBBO(isInNBBO);
    }

    public synchronized void setAskInNBBOForCurrentMarket(boolean isInNBBO)
    {
        getBestMarket().setAskInNBBO(isInNBBO);
        getBestLimitMarket().setAskInNBBO(isInNBBO);
        if(bestPublicMarket!=null)
            bestPublicMarket.setAskInNBBO(isInNBBO);
        if(bestPublicMarketAtTop!=null)
            bestPublicMarketAtTop.setAskInNBBO(isInNBBO);
    }

    public void setExchangeIndicators(ExchangeIndicatorStruct[] indicators)
    {

        UnsupportedOperationException usoe = new UnsupportedOperationException(
                "This method is not supported, cannot be called for NullBroker");
        Log.exception(this, "Not supported Method", usoe);
        throw usoe;
    }


    /**
     * Return the cached LinkageClassGateHome
     * 
     * @return LinkageClassGateHome
     * @author Nikhil Patel
     */
    public LinkageClassGateHome getLinkageClassGateHome()
    {
        if (linkageClassGateHome == null)
        {
            try
            {
                linkageClassGateHome = (LinkageClassGateHome) HomeFactory.getInstance().findHome(
                        LinkageClassGateHome.HOME_NAME);
            }
            catch (Exception e)
            {
                Log.exception("Product State Service is Unable to find LinkageClassGateHome", e);
            }
        }
        return linkageClassGateHome;
    }

    /**
     * Return the cached LinkageClassGate
     * 
     * @return LinkageClassGate
     * @author Nikhil Patel
      */
    public LinkageClassGate getLinkageClassGate()
    {
        if (classGate == null)
        {
            classGate = getLinkageClassGateHome().getGate(getClassKey());
        }
        return classGate;
    }

    public String getAskSideLocation()
    {
        return askSideLocation;
    }

    public String getBidSideLocation()
    {
        return bidSideLocation;
    }

    public void setAskSideLocation(String location)
    {
        askSideLocation = location;
    }

    public void setBidSideLocation(String location)
    {
        bidSideLocation = location;
    }
    
    public void setMDHLocation(String location)
    {
        mdhLocation = location;
    }
    
    public String getMDHLocation()
    {
        return mdhLocation;
    }

    
    /**
     * Updates the Away Exchange quote. It checks if the quote is present or not. If not present and is
     * received first time then it will create and store in the array at exchange's offset else it will
     * update the values. 
     * @param exchangeCode
     * @param bidPrice
     * @param bidVolume
     * @param askPrice
     * @param askVolume
     * @param isPartOfBOTR
     */
    public synchronized AwayExchangeQuote addOrUpdateQuoteForExchange(char exchangeCode, Price bidPrice, int bidVolume,
                                                         Price askPrice, int askVolume, char marketIndicator)
    {
        AwayExchangeQuote awayExchangequote = null;
        if(Character.isUpperCase(exchangeCode))
        {
            int offSet = exchangeCode - 'A';
            
            awayExchangequote = awayMarketQuotesByExchange[offSet];
            if(awayExchangequote == null) //First time Quote
            {
                awayExchangequote = new AwayExchangeQuoteImpl(exchangeCode, bidPrice, bidVolume,
                                                          askPrice, askVolume, marketIndicator);
                awayMarketQuotesByExchange[offSet] = awayExchangequote;

                	// maintain the list of away exchange indicators
                ExchangeIndicatorStruct exchangeStruct = new ExchangeIndicatorStruct();
                exchangeStruct.exchange             = awayExchangequote.getExchangeName();
                exchangeStruct.marketCondition		= (short)awayExchangequote.getMarketIndicator();
                exchangeIndicatorList.add(exchangeStruct); 
                exchangeIndicatorListMapper[offSet] = (byte)(exchangeIndicatorList.size() - 1);
                exchangeIndicators = new ExchangeIndicatorStruct[exchangeIndicatorList.size()];
                exchangeIndicatorList.toArray(exchangeIndicators);
            }
            else
            {
                if(bidPrice != null)
                {
                    awayExchangequote.setBidPrice(bidPrice);
                }
                awayExchangequote.setBidVolume(bidVolume);
                
                if(askPrice != null)
                {
                    awayExchangequote.setAskPrice(askPrice);
                }
                awayExchangequote.setAskVolume(askVolume);
                
                awayExchangequote.setMarketIndicator(marketIndicator);	// set the new indicator
                	// update the exchange indicator in the array list of all possible exchanges
                ExchangeIndicatorStruct struct = exchangeIndicatorList.get(exchangeIndicatorListMapper[offSet]);
                struct.marketCondition = (short)marketIndicator;
                	// update the exchange indicators array that is returned by the getExchangeIndicators method
                exchangeIndicators[exchangeIndicatorListMapper[offSet]].marketCondition = (short)marketIndicator;
            }
        }
        return awayExchangequote;
    }

    public synchronized  void updateExchangeIndicator(char exchangeCode, long productKey, char marketIndicator)
    {
    	AwayExchangeQuote awayExchangequote = null;
    	if(Character.isUpperCase(exchangeCode))
    	{
    		int offSet = exchangeCode - 'A';

    			// update the market indicator stored with the away exchange quote
    		awayExchangequote = awayMarketQuotesByExchange[offSet];
    		if(awayExchangequote != null) 
    			awayExchangequote.setMarketIndicator(marketIndicator);
    		
    			// update the market indicator stored in the array list of all possible exchanges
    		try
    		{
    			ExchangeIndicatorStruct struct = exchangeIndicatorList.get(exchangeIndicatorListMapper[offSet]);
    			if (struct != null)
    			{
    				struct.marketCondition = (short)marketIndicator;
            			// update the exchange indicators array that is returned by the getExchangeIndicators method 
    				exchangeIndicators[exchangeIndicatorListMapper[offSet]].marketCondition = (short)marketIndicator;
    			}
    		}
        	catch(Exception ex){
                Log.exception(this, "Unable to update indicator for exchange and key " + exchangeCode + ":" + productKey + " no valid away exchange quote", ex);
        	}
    	}
    }


    public synchronized AwayExchangeQuote updatePriceForSide(char botrExchangeCode, char side, Price updatePrice)
    {
        int exchangeIndex = botrExchangeCode - 'A';
        AwayExchangeQuote updateExchange = awayMarketQuotesByExchange[exchangeIndex];
        if(updateExchange == null) {
            // it doesn't exist, let's just create it.
            updateExchange = new AwayExchangeQuoteImpl(botrExchangeCode);
            awayMarketQuotesByExchange[exchangeIndex] = updateExchange;
        }
    
        if(side == Sides.ASK || side == Sides.SELL)
        {
            updateExchange.setAskPrice(updatePrice);
        }
        else
        {
            updateExchange.setBidPrice(updatePrice);
        }
        
        return updateExchange;
    }

    
    public synchronized AwayExchangeQuote updateExchangeQuoteBySide(char botrExchangeCode, char side, 
                                                                    Price updatePrice, int updateVolume)
    {
        int exchangeIndex = botrExchangeCode - 'A';
        AwayExchangeQuote updateExchange = awayMarketQuotesByExchange[exchangeIndex];
        if(updateExchange == null) {
            // it doesn't exist, let's just create it.
            updateExchange = new AwayExchangeQuoteImpl(botrExchangeCode);
            awayMarketQuotesByExchange[exchangeIndex] = updateExchange;
        }
    
        if(side == Sides.ASK || side == Sides.SELL)
        {
            updateExchange.setAskPrice(updatePrice);
            updateExchange.setAskVolume(updateVolume);
        }
        else
        {
            updateExchange.setBidPrice(updatePrice);
            updateExchange.setBidVolume(updateVolume);
        }
        
        return updateExchange;
    }

    
    public byte[] getBidBOTRExchanges()
    {
        return bidBOTRExchanges;
    }

    public void setBidBOTRExchanges(byte[] p_bidBOTRExchanges, int numberOfBidBOTRExchanges)
    {
        for(int i = 0; i < numberOfBidBOTRExchanges; i++)
        {
            int index = ((char) p_bidBOTRExchanges[i]) - 'A';
            if(awayMarketQuotesByExchange[index] != null)
            {
                bidBOTRExchanges[i] = p_bidBOTRExchanges[i];
            }
            else
            {
                if(Log.isDebugOn())
                {
                    Log.alarm("received invalid BID BOTR Exchange Code (has no valid Quote associated with it). For exchange Code: " + ((char) p_bidBOTRExchanges[i]));
                }
            }
        }
        bidBOTRExchanges[numberOfBidBOTRExchanges] = '\0';
    }

    

    public byte[] getAskBOTRExchanges()
    {
        return askBOTRExchanges;
    }

    public void setAskBOTRExchanges(byte[] p_askBOTRExchanges, int numberOfAskBOTRExchanges)
    {
        for(int i = 0; i < numberOfAskBOTRExchanges; i++)
        {
            int index = ((char) p_askBOTRExchanges[i]) - 'A';
            if(awayMarketQuotesByExchange[index] != null)
            {
                askBOTRExchanges[i] = p_askBOTRExchanges[i];
            }
            else
            {
                if(Log.isDebugOn())
                {
                    Log.alarm("received invalid ASK BOTR Exchange Code (has no valid Quote associated with it). For exchange Code: " + ((char) p_askBOTRExchanges[i]));
                }
            }
        }
        askBOTRExchanges[numberOfAskBOTRExchanges] = '\0';
    }


    /**
     * 
     * @param quoteSide
     * @param awayMarketQuotesForExchange
     * @param preferredTieExchanges
     * @return
     */
    public synchronized SweepElement toSweepElement(char quoteSide, AwayExchangeQuote awayMarketQuotesForExchange,
                                                    Map<String, Integer> preferredTieExchanges)
    {
        SweepElementImpl returnedSE = null;
        if(quoteSide == com.cboe.idl.cmiConstants.Sides.BID || 
                quoteSide == com.cboe.idl.cmiConstants.Sides.BUY ||
                quoteSide == 'X') // small hook, remove later
        {           
            Integer preferenceOrder = preferredTieExchanges.get(awayMarketQuotesForExchange.getExchangeName());
            if(preferenceOrder == null)
            {
                preferenceOrder = ONE;
            }

            returnedSE = new SweepElementImpl(awayMarketQuotesForExchange.getExchangeName(), 
                                              awayMarketQuotesForExchange.getExchangeName(),
                                              preferenceOrder.intValue(),
                                              awayMarketQuotesForExchange.getBidPrice(),
                                              awayMarketQuotesForExchange.getBidVolume(),
                                              false, 
                                              false, 
                                              true);    
        } else {
            // Ask side
            Integer preferenceOrder = preferredTieExchanges.get(awayMarketQuotesForExchange.getExchangeName());
            if(preferenceOrder == null)
            {
                preferenceOrder = 1;
            }
            
            returnedSE = new SweepElementImpl(awayMarketQuotesForExchange.getExchangeName(), 
                                              awayMarketQuotesForExchange.getExchangeName(),
                                              preferenceOrder.intValue(), 
                                              awayMarketQuotesForExchange.getAskPrice(),
                                              awayMarketQuotesForExchange.getAskVolume(),
                                              false, 
                                              false, 
                                              true); 
        }
        return returnedSE;
    }

    /**
     * Finds the Exchnage Quote for a given Exchange.
     */
    public AwayExchangeQuote findExchangeBBOEntry(String exchangeName)
    {
        int index = MarketDataAwayExchanges.findLinkageExchange(exchangeName).exchangeChar - 'A';
        
        if(index >= 0)
        {
            
            return awayMarketQuotesByExchange[index];
        }
        else
        {
            return null;
        }
    }

    public AwayExchangeQuote findExchangeBBOEntry(char exchangeCode)
    {
        int index = exchangeCode - 'A';
        
        if(index >= 0)
        {
            
            return awayMarketQuotesByExchange[index];
        }
        else
        {
            return null;
        }
    }

    
    /**
     * 
     * @return
     */
    

    public AwayExchangeQuote[] getAwayMarketQuotesByExchange()
    {
        return awayMarketQuotesByExchange;
    }
    public ExchangeIndicatorStruct[] getExchangeIndicators()
    {
    	return exchangeIndicators;
    }

    public boolean getShortSaleTriggeredMode()
    {
        return shortSaleMode;
    }

    public void setShortSaleTriggeredMode(boolean shortSaleTriggeredMode)
    {
        shortSaleMode = shortSaleTriggeredMode; 
    }
}

