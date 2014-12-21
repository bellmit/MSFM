package com.cboe.presentation.fix.util;

import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiStrategy.StrategyLegStruct;
import com.cboe.idl.cmiUser.ExchangeAcronymStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.idl.cmiUtil.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.presentation.product.Strategy;
import com.cboe.domain.util.fixUtil.*;
import com.cboe.domain.util.ExtensionsHelper;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.presentation.api.APIHome;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.util.ExceptionBuilder;

import com.javtech.appia.ExecutionReport;
import com.javtech.appia.Order;
import com.javtech.appia.OrderCancelReplace;
import com.javtech.appia.OrderList;

import java.util.Date;
import java.util.StringTokenizer;


/**
 * Author: beniwalv
 * Date: Jul 21, 2004
 * Time: 2:15:15 PM
 *
 * This class maps a fix message to CMI structs.
 */


public class FixExecutionReportToCmiMapper {
	
    public static OrderDetailStruct mapToOrderDetailStruct(ExecutionReport er,
			int doNotSendValue) throws DataValidationException {
        OrderDetailStruct ordDetailStruct = new OrderDetailStruct();
        //populate OrderStruct
        ordDetailStruct.orderStruct=
                FixExecutionReportToCmiMapper.mapToOrderStruct(er, doNotSendValue);
        //populate ProductNameStruct
        ordDetailStruct.productInformation =
                FixExecutionReportToCmiMapper.mapToProductNameStruct(er);
        ordDetailStruct.statusChange = StatusUpdateReasons.QUERY;

        return  ordDetailStruct;
    }

    public static OrderDetailStruct mapToOrderDetailStruct(ExecutionReport er,
			Order order, int doNotSendValue) throws DataValidationException {
		OrderDetailStruct ordDetailStruct = new OrderDetailStruct();
		//populate OrderStruct
		ordDetailStruct.orderStruct = 
			FixExecutionReportToCmiMapper.mapToOrderStruct(er, order, doNotSendValue);
		//populate ProductNameStruct
		ordDetailStruct.productInformation = FixExecutionReportToCmiMapper
				.mapToProductNameStruct(er);
		ordDetailStruct.statusChange = StatusUpdateReasons.NEW;

		return ordDetailStruct;
	}
    
    public static OrderDetailStruct mapToOrderDetailStruct(ExecutionReport er,
			OrderCancelReplace order, int doNotSendValue) throws DataValidationException {
		OrderDetailStruct ordDetailStruct = new OrderDetailStruct();
		//populate OrderStruct
		ordDetailStruct.orderStruct = 
			FixExecutionReportToCmiMapper.mapToOrderStruct(er, order, doNotSendValue);
		//populate ProductNameStruct
		ordDetailStruct.productInformation = FixExecutionReportToCmiMapper
				.mapToProductNameStruct(er);
		ordDetailStruct.statusChange = StatusUpdateReasons.NEW;

		return ordDetailStruct;
	}

    public static OrderDetailStruct mapToOrderDetailStruct(ExecutionReport er,
			OrderList oList, int index, int doNotSendValue) throws DataValidationException {
        Order order = FixExecutionReportToCmiMapper.extractOrderFromOrderList(oList, index);
		OrderDetailStruct ordDetailStruct = new OrderDetailStruct();
		//populate OrderStruct
		ordDetailStruct.orderStruct =
			FixExecutionReportToCmiMapper.mapToOrderStruct(er, order, doNotSendValue);
		//populate ProductNameStruct
		ordDetailStruct.productInformation = FixExecutionReportToCmiMapper
				.mapToProductNameStruct(er);
		ordDetailStruct.statusChange = StatusUpdateReasons.NEW;

		return ordDetailStruct;
	}

    public static OrderFilledReportStruct mapToOrderFilledReportStruct(
			ExecutionReport er, int doNotSendValue)
			throws DataValidationException {
        OrderFilledReportStruct orderFilledReportStruct = new OrderFilledReportStruct();
        // populate OrderDetailStruct
        orderFilledReportStruct.filledOrder =
                FixExecutionReportToCmiMapper.mapToOrderDetailStruct(er, doNotSendValue);
        // populate FilledReportStruct[]
        orderFilledReportStruct.filledReport = new FilledReportStruct[1];
        orderFilledReportStruct.filledReport[0] =
                FixExecutionReportToCmiMapper.mapToFilledReportStruct(er);

        orderFilledReportStruct.filledOrder.statusChange = StatusUpdateReasons.FILL;
        
        return orderFilledReportStruct;
    }

    public static OrderCancelReportStruct mapToOrderCancelReportStruct(
			ExecutionReport er, int doNotSendValue)
			throws DataValidationException {
        OrderCancelReportStruct orderCancelReportStruct = new OrderCancelReportStruct();
        // populate OrderDetailStruct
        orderCancelReportStruct.cancelledOrder =
                FixExecutionReportToCmiMapper.mapToOrderDetailStruct(er, doNotSendValue);
        // populate CancelReportStruct[]
        orderCancelReportStruct.cancelReport = new CancelReportStruct[1];
        orderCancelReportStruct.cancelReport[0] =
                FixExecutionReportToCmiMapper.mapToCancelReportStruct(er);

        orderCancelReportStruct.cancelledOrder.statusChange = 
        	StatusUpdateReasons.CANCEL;
        
        return orderCancelReportStruct;
    }

    public static OrderBustReportStruct mapToOrderBustReportStruct(
			ExecutionReport er, int doNotSendValue)
			throws DataValidationException {
        OrderBustReportStruct orderBustReportStruct = new OrderBustReportStruct();
        // populate OrderDetailStruct
        orderBustReportStruct.bustedOrder =
                FixExecutionReportToCmiMapper.mapToOrderDetailStruct(er, doNotSendValue);
        // populate BustReportStruct[]
        orderBustReportStruct.bustedReport = new BustReportStruct[1];
        orderBustReportStruct.bustedReport[0] =
                FixExecutionReportToCmiMapper.mapToBustReportStruct(er);

 		if (FixUtilConstants.PossResend.POSSIBLE_RESEND.equals(er.header.PossResend)) {
			orderBustReportStruct.bustedOrder.statusChange = StatusUpdateReasons.POSSIBLE_RESEND;
		} else {
			orderBustReportStruct.bustedOrder.statusChange = StatusUpdateReasons.BUST;
		}
  
        return orderBustReportStruct;
    }

    public static OrderBustReinstateReportStruct mapToOrderBustReinstateReportStruct(
			ExecutionReport er, int doNotSendValue)
			throws DataValidationException {
        OrderBustReinstateReportStruct orderBustReinstateReportStruct = new OrderBustReinstateReportStruct();

        // populate OrderDetailStruct
        orderBustReinstateReportStruct.reinstatedOrder =
                FixExecutionReportToCmiMapper.mapToOrderDetailStruct(er, doNotSendValue);
        // populate BustReinstateReportStruct
        orderBustReinstateReportStruct.bustReinstatedReport =
                FixExecutionReportToCmiMapper.mapToBustReinstateReportStruct(er);

        orderBustReinstateReportStruct.reinstatedOrder.statusChange = 
        	StatusUpdateReasons.REINSTATE;
        
        return orderBustReinstateReportStruct;
    }



    public static OrderStruct mapToOrderStruct(ExecutionReport er,
			int doNotSendValue) throws DataValidationException {
        OrderStruct orderStruct = new OrderStruct();
        FixUtilUserDefinedFieldTable udfTable = new FixUtilUserDefinedFieldTable(er.UserDefined);

        // populate OrderIdStruct - orderId
        orderStruct.orderId = FixExecutionReportToCmiMapper.mapToOrderIdStruct(er);

        // populate ExchangeAcronymStruct - originator
        orderStruct.originator =
                FixExecutionReportToCmiMapper.mapToOriginatorStruct(er);

        orderStruct.originalQuantity = (int)er.OrderQty;

        // tag 48 populated unless its a reject
        orderStruct.productKey = Integer.parseInt(nonull(er.SecurityID));

        orderStruct.side = FixUtilMapper.getCmiSide(er.Side, er.SecurityType);

        orderStruct.price = getCmiPrice(er.Price, doNotSendValue);

        orderStruct.timeInForce = getCmiTimeInForce(er.TimeInForce);

        // populate DateTimeStruct expireTime
        orderStruct.expireTime =
                FixExecutionReportToCmiMapper.mapToExpireDateTimeStruct(er);

        // populate OrderContingencyStruct contingency
        orderStruct.contingency =
                FixExecutionReportToCmiMapper.mapToOrderContingencyStruct(er);

        // populate ExchangeFirmStruct cmta
        orderStruct.cmta =
                FixExecutionReportToCmiMapper.mapToCrossedOrderExchangeFirmStruct(er);


        orderStruct.extensions = FixExecutionReportToCmiMapper.getExtensions(er);

        orderStruct.account = er.Account;
        orderStruct.subaccount = er.ClearingAccount;
        orderStruct.positionEffect = FixUtilMapper.getCmiPositionEffect(er.OpenClose);

        // check if an execution report ever has info about this?
        orderStruct.cross = false;

        orderStruct.orderOriginType = mapToOrderOriginType(er);

        // Do we care for this? Can get it from the original order; not a part of Execution Report
        // Tag 203 - CoveredOrUncovered
        orderStruct.coverage = CoverageTypes.UNSPECIFIED;

        // map Tag 9369 from UserDefinedTable
        orderStruct.orderNBBOProtectionType =
                Short.parseShort(udfTable.getValue(FixUtilUserDefinedTagConstants.PRICE_PROTECTION_SCOPE));
        // map Clearing Optional Data - tag 9324
        orderStruct.optionalData = udfTable.getValue(FixUtilUserDefinedTagConstants.CLEARING_OPTIONAL_DATA);

        // This is the user ID. Need to get it from the session variable.
        orderStruct.userId = "";

        // populate ExchangeAcronymStruct userAcronym
        orderStruct.userAcronym =
                FixExecutionReportToCmiMapper.mapToUserAcronymStruct(er);

        Product product = getProduct(orderStruct.productKey);
       	orderStruct.productType = product.getProductType();
        orderStruct.classKey = product.getProductKeysStruct().classKey;

        // populate DateTimeStruct receivedTime
        orderStruct.receivedTime =
                FixExecutionReportToCmiMapper.mapToReceivedDateTimeStruct(er);

        // Does Execution Report carry this info? Is this needed.
        orderStruct.state = -1;

        orderStruct.tradedQuantity = (int)er.CumQty;
        orderStruct.cancelledQuantity = (int)er.CxlQty;
        orderStruct.leavesQuantity = (int)er.LeavesQty;

        // populate PriceStruct averagePrice
        orderStruct.averagePrice = FixUtilPriceHelper.makeValuedPrice(er.AvgPx);

        orderStruct.sessionTradedQuantity = (int)er.DayCumQty;

        // sessionCancelledQuantity information not carried in ExecutionReport.
        orderStruct.sessionCancelledQuantity = -1;

        // populate PriceStruct sessionAveragePrice
        orderStruct.sessionAveragePrice = FixUtilPriceHelper.makeValuedPrice(er.AvgPx);


        orderStruct.orsId = er.SecondaryOrderID;

        // Check if this can be done - or even needs to be done.
        orderStruct.source = Sources.SBT;

        // populate OrderIdStruct crossedOrder
        orderStruct.crossedOrder =
                FixExecutionReportToCmiMapper.mapToCrossedOrderStruct(er);

        orderStruct.transactionSequenceNumber = FixExecutionReportToCmiMapper.getTransactionSequenceNo(er);

        orderStruct.userAssignedId = nonull(er.UserDefined);

        // do we need to populate this sessionNames field?
        orderStruct.sessionNames = new String[1];
        orderStruct.sessionNames[0] = er.TradingSessionID;

        orderStruct.activeSession = er.TradingSessionID;

        // populate LegOrderDetailStruct[] legOrderDetails
        if (product.getProductType() == ProductTypes.STRATEGY) {
        	StrategyLegStruct [] productLegs = ((Strategy)product).getStrategyLegStructs();
        	orderStruct.legOrderDetails = new LegOrderDetailStruct[productLegs.length];
        	for (int i=0; i < productLegs.length; i++) {
        		orderStruct.legOrderDetails[i] = mapToLegOrderDetailStruct(er, productLegs[i]);
        	}
        } else {
        	orderStruct.legOrderDetails = new LegOrderDetailStruct[1];
        	orderStruct.legOrderDetails[0] = mapToLegOrderDetailStruct(er);
        }

        return orderStruct;
    }

	public static OrderStruct mapToOrderStruct(ExecutionReport er, Order order,
			int doNotSendValue) throws DataValidationException {
        OrderStruct orderStruct = new OrderStruct();
        FixUtilUserDefinedFieldTable udfTable = new FixUtilUserDefinedFieldTable(er.UserDefined);

        // populate OrderIdStruct - orderId
        orderStruct.orderId = FixExecutionReportToCmiMapper.mapToOrderIdStruct(er);

        // populate ExchangeAcronymStruct - originator
        orderStruct.originator =
                FixExecutionReportToCmiMapper.mapToOriginatorStruct(er);

        orderStruct.originalQuantity = (int)er.OrderQty;

        // tag 48 populated unless its a reject. Error checking
        orderStruct.productKey = Integer.parseInt(er.SecurityID);

        orderStruct.side = FixUtilMapper.getCmiSide(er.Side, er.SecurityType);

        orderStruct.price = getCmiPrice(er.Price, doNotSendValue);

        orderStruct.timeInForce = getCmiTimeInForce(er.TimeInForce);

        // populate DateTimeStruct expireTime
        orderStruct.expireTime =
                FixExecutionReportToCmiMapper.mapToExpireDateTimeStruct(er);

        // populate OrderContingencyStruct contingency
        orderStruct.contingency =
                FixExecutionReportToCmiMapper.mapToOrderContingencyStruct(er);

        // populate ExchangeFirmStruct cmta
        orderStruct.cmta =
                FixExecutionReportToCmiMapper.mapToCrossedOrderExchangeFirmStruct(er);


        orderStruct.extensions = FixExecutionReportToCmiMapper.getExtensions(er);

        orderStruct.account = er.Account;
        orderStruct.subaccount = er.ClearingAccount;
        orderStruct.positionEffect = FixUtilMapper.getCmiPositionEffect(er.OpenClose);

        // check if an execution report ever has info about this?
        orderStruct.cross = false;

        orderStruct.orderOriginType = FixExecutionReportToCmiMapper.mapToOrderOriginType(er);

        // Get it from the original order; not a part of Execution Report
        // Tag 203 - CoveredOrUncovered
        orderStruct.coverage = getCmiCoverageType(order.CoveredOrUncovered);

        // map Tag 9369 from UserDefinedTable
        orderStruct.orderNBBOProtectionType =
                Short.parseShort(udfTable.getValue(FixUtilUserDefinedTagConstants.PRICE_PROTECTION_SCOPE));
        // map Clearing Optional Data - tag 9324
        orderStruct.optionalData = udfTable.getValue(FixUtilUserDefinedTagConstants.CLEARING_OPTIONAL_DATA);

        // This is the user ID. Need to get it from the session variable.
        orderStruct.userId = "";

        // populate ExchangeAcronymStruct userAcronym
        orderStruct.userAcronym =
                FixExecutionReportToCmiMapper.mapToUserAcronymStruct(er);

        Product product = getProduct(orderStruct.productKey);
       	orderStruct.productType = product.getProductType();
        orderStruct.classKey = product.getProductKeysStruct().classKey;

        // populate DateTimeStruct receivedTime
        orderStruct.receivedTime =
                FixExecutionReportToCmiMapper.mapToReceivedDateTimeStruct(er);

        // Does Execution Report carry this info? Is this needed.
        orderStruct.state = -1;

        orderStruct.tradedQuantity = (int)er.CumQty;
        orderStruct.cancelledQuantity = (int)er.CxlQty;
        orderStruct.leavesQuantity = (int)er.LeavesQty;

        // populate PriceStruct averagePrice
        orderStruct.averagePrice = FixUtilPriceHelper.makeValuedPrice(er.AvgPx);

        orderStruct.sessionTradedQuantity = (int)er.DayCumQty;

        // sessionCancelledQuantity information not carried in ExecutionReport.
        orderStruct.sessionCancelledQuantity = -1;

        // populate PriceStruct sessionAveragePrice
        orderStruct.sessionAveragePrice = FixUtilPriceHelper.makeValuedPrice(er.AvgPx);


        orderStruct.orsId = er.SecondaryOrderID;

        // Check if this can be done - or even needs to be done.
        orderStruct.source = Sources.SBT;

        // populate OrderIdStruct crossedOrder
        orderStruct.crossedOrder =
                FixExecutionReportToCmiMapper.mapToCrossedOrderStruct(er);

        orderStruct.transactionSequenceNumber = FixExecutionReportToCmiMapper.getTransactionSequenceNo(er);

        orderStruct.userAssignedId = nonull(er.UserDefined);

        orderStruct.sessionNames = new String[1];
        orderStruct.sessionNames[0] = er.TradingSessionID;

        orderStruct.activeSession = er.TradingSessionID;

        // populate LegOrderDetailStruct[] legOrderDetails
        if (product.getProductType() == ProductTypes.STRATEGY) {
        	StrategyLegStruct [] productLegs = ((Strategy)product).getStrategyLegStructs();
        	orderStruct.legOrderDetails = new LegOrderDetailStruct[productLegs.length];
        	for (int i=0; i < productLegs.length; i++) {
        		orderStruct.legOrderDetails[i] = mapToLegOrderDetailStruct(er, productLegs[i]);
        	}
        } else {
        	orderStruct.legOrderDetails = new LegOrderDetailStruct[1];
        	orderStruct.legOrderDetails[0] = mapToLegOrderDetailStruct(er);
        }

        return orderStruct;
    }

    public static OrderStruct mapToOrderStruct(ExecutionReport er,
			OrderCancelReplace order, int doNotSendValue)
			throws DataValidationException {
        OrderStruct orderStruct = new OrderStruct();
        FixUtilUserDefinedFieldTable udfTable = new FixUtilUserDefinedFieldTable(er.UserDefined);

        // populate OrderIdStruct - orderId
        orderStruct.orderId = FixExecutionReportToCmiMapper.mapToOrderIdStruct(er);

        // populate ExchangeAcronymStruct - originator
        orderStruct.originator =
                FixExecutionReportToCmiMapper.mapToOriginatorStruct(er);

        orderStruct.originalQuantity = (int)er.OrderQty;

        orderStruct.productKey = Integer.parseInt(nonull(er.SecurityID));

        orderStruct.side = FixUtilMapper.getCmiSide(er.Side, er.SecurityType);

        orderStruct.price = getCmiPrice(er.Price, doNotSendValue);

        orderStruct.timeInForce = getCmiTimeInForce(er.TimeInForce);

        // populate DateTimeStruct expireTime
        orderStruct.expireTime =
                FixExecutionReportToCmiMapper.mapToExpireDateTimeStruct(er);

        // populate OrderContingencyStruct contingency
        orderStruct.contingency =
                FixExecutionReportToCmiMapper.mapToOrderContingencyStruct(er);

        // populate ExchangeFirmStruct cmta
        orderStruct.cmta =
                FixExecutionReportToCmiMapper.mapToCrossedOrderExchangeFirmStruct(er);


        orderStruct.extensions = FixExecutionReportToCmiMapper.getExtensions(er);

        orderStruct.account = er.Account;
        orderStruct.subaccount = er.ClearingAccount;
        orderStruct.positionEffect = FixUtilMapper.getCmiPositionEffect(er.OpenClose);

        // check if an execution report ever has info about this?
        orderStruct.cross = false;

        orderStruct.orderOriginType = FixExecutionReportToCmiMapper.mapToOrderOriginType(er);

        // Get it from the original order; not a part of Execution Report
        // Tag 203 - CoveredOrUncovered
        orderStruct.coverage = getCmiCoverageType(order.CoveredOrUncovered);

        // map Tag 9369 from UserDefinedTable
        orderStruct.orderNBBOProtectionType =
                Short.parseShort(udfTable.getValue(FixUtilUserDefinedTagConstants.PRICE_PROTECTION_SCOPE));
        // map Clearing Optional Data - tag 9324
        orderStruct.optionalData = udfTable.getValue(FixUtilUserDefinedTagConstants.CLEARING_OPTIONAL_DATA);

        // This is the user ID. Need to get it from the session variable.
        orderStruct.userId = "";

        // populate ExchangeAcronymStruct userAcronym
        orderStruct.userAcronym =
                FixExecutionReportToCmiMapper.mapToUserAcronymStruct(er);

        Product product = getProduct(orderStruct.productKey);
       	orderStruct.productType = product.getProductType();
        orderStruct.classKey = product.getProductKeysStruct().classKey;

        // populate DateTimeStruct receivedTime
        orderStruct.receivedTime =
                FixExecutionReportToCmiMapper.mapToReceivedDateTimeStruct(er);

        // Does Execution Report carry this info? Is this needed.
        orderStruct.state = -1;

        orderStruct.tradedQuantity = (int)er.CumQty;
        orderStruct.cancelledQuantity = (int)er.CxlQty;
        orderStruct.leavesQuantity = (int)er.LeavesQty;

        // populate PriceStruct averagePrice
        orderStruct.averagePrice = FixUtilPriceHelper.makeValuedPrice(er.AvgPx);

        orderStruct.sessionTradedQuantity = (int)er.DayCumQty;

        // sessionCancelledQuantity information not carried in ExecutionReport.
        orderStruct.sessionCancelledQuantity = -1;

        // populate PriceStruct sessionAveragePrice
        orderStruct.sessionAveragePrice = FixUtilPriceHelper.makeValuedPrice(er.AvgPx);


        orderStruct.orsId = er.SecondaryOrderID;

        // Check if this can be done - or even needs to be done.
        orderStruct.source = Sources.SBT;

        // populate OrderIdStruct crossedOrder
        orderStruct.crossedOrder =
                FixExecutionReportToCmiMapper.mapToCrossedOrderStruct(er);

        orderStruct.transactionSequenceNumber = FixExecutionReportToCmiMapper.getTransactionSequenceNo(er);

        orderStruct.userAssignedId = nonull(er.UserDefined);

        // do we need to populate this sessionNames field?
        orderStruct.sessionNames = new String[1];
        orderStruct.sessionNames[0] = er.TradingSessionID;

        orderStruct.activeSession = er.TradingSessionID;

        // populate LegOrderDetailStruct[] legOrderDetails
        if (product.getProductType() == ProductTypes.STRATEGY) {
        	StrategyLegStruct [] productLegs = ((Strategy)product).getStrategyLegStructs();
        	orderStruct.legOrderDetails = new LegOrderDetailStruct[productLegs.length];
        	for (int i=0; i < productLegs.length; i++) {
        		orderStruct.legOrderDetails[i] = mapToLegOrderDetailStruct(er, productLegs[i]);
        	}
        } else {
        	orderStruct.legOrderDetails = new LegOrderDetailStruct[1];
        	orderStruct.legOrderDetails[0] = mapToLegOrderDetailStruct(er);
        }

        return orderStruct;
    }

    public static ProductNameStruct mapToProductNameStruct(ExecutionReport er)
    {
		// Not in cache - try to map it from the FIX message
        ProductNameStruct productNameStruct = new ProductNameStruct();
        productNameStruct.reportingClass = er.Symbol;
        
        productNameStruct.expirationDate =
                FixExecutionReportToCmiMapper.mapToExpireDateStruct(er);

        if (er.SecurityType.equals(FixUtilConstants.SecurityType.OPTION)) {
        	// Map option fields
        	switch (er.PutOrCall) {
        	case FixUtilConstants.PutOrCall.PUT :
        		productNameStruct.optionType = OptionTypes.PUT;
        	productNameStruct.exercisePrice = FixUtilPriceHelper.makeValuedPrice(er.StrikePrice);
        	break;
        	case FixUtilConstants.PutOrCall.CALL :
        		productNameStruct.optionType = OptionTypes.CALL;
        	productNameStruct.exercisePrice = FixUtilPriceHelper.makeValuedPrice(er.StrikePrice);
        	}
        } else // it's not an option, so no optionType
        {
        	productNameStruct.optionType = ' ';
        	productNameStruct.exercisePrice = FixUtilPriceHelper.makeNoPrice();
         }

        return productNameStruct;
    }

    public static FilledReportStruct mapToFilledReportStruct(ExecutionReport er) throws DataValidationException
    {
        FilledReportStruct filledReportStruct = new FilledReportStruct();
        FixUtilUserDefinedFieldTable udfTable = new FixUtilUserDefinedFieldTable(er.UserDefined);

        filledReportStruct.tradeId =
                FixExecutionReportToCmiMapper.mapToCboeTradeIdStruct(er.ExecID);

        filledReportStruct.fillReportType = (short) (Integer.parseInt(er.MultiLegReportingType));

        FixUtilExchangeFirmMapper execOrGiveUpFirmMapper = new FixUtilExchangeFirmMapper(er.ExecBroker);
        filledReportStruct.executingOrGiveUpFirm = execOrGiveUpFirmMapper.getCMIExchangeFirmStruct();

        // This is the user ID. Need to get it from the session variable.
        filledReportStruct.userId = "";

        filledReportStruct.userAcronym =
                FixExecutionReportToCmiMapper.mapToUserAcronymStruct(er);

        filledReportStruct.productKey = Integer.parseInt(er.SecurityID);

        filledReportStruct.sessionName = er.TradingSessionID;
        filledReportStruct.tradedQuantity = (int)er.CumQty;
        filledReportStruct.leavesQuantity = (int)er.LeavesQty;

        filledReportStruct.price = FixUtilPriceHelper.makeValuedPrice(er.LastPx);

        filledReportStruct.side = FixUtilMapper.getCmiSide(er.Side, er.SecurityType);

        filledReportStruct.orsId = er.SecondaryOrderID;

        filledReportStruct.executingBroker =
                udfTable.getValue(FixUtilUserDefinedTagConstants.EXECUTION_INFORMATION);

        filledReportStruct.cmta =
                FixExecutionReportToCmiMapper.mapToCrossedOrderExchangeFirmStruct(er);

        filledReportStruct.account = er.Account;
        filledReportStruct.subaccount = er.ClearingAccount;

        filledReportStruct.originator =
                FixExecutionReportToCmiMapper.mapToOriginatorStruct(er);

        filledReportStruct.optionalData = udfTable.getValue(FixUtilUserDefinedTagConstants.CLEARING_OPTIONAL_DATA);
        filledReportStruct.userAssignedId = er.UserDefined;

        // populate using ExtensionFields and ExtensionHelper
        filledReportStruct.extensions = "";

        filledReportStruct.contraParties =
                FixExecutionReportToCmiMapper.mapToContraPartyStruct(er);

        filledReportStruct.timeSent =
                FixExecutionReportToCmiMapper.mapToReceivedDateTimeStruct(er);

        filledReportStruct.positionEffect = FixUtilMapper.getCmiPositionEffect(er.OpenClose);

        filledReportStruct.transactionSequenceNumber = FixExecutionReportToCmiMapper.getTransactionSequenceNo(er);

        return filledReportStruct;
    }

    public static CancelReportStruct mapToCancelReportStruct(ExecutionReport er)
            throws DataValidationException
    {
        CancelReportStruct cancelReportStruct = new CancelReportStruct();
        FixUtilClOrdIDWrapper clOrdIdWrapper = new FixUtilClOrdIDWrapper();
        cancelReportStruct.orderId =
                FixExecutionReportToCmiMapper.mapToOrderIdStruct(er);

        cancelReportStruct.cancelReportType = FixExecutionReportToCmiMapper.getMultiLegReportingType(er);

        cancelReportStruct.cancelReason = FixExecutionReportToCmiMapper.getCancelReason(er);

        cancelReportStruct.productKey = Integer.parseInt(er.SecurityID);

        cancelReportStruct.sessionName = er.TradingSessionID;

        cancelReportStruct.cancelledQuantity = (int)er.CxlQty;

        // Verify that this is the way to go.
        cancelReportStruct.tlcQuantity = 0;
        cancelReportStruct.mismatchedQuantity = 0;

        cancelReportStruct.timeSent =
                FixExecutionReportToCmiMapper.mapToReceivedDateTimeStruct(er);

        cancelReportStruct.orsId = er.SecondaryOrderID;

        cancelReportStruct.totalCancelledQuantity = (int)er.CxlQty;

        cancelReportStruct.transactionSequenceNumber =
                        FixExecutionReportToCmiMapper.getTransactionSequenceNo(er);


        clOrdIdWrapper.setClOrdID(er.ClOrdID);
        cancelReportStruct.userAssignedCancelId = clOrdIdWrapper.getClOrdID();

        return cancelReportStruct;
    }

    public static BustReportStruct mapToBustReportStruct(ExecutionReport er) throws DataValidationException
    {
        BustReportStruct bustReportStruct = new BustReportStruct();

        FixUtilUserDefinedFieldTable udfTable = new FixUtilUserDefinedFieldTable(er.UserDefined);

        bustReportStruct.tradeId =
                FixExecutionReportToCmiMapper.mapToCboeTradeIdStruct(er.ExecRefID);

        bustReportStruct.bustReportType = FixExecutionReportToCmiMapper.getMultiLegReportingType(er);

        bustReportStruct.sessionName = er.TradingSessionID;

        FixUtilExchangeFirmMapper execOrGiveUpFirmMapper = new FixUtilExchangeFirmMapper(er.ExecBroker);
        bustReportStruct.executingOrGiveUpFirm = execOrGiveUpFirmMapper.getCMIExchangeFirmStruct();

        // This is the user ID. Need to get it from the session variable.
        bustReportStruct.userId = "";

        bustReportStruct.userAcronym =
                FixExecutionReportToCmiMapper.mapToUserAcronymStruct(er);

        bustReportStruct.bustedQuantity = Integer.parseInt
                (udfTable.getValue(FixUtilUserDefinedTagConstants.LAST_BUST_SHARES));

        bustReportStruct.price = FixUtilPriceHelper.makeValuedPrice(er.Price);

        bustReportStruct.productKey = Integer.parseInt(er.SecurityID);

        bustReportStruct.side = FixUtilMapper.getCmiSide(er.Side, er.SecurityType);

        bustReportStruct.timeSent =
                FixExecutionReportToCmiMapper.mapToReceivedDateTimeStruct(er);

        bustReportStruct.reinstateRequestedQuantity = 0;

        bustReportStruct.transactionSequenceNumber =
                        FixExecutionReportToCmiMapper.getTransactionSequenceNo(er);

        return bustReportStruct;
    }

    public static BustReinstateReportStruct mapToBustReinstateReportStruct(ExecutionReport er) throws DataValidationException
    {
        BustReinstateReportStruct bustReinstateReportStruct = new BustReinstateReportStruct();

        FixUtilUserDefinedFieldTable udfTable = new FixUtilUserDefinedFieldTable(er.UserDefined);

        // Seems to populate tradeId with zero, but I'll try to get it anyway.
        bustReinstateReportStruct.tradeId =
                FixExecutionReportToCmiMapper.mapToCboeTradeIdStruct(er.ExecRefID);

        bustReinstateReportStruct.bustedQuantity = Integer.parseInt
                (udfTable.getValue(FixUtilUserDefinedTagConstants.LAST_BUST_SHARES));

        // Dont think that er has this, but doublecheck
        bustReinstateReportStruct.reinstatedQuantity = (int) er.CumQty;

        bustReinstateReportStruct.totalRemainingQuantity = (int) er.LeavesQty;

        bustReinstateReportStruct.price = FixUtilPriceHelper.makeValuedPrice(er.Price);

        bustReinstateReportStruct.productKey = Integer.parseInt(er.SecurityID);

        bustReinstateReportStruct.sessionName = er.TradingSessionID;

        bustReinstateReportStruct.side = FixUtilMapper.getCmiSide(er.Side, er.SecurityType);

        bustReinstateReportStruct.timeSent = FixExecutionReportToCmiMapper.mapToReceivedDateTimeStruct(er);

        bustReinstateReportStruct.transactionSequenceNumber =
                                    FixExecutionReportToCmiMapper.getTransactionSequenceNo(er);

        return bustReinstateReportStruct;
    }

    public static LegOrderDetailStruct mapToLegOrderDetailStruct(ExecutionReport er) throws DataValidationException
    {
        LegOrderDetailStruct legOrderDetailStruct = new LegOrderDetailStruct();
        legOrderDetailStruct.productKey = Integer.parseInt(er.SecurityID);

        legOrderDetailStruct.mustUsePrice = FixUtilPriceHelper.makeValuedPrice(er.Price);

        FixUtilExchangeFirmMapper execOrGiveUpFirmMapper = new FixUtilExchangeFirmMapper(er.ExecBroker);
        legOrderDetailStruct.clearingFirm = execOrGiveUpFirmMapper.getCMIExchangeFirmStruct();

        legOrderDetailStruct.coverage = CoverageTypes.UNSPECIFIED;

        legOrderDetailStruct.positionEffect = FixUtilMapper.getCmiPositionEffect(er.OpenClose);
        legOrderDetailStruct.side = FixUtilMapper.getCmiSide(er.Side, er.SecurityType);

        legOrderDetailStruct.originalQuantity = (int)er.OrderQty;
        legOrderDetailStruct.tradedQuantity = (int)er.CumQty;
        legOrderDetailStruct.cancelledQuantity = (int)er.CxlQty;
        legOrderDetailStruct.leavesQuantity = (int)er.LeavesQty;

        return legOrderDetailStruct;
    }

    public static LegOrderDetailStruct mapToLegOrderDetailStruct(ExecutionReport er,
    		StrategyLegStruct productLeg) throws DataValidationException
    {
        LegOrderDetailStruct legOrderDetailStruct = new LegOrderDetailStruct();
        legOrderDetailStruct.productKey = productLeg.product;

        legOrderDetailStruct.mustUsePrice = FixUtilPriceHelper.makeValuedPrice(er.Price);

        FixUtilExchangeFirmMapper execOrGiveUpFirmMapper = new FixUtilExchangeFirmMapper(er.ExecBroker);
        legOrderDetailStruct.clearingFirm = execOrGiveUpFirmMapper.getCMIExchangeFirmStruct();

        legOrderDetailStruct.coverage = CoverageTypes.UNSPECIFIED;

        legOrderDetailStruct.positionEffect = FixUtilMapper.getCmiPositionEffect(er.OpenClose);
        legOrderDetailStruct.side = productLeg.side;

        legOrderDetailStruct.originalQuantity = (int)er.OrderQty;
        legOrderDetailStruct.tradedQuantity = (int)er.CumQty;
        legOrderDetailStruct.cancelledQuantity = (int)er.CxlQty;
        legOrderDetailStruct.leavesQuantity = (int)er.LeavesQty;

        return legOrderDetailStruct;
    }
    public static ContraPartyStruct[] mapToContraPartyStruct(ExecutionReport er)
    {
        int numContras = er.NoContraBrokers;

        ContraPartyStruct[] cps = new ContraPartyStruct[numContras];
        ExchangeAcronymStruct[] user = new ExchangeAcronymStruct[numContras];
        ExchangeFirmStruct[] firm = new ExchangeFirmStruct[numContras];
        int[] quantity = new int[numContras];

        for (int i=0; i < numContras; i++)
        {
            FixUtilExchangeFirmMapper contraPartyMapper = new FixUtilExchangeFirmMapper(er.ContraBroker[i]);
            firm[i] = contraPartyMapper.getCMIExchangeFirmStruct();

        	user[i] = new ExchangeAcronymStruct();
        	if (er.ContraTrader[i] != null) {
        		user[i].acronym = er.ContraTrader[i];
        	} else {
        		user[i].acronym = "";
        	}
        	
            // Use exchange from contra broker for contra trader
            user[i].exchange = contraPartyMapper.getCMIExchangeFirmStruct().exchange;
            
            quantity[i] = (int) er.ContraTradeQty[i];

            cps[i] = new ContraPartyStruct(user[i], firm[i], quantity[i]);

        }

        return cps;

    }

    public static OrderIdStruct mapToOrderIdStruct(ExecutionReport er) throws DataValidationException
    {
        OrderIdStruct orderIdStruct = new OrderIdStruct();
        FixUtilExchangeFirmMapper execOrGiveUpFirmMapper = new FixUtilExchangeFirmMapper(er.ExecBroker);

        orderIdStruct.executingOrGiveUpFirm = execOrGiveUpFirmMapper.getCMIExchangeFirmStruct();

        FixUtilClOrdIDWrapper clOrdIdWrapper = new FixUtilClOrdIDWrapper(er.ClOrdID);

        orderIdStruct.branch = clOrdIdWrapper.getBranch();
        orderIdStruct.branchSequenceNumber = clOrdIdWrapper.getSequenceAsInt();
        orderIdStruct.correspondentFirm = er.ClientID;
        orderIdStruct.orderDate = clOrdIdWrapper.getOrderDate();

        // Get these from the ExecID... which is OrderHigh:OrderLow.TradeHigh:TradeLow.SequenceNumber
        // What we need is the OrderHigh and OrderLow
        orderIdStruct.highCboeId = FixExecutionReportToCmiMapper.getOrderHigh(er);
        orderIdStruct.lowCboeId = FixExecutionReportToCmiMapper.getOrderLow(er);

        return orderIdStruct;
    }

    // This is returning the same struct as above.
    public static OrderIdStruct mapToCrossedOrderStruct(ExecutionReport er) throws DataValidationException
    {
        FixUtilExchangeFirmMapper execOrGiveUpFirmMapper = new FixUtilExchangeFirmMapper(er.ExecBroker);
        ExchangeFirmStruct executingOrGiveUpFirm = execOrGiveUpFirmMapper.getCMIExchangeFirmStruct();

        FixUtilClOrdIDWrapper clOrdIdWrapper = new FixUtilClOrdIDWrapper(er.ClOrdID);
        String branch = clOrdIdWrapper.getBranch();
        int branchSequenceNumber = clOrdIdWrapper.getSequenceAsInt();

        String correspondentFirm = er.ClientID;
        String orderDate = clOrdIdWrapper.getOrderDate();

        // Get these from the ExecID... which is OrderHigh:OrderLow.TradeHigh:TradeLow.SequenceNumber
        // What we need is the OrderHigh and OrderLow
        int highCboeId = FixExecutionReportToCmiMapper.getOrderHigh(er);
        int lowCboeId = FixExecutionReportToCmiMapper.getOrderLow(er);

        return new OrderIdStruct(executingOrGiveUpFirm, branch, branchSequenceNumber,
                correspondentFirm, orderDate, highCboeId, lowCboeId);
    }

    public static ExchangeAcronymStruct mapToOriginatorStruct(ExecutionReport er)
    {

        FixUtilUserDefinedFieldTable udfTable = new FixUtilUserDefinedFieldTable(er.UserDefined);
        ExchangeAcronymStruct eas;
        if (!udfTable.getValue(FixUtilUserDefinedTagConstants.ORDER_ORIGINATOR).equals("")) {
			FixUtilExchangeAcronymMapper feam =
				new FixUtilExchangeAcronymMapper(udfTable.getValue(FixUtilUserDefinedTagConstants.ORDER_ORIGINATOR));
            eas = feam.getCMIExchangeAcronymStruct();
		}else
        {
            eas = new ExchangeAcronymStruct();
        }

        return eas;
    }

    public static ExchangeAcronymStruct mapToUserAcronymStruct(ExecutionReport er)
    {
        // Do we need this? Dont see something like this coming from a fix message on the fixcas side.
        String exchange;
        String acronym;

        return new ExchangeAcronymStruct();
    }

    // Return system time
    public static DateTimeStruct mapToReceivedDateTimeStruct(ExecutionReport er)
    {
    	return FixUtilDateTimeHelper.makeDateTimeStruct(new Date());
    }

    // we dont really care about this time
    public static DateTimeStruct mapToExpireDateTimeStruct(ExecutionReport er)
    {
      	return FixUtilDateTimeHelper.makeDateTimeStruct(new Date());
    }

    public static DateStruct mapToExpireDateStruct(ExecutionReport er)
    {
    	byte day = 0;
    	byte month = 0;
    	short year = 0;
    	
    	if (er.MaturityDay != null) {
    		try {
    			day = (byte) Integer.parseInt(er.MaturityDay);
    		} catch (NumberFormatException e) {
    			// Leave a value of 0 if not numeric
    		}
    	}
        
    	if (er.MaturityMonthYear != null && er.MaturityMonthYear.length() >= 6) {
    		month = (byte) Integer.parseInt(er.MaturityMonthYear.substring(4, 6));       	
        	year = (short) Integer.parseInt(er.MaturityMonthYear.substring(0, 4));
    	}
        return new DateStruct(month, day, year);
    }

    public static OrderContingencyStruct mapToOrderContingencyStruct(ExecutionReport er)
    {
        short type = ContingencyTypes.NONE;
        PriceStruct price = FixUtilPriceHelper.makeValuedPrice(er.Price);
        int volume = 0;

        return new OrderContingencyStruct(type, price, volume);
    }

    public static ExchangeFirmStruct mapToExecutingOrGiveUpFirmStruct(ExecutionReport er)
    {
        FixUtilExchangeFirmMapper execOrGiveUpFirmMapper = new FixUtilExchangeFirmMapper(er.ExecBroker);
        return execOrGiveUpFirmMapper.getCMIExchangeFirmStruct();
    }

    public static ExchangeFirmStruct mapToCrossedOrderExchangeFirmStruct(ExecutionReport er)
    {
        FixUtilExchangeFirmMapper execOrGiveUpFirmMapper = new FixUtilExchangeFirmMapper(er.ClearingFirm);
        return execOrGiveUpFirmMapper.getCMIExchangeFirmStruct();
    }

    public static CboeIdStruct mapToCboeTradeIdStruct(String execId)
    {
        CboeIdStruct cis = new CboeIdStruct();

        cis.highCboeId = FixExecutionReportToCmiMapper.getTradeHigh(execId);
        cis.lowCboeId = FixExecutionReportToCmiMapper.getTradeLow(execId);

        return cis;
    }

    public static char mapToOrderOriginType(ExecutionReport er) throws DataValidationException
    {
        // We get this from Rule80A or CustomerOrFirm.
        if (er.Rule80A != null)
        {
        	return FixUtilMapper.getCmiOrderCapacity(er.Rule80A);
        }else
        //Try and get it from Tag 204 = CustomerOrFirm
        // This may be a redunadant piece of code?
        // Will Tag 47 be always populated? So that we'll never get to this piece?
        {
        	return FixUtilMapper.getCmiOriginType(er.CustomerOrFirm);
        }
    }

    // ExecID is of the form : "OrderHigh:OrderLow.TradeHigh:TradeLow.SequenceNumber"
    // For strategy legs, an extra field (current time millis) follows a blank.
    public static int getTransactionSequenceNo(ExecutionReport er)
    {
    	int transSequenceNo = 0;
    	
        //We want the 3rd token of ExecID
        String[] tokens = er.ExecID.split(". ");
        if (tokens.length >= 3) {
        	transSequenceNo =  Integer.parseInt(tokens[2]);
        }
        return transSequenceNo;
    }

    // ExecID is of the form : "OrderHigh:OrderLow.TradeHigh:TradeLow.SequenceNumber"
    private static int getOrderHigh(ExecutionReport er)
    {
        StringTokenizer strTkn = new StringTokenizer(er.ExecID, ":");
        // We want the first token separated by ":"
        String oH =  strTkn.nextToken();
        int orderHigh =  Integer.parseInt(oH);

        return orderHigh;

    }

    // ExecID is of the form : "OrderHigh:OrderLow.TradeHigh:TradeLow.SequenceNumber"
    private static int getOrderLow(ExecutionReport er)
    {
        StringTokenizer strTkn = new StringTokenizer(er.ExecID, ".");
        // We want the first token separated by "."
        String oHoL =  strTkn.nextToken();

        StringTokenizer strTkn2 = new StringTokenizer(oHoL, ":");
        //We want the second token here.
        String oL = strTkn2.nextToken();
        while (strTkn2.hasMoreTokens())
            oL = strTkn2.nextToken();
        int orderLow =  Integer.parseInt(oL);

        return orderLow;

    }

    // ExecID is of the form : "OrderHigh:OrderLow.TradeHigh:TradeLow.SequenceNumber"
    private static int getTradeHigh(String execID)
    {
        StringTokenizer strTkn = new StringTokenizer(execID, ".");
        // We want the second token separated by "."
        String tHtL =  strTkn.nextToken();
        tHtL = strTkn.nextToken();
        // Now we want the first token in strTkn2, separated by ":"
        StringTokenizer strTkn2 = new StringTokenizer(tHtL, ":");

        int tradeHigh =  Integer.parseInt(strTkn2.nextToken());

        return tradeHigh;

    }

    // ExecID is of the form : "OrderHigh:OrderLow.TradeHigh:TradeLow.SequenceNumber"
    private static int getTradeLow(String execID)
    {
        StringTokenizer strTkn = new StringTokenizer(execID, ".");
        // We want the second token separated by "."
        String tHtL =  strTkn.nextToken();
        tHtL = strTkn.nextToken();

        StringTokenizer strTkn2 = new StringTokenizer(tHtL, ":");
        //We want the second token here.
        String tL = strTkn2.nextToken();
        while (strTkn2.hasMoreTokens())
            tL = strTkn2.nextToken();
        int tradeLow =  Integer.parseInt(tL);

        return tradeLow;

    }

    private static short getMultiLegReportingType(ExecutionReport er) throws DataValidationException
    {
        short reportType;
        switch (er.MultiLegReportingType.charAt(0)){
            case '1':
                reportType = ReportTypes.REGULAR_REPORT;
                break;
            case '2':
                reportType = ReportTypes.STRATEGY_LEG_REPORT;
                break;
            case '3':
                reportType = ReportTypes.STRATEGY_REPORT;
                break;
            default:
                throw ExceptionBuilder.dataValidationException("Invalid Tag " +
                        er.MultiLegReportingType + " ", FixUtilCmiErrorCodes.FixDataValidationCodes.INVALID_STRATEGY);

        }
        return reportType;
    }

    private static short getCancelReason(ExecutionReport er) throws DataValidationException
    {
        short reason;
        switch(er.Text.trim().charAt(0))
        {
            case 'N':
                reason = ActivityReasons.NOTHING_DONE;
                break;
            case 'U':
                reason = ActivityReasons.USER;
                break;
            case 'S':
                reason = ActivityReasons.SYSTEM;
                break;
            case 'L':
                reason = ActivityReasons.LOST_CONNECTION;
                break;
            case 'Q':
                reason = ActivityReasons.QRM_REMOVED;
                break;
            case 'P':
                switch(er.Text.trim().charAt(8))
                {
                    case 'H':
                        reason = ActivityReasons.PRODUCT_HALTED;
                        break;
                    case 'S':
                        reason = ActivityReasons.PRODUCT_SUSPENDED;
                        break;
                    default:
                        throw ExceptionBuilder.dataValidationException("Invalid Value for Cancel Reason " +
                        er.Text + " ", FixUtilCmiErrorCodes.FixDataValidationCodes.INVALID_CANCEL_REQUEST);
                }

            default:
                throw ExceptionBuilder.dataValidationException("Invalid Value for Cancel Reason " +
                        er.Text + " ", FixUtilCmiErrorCodes.FixDataValidationCodes.INVALID_CANCEL_REQUEST);

        }
        return reason;
    }

    private static String getExtensions(ExecutionReport er) throws DataValidationException
    {
        FixUtilUserDefinedFieldTable udfTable = new FixUtilUserDefinedFieldTable(er.UserDefined);
        // Need to do reverse of all the work that is done in the ExecutionReportMapper.
        StringBuffer extBuffer = new StringBuffer();
        // LastMkt (30) --> EXCHANGE_DESTINATION
        if (er.LastMkt != null && !er.LastMkt.trim().equalsIgnoreCase(""))
        {
            extBuffer.append(buildExtensionsMapping
                    (ExtensionFields.EXCHANGE_DESTINATION,FixUtilMapper.getCmiExchangeFromFixOla(er.LastMkt)));

        }
        // ExecId (17) --> AWAY_EXCHANGE_EXEC_ID
        if(er.Rule80A.charAt(0) == OrderOrigins.PRINCIPAL)
        {
            if (er.ExecID != null && !er.ExecID.trim().equalsIgnoreCase(""))
            {
                extBuffer.append(buildExtensionsMapping(ExtensionFields.AWAY_EXCHANGE_EXEC_ID,er.ExecID));
            }

        }
        // AutoexecSize (5201) --> AUTO_EXECUTION_SIZE
        String autoExecSize = udfTable.getValue(ExtensionFields.AUTO_EXECUTION_SIZE);
        if (autoExecSize != null && !autoExecSize.trim().equalsIgnoreCase(""))
        {
            extBuffer.append(buildExtensionsMapping(ExtensionFields.AUTO_EXECUTION_SIZE,autoExecSize));
        }
        // OLAOrdRejReason (5209) --> OLA_REJECT_REASON
        /*
            // OLAOrdRejReason (5209)
              workResult = eh.getValue(ExtensionFields.OLA_REJECT_REASON);
              if (workResult != null && !workResult.equals("")) {
                  try {
                      short olaRejectReason = (short)Integer.parseInt(workResult);
                      int   fixRejectReason = FixMapper.mapToFixOlaOrdRejReason(olaRejectReason);
                      writeToUdf(ExtensionFields.OLA_REJECT_REASON, Integer.toString(fixRejectReason));
                  }
                  catch (NumberFormatException e) {
                      Log.exception(e);
                  }
              }
        */
        String olaRejReason = udfTable.getValue(ExtensionFields.OLA_REJECT_REASON);
        if (olaRejReason != null && !olaRejReason.trim().equalsIgnoreCase(""))
        {
            int fixRejReason = Integer.parseInt(olaRejReason);
            // In FixUtilMapper - write the opposite of mapToFixOlaOrdRejReason
            extBuffer.append(buildExtensionsMapping(ExtensionFields.OLA_REJECT_REASON,olaRejReason));
        }
        // StockFirm
        String stockFirm = udfTable.getValue(FixUtilUserDefinedTagConstants.STOCK_FIRM_NAME);
        if (stockFirm != null && !stockFirm.trim().equalsIgnoreCase(""))
        {
            extBuffer.append(buildExtensionsMapping(ExtensionFields.STOCK_FIRM,stockFirm));
        }
        // StockFirmName
        String stockFirmName = udfTable.getValue(FixUtilUserDefinedTagConstants.STOCK_FIRM_NAME_KEY);
        if (stockFirmName != null && !stockFirmName.trim().equalsIgnoreCase(""))
        {
            extBuffer.append(buildExtensionsMapping(ExtensionFields.STOCK_FIRM_NAME,stockFirmName));
        }

        return extBuffer.toString();
    }

    private static String buildExtensionsMapping(String field, String value)
    {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append(field);
        strBuffer.append(ExtensionsHelper.DEFAULT_TAG_DELIMITER);
        strBuffer.append(value);
        strBuffer.append(ExtensionsHelper.DEFAULT_FIELD_DELIMITER);
        return strBuffer.toString();
    }

    public static char getCmiTimeInForce(String fixTimeInForce) {
    	if (FixUtilConstants.TimeInForce.GTC.equals(fixTimeInForce)) {
    		return TimesInForce.GTC;	
    	} else if (FixUtilConstants.TimeInForce.GTD.equals(fixTimeInForce)) {
    		return TimesInForce.GTD;
    	} else {
    		// DAY is the default for FIX
    		return TimesInForce.DAY;
    	}
    }
    
    /**
     * Lookup product, usually found in local cache
     * @param productKey the unique key
     * @return a full description of an instrument
     * @throws DataValidationException
     */
    private static Product getProduct(int productKey) throws DataValidationException {
		Product product = null;
		
	    try {
	    	product = APIHome.findProductQueryAPI().getProductByKey(productKey);
		} catch (SystemException e) {
			GUILoggerHome.find().exception(e);
		} catch (CommunicationException e) {
			GUILoggerHome.find().exception(e);
		} catch (AuthorizationException e) {
			GUILoggerHome.find().exception(e);
		} catch (NotFoundException e) {
			GUILoggerHome.find().exception(e);
		}
		
		return product;
    }
    
    
    /**
     * Map FIX CoveredOrUncovered to CMi CoverageTypes
     */
    private static char getCmiCoverageType(int fixCoverage) {
        switch(fixCoverage){
            case FixUtilConstants.CoveredOrUncovered.COVERED :
            	return CoverageTypes.COVERED;
            case FixUtilConstants.CoveredOrUncovered.UNCOVERED :
            	return CoverageTypes.UNCOVERED;
            default :
            	return CoverageTypes.UNSPECIFIED;        }
    }


    /**
	 * Convert a FIX price to a CMi PriceStruct
	 * @param fixPrice as represented in FIX
	 * @return a CMi price
	 */
	private static PriceStruct getCmiPrice(double fixPrice, int doNotSendValue) {
		if (fixPrice == doNotSendValue) {
			return FixUtilPriceHelper.makeNoPrice();
		} else {
			return FixUtilPriceHelper.makeValuedPrice(fixPrice);
		}
	}

    public static Order extractOrderFromOrderList (OrderList orderListMsg, int index)
    {
        Order orderMsg = new Order();

        orderMsg.Account = nonull(orderListMsg.Account, index);
        orderMsg.ClearingAccount = nonull(orderListMsg.ClearingAccount, index);
        orderMsg.ClearingFirm = nonull(orderListMsg.ClearingFirm, index);
        orderMsg.ClientID = nonull(orderListMsg.ClientID, index);
        orderMsg.ClOrdID = nonull(orderListMsg.ClOrdID, index);
        if (orderListMsg.CoveredOrUncovered != null && orderListMsg.CoveredOrUncovered.length !=0)
        orderMsg.CoveredOrUncovered = orderListMsg.CoveredOrUncovered[index];
        if (orderListMsg.CustomerOrFirm != null && orderListMsg.CustomerOrFirm.length !=0)
        orderMsg.CustomerOrFirm = orderListMsg.CustomerOrFirm[index];
        orderMsg.DiscretionInst = nonull(orderListMsg.DiscretionInst,index);
        if (orderListMsg.DiscretionOffset != null && orderListMsg.DiscretionOffset.length !=0)
        orderMsg.DiscretionOffset = orderListMsg.DiscretionOffset[index];
        orderMsg.ExDestination = nonull(orderListMsg.ExDestination, index);
        orderMsg.ExecBroker = nonull(orderListMsg.ExecBroker, index);
        orderMsg.ExecInst = nonull(orderListMsg.ExecInst, index);
        orderMsg.ExpireDate = nonull(orderListMsg.ExpireDate, index);
        orderMsg.ExpireTime = nonull(orderListMsg.ExpireTime, index);
        orderMsg.HandlInst = nonull(orderListMsg.HandlInst, index);
        orderMsg.MaturityDay = nonull(orderListMsg.MaturityDay, index);
        orderMsg.MaturityMonthYear = nonull(orderListMsg.MaturityMonthYear, index);
        if (orderListMsg.MinQty != null && orderListMsg.MinQty.length !=0)
        orderMsg.MinQty = orderListMsg.MinQty[index];
        if (orderListMsg.NoTradingSessions != null && orderListMsg.NoTradingSessions.length !=0)
        orderMsg.NoTradingSessions = orderListMsg.NoTradingSessions[index];
        orderMsg.OpenClose = nonull(orderListMsg.OpenClose, index);
        if (orderListMsg.OrderQty != null && orderListMsg.OrderQty.length !=0)
        orderMsg.OrderQty = orderListMsg.OrderQty[index];
        if (orderListMsg.OrderQty2 != null && orderListMsg.OrderQty2.length !=0)
        orderMsg.OrderQty2 = orderListMsg.OrderQty2[index];
        orderMsg.OrdType = nonull(orderListMsg.OrdType, index);
        if (orderListMsg.Price != null && orderListMsg.Price.length !=0)
        orderMsg.Price = orderListMsg.Price[index];
        if (orderListMsg.PutOrCall != null && orderListMsg.PutOrCall.length !=0)
        orderMsg.PutOrCall = orderListMsg.PutOrCall[index];
        orderMsg.QuoteID = nonull(orderListMsg.QuoteID, index);
        orderMsg.Rule80A = nonull(orderListMsg.Rule80A, index);
        orderMsg.SecurityDesc = nonull(orderListMsg.SecurityDesc, index);
        orderMsg.SecurityExchange = nonull(orderListMsg.SecurityExchange, index);
        orderMsg.SecurityID = nonull(orderListMsg.SecurityID, index);
        orderMsg.SecurityType = nonull(orderListMsg.SecurityType, index);
        orderMsg.SettlmntTyp = nonull(orderListMsg.SettlmntTyp, index);
        orderMsg.Side = nonull(orderListMsg.Side, index);
        if (orderListMsg.StopPx != null && orderListMsg.StopPx.length !=0)
        orderMsg.StopPx = orderListMsg.StopPx[index];
        if (orderListMsg.StrikePrice != null && orderListMsg.StrikePrice.length !=0)
        orderMsg.StrikePrice = orderListMsg.StrikePrice[index];
        orderMsg.Symbol = nonull(orderListMsg.Symbol, index);
        orderMsg.TimeInForce = nonull(orderListMsg.TimeInForce, index);
        //todo? - think this will always be populated from the fixcas - so don't need to check
        orderMsg.TradingSessionID = orderListMsg.TradingSessionID[index];
        orderMsg.TransactTime = nonull(orderListMsg.TransactTime, index);

        orderMsg.UserDefined = nonull(orderListMsg.UserDefined);

        return orderMsg;
    }

    /** Ensure that we don't have a null String.
     * @param s String to check.
     * @return s if not null, else empty string.
     */
    protected final static String nonull(String s)
    {
        return (s == null) ? "" : s;
    }

    /** Ensure that we don't have a null Array & a no null in the value.
     * @param sA string array entry
     * @param index the entry in the array being checked
     * @return s if not null, else empty string.
     */
    protected final static String nonull(String[] sA, int index)
    {
        if (sA != null && sA.length != 0)
        {
            return (sA[index] == null) ? "" : sA[index];

        }else
        {
            return null;
        }
    }

}
