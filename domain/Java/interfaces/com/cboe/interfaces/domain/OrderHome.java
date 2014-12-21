package com.cboe.interfaces.domain;

import java.util.ArrayList;
import java.util.List;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.idl.cmiOrder.FilledReportStruct;
import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.idl.cmiUtil.CboeIdStruct;
import com.cboe.idl.order.MarketDetailStruct;
import com.cboe.idl.order.OrderHandlingStruct;
import com.cboe.idl.order.OrderHandlingStructV2;

/**
 *  Define what the external interface to an OrderHome is, and also implements
 *  the singleton pattern to access the configured type of OrderHome.
 */
public interface OrderHome extends OrderQueryHome
{
	public final static String HOME_NAME = "OrderHome";
	public abstract Order create(OrderHandlingStruct anOrderStruct)
		throws TransactionFailedException, SystemException, DataValidationException;
	public abstract Cross create(OrderHandlingStruct anOrderStruct1, OrderHandlingStruct anOrderStruct2)
		throws TransactionFailedException, SystemException, DataValidationException;
    
	public abstract Cross createCrossForExistingOrder(Order existingOrder, OrderStruct newOrderStruct, boolean isProcessingOutOfSequence, boolean isCrossWithAutoLinkedPAOrder) throws TransactionFailedException, SystemException, DataValidationException;
	public abstract Cross createCrossOnExistingOrders(Order existingOrder, Order order2, boolean processingOutOfSequence) throws TransactionFailedException, SystemException, DataValidationException ;

    /**
     * Creats a cross for an existing order with a new order
     * @param existingOrder the existing order to be one side of the cross
     * @param newOrder the oppsite side new order to cross with the existing order
     * @return Cross a specific type of cross using the orders.
     */
	public abstract Order createLinkageOrder(Order heldOrder, String userId, int linkAwayQuantity, Price botr, String exchange )
		throws TransactionFailedException, DataValidationException, SystemException;


    /**
     * Creates a cross for a NBBO protected customer order, and a new min size guarantee order
     * for a NBBOAgent
     *
     * @param Order nbboProtectedOrder: the customer order for which the nbbo min size guarantee will
     *      be satisfied.
     * @param String nbboAgentId: the userId of the NBBOAgent for the product class of the customer order
     * @param int quantity: the quantity needed for the min size guarantee order
     * @return Cross a cross of nbbo protected customer order and the new min size guarantee order
     */
    public Cross createNBBOMinSizeCross(Order nbboProtectedOrder, String nbboAgentId, int quantity)
		throws TransactionFailedException, SystemException, DataValidationException;

    /**
     * Creates a cross for a held customer order
     * for a NBBOAgent
     *
     * @param Order heldOrder: the customer order being held 
     * @param String nbboAgentId: the userId of the NBBOAgent for the product class of the customer order
     * @param int quantity: the quantity to cross
     * @param boolean isCrossWithAutoLinkedPAOrder
     * @param String awayExchange
     * @param FilledReportStruct filledReportStruct: the fill report containing ClearingFirm, quantity, price, outbound vendor
     * @return Cross a cross of held customer order and the new order agent is going to fill
     */
    public Cross createHeldOrderCross(Order heldOrder, String nbboAgentId, boolean isCrossWithAutoLinkedPAOrder, String awayExchange, FilledReportStruct filledReportStruct)
        throws TransactionFailedException, SystemException, DataValidationException;

    /**
     * Builds the order struct for the given user id and given quantity, to satisfy the given satisfaction order.
     * @param satisfactionOrder
     * @param userId
     * @param quantity
     * @return
     * @throws DataValidationException
     */
    public OrderStruct buildOrderStructForSatisfaction(Order satisfactionOrder, String userId, int quantity)
        throws DataValidationException;
    /**
     * Finds all active market orders.
     *
     * @return all active market orders that have remaining quantity.
     */
    Order[] findActiveMarketOrders();
    
    public abstract Order findOrder(OrderIdStruct orderId)
        throws TransactionFailedException, NotFoundException;
    
    public abstract Order findOrder(String sessionName, int productKey, CboeIdStruct cboeOrderId)
        throws TransactionFailedException, NotFoundException;
    
    public Order findOrder(long orderKey)
        throws TransactionFailedException, DataValidationException, NotFoundException;
    
    public Order[] findPendingStopOrders();
    /**
     * Find all orders for a product -- for all users
     * This is used for price adjustments (stock splits).
     */
    public abstract Order[] findOrdersForProduct(int productKey, boolean sorted);
    
    public abstract Order[] findOrdersForProduct(int productKey, boolean sorted, ArrayList nbboBuyFlashedOrders, ArrayList nbboSellFlashedOrders);

    
    public abstract Order[] findQuoteLikeOrdersForProduct(int productKey)
        throws TransactionFailedException;
    
    public abstract Order[] findQuoteLikeOrdersForUser(String user)
        throws TransactionFailedException;
    
    public void createOrders( OrderHandlingStructV2[] orderHandlingStructs);



    public Order[] findPendingContingentOrders(short contingencyType);

    public void deleteFromCache(List orderIds);

	/**
	 * This method will be called from ORD.
	 */

	public void cancelQuoteLikeOrders(String p_userName,
			boolean p_systemCancel, short cancelReason)
			throws TransactionFailedException, SystemException,
			NotAcceptedException, DataValidationException;
	public abstract Order persistOrder(OrderStruct orderStruct)
    throws SystemException, CommunicationException, TransactionFailedException, AuthorizationException, DataValidationException;
    public abstract Order[] findActivePAOrders();
    public abstract Order[] findActiveSatisfactionOrders();
    public void deleteOrderFromCache(Order orderToBeDeleted);
public Order getOrderFromOHSForBust(String sessionName, int productKey,CboeIdStruct cboeOrderId) throws NotFoundException;
//public void publishAuctionEvent(AuctionHistoryStruct auctionInfo);
public void postSlave();

    public MarketDetailStruct[] buildMarketDetailStructs(String sessionName, int[] productKeys);
}

