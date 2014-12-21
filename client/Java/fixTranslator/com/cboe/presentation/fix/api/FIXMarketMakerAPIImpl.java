//
// -----------------------------------------------------------------------------------
// Source file: FIXMarketMakerAPIImpl.java
//
// PACKAGE: com.cboe.presentation.fix.api;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.fix.api;

import com.cboe.exceptions.AlreadyExistsException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.idl.cmiAdmin.MessageStruct;
import com.cboe.idl.cmiCallback.CMIUserSessionAdmin;
import com.cboe.idl.cmiConstants.OrderCancelTypes;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.idl.cmiOrder.CancelRequestStruct;
import com.cboe.idl.cmiOrder.InternalizationOrderResultStruct;
import com.cboe.idl.cmiOrder.LegOrderEntryStruct;
import com.cboe.idl.cmiOrder.OrderDetailStruct;
import com.cboe.idl.cmiOrder.OrderEntryStruct;
import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiQuote.RFQEntryStruct;
import com.cboe.idl.cmiTraderActivity.ActivityHistoryStruct;
import com.cboe.idl.cmiUser.AccountStruct;
import com.cboe.idl.cmiUser.SessionProfileStruct;
import com.cboe.idl.cmiUser.SessionProfileUserStruct;
import com.cboe.idl.cmiV3.UserSessionManagerV3;
import com.cboe.idl.cmiV9.UserSessionManagerV9;
import com.cboe.interfaces.presentation.api.FIXMarketMakerAPI;
import com.cboe.interfaces.presentation.common.formatters.OperationResultFormatStrategy;
import com.cboe.interfaces.presentation.user.UserStructModel;
import com.cboe.presentation.api.MarketMakerAPIImpl;
import com.cboe.presentation.common.formatters.FormatFactory;
import com.cboe.presentation.common.formatters.MatchOrderTypes;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.user.UserStructModelImpl;
import com.cboe.util.ExceptionBuilder;
import com.cboe.util.event.EventChannelListener;

/**
 * Implementation of the FIXMarketMakerAPI to the FIX/Appia engine.
 * extends MarketMakerAPIImpl -->  TraderAPIImpl
 */
public class FIXMarketMakerAPIImpl extends MarketMakerAPIImpl implements FIXMarketMakerAPI
{
    // protected FixUserSessionManager fixSessionManager;
    protected UserSessionManagerV3 fixSessionManager;

    public FIXMarketMakerAPIImpl( UserSessionManagerV9 sessionMgr, UserSessionManagerV3 fixSessionMgrV3, CMIUserSessionAdmin userListener, EventChannelListener clientListener, boolean gmd)
    {
        super(sessionMgr, userListener, clientListener, gmd);

        setFIXSessionManager(fixSessionMgrV3);
    }

    /**
     * Obtain the reference to the the Validated User
     * @return Validated User Information
     */
    public UserStructModel getValidFIXUser()
            throws SystemException, CommunicationException, AuthorizationException
    {
    	SessionProfileUserStruct fixProfile = fixSessionManager.getValidSessionProfileUser();
     	
    	/**
    	 * For back compatibility with FIXCAS that doesn't return user profile:
    	 * Create profile from profile of CMi session.
    	 */
    	if (fixProfile.userId == null || fixProfile.userId.length() == 0) {
    		fixProfile.userId = super.getValidUser().getUserStruct().userId +"_FIX";
    		fixProfile.accounts = new AccountStruct[1];
    		fixProfile.accounts[0] = 
    			new AccountStruct(super.getValidUser().getUserStruct().userId+"_FIX", 
    				super.getValidUser().getUserStruct().defaultProfile.executingGiveupFirm);
    		fixProfile.defaultProfile = new SessionProfileStruct();
    		fixProfile.defaultProfile.account = fixProfile.accounts[0].account;
    		fixProfile.defaultProfile.executingGiveupFirm = 
    			fixProfile.accounts[0].executingGiveupFirm;

    	}
        return new UserStructModelImpl(fixProfile);
    }


    // todo -----------------------------------------------------------------------------------------------------------
    // Subscribe/Unsubscribe start
    // todo -----------------------------------------------------------------------------------------------------------
    /**
     * Subscribes an EventChannelListener to receive events for the given session and class. This method returns all of
     * the current Auctions for the given criteria.
     *
     * @param sessionName
     * @param classKey
     */
    /*
    public void subscribeAuctionForClass(String sessionName, int classKey, short[] auctionTypes, EventChannelListener listener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.AUCTION))
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = auctionTypes;

            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeAuctionForClass", GUILoggerBusinessProperty.AUCTION, argObj);
        }

        // subscribe one by one
        int length = auctionTypes.length;
        AuctionSubscriptionResultStruct[][] results = new AuctionSubscriptionResultStruct[length][length];
        for (int i=0; i<length; i++)
        {
            // todo - send fix message here
            ChannelKey key = new ChannelKey(ChannelType.CB_AUCTION, new AuctionTypeContainer(sessionName, classKey, auctionTypes[i]));
            if (SubscriptionManagerFactory.find().subscribe(key, listener, auctionConsumer) == 1)
            {
                short[] auctionType = new short[1];
                auctionType[0] = auctionTypes[i];
                results[i] = orderQueryV3.subscribeAuctionForClass(sessionName, classKey, auctionType, auctionConsumer);
            }
        }

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.AUCTION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeAuctionForClass()-Completed...", GUILoggerBusinessProperty.AUCTION, results);
        }

        checkAuctionSubscriptionResults(results);
    }

    */

    /**
     * Unsubscribes an EventChannelListener for the given session and class.
     *
     * @param sessionName
     * @param classKey
     */

    /*
    public void unsubscribeAuctionForClass(String sessionName, int classKey, short[] auctionTypes, EventChannelListener listener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.AUCTION))
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = auctionTypes;

            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeAuctionForClass", GUILoggerBusinessProperty.AUCTION, argObj);
        }

        int length = auctionTypes.length;
        AuctionSubscriptionResultStruct[][] results = new AuctionSubscriptionResultStruct[length][length];
        for (int i=0; i<length; i++)
        {
            ChannelKey key = new ChannelKey(ChannelType.CB_AUCTION, new AuctionTypeContainer(sessionName, classKey, auctionTypes[i]));
            if (SubscriptionManagerFactory.find().unsubscribe(key, listener, auctionConsumer) == 0)
            {
                short[] auctionType = new short[1];
                auctionType[0] = auctionTypes[i];
                results[i] = orderQueryV3.unsubscribeAuctionForClass(sessionName, classKey, auctionType, auctionConsumer);
            }
        }

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.AUCTION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeAuctionForClass()-Completed...", GUILoggerBusinessProperty.AUCTION, results);
        }

        checkAuctionSubscriptionResults(results);
    }

    */

    // todo -----------------------------------------------------------------------------------------------------------
    // Subscribe/Unsubscribe end
    // todo -----------------------------------------------------------------------------------------------------------


    public InternalizationOrderResultStruct acceptInternalizationOrder(OrderEntryStruct primaryOrderEntry,
                                                                OrderEntryStruct matchedOrderEntry, short matchOrderType)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   NotAcceptedException, TransactionFailedException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.ORDER_ENTRY))
        {
            Object[] argObj = new Object[3];
            argObj[0] = primaryOrderEntry;
            argObj[1] = matchedOrderEntry;
            argObj[2] = MatchOrderTypes.toString(matchOrderType);
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": acceptInternalizationOrder", GUILoggerBusinessProperty.ORDER_ENTRY, argObj);
        }
        // todo -
        InternalizationOrderResultStruct results = fixSessionManager.getOrderEntryV3().acceptInternalizationOrder(primaryOrderEntry, matchedOrderEntry, matchOrderType);
        checkInternalizationOrderResults(results);

        return results;
    }

    private void checkInternalizationOrderResults(InternalizationOrderResultStruct results) throws DataValidationException
    {
        if (results != null && results.primaryOrderResult != null && results.matchOrderResult != null)
        {
            if (results.primaryOrderResult.result.errorCode != 0 || results.matchOrderResult.result.errorCode != 0)
            {
                OperationResultFormatStrategy formatter = FormatFactory.getOperationResultFormatStrategy();

                StringBuffer textBuffer = new StringBuffer(255);
                if (results.primaryOrderResult.result.errorCode != 0)
                {
                     textBuffer.append("Primary: ");
                     textBuffer.append(formatter.format(results.primaryOrderResult.result));
                     textBuffer.append('\n');
                }

                if (results.matchOrderResult.result.errorCode != 0)
                {
                    textBuffer.append("Match: ");
                    textBuffer.append(formatter.format(results.matchOrderResult.result));
                }

                throw ExceptionBuilder.dataValidationException(textBuffer.toString(), 0);
            }
        }
        else
        {
            GUILoggerHome.find().alarm("The InternalizationOrderResultStruct is null or internal structs are null...");
        }
    }

    public OrderIdStruct acceptOrder(OrderEntryStruct anOrder)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException, AlreadyExistsException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(FIX_TRANSLATOR_NAME+": acceptOrder", GUILoggerBusinessProperty.ORDER_ENTRY, anOrder);
        }
        OrderIdStruct idStruct = fixSessionManager.getOrderEntry().acceptOrder(anOrder);
        return idStruct;
    }

    public OrderIdStruct acceptOrderByProductName(ProductNameStruct productName,
            OrderEntryStruct anOrder) throws SystemException,
            CommunicationException, AuthorizationException,
            DataValidationException, NotAcceptedException,
            TransactionFailedException, AlreadyExistsException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = productName;
            argObj[1] = anOrder;

            GUILoggerHome.find().debug(FIX_TRANSLATOR_NAME+": acceptOrderByProductName", GUILoggerBusinessProperty.ORDER_ENTRY, argObj);
        }

        return( fixSessionManager.getOrderEntry().acceptOrderByProductName(productName, anOrder) );
    }

    /**
     * @description
     * Request cancellation and replacement of part or all of an order
     *
     * @usage
     * Must be for a valid quantity and an existing order
     *
     * @param orderId identifier of an existing order
     * @param originalOrderRemainingQuantity The original remaining unfilled quantity of an order
     * @param newOrder the new order
     * @returns OrderIdStruct for new order
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotAcceptedException
     * @exception TransactionFailedException
     */
    public OrderIdStruct acceptOrderCancelReplaceRequest(OrderIdStruct orderId, int originalOrderRemainingQuantity, OrderEntryStruct newOrder)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {

        OrderDetailStruct order = null;
        try
        {
            order = getOrderById(orderId);
        }
        catch ( NotFoundException e )
        {
            throw ExceptionBuilder.dataValidationException("OrderId not found", DataValidationCodes.INVALID_ORDER_ID );
        }

        CancelRequestStruct aCancelRequestStruct = new CancelRequestStruct(orderId, order.orderStruct.activeSession, "", OrderCancelTypes.DESIRED_CANCEL_QUANTITY , originalOrderRemainingQuantity);

        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = aCancelRequestStruct;
            argObj[1] = newOrder;
            GUILoggerHome.find().debug(FIX_TRANSLATOR_NAME+": acceptOrderCancelReplaceRequest", GUILoggerBusinessProperty.ORDER_ENTRY, argObj);
        }

        return( fixSessionManager.getOrderEntry().acceptOrderCancelReplaceRequest(aCancelRequestStruct, newOrder) );
    }

    /**
     * @description
     * Request cancellation of part or all of an order
     *
     * @usage
     * Must be for a valid quantity and an existing order
     *
     * @param cancelRequest CancelRequestStruct
     * @returns void
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotAcceptedException
     * @exception TransactionFailedException
     */
    public void acceptOrderCancelRequest(CancelRequestStruct cancelRequest)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(FIX_TRANSLATOR_NAME+": acceptOrderCancelRequest", GUILoggerBusinessProperty.ORDER_ENTRY, cancelRequest);
        }
        fixSessionManager.getOrderEntry().acceptOrderCancelRequest(cancelRequest);
    }

    /**
     * @description
     * Request update of part or all of an order
     *
     * @usage
     * Must be for a valid quantity and an existing order
     *
     * @param currentRemainingQuantity The current remaining unfilled quantity of an order
     * @param updatedOrder Updated order
     * @returns void
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotAcceptedException
     * @exception TransactionFailedException
     */
    public void acceptOrderUpdateRequest(int currentRemainingQuantity, OrderEntryStruct updatedOrder)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = new Integer(currentRemainingQuantity);
            argObj[1] = updatedOrder;
            GUILoggerHome.find().debug(FIX_TRANSLATOR_NAME+": acceptOrderUpdateRequest", GUILoggerBusinessProperty.ORDER_ENTRY, argObj);
        }
        fixSessionManager.getOrderEntry().acceptOrderUpdateRequest(currentRemainingQuantity, updatedOrder);
    }

    public void acceptRequestForQuote(RFQEntryStruct rfq)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(FIX_TRANSLATOR_NAME+": acceptRequestForQuote", GUILoggerBusinessProperty.RFQ, rfq);
        }
        fixSessionManager.getOrderEntry().acceptRequestForQuote(rfq);
    }

    public OrderIdStruct acceptStrategyOrder(OrderEntryStruct anOrder,
			LegOrderEntryStruct[] legEntryDetails) throws SystemException,
			CommunicationException, AuthorizationException,
			DataValidationException, NotAcceptedException,
			TransactionFailedException, AlreadyExistsException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = anOrder;
            argObj[1] = legEntryDetails;
            GUILoggerHome.find().debug(FIX_TRANSLATOR_NAME+": acceptStrategyOrder", GUILoggerBusinessProperty.ORDER_ENTRY, argObj);
        }
        return( fixSessionManager.getOrderEntry().acceptStrategyOrder(anOrder, legEntryDetails) );
    }

    public OrderIdStruct acceptStrategyOrderCancelReplaceRequest(OrderIdStruct orderId, int cancelQuantity, OrderEntryStruct newOrder, LegOrderEntryStruct[] legEntryDetails)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        OrderDetailStruct order = null;
        try
        {
            order = getOrderById(orderId);
        }
        catch ( NotFoundException e )
        {
            throw ExceptionBuilder.dataValidationException("OrderId not found", DataValidationCodes.INVALID_ORDER_ID );
        }

        CancelRequestStruct aCancelRequestStruct = new CancelRequestStruct(orderId, order.orderStruct.activeSession, "", OrderCancelTypes.DESIRED_CANCEL_QUANTITY , cancelQuantity);

        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(FIX_TRANSLATOR_NAME+": acceptStrategyOrderCancelReplaceRequest", GUILoggerBusinessProperty.ORDER_ENTRY, aCancelRequestStruct);
        }
        return( fixSessionManager.getOrderEntry().acceptStrategyOrderCancelReplaceRequest(aCancelRequestStruct,  newOrder, legEntryDetails) );
    }

    /**
     * Gets the order history for the given order id.
     *
     * @return the order's history.
     * @param orderId the order id to get historical information for.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public ActivityHistoryStruct queryOrderHistory(OrderIdStruct orderId)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(FIX_TRANSLATOR_NAME+": queryOrderHistory", GUILoggerBusinessProperty.ORDER_QUERY, orderId);
        }
        return fixSessionManager.getOrderQuery().queryOrderHistory( orderId );
    }
    
    public void logout()
           throws SystemException, CommunicationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(FIX_TRANSLATOR_NAME+": logout", GUILoggerBusinessProperty.USER_SESSION, "");
        }
        fixSessionManager.logout();
//        FIXUserAccessFactory.unregisterClientListener( userClientListener );
        super.logout();
    }

    protected void setFIXSessionManager(UserSessionManagerV3 sessionManagerV3)
    {
        this.fixSessionManager = sessionManagerV3;
    }
    
    ////////////////////////// begin TextMessagingService ////////////////////////
    /**
     * Send a message to a user and/or group
     * @usage Send a message to a user and/or group
     * @returns sent message's messageId
     * @raises SystemException, CommunicationException, AuthorizationException, DataValidationException
     */
    public int sendMessage(MessageStruct message)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": sendMessage", GUILoggerBusinessProperty.TEXT_MESSAGE, message);
        }
        return fixSessionManager.getAdministrator().sendMessage(message);
    }
    ////////////////////////// end of TextMessagingService ////////////////////////


/*
//todo: may need to override these...

    protected void cleanupCallbackConsumers()
    {

        super.cleanupCallbackConsumers();
    }

    protected void cleanupCallbackV2Consumers()
    {

        super.cleanupCallbackV2Consumers();
    }

    protected void cleanupSessionManagerInterfaces()
    {

        super.cleanupSessionManagerInterfaces();
    }

    protected void cleanupSessionManagerV2Interfaces()
    {

        super.cleanupSessionManagerV2Interfaces();
    }

    protected void initializeCallbackConsumers()
    {
        super.initializeCallbackConsumers();

    }

    protected void initializeCallbackV2Consumers()
    {
        super.initializeCallbackV2Consumers();

    }

    protected void initializeCallbackV3Consumers()
    {
        super.initializeCallbackV3Consumers();

    }

    protected void initializeSessionManagerInterfaces() throws SystemException, CommunicationException, AuthorizationException
    {
        super.initializeSessionManagerInterfaces();

    }

    protected void initializeSessionManagerV2Interfaces() throws SystemException, CommunicationException, AuthorizationException
    {
        super.initializeSessionManagerV2Interfaces();

    }

    protected void initializeSessionManagerV3Interfaces() throws SystemException, CommunicationException, AuthorizationException
    {
        super.initializeSessionManagerV3Interfaces();

    }
*/
}
