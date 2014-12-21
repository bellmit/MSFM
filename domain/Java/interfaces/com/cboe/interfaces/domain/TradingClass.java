package com.cboe.interfaces.domain;

import java.util.List;
import java.util.Map;

import EDU.oswego.cs.dl.util.concurrent.Semaphore;

import com.cboe.exceptions.NotAcceptedException;
import com.cboe.idl.cmiOrder.FilledReportStruct;
import com.cboe.idl.cmiProduct.EPWStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiSession.SessionClassDetailStruct;
import com.cboe.idl.cmiUser.ExchangeAcronymStruct;
import com.cboe.idl.quote.QuoteInfoStruct;
import com.cboe.idl.tradingProperty.AllocationStrategyStructV2;
import com.cboe.idl.tradingProperty.AuctionBooleanStruct;
import com.cboe.idl.tradingProperty.AuctionLongStruct;
import com.cboe.idl.tradingProperty.AuctionOrderSizeTicksStruct;
import com.cboe.idl.tradingProperty.AuctionRangeStruct;
import com.cboe.idl.tradingProperty.DpmRightsScaleStruct;
import com.cboe.idl.tradingProperty.InternalizationPercentageStruct;
import com.cboe.idl.tradingProperty.TimeRangeStruct;
import com.cboe.infrastructureServices.foundationFramework.transactionManagement.BOSession;
import com.cboe.interfaces.domain.linkageClassGate.LinkageClassGate;
import com.cboe.interfaces.domain.tradingProperty.AllowedHALTypes;
import com.cboe.interfaces.domain.tradingProperty.AllowedHalOriginCodes;
import com.cboe.interfaces.domain.tradingProperty.AllowedSalOriginCodes;
import com.cboe.interfaces.domain.tradingProperty.AuctionMinMaxOrderSize;
import com.cboe.interfaces.domain.tradingProperty.AutoLinkDisqualifiedExchanges;
import com.cboe.interfaces.domain.tradingProperty.AutoLinkOriginCodes;
import com.cboe.interfaces.domain.tradingProperty.AutoLinkPreferredTieExchanges;
import com.cboe.interfaces.domain.tradingProperty.ExchangePrescribedWidth;
import com.cboe.interfaces.domain.tradingProperty.ExtremelyWideQuoteWidth;
import com.cboe.interfaces.domain.tradingProperty.MKTOrderDrillThroughPennies;
import com.cboe.interfaces.domain.tradingProperty.OpeningPriceValidation;
import com.cboe.interfaces.domain.tradingProperty.PDPMRightsScales;
import com.cboe.interfaces.domain.tradingProperty.RangeScale;
import com.cboe.interfaces.domain.tradingProperty.RegularMarketHours;
import com.cboe.interfaces.domain.tradingProperty.AllowedWtpOriginCodes;

/**
 * A source of trading information about a product class.
 *
 * @author John Wickberg
 */

public interface TradingClass {

	/**
	 * Session name when it is unassigned.
	 */
	public static final String NO_ASSIGNED_SESSION = "$NO_SESSION$";

	/**
     * Get the internal (transient) state of this trading class.  
     * This is generally for UserInputMonitor.  A class is considered 
     * 'open' if at least one series within it is open.  The internal
     * state then change only when a class-level state change occurs: 
     * there is no check for "all series of the class are collectively 
     * in state X".  Additionally, at the time of this writing there is
     * little done to ensure that a class's internal state transitions
     * to CLOSE from OPEN.
     * 
     * @return short - constant from com.cboe.idl.cmiConstants.ClassStates
     */
    public short getInternalState();
    
    /**
     * Set the internal (transient) state of this trading class.
     * @see #getInternalState() 
     * @param p_state - constant from com.cboe.idl.cmiConstants.ClassStates
     */
    public void setInternalState(short p_state);
    
    /**
     * Gets the state of the trading class in order to try and match current state
     * of global's session_element_class->class_state field.  This field is only 
     * being used by PSSLI to help assist in product state changes based on the underlying
     * @return
     */
    public short getGlobalState();
    
    /**
     * Sets the state of the trading class in order to try and match current state
     * of global's session_element_class->class_state field.  This field is only 
     * being used by PSSLI to help assist in product state changes based on the underlying.
     * This field is set once the HTS publishes to Global.
     * @param p_state
     */
    public void setGlobalState(short p_state);

    /*
     * return true if the object is a proxy for trading class hosted in another
     * trade server
     */
    public boolean isProxy();
    public void setProxy(boolean isProxy);

    /**
     * @return the currentTimeMillis() of the most recent internalState change.
     */
    public long getInternalStateChangeTime();
    
    /**
     * Returns true if this class is defined as a test class.
     * @return
     */
    public boolean isTestClass();
    
	/**
	 * Accepts a command to be queued.
	 *
	 * @param command a command to be queued
     * @param ignoreQueueLimit indicator on if the queue limit will be checked.  Use true when one command was created
     * during the process or another command.
	 * @exception NotAcceptedException if command cannot be queued for processing
	 */
	void acceptCommand(TradingClassCommand command, boolean ignoreQueueLimit) throws NotAcceptedException;

	/**
	 * Adds the requested number of ticks to the given price.
	 *
	 */
	Price addTicks(Price curPrice, int nbrTicks);

    /**
     * Return the number of tick difference of price from basePrice
     */
    public int getTickDifference(Price basePrice, Price price);

	/**
	 * Gets the symbol of the ProductClass of this class.
	 *
	 * @return symbol of ProductClass
	 */
	String getClassSymbol();

	/**
	 * Gets the exchange prescribed width based on a bid price.  The width
	 * will be adjusted if the class is in a fast market.
	 *
	 * @param basePrice base price for width lookup
     * @param fastMarket if true, width will be adjusted by fast market multiplier
	 * @return exchange prescribed width
	 */
	Price getExchangePrescribedWidth(Price basePrice, boolean fastMarket);

	/**
	 * Gets the key of the ProductClass of this class.
	 *
	 * @return key of ProductClass
	 */
	int getProductClassKey();

	/**
	 * Get trading products for this class.
	 *
     * @param enabledOnly if true, only products enabled in the current session will be returned
	 * @return trading products defined for this class
	 */
	TradingProduct[] getProducts(boolean enabledOnly);

	/**
	 * Gets the product type code of this class.
	 */
	short getProductType();

	/**
	 * Gets the name of the current trading session for this class.
	 *
	 * @return name of trading session or NO_ASSIGNED_SESSION constant if class is not in a session
	 */
	String getSessionName();

	/**
	 * Gets product keys for the underlying product of this class.  Return value will be null if this class
     * does not have an underlying.
	 */
	ProductKeysStruct getUnderlyingProductKeys();

	/**
	 * Gets the session name for the underlying product of this class.
	 */
	String getUnderlyingSessionName();

    public long getProductCloseTime();

    	/**
	 * Checks to see if class is in a trading session.
	 *
	 * @return true if class is in a trading session.
	 */
	boolean inTradingSession();

    /**
     * Tests to see if class is a strategy.
     *
     * @return true if class is a strategy
     */
	boolean isStrategy();
	
	  /**
     * Tests to see if class is an index.
     *
     * @return true if class is an index
     */
    boolean isIndex();
	

    /**
   * Tests to see if class is an equity.
   *
   * @return true if class is an equity
   */
  boolean isEquity();
	/**
     * Determines if price is valid for an order.
     *
     * @param premiumPrice premium price to be checked
     * @return true if price is valid
     */
    boolean isValidPriceForOrder(Price premiumPrice);

    /**
     * Determines if price is valid for a quote.
     *
     * @param premiumPrice premium price to be checked
     * @return true if price is valid
     */
    boolean isValidPriceForQuote(Price premiumPrice);

	/**
	 * Checks bid/ask value to see if spread is within exchange prescribed
	 * width.
	 *
	 * @param bidPrice value of bid
	 * @param askPrice valid of ask
     * @param fastMarket if true, fast market multiplier will be used when checking width
	 * @return true if spread is within exchange prescribed width.
	 */
	boolean isWithinPrescribedWidth(Price bidPrice, Price askPrice, boolean fastMarket);

    /**
     * Checks bid/ask value to see if spread is within exchange prescribed
     * width.
     *
     * @param bidPrice value of bid
     * @param askPrice valid of ask
     * @param fastMarket if true, fast market multiplier will be used when checking width
     * @return true if spread is within exchange prescribed width.
     */
    boolean isWithinOpeningPrescribedWidth(Price bidPrice, Price askPrice);
    boolean isWithinLeapsOpeningPrescribedWidth(Price bidPrice, Price askPrice);
 	/**
	 * Checks to see if passed thread is the same thread as the command
     * processor thread.
	 */
	boolean isCommandProcessorThread(Thread aThread);

	/**
	 * Calculates the nearest valid price to the given value.
	 */
	Price nearestPrice(double value);

	/**
     * Returns the smallest (closest to negative infinity) Price that is
     * not less than the argument and is on a valid tick for this class.
	 */
	Price ceilPremiumToTick(Price premiumPrice);

	/**
     * Returns the largest (closest to positive infinity) Price that is
     * not greater than the argument and is on a valid tick for the class.
	 */
	Price floorPremiumToTick(Price premiumPrice);

	/**
	 * Sets the name of the current trading session for this class.
	 *
	 * @param newSession name of trading session
	 */
	void setSessionName(String newSession);

	/**
	 * Sets the session name for the underlying product of this class.
	 */
	void setUnderlyingSessionName(String newSession);

    void setProductCloseTime(long millis);

 	/**
	 * Determine if a user is also a DPM based on userid
	 *
	 * @param userId
	 */
	boolean isDPM(String userId);

    /**
	 * Determine if a user is also a DPM based on acronym
	 *
	 * @param userAcronym
	 */
	boolean isDPM(ExchangeAcronymStruct userAcronym);

 	/**
	 * Determine if a user is also a eDPM based on userid
	 *
	 * @param userId
	 */
	boolean isEDPM(String userId);

    /**
	 * Determine if a user is also a eDPM based on acronym
	 *
	 * @param userAcronym
	 */
	boolean isEDPM(ExchangeAcronymStruct userAcronym);

    /**
     * Return the broker associated with the trading class
     */
    Broker getBroker();

    /**
     * Return the tick size
     */
    Price getTickSize(Price basePrice,  int direction);


    /**
     * Gets a unique sequence number for the trading class.  The numbers are unique by day and are always increasing.
     * There is no guarentee that the numbers are sequential.
     *
     */
	int getNextSequence();

    /**
     * @return the indicator if the quote lock  minimum trade quantity is set or not.
     * if the min qty is 0, it means the function is turned off
     */
    boolean isQuoteLockMinimumTradeQuantityNeeded();

    boolean isQuoteLockProcessingNeeded();

    boolean isQuoteLockNotificationNeeded();

    boolean isQuoteLockNotificationImmediate();

    /**
     * return a boolean indicating if current process is doing actual trading
     */
    boolean isTradeEngineOn();

    void setTradeEngine (boolean on);

    /**
     * Return the underlying product struct if it has a underlying product.
     * Otherwise null is returned.
     */
    public ProductStruct toUnderlyingProductStruct();

    /**
     * The following group of methods will return a specific type. If aTradingPropertyType is not defined as the
     * return type specified by the method signature, aDataValidationException will be thrown
     */
    public EPWStruct[] getTradingPropertyEPW();
    public int getTradingPropertyMinQuoteCreditDefaultSize();
    public double getTradingPropertyRFQResponseRatio();
    public AllocationStrategyStructV2[] getTradingPropertyAllocationStrategy();
    public int getTradingPropertyContingencyTimeToLive();
    public int getTradingPropertyContinuousQuotePeriodForCredit();
    public double getTradingPropertyFastMarketSpreadMultiplier();
    public TimeRangeStruct getTradingPropertyOpeningTimePeriodRange();
    public int getTradingPropertyOpeningPriceDelay();
    public int getTradingPropertyOpeningPriceRate();
    public TimeRangeStruct getTradingPropertyPreClosingTimePeriod();
    public double getTradingPropertyPrescribedWidthRatio();
    public int getTradingPropertyRFQTimeOut();
    public double getTradingPropertyDPMParticipationPercentage();
    public int getTradingPropertyBookDepthSize();
    public int getTradingPropertyMinSizeForBlockTrade();
    public int getTradingPropertyIPPMinSize();
    public boolean getTradingPropertyIPPTradeThroughFlag();
    public int getTradingPropertyQuoteLockTimer();
    public int getTradingPropertyQuoteLockNotificationTimer();
    public int getTradingPropertyQuoteTriggerTimer();
    public DpmRightsScaleStruct[] getTradingPropertyDPMRightsScale();
    public double getTradingPropertyDPMRightsSplitRate();
    public double getTradingPropertyUMASplitRate();
    public int getTradingPropertyUMAEqualDistributionWeightForDPM();
    public int getTradingPropertyLotSize();
    public boolean getTradingPropertyETFFlag();
    public double getTradingPropertyIPPToleranceAmount();
    public boolean getTradingPropertyNeedsDPMQuoteToOpen();
    public int getTradingPropertySOrderTimeToLive();
    public int getTradingPropertySOrderTimeToCreate();
    public int getTradingPropertySOrderTimeToCreateBeforeClose();
    public int getTradingPropertySOrderTimeToRejectFill();
    public int getTradingPropertyPOrderTimeToLive();
    public int getTradingPropertyPAOrderTimeToLive();
    public char getTradingPropertyTradeType();
    public int getTradingPropertyProductOpenProcedureType();
    public boolean getTradingPropertySatisfactionAlertFlag();
    public int getTradingPropertyFirmPrincipalQuoteSize();
    public int getTradingPropertyFirmCustomerQuoteSize();
    public boolean getTradingPropertyLinkageEnabledFlag();
    public int getTradingPropertyQuoteLockMinTradeQuantity();
    public AuctionRangeStruct[] getTradingPropertyAuctionTimeToLive();
    public AuctionLongStruct[] getTradingPropertyAuctionMinPriceIncrement();
    public AuctionLongStruct[] getTradingPropertyAuctionMinQuoters();
    public AuctionLongStruct[] getTradingPropertyAuctionReceiverTypes();
    public InternalizationPercentageStruct[] getTradingPropertyInternalizationGuaranteedPercentage();
    public AuctionLongStruct[] getTradingPropertyAuctionOrderTicksAwayFromNBBO();
    public int[] getTradingPropertyAutoExEligibleStrategyTypes();
    public AuctionBooleanStruct[] getTradingPropertyAuctionEnabled();
    public AuctionOrderSizeTicksStruct[] getTradingPropertyAuctionMinOrderSizeForTicksAboveNBBO();
    public boolean getTradingPropertyAllowMarketOrder();
    public double getTradingPropertyPriceProtectPercentage();
    public double getTradingPropertySizeIncreasePercentageForNewRfp();
    public double getTradingPropertyMarketTurnerPercentage();
    public int getTradingPropertyMaxProductsPerBookDepthClassRefreshCommand();

    public int getAuctionTimeToLive(short auctionType);
    public int getAuctionMinQuoters(short auctionType);
    public boolean getAuctionEnabled(short auctionType);
    public AuctionMinMaxOrderSize getAuctionMinMaxOrderSize(short auctionType);
    public int getAuctionOrderTicksAwayFromNBBO(short auctionType);
    public int getAuctionTicksAboveNBBO(short auctionType, int orderSize);
    public OpeningPriceValidation getOpeningPriceValidation();
    public ExchangePrescribedWidth[] getOpeningExchangePrescribedWidth();
    public Price getOpeningEPW(Price basePrice);
    public Price getLeapsOepw(Price basePrice);
    public PDPMRightsScales[] getPDPMRightsScales();
    public boolean getTradingPropertyBOTREnabled();
    public long getTradingPropertyEOPStartTime();
    public ExchangePrescribedWidth[] getTradingPropertyLeapsOpeningEPW();
    public ExchangePrescribedWidth[] getTradingPropertyOpeningMaxQuoteInversion();
    public int getTradingPropertyTicksAwayForReCOA();    
    public int getTradingPropertyReCOAInterval();
    public int getTradingPropertyNumberOfAttemptsForReCOA();
    public int getTradingPropertySleepTimerForReCOA();
    public ExchangePrescribedWidth[] getTradingPropertyStrategyEPW();
    
    public int getTradingPropertyTicksAwayForCPSReCOA();    
    public int getTradingPropertyCPSReCOAInterval();
    public int getTradingPropertyNumberOfAttemptsForCPSReCOA();
    public int getTradingPropertySleepTimerForCPSReCOA();    
    public boolean getEnableCPSBooking();
    
    public int getTradingPropertyCPSSplittingMinimumStockNBBOQuantity();
    public double getTradingPropertyCPSSplittingMinimumOptionBOTRBid();
    public int getTradingPropertyCPSSplittingMaxStockOrderSize();
    public int getTradingPropertyCPSSplittingMaxOptionOrderSize();
    public boolean getEnableCPSMktSplitting();
    public boolean getVarianceStripIndicator();
    
    
    public int getOpeningMaxRetriesWaitingForQuote();
    public RangeScale[] getTradingPropertyBotrRangeScales();
    public double getBotrScale(int index);
    public int getTradingPropertyEOPInterval();
    public AllowedHALTypes[] getTradingPropertyAllowedHALTypes();
    public Map<Short, Boolean> getTradingPropertyAllowedHALTypesMap();
    public int getTradingPropertyHALTriggerTimer();
    public int getTradingPropertyHALOTriggerTimer();
    public int getTradingPropertyReportingPriceAdjustment();
    public int getTradingPropertyITSCommitmentAutoCancelThreshold();
    public int getTradingPropertyMOCInterval();
    public int getTradingPropertyCrossMinOrderSize();
    public int getTradingPropertyCrossMinDollarAmount();
    public int getTradingPropertyMarketLotSize();
    public int getTradingPropertyNBBOCrossMinOrderSize();
    public int getTradingPropertyNBBOCrossMinDollarAmount();
    public int getManualquoteCancelTimerForNonPAR();
    public int getManualquoteCancelTimerForPAR();
    public int getAutoExSizeAgainstNonCustomer();
    public boolean getIndexHybridIndicator();
    public ExtremelyWideQuoteWidth[] getExtremelyWideQuoteWidth();
    public int getHoldBackTimer();
    public double getTradingPropertyBBOAllocationPercentage();
    public int getAllocateAllToRightsParticipantTradingProperty();   
    public int getTradingPropertyDirectedAIMTimer();
    
    //Minimum qty for Qualified contingent trade orders.
    public int getTradingPropertyQCCMinValue();
    public Price getMKTOrderRestrictedPrice(Price basePrice, boolean sell);
    
    //Added for Block Trade display functionality
    public int getLargeTradeTickerSize();

    public Price addAuctionTicks (Price thePrice, int tickNumbers, short auctionType);
    public int getAONSolicitationMinimumQuantity();
    public AllowedSalOriginCodes[] getAllowedSALOriginCodesList();    
    public boolean getAsynchronousTradingEnabled();
    public boolean getEnableEarlyClose();
    /**
     * Returns the smallest (closest to negative infinity) Price that is
     * not less than the argument and is on a valid tick for this class.
     *
     * @param premiumPrice to quantize
     * @param auctionType
     * @param auctionPrice
     * @return quantized price
     */
    public Price ceilPremiumToAuctionTick(Price premiumPrice, short auctionType, Price auctionPrice);

    /**
     * Returns the largest (closest to positive infinity) Price that is
     * not greater than the argument and is on a valid tick for the class.
     *
     * @param premiumPrice to quantize
     * @param auctionType
     * @param auctionPrice
     * @return quantized price
     */
    public Price floorPremiumToAuctionTick(Price premiumPrice, short auctionType, Price auctionPrice);

    public TickScale getTickScale();
    public TickScale getAuctionTickScale(short auctionType);

    public boolean isTypeEligibleToHAL(short type);

    //Following method will be used to print new-way defined trading property value
    public String printTradingProperty(int type);

    /**
     * Return the cached LinkageClassGate
     * @return LinkageClassGate
     * @author Mark Wolters
      */
    public LinkageClassGate getLinkageClassGate();

    /*
     * The methods startProcessing / completeProcessing are called from CommandProcessor after acquiring
     * class level lock and before releasing it. These two methods are to be called in a pair and from a
     * synchronized block of code.
     */
    public void startProcessing ();

    public void completeProcessing ();

    /*
    This method returns the semaphore for order control flow on this class;
    */
    public Semaphore getOrderFlowSemaphore();

    /*
     * The method acceptFillReport gets called directly from the TradeReportImpl instead of the fill report
     * event consumer.
     */
	public void acceptFillReport(QuoteInfoStruct quoteInfo, short statusUpdateReason, FilledReportStruct[] reports, String salePrefix);
	
	public void acceptQuoteFill(int quoteKey, String userId, long tradeTime, int tradedQuantity);

    /**
     * This method is to add the trading product in the list of products for which state was changed in the current transaction.
     * ProductStateService will call this method after changing the product state.
     * The accumulated list will be used to notify interested services to short cut the state change event loopback.
     * @param product
     */
    public void addProductForStateChangeNotification(TradingProduct product);

    void cleanupCommandCache();

    /**
     * method for setting results of an operation on trading class.
     */
    void setResult (int resultCode, String msg);
    
    int getResultCode ();
    
    String getResultMsg ();
    
    void clearResults ();

    /*
     * This method is used to add the incoming MOC order to the MOCHolder. MOCHolder allows adding the order if the classTimer is not
     * activated for this class today yet. Otherwise, the order will be activated immediately.
     */
    public boolean addOrderToHolder(Order anOrder, Broker broker);

    public void removeOrderFromHolder(Order anOrder);

//    public void setElementKey(Integer elementKeyForClass);//future usage
    
    /** gets the time threshold in milliseconds to linkage order time to live as we do not
     *  want to have exact time as the linkage timeout.
     * 
     * @return int
     */
    public int getLinkageOrderTimeToLiveThreshold();
    
    /**
     * If the NBBOAgent Handing is enabled.
     * @return true or false
     */
    public boolean isNBBOAgentHeldingEnabled();
        
    public void addToToBeRemovedOrders(Order toBeRemovedOrder);
    
    public boolean isAONSolicitationEnabled();
    
    public Price getOpeningMaxQuoteInversion(Price baseBid);
    
    public boolean isWithinOpeningMaxQuoteInversion(Price bid, Price ask);
    
    public Price getExchangePrescribedWidthForStrategy(Price basePrice);

    public boolean getEnablePriceProtectionForStrategy();
    
    public void initMarketBuffer();
    
    //public AutoLinkOriginCodes[] getAutoLinkOriginCodesList();
    
    public boolean getAutoLinkOriginCodes(char orderOriginCode);
    
    public Map<Character, Boolean> getAutoLinkOriginCodesList();
    
    public AutoLinkPreferredTieExchanges[] getNewLinkagePreferredTieExchangeList();
    
    public AutoLinkDisqualifiedExchanges[] getAutoLinkDisqualifiedExchangesList();
    
    public List<String> getAutoLinkOnlyDisqualifiedExchangesList();
    
    //public AllowedHalOriginCodes[] getAllowedHalOriginCodesList();
    
    public Map<Character, Boolean> getAllowedHalOriginCodesList();
    
    public boolean getAllowedHalOriginCodes(char orderOriginCode);
    
    public MKTOrderDrillThroughPennies[] getMKTOrderDrillThroughPennies();
    
    public String getPrimaryExchange();
    
    public boolean isTradingClassForProduct(int productKey);

    public AllocationStrategyStructV2[] getDefaultAllocationStrategiesForClass();
    
    public boolean isMarketUpdateDueToMassQuoteCancel();

    public void setMarketUpdateDueToMassQuoteCancel(boolean p_isMarketUpdateDueToMassQuoteCancel);

    public RegularMarketHours getRegularMarketHours();
    
    public boolean getEnabledRegularMaketTimeValidation();
    public boolean isTypeEligibleToHALV2(short type);

    public void addToSession(String sessionName, SessionClassDetailStruct sessionClassDetail, long productCloseTime);

    public void removeFromSession(String sessionName, SessionClassDetailStruct sessionClassDetail);

    public AllowedWtpOriginCodes[] getAllowedWtpOriginCodesList();
    
    public boolean getAllowedWtpOriginCodes(char orderOriginCode);
    public boolean getAllowComplexTradesWithQuotes();
    public boolean isPennyOrSubPennyClass();
    public int getAuctionMinPriceIncrement(short auctionType);
    public Price getTickSizeBelow();
}
