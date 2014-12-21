package com.cboe.infrastructureServices.systemsManagementService.asynchronousClient;

import com.cboe.infrastructureServices.interfaces.adminService.Property;
import com.cboe.infrastructureServices.interfaces.adminService.Command;
import com.cboe.infrastructureServices.interfaces.adminEvents.*;
import com.cboe.infrastructureServices.systemsManagementService.adminEvents.AdminRequestConsumer;
import com.cboe.infrastructureServices.systemsManagementService.adminEvents.AdminRequestPublisher;
import com.cboe.infrastructureServices.systemsManagementService.adminEvents.AdminResponseConsumer;
import com.cboe.infrastructureServices.systemsManagementService.adminEvents.AdminResponseConsumerImpl;
import com.cboe.infrastructureServices.timeService.TimeService;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.eventService.EventService;
import com.cboe.infrastructureServices.eventService.ConsumerFilter;
import com.cboe.util.Timer;

import java.util.Map;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Eric J. Fredericks
 * Date: Oct 3, 2003
 * Time: 11:28:01 AM
 * To change this template use Options | File Templates.
 */

class AdminServiceAsyncClientImpl implements AdminServiceClientAsync, AdminResponseConsumer, Timer
{
    private static int ADMIN_RESPONSE_TIMER = 0; 
    private static final String className = "AdminServiceAsyncClientImpl";
    private static final String classPrefix = className + " -> ";
    private EventService cachedEventService;
    private String uniqueProcessIdentifier;
    private String eventChannelName;
    private TimeService timeService;
    private Map requestMap;
    private int cachedRequestId;
    private int maxTimeOut;
    private int defaultTimeOut;
    private AdminRequestConsumer requestPublisher;
    private AdminResponseConsumerImpl eventChannelConsumer;

    public AdminServiceAsyncClientImpl()
    {
        maxTimeOut = 10000; // default is 10 seconds
        defaultTimeOut = 10000;
    }
    
    public synchronized void setMaxTimeout(int timeout)
    {
        if(timeout <= 0)
        {
            throw new IllegalArgumentException("Max timeout must be greater than 0");
        }
        
        maxTimeOut = timeout;
        
        if(defaultTimeOut > getMaxTimeout())
        {
            Log.information(classPrefix + "New max timeout is less than default timeout; setting default timeout to new max timeout");
            defaultTimeOut = maxTimeOut;
        }
    }

    public int getMaxTimeout()
    {
        return maxTimeOut;
    }
    
    public synchronized void setDefaultTimeout(int defaultTimeout)
    {
        if(defaultTimeout <= 0)
        {
            throw new IllegalArgumentException("Default timeout must be greater than 0");
        }
        
        if(defaultTimeout > getMaxTimeout())
        {
            throw new IllegalArgumentException("Default timeout cannot be greater than max timeout");
        }
        this.defaultTimeOut = defaultTimeout;
    }
    
    public int getDefaultTimeout()
    {
        return defaultTimeOut;
    }
    
    
    void initialize(String uniqueProcessIdentifier, String channelName) throws Exception
    {
        if(Log.isDebugOn())
        {
            Log.debug("Initializing " + className + "....");
        }
        
        // uniqueProcessIdentifier and channelName validated by framework
        this.uniqueProcessIdentifier = uniqueProcessIdentifier;
        this.eventChannelName = channelName;
        
        timeService = FoundationFramework.getInstance().getTimeService();
        cachedEventService = FoundationFramework.getInstance().getEventService();

        requestMap = Collections.synchronizedMap(new HashMap());
        
        long currentTime = timeService.getCurrentDateTime();
        cachedRequestId = (int) currentTime;

        // Get AdminRequestPublisher
        if(Log.isDebugOn())
        {
            Log.debug(classPrefix + "Initializing request publication mechanism...");
        }
        String interfaceRepId = AdminRequestEventConsumerHelper.id();
        org.omg.CORBA.Object obj = getEventService().getTypedEventChannelSupplierStub(channelName, interfaceRepId);
        AdminRequestEventConsumer eventConsumer = AdminRequestEventConsumerHelper.narrow(obj);
        
        if(eventConsumer == null)
        {
            if(Log.isDebugOn())
            {
                Log.debug(classPrefix + "Initialization failed -- narrowed event consumer for request publisher is null.");
            }
            throw new NullPointerException(classPrefix + "narrowed AdminRequestEventConsumer for publishing requests is null.");
        }

        requestPublisher = new AdminRequestPublisher(eventConsumer);
        
        if(Log.isDebugOn())
        {
            Log.debug(classPrefix + "Request publisher initialized");
        }


        // Create the consumer for responses on the event channel.
        // This consumer will delegate all calls to this object.
        if(Log.isDebugOn())
        {
            Log.debug(classPrefix + "Creating response consumer...");
        }
        
        eventChannelConsumer = new AdminResponseConsumerImpl(this);

        if(Log.isDebugOn())
        {
            Log.debug(classPrefix + "Applying filters for responses directed to unique id=" + uniqueProcessIdentifier);
        }
        
        ConsumerFilter inclusionFilter;
        String methodName;
        String constraint;
        
        interfaceRepId = AdminResponseEventConsumerHelper.id();
        
        methodName = AdminServiceMethodNames.DEFN_PROPERTIES + "Return";
        constraint = "$." + methodName + ".routingParameters.requestSource=='" + uniqueProcessIdentifier + "'";
        inclusionFilter = cachedEventService.createNewInclusionFilter(eventChannelConsumer, interfaceRepId, methodName, constraint, channelName);
        cachedEventService.applyFilter(inclusionFilter);
        
        methodName = AdminServiceMethodNames.DEFN_PROPERTY + "Return";
        constraint = "$." + methodName + ".routingParameters.requestSource=='" + uniqueProcessIdentifier + "'";
        inclusionFilter = cachedEventService.createNewInclusionFilter(eventChannelConsumer, interfaceRepId, methodName, constraint, channelName);
        cachedEventService.applyFilter(inclusionFilter);

        methodName = AdminServiceMethodNames.EXECUTE_COMMAND + "Return";
        constraint = "$." + methodName + ".routingParameters.requestSource=='" + uniqueProcessIdentifier + "'";
        inclusionFilter = cachedEventService.createNewInclusionFilter(eventChannelConsumer, interfaceRepId, methodName, constraint, channelName);
        cachedEventService.applyFilter(inclusionFilter);

        methodName = AdminServiceMethodNames.GET_ALL_COMMANDS + "Return";
        constraint = "$." + methodName + ".routingParameters.requestSource=='" + uniqueProcessIdentifier + "'";
        inclusionFilter = cachedEventService.createNewInclusionFilter(eventChannelConsumer, interfaceRepId, methodName, constraint, channelName);
        cachedEventService.applyFilter(inclusionFilter);

        methodName = AdminServiceMethodNames.GET_COMMAND + "Return";
        constraint = "$." + methodName + ".routingParameters.requestSource=='" + uniqueProcessIdentifier + "'";
        inclusionFilter = cachedEventService.createNewInclusionFilter(eventChannelConsumer, interfaceRepId, methodName, constraint, channelName);
        cachedEventService.applyFilter(inclusionFilter);

        methodName = AdminServiceMethodNames.GET_PROPERTIES + "Return";
        constraint = "$." + methodName + ".routingParameters.requestSource=='" + uniqueProcessIdentifier + "'";
        inclusionFilter = cachedEventService.createNewInclusionFilter(eventChannelConsumer, interfaceRepId, methodName, constraint, channelName);
        cachedEventService.applyFilter(inclusionFilter);

        methodName = AdminServiceMethodNames.GET_PROPERTY_VALUE + "Return";
        constraint = "$." + methodName + ".routingParameters.requestSource=='" + uniqueProcessIdentifier + "'";
        inclusionFilter = cachedEventService.createNewInclusionFilter(eventChannelConsumer, interfaceRepId, methodName, constraint, channelName);
        cachedEventService.applyFilter(inclusionFilter);
        
        methodName = "catchException";
        constraint = "$." + methodName + ".routingParameters.requestSource=='" + uniqueProcessIdentifier + "'";
        inclusionFilter = cachedEventService.createNewInclusionFilter(eventChannelConsumer, interfaceRepId, methodName, constraint, channelName);
        cachedEventService.applyFilter(inclusionFilter);
        
        if(Log.isDebugOn())
        {
            Log.debug(classPrefix + "Filters applied; connecting response consumer...");
        }
                
        // Finally, connect the consumer
        getEventService().connectTypedNotifyChannelConsumer(channelName, interfaceRepId, eventChannelConsumer);
        
        if(Log.isDebugOn())
        {
            Log.debug(classPrefix + "Event channel consumer for response consumption connected...");
        }
    }
    
    private EventService getEventService()
    {
        if(cachedEventService == null)
        {
            cachedEventService = FoundationFramework.getInstance().getEventService();
        }
        
        return cachedEventService;
    }
    
    private String getUniqueProcessIdentifier()
    {
        return uniqueProcessIdentifier;
    }
    
    
    private synchronized int getNextRequestId()
    {
        int nextId = cachedRequestId;
        cachedRequestId++;
        return nextId;
    }
    
    // utility method for testing
    int getLastRequestId()
    {
        return cachedRequestId - 1;
    }
    
    // utility method for testing
    int getRequestMapSize()
    {
        return requestMap.size();
    }
    
    private void validate(String destination, int timeout, AdminBaseCallback callback)
    {
        if(callback == null)
        {
            if(Log.isDebugOn())
            {
                Log.debug(classPrefix + "Callback provided is null!  Throwing IllegalArgumentException");
            }
            throw new IllegalArgumentException("Callback cannot be null");
        }
        
        if(destination == null)
        {
            if(Log.isDebugOn())
            {
                Log.debug(classPrefix + "Destination string provided is null!  Throwing IllegalArgumentException");
            }
            throw new IllegalArgumentException("Destination cannot be null");
        }
        
        if(timeout <= 0)
        {
            if(Log.isDebugOn())
            {
                Log.debug(classPrefix + "Invalid timeout (must be > 0); value=" + timeout + "... Throwing IllegalArgumentException");
            }
            throw new IllegalArgumentException("Timeout must be greater than 0");
        }

        if(timeout > maxTimeOut)
        {
            if(Log.isDebugOn())
            {
                Log.debug(classPrefix + "Invalid timeout (must be < maxTimeout=" + maxTimeOut +"); value=" + timeout + "... Throwing IllegalArgumentException");
            }
            throw new IllegalArgumentException("Timeout cannot be greater than " + maxTimeOut);
        }
    }
    
    public void defineProperties(String destination, Property[] properties, DefinePropertiesCallback callback)
    {
        defineProperties(destination, getDefaultTimeout(), properties, callback);
    }

    public void defineProperties(String destination, int timeout, Property[] properties, DefinePropertiesCallback callback)    
    {
        if(Log.isDebugOn())
        {
            Log.debug(classPrefix + "Received defineProperties request.");
        }
        validate(destination, timeout, callback);
        
        if(properties == null)
        {
            if(Log.isDebugOn())
            {
                Log.debug(classPrefix + "Properties on defineProperties request cannot be null");
            }
            throw new IllegalArgumentException("Properties cannot be null");
        }
        
        int id = getNextRequestId();
        DefinePropertiesRequest request =  new DefinePropertiesRequest(id, destination, timeout, AdminServiceMethodNames.DEFN_PROPERTIES, callback);
        Integer mapKey = new Integer(id);
        
        requestMap.put(mapKey, request);

        AdminRoutingStruct routingParameters = new AdminRoutingStruct();
        routingParameters.requestDestination = destination;
        routingParameters.requestId = id;
        routingParameters.requestSource = getUniqueProcessIdentifier();
        
        int timerId = timeService.enqueue(ADMIN_RESPONSE_TIMER, timeout, mapKey , this);
        request.setTimerId(timerId);

        if(Log.isDebugOn())
        {
            Log.debug(classPrefix + "Publishing defineProperties request, requestId=" + id + " timerId=" + timerId);
        }
        requestPublisher.defineProperties(routingParameters, properties);
    }

    public void defineProperty(String destination, Property property, DefinePropertyCallback callback)
    {
        defineProperty(destination, getDefaultTimeout(), property, callback);
    }

    public void defineProperty(String destination, int timeout, Property property, DefinePropertyCallback callback)
    {
        if(Log.isDebugOn())
        {
            Log.debug(classPrefix + "Received defineProperty request.");
        }
        validate(destination, timeout, callback);
        
        if(property == null)
        {
            if(Log.isDebugOn())
            {
                Log.debug(classPrefix + "Property on defineProperty request cannot be null");
            }
            throw new IllegalArgumentException("Property cannot be null");
        }
        
        int id = getNextRequestId();
        DefinePropertyRequest request =  new DefinePropertyRequest(id, destination, timeout, AdminServiceMethodNames.DEFN_PROPERTY, callback);
        Integer mapKey = new Integer(id);
        
        requestMap.put(mapKey, request);

        AdminRoutingStruct routingParameters = new AdminRoutingStruct();
        routingParameters.requestDestination = destination;
        routingParameters.requestId = id;
        routingParameters.requestSource = getUniqueProcessIdentifier();

        int timerId = timeService.enqueue(ADMIN_RESPONSE_TIMER, timeout, mapKey , this);
        request.setTimerId(timerId);

        if(Log.isDebugOn())
        {
            Log.debug(classPrefix + "Publishing defineProperty request, requestId=" + id + " timerId=" + timerId);
        }
        requestPublisher.defineProperty(routingParameters, property);
    }

    public void executeCommand(String destination, Command command, ExecuteCommandCallback callback)
    {
        executeCommand(destination, getDefaultTimeout(), command, callback);
    }

    public void executeCommand(String destination, int timeout, Command command, ExecuteCommandCallback callback)
    {
        if(Log.isDebugOn())
        {
            Log.debug(classPrefix + "Received executeCommand request.");
        }

        validate(destination, timeout, callback);
        
        if(command == null)
        {
            if(Log.isDebugOn())
            {
                Log.debug(classPrefix + "Command on executeCommand request cannot be null");
            }
            throw new IllegalArgumentException("Command cannot be null");
        }
        
        int id = getNextRequestId();
        ExecuteCommandRequest request =  new ExecuteCommandRequest(id, destination, timeout, AdminServiceMethodNames.EXECUTE_COMMAND, callback);
        Integer mapKey = new Integer(id);
        
        requestMap.put(mapKey, request);

        AdminRoutingStruct routingParameters = new AdminRoutingStruct();
        routingParameters.requestDestination = destination;
        routingParameters.requestId = id;
        routingParameters.requestSource = getUniqueProcessIdentifier();

        int timerId = timeService.enqueue(ADMIN_RESPONSE_TIMER, timeout, mapKey , this);
        request.setTimerId(timerId);

        if(Log.isDebugOn())
        {
            Log.debug(classPrefix + "Publishing executeCommand request, requestId=" + id + " timerId=" + timerId);
        }
        requestPublisher.executeCommand(routingParameters, command);
    }

    public void getAllCommands(String destination, GetAllCommandsCallback callback)
    {
        getAllCommands(destination, getDefaultTimeout(), callback);
    }

    public void getAllCommands(String destination, int timeout, GetAllCommandsCallback callback)
    {
        if(Log.isDebugOn())
        {
            Log.debug(classPrefix + "Received getAllCommands request.");
        }

        validate(destination, timeout, callback);
        
        int id = getNextRequestId();
        GetAllCommandsRequest request =  new GetAllCommandsRequest(id, destination, timeout, AdminServiceMethodNames.GET_ALL_COMMANDS, callback);
        Integer mapKey = new Integer(id);
        
        requestMap.put(mapKey, request);

        AdminRoutingStruct routingParameters = new AdminRoutingStruct();
        routingParameters.requestDestination = destination;
        routingParameters.requestId = id;
        routingParameters.requestSource = getUniqueProcessIdentifier();

        int timerId = timeService.enqueue(ADMIN_RESPONSE_TIMER, timeout, mapKey , this);
        request.setTimerId(timerId);

        if(Log.isDebugOn())
        {
            Log.debug(classPrefix + "Publishing getAllCommands request, requestId=" + id + " timerId=" + timerId);
        }
        requestPublisher.getAllCommands(routingParameters);
    }

    public void getCommand(String destination, String commandName, GetCommandCallback callback)
    {
        getCommand(destination, getDefaultTimeout(), commandName, callback);
    }

    public void getCommand(String destination, int timeout, String commandName, GetCommandCallback callback)
    {
        if(Log.isDebugOn())
        {
            Log.debug(classPrefix + "Received getCommand request.");
        }

        validate(destination, timeout, callback);
        
        if(commandName == null)
        {
            if(Log.isDebugOn())
            {
                Log.debug(classPrefix + "Command name on getCommand request cannot be null");
            }
            throw new IllegalArgumentException("Command name cannot be null");
        }
        
        int id = getNextRequestId();
        GetCommandRequest request =  new GetCommandRequest(id, destination, timeout, AdminServiceMethodNames.GET_COMMAND, callback);
        Integer mapKey = new Integer(id);
        
        requestMap.put(mapKey, request);

        AdminRoutingStruct routingParameters = new AdminRoutingStruct();
        routingParameters.requestDestination = destination;
        routingParameters.requestId = id;
        routingParameters.requestSource = getUniqueProcessIdentifier();

        int timerId = timeService.enqueue(ADMIN_RESPONSE_TIMER, timeout, mapKey , this);
        request.setTimerId(timerId);

        if(Log.isDebugOn())
        {
            Log.debug(classPrefix + "Publishing getCommand request, requestId=" + id + " timerId=" + timerId);
        }
        requestPublisher.getCommand(routingParameters, commandName);
    }

    public void getProperties(String destination, GetPropertiesCallback callback)
    {
        getProperties(destination, getDefaultTimeout(), callback);
    }

    public void getProperties(String destination, int timeout, GetPropertiesCallback callback)
    {
        if(Log.isDebugOn())
        {
            Log.debug(classPrefix + "Received getProperties request.");
        }

        validate(destination, timeout, callback);
        
        int id = getNextRequestId();
        GetPropertiesRequest request =  new GetPropertiesRequest(id, destination, timeout, AdminServiceMethodNames.GET_PROPERTIES, callback);
        Integer mapKey = new Integer(id);
        
        requestMap.put(mapKey, request);

        AdminRoutingStruct routingParameters = new AdminRoutingStruct();
        routingParameters.requestDestination = destination;
        routingParameters.requestId = id;
        routingParameters.requestSource = getUniqueProcessIdentifier();

        int timerId = timeService.enqueue(ADMIN_RESPONSE_TIMER, timeout, mapKey , this);
        request.setTimerId(timerId);

        if(Log.isDebugOn())
        {
            Log.debug(classPrefix + "Publishing getProperties request, requestId=" + id + " timerId=" + timerId);
        }
        requestPublisher.getProperties(routingParameters);
    }

    public void getPropertyValue(String destination, String propertyName, GetPropertyValueCallback callback)
    {
        getPropertyValue(destination, getDefaultTimeout(), propertyName, callback);
    }
    
    public void getPropertyValue(String destination, int timeout, String propertyName, GetPropertyValueCallback callback)
    {
        if(Log.isDebugOn())
        {
            Log.debug(classPrefix + "Received getPropertyValue request.");
        }

        validate(destination, timeout, callback);
        
        if(propertyName == null)
        {
            if(Log.isDebugOn())
            {
                Log.debug(classPrefix + "Property name on getPropertyValue request cannot be null");
            }
            throw new IllegalArgumentException("Property name cannot be  null");
        }
        
        int id = getNextRequestId();
        GetPropertyValueRequest request =  new GetPropertyValueRequest(id, destination, timeout, AdminServiceMethodNames.GET_PROPERTY_VALUE, callback); 
        Integer mapKey = new Integer(id);
        
        requestMap.put(mapKey, request);

        AdminRoutingStruct routingParameters = new AdminRoutingStruct();
        routingParameters.requestDestination = destination;
        routingParameters.requestId = id;
        routingParameters.requestSource = getUniqueProcessIdentifier();

        int timerId = timeService.enqueue(ADMIN_RESPONSE_TIMER, timeout, mapKey , this);
        request.setTimerId(timerId);

        if(Log.isDebugOn())
        {
            Log.debug(classPrefix + "Publishing getPropertyValue request, requestId=" + id + " timerId=" + timerId);
        }
        requestPublisher.getPropertyValue(routingParameters, propertyName);
    }
    
    public void catchException(AdminRoutingStruct adminRoutingStruct, AdminExceptionStruct adminExceptionStruct)
    {
        if(Log.isDebugOn())
        {
            String exceptionName = null;
            switch(adminExceptionStruct.type)
            {
                case AdminServiceExceptionTypes.INVALID_PARAMETER:
                    exceptionName = "InvalidParameter";
                    break;
                case AdminServiceExceptionTypes.INVALID_PROPERTY_NAME:
                    exceptionName = "InvalidPropertyName";
                    break;
                case AdminServiceExceptionTypes.INVALID_PROPERTY_VALUE:
                    exceptionName = "InvalidPropertyValue";
                    break;
                case AdminServiceExceptionTypes.MULTIPLE_EXCEPTIONS:
                    exceptionName = "MultipleExceptions";
                    break;
                case AdminServiceExceptionTypes.PROPERTY_NOT_FOUND:
                    exceptionName = "PropertyNotFound";
                    break;
                case AdminServiceExceptionTypes.RUNTIME_EXCEPTION:
                    exceptionName = "RuntimeException";
                    break;
                case AdminServiceExceptionTypes.UNSUPPORTED_COMMAND:
                    exceptionName = "UnsupportedCommand";
                    break;
                case AdminServiceExceptionTypes.UNSUPPORTED_PROPERTY:
                    exceptionName = "UnsupportedProperty";
                    break;
                default:
                    exceptionName = "UNKNOWN EXCEPTION";
                    
            }

            Log.debug(classPrefix + "Consumed exception for requestId=" + adminRoutingStruct.requestId + " exception=" + exceptionName + " message=" + adminExceptionStruct.message);
        }
        
        AdminRequestHolder request = null;
        boolean requestMismatch = false; // variable used to flag if request is mismatched
                                         // so logging can be done outside of synchronized block
        boolean noMatchingRequest = false; // variable used to flag if request doesn't exist
        
        synchronized(requestMap)
        {
            Integer mapKey = new Integer(adminRoutingStruct.requestId);
            
            request = (AdminRequestHolder) requestMap.get(mapKey);
            
            if(request == null)
            {
                noMatchingRequest = true;
            }
            else if(!adminExceptionStruct.methodName.equals(request.getMethodName()))
            {
                requestMismatch = true;
                // leave request in map, it will either timeout or the broken server will respond eventually.
            }
            else
            {
                requestMap.remove(mapKey);
            }
        }
    
        // Log errors...
        if(noMatchingRequest)
        {
            Log.alarm(classPrefix + "No matching request for requestId=" + adminRoutingStruct.requestId);
            return;
        }

        // Log errors...
        if(requestMismatch)
        {
           Log.alarm(classPrefix + "Received exception for requestId=" +
                    adminRoutingStruct.requestId +
                    " for a method different from that of the request: requestMethodName=" +
                    request.getMethodName() + " responseMethodName=" +
                    adminExceptionStruct.methodName + " ...  Request is still pending (will either timeout or get response from server).");
            return;
        }
        
        // Log errors...
        if(request == null)
        {
            if(Log.isDebugOn())
            {
                Log.debug(classPrefix + "Received exception event for non-existent request (may have previously timed out): requestId=" +
                        adminRoutingStruct.requestId + " responseSource=" + adminRoutingStruct.requestDestination);
            }
            return;
        }
        
        int timerId = request.getTimerId();
        boolean success = timeService.delete(timerId);
        if(Log.isDebugOn())
        {
            if(success)
            {
                Log.debug(classPrefix + "Cancelled timer for requestId=" + request.getRequestId() + " timerId=" + timerId);
            }
            else
            {
                Log.debug(classPrefix + "Unable to cancel timer for requestId=" + request.getRequestId() + " timerId=" + timerId);
            }
        }

        request.processException(adminExceptionStruct);
    }

    public void definePropertiesReturn(AdminRoutingStruct adminRoutingStruct)
    {
        AdminRequestHolder request = (AdminRequestHolder) requestMap.remove(new Integer(adminRoutingStruct.requestId));
        
        if(request == null)
        {
            if(Log.isDebugOn())
            {
                Log.debug(classPrefix + "Received definePropertiesReturn event for non-existent request (may have previously received another response or timed out): requestId=" +
                        adminRoutingStruct.requestId + " responseSource=" + adminRoutingStruct.requestDestination);
            }
            return;
        }
        
        int timerId = request.getTimerId();
        boolean success = timeService.delete(timerId);
        if(Log.isDebugOn())
        {
            if(success)
            {
                Log.debug(classPrefix + "Cancelled timer for requestId=" + request.getRequestId() + " timerId=" + timerId);
            }
            else
            {
                Log.debug(classPrefix + "Unable to cancel timer for requestId=" + request.getRequestId() + " timerId=" + timerId);
            }
        }
        
        ((DefinePropertiesRequest) request).returned();
    }

    public void definePropertyReturn(AdminRoutingStruct adminRoutingStruct)
    {
        AdminRequestHolder request = (AdminRequestHolder) requestMap.remove(new Integer(adminRoutingStruct.requestId));
        
        if(request == null)
        {
            if(Log.isDebugOn())
            {
                Log.debug(classPrefix + "Received definePropertyReturn event for non-existent request (may have previously received another response or timed out): requestId=" +
                        adminRoutingStruct.requestId + " responseSource=" + adminRoutingStruct.requestDestination);
            }
            return;
        }
        
        int timerId = request.getTimerId();
        boolean success = timeService.delete(timerId);
        if(Log.isDebugOn())
        {
            if(success)
            {
                Log.debug(classPrefix + "Cancelled timer for requestId=" + request.getRequestId() + " timerId=" + timerId);
            }
            else
            {
                Log.debug(classPrefix + "Unable to cancel timer for requestId=" + request.getRequestId() + " timerId=" + timerId);
            }
        }

        ((DefinePropertyRequest) request).retruned();
    }

    public void executeCommandReturn(AdminRoutingStruct adminRoutingStruct, boolean result, Command command)
    {
        AdminRequestHolder request = (AdminRequestHolder) requestMap.remove(new Integer(adminRoutingStruct.requestId));
        
        if(request == null)
        {
            if(Log.isDebugOn())
            {
                Log.debug(classPrefix + "Received executeCommandReturn event for non-existent request (may have previously received another response or timed out): requestId=" +
                        adminRoutingStruct.requestId + " responseSource=" + adminRoutingStruct.requestDestination);
            }
            return;
        }

        int timerId = request.getTimerId();
        boolean success = timeService.delete(timerId);
        if(Log.isDebugOn())
        {
            if(success)
            {
                Log.debug(classPrefix + "Cancelled timer for requestId=" + request.getRequestId() + " timerId=" + timerId);
            }
            else
            {
                Log.debug(classPrefix + "Unable to cancel timer for requestId=" + request.getRequestId() + " timerId=" + timerId);
            }
        }

        ((ExecuteCommandRequest) request).returned(command, result);
    }

    public void getAllCommandsReturn(AdminRoutingStruct adminRoutingStruct, Command[] commands)
    {
        AdminRequestHolder request = (AdminRequestHolder) requestMap.remove(new Integer(adminRoutingStruct.requestId));
        
        if(request == null)
        {
            if(Log.isDebugOn())
            {
                Log.debug(classPrefix + "Received getAllCommandsReturn event for non-existent request (may have previously received another response or timed out): requestId=" +
                        adminRoutingStruct.requestId + " responseSource=" + adminRoutingStruct.requestDestination);
            }
            return;
        }

        int timerId = request.getTimerId();
        boolean success = timeService.delete(timerId);
        if(Log.isDebugOn())
        {
            if(success)
            {
                Log.debug(classPrefix + "Cancelled timer for requestId=" + request.getRequestId() + " timerId=" + timerId);
            }
            else
            {
                Log.debug(classPrefix + "Unable to cancel timer for requestId=" + request.getRequestId() + " timerId=" + timerId);
            }
        }

        ((GetAllCommandsRequest) request).returned(commands);
    }

    public void getCommandReturn(AdminRoutingStruct adminRoutingStruct, Command command)
    {
        AdminRequestHolder request = (AdminRequestHolder) requestMap.remove(new Integer(adminRoutingStruct.requestId));
        
        if(request == null)
        {
            if(Log.isDebugOn())
            {
                Log.debug(classPrefix + "Received getCommandReturn event for non-existent request (may have previously received another response or timed out): requestId=" +
                        adminRoutingStruct.requestId + " responseSource=" + adminRoutingStruct.requestDestination);
            }
            return;
        }

        int timerId = request.getTimerId();
        boolean success = timeService.delete(timerId);
        if(Log.isDebugOn())
        {
            if(success)
            {
                Log.debug(classPrefix + "Cancelled timer for requestId=" + request.getRequestId() + " timerId=" + timerId);
            }
            else
            {
                Log.debug(classPrefix + "Unable to cancel timer for requestId=" + request.getRequestId() + " timerId=" + timerId);
            }
        }

        ((GetCommandRequest) request).returned(command);
    }

    public void getPropertiesReturn(AdminRoutingStruct adminRoutingStruct, Property[] properties)
    {
        AdminRequestHolder request = (AdminRequestHolder) requestMap.remove(new Integer(adminRoutingStruct.requestId));
        
        if(request == null)
        {
            if(Log.isDebugOn())
            {
                Log.debug(classPrefix + "Received getPropertiesReturn event for non-existent request (may have previously received another response or timed out): requestId=" +
                        adminRoutingStruct.requestId + " responseSource=" + adminRoutingStruct.requestDestination);
            }
            return;
        }
        
        int timerId = request.getTimerId();
        boolean success = timeService.delete(timerId);
        if(Log.isDebugOn())
        {
            if(success)
            {
                Log.debug(classPrefix + "Cancelled timer for requestId=" + request.getRequestId() + " timerId=" + timerId);
            }
            else
            {
                Log.debug(classPrefix + "Unable to cancel timer for requestId=" + request.getRequestId() + " timerId=" + timerId);
            }
        }
        
        ((GetPropertiesRequest) request).returned(properties);
    }

    public void getPropertyValueReturn(AdminRoutingStruct adminRoutingStruct, String value)
    {
        AdminRequestHolder request = (AdminRequestHolder) requestMap.remove(new Integer(adminRoutingStruct.requestId));
        
        if(request == null)
        {
            if(Log.isDebugOn())
            {
                Log.debug(classPrefix + "Received getPropertyValueReturn event for non-existent request (may have previously received another response or timed out): requestId=" +
                        adminRoutingStruct.requestId + " responseSource=" + adminRoutingStruct.requestDestination);
            }
            return;
        }

        int timerId = request.getTimerId();
        boolean success = timeService.delete(timerId);
        if(Log.isDebugOn())
        {
            if(success)
            {
                Log.debug(classPrefix + "Cancelled timer for requestId=" + request.getRequestId() + " timerId=" + timerId);
            }
            else
            {
                Log.debug(classPrefix + "Unable to cancel timer for requestId=" + request.getRequestId() + " timerId=" + timerId);
            }
        }
        
        ((GetPropertyValueRequest) request).returned(value);
    }
    
    public void dequeue(int type, Object mapKey)
    {
        // the timer has popped
        AdminRequestHolder request = (AdminRequestHolder) requestMap.remove(mapKey);
        if(request != null)
        {
            if(Log.isDebugOn())
            {
                Log.debug(classPrefix + "Processing timeout for requestId=" + request.getRequestId() + " timerId=" + request.getTimerId());
            }
            request.processTimeout();
        }
        else
        {
            if(Log.isDebugOn())
            {
                Log.debug(classPrefix + "Unable to find request for request timeout -- response may have beaten timeout -- requestId =" + mapKey);
            }
        }
    }
}
