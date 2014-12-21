package com.cboe.infrastructureServices.systemsManagementService;

import org.omg.PortableServer.Servant;

import com.cboe.infrastructureServices.eventService.ConsumerFilter;
import com.cboe.infrastructureServices.eventService.EventService;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.interfaces.adminEvents.AdminExceptionStruct;
import com.cboe.infrastructureServices.interfaces.adminEvents.AdminRequestEventConsumerHelper;
import com.cboe.infrastructureServices.interfaces.adminEvents.AdminResponseEventConsumer;
import com.cboe.infrastructureServices.interfaces.adminEvents.AdminResponseEventConsumerHelper;
import com.cboe.infrastructureServices.interfaces.adminEvents.AdminRoutingStruct;
import com.cboe.infrastructureServices.interfaces.adminEvents.AdminServiceExceptionTypes;
import com.cboe.infrastructureServices.interfaces.adminEvents.AdminServiceMethodNames;
import com.cboe.infrastructureServices.interfaces.adminService.Command;
import com.cboe.infrastructureServices.interfaces.adminService.CommandHolder;
import com.cboe.infrastructureServices.interfaces.adminService.Property;
import com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.InvalidParameter;
import com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.InvalidPropertyName;
import com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.InvalidPropertyValue;
import com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.MultipleExceptions;
import com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.PropertyNotFound;
import com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.UnsupportedCommand;
import com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.UnsupportedProperty;
import com.cboe.infrastructureServices.systemsManagementService.adminEvents.AdminRequestConsumer;
import com.cboe.infrastructureServices.systemsManagementService.adminEvents.AdminRequestConsumerImpl;
import com.cboe.infrastructureServices.systemsManagementService.adminEvents.AdminResponseConsumer;
import com.cboe.infrastructureServices.systemsManagementService.adminEvents.AdminResponsePublisher;
import com.cboe.systemsManagementService.managedObjectFramework.systemManagementAdapter.SystemManagementAdapter;

import static com.cboe.infrastructureServices.foundationFramework.utilities.Log.information;
import static com.cboe.infrastructureServices.foundationFramework.utilities.Log.alarm;
import static com.cboe.infrastructureServices.foundationFramework.utilities.Log.exception;
/**
 * Created by IntelliJ IDEA.
 * User: Eric J. Fredericks
 * Date: Sep 29, 2003
 * Time: 5:42:35 PM
 * To change this template use Options | File Templates.
 */

public class AdminServiceAsyncImpl extends AdminServiceImpl implements AdminRequestConsumer
{
	private static String MSG_HEAD = "AdminServiceAsyncImpl >> ";
	private static String NEW_CHANNEL_NAME_SUFFIX = "V2";
	private static String CHANNEL_NAME_PROP = "channelName";
	
    private EventService cachedEventService;
    private String v2ChannelName;

    private String uniqueProcessFilterString;
    private String[] optionalAdminGroups;
    
    private AdminRequestConsumerImpl v2EventChannelConsumer;
    private AdminResponseConsumer v2ResponsePublisher;
    
    /**
     * The following declarations are for the purpose of conversion from RTServer 
     * based event channel to AMQ based event channel. They should be removed when
     * conversion is completed.
     */
    private static String CONVERSION_PHASE_PROP = "AdminService.conversionPhase";
    private String v1ChannelName;
    private AdminRequestConsumerImpl v1EventChannelConsumer;
    private AdminResponseConsumer v1ResponsePublisher;
    private boolean isInConversionPhase = false;
    
    
    public AdminServiceAsyncImpl()
    {
        super();
    }
    
    public boolean initialize(ConfigurationService configService)
    {
        information(MSG_HEAD + "Initializing...");
    	
        if (! loadConfigProperties(configService)) {
        	alarm(MSG_HEAD + "Initialization failed due to that config properties were failed to load");
        	return false;
        }
        
        if (! initializeConsumersAndPublishers()) {
        	alarm(MSG_HEAD + "Initialization failed due to that consumers/publishers could not be initialized");
        	return false;
        }     
        
        boolean success = super.initialize(configService);
        
        information(MSG_HEAD + " Initialization completed: " + success);

        return success;
    }
    
    private boolean initializeConsumersAndPublishers()
    {
    	if (! initializeV2ConsumerAndPublisher()) {
    		return false;
    	}
    	
    	if (isInConversionPhase) {
    		if (! initializeV1ConsumerAndPublisher()) {
    			return  false;
    		}
    	}
    	return true;
    }
    
    private boolean initializeV2ConsumerAndPublisher()
    {
    	try {
    		String interfaceRepId = AdminResponseEventConsumerHelper.id();
            org.omg.CORBA.Object obj = getEventService().getTypedEventChannelSupplierStub(v2ChannelName, interfaceRepId);
            AdminResponseEventConsumer channel = AdminResponseEventConsumerHelper.narrow(obj);
            v2ResponsePublisher = new AdminResponsePublisher(channel);
            
            information(MSG_HEAD + "AdminResponse Publisher is initialized: " + v2ChannelName);                
            
    	}
    	catch (Exception e) {
    		alarm(MSG_HEAD + "Could not initialize AdminResponse Publisher: " + v2ChannelName);
    		e.printStackTrace(System.out);
    		return false;
    	}
 
    	try {
            v2EventChannelConsumer = new AdminRequestConsumerImpl(this);
            applyFiltersForDestination(uniqueProcessFilterString, v2EventChannelConsumer, v2ChannelName);
            for(int i = 0; i < optionalAdminGroups.length; i++)
            {
                applyFiltersForDestination(optionalAdminGroups[i], v2EventChannelConsumer, v2ChannelName);
            }
            String interfaceRepId = AdminRequestEventConsumerHelper.id();
            getEventService().connectTypedNotifyChannelConsumer(v2ChannelName, interfaceRepId, v2EventChannelConsumer);
            
            information(MSG_HEAD + "AdminRequest Consumer is initialized: " + v2ChannelName);
    	}
    	catch (Exception e) {
    		alarm(MSG_HEAD + "Could not initialize AdminRequest Consumer: " + v2ChannelName);
    		e.printStackTrace(System.out);
    		return false;
    	}
    	
    	return true;
    }

    private boolean initializeV1ConsumerAndPublisher()
    {
    	try {
    		String interfaceRepId = AdminResponseEventConsumerHelper.id();
            org.omg.CORBA.Object obj = getEventService().getTypedEventChannelSupplierStub(v1ChannelName, interfaceRepId);
            AdminResponseEventConsumer channel = AdminResponseEventConsumerHelper.narrow(obj);
            v1ResponsePublisher = new AdminResponsePublisher(channel);
            
            information(MSG_HEAD + "Old AdminResponse Publisher is initialized: " + v1ChannelName);                
            
    	}
    	catch (Exception e) {
    		alarm(MSG_HEAD + "Could not initialize Old AdminResponse Publisher: " + v1ChannelName);
    		e.printStackTrace(System.out);
    		return false;
    	}
 
    	try {
            v1EventChannelConsumer = new AdminRequestConsumerImpl(this);
            applyFiltersForDestination(uniqueProcessFilterString, v1EventChannelConsumer, v1ChannelName);
            for(int i = 0; i < optionalAdminGroups.length; i++)
            {
                applyFiltersForDestination(optionalAdminGroups[i], v1EventChannelConsumer, v1ChannelName);
            }
            String interfaceRepId = AdminRequestEventConsumerHelper.id();
            getEventService().connectTypedNotifyChannelConsumer(v1ChannelName, interfaceRepId, v1EventChannelConsumer);
            
            information(MSG_HEAD + "Old AdminRequest Consumer is initialized: " + v1ChannelName);
    	}
    	catch (Exception e) {
    		alarm(MSG_HEAD + "Could not initialize Old AdminRequest Consumer: " + v1ChannelName);
    		e.printStackTrace(System.out);
    		return false;
    	}
    	
    	return true;
    }    
    
    private boolean loadConfigProperties(ConfigurationService configService){
    	boolean success;
    	try {
    		loadConversionPhaseProperty();    		
    		loadChannelNameProperty();
    		loadUniqueProcessFilterStringProperty();
    		loadOptionalAdminGroups(configService);
    		success = true;
    	}
    	catch (NoSuchPropertyException e) {
    		success = false;
    	}
    	return success;
    }
    
    private void loadChannelNameProperty() throws NoSuchPropertyException
    {
		String cNameStr = getProperty(CHANNEL_NAME_PROP, null);	
		if (cNameStr != null) {
			v2ChannelName = cNameStr;
			if (isInConversionPhase) {
				getV1ChannelName();
			}
			
			information(MSG_HEAD + "Load channelName = " + v2ChannelName);
		}
		else {
			alarm(MSG_HEAD + "Property channelName is needed, but not defined.");
			
		    throw new NoSuchPropertyException(CHANNEL_NAME_PROP);
		}
    }
    
    private void getV1ChannelName()
    {
		if (v2ChannelName.endsWith(NEW_CHANNEL_NAME_SUFFIX)) {
			int l = v2ChannelName.length() - NEW_CHANNEL_NAME_SUFFIX.length();
			v1ChannelName = v2ChannelName.substring(0, l);
		}
		else {
			v1ChannelName = v2ChannelName;   
		}
		
		information(MSG_HEAD + "Because it is in conversion phase, the old channel name: " + v1ChannelName);
    }
    
    private void loadConversionPhaseProperty(){
    	String str = System.getProperty(CONVERSION_PHASE_PROP);
    	if (str != null) {
    		isInConversionPhase = Boolean.parseBoolean(str);
    	}
    	else {
    		isInConversionPhase = false;
    	}
    	
    	information(MSG_HEAD + "Loaded isInConversionPhase = " + isInConversionPhase);
    }
    
    private void loadUniqueProcessFilterStringProperty() throws NoSuchPropertyException
    {
        uniqueProcessFilterString = System.getProperty("asyncAdminUniqueProcess");
        if (uniqueProcessFilterString == null) {
        	try {
        	uniqueProcessFilterString = SystemManagementAdapter.getInstance().getMBean("Process").getName();
        	}
        	catch (Exception e){
        		e.printStackTrace(System.out);
        	}
        }
        if (uniqueProcessFilterString == null) {
        	alarm(MSG_HEAD + "Property asyncAdminUniqueProcess is needed, but not defined.");
            throw new NoSuchPropertyException("asyncAdminUniqueProcess");    
        }
        
        information(MSG_HEAD + "Loaded uniqueProcessFilterString = " + uniqueProcessFilterString);
    }    
    
    private void loadOptionalAdminGroups(ConfigurationService configService)
    {
        optionalAdminGroups = configService.getPropertyList("adminGroups", ",", null);        
        if(optionalAdminGroups == null)
        {
            optionalAdminGroups = new String[0];
        }
        StringBuffer buf = new StringBuffer();
        for(int i = 0; i < optionalAdminGroups.length; i++)
        {
            buf.append(optionalAdminGroups[i]);
            buf.append(", ");
        }
        information(MSG_HEAD + "Load optionalAdminGroups = " + buf.toString());
    }    
    
    private EventService getEventService()
    {
        if(cachedEventService == null)
        {
            cachedEventService = FoundationFramework.getInstance().getEventService();
        }
        
        return cachedEventService;
    }
    
    private void applyFiltersForDestination(String destination, Servant consumer, String channel) throws Exception
    {
        information(MSG_HEAD + "Applying filters for requests directed at destination = " + destination);
        
        EventService eventService = getEventService();
        String interfaceRepId = AdminRequestEventConsumerHelper.id();
        
        ConsumerFilter inclusionFilter;
        String methodName;
        String constraint;
        
        methodName = AdminServiceMethodNames.DEFN_PROPERTIES;
        constraint = "$." + methodName + ".routingParameters.requestDestination=='" + destination + "'";
        inclusionFilter = eventService.createNewInclusionFilter(consumer, interfaceRepId, methodName, constraint, channel);
        eventService.applyFilter(inclusionFilter);
        
        methodName = AdminServiceMethodNames.DEFN_PROPERTY;
        constraint = "$." + methodName + ".routingParameters.requestDestination=='" + destination + "'";
        inclusionFilter = eventService.createNewInclusionFilter(consumer, interfaceRepId, methodName, constraint, channel);
        eventService.applyFilter(inclusionFilter);

        methodName = AdminServiceMethodNames.EXECUTE_COMMAND;
        constraint = "$." + methodName + ".routingParameters.requestDestination=='" + destination + "'";
        inclusionFilter = eventService.createNewInclusionFilter(consumer, interfaceRepId, methodName, constraint, channel);
        eventService.applyFilter(inclusionFilter);

        methodName = AdminServiceMethodNames.GET_ALL_COMMANDS;
        constraint = "$." + methodName + ".routingParameters.requestDestination=='" + destination + "'";
        inclusionFilter = eventService.createNewInclusionFilter(consumer, interfaceRepId, methodName, constraint, channel);
        eventService.applyFilter(inclusionFilter);

        methodName = AdminServiceMethodNames.GET_COMMAND;
        constraint = "$." + methodName + ".routingParameters.requestDestination=='" + destination + "'";
        inclusionFilter = eventService.createNewInclusionFilter(consumer, interfaceRepId, methodName, constraint, channel);
        eventService.applyFilter(inclusionFilter);

        methodName = AdminServiceMethodNames.GET_PROPERTIES;
        constraint = "$." + methodName + ".routingParameters.requestDestination=='" + destination + "'";
        inclusionFilter = eventService.createNewInclusionFilter(consumer, interfaceRepId, methodName, constraint, channel);
        eventService.applyFilter(inclusionFilter);

        methodName = AdminServiceMethodNames.GET_PROPERTY_VALUE;
        constraint = "$." + methodName + ".routingParameters.requestDestination=='" + destination + "'";
        inclusionFilter = eventService.createNewInclusionFilter(consumer, interfaceRepId, methodName, constraint, channel);
        eventService.applyFilter(inclusionFilter);
    }
    
    private String structToString(AdminRoutingStruct adminRoutingStruct) {
    	String str = "AdminRoutingStruct(source/destination/id=";
    	str = str + adminRoutingStruct.requestSource + "/" + adminRoutingStruct.requestDestination + "/";
    	str = str + adminRoutingStruct.requestId + ")";
    	return str;
    }
    /*******************************************************************************************
     * The following methods are interface methods defined in AdminRequestConsumer
     ******************************************************************************************* 
     */
    public void defineProperties(AdminRoutingStruct adminRoutingStruct, Property[] properties)
    {      
    	information(MSG_HEAD + "defineProperties: " + structToString(adminRoutingStruct));
    	
        AdminExceptionStruct exception = null;

        try
        {
            defineProperties(properties);
        }
        catch(MultipleExceptions e)
        {	
            exception = new AdminExceptionStruct();
            exception.message = e.getMessage();
            exception.methodName = AdminServiceMethodNames.DEFN_PROPERTIES;
            exception.type = AdminServiceExceptionTypes.MULTIPLE_EXCEPTIONS;
        }
        catch(RuntimeException e)
        {
            exception(MSG_HEAD + "Caught RuntimeException", e);
            
            exception = new AdminExceptionStruct();
            exception.message = e.getMessage();
            exception.methodName = AdminServiceMethodNames.DEFN_PROPERTIES;
            exception.type = AdminServiceExceptionTypes.RUNTIME_EXCEPTION;
        }
        
        adminRoutingStruct.requestDestination = uniqueProcessFilterString;

        if(exception == null){
            v2ResponsePublisher.definePropertiesReturn(adminRoutingStruct);
            if (isInConversionPhase) {
            	v1ResponsePublisher.definePropertiesReturn(adminRoutingStruct);	
            }
        }
        else {
        	v2ResponsePublisher.catchException(adminRoutingStruct, exception);
            if (isInConversionPhase) {
            	v1ResponsePublisher.catchException(adminRoutingStruct, exception);	
            }
        }
    }

    public void defineProperty(AdminRoutingStruct adminRoutingStruct, Property property)
    {
    	information(MSG_HEAD + "defineProperty: " + structToString(adminRoutingStruct));
    	
        AdminExceptionStruct exception = null;
        try
        {
            defineProperty(property);
        }
        catch(InvalidPropertyName e)
        {
            exception = new AdminExceptionStruct();
            exception.message = e.getMessage();
            exception.methodName = AdminServiceMethodNames.DEFN_PROPERTY;
            exception.type = AdminServiceExceptionTypes.INVALID_PROPERTY_NAME;
        }
        catch(InvalidPropertyValue e)
        {           
            exception = new AdminExceptionStruct();
            exception.message = e.getMessage();
            exception.methodName = AdminServiceMethodNames.DEFN_PROPERTY;
            exception.type = AdminServiceExceptionTypes.INVALID_PROPERTY_VALUE;
        }
        catch(UnsupportedProperty e)
        {
            exception = new AdminExceptionStruct();
            exception.message = e.getMessage();
            exception.methodName = AdminServiceMethodNames.DEFN_PROPERTY;
            exception.type = AdminServiceExceptionTypes.UNSUPPORTED_PROPERTY;
        }
        catch(RuntimeException e)
        {
            exception(MSG_HEAD + "Caught runtime exception", e);
            
            exception = new AdminExceptionStruct();
            exception.message = e.getMessage();
            exception.methodName = AdminServiceMethodNames.DEFN_PROPERTY;
            exception.type = AdminServiceExceptionTypes.RUNTIME_EXCEPTION;
        } 
        
        adminRoutingStruct.requestDestination = uniqueProcessFilterString;
        
        if(exception == null) {
        	v2ResponsePublisher.definePropertyReturn(adminRoutingStruct);
            if (isInConversionPhase){
            	v1ResponsePublisher.definePropertyReturn(adminRoutingStruct);	
            }
        }
        else {
        	v2ResponsePublisher.catchException(adminRoutingStruct, exception);
            if (isInConversionPhase) {
            	v1ResponsePublisher.catchException(adminRoutingStruct, exception);	
            }
        }
    }

    public void executeCommand(AdminRoutingStruct adminRoutingStruct, Command command)
    {
    	information(MSG_HEAD + "executeCommand: " + structToString(adminRoutingStruct));
    	
        AdminExceptionStruct exception = null;
        
        CommandHolder holder = new CommandHolder(command);
        boolean returnValue = false;
        try
        {
            returnValue = this.executeCommand(holder);
        }
        catch(InvalidParameter e)
        {
            exception = new AdminExceptionStruct();
            exception.message = e.getMessage();
            exception.methodName = AdminServiceMethodNames.EXECUTE_COMMAND;
            exception.type = AdminServiceExceptionTypes.INVALID_PARAMETER;
        }
        catch(RuntimeException e)
        {
            exception(MSG_HEAD + "Caught RuntimeException", e);
            
            exception = new AdminExceptionStruct();
            exception.message = e.getMessage();
            exception.methodName = AdminServiceMethodNames.EXECUTE_COMMAND;
            exception.type = AdminServiceExceptionTypes.RUNTIME_EXCEPTION;
        }
        
        adminRoutingStruct.requestDestination = uniqueProcessFilterString;
        if(exception == null) {
        	v2ResponsePublisher.executeCommandReturn(adminRoutingStruct, returnValue, holder.value);
            if (isInConversionPhase) {
            	v1ResponsePublisher.executeCommandReturn(adminRoutingStruct, returnValue, holder.value);	
            }
        }
        else {
        	v2ResponsePublisher.catchException(adminRoutingStruct, exception);
            if (isInConversionPhase) {
            	v1ResponsePublisher.catchException(adminRoutingStruct, exception);	
            }
        }
        
    }

    public void getAllCommands(AdminRoutingStruct adminRoutingStruct)
    {
    	information(MSG_HEAD + "getAllCommands: " + structToString(adminRoutingStruct));
    	
        AdminExceptionStruct exception = null;
        Command[] commands = null;
        try
        {
            commands = getAllCommands();
            
            if(commands == null)
            {
                throw new NullPointerException("getAllCommands returned null");
            }
        }
        catch(RuntimeException e)
        {
            exception(MSG_HEAD + "Caught RuntimeException", e);
            
            exception = new AdminExceptionStruct();
            exception.message = e.getMessage();
            exception.methodName = AdminServiceMethodNames.GET_ALL_COMMANDS;
            exception.type = AdminServiceExceptionTypes.RUNTIME_EXCEPTION;
        }
        
        adminRoutingStruct.requestDestination = uniqueProcessFilterString;
        if(exception == null){
        	v2ResponsePublisher.getAllCommandsReturn(adminRoutingStruct, commands);
            if (isInConversionPhase) {
            	v1ResponsePublisher.getAllCommandsReturn(adminRoutingStruct, commands);	
            }
        }
        else {
        	v2ResponsePublisher.catchException(adminRoutingStruct, exception);
            if (isInConversionPhase) {
            	v1ResponsePublisher.catchException(adminRoutingStruct, exception);	
            }
        }
    }

    public void getCommand(AdminRoutingStruct adminRoutingStruct, String commandName)
    {
    	information(MSG_HEAD + "getCommand: " + structToString(adminRoutingStruct) + ". CommandName: " + commandName);
    	
        Command command = null;
        AdminExceptionStruct exception = null;
        try
        {
            command = getCommand(commandName);
            if(command == null)
            {
                throw new NullPointerException("getCommand returned null");
            }
        }
        catch(UnsupportedCommand e)
        {
            exception = new AdminExceptionStruct();
            exception.message = e.getMessage();
            exception.methodName = AdminServiceMethodNames.GET_COMMAND;
            exception.type = AdminServiceExceptionTypes.UNSUPPORTED_COMMAND;
        }
        catch(RuntimeException e)
        {
            exception(MSG_HEAD + "Caught RuntimeException", e);
            
            exception = new AdminExceptionStruct();
            exception.message = e.getMessage();
            exception.methodName = AdminServiceMethodNames.GET_COMMAND;
            exception.type = AdminServiceExceptionTypes.RUNTIME_EXCEPTION;
        }
        
        adminRoutingStruct.requestDestination = uniqueProcessFilterString;
        if(exception == null) {
        	v2ResponsePublisher.getCommandReturn(adminRoutingStruct, command);
            if (isInConversionPhase) {
            	v1ResponsePublisher.getCommandReturn(adminRoutingStruct, command);	
            }
        }
        else {
        	v2ResponsePublisher.catchException(adminRoutingStruct, exception);
            if (isInConversionPhase) {
            	v1ResponsePublisher.catchException(adminRoutingStruct, exception);	
            }
        }
    }

    public void getProperties(AdminRoutingStruct adminRoutingStruct)
    {
    	information(MSG_HEAD + "getProperties: " + structToString(adminRoutingStruct));
    	
        AdminExceptionStruct exception = null;

        Property[] properties = null;
        try
        {
            properties = getProperties();
            
            if(properties == null)
            {
                throw new NullPointerException("getProperties returned null");
            }
        }
        catch(RuntimeException e)
        {
            exception(MSG_HEAD + "Caught RuntimeException", e);
            
            exception = new AdminExceptionStruct();
            exception.message = e.getMessage();
            exception.methodName = AdminServiceMethodNames.GET_PROPERTIES;
            exception.type = AdminServiceExceptionTypes.RUNTIME_EXCEPTION;
        }
        
        adminRoutingStruct.requestDestination = uniqueProcessFilterString;
        if(exception == null){
        	v2ResponsePublisher.getPropertiesReturn(adminRoutingStruct, properties);
            if (isInConversionPhase) {
            	v1ResponsePublisher.getPropertiesReturn(adminRoutingStruct, properties);	
            }
        }
        else{
        	v2ResponsePublisher.catchException(adminRoutingStruct, exception);
            if (isInConversionPhase) {
            	v1ResponsePublisher.catchException(adminRoutingStruct, exception);	
            }
        }
        
    }

    public void getPropertyValue(AdminRoutingStruct adminRoutingStruct, String propertyName)
    {
    	information(MSG_HEAD + "getPropertyValue: " + structToString(adminRoutingStruct) + ". PropertyName: " + propertyName);
    	
        AdminExceptionStruct exception = null;
        String propertyValue = null;
        
        try
        {
            propertyValue = getPropertyValue(propertyName);
            
            if(propertyValue == null)
            {
                throw new NullPointerException("getPropertyValue returned null");
            }
        }
        catch(PropertyNotFound e)
        {
            exception = new AdminExceptionStruct();
            exception.message = e.getMessage();
            exception.methodName = AdminServiceMethodNames.GET_PROPERTY_VALUE;
            exception.type = AdminServiceExceptionTypes.PROPERTY_NOT_FOUND;
        }
        catch(InvalidPropertyName e)
        {
            exception = new AdminExceptionStruct();
            exception.message = e.getMessage();
            exception.methodName = AdminServiceMethodNames.GET_PROPERTY_VALUE;
            exception.type = AdminServiceExceptionTypes.INVALID_PROPERTY_NAME;
        }
        catch(RuntimeException e)
        {
            exception(MSG_HEAD + "Caught RuntimeException", e);
            
            exception = new AdminExceptionStruct();
            exception.message = e.getMessage();
            exception.methodName = AdminServiceMethodNames.GET_PROPERTY_VALUE;
            exception.type = AdminServiceExceptionTypes.RUNTIME_EXCEPTION;
        }
       
        adminRoutingStruct.requestDestination = uniqueProcessFilterString;
        if(exception == null) {
        	v2ResponsePublisher.getPropertyValueReturn(adminRoutingStruct, propertyValue);
            if (isInConversionPhase) {
            	v1ResponsePublisher.getPropertyValueReturn(adminRoutingStruct, propertyValue);	
            }
        }
        else {
        	v2ResponsePublisher.catchException(adminRoutingStruct, exception);
            if (isInConversionPhase) {
            	v1ResponsePublisher.catchException(adminRoutingStruct, exception);	
            }
        }
    }
}
