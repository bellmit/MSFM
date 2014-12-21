package com.cboe.interfaces.domain;

import java.util.ArrayList;
import java.util.HashMap;

import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.infrastructureServices.uuidService.IdService;

/**
 * this class is designed to handle the trade report creation for quote trigger
 * tradable
 */

public interface QuoteTriggerTradeReport extends TradeReport{

    /**
     * add trade to the report
     */
    public void addTrade(ArrayList aQuoteTriggerParty, ArrayList nonQParty, ArrayList tradePrices, ArrayList tradedQuantities, Tradable unbookedTradable);

    /**
     * prepare for completion
     */
    public void prepareForCompletion();

    /**
     *
     */
    public ParticipantList getQuoteTriggerParticipantList();

    /**
     * get NonQParticipants
     */
    public ParticipantList getNonQParticipantList();

    /**
     * set QuoteTriggerParticipants
     */
    public void setQuoteTriggerParticipantList(ParticipantList aQuoteTriggerParty);

    /**
     * set NonQParticipants
     */
    public void setNonQParticipantList(ParticipantList aNonQParty);

    /**
     * complete pending trade
     */
    public void completePendingTrade(ArrayList atomicTrades, IdService idService)
    throws SystemException, DataValidationException;

    /**
     * complete pending trade
     */
    public void completePendingTrade(ArrayList atomicTrades, IdService idService, HashMap buySideLegsMap, HashMap sellSideLegsMap)
    throws SystemException, DataValidationException;

     // UD 11/15/04 ...
    // QT changes for strategy product.

    /**
     *  set strategy leg prices for this quote trigger tradable.
     */
    public void setStrategyLegTradePrices(StrategyLegTradePrice[] triggerLegTradePrices);

    /**
    *  returns the strategy leg trade prices for this trigger.
    */
    public StrategyLegTradePrice[] getStrategyLegTradePrices();

    /**
     *  set strategy leg trade prices for this quote trigger tradable.
     */
    public void setTradePricesOfLegs(Price[] tradePriceOfLegs);

    /**
     *  get strategy leg trade prices for this quote trigger tradable.
     */
    public Price[] getTradePricesOfLegs();

    /**
     *  set strategy leg sides
     */
    public void setSidesOfLegs(Side[] sidesOfLegs);

    /**
     *  get strategy leg sides
     */
    public Side[] getSidesOfLegs();

    /**
    *  set dervied trade side
    */
    public void setDerivedTradeSide(Side derivedTradeSide);

    /**
    *  get dervied trade side
    */
    public Side getDerivedTradeSide();
    
    public void setQuoteTriggerType(short qtType);

    /**
     *
      * Due to split it is possible to have multiple prices for StrategyLegTradePrice
     * so to create a corresponding StrategyLegQuoteTriggerContainer..
     */
//Future use, if needed.
//Following introduced when noticed that if a split occurs num(Prices) > num(strategyLegTradePrices)
//Currently, its commented out based on decision that tickValue(strategy) will not be less than tickValue(legs)
//    public void setTradingProductsFromLegTradePrices(TradingProduct[] tradingProducts);
//    public TradingProduct[] getTradingProductsFromLegTradePrices();
}