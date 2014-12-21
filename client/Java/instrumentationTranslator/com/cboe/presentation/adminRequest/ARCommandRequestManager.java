//
// -----------------------------------------------------------------------------------
// Source file: ARCommandRequestManager.java
//
// PACKAGE: com.cboe.presentation.adminRequest
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.adminRequest;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.*;

import com.cboe.exceptions.AlreadyExistsException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.NotAcceptedException;

import com.cboe.interfaces.domain.Delimeter;
import com.cboe.interfaces.instrumentation.adminRequest.ARCommand;
import com.cboe.interfaces.instrumentation.adminRequest.ARCommandManagerStateListener;
import com.cboe.interfaces.instrumentation.adminRequest.ARCommandRequest;
import com.cboe.interfaces.instrumentation.adminRequest.ARCommandRequestStatusListener;
import com.cboe.interfaces.instrumentation.adminRequest.ARCommandResponse;
import com.cboe.interfaces.instrumentation.adminRequest.ExecutionResult;

import com.cboe.util.ExceptionBuilder;

import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.common.logging.GUILoggerHome;

import com.cboe.infrastructureServices.interfaces.adminService.Command;
import com.cboe.infrastructureServices.interfaces.adminService.DataItem;

/**
 * This class is the Manager to create and send Admin Request Commands.
 */
public class ARCommandRequestManager
{
    private static final String SECURITY_OVERRIDE_PROPERTY = "ARCommandRequestSecurityOverride";

    private static ARCommandRequestManager ourInstance = new ARCommandRequestManager();

    private final List<ARCommandRequest> activeRequests;
    private final Map<String, List<ARCommandRequest>> activeRequestByORB;

    private final List<ARCommandRequest> completedRequests;

    private final Object lockObject;

    private final ThreadGroup asyncThreadGroup;
    private int threadCounter;

    private final List<ARCommandManagerStateListener> listeners;

    private static final ARCommandRequest[] EMPTY_REQUEST_ARRAY = new ARCommandRequest[0];

    private final Random idGenerator;

    /**
     * Gets the singleton instance of this manager.
     */
    public static ARCommandRequestManager getInstance()
    {
        return ourInstance;
    }

    private ARCommandRequestManager()
    {
        activeRequests = new ArrayList<ARCommandRequest>(10);
        activeRequestByORB = new ConcurrentHashMap<String, List<ARCommandRequest>>(10);
        completedRequests = new ArrayList<ARCommandRequest>(20);
        lockObject = new Object();
        listeners = new ArrayList<ARCommandManagerStateListener>(5);
        asyncThreadGroup = new ThreadGroup("ARCommandRequestManager");
        threadCounter = 0;
        idGenerator = new Random();
    }

    /**
     * Builds a new immutable ARCommandRequest, containing the pass arguments.
     * @param command to send
     * @param orbNames to send to
     * @param argumentValues to with command
     * @param shouldShowStatus true if the status should be displayed, false if it should be hidden
     * @return ARCommandRequest object representing the passed arguments.
     */
    public ARCommandRequest buildNewRequest(ARCommand command, String[] orbNames,
                                            String[] argumentValues, int timeoutMillis,
                                            boolean shouldShowStatus)
    {
        return new ARCommandRequestImpl(command, orbNames, argumentValues,
                                        timeoutMillis, shouldShowStatus);
    }

    /**
     * Begins processing a newly built ARCommandRequest.
     * This implementation will process the request asynchronously, in parallel, to each ORB name.
     * The method will return almost immediately, after security checks.
     * @param request to process
     * @throws AlreadyExistsException will be thrown if a request is currently active for any
     * of the same ORB names that on are on the new request
     * @throws AuthorizationException will be thrown if the request is not allowed due to
     * security restrictions.
     * @throws NotAcceptedException will be thrown if a request is submitted that has already
     * been processed.
     */
    public void processNewRequestAsync(ARCommandRequest request)
            throws AlreadyExistsException, AuthorizationException, NotAcceptedException
    {
        //noinspection InstanceofInterfaces
        if(request instanceof ARCommandRequestImpl)
        {
            final ARCommandRequestImpl requestImpl = (ARCommandRequestImpl) request;

            checkSecurity(request);
            checkRequestState(request);

            synchronized(lockObject)
            {
                checkORBRestrictions(request);
                updateActiveCollections(request);
                fireRequestStartedEvent(request);
            }

            final long uniqueId = idGenerator.nextLong();

            Runnable runnable = new Runnable()
            {
                public void run()
                {
                    requestImpl.addStatusListener(new ActiveToCompleteListener(uniqueId));
                    requestImpl.send(true);
                }
            };

            String threadName = asyncThreadGroup.getName() + ':' + uniqueId + ':' + threadCounter++;
            Thread runner = new Thread(asyncThreadGroup, runnable, threadName);
            runner.start();
        }
        else
        {
            throw new UnsupportedOperationException("ARCommandRequest not built by " +
                                                    "ARCommandRequestManager. Please use " +
                                                    "buildNewRequest() method.");
        }
    }

    /**
     * Begins processing a newly built ARCommandRequest.
     * This implementation will process the request synchronously, to each ORB name, one at a time.
     * The method will not return until all responses have returned.
     * @param request to process
     * @return all responses from the request
     * @throws AlreadyExistsException will be thrown if a request is currently active for any
     * of the same ORB names that on are on the new request
     * @throws AuthorizationException will be thrown if the request is not allowed due to
     * security restrictions.
     * @throws NotAcceptedException will be thrown if a request is submitted that has already
     * been processed.
     */
    public ARCommandResponse[] processNewRequestSync(ARCommandRequest request)
            throws AlreadyExistsException, AuthorizationException, NotAcceptedException
    {
        //noinspection InstanceofInterfaces
        if(request instanceof ARCommandRequestImpl)
        {
            ARCommandRequestImpl requestImpl = (ARCommandRequestImpl) request;

            checkSecurity(request);
            checkRequestState(request);

            synchronized(lockObject)
            {
                checkORBRestrictions(request);
                updateActiveCollections(request);
                fireRequestStartedEvent(request);
            }

            long uniqueId = idGenerator.nextLong();

            requestImpl.addStatusListener(new ActiveToCompleteListener(uniqueId));

            requestImpl.send(false);

            //noinspection UnnecessaryLocalVariable
            ARCommandResponse[] responses = requestImpl.getAllResponses();

            return responses;
        }
        else
        {
            throw new UnsupportedOperationException("ARCommandRequest not built by " +
                                                    "ARCommandRequestManager. Please use " +
                                                    "buildNewRequest() method.");
        }
    }

    /**
     * Determines if this manager has any active requests being processed.
     * @return true if at least one active request is being processed, false otherwise.
     */
    public boolean containsActiveRequest()
    {
        synchronized(lockObject)
        {
            return !activeRequests.isEmpty();
        }
    }

    /**
     * Determines if this manager has an active request being processed, for orbName.
     * @param orbName to determine whether active requests exist for
     * @return true if at least one active request is being processed for orbName,
     *  false otherwise.
     */
    public boolean containsActiveRequest(String orbName)
    {
        boolean response = false;
        synchronized(lockObject)
        {
            List<ARCommandRequest> activeForORB = activeRequestByORB.get(orbName);
            if(activeForORB != null && !activeForORB.isEmpty())
            {
                response = true;
            }
        }
        return response;
    }

    /**
     * Gets the count of active requests currently being processed
     * @return count of active requests being processed
     */
    public int getCountActiveRequests()
    {
        synchronized(lockObject)
        {
            return activeRequests.size();
        }
    }

    /**
     * Gets all the active requests currently being processed
     * @return array of active requests being processed. Will return a zero-length array
     * if none are active
     */
    public ARCommandRequest[] getAllActiveRequests()
    {
        ARCommandRequest[] allActiveRequests;
        synchronized(lockObject)
        {
            allActiveRequests = new ARCommandRequest[activeRequests.size()];
            allActiveRequests = activeRequests.toArray(allActiveRequests);
        }
        return allActiveRequests;
    }

    /**
     * Gets all the active requests currently being processed, for orbName
     * @param orbName to find active requests for
     * @return array of active requests being processed for orbName. Will return a zero-length array
     * if none are active.
     */
    public ARCommandRequest[] getActiveRequests(String orbName)
    {
        ARCommandRequest[] allActiveRequests;
        synchronized(lockObject)
        {
            List<ARCommandRequest> activeForORB = activeRequestByORB.get(orbName);
            if(activeForORB != null)
            {
                allActiveRequests = new ARCommandRequest[activeForORB.size()];
                allActiveRequests = activeForORB.toArray(allActiveRequests);
            }
            else
            {
                allActiveRequests = EMPTY_REQUEST_ARRAY;
            }
        }
        return allActiveRequests;
    }

    /**
     * Gets all the completed requests that have already been processed
     * @return array of completed requests that have already been processed.
     * Will return a zero-length array if none have been completed.
     */
    public ARCommandRequest[] getCompletedRequests()
    {
        ARCommandRequest[] allCompletedRequests;
        synchronized(lockObject)
        {
            allCompletedRequests = new ARCommandRequest[completedRequests.size()];
            allCompletedRequests = completedRequests.toArray(allCompletedRequests);
        }
        return allCompletedRequests;
    }

    /**
     * Adds the listener for callbacks to specific events
     * @param listener to add
     */
    public void addStateListener(ARCommandManagerStateListener listener)
    {
        synchronized(listeners)
        {
            if(!listeners.contains(listener))
            {
                listeners.add(listener);
            }
        }
    }

    /**
     * Removes the listener for callbacks to specific events
     * @param listener to remove
     */
    public void removeStateListener(ARCommandManagerStateListener listener)
    {
        synchronized(listeners)
        {
            listeners.remove(listener);
        }
    }

    protected void fireRequestStartedEvent(ARCommandRequest request)
    {
        ARCommandManagerStateListener[] localListeners;
        synchronized(listeners)
        {
            localListeners = listeners.toArray(new ARCommandManagerStateListener[listeners.size()]);
        }

        for(ARCommandManagerStateListener listener : localListeners)
        {
            //noinspection CatchGenericClass
            try
            {
                listener.requestStarted(request);
            }
            catch(Exception e)
            {
                GUILoggerHome.find().exception("Exception Sending Event from " +
                                               "ARCommandRequestManager", e);
            }
        }
    }

    protected void fireRequestCompletedEvent(ARCommandRequest request)
    {
        ARCommandManagerStateListener[] localListeners;
        synchronized(listeners)
        {
            localListeners = listeners.toArray(new ARCommandManagerStateListener[listeners.size()]);
        }

        for(ARCommandManagerStateListener listener : localListeners)
        {
            //noinspection CatchGenericClass
            try
            {
                listener.requestCompleted(request);
            }
            catch(Exception e)
            {
                GUILoggerHome.find().exception("Exception Sending Event from " +
                                               "ARCommandRequestManager", e);
            }
        }
    }

    @SuppressWarnings({"NonBooleanMethodNameMayNotStartWithQuestion"})
    protected void checkSecurity(ARCommandRequest request) throws AuthorizationException
    {
        boolean shouldOverride =
                Boolean.parseBoolean(System.getProperty(SECURITY_OVERRIDE_PROPERTY));

        if(!shouldOverride)
        {
            ARCommand command = request.getCommand();
            if(command.getSeverity() == ARCommand.Severity.NOT_DEFINED ||
               command.getSeverity() == ARCommand.Severity.RESTRICTED)
            {
                throw ExceptionBuilder
                        .authorizationException("AR Command has a Severity of: " +
                                                command.getSeverity() +
                                                ". This Severity is not currently allowed.", 0);
            }
        }
    }

    @SuppressWarnings({"NonBooleanMethodNameMayNotStartWithQuestion"})
    protected void checkORBRestrictions(ARCommandRequest request) throws AlreadyExistsException
    {
        String[] requestOrbs = request.getAllOrbNames();
        for(String requestOrb : requestOrbs)
        {
            if(containsActiveRequest(requestOrb))
            {
                throw ExceptionBuilder
                        .alreadyExistsException("Cannot send AR Request for ORB: " + requestOrb +
                                                ". AR Request is currently active for " +
                                                "this ORB.", 0);
            }
        }
    }

    @SuppressWarnings({"NonBooleanMethodNameMayNotStartWithQuestion"})
    private void checkRequestState(ARCommandRequest request) throws NotAcceptedException
    {
        if(request.isRequestStarted())
        {
            throw ExceptionBuilder.notAcceptedException("Cannot submit an ARCommandRequest that " +
                                                        "has already been processed.", 0);
        }
    }

    protected void updateActiveCollections(ARCommandRequest request)
    {
        activeRequests.add(request);

        String[] requestOrbs = request.getAllOrbNames();
        for(String requestOrb : requestOrbs)
        {
            List<ARCommandRequest> activeForORB = activeRequestByORB.get(requestOrb);
            if(activeForORB == null)
            {
                activeForORB = new ArrayList<ARCommandRequest>(3);
                activeRequestByORB.put(requestOrb, activeForORB);
            }
            activeForORB.add(request);
        }
    }

    protected void moveActiveToComplete(ARCommandRequest request)
    {
        int index = activeRequests.indexOf(request);
        ARCommandRequest removedRequest = activeRequests.remove(index);

        completedRequests.add(removedRequest);

        String[] requestOrbs = removedRequest.getAllOrbNames();
        for(String requestOrb : requestOrbs)
        {
            List<ARCommandRequest> activeForORB = activeRequestByORB.get(requestOrb);
            if(activeForORB != null)
            {
                activeForORB.remove(removedRequest);
                if(activeForORB.isEmpty())
                {
                    activeRequestByORB.remove(requestOrb);
                }
            }
        }
    }

    private class ActiveToCompleteListener implements ARCommandRequestStatusListener
    {
        private long uniqueId;

        private ActiveToCompleteListener(long uniqueId)
        {
            this.uniqueId = uniqueId;
        }

        /**
         * A call to this method is made when the request has completely finished processing.
         * @param request that finished processing
         */
        public void requestCompleted(ARCommandRequest request)
        {
            synchronized(lockObject)
            {
                moveActiveToComplete(request);
                request.removeStatusListener(this);
                fireRequestCompletedEvent(request);
            }
        }

        public void requestStarted(ARCommandRequest request)
        {
            log(request);
        }

        public void responseReceived(ARCommandRequest request, ARCommandResponse response)
        {
            log(response);
        }

        private void log(ARCommandRequest request)
        {
            ARCommand command = request.getCommand();
            String[] allOrbs = request.getAllOrbNames();
            String[] args = request.getArgumentValues();

            StringBuilder msg = new StringBuilder(300);
            msg.append("ARCmdRequest-ID=").append(uniqueId);
            msg.append(';').append(Delimeter.PROPERTY_DELIMETER);

            try
            {
                InetAddress localHost = InetAddress.getLocalHost();
                msg.append("Src=").append(localHost.getHostAddress());
                msg.append(" (").append(localHost.getHostName()).append(");");
                msg.append(Delimeter.PROPERTY_DELIMETER);
            }
            catch(UnknownHostException e)
            {
                DefaultExceptionHandlerHome.find().process(e);
            }

            msg.append("UsrNm=").append(System.getProperty("user.name"));
            msg.append(';').append(Delimeter.PROPERTY_DELIMETER);
            msg.append("Dest=").append(Arrays.toString(allOrbs));
            msg.append(';').append(Delimeter.PROPERTY_DELIMETER);
            msg.append("SrcTimestamp=").append(request.getStartedTime().toString());
            msg.append(';').append(Delimeter.PROPERTY_DELIMETER);
            msg.append("CmdNm=").append(command.getFullName());
            msg.append(';').append(Delimeter.PROPERTY_DELIMETER);
            msg.append("Args=").append(Arrays.toString(args));
            msg.append(';').append(Delimeter.PROPERTY_DELIMETER);

            GUILoggerHome.find().nonRepudiationAudit(msg.toString());
        }

        private void log(ARCommandResponse response)
        {
            StringBuilder msg = new StringBuilder(300);
            msg.append("ARCmdResponse-ID=").append(uniqueId);
            msg.append(';').append(Delimeter.PROPERTY_DELIMETER);
            msg.append("Dest=").append(response.getOrbName());
            msg.append(';').append(Delimeter.PROPERTY_DELIMETER);
            msg.append("SrcTimestamp=").append(response.getResponseTime().toString());
            msg.append(';').append(Delimeter.PROPERTY_DELIMETER);
            msg.append("Success=").append(response.isSuccess());
            msg.append(';').append(Delimeter.PROPERTY_DELIMETER);

            //noinspection CatchGenericClass
            try
            {
                ExecutionResult result = response.getExecutionResult();
                msg.append("ResultValues=[");
                Command command = result.getCommandResult();
                DataItem[] dataItems = command.retValues;
                for(int i = 0; i < dataItems.length; i++)
                {
                    DataItem dataItem = dataItems[i];
                    msg.append(dataItem.name).append('=').append(dataItem.value);
                    if(i + 1 < dataItems.length)
                    {
                        msg.append("; ");
                    }
                }
                msg.append("];").append(Delimeter.PROPERTY_DELIMETER);
            }
            catch(Exception e)
            {
                msg.append("Exception=").append(e.getMessage());
                msg.append(';').append(Delimeter.PROPERTY_DELIMETER);
            }

            GUILoggerHome.find().nonRepudiationAudit(msg.toString());
        }
    }
}
