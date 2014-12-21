package com.cboe.application.userServices;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.shared.consumer.UserSessionLogoutProcessor;
import com.cboe.application.shared.consumer.UserSessionLogoutProcessorFactory;
import com.cboe.domain.logout.LogoutServiceFactory;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiTraderActivity.ActivityHistoryStruct;
import com.cboe.idl.constants.OperationTypes;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.SessionManager;
import com.cboe.interfaces.application.UserEnablement;
import com.cboe.interfaces.application.UserSessionLogoutCollector;
import com.cboe.interfaces.businessServices.UserActivityService;
import com.cboe.util.ExceptionBuilder;
import com.cboe.util.event.EventChannelAdapterFactory;

/**
 * User History Service implementation
 *
 * @author Dean Grippo
 * @version 08/02/2000
 */

public class UserHistoryImpl extends BObject implements com.cboe.interfaces.application.UserHistory, UserSessionLogoutCollector
{
    private SessionManager          sessionManager          = null;
    private UserActivityService      userActivityService      = null;
    private UserSessionLogoutProcessor logoutProcessor;


    /**
     * UserHistoryImpl constructor
     */
    public UserHistoryImpl()
    {
        super();
    }

    /**
     * Sets the SessionManager for this User History Service.  This operation is
     * primarily performed by the UserHistoryHomeImpl.
     *
     * @author Dean Grippo
     * @param  sessionMgr
     */
    public void setSessionManager(SessionManager sessionMgr)
    {
        if (sessionMgr != null)
        {
            sessionManager = sessionMgr;
            logoutProcessor = UserSessionLogoutProcessorFactory.create(this);
            EventChannelAdapterFactory.find().addChannelListener(this, logoutProcessor, sessionMgr);
            LogoutServiceFactory.find().addLogoutListener(sessionMgr, this);
        }
    }




    /**
     * Sends the getTraderProductActivityByTime request to the User History Service.
     *
     * @author Dean Grippo
     *
     * @param productKey
     * @param startTime
     * @param direction
     * @returns   ActivityHistoryStruct
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public ActivityHistoryStruct getTraderProductActivityByTime( String sessionName, int productKey,
                                                                 com.cboe.idl.cmiUtil.DateTimeStruct startTime,
                                                                 short direction )
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling getTraderProductActivityByTime for " + sessionManager);
        }

        try
        {
            ProductStruct product = sessionManager.getProductQuery().getProductByKey(productKey);

            int classKey = product.productKeys.classKey;
             //verify if it is enabled
            getUserEnablementService().verifyUserEnablement(sessionName, classKey, OperationTypes.USERHISTORY);

        }
            catch( NotFoundException e )
            {
                throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
            }

         String userId = sessionManager.getValidSessionProfileUser().userId;

         return getUserActivityService().getTraderProductActivityByTime( sessionName, userId, productKey, startTime, direction );
    }

    /**
     * Sends the getTraderClassActivityByTime request to the User History Service.
     *
     * @author Dean Grippo
     *
     * @param classKey
     * @param startTime
     * @param direction
     * @returns   ActivityHistoryStruct
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public ActivityHistoryStruct getTraderClassActivityByTime( String sessionName,
                                                               int classKey,
                                                               com.cboe.idl.cmiUtil.DateTimeStruct startTime,
                                                               short direction )
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling getTraderClassActivityByTime for " + sessionManager);
        }

        //verify if it is enabled
        getUserEnablementService().verifyUserEnablement(sessionName, classKey, OperationTypes.USERHISTORY);

         String userId = sessionManager.getValidSessionProfileUser().userId;

         return getUserActivityService().getTraderClassActivityByTime( sessionName, userId, classKey, startTime, direction );
    }



    /**
     * Returns the instance of the User History service.
     *
     * @author Dean Grippo
     */
    private UserActivityService getUserActivityService()
    {
        if (userActivityService == null )
        {
            userActivityService = ServicesHelper.getUserActivityService();
        }
        return userActivityService;
    }


    public void acceptUserSessionLogout() {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptUserSessionLogout for " + sessionManager);
        }
        // Do any individual service clean up needed for logout
        EventChannelAdapterFactory.find().removeListenerGroup(this);
        LogoutServiceFactory.find().logoutComplete(sessionManager,this);
        logoutProcessor.setParent(null);
        logoutProcessor = null;

        sessionManager = null;
        userActivityService      = null;
    }

    protected UserEnablement getUserEnablementService()
    throws SystemException, CommunicationException, AuthorizationException
    {
       return ServicesHelper.getUserEnablementService(sessionManager.getValidSessionProfileUser().userId
                                                      , sessionManager.getValidSessionProfileUser().userAcronym.exchange
                                                      , sessionManager.getValidSessionProfileUser().userAcronym.acronym);
    }

}
