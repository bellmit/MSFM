package com.cboe.presentation.marketData.displayrules;

import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.dateTime.Date;
import com.cboe.interfaces.presentation.product.Strategy;
import com.cboe.interfaces.presentation.product.StrategyLeg;
import com.cboe.interfaces.presentation.marketData.DsmBidAskStruct;
import com.cboe.interfaces.presentation.marketData.UserMarketDataStruct;
import com.cboe.presentation.common.formatters.DisplayPriceFactory;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.api.StrategyUtility;
import com.cboe.presentation.product.ProductFactory;
import com.cboe.idl.cmiStrategy.StrategyLegStruct;
import com.cboe.idl.cmiStrategy.StrategyStruct;
import com.cboe.idl.cmiConstants.Sides;
import com.cboe.idl.cmiSession.SessionStrategyStruct;
import com.cboe.idl.cmiMarketData.MarketVolumeStruct;
import com.cboe.domain.util.PriceFactory;
import com.cboe.domain.util.SessionStrategyStructHelper;

/**
 * Default display for a strategy for the bid/ask, bidSize/askSize and customer bid/ask size.
 * If a no display rules exist for a strategy then it will default to this rules.
 * 
 * This is based on the algo used in COBWEB.
 * 	- Negative goes to bid
 *  - Positive goes to ask
 *  - Zero do nothing
 *  - Both positive, the lower goes to bid and the higher goes to ask. 
 * 
 * @author Eric Maheo
 * @author Shawn Khosravani - refactored \gui\Java\commonBusiness\com\cboe\presentation\marketDisplay\displayrules\AbstractBidAskDisplayStrategyRule.java 
 */
public class StrategyBidAskFlipper
{
    /**
     * Define a price with a value Zero.
     */
    protected static final Price ZERO_PRICE = DisplayPriceFactory.create(0.0);
    /**
     * Define a price that has no display. In general a price that has a quantity zero.
     */
    protected static final Price NO_DISPLAY = DisplayPriceFactory.create(Double.MAX_VALUE);
    /**
     * Define a positive price.
     */
    protected static final int POSITIVE = 1;
    /**
     * Define a negative price.
     */
    protected static final int NEGATIVE = -1;
    /**
     * Define a price with a value of 0.00.
     */
    protected static final int ZERO		= 0;
    /**
     * Define a price that has an unknown value.
     * This is used to for Price Object that are in an illegalstate.
     */
    protected static final int UNKNOWN = -100;
    /**
     * Define a price that has no value.
     * Typically for NoPrice, MktPrice or CabPrice.
     */
    protected static final int  NOVALUE = 100;
    protected static final char BUY     = 'B';
    protected static final char SELL    = 'S';

    private static String category = "StrategyBidAskFlipper";

    private static StrategyBidAskFlipper instance = null;


    private StrategyBidAskFlipper()
    {
    }

    public static StrategyBidAskFlipper getInstance()
    {
        if (instance == null)
        {
            instance = new StrategyBidAskFlipper();
            category = instance.getClass().getName();
        }
        return instance;
    }

    public static boolean isBidAskFlipped(StrategyType strategyName, Strategy strategy, UserMarketDataStruct userMarketDataStruct)
    {
        return isBidAskFlipped(strategyName, strategy.getStrategyLegs(), userMarketDataStruct);
    }

    public static boolean isBidAskFlipped(StrategyType strategyName, StrategyLeg[] legs, UserMarketDataStruct userMarketDataStruct)
    {
        if (userMarketDataStruct == null)
        {
            throw new IllegalArgumentException("UserMarketDataWithDsmStruct cannot be null.");
        }
        Price bid = DisplayPriceFactory.create(userMarketDataStruct.currentMarket.bidPrice);
        Price ask = DisplayPriceFactory.create(userMarketDataStruct.currentMarket.askPrice);
        MarketVolumeStruct[] bidQty = userMarketDataStruct.currentMarket.bidSizeSequence;
        MarketVolumeStruct[] askQty = userMarketDataStruct.currentMarket.askSizeSequence;
        int bidSize = (bidQty != null && bidQty.length>0) ? bidQty[0].quantity : 0;
        int askSize = (askQty != null && askQty.length>0) ? askQty[0].quantity : 0;

        boolean isFlipped = isBidAskFlipped(strategyName, legs, bid, ask, bidSize, askSize);
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.COMMON))
        {
            GUILoggerHome.find().debug(category + ".isBidAskFlipped", GUILoggerBusinessProperty.COMMON,
                                       " bid=[" + bid + "] ask=[" + ask + "] isFlipped=" + isFlipped);
        }
        return isFlipped;
    }

    public static boolean isDsmBidAskFlipped(Strategy strategy, DsmBidAskStruct dsmBidAskStruct)
    {
        StrategyType strategyType = StrategyUtility.getStrategyType(strategy);
        return isDsmBidAskFlipped(strategyType, strategy.getStrategyLegs(), dsmBidAskStruct);
    }

    public static boolean isDsmBidAskFlipped(StrategyType strategyType, Strategy strategy, DsmBidAskStruct dsmBidAskStruct)
    {
        return isDsmBidAskFlipped(strategyType, strategy.getStrategyLegs(), dsmBidAskStruct);
    }

    public static boolean isDsmBidAskFlipped(StrategyType strategyType, StrategyLeg[] legs, DsmBidAskStruct dsm)
    {
        if (dsm == null)
        {
            throw new IllegalArgumentException("DsmBidAskStruct cannot be null.");
        }
        Price bid = PriceFactory.create(dsm.getBid());
        Price ask = PriceFactory.create(dsm.getAsk());

        boolean isFlipped = isBidAskFlipped(strategyType, legs, bid, ask, 1, 1);        // TODO ?? where to get volume from ??
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.STRATEGY_DSM))
        {
            GUILoggerHome.find().debug(category + ".isDsmBidAskFlipped", GUILoggerBusinessProperty.STRATEGY_DSM,
                                       " strategyType=" + strategyType + " bid=[" + bid + "] ask=[" + ask + "] isFlipped=" + isFlipped);
        }
        return isFlipped;
    }

    public static boolean isBidAskFlipped(StrategyType strategyType, StrategyLeg[] strategyLegs, Price bidPrice, Price askPrice, int bidSize, int askSize)
    {
        boolean isFlipped;

        switch(strategyType)
        {
            case TIME:
                isFlipped = isBidAskFlippedTime(strategyLegs);
                break;
            case VERTICAL:
                isFlipped = isBidAskFlippedVertical(strategyLegs, bidPrice, askPrice);
                break;
            case BUTTERFLY:
                isFlipped = isBidAskFlippedButterfly(strategyLegs);
                break;
            case SPREAD_WITH_3_LEGS:
            case UNKNOWN:
            default:
                isFlipped = isBidAskFlippedDefault(bidPrice, askPrice, bidSize, askSize);
                break;
        }

        return isFlipped;
    }

//    // TODO ?? not used yet
//    public static StrategyImpliedMarketWrapper getStrategyImpliedMarket(StrategyType strategyName, DsmBidAskStruct dsmBidAsk, boolean isFlipped)
//    {
//        StrategyImpliedMarketWrapper im = null;
//        Price ask = DisplayPriceFactory.create(dsmBidAsk.getAsk());
//        Price bid = DisplayPriceFactory.create(dsmBidAsk.getBid());
//        if (ask.isValuedPrice()  &&  bid.isValuedPrice())
//        {
//            switch(strategyName)
//            {
//                case TIME:
//                case VERTICAL:
//                case BUTTERFLY:
//                    if (isFlipped)
//                    {
//                        im = new StrategyImpliedMarketWrapperImpl(bid.toDouble(), ask.toDouble());
//                    }
//                    else
//                    {
//                        im = new StrategyImpliedMarketWrapperImpl(ask.toDouble(), bid.toDouble());
//                    }
//                    break;
//                case SPREAD_WITH_3_LEGS:
//                case UNKNOWN:
//                default:
//                    if (bid.toDouble() > 0.0  &&  ask.toDouble() > 0.0)
//                    {
//                        double lower  = Math.min(bid.toDouble(), ask.toDouble());
//                        double higher = Math.max(bid.toDouble(), ask.toDouble());
//                        im = new StrategyImpliedMarketWrapperImpl(higher, lower);
//                    }
//                    break;
//            }
//        }
//
//        return im;
//    }

    /**
     * Return true if the quantity is greater than 0.
     * @param qty
     * @return true if > 0 and false <= 0 or false if null.
     */
    protected static boolean hasQuantity(Integer qty){
        return (qty != null && qty > 0);
    }
    /**
     * Get the state of the price. If the price is null it will return UNKNOWN.
     *
     * @param price
     * @return one of the state {@link #NEGATIVE}, {@link #ZERO}, {@link #POSITIVE}, {@link #UNKNOWN} or {@link #NOVALUE}.
     */
    protected static int getStatePrice(Price price){
        if (price == null){
            GUILoggerHome.find().exception("Found price null.", new IllegalArgumentException("Price cannot be null.").fillInStackTrace());
            return UNKNOWN;
        }
        else if (!price.isValuedPrice()){
            return NOVALUE;
        }
        else { // price is a value. It have 3 states (-,0,+).
            if (price.greaterThan(ZERO_PRICE)){
                return POSITIVE;
            }
            else if (price.lessThan(ZERO_PRICE)){
                return NEGATIVE;
            }
            return ZERO;
        }
    }

    private static boolean isBidAskFlippedDefault(Price bestBookbid, Price bestBookAsk, int bidSize, int askSize)
    {
        boolean isFlipped = false;

        boolean bidqty = hasQuantity(bidSize);
        boolean askqty = hasQuantity(askSize);
        if (bidqty && askqty){//obviously the order of this if/elsif matters.
            isFlipped = assignBidAskWithQty(bestBookbid, bestBookAsk);
        }
        else if (bidqty){
            isFlipped = assignBidAskWithBidQty(bestBookbid, bestBookAsk);
        }
        else if (askqty){
            isFlipped = assignAskWithAskQty(bestBookbid, bestBookAsk);
        }

        return isFlipped;
    }

    private static boolean isBidAskFlippedTime(StrategyLeg[] strategyLeg)
    {
        boolean isFlipped = false;

        if (strategyLeg == null){
            GUILoggerHome.find().exception("legs for the strategy cannot be null.", new IllegalStateException("strategy with no leg.").fillInStackTrace());
        }
        else if (strategyLeg.length != 2){
            GUILoggerHome.find().exception("Found an illegal time strategy with " + strategyLeg.length + " legs.", new IllegalStateException("Illegal number of legs for time strategy.").fillInStackTrace());
        }
        else{
            Date date0 = strategyLeg[0].getProduct().getExpirationDate();
            Date date1 = strategyLeg[1].getProduct().getExpirationDate();

            boolean isBuyFartherMonth;
            if (strategyLeg[0].getSide() == 'B')
            {
                isBuyFartherMonth = isBuyFartherMonth(date0, date1);
            }
            else
            {
                isBuyFartherMonth = isBuyFartherMonth(date1, date0);
            }

            isFlipped = ! isBuyFartherMonth;

            // isFlipped = ! (strategyLeg[0].getSide() == 'B'  &&  isBuyFartherMonth(date0, date1));
        }
        return isFlipped;
    }

    private static boolean isBidAskFlippedVertical(StrategyLeg[] strategyLeg, Price bidPrice, Price askPrice)
    {
        boolean isFlipped = false;

        if (strategyLeg == null){
            GUILoggerHome.find().exception("legs for the strategy cannot be null.", new IllegalStateException("strategy with no leg.").fillInStackTrace());
        }
        else if (strategyLeg.length != 2){
            GUILoggerHome.find().exception("Found an illegal vertical strategy with " + strategyLeg.length + " legs.", new IllegalStateException("Illegal number of legs for vertical strategy.").fillInStackTrace());
        }
        else{
            isFlipped = !isBuyLowerStrikePrice(bidPrice, askPrice);
        }
        return isFlipped;
    }

    private static boolean isBidAskFlippedButterfly(StrategyLeg[] strategyLeg)
    {
        boolean isFlipped = false;

        if (strategyLeg == null){
            GUILoggerHome.find().exception("legs for the strategy cannot be null.", new IllegalStateException("strategy with no leg.").fillInStackTrace());
        }
        else if (strategyLeg.length != 3){
            GUILoggerHome.find().exception("Found an illegal butterfly strategy with " + strategyLeg.length + " legs.", new IllegalStateException("Illegal number of legs for butterfly strategy.").fillInStackTrace());
        }
        else{
            isFlipped = isTwoLegsRatioWithQtyOneIsSell(strategyLeg);
        }
        return isFlipped;
    }

    //same sign -- so it can be reuse for both positive/negative and even.
    private static boolean assignBidAskWithQty(Price pricebid, Price priceask){
        boolean reverse = false;
        int stateBid = getStatePrice(pricebid);
        int stateAsk = getStatePrice(priceask);

        if ((stateBid == NOVALUE || stateBid == UNKNOWN) && (stateAsk == NOVALUE || stateAsk == UNKNOWN)){
            if (stateBid == UNKNOWN || stateAsk == UNKNOWN){
                throw new IllegalStateException("Bid or ask price are an unknwown state.");
            }
        }
        else {
            if (!priceask.isValuedPrice()  ||  !pricebid.isValuedPrice()){
                Price ask;
                if (pricebid.isNoPrice()){
                    ask=priceask.greaterThan(ZERO_PRICE)?priceask:ZERO_PRICE;
                }
                else if (priceask.isNoPrice()){
                    ask=pricebid.greaterThan(ZERO_PRICE)?pricebid:ZERO_PRICE;
                }
                else {
                    ask = priceask;
                }
                //in case of non value the equals used the identity of the object and not its value.
                reverse = !ask.toString().equals(priceask.toString());
            }
            else {
                reverse = !priceask.equals(priceask.greaterThan(pricebid)? priceask : pricebid);
            }
        }
        return reverse;
    }

    private static boolean assignBidAskWithBidQty(Price pricebid, Price priceask){
        boolean reverse = false;
        int stateBid = getStatePrice(pricebid);
        int stateAsk = getStatePrice(priceask);

        if (stateBid == NOVALUE || stateBid == UNKNOWN){
            if (stateAsk == NOVALUE || stateAsk == UNKNOWN){
                throw new IllegalStateException("Bid or ask price are an unknwown state.");
            }
        }
        else {
            // reverse = !pricebid.equals(pricebid.lessThan(ZERO_PRICE)? pricebid : NO_DISPLAY);
            if (pricebid.lessThan(ZERO_PRICE)) {
                reverse = false;
            }
            else {
                reverse = pricebid.isValuedPrice();
            }
        }
        return reverse;
    }

    private static boolean assignAskWithAskQty(Price pricebid, Price priceask){
        boolean reverse = false;
        int stateBid = getStatePrice(pricebid);
        int stateAsk = getStatePrice(priceask);

        if (stateAsk == NOVALUE || stateAsk == UNKNOWN){
            if (stateBid == NOVALUE || stateBid == UNKNOWN){
                throw new IllegalStateException("Bid or ask price are an unknwown state.");
            }
        }
        else {
            // reverse = !priceask.equals(priceask.greaterThan(ZERO_PRICE)? priceask: NO_DISPLAY);
            if (priceask.lessThan(ZERO_PRICE)) {
                reverse = false;
            }
            else {
                reverse = priceask.isValuedPrice();
            }
        }
        return reverse;
    }

    /**
     * Check if the buy date is greater than the sale date.
     * @param buyDate
     * @param sellDate
     * @return true if the buy date is greater than the sale date.
     */
    private static boolean isBuyFartherMonth(Date buyDate, Date sellDate){
        return buyDate.compareTo(sellDate) > 0;
    }

    /**
     * The current implementation return true if:
     * 	both prices are values and the buy price is lower than the sell price.
     *  If buy price lower than zero it will return true.
     *  If sell price greater than zero it will return true.
     *
     * @param buyPrice ?
     * @param sellPrice ?
     * @return true if both prices are value and the buy price is lower than sell price.
     */
    private static boolean isBuyLowerStrikePrice(Price buyPrice, Price sellPrice){
        if (buyPrice == null || sellPrice == null ){
            return false;
        }
        //need to check if the price is a value or not.
        if (buyPrice.isValuedPrice() && sellPrice.isValuedPrice()){
            return buyPrice.lessThanOrEqual(sellPrice);
        }
        else if (buyPrice.isNoPrice() && sellPrice.isValuedPrice()){
            if (ZERO_PRICE.lessThan(sellPrice)){
                return true;
            }
        }
        else if (buyPrice.isValuedPrice() && sellPrice.isNoPrice()){
            if (buyPrice.lessThan(ZERO_PRICE)){
                return true;
            }
        }
        else if (buyPrice.isNoPrice() && sellPrice.isNoPrice()){
            return true;// don't flip prices if both are noPrice.
        }
        return false;
    }

    /**
     * Return true is the legs with a ratio qty is 1 and their side is Buy.
     * The StrategyLeg was qualified to be a butterfly. However the legs could be
     * shuffled so we test if the leg with ratio 1 is a BUY rather than the first leg.
     *
     * @param legs must not be null.
     * @return true if the leg ratio 1 is a buy and false if it's a sell.
     */
    private static boolean isTwoLegsRatioWithQtyOneIsSell(StrategyLeg[] legs){
        int len = legs.length;
        for (int i=0; i<len; i++){
            if (legs[i].getRatioQuantity() == 1){
                return legs[i].getSide() == SELL;
            }
        }
        return false;
    }

}
