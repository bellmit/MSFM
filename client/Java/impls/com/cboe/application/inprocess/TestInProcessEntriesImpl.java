package com.cboe.application.inprocess;

import com.cboe.application.inprocess.shared.InProcessServicesHelper;
import com.cboe.application.shared.UnitTestHelper;
import com.cboe.domain.util.OrderStructBuilder;
import com.cboe.domain.util.PriceFactory;
import com.cboe.domain.util.TimeServiceWrapper;
import com.cboe.idl.cmiAdmin.MessageStruct;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiQuote.QuoteDetailStruct;
import com.cboe.idl.cmiQuote.QuoteStruct;
import com.cboe.idl.cmiQuote.QuoteStructV4;
import com.cboe.idl.cmiQuote.QuoteStructV3;
import com.cboe.idl.cmiSession.ProductStateStruct;
import com.cboe.idl.cmiSession.SessionClassStruct;
import com.cboe.idl.cmiSession.SessionProductStruct;
import com.cboe.idl.cmiSession.TradingSessionStateStruct;
import com.cboe.idl.cmiUser.ExchangeAcronymStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.idl.cmiUser.UserLogonStruct;
import com.cboe.idl.cmiCallbackV2.*;
import com.cboe.idl.cmiConstants.Sides;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.inprocess.*;
import com.cboe.interfaces.application.inprocess.OrderStatusConsumer;
import com.cboe.interfaces.application.inprocess.QuoteStatusConsumer;
import com.cboe.interfaces.application.inprocess.UserSessionAdminConsumer;
import com.cboe.interfaces.application.inprocess.ExpectedOpeningPriceV2Consumer;
import com.cboe.interfaces.application.inprocess.TickerV2Consumer;
import com.cboe.interfaces.application.inprocess.RecapV2Consumer;
import com.cboe.interfaces.application.inprocess.NBBOV2Consumer;
import com.cboe.interfaces.application.inprocess.CurrentMarketV2Consumer;
import com.cboe.interfaces.application.inprocess.OrderBookV2Consumer;
import com.cboe.interfaces.callback.*;
//import com.cboe.interfaces.callback.OrderBookV2Consumer;
//import com.cboe.interfaces.callback.CurrentMarketV2Consumer;
//import com.cboe.interfaces.callback.NBBOV2Consumer;
//import com.cboe.interfaces.callback.RecapV2Consumer;
//import com.cboe.interfaces.callback.TickerV2Consumer;
//import com.cboe.interfaces.callback.ExpectedOpeningPriceV2Consumer;

/**
 * @author Jing Chen.
 */
public class TestInProcessEntriesImpl extends BObject implements TestInProcessEntries
{
    InProcessSessionManager session;
    String _sessionName;
    SessionProductStruct[] products;
    public TestInProcessEntriesImpl()
    {
    }

    public void create(String aName)
    {
        super.create(aName);
        try
        {
            if(Log.isDebugOn()) { Log.debug(this, "Registering login command callback"); }
            // Register command to start the service
            getBOHome().registerCommand(    this, // Callback Object
                                            "login", // External name
                                            "login", // Method name
                                            "login user", // Method description
                                            new String[] { "java.lang.String", "java.lang.String" },
                                            new String[] {"userId", "login mode"}
                                        );

            if(Log.isDebugOn()) { Log.debug(this, "Registering tradingSession command callback"); }
            getBOHome().registerCommand(    this, // Callback Object
                                            "testTradingSession", // External name
                                            "testTradingSession", // Method name
                                            "test interface trading session", // Method description
                                            new String[] { "java.lang.String", "java.lang.String", "java.lang.String"},
                                            new String[] {"tradingSession", "symbol", "productType"}
                                        );

            if(Log.isDebugOn()) { Log.debug(this, "Registering orderEntry command callback"); }
            getBOHome().registerCommand(    this, // Callback Object
                                            "testOrderEntry", // External name
                                            "testOrderEntry", // Method name
                                            "test order entry.", // Method description
                                            new String[] {"java.lang.String","java.lang.String","java.lang.String","java.lang.String"},
                                            new String[] {"sequenceNumber", "quantity", "price", "side"}
                                        );

            if(Log.isDebugOn()) { Log.debug(this, "Registering orderQuery command callback"); }
            getBOHome().registerCommand(    this, // Callback Object
                                            "testOrderQuery", // External name
                                            "testOrderQuery", // Method name
                                            "test order query.", // Method description
                                            new String[] {String.class.getName()},
                                            new String[] {""}
                                        );

            if(Log.isDebugOn()) { Log.debug(this, "Registering quoteEntry command callback"); }
            getBOHome().registerCommand(    this, // Callback Object
                                            "testQuoteEntry", // External name
                                            "testQuoteEntry", // Method name
                                            "test quote entry.", // Method description
                                            new String[] {"java.lang.String","java.lang.String","java.lang.String","java.lang.String"},
                                            new String[] {"askPrice", "askQuantity", "bidPrice", "bidQuantity"}
                                        );

            if(Log.isDebugOn()) { Log.debug(this, "Registering quoteQuery command callback"); }
            getBOHome().registerCommand(    this, // Callback Object
                                            "testQuoteQuery", // External name
                                            "testQuoteQuery", // Method name
                                            "test quote query", // Method description
                                            new String[] {String.class.getName()},
                                            new String[] {""}
                                        );
        }
        catch (Exception e)
        {
            Log.information(this, "Could not register channelAdapterStatus callback.");
            Log.exception(this, e);
        }
    }

    private class UserSessionAdminConsumerImpl implements UserSessionAdminConsumer
    {
        public void acceptLogout(String reason)
        {
            if(Log.isDebugOn()) { Log.debug("acceptLogout. Reason:"+reason); }
        }
        public void acceptTextMessage(MessageStruct message)
        {
            if(Log.isDebugOn()) { Log.debug("acceptTextMessaging."); }
        }
    }

    private class TradingSessionStatusConsumerImpl implements TradingSessionStatusConsumer
    {
        public void acceptTradingSessionState(TradingSessionStateStruct session)
        {
            if(Log.isDebugOn()) { Log.debug("acceptTradingSessionStruct for session:"+session); }
        }
    }

    private class ProductStatusConsumerImpl implements ProductStatusConsumer
    {
        public void updateProduct(SessionProductStruct productStruct)
        {
            if(Log.isDebugOn()) { Log.debug("updateProduct for session:"+session); }
        }

        public void acceptProductState(ProductStateStruct[] productStateStruct)
        {
            if(Log.isDebugOn()) { Log.debug("acceptProductState for session:"+session); }
        }
    }

    private class OrderStatusConsumerImpl implements OrderStatusConsumer
    {
        public void acceptOrderStatus(OrderStruct order, ProductStruct productName, short statusChange, int queueDepth)
        {
            if(Log.isDebugOn()) { Log.debug("acceptOrderStatus for session:"+session); }
        }
        public void acceptOrderCanceledReport(OrderStruct order, ProductStruct productName, short statusChange, CancelReportStruct[] cancelReports, int queueDepth)
        {
            if(Log.isDebugOn()) { Log.debug("acceptOrderCanceledReport for session:"+session); }
        }
        public void acceptOrderFilledReport(OrderStruct order, ProductStruct productName, short statusChange, FilledReportStruct[] filledReports, int queueDepth)
        {
            if(Log.isDebugOn()) { Log.debug("acceptOrderFilledReport for session:"+session); }
        }
        public void acceptOrderBustReport(OrderStruct order, ProductStruct productName, short statusChange, BustReportStruct[] bustReports, int queueDepth)
        {
            if(Log.isDebugOn()) { Log.debug("acceptOrderBustReport for session:"+session); }
        }
        public void acceptOrderBustReinstateReport(OrderStruct order, ProductStruct productName, short statusChange, BustReinstateReportStruct bustReinstatedReport, int queueDepth)
        {
            if(Log.isDebugOn()) { Log.debug("acceptOrderBustReinstateReport for session:"+session); }
        }
        public void acceptNewOrder(OrderStruct order, ProductStruct productName, short statusChange, int queueDepth)
        {
            if(Log.isDebugOn()) { Log.debug("acceptNewOrder for session:"+session); }
        }
        public void acceptOrderStatusUpdate(OrderStruct order, ProductStruct productName, short statusChange, int queueDepth)
        {
            if(Log.isDebugOn()) { Log.debug("acceptOrderStatusUpdate for session:"+session); }
        }
        // handle exception
        public void acceptConsumerException(OrderStruct order, String text, short statusChange, int queueDepth)
        {
            if(Log.isDebugOn()) { Log.debug("acceptConsumerException for session:"+session); }
        }

    }

    private class QuoteStatusConsumerImpl implements QuoteStatusConsumer
    {
        public void acceptQuoteBustReport(BustReportStruct[] bustReports, ProductStruct product, int quoteKey, short statusChange, int queueDepth)
        {
            if(Log.isDebugOn()) { Log.debug("acceptQuoteBustReport for session:"+session); }
        }
        public void acceptQuoteDeleteReport(QuoteDetailStruct delete, short reason, int queueDepth)
        {
            if(Log.isDebugOn()) { Log.debug("acceptQuoteDeleteReport for session:"+session); }
        }
        public void acceptQuoteFilledReport(FilledReportStruct[] fill, QuoteDetailStruct quote, int queueDepth)
        {
            if(Log.isDebugOn()) { Log.debug("acceptQuoteFilledReport for session:"+session); }
        }
        public void acceptQuoteFilledReport(FilledReportStruct[] fill, ProductStruct product, int quoteKey, short statusChange, int queueDepth)
        {
            if(Log.isDebugOn()) { Log.debug("acceptQuoteFilledReport for session:"+session); }
        }
        public void acceptQuoteStatus(QuoteDetailStruct quoteDetails, int queueDepth)
        {
            if(Log.isDebugOn()) { Log.debug("acceptQuoteStatus for session:"+session); }
        }
        public void acceptQuoteUpdate(QuoteDetailStruct quoteDetails, int queueDepth)
        {
            if(Log.isDebugOn()) { Log.debug("acceptQuoteStatus for session:"+session); }
        }

    }

    private class MarketDataConsumerImpl implements OrderBookV2Consumer,
            CurrentMarketV2Consumer, NBBOV2Consumer, RecapV2Consumer, TickerV2Consumer, ExpectedOpeningPriceV2Consumer
    {
        private com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer orderBookV2Consumer=null;
        private com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer currentMarketV2Consumer=null;
        private com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer expectedOpeningPriceV2Consumer=null;
        private com.cboe.idl.cmiCallbackV2.CMINBBOConsumer nbboV2Consumer=null;
        private com.cboe.idl.cmiCallbackV2.CMIRecapConsumer recapV2Consumer=null;
        private com.cboe.idl.cmiCallbackV2.CMITickerConsumer tickerV2Consumer=null;

        public CMITickerConsumer getCmiTickerConsumer() {
            if (tickerV2Consumer == null) {
                tickerV2Consumer =  new _CMITickerConsumerStub ();
            }
            return tickerV2Consumer;
        }

        public CMIRecapConsumer getCmiRecapConsumer() {
            if (recapV2Consumer == null) {
                recapV2Consumer =  new _CMIRecapConsumerStub ();
            }
            return recapV2Consumer;
        }

        public CMINBBOConsumer getCmiNbboConsumer() {
            if (nbboV2Consumer == null) {
                nbboV2Consumer =  new _CMINBBOConsumerStub ();
            }
            return nbboV2Consumer;
        }

        public CMIExpectedOpeningPriceConsumer getCmiExpectedOpeningPriceConsumer() {
            if (expectedOpeningPriceV2Consumer == null) {
                expectedOpeningPriceV2Consumer =  new _CMIExpectedOpeningPriceConsumerStub ();
            }
            return expectedOpeningPriceV2Consumer;
        }

        public CMIOrderBookConsumer getCmiOrderBookConsumer() {
            if (orderBookV2Consumer == null) {
                orderBookV2Consumer =  new _CMIOrderBookConsumerStub ();
            }
            return orderBookV2Consumer;
        }
            
        public CMICurrentMarketConsumer getCmiCurrentMarketConsumer() {
            if (currentMarketV2Consumer == null) {
                currentMarketV2Consumer =  new _CMICurrentMarketConsumerStub ();
            }
            return currentMarketV2Consumer;
        }
            

        public void acceptBookDepth(BookDepthStruct[] bookDepthStructs, int i, short i1)
        {
            if(Log.isDebugOn()) { Log.debug("acceptBookDepth for session:"+session); }
        }

        public void acceptCurrentMarket(CurrentMarketStruct[] currentMarketStructs, int i, short i1)
        {
            if(Log.isDebugOn()) { Log.debug("acceptCurrentMarket for session:"+session); }
        }

        public void acceptNBBO(NBBOStruct[] nbboStructs, int i, short i1)
        {
             if(Log.isDebugOn()) { Log.debug("acceptNBBO for session:"+session); }
        }

        public void acceptRecap(RecapStruct[] recapStructs, int i, short i1)
        {
            if(Log.isDebugOn()) { Log.debug("acceptRecap for session:"+session); }
        }

        public void acceptTicker(TickerStruct[] tickerStructs, int i, short i1)
        {
            if(Log.isDebugOn()) { Log.debug("acceptTicker for session:"+session); }
        }

        public void acceptExpectedOpeningPrice(ExpectedOpeningPriceStruct[] expectedOpeningPriceStructs, int i, short i1)
        {
            if(Log.isDebugOn()) { Log.debug("acceptExpectedOpeningPrice for session:"+session); }
        }
    }

    public void login(String userId, String mode)
    {
        try
        {
            char _mode = mode.charAt(0);
            UserLogonStruct logonStruct = new UserLogonStruct(userId, userId, "2.0", _mode);
            UserSessionAdminConsumer consumer = new UserSessionAdminConsumerImpl();
            session = InProcessServicesHelper.getUserAccess().logon(logonStruct,consumer);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void testTradingSession(String sessionName, String symbol, String productType)
    {
        try
        {
            _sessionName = sessionName;
            short _productType = Short.parseShort(productType);
            TradingSessionStatusConsumer consumer = new TradingSessionStatusConsumerImpl();
            session.getInProcessTradingSession().getCurrentTradingSessions(consumer);
            ProductStatusConsumer productStatusConsumer = new ProductStatusConsumerImpl();
            SessionClassStruct sessionClass = session.getInProcessTradingSession().getClassBySessionForSymbol(sessionName, _productType, symbol);
            products = session.getInProcessTradingSession().getProductsForSession(sessionName,sessionClass.classStruct.classKey,productStatusConsumer);
            MarketDataConsumerImpl marketDataConsumer = new MarketDataConsumerImpl();
            session.getInProcessMarketQuery().subscribeBookDepthForClass(sessionClass, marketDataConsumer);
            session.getInProcessMarketQuery().subscribeCurrentMarketForClass(sessionClass, marketDataConsumer);
            session.getInProcessMarketQuery().subscribeNBBOForClass(sessionClass, marketDataConsumer);
            session.getInProcessMarketQuery().subscribeRecapForClass(sessionClass, marketDataConsumer);
            session.getInProcessMarketQuery().subscribeTickerForClass(sessionClass, marketDataConsumer);
            session.getInProcessMarketQuery().subscribeExpectedOpeningPriceForClass(sessionClass, marketDataConsumer);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }


    public void testOrderEntry(String sequenceNumber, String quantity, String price, String side)
    {
        try
        {
            int _sequenceNumber = Integer.parseInt(sequenceNumber);
            int _quantity = Integer.parseInt(quantity);
            double _price = Double.parseDouble(price);
            char _side = side.charAt(0);
            ExchangeFirmStruct firm = session.getValidUser().defaultProfile.executingGiveupFirm;//new ExchangeFirmStruct("CBOE","690");
            OrderIdStruct inputOrderId = UnitTestHelper.createOrderIdStruct(firm,session.getValidUser().userId,_sequenceNumber,"FRM",TimeServiceWrapper.formatToDate());
            OrderEntryStruct orderEntry = UnitTestHelper.createOrderEntryStruct(_sessionName, products[0].productStruct.productKeys.productKey, session.getValidUser().userId, inputOrderId);
            orderEntry.side = _side;
            orderEntry.price = PriceFactory.create(_price).toStruct();
            orderEntry.originalQuantity = _quantity;
            orderEntry.contingency.price = PriceFactory.create(0).toStruct();
            ProductStruct product = session.getInProcessProductQuery().getProductByKey(orderEntry.productKey);
            ExchangeAcronymStruct userAcronym = session.getValidUser().userAcronym;
            OrderStruct order = OrderStructBuilder.buildOrderStruct(orderEntry, product.productKeys, session.getValidUser().userId, userAcronym);
            session.getInProcessOrderEntry().acceptOrder(order, products[0].productStruct);
        }
        catch(Exception e)
        {
            Log.exception(e);
        }
    }

    public void testOrderQuery(String input)
    {
        try
        {
            OrderStatusConsumer consumer = new OrderStatusConsumerImpl();
            session.getInProcessOrderQuery().subscribeOrderStatus(consumer, true);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void testQuoteEntry(String askPrice, String askQuantity, String bidPrice, String bidQuantity)
    {
        try
        {
            QuoteStructV4 quoteV4 = new QuoteStructV4();
            quoteV4.quoteV3 = new QuoteStructV3();
            quoteV4.quoteV3.quote = new QuoteStruct();

            quoteV4.quoteV3.quote.productKey = products[0].productStruct.productKeys.productKey;
            quoteV4.quoteV3.quote.sessionName = _sessionName;
            quoteV4.quoteV3.quote.transactionSequenceNumber = 0;
            quoteV4.quoteV3.quote.userAssignedId = "JC";
            quoteV4.quoteV3.quote.userId = session.getValidUser().userId;
            quoteV4.quoteV3.quote.askQuantity = Integer.parseInt(askQuantity);
            quoteV4.quoteV3.quote.bidQuantity = Integer.parseInt(bidQuantity);
            double _price = Double.parseDouble(askPrice);
            quoteV4.quoteV3.quote.askPrice = PriceFactory.create(_price).toStruct();
            _price = Double.parseDouble(bidPrice);
            quoteV4.quoteV3.quote.bidPrice = PriceFactory.create(_price).toStruct();

            quoteV4.sellShortIndicator = Sides.SELL_SHORT;

            session.getInProcessQuoteEntry().acceptQuote(quoteV4);
        }
        catch (Exception e)
        {
            Log.exception(e);
        }
    }

    public void testQuoteQuery(String input)
    {
        try
        {
            QuoteStatusConsumer consumer = new QuoteStatusConsumerImpl();
            session.getInProcessQuoteQuery().subscribeQuoteStatus(consumer, true);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
