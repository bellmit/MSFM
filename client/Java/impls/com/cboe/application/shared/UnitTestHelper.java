package com.cboe.application.shared;

import java.util.*;
import java.util.Random;
import java.lang.reflect.Method;

import com.cboe.interfaces.application.*;
import com.cboe.interfaces.businessServices.*;

import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.product.*;
import com.cboe.idl.cmiUser.*;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiQuote.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.marketData.InternalTickerStruct;
import com.cboe.idl.cmiStrategy.*;
import com.cboe.idl.cmi.UserPreferenceQuery;
import com.cboe.idl.businessServices.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiUtil.*;
import com.cboe.idl.cmiSession.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.quote.QuoteInfoStruct;
import com.cboe.idl.user.*;
import com.cboe.idl.cmiIntermarketMessages.HeldOrderStruct;
import com.cboe.idl.cmiIntermarketMessages.ExchangeMarketStruct;
import com.cboe.domain.util.*;

import com.cboe.domain.util.PriceSqlType;
import com.cboe.infrastructureServices.persistenceService.*;
import com.cboe.interfaces.domain.*;

import com.cboe.application.shared.*;

import com.cboe.util.event.*;
import com.cboe.infrastructureServices.loggingService.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.*;
import com.cboe.infrastructureServices.systemsManagementService.*;

public class UnitTestHelper extends Object
{
    private static final Random generator = new Random();

    /* Set up some constants for filling in quantities in orders */
    private static final int ORIGINAL_QUANTITY = 50;
    private static final int TRADED_QUANTITY =30;

    private static final int LEAVES_QUANTITY            = 20;
    private static final int SESSION_TRADE_QUANTITY     = 30;
    private static final int SESSION_CANCELLED_QUANTITY = 10;

    private static final char SOURCE = Sources.SBT;
    private static final char ORIGINATOR = OrderOrigins.CUSTOMER;
    private static final char TIME_IN_FORCE = 'G';
    private static final String OPTIONAL_DATA = "This is optional data";
    private static final char POSITION_EFFECT = 'G';
    private static final short CONTINGENCY_TYPE = ContingencyTypes.NONE;
    private static final PriceStruct CONTINGENCY_PRICE = new PriceStruct( (short)PriceTypes.VALUED,1,0 );
    private static final int CONTINGENCY_VOLUME = 2;
    private static final PriceStruct PRICE_STRUCT = new PriceStruct( PriceTypes.VALUED, 10, 0 );
    private static char[] asciiSet = null;
    private static final int ASCII_SET_SIZE = 62; // The number of alphanumeric characters
    static {
        asciiSet = new char[ ASCII_SET_SIZE ];
        char c; // Used for filling in set of characters
        int i;
        for ( i = 0, c = 'A'; c <= 'Z'; i++, c++ ) {
            asciiSet[ i ] = c;
        }
        for ( c = 'a'; c <= 'z'; i++, c++ ) {
            asciiSet[ i ] = c;
        }
        for ( c = '0'; c <= '9'; i++, c++ ) {
            asciiSet[ i ] = c;
        }
    }

    public UnitTestHelper() {
    }


/**
 * These methods will create the new order structs for the testing.
 * @author Thomas Lynch
 */
public static OrderStruct createNewOrderStruct( OrderEntryStruct originalOrder, int classKey, short productType, String userId ) {
        OrderStruct aStruct = new OrderStruct();
        aStruct.account = originalOrder.account;

        aStruct.leavesQuantity              = originalOrder.originalQuantity;
        aStruct.averagePrice                = PriceFactory.create(0.0).toStruct();
        aStruct.sessionTradedQuantity       = 0;
        aStruct.sessionCancelledQuantity    = 0;
        aStruct.sessionAveragePrice         = PriceFactory.create(0.0).toStruct();

        aStruct.classKey = classKey;
        aStruct.cmta = originalOrder.cmta;
        aStruct.contingency = originalOrder.contingency;
        aStruct.coverage = originalOrder.coverage;
        aStruct.orderNBBOProtectionType = originalOrder.orderNBBOProtectionType;
        aStruct.cross = originalOrder.cross;
        aStruct.crossedOrder = OrderStructBuilder.buildOrderIdStruct();
        aStruct.expireTime = originalOrder.expireTime;
        aStruct.optionalData = originalOrder.optionalData;
        aStruct.orderId = new OrderIdStruct(originalOrder.executingOrGiveUpFirm
                                            , originalOrder.branch
                                            , originalOrder.branchSequenceNumber
                                            , randomString(3)
                                            , TimeServiceWrapper.formatToDate()
                                            , positiveRandomInt()
                                            , positiveRandomInt()
                                            );
        aStruct.originalQuantity = originalOrder.originalQuantity;
        aStruct.originator = StructBuilder.buildExchangeAcronymStruct("","");
        aStruct.orderOriginType = originalOrder.orderOriginType;
        aStruct.orsId = randomString( 6 );
        aStruct.positionEffect = originalOrder.positionEffect;
        aStruct.productType = productType;
        aStruct.price = originalOrder.price;
        aStruct.productKey = originalOrder.productKey;
        aStruct.receivedTime = StructBuilder.buildDateTimeStruct();
        aStruct.side = Sides.BUY;
        aStruct.source = originalOrder.side;
        aStruct.state = OrderStates.ACTIVE;
        aStruct.subaccount = originalOrder.subaccount;
        aStruct.timeInForce = originalOrder.timeInForce;
        aStruct.tradedQuantity = TRADED_QUANTITY;
        aStruct.transactionSequenceNumber = 1;
        aStruct.userId = userId;
        aStruct.userAssignedId = originalOrder.userAssignedId;
        aStruct.sessionNames = new String[ 1 ];
        aStruct.sessionNames[0] = originalOrder.sessionNames[0];
        aStruct.activeSession = originalOrder.sessionNames[0];
        aStruct.legOrderDetails = new LegOrderDetailStruct[0];
        aStruct.extensions = "extensions";
        aStruct.userAcronym = new ExchangeAcronymStruct("","");
        aStruct.legOrderDetails = new LegOrderDetailStruct[0];
        return aStruct;
}//createNewOrderStruct

public static OrderStruct createNewOrderStruct( String sessionName, int productKey, int classKey, short productType, String userId, OrderIdStruct orderId ) {
     return createNewOrderStruct( createOrderEntryStruct( sessionName, productKey, userId, orderId ), classKey, productType, userId );
}//createNewOrderStruct

public static OrderEntryStruct createOrderEntryStruct( String sessionName, int productKey, String userId, OrderIdStruct orderId  ) {
    OrderEntryStruct aStruct = new OrderEntryStruct();
    aStruct.account = userId;
    aStruct.branch = orderId.branch;
    aStruct.branchSequenceNumber = orderId.branchSequenceNumber;
    aStruct.correspondentFirm = orderId.correspondentFirm;
    aStruct.cmta = StructBuilder.buildExchangeFirmStruct("","");
    aStruct.originator = StructBuilder.buildExchangeAcronymStruct("","");
    aStruct.contingency = new OrderContingencyStruct();
    aStruct.contingency.type = CONTINGENCY_TYPE;
    aStruct.contingency.price = CONTINGENCY_PRICE;
    aStruct.contingency.volume = CONTINGENCY_VOLUME;
    aStruct.extensions ="extensions";
    aStruct.coverage = 'B';
    aStruct.orderNBBOProtectionType = 1;
    aStruct.cross = false;
    aStruct.expireTime = createCurrentDateTimeStruct();
    aStruct.executingOrGiveUpFirm = orderId.executingOrGiveUpFirm;
    aStruct.optionalData = OPTIONAL_DATA;
    aStruct.originalQuantity = ORIGINAL_QUANTITY;
    aStruct.orderOriginType = ORIGINATOR;
    aStruct.positionEffect = 'N';
    aStruct.price = PRICE_STRUCT;
    aStruct.productKey = productKey;
    aStruct.side = Sides.BUY;
    aStruct.subaccount = "GHIJLM";
    aStruct.timeInForce = TIME_IN_FORCE;
    aStruct.userAssignedId = "";
    aStruct.orderDate = orderId.orderDate;

     if ( 0 == ( positiveRandomInt() % 2 ) ) {
        aStruct.side = 'S';
     }
     else {
        aStruct.side = 'B';
     }

     aStruct.sessionNames = new String[ 1 ];
     aStruct.sessionNames[0] = sessionName;

     return aStruct;
}//createOrderEntryStruct

public static OrderIdStruct createOrderIdStruct( ExchangeFirmStruct firm, String branch, int sequence, String cFirm, String ndate ) {
        return new OrderIdStruct (firm, branch, sequence, cFirm, ndate, 0, 0);

}

public static RFQEntryStruct createRFQEntryStruct(String sessionName, int productKey, int quantity) {
        return new RFQEntryStruct (productKey, sessionName, quantity);
}

public static FilledReportStruct createFilledReportStruct(OrderStruct order, int filledQuantity) {
        FilledReportStruct filledReport = new FilledReportStruct();

        filledReport.cmta = order.cmta;
        ContraPartyStruct[] contraParties = new ContraPartyStruct[2];
        int aboutHalf = (filledQuantity/2);
        int theRest = filledQuantity - aboutHalf;

        ExchangeFirmStruct firmStruct0 = StructBuilder.buildExchangeFirmStruct("CBOE", "123");
        ExchangeFirmStruct firmStruct1 = StructBuilder.buildExchangeFirmStruct("CBOE", "666");
        ExchangeAcronymStruct userStruct0 = StructBuilder.buildExchangeAcronymStruct("CBOE", "ABC");
        ExchangeAcronymStruct userStruct1 = StructBuilder.buildExchangeAcronymStruct("CBOE", "DEF");
        contraParties[0] = new ContraPartyStruct( userStruct0, firmStruct0, aboutHalf);
        contraParties[1] = new ContraPartyStruct( userStruct1, firmStruct1, theRest );
        filledReport.contraParties = contraParties;
        filledReport.tradeId = new CboeIdStruct(0, positiveRandomInt());
        filledReport.executingOrGiveUpFirm = order.orderId.executingOrGiveUpFirm;
        filledReport.originator = order.originator;
        filledReport.account = "TEST";
        filledReport.subaccount = "";
        filledReport.fillReportType = ReportTypes.REGULAR_REPORT;
        filledReport.userAssignedId="TESTID";
        filledReport.leavesQuantity = OrderStructBuilder.getRemainingQuantity(order) - filledQuantity;
        filledReport.optionalData = order.optionalData;
        filledReport.orsId = order.orsId;
        filledReport.executingBroker = "XXS";
        filledReport.positionEffect = order.positionEffect;
        filledReport.price = StructBuilder.clonePrice(order.price);
        filledReport.productKey = order.productKey;
        filledReport.side = order.side;
        filledReport.timeSent = StructBuilder.buildDateTimeStruct();
        filledReport.tradedQuantity = filledQuantity;
        filledReport.transactionSequenceNumber = order.transactionSequenceNumber + 1;
        filledReport.userId = order.userId;
        filledReport.sessionName = order.activeSession;
        filledReport.extensions = "extensions";
        filledReport.userAcronym = new ExchangeAcronymStruct("","");
//        filledReport.legFillReports = buildLegFillReports(filledReport);

        return filledReport;
}
/*
private static LegFilledReportStruct[] buildLegFillReports(FilledReportStruct filledReport)
{
    LegFilledReportStruct legFillReport = new LegFilledReportStruct();

    legFillReport.contraParties = filledReport.contraParties;
    legFillReport.positionEffect = filledReport.positionEffect;
    legFillReport.price = filledReport.price;
    legFillReport.productKey = filledReport.productKey;
    legFillReport.sessionName = filledReport.sessionName;
    legFillReport.side = filledReport.side;
    legFillReport.tradedQuantity = filledReport.tradedQuantity;
    legFillReport.tradeId = filledReport.tradeId;

    LegFilledReportStruct[] legFillReports = {legFillReport};
    return legFillReports;
}
*/
public static OrderFilledReportStruct createOrderFilledReportStruct( ProductNameStruct productName, OrderStruct order, int filledQuantity ) {
        OrderDetailStruct filledOrder = new OrderDetailStruct(productName,StatusUpdateReasons.NEW, order);
        FilledReportStruct[] filledReports = {createFilledReportStruct(order, filledQuantity)};
        OrderFilledReportStruct orderFilledRpt = new OrderFilledReportStruct(filledOrder, filledReports);
        return orderFilledRpt;
}

public static BustReportStruct createBustReportStruct(  CboeIdStruct tradeId,
                                                        OrderStruct orderStruct,
                                                        int bustedQuantity)
{
    BustReportStruct busted = new BustReportStruct();

    busted.tradeId = tradeId;
    busted.bustReportType = ReportTypes.REGULAR_REPORT;
    busted.executingOrGiveUpFirm = orderStruct.orderId.executingOrGiveUpFirm;
    busted.userId = orderStruct.userId;
    busted.bustedQuantity = bustedQuantity;
    busted.price = orderStruct.price;
    busted.productKey = orderStruct.productKey;
    busted.side = orderStruct.side;
    busted.timeSent = TimeServiceWrapper.toDateTimeStruct();
    busted.reinstateRequestedQuantity = bustedQuantity;
    busted.transactionSequenceNumber = orderStruct.transactionSequenceNumber + 1;
    busted.sessionName = orderStruct.activeSession;
    busted.userAcronym = orderStruct.userAcronym;

    return busted;
}
/*
private LegBustReportStruct[] buildLegBustReports(BustReportStruct bustReport)
{
    LegBustReportStruct legBustReport = new LegBustReportStruct();

    legBustReport.bustedQuantity = bustReport.bustedQuantity;
    legBustReport.price = bustReport.price;
    legBustReport.productKey = bustReport.productKey;
    legBustReport.sessionName = bustReport.sessionName;
    legBustReport.side = bustReport.side;
    legBustReport.tradeId = bustReport.tradeId;

    LegBustReportStruct[] legBustReports = {legBustReport};
    return legBustReports;
}
*/
public static OrderBustReportStruct createOrderBustReportStruct(ProductNameStruct productName,
                                                                CboeIdStruct tradeId,
                                                                OrderStruct orderStruct,
                                                                int bustedQuantity,
                                                                boolean reinstateRequested)
{
    OrderBustReportStruct orderBustReport = new OrderBustReportStruct();

    orderBustReport.bustedOrder.productInformation = productName;
    orderBustReport.bustedOrder.orderStruct = orderStruct;

    BustReportStruct bustReport = createBustReportStruct(tradeId, orderStruct, bustedQuantity);
    BustReportStruct[] bustReports = {bustReport};
    orderBustReport.bustedReport = bustReports;

    return orderBustReport;
}

public static BustReinstateReportStruct createBustReinstateReportStruct(CboeIdStruct tradeId,
                                                                        OrderStruct orderStruct,
                                                                        int reinstateQuantity)
{
    BustReinstateReportStruct reinstatedRpt = new BustReinstateReportStruct();
    reinstatedRpt.tradeId = tradeId;
    reinstatedRpt.reinstatedQuantity = reinstateQuantity;
    reinstatedRpt.bustedQuantity = reinstateQuantity;
    reinstatedRpt.totalRemainingQuantity = OrderStructBuilder.getRemainingQuantity(orderStruct) + reinstateQuantity;
    reinstatedRpt.price = orderStruct.price;
    reinstatedRpt.productKey = orderStruct.productKey;
    reinstatedRpt.side = orderStruct.side;
    reinstatedRpt.timeSent = TimeServiceWrapper.toDateTimeStruct();
    reinstatedRpt.transactionSequenceNumber = orderStruct.transactionSequenceNumber + 1;
    reinstatedRpt.sessionName = orderStruct.activeSession;

    return reinstatedRpt;
}

public static OrderBustReinstateReportStruct createOrderBustReinstateReportStruct(ProductNameStruct productName,
                                                                                  CboeIdStruct tradeId,
                                                                                  OrderStruct orderStruct,
                                                                                  int reinstateQuantity)
{
    OrderBustReinstateReportStruct reinstateReport = new OrderBustReinstateReportStruct();
    reinstateReport.reinstatedOrder.productInformation = productName;
    reinstateReport.reinstatedOrder.orderStruct = orderStruct;
    reinstateReport.bustReinstatedReport = createBustReinstateReportStruct(tradeId, orderStruct, reinstateQuantity);
    reinstateReport.bustReinstatedReport.transactionSequenceNumber = orderStruct.transactionSequenceNumber + 1;
    return reinstateReport;
}

public static CancelReportStruct createCancelReportStruct( OrderStruct order, int quantityToCancel ) {
        int cancelQuantity = 0;
        int tlcQuantity = 0;
        int remainingQuantity = OrderStructBuilder.getRemainingQuantity(order);

        if (remainingQuantity < quantityToCancel)
        {
            cancelQuantity = remainingQuantity;
            tlcQuantity = quantityToCancel - remainingQuantity;
        }
        else
        {
            cancelQuantity = quantityToCancel;
        }

        CancelReportStruct cancelReport = new CancelReportStruct(order.orderId,
                                                                 ReportTypes.REGULAR_REPORT,
                                                                 ActivityReasons.USER,
                                                                 order.productKey,
                                                                 order.activeSession,
                                                                 cancelQuantity,
                                                                 tlcQuantity,
                                                                 0,
                                                                 order.receivedTime,
                                                                 order.orsId,
                                                                 quantityToCancel + order.cancelledQuantity,
                                                                 order.transactionSequenceNumber + 1
                                                                 , order.userAssignedId + ":CANCEL"
                                                                 );
        return cancelReport;
}//createCancelReportStruct

public static QuoteEntryStruct CreateNewQuoteEntryStruct(String sessionName, int productKey, int askSideQuantity, int bidSideQuantity )
{
    QuoteEntryStruct quoteEntry = QuoteStructBuilder.buildQuoteEntryStruct();

    quoteEntry.productKey = productKey;
    quoteEntry.askQuantity = askSideQuantity;
    quoteEntry.bidQuantity = bidSideQuantity;

    int price = 6;

    quoteEntry.askPrice = new PriceStruct( PriceTypes.VALUED, price + 1, 0 );
    quoteEntry.bidPrice = new PriceStruct( PriceTypes.VALUED, price, 0 );
    quoteEntry.sessionName = sessionName;

    return quoteEntry;
}

public static QuoteFilledReportStruct createNewQuoteFilledReportStruct( QuoteDetailStruct quoteDetail ) {
    QuoteFilledReportStruct quoteFilledReport = new QuoteFilledReportStruct();
    quoteFilledReport.quoteKey = quoteDetail.quote.quoteKey;
    FilledReportStruct[] filledReport = {createNewFilledReportStruct(quoteDetail)};
    quoteFilledReport.filledReport = filledReport;
    quoteFilledReport.productKeys = quoteDetail.productKeys;
    quoteFilledReport.productName = quoteDetail.productName;
    return quoteFilledReport;
}//createNewQuoteFilledReportStruct

public static QuoteBustReportStruct createNewQuoteBustReportStruct(CboeIdStruct tradeId, int bustedQuantity, QuoteDetailStruct quoteDetail )
{
    char side = 'S';    // default side
    PriceStruct price = null;
    if ( quoteDetail.quote.askQuantity > 0) {
        side = 'S';
        price = quoteDetail.quote.askPrice;
    }
    else {
        side = 'B';
        price = quoteDetail.quote.bidPrice;
    }

    QuoteBustReportStruct bustedQuote = new QuoteBustReportStruct();
    bustedQuote.quoteKey = quoteDetail.quote.quoteKey;
    bustedQuote.productKeys = quoteDetail.productKeys;
    bustedQuote.productName = quoteDetail.productName;

    BustReportStruct bustedReport = new BustReportStruct();
    bustedReport.bustedQuantity = bustedQuantity;
    bustedReport.bustReportType = ReportTypes.REGULAR_REPORT;
    bustedReport.executingOrGiveUpFirm = StructBuilder.buildExchangeFirmStruct("CBOE", "123");
    bustedReport.price = price;
    bustedReport.productKey = quoteDetail.productKeys.productKey;
    bustedReport.reinstateRequestedQuantity = bustedQuantity;
    bustedReport.side = side;
    bustedReport.timeSent = TimeServiceWrapper.toDateTimeStruct();
    bustedReport.tradeId = tradeId;
    bustedReport.transactionSequenceNumber = quoteDetail.quote.transactionSequenceNumber + 1;
    bustedReport.userId = quoteDetail.quote.userId;
    bustedReport.sessionName = quoteDetail.quote.sessionName;
    bustedReport.userAcronym = StructBuilder.buildExchangeAcronymStruct("CBOE","CCC");

    BustReportStruct[] bustReports = {bustedReport};

    bustedQuote.bustedReport = bustReports;
    return bustedQuote;
}//createNewQuoteFilledReportStruct

public static FilledReportStruct createNewFilledReportStruct( QuoteDetailStruct quoteDetail ) {

    char side = 'S';    // default side
    int quantity = 1;


    FilledReportStruct filledReport = new FilledReportStruct();

    filledReport.cmta = StructBuilder.buildExchangeFirmStruct("CBOE", "CMTA");
    filledReport.tradeId = new CboeIdStruct(0, positiveRandomInt());
    filledReport.contraParties = new ContraPartyStruct[0];
    filledReport.executingOrGiveUpFirm = StructBuilder.buildExchangeFirmStruct("CBOE", "clearingFirm");
    filledReport.originator = StructBuilder.buildExchangeAcronymStruct("CBOE", "ABC");
    filledReport.account = "TEST";
    filledReport.subaccount = "";
    filledReport.userAssignedId="TESTID";
    filledReport.leavesQuantity = 0;
    filledReport.optionalData = "";
    filledReport.orsId = "";
    filledReport.fillReportType = ReportTypes.REGULAR_REPORT;
    filledReport.positionEffect = '0';
    filledReport.price = StructBuilder.buildPriceStruct();
    filledReport.productKey = quoteDetail.productKeys.productKey;
    filledReport.side = 'B';
    filledReport.timeSent = StructBuilder.buildDateTimeStruct();
    filledReport.tradedQuantity = 0;
    filledReport.transactionSequenceNumber = quoteDetail.quote.transactionSequenceNumber + 1;
    filledReport.userId = quoteDetail.quote.userId;
    filledReport.sessionName = quoteDetail.quote.sessionName;
    filledReport.executingBroker = "XXS";
    filledReport.transactionSequenceNumber = quoteDetail.quote.transactionSequenceNumber + 1;
    filledReport.extensions = "";
    filledReport.userAcronym = new ExchangeAcronymStruct("","");

    if ( quoteDetail.quote.askQuantity > 0) {
        side = 'S';
        filledReport.tradedQuantity = quantity;
        filledReport.leavesQuantity = quoteDetail.quote.askQuantity - quantity;
        filledReport.price = StructBuilder.clonePrice(quoteDetail.quote.askPrice);
        filledReport.side = side;
    }
    else {
        side = 'B';

        filledReport.tradedQuantity = quantity;
        filledReport.leavesQuantity = quoteDetail.quote.bidQuantity - quantity;
        filledReport.price = StructBuilder.clonePrice(quoteDetail.quote.bidPrice);
        filledReport.side = side;
    }

    return filledReport;
}//createNewFilledReportStruct

public static PreferenceStruct[] defaultPreferences(String[] memberKeys) {
    int size = 2;
    if (null != memberKeys ) {
        size +=memberKeys.length;
    }

    PreferenceStruct[] preferences = new PreferenceStruct[2];
    preferences[0] = UserStructBuilder.buildPreferenceStruct();
    preferences[0].name = "ACCOUNT";
    preferences[0].value = "ACCOUNTVALUE";
    preferences[1] = UserStructBuilder.buildPreferenceStruct();
    preferences[1].name = "BACKOFFICE";
    if (null == memberKeys ) {
            preferences[1].value = "NO";
    }
    else{
            preferences[1].value = "YES";
            for (int i=0; i < memberKeys.length ;i++) {
                //preferences[i+2].path = "BACKOFFICE";
                preferences[i+2].name = "USERLIST";
                preferences[i+2].value = memberKeys[i];
            }
    }
    return preferences;
}

public static UserLogonStruct createNewUserLogonStruct(String userName, String password)
{
    UserLogonStruct ret = new UserLogonStruct(userName, password, "2.0",'t' );
    return ret;
}

  private static String getMemberKeyFromName(String userName, int count)
  {
    if ( userName.length() <= count)
    {
        return userName;
    }
    else
        return userName.substring(0,count);
  }

    private static SessionProfileUserStruct createSessionProfileUserStruct(String userName)
    {
        SessionProfileUserStruct sfu = ClientUserStructBuilder.buildSessionProfileUserStruct();
        sfu.firm = StructBuilder.buildExchangeFirmStruct("CBOE", "007");
        sfu.fullName = userName;
        sfu.role = OrderOrigins.MARKET_MAKER;
        sfu.userId = userName;
        sfu.userAcronym = StructBuilder.buildExchangeAcronymStruct("CBOE", userName);

        return sfu;
    }

public static SessionProfileUserStruct createNewValidUserStruct(String userName, PreferenceStruct[] preferences)
{
    return createSessionProfileUserStruct(userName);
}

public static SessionProfileUserStruct createNewValidSessionProfileUserStruct(String userName)
{
    return createNewValidUserStruct(userName, defaultPreferences(null));
}
    
public static SessionProfileUserStructV2 createNewValidSessionProfileUserStructV2(String userName)
{
    SessionProfileUserStruct sp = createNewValidSessionProfileUserStruct(userName);
    return new SessionProfileUserStructV2(123456, sp);
}

/*
public static Object createLastSaleSummaryStruct(Object marketData)
{
    LastSaleSummaryStruct lastSale = new LastSaleSummaryStruct();
    lastSale.productKeys = marketData.productKeys;
    lastSale.base = marketData.lastSale;
    return lastSale;
}
*/

public static RFQStruct createRFQStruct(String sessionName, ProductStruct product)
{
    RFQStruct rfq = new RFQStruct();
    rfq.productKeys = product.productKeys;
    rfq.quantity = 100;
    rfq.timeToLive = 100;
    rfq.rfqType = RFQTypes.SYSTEM;
    rfq.entryTime = TimeServiceWrapper.toTimeStruct();
    rfq.sessionName = sessionName;

    return rfq;
}

public static ProductStateStruct createProductStateStruct(String sessionName, ProductKeysStruct productKeys, short productState)
{
    return new ProductStateStruct(productKeys, sessionName, productState, 333); //Only a little evil
}

public static RecapStruct createRecapStruct(String sessionName, ProductKeysStruct productKeys, ProductNameStruct productName)
{
    RecapStruct recap = new RecapStruct();
    recap.productKeys = productKeys;
    recap.productInformation = productName;
    recap.askPrice = PRICE_STRUCT;
    recap.askSize = 9;
    recap.askTime = StructBuilder.buildTimeStruct();;
    recap.bidPrice = PRICE_STRUCT;
    recap.bidSize = 10;
    recap.bidTime = StructBuilder.buildTimeStruct();;
    recap.closePrice = PRICE_STRUCT;
    recap.highPrice = PRICE_STRUCT;
    recap.lastSalePrice = PRICE_STRUCT;
    recap.lastSaleVolume = 5;
    recap.lowPrice = PRICE_STRUCT;
    recap.netChange = PRICE_STRUCT;
    recap.openInterest = 20;
    recap.openPrice = PRICE_STRUCT;
    recap.previousClosePrice = PRICE_STRUCT;
    recap.recapPrefix = "recapPrefix";
    recap.tick = PRICE_STRUCT;
    recap.tickDirection = 'S';
    recap.totalVolume = 10;
    recap.tradeTime = StructBuilder.buildTimeStruct();
    recap.sessionName = sessionName;

    return recap;
}
public static InternalTickerStruct createTickerStruct(String sessionName, ProductKeysStruct productKeys, String productSymbol)
{
    InternalTickerStruct internalTicker = new InternalTickerStruct();
    internalTicker.tradeTime = DateWrapper.convertToTime(System.currentTimeMillis());
    internalTicker.ticker = new TickerStruct();
    internalTicker.ticker.productKeys = productKeys;
    internalTicker.ticker.exchangeSymbol = productSymbol;
    internalTicker.ticker.lastSalePrice = PRICE_STRUCT;
    internalTicker.ticker.lastSaleVolume = 100;
    internalTicker.ticker.salePostfix = "102";
    internalTicker.ticker.salePrefix = "120";
    internalTicker.ticker.sessionName = sessionName;
    return internalTicker;
}
/**
 * This method was created in VisualAge.
 * @author Craig Murphy
 * @return int
 */
public static int positiveRandomInt() {
    int retVal = generator.nextInt();
    if ( retVal < 0 ) {
        retVal = -retVal;
    }
    return retVal;
}

/**
 * This method was created in VisualAge.
 * @author Craig Murphy
 * @return java.lang.String
 */
public static String randomString(int length) {
    // Generate a random string for use in arbitrarily setting order IDs to what we can
    // hope is a unique value
    char[] retVal = new char[ length ];
    for ( int i = 0; i < length; i++ ) {
        retVal[ i ] = asciiSet[ positiveRandomInt() %  ASCII_SET_SIZE ];
    }
    return new String( retVal );
}

    /**
     * Initializes foundation framework.
     * @author Connie Feng
     * @author Thomas Lynch
     */
    public static void initFFEnv() {
            String[] args = {"CAS.properties"};
                initFFEnv(args, 0);
    }

    public static void initFFEnv(String[] args, int firstParameter)
    {
         initFFEnv( args, firstParameter, "ClientApplicationServer" );
    }

    public static void initFFEnv(String[] args, int firstParameter, String serverName)
    {
        try
        {
            RemoteConnectionFactory.create(args);
            EventChannelAdapterFactory.find().setDynamicChannels(true);

            FoundationFramework ff = FoundationFramework.getInstance();
            ConfigurationService configService = new ConfigurationServiceFileImpl();
            configService.initialize(args, firstParameter);
            ff.initialize(serverName, configService);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            System.exit(1);
        }
    }




    public static DateStruct createCurrentDateStruct() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        return new DateStruct((byte)cal.get(cal.MONTH), (byte)cal.get(cal.DAY_OF_MONTH) , (short)cal.get(cal.YEAR));
    }


    public static TimeStruct createCurrentTimeStruct() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        return new TimeStruct((byte)cal.get(cal.HOUR_OF_DAY), (byte)cal.get(cal.MINUTE), (byte)cal.get(cal.SECOND), (byte)cal.get(cal.MILLISECOND));
    }

    /**
      * gets an Empty current market struct
      * @return CurrentMarketStruct
      */
    public static CurrentMarketStruct createNewMarketStruct(String sessionName, ProductKeysStruct keys) {
        CurrentMarketStruct baseMarket = new CurrentMarketStruct();
        baseMarket.exchange = "";
        baseMarket.askIsMarketBest = false;
        baseMarket.askPrice = PRICE_STRUCT;
        baseMarket.askSizeSequence = new MarketVolumeStruct[0];
        baseMarket.bidIsMarketBest = false;
        baseMarket.bidPrice = PRICE_STRUCT;
        baseMarket.bidSizeSequence = new MarketVolumeStruct[0];
        baseMarket.sentTime = createCurrentTimeStruct();
        baseMarket.productKeys = keys;
        baseMarket.legalMarket = true;
        baseMarket.sessionName = sessionName;

        return baseMarket;
    }

    public static DateTimeStruct createCurrentDateTimeStruct() {
        return new DateTimeStruct(createCurrentDateStruct(), createCurrentTimeStruct());
    }

    public static StrategyRequestStruct createStraddleStrategyRequestStruct(int callProductKey, int putProductKey)
    {
        StrategyRequestStruct request = new StrategyRequestStruct();
        request.strategyLegs = new StrategyLegStruct[2];
        request.strategyLegs[0] = new StrategyLegStruct(callProductKey, 1, Sides.BUY);
        request.strategyLegs[1] = new StrategyLegStruct(putProductKey, 1, Sides.SELL);
        return request;
    }

    public static QuoteInfoStruct buildQuoteInfoStruct(QuoteStruct quote)
    {
        QuoteInfoStruct quoteInfo = new QuoteInfoStruct();

        quoteInfo.firm = StructBuilder.buildExchangeFirmStruct("CBOE", "FIRM");
        quoteInfo.productKey = quote.productKey;
        quoteInfo.quoteKey = quote.quoteKey;
        quoteInfo.transactionSequenceNumber = quote.transactionSequenceNumber;
        quoteInfo.userId = quote.userId;

        return quoteInfo;
    }

    public static QuoteRiskManagementProfileStruct createQRMProfileStruct(int classKey, boolean QRMEnabledStatus)
    {
        QuoteRiskManagementProfileStruct QRMProfile = new QuoteRiskManagementProfileStruct();
        QRMProfile.classKey = classKey;
        QRMProfile.quoteRiskManagementEnabled = QRMEnabledStatus;
        QRMProfile.timeWindow = 10;
        QRMProfile.volumeThreshold = 50;
        return QRMProfile;
    }

    public static UserQuoteRiskManagementProfileStruct createUserQRMProfileStruct(String userId, boolean QRMEnabledGlobal)
    {
        UserQuoteRiskManagementProfileStruct userQRMProfile = new UserQuoteRiskManagementProfileStruct();
        QuoteRiskManagementProfileStruct[] QRMProfiles = {createQRMProfileStruct(0,false)};
        userQRMProfile.globalQuoteRiskManagementEnabled = QRMEnabledGlobal;
        userQRMProfile.defaultQuoteRiskProfile = createQRMProfileStruct(0, true);
        userQRMProfile.quoteRiskProfiles = QRMProfiles;

        return userQRMProfile;
    }

    public static UserDefinitionStruct createUserDefinitionStruct(String userId)
    {
        UserDefinitionStruct struct = new  UserDefinitionStruct();
        struct.userId = userId;
        return struct;
    }

    public static SessionProfileUserDefinitionStruct createSessionProfileUserDefinitionStruct(String userId)
    {
        SessionProfileUserDefinitionStruct struct = new  SessionProfileUserDefinitionStruct();
        struct.userId = userId;
        return struct;
    }

    public static UserEnablementStruct createUserEnablementStruct(String userId)
    {
        UserEnablementStruct struct = new  UserEnablementStruct();
        struct.userId = userId;
        struct.sessionEnablements = new UserSessionEnablementStruct[0];
        return struct;
    }

}//EOC
