// Copyright (c) 1999-2011 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.application.activityService;


import com.cboe.interfaces.application.SessionManager;
import com.cboe.interfaces.application.ActivityService;
import com.cboe.interfaces.internalBusinessServices.ActivityHistoryService;
import com.cboe.interfaces.internalBusinessServices.ActivityHistoryServiceHome;
import com.cboe.idl.terminalActivity.HistoryResultStruct;
import com.cboe.idl.terminalActivity.HistoryRequestStruct;
import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.session.BaseSessionManager;

@SuppressWarnings({"ObjectToString"})
public class ActivityServiceImpl
        extends BObject
        implements ActivityService
{
    protected ActivityService activityService;
    protected ActivityHistoryService activityHistoryService;
    private static String createName = "ActivityServiceHomeImpl";
    protected SessionManager currentSession;   
    

    @SuppressWarnings({"ThisEscapedInObjectConstruction"})
    public ActivityServiceImpl(SessionManager sessionManager)
    {
        super();
        currentSession = sessionManager;
    }

    /**
     * Creates an valid instance of the service.
     *
     * @param name name of this object
     */

    public void create(String name)
    {
        super.create(name);
        

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

    
    public BaseSessionManager getSessionManager()
    {
        return currentSession;
    }
    
    private ActivityHistoryService getServerActivityHistoryService()
    {
        if (activityHistoryService == null)
        {
            try
            {
                ActivityHistoryServiceHome
                        home = (ActivityHistoryServiceHome) HomeFactory.getInstance().findHome(ActivityHistoryServiceHome.PROXY_HOME_NAME);

                activityHistoryService = (ActivityHistoryService) home.find();
            }
            catch (CBOELoggableException e)
            {
                throw new NullPointerException("Could not find ActivityHistoryServiceHome");
            }
        }
        return activityHistoryService;
    }
    
    
    /*
     * *******************************************************************
     * Activity History Query 
     * *******************************************************************
     */
     public HistoryResultStruct queryActivityHistory(
            HistoryRequestStruct historyRequestStruct,
            String queryID,
            DateStruct date,
            int relativeDay,
            long startTime,
            short direction)
    throws SystemException,CommunicationException,AuthorizationException,DataValidationException
    {
         Log.information(this, "calling queryActivityHistory for:" + currentSession.toString());
         HistoryResultStruct historyResultStruct = null;         
         activityHistoryService = getServerActivityHistoryService();
         if (activityHistoryService != null)
         { 
             historyResultStruct = activityHistoryService.queryActivityHistory(
                    historyRequestStruct,queryID,date,relativeDay,startTime,direction);
             Log.information(this, "finish calling queryActivityHistory for:" + currentSession.toString());
         }else
         {
             Log.debug(this, "Activity History Service Proxy is null.");
             
         }  
         return historyResultStruct;
    }


    public HistoryResultStruct queryActivityTradeHistory(
           HistoryRequestStruct historyRequestStruct,
           String queryID,
           DateStruct date,
           int relativeDay,
           long startTime,
           short direction)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        Log.information(this, "calling queryActivityTradeHistory for:" + currentSession.toString());
        HistoryResultStruct historyResultStruct = null;
        activityHistoryService = getServerActivityHistoryService();
        if (activityHistoryService != null)
        {
            historyResultStruct = activityHistoryService.queryActivityTradeHistory(
                   historyRequestStruct,queryID,date,relativeDay,startTime,direction);
            
        }else
        {
            Log.debug(this, "Activity History Service Proxy is null.");
           
        }   
        return historyResultStruct;
        
    }

}
