package com.cboe.interfaces.domain;

import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiUtil.CboeIdStruct;
import com.cboe.exceptions.*;

/**
 * This class creates and finds OrderHistory.
 *
 * @author Werner Kubitsch
 */
public interface OrderHistoryHome
{
	public final static String HOME_NAME = "OrderHistoryHome";
/**
 * @author Werner Kubitsch
 * @param theOrder Order
 * @param entryType char
 * @param entry String
 */
public abstract OrderHistory create(Order theOrder, short entryType)
		throws TransactionFailedException;

public void createHistoryNewOrder(Order theOrder, short entryType)
        throws TransactionFailedException;

public void createHistoryBookOrder(Order theOrder, short entryType,
            int amountBooked)
		throws TransactionFailedException;

public void createHistoryBustOrder(Order theOrder, short entryType,
            int amountBusted, CboeIdStruct tradeId)
		throws TransactionFailedException;

public void createHistoryStateChangeOrder(Order theOrder, short entryType,
            short newState)
		throws TransactionFailedException;

public void createHistoryCancelOrder(Order theOrder, short entryType,
            int remainingQuantity , int cancelledQuantity , int tooLateToCancelQuantity, String userAssignedCancelId);

public void createHistoryCancelOrder(Order theOrder, short entryType, short cancelReason,
            int remainingQuantity , int cancelledQuantity , int tooLateToCancelQuantity, String userAssignedCancelId);

public void createHistoryCancelReplaceOrder(Order theOrder, short entryType,
            int remainingQuantity , int cancelledQuantity , int mismatchedQuantity, Order replaceOrder);

public void createHistoryCancelReplaceOrder(Order theOrder, short entryType,
            int tltcQuantity, Order replaceOrder);

public void createHistoryFillOrder(Order theOrder, short entryType,
            int totalQuantity , int leavesdQuantity, Price tradePrice, CboeIdStruct tradeId, short subEntryType)
		throws TransactionFailedException;

public void createHistoryPriceAdjustOrder(Order theOrder, short entryType,
            int newProductKey, Price newPrice, int newQuantity )
		throws TransactionFailedException;

public void createHistoryBustReinstateOrder(Order theOrder, short entryType,
            int quantity, CboeIdStruct tradeId)
		throws TransactionFailedException;

public void createHistoryUpdateOrder(Order theOrder, short entryType)
		throws TransactionFailedException;

// new methods for spread history

// fix-me: May want to clean up the methods above for regular order history.
// Why do you need to pass the entry type when the method name says what kind of history you're creating?
// Ick.  EJF 8/31/2001

void createHistoryNewOrderLeg(LegOrderDetail theLegDetail);

void createHistoryFillOrderLeg(LegOrderDetail theLegDetail,
    int totalQuantity , int leavesdQuantity, Price tradePrice, CboeIdStruct tradeId);

void createHistoryCancelOrderLeg(LegOrderDetail theLegDetail,
    int remainingQuantity , int cancelledQuantity , int tooLateToCancelQuantity,
    String userAssignedCancelId);

void createHistoryBustOrderLeg(LegOrderDetail theLegDetail,
    int amountBusted, CboeIdStruct tradeId);

void createHistoryBustReinstateOrderLeg(LegOrderDetail theLegDetail,
    int quantity, CboeIdStruct tradeId);

void createHistoryUpdateOrderLeg(LegOrderDetail theLegDetail);

void createHistoryPriceAdjustOrderLeg(LegOrderDetail theLegDetail,
    int newProductKey, Price newPrice, int newQuantity);

/**
 * @author Werner Kubitsch
 * @return QuoteHistory[]
 * @param anOrder Order
 */
public abstract OrderHistory[] find(Order anOrder)
	throws TransactionFailedException, NotFoundException;

/**
 * @author Magic Magee
 * @return OrderHistory[]
 * @param classKey int
 * @param startTime long
 * @param direction short
 */
OrderHistory[] findClassOrdersByTime(String userId, int classKey, long startTime, short direction) throws NotFoundException;
/**
 * @author Magic Magee
 * @return OrderHistory[]
 * @param productKey int
 * @param startTime long
 * @param direction short
 */
OrderHistory[] findProductOrdersByTime(String userId, int productKey, long startTime, short direction) throws NotFoundException;



//Following methods were added by Ravi Rade 09/13/02 for linkage history.

/**
 * This method creates new Linkage Order History for
 * Order Fill Report Reject :entryType = ActivityType.FILL_REJECT
 * @param order Order
 * @parm totalQuantity int
 * @parm leavesQuantity int
 * @parm tradePrice Price
 * @parm tradeId CboeIdStruct
 * @exception TransactionFailedException
 */
public void createHistoryFillReportReject(Order theOrder,
                                    int totalQuantity ,
                                    int leavesdQuantity,
                                    Price tradePrice,
                                    CboeIdStruct tradeId);

/**
 * This method creates new Linkage Order History for
 * New Order Reject :entryType = ActrivityType.NEW_ORDER_REJECT
 * @param order Order
 * @parm cancelReason short
 * @parm remainingQuantity int
 * @parm cancelledQuantity int
 * @parm tooLateToCancelQauntity int
 * @parm userAssignedCancelId String
 */
public void createHistoryNewOrderReject(Order theOrder,
                                    short cancelReason,
                                    int remainingQuantity ,
                                    int cancelledQuantity ,
                                    int tooLateToCancelQuantity,
                                    String userAssignedCancelId);


/**
 * This method creates new Linkage Order History for
 * Cancel Request :entryType = ActivityTypes.CANCEL_ORDER_REQUEST
 * @param order Order
 * @parm cancelReason short
 * @parm remainingQuantity int
 * @parm cancelledQuantity int
 * @parm tooLateToCancelQauntity int
 * @parm userAssignedCancelId String
 */

public void createHistoryCancelOrderRequest(Order theOrder,
                                    short cancelReason,
                                    int remainingQuantity ,
                                    int cancelledQuantity ,
                                    int tooLateToCancelQuantity,
                                    String userAssignedCancelId);



/**
 * This method creates new Linkage Order History for
 * Cancel Request :entryType = ActrivityType.CANCEL_ORDER_REQUEST_REJECT
 * @param order Order
 * @param entryType short
 * @parm cancelReason short
 * @parm remainingQuantity int
 * @parm cancelledQuantity int
 * @parm tooLateToCancelQauntity int
 * @parm userAssignedCancelId String
 */
public void createHistoryCancelOrderRequestReject(Order theOrder,
                                    short cancelReason,
                                    int remainingQuantity ,
                                    int cancelledQuantity ,
                                    int tooLateToCancelQuantity,
                                    String userAssignedCancelId);

/**
 * This method creates new Linkage Order History for
 * Cancel Request :entryType = ActrivityType.CANCEL_REPORT_REJECT
 * @param order Order
 * @parm cancelReason short
 * @parm remainingQuantity int
 * @parm cancelledQuantity int
 * @parm tooLateToCancelQauntity int
 * @parm userAssignedCancelId String
 */
public void createHistoryCancelReportReject(Order theOrder,
                                    short cancelReason,
                                    int remainingQuantity ,
                                    int cancelledQuantity ,
                                    int tooLateToCancelQuantity,
                                    String userAssignedCancelId);

/**
 * This method creates new hybrid order handling History for
 * Request :entryType = ActrivityTypes.HYBRID_PROCESSING_REQUESTED
 * @param order Order
 * @parm handlingInstruction handling instruction for the order
 */
public void createHistoryOrderHandlingInstruction(Order theOrder, HandlingInstruction handlingInstruction)
		throws TransactionFailedException;
/**
 * This method creates new hybrid order returned History for
 * return or an request with reason :entryType = ActrivityTypes.HYBRID_PROCESSING_REQUESTED
 * @param order Order
 * @parm returnReason return reason for the order
 */
public void createHistoryOrderReturned(Order theOrder, short returnReason)
		throws TransactionFailedException;

public void createHistoryFillOrder(Order theOrder, short entryType,
          int totalQuantity , int leavesdQuantity, Price tradePrice, CboeIdStruct tradeId, String UserAssignedId)
           throws TransactionFailedException;

}
