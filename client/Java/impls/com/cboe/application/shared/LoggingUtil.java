package com.cboe.application.shared;

import com.cboe.application.util.QuoteCallSnapshot;
import com.cboe.application.util.OrderCallSnapshot;
import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiOrder.LightOrderEntryStruct;
import com.cboe.idl.cmiOrder.LightOrderResultStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;

/**
 * Created by IntelliJ IDEA.
 * User: josephg
 * Date: Nov 14, 2007
 * Time: 2:20:05 PM
 */
public class LoggingUtil {
    public static final String TOTAL_TIME = "TT";
    public static final String SERVER_TIME = "ST";
    public static final String CAS_TIME = "CT";
    public static final String FIX_TIME = "FT";
    public static final String CLASS_KEY = "CK";
    public static final String PRODUCT_KEY = "PK";
    public static final String QUOTE_KEY = "QK";
    public static final String SESSION_NAME = "SESN";
    public static final String USER_ASSIGNED_ID = "UAID";
    public static final String USER_ACRONYM = "UA";
    public static final String BRANCH = "BR";
    public static final String BRANCH_SEQ_NUMBER = "SEQ";
    public static final String QUANTITY = "QTY";
    public static final String TYPE = "TYPE";
    public static final String SIZE = "SZ";
    public static final String SIDE = "SD";
    public static final String CORR_FIRM = "CF";
    public static final String EXGU_FIRM = "EF";
    public static final String NUM_LEGS = "LEGS";
    public static final String CLASS_LOCK_WAIT_TIME = "ClsLWT";
    public static final String CLASS_LOCK_HOLD_TIME = "ClsLHT";
    public static final String CACHE_LOCK_WAIT_TIME = "CacheLWT";
    public static final String CACHE_LOCK_HOLD_TIME = "CacheLHT";
    public static final String ENTITY_ID = "EID";
    public static final String SESSION_TYPE = "SnT";
    public static final String SESSION_ID = "SnId";
    public static final String QUOTE_TOKEN = "QT";
    public static final String CONCURRENT_CLASS_LOCKS = "CCL";
    
    public static final String USER_ID = "UID";
    public static final String USER = "USER";
    public static final String CONTRA_BROKER = "CBR";
    public static final String CONTRA_FIRM = "CFM";
    public static final String FIRM = "FM";
    public static final String CMTA = "CMTA";
    public static final String TRADED_TIME = "TRADED_TIME";

    public static final String ORDER_2 = "order2";
    public static final String VALUE_DELIMITER = ":";
    public static final String TOKEN_DELIMITER = " ";
    public static final String CALLING = "calling";
    public static final String RETURNING = "returning";

    public static final String TRADE_QUANTITY = "TQTY";
    public static final String LEAVE_QUANTITY = "LQTY";
    public static final String CANCELED_QUANTITY = "CQTY";
    public static final String ORDER_HIGH_ID = "HI";
    public static final String ORDER_LOW_ID = "LI";
    
    /**
     * creates a quote timing log message
     */
    public static String createQuoteLogSnapshot(String method, int classKey, String sessionName, String userAssignedId, long entityId, String sessInfo)
    {
        return createQuoteLogSnapshot(method, classKey, sessionName, userAssignedId, -1, entityId, sessInfo);
    }
    /**
     * creates a quote timing log message
     */
    public static String createQuoteLogSnapshot(String method, int classKey, String sessionName, String userAssignedId, int firstQuoteKey, long entityId, String sessInfo)
    {
        StringBuilder logmsg = new StringBuilder(330);
        logmsg.append(method).append(TOKEN_DELIMITER).append(RETURNING);
        logmsg.append(TOKEN_DELIMITER).append(TOTAL_TIME).append(VALUE_DELIMITER);
        logmsg.append(QuoteCallSnapshot.elapsedTotalTime());
        logmsg.append(TOKEN_DELIMITER).append(CAS_TIME).append(VALUE_DELIMITER);
        logmsg.append(QuoteCallSnapshot.elapsedCasTime());
        logmsg.append(TOKEN_DELIMITER).append(SERVER_TIME).append(VALUE_DELIMITER);
        logmsg.append(QuoteCallSnapshot.elapsedServerTime());
        logmsg.append(TOKEN_DELIMITER).append(FIX_TIME).append(VALUE_DELIMITER);
        logmsg.append(QuoteCallSnapshot.elapsedFixTime());
        logmsg.append(TOKEN_DELIMITER).append(CLASS_LOCK_WAIT_TIME).append(VALUE_DELIMITER);
        logmsg.append(QuoteCallSnapshot.elapsedClassKeyLockWaitTime());
        logmsg.append(TOKEN_DELIMITER).append(CLASS_LOCK_HOLD_TIME).append(VALUE_DELIMITER);
        logmsg.append(QuoteCallSnapshot.elapsedClassKeyLockHoldTime());
        logmsg.append(TOKEN_DELIMITER).append(CACHE_LOCK_WAIT_TIME).append(VALUE_DELIMITER);
        logmsg.append(QuoteCallSnapshot.elapsedQuoteCacheLockWaitTime());
        logmsg.append(TOKEN_DELIMITER).append(CACHE_LOCK_HOLD_TIME).append(VALUE_DELIMITER);
        logmsg.append(QuoteCallSnapshot.elapsedQuoteCacheLockHoldTime());
        logmsg.append(TOKEN_DELIMITER).append(ENTITY_ID).append(VALUE_DELIMITER);
        logmsg.append(entityId);
        logmsg.append(TOKEN_DELIMITER);
        logmsg.append(sessInfo);
        logmsg.append(TOKEN_DELIMITER).append(SESSION_NAME).append(VALUE_DELIMITER);
        logmsg.append(sessionName);
        logmsg.append(TOKEN_DELIMITER).append(USER_ASSIGNED_ID).append(VALUE_DELIMITER);
        logmsg.append(userAssignedId);
        logmsg.append(TOKEN_DELIMITER).append(CLASS_KEY).append(VALUE_DELIMITER);
        logmsg.append(classKey);
        logmsg.append(TOKEN_DELIMITER).append(SIZE).append(VALUE_DELIMITER);
        logmsg.append(QuoteCallSnapshot.elementSequenceSize());
        if (firstQuoteKey >= 0)
        {
            logmsg.append(TOKEN_DELIMITER).append(QUOTE_KEY).append(VALUE_DELIMITER);
            logmsg.append(firstQuoteKey);
        }
        logmsg.append(TOKEN_DELIMITER).append(CONCURRENT_CLASS_LOCKS).append(VALUE_DELIMITER);
        logmsg.append(QuoteCallSnapshot.getSemaphoresUsed());
        return logmsg.toString();
    }

    /**
     * creates a log message for quote cancel timings
     */
    public static String createQuoteLogSnapshotForCancel(String method, String sessionName, int classKey, int productKey, long entityId, String sessInfo)
    {
        StringBuilder logmsg = new StringBuilder(330);
        logmsg.append(method).append(TOKEN_DELIMITER).append(RETURNING);
        logmsg.append(TOKEN_DELIMITER).append(TOTAL_TIME).append(VALUE_DELIMITER);
        logmsg.append(QuoteCallSnapshot.elapsedTotalTime());
        logmsg.append(TOKEN_DELIMITER).append(CAS_TIME).append(VALUE_DELIMITER);
        logmsg.append(QuoteCallSnapshot.elapsedCasTime());
        logmsg.append(TOKEN_DELIMITER).append(SERVER_TIME).append(VALUE_DELIMITER);
        logmsg.append(QuoteCallSnapshot.elapsedServerTime());
        logmsg.append(TOKEN_DELIMITER).append(FIX_TIME).append(VALUE_DELIMITER);
        logmsg.append(QuoteCallSnapshot.elapsedFixTime());
        logmsg.append(TOKEN_DELIMITER).append(CLASS_LOCK_WAIT_TIME).append(VALUE_DELIMITER);
        logmsg.append(QuoteCallSnapshot.elapsedClassKeyLockWaitTime());
        logmsg.append(TOKEN_DELIMITER).append(CLASS_LOCK_HOLD_TIME).append(VALUE_DELIMITER);
        logmsg.append(QuoteCallSnapshot.elapsedClassKeyLockHoldTime());
        logmsg.append(TOKEN_DELIMITER).append(CACHE_LOCK_WAIT_TIME).append(VALUE_DELIMITER);
        logmsg.append(QuoteCallSnapshot.elapsedQuoteCacheLockWaitTime());
        logmsg.append(TOKEN_DELIMITER).append(CACHE_LOCK_HOLD_TIME).append(VALUE_DELIMITER);
        logmsg.append(QuoteCallSnapshot.elapsedQuoteCacheLockHoldTime());
        logmsg.append(TOKEN_DELIMITER).append(ENTITY_ID).append(VALUE_DELIMITER);
        logmsg.append(entityId);
        logmsg.append(TOKEN_DELIMITER);
        logmsg.append(sessInfo);
        logmsg.append(TOKEN_DELIMITER).append(SESSION_NAME).append(VALUE_DELIMITER);
        logmsg.append(sessionName);
        if (classKey > 0)
        {
            logmsg.append(TOKEN_DELIMITER).append(CLASS_KEY).append(VALUE_DELIMITER);
	        logmsg.append(classKey);
        }
        if (productKey > 0)
        {
            logmsg.append(TOKEN_DELIMITER).append(PRODUCT_KEY).append(VALUE_DELIMITER);
	        logmsg.append(productKey);
        }
        return logmsg.toString();
    }

     /**
     * creates an order timing log message
     */
    public static String createOrderLogSnapshot(String method, final OrderStruct order, long entityId, String sessInfo)
    {
        StringBuilder logmsg = new StringBuilder(330);
        logmsg.append(method).append(TOKEN_DELIMITER).append(RETURNING);
        logmsg.append(TOKEN_DELIMITER).append(TOTAL_TIME).append(VALUE_DELIMITER);
        logmsg.append(OrderCallSnapshot.elapsedTotalTime());
        logmsg.append(TOKEN_DELIMITER).append(CAS_TIME).append(VALUE_DELIMITER);
        logmsg.append(OrderCallSnapshot.elapsedCasTime());
        logmsg.append(TOKEN_DELIMITER).append(SERVER_TIME).append(VALUE_DELIMITER);
        logmsg.append(OrderCallSnapshot.elapsedServerTime());
        logmsg.append(TOKEN_DELIMITER).append(FIX_TIME).append(VALUE_DELIMITER);
        logmsg.append(OrderCallSnapshot.elapsedFixTime());
        logmsg.append(TOKEN_DELIMITER).append(ENTITY_ID).append(VALUE_DELIMITER);
        logmsg.append(entityId);
        logmsg.append(TOKEN_DELIMITER);
        logmsg.append(sessInfo);
        logmsg.append(TOKEN_DELIMITER).append(SESSION_NAME).append(VALUE_DELIMITER);
        logmsg.append(order.sessionNames[0]);
        logmsg.append(TOKEN_DELIMITER).append(USER_ASSIGNED_ID).append(VALUE_DELIMITER);
        logmsg.append(order.userAssignedId);
        logmsg.append(TOKEN_DELIMITER).append(USER_ACRONYM).append(VALUE_DELIMITER);
        logmsg.append(order.userAcronym.acronym);
        logmsg.append(TOKEN_DELIMITER).append(BRANCH).append(VALUE_DELIMITER);
        logmsg.append(order.orderId.branch);
        logmsg.append(TOKEN_DELIMITER).append(BRANCH_SEQ_NUMBER).append(VALUE_DELIMITER);
        logmsg.append(order.orderId.branchSequenceNumber);
        logmsg.append(TOKEN_DELIMITER).append(CORR_FIRM).append(VALUE_DELIMITER);
        logmsg.append(order.orderId.correspondentFirm);
        logmsg.append(TOKEN_DELIMITER).append(EXGU_FIRM).append(VALUE_DELIMITER);
        logmsg.append(order.orderId.executingOrGiveUpFirm.firmNumber);
        logmsg.append(TOKEN_DELIMITER).append(CLASS_KEY).append(VALUE_DELIMITER);
        logmsg.append(order.classKey);
        logmsg.append(TOKEN_DELIMITER).append(PRODUCT_KEY).append(VALUE_DELIMITER);
        logmsg.append(order.productKey);
        logmsg.append(TOKEN_DELIMITER).append(SIDE).append(VALUE_DELIMITER);
        logmsg.append(order.side);
        logmsg.append(TOKEN_DELIMITER).append(QUANTITY).append(VALUE_DELIMITER);
        logmsg.append(order.originalQuantity);
        logmsg.append(TOKEN_DELIMITER).append(TYPE).append(VALUE_DELIMITER);
        logmsg.append(order.orderOriginType);
    	return logmsg.toString();
    }
    /**
    * creates an order timing log message
    */
   public static String createLightOrderLogSnapshot(String method, String sessionInfo, String sessionName, String userAcronym,int classKey, final LightOrderEntryStruct order, long entityId)
   {
       StringBuilder logmsg = new StringBuilder(330);
       logmsg.append(method).append(TOKEN_DELIMITER).append(RETURNING);
       logmsg.append(TOKEN_DELIMITER).append(TOTAL_TIME).append(VALUE_DELIMITER);
       logmsg.append(OrderCallSnapshot.elapsedTotalTime());
       logmsg.append(TOKEN_DELIMITER).append(CAS_TIME).append(VALUE_DELIMITER);
       logmsg.append(OrderCallSnapshot.elapsedCasTime());
       logmsg.append(TOKEN_DELIMITER).append(SERVER_TIME).append(VALUE_DELIMITER);
       logmsg.append(OrderCallSnapshot.elapsedServerTime());
       logmsg.append(TOKEN_DELIMITER).append(FIX_TIME).append(VALUE_DELIMITER);
       logmsg.append(OrderCallSnapshot.elapsedFixTime());
       logmsg.append(TOKEN_DELIMITER).append(ENTITY_ID).append(VALUE_DELIMITER);
       logmsg.append(entityId);
       logmsg.append(TOKEN_DELIMITER);
       logmsg.append(sessionInfo);
       logmsg.append(TOKEN_DELIMITER).append(SESSION_NAME).append(VALUE_DELIMITER);
       logmsg.append(sessionName);
       logmsg.append(TOKEN_DELIMITER).append(USER_ASSIGNED_ID).append(VALUE_DELIMITER);
       logmsg.append(order.userAssignedId);
       logmsg.append(TOKEN_DELIMITER).append(USER_ACRONYM).append(VALUE_DELIMITER);
       logmsg.append(userAcronym);
       logmsg.append(TOKEN_DELIMITER).append(BRANCH).append(VALUE_DELIMITER);
       logmsg.append(order.branch);
       logmsg.append(TOKEN_DELIMITER).append(BRANCH_SEQ_NUMBER).append(VALUE_DELIMITER);
       logmsg.append(order.branchSequenceNumber);
       logmsg.append(TOKEN_DELIMITER).append(CORR_FIRM).append(VALUE_DELIMITER);
       logmsg.append(order.cmtaFirmNumber);
       logmsg.append(TOKEN_DELIMITER).append(CLASS_KEY).append(VALUE_DELIMITER);
       logmsg.append(classKey);
       logmsg.append(TOKEN_DELIMITER).append(PRODUCT_KEY).append(VALUE_DELIMITER);
       logmsg.append(order.productKey);
       logmsg.append(TOKEN_DELIMITER).append(SIDE).append(VALUE_DELIMITER);
       logmsg.append(order.side);
       logmsg.append(TOKEN_DELIMITER).append(QUANTITY).append(VALUE_DELIMITER);
       logmsg.append(order.originalQuantity);
       logmsg.append(TOKEN_DELIMITER).append(TYPE).append(VALUE_DELIMITER);
       logmsg.append(order.orderOriginType);
       return logmsg.toString();
   }

    public static String createLightOrderCancelByIdLogSnapshot(String method, String sessionInfo, String sessionName, String userAcronym,int classKey, int productKey,  final LightOrderResultStruct resultStruct, long entityId, int orderHighId,int orderLowId)
    {
        StringBuilder logmsg = new StringBuilder(400);
        logmsg.append(method).append(TOKEN_DELIMITER).append(RETURNING);
        logmsg.append(TOKEN_DELIMITER).append(TOTAL_TIME).append(VALUE_DELIMITER);
        logmsg.append(OrderCallSnapshot.elapsedTotalTime());
        logmsg.append(TOKEN_DELIMITER).append(CAS_TIME).append(VALUE_DELIMITER);
        logmsg.append(OrderCallSnapshot.elapsedCasTime());
        logmsg.append(TOKEN_DELIMITER).append(SERVER_TIME).append(VALUE_DELIMITER);
        logmsg.append(OrderCallSnapshot.elapsedServerTime());
        logmsg.append(TOKEN_DELIMITER).append(FIX_TIME).append(VALUE_DELIMITER);
        logmsg.append(OrderCallSnapshot.elapsedFixTime());
        logmsg.append(TOKEN_DELIMITER).append(ENTITY_ID).append(VALUE_DELIMITER);
        logmsg.append(entityId);
        logmsg.append(TOKEN_DELIMITER);
        logmsg.append(sessionInfo);
        logmsg.append(TOKEN_DELIMITER).append(SESSION_NAME).append(VALUE_DELIMITER);
        logmsg.append(sessionName);
        logmsg.append(TOKEN_DELIMITER).append(USER_ACRONYM).append(VALUE_DELIMITER);
        logmsg.append(userAcronym);
        logmsg.append(TOKEN_DELIMITER).append(BRANCH).append(VALUE_DELIMITER);
        logmsg.append(resultStruct.branch);
        logmsg.append(TOKEN_DELIMITER).append(BRANCH_SEQ_NUMBER).append(VALUE_DELIMITER);
        logmsg.append(resultStruct.branchSequenceNumber);
        if(orderHighId!=orderLowId)
        {
            logmsg.append(TOKEN_DELIMITER).append(ORDER_HIGH_ID).append(VALUE_DELIMITER);
            logmsg.append(orderHighId);
            logmsg.append(TOKEN_DELIMITER).append(ORDER_LOW_ID).append(VALUE_DELIMITER);
            logmsg.append(orderLowId);


        }
        logmsg.append(TOKEN_DELIMITER).append(CLASS_KEY).append(VALUE_DELIMITER);
        logmsg.append(classKey);
        logmsg.append(TOKEN_DELIMITER).append(PRODUCT_KEY).append(VALUE_DELIMITER);
        logmsg.append(productKey);
        logmsg.append(TOKEN_DELIMITER).append(SIDE).append(VALUE_DELIMITER);
        logmsg.append(resultStruct.side);
        logmsg.append(TOKEN_DELIMITER).append(CANCELED_QUANTITY).append(VALUE_DELIMITER);
        logmsg.append(resultStruct.cancelledQuantity);
        logmsg.append(TOKEN_DELIMITER).append(LEAVE_QUANTITY).append(VALUE_DELIMITER);
        logmsg.append(resultStruct.leavesQuantity);
        logmsg.append(TOKEN_DELIMITER).append(TRADE_QUANTITY).append(VALUE_DELIMITER);
        logmsg.append(resultStruct.tradedQuantity);
        return logmsg.toString();



    }

 public static String createLightOrderCancelLogSnapshot(String method, String sessionInfo, String sessionName, String userAcronym,int classKey, int productKey,  final LightOrderResultStruct resultStruct, long entityId)
 {
     return createLightOrderCancelByIdLogSnapshot(method,sessionInfo,sessionName,userAcronym,classKey, productKey,resultStruct,entityId,-1,-1);
 }
    /**
     * creates a non-order timing log message
     */
    public static String createLogSnapshot(String method, ProductKeysStruct productKeysStruct, String sessionName, OrderIdStruct orderId, String userAssignedId, int quantity, long entityId, String sessInfo)
    {
        StringBuilder logmsg = new StringBuilder(330);
        logmsg.append(method).append(TOKEN_DELIMITER).append(RETURNING);
        logmsg.append(TOKEN_DELIMITER).append(TOTAL_TIME).append(VALUE_DELIMITER);
        logmsg.append(OrderCallSnapshot.elapsedTotalTime());
        logmsg.append(TOKEN_DELIMITER).append(CAS_TIME).append(VALUE_DELIMITER);
        logmsg.append(OrderCallSnapshot.elapsedCasTime());
        logmsg.append(TOKEN_DELIMITER).append(SERVER_TIME).append(VALUE_DELIMITER);
        logmsg.append(OrderCallSnapshot.elapsedServerTime());
        logmsg.append(TOKEN_DELIMITER).append(FIX_TIME).append(VALUE_DELIMITER);
        logmsg.append(OrderCallSnapshot.elapsedFixTime());
        logmsg.append(TOKEN_DELIMITER).append(ENTITY_ID).append(VALUE_DELIMITER);
        logmsg.append(entityId);
        logmsg.append(TOKEN_DELIMITER);
        logmsg.append(sessInfo);
        logmsg.append(TOKEN_DELIMITER).append(SESSION_NAME).append(VALUE_DELIMITER);
        logmsg.append(sessionName);
        logmsg.append(TOKEN_DELIMITER).append(USER_ASSIGNED_ID).append(VALUE_DELIMITER);
        logmsg.append(userAssignedId);
//        logmsg.append(TOKEN_DELIMITER).append(USER_ACRONYM).append(VALUE_DELIMITER);
//        logmsg.append(userAcronym);
        if (orderId != null)
        {
            logmsg.append(TOKEN_DELIMITER).append(CORR_FIRM).append(VALUE_DELIMITER);
            logmsg.append(orderId.correspondentFirm);
            logmsg.append(TOKEN_DELIMITER).append(BRANCH).append(VALUE_DELIMITER);
            logmsg.append(orderId.branch);
            logmsg.append(TOKEN_DELIMITER).append(BRANCH_SEQ_NUMBER).append(VALUE_DELIMITER);
            logmsg.append(orderId.branchSequenceNumber);
        }
        if (orderId != null)
        {
            logmsg.append(TOKEN_DELIMITER).append(EXGU_FIRM).append(VALUE_DELIMITER);
            logmsg.append(orderId.executingOrGiveUpFirm.firmNumber);
        }
        logmsg.append(TOKEN_DELIMITER).append(CLASS_KEY).append(VALUE_DELIMITER);
        logmsg.append(productKeysStruct.classKey);
        logmsg.append(TOKEN_DELIMITER).append(PRODUCT_KEY).append(VALUE_DELIMITER);
        logmsg.append(productKeysStruct.productKey);
        logmsg.append(TOKEN_DELIMITER).append(QUANTITY).append(VALUE_DELIMITER);
        logmsg.append(quantity);
    	return logmsg.toString();
    }

    public static String createQuoteLogSnapshotForCancelAll(String method, String sessionName, int classKey, int productKey, long entityId, String sessInfo)
    {
        StringBuilder logmsg = new StringBuilder(330);
        logmsg.append(method).append(TOKEN_DELIMITER).append(RETURNING);
        logmsg.append(TOKEN_DELIMITER).append(TOTAL_TIME).append(VALUE_DELIMITER);
        logmsg.append(QuoteCallSnapshot.elapsedTotalTime());
        logmsg.append(TOKEN_DELIMITER).append(CAS_TIME).append(VALUE_DELIMITER);
        logmsg.append(QuoteCallSnapshot.elapsedCasTime());
        logmsg.append(TOKEN_DELIMITER).append(SERVER_TIME).append(VALUE_DELIMITER);
        logmsg.append(QuoteCallSnapshot.elapsedServerTime());
        logmsg.append(TOKEN_DELIMITER).append(FIX_TIME).append(VALUE_DELIMITER);
        logmsg.append(QuoteCallSnapshot.elapsedFixTime());
        logmsg.append(TOKEN_DELIMITER).append(CLASS_LOCK_WAIT_TIME).append(VALUE_DELIMITER);
        logmsg.append(QuoteCallSnapshot.elapsedClassKeyLockWaitTime());
        logmsg.append(TOKEN_DELIMITER).append(CLASS_LOCK_HOLD_TIME).append(VALUE_DELIMITER);
        logmsg.append(QuoteCallSnapshot.elapsedClassKeyLockHoldTime());
        logmsg.append(TOKEN_DELIMITER).append(CACHE_LOCK_WAIT_TIME).append(VALUE_DELIMITER);
        logmsg.append(QuoteCallSnapshot.elapsedQuoteCacheLockWaitTime());
        logmsg.append(TOKEN_DELIMITER).append(CACHE_LOCK_HOLD_TIME).append(VALUE_DELIMITER);
        logmsg.append(QuoteCallSnapshot.elapsedQuoteCacheLockHoldTime());
        logmsg.append(TOKEN_DELIMITER).append(ENTITY_ID).append(VALUE_DELIMITER);
        logmsg.append(entityId);
        logmsg.append(TOKEN_DELIMITER);
        logmsg.append(sessInfo);
        logmsg.append(TOKEN_DELIMITER).append(SESSION_NAME).append(VALUE_DELIMITER);
        logmsg.append(sessionName);
        if (classKey > 0)
        {
            logmsg.append(TOKEN_DELIMITER).append(CLASS_KEY).append(VALUE_DELIMITER);
	        logmsg.append(classKey);
        }
        if (productKey > 0)
        {
            logmsg.append(TOKEN_DELIMITER).append(PRODUCT_KEY).append(VALUE_DELIMITER);
	        logmsg.append(productKey);
        }
        return logmsg.toString();
    }


    public static String createQuoteLog(String method, String sessInfo, String sessionName)
    {
//        return createQuoteLog(method, 0, sessInfo, sessionName, 0, 0, 0, (short)-1);
        StringBuilder logmsg = new StringBuilder(330);
        logmsg.append(method).append(TOKEN_DELIMITER).append(CALLING);
        logmsg.append(TOKEN_DELIMITER);
        logmsg.append(sessInfo);
        logmsg.append(TOKEN_DELIMITER).append(SESSION_NAME).append(VALUE_DELIMITER);
        logmsg.append(sessionName);
        return logmsg.toString();
    }

    public static String createQuoteLog(String method, String sessInfo, String sessionName, int productKey)
    {
        StringBuilder logmsg = new StringBuilder(330);
        logmsg.append(method).append(TOKEN_DELIMITER).append(CALLING);
        logmsg.append(TOKEN_DELIMITER);
        logmsg.append(sessInfo);
        logmsg.append(TOKEN_DELIMITER).append(SESSION_NAME).append(VALUE_DELIMITER);
        logmsg.append(sessionName);
        logmsg.append(TOKEN_DELIMITER).append(PRODUCT_KEY).append(VALUE_DELIMITER);
        logmsg.append(productKey);
        return logmsg.toString();
    }

    public static String createQuoteLog(String method, String sessInfo, int classKey, String sessionName)
    {
        StringBuilder logmsg = new StringBuilder(330);
        logmsg.append(method).append(TOKEN_DELIMITER).append(CALLING);
        logmsg.append(TOKEN_DELIMITER);
        logmsg.append(sessInfo);
        logmsg.append(TOKEN_DELIMITER).append(SESSION_NAME).append(VALUE_DELIMITER);
        logmsg.append(sessionName);
        logmsg.append(TOKEN_DELIMITER).append(CLASS_KEY).append(VALUE_DELIMITER);
        logmsg.append(classKey);
        return logmsg.toString();
    }

    public static String createQuoteLog(String method, long entityId, String sessInfo, String sessionName, int productKey)
    {
        StringBuilder logmsg = new StringBuilder(330);
        logmsg.append(method).append(TOKEN_DELIMITER).append(CALLING);
        if (entityId > 0)
        {
            logmsg.append(TOKEN_DELIMITER).append(ENTITY_ID).append(VALUE_DELIMITER);
            logmsg.append(entityId);
        }
        logmsg.append(TOKEN_DELIMITER);
        logmsg.append(sessInfo);
        logmsg.append(TOKEN_DELIMITER).append(SESSION_NAME).append(VALUE_DELIMITER);
        logmsg.append(sessionName);
        logmsg.append(TOKEN_DELIMITER).append(PRODUCT_KEY).append(VALUE_DELIMITER);
        logmsg.append(productKey);
        return logmsg.toString();
    }

    public static String createQuoteLog(String method, long entityId, String sessInfo, String sessionName, int classKey, int size)
    {
        StringBuilder logmsg = new StringBuilder(330);
        logmsg.append(method).append(TOKEN_DELIMITER).append(CALLING);
        if (entityId > 0)
        {
            logmsg.append(TOKEN_DELIMITER).append(ENTITY_ID).append(VALUE_DELIMITER);
            logmsg.append(entityId);
        }
        logmsg.append(TOKEN_DELIMITER);
        logmsg.append(sessInfo);
        logmsg.append(TOKEN_DELIMITER).append(SESSION_NAME).append(VALUE_DELIMITER);
        logmsg.append(sessionName);
        logmsg.append(TOKEN_DELIMITER).append(CLASS_KEY).append(VALUE_DELIMITER);
        logmsg.append(classKey);
        logmsg.append(TOKEN_DELIMITER).append(SIZE).append(VALUE_DELIMITER);
        logmsg.append(size);
        return logmsg.toString();
    }

    public static String createQuoteLog(String method, long entityId, String sessInfo, String sessionName, int classKey, int size, short quoteToken)
    {
        StringBuilder logmsg = new StringBuilder(330);
        logmsg.append(method).append(TOKEN_DELIMITER).append(CALLING);
        if (entityId > 0)
        {
            logmsg.append(TOKEN_DELIMITER).append(ENTITY_ID).append(VALUE_DELIMITER);
            logmsg.append(entityId);
        }
        logmsg.append(TOKEN_DELIMITER);
        logmsg.append(sessInfo);
        logmsg.append(TOKEN_DELIMITER).append(SESSION_NAME).append(VALUE_DELIMITER);
        logmsg.append(sessionName);
        logmsg.append(TOKEN_DELIMITER).append(CLASS_KEY).append(VALUE_DELIMITER);
        logmsg.append(classKey);
        logmsg.append(TOKEN_DELIMITER).append(SIZE).append(VALUE_DELIMITER);
        logmsg.append(size);
        logmsg.append(TOKEN_DELIMITER).append(QUOTE_TOKEN).append(VALUE_DELIMITER);
        logmsg.append(quoteToken);
        return logmsg.toString();
    }
    
    public static String createQuoteLog(String method, long entityId, String sessInfo, String sessionName, int classKey, int productKey,
                                               int size, short quoteToken)
    {
        StringBuilder logmsg = new StringBuilder(330);
        logmsg.append(method).append(TOKEN_DELIMITER).append(CALLING);
        if (entityId > 0)
        {
            logmsg.append(TOKEN_DELIMITER).append(ENTITY_ID).append(VALUE_DELIMITER);
            logmsg.append(entityId);
        }
        logmsg.append(TOKEN_DELIMITER);
        logmsg.append(sessInfo);
        logmsg.append(TOKEN_DELIMITER).append(SESSION_NAME).append(VALUE_DELIMITER);
        logmsg.append(sessionName);
        if (classKey > 0)
        {
            logmsg.append(TOKEN_DELIMITER).append(CLASS_KEY).append(VALUE_DELIMITER);
            logmsg.append(classKey);
        }
        if (productKey > 0)
        {
            logmsg.append(TOKEN_DELIMITER).append(PRODUCT_KEY).append(VALUE_DELIMITER);
            logmsg.append(productKey);
        }
        if (size > 0)
        {
            logmsg.append(TOKEN_DELIMITER).append(SIZE).append(VALUE_DELIMITER);
            logmsg.append(size);
        }
        if (quoteToken >= 0)
        {
            logmsg.append(TOKEN_DELIMITER).append(QUOTE_TOKEN).append(VALUE_DELIMITER);
            logmsg.append(quoteToken);
        }
        return logmsg.toString();
    }

    public static String createOrderLog(String method, long entityId, String sessInfo, String sessionName)
    {
        StringBuilder logmsg = new StringBuilder(330);
        logmsg.append(method).append(TOKEN_DELIMITER).append(CALLING);
        if (entityId > 0)
        {
            logmsg.append(TOKEN_DELIMITER).append(ENTITY_ID).append(VALUE_DELIMITER);
            logmsg.append(entityId);
        }
        logmsg.append(TOKEN_DELIMITER);
        logmsg.append(sessInfo);
        logmsg.append(TOKEN_DELIMITER).append(SESSION_NAME).append(VALUE_DELIMITER);
        logmsg.append(sessionName);
        return logmsg.toString();        
    }

    public static String createOrderLog(String method, long entityId, String sessInfo, String sessionName, int productKey)
    {
        return createOrderLog(method,entityId,sessInfo,-1,sessionName,productKey);

    }
    public static String createOrderLog(String method, long entityId, String sessInfo, int classKey, String sessionName, int productKey)
    {
        StringBuilder logmsg = new StringBuilder(330);
        logmsg.append(method).append(TOKEN_DELIMITER).append(CALLING);
        if (entityId > 0)
        {
            logmsg.append(TOKEN_DELIMITER).append(ENTITY_ID).append(VALUE_DELIMITER);
            logmsg.append(entityId);
        }
        logmsg.append(TOKEN_DELIMITER);
        logmsg.append(sessInfo);
        logmsg.append(TOKEN_DELIMITER).append(SESSION_NAME).append(VALUE_DELIMITER);
        logmsg.append(sessionName);
        if (productKey > 0)
        {
            logmsg.append(TOKEN_DELIMITER).append(PRODUCT_KEY).append(VALUE_DELIMITER);
            logmsg.append(productKey);
        }
        if (classKey > 0)
        {
            logmsg.append(TOKEN_DELIMITER).append(CLASS_KEY).append(VALUE_DELIMITER);
            logmsg.append(classKey);
        }

        return logmsg.toString();
    }

    public static String createOrderLog(String method, long entityId, String sessInfo, String sessionName, String orderId, String orderId2)
    {
        StringBuilder logmsg = new StringBuilder(330);
        logmsg.append(method).append(TOKEN_DELIMITER).append(CALLING);
        if (entityId > 0)
        {
            logmsg.append(TOKEN_DELIMITER).append(ENTITY_ID).append(VALUE_DELIMITER);
            logmsg.append(entityId);
        }
        logmsg.append(TOKEN_DELIMITER);
        logmsg.append(sessInfo);
        logmsg.append(TOKEN_DELIMITER).append(SESSION_NAME).append(VALUE_DELIMITER);
        logmsg.append(sessionName);
        if (orderId != null)
        {
            logmsg.append(TOKEN_DELIMITER);
            logmsg.append(orderId);
        }
        if (orderId2 != null)
        {
            logmsg.append(TOKEN_DELIMITER).append(ORDER_2).append(TOKEN_DELIMITER);
            logmsg.append(orderId2);
        }
        return logmsg.toString();
    }

    public static String createOrderLog(String method, long entityId, String sessInfo, String sessionName, String orderId, String orderId2, int productKey,int classKey)
    {
        StringBuilder logmsg = new StringBuilder(330);
        logmsg.append(method).append(TOKEN_DELIMITER).append(CALLING);
        if (entityId > 0)
        {
            logmsg.append(TOKEN_DELIMITER).append(ENTITY_ID).append(VALUE_DELIMITER);
            logmsg.append(entityId);
        }
        logmsg.append(TOKEN_DELIMITER);
        logmsg.append(sessInfo);
        logmsg.append(TOKEN_DELIMITER).append(SESSION_NAME).append(VALUE_DELIMITER);
        logmsg.append(sessionName);
        if (orderId != null)
        {
            logmsg.append(TOKEN_DELIMITER);
            logmsg.append(orderId);
        }
        if (orderId2 != null)
        {
            logmsg.append(TOKEN_DELIMITER).append(ORDER_2).append(TOKEN_DELIMITER);
            logmsg.append(orderId2);
        }
        if (classKey > 0)
        {
            logmsg.append(TOKEN_DELIMITER).append(CLASS_KEY).append(VALUE_DELIMITER);
            logmsg.append(classKey);
        }
        if (productKey > 0)
        {
            logmsg.append(TOKEN_DELIMITER).append(PRODUCT_KEY).append(VALUE_DELIMITER);
            logmsg.append(productKey);
        }
        return logmsg.toString();
    }
    public static String createOrderLog(String method, long entityId, String sessInfo, String sessionName, String orderId, String orderId2, int productKey)
    {
        return createOrderLog(method,entityId,sessInfo,sessionName,orderId,orderId2,productKey,-1);
    }
    public static String createFloorTradeLog(String method, long entityId, String userId, com.cboe.idl.cmiTrade.FloorTradeEntryStruct floorTradeEntryStruct)
    {
    	StringBuilder logmsg = new StringBuilder(300);
    	logmsg.append(method).append(TOKEN_DELIMITER).append(CALLING);
    	logmsg.append(TOKEN_DELIMITER).append(USER_ID).append(VALUE_DELIMITER);
    	logmsg.append(userId);
    	logmsg.append(TOKEN_DELIMITER).append(PRODUCT_KEY).append(VALUE_DELIMITER);
    	logmsg.append(floorTradeEntryStruct.productKey);
    	logmsg.append(TOKEN_DELIMITER).append(SESSION_NAME).append(VALUE_DELIMITER);
    	logmsg.append(floorTradeEntryStruct.sessionName);
    	logmsg.append(TOKEN_DELIMITER).append(QUANTITY).append(VALUE_DELIMITER);
    	logmsg.append(floorTradeEntryStruct.quantity);
    	logmsg.append(TOKEN_DELIMITER).append(SIDE).append(VALUE_DELIMITER);
    	logmsg.append(floorTradeEntryStruct.side);
    	logmsg.append(TOKEN_DELIMITER).append(USER).append(VALUE_DELIMITER);
    	logmsg.append(floorTradeEntryStruct.executingMarketMaker.acronym).append(VALUE_DELIMITER).append(floorTradeEntryStruct.executingMarketMaker.exchange);
    	logmsg.append(TOKEN_DELIMITER).append(FIRM).append(VALUE_DELIMITER);
        if (floorTradeEntryStruct.firm != null) {
    	    logmsg.append(floorTradeEntryStruct.firm.exchange).append(VALUE_DELIMITER).append(floorTradeEntryStruct.firm.firmNumber);
        }
        logmsg.append(TOKEN_DELIMITER).append(CMTA).append(VALUE_DELIMITER);
        if (floorTradeEntryStruct.cmta != null) {
            logmsg.append(floorTradeEntryStruct.cmta.exchange).append(VALUE_DELIMITER).append(floorTradeEntryStruct.cmta.firmNumber);
        }
        logmsg.append(TOKEN_DELIMITER).append(CONTRA_BROKER).append(VALUE_DELIMITER);
    	logmsg.append(floorTradeEntryStruct.contraBroker.acronym).append(VALUE_DELIMITER).append(floorTradeEntryStruct.contraBroker.exchange);
    	logmsg.append(TOKEN_DELIMITER).append(CONTRA_FIRM).append(VALUE_DELIMITER);
    	logmsg.append(floorTradeEntryStruct.contraFirm.exchange).append(VALUE_DELIMITER).append(floorTradeEntryStruct.contraFirm.firmNumber);
    	logmsg.append(TOKEN_DELIMITER).append(TRADED_TIME).append(VALUE_DELIMITER);
    	logmsg.append(floorTradeEntryStruct.timeTraded.time.hour).append(VALUE_DELIMITER).append(floorTradeEntryStruct.timeTraded.time.minute);    	
        logmsg.append(TOKEN_DELIMITER).append(ENTITY_ID).append(VALUE_DELIMITER);
        logmsg.append(entityId);
    	return logmsg.toString();
    }
}

