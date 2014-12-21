package com.cboe.infrastructureServices.systemsManagementService;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import junit.framework.TestCase;

import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.interfaces.adminService.Command;
import com.cboe.infrastructureServices.interfaces.adminService.DataItem;
import com.cboe.infrastructureServices.interfaces.adminService.StorageType;
import com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.UnsupportedCommand;
import com.cboe.infrastructureServices.loggingService.MsgCategory;
import com.cboe.infrastructureServices.loggingService.MsgPriority;

/**
 * Use this service register command callbacks and properties.
 *
 * @version 3.2
 */
public class CommandCallbackService
{
	final static String getItKey = "getItKey";
	final static String setItKey = "setItKey";
	private static CommandCallbackService instance;
	/** The default implmentation */
	protected static String serviceImplClassName = "com.cboe.infrastructureServices.systemsManagementService.CommandCallbackService";

	private Hashtable registeredProperties;
	private Hashtable registeredCallbacks;

	/**
	 * The name of a class that extends this class and has a public default constructor.
	 * @author Dave Hoag
	 * @param newValue java.lang.String
	 */
	public static void setServiceImplClassName(String newValue)
	{
		serviceImplClassName = newValue;
	}
	static String getServiceImplClassName()
	{
		return serviceImplClassName;
	}

	/**
	 * Execute the provided command object. Values for the arguments should be set in the DataItems in the command object.
	 *
	 * @param command The command object obtained via the getCommand method.
	 * @see #getCommand
	 */
	public boolean executeCommand(Command command)
	{
        Log.debug("received request for method:"+command.name +" "+this);
        CallbackHolder holder = getRegisteredCallbackHolder(command.name);
        if(holder != null)
        {
		    holder.executeCommand(command);
    		return true;
        }
        else
        {
            Log.information(" callback holder not found for method:"+ command.name);
            return false;
        }
	}
	/**
	 * Convert a registered Callback object to aCommand
	 *
	 * @param aName A registered command name.
	 * @return The command registered with the provided name.
	 * @exception com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.UnsupportedCommand
	 * @see #registerForCommandCallback
	 */
	public Command getCommand(String aName) throws UnsupportedCommand
	{
		CallbackHolder aCallbackHolder = getRegisteredCallbackHolder(aName);
		if(aCallbackHolder == null) throw new UnsupportedCommand(aName);
		String [] argTypes = aCallbackHolder.getArguments();
		String [] argsDescriptions = aCallbackHolder.getArgumentDescriptions();
		DataItem [] args = new DataItem [ argTypes.length ];
		for(int i = 0; i < args.length; ++i)
		{
		    String desc;
		    if(i < argsDescriptions.length)
		    {
		        desc = argsDescriptions[i];
		    }
		    else
		    {
		        desc = "";
		    }
            String type = argTypes[i];
            if(type == null)
            {
            	type = "";
            }
		    args [i] = new DataItem("",type , "", desc);
		}
		Method meth = aCallbackHolder.getMethod();
		DataItem [] returnType = null;
		if(meth.getReturnType() == void.class)
		{
		    returnType = new DataItem[0];
		}
		else
		{
		    returnType = new DataItem [1];
		    returnType [0] = new DataItem("", meth.getReturnType().getName() , "", "");
		}
		return new Command(aCallbackHolder.getExternalCallName(), aCallbackHolder.getMethodDescription(), args, returnType);
	}
	/**
	 * @roseuid 365B6F4701B1
	 *
	 * @return The singleton instance of the CommandCallbackService.
	 */
	public static CommandCallbackService getInstance()
	{
		if (instance == null)
		{
			try
			{
				Class c = Class.forName(getServiceImplClassName());
				instance = (CommandCallbackService)c.newInstance();
			}
			catch (Exception e)
			{
				FoundationFramework.getInstance().getDefaultLogService().log(MsgPriority.high, MsgCategory.systemAlarm, "AdminService.getInstance", "Failed to create the CommandCallbackService " + getServiceImplClassName() + '.', e);
			}
		}
		return instance;
	}
	/**
	 * @return All of the names of the currently registered callbacks.
	 */
	public Enumeration getCallbackNames()
	{
		return getRegisteredCallbacks().keys();
	}
	/**
	 * Usefull for determining the property names.
	 * @return All of the names of the currently registered callbacks.
	 */
	public Enumeration getPropertyNames()
	{
		final Enumeration e = getRegisteredProperties().keys();
		return new Enumeration ()
		{
			Object next = null;
			Vector shown = new Vector();
			public boolean hasMoreElements()
			{
				if(! e.hasMoreElements()) return false;
				if(next != null) return true;
				next = realNext();
				return (next != null);
			}
			public Object nextElement()
			{
				if(next != null)
				{
					Object tmp = next;
					next = null;
					return tmp;
				}
				return realNext();
			}
			private Object realNext()
			{
				//Generate the appropriate exception
				if(! e.hasMoreElements()) e.nextElement();
				while(e.hasMoreElements())
				{
					String key = (String)e.nextElement();
					key = key.substring(8 , key.length());
					if(shown.contains(key)) continue;
					shown.addElement(key);
					return key;
				}
				return null;
			}
		};
	}
	/**
	 * Return the CallbackHolder for a given name
	 * @param aName Name of a uniquely identified command.
	 */
	public CallbackHolder getRegisteredCallbackHolder(String aName)
	{
	   return (CallbackHolder)getRegisteredCallbacks().get(aName);
	}
	/**
	 * @return a Hashtable containing all of the registered callbacks.
	 */
	public Hashtable getRegisteredCallbacks()
	{
		if (registeredCallbacks == null)
		{
			registeredCallbacks = new Hashtable();
		}
		return registeredCallbacks;
	}
	/**
	 * @return a Hashtable containing the registered properties
	 */
	public Hashtable getRegisteredProperties()
	{
		if (registeredProperties == null)
			registeredProperties = new Hashtable();
		return registeredProperties;
	}
	/**
	 * Initialize the CommandCallback Service and return a true value if successful.
	 * The initialize() method uses the specification for the CommandCallback service
	 * from the ApplicationConfigurationService.
	 * @roseuid 3656342E028C
	 */
	public boolean initialize(ConfigurationService appService)
	{
		return true;
	}
	/**
	 * Return true if registration of a callback object is sucessful.
	 * This method stores the callback into the list of registered callbacks.
	 * @roseuid 362CA8260289
	 *
	 * @param callbackObject The object on which to invoke the command.
	 * @param externalCallName
	 * @param methodName
	 * @param methodDescription
	 * @param arguments
	 * @param argumentDescriptions
	 * @exception com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException
	 */
	public void registerForCommandCallback(Object callbackObject, String externalCallName, String methodName, String methodDescription, String[] arguments,  String[] argumentDescriptions) throws CBOELoggableException
	{
		if (getRegisteredCallbackHolder(externalCallName) == null)
		{
			try
			{
				CallbackHolder callbackHolder = new  CallbackHolder(callbackObject, externalCallName, methodName, methodDescription, arguments, argumentDescriptions );
				getRegisteredCallbacks().put(externalCallName, callbackHolder);
			}
			catch (IllegalArgumentException ex)
			{
				throw new CBOELoggableException("Failed to register command " + externalCallName + " - " + ex, MsgPriority.high);
			}
		}
		else
		{
			throw new CBOELoggableException("Attempt to registered a Duplicate CallbackHolder with name = " + externalCallName + " - use another Call Name", MsgPriority.high);
		}
	}
	/**
	 * Return true if registration of a property is sucessful.
	 * This method stores the property into the list of registered properties.
	 *
	 * @param target The object containing the property.
	 * @param propertyName A unique property name.
	 * @param setMethodName The name of a method to call to set the property.
	 * @param getMethodName The name of a method to call to get the property value.
	 * @exception com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException If the propertyName has already been registered.
	 */
	public boolean registerProperty(Object target, String propertyName, String setMethodName, String getMethodName) throws CBOELoggableException
	{
		Hashtable knownProps = getRegisteredProperties();
		final String [] args = {};
		final String [] desc = {};
		String key = getItKey /* + target.getClass().getName() */ + propertyName;
		CallbackHolder callback = null;
		if(getMethodName != null)
		{
			callback = new CallbackHolder(target, key, getMethodName, "", args, desc);
			if(knownProps.containsKey(key))
			{
				throw new CBOELoggableException("Attempt to registered a Duplicate Property with name = " +  propertyName + " - use another property name.", MsgPriority.high);
			}
			knownProps.put(key, callback);
		}
		if(setMethodName != null)
		{
			final String [] args2 = { "java.lang.String" };
			key = setItKey /* + target.getClass().getName() */ + propertyName;
			callback = new CallbackHolder(target, key, setMethodName, "", args2, desc);
			knownProps.put(key, callback);
		}
		return true;
	}
	/**
	 * A utility method used by this implementation to obtain the callback holder object.
	 *
	 * @param propertyName String
	 * @param setMethod
	 */
	protected CallbackHolder getPropertyCallbackHolder( final String propertyName, final boolean setMethod)
	{
		String key;
		if(setMethod)
		{
			key = setItKey /*+ target.getClass().getName() */ + propertyName;
		}
		else
		{
			key = getItKey /* + target.getClass().getName() */ + propertyName;
		}
		Hashtable knownProps = getRegisteredProperties();
		return (CallbackHolder)knownProps.get(key);
	}
	/**
	 * @fixme Should an exception be thrown if the property can not be found?
	 *
	 * @param propertyName String
	 */
	public com.cboe.infrastructureServices.interfaces.adminService.Property getProperty(String propertyName)
	{
		CallbackHolder callbackHolder = getPropertyCallbackHolder( propertyName, false);
		if(callbackHolder == null)
		{
			return new com.cboe.infrastructureServices.interfaces.adminService.Property(propertyName, "", StorageType.TRANSIENT);
		}
		DataItem [] args = new DataItem[0];

		Command c  = new Command (propertyName,"",args,null);
		c = callbackHolder.executeCommand(c);
		DataItem [] ret = c.retValues;
		if(ret == null || ret.length == 0)
		{
			return new com.cboe.infrastructureServices.interfaces.adminService.Property(propertyName, "", StorageType.TRANSIENT);
		}
		DataItem di = ret[0];
		return new com.cboe.infrastructureServices.interfaces.adminService.Property(propertyName, di.value.toString(), StorageType.TRANSIENT);
	}
	/**
	 * Invoke the appropriate commands for each of the properties.
	 * Pass the properties on to the ConfigurationService.
	 */
	public void defineProperties(com.cboe.infrastructureServices.interfaces.adminService.Property [] props)
	{
		for(int i = 0; i < props.length; i++)
		{
			definePropertyWork(props[i]);
		}
		if(FoundationFramework.getInstance() == null) return;
		if(FoundationFramework.getInstance().getConfigService() == null) return;
		FoundationFramework.getInstance().getConfigService().defineProperties(props);
	}
	/**
	 * @fixme Should an exception be thrown if the property can not be defined?
	 */
	public void defineProperty(com.cboe.infrastructureServices.interfaces.adminService.Property prop)
	{
		definePropertyWork(prop);
		if(FoundationFramework.getInstance() == null) return;
		if(FoundationFramework.getInstance().getConfigService() == null) return;
		FoundationFramework.getInstance().getConfigService().defineProperty(prop);
	}
	/**
	 * @fixme Should an exception be thrown if the property can not be defined?
	 */
	protected void definePropertyWork(com.cboe.infrastructureServices.interfaces.adminService.Property prop)
	{
		final String propertyName = prop.name;
		CallbackHolder callbackHolder = getPropertyCallbackHolder( propertyName, true);
		if(callbackHolder == null) return;
		DataItem value = new DataItem("", "java.lang.String", prop.value, null);
		java.lang.reflect.Method meth = callbackHolder.getMethod();
		Object [] args = new Object [1];
		args[0] = prop.value;
		try
		{
			meth.invoke(callbackHolder.getCallbackObject(), args);
		}
		catch (InvocationTargetException e)
		{
			FoundationFramework.getInstance().getDefaultLogService().log(MsgPriority.medium, MsgCategory.systemAlarm, "CommandCallbackService.definePropertyWork", "Failed to define property " + propertyName, ((InvocationTargetException)e).getTargetException());
		}
		catch (Exception e)
		{
			FoundationFramework.getInstance().getDefaultLogService().log(MsgPriority.medium, MsgCategory.systemAlarm, "CommandCallbackService.definePropertyWork","Failed to define property " + propertyName, e);
		}
	}
	/**
	 */
    public static class UnitTest extends TestCase
    {
    	//The following is only necessary for unit testing. Hence it is not part of the actual service.
    	public static void clearInstance()
    	{
    		CommandCallbackService.instance = null;
		}
        String testVar;
        public UnitTest(String methodName)
        {
            super(methodName);
        }
        public void setUp() throws Exception
        {
            super.setUp();
            testVar = "returnMe";
        }
	    /**
	    * Test methods.
	    */
	    public void setMe(String arg)
	    {
	        testVar = arg;
	    }
	    /**
	    * Test methods.
	    */
	    public String getMe()
	    {
		    return testVar;
	    }
        public static void main(String [] args)
        {
            junit.textui.TestRunner.run(UnitTest.class);
        }
		/**
		 */
		public void testProperties()
		{
		    CommandCallbackService svc = CommandCallbackService.getInstance();
		    try
		    {
			    svc.registerProperty(this, "me", "setMe", "getMe");
			    svc.registerProperty(this, "me2", "setMe", "getMe");
			    com.cboe.infrastructureServices.interfaces.adminService.Property p = svc.getProperty("me");
			    assertEquals("me", p.name);
			    assertEquals("returnMe", p.value);

                String newVal = "newValue";
			    com.cboe.infrastructureServices.interfaces.adminService.Property struct = new com.cboe.infrastructureServices.interfaces.adminService.Property("me", newVal, StorageType.TRANSIENT);
			    svc.defineProperty(struct);
			    Enumeration e = svc.getPropertyNames();
			    int count = 0;
			    while(e.hasMoreElements())
			    {
			        e.nextElement();
			        count++;
			    }
			    assertEquals("Failed to find all properties", 2, count);
			    p = svc.getProperty("me");
			    assertEquals(newVal, p.value);
		    }
		    catch (Throwable t) { assertTrue(t.toString(), false);  }
		}
        /**
         */
        public void testBadRegister()
        {
            //FoundationFramework.getInstance().getLogService();
		    CommandCallbackService svc = CommandCallbackService.getInstance();
		    try
		    {
			    svc.registerForCommandCallback(this, "me", "setMeTwo", null, new String [0], new String [0]);
                assertTrue("Method does not exist, should throw runtime exception.", false);
            }
            catch(CBOELoggableException ex)
            {
            }
            catch(Throwable t)
            {
                t.printStackTrace();
                assertTrue(t.toString(), false);
            }
        }
		/**
		 */
		public void testCommandCallback()
		{
		    CommandCallbackService svc = CommandCallbackService.getInstance();
		    try
		    {
		        String methodDesc = "aMethodDesc";
		        String [] args = new String [ ] { "java.lang.String" };
		        String [] argDesc = new String [ ] { "anArgDesc" };
			    svc.registerForCommandCallback(this, "aMethodKey" ,"setMe" , methodDesc, args ,  argDesc );
			    Command command = svc.getCommand("aMethodKey");
			    assertEquals("aMethodKey", command.name);
			    assertEquals(methodDesc, command.description);
			    for(int i = 0; i < command.args.length; ++i)
			    {
			        assertEquals(command.args[i].type, "java.lang.String");
			        assertEquals(command.args[i].description, "anArgDesc");
			    }
			    try
			    {
			        svc.getCommand("aBogusCommand");
			        assertTrue("UnsupportedCommandException not thrown!", false);
			    }
			    catch (UnsupportedCommand ex)
			    {
			    }
		    }
		    catch (Throwable t) { assertTrue(t.toString(), false);  }
		}
		/**
		 */
		public void testCommandCallbackInheritence()
		{
		    CommandCallbackService svc = CommandCallbackService.getInstance();
		    try
		    {
		        String methodDesc = "aMethodDesc";
		        String [] args = new String [ ] { };
		        String [] argDesc = new String [ ] { };
			    svc.registerForCommandCallback(this, "toString" ,"toString" , methodDesc, args ,  argDesc );
			    Command command = svc.getCommand("toString");
			    assertEquals("toString", command.name);
			    assertEquals(methodDesc, command.description);
			    assertEquals(0 , command.args.length);
		    }
		    catch (Throwable t) { assertTrue(t.toString(), false);  }
		}
    }
}
