package com.cboe.infrastructureServices.systemsManagementService;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.loggingService.MsgCategory;
import com.cboe.infrastructureServices.loggingService.MsgPriority;
/**
 * Holder for callback objects in the Command Callback Service 
 * Might make this an Inner class
 * Needs to include argument accessors 
 */
public class CallbackObject
{
	private String[] arguments;
	private String methodName;
	private Object callbackObject;
	/**
	 */
	CallbackObject()
	{
	}
	/**
	   @roseuid 365C94220046
	 */
	public String[] executeCommand(String command, String[] parameters)
	{
		Class[] parms = null;
		Object[] invokeParms = null;
		if(parameters.length == 0)
		{
			parms = new Class[0];
			invokeParms = new Object[0];
		}
		else
		{
			parms = new Class[parameters.length];
			invokeParms = new Object[parameters.length];
			for(int i = 0; i < parameters.length; i++)
			{
				parms[i] = parameters[i].getClass();
				invokeParms[i] = (Object)parameters[i];
			}
		}
		// Lookup the command in the command callback list
		CallbackHolder callbackObject = CommandCallbackService.getInstance().getRegisteredCallbackHolder(command);
		String methodName = callbackObject.getMethodName();

		try
		{
			Method method = callbackObject.getClass().getMethod(methodName, parms);
			method.invoke(callbackObject, invokeParms);
		}
		catch (IllegalAccessException e) 
		{
			FoundationFramework.getInstance().getDefaultLogService().log(MsgPriority.high, MsgCategory.systemAlarm, "CallbackObject.executeCommand","Failed to invoke callback method " + methodName + ".", e);
		}
		catch (NoSuchMethodException e)
		{
			FoundationFramework.getInstance().getDefaultLogService().log(MsgPriority.high, MsgCategory.systemAlarm, "CallbackObject.executeCommand","Failed to invoke callback method " + methodName + ".", e);
		}
		catch (InvocationTargetException e)
		{
			FoundationFramework.getInstance().getDefaultLogService().log(MsgPriority.high, MsgCategory.systemAlarm,"CallbackObject.executeCommand", "Failed to invoke callback method " + methodName + ".", ((InvocationTargetException)e).getTargetException());
		}

		return new String[0];
	}
	/**
	   @roseuid 365B4DC701E1
	 */
	public Object getCallbackObject()
	{
		return callbackObject;
	}
	/**
	   @roseuid 365B4D400165
	 */
	public String getMethodName() {
		return methodName;
	}
	/**
	   @roseuid 365B4E840265
	 */
	public void setCallbackObject(Object callbackObject)
	{
		this.callbackObject = callbackObject;
	}
	/**
	   @roseuid 365B4E910304
	 */
	public void setMethodName(String methodName)
	{
		this.methodName = methodName;
	}
}