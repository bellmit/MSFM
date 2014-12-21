package com.cboe.proxy.businessServicesClient;

import com.cboe.idl.businessServices.UserActivityService;
import com.cboe.idl.cmiErrorCodes.NotFoundCodes;
import com.cboe.idl.cmiTraderActivity.*;
import com.cboe.idl.cmiUtil.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

import com.cboe.exceptions.*;
import com.cboe.util.*;
import com.cboe.domain.util.*;

import java.util.*;

/**
 * This class is a routing proxy that delgates the incoming requests
 * to the appropriate order book service. The class maintains a table which
 * maps every service to its respective process ( route ).
 *
**/

public 	class UserActivityServiceClientRoutingProxy
	extends NonGlobalServiceClientRoutingProxy
	implements com.cboe.interfaces.businessServices.UserActivityService
{
    /**
     * Default constructor
     **/
    public	UserActivityServiceClientRoutingProxy()
    {
    }

    /**
     *  Forwards request to delegate based on classKey
     **/
    public ActivityHistoryStruct getTraderClassActivityByTime(
            String sessionName, String userId, int classKey, DateTimeStruct startTime, short direction)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        UserActivityService service = (UserActivityService) getServiceByClass(sessionName, classKey);
        return service.getTraderClassActivityByTime(sessionName, userId, classKey, startTime, direction);
    }

    /**
     * Forwards request to delegate based on productKey
     **/
    public ActivityHistoryStruct getTraderProductActivityByTime(
            String sessionName, String userId, int productKey, DateTimeStruct startTime, short direction)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        UserActivityService service = (UserActivityService) getServiceByProduct(sessionName, productKey);
        return service.getTraderProductActivityByTime(sessionName, userId, productKey, startTime, direction);
    }

    /**
     * Return the service helper class name
     */
    protected String getHelperClassName()
    {
        return "com.cboe.idl.businessServices.UserActivityServiceHelper";
    }

    /**
     * Returns the most recent activity time on any trade server.
     *
     * @see com.cboe.idl.businessServices.UserActivityServiceOperations#getLastUserActivityTime(java.lang.String)
     */
    public DateTimeStruct getLastUserActivityTime(String userid) throws SystemException, CommunicationException, AuthorizationException, NotFoundException
    {
        long maxTimeMillis = 0;
        int num = 0, numNotFound = 0;
        StringBuilder activity = new StringBuilder(150);
        for (String serviceRoute : routeMap.keySet())
        {
            if (Log.isDebugOn())
            {
                Log.debug( this, "Sending request to service (getLastUserActivityTime): " + serviceRoute );
            }
            UserActivityService targetService = (UserActivityService)routeMap.get(serviceRoute);
            num++;
            try
            {
                DateTimeStruct lastTime = targetService.getLastUserActivityTime(userid);
                long lastTimeMillis = DateWrapper.convertToMillis(lastTime);
                activity.setLength(0);
                if (lastTimeMillis > maxTimeMillis)
                {
                    activity.append("getLastUserActivityTime(").append(userid)
                            .append(") - most recent last activity so far is ").append(lastTimeMillis)
                            .append(" (").append(new Date(lastTimeMillis)).append(")")
                            .append(" from ").append(serviceRoute);
                    Log.information(this, activity.toString());
                    maxTimeMillis = lastTimeMillis;
                }
                else
                {
                    activity.append("getLastUserActivityTime(").append(userid)
                            .append(") - (not the most recent) last activity was at ")
                            .append(lastTimeMillis).append(" (").append(new Date(lastTimeMillis)).append(")")
                            .append(" from ").append(serviceRoute);
                    Log.information(this, activity.toString());
                }
            }
            catch (NotFoundException ex)
            {
                numNotFound++;
                Log.information(this, "getLastUserActivityTime("+userid+") - no recent user activity reported by " + serviceRoute);
            }
        }
        if (numNotFound == num)
        {
            throw ExceptionBuilder.notFoundException("No activity for user '" + userid + "' found on any UAS service.", NotFoundCodes.RESOURCE_DOESNT_EXIST);
        }
        activity.setLength(0);
        activity.append("getLastUserActivityTime(").append(userid)
                .append(") - called ").append(num)
                .append(" remote services.  Max last activity millis = ").append(maxTimeMillis)
                .append(" (").append(new Date(maxTimeMillis)).append(")");
        Log.information(this, activity.toString());
        return DateWrapper.convertToDateTime(maxTimeMillis);
    }
}
