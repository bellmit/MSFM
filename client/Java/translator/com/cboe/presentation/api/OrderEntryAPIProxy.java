package com.cboe.presentation.api;

import static com.cboe.presentation.api.TraderAPIImpl.handleTransactionException;
import static com.cboe.presentation.api.TraderAPIImpl.logTransaction;

import org.omg.CORBA.UserException;

import com.cboe.domain.util.StructBuilder;
import com.cboe.exceptions.AlreadyExistsException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.idl.cmiOrder.CancelRequestStruct;
import com.cboe.idl.cmiOrder.InternalizationOrderResultStruct;
import com.cboe.idl.cmiOrder.LegOrderEntryStruct;
import com.cboe.idl.cmiOrder.LegOrderEntryStructV2;
import com.cboe.idl.cmiOrder.LightOrderEntryStruct;
import com.cboe.idl.cmiOrder.LightOrderResultStruct;
import com.cboe.idl.cmiOrder.OrderDetailStruct;
import com.cboe.idl.cmiOrder.OrderEntryStruct;
import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiQuote.RFQEntryStruct;
import com.cboe.idl.cmiV9.OrderEntry;
import com.cboe.interfaces.presentation.api.OrderEntryFacade;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.order.OrderFactory;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;

public class OrderEntryAPIProxy implements OrderEntryFacade
{
    private final OrderEntry delegate;

    /**
     * @param delegate
     */
    public OrderEntryAPIProxy(OrderEntry delegate)
    {
        this.delegate = delegate;
    }

    @Override
    public LightOrderResultStruct acceptLightOrder(LightOrderEntryStruct struct) throws SystemException, CommunicationException,
            AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException, AlreadyExistsException
    {
        logTransaction("acceptLightOrder", struct);

        LightOrderResultStruct result = null;
        try
        {
            result = delegate.acceptLightOrder(struct);
            publishOrderStatus(struct, result);
        }
        catch (Exception e)
        {
            handleTransactionException("acceptLightOrder", e);
        }

        logTransaction("acceptLightOrder", result);

        return result;
    }

    private void publishOrderStatus(LightOrderEntryStruct struct, LightOrderResultStruct result)
    {
        OrderDetailStruct orderDetail;
        try
        {
            OrderStruct order = OrderFactory.createLightOrderStruct(struct, result);
            orderDetail = OrderFactory.buildOrderDetailStruct(order);
            publishOrderStatus(orderDetail);
        }
        catch (UserException e)
        {
            GUILoggerHome.find().exception(e);
        }
    }

    private void publishOrderStatus(LightOrderResultStruct result)
    {
        try
        {
            OrderIdStruct idStruct = new OrderIdStruct();
            idStruct.highCboeId = result.orderHighId;
            idStruct.lowCboeId = result.orderLowId;
            idStruct.branch =  result.branch;
            idStruct.branchSequenceNumber =  result.branchSequenceNumber;
            idStruct.correspondentFirm = "";
            idStruct.executingOrGiveUpFirm = StructBuilder.buildExchangeFirmStruct("", "");
            idStruct.orderDate = "";

            OrderDetailStruct orderDetailStruct = APIHome.findOrderQueryAPI().getOrderById(idStruct, true);
            if(orderDetailStruct != null)
            {
                orderDetailStruct.orderStruct.cancelledQuantity = result.cancelledQuantity;
                orderDetailStruct.orderStruct.leavesQuantity = result.leavesQuantity;
                orderDetailStruct.orderStruct.tradedQuantity = result.tradedQuantity;
                orderDetailStruct.orderStruct.receivedTime = result.time;
                publishOrderStatus(orderDetailStruct);
            }
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e);
        }
    }

    private void publishOrderStatus(OrderDetailStruct... orders)
    {
        ChannelKey key = null;
        ChannelEvent event = null;
        EventChannelAdapter eventChannel = EventChannelAdapterFactory.find();

        // this channel is not keyed.
        key = new ChannelKey(ChannelType.CB_ALL_ORDERS, new Integer(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, orders);
        eventChannel.dispatch(event);

        // keyed by productKey.
        key = new ChannelKey(ChannelType.CB_ORDERS_FOR_PRODUCT, new Integer(orders[0].orderStruct.productKey));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, orders);
        eventChannel.dispatch(event);

        // keyed by product type.
        key = new ChannelKey(ChannelType.CB_ALL_ORDERS_FOR_TYPE, new Integer(orders[0].orderStruct.productType));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, orders);
        eventChannel.dispatch(event);

        // keyed by product class.
        key = new ChannelKey(ChannelType.CB_ORDERS_BY_CLASS, new Integer(orders[0].orderStruct.classKey));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, orders);
        eventChannel.dispatch(event);

        // keyed by product class.
        key = new ChannelKey(ChannelType.CB_ORDERS_FOR_SESSION, orders[0].orderStruct.activeSession);
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, orders);
        eventChannel.dispatch(event);

        // keyed by firm key.
        key = new ChannelKey(ChannelType.CB_ORDERS_BY_FIRM, new Integer(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, orders);
        eventChannel.dispatch(event);
    }

    @Override
    public LightOrderResultStruct acceptLightOrderCancelRequest(String branch, int sequenceNumber, int productKey, String session,
            String userAssignedCancelId) throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
            NotAcceptedException, TransactionFailedException
    {
        logTransaction("acceptLightOrderCancelRequest", branch, sequenceNumber, productKey, session, userAssignedCancelId);

        LightOrderResultStruct result = null;
        try
        {
            result = delegate.acceptLightOrderCancelRequest(branch, sequenceNumber, productKey, session, userAssignedCancelId);
            publishOrderStatus(result);
        }
        catch (Exception e)
        {
            try
            {
                handleTransactionException("acceptLightOrderCancelRequest", e);
            }
            catch (AlreadyExistsException e1)
            {
            }
        }

        return result;
    }

    @Override
    public LightOrderResultStruct acceptLightOrderCancelRequestById(int orderHighId, int orderLowId, int productKey, String session,
            String userAssignedCancelId) throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
            NotAcceptedException, TransactionFailedException
    {
        logTransaction("acceptLightOrderCancelRequestById", orderHighId, orderLowId, productKey, session, userAssignedCancelId);

        LightOrderResultStruct result = null;
        try
        {
            result = delegate.acceptLightOrderCancelRequestById(orderHighId, orderLowId, productKey, session, userAssignedCancelId);
            publishOrderStatus(result);
        }
        catch (Exception e)
        {
            try
            {
                handleTransactionException("acceptLightOrderCancelRequestById", e);
            }
            catch (AlreadyExistsException e1)
            {
            }
        }

        return result;
    }

    @Override
    public InternalizationOrderResultStruct acceptInternalizationStrategyOrderV7(OrderEntryStruct primaryOrder,
            LegOrderEntryStructV2[] primaryOrderLegEntriesV2, OrderEntryStruct matchOrder, LegOrderEntryStructV2[] matchOrderLegEntriesV2,
            short matchType) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException,
            TransactionFailedException
    {
        /* Order Entry method in TraderAPIImpl should be moved here */
        throw new UnsupportedOperationException();
    }

    @Override
    public OrderIdStruct acceptStrategyOrderCancelReplaceRequestV7(OrderIdStruct orderId, int cancelQuantity, OrderEntryStruct newOrder,
            LegOrderEntryStructV2[] legEntryDetailsV2) throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException, NotAcceptedException, TransactionFailedException
    {
        /* Order Entry method in TraderAPIImpl should be moved here */
        throw new UnsupportedOperationException();
    }

    @Override
    public OrderIdStruct acceptStrategyOrderV7(OrderEntryStruct anOrder, LegOrderEntryStructV2[] legEntryDetailsV2) throws SystemException,
            CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException,
            AlreadyExistsException
    {
        /* Order Entry method in TraderAPIImpl should be moved here */
        throw new UnsupportedOperationException();
    }

    @Override
    public InternalizationOrderResultStruct acceptInternalizationStrategyOrder(OrderEntryStruct primaryOrder,
            LegOrderEntryStruct[] primaryOrderLegDetails, OrderEntryStruct matchOrder, LegOrderEntryStruct[] matchOrderLegDetails,
            short matchOrderType) throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
            NotAcceptedException, TransactionFailedException
    {
        /* Order Entry method in TraderAPIImpl should be moved here */
        throw new UnsupportedOperationException();
    }

    @Override
    public InternalizationOrderResultStruct acceptInternalizationOrder(OrderEntryStruct orderEntryStruct1, OrderEntryStruct orderEntryStruct2,
            short matchOrderType) throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
            NotAcceptedException, TransactionFailedException
    {
        /* Order Entry method in TraderAPIImpl should be moved here */
        throw new UnsupportedOperationException();
    }

    @Override
    public void acceptCrossingOrder(OrderEntryStruct buyCrossingOrder, OrderEntryStruct sellCrossingOrder) throws SystemException,
            CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException,
            AlreadyExistsException
    {
        /* Order Entry method in TraderAPIImpl should be moved here */
        throw new UnsupportedOperationException();
    }

    @Override
    public OrderIdStruct acceptOrder(OrderEntryStruct anOrder) throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException, NotAcceptedException, TransactionFailedException, AlreadyExistsException
    {
        /* Order Entry method in TraderAPIImpl should be moved here */
        throw new UnsupportedOperationException();
    }

    @Override
    public OrderIdStruct acceptOrderByProductName(ProductNameStruct product, OrderEntryStruct anOrder) throws SystemException,
            CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException,
            AlreadyExistsException
    {
        /* Order Entry method in TraderAPIImpl should be moved here */
        throw new UnsupportedOperationException();
    }

    @Override
    public OrderIdStruct acceptOrderCancelReplaceRequest(OrderIdStruct orderId, int originalOrderRemainingQuantity, OrderEntryStruct newOrder)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException,
            TransactionFailedException
    {
        /* Order Entry method in TraderAPIImpl should be moved here */
        throw new UnsupportedOperationException();
    }

    @Override
    public void acceptOrderCancelRequest(CancelRequestStruct cancelRequest) throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException, NotAcceptedException, TransactionFailedException
    {
        /* Order Entry method in TraderAPIImpl should be moved here */
        throw new UnsupportedOperationException();
    }

    @Override
    public void acceptOrderUpdateRequest(int currentRemainingQuantity, OrderEntryStruct updatedOrder) throws SystemException, CommunicationException,
            AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        /* Order Entry method in TraderAPIImpl should be moved here */
        throw new UnsupportedOperationException();
    }

    @Override
    public void acceptRequestForQuote(RFQEntryStruct rfq) throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException, NotAcceptedException, TransactionFailedException
    {
        /* Order Entry method in TraderAPIImpl should be moved here */
        throw new UnsupportedOperationException();
    }

    @Override
    public OrderIdStruct acceptStrategyOrder(OrderEntryStruct anOrder, LegOrderEntryStruct[] legEntryDetails) throws SystemException,
            CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException,
            AlreadyExistsException
    {
        /* Order Entry method in TraderAPIImpl should be moved here */
        throw new UnsupportedOperationException();
    }

    @Override
    public OrderIdStruct acceptStrategyOrderCancelReplaceRequest(OrderIdStruct orderId, int cancelQuantity, OrderEntryStruct newOrder,
            LegOrderEntryStruct[] legEntryDetails) throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
            NotAcceptedException, TransactionFailedException
    {
        /* Order Entry method in TraderAPIImpl should be moved here */
        throw new UnsupportedOperationException();
    }

    @Override
    public void acceptStrategyOrderUpdateRequest(int currentRemainingQuantity, OrderEntryStruct updateOrder, LegOrderEntryStruct[] legEntryDetails)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException,
            TransactionFailedException
    {
        /* Order Entry method in TraderAPIImpl should be moved here */
        throw new UnsupportedOperationException();
    }

}
