//
// -----------------------------------------------------------------------------------
// Source file: com/cboe/presentation/api/StrategyUtility.java
//
// PACKAGE: com.cboe.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

package com.cboe.presentation.api;

import com.cboe.interfaces.presentation.product.*;
import com.cboe.interfaces.presentation.marketData.DsmBidAskStruct;
import com.cboe.interfaces.presentation.marketData.DsmParameterStruct;
import com.cboe.interfaces.presentation.marketData.StrategyImpliedMarketWrapper;
import com.cboe.interfaces.presentation.common.logging.IGUILogger;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.dateTime.Date;
import com.cboe.presentation.common.formatters.ProductTypes;
import com.cboe.presentation.common.formatters.Sides;
import com.cboe.presentation.common.formatters.DisplayPriceFactory;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.marketData.StrategyImpliedMarketWrapperImpl;
import com.cboe.presentation.marketData.displayrules.StrategyType;
import com.cboe.presentation.marketData.displayrules.StrategyBidAskFlipper;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.idl.cmiConstants.PriceTypes;
import com.cboe.idl.cmiStrategy.StrategyLegStruct;
import com.cboe.idl.cmiConstants.StrategyTypes;
import com.cboe.domain.util.ValuedPrice;
import com.cboe.domain.util.PriceFactory;
import org.omg.CORBA.UserException;

import java.util.Map;
import java.util.HashMap;


/**
 * @author  Shawn Khosravani - refactored some code from Eric Maheo for determining strategy type. added DSM related stuff
 */
public class StrategyUtility
{
    public  static final String UNKNOWN_DSM_DISPLAY_STRING = "No DSM";
    private static final char   BUY = 'B';
    private static final char   SELL = 'S';

    private static final Map<Integer, Short>       futureDecisionTable;
    private static final Map<Integer, Short>       optionDecisionTable;

    private static final GUILoggerBusinessProperty loggingProperty;
    private static final String                    myClassName;
    private static final IGUILogger                logger;


    static
    {
        myClassName     = StrategyUtility.class.getName();
        logger          = GUILoggerHome.find();
        loggingProperty = GUILoggerBusinessProperty.MARKET_QUERY;       // STRATEGY_DSM;

        //Initialize the FUTURE Product decision table
        // The value for StrategyType "RATIO" will be different for FUTURES.
        futureDecisionTable = new HashMap<Integer, Short>();
        futureDecisionTable.put(13, StrategyTypes.TIME);
        futureDecisionTable.put(12, StrategyTypes.RATIO);
        futureDecisionTable.put(10, StrategyTypes.RATIO);

        //Intialize the mapping between common type and its name
        optionDecisionTable = new HashMap<Integer, Short>();
        optionDecisionTable.put(23, StrategyTypes.STRADDLE);
        optionDecisionTable.put(21, StrategyTypes.PSEUDO_STRADDLE);
        optionDecisionTable.put(19, StrategyTypes.PSEUDO_STRADDLE);
        optionDecisionTable.put(17, StrategyTypes.PSEUDO_STRADDLE);
        optionDecisionTable.put(13, StrategyTypes.TIME);
        optionDecisionTable.put(11, StrategyTypes.VERTICAL);
        optionDecisionTable.put(10, StrategyTypes.RATIO);
        optionDecisionTable.put( 9, StrategyTypes.DIAGONAL);
        optionDecisionTable.put( 7, StrategyTypes.COMBO);
    }

    public static StrategyType getStrategyType(Strategy strategy)
    {
        // can't use this because when strategy is being created in Complex Order Entry screen, it doesn't have name yet
        //     return parseStrategyType(strategy.toString(), strategy.getStrategyLegs());

        StrategyLegStruct[] legStructs   = createStrategyLegStruct(strategy);
        Product          [] products     = createProducts(strategy);

        return getStrategyType(legStructs, strategy.getStrategyLegs(), products);
    }

    public static StrategyType getStrategyType(StrategyLegStruct[] legStructs, StrategyLeg[] legs, Product[] products)
    {
        // can't use this because when strategy is being created in Complex Order Entry screen, it doesn't have name yet
        //     return parseStrategyType(strategy.toString(), strategy.getStrategyLegs());

        StrategyType returnType   = StrategyType.UNKNOWN;
        short        strategyType = determineStrategyType(legStructs, products);

        switch(strategyType)
        {
            case StrategyTypes.TIME:
                returnType = StrategyType.TIME;
                break;
            case StrategyTypes.VERTICAL:
                returnType =  StrategyType.VERTICAL;
                break;
            default:   // StrategyTypes.RATIO, STRADDLE, PSEUDO_STRADDLE, DIAGONAL, COMBO
                if (isThreeOptions(products) && isButterflyStrategy(legs))
                {
                    returnType =  StrategyType.BUTTERFLY;
                }
                // else remains StrategyType.UNKNOWN;
                break;
        }
        logDebug(myClassName + ".getStrategyType:", " returnType=" + returnType);

        return returnType;
    }

    /**
     * Standartize the Bid price to display a positive number when the price is negative
     * and display a negative number when the price is positive.
     * @param bid price to standartize.
     * @return the bid price standartized.
     */
    public static Price standartizeBidPrice(Price bid)
    {
        if (bid == null)
        {
            throw new IllegalArgumentException("Bid price cannot be null.");
        }
        if (bid.isNoPrice())
        {
            return bid;
        }
        if (bid.isMarketPrice())
        {
            return bid;
        }
        if (bid.isCabinetPrice())
        {
            return bid;
        }
        return DisplayPriceFactory.create( bid.toDouble() * (-1) );//returned the opposite value.
    }

// Unused
//    /*
//     * Parse the strategy name from the marketDisplayInfo.
//     */
//    private static StrategyType parseStrategyType(String serieName, StrategyLeg[] legs)
//    {
//        StrategyType name = StrategyType.UNKNOWN;
//
//        for (StrategyType entry: StrategyType.values() )
//        {
//            //a startWith is subject to misqualification. ie: abc and abcde -- So far (and shouldn't happen) there isn't two strategy names started by the same characters.
//            //ie: serieName is like Vertical 20090620 80.0000:1242654812341 and entry is Vertical.
//            if (serieName.startsWith(entry.toString()))
//            {
//                name = entry;
//                //Alter the logic because there is no label Butterfly but only a generic label for spread with 3 legs.
//                //So once it finds a spread with 3 legs it needs to be checked if it has a butterfly strategy.
//                if (name.equals(StrategyType.SPREAD_WITH_3_LEGS) && isButterflyStrategy(legs))
//                {
//                    name = StrategyType.BUTTERFLY; //override the previous spread with 3 legs assignement to butterfly.
//                }
//                break;
//            }
//        }
//        return name;
//    }

    /**
     * Check if the MarketDisplayInfo has a butterfly strategy.
     * A butterfly strategy is defined in this method by having 3 legs and with a
     * ratio of 1:2:1 and a S/B/S or B/S/B in this order, all CALL or all PUT and all experition dates are the same.
     * This method doesn't check that the strike prices are in ascendant order with same interval (ie: 15:20:25).
     *
     * @return true if the spread 3 legs strategy is a butterfly strategy.
     */
    private static boolean isButterflyStrategy(StrategyLeg[] legs)
    {
        if (legs == null || legs.length != 3){
            if (legs == null)
            {
                GUILoggerHome.find().exception("legs for the strategy cannot be null.", new IllegalStateException("strategy with no leg.").fillInStackTrace());
            }
            else
            {
                GUILoggerHome.find().exception("Found an illegal spread 3 legs strategy with " + legs.length + " legs.", new IllegalStateException("Illegal number of legs for spread with 3 legs strategy.").fillInStackTrace());
            }
            return false;//things went badly if we reach this case. -- not much we can other than logging it.
        }
        legs = reformatButterflyLegs(legs);
        //strategy butterfly requires a lot of conditions...
        if (legs[0].getRatioQuantity() == 1 &&
                legs[1].getRatioQuantity() == 2 && //test with the ratio is 1:2:1
                legs[2].getRatioQuantity() == 1)
        {
            if (((legs[0].getSide() == BUY && legs[1].getSide() == SELL && legs[2].getSide() == BUY) || //test if legs are Buy:Sell:Buy or Sell:Buy:Sell
                    (legs[0].getSide() == SELL && legs[1].getSide() == BUY && legs[2].getSide() == SELL)) &&
                    ((legs[0].getProduct().getProductNameStruct().optionType == legs[1].getProduct().getProductNameStruct().optionType) // test with all 3 legs are all CALL or all PUT
                            && (legs[0].getProduct().getProductNameStruct().optionType == legs[2].getProduct().getProductNameStruct().optionType)) &&
                            isSameExperationDate(legs[0].getProduct().getExpirationDate(), legs[1].getProduct().getExpirationDate(), legs[2].getProduct().getExpirationDate()))  //test if all 3 legs have the same expiration date (month/day/year).
            {
                return true;
            }
        }
        return false;
    }

    /**
     * The method test if the Years, the Months and Days are the same accross all date passed as argument.
     *
     * @param date0 expiration for leg 0
     * @param date1 expiration for leg 1
     * @param date2 expiration for leg 2
     * @return true if same expiration date.
     */
    private static boolean isSameExperationDate(final Date date0,final  Date date1,final Date date2)
    {
        return date0.compareTo(date1)==0 && date0.compareTo(date2)==0;
    }

    /**
     * Attempt to reformat the legs order to a ratio 1:2:1 like a butterfly strategy.
     * This method can throw NullPointerException and IndexOutOfBoundsException.
     * So it should be checked that it has only 3 legs and legs isn't null before using this method.
     *
     * @param legs of the strategy.
     * @return a copy of the legs reformatted.
     */
    private static StrategyLeg[] reformatButterflyLegs(StrategyLeg[] legs)
    {
         StrategyLeg[] copy = new StrategyLeg[3];//it has to be 3, this was tested previously.
         int count = 0;
         int ratioTwoPosition=-1;//position for the leg with ratio 2.

         for (StrategyLeg leg : legs)
         {
             if ( leg.getRatioQuantity() == 2 )
             {
                 ratioTwoPosition = count;
             }
             copy[count++] = leg;
         }
         if (ratioTwoPosition > -1)
         {
             StrategyLeg tmp = copy[1];
             copy[1]=legs[ratioTwoPosition];
             copy[ratioTwoPosition]=tmp;
         }
         return copy;
    }

    public static boolean isEquityOrIndex(Product product)
    {
        short productType = product.getProductType();
        return productType == ProductTypes.EQUITY  ||  productType == ProductTypes.INDEX;
    }

    public static boolean containsUnderlyingLeg(SessionStrategy sessionStrategy)
    {
        return findEquityLegIndex(sessionStrategy.getSessionStrategyLegs()) >= 0;
    }

    public static int findEquityLegIndex(StrategyLeg[] legs)
    {
        int i = 0;
        for ( ; i < legs.length ; ++i)
        {
            if (isEquityOrIndex(legs[i].getProduct()))
            {
                break;
            }
        }
        return i < legs.length ? i : -1;
    }

    public static CurrentMarketStruct[] getCmForLegs(StrategyLeg[] legs, CurrentMarketGetter cmGetter)
    {
        CurrentMarketStruct[] cm = new CurrentMarketStruct[legs.length];

        for (int i = 0; i < legs.length; i++)
        {
            Product product = legs[i].getProduct();

            try
            {
                if (isEquityOrIndex(product))
                {
                    cm[i] = cmGetter.getUnderlyingCM(product);
                }
                else
                {
                    cm[i] = cmGetter.getCurrentMarket(product);
                }
            }
            catch (Exception ex)     // TODO UserException ??
            {
                DefaultExceptionHandlerHome.find().process(ex, "StrategyUtility.getCmForLegs: Could not MD for legs to calculate DSM: classKey=" +
                                                           product.getProductKeysStruct().classKey + ", product=" + product);
                cm = null;
                break;
            }
        }
        return cm;
    }

    public static DsmBidAskStruct getStrategyDSM(SessionStrategy sessionStrategy, CurrentMarketGetter cmGetter)
    {
        StrategyLeg[]   strategyLegs = sessionStrategy.getSessionStrategyLegs();
        PriceStruct     dsmBidPrice  = new PriceStruct(PriceTypes.NO_PRICE, 0, 0);
        PriceStruct     dsmAskPrice  = new PriceStruct(PriceTypes.NO_PRICE, 0, 0);
        DsmBidAskStruct dsmBidAsk    = new DsmBidAskStruct(dsmBidPrice, dsmAskPrice);

        CurrentMarketStruct[] cm            = getCmForLegs(strategyLegs, cmGetter);
        DsmParameterStruct [] dsmParameters = new DsmParameterStruct[strategyLegs.length];

        boolean allArePriced = true;
        for (int i = 0; allArePriced  &&  i < strategyLegs.length; i++)
        {
            if (cm == null  ||  cm[i] == null)
            {
                allArePriced = false;  // kicks out of the for loop
            }
            else
            {
                dsmParameters[i] = createDsmParameterStruct(strategyLegs[i], cm[i].bidPrice, cm[i].askPrice);
            }
        } // for each leg

        if (allArePriced)
        {
            dsmBidAsk = StrategyUtility.calculateDSM(sessionStrategy, dsmParameters);   // calls version 2
        }
        return dsmBidAsk;
    } // end method getStrategyDSM

   public static DsmParameterStruct createDsmParameterStruct(StrategyLeg strategyLeg,
                                                             PriceStruct bidPrice, PriceStruct askPrice)
   {
       DsmParameterStruct ret = new DsmParameterStruct();

       ret.side     = strategyLeg.getSide();
       ret.ratio    = strategyLeg.getRatioQuantity();
       ret.product  = strategyLeg.getProduct();
       ret.bidPrice = bidPrice;
       ret.askPrice = askPrice;

       if (isEquityOrIndex(ret.product))
       {
           ret.ratio /= 100.0;
       }
       if (isDebug())
       {
           StringBuffer msg = new StringBuffer();
               msg.append("product=").append(ret.product)
                  .append("] ratio=").append(ret.ratio)
                  .append(", bid=").append(DisplayPriceFactory.createValuedPrice(bidPrice))
                  .append(", ask=").append(DisplayPriceFactory.createValuedPrice(askPrice));
           logDebug(myClassName + ".createDsmParameterStruct:", msg.toString());
       }

       return ret;
   }

    // this version is called from Complex Order Entry screen where we create the Strategy product which may not exist yet
    public static DsmBidAskStruct calculateDSM(DsmParameterStruct[] dsmParameters,                          // version 1
                                               StrategyLegStruct [] strategyLegStructs,
                                               StrategyLeg       [] strategyLegs,
                                               Product           [] products)
    {
        DsmBidAskStruct displayedDSM  = null;
        DsmBidAskStruct calculatedDSM = calculateDSM(dsmParameters);        // calls version 3
        if (calculatedDSM != null)
        {
            StrategyType strategyType = getStrategyType(strategyLegStructs, strategyLegs, products);
            displayedDSM = getDisplayedDSM(strategyType, strategyLegs, calculatedDSM);
        }
        return displayedDSM;
    } // end method calculateDSM

    // this version is called when the Strategy product already exists. it calculates DSM and sets the flip flag
    public static DsmBidAskStruct calculateDSM(Strategy strategy, DsmParameterStruct[] dsmParameters)       // version 2
    {
        DsmBidAskStruct displayedDSM  = null;
        DsmBidAskStruct calculatedDSM = calculateDSM(dsmParameters);        // calls version 3
        if (calculatedDSM != null)
        {
            StrategyType strategyType = getStrategyType(strategy);
            displayedDSM = getDisplayedDSM(strategyType, strategy.getStrategyLegs(), calculatedDSM);
        }
        return displayedDSM;
    }

    public static DsmBidAskStruct getDisplayedDSM(StrategyType strategyType, StrategyLeg[] strategyLegs, DsmBidAskStruct calculatedDSM)
    {
        DsmBidAskStruct displayedDSM = null;
        if (calculatedDSM != null)
        {
            calculatedDSM.setFlipped(StrategyBidAskFlipper.isDsmBidAskFlipped(strategyType, strategyLegs, calculatedDSM));

            double calculatedBidValue = PriceFactory.create(calculatedDSM.getBid()).toDouble();
            double calculatedAskValue = PriceFactory.create(calculatedDSM.getAsk()).toDouble();

            displayedDSM = setBidSignAndFlip(calculatedDSM);

            double  displayedBidValue = PriceFactory.create(displayedDSM.getBid()).toDouble();
            double  displayedAskValue = PriceFactory.create(displayedDSM.getAsk()).toDouble();
            boolean isFlipped         = isFlipped(calculatedBidValue, calculatedAskValue, displayedBidValue, displayedAskValue);

            displayedDSM.setIsBidDebit(isDebit(displayedBidValue, isFlipped));
            displayedDSM.setIsAskDebit(isDebit(displayedAskValue, isFlipped));

            logDebug(myClassName + ".getDisplayedDSM(4): ", "calc bid/ask=" + calculatedBidValue + "/="
                   + calculatedAskValue + ", disp bid/ask=" + displayedBidValue + "/" + displayedAskValue
                   + ", isFlipped="+ isFlipped + ", bid.isDebit=" + displayedDSM.isBidDebit()
                   + ", ask.isDebit=" + displayedDSM.isAskDebit());
        }
        return displayedDSM;
    }

    // this calculates the DSM bases on leg ratios, sides, and bid / ask prices. it is called both when the Strategy
    // product already exists and when it's being defined. it is public so that it can be called from DSMUnitTest, others
    // should call one of the other 2 versions above
    public static DsmBidAskStruct calculateDSM(DsmParameterStruct[] dsmParameters)                          // version 3
    {
        String      methodName       = myClassName + ".calculateDSM(1):";
        boolean allLegsHavePrice = true;
        double      dsmSame          = 0.0;
        double      dsmOpposite      = 0.0;
        PriceStruct dsmBidPrice;
        PriceStruct dsmAskPrice;

        for (int i = 0; allLegsHavePrice  &&  i < dsmParameters.length; i++)
        {
            if (dsmParameters[i].bidPrice.type == PriceTypes.NO_PRICE
            ||  dsmParameters[i].askPrice.type == PriceTypes.NO_PRICE)
            {
                allLegsHavePrice = false;
            }
            else
            {
                ValuedPrice bidPrice = new ValuedPrice(dsmParameters[i].bidPrice);
                ValuedPrice askPrice = new ValuedPrice(dsmParameters[i].askPrice);
                double      legBid   = bidPrice.toDouble() * dsmParameters[i].ratio;
                double      legAsk   = askPrice.toDouble() * dsmParameters[i].ratio;
                logDebug(methodName, " for leg # " + i + ": side=" +
                         dsmParameters[i].side + ", legBid=" + legBid + ", legAsk=" + legAsk);

                if (Sides.isBuyEquivalent(dsmParameters[i].side))
                {
                    dsmSame     -= legBid;
                    dsmOpposite += legAsk;
                    logDebug(methodName, "  B side. dsmSame=" + dsmSame + " dsmOpposite=" + dsmOpposite);
                } // end if Buy side
                else
                {
                    dsmSame     += legAsk;
                    dsmOpposite -= legBid;
                    logDebug(methodName, "  S side. dsmSame=" + dsmSame + " dsmOpposite=" + dsmOpposite);
                } // end else Sell side
            }
        } // for each leg

        logDebug(methodName, " final dsmSame=" + dsmSame + " dsmOpposite=" + dsmOpposite);

        DsmBidAskStruct ret = null;
        if (allLegsHavePrice)
        {
            dsmAskPrice = (new ValuedPrice(dsmOpposite)).toStruct();
            dsmBidPrice = (new ValuedPrice(dsmSame)).toStruct();
            ret = new DsmBidAskStruct(dsmBidPrice, dsmAskPrice);
        }
        return ret;
    } // end method calculateDSM

    public static StrategyImpliedMarketWrapperImpl convertToImpliedMarket(DsmBidAskStruct dsmBidAsk)
    {
        StrategyImpliedMarketWrapperImpl ret = null;

        if (dsmBidAsk != null)
        {
            DsmBidAskStruct displayedDSM = setBidSignAndFlip(dsmBidAsk);

            Price askPrice = PriceFactory.create(displayedDSM.getAsk());
            Price bidPrice = PriceFactory.create(displayedDSM.getBid());

            if (askPrice.isValuedPrice()  &&  bidPrice.isValuedPrice())
            {
                    ret = new StrategyImpliedMarketWrapperImpl(askPrice.toDouble(), bidPrice.toDouble());
            }
            // else                                                                                                            // TODO remove ??
            // {
            //     System.out.println("???? StrategyUtility.convertToImpliedMarket: bid or ask is not valued price" +
            //                        "\n\t\tbid=" + bid.isValuedPrice() + ", ask=" + ask.isValuedPrice());                                                         // TODO remove ??
            //     Exception ex = new Exception("*** STACK TRACE *** ");
            //     ex.printStackTrace();
            // }
        }
        return ret;
    }

    public static DsmBidAskStruct setBidSignAndFlip(DsmBidAskStruct dsmBidAsk)
    {
        DsmBidAskStruct dsmBidAskStruct = new DsmBidAskStruct(dsmBidAsk);
        dsmBidAskStruct.setFlipped(dsmBidAsk.isFlipped());
        
        if (dsmBidAsk.isFlipped())
        {
            dsmBidAskStruct.setBid(dsmBidAsk.getAsk());
            dsmBidAskStruct.setAsk(dsmBidAsk.getBid());
        }

        Price bidPrice             = PriceFactory.create(dsmBidAskStruct.getBid());
        Price bidAfterSignChange   = PriceFactory.create(-1.0 * bidPrice.toDouble());
        PriceStruct bidPriceStruct = bidAfterSignChange.toStruct();
        dsmBidAskStruct.setBid(bidPriceStruct);

        return dsmBidAskStruct;
    }

    public static boolean isFlipped(StrategyImpliedMarketWrapper calculatedIM, StrategyImpliedMarketWrapper displayedIM)
    {
        return Math.abs(calculatedIM.getImpliedSame()) == Math.abs(displayedIM.getImpliedOpposite());
         // && Math.abs(calculatedIM.getImpliedSame()) != Math.abs(displayedIM.getImpliedSame());
    }

    public static boolean isFlipped(double calculatedBid, double calculatedAsk, double displayedBid, double displayedAsk)
    {
        return Math.abs(calculatedBid) == Math.abs(displayedAsk);
         // && Math.abs(calculatedBid) != Math.abs(displayedBid);
    }

    public static boolean isDebit(double value, boolean isFlipped)
    {
        return (value > 0.0  &&  !isFlipped)
           ||  (value < 0.0  &&   isFlipped);
    }

    public static boolean isUnderlying(Product product)
    {
        return product.getProductType() == ProductTypes.EQUITY
            || product.getProductType() == ProductTypes.INDEX;
    }

    private static boolean isThreeOptions(Product[] products)
    {
        return products.length == 3
            && products[0].getProductType() == ProductTypes.OPTION
            && products[1].getProductType() == ProductTypes.OPTION
            && products[2].getProductType() == ProductTypes.OPTION;
    }

    private static StrategyLegStruct[] createStrategyLegStruct(Strategy strategy)
    {
        StrategyLeg      [] legs       = strategy.getStrategyLegs();
        StrategyLegStruct[] legStructs = new StrategyLegStruct[legs.length];
        for (int i = 0; i < legs.length; ++i)
        {
            StrategyLeg leg = legs[i];
            legStructs[i] = new StrategyLegStruct(leg.getProductKey(), leg.getRatioQuantity(), leg.getSide());
        }
        return legStructs;
    }

    private static Product[] createProducts(Strategy strategy)
    {
        StrategyLeg[] legs     = strategy.getStrategyLegs();
        Product    [] products = new Product[legs.length];
        for (int i = 0; i < legs.length; ++i)
        {
            products[i] = legs[i].getProduct();
        }
        return products;
    }

    private static boolean isProductUnderlying(Product product)
    {
        return product.getProductType() == ProductTypes.EQUITY  ||  product.getProductType() == ProductTypes.INDEX;
    }

    // borrowed and refactored from: com.cboe.businessServices.productService.StrategyFactory.determineStrategyType
    private static short determineStrategyType(StrategyLegStruct[] legs, Product[] legProducts)
    {
        // If any leg is an equity or an index product then it is an  Equity legged strategy -
        // called a Buy_Write but this is a misnomer
        for (int i = 0; i < legProducts.length; i++)
        {
            if ( isProductUnderlying ( legProducts[i] ))
            {
                return StrategyTypes.BUY_WRITE;
            }
        }

        // All other strategies are options only and are named in constants when legs are 2
        if ( legs.length != 2 )
        {
               return StrategyTypes.UNKNOWN; //no type defined for option spread whose number of legs is not 2
        }

        boolean sameOptionTypes;
        boolean sameMonths;
        boolean sameStrikes;
        boolean oneToOne;
        boolean sameTradeTypes;
        Short   type;


        //  2 legged spreads only will use these variables
        StrategyLegStruct leg1 = legs[0];
        StrategyLegStruct leg2 = legs[1];

        // futures only make sense for TIME or OTHER
        // TIME type is based solely off the date so simplify the check
        // so that it sets up a TIME or OTHER decission.

        // Last 2 values checked are from leg definitions
        oneToOne        = leg1.ratioQuantity == 1 && leg2.ratioQuantity == 1;
        sameTradeTypes  = leg1.side == leg2.side;
        sameMonths      = legProducts[0].getExpirationDate().getDate().getTime() ==
                          legProducts[1].getExpirationDate().getDate().getTime();

        int typeValue;
        if ( legProducts[0].getProductType() == ProductTypes.FUTURE )
        {
            typeValue  = (sameTradeTypes  ? 16 : 0)
                       + 8                              // (sameOptionTypes ?  8 : 0)  sameOptionTypes is always true
                       + 4                              // (sameStrikes     ?  4 : 0)  sameStrikes is always true
                       + (sameMonths      ?  2 : 0)
                       + (oneToOne        ?  1 : 0);
            type = futureDecisionTable.get(typeValue);

            logDebug(myClassName + ".determineStrategyType:", " Future Strategy Type bitval=" + typeValue + ", type=" + type);
        }
        else // ProductTypes.OPTION
        {
            sameOptionTypes = legProducts[0].getProductNameStruct().optionType ==
                              legProducts[1].getProductNameStruct().optionType;
            sameStrikes     = legProducts[0].getExercisePrice().equals(legProducts[1].getExercisePrice());
            // The five conditions are combined into a five position binary number
            typeValue = (sameTradeTypes  ? 16 : 0)
                      + (sameOptionTypes ?  8 : 0)
                      + (sameStrikes     ?  4 : 0)
                      + (sameMonths      ?  2 : 0)
                      + (oneToOne        ?  1 : 0);
            type = optionDecisionTable.get(typeValue);

            logDebug(myClassName + ".determineStrategyType:", " Option Strategy Type bitval=" + typeValue + ", type=" + type);
        }

        if (type == null)
        {
            return StrategyTypes.UNKNOWN;
        }
        return type;
    }

    private static void logDebug(String methodName, String msg)
    {
        logDebug(true, methodName, msg);
    }

    private static void logDebug(boolean condition, String methodName, String msg)
    {
        if (isDebug(condition))
        {
            logger.debug(methodName, loggingProperty, msg);
        }
    }

    private static boolean isDebug()
    {
        return isDebug(true);
    }

    private static boolean isDebug(boolean condition)
    {
        return condition && logger.isDebugOn() && logger.isPropertyOn(loggingProperty);
    }


    // inner class /////////////////////////////////////
    public static abstract class CurrentMarketGetter        // TODO make it an interface ??
    {
        protected abstract CurrentMarketStruct getCurrentMarket(Product product) throws UserException;
        protected abstract CurrentMarketStruct getUnderlyingCM (Product product) throws UserException;
    }
}
