package com.cboe.infrastructureServices.systemsManagementService;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import junit.framework.TestCase;

import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.utilities.ClassUtil;
import com.cboe.infrastructureServices.interfaces.adminService.Command;
import com.cboe.infrastructureServices.interfaces.adminService.DataItem;
import com.cboe.infrastructureServices.loggingService.MsgCategory;
import com.cboe.infrastructureServices.loggingService.MsgPriority;
 
/** 
 * Holder for callback objects in the Command Callback Service
 * @author Dave Hoag
 * @version 3.1
 */
public class CallbackHolder
{
	private String externalCallName;
	private Object callbackObject;
	private String methodName;
	private String[] arguments;
	private String[] argumentDescriptions;
	private String methodDescription;
	private Method method;

	/**
	 * Should only be used by the systemsManagementService facade.
	 */
	CallbackHolder()
	{
	}
	/**
	 * Constructor that initializes the CallbackHolder.
	 * 
	 * @param callbackObject The target of the reflected invocation.
	 * @param externalCallName String The unique name identifying this command.
	 * @param methodName String The name of the method to invoke.
	 * @param methodDescription String A nice description of the method.
	 * @param arguments String [] Fully qualified class names.
	 * @param arguments String [] Friendly descriptions of the arguments.
	 */
	public CallbackHolder(Object callbackObject, String externalCallName, String methodName, String methodDescription, String[] arguments, String[] argumentDescriptions)
	{ 
		setCallbackObject(callbackObject);
		setExternalCallName(externalCallName);
		setMethodName(methodName);
		setArguments(arguments);
		setArgumentDescriptions(argumentDescriptions);
		setMethodDescription(methodDescription);
		Method meth = getCallbackMethod(this);
		if(meth == null)
		{
			throw new IllegalArgumentException("Unable to reflectively locate method " + methodName + ". A Method is required.");
		}
		setMethod(meth);
	}
	/**
	 * Execute the method of the callback object contained in the callbackHolder
	 */
	public Command executeCommand(Command command)
	{
        FoundationFramework ff = FoundationFramework.getInstance();
		try
		{
			String [] arguments;
			if (command.args.length < 1)
			{
				arguments = new String [0];
			}
			else
			{
				arguments = new String [command.args.length];
				for(int i = 0; i < command.args.length; ++i)
				{
					arguments[i] = command.args[i].value;
				} 
			} 
			// populate the command object
			Object result;

			Class[] params = method.getParameterTypes();
			if(params.length == 1 && params[0] == arguments.getClass())
			{
				Object [] argsWrapper = new Object [] { arguments };
				result = method.invoke(getCallbackObject(), argsWrapper);
			}
			else {
				result = method.invoke(getCallbackObject(), (java.lang.Object[])arguments);
			}

			command.retValues = getResultValues(method, result);
		}
		catch (IllegalAccessException e)
		{
			FoundationFramework.getInstance().getDefaultLogService().log(MsgPriority.high, MsgCategory.systemAlarm, "CallbackHolder.executeCommand","Failed execute callback command.", e);
		}
		catch (InvocationTargetException e)
		{
			FoundationFramework.getInstance().getDefaultLogService().log(MsgPriority.high, MsgCategory.systemAlarm,"CallbackHolder.executeCommand", "Failed execute callback command.", ((InvocationTargetException)e).getTargetException());
        }
		return command;
	}
	/**
	 * Extract the elements of a result array into the individual elements.
	 * Each element in the array will have a corresponding data item.
	 * If the method has a return type of a 'String' instead of an array, return
	 * an array of size 1 containing that string.
	 * @return a collection of the result values.
	 * @param meth The method we invoked.
	 * @param result The result of the invocation
	 */
	protected DataItem [] getResultValues(Method meth, Object result)
	{
		DataItem [] returnValues = null;
		if(meth.getReturnType() == void.class)
		{
		    returnValues = new DataItem[0];
		}
		else
		{
			if(result.getClass().isArray())
			{
				int len = java.lang.reflect.Array.getLength(result);
				returnValues = new DataItem [ len ];
				for(int i = 0; i < len; ++i)
				{
					returnValues[i] = new DataItem("", meth.getReturnType().getComponentType().getName() , java.lang.reflect.Array.get(result, i).toString() , "");
				} 
			}
			else
			{
			    returnValues = new DataItem [1];
		    	returnValues[0] = new DataItem("", meth.getReturnType().getName() , result.toString(), "");
		    }
		}
		return returnValues;
	}
	/**
	 * Return the argument descriptions as a string array object
	 */
	public String[] getArgumentDescriptions()
	{
		return argumentDescriptions;
	}
	public String[] getArguments()
	{
		return arguments;
	}
	/**
	 * Return the correct Method object specified for the callbackObject
	 * in the CallbackHolder.  The CallbackHolder must be correctly
	 * configured.  This method acts as a validation of that specification.
	 * For instance, An exception is thrown from the getMethod
	 * method if the method is not found or an access exception is thrown if access
	 * is not public
	 * @param callbackObj
	 * @return java.lang.reflect.Method The reflected method.
	 */
	private Method getCallbackMethod(CallbackHolder callbackObj)
	{
		//check name of class
		Method returnVal = null;
		boolean logError = false;
		Exception error = null;
		Class callbackClass = callbackObj.getCallbackObject().getClass();
		String methodName = callbackObj.getMethodName(); 
		try
		{
			Class[] arguments = ClassUtil.getClassesForStringArguments(callbackObj.getArguments());
			returnVal = callbackClass.getMethod(methodName , arguments);
		}
		catch (NoSuchMethodException ex)
		{
			//Could be a simply the method takes an array of strings, while the declaration individually presented the arguments.
			try
			{
				String [] emptyVar = new String [0];
				returnVal = callbackClass.getMethod(methodName , new Class [] { emptyVar.getClass() });
			}
			catch (Exception e)
			{
				logError = true;
				error = e;
			}
		}
		catch (Exception e)
		{
			logError = true;
			error = e;
		}
		if(logError)
		{
			FoundationFramework.getInstance().getDefaultLogService().log(MsgPriority.high, MsgCategory.systemAlarm, "CallbackHolder.getCallbackMethod","Failed to define callback method " + callbackObj.getMethodName() + " in " + callbackObj.getCallbackObject() + " with args " + callbackObj.getArguments(), error);
		}
		return returnVal;
	}   
	/**
	 * @return The object upon which this callback will be invoked.
	 */
	public Object getCallbackObject()
	{
		return callbackObject;
	}
	/**
	 * @return String The name under which this command is registered.
	 */
	public String getExternalCallName()
	{
		return externalCallName;
	} 
	/**
	 * @return the Method object that was determined from the getCallbackMethod method.
	 * @see #getCallbackMethod(CallbackHolder)
	 */
	public Method getMethod()
	{
		return method;
	}
	/**
	 */
	public String getMethodDescription()
	{
		return methodDescription;
	} 
	/**
	 * @roseuid 365B4D400165
	 */
	public String getMethodName()
	{
		return methodName;
	}
	/**
	 * Set the argument descriptions
	 */
	public void setArgumentDescriptions(String[] someDescriptions)
	{
		argumentDescriptions = someDescriptions;
	}
	/**
	 * Set the unvalidated arguments
	 */
	public void setArguments(String[] someArguments)
	{
		arguments = someArguments;
	}
	/**
	 * Set the callback object
	 */
	public void setCallbackObject(Object aCallbackObject)
	{
		callbackObject = aCallbackObject;
	}
	/**
	 * Set the name of the external call. This is the name
	 * of the command that the remote client knows about.
	 */
	public void setExternalCallName(String anExternalCallName)
	{
		externalCallName = anExternalCallName;
	} 
	public void setMethod(Method aMethod)
	{
		method = aMethod;
	}
	/**
	 * Set the method description string.
	 */
	public void setMethodDescription(String aMethodDescription)
	{
		methodDescription = aMethodDescription;
	} 
	/**
	 * Set the mthod name
	 */
	public void setMethodName(String aMethodName)
	{
		methodName = aMethodName;
	}
	/**
	 * Unit test of the ProcessDescriptor.
	 */
	public static class UnitTest extends TestCase
	{
		String [] complexResult = new String [] { "one", "two", "three" };
		public String [] noArgsBigResult() { return complexResult; }
		public String noArgs() { return "shortOne"; }
		public String oneArg(String one ) { return one; }
		public String [] twoArgs(String one, String two){ return new String [] { one, two} ; }
		public String [] twoArgsInOne(String [] vals ){ return vals; }
	    
		/**
		 * No arguments will run all tests. Each argument is a test name.
		 */
        public static void main(String [] args)
        {
            junit.textui.TestRunner.run(UnitTest.class);
        }
        public UnitTest(String methodName)
        {
            super(methodName);
        }
        public void testNoArgsSimpleReslut()
        {
			CallbackHolder holder = new CallbackHolder(this, "one", "noArgs", "A Description", new String[0] , new String[0]);
			assertTrue("Method not found! ", holder.getMethod() != null);
			DataItem [] args = new DataItem [0];
			Command command = new Command ("one","", args , new DataItem[0] );
			command = holder.executeCommand(command);
			assertEquals("shortOne", command.retValues[0].value);
        }
        public void testNoArgsComplexResult()
        {
			CallbackHolder holder = new CallbackHolder(this, "one", "noArgsBigResult", "A Description", new String[0] , new String[0]);
			assertTrue("Method not found! ", holder.getMethod() != null);
			DataItem [] args = new DataItem [0];
			Command command = new Command ("one","", args , new DataItem[0] );
			command = holder.executeCommand(command);
			assertEquals(3, command.retValues.length);
			for(int i = 0; i < complexResult.length; ++i)
			{
				assertEquals(complexResult[i], command.retValues[i].value);
			} 
        }
        public void testOneArg()
        {
			CallbackHolder holder = new CallbackHolder(this, "one", "oneArg", "A Description", new String[] { "java.lang.String"} , new String[0]);
			assertTrue("Method not found! ", holder.getMethod() != null);
			DataItem [] args = new DataItem [1];
			String value = "aValue";
			args [0] = new DataItem("", "java.lang.String", value, "");
			Command command = new Command ("one", "", args , new DataItem[0] );
			command = holder.executeCommand(command);
			assertEquals(value, command.retValues[0].value);
        }
        public void testArgsArrayDecl()
        {
        	String [] emptyVar = new String [0];
			CallbackHolder holder = new CallbackHolder(this, "one", "twoArgsInOne", "A Description", new String[] { emptyVar.getClass().getName() }  , new String[0]);
			assertTrue("Method not found! ", holder.getMethod() != null);
			DataItem [] args = new DataItem [2];
			String value = "aValue";
			String value2 = "aValue2";
			args [0] = new DataItem("", "java.lang.String", value, "");
			args [1] = new DataItem("", "java.lang.String", value2, "");
			Command command = new Command ("one", "", args , new DataItem[0] );
			command = holder.executeCommand(command);
			assertEquals(2, command.retValues.length);
			assertEquals(value, command.retValues[0].value);
			assertEquals(value2, command.retValues[1].value);
        }
        public void testTwoArgs()
        {
			CallbackHolder holder = new CallbackHolder(this, "one", "twoArgs", "A Description", new String[] { "java.lang.String", "java.lang.String" }  , new String[0]);
			assertTrue("Method not found! ", holder.getMethod() != null);
			DataItem [] args = new DataItem [2];
			String value = "aValue";
			String value2 = "aValue2";
			args [0] = new DataItem("", "java.lang.String", value, "");
			args [1] = new DataItem("", "java.lang.String", value2, "");
			Command command = new Command ("one", "", args , new DataItem[0] );
			command = holder.executeCommand(command);
			assertEquals(2, command.retValues.length);
			assertEquals(value, command.retValues[0].value);
			assertEquals(value2, command.retValues[1].value);
        }
        public void testArgsArrayIndividualDecl()
        {
			CallbackHolder holder = new CallbackHolder(this, "one", "twoArgsInOne", "A Description", new String[] { "java.lang.String" }  , new String[0]);
			assertTrue("Method not found! ", holder.getMethod() != null);
			DataItem [] args = new DataItem [3];
			String value = "aValue";
			String value2 = "aValue2";
			String value3 = "aValue3";
			args [0] = new DataItem("", "java.lang.String", value, "");
			args [1] = new DataItem("", "java.lang.String", value2, "");
			args [2] = new DataItem("", "java.lang.String", value3, "");
			Command command = new Command ("one", "", args , new DataItem[0] );
			command = holder.executeCommand(command);
			assertEquals(3, command.retValues.length);
			assertEquals(value, command.retValues[0].value);
			assertEquals(value2, command.retValues[1].value);
			assertEquals(value3, command.retValues[2].value);
        }
	}
}
