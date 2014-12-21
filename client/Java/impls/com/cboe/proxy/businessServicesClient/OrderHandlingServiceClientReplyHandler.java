package com.cboe.proxy.businessServicesClient;

import com.cboe.client.util.ClientObjectResolver;
import com.cboe.idl.businessServices.AMI_OrderHandlingServiceExceptionHolder;
import com.cboe.idl.businessServices.AMI_OrderHandlingServiceHandler;
import com.cboe.idl.businessServices.AMI_OrderHandlingServiceHandlerHelper;
import com.cboe.idl.businessServices.AMI_OrderRoutingServiceExceptionHolder;
import com.cboe.idl.businessServices.POA_AMI_OrderHandlingServiceHandler;
import com.cboe.idl.cmiOrder.InternalizationOrderResultStruct;
import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.idl.cmiOrder.PendingOrderStruct;
import com.cboe.idl.cmiTraderActivity.ActivityHistoryStruct;
import com.cboe.idl.order.CrossOrderIdStruct;
import com.cboe.idl.order.OrderQueryResultStruct;
import com.cboe.idl.util.ServerResponseStruct;
import com.cboe.idl.util.ServerResponseStructV2;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.orbService.NoSuchPOAException;
import com.cboe.interfaces.businessServicesClient.ReplyHandlerClient;

import java.util.Vector;

/**
 * Implementation specific to OrderHandlingServiceClient. Also implements the AMI interface.
 *
 * @date January 12, 2009
 */
public class OrderHandlingServiceClientReplyHandler extends POA_AMI_OrderHandlingServiceHandler
        implements ReplyHandlerClient
{

    private int numberOfRequests;
    private int numberOfResponses;
    private int numberOfExceptions;

    private boolean handlerState;
    private OrderStruct[] orderData;
    private ActivityHistoryStruct orderHistory;
    private Vector orderDataVector; // used for cumulative responses
    private AMI_OrderHandlingServiceHandler narrowedReplyHandler;
    private OrderHandlingServiceClientReplyHandlerManager replyHandlerManager;

    private static final OrderStruct[] EMPTY_OrderStruct_ARRAY = new OrderStruct[0];

    public static final String HANDLER_POA_NAME = "AMIHandlerPOA";

    /**
     * OrderHandlingServiceReplyHandler constructor comment.
     */
    public OrderHandlingServiceClientReplyHandler(OrderHandlingServiceClientReplyHandlerManager rhm)
    {
        super();
        orderDataVector = new Vector();
        setReplyHandlerManager(rhm);
    }


    public void initialize()
    {
        com.cboe.infrastructureServices.orbService.OrbService orb = FoundationFramework.getInstance().getOrbService();
        try
        {
            org.omg.CORBA.Object obj = orb.connect(HANDLER_POA_NAME, this);
            //org.omg.CORBA.Object obj = orb.connect( com.cboe.infrastructureServices.orbService.OrbServicePOAImpl.HANDLER_POA_NAME, this );
            narrowedReplyHandler = (AMI_OrderHandlingServiceHandler) ClientObjectResolver.resolveObject(obj, AMI_OrderHandlingServiceHandlerHelper.class.getName());

        }
        catch (NoSuchPOAException e)
        {
            Log.information("OrderHandlingServiceReplyHandler: Error initializing reply handler");
            Log.exception(e);
        }
    }

    public void setReplyHandlerManager(OrderHandlingServiceClientReplyHandlerManager rhm)
    {
        try
        {
            if (rhm == null)
            {
                throw new Exception();
            }
            replyHandlerManager = rhm;
        }
        catch (Exception e)
        {
            Log.information("OrderHandlingServiceReplyHandler: Null " +
                    "OrderHandlingServiceReplyHandlerManager passed to setReplyHandlerManager.");
            Log.exception(e);
        }
    }

    public OrderHandlingServiceClientReplyHandlerManager getReplyHandlerManager()
    {
        return replyHandlerManager;
    }


    public void acceptCrossingOrder()
    {
        // No implementation
    }

    public void acceptCrossingOrder_excep(com.cboe.idl.businessServices.AMI_OrderHandlingServiceExceptionHolder excep_holder)
    {
        // No implementation
    }


    public void acceptCrossingOrderV2(CrossOrderIdStruct ami_return_val)
    {
        // TODO Auto-generated method stub

    }

    public void acceptCrossingOrderV2_excep(AMI_OrderHandlingServiceExceptionHolder excep_holder)
    {
        // TODO Auto-generated method stub

    }

    public void acceptInternalizationStrategyOrders(InternalizationOrderResultStruct internalizationOrderResultStruct)
    {

    }

    public void acceptInternalizationStrategyOrders_excep(AMI_OrderHandlingServiceExceptionHolder ami_orderHandlingServiceExceptionHolder)
    {

    }

    public void acceptInternalizationStrategyOrdersV2(InternalizationOrderResultStruct internalizationOrderResultStruct)
    {
        //Todo: Blank method to complile
        //No implementation
    }

    public void acceptInternalizationStrategyOrdersV2_excep(AMI_OrderHandlingServiceExceptionHolder ami_orderHandlingServiceExceptionHolder)
    {
        //Todo: Blank method to complile
        //No implementation
    }

    public void acceptInternalizationOrders(InternalizationOrderResultStruct internalizationOrderResultStruct)
    {
        //Todo: Blank method to complile 08/19/2004 CAAP
        // No implementation
    }

    public void acceptInternalizationOrders_excep(AMI_OrderHandlingServiceExceptionHolder ami_orderHandlingServiceExceptionHolder)
    {
        //Todo: Blank method to complile 08/19/2004 CAAP
        // No implementation
    }

    public void acceptCancel()
    {
        // No implementation
    }

    public void acceptCancel_excep(com.cboe.idl.businessServices.AMI_OrderRoutingServiceExceptionHolder excep_holder)
    {
        // No implementation
    }

    public void acceptCancelV2(boolean value)
    {
        // No implementation
    }

    public void acceptCancelV2_excep(com.cboe.idl.businessServices.AMI_OrderRoutingServiceExceptionHolder excep_holder)
    {
        // No implementation
    }

    public void acceptCancelReplace(com.cboe.idl.cmiOrder.OrderIdStruct orderId)
    {
        // No implementation
    }

    public void acceptCancelReplace_excep(com.cboe.idl.businessServices.AMI_OrderRoutingServiceExceptionHolder excep_holder)
    {
        // No implementation
    }

    public void acceptOrder(com.cboe.idl.cmiOrder.OrderIdStruct orderId)
    {
        // No implementation
    }

    public void acceptOrder_excep(com.cboe.idl.businessServices.AMI_OrderRoutingServiceExceptionHolder excep_holder)
    {
        // No implementation
    }

    public void acceptUpdate()
    {
        // No implementation
    }

    public void acceptUpdate_excep(com.cboe.idl.businessServices.AMI_OrderHandlingServiceExceptionHolder excep_holder)
    {
        // No implementation
    }

    public void acceptStrategyCancelReplace(OrderIdStruct p0)
    {
        // No implementation
    }

    public void acceptStrategyCancelReplace_excep(AMI_OrderHandlingServiceExceptionHolder p0)
    {
        // No implementation
    }

    public void acceptStrategyCancelReplaceV2(OrderIdStruct p0)
    {
        // No implementation
    }

    public void acceptStrategyCancelReplaceV2_excep(AMI_OrderHandlingServiceExceptionHolder p0)
    {
        // No implementation
    }

    public void getAssociatedOrders(OrderStruct[] ami_return_val)
    {

    }

    public void getAssociatedOrders_excep(AMI_OrderHandlingServiceExceptionHolder excep_holder)
    {

    }

    public void getOrdersByOrderTypeAndClass(OrderStruct[] ami_return_val)
    {

    }

    public void getOrdersByOrderTypeAndClass_excep(AMI_OrderHandlingServiceExceptionHolder excep_holder)
    {

    }

    public void getOrdersByOrderTypeAndProduct(OrderStruct[] ami_return_val)
    {

    }

    public void getOrdersByOrderTypeAndProduct_excep(AMI_OrderHandlingServiceExceptionHolder excep_holder)
    {

    }

    public void acceptStrategyOrder(OrderIdStruct p0)
    {
        // No implementation
    }

    public void acceptStrategyOrder_excep(AMI_OrderHandlingServiceExceptionHolder p0)
    {
        // No implementation
    }

    public void acceptStrategyOrderV2(OrderIdStruct p0)
    {
        // No implementation
    }

    public void acceptStrategyOrderV2_excep(AMI_OrderHandlingServiceExceptionHolder p0)
    {
        // No implementation
    }

    public void acceptStrategyUpdate()
    {
        // No implementation
    }

    public void acceptStrategyUpdate_excep(AMI_OrderHandlingServiceExceptionHolder p0)
    {
        // No implementation
    }

    // Start of service specific methods

    public AMI_OrderHandlingServiceHandler getAMIHandler()
    {
        return narrowedReplyHandler;
    }

    /**
     * Returns the number of exceptions returned
     */

    public int getNumberOfExceptions()
    {
        return numberOfExceptions;
    }

    /**
     * Returns the number of responses pending
     */

    public int getNumberOfPendingResponses()
    {
        return (numberOfRequests - (numberOfResponses + numberOfExceptions));
    }

    /**
     * Returns the number of requests to be forwarded
     */

    public int getNumberOfRequests()
    {
        return numberOfRequests;
    }

    public synchronized void getOrderById(com.cboe.idl.cmiOrder.OrderStruct ami_return_val)
    {
        // Notifies after first successful response
        numberOfResponses++;
        orderData = new OrderStruct[1];
        orderData[0] = ami_return_val;
        handlerState = true;
        notifyAll();
        getReplyHandlerManager().returnReplyHandler(this);
    }

    public synchronized void getOrderById(String userId, com.cboe.idl.cmiOrder.OrderStruct ami_return_val)
    {
        // Notifies after first successful response
        numberOfResponses++;
        orderData = new OrderStruct[1];
        orderData[0] = ami_return_val;
        handlerState = true;
        notifyAll();
        getReplyHandlerManager().returnReplyHandler(this);
    }

    public synchronized void getOrderById_excep(com.cboe.idl.businessServices.AMI_OrderHandlingServiceExceptionHolder excep_holder)
    {
        // Notifies after all responses are received as exceptions
        numberOfExceptions++;
        if (numberOfExceptions >= numberOfRequests) // ">=" In case numberOfRequests isn't set...
        {
            handlerState = true;
            notifyAll();
            getReplyHandlerManager().returnReplyHandler(this);
        }
    }

    /* Operation Definition */
    public void getOrderByIdForProduct(OrderStruct ami_return_val)
    {
        // Notifies after first successful response
        numberOfResponses++;
        orderData = new OrderStruct[1];
        orderData[0] = ami_return_val;
        handlerState = true;
        notifyAll();
        getReplyHandlerManager().returnReplyHandler(this);
    }

    /* Operation Definition */
    public void getOrderByIdForProduct_excep(AMI_OrderHandlingServiceExceptionHolder excep_holder)
    {
        // Notifies after all responses are received as exceptions
        numberOfExceptions++;
        if (numberOfExceptions >= numberOfRequests) // ">=" In case numberOfRequests isn't set...
        {
            handlerState = true;
            notifyAll();
            getReplyHandlerManager().returnReplyHandler(this);
        }
    }

    /* Operation Definition */
    public void getOrderByIdForClass(OrderStruct ami_return_val)
    {
        // Notifies after first successful response
        numberOfResponses++;
        orderData = new OrderStruct[1];
        orderData[0] = ami_return_val;
        handlerState = true;
        notifyAll();
        getReplyHandlerManager().returnReplyHandler(this);
    }

    /* Operation Definition */
    public void getOrderByIdForClass_excep(AMI_OrderHandlingServiceExceptionHolder excep_holder)
    {
        // Notifies after all responses are received as exceptions
        numberOfExceptions++;
        if (numberOfExceptions >= numberOfRequests) // ">=" In case numberOfRequests isn't set...
        {
            handlerState = true;
            notifyAll();
            getReplyHandlerManager().returnReplyHandler(this);
        }
    }

    public synchronized void getOrder(com.cboe.idl.cmiOrder.OrderStruct ami_return_val)
    {
        // Notifies after first successful response
        numberOfResponses++;
        orderData = new OrderStruct[1];
        orderData[0] = ami_return_val;
        handlerState = true;
        notifyAll();
        getReplyHandlerManager().returnReplyHandler(this);
    }

    public void getOrder_excep(com.cboe.idl.businessServices.AMI_OrderHandlingServiceExceptionHolder excep_holder)
    {
        // Notifies after all responses are received as exceptions

        synchronized (this)
        {
            numberOfExceptions++;
            if (numberOfExceptions >= numberOfRequests)// ">=" In case numberOfRequests isn't set...
            {
                handlerState = true;
                notifyAll();
                getReplyHandlerManager().returnReplyHandler(this);
            }
        }
    }

    public OrderStruct[] getOrderData()
    {
        if (orderData == null)
        {
            orderData = EMPTY_OrderStruct_ARRAY;
        }

        return orderData;
    }

    public ActivityHistoryStruct getOrderHistory()
    {
        if (orderHistory == null)
        {
            orderHistory = new ActivityHistoryStruct();
        }

        return orderHistory;
    }

    public void getOrders(com.cboe.idl.cmiOrder.OrderStruct[] ami_return_val)
    {
    }

    public void getOrders_excep(com.cboe.idl.businessServices.AMI_OrderHandlingServiceExceptionHolder excep_holder)
    {
    }

    /**
     * Callback method where responses are expected from multiple sources
     * Notifies after all respones have been received either as exceptions or data
     */
    public void getOrdersForProduct(com.cboe.idl.cmiOrder.OrderStruct[] ami_return_val)
    {
        if (ami_return_val.length != 0)
        {
            for (int i = 0; i < ami_return_val.length; i++)
            {
                orderDataVector.addElement(ami_return_val[i]);
            }
        }
        synchronized (this)
        {
            numberOfResponses++;
            if ((numberOfResponses + numberOfExceptions) >= numberOfRequests) // ">=" In case numberOfRequests isn't set...
            {
                orderData = new OrderStruct[orderDataVector.size()];
                orderDataVector.copyInto(orderData);
                handlerState = true;
                orderDataVector.removeAllElements();
                notifyAll();
                getReplyHandlerManager().returnReplyHandler(this);
            }
        }
    }

    /**
     * Callback method for exception handling
     * Notifies if all requests are satisfied by execptions
     */
    public void getOrdersForProduct_excep(com.cboe.idl.businessServices.AMI_OrderHandlingServiceExceptionHolder excep_holder)
    {
        // Notifies after all responses are received as exceptions
        synchronized (this)
        {
            numberOfExceptions++;
            if (numberOfExceptions >= numberOfRequests) // ">=" In case numberOfRequests isn't set...
            {
                handlerState = true;
                notifyAll();
                getReplyHandlerManager().returnReplyHandler(this);
            }
        }
    }

    public void getOrderForProduct(com.cboe.idl.cmiOrder.OrderStruct ami_return_val)
    {
    }

    public void getOrderForProduct_excep(com.cboe.idl.businessServices.AMI_OrderHandlingServiceExceptionHolder excep_holder)
    {
    }

    public void getPendingAdjustmentOrdersByClass(com.cboe.idl.cmiOrder.OrderStruct[] ami_return_val)
    {
    }

    public void getPendingAdjustmentOrdersByClass_excep(com.cboe.idl.businessServices.AMI_OrderHandlingServiceExceptionHolder excep_holder)
    {
    }

    public void getPendingAdjustmentOrdersByProduct(com.cboe.idl.cmiOrder.OrderStruct[] ami_return_val)
    {
    }

    public void getPendingAdjustmentOrdersByProduct_excep(com.cboe.idl.businessServices.AMI_OrderHandlingServiceExceptionHolder excep_holder)
    {
    }

    /**
     * Returns the state of the handler - if its is in a state where it can
     * be polled for data
     */

    public boolean isReady()
    {
        return handlerState;
    }

    public void publishOrdersForUser()
    {
        synchronized (this)
        {
            numberOfResponses++;
            if ((numberOfResponses + numberOfExceptions) >= numberOfRequests) // ">=" In case numberOfRequests isn't set...
            {
                getReplyHandlerManager().returnReplyHandler(this);
            }
        }
    }

    public void publishOrdersForUser_excep(com.cboe.idl.businessServices.AMI_OrderHandlingServiceExceptionHolder excep_holder)
    {
        // Notifies after all responses are received as exceptions
        synchronized (this)
        {
            numberOfExceptions++;
            if (numberOfExceptions >= numberOfRequests) // ">=" In case numberOfRequests isn't set...
            {
                handlerState = true;
                notifyAll();
                getReplyHandlerManager().returnReplyHandler(this);
            }
        }
    }

    public void publishOrdersForFirm()
    {
        synchronized (this)
        {
            numberOfResponses++;
            if ((numberOfResponses + numberOfExceptions) >= numberOfRequests) // ">=" In case numberOfRequests isn't set...
            {
                getReplyHandlerManager().returnReplyHandler(this);
            }
        }
    }

    public void publishOrdersForFirm_excep(com.cboe.idl.businessServices.AMI_OrderHandlingServiceExceptionHolder excep_holder)
    {
        // Notifies after all responses are received as exceptions
        synchronized (this)
        {
            numberOfExceptions++;
            if (numberOfExceptions >= numberOfRequests) // ">=" In case numberOfRequests isn't set...
            {
                handlerState = true;
                notifyAll();
                getReplyHandlerManager().returnReplyHandler(this);
            }
        }
    }

    public void queryOrderHistory(com.cboe.idl.cmiTraderActivity.ActivityHistoryStruct orderHistoryStruct)
    {
        // Notifies after first successful response

        synchronized (this)
        {
            numberOfResponses++;
            orderHistory = orderHistoryStruct;
            handlerState = true;
            notifyAll();
            getReplyHandlerManager().returnReplyHandler(this);
        }
    }

    public void queryOrderHistory_excep(com.cboe.idl.businessServices.AMI_OrderHandlingServiceExceptionHolder excep_holder)
    {
        // Notifies after all responses are received as exceptions
        synchronized (this)
        {
            numberOfExceptions++;
            if (numberOfExceptions >= numberOfRequests) // ">=" In case numberOfRequests isn't set...
            {
                handlerState = true;
                notifyAll();
                getReplyHandlerManager().returnReplyHandler(this);
            }
        }
    }

    /**
     * Resets the internal attributes
     */
    public void reset()
    {
        numberOfRequests = 0;
        numberOfResponses = 0;
        numberOfExceptions = 0;
        handlerState = false;
        orderData = null;
        orderHistory = null;
        orderDataVector.removeAllElements();
    }

    /**
     * Sets the number of requests forwarded
     */

    public void setNumberOfRequests(int requests)
    {
        numberOfRequests = requests;
    }

    public int getNumberOfResponses()
    {
        return numberOfResponses;
    }

    public void cancelOrderForUsers(ServerResponseStruct[] serverResponseStructs)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void cancelOrderForUsers_excep(
            AMI_OrderHandlingServiceExceptionHolder ami_orderHandlingServiceExceptionHolder)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void getOrderCountForUsers(ServerResponseStruct[] serverResponseStructs)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void getOrderCountForUsers_excep(
            AMI_OrderHandlingServiceExceptionHolder ami_orderHandlingServiceExceptionHolder)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    public void getOrderByIdV2(OrderStruct p_ami_return_val)
    {
        // TODO Auto-generated method stub

    }


    public void getOrderByIdV2_excep(AMI_OrderHandlingServiceExceptionHolder p_excep_holder)
    {
        // TODO Auto-generated method stub

    }


    public void getOrderByORSID(OrderStruct p_ami_return_val)
    {
        // TODO Auto-generated method stub

    }


    public void getOrderByORSID_excep(AMI_OrderHandlingServiceExceptionHolder p_excep_holder)
    {
        // TODO Auto-generated method stub

    }


    public void getOrdersByClassAndTime(OrderQueryResultStruct p_ami_return_val)
    {
        // TODO Auto-generated method stub

    }


    public void getOrdersByClassAndTime_excep(AMI_OrderHandlingServiceExceptionHolder p_excep_holder)
    {
        // TODO Auto-generated method stub

    }

    public void getOrdersByLocationType_excep(AMI_OrderHandlingServiceExceptionHolder p_excep_holder)
    {
        // TODO Auto-generated method stub

    }


    public void getOrdersByLocation_excep(AMI_OrderHandlingServiceExceptionHolder p_excep_holder)
    {
        // TODO Auto-generated method stub

    }


    public void getOrdersByProductAndTime(OrderQueryResultStruct p_ami_return_val)
    {
        // TODO Auto-generated method stub

    }


    public void getOrdersByProductAndTime_excep(AMI_OrderHandlingServiceExceptionHolder p_excep_holder)
    {
        // TODO Auto-generated method stub

    }


    public void publishOrdersForDestination_excep(AMI_OrderHandlingServiceExceptionHolder p_excep_holder)
    {
        // TODO Auto-generated method stub

    }


    public void acceptDirectRoute()
    {
        // TODO Auto-generated method stub

    }


    public void acceptDirectRoute_excep(AMI_OrderRoutingServiceExceptionHolder p_excep_holder)
    {
        // TODO Auto-generated method stub

    }


    public void publishAllMessagesForDestination_excep(AMI_OrderHandlingServiceExceptionHolder p_excep_holder)
    {
        // TODO Auto-generated method stub

    }


    public void publishAllMessagesForDestination()
    {
        // TODO Auto-generated method stub

    }


    public void acceptManualCancel()
    {
        // TODO Auto-generated method stub

    }


    public void acceptManualCancel_excep(AMI_OrderHandlingServiceExceptionHolder p_excep_holder)
    {
        // TODO Auto-generated method stub

    }


    public void acceptManualCancelReplace_excep(AMI_OrderHandlingServiceExceptionHolder p_excep_holder)
    {
    }


    public void getPendingAdjustmentOrders(PendingOrderStruct[] p_ami_return_val)
    {

    }


    public void getPendingAdjustmentOrders_excep(AMI_OrderHandlingServiceExceptionHolder p_excep_holder)
    {

    }


    public void markMessageAsRead()
    {
        // TODO Auto-generated method stub

    }


    public void markMessageAsRead_excep(AMI_OrderHandlingServiceExceptionHolder p_excep_holder)
    {
        // TODO Auto-generated method stub

    }


    public void acceptMessageRoute()
    {
        // TODO Auto-generated method stub

    }


    public void acceptMessageRoute_excep(AMI_OrderHandlingServiceExceptionHolder p_excep_holder)
    {
        // TODO Auto-generated method stub

    }


    public void getOrdersByLocation(ServerResponseStructV2[] p_ami_return_val)
    {
        // TODO Auto-generated method stub

    }


    public void getOrdersByLocationType(ServerResponseStructV2[] p_ami_return_val)
    {
        // TODO Auto-generated method stub

    }


    public void acceptManualUpdate()
    {
        // TODO Auto-generated method stub

    }


    public void acceptManualUpdate_excep(AMI_OrderHandlingServiceExceptionHolder p_excep_holder)
    {
        // TODO Auto-generated method stub

    }


    public void acceptManualFillReport()
    {
        // TODO Auto-generated method stub

    }


    public void acceptManualFillReport_excep(AMI_OrderHandlingServiceExceptionHolder p_excep_holder)
    {
        // TODO Auto-generated method stub

    }


    public void acceptManualCancelReplace(OrderIdStruct p_ami_return_val)
    {

    }

    /**
     * Blank implementation of method
     *
     * @author Cognizant Technology Solutions.
     */
    public void cancelOrderForUsersV2(ServerResponseStruct[] serverResponseStructs)
    {
        //Null implementation of method cancelOrderForUsersV2
    }

    /**
     * Blank implementation of method
     *
     * @author Cognizant Technology Solutions.
     */
    public void cancelOrderForUsersV2_excep(
            AMI_OrderHandlingServiceExceptionHolder ami_orderHandlingServiceExceptionHolder)
    {
        //Null implementation of method cancelOrderForUsersV2
    }

    /**
     * Blank implementation of method
     *
     * @author Cognizant Technology Solutions.
     */
    public void cancelOrdersForRoutingGroups(ServerResponseStruct[] ami_return_val)
    {
        //Null implementation of method cancelOrdersForRoutingGroups
    }

    /**
     * Blank implementation of method
     *
     * @author Cognizant Technology Solutions.
     */
    public void cancelOrdersForRoutingGroups_excep(AMI_OrderHandlingServiceExceptionHolder excep_holder)
    {
        //Null implementation of method cancelOrdersForRoutingGroups
    }

    /**
     * Blank implementation of method
     *
     * @author Cognizant Technology Solutions.
     */
    public void cancelOrdersForUser(ServerResponseStruct[] serverResponseStructs)
    {
        //Null implementation of method cancelOrdersForUser
    }

    /**
     * Blank implementation of method
     *
     * @author Cognizant Technology Solutions.
     */
    public void cancelOrdersForUser_excep(AMI_OrderHandlingServiceExceptionHolder excep_holder)
    {
        //Null implementation of method cancelOrdersForUser
    }


    /**
     * Blank implementation of method
     *
     * @author Cognizant Technology Solutions.
     */
    public void cancelOrdersForAllUsersByClass(ServerResponseStruct[] serverResponseStructs)
    {

        //Null implementation of method cancelOrderForAllUsersByClass
    }


    /**
     * Blank implementation of method
     *
     * @author Cognizant Technology Solutions.
     */
    public void cancelOrdersForAllUsersByClass_excep(
            AMI_OrderHandlingServiceExceptionHolder ami_orderHandlingServiceExceptionHolder)
    {

        //Null implementation of method cancelOrderForAllUsersByClass
    }


    /**
     * Blank implementation of method
     *
     * @author Cognizant Technology Solutions.
     */
    public void cancelOrdersForAllUsersByProduct(ServerResponseStruct[] serverResponseStructs)
    {

        //Null implementation of method cancelOrderForAllUsersByProduct
    }


    /**
     * Blank implementation of method
     *
     * @author Cognizant Technology Solutions.
     */
    public void cancelOrdersForAllUsersByProduct_excep(
            AMI_OrderHandlingServiceExceptionHolder ami_orderHandlingServiceExceptionHolder)
    {

        //Null implementation of method cancelOrderForAllUsersByProduct
    }


    /**
     * Blank implementation of method
     *
     * @author Cognizant Technology Solutions.
     */
    public void cancelOrdersForUserByOrsId(ServerResponseStruct[] serverResponseStructs)
    {

        //Null implementation of method cancelOrdersForUserByOrsId
    }


    /**
     * Blank implementation of method
     *
     * @author Cognizant Technology Solutions.
     */
    public void cancelOrdersForUserByOrsId_excep(
            AMI_OrderHandlingServiceExceptionHolder ami_orderHandlingServiceExceptionHolder)
    {

        //Null implementation of method cancelOrdersForUserByOrsId
    }

    /*
      ***************************************************
      * PAR methods
      * *************************************************
     */
    public void acceptManualCancelReport()
    {
        throw new RuntimeException("Method acceptManualCancelReport Not supported");

    }

    public void acceptManualCancelReport_excep(AMI_OrderHandlingServiceExceptionHolder ami_orderHandlingServiceExceptionHolder)
    {
        throw new RuntimeException("Method acceptManualCancelReport_excep Not supported");

    }

    public void acceptManualFillReportV2()
    {
        throw new RuntimeException("Method acceptManualFillReportV2 Not supported");

    }

    public void acceptManualFillReportV2_excep(AMI_OrderHandlingServiceExceptionHolder ami_orderHandlingServiceExceptionHolder)
    {
        throw new RuntimeException("Method acceptManualFillReportV2_excep Not supported");

    }

    public void acceptPrintRequest()
    {
        throw new RuntimeException("Method acceptPrintRequest Not supported");

    }

    public void acceptPrintRequest_excep(AMI_OrderHandlingServiceExceptionHolder ami_orderHandlingServiceExceptionHolder)
    {
        throw new RuntimeException("Method acceptPrintRequest_excep Not supported");

    }

    public void acceptManualOrderReturn()
    {
        throw new RuntimeException("Method acceptManualOrderReturn Not supported");

    }

    public void acceptManualOrderReturn_excep(AMI_OrderHandlingServiceExceptionHolder ami_orderHandlingServiceExceptionHolder)
    {
        throw new RuntimeException("Method acceptManualOrderReturn_excep Not supported");

    }

    public void acceptVolumeChange()
    {
        throw new RuntimeException("Method acceptVolumeChange Not supported");

    }

    public void acceptVolumeChange_excep(AMI_OrderHandlingServiceExceptionHolder ami_orderHandlingServiceExceptionHolder)
    {
        throw new RuntimeException("Method acceptVolumeChange_excep Not supported");

    }

    public void acceptPrintCancel()
    {
        throw new RuntimeException("Method acceptPrintCancel Not supported");

    }

    public void acceptPrintCancel_excep(AMI_OrderHandlingServiceExceptionHolder ami_orderHandlingServiceExceptionHolder)
    {
        throw new RuntimeException("Method acceptPrintCancel_excep Not supported");

    }

    public void acceptPrintCancelReplace()
    {
        throw new RuntimeException("Method acceptPrintCancelReplace Not supported");

    }

    public void acceptPrintCancelReplace_excep(AMI_OrderHandlingServiceExceptionHolder ami_orderHandlingServiceExceptionHolder)
    {
        throw new RuntimeException("Method acceptPrintCancelReplace_excep Not supported");

    }

    public void acceptManualFillTimeout()
    {
        throw new RuntimeException("Method acceptManualFillTimeout Not supported");

    }

    public void acceptManualFillTimeout_excep(AMI_OrderHandlingServiceExceptionHolder ami_orderHandlingServiceExceptionHolder)
    {
        throw new RuntimeException("Method acceptManualFillTimeout_excep Not supported");

    }

    public void acceptManualOrderReturnTimeout()
    {
        throw new RuntimeException("Method acceptManualOrderReturnTimeout Not supported");

    }

    public void acceptManualOrderReturnTimeout_excep(AMI_OrderHandlingServiceExceptionHolder ami_orderHandlingServiceExceptionHolder)
    {
        throw new RuntimeException("Method acceptManualOrderReturnTimeout_excep Not supported");

    }

    public void acceptManualQuote()
    {
        throw new RuntimeException("Method acceptManualQuote Not supported");

    }

    public void acceptManualQuote_excep(AMI_OrderHandlingServiceExceptionHolder ami_orderHandlingServiceExceptionHolder)
    {
        throw new RuntimeException("Method acceptManualQuote_excep Not supported");

    }

    public void cancelManualQuote()
    {
        throw new RuntimeException("Method cancelManualQuote Not supported");

    }

    public void cancelManualQuote_excep(AMI_OrderHandlingServiceExceptionHolder ami_orderHandlingServiceExceptionHolder)
    {
        throw new RuntimeException("Method cancelManualQuote_excep Not supported");

    }


}//EOF
