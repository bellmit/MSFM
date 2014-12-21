package com.cboe.domain.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiConstants.AuctionTypes;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
import com.cboe.idl.cmiMarketData.ExchangeVolumeStruct;
import com.cboe.idl.cmiMarketData.NBBOStruct;
import com.cboe.idl.constants.OrderRoutingReasons;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.businessServices.MarketDataService;
import com.cboe.interfaces.businessServices.MarketDataServiceHome;
import com.cboe.interfaces.businessServices.SweepEvaluationService;
import com.cboe.interfaces.domain.HALStruct;
import com.cboe.interfaces.domain.HandlingInstruction;
import com.cboe.interfaces.domain.Order;
import com.cboe.interfaces.domain.OrderBook;
import com.cboe.interfaces.domain.OrderBookHome;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.PriceRange;
import com.cboe.interfaces.domain.Side;
import com.cboe.interfaces.domain.Tradable;
import com.cboe.interfaces.domain.TradingClass;
import com.cboe.interfaces.domain.bestQuote.BestQuote;
import com.cboe.interfaces.domain.marketData.MarketData;
import com.cboe.interfaces.domain.marketData.MarketDataHome;
import com.cboe.interfaces.domain.optionsLinkage.AllExchangesBBO;
import com.cboe.interfaces.domain.optionsLinkage.SweepElement;
import com.cboe.interfaces.domain.tradingProperty.AllowedHALTypes;
import com.cboe.util.ExceptionBuilder;


public class HALHelper
{
    private static OrderBookHome orderBookHome;
    private static MarketDataHome marketDataHome;
    private static Price noPrice = PriceFactory.create(Price.NO_PRICE_STRING);
    private static final Price zeroPrice = PriceFactory.create(0.0);
    private static Price marketPrice = PriceFactory.create(Price.MARKET_STRING);
    private static MarketDataService mds;

    static class QueriedMarketData
    {
        Price oppositeSideNBBOPrice = noPrice;
        Price sameSideBOTRPrice = noPrice;
        Price oppositeSideBOTRPrice = noPrice;
        int oppositeSideBOTRQuantity = 0;
    }

    /*
     * Return the HAL information based on MarketData.
     */
    public static HALStruct getHALInformation(Order order, short auctionType)
    {
        MarketData marketData = getMarketData(order);
        QueriedMarketData md = getMarketDataPrices(marketData, order, auctionType);

        return getHALStruct(order,
                            auctionType,
                            md.oppositeSideNBBOPrice,
                            md.sameSideBOTRPrice,
                            md.oppositeSideBOTRPrice,
                            md.oppositeSideBOTRQuantity);

    }

    /*
     * Return the HAL information based on MarketData.
     */
    public static HALStruct getSweepHALInformation(Order order, short auctionType)
    {
        MarketData marketData = getMarketData(order);
        QueriedMarketData md = getMarketDataPrices(marketData, order);

        HALStruct sweepHALStruct = getHALStruct(order,
                                    auctionType,
                                    md.oppositeSideNBBOPrice,
                                    md.sameSideBOTRPrice,
                                    md.oppositeSideBOTRPrice,
                                    md.oppositeSideBOTRQuantity);
        sweepHALStruct.halFlashPrice = getSweepFlashPrice(order, sweepHALStruct.halOrderType, md.oppositeSideNBBOPrice, getCBOEPriceConsideringContingency(order.getProductKey().intValue(), order.getSide().getOtherSide()));
        return sweepHALStruct;

    }

    /*
     * Return the HAL information based on the provided BOTR.
     */
    public static HALStruct getRevisedHALInformation(Order order, short auctionType, HALStruct halStruct)
    {
        int productKey = order.getProductKey().intValue();

        Price oppositeSideNBBOPrice = getNBBOPrice(halStruct.oppositeSideBOTRPrice, productKey, order.getSide().getOtherSide());

        return getHALStruct(order, auctionType, oppositeSideNBBOPrice, halStruct.sameSideBOTRPrice, halStruct.oppositeSideBOTRPrice, halStruct.oppositeSideBOTRQuantity);
    }

    public static HALStruct getRevisedHALInformation(Order order, HALStruct halStruct)
    {
    	return getRevisedHALInformation(order, halStruct.auctionType, halStruct);
    }
    
    public static HALStruct getRevisedHALInformationConsideringContingency(Order order, short auctionType, HALStruct halStruct, OrderBook orderBook)
    {
        Price oppositeSideNBBOPrice = getNBBOPriceConsideringContingency(halStruct.oppositeSideBOTRPrice, order.getSide().getOtherSide(), orderBook);

        HALStruct struct = getHALStruct(order, auctionType, oppositeSideNBBOPrice, halStruct.sameSideBOTRPrice, halStruct.oppositeSideBOTRPrice, halStruct.oppositeSideBOTRQuantity);
        struct.oppositeSideCBOEPrice = getCBOEPriceConsideringContingency(order.getSide().getOtherSide(), orderBook);
        return struct;
    }

    private static HALStruct getHALStruct(Order order,
                                          short auctionType,
                                          Price oppositeSideNBBOPrice,
                                          Price sameSideBOTRPrice,
                                          Price oppositeSideBOTRPrice,
                                          int oppositeSideBOTRQuantity)
    {
        short halOrderType = getHALOrderType(order, sameSideBOTRPrice, oppositeSideBOTRPrice);
        Price flashPrice = getFlashPrice(order, halOrderType, oppositeSideNBBOPrice);
        int cboeTicksAwayNBBO = ticksOfCBOEFromNBBO(order, oppositeSideNBBOPrice);
        boolean isCBOEWithinNTicksFromNBBO = isCBOEWithinNTicksFromNBBO(order, auctionType, cboeTicksAwayNBBO);
        boolean isCurrentMarketNBBO = (cboeTicksAwayNBBO == 0) ? true : false;
        Price oppositeSideCBOEPrice = getCBOEPrice(order.getProductKey().intValue(), order.getSide().getOtherSide());

        HALStruct hal = new HALStruct(halOrderType,
                                      flashPrice,
                                      isCBOEWithinNTicksFromNBBO,
                                      isCurrentMarketNBBO,
                                      sameSideBOTRPrice,
                                      oppositeSideBOTRPrice,
                                      oppositeSideBOTRQuantity,
                                      oppositeSideCBOEPrice,
                                      auctionType);

        if (Log.isDebugOn())
        {
            Log.debug("HALHelper >>> order: " + order + " " + hal);
        }

        return hal;
    }

    /*
     * Returns the HAL order type by comparing it with CBOE and NBBO.
     */
    private static short getHALOrderType(Order order, Price sameSideBOTRPrice, Price oppositeSideBOTRPrice)
    {
        short halOrderType = AllowedHALTypes.NONE_HAL;

        // if opposite side BOTR is not valid (null || NP || 0.0), HAL order will not be HAL'ed
        if (null == oppositeSideBOTRPrice || oppositeSideBOTRPrice.isNoPrice() || isZeroPrice(oppositeSideBOTRPrice))
        {
            return halOrderType;
        }

        // MKT order is always NBBO_REJECT
        if (order.getPrice().isMarketPrice())
        {
            return AllowedHALTypes.NBBO_REJECT;
        }


        OrderBook orderBook = getOrderBookHome().find(order.getProductKey().intValue());
        if (orderBook.isLocked())//then, consider botr price to determine the haltype
        {
            if (crossesBOTR(order, oppositeSideBOTRPrice))
            {
                halOrderType = AllowedHALTypes.TWEENER_LOCK;
                if (orderBook.crossesMarket(order, null))
                {
                    halOrderType = AllowedHALTypes.NBBO_REJECT;
                }
            }
        }
        else
        {
            if (orderBook.bettersMarket(order))//betters market but doesn't cross bestbook
            {
                if (bettersBOTR(order, sameSideBOTRPrice, oppositeSideBOTRPrice))
                {
                    halOrderType = AllowedHALTypes.TWEENER;
                }
                else if (crossesBOTR(order, oppositeSideBOTRPrice))
                {
                    halOrderType = AllowedHALTypes.TWEENER_LOCK;
                }
            }
            else if (orderBook.crossesMarket(order, null))
            {
                if (crossesBOTR(order, oppositeSideBOTRPrice))
                {
                    halOrderType = AllowedHALTypes.NBBO_REJECT;
                }
            }
        }
        return halOrderType;
    }

    /*
     * Returns flash price - for TWEENER, order price is used as HAL flash price
     *       for TWEENER_LOCK and NBBO_REJECT, the opposite NBBO price is used
     */
    private static Price getFlashPrice(Order order, short halOrderType, Price oppositeSideNBBOPrice)
    {
        if (halOrderType == AllowedHALTypes.TWEENER)
        {
            return order.getPrice();
        }
        else
        {
            return oppositeSideNBBOPrice;
        }
    }

    /*
     * Returns flash price - for TWEENER, order price is used as HAL flash price
     *       for TWEENER_LOCK and NBBO_REJECT, the opposite NBBO price is used.
     *       Also considers NONE_HAL conditions.
     */
    private static Price getSweepFlashPrice(Order order, short halOrderType, Price oppositeSideNBBOPrice, Price oppositeSideCBOEPrice)
    {
        // MKT order is always considered as NBBO_REJECT so use Opposite side NBBO price
        if (order.getPrice().isMarketPrice())
        {
            if (oppositeSideNBBOPrice == null || oppositeSideNBBOPrice.isNoPrice() || HALHelper.isZeroPrice(oppositeSideNBBOPrice))
            {
                return getValidPrice(oppositeSideCBOEPrice);
            }
            return oppositeSideNBBOPrice;
        }
        if (halOrderType == AllowedHALTypes.TWEENER)
        {
            return order.getPrice();
        }
        else if(halOrderType == AllowedHALTypes.NONE_HAL)
        {
            Side otherSide = order.getSide().getOtherSide();
            return (otherSide.isFirstBetter(order.getPrice(), oppositeSideNBBOPrice) ? order.getPrice() : oppositeSideNBBOPrice);
        }
        else
        {
            return oppositeSideNBBOPrice;
        }
    }

    public static Price getValidPrice(Price paramPrice)
    {
        return (paramPrice == null || paramPrice.isNoPrice() || paramPrice.isMarketPrice()) ? zeroPrice : paramPrice;
    }

    /*
     * Returns true if CBOE is within N ticks of NBBO, where N is defined by
     * trading property AuctionOrderTicksAwayFromNBBO.
     */
    private static boolean isCBOEWithinNTicksFromNBBO(Order order, short auctionType, int cboeTicksAwayNBBO)
    {
        OrderBook orderBook = getOrderBookHome().find(order.getProductKey().intValue());
        TradingClass tradingClass = orderBook.getTradingProduct().getTradingClass();

        // getting TicksAwayFromNBBO is only required for HAL, not for NEW_HAL
        int auctionOrderTicksAwayFromNBBO = 0;
        if (auctionType != InternalAuctionTypes.AUCTION_NEW_HAL) {
        	auctionOrderTicksAwayFromNBBO = tradingClass.getAuctionOrderTicksAwayFromNBBO(auctionType);
        }

        if ((cboeTicksAwayNBBO >= 0) && (cboeTicksAwayNBBO <= auctionOrderTicksAwayFromNBBO))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /*
     * Returns the number of ticks that CBOE is away from NBBO.
     * if NBBO or CBOE is not set
     *      return -1
     * else
     *      return ticks difference     // this number should never be negative here since CBOE could be NBBO
     */
    private static int ticksOfCBOEFromNBBO(Order order, Price oppositeSideNBBOPrice)
    {
        if (oppositeSideNBBOPrice.isNoPrice())
        {
            if (Log.isDebugOn())
            {
                Log.debug("HALHelper >>> order side: " + order.getSide() + " order price: " + order.getPrice() + " NBBO: NO_PRICE NBBO.");
            }
            return -1;
        }

        OrderBook orderBook = getOrderBookHome().find(order.getProductKey().intValue());
        TradingClass tradingClass = orderBook.getTradingProduct().getTradingClass();

        Price cboeOppositePrice = PriceFactory.create(Price.NO_PRICE_STRING);
        int cboeTicksAwayNBBO = 0;

        if (order.getSide().isBuySide())
        {
            cboeOppositePrice = orderBook.getBestNonContingentAskPrice();
            if (cboeOppositePrice.isNoPrice())
            {
                if (Log.isDebugOn())
                {
                    Log.debug("HALHelper >>> order side: " + order.getSide() + " order price: " + order.getPrice() + " CBOE: NO_PRICE NBBO: " + oppositeSideNBBOPrice + ".");
                }
                return -1;
            }

            cboeTicksAwayNBBO = tradingClass.getTickDifference(oppositeSideNBBOPrice, cboeOppositePrice);
        }
        else
        {
            cboeOppositePrice = orderBook.getBestNonContingentBidPrice();
            if (cboeOppositePrice.isNoPrice())
            {
                if (Log.isDebugOn())
                {
                    Log.debug("HALHelper >>> order side: " + order.getSide() + " order price: " + order.getPrice() + " CBOE: NO_PRICE NBBO: " + oppositeSideNBBOPrice + ".");
                }
                return -1;
            }

            cboeTicksAwayNBBO = tradingClass.getTickDifference(cboeOppositePrice, oppositeSideNBBOPrice);
        }

        if (Log.isDebugOn())
        {
            Log.debug("HALHelper >>> order side: " + order.getSide() + " order price: " + order.getPrice() + " CBOE: " + cboeOppositePrice + " NBBO: " + oppositeSideNBBOPrice + ", and CBOE is " + cboeTicksAwayNBBO + " ticks away from NBBO.");
        }

        return cboeTicksAwayNBBO;
    }

    /**
     * Returns true if order betters the same side NBBO, and does not cross
     * the opposite side NBBO.
     */
    public static boolean bettersBOTR(Order order, Price sameSideBOTRPrice, Price oppositeSideBOTRPrice)
    {
        Side side = order.getSide();

        return side.isFirstBetter(order.getPrice(), sameSideBOTRPrice) &&
               side.getOtherSide().isFirstBetter(order.getPrice(), oppositeSideBOTRPrice);
    }

    /**
     * Returns true if order price locks or crosses NBBO on the opposite side.
     * If the tradable has MKT price, return true
     */
    public static boolean crossesBOTR(Order order, Price oppositeSideBOTRPrice)
    {
        Side side = order.getSide().getOtherSide();

        return side.areTwoPricesTradable(oppositeSideBOTRPrice, order.getPrice());
    }
    
    public static boolean tradableWithPrice(Order order, Price givenPrice)
    {
        return crossesBOTR(order, givenPrice);
    }

    public static Price getNBBOPrice(MarketData marketData, Side side)
    {
        Price nbboPrice = PriceFactory.create(Price.NO_PRICE_STRING);

        BestQuote nbbo = marketData.getNBBO();
        if (null != nbbo)
        {
            if (side.isBuySide())
            {
                nbboPrice = nbbo.getBidPrice();
            }
            else
            {
                nbboPrice = nbbo.getAskPrice();
            }
        }

        return nbboPrice;
    }

    public static Price getNBBOPrice(Price botrPrice, int productKey, Side side)
    {
        Price cboePrice = getCBOEPrice(productKey, side);
        return side.isFirstBetter(botrPrice, cboePrice) ? botrPrice : cboePrice;
    }

    public static Price getNBBOPrice(Price botrPrice, OrderBook orderBook, Side side)
    {
        Price cboePrice = side.isBuySide() ? orderBook.getBestNonContingentBidPrice() : orderBook.getBestNonContingentAskPrice();
        return side.isFirstBetter(botrPrice, cboePrice) ? botrPrice : cboePrice;
    }

    /**
     * Get NBBO price considering CBOE contingent market
     * HALHelper
     * @param botrPrice
     * @param productKey
     * @param side
     * @param includeContingent
     * @return
     * Price
     */
    public static Price getNBBOPriceConsideringContingency(Price botrPrice, Side side, OrderBook orderBook)
    {
    	Price cboeBestPrice = getCBOEPriceConsideringContingency(side, orderBook);
    	return side.isFirstBetter(botrPrice, cboeBestPrice) ? botrPrice : cboeBestPrice;
    }

    public static Price getCBOEPriceConsideringContingency(Side side, OrderBook orderBook)
    {
        Price cboeBestPrice = orderBook.getBestPrice(side);
        return cboeBestPrice;
    }

    public static boolean isCBOENBBO(Price botrPrice, int productKey, Side side, Price cboePrice)
    {
        return side.isFirstBetter(botrPrice, cboePrice) ? false : true;
    }

    public static boolean isCBOENBBO(Price botrPrice, int productKey, Side side)
    {
    	Price cboePrice = getCBOEPrice(productKey, side);
    	return isCBOENBBO(botrPrice, productKey, side, cboePrice);
    }
    
    public static boolean isCBOENBBOConsideringContingency(Price botrPrice, int productKey, Side side)
    {
        Price cboePrice = getCBOEPriceConsideringContingency(productKey, side);
        return isCBOENBBO(botrPrice, productKey, side, cboePrice);
    }

    public static int getBOTRQuantity(MarketData marketData, Side side)
    {
        int botrQuantity = 0;
        if (marketData != null)
        {
            botrQuantity = marketData.getBotrQuantityBySide(side);
        }

        return botrQuantity;
    }

    public static Price getBOTRPrice(MarketData marketData, Side side)
    {
        Price botrPrice = null;

        BestQuote botr = marketData.getBOTR();
        if (null != botr)
        {
            if (side.isBuySide())
            {
                botrPrice = botr.getBidPrice();
            }
            else
            {
                botrPrice = botr.getAskPrice();
            }
        }
        if (botrPrice == null)
        {
        	botrPrice = PriceFactory.create(Price.NO_PRICE_STRING);
        }

        return botrPrice;
    }

    public static Price getBOTRPrice(NBBOStruct botr, Side side)
    {
    	return getBestPrice(botr, side);
    }

    public static MarketData getMarketData(Order order)
    {
        String sessionName = order.getActiveSession();
        int productKey = order.getProductKey().intValue();

        try
        {
            return getMarketDataHome().findByProduct(sessionName, productKey);
        }
        catch (Exception e)
        {
            Log.exception("Can not get market data for session:product " + sessionName + ":" + productKey, e);
        }

        return null;
    }

    private static OrderBookHome getOrderBookHome()
    {
        if (orderBookHome == null)
        {
            try
            {
                orderBookHome = (OrderBookHome)HomeFactory.getInstance().findHome(OrderBookHome.HOME_NAME);
            }
            catch (Exception e)
            {
                Log.alarm("Unable to get OrderBookHome");
            }
        }
        return orderBookHome;
    }

    private static MarketDataHome getMarketDataHome()
    {
        if (marketDataHome == null)
        {
            try
            {
                marketDataHome = (MarketDataHome)HomeFactory.getInstance().findHome(MarketDataHome.HOME_NAME);
            }
            catch (Exception e)
            {
                Log.alarm("Unable to get MarketDataHome");
            }
        }
        return marketDataHome;
    }

    public static Price getPossibleHALTradePrice(Order auctionedOrder, Side inComingOrderSide, Price inComingOrderPrice, Price orderSideBotrPrice, Price oppositeToOrderSideBotrPrice)
    { // Returns a valid price object if the incoming order can trade against the flashing order.
      // If the trade is not possible then null is returned.

        if (Log.isDebugOn())
        {
            StringBuilder buffer = new StringBuilder(200);
            buffer.append("getPossibleHALTradePrice():")
            .append("\n AuctionedOrderLimitPrice:")
            .append(auctionedOrder.getPrice())
            .append("\n inComingOrderSide:")
            .append(inComingOrderSide)
            .append("\n inComingOrderPrice:")
            .append(inComingOrderPrice)
            .append("\n orderSideBotrPrice:")
            .append(orderSideBotrPrice)
            .append("\n oppositeToOrderSideBotrPrice:")
            .append(oppositeToOrderSideBotrPrice);

            Log.debug("HALHelper >>>> " + buffer.toString());
        }

        if (auctionedOrder.getSide().isSameSide(inComingOrderSide))
        { // Order has to be from the opposite site to be able to trade against the flashing order.
            if (Log.isDebugOn())
            {
                Log.debug("HALHelper >>>> getPossibleHALTradePrice(): both tradables are on the same side. Can not calculate price");
            }
            return null;
        }

        orderSideBotrPrice= HALHelper.checkBOTRForNoPrice(orderSideBotrPrice,inComingOrderSide, inComingOrderPrice, auctionedOrder.getPrice());
        oppositeToOrderSideBotrPrice= HALHelper.checkBOTRForNoPrice(oppositeToOrderSideBotrPrice,inComingOrderSide.getOtherSide(), inComingOrderPrice, auctionedOrder.getPrice());

// The follwoing lines are changed to account for a situation where CBOE is NBBO.
        OrderBook orderBook = getOrderBookHome().find(auctionedOrder.getProductKey().intValue());

        Price orderSideCBOEPrice = null;
        Price oppositeToOrderSideCBOEPrice = null;

        if (inComingOrderSide.isBuySide())
        {
             orderSideCBOEPrice = orderBook.getBestNonContingentBidPrice();
             oppositeToOrderSideCBOEPrice = orderBook.getBestNonContingentAskPrice();
        }
        else
        {
             orderSideCBOEPrice = orderBook.getBestNonContingentAskPrice();
             oppositeToOrderSideCBOEPrice = orderBook.getBestNonContingentBidPrice();
        }



        if (!inComingOrderSide.isFirstBetter(orderSideBotrPrice,orderSideCBOEPrice))
        {
            orderSideBotrPrice = orderSideCBOEPrice;
        }

        if (!inComingOrderSide.getOtherSide().isFirstBetter(oppositeToOrderSideBotrPrice,oppositeToOrderSideCBOEPrice))
        {
            oppositeToOrderSideBotrPrice = oppositeToOrderSideCBOEPrice;
        }



        if (inComingOrderSide.isFirstBetter(orderSideBotrPrice,inComingOrderPrice))
        { // There is a no possibility of a trade here as incoming order is worse than BOTR. for trade to occur
          // incoming order should match or better BOTR.
            if (Log.isDebugOn())
            {
                Log.debug("HALHelper >>>> getPossibleHALTradePrice(): Incoming " + inComingOrderSide +" tradable at price: " + inComingOrderPrice + " is worse than BOTR: " + orderSideBotrPrice);
            }
            return null;
        }
        Price possibleTradePrice = null;
		Price tempPrice1 = null;
		Price tempPrice2 = null;

        if (inComingOrderSide.isFirstBetter(inComingOrderPrice,oppositeToOrderSideBotrPrice))
        {
            // Possible trade price is the worse of the incoming order price and the opposite side Botr as we need to have
            // the orders trade within BOTR.
            tempPrice1 = oppositeToOrderSideBotrPrice;
        }
        else
        {
            tempPrice1 = inComingOrderPrice;
        }
        if (Log.isDebugOn())
        {
            Log.debug("HALHelper >>>> getPossibleHALTradePrice() tempPrice1: is " + tempPrice1);
        }

        if (auctionedOrder.getSide().isFirstBetter(auctionedOrder.getPrice(),orderSideBotrPrice))
        {
            // Possible trade price is the worse of the incoming order price and the opposite side Botr as we need to have
            // the orders trade within BOTR.
            tempPrice2 = orderSideBotrPrice;
        }
        else
        {
            tempPrice2 = auctionedOrder.getPrice();
        }
        if (Log.isDebugOn())
        {
            Log.debug("HALHelper >>>> getPossibleHALTradePrice() tempPrice2: is " + tempPrice2);
        }

        if (auctionedOrder.getSide().isFirstBetter(tempPrice1,tempPrice2))
        {
            // Possible trade price is the worse of the incoming order price and the opposite side Botr as we need to have
            // the orders trade within BOTR.
            possibleTradePrice = tempPrice1;
        }
        else
        {
            possibleTradePrice = tempPrice2;
        }
        if (possibleTradePrice.isValuedPrice() && possibleTradePrice.toLong() == 0)
        {
            if (Log.isDebugOn())
            {
                Log.debug("HALHelper >>>> getPossibleHALTradePrice() possibleTradePrice: is " + possibleTradePrice
                    + ": zero is not a valid trading price in HAL.");
            }
            return null;
        }
        if (Log.isDebugOn())
        {
            Log.debug("HALHelper >>>> getPossibleHALTradePrice() possibleTradePrice: is " + possibleTradePrice);
        }


        if (!(auctionedOrder.getSide().areTwoPricesTradable(auctionedOrder.getPrice(),possibleTradePrice)))
        {
            if (Log.isDebugOn())
            {
                Log.debug("HALHelper >>>> getPossibleHALTradePrice(): orders can not trade at the given price of: " + possibleTradePrice + " Trade is not possible.");
            }
            possibleTradePrice = null;
	        return possibleTradePrice;
        }

        if ((possibleTradePrice != null) && (possibleTradePrice.isMarketPrice()))
        { // This check is added to account for a situation where BOTR price is not available and flashing and incoming orders are MKT orders.
            if (orderSideBotrPrice.isMarketPrice() && !(oppositeToOrderSideBotrPrice.isMarketPrice()))
            {
                possibleTradePrice = oppositeToOrderSideBotrPrice;

            } else
            {
                if (!orderSideBotrPrice.isMarketPrice() && oppositeToOrderSideBotrPrice.isMarketPrice())
                {
                    possibleTradePrice = orderSideBotrPrice;
                }
            }
            if (Log.isDebugOn())
            {
                Log.debug("HALHelper >>>> getPossibleHALTradePrice(): was MKT now changed to a valid trading price of: " + possibleTradePrice);
            }
        }

        if ((possibleTradePrice != null) && (!possibleTradePrice.isValuedPrice()))
        { // If the possibel trade price is not a value price then we can not create a trade and need to return a null value.
            possibleTradePrice = null;
        }

        if (Log.isDebugOn())
        {
            Log.debug("HALHelper >>>> getPossibleHALTradePrice(): returning possible trade price of: " + possibleTradePrice );
        }
        return possibleTradePrice;
    }

    // if BOTR price is No Price then assign it the worse of the two prices sent in.
    private static Price checkBOTRForNoPrice(Price botrPrice, Side botrSide, Price Price1, Price Price2)
    {
        if (botrPrice.isNoPrice() || isZeroPrice(botrPrice))
        {
            if (botrSide.isFirstBetter(Price1, Price2))
            {
                botrPrice = Price2;
            }
            else
            {
                botrPrice = Price1;
            }
            if (Log.isDebugOn())
            {
                Log.debug("HALHelper >>>> getPossibleHALTradePrice(): BOTR Price was Zero/NP on side: " + botrSide + " Now changed to:" +botrPrice );
            }
        }
        return botrPrice;
    }

    public static BestQuote getBOTR(String sessionName, int productKey)
    {
        try
        {
            MarketData market = getMarketDataHome().findByProduct(sessionName, productKey);
            if(market != null){
                return market.getBOTR();
            }
        }
        catch (Exception e)
        {
            Log.exception("HALHelper>>> Can not get the BOTR market data for session:product " + sessionName + ":" + productKey, e);
        }

        return null;
    }

    public static boolean isZeroPrice(Price price)
    {
        boolean rtnVal = false;
        if (price.isValuedPrice()){
            Price zeroPrice = PriceFactory.create(0L);
            if(zeroPrice.equals(price)){
                rtnVal = true;
            }
        }

        return rtnVal;
    }

    /**
     * Is Market order trade price within NBBO EPW
     * based on available HALStruct
     * HALHelper
     * @param order
     * @param orderBook
     * @param hal : order side HAL
     * @return
     * boolean
     */
    public static boolean isMarketOrderTradePriceWithinNBBOEPW(Side orderSide, OrderBook orderBook, HALStruct hal)
    {
    	Side orderOppositeSide = orderSide.getOtherSide();
    	boolean isOrderBidSide = orderSide.isBuySide();
    	Side bidSide = isOrderBidSide ? orderSide : orderOppositeSide;
    	Side askSide = isOrderBidSide ? orderOppositeSide : orderSide;

    	Price botrBidPrice = isOrderBidSide ? hal.sameSideBOTRPrice : hal.oppositeSideBOTRPrice;
    	Price botrAskPrice = isOrderBidSide ? hal.oppositeSideBOTRPrice : hal.sameSideBOTRPrice;

    	Price NBBOBidPrice = getValidPrice(getNBBOPrice(botrBidPrice, orderBook, bidSide));
    	Price NBBOAskPrice = getValidPrice(getNBBOPrice(botrAskPrice, orderBook, askSide));
    	Price auctionSideNBBOPrice = isOrderBidSide ? NBBOBidPrice : NBBOAskPrice;
    	Price auctionOppositeSideNBBOPrice = isOrderBidSide ? NBBOAskPrice : NBBOBidPrice;
    	return isMarketOrderTradePriceWithinNBBOEPW(orderSide, orderBook, NBBOBidPrice, auctionSideNBBOPrice, auctionOppositeSideNBBOPrice);
    }

    /**
     * check if the possible tradable price is withing NBBO EPW,
     * the trade price is either order opposite side NBBO or mid point price
     * for incoming opposite side customer order tradable with
     * auction side NBBO or better than incoming order side NBBO
     * HALHelper
     * @param orderSide
     * @param orderBook
     * @param nbboStruct
     * @return
     * boolean
     */
    public static boolean isMarketOrderTradePriceWithinNBBOEPW(Side orderSide, OrderBook orderBook, NBBOStruct nbboStruct)
    {
    	Price NBBOBidPrice = getNBBOBidPrice(nbboStruct);
    	Price NBBOAskPrice = getNBBOAskPrice(nbboStruct);
    	Price orderSideNBBO = orderSide.isBuySide() ? NBBOBidPrice : NBBOAskPrice;
    	Price orderOppositeSideNBBO = orderSide.isBuySide() ? NBBOAskPrice : NBBOBidPrice;
    	return isMarketOrderTradePriceWithinNBBOEPW(orderSide, orderBook, NBBOBidPrice, orderSideNBBO, orderOppositeSideNBBO);
    }

    /**
     * check if the possible tradable price is withing NBBO EPW
     * HALHelper
     * @param orderSide
     * @param orderBook
     * @param nbboBidPrice
     * @param orderSideNBBOPrice
     * @param tradePrice
     * @return
     * boolean
     */
    public static boolean isMarketOrderTradePriceWithinNBBOEPW(Side orderSide, OrderBook orderBook, Price nbboBidPrice, Price orderSideNBBOPrice, Price tradePrice)
    {
    	Side buySide = new BuySide();
    	Side sellSide = new SellSide();
    	Price EPW = orderBook.getTradingProduct().getExchangePrescribedWidth(nbboBidPrice);
    	double calculatedEPW = orderSide.isBuySide() ? orderSideNBBOPrice.toDouble() + EPW.toDouble() : orderSideNBBOPrice.toDouble() - EPW.toDouble();
    	if (calculatedEPW < 0) {
    		calculatedEPW = 0;
    	}
    	Price NBBOEPWPrice = PriceFactory.create(calculatedEPW);
    	return orderSide.isBuySide() ? sellSide.isFirstBetterOrEqual(tradePrice, NBBOEPWPrice) : buySide.isFirstBetterOrEqual(tradePrice, NBBOEPWPrice);
    }
    public static Price getBestPrice(NBBOStruct nbboStruct, Side side)
    {
    	if (nbboStruct == null) {
    		return PriceFactory.getNoPrice();
    	}
    	Price bestPrice;
    	if (side.isBuySide()) {
    		bestPrice = PriceFactory.create(nbboStruct.bidPrice);
    	} else {
    		bestPrice = PriceFactory.create(nbboStruct.askPrice);
    	}
    	bestPrice = bestPrice==null ? PriceFactory.getNoPrice():bestPrice;
        if (bestPrice.isValuedPrice() && bestPrice.toLong()==0)
        	bestPrice = PriceFactory.getNoPrice();
    	return bestPrice;
    }

    /**
     * Is bid side and ask side prices inverted
     * HALHelper
     * @param nbboStruct
     * @return
     * boolean
     */
    public static boolean isNBBOInverted(NBBOStruct nbboStruct)
    {
    	Price bestBid = getBestPrice(nbboStruct, new BuySide());
    	Price bestAsk = getBestPrice(nbboStruct, new SellSide());
    	if (bestBid.isValuedPrice() && bestAsk.isValuedPrice() && bestBid.greaterThan(bestAsk))
    		return true;
    	return false;
    }

    /**
     * is NBBO inverted or locked
     * HALHelper
     * @param nbboStruct
     * @return
     * boolean
     */
    public static boolean isNBBOInvertedOrLocked(NBBOStruct nbboStruct)
    {
    	Side buySide = new BuySide();
    	Price bestBid = getBestPrice(nbboStruct, buySide);
    	Price bestAsk = getBestPrice(nbboStruct, new SellSide());

    	// inverted or locked means that NBBO bid price is same or bigger than ask price
    	if (bestBid.isValuedPrice() && bestAsk.isValuedPrice() &&
    			bestBid.greaterThanOrEqual(bestAsk)) {
    		return true;
    	}
    	return false;
    }

    /**
     * check if NBBO is inverted or locked based on BOTR
     * because current market is not updated, nbbo in market data is not calculated
     * so have to get nbbo based on botr
     * HALHelper
     * @param hal
     * @param orderBook
     * @param order
     * @return
     * boolean
     */
    public static boolean isNBBOInvertedOrLocked(HALStruct hal, OrderBook orderBook, Side orderSide)
    {
    	Price botrBidPrice = orderSide.isBuySide() ? hal.sameSideBOTRPrice : hal.oppositeSideBOTRPrice;
    	Price botrAskPrice = orderSide.isBuySide() ? hal.oppositeSideBOTRPrice : hal.sameSideBOTRPrice;
    	Price nbboBidPrice = HALHelper.getNBBOPrice(botrBidPrice, orderBook, new BuySide());
    	Price nbboAskPrice = HALHelper.getNBBOPrice(botrAskPrice, orderBook, new SellSide());

    	// inverted or locked means that NBBO bid price is same is bigger than ask
    	if (nbboBidPrice.isValuedPrice() && nbboAskPrice.isValuedPrice() &&
    			nbboBidPrice.greaterThanOrEqual(nbboAskPrice)) {
    		return true;
    	}
    	return false;
    }

    public static boolean isCBOENBBO(NBBOStruct botr, int productKey, Side side)
    {
    	Price botrPrice;
    	if (side.isBuySide()) {
    		botrPrice = getBestPrice(botr, new BuySide());
    	} else {
    		botrPrice = getBestPrice(botr, new SellSide());
    	}
    	return isCBOENBBO(botrPrice, productKey, side);
    }

    /**
     * If CBOE market is inverted or locked including contingent market
     * HALHelper
     * @param orderBook
     * @return
     * boolean
     */
    public static boolean isCBOEInvertedOrLocked(OrderBook orderBook)
    {
    	Price bestBuyPrice = orderBook.getBestPrice(new BuySide());
    	Price bestSellPrice = orderBook.getBestPrice(new SellSide());
    	if (bestBuyPrice != null && bestSellPrice != null &&
    			bestBuyPrice.isValuedPrice() && bestSellPrice.isValuedPrice() &&
    			bestBuyPrice.greaterThanOrEqual(bestSellPrice)) {
    		return true;
    	}
    	return false;
    }

    /**
     * Is bid price and ask price locked
     * HALHelper
     * @param nbboStruct
     * @return
     * boolean
     */
    public static boolean isNBBOLocked(NBBOStruct nbboStruct)
    {
    	Price bestBid = getBestPrice(nbboStruct, new BuySide());
    	Price bestAsk = getBestPrice(nbboStruct, new SellSide());
    	if (bestBid.isValuedPrice() && bestAsk.isValuedPrice() && bestBid.equals(bestAsk))
    		return true;
    	return false;
    }

    public static Price getNBBOBidPrice(NBBOStruct nbboStruct)
    {
    	Price bestBid = getBestPrice(nbboStruct, new BuySide());
    	return getValidPrice(bestBid);
    }

    public static Price getNBBOAskPrice(NBBOStruct nbboStruct)
    {
    	Price bestAsk = getBestPrice(nbboStruct, new SellSide());
    	return getValidPrice(bestAsk);
    }

    public static Price getFlashPrice(short auctionType, Order order, Price flashPrice)
    {
        if (auctionType == AuctionTypes.AUCTION_SAL)
        {
            TradingClass tradingClass = getTradingClass(order.getProductKey().intValue());
            int ticks = tradingClass.getAuctionTicksAboveNBBO(auctionType, order.getRemainingQuantity());
            if (order.getSide().isBuySide())
            {
                return tradingClass.addAuctionTicks(flashPrice, (-1)*ticks, auctionType);
            }
            else
            {
                return tradingClass.addAuctionTicks(flashPrice, ticks, auctionType);
            }
        }
        else
        {
            return flashPrice;
        }
    }

    private static TradingClass getTradingClass(int productKey)
    {
        OrderBook orderBook = getOrderBookHome().find(productKey);
        return orderBook.getTradingProduct().getTradingClass();
    }

    public static Price getCBOEPrice(int productKey, Side side)
    {
        OrderBook orderBook = getOrderBookHome().find(productKey);

        if (side.isBuySide())
        {
            return orderBook.getBestNonContingentBidPrice();
        }
        else
        {
            return orderBook.getBestNonContingentAskPrice();
        }
    }

    public static Price getCBOEPriceConsideringContingency(int productKey, Side side)
    {
        OrderBook orderBook = getOrderBookHome().find(productKey);
        return orderBook.getBestPrice(side);
    }

    private static QueriedMarketData getMarketDataPrices(MarketData marketData, Order order, short auctionType)
    {
        QueriedMarketData awayMarketData = new QueriedMarketData();

        Side orderSide = order.getSide();
        OrderBook book = getOrderBookHome().find(order.getProductKey().intValue());
        BestQuote botr = marketData.getBOTR();
        if (null != botr)
        {
            ExchangeVolumeStruct[] exchangeVolumes;

            // synchronized block is added to ensure market data thread safe
            synchronized (botr) {
                if (orderSide.isBuySide())
                {
                    awayMarketData.sameSideBOTRPrice = botr.getBidPrice();
                    awayMarketData.oppositeSideBOTRPrice = botr.getAskPrice();
                    exchangeVolumes = botr.getAskExchangeVolumes();
                }
                else
                {
                    awayMarketData.sameSideBOTRPrice = botr.getAskPrice();
                    awayMarketData.oppositeSideBOTRPrice = botr.getBidPrice();
                    exchangeVolumes = botr.getBidExchangeVolumes();

                }
            }
            for (int i=0; i<exchangeVolumes.length; i++)
            {
                awayMarketData.oppositeSideBOTRQuantity += exchangeVolumes[i].volume;
            }
        }

        if (awayMarketData.sameSideBOTRPrice.isNoPrice() || isZeroPrice(awayMarketData.sameSideBOTRPrice))
        {
            if (auctionType == AuctionTypes.AUCTION_HAL || auctionType == InternalAuctionTypes.AUCTION_NEW_HAL) {
                awayMarketData.sameSideBOTRPrice = getNBBOPrice(marketData, orderSide);
            } else if (auctionType == InternalAuctionTypes.AUCTION_HALO) {
                // using local book data since the book is locked
                awayMarketData.sameSideBOTRPrice = book.getBestLimitPrice(orderSide);
            }
        }

        awayMarketData.oppositeSideNBBOPrice = getNBBOPrice(marketData, orderSide.getOtherSide());
        if (awayMarketData.oppositeSideBOTRPrice.isNoPrice() || isZeroPrice(awayMarketData.oppositeSideBOTRPrice))
        {
            if (auctionType == AuctionTypes.AUCTION_HAL || auctionType == InternalAuctionTypes.AUCTION_NEW_HAL ) {
                awayMarketData.oppositeSideBOTRPrice = awayMarketData.oppositeSideNBBOPrice;//use NBBO(could be CBOE) price.
            } else if (auctionType == InternalAuctionTypes.AUCTION_HALO) {
                // using local book data since the book is locked
                awayMarketData.oppositeSideBOTRPrice = book.getBestLimitPrice(orderSide.getOtherSide());
            }
            awayMarketData.oppositeSideBOTRQuantity = 0;
        }

        return awayMarketData;
    }

    private static QueriedMarketData getMarketDataPrices(MarketData marketData, Order order)
    {
        QueriedMarketData awayMarketData = new QueriedMarketData();

        Side orderSide = order.getSide();
        BestQuote botr = marketData.getBOTR();
        if (null != botr)
        {
            ExchangeVolumeStruct[] exchangeVolumes;

            // synchronized block is added to ensure market data thread safe
            synchronized (botr) {
                if (orderSide.isBuySide())
                {
                    awayMarketData.sameSideBOTRPrice = botr.getBidPrice();
                    awayMarketData.oppositeSideBOTRPrice = botr.getAskPrice();
                    exchangeVolumes = botr.getAskExchangeVolumes();
                }
                else
                {
                    awayMarketData.sameSideBOTRPrice = botr.getAskPrice();
                    awayMarketData.oppositeSideBOTRPrice = botr.getBidPrice();
                    exchangeVolumes = botr.getBidExchangeVolumes();

                }
            }
            for (int i=0; i<exchangeVolumes.length; i++)
            {
                awayMarketData.oppositeSideBOTRQuantity += exchangeVolumes[i].volume;
            }
        }
        awayMarketData.oppositeSideNBBOPrice = getNBBOPrice(marketData, orderSide.getOtherSide());

        return awayMarketData;
    }

    public static boolean isHALLikeAuction(short auctionType)
    {
        return (auctionType == AuctionTypes.AUCTION_HAL ||
                auctionType == InternalAuctionTypes.AUCTION_HALO ||
                auctionType == InternalAuctionTypes.AUCTION_NEW_HAL);
    }

    /**
     * check if a quote or order cancel reason is due to product state
     * change
     * HALHelper
     * @param cancelReason
     * @return
     * boolean
     */
    public static boolean isProductStateChangeCancel(short cancelReason)
    {
    	return ((cancelReason == InternalActivityReasons.INTERNAL_PROD_CLOSED_BYCLASS)
                || (cancelReason == InternalActivityReasons.INTERNAL_PROD_HALTED_BYCLASS)
                || (cancelReason == InternalActivityReasons.INTERNAL_PROD_SUSPENDED_BYCLASS)
                || (cancelReason == InternalActivityReasons.INTERNAL_PROD_CLOSED)
                || (cancelReason == InternalActivityReasons.INTERNAL_PROD_HALTED)
                || (cancelReason == InternalActivityReasons.INTERNAL_PROD_SUSPENDED)
                );
    }

    /**
     * to provide convenient method to get auction name
     * based on the value
     *
     */
    public enum AUCTION_TYPE_STRING {
		HAL((short)4), HALO((short)-4), SAL((short)5), NEW_HAL((short)-8);

		private short type_value;

		AUCTION_TYPE_STRING(short type) {
			this.type_value = type;
		}

		public short getTypeValue() {
			return type_value;
		}

		public static String getAuctionTypeString(short type) {
			for (AUCTION_TYPE_STRING t : AUCTION_TYPE_STRING.values() ) {
				if (type == t.type_value)
					return t.toString();
			}
			return "";
		}
    }
    /**
     * Get NBBO price range
     * HALHelper
     * @param halStruct
     * @param orderBook
     * @param orderSide
     * @return
     * PriceRange
     */
    public static PriceRange getNBBOPriceRange(HALStruct halStruct, OrderBook orderBook, Side orderSide, Order order)
    {


        Side bidSide = SideFactory.getBuySide();
        Side askSide = SideFactory.getSellSide();
        String sessionName = order.getActiveSession();

         // if there is response side quoteSide, let it participate into the check and possible trade
            Price bidBotrPrice = HALHelper.getBestPrice(orderBook.getTradingProduct().getBOTR(sessionName), bidSide);
            Price bidPrice = getNBBOPrice(bidBotrPrice, orderBook, bidSide);
            Price askBotrPrice = HALHelper.getBestPrice(orderBook.getTradingProduct().getBOTR(sessionName), askSide);
            Price askPrice = getNBBOPrice(askBotrPrice, orderBook, askSide);

            return new PriceRangeImpl(bidPrice, askPrice);

    }

    /*
     * Returns best non C price or null.
     */
    public static Price getBestNoNCustomerPrice(OrderBook orderBook, Side aSide, Order anOrder){
        Enumeration tradables = orderBook.getTradables(aSide);
        Price bestPrice = null;
        while (tradables.hasMoreElements())
        {
            Tradable tradable = ((Tradable) tradables.nextElement());
            if( tradable.treatedLikeCustomer()) continue;
            bestPrice = tradable.getSide().isFirstBetter(bestPrice, tradable.getPrice()) ? bestPrice : tradable.getPrice();
        }
        return bestPrice;
    }

    public static boolean couldBOBOrderBeShippedAway(Order anOrder, OrderBook orderBook, SweepEvaluationService sweepService){
        Price limitPrice = getBestNoNCustomerPrice(orderBook, anOrder.getSide().getOtherSide(), anOrder);
        AllExchangesBBO allExchangesBBO = sweepService.fetchAllMarketData(anOrder);

        List<SweepElement> awaySweepList = new ArrayList<SweepElement>();
        if(allExchangesBBO != null)
        {
            List<SweepElement> qualifiedExchanges = allExchangesBBO.getQualifiedExchangesBBO();
            if(qualifiedExchanges != null)
            {
                awaySweepList.addAll(qualifiedExchanges);
            }
        }

        for (SweepElement eachSweepElement : awaySweepList)
        {
            if(limitPrice != null && anOrder.getSide().getOtherSide().isFirstBetter(eachSweepElement.getSweepPrice(), limitPrice)){
                return true;
            }else{
                if(limitPrice  == null && anOrder.getSide().getOtherSide().isFirstBetterOrEqual(eachSweepElement.getSweepPrice(), anOrder.getPrice())){
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * This method checks CBOE is NBBO and order price is not inferior to CBOE current market
     * @param order
     * @throws SystemException
     * @throws AuthorizationException
     * @throws com.cboe.exceptions.DataValidationException
     * @throws CommunicationException
     */
    public static boolean validateNBBOPrice(Order order, OrderBook orderBook) throws SystemException, DataValidationException
    {
        boolean validNBBOPrice=false;
        
        // the check below doesn't apply to quote like order
        if (order.treatedLikeQuote())
            return true;
        
        if( !order.getPrice().isValuedPrice())
        {
            throw ExceptionBuilder.dataValidationException("Order Price should be only Limit Price.", DataValidationCodes.INVALID_PRICE);
        }

        CurrentMarketStruct currentMarket=null;
        NBBOStruct botr = null;
        try
        {
            currentMarket = getMarketDataService().getCurrentMarketForProduct(order.getActiveSession(),order.getProductKey());
            botr = orderBook.getTradingProduct().getBOTR(order.getActiveSession());
        }
        catch(AuthorizationException e)
        {
            Log.exception("this.getClass().getName()", e);
        }
        catch(CommunicationException e)
        {
            Log.exception("this.getClass().getName()", e);
        }
        catch(NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Could not find CBOE market on opposite side of order.", 0/*DataValidationCodes.NOT_NBBO*/);
        }
        
        Price cboePrice = order.isBuySide() ? PriceFactory.create(currentMarket.askPrice) : PriceFactory.create(currentMarket.bidPrice);
        Price botrPrice = order.isBuySide() ? PriceFactory.create(botr.askPrice) : PriceFactory.create(botr.bidPrice);
        if (order.getSide().getOtherSide().isFirstBetterOrEqual(cboePrice, botrPrice)) {
            validNBBOPrice = true;
        }
            
        return validNBBOPrice;
    }

    /**
    *
    * @return
    */
    private static MarketDataService getMarketDataService()
    {
       if(mds == null)
       {
           try
           {
               MarketDataServiceHome home = (MarketDataServiceHome) HomeFactory.getInstance().findHome(MarketDataServiceHome.HOME_NAME);
               mds = home.find();
           }
           catch (CBOELoggableException e)
           {
               Log.exception("this.getClass().getName()", e);
           }
       }
       return mds;
    }
    
    public static short getReturnReasonCode(Order anOrder)
    {
        short returnReason = -1;
        HandlingInstruction instruction = anOrder.getHandlingInstruction();
        int allowedQuantity = anOrder.getQuantityAllowed();
        if (0 == allowedQuantity ) {
            returnReason = OrderRoutingReasons.COMPLETED_INSTRUCTIONS;
        }
        else {
            if (null != instruction && instruction.getMaximumExecutionVolume() > allowedQuantity) {
                returnReason = OrderRoutingReasons.PARTIALLY_TRADED;
            }
            else {
                returnReason = OrderRoutingReasons.NOT_TRADED_EXEC_PRICE;
            }
        }
        return returnReason;
    }
    
}
