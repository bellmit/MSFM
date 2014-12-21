//
// -----------------------------------------------------------------------------------
// Source file: ARCommandRequestImpl.java
//
// PACKAGE: com.cboe.presentation.adminRequest
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.adminRequest;

import java.util.*;

import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;

import com.cboe.interfaces.domain.dateTime.DateTime;
import com.cboe.interfaces.instrumentation.adminRequest.ARCommand;
import com.cboe.interfaces.instrumentation.adminRequest.ARCommandRequest;
import com.cboe.interfaces.instrumentation.adminRequest.ARCommandRequestStatusListener;
import com.cboe.interfaces.instrumentation.adminRequest.ARCommandResponse;
import com.cboe.interfaces.instrumentation.adminRequest.ExecuteCommandService;
import com.cboe.interfaces.instrumentation.adminRequest.ExecutionResult;
import com.cboe.interfaces.presentation.processes.CBOEProcess;
import com.cboe.interfaces.presentation.common.exceptionHandling.DefaultExceptionHandler;

import com.cboe.util.ExceptionBuilder;

import com.cboe.presentation.api.InstrumentationTranslatorFactory;
import com.cboe.presentation.common.dateTime.DateTimeImpl;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;

import com.cboe.infrastructureServices.interfaces.adminService.Command;

class ARCommandRequestImpl implements ARCommandRequest
{
    private ARCommand command;
    private List<String> orbNames;
    private List<String> argumentValues;
    private int timeoutMillis;
    private DateTime startDateTime;

    private boolean isActive;
    private boolean isStarted;
    private boolean shouldShowStatus;

    private Map<String, ARCommandResponse> responses;

    @SuppressWarnings({"FieldAccessedSynchronizedAndUnsynchronized"})
    private int responseCount;

    private final Object responseLockObject = new Object();

    private final List<ARCommandRequestStatusListener> listeners =
            new ArrayList<ARCommandRequestStatusListener>(5);

    private ARCommandRequestImpl()
    {
        isActive = false;
        isStarted = false;

        orbNames = new ArrayList<String>(5);
        argumentValues = new ArrayList<String>(5);
    }

    ARCommandRequestImpl(ARCommand command, String[] orbNames, String[] argumentValues,
                         int timeoutMillis, boolean shouldShowStatus)
    {
        this();
        this.command = command;
        this.shouldShowStatus = shouldShowStatus;
        this.timeoutMillis = timeoutMillis;
        setOrbNames(orbNames);
        setArgumentValues(argumentValues);

        responses = new HashMap<String, ARCommandResponse>(orbNames.length);
        responseCount = 0;
    }

    /**
     * Provides public access to clone the request.
     * @return a clone of this request
     * @throws CloneNotSupportedException will be thrown if some class up the hierarchy cannot be
     * cloned
     */
    @SuppressWarnings({"AccessingNonPublicFieldOfAnotherObject"})
    public Object clone() throws CloneNotSupportedException
    {
        ARCommandRequestImpl newObject = (ARCommandRequestImpl) super.clone();
        newObject.command = getCommand();
        newObject.setOrbNames(getAllOrbNames());
        newObject.setArgumentValues(getArgumentValues());
        newObject.startDateTime = null;
        newObject.isActive = false;
        newObject.isStarted = false;
        newObject.responses.clear();
        newObject.listeners.clear();
        return newObject;
    }

    /**
     * Adds the listener for callbacks to specific events
     * @param listener to add
     */
    public void addStatusListener(ARCommandRequestStatusListener listener)
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
    public void removeStatusListener(ARCommandRequestStatusListener listener)
    {
        synchronized(listeners)
        {
            listeners.remove(listener);
        }
    }

    /**
     * Determines if this request should show the status to the user
     * @return true if the status should be displayed, false if it should be hidden
     */
    public boolean shouldShowStatus()
    {
        return shouldShowStatus;
    }

    /**
     * Gets the ARCommand that this request if for.
     * @return ARCommand this request should send
     */
    public ARCommand getCommand()
    {
        return command;
    }

    /**
     * Gets the time that this request was started for processing.
     * @return DateTime that represents when this request was started. May return null if this
     *         request has not been started.
     */
    public DateTime getStartedTime()
    {
        return startDateTime;
    }

    /**
     * Determines if this request has been started and is still active. It will still be active if
     * all responses have not returned yet.
     * @return true if request has been sent, but not all responses have returned.
     */
    public boolean isRequestActive()
    {
        return isActive;
    }

    /**
     * Determines if this request has ever been processed.
     * @return true if request has ever been processed and sent.
     */
    public boolean isRequestStarted()
    {
        return isStarted;
    }

    /**
     * Gets the number of ORB's this request if for
     * @return count of ORB's
     */
    public int getOrbCount()
    {
        return orbNames.size();
    }

    /**
     * Gets the name of the ORB at the index
     * @param index to get ORB name of
     * @return ORB name
     */
    public String getOrbName(int index)
    {
        return orbNames.get(index);
    }

    /**
     * Convenience method to get the CBOEProcess for the ORB name that is at index
     * @param index of ORB name to get as CBOEProcess
     * @return CBOEProcess for the ORB name
     * @throws com.cboe.exceptions.DataValidationException may be thrown if the CBOEProcess could
     * not be obtained
     */
    public CBOEProcess getOrbProcess(int index) throws DataValidationException
    {
        String orbName = getOrbName(index);
        return InstrumentationTranslatorFactory.find().getProcess(orbName, null);
    }

    /**
     * Gets all the ORB names
     * @return all ORB names in this request
     */
    public String[] getAllOrbNames()
    {
        return orbNames.toArray(new String[orbNames.size()]);
    }

    /**
     * Convenience method to get the CBOEProcess'es for all the ORB names
     * @return a sequence of CBOEProcess objects that each represent one of the ORB's in this
     *         request
     * @throws com.cboe.exceptions.DataValidationException may be thrown if the CBOEProcess could
     * not be obtained
     */
    public CBOEProcess[] getAllOrbProcesses() throws DataValidationException
    {
        CBOEProcess[] allProcesses = new CBOEProcess[getOrbCount()];
        for(int i = 0; i < getOrbCount(); i++)
        {
            allProcesses[i] = getOrbProcess(i);
        }
        return allProcesses;
    }

    /**
     * Determines if the orbName exists in this request
     * @param orbName to search for
     * @return true if this request will be sent to orbName, false otherwise
     */
    public boolean containsOrbName(String orbName)
    {
        return orbNames.contains(orbName);
    }

    /**
     * Gets the amount of time that each ORB should wait for a response from a command
     * @return milliseconds to wait for each ORB to respond
     */
    public int getOrbTimeout()
    {
        return timeoutMillis;
    }

    /**
     * Gets the number of argument values to be sent with the request to the ORB's
     * @return count of argument values
     */
    public int getArgumentCount()
    {
        return argumentValues.size();
    }

    /**
     * Gets the argument values at index
     * @param index of argument value to obtain
     * @return argument value at index. Maybe null or empty String.
     */
    public String getArgumentValue(int index)
    {
        return argumentValues.get(index);
    }

    /**
     * Gets all the argument values to be sent with the request to the ORB's
     * @return sequence of all argument values. Any element may be null or empty String.
     */
    public String[] getArgumentValues()
    {
        return argumentValues.toArray(new String[argumentValues.size()]);
    }

    /**
     * Determines if any responses are available.
     * @return true if at least one response is avialable
     */
    public boolean isResponseAvailable()
    {
        synchronized(responseLockObject)
        {
            return !responses.isEmpty();
        }
    }

    /**
     * Determines if a response is available for orbName
     * @param orbName to determine if response has returned for
     * @return true if we have a response for orbName
     * @throws com.cboe.exceptions.DataValidationException will be thrown if this request was not
     * for the orbName
     */
    public boolean isResponseAvailable(String orbName) throws DataValidationException
    {
        if(!containsOrbName(orbName))
        {
            throw ExceptionBuilder.dataValidationException("orbName does not exist in this " +
                                                           "request: " + orbName, 0);
        }
        synchronized(responseLockObject)
        {
            return responses.containsKey(orbName);
        }
    }

    /**
     * Gets all currently received responses.
     * @return all currently received responses. This sequence may not include a response for every
     *         ORB, if not all responses have returned.
     */
    public ARCommandResponse[] getAllResponses()
    {
        ARCommandResponse[] responsesArray;
        synchronized(responseLockObject)
        {
            Collection<ARCommandResponse> values = responses.values();
            responsesArray = new ARCommandResponse[values.size()];
            responsesArray = values.toArray(responsesArray);
        }
        return responsesArray;
    }

    /**
     * Gets the response for orbName
     * @param orbName to get response for
     * @return response for orbName.
     * @throws com.cboe.exceptions.DataValidationException will be thrown if this request was not
     * for the orbName
     * @throws com.cboe.exceptions.NotFoundException will be thrown if a response has not been
     * received for the orbName
     */
    public ARCommandResponse getResponse(String orbName)
            throws NotFoundException, DataValidationException
    {
        synchronized(responseLockObject)
        {
            if(isResponseAvailable(orbName))
            {
                return responses.get(orbName);
            }
            else
            {
                throw ExceptionBuilder.notFoundException("Response is not available yet " +
                                                         "for orbName:" + orbName, 0);
            }
        }
    }

    /**
     * Begins sending the command to the contained ORB's
     * @param parallelMode True if the command should be sent in parallel to all the contained ORB's
     * asynchronously. False if the command should be sent to each ORB, one at a time, and only sent
     * to the next ORB when the previous one responds.
     */
    protected void send(boolean parallelMode)
    {
        if(!isStarted)
        {
            isStarted = true;
            isActive = true;
            startDateTime = new DateTimeImpl(System.currentTimeMillis());

            fireRequestStartedEvent();

            String[] arguments = getArgumentValues();
            Command executeCommand = getCommand().buildExecuteCommand(arguments);

            if(parallelMode)
            {
                sendToAll(executeCommand);
            }
            else
            {
                sendToEach(executeCommand);
            }

            isActive = false;
            fireRequestCompletedEvent();
        }
    }

    private void sendToAll(final Command executeCommand)
    {
        final String[] allOrbNames = getAllOrbNames();

        ThreadGroup threadGroup = new ThreadGroup("ARCommandLaunchThreadGroup:" + toString());
        final Object waitObject = new Object();

        for(final String orbName : allOrbNames)
        {
            Runnable runnable = new Runnable()
            {
                public void run()
                {
                    //noinspection CatchGenericClass
                    try
                    {
                        sendToORB(orbName, executeCommand);
                    }
                    catch(Exception e)
                    {
                        DefaultExceptionHandler handler = DefaultExceptionHandlerHome.find();
                        handler.process(e, "Unexpected exception occurred when sending " +
                                           "AR Command.");
                    }
                    finally
                    {
                        responseCount++;
                    }

                    if(responseCount == allOrbNames.length)
                    {
                        synchronized(waitObject)
                        {
                            //noinspection CallToNotifyInsteadOfNotifyAll,NakedNotify
                            waitObject.notify();
                        }
                    }
                }
            };

            String threadName = threadGroup.getName() + ':' + orbName;
            Thread runner = new Thread(threadGroup, runnable, threadName);
            runner.start();
        }

        synchronized(waitObject)
        {
            while(responseCount != allOrbNames.length)
            {
                //noinspection UnusedCatchParameter
                try
                {
                    waitObject.wait();
                }
                catch(InterruptedException e)
                {
                    //do nothing, checks loop condition again
                }
            }
        }
    }

    private void sendToEach(Command executeCommand)
    {
        String[] allOrbNames = getAllOrbNames();
        for(String orbName : allOrbNames)
        {
            sendToORB(orbName, executeCommand);
        }
    }

    private void sendToORB(String orbName, Command executeCommand)
    {
        ExecuteCommandService service = ARCommandServiceFactory.getExecuteCommandService();
        service.setDestination(orbName);
        service.setTimeout(getOrbTimeout());

        ARCommandResponse response;

        //noinspection CatchGenericClass
        try
        {
            ExecutionResult result = service.executeCommand(executeCommand);
            response = new ARCommandResponseImpl(result, orbName);
        }
        catch(Exception e)
        {
            response = new ARCommandResponseImpl(e, orbName);
        }

        synchronized(responseLockObject)
        {
            responses.put(orbName,  response);
            fireResponseReceived(response);
        }
    }

    protected void setOrbNames(String[] orbNames)
    {
        this.orbNames.clear();
        if(orbNames == null || orbNames.length == 0)
        {
            throw new IllegalArgumentException("orbNames may not be null or empty.");
        }
        for(String orbName : orbNames)
        {
            if(orbName == null || orbName.length() == 0)
            {
                throw new IllegalArgumentException("an orbName element may not be null or empty.");
            }
            this.orbNames.add(orbName);
        }
    }

    protected void setArgumentValues(String[] argumentValues)
    {
        this.argumentValues.clear();
        if(argumentValues != null)
        {
            this.argumentValues.addAll(Arrays.asList(argumentValues));
        }
    }

    protected void fireRequestStartedEvent()
    {
        ARCommandRequestStatusListener[] localListeners;
        synchronized(listeners)
        {
            localListeners = listeners.toArray(new ARCommandRequestStatusListener[listeners.size()]);
        }

        for(ARCommandRequestStatusListener listener : localListeners)
        {
            //noinspection CatchGenericClass
            try
            {
                listener.requestStarted(this);
            }
            catch(Exception e)
            {
                GUILoggerHome.find()
                        .exception("Exception Sending Event from " + "ARCommandRequestImpl", e);
            }
        }
    }

    protected void fireRequestCompletedEvent()
    {
        ARCommandRequestStatusListener[] localListeners;
        synchronized(listeners)
        {
            localListeners =
                    listeners.toArray(new ARCommandRequestStatusListener[listeners.size()]);
        }

        for(ARCommandRequestStatusListener listener : localListeners)
        {
            //noinspection CatchGenericClass
            try
            {
                listener.requestCompleted(this);
            }
            catch(Exception e)
            {
                GUILoggerHome.find()
                        .exception("Exception Sending Event from " + "ARCommandRequestImpl", e);
            }
        }
    }

    protected void fireResponseReceived(ARCommandResponse response)
    {
        ARCommandRequestStatusListener[] localListeners;
        synchronized(listeners)
        {
            localListeners =
                    listeners.toArray(new ARCommandRequestStatusListener[listeners.size()]);
        }

        for(ARCommandRequestStatusListener listener : localListeners)
        {
            //noinspection CatchGenericClass
            try
            {
                listener.responseReceived(this, response);
            }
            catch(Exception e)
            {
                GUILoggerHome.find()
                        .exception("Exception Sending Event from " + "ARCommandRequestImpl", e);
            }
        }
    }
}
