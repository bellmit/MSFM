package com.cboe.proxy.businessServicesClient;

import com.cboe.application.shared.TransactionTimingRegistration;
import com.cboe.application.shared.TransactionTimingUtil;
import com.cboe.application.util.OrderCallSnapshot;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.idl.businessServices.LightOrderHandlingService;
import com.cboe.instrumentationService.transactionTimingCommon.TransactionTimer;


public class LightOrderHandlingServiceClientRoutingProxy extends NonGlobalServiceClientRoutingProxy
        implements com.cboe.interfaces.businessServices.EnhancedOrderHandlingService
{


    /**
     * Default constructor
     */

    public LightOrderHandlingServiceClientRoutingProxy()
    {
    }

    /**
     * Default create method
     */
    public void create(String name)
    {
        super.create(name);

    }


    public void shutdown()
    {
    }

    /**
     * Return the Service Helper class name
     *
     * @return String, the service helper class name related to this proxy
     */
    protected String getHelperClassName()
    {
        return "com.cboe.idl.businessServices.LightOrderHandlingServiceHelper";
    }


    public byte[] acceptOrder(byte[] input, int productKey, String sessionName, long entityId)
            throws CommunicationException,
                   DataValidationException,
                   TransactionFailedException,
                   NotAcceptedException,
                   AuthorizationException,
                   SystemException
    {

        byte[] returnBuffer = null;
        boolean exceptionThrown = true;
        TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOrderRoutingProxyEmitPoint(), entityId, TransactionTimer.Enter);
        try
        {
            LightOrderHandlingService service = (LightOrderHandlingService) getServiceByProduct(sessionName, productKey);
            OrderCallSnapshot.startServerCall();
            returnBuffer = service.rcv(input);
            OrderCallSnapshot.endServerCall();
            exceptionThrown = false;
        }
        finally
        {
            TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOrderRoutingProxyEmitPoint(), entityId,
                                                     exceptionThrown ? TransactionTimer.LeaveWithException : TransactionTimer.Leave);
        }
        return returnBuffer;
    }

    public byte[] rcv(byte[] input)
            throws CommunicationException,
                   DataValidationException,
                   TransactionFailedException,
                   NotAcceptedException,
                   AuthorizationException,
                   SystemException
    {
        return null;
    }


    public byte[] acceptLightOrderCancelRequest(byte[] input, int productKey, String sessionName, long entityId) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        byte[] returnBuffer = null;
        boolean exceptionThrown = true;
        TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getCancelOrderRoutingProxyEmitPoint(), entityId, TransactionTimer.Enter);
        try
        {
            LightOrderHandlingService service = (LightOrderHandlingService) getServiceByProduct(sessionName, productKey);
            OrderCallSnapshot.startServerCall();
            returnBuffer = service.rcv(input);
            OrderCallSnapshot.endServerCall();

            exceptionThrown = false;
        }
        finally
        {
            TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getCancelOrderRoutingProxyEmitPoint(), entityId,
                                                     exceptionThrown ? TransactionTimer.LeaveWithException : TransactionTimer.Leave);
        }
        return returnBuffer;
    }

    public byte[] acceptLightOrderCancelRequestById(byte[] input, int productKey, String sessionName, long entityId) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        return acceptLightOrderCancelRequest(input, productKey, sessionName, entityId);
    }


}//EOF



