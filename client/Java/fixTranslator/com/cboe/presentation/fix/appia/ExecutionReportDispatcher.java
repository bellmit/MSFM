package com.cboe.presentation.fix.appia;

/**
  * Author: beniwalv
 * Date: Jul 16, 2004
 * Time: 1:57:36 PM
 */

import com.cboe.consumers.callback.OrderStatusConsumerFactory;
import com.cboe.idl.cmiCallback.CMIOrderStatusConsumer;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.presentation.userSession.UserSessionFactoryListener;
import com.cboe.presentation.userSession.UserSessionListener;
import com.cboe.presentation.userSession.UserSessionEvent;
import com.cboe.presentation.fix.quote.QuoteImpl;
import com.cboe.presentation.fix.userSession.FIXUserSessionFactory;
import com.cboe.presentation.threading.APIWorkerImpl;
import com.cboe.presentation.threading.GUIWorkerImpl;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;

import com.javtech.appia.ExecutionReport;
import com.javtech.appia.MessageObject;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Set;
import java.util.Iterator;

/**
 * Top level Handler for Execution Report Messages received from Appia Fix Engine
 */

public class ExecutionReportDispatcher implements FixMessageDispatcher {
    private Object loginLockObject = new Object();

    protected EventChannelAdapter eventChannel;
    protected CMIOrderStatusConsumer ordStatusConsumer;

    private HashMap reportQueue = new HashMap();
    private boolean userLoginComplete = false;
    private UserSessionListener userListener;

    /**
     * Creates a new ExecutionReportDispatcher
     */
    public ExecutionReportDispatcher() {
        super();

        // initialize the consumer
        this.initializeConsumer();
        this.initListeners();
    }
    
    public void dispatch(MessageObject message, FixSessionImpl session) {
        ExecutionReport execReport = (ExecutionReport) message;

        synchronized(loginLockObject)
        {
            // if the login process isn't finished, queue the execReport
            if(!userLoginComplete)
            {
                ArrayList execReportList = (ArrayList)reportQueue.get(session);
                if(execReportList == null)
                {
                    execReportList = new ArrayList();
                    reportQueue.put(session, execReportList);
                }
                execReportList.add(execReport);
                if(GUILoggerHome.find().isDebugOn())
                {
                    GUILoggerHome.find().debug("ExecutionReportDispatcher -- received ExecutionReport before login was complete; adding report to queue, ExecID='"+
                            execReport.ExecID+"'",
                            GUILoggerBusinessProperty.USER_SESSION);
                }
            }
            else
            {
                if (execReport.ClOrdID != null && 
                    QuoteImpl.isAQuoteId(execReport.ClOrdID)) {
                    QuoteExecutionPublisher.processQuoteExecution(session, execReport);
                } else {
                    processOrderExecution(session, execReport);
                }
            }
        }
    }

    /**
     * Process an order execution
     * @param session the FIX session that received the ExecutionReport
     * @param execReport the FIX message
     */
    private void processOrderExecution(FixSessionImpl session, ExecutionReport execReport) {
        // Create the right publisher based on the Execution Report
        ExecutionReportPublisher erp = ExecutionReportPublisherFactory.createPublisher(execReport, session);
   
        // Publish the execution report.
        erp.publishExecutionReport(execReport, session, ordStatusConsumer);
    }

    protected void initListeners()
    {
        FIXUserSessionFactory.addListener(new UserSessionFactoryListener()
        {
            // when the FIXUserSessionFactory is done initializaing, add a listener to the userSession to 
            //    get the LOGGED_IN_EVENT
            // (if the factory is already initialized, then the userSessionFactoryInit() will
            //    be called right away, so we'll still be registered for LOGGED_IN_EVENT)
            public void userSessionFactoryInit()
            {
                synchronized(loginLockObject)
                {
                    if(!FIXUserSessionFactory.findUserSession().isLoggedIn())
                    {
                        userLoginComplete = false;
                        FIXUserSessionFactory.findUserSession().addUserSessionListener(getUserSessionListener());
                    }
                    else
                    {
                        userLoginComplete = true;
                        dispatchQueuedReports();
                    }
                }
            }
        });
    }

    protected UserSessionListener getUserSessionListener()
    {
        if(userListener == null)
        {
            userListener = new UserSessionListener()
            {
                // when the logged_in_event is received, dispatch any execReports that were queued
                public void userSessionChange(UserSessionEvent event)
                {
                    synchronized(loginLockObject)
                    {
                        if(event.getActionType() == event.LOGGED_IN_EVENT)
                        {
                            userLoginComplete = true;
                            dispatchQueuedReports();
                        }
                    }
                }
            };
        }
        return userListener;
    }

    protected void initializeConsumer(){
        eventChannel = EventChannelAdapterFactory.find();
        //TODO - find out what does this do? - eventChannel.setDynamicChannels(true);
        eventChannel.setDynamicChannels(true);
        ordStatusConsumer = OrderStatusConsumerFactory.create(eventChannel);
    }

    private void dispatchQueuedReports()
    {
        if(reportQueue.size() > 0)
        {
            GUIWorkerImpl worker = new GUIWorkerImpl()
            {
                public void execute() throws Exception
                {
                    //userLoginComplete = true;
                    Set sessionSet = reportQueue.keySet();
                    for(Iterator it=sessionSet.iterator(); it.hasNext(); )
                    {
                        FixSessionImpl session = (FixSessionImpl)it.next();
                        ArrayList execReportList = (ArrayList)reportQueue.get(session);
                        for(int i=0; i<execReportList.size(); i++)
                        {
                            MessageObject mo = (MessageObject)execReportList.get(i);
                            if(GUILoggerHome.find().isDebugOn())
                            {
                                GUILoggerHome.find().debug("ExecutionReportDispatcher -- login complete; dispatching report from queue, ExecID='"+
                                        ((ExecutionReport)mo).ExecID+"'",
                                        GUILoggerBusinessProperty.USER_SESSION);
                            }
                            dispatch(mo, session);
                        }
                        execReportList.clear();
                        reportQueue.remove(session);
                    }
                }
            };
            APIWorkerImpl.run(worker);
        }
    }
}
