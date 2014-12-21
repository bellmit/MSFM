//
// -----------------------------------------------------------------------------------
// Source file: OrderManagementServiceImpl.java
//
// PACKAGE: com.cboe.application.orderManagement;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2011 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.application.parOrderManagementService;


import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.shared.consumer.OrderRoutingProcessor;
import com.cboe.application.shared.consumer.OrderRoutingProcessorFactory;
import com.cboe.application.supplier.OrderRoutingSupplier;
import com.cboe.application.supplier.OrderRoutingSupplierFactory;
import com.cboe.application.cas.UserLogonHelper;
import com.cboe.application.session.SessionManagerImpl;
import com.cboe.domain.iec.ClientIECFactory;
import com.cboe.domain.iec.ConcurrentEventChannelAdapter;
import com.cboe.domain.util.ReflectiveStructBuilder;
import com.cboe.domain.util.RoutingGroupParCancelReplaceStructSequenceContainer;
import com.cboe.domain.util.RoutingGroupParCancelRequestStructSequenceContainer;
import com.cboe.domain.util.RoutingGroupParOrderStructSequenceContainer;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.exceptions.AuthenticationException;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.idl.cmiOrder.CancelReportStruct;
import com.cboe.idl.cmiOrder.FilledReportStruct;
import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.idl.cmiUser.PreferenceStruct;
import com.cboe.idl.cmiUtil.CboeIdStruct;
import com.cboe.idl.ohsConsumers.OrderRoutingConsumer;
import com.cboe.idl.order.CancelReplaceRoutingStruct;
import com.cboe.idl.order.CancelReportDropCopyRoutingStruct;
import com.cboe.idl.order.CancelRoutingStruct;
import com.cboe.idl.order.FillReportDropCopyRoutingStruct;
import com.cboe.idl.order.FillReportRejectRoutingStruct;
import com.cboe.idl.order.LinkageCancelReportRoutingStruct;
import com.cboe.idl.order.LinkageExtensionsStruct;
import com.cboe.idl.order.LinkageFillReportRoutingStruct;
import com.cboe.idl.order.ManualCancelReplaceStruct;
import com.cboe.idl.order.ManualCancelReportStruct;
import com.cboe.idl.order.ManualCancelRequestStruct;
import com.cboe.idl.order.ManualCancelRequestStructV2;
import com.cboe.idl.order.ManualFillStruct;
import com.cboe.idl.order.ManualFillStructV2;
import com.cboe.idl.order.ManualFillTimeoutRoutingStruct;
import com.cboe.idl.order.ManualMarketBrokerDataStruct;
import com.cboe.idl.order.ManualOrderTimeoutRoutingStruct;
import com.cboe.idl.order.OrderHandlingInstructionStruct;
import com.cboe.idl.order.OrderIdRoutingStruct;
import com.cboe.idl.order.OrderLocationServerResponseStruct;
import com.cboe.idl.order.OrderLocationSummaryServerResponseStruct;
import com.cboe.idl.order.OrderManualHandlingStructV2;
import com.cboe.idl.order.OrderRoutingParameterStruct;
import com.cboe.idl.order.OrderRoutingStruct;
import com.cboe.idl.order.TradeNotificationRoutingStruct;
import com.cboe.idl.util.RoutingParameterV2Struct;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.securityService.SecurityService;
import com.cboe.infrastructureServices.sessionManagementService.SessionManagementService;
import com.cboe.interfaces.application.OrderRoutingCollector;
import com.cboe.interfaces.application.ParOrderManagementService;
import com.cboe.interfaces.application.ParOrderManagementServiceHome;
import com.cboe.interfaces.application.SessionManager;
import com.cboe.interfaces.businessServices.OrderHandlingService;
import com.cboe.interfaces.businessServices.UserService;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.interfaces.ohsEvents.IECOrderRoutingConsumerHome;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.ExceptionBuilder;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.channel.ChannelListener;
import org.omg.CORBA.UserException;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import com.cboe.interfaces.infrastructureServices.SecurityAdminService;
import com.cboe.interfaces.infrastructureServices.SecurityAdminServiceHome;
import com.cboe.idl.infrastructureServices.securityService.securityAdmin.MemberAccountStruct;


@SuppressWarnings({"ObjectToString"})
public class ParOrderManagementServiceImpl
        extends BObject
        implements ParOrderManagementService, OrderRoutingCollector
{
    private static String createName = "ParOrderManagementServiceHomeImpl";
    protected ParOrderManagementService parOrderManagementService;
    protected OrderHandlingService orderHandlingService;   
    private SecurityAdminService securityAdminService;
    private OrderRoutingSupplier orderRoutingSupplier = null;
    private OrderRoutingProcessor orderRoutingProcessor = null;
    private ConcurrentEventChannelAdapter internalEventChannel;
    private UserService userService;   
    protected SessionManager currentSession;    
    protected String destination;
    protected String userId;      
    protected HashMap<String, String> parUsersMap = new HashMap<String, String>();
    private List subcribedUser = new ArrayList();
    
    @SuppressWarnings({"ThisEscapedInObjectConstruction"})
    public ParOrderManagementServiceImpl(SessionManager sessionManager)
    {
        super();
        try
        {
            internalEventChannel = ClientIECFactory.findConcurrentEventChannelAdapter(ClientIECFactory.CAS_IEC);
        }
        catch (Exception e)
        {
            Log.exception("Exception getting CAS_IEC!", e);
        }

        currentSession = sessionManager;

        try
        {
            userId = sessionManager.getValidUser().userId;


        }
        catch (UserException e)
        {
            Log.exception(this, "session : " + sessionManager, e);
        }
    }

    /**
     * Creates an valid instance of the service.
     *
     * @param name name of this object
     */

    public void create(String name)
    {
        super.create(name);
        getParOrderManagementService();
        orderHandlingService = ServicesHelper.getOrderHandlingService();
        orderRoutingSupplier = OrderRoutingSupplierFactory.create(currentSession);
        orderRoutingSupplier.setDynamicChannels(true);
        orderRoutingProcessor = OrderRoutingProcessorFactory.create(this);

    }

    /**
     * Creates an valid instance of the service.
     * <p/>
     * note: this should never get called as the homes usually
     * call bo.create(name)...
     */
    public void create()
    {
        create(createName);
    }

    /**
     * Retrieves the ParOrderManagementService
     */
    public ParOrderManagementService getParOrderManagementService()
    {
        if (parOrderManagementService == null)
        {
            try
            {
                ParOrderManagementServiceHome home = (ParOrderManagementServiceHome) HomeFactory.getInstance().findHome(ParOrderManagementServiceHome.HOME_NAME);
                parOrderManagementService = home.find();
            }
            catch (CBOELoggableException e)
            {
                throw new NullPointerException("Could not find ParOrderManagementServiceHome");
            }
        }
        return parOrderManagementService;
    }


    public BaseSessionManager getSessionManager()
    {
        return currentSession;
    }
    

    public void subscribeParOrdersForManualHandling(OrderRoutingConsumer parOrderManagementConsumer,
                                                    boolean gmdCallback)
            throws SystemException, CommunicationException, AuthorizationException
    {
        boolean sub = false;
        //This subcribedUser list is prevent par for resubscribe while cas still working on apply filter
        synchronized(subcribedUser)
        {
            if ((subcribedUser.isEmpty())||(!subcribedUser.contains(userId)))
            {
                subcribedUser.add(userId);
                sub = true;
                
            }
        }    
        
        if (sub)
        {
                Log.information(this, "calling subscribeParOrdersForManualHandling for " + currentSession +
                            " gmd:" + gmdCallback);
                if (parOrderManagementConsumer != null)
                {
                    try
                    {  
                        ChannelListener proxyListener =
                                ServicesHelper.getOrderRoutingConsumerProxy(parOrderManagementConsumer,
                                                                            currentSession, gmdCallback);
                        ChannelKey channelKey;
        
                        IECOrderRoutingConsumerHome consumerHome =
                                ServicesHelper.getOrderRoutingConsumerHome();
        
                        channelKey = new ChannelKey(ChannelType.CB_PAR_ORDER_ACCEPTED, currentSession);
                        orderRoutingSupplier.addChannelListener(this, proxyListener, channelKey);
        
                        channelKey = new ChannelKey(ChannelType.PAR_ORDER_ACCEPTED, userId);
                        internalEventChannel.addChannelListener(this, orderRoutingProcessor, channelKey);
                        consumerHome.addFilter(channelKey);
        
                        channelKey = new ChannelKey(ChannelType.CB_PAR_ORDER_CANCELED, currentSession);
                        orderRoutingSupplier.addChannelListener(this, proxyListener, channelKey);
        
                        channelKey = new ChannelKey(ChannelType.PAR_ORDER_CANCELED, userId);
                        internalEventChannel
                                .addChannelListener(this, orderRoutingProcessor, channelKey);
                        consumerHome.addFilter(channelKey);
        
                        channelKey = new ChannelKey(ChannelType.CB_PAR_ORDER_CANCEL_REPLACED, currentSession);
                        orderRoutingSupplier.addChannelListener(this, proxyListener, channelKey);
        
                        channelKey = new ChannelKey(ChannelType.PAR_ORDER_CANCEL_REPLACED, userId);
                        internalEventChannel
                                .addChannelListener(this, orderRoutingProcessor, channelKey);
                        consumerHome.addFilter(channelKey);
        
        
                    }
                    catch (DataValidationException e)
                    {
                        Log.exception(this, "Error adding channel listeners for ParOrderRoutingConsumer.", e);
                    }
                }
                else
                {
                    Log.alarm(this, "null ParOrderRoutingConsumer in subscribeParOrdersForManualHandling " +
                            currentSession);
                }
                Log.information(this, "finish calling subscribeParOrdersForManualHandling for " + currentSession);
         }
    }

    public void unSubscribeParOrdersForManualHandling()
            throws SystemException, CommunicationException, AuthorizationException
    {
        boolean unSub = false;
        synchronized(subcribedUser)
        {
            if (subcribedUser.contains(userId))
            {
                subcribedUser.remove(userId);
                unSub = true;
                
            }
        }    
        
        if (unSub)
        {
            Log.information(this, "calling unSubscribeParOrdersForManualHandling for " + currentSession);
            
            try
            {
    
                ChannelKey channelKey;
                IECOrderRoutingConsumerHome consumerHome = ServicesHelper.getOrderRoutingConsumerHome();
    
                channelKey = new ChannelKey(ChannelType.PAR_ORDER_ACCEPTED, userId);
                internalEventChannel.removeChannelListener(this, orderRoutingProcessor, channelKey);
                consumerHome.removeFilter(channelKey);
    
                channelKey = new ChannelKey(ChannelType.PAR_ORDER_CANCELED, userId);
                internalEventChannel.removeChannelListener(this, orderRoutingProcessor, channelKey);
                consumerHome.removeFilter(channelKey);
    
                channelKey = new ChannelKey(ChannelType.PAR_ORDER_CANCEL_REPLACED, userId);
                internalEventChannel.removeChannelListener(this, orderRoutingProcessor, channelKey);
                consumerHome.removeFilter(channelKey);
    
    
            }
            catch (Exception e)
            {
                Log.exception(this, "Error removing filters for  Par OrderRoutingConsumer.", e);
            }
            
            
            Log.information(this, "finish calling unSubscribeParOrdersForManualHandling for " + currentSession);
            
        }
    }

    public void publishAllParMessagesForDestination()
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
  
        Log.information(this, "calling publishAllParMessagesForDestination for: " + userId);
        orderHandlingService.publishAllMessagesForDestination(userId);
        
        if (Log.isDebugOn())
        {
            Log.debug(this, "finish calling publishAllParMessagesForDestination for: " + userId);
        }

    }

    public void registerUser(String registeredUserId, String password)
    throws SystemException,
           CommunicationException,
           DataValidationException,
           AuthorizationException,
           AuthenticationException,
           NotFoundException
    {
        Log.information("calling registerUser for: "+registeredUserId+" to: "+userId);
        
        
            
        String registeredUserIdWithWorkstation = registeredUserId+":"+userId;
        UserLogonHelper.checkValidCASState(true);
        int sessionID = UserLogonHelper.registerParSession(registeredUserIdWithWorkstation, password);
        
        //Now unregister old session if its a switch
        for(Iterator<String> it=currentSession.dependentSessions().keySet().iterator();it.hasNext();)
        {
            String oldUserId= it.next();
           if(!oldUserId.equals(registeredUserIdWithWorkstation))
           {
                Log.information(this,new StringBuilder(100).
                    append("Closing/swiching old SMS session:").
                    append(oldUserId).
                    append(" with new session:").
                    append(registeredUserIdWithWorkstation).toString());
                unRegisterUser(oldUserId);
                it.remove();
                parUsersMap.remove(userId);
                
           }
           //Add to PAR workstationID/UserID map
           parUsersMap.put(userId,registeredUserId);
        }
        //put new session .
        currentSession.dependentSessions().put(registeredUserIdWithWorkstation,sessionID);
        if (Log.isDebugOn())
        {
            Log.debug("finish calling registerUser for: "+registeredUserId+" to: "+userId);
        }    
    }

    public void unRegisterUser(String registeredUserId)
        throws SystemException,
               CommunicationException,
               DataValidationException,
               AuthorizationException,
               AuthenticationException,
               NotFoundException
    {
        Log.information("calling unRregisterUser for: "+registeredUserId);
        String registeredUserIdWithWorkstation = registeredUserId+":"+userId;
        UserLogonHelper.unRegisterParSession(registeredUserIdWithWorkstation);
        currentSession.dependentSessions().remove(registeredUserIdWithWorkstation);
        parUsersMap.remove(userId);
        
        if (Log.isDebugOn())
        {
            Log.debug("finish calling unRregisterUser for: "+registeredUserId);
        }
    
    }
    
    private SecurityAdminService getSecurityAdminService()
    {
        if ( securityAdminService == null)
        {
            try
            {
                SecurityAdminServiceHome home = (SecurityAdminServiceHome)HomeFactory.getInstance().findHome(SecurityAdminServiceHome.ADMIN_HOME_NAME);
                securityAdminService = home.find();
            }
            catch (CBOELoggableException e)
            {
                throw new NullPointerException("Could not find SecurityAdminServiceHomeAdmin");
            }
        }
        return securityAdminService;
    }

    public void changeRegisteredUserPassword(String registeredUserId, String oldPassword, String newPassword)
    throws SystemException,
           CommunicationException,
           AuthorizationException,
           DataValidationException
    {
           
           
           if(Log.isDebugOn())
           {
               Log.debug(this,"calling changeRegisteredUserPassword for: "+ registeredUserId );
           }
           securityAdminService = getSecurityAdminService();
           try{
              
              MemberAccountStruct memberAccountStruct = securityAdminService.getAccount(registeredUserId);
              memberAccountStruct.password = newPassword;
              securityAdminService.updateAccount(memberAccountStruct); 
    
              
          }catch(Exception ex)
          {
              Log.exception(this,"Can't find SecurityAdminService",ex);
          }
    }


 
    /*
     * *******************************************************************
     * PAR entry/query
     * *******************************************************************
     */
    public void acceptManualCancelReport(OrderRoutingParameterStruct orderRoutingParameterStruct, ManualCancelReportStruct[] manualCancelReportStructs, int productKey, int transactionSequenceNumber) throws SystemException, CommunicationException, DataValidationException, NotFoundException, AuthorizationException, NotAcceptedException, TransactionFailedException
    {
        StringBuilder msg = new StringBuilder(100);
        msg.append("calling acceptManualCancelReport for: ").append(currentSession.getUserId());
        Log.information(this, msg.toString());
        if (Log.isDebugOn())
        {
            
            for(ManualCancelReportStruct cr : manualCancelReportStructs)
            {
                Log.debug("OrderId: "+cr.orderId+"/n");
            }
        }

        orderHandlingService.acceptManualCancelReport(orderRoutingParameterStruct, manualCancelReportStructs, productKey, transactionSequenceNumber);

    }

    public void acceptManualFillReportV2(short activityType,
                                         OrderRoutingParameterStruct orderRouting, ManualFillStructV2[] fillReports,
                                         int transactionSequenceNumber)
            throws SystemException, CommunicationException,
                   DataValidationException, NotFoundException, AuthorizationException,
                   NotAcceptedException, TransactionFailedException
    {
        StringBuilder msg = new StringBuilder(100);
        msg.append("calling acceptManualFillReportV2 for: ").append(currentSession.getUserId());
        Log.information(this, msg.toString());
        if (Log.isDebugOn())
        {
            for(ManualFillStructV2 fr : fillReports)
            {
                Log.debug("OrderId: "+fr.manualFill.orderId+"/n");
            }
        }
        
        orderHandlingService.acceptManualFillReportV2(activityType, orderRouting, fillReports, transactionSequenceNumber);
    }

    public void acceptManualFillTimeout(OrderRoutingParameterStruct orderRoutingStruct,
                                        ManualFillStruct[] fillReport, short activityType, int transactionSequenceNumber)
            throws SystemException, CommunicationException, DataValidationException,
                   NotFoundException, AuthorizationException, NotAcceptedException,
                   TransactionFailedException
    {
        StringBuilder msg = new StringBuilder(100);
        msg.append("calling acceptManualFillTimeout for: ").append(currentSession.getUserId());
        Log.information(this, msg.toString());
        if (Log.isDebugOn())
        {
            for(ManualFillStruct fr : fillReport)
            {
                Log.debug("OrderId: "+fr.orderId+"/n");
            }
        }
        
        orderHandlingService.acceptManualFillTimeout(orderRoutingStruct, fillReport, activityType, transactionSequenceNumber);
    }

    public void acceptManualOrderReturn(ManualMarketBrokerDataStruct marketData,
                                        OrderRoutingParameterStruct orderRouting, OrderIdStruct orderID,
                                        String sessionName, int productKey, OrderHandlingInstructionStruct orderHandling,
                                        int[] legMaxExcutionVolume, long requestTime, short activityType)
            throws SystemException, CommunicationException, DataValidationException,
                   NotFoundException, AuthorizationException, NotAcceptedException,
                   TransactionFailedException
    {
        
        StringBuilder msg = new StringBuilder(100);
        msg.append("calling acceptManualOrderReturn for: ").append(currentSession.getUserId()).append("OrderId"+orderID);
        Log.information(this, msg.toString());
        orderHandlingService.acceptManualOrderReturn(marketData,
                                                     orderRouting, orderID, sessionName, productKey, orderHandling, legMaxExcutionVolume, requestTime, activityType);

    }

    public void acceptManualOrderReturnTimeout(ManualMarketBrokerDataStruct marketData,
                                               OrderRoutingParameterStruct orderRouting, OrderIdStruct orderID,
                                               String sessionName, int productKey, long requestTime,
                                               OrderHandlingInstructionStruct orderHandling, int quantity, int[] legQuantities,
                                               short activityType) throws SystemException, CommunicationException,
                                                                          DataValidationException, NotFoundException, AuthorizationException,
                                                                          NotAcceptedException, TransactionFailedException
    {
        
        StringBuilder msg = new StringBuilder(100);
        msg.append("calling acceptManualOrderReturnTimeout for: ").append(currentSession.getUserId()).append("OrderId"+orderID);
        Log.information(this, msg.toString());
        orderHandlingService.acceptManualOrderReturnTimeout(marketData,
                                                            orderRouting, orderID, sessionName, productKey, requestTime, orderHandling, quantity, legQuantities, activityType);
    }

    public void acceptPrintCancel(OrderRoutingParameterStruct orderRoutingStruct,
                                  ManualCancelRequestStruct cancelRequest, int productKey) throws SystemException,
                                                                                                  CommunicationException, DataValidationException, NotFoundException,
                                                                                                  AuthorizationException, NotAcceptedException, TransactionFailedException
    {
        StringBuilder msg = new StringBuilder(100);
        msg.append("calling acceptPrintCancel for: ").append(currentSession.getUserId()).append(cancelRequest.orderId);
        Log.information(this, msg.toString());
        orderHandlingService.acceptPrintCancel(orderRoutingStruct, cancelRequest, productKey);

    }

    public void acceptPrintCancelReplace(OrderRoutingParameterStruct orderRoutingStruct,
                                         ManualCancelRequestStruct cancelRequest, OrderIdStruct orderID, int productKey)
            throws SystemException, CommunicationException, DataValidationException,
                   NotFoundException, AuthorizationException, NotAcceptedException,
                   TransactionFailedException
    {
        StringBuilder msg = new StringBuilder(100);
        msg.append("calling acceptPrintCancelReplace for: ").append(currentSession.getUserId()).append("OrderId"+orderID);
        Log.information(this, msg.toString());
        orderHandlingService.acceptPrintCancelReplace(orderRoutingStruct, cancelRequest, orderID, productKey);

    }

    public void acceptPrintRequest(OrderRoutingParameterStruct orderRouting,
                                   OrderIdStruct orderID, String sessionName, int productKey, long requestTime,
                                   int[] legPrintVolume, int printQuantity, short printRequestType)
            throws SystemException, CommunicationException, DataValidationException,
                   NotFoundException, AuthorizationException, NotAcceptedException,
                   TransactionFailedException
    {
        StringBuilder msg = new StringBuilder(100);
        msg.append("calling acceptPrintRequest for: ").append(currentSession.getUserId()).append("OrderId"+orderID);
        Log.information(this, msg.toString());
        orderHandlingService.acceptPrintRequest(orderRouting, orderID, sessionName, productKey, requestTime,
                                                legPrintVolume, printQuantity, printRequestType);
    }

    public void acceptVolumeChange(OrderRoutingParameterStruct orderRouting,
                                   OrderIdStruct orderID, String sessionName, int productKey, int changeVolume,
                                   long requestTime) throws SystemException, CommunicationException,
                                                            DataValidationException, NotFoundException, AuthorizationException,
                                                            NotAcceptedException, TransactionFailedException
    {
        StringBuilder msg = new StringBuilder(100);
        msg.append("calling acceptVolumeChange for: ").append(currentSession.getUserId()).append("OrderId"+orderID)   ;
        Log.information(this, msg.toString());
        orderHandlingService.acceptVolumeChange(orderRouting, orderID, sessionName, productKey, changeVolume, requestTime);

    }
    
    
    public void setParUserPreferences(String userId, PreferenceStruct[] preferences)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        StringBuilder msg = new StringBuilder(100);
        msg.append("calling setParUserPreferences for: ")
           .append (userId)
           .append("from workstation: ")
           .append(currentSession.getUserId());
        Log.information(this, msg.toString());
        StringBuilder msgBuffer = new StringBuilder(100);
        getUserService().setUserPreferences(userId, preferences);
        
    }

    public void removeParUserPreference(String userId, PreferenceStruct[] preferenceSequence)
      throws SystemException, CommunicationException, AuthorizationException, DataValidationException
      {
        StringBuilder msg = new StringBuilder(100);
        msg.append("calling removeParUserPreference for: ")
           .append (userId)
           .append("from workstation: ")
           .append(currentSession.getUserId());
        Log.information(this, msg.toString());
        getUserService().removeUserPreference(userId, preferenceSequence);
      }
    
    public PreferenceStruct[] getAllParUserPreferences(String userId)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
      StringBuilder msg = new StringBuilder(100);
      msg.append("calling getAllParUserPreferences for: ")
         .append (userId)
         .append("from workstation: ")
         .append(currentSession.getUserId());
      Log.information(this, msg.toString());
      return getUserService().getAllUserPreferences(userId);
    }
      
    public PreferenceStruct[] getParUserPreferences(String userId, String prefix)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        StringBuilder msg = new StringBuilder(100);
        msg.append("calling getParUserPreferencesByPrefix for: ")
           .append (userId)
           .append("from workstation: ")
           .append(currentSession.getUserId());
        Log.information(this, msg.toString());
        return getUserService().getUserPreferencesByPrefix(userId, prefix);
    }
    
    
   
    public void removeParUserPreferencesByPrefix(String userId, String prefix)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        StringBuilder msg = new StringBuilder(100);
        msg.append("calling removeParUserPreferencesByPrefix for: ")
           .append (userId)
           .append("from workstation: ")
           .append(currentSession.getUserId());
        Log.information(this, msg.toString());
        getUserService().removeUserPreferencesByPrefix(userId, prefix);
    }
      
    public PreferenceStruct[] getAllParSystemPreferences(String userId)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        StringBuilder msg = new StringBuilder(100);
        msg.append("calling getAllParSystemPreferences for: ")
           .append (userId)
           .append("from workstation: ")
           .append(currentSession.getUserId());
        Log.information(this, msg.toString());
        return getUserService().getAllSystemPreferences(userId);
    }
      
    public PreferenceStruct[] getParSystemPreferencesByPrefix(String userId, String prefix)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        StringBuilder msg = new StringBuilder(100);
        msg.append("calling getParSystemPreferencesByPrefix for: ")
           .append (userId)
           .append("from workstation: ")
           .append(currentSession.getUserId());
        Log.information(this, msg.toString());
        return getUserService().getSystemPreferencesByPrefix(userId,prefix);
    }
     
    private UserService getUserService()
    {
        if (userService == null )
        {
            userService = ServicesHelper.getUserService();
        }

        return userService;
    }
    
    /*
        * ************************************************************************************************************************************
        * OrderRoutingConsumer Call back
        **************************************************************************************************************************************
        */
    public void acceptManualOrders(RoutingParameterV2Struct routingParameterV2Struct,
                                   OrderManualHandlingStructV2[] orders)
    {
        Log.information(this, "calling acceptManualOrders for " + getSessionManager() + " source:" +
                routingParameterV2Struct.source);
        if (Log.isDebugOn())
        {
            for(OrderManualHandlingStructV2 order : orders)
            {
                Log.debug(" OrderId: "+order.order.orderId+"/n");
            }
        }

        RoutingGroupParOrderStructSequenceContainer container =
                new RoutingGroupParOrderStructSequenceContainer(routingParameterV2Struct,
                                                                orders);
        ChannelKey key = new ChannelKey(ChannelType.CB_PAR_ORDER_ACCEPTED, currentSession);
        ChannelEvent event = internalEventChannel.getChannelEvent(this, key, container);
        orderRoutingSupplier.dispatch(event);
    }


    public void acceptManualCancels(RoutingParameterV2Struct routingParameterV2Struct,
                                    ManualCancelRequestStructV2[] cancelRequests)
    {
        Log.information(this, "calling acceptManualCancels for " + getSessionManager() + " source:" +
                routingParameterV2Struct.source);
        if (Log.isDebugOn())
        {
            for(ManualCancelRequestStructV2 cr : cancelRequests)
            {
                Log.debug(" OrderId: "+cr.cancelRequest.orderId+"/n");
            }
        }
        
        RoutingGroupParCancelRequestStructSequenceContainer container = new RoutingGroupParCancelRequestStructSequenceContainer(
                routingParameterV2Struct, cancelRequests);
        ChannelKey key = new ChannelKey(ChannelType.CB_PAR_ORDER_CANCELED, currentSession);
        ChannelEvent event = internalEventChannel.getChannelEvent(this, key, container);
        orderRoutingSupplier.dispatch(event);
    }

    public void acceptManualCancelReplaces(RoutingParameterV2Struct routingParameterV2Struct,
                                           ManualCancelReplaceStruct[] cancelReplaces)
    {
        Log.information(this, "calling acceptManualCancelReplaces for " + getSessionManager() + " source:" +
                routingParameterV2Struct.source);
        if (Log.isDebugOn())
        {
            for(ManualCancelReplaceStruct cxlre : cancelReplaces)
            {
                Log.debug(" OrderId: "+cxlre.cancelRequest.orderId+"/n");
            }
        }
        RoutingGroupParCancelReplaceStructSequenceContainer container =
                new RoutingGroupParCancelReplaceStructSequenceContainer(routingParameterV2Struct,
                                                                        cancelReplaces);
        ChannelKey key = new ChannelKey(ChannelType.CB_PAR_ORDER_CANCEL_REPLACED, currentSession);
        ChannelEvent event = internalEventChannel.getChannelEvent(this, key, container);
        orderRoutingSupplier.dispatch(event);
    }


    public void acceptOrders(RoutingParameterV2Struct routingParameterV2Struct,
                             OrderRoutingStruct[] orders)
    {
        throw new RuntimeException("acceptOrders Not supported.");
    }


    public void acceptCancels(RoutingParameterV2Struct routingParameterV2Struct,
                              CancelRoutingStruct[] cancels)
    {
        throw new RuntimeException("acceptCancels Not supported.");

    }

    public void acceptCancelReplaces(RoutingParameterV2Struct routingParameterV2Struct,
                                     CancelReplaceRoutingStruct[] cancelReplaces)
    {
        throw new RuntimeException("acceptCancelReplaces Not supported.");

    }

    public void acceptFillReportReject(RoutingParameterV2Struct routingParameterV2Struct,
                                       FillReportRejectRoutingStruct[] fillReportRejects)
    {
        throw new RuntimeException("acceptFillReportReject Not supported.");
    }

    public void acceptRemoveOrder(RoutingParameterV2Struct routingParameterV2Struct,
                                  OrderIdRoutingStruct[] orderIds)
    {
        throw new RuntimeException("acceptRemoveOrder Not supported.");
    }

    public void acceptLinkageCancelReport(RoutingParameterV2Struct routingParameterV2Struct,
                                          LinkageCancelReportRoutingStruct[] cancelReports)
    {
        throw new RuntimeException("acceptLinkageCancelReport Not supported.");
    }

    public void acceptLinkageFillReport(RoutingParameterV2Struct routingParameterV2Struct,
                                        LinkageFillReportRoutingStruct[] fillReports)
    {
        throw new RuntimeException("acceptLinkageFillReport Not supported.");
    }

    public void acceptOrderLocationServerResponse(OrderLocationServerResponseStruct response)
    {
        throw new RuntimeException("acceptOrderLocationServerResponse Not supported.");
    }


    public void acceptOrderLocationSummaryServerResponse(OrderLocationSummaryServerResponseStruct response)
    {
        throw new RuntimeException("acceptOrderLocationSummaryServerResponse Not supported.");
    }


    public void acceptRemoveMessage(RoutingParameterV2Struct routingParameterV2Struct, long msgId)
    {
        throw new RuntimeException("acceptRemoveMessage Not supported.");
    }

    public void acceptTradeNotifications(RoutingParameterV2Struct routingParameterV2Struct,
                                         TradeNotificationRoutingStruct[] tradeNotifications)
    {
        throw new RuntimeException("acceptTradeNotifications Not supported.");
    }

    public void acceptFillReportDropCopy(RoutingParameterV2Struct routingParameterV2Struct,
                                         FillReportDropCopyRoutingStruct[] fillReportDropCopies)
    {
        throw new RuntimeException("acceptFillReportDropCopy Not supported.");
    }

    public void acceptCancelReportDropCopy(RoutingParameterV2Struct routingParameterV2Struct,
                                           CancelReportDropCopyRoutingStruct[] cancelRoprtDropCopies)
    {
        throw new RuntimeException("acceptCancelReportDropCopy Not supported.");
    }

    public void acceptManualOrderTimeout(RoutingParameterV2Struct routingParameters, ManualOrderTimeoutRoutingStruct[] manualOrderTimeouts)
    {
        throw new RuntimeException("acceptManualOrderTimeout Not supported.");
    }

    public void acceptManualFillTimeout(RoutingParameterV2Struct routingParameters, ManualFillTimeoutRoutingStruct[] fillReports)
    {
        throw new RuntimeException("acceptManualFillTimeout Not supported.");
    }

    public void acceptCancelReport(RoutingParameterV2Struct p_orderRoutingStruct,
            CancelReportStruct[] p_cancelReports, LinkageExtensionsStruct p_linkageExtensions)
    {
        // TODO Auto-generated method stub
        
    }

    public void acceptFillReport(RoutingParameterV2Struct p_orderRoutingStruct,
            FilledReportStruct[] p_fillReports, LinkageExtensionsStruct p_linkageExtensions)
    {
        // TODO Auto-generated method stub
        
    }

    public void acceptFillReportReject(RoutingParameterV2Struct p_orderRoutingStruct,
            CboeIdStruct p_cboeId, ManualFillStruct[] p_fillReports, String p_rejectReason)
    {
        // TODO Auto-generated method stub
        
    }

    public void acceptOrders(RoutingParameterV2Struct p_orderRoutingStruct, OrderStruct[] Orders)
    {
        // TODO Auto-generated method stub
        
    }

    public void acceptRemoveOrder(RoutingParameterV2Struct p_orderRoutingStruct,
            OrderIdStruct[] p_orderId)
    {
        // TODO Auto-generated method stub
        
    }

    

   

}
