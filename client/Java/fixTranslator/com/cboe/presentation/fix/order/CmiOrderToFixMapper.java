/*
 * Created on Aug 2, 2004
 *
 */
package com.cboe.presentation.fix.order;

import java.text.ParseException;
import java.util.Iterator;
import java.util.Set;

import com.cboe.domain.util.ExtensionsHelper;
import com.cboe.domain.util.fixUtil.*;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiConstants.ContingencyTypes;
import com.cboe.idl.cmiConstants.CoverageTypes;
import com.cboe.idl.cmiConstants.ExtensionFields;
import com.cboe.idl.cmiConstants.OrderCancelTypes;
import com.cboe.idl.cmiConstants.PriceTypes;
import com.cboe.idl.cmiConstants.ProductTypes;
import com.cboe.idl.cmiConstants.TimesInForce;
import com.cboe.idl.cmiOrder.CancelRequestStruct;
import com.cboe.idl.cmiOrder.LegOrderEntryStruct;
import com.cboe.idl.cmiOrder.OrderContingencyStruct;
import com.cboe.idl.cmiOrder.OrderDetailStruct;
import com.cboe.idl.cmiOrder.OrderEntryStruct;
import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiQuote.RFQEntryStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.interfaces.presentation.common.logging.IGUILogger;
import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.presentation.product.Strategy;
import com.cboe.presentation.api.APIHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.util.ExceptionBuilder;
import com.cboe.fix.cas.UserDefinedTagConstants;

import com.javtech.appia.Order;
import com.javtech.appia.OrderList;
import com.javtech.appia.OrderCancelReplace;
import com.javtech.appia.OrderCancelRequest;
import com.javtech.appia.QuoteRequest;

/**
 * Map OrderEntry request structures to FIX messages.
 * All methods are static--this class does not maintain state.
 * @author Don Mendelson
 *
 */
public class CmiOrderToFixMapper {

    private static final String FIELD_DELIMITER = "|";
    private static final String TAG_DELIMITER = "=";
    private static final int PRIMARY_ORDER = 0;
    private static final int MATCHED_ORDER = 1;

	/**
	 * Map a CMi multi-legged cancel/replace to a FIX cancel/replace request
	 * @param cancelRequest a CMi cancel request
	 * @param newOrder the CMi replacement order
	 * @param legEntryDetails an array of order legs
	 * @param fixOrder a FIX cancel/replace request
	 * @param doNotSendValue indicator of a null numeric FIX field
	 * @throws SystemException
	 * @throws DataValidationException
	 * @throws NotFoundException
	 * @throws AuthorizationException
	 * @throws CommunicationException
	 */
	public static void mapCancelReplaceToFix(CancelRequestStruct cancelRequest,
			OrderEntryStruct newOrder, LegOrderEntryStruct[] legEntryDetails,
			OrderCancelReplace fixOrder, int doNotSendValue)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException, NotFoundException {
		fixOrder.OrderID = FixUtilMapper.getFixOrderID(cancelRequest.orderId);
		fixOrder.OrigClOrdID = mapClOrdId(cancelRequest.orderId);

		mapOrderFields(cancelRequest, newOrder, fixOrder, doNotSendValue);
		mapSecurity(newOrder.productKey, fixOrder);

		// Append user defined fields to existing value, if any, and update in place.
		fixOrder.UserDefined = mapOrderLegs(legEntryDetails, fixOrder.UserDefined);
	}

	/**
	 * Map a CMi cancel/replace to a FIX cancel/replace request
	 * @param cancelRequest a CMi cancel request
	 * @param newOrder the CMi replacement order
	 * @param fixOrder a FIX cancel/replace request
	 * @param doNotSendValue indicator of a null numeric FIX field
	 * @throws SystemException
	 * @throws CommunicationException
	 * @throws AuthorizationException
	 * @throws DataValidationException
	 * @throws NotFoundException
	 */
	public static void mapCancelReplaceToFix(CancelRequestStruct cancelRequest,
			OrderEntryStruct newOrder, OrderCancelReplace fixOrder,
			int doNotSendValue) throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException, NotFoundException {
		fixOrder.OrderID = FixUtilMapper.getFixOrderID(cancelRequest.orderId);
		fixOrder.OrigClOrdID = mapClOrdId(cancelRequest.orderId);

		mapOrderFields(cancelRequest, newOrder, fixOrder, doNotSendValue);
		mapSecurity(newOrder.productKey, fixOrder);
	}

	/**
	 * Map a CMi CancelRequestStruct to a FIX cancel request
	 * @param cancelRequest a CMi cancel request
	 * @param cancel a FIX cancel request
	 * @throws DataValidationException
	 */
	public static void mapCancelRequestToFix(CancelRequestStruct cancelRequest,
			OrderCancelRequest cancel) throws DataValidationException {
		cancel.ClientID = cancelRequest.orderId.correspondentFirm;
		cancel.OrderID = FixUtilMapper.getFixOrderID(cancelRequest.orderId);
		cancel.OrigClOrdID = mapClOrdId(cancelRequest.orderId);
		cancel.TransactTime = FixUtilDateTimeFormatter.currentLocalTime();
		cancel.ClOrdID = generateCxlClOrdId(cancelRequest);

		FixUtilExchangeFirmMapper firmMapper2 = new FixUtilExchangeFirmMapper(cancelRequest.orderId.executingOrGiveUpFirm);
		cancel.ExecBroker = firmMapper2.getFIXExchangeFirmString();

		// Symbol and side are required by FIX but not needed by FIXCAS
		cancel.Symbol = "XXX";
		cancel.Side = FixUtilConstants.Side.BUY;
	}

	/**
	 * Map a multi-legged OrderEntryStruct to a FIX Order
	 * @param orderEntry CMi order structure
	 * @param legEntryDetails an array of order legs
	 * @param fixOrder a FIX order
	 * @param doNotSendValue indicator of a null numeric FIX field
	 * @throws SystemException
	 * @throws CommunicationException
	 * @throws AuthorizationException
	 * @throws DataValidationException
	 */
	public static void mapOrderToFix(OrderEntryStruct orderEntry,
			LegOrderEntryStruct[] legEntryDetails, Order fixOrder,
			int doNotSendValue) throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException {

		mapOrderFields(orderEntry, fixOrder, doNotSendValue);
		mapSecurity(orderEntry.productKey, fixOrder);

		// Append user defined fields to existing value, if any, and update in place.
		fixOrder.UserDefined = mapOrderLegs(legEntryDetails, fixOrder.UserDefined);

	}

	/**
	 * Map an OrderEntryStruct to a FIX Order
	 * @param orderEntry CMi order structure
	 * @param fixOrder a FIX order
	 * @param doNotSendValue indicator of a null numeric FIX field
	 * @throws SystemException
	 * @throws AuthorizationException
	 * @throws DataValidationException
	 * @throws CommunicationException
	 */
	public static void mapOrderToFix(OrderEntryStruct orderEntry,
		Order fixOrder, int doNotSendValue)
		throws SystemException, CommunicationException, AuthorizationException, DataValidationException {
		mapOrderFields(orderEntry, fixOrder, doNotSendValue);
		mapSecurity(orderEntry.productKey, fixOrder);
	}

	/**
	 * Map an OrderEntryStruct and ProductNameStruct to a FIX Order
	 * @param orderEntry CMi order structure
	 * @param product product description
	 * @param fixOrder a FIX order
	 * @param doNotSendValue indicator of a null numeric FIX field
	 * @throws SystemException
	 * @throws DataValidationException
	 * @throws AuthorizationException
	 * @throws CommunicationException
	 */
	public static void mapOrderToFix(OrderEntryStruct orderEntry,
			ProductNameStruct product, Order fixOrder, int doNotSendValue)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException {
		// Ignore product because it doesn't provide security type, which is required
		mapOrderToFix(orderEntry, fixOrder, doNotSendValue);
	}

	/**
	 * Map a CMi partial cancel to a FIX cancel/replace
	 * @param cancelRequest CMi cancel request for partial cancel
	 * @param fixOrder FIX cancel/replace
	 * @param doNotSendValue indicator of a null numeric FIX field
	 * @throws SystemException
	 * @throws CommunicationException
	 * @throws AuthorizationException
	 * @throws DataValidationException
	 * @throws NotFoundException
	 */
	public static void mapPartialCancelToFix(CancelRequestStruct cancelRequest,
			OrderCancelReplace fixOrder, int doNotSendValue)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException, NotFoundException {
		fixOrder.OrderID = FixUtilMapper.getFixOrderID(cancelRequest.orderId);
		fixOrder.OrigClOrdID = mapClOrdId(cancelRequest.orderId);
		fixOrder.ClOrdID = generateCxlClOrdId(cancelRequest);

		OrderDetailStruct orderDetail = getOrder(cancelRequest.orderId);
		// Map other fields from original order
		mapOrderFields(orderDetail.orderStruct, fixOrder, doNotSendValue);
		mapSecurity(orderDetail.orderStruct.productKey, fixOrder);

		switch (cancelRequest.cancelType) {
			case OrderCancelTypes.DESIRED_CANCEL_QUANTITY :
				fixOrder.OrderQty =
					orderDetail.orderStruct.originalQuantity - cancelRequest.quantity;
				break;
			case OrderCancelTypes.DESIRED_REMAINING_QUANTITY :
				fixOrder.OrderQty = cancelRequest.quantity;
				break;
		}
	}




    /**
     *
     * @param fixOrderList
     * @param primaryOrderEntry
     * @param matchedOrderEntry
     * @param matchOrderType
     */
    public static void mapOrderListToFix(OrderEntryStruct primaryOrderEntry, OrderEntryStruct matchedOrderEntry,
                                            OrderList fixOrderList, short matchOrderType)
            throws SystemException, CommunicationException,	AuthorizationException, DataValidationException
    {
        // ListID is not used by FixCAS. Do we need to populate this?
        fixOrderList.ListID = "L_" + mapClOrdId(primaryOrderEntry);
        fixOrderList.header.SenderSubID = "L_" + mapClOrdId(primaryOrderEntry);
        // BidType is not used by FixCAS BidType = 2 [Disclosed]
        fixOrderList.BidType = 2;
        fixOrderList.ListNoOrds = 2;
        fixOrderList.NoOrders = 2;

        initOrderListFields(fixOrderList, 2);

        mapOrderListFields(fixOrderList, primaryOrderEntry, PRIMARY_ORDER);
        mapSecurity(primaryOrderEntry.productKey, fixOrderList, PRIMARY_ORDER);
        mapOrderListFields(fixOrderList, matchedOrderEntry, MATCHED_ORDER);
        mapSecurity(matchedOrderEntry.productKey, fixOrderList, MATCHED_ORDER);

        // map user defined tag 9382 = LimitPrice
        fixOrderList.UserDefined = mapUserDefinedFields(matchOrderType, fixOrderList.UserDefined);
    }

	/**
	 * Returns whether the cancel request is a quantity reduction as opposed to a
	 * request to cancel the balance of the open order.
	 * @param cancelRequest a CMi cancel request
	 * @return Returns <tt>true</tt> if it is a partial cancel
	 * @throws SystemException
	 * @throws CommunicationException
	 * @throws AuthorizationException
	 * @throws DataValidationException
	 * @throws NotFoundException
	 */
	public static boolean isPartialCancel(CancelRequestStruct cancelRequest)
	throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException {
		boolean isPartial = false;
		switch (cancelRequest.cancelType) {
		case OrderCancelTypes.CANCEL_ALL_QUANTITY :
			isPartial = false;
			break;
		case OrderCancelTypes.DESIRED_CANCEL_QUANTITY :
			OrderDetailStruct orderDetail = getOrder(cancelRequest.orderId);
			isPartial = (cancelRequest.quantity < orderDetail.orderStruct.leavesQuantity);
			break;
		case OrderCancelTypes.DESIRED_REMAINING_QUANTITY :
			isPartial = (cancelRequest.quantity > 0);
			break;
		}
		return isPartial;
	}

	/**
	 * Map a CMi RFQEntryStruct to a FIX QuoteRequest message
	 * @param rfq a CMi quote request
	 * @param quoteRequest a FIX RFQ
	 * @throws DataValidationException
	 * @throws AuthorizationException
	 * @throws CommunicationException
	 * @throws SystemException
	 */
	public static void mapRfqToFix(RFQEntryStruct rfq,
			QuoteRequest quoteRequest) throws SystemException, CommunicationException, AuthorizationException, DataValidationException {
		quoteRequest.NoRelatedSym = 1;
		mapSecurity(rfq.productKey, quoteRequest);
		quoteRequest.TradingSessionID = new String[quoteRequest.NoRelatedSym];
		quoteRequest.TradingSessionID[0] = rfq.sessionName;
		quoteRequest.OrderQty = new double[quoteRequest.NoRelatedSym];
		quoteRequest.OrderQty[0] = rfq.quantity;
	}

	/**
    * MAP CMi Coverage to FIX CoveredOrUncovered
    * @throws DataValidationException
    */
    private static int getFixCoveredOrUncovered(char cmiCoverage)
    	throws DataValidationException {
        switch(cmiCoverage){
            case CoverageTypes.COVERED:
                return FixUtilConstants.CoveredOrUncovered.COVERED;
            case CoverageTypes.UNCOVERED:
                return FixUtilConstants.CoveredOrUncovered.UNCOVERED;
            case CoverageTypes.UNSPECIFIED:
            	// According to CBOE spec, send a dummy value if not an option
                return FixUtilConstants.CoveredOrUncovered.COVERED;
            default:
                throw ExceptionBuilder.dataValidationException(
					"Invalid or unknown CoverageType: ", 0);

        }
    }

	/**
	 * Lookup order by order ID
	 * @param orderId ID of order to find
	 * @return order detail
	 * @throws SystemException
	 * @throws CommunicationException
	 * @throws AuthorizationException
	 * @throws DataValidationException
	 * @throws NotFoundException
	 */
	private static OrderDetailStruct getOrder(OrderIdStruct orderId)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException, NotFoundException {
		return APIHome.findOrderQueryAPI().getOrderById(orderId);
	}

	/**
	 * Lookup a product by product key
	 * @param productKey unique ID of product
	 * @return full product information
	 * @throws SystemException
	 * @throws CommunicationException
	 * @throws AuthorizationException
	 * @throws DataValidationException
	 * @throws NotFoundException
	 */
	private static Product getProduct(int productKey) throws SystemException,
			CommunicationException, AuthorizationException,
			DataValidationException, NotFoundException {
		return APIHome.findProductQueryAPI().getProductByKey(productKey);
	}

	/**
	 * Make a unique client order ID from component order fields
	 * @param orderEntry a CMi order
	 * @return string formatted for FIX ClOrdId
	 * @throws DataValidationException
	 */
	private static String mapClOrdId(OrderEntryStruct orderEntry) throws DataValidationException {
		FixUtilClOrdIDWrapper  clOrdId =
			new FixUtilClOrdIDWrapper (orderEntry.branch, orderEntry.branchSequenceNumber, orderEntry.orderDate);
		return clOrdId.getClOrdID();
	}

	/**
	 * Make a unique client order ID from component order fields
	 * @param orderId a CMi order
	 * @return string formatted for FIX ClOrdId
	 * @throws DataValidationException
	 */
	private static String mapClOrdId(OrderIdStruct orderId) throws DataValidationException {
		FixUtilClOrdIDWrapper clOrdId =
			new FixUtilClOrdIDWrapper(orderId.branch, orderId.branchSequenceNumber, orderId.orderDate);
		return clOrdId.getClOrdID();
	}

	/**
	 * Map CMi extensions fields to a FIX order
	 * @param extensions CMi extensions fields
	 * @param fixOrder FIX order to map to
	 * @throws DataValidationException
	 */
	private static void mapExtensionsFields(String extensions, Order fixOrder)
			throws DataValidationException {
		ExtensionsHelper helper;
		try {
			helper = new ExtensionsHelper(extensions);
			String cmiBartId = helper.getValue(ExtensionFields.BARTID);
			if (cmiBartId != null) {
				fixOrder.header.TargetLocationID = cmiBartId;
			}
			String cmiExchange = helper.getValue(ExtensionFields.EXCHANGE_DESTINATION);
			if (cmiExchange != null) {
				fixOrder.ExDestination =
					FixUtilMapper.getFixOlaExchangeFromCmi(cmiExchange);
			}
		} catch (ParseException e) {
			throw ExceptionBuilder.dataValidationException(
					"Invalid extensions field; " + e.getMessage(), 0);
		}
	}

	/**
	 * Map CMi extensions fields to a FIX cancel/replace
	 * @param extensions CMi extensions fields
	 * @param fixOrder FIX cancel/replace to map to
	 * @throws DataValidationException
	 */
	private static void mapExtensionsFields(String extensions,
			OrderCancelReplace fixOrder)
			throws DataValidationException {
		// Same as order implementation
		ExtensionsHelper helper;
		try {
			helper = new ExtensionsHelper(extensions);
			String cmiBartId = helper.getValue(ExtensionFields.BARTID);
			if (cmiBartId != null) {
				fixOrder.header.TargetLocationID = cmiBartId;
			}
			String cmiExchange = helper.getValue(ExtensionFields.EXCHANGE_DESTINATION);
			if (cmiExchange != null) {
				fixOrder.ExDestination =
					FixUtilMapper.getFixOlaExchangeFromCmi(cmiExchange);
			}
		} catch (ParseException e) {
			throw ExceptionBuilder.dataValidationException(
					"Invalid extensions field; " + e.getMessage(), 0);
		}
	}

    /**
	 * Map CMi extensions fields to a FIX order
	 * @param extensions CMi extensions fields
	 * @param fixOrderList FIX order list to map to
     * @param index - 0 or 1, the index of the order in the list
	 * @throws DataValidationException
	 */
	private static void mapExtensionsFields(String extensions, OrderList fixOrderList, int index)
			throws DataValidationException {
		ExtensionsHelper helper;
		try {
			helper = new ExtensionsHelper(extensions);
			String cmiBartId = helper.getValue(ExtensionFields.BARTID);
			if (cmiBartId != null) {
				fixOrderList.header.TargetLocationID = cmiBartId;
			}
			String cmiExchange = helper.getValue(ExtensionFields.EXCHANGE_DESTINATION);
			if (cmiExchange != null) {
				fixOrderList.ExDestination[index] =
					FixUtilMapper.getFixOlaExchangeFromCmi(cmiExchange);
			}
		} catch (ParseException e) {
			throw ExceptionBuilder.dataValidationException(
					"Invalid extensions field; " + e.getMessage(), 0);
		}
	}


	/**
	 * Map common order fields not covered by other methods
	 * @param orderEntry a CMi order
	 * @param fixOrder a FIX order
	 * @param doNotSendValue indicator of a null numeric FIX field
	 * @throws SystemException
	 * @throws DataValidationException
	 */
	private static void mapOrderFields(OrderEntryStruct orderEntry, Order fixOrder,
			int doNotSendValue) throws SystemException, DataValidationException {
		fixOrder.Account = orderEntry.account;
		fixOrder.ClearingAccount = orderEntry.subaccount;

		FixUtilExchangeFirmMapper firmMapper = new FixUtilExchangeFirmMapper(orderEntry.cmta);
		fixOrder.ClearingFirm = firmMapper.getFIXExchangeFirmString();

		fixOrder.ClientID = orderEntry.correspondentFirm;
		fixOrder.ClOrdID = mapClOrdId(orderEntry);
		fixOrder.CoveredOrUncovered = getFixCoveredOrUncovered(orderEntry.coverage);
		fixOrder.Rule80A = FixUtilMapper.getFixOrderCapacity(orderEntry.orderOriginType);

		FixUtilExchangeFirmMapper firmMapper2 = new FixUtilExchangeFirmMapper(orderEntry.executingOrGiveUpFirm);
		fixOrder.ExecBroker = firmMapper2.getFIXExchangeFirmString();

		fixOrder.ExecInst = FixUtilMapper.getFixExecInst(orderEntry.contingency.type);
		fixOrder.HandlInst = FixUtilConstants.HandlInst.AUTO_BROKER_ALLOWED;
		fixOrder.OpenClose = FixUtilMapper.getFixOpenClose(orderEntry.positionEffect);
		fixOrder.OrderQty = orderEntry.originalQuantity;
		fixOrder.Side = FixUtilMapper.getFixSide(orderEntry.side);
		fixOrder.NoTradingSessions = orderEntry.sessionNames.length;
		fixOrder.TradingSessionID = orderEntry.sessionNames;
		fixOrder.TransactTime = FixUtilDateTimeFormatter.currentLocalTime();

		mapOrderType(orderEntry.price, orderEntry.contingency, fixOrder);
		mapTimeInForce(orderEntry.timeInForce, orderEntry.contingency,
				orderEntry.expireTime, fixOrder);
		mapExtensionsFields(orderEntry.extensions, fixOrder);

		// Append user defined fields to existing value, if any, and update in place.
		fixOrder.UserDefined = mapUserDefinedFields(orderEntry, fixOrder.UserDefined);

		// Append user defined fields to existing value from userAssignedId
		fixOrder.UserDefined = mapUserAssignedIDFields(orderEntry.userAssignedId, fixOrder);
	}

	/**
	 * Map cancel/replace fields not covered by other methods
	 * @param cancelRequest an ID of the order to replace
	 * @param orderEntry a CMi order
	 * @param fixOrder a FIX cancel/replace
	 * @param doNotSendValue indicator of a null numeric FIX field
	 * @throws SystemException
	 * @throws DataValidationException
	 * @throws NotFoundException
	 * @throws AuthorizationException
	 * @throws CommunicationException
	 */
	private static void mapOrderFields(CancelRequestStruct cancelRequest,
			OrderEntryStruct orderEntry,
			OrderCancelReplace fixOrder, int doNotSendValue)
			throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException {
		// Same implementation as order mapping, but Appia class does not provide polymorphism
		fixOrder.Account = orderEntry.account;
		fixOrder.ClearingAccount = orderEntry.subaccount;

		FixUtilExchangeFirmMapper firmMapper = new FixUtilExchangeFirmMapper(orderEntry.cmta);
		fixOrder.ClearingFirm = firmMapper.getFIXExchangeFirmString();

		fixOrder.ClientID = orderEntry.correspondentFirm;
		fixOrder.ClOrdID = mapClOrdId(orderEntry);
		fixOrder.CoveredOrUncovered = getFixCoveredOrUncovered(orderEntry.coverage);
		fixOrder.Rule80A = FixUtilMapper.getFixOrderCapacity(orderEntry.orderOriginType);
		FixUtilExchangeFirmMapper firmMapper2 = new FixUtilExchangeFirmMapper(orderEntry.executingOrGiveUpFirm);
		fixOrder.ExecBroker = firmMapper2.getFIXExchangeFirmString();

		fixOrder.ExecInst = FixUtilMapper.getFixExecInst(orderEntry.contingency.type);
		fixOrder.HandlInst = FixUtilConstants.HandlInst.AUTO_BROKER_ALLOWED;
		fixOrder.OpenClose = FixUtilMapper.getFixOpenClose(orderEntry.positionEffect);
		fixOrder.Side = FixUtilMapper.getFixSide(orderEntry.side);
		fixOrder.NoTradingSessions = orderEntry.sessionNames.length;
		fixOrder.TradingSessionID = orderEntry.sessionNames;
		fixOrder.TransactTime = FixUtilDateTimeFormatter.currentLocalTime();

		mapOrderType(orderEntry.price, orderEntry.contingency, fixOrder);
		mapTimeInForce(orderEntry.timeInForce, orderEntry.contingency,
				orderEntry.expireTime, fixOrder);
		mapExtensionsFields(orderEntry.extensions, fixOrder);

		// Append user defined fields to existing value, if any, and update in place.
		fixOrder.UserDefined = mapUserDefinedFields(orderEntry, fixOrder.UserDefined);

		// Append user defined fields to existing value from userAssignedId
		fixOrder.UserDefined = mapUserAssignedIDFields(orderEntry.userAssignedId, fixOrder);

		// For FIXCAS, order quantity is expected fill quantity of original + replacement
		// order, but not for a whole order chain.
		OrderDetailStruct originalOrder = getOrder(cancelRequest.orderId);
		fixOrder.OrderQty = orderEntry.originalQuantity + originalOrder.orderStruct.tradedQuantity;
	}

	/**
	 * Populate a FIX cancel/replace from original order
	 * @param originalOrder order to be replaced
	 * @param fixOrder replacement order
	 * @param doNotSendValue indicator of a null numeric FIX field
	 * @throws DataValidationException
	 * @throws SystemException
	 */
	private static void mapOrderFields(OrderStruct originalOrder,
			OrderCancelReplace fixOrder, int doNotSendValue)
			throws DataValidationException, SystemException {
		fixOrder.Account = originalOrder.account;
		fixOrder.ClearingAccount = originalOrder.subaccount;

		FixUtilExchangeFirmMapper firmMapper = new FixUtilExchangeFirmMapper(originalOrder.cmta);
		fixOrder.ClearingFirm = firmMapper.getFIXExchangeFirmString();

		//fixOrder.ClientID = originalOrder.correspondentFirm;
		fixOrder.CoveredOrUncovered = getFixCoveredOrUncovered(originalOrder.coverage);
		fixOrder.Rule80A = FixUtilMapper.getFixOrderCapacity(originalOrder.orderOriginType);

		FixUtilExchangeFirmMapper firmMapper2 = new FixUtilExchangeFirmMapper(originalOrder.orderId.executingOrGiveUpFirm);
		fixOrder.ExecBroker = firmMapper2.getFIXExchangeFirmString();

		fixOrder.ExecInst = FixUtilMapper.getFixExecInst(originalOrder.contingency.type);
		fixOrder.HandlInst = FixUtilConstants.HandlInst.AUTO_BROKER_ALLOWED;
		fixOrder.OpenClose = FixUtilMapper.getFixOpenClose(originalOrder.positionEffect);
		fixOrder.Side = FixUtilMapper.getFixSide(originalOrder.side);
		fixOrder.NoTradingSessions = originalOrder.sessionNames.length;
		fixOrder.TradingSessionID = originalOrder.sessionNames;
		fixOrder.TransactTime = FixUtilDateTimeFormatter.currentLocalTime();

		mapOrderType(originalOrder.price, originalOrder.contingency, fixOrder);
		mapTimeInForce(originalOrder.timeInForce, originalOrder.contingency,
				originalOrder.expireTime, fixOrder);
		mapExtensionsFields(originalOrder.extensions, fixOrder);

		// Append user defined fields to existing value, if any, and update in place.
		// TODO
		// fixOrder.UserDefined = mapUserDefinedFields(originalOrder, fixOrder.UserDefined);

		// Append user defined fields to existing value from userAssignedId
		fixOrder.UserDefined = mapUserAssignedIDFields(originalOrder.userAssignedId, fixOrder);

	}

    /**
     *
     * @param fixOrderList
     * @param aL the Array Length = no. of orders in OrderList
     * This initializes all the arrays in an OrderList
     */
    public static void initOrderListFields(OrderList fixOrderList, int aL)
    {
        fixOrderList.Account = new String[aL];
        fixOrderList.ClearingAccount = new String[aL];
        fixOrderList.ClearingFirm = new String[aL];
        fixOrderList.ClientID = new String[aL];
        fixOrderList.ClOrdID = new String[aL];
        fixOrderList.CoveredOrUncovered = new int[aL];
        fixOrderList.Rule80A = new String[aL];
        fixOrderList.ExecBroker = new String[aL];
        fixOrderList.ExecInst = new String[aL];
		fixOrderList.HandlInst = new String[aL];
		fixOrderList.OpenClose = new String[aL];
		fixOrderList.OrderQty = new double[aL];
		fixOrderList.Side = new String[aL];
		fixOrderList.NoTradingSessions = new int[aL];
		fixOrderList.TradingSessionID = new String[aL][];
		fixOrderList.TransactTime = new String[aL];
        fixOrderList.OrdType = new String[aL];
        fixOrderList.TimeInForce = new String[aL];
        fixOrderList.Price = new double[aL];
        fixOrderList.StopPx = new double[aL];
        fixOrderList.ExpireDate = new String[aL];
        fixOrderList.ExDestination = new String[aL];
        fixOrderList.ExDestination = new String[aL];
        fixOrderList.SecurityType = new String[aL];
        fixOrderList.Symbol = new String[aL];
        fixOrderList.MaturityMonthYear = new String[aL];
        fixOrderList.PutOrCall = new int[aL];
        fixOrderList.StrikePrice = new double[aL];
        fixOrderList.SecurityDesc = new String[aL];
        fixOrderList.SecurityID = new String[aL];
        fixOrderList.IDSource = new String[aL];
        fixOrderList.ListSeqNo = new int[aL];
    }




    /**
     *
     * @param fixOrderList
     * @param orderEntry
     * @param index
     * @throws DataValidationException
     * @throws SystemException
     */
    private static void mapOrderListFields(OrderList fixOrderList, OrderEntryStruct orderEntry, int index)
        throws DataValidationException, SystemException
    {

        fixOrderList.ListSeqNo[index] = index + 1;
        fixOrderList.Account[index] = orderEntry.account;
		fixOrderList.ClearingAccount[index] = orderEntry.subaccount;

		FixUtilExchangeFirmMapper firmMapper = new FixUtilExchangeFirmMapper(orderEntry.cmta);
		fixOrderList.ClearingFirm[index] = firmMapper.getFIXExchangeFirmString();

		fixOrderList.ClientID[index] = orderEntry.correspondentFirm;
		fixOrderList.ClOrdID[index] = mapClOrdId(orderEntry);
		fixOrderList.CoveredOrUncovered[index] = getFixCoveredOrUncovered(orderEntry.coverage);
		fixOrderList.Rule80A[index] = FixUtilMapper.getFixOrderCapacity(orderEntry.orderOriginType);

		FixUtilExchangeFirmMapper firmMapper2 = new FixUtilExchangeFirmMapper(orderEntry.executingOrGiveUpFirm);
		fixOrderList.ExecBroker[index] = firmMapper2.getFIXExchangeFirmString();

		fixOrderList.ExecInst[index] = FixUtilMapper.getFixExecInst(orderEntry.contingency.type);
		fixOrderList.HandlInst[index] = FixUtilConstants.HandlInst.AUTO_BROKER_ALLOWED;
		fixOrderList.OpenClose[index] = FixUtilMapper.getFixOpenClose(orderEntry.positionEffect);
		fixOrderList.OrderQty[index] = orderEntry.originalQuantity;
		fixOrderList.Side[index] = FixUtilMapper.getFixSide(orderEntry.side);
		fixOrderList.NoTradingSessions[index] = orderEntry.sessionNames.length;
		fixOrderList.TradingSessionID[index] = orderEntry.sessionNames;
		fixOrderList.TransactTime[index] = FixUtilDateTimeFormatter.currentLocalTime();

		mapOrderType(orderEntry.price, orderEntry.contingency, fixOrderList, index);
		mapTimeInForce(orderEntry.timeInForce, orderEntry.contingency,
				                        orderEntry.expireTime, fixOrderList, index);
		mapExtensionsFields(orderEntry.extensions, fixOrderList, index);

		// Append user defined fields to existing value, if any, and update in place.
		fixOrderList.UserDefined = mapUserDefinedFields(orderEntry, fixOrderList.UserDefined);

        // Append user defined fields to existing value from userAssignedId
		fixOrderList.UserDefined = mapUserAssignedIDFields(orderEntry.userAssignedId, fixOrderList, index);
    }
	/**
	 * Appends definition of strategy legs to user defined fields
	 * @param legEntryDetails array of order legs
	 * @param initialValue predefined user defined fields
	 * @return updated user defined fields
	 * @throws SystemException
	 * @throws DataValidationException
	 */
	private static String mapOrderLegs(LegOrderEntryStruct[] legEntryDetails,
			String initialValue) throws SystemException, DataValidationException {
		int noOfLegs = legEntryDetails.length;
		FixUtilUserDefinedFieldTable udfTable = new FixUtilUserDefinedFieldTable();

		if (noOfLegs > 0) {
			FixUtilExchangeFirmMapper firmMapper = new FixUtilExchangeFirmMapper(legEntryDetails[0].clearingFirm);
			String clearingFirm = firmMapper.getFIXExchangeFirmString();
			if (clearingFirm != null) {
				udfTable.setValue( FixUtilUserDefinedTagConstants.MULTI_LEG_STOCK_CLEARING_FIRM,
						clearingFirm);
			}

			StringBuffer sbMultilegPositionEffects = new StringBuffer(noOfLegs);
			StringBuffer sbMultilegCovered = new StringBuffer(noOfLegs);
			for (int i = 0; i < noOfLegs; i++) {
				sbMultilegPositionEffects.append( FixUtilMapper.getFixOpenClose(
						legEntryDetails[i].positionEffect) );
				sbMultilegCovered.append(
						getFixCoveredOrUncovered(legEntryDetails[i].coverage) );
			}
			udfTable.setValue(FixUtilUserDefinedTagConstants.MULTI_LEG_POSITION_EFFECTS,
					sbMultilegPositionEffects.toString());
			udfTable.setValue(FixUtilUserDefinedTagConstants.MULTI_LEG_COVERED_OR_UNCOVERED,
					sbMultilegCovered.toString());
		}

		// Append leg fields to end of existing udf string
		StringBuffer sb = new StringBuffer(initialValue);
		if (initialValue.length() > 0) {
			sb.append(FIELD_DELIMITER);
		}
		sb.append(udfTable.getUDFString());

		return sb.toString();
	}
	/**
	 * Map order type and price from CMi to FIX
	 * @param price a CMi order price
	 * @param contingency  a CMi order contengency
	 * @param fixOrder a FIX order
	 */
	private static void mapOrderType(PriceStruct price,
			OrderContingencyStruct contingency, Order fixOrder) {
		fixOrder.OrdType = FixUtilMapper.getFixOrderType(price.type,
				contingency.type);
		if (price.type == PriceTypes.VALUED) {
			fixOrder.Price = FixUtilPriceHelper.priceStructToDouble(price);
		}
		if (contingency.type == ContingencyTypes.STP ||
			contingency.type == ContingencyTypes.STP_LIMIT ||
			contingency.type == ContingencyTypes.STP_LOSS) {
			fixOrder.StopPx =
				FixUtilPriceHelper.priceStructToDouble(contingency.price);
		}
	}

    /**
	 * Map order type and price from CMi to FIX
	 * @param price a CMi order price
	 * @param contingency  a CMi order contengency
	 * @param fixOrder a FIX cancel/replace message
	 */
	private static void mapOrderType(PriceStruct price,
			OrderContingencyStruct contingency,
			OrderCancelReplace fixOrder) {
		// Same implementation as order mapping, but Appia class does not provide polymorphism
		fixOrder.OrdType = FixUtilMapper.getFixOrderType(price.type,
				contingency.type);
		if (price.type == PriceTypes.VALUED) {
			fixOrder.Price = FixUtilPriceHelper.priceStructToDouble(price);
		}
		if (contingency.type == ContingencyTypes.STP ||
			contingency.type == ContingencyTypes.STP_LIMIT ||
			contingency.type == ContingencyTypes.STP_LOSS) {
			fixOrder.StopPx =
				FixUtilPriceHelper.priceStructToDouble(contingency.price);
		}
	}

    /**
	 * Map order type and price from CMi to FIX
	 * @param price a CMi order price
	 * @param contingency  a CMi order contengency
	 * @param fixOrderList a FIX order list
	 * @param price a CMi order price
     * @param contingency a CMi order contengency
	 */
	private static void mapOrderType(PriceStruct price,
			OrderContingencyStruct contingency, OrderList fixOrderList, int index) {
		fixOrderList.OrdType[index] = FixUtilMapper.getFixOrderType(price.type,
				contingency.type);
		if (price.type == PriceTypes.VALUED) {
			fixOrderList.Price[index] = FixUtilPriceHelper.priceStructToDouble(price);
		}
		if (contingency.type == ContingencyTypes.STP ||
			contingency.type == ContingencyTypes.STP_LIMIT ||
			contingency.type == ContingencyTypes.STP_LOSS) {
			fixOrderList.StopPx[index] =
				FixUtilPriceHelper.priceStructToDouble(contingency.price);
		}
	}

	/**
	 * Map security attributes to a FIX order
	 * @param productKey product ID
	 * @param fixOrder FIX order
	 * @throws SystemException
	 * @throws AuthorizationException
	 * @throws DataValidationException
	 * @throws CommunicationException
	 */
	private static void mapSecurity(int productKey, Order fixOrder)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException {
		try {
			Product product = getProduct(productKey);
			short productType = product.getProductType();
			fixOrder.SecurityType = FixUtilMapper.getFixSecurityType(productType);
			fixOrder.Symbol = product.getProductNameStruct().reportingClass;

			// Security attributes
			if (productType == ProductTypes.OPTION ||
				productType == ProductTypes.FUTURE ) {
				fixOrder.MaturityMonthYear = FixUtilMapper.getFixMaturityMonthYear(product.getProductNameStruct().expirationDate);
			}

			if (productType == ProductTypes.OPTION) {
				fixOrder.PutOrCall = FixUtilMapper.getFixPutOrCall(product.getProductNameStruct().optionType);
				fixOrder.StrikePrice = FixUtilMapper.getFixStrikePrice(product.getProductNameStruct().exercisePrice);
			}

			if (productType == ProductTypes.STRATEGY) {
//				IGUILogger logger = GUILoggerHome.find();
//				if (logger.isDebugOn()) {
//					logger.debug("CmiOrderToFixMapper.mapSecurity", GUILoggerBusinessProperty.ORDER_ENTRY, product.getDescription());
//				}
				short strategyType = ((Strategy)product).getStrategyType();
				fixOrder.SecurityDesc = FixUtilMapper.getFixSecurityDesc(strategyType);

				fixOrder.SecurityID = Integer.toString(productKey);
				fixOrder.IDSource = FixUtilConstants.IDSource.EXCHANGE;

			}
		} catch (NotFoundException e) {
			throw ExceptionBuilder.dataValidationException("Product not found",
					e.details.error);
		}
	}

    /**
     * Map security attributes to a FIX order
     * @param productKey product ID
     * @param fixOrderList FIX order
     * @param index the index of the Order in the OrderList
     * @throws SystemException
     * @throws AuthorizationException
     * @throws DataValidationException
     * @throws CommunicationException
     */
    private static void mapSecurity(int productKey, OrderList fixOrderList, int index)
                throws SystemException, CommunicationException,
                AuthorizationException, DataValidationException
    {
        try {
            Product product = getProduct(productKey);
            short productType = product.getProductType();
            fixOrderList.SecurityType[index] = FixUtilMapper.getFixSecurityType(productType);
            fixOrderList.Symbol[index] = product.getProductNameStruct().reportingClass;

            // Security attributes
            if (productType == ProductTypes.OPTION ||
                productType == ProductTypes.FUTURE ) {
                fixOrderList.MaturityMonthYear[index] =
                        FixUtilMapper.getFixMaturityMonthYear(product.getProductNameStruct().expirationDate);
            }

            if (productType == ProductTypes.OPTION) {
                fixOrderList.PutOrCall[index] =
                        FixUtilMapper.getFixPutOrCall(product.getProductNameStruct().optionType);
                fixOrderList.StrikePrice[index] =
                        FixUtilMapper.getFixStrikePrice(product.getProductNameStruct().exercisePrice);
            }

            if (productType == ProductTypes.STRATEGY) {
                //	IGUILogger logger = GUILoggerHome.find();
                //	if (logger.isDebugOn()) {
                //		logger.debug("CmiOrderToFixMapper.mapSecurity", GUILoggerBusinessProperty.ORDER_ENTRY, product.getDescription());
                //	}
                short strategyType = ((Strategy)product).getStrategyType();
                fixOrderList.SecurityDesc[index] = FixUtilMapper.getFixSecurityDesc(strategyType);

                fixOrderList.SecurityID[index] = Integer.toString(productKey);
                fixOrderList.IDSource[index] = FixUtilConstants.IDSource.EXCHANGE;

            }
        } catch (NotFoundException e) {
            throw ExceptionBuilder.dataValidationException("Product not found",
                    e.details.error);
        }
    }

	/**
	 * Map security attributes to a FIX cancel/replace
	 * @param productKey product ID
	 * @param fixOrder FIX cancel/replace
	 * @throws SystemException
	 * @throws AuthorizationException
	 * @throws DataValidationException
	 * @throws CommunicationException
	 */
	private static void mapSecurity(int productKey,
			OrderCancelReplace fixOrder)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException {
		// Same as order implementation
		try {
			Product product = getProduct(productKey);
			short productType = product.getProductType();
			fixOrder.SecurityType = FixUtilMapper.getFixSecurityType(productType);
			fixOrder.Symbol = product.getProductNameStruct().reportingClass;

			// Security attributes
			if (productType == ProductTypes.OPTION ||
				productType == ProductTypes.FUTURE) {
				fixOrder.MaturityMonthYear = FixUtilMapper.getFixMaturityMonthYear(product.getProductNameStruct().expirationDate);
			}
			if (productType == ProductTypes.OPTION) {
				fixOrder.PutOrCall = FixUtilMapper.getFixPutOrCall(product.getProductNameStruct().optionType);
				fixOrder.StrikePrice = FixUtilMapper.getFixStrikePrice(product.getProductNameStruct().exercisePrice);
			}
			if (productType == ProductTypes.STRATEGY) {
				fixOrder.SecurityDesc = FixUtilMapper.getFixSecurityDesc(productType);
				fixOrder.SecurityID = Integer.toString(productKey);
				fixOrder.IDSource = FixUtilConstants.IDSource.EXCHANGE;

			}
		} catch (NotFoundException e) {
			throw ExceptionBuilder.dataValidationException("Product not found",
					e.details.error);
		}
	}

	/**
	 * Map security attributes to a FIX quote request
	 * @param productKey product ID
	 * @param quoteRequest a FIX RFQ
	 * @throws SystemException
	 * @throws AuthorizationException
	 * @throws DataValidationException
	 * @throws CommunicationException
	 */
	private static void mapSecurity(int productKey, QuoteRequest quoteRequest)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException {
		try {
			Product product = getProduct(productKey);

			quoteRequest.Symbol = new String[1];
			quoteRequest.Symbol[0] = product.getProductNameStruct().reportingClass;

			short productType = product.getProductType();
			quoteRequest.SecurityType = new String[1];
			quoteRequest.SecurityType[0] = FixUtilMapper.getFixSecurityType(productType);

			// Security attributes
			if (productType == ProductTypes.OPTION ||
				productType == ProductTypes.FUTURE) {
				quoteRequest.MaturityMonthYear = new String[1];
				quoteRequest.MaturityMonthYear[0] = FixUtilMapper.getFixMaturityMonthYear(product.getProductNameStruct().expirationDate);
			}

			if (productType == ProductTypes.OPTION) {
				quoteRequest.PutOrCall = new int[1];
				quoteRequest.PutOrCall[0] = FixUtilMapper.getFixPutOrCall(product.getProductNameStruct().optionType);
				quoteRequest.StrikePrice = new double[1];
				quoteRequest.StrikePrice[0] = FixUtilMapper.getFixStrikePrice(product.getProductNameStruct().exercisePrice);
			}

			if (productType == ProductTypes.STRATEGY) {
				quoteRequest.SecurityDesc = new String[1];
				quoteRequest.SecurityDesc[0] = FixUtilMapper.getFixSecurityDesc(productType);
				quoteRequest.SecurityID = new String[1];
				quoteRequest.SecurityID[0] = Integer.toString(productKey);
				quoteRequest.IDSource = new String[1];
				quoteRequest.IDSource[0] = FixUtilConstants.IDSource.EXCHANGE;

			}
		} catch (NotFoundException e) {
			throw ExceptionBuilder.dataValidationException("Product not found",
					e.details.error);
		}
	}

	/**
	 * Map time in force from CMi to FIX
	 * @param timeInForce CMi TIF code
	 * @param contingency CMi order contingency
	 * @param expireTime for GTD orders
	 * @param fixOrder FIX order to populate
	 */
	private static void mapTimeInForce(char timeInForce,
			OrderContingencyStruct contingency, DateTimeStruct expireTime,
			Order fixOrder) {
		fixOrder.TimeInForce = FixUtilMapper.getFixTimeInForce(timeInForce,
				contingency.type);
		if (timeInForce == TimesInForce.GTD) {
			fixOrder.ExpireDate =
				FixUtilDateTimeHelper.dateTimeStructToString(expireTime);
		}
	}

	/**
	 * Map time in force from CMi to FIX
	 * @param timeInForce CMi TIF code
	 * @param contingency CMi order contingency
	 * @param expireTime for GTD orders
	 * @param fixOrder FIX cancel/replace message
	 */
	private static void mapTimeInForce(char timeInForce,
			OrderContingencyStruct contingency, DateTimeStruct expireTime,
			OrderCancelReplace fixOrder) {
		// Same implementation as order mapping, but Appia class does not provide polymorphism
		fixOrder.TimeInForce = FixUtilMapper.getFixTimeInForce(timeInForce,
				contingency.type);
		if (timeInForce == TimesInForce.GTD) {
			fixOrder.ExpireDate =
				FixUtilDateTimeHelper.dateTimeStructToString(expireTime);
		}
	}

    /**
	 * Map time in force from CMi to FIX
	 * @param timeInForce CMi TIF code
	 * @param contingency CMi order contingency
	 * @param expireTime for GTD orders
	 * @param fixOrderList FIX order list to populate
     * @param index - 0 or 1, the index of the fix order in the list
	 */
	private static void mapTimeInForce(char timeInForce,
			OrderContingencyStruct contingency, DateTimeStruct expireTime,
			OrderList fixOrderList, int index) {
		fixOrderList.TimeInForce[index] = FixUtilMapper.getFixTimeInForce(timeInForce,
				contingency.type);
		if (timeInForce == TimesInForce.GTD) {
			fixOrderList.ExpireDate[index] =
				FixUtilDateTimeHelper.dateTimeStructToString(expireTime);
		}
	}

	/**
	 * Map userAssignedId to FIX order, some parts to be appended to user defined fields
	 * @param userAssignedId CMi userAssignedId
	 * @param fixOrder FIX order to populate
	 * @return user defined fields with appended values
	 * @throws DataValidationException
	 */
	private static String mapUserAssignedIDFields(String userAssignedId, Order fixOrder)
		throws DataValidationException {
		FixUtilUserDefinedFieldTable udfTable = new FixUtilUserDefinedFieldTable();
		udfTable.setUDFString(fixOrder.UserDefined);

		ExtensionsHelper userAssignedTable = new ExtensionsHelper();
		userAssignedTable.setFieldDelimiter("|");

		try {
			userAssignedTable.setExtensions(userAssignedId);
		} catch (ParseException e) {
			throw ExceptionBuilder.dataValidationException(
					"Invalid userAssignedId field; " + e.getMessage(), 0);
		}

		Set keys = userAssignedTable.getKeys();
		Iterator keysIter = keys.iterator();
		while ( keysIter.hasNext() ) {
			String key = keysIter.next().toString();
			try {
				int tag = Integer.parseInt(key);
				String value = userAssignedTable.getValue(key);
				if (tag >= 500) {
					// If it's in user defined tag range, append to udf's.
					udfTable.setValue(key, value);
				} else {
					// Otherwise set regular FIX field
					switch (tag) {
					case FixUtilConstants.SenderSubID.TAGNUMBER :
						fixOrder.header.SenderSubID = value;
						break;
					case FixUtilConstants.SenderLocationID.TAGNUMBER :
						fixOrder.header.SenderLocationID = value;
						break;
					case FixUtilConstants.OnBehalfOfCompID.TAGNUMBER :
						fixOrder.header.OnBehalfOfCompID = value;
						break;
					case FixUtilConstants.OnBehalfOfSubID.TAGNUMBER :
						fixOrder.header.OnBehalfOfSubID = value;
						break;
					case FixUtilConstants.OnBehalfOfLocationID.TAGNUMBER :
						fixOrder.header.OnBehalfOfLocationID = value;
						break;
                    case FixUtilConstants.TargetLocationID.TAGNUMBER :
                            fixOrder.header.TargetLocationID= value;
                            break;
                    case FixUtilConstants.ExDestination.TAGNUMBER :
                                fixOrder.ExDestination= value;
                                break;

					default :
						GUILoggerHome.find().information(
								"Unexpected tag in userAssignedId ignored: "
										+ key,
								GUILoggerBusinessProperty.ORDER_ENTRY);
					}
				}
			} catch (NumberFormatException e) {
				throw ExceptionBuilder.dataValidationException(
						"Invalid userAssignedId field - non-numeric tag; " + e.getMessage(), 0);
			}
		}

		return udfTable.getUDFString();
	}

	/**
	 * Map userAssignedId to FIX order, some parts to be appended to user defined fields
	 * @param userAssignedId CMi userAssignedId
	 * @param fixOrder FIX order to populate
	 * @return user defined fields with appended values
	 * @throws DataValidationException
	 */
	private static String mapUserAssignedIDFields(String userAssignedId,
			OrderCancelReplace fixOrder) throws DataValidationException {
		// Same as order implementation
		FixUtilUserDefinedFieldTable udfTable = new FixUtilUserDefinedFieldTable();
		udfTable.setUDFString(fixOrder.UserDefined);

		ExtensionsHelper userAssignedTable = new ExtensionsHelper();
		userAssignedTable.setFieldDelimiter("|");

		try {
			userAssignedTable.setExtensions(userAssignedId);
		} catch (ParseException e) {
			throw ExceptionBuilder.dataValidationException(
					"Invalid userAssignedId field; " + e.getMessage(), 0);
		}

		Set keys = userAssignedTable.getKeys();
		Iterator keysIter = keys.iterator();
		while ( keysIter.hasNext() ) {
			String key = keysIter.next().toString();
			try {
				int tag = Integer.parseInt(key);
				String value = userAssignedTable.getValue(key);
				if (tag >= 5000) {
					// If it's in user defined tag range, append to udf's.
					udfTable.setValue(key, value);
				} else {
					// Otherwise set regular FIX field
					switch (tag) {
					case FixUtilConstants.SenderSubID.TAGNUMBER :
						fixOrder.header.SenderSubID = value;
						break;
					case FixUtilConstants.SenderLocationID.TAGNUMBER :
						fixOrder.header.SenderLocationID = value;
						break;
					case FixUtilConstants.OnBehalfOfCompID.TAGNUMBER :
						fixOrder.header.OnBehalfOfCompID = value;
						break;
					case FixUtilConstants.OnBehalfOfSubID.TAGNUMBER :
						fixOrder.header.OnBehalfOfSubID = value;
						break;
					case FixUtilConstants.OnBehalfOfLocationID.TAGNUMBER :
						fixOrder.header.OnBehalfOfLocationID = value;
						break;
                    case FixUtilConstants.ExDestination.TAGNUMBER:
                            fixOrder.ExDestination = value;
                            break;

					default :
						GUILoggerHome.find().information(
								"Unexpected tag in userAssignedId ignored: "
										+ key,
								GUILoggerBusinessProperty.ORDER_ENTRY);
					}
				}
			} catch (NumberFormatException e) {
				throw ExceptionBuilder.dataValidationException(
						"Invalid userAssignedId field - non-numeric tag; " + e.getMessage(), 0);
			}
		}

		return udfTable.getUDFString();
	}

    /**
	 * Map userAssignedId to FIX order, some parts to be appended to user defined fields
	 * @param userAssignedId CMi userAssignedId
	 * @param fixOrderList FIX order list to populate
     * @param index - 0 or 1, the index of the order in the list
	 * @return user defined fields with appended values
	 * @throws DataValidationException
	 */
	private static String mapUserAssignedIDFields(String userAssignedId, OrderList fixOrderList, int index)
		throws DataValidationException {
		FixUtilUserDefinedFieldTable udfTable = new FixUtilUserDefinedFieldTable();
		udfTable.setUDFString(fixOrderList.UserDefined);

		ExtensionsHelper userAssignedTable = new ExtensionsHelper();
		userAssignedTable.setFieldDelimiter("|");

		try {
			userAssignedTable.setExtensions(userAssignedId);
		} catch (ParseException e) {
			throw ExceptionBuilder.dataValidationException(
					"Invalid userAssignedId field; " + e.getMessage(), 0);
		}

		Set keys = userAssignedTable.getKeys();
		Iterator keysIter = keys.iterator();
		while ( keysIter.hasNext() ) {
			String key = keysIter.next().toString();
			try {
				int tag = Integer.parseInt(key);
				String value = userAssignedTable.getValue(key);
				if (tag >= 500) {
					// If it's in user defined tag range, append to udf's.
					udfTable.setValue(key, value);
				} else {
					// Otherwise set regular FIX field
					switch (tag) {
					case FixUtilConstants.SenderSubID.TAGNUMBER :
						fixOrderList.header.SenderSubID = value;
						break;
					case FixUtilConstants.SenderLocationID.TAGNUMBER :
						fixOrderList.header.SenderLocationID = value;
						break;
					case FixUtilConstants.OnBehalfOfCompID.TAGNUMBER :
						fixOrderList.header.OnBehalfOfCompID = value;
						break;
					case FixUtilConstants.OnBehalfOfSubID.TAGNUMBER :
						fixOrderList.header.OnBehalfOfSubID = value;
						break;
					case FixUtilConstants.OnBehalfOfLocationID.TAGNUMBER :
						fixOrderList.header.OnBehalfOfLocationID = value;
						break;
                    case FixUtilConstants.TargetLocationID.TAGNUMBER :
                        fixOrderList.header.TargetLocationID= value;
                        break;
                    // todo - this value should be the same across both primary and match order to make sense. VERIFY!
                    case FixUtilConstants.ExDestination.TAGNUMBER :
                        fixOrderList.ExDestination[index]= value;
                        break;

					default :
						GUILoggerHome.find().information(
								"Unexpected tag in userAssignedId ignored: "
										+ key,
								GUILoggerBusinessProperty.ORDER_ENTRY);
					}
				}
			} catch (NumberFormatException e) {
				throw ExceptionBuilder.dataValidationException(
						"Invalid userAssignedId field - non-numeric tag; " + e.getMessage(), 0);
			}
		}

		return udfTable.getUDFString();
	}

	/**
	 * Populate user defined FIX tags from a CMi order. Append values
	 * to initial value.
	 * @param matchOrderType - short
	 * @param initialValue previously set value of user defined fields
	 * @return user defined tags as a delimited String
	 */
	private static String mapUserDefinedFields(short matchOrderType, String initialValue)
	{
		FixUtilUserDefinedFieldTable udfTable = new FixUtilUserDefinedFieldTable();
		udfTable.setUDFString(initialValue);

		if (matchOrderType !=  0) {
			udfTable.setValue(FixUtilUserDefinedTagConstants.MATCH_TYPE, Short.toString(matchOrderType));
		}

		return udfTable.getUDFString();
	}

    /**
	 * Populate user defined FIX tags from a CMi order. Append values
	 * to initial value.
	 * @param orderEntry a CMi order
	 * @param initialValue previously set value of user defined fields
	 * @return user defined tags as a delimited String
	 * @throws SystemException
	 */
	private static String mapUserDefinedFields(OrderEntryStruct orderEntry,
			String initialValue)
			throws SystemException {
		FixUtilUserDefinedFieldTable udfTable = new FixUtilUserDefinedFieldTable();
		udfTable.setUDFString(initialValue);

		if (orderEntry.optionalData != null && orderEntry.optionalData.length() > 0) {
			udfTable.setValue(FixUtilUserDefinedTagConstants.CLEARING_OPTIONAL_DATA,
				orderEntry.optionalData);
		}

		udfTable.setValue(FixUtilUserDefinedTagConstants.PRICE_PROTECTION_SCOPE,
				FixUtilMapper.getFixPriceProtectionScope(orderEntry.orderNBBOProtectionType));

		if (orderEntry.originator.acronym != null
				&& orderEntry.originator.acronym.length() > 0) {
			FixUtilExchangeAcronymMapper mapper =
				new FixUtilExchangeAcronymMapper(orderEntry.originator);
			udfTable.setValue(FixUtilUserDefinedTagConstants.ORDER_ORIGINATOR,
					mapper.getFIXExchangeAcronymString() );
		}

		if (orderEntry.price.type == PriceTypes.CABINET) {
			udfTable.setValue(FixUtilUserDefinedTagConstants.EXTENDED_PRICE_TYPE,
					FixUtilConstants.ExtendedPriceType.CABINET);
		}

		return udfTable.getUDFString();
	}

	/**
	 * Generate a ClOrdId for a cancel request in branch-sequence number format
	 * @param cancelRequest a CMi cancel request
	 * @return a unique ID suitable for ClOrdId
	 * @throws DataValidationException
	 */
	private static String generateCxlClOrdId(CancelRequestStruct cancelRequest)
	throws DataValidationException {
		// GUI doesn't format userAssignedCancelId as branch-sequene number,
		// so form it from OrigClOrdID.
		OrderIdStruct cxlId = new OrderIdStruct();

		// Bump up first letter of branch
		char firstChar = cancelRequest.orderId.branch.charAt(0);
		firstChar++;
		StringBuffer sbBranch = new StringBuffer();
		sbBranch.append(firstChar);
		sbBranch.append(cancelRequest.orderId.branch.substring(1));
		cxlId.branch = sbBranch.toString();

		cxlId.branchSequenceNumber = cancelRequest.orderId.branchSequenceNumber;
		cxlId.orderDate = cancelRequest.orderId.orderDate;
		String clOrdId = mapClOrdId(cxlId);
		return clOrdId;
	}


}
