package com.cboe.presentation.api.orderBook;

import java.util.*;

import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.idl.cmiOrder.BustReportStruct;
import com.cboe.idl.cmiOrder.FilledReportStruct;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.idl.cmiMarketData.MarketVolumeStruct;
import com.cboe.idl.cmiQuote.QuoteStruct;
import com.cboe.idl.cmiConstants.VolumeTypes;
import com.cboe.idl.cmiConstants.ProductTypes;
import com.cboe.idl.cmiConstants.PriceTypes;

import com.cboe.domain.util.StructBuilder;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.domain.util.QuoteStructBuilder;

import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;

import com.cboe.interfaces.presentation.marketData.PersonalBestBook;
import com.cboe.interfaces.presentation.api.Tradable;
import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.presentation.product.SessionProduct;

import com.cboe.presentation.api.APIHome;
import com.cboe.presentation.common.comparators.AskPriceComparator;
import com.cboe.presentation.common.comparators.BidPriceComparator;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.formatters.Sides;

import com.cboe.presentation.marketData.PersonalBestBookImpl;
import com.cboe.exceptions.NotFoundException;
import org.omg.CORBA.UserException;

public class TradersOrderBook
{
    public int productKey;
    public int classKey;
    public String sessionName;

    private PriceStruct bestAskPrice = null;
    private PriceStruct bestBidPrice = null;
    int bestAskQty = 0;
    int bestBidQty = 0;

    TreeMap sortedBidPriceTreeMap;
    TreeMap sortedAskPriceTreeMap;

    PersonalBestBook lastBestBook;
    private PriceStruct emptyPriceStruct = null;
    Tradable tradable = null;
    private boolean orderSideFlag;// BUY = true, SELL = false

    QuoteStruct nullQuote = null;

    public TradersOrderBook(String sessionName, int productKey, int classKey) throws TradersOrderBookInitializationException
    {
        if(GUILoggerHome.find().isDebugOn())
        {
            Object [] args = new Object[3];
            args[0] = sessionName;
            args[1] = new Integer(productKey);
            args[2] = new Integer(classKey);
            GUILoggerHome.find().debug("Creating TradersOrderBook.", GUILoggerBusinessProperty.ORDER_BOOK, args);
        }
        this.productKey  = productKey;
        this.classKey    = classKey;
        this.sessionName = sessionName;

        bestAskPrice = getEmptyPriceStruct();
        bestBidPrice = getEmptyPriceStruct();

        nullQuote = QuoteStructBuilder.buildQuoteStruct(QuoteStructBuilder.buildQuoteEntryStruct());

        SessionProduct sessionProduct = null;
        Product product = null;

        sessionProduct = getSessionProduct(sessionName, productKey);

        if ( sessionProduct == null )
        {
            // If session product was not found, try to get sessionless product
            product = getProduct(productKey);
        }

        if (sessionProduct != null)
        {
            // Initialize OrderBook
            if (sessionProduct.getProductType() == ProductTypes.STRATEGY)
            {
                sortedBidPriceTreeMap = new TreeMap(new AskPriceComparator());
            }
            else
            {
                sortedBidPriceTreeMap = new TreeMap(new BidPriceComparator());
            }

            sortedAskPriceTreeMap = new TreeMap(new AskPriceComparator());

            tradable = TradableFactory.create(0, getEmptyPriceStruct(), false, "BID");
            sortedBidPriceTreeMap.put(tradable, tradable);

            tradable = TradableFactory.create(0, getEmptyPriceStruct(), false, "ASK");
            sortedAskPriceTreeMap.put(tradable, tradable);
        }
        else if ( sessionProduct == null && product != null )
        {
            // This should be the case of INACTIVE produt, i.e. there is no sessionProduct, but
            // we got sessionless product.
            throw new TradersOrderBookInitializationException("Unable to create TradersOrderBook. Product for key "+productKey+" is not a SessionProduct");
        }
        else
        {
            // We could not get product for this product key
            // We want to throw this exception, so this OrderBook is not used. It was not initialized properly.
            throw new IllegalArgumentException("Unable to create TradersOrderBook. Product for key "+productKey+" is NotFound.");
        }

    }

    private Product getProduct(int productKey)
    {
        Product product = null;
        try
        {
            product = APIHome.findProductQueryAPI().getProductByKey(productKey);
        }
        catch(UserException e)
        {
            GUILoggerHome.find().exception("TradersOrderBook.TradersOrderBook", "Unable to obtain Product for key "+productKey,e);
        }
        return product;
    }

    private SessionProduct getSessionProduct(String sessionName, int productKey)
    {
        SessionProduct sessionProduct = null;
        try
        {
            sessionProduct = APIHome.findProductQueryAPI().getProductByKeyForSession(sessionName, productKey);
        }
        catch(UserException e)
        {
            GUILoggerHome.find().exception("TradersOrderBook.TradersOrderBook", "Unable to obtain Product for session "+sessionName+", key "+productKey, e);
        }
        return sessionProduct;
    }

    public synchronized void updateBook(OrderStruct order)
    {
        // Don't process this order if its a different product.
        if ( !(order.productKey == productKey) )
        {
           throw new IllegalArgumentException( "Invalid product key for the OrderBook. Order Product Key="+order.productKey+
                                               " OrderBook Product Key="+this.productKey);
        }

        setNewOrderSideFlag(order);

        tradable = TradableFactory.create(order.leavesQuantity, order.price, true, order.orderId.highCboeId+":"+order.orderId.lowCboeId);
        put(tradable);

        publishBook();
    }

    public synchronized void updateBook(QuoteStruct quote)
    {
        if(quote.bidQuantity == 0)
            quote.bidPrice = getEmptyPriceStruct();

        if(quote.askQuantity == 0)
            quote.askPrice = getEmptyPriceStruct();

        Iterator it = sortedAskPriceTreeMap.values().iterator();
        while (it.hasNext())
        {
            if(!(tradable = (Tradable)it.next()).isOrder())
                it.remove();
        }

        Iterator it2 = sortedBidPriceTreeMap.values().iterator();
        while (it2.hasNext())
        {
            if(!(tradable = (Tradable)it2.next()).isOrder())
                it2.remove();
        }

        tradable = TradableFactory.create(quote.bidQuantity, quote.bidPrice, false, "BID");
        sortedBidPriceTreeMap.put(tradable, tradable);

        tradable = TradableFactory.create(quote.askQuantity, quote.askPrice, false, "ASK");
        sortedAskPriceTreeMap.put(tradable, tradable);

        calculateBest(sortedAskPriceTreeMap);
        calculateBest(sortedBidPriceTreeMap);

        publishBook();
    }
// TODO: Verify implementation updateBook(BustReportStruct quoteBustReportStruct)
    public synchronized void updateBook(BustReportStruct quoteBustReportStruct)
    {
        int bustedQty = quoteBustReportStruct.bustedQuantity;
        PriceStruct price = quoteBustReportStruct.price;
        if(Sides.isBuyEquivalent(quoteBustReportStruct.side)) // BID
        {
            int bidQty = 0;
            // remove previous quote
            Iterator it = sortedBidPriceTreeMap.values().iterator();
            while (it.hasNext())
            {
                if (!(tradable = (Tradable) it.next()).isOrder())
                {
                    bidQty = tradable.getSize() - bustedQty;
                    if(bidQty < 0)
                    {   // unlikely that we get here because it would mean
                        // we did not get the quote, or the bust
                        // is for more than the quote size.
                        bidQty = 0;
                    }
                    it.remove();
                }
            }
            tradable = TradableFactory.create(bidQty, price, false, "BID");
            sortedBidPriceTreeMap.put(tradable, tradable);
            calculateBest(sortedBidPriceTreeMap);
        }
        else // ASK
        {
            int askQty = 0;
            // remove previous quote
            Iterator it = sortedAskPriceTreeMap.values().iterator();
            while (it.hasNext())
            {
                if (!(tradable = (Tradable) it.next()).isOrder())
                {
                    askQty = tradable.getSize() - bustedQty;
                    if (askQty < 0)
                    {
                        askQty = 0;
                    }
                    it.remove();
                }
            }
            tradable = TradableFactory.create(askQty, price, false, "ASK");
            sortedAskPriceTreeMap.put(tradable, tradable);
            calculateBest(sortedAskPriceTreeMap);
        }
        publishBook();
    }
// TODO: Verify implementation updateBook(FilledReportStruct quoteFillReportStruct)
    public synchronized void updateBook(FilledReportStruct quoteFillReportStruct)
    {
        PriceStruct price = quoteFillReportStruct.price;
        int leavesQty = quoteFillReportStruct.leavesQuantity;
        if (Sides.isBuyEquivalent(quoteFillReportStruct.side)) // BID
        {
            Iterator it = sortedBidPriceTreeMap.values().iterator();
            while (it.hasNext())
            {
                if (!(tradable = (Tradable) it.next()).isOrder())
                {
                    it.remove();
                }
            }
            tradable = TradableFactory.create(leavesQty, price, false, "BID");
            sortedBidPriceTreeMap.put(tradable, tradable);
            calculateBest(sortedBidPriceTreeMap);
        }
        else // ASK
        {
            Iterator it = sortedAskPriceTreeMap.values().iterator();
            while (it.hasNext())
            {
                if (!(tradable = (Tradable) it.next()).isOrder())
                {
                    it.remove();
                }
            }
            tradable = TradableFactory.create(leavesQty, price, false, "ASK");
            sortedAskPriceTreeMap.put(tradable, tradable);
            calculateBest(sortedAskPriceTreeMap);
        }
        publishBook();

    }
    public synchronized void deleteQuote()
    {
        updateBook(nullQuote);
    }

    public synchronized PersonalBestBook getPersonalBestBook()
    {
        PersonalBestBook book = new PersonalBestBookImpl(sessionName,  productKey,
                                    getBestBidPrice(), getBestBidSizeSequence(),
                                    getBestAskPrice(), getBestAskSizeSequence());

        return book;
    }

    public synchronized PriceStruct getBestBidPrice()
    {
        return this.bestBidPrice;
    }

    public synchronized PriceStruct getBestAskPrice()
    {
        return this.bestAskPrice;
    }

    public synchronized MarketVolumeStruct[] getBestBidSizeSequence()
    {
        MarketVolumeStruct[] mktVol = new MarketVolumeStruct[1];
        mktVol[0] = new MarketVolumeStruct(VolumeTypes.LIMIT, this.bestBidQty, false);
        return mktVol;
    }

    public synchronized MarketVolumeStruct[] getBestAskSizeSequence()
    {
        MarketVolumeStruct[] mktVol = new MarketVolumeStruct[1];
        mktVol[0] = new MarketVolumeStruct(VolumeTypes.LIMIT, this.bestAskQty, false);

        return mktVol;
    }

    public synchronized PriceStruct getEmptyPriceStruct()
    {
        if(this.emptyPriceStruct == null)
        {
            this.emptyPriceStruct = StructBuilder.buildPriceStruct();
        }
        return this.emptyPriceStruct;
    }

    private synchronized void put(Tradable tradable)
    {
        TreeMap sortedMap;
        if(orderSideFlag)
        {
            sortedMap = sortedBidPriceTreeMap;
        }
        else
        {
            sortedMap = sortedAskPriceTreeMap;
        }

        sortedMap.put(tradable, tradable);
        Iterator it = sortedMap.values().iterator();
        while (it.hasNext())
        {
            if( (tradable = (Tradable)it.next()).getSize() == 0 && tradable.isOrder())
                it.remove();
        }

        if ( GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.ORDER_BOOK))
        {
            GUILoggerHome.find().debug("TradersOrderBook.put() " + tradable.toString(), GUILoggerBusinessProperty.ORDER_BOOK);
            GUILoggerHome.find().debug((orderSideFlag ? "BidMap" : "AskMap"), GUILoggerBusinessProperty.ORDER_BOOK);
            Iterator iter = sortedMap.values().iterator();
            while (iter.hasNext())
            {
                tradable = (Tradable) iter.next();
                GUILoggerHome.find().debug(tradable.toString(), GUILoggerBusinessProperty.ORDER_BOOK);
            }
        }

        calculateBest(sortedMap);

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.ORDER_BOOK))
        {
            GUILoggerHome.find().debug(getPersonalBestBook().toString(), GUILoggerBusinessProperty.ORDER_BOOK);
        }

    }

    //Convenience method to determine if Map of Tradables contains Market orders
    private boolean containsMarketOrders(TreeMap map)
    {
        boolean contains = false;
        Iterator itr = map.keySet().iterator();
        Tradable localTradable = null;

        while(itr.hasNext() && !contains)
        {
            localTradable = (Tradable)itr.next();

            if(localTradable.getPrice() == null)
                continue;

            if(localTradable.getPrice().type == PriceTypes.MARKET)
            {
                contains = true;
            }
        }

        return contains;
    }

    synchronized void calculateBest(TreeMap map)
    {
        Tradable localTradable = null;
        Tradable lastTradable = null;

        int totalQuantity = 0;

        if (!map.isEmpty())
        {
            boolean containsMarket = containsMarketOrders(map);
            Iterator it = map.values().iterator();

            //if contains market orders, simply count them up and return them
            if (containsMarket)
            {
//                System.out.println("TradersOrderBook.calculateBest. Contains Market.");
                while (it.hasNext())
                {
                    localTradable = (Tradable) it.next();
                    if (localTradable.getPrice().type == PriceTypes.MARKET)
                    {
                        //aggregate quantity
                        totalQuantity += localTradable.getSize();
                        lastTradable = localTradable;
                    }
                }
            }
            else
            {
            //if no market orders...
                while (it.hasNext())
                {
                    localTradable = (Tradable) it.next();

                    if (lastTradable == null)
                    {
                        lastTradable = localTradable;
                        totalQuantity += localTradable.getSize();
                    }
                    else if( map == sortedAskPriceTreeMap && lastTradable.getSize() == 0)
                    {
                        lastTradable = localTradable;
                        totalQuantity += localTradable.getSize();
                    }
                    else if( (localTradable.getPrice().whole == lastTradable.getPrice().whole) &&
                        (localTradable.getPrice().fraction == lastTradable.getPrice().fraction))
                    {
                        totalQuantity += localTradable.getSize();
                    }
                    else
                    {
                        break;
                    }
                }
            }
        }

        if (map == sortedAskPriceTreeMap)
        {
            this.bestAskQty = totalQuantity;
            this.bestAskPrice = lastTradable.getPrice();
        }
        else
        {
            this.bestBidQty = totalQuantity;
            this.bestBidPrice = lastTradable.getPrice();
        }
    }

    private void publishBook()
    {
        PersonalBestBook newBestBook = getPersonalBestBook();

        if ( lastBestBook == null || !newBestBook.equals(lastBestBook))
        {
            lastBestBook = newBestBook;
            generatePersonalBestBookEvent( productKey, newBestBook );
            generatePersonalBestBookEvent( classKey, newBestBook );
        }
    }

    private void generatePersonalBestBookEvent(int key, PersonalBestBook newBestBook)
    {
        SessionKeyContainer container = new SessionKeyContainer(this.sessionName, key);
        ChannelKey channelKey = new ChannelKey(ChannelType.CB_PERSONAL_BEST_BOOK, container);
        ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, newBestBook);
        EventChannelAdapterFactory.find().dispatch(event);
    }

    private void setNewOrderSideFlag(OrderStruct order)
    {
        if(com.cboe.presentation.common.formatters.Sides.isBuyEquivalent(order.side) )
            orderSideFlag = true;// BUY
        else
            orderSideFlag = false;//SELL
    }
}
