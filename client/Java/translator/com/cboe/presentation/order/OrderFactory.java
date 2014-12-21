//
// -----------------------------------------------------------------------------------
// Source file: OrderFactory.java
//
// PACKAGE: com.cboe.presentation.order
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.order;

import com.cboe.domain.util.OrderStructBuilder;
import com.cboe.domain.util.PriceFactory;
import com.cboe.domain.util.StructBuilder;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiConstants.CoverageTypes;
import com.cboe.idl.cmiConstants.OrderNBBOProtectionTypes;
import com.cboe.idl.cmiConstants.OrderOrigins;
import com.cboe.idl.cmiConstants.OrderStates;
import com.cboe.idl.cmiConstants.PositionEffects;
import com.cboe.idl.cmiConstants.PriceTypes;
import com.cboe.idl.cmiConstants.ProductTypes;
import com.cboe.idl.cmiConstants.Sides;
import com.cboe.idl.cmiConstants.StatusUpdateReasons;
import com.cboe.idl.cmiConstants.TimesInForce;
import com.cboe.idl.cmiOrder.LegOrderDetailStruct;
import com.cboe.idl.cmiOrder.LightOrderEntryStruct;
import com.cboe.idl.cmiOrder.LightOrderResultStruct;
import com.cboe.idl.cmiOrder.OrderDetailStruct;
import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.interfaces.presentation.order.LegOrderDetail;
import com.cboe.interfaces.presentation.order.MutableOrder;
import com.cboe.interfaces.presentation.order.MutableStrategyOrder;
import com.cboe.interfaces.presentation.order.Order;
import com.cboe.interfaces.presentation.order.StrategyOrder;
import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.presentation.user.Role;
import com.cboe.interfaces.presentation.user.UserModel;
import com.cboe.presentation.api.APIHome;
import com.cboe.presentation.common.formatters.ContingencyTypes;
import com.cboe.presentation.common.formatters.Sources;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.product.ProductFactoryHome;
import com.cboe.presentation.product.ProductHelper;
import com.cboe.presentation.user.ExchangeFirmFactory;
import com.cboe.presentation.userSession.UserSessionFactory;

public class OrderFactory
{
    protected static final LegOrderDetailStruct[] ZERO_LEGS = new LegOrderDetailStruct[0];

    public static Order createOrder(OrderStruct orderStruct)
    {
        Order order;

        if (orderStruct.productType == ProductTypes.STRATEGY)
        {
            order = createStrategyOrder(orderStruct);
        }
        else
        {
            order = new OrderImpl(orderStruct);
        }
        return order;
    }

    public static MutableOrder createMutableOrder(OrderStruct orderStruct)
    {
        MutableOrder order;

        if (orderStruct.productType == ProductTypes.STRATEGY)
        {
            order = createMutableStrategyOrder(orderStruct);
        }
        else
        {
            order = (MutableOrder) createOrder(orderStruct);
        }
        return order;
    }

    public static MutableOrder createOppositeOrder(OrderStruct orderStruct)
    {
        MutableOrder order;
        OrderStruct newStruct = OrderStructBuilder.cloneOrderStruct(orderStruct);
        if (orderStruct.productType == ProductTypes.STRATEGY)
        {
            LegOrderDetailStruct[] details = newStruct.legOrderDetails;
            for (int i = 0; i < details.length; i++)
            {
                if (details[i].side == Sides.BUY)
                {
                    details[i].side = Sides.SELL;
                }
                else
                {
                    details[i].side = Sides.BUY;
                }
            }
        }

        order = createMutableOrder(newStruct);
        if (orderStruct.productType == ProductTypes.STRATEGY)
        {
            order.setPrice(PriceFactory.create(order.getPrice().toLong() * (-1L)));
        }
        return order;
    }

    public static Order createOrder()
    {
        Order order;
        int productType = APIHome.findProductQueryAPI().getDefaultSessionProduct().getProductType();

        if (productType == ProductTypes.STRATEGY)
        {
            order = createStrategyOrder();
        }
        else
        {
            order = new OrderImpl();
        }
        return order;
    }

    public static MutableOrder createMutableOrder()
    {
        return (MutableOrder) createOrder();
    }

    public static StrategyOrder createStrategyOrder()
    {
        return new ComplexOrderImpl();
    }

    public static MutableStrategyOrder createMutableStrategyOrder()
    {
        return (MutableStrategyOrder) createStrategyOrder();
    }

    public static StrategyOrder createStrategyOrder(OrderStruct orderStruct)
    {
        return new ComplexOrderImpl(orderStruct);
    }

    public static MutableStrategyOrder createMutableStrategyOrder(OrderStruct orderStruct)
    {
        return (MutableStrategyOrder) createStrategyOrder(orderStruct);
    }

    public static OrderStruct createDefaultOrderStruct()
    {
        OrderStruct newStruct = new OrderStruct();

        UserModel user = null;
        user = UserSessionFactory.findUserSession().getUserModel();

        if (user != null)
        {
            newStruct.account = user.getDefaultProfile().getAccount();
        }
        else
        {
            newStruct.account = "";
        }

        Role role = user.getRole();
        if (role == Role.MARKET_MAKER || role == Role.DPM)
        {
            newStruct.orderOriginType = OrderOrigins.MARKET_MAKER;
        }
        else
        {
            newStruct.orderOriginType = OrderOrigins.CUSTOMER;
        }

        newStruct.cancelledQuantity = 0;

        newStruct.leavesQuantity = 0;
        newStruct.averagePrice = new PriceStruct(PriceTypes.VALUED, 0, 0);
        newStruct.sessionTradedQuantity = 0;
        newStruct.sessionCancelledQuantity = 0;
        newStruct.sessionAveragePrice = new PriceStruct(PriceTypes.VALUED, 0, 0);

        newStruct.classKey = APIHome.findProductQueryAPI().getDefaultSessionProductClass().getClassKey();
        newStruct.cmta = StructBuilder.buildExchangeFirmStruct("", "");
        newStruct.contingency = OrderContingencyFactory.createDefaultStruct();
        newStruct.coverage = CoverageTypes.UNCOVERED;
        newStruct.orderNBBOProtectionType = OrderNBBOProtectionTypes.NONE;
        newStruct.cross = false;
        newStruct.crossedOrder = OrderIdFactory.createDefaultStruct();
        newStruct.expireTime = StructBuilder.buildDateTimeStruct();
        newStruct.extensions = "";
        newStruct.optionalData = "";
        newStruct.orderId = OrderIdFactory.createDefaultStruct();
        newStruct.originalQuantity = 0;
        newStruct.originator = StructBuilder.buildExchangeAcronymStruct("", "");
        newStruct.orsId = "";
        newStruct.positionEffect = PositionEffects.CLOSED;
        newStruct.price = StructBuilder.buildPriceStruct();
        newStruct.productType = APIHome.findProductQueryAPI().getDefaultSessionProduct().getProductType();
        newStruct.productKey = APIHome.findProductQueryAPI().getDefaultSessionProduct().getProductKey();
        newStruct.receivedTime = StructBuilder.buildDateTimeStruct();
        newStruct.side = Sides.BUY;
        newStruct.source = ' ';
        newStruct.state = OrderStates.ACTIVE;
        newStruct.subaccount = "";
        newStruct.timeInForce = TimesInForce.DAY;
        newStruct.tradedQuantity = 0;
        newStruct.transactionSequenceNumber = 0;
        newStruct.userId = "";
        newStruct.userAssignedId = "";
        newStruct.activeSession = APIHome.findProductQueryAPI().getDefaultSessionProduct().getTradingSessionName();
        newStruct.sessionNames = new String[0];
        newStruct.userAcronym = StructBuilder.buildExchangeAcronymStruct("", "");

        LegOrderDetailStruct legOrderDetail = new LegOrderDetailStruct();
        legOrderDetail.clearingFirm = StructBuilder.buildExchangeFirmStruct("", "");
        legOrderDetail.coverage = ' ';
        legOrderDetail.mustUsePrice = StructBuilder.buildPriceStruct();
        legOrderDetail.positionEffect = ' ';
        legOrderDetail.productKey = 0;
        legOrderDetail.side = 0;
        LegOrderDetailStruct[] legOrderDetails = {legOrderDetail};

        newStruct.legOrderDetails = legOrderDetails;

        return newStruct;
    }

    public static OrderStruct createDefaultStrategyOrderStruct()
    {
        OrderStruct newStruct = createDefaultOrderStruct();

        newStruct.productType = APIHome.findProductQueryAPI().getDefaultSessionStrategy().getProductType();
        newStruct.productKey = APIHome.findProductQueryAPI().getDefaultSessionStrategy().getProductKey();
        newStruct.activeSession = APIHome.findProductQueryAPI().getDefaultSessionStrategy().getTradingSessionName();

        return newStruct;
    }

    public static OrderDetailStruct buildOrderDetailStruct(OrderStruct order) throws CommunicationException, AuthorizationException,
            NotFoundException, SystemException
    {
        ProductNameStruct productName;
        try
        {
            productName = APIHome.findProductQueryAPI().getProductByKey(order.productKey).getProductNameStruct();
        }
        catch (DataValidationException e)
        {
            GUILoggerHome.find().exception(e);
            Product invalidProduct = ProductFactoryHome.find().createInvalidProduct(order.productKey);
            productName = invalidProduct.getProductNameStruct();
        }

        return new OrderDetailStruct(productName, StatusUpdateReasons.QUERY, order);
    }

    @SuppressWarnings({"OverlyLongMethod", "deprecation"})
    public static OrderStruct createOrderStruct(Order order)
    {
        OrderStruct struct;
        if (order instanceof StrategyOrder)
        {
            struct = createDefaultStrategyOrderStruct();
            LegOrderDetail[] details = order.getLegOrderDetails();
            struct.legOrderDetails = new LegOrderDetailStruct[details.length];
            for (int i = 0; i < details.length; i++)
            {
                struct.legOrderDetails[i] = details[i].getStruct();
            }
        }
        else
        {
            struct = createDefaultOrderStruct();
            struct.legOrderDetails = ZERO_LEGS;
        }
        struct.account = order.getAccount();
        struct.activeSession = order.getActiveSession();
        struct.averagePrice = order.getAveragePrice().toStruct();
        struct.cancelledQuantity = order.getCancelledQuantity();
        struct.classKey = order.getClassKey();
        struct.cmta = order.getCmta().getExchangeFirmStruct();
        struct.contingency = order.getContingency().getStruct();
        struct.coverage = order.getCoverage();
        struct.cross = order.isCross();
        struct.crossedOrder = order.getCrossedOrder().getStruct();
        struct.expireTime = order.getExpireTime().getDateTimeStruct();
        struct.extensions = order.getExtensions();
        struct.leavesQuantity = order.getLeavesQuantity();
        struct.optionalData = order.getOptionalData();
        struct.orderId = order.getOrderId().getStruct();
        struct.orderNBBOProtectionType = order.getOrderNBBOProtectionType();
        struct.orderOriginType = order.getOrderOriginType();
        struct.originalQuantity = order.getOriginalQuantity();
        struct.originator = order.getOriginator().getExchangeAcronymStruct();
        struct.orsId = order.getOrsId();
        struct.positionEffect = order.getPositionEffect();
        struct.price = order.getPrice().toStruct();
        struct.productKey = order.getProductKey();
        struct.productType = order.getProductType();
        struct.receivedTime = order.getReceivedTime().getDateTimeStruct();
        struct.sessionAveragePrice = order.getSessionAveragePrice().toStruct();
        struct.sessionCancelledQuantity = order.getSessionCancelledQuantity();
        struct.sessionNames = order.getSessionNames();
        struct.sessionTradedQuantity = order.getSessionTradedQuantity();
        struct.side = order.getSide();
        struct.source = order.getSource();
        struct.state = order.getState();
        struct.subaccount = order.getSubaccount();
        struct.timeInForce = order.getTimeInForce();
        struct.tradedQuantity = order.getTradedQuantity();
        struct.transactionSequenceNumber = order.getTransactionSequenceNumber();
        struct.userAcronym = order.getUserAcronym().getExchangeAcronymStruct();
        struct.userAssignedId = order.getUserAssignedId();
        struct.userId = order.getUserId();

        return struct;
    }

    public static OrderStruct createLightOrderStruct(LightOrderEntryStruct struct, LightOrderResultStruct result)
    {
        OrderStruct order = createDefaultOrderStruct();
        order.orderId.branch = struct.branch;
        order.orderId.branchSequenceNumber = struct.branchSequenceNumber;
        order.side = struct.side;
        order.originalQuantity = struct.originalQuantity;
        order.price = PriceFactory.create(struct.Price).toStruct();
        order.productKey = struct.productKey;
        order.classKey = ProductHelper.getProduct(struct.productKey).getProductKeysStruct().classKey;
        order.positionEffect = struct.positionEffect;
        order.coverage = struct.coverage;
        order.contingency = struct.isIOC ? OrderContingencyFactory.createOrderContingencyWithType(ContingencyTypes.IOC).getStruct()
                : OrderContingencyFactory.createDefaultStruct();
        order.orderOriginType = struct.orderOriginType;
        order.cmta = ExchangeFirmFactory.createExchangeFirm(struct.cmtaExchange, struct.cmtaFirmNumber).getExchangeFirmStruct();
        order.userAssignedId = struct.userAssignedId;
        order.activeSession = struct.activeSession;
        order.orderId.highCboeId = result.orderHighId;
        order.orderId.lowCboeId = result.orderLowId;
        order.leavesQuantity = result.leavesQuantity;
        order.tradedQuantity = result.tradedQuantity;
        order.cancelledQuantity = result.cancelledQuantity;
        order.receivedTime = result.time;
        order.source = Sources.LIGHT;
        order.orderNBBOProtectionType = struct.isNBBOProtected ? OrderNBBOProtectionTypes.FULL : OrderNBBOProtectionTypes.NONE;

        return order;
    }

}