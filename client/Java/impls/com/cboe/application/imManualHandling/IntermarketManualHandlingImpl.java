
package com.cboe.application.imManualHandling;

import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.ProductQueryServiceAdapter;
import com.cboe.interfaces.application.UserSessionLogoutCollector;
import com.cboe.interfaces.application.SessionManager;
import com.cboe.interfaces.application.IntermarketManualHandling;
import com.cboe.interfaces.businessServices.NBBOAgentService;
import com.cboe.interfaces.businessServices.OrderHandlingService;
import com.cboe.interfaces.externalIntegrationServices.IntermarketControlService;
import com.cboe.application.shared.consumer.UserSessionLogoutProcessor;
import com.cboe.application.shared.consumer.UserSessionLogoutProcessorFactory;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.ExceptionBuilder;
import com.cboe.domain.logout.LogoutServiceFactory;
import com.cboe.domain.util.OrderStructBuilder;
import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiOrder.OrderEntryStruct;
import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.idl.cmiUtil.CboeIdStruct;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiUser.ExchangeAcronymStruct;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.idl.cmiIntermarketMessages.*;
import com.cboe.idl.cmiConstants.StatusUpdateReasons;
import com.cboe.exceptions.*;

public class IntermarketManualHandlingImpl extends BObject
         implements UserSessionLogoutCollector, IntermarketManualHandling
{
    private SessionManager sessionManager                = null;
    private NBBOAgentService nbboAgentService            = null;
    private OrderHandlingService orderHandlingService    = null;
    private IntermarketControlService intermarketService = null;
    private UserSessionLogoutProcessor logoutProcessor   = null;
    private String  thisUserId;
    private ProductQueryServiceAdapter pqsAdapter = null;

    public IntermarketManualHandlingImpl()
    {
        super();
    }

    public void initialize()
    {
    }

    public void setSessionManager(SessionManager sessionMgr)
    {
        if (sessionMgr != null)
        {
            sessionManager = sessionMgr;
            logoutProcessor = UserSessionLogoutProcessorFactory.create(this);
            EventChannelAdapterFactory.find().addChannelListener(this, logoutProcessor, sessionMgr);
            LogoutServiceFactory.find().addLogoutListener(sessionMgr, this);
            try
            {
                thisUserId = sessionMgr.getValidUser().userId;
            }
            catch(org.omg.CORBA.UserException e)
            {
                Log.exception(this, "session : " + sessionManager, e);
            }
        }
    }

    public void acceptUserSessionLogout()
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptUserSessionLogout for " + sessionManager);
        }
        EventChannelAdapterFactory.find().removeListenerGroup(this);
        LogoutServiceFactory.find().logoutComplete(sessionManager, this);
        logoutProcessor.setParent(null);
        logoutProcessor = null;

        // service clean up
        sessionManager = null;
        thisUserId = null;
        nbboAgentService = null;
    }

    public void acceptHeldOrderReroute(OrderIdStruct heldOrderId,
                                 String session,
                                 int productKey,
                                 boolean nbboProtectionFlag)
        throws SystemException, CommunicationException, AuthorizationException,DataValidationException, TransactionFailedException,NotAcceptedException
    {
        getNBBOAgentService().rerouteHeldOrder(session,  productKey, thisUserId, heldOrderId, nbboProtectionFlag);
    }

    public void acceptHeldOrderByClassReroute(int classKey, String session, boolean nbboProtectionFlag)
        throws SystemException, CommunicationException, AuthorizationException,DataValidationException, TransactionFailedException,NotAcceptedException
    {
        getNBBOAgentService().rerouteHeldOrdersByClass(session, classKey, thisUserId, nbboProtectionFlag);
    }

    public void acceptHeldOrderFill(OrderIdStruct orderId, String sessionName, OrderEntryStruct nbboAgentOrder)
        throws SystemException, CommunicationException, AuthorizationException,DataValidationException, TransactionFailedException,NotAcceptedException
    {
        OrderStruct order = buildOrder(nbboAgentOrder);
        getNBBOAgentService().acceptFillHeldOrder(sessionName, thisUserId, orderId, order);
    }

    public void acceptCancelResponse(OrderIdStruct orderId, CboeIdStruct cancelRequestId, String sessionName, int productKey )
            throws SystemException, CommunicationException, AuthorizationException,DataValidationException, TransactionFailedException,NotAcceptedException
    {
        getNBBOAgentService().acceptCancelResponse(sessionName, productKey, thisUserId, orderId, cancelRequestId);
    }

    private OrderStruct buildOrder(OrderEntryStruct orderEntry)
        throws SystemException, CommunicationException, DataValidationException, AuthorizationException
     {
        try
        {
            ProductStruct product = getProductQueryServiceAdapter().getProductByKey(orderEntry.productKey);

            ExchangeAcronymStruct userAcronym = sessionManager.getValidUser().userAcronym;

            return OrderStructBuilder.buildOrderStruct(orderEntry, product.productKeys, thisUserId, userAcronym);
        }
        catch(NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    private NBBOAgentService getNBBOAgentService()
    {
        if ( nbboAgentService == null )
        {
            nbboAgentService = ServicesHelper.getNBBOAgentService();
        }

        return nbboAgentService;
    }

    private OrderHandlingService getOrderHandlingService()
    {
        if ( orderHandlingService == null )
        {
            orderHandlingService = ServicesHelper.getOrderHandlingService();
        }
        return orderHandlingService;
    }

    private IntermarketControlService getIntermarketService()
    {
        if ( intermarketService == null )
        {
            intermarketService = ServicesHelper.getIntermarketControlService();
        }
        return intermarketService;
    }


    public HeldOrderDetailStruct getHeldOrderById( String sessionName, int productKey, OrderIdStruct orderId )
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        HeldOrderStruct heldOrder = getNBBOAgentService().getHeldOrderById(thisUserId,sessionName,productKey, orderId);
        HeldOrderDetailStruct heldOrderDetail = buildHeldOrderDetailStruct(heldOrder, StatusUpdateReasons.QUERY);
        return heldOrderDetail;
    }

    private HeldOrderDetailStruct buildHeldOrderDetailStruct( HeldOrderStruct heldOrder, short statusUpdateReason)
    {
        ProductNameStruct productName = null;
        try {
            productName = sessionManager.getProductQuery().getProductByKey(heldOrder.order.productKey).productName;
        }
        catch (org.omg.CORBA.UserException e) {
            Log.exception(this, "session : " + sessionManager, e);
            return null;
        }

        return new HeldOrderDetailStruct( productName, statusUpdateReason, heldOrder);
    }


    public void acceptSatisfactionOrderFill(String session,
                                            OrderIdStruct satisfactionOrderId,
                                            OrderEntryStruct nbboAgentOrder,
                                            int crowdQuantity,
                                            boolean cancelRemaining,
                                            short resolution)
      throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException, NotAcceptedException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptSatisfactionOrderFill for " + session + "SOrderId="+ satisfactionOrderId.highCboeId+ " " + satisfactionOrderId.lowCboeId);
        }
        OrderStruct order = buildOrder(nbboAgentOrder);
        getNBBOAgentService().acceptSatisfactionOrderFill(session, thisUserId, satisfactionOrderId, order, crowdQuantity, cancelRemaining, resolution);
    }

    public void acceptSatisfactionOrderInCrowdFill(String session,
                                                   int productKey,
                                                   OrderIdStruct satisfactionOrderId,
                                                   int crowdQuantity,
                                                   boolean cancelRemaining,
                                                   short resolution)
      throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException, NotAcceptedException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptSatisfactionOrderInCrowdFill for " + session + "SOrderId="+ satisfactionOrderId.highCboeId+ " " + satisfactionOrderId.lowCboeId);
        }
        getNBBOAgentService().acceptSatisfactionOrderInCrowdFill(session, productKey, thisUserId, satisfactionOrderId, crowdQuantity, cancelRemaining, resolution);
    }

    public void acceptSatisfactionOrderReject(String session, int productKey, OrderIdStruct satisfactionOrderId, short resolution)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException, NotAcceptedException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptSatisfactionOrderReject for " + session + "SOrderId="+ satisfactionOrderId.highCboeId+ " " + satisfactionOrderId.lowCboeId);
        }
        getNBBOAgentService().acceptSatisfactionOrderReject(session, productKey, thisUserId, satisfactionOrderId, resolution);
    }


    public void acceptCustomerOrderSatisfy(String session, OrderIdStruct satisfactionOrderId, OrderEntryStruct nbboAgentOrder)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException, NotAcceptedException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptCustomerOrderSatisfy for " + session + "SOrderId="+ satisfactionOrderId.highCboeId+ " " + satisfactionOrderId.lowCboeId);
        }
        OrderStruct order = buildOrder(nbboAgentOrder);
        getNBBOAgentService().acceptCustomerOrderSatisfy(session, thisUserId, satisfactionOrderId, order);
    }


    public void acceptFillReject(String session, int productKey, com.cboe.idl.cmiIntermarketMessages.FillRejectRequestStruct fillRejectRequest)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException, NotAcceptedException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptFillReject for " + session );
        }
        getNBBOAgentService().rejectFill(session, productKey, thisUserId, fillRejectRequest);
    }

    public OrderStruct[] getAssociatedOrders(String session, int productKey, OrderIdStruct orderId)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling getAssociatedOrders for " + session + "productKey=" + productKey + orderId.highCboeId+ " " + orderId.lowCboeId);
        }
        return getOrderHandlingService().getAssociatedOrders(thisUserId, session, productKey, orderId);
    }

    public OrderStruct[] getOrdersByOrderTypeAndClass(String session, int classKey, String[] exchanges, char[] orderTypes, short orderFlowDirection)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling getOrdersByOrderTypeAndClass for " + session + "classKey=" + classKey );
        }
        return getOrderHandlingService().getOrdersByOrderTypeAndClass(session, classKey, thisUserId, exchanges, orderTypes, orderFlowDirection);
     }

    public OrderStruct[] getOrdersByOrderTypeAndProduct(String session, int productKey, String[] exchanges, char[] orderTypes, short orderFlowDirection)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling getOrdersByOrderTypeAndProduct for " + session + "productKey=" + productKey );
        }
        return getOrderHandlingService().getOrdersByOrderTypeAndProduct(session, productKey, thisUserId, exchanges, orderTypes, orderFlowDirection);
    }

    public void acceptAdminMessage(String session,
          String destinationExchange,
          int productKey,
          AdminStruct adminMessage
          )
            throws SystemException, CommunicationException, DataValidationException, NotAcceptedException, AuthorizationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptAdminMessage for " + session + "productKey=" + productKey );
        }
        getIntermarketService().acceptAdminMessage(session, destinationExchange, productKey, adminMessage, thisUserId );
    }

    public void acceptPreOpeningIndication(String session, String originatingExchange, int productKey, PreOpeningIndicationPriceStruct preOpenIndication)
            throws SystemException, CommunicationException, DataValidationException, NotAcceptedException, AuthorizationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptPreOpeningIndication for " + session + "productKey=" + productKey );
        }
        getIntermarketService().acceptPreOpeningIndication(session, originatingExchange, productKey, preOpenIndication, thisUserId);
    }

    public void acceptPreOpeningResponse(String session, String originatingExchange, String destinationExchange, int productKey, PreOpeningResponsePriceStruct[] preOpeningResponse)
            throws SystemException, CommunicationException, DataValidationException, NotAcceptedException, AuthorizationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptPreOpeningResponse for " + session + "productKey=" + productKey );
        }
        getIntermarketService().acceptPreOpeningResponse(session, originatingExchange, destinationExchange, productKey, thisUserId, preOpeningResponse);
    }

    public boolean lockProduct(String session, int productKey)
            throws SystemException, CommunicationException, DataValidationException, NotAcceptedException, AuthorizationException, NotFoundException
    {
        String nbboAgentId = thisUserId;
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling lockProduct for " + session + "productKey=" + productKey );
        }
        return getNBBOAgentService().lockProduct(session, productKey,nbboAgentId);
    }

    public boolean unlockProduct(String session, int productKey)
            throws SystemException, CommunicationException, DataValidationException, NotAcceptedException, AuthorizationException, NotFoundException
    {
        String nbboAgentId = thisUserId;
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unlockProduct for " + session + "productKey=" + productKey );
        }
        return getNBBOAgentService().unlockProduct(session, productKey,nbboAgentId);
    }

    public boolean rerouteBookedOrderToHeldOrder(OrderIdStruct bookedOrderId, String session, int productKey, boolean nbboProtectionFlag)
            throws SystemException, CommunicationException, DataValidationException, NotAcceptedException, AuthorizationException, TransactionFailedException
    {
        String nbboAgentId = thisUserId;
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling rerouteBookedOrderToHeldOrder for " + session + "productKey=" + productKey );
        }
        return getNBBOAgentService().rerouteBookedOrderToHeldOrder(bookedOrderId, session, productKey, nbboProtectionFlag,nbboAgentId);
    }

    public void acceptOpeningPriceForProduct(PriceStruct openingPrice, String session, int productKey)
            throws SystemException, CommunicationException, DataValidationException, NotAcceptedException, AuthorizationException, TransactionFailedException
    {
        String nbboAgentId = thisUserId;
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptOpeningPriceForProduct for " + session + "productKey=" + productKey );
        }
        getNBBOAgentService().acceptOpeningPriceForProduct(openingPrice, session, productKey,nbboAgentId);
    }

    /**
     * Get the instance of the ProductQueryServiceAdapter.
     */
    private ProductQueryServiceAdapter getProductQueryServiceAdapter()
    {
        if (null == pqsAdapter)
        {
            pqsAdapter = ServicesHelper.getProductQueryServiceAdapter();
        }
        return pqsAdapter;
    }
}
