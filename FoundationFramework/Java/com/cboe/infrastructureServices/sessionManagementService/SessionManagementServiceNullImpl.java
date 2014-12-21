package com.cboe.infrastructureServices.sessionManagementService;

import com.cboe.exceptions.*;
/**
 *  Null implementation for the SessionManagementService fascade.
 */
public class SessionManagementServiceNullImpl
	extends SessionManagementServiceBaseImpl implements SessionManagementServiceV2
{
	/**
	 *  After having authenticated with the security service, the CAS calls
	 *  createSession() with the resulting security session id string
	 *
	 *  @param securitySessionId - string returned by the security fascade upon
	 *		authentication.
	 *  @return int - the sessionKey to identify this login session in the
	 *		future.
	 */
	public int createSession(String userId, String securitySessionId)
	{
		String msg = "SessionManagementServiceNullImpl>> createSession is called. ";
		msg = msg + "userId/securitySessionId=" + userId + "/" + securitySessionId;
		System.out.println(msg);
		return 1234;
	}

	public int getSessionForUser(String userId)
	{
		String msg = "SessionManagementServiceNullImpl>> getSessionForUser is called. ";
		msg = msg + "userId=" + userId;
		System.out.println(msg);
		return 1234;
	}

	/**
	 *  close a session using the sessionKey that was return from the login.
	 *
	 *  @param sessionKey - the key returned from createSession(String)
	 */
	public void closeSession(int sessionKey)
	{
		String msg = "SessionManagementServiceNullImpl>> closeSession is called. ";
		msg = msg + "sessionKey=" + sessionKey;
		System.out.println(msg);
	}

	/**
	 *  force close a session using the sessionKey that was return from the login.
	 *
	 *  @param sessionKey - the key returned from createSession(String)
	 *  @param message - message to user explaining why session is being closed
	 */
	public void forceCloseSession(int sessionKey, String message)
	{
		String msg = "SessionManagementServiceNullImpl>> forceCloseSession is called. ";
		msg = msg + "sessionKey/message=" + sessionKey + "/" + message;
		System.out.println(msg);
	}

	/**
	 *  close a session using the sessionKey that was return from the login.
	 *
	 *  @param sessionKey - the key returned from createSession(String)
	 */
	public void leaveSession(int sessionKey)
	{
		String msg = "SessionManagementServiceNullImpl>> leaveSession is called. ";
		msg = msg + "sessionKey=" + sessionKey;
		System.out.println(msg);
	}

	/**
	 *  Join a session using the sessionKey that was returned from the login,
	 *  and the security id returned from authentication.
	 *
	 *  @param sessionKey - the session key to logout.
	 *  @param securitySessionId - auth. string issued by the security service.
	 */
	public void joinSession(int sessionKey, String userId, String securitySessionId)
	{
		String msg = "SessionManagementServiceNullImpl>> joinSession is called. ";
		msg = msg + "sessionKey/userId/securitySessionId=";
		msg = msg + sessionKey + "/" + userId + "/" + securitySessionId;
		System.out.println(msg);
	}

	/**
	 *  Register consumer for login/logout notifications.
	 *
	 *  @param consumer - The consumer instance to register.
	 */
	public void registerConsumer(SessionManagementServiceUserConsumer consumer)
	{
		String msg = "SessionManagementServiceNullImpl>> registerConsumer(UserConsumer) is called. ";
		System.out.println(msg);
	}

	/**
	 *  Register consumer for component established/lost notifications.
	 *
	 *  @param consumer - The consumer instance to register.
	 */
	public void registerConsumer(SessionManagementServiceComponentConsumer consumer) throws SystemException
	{
		String msg = "SessionManagementServiceNullImpl>> registerConsumer(ComponentConsumer) is called. ";
		System.out.println(msg);
	}

	/**
	 *  Register consumer for component established/lost
	 *  notifications.
	 *
	 *  @param consumer - The consumer instance to register.
	 */
	public void registerConsumer(SessionManagementServiceComponentConsumer consumer, String[] components) throws SystemException
	{
		String msg = "SessionManagementServiceNullImpl>> registerConsumer(ComponentConsumer, components) is called. ";
		System.out.println(msg);
	}

	/**
	 *  Register consumer for component established/lost
	 *  notifications.
	 *
	 *  @param consumer - The consumer instance to register.
	 */
	public void registerConsumerForProcessReferences(SessionManagementServiceComponentConsumer consumer) throws SystemException
	{
		String msg = "SessionManagementServiceNullImpl>> registerConsumerForProcessReferences is called. ";
		System.out.println(msg);
	}

	/**
	 *  Register consumer for component established/lost and login/logout
	 *  notifications.
	 *
	 *  @param consumer - The consumer instance to register.
	 */
	public void registerConsumerForProcessBackReferences(SessionManagementServiceComponentConsumer consumer) throws SystemException
	{
		String msg = "SessionManagementServiceNullImpl>> registerConsumerForProcessBackReferences is called. ";
		System.out.println(msg);
	}

	/**
	 *  Remove consumer for login/logout notifications.
	 *
	 *  @param consumer - The consumer instance to register.
	 */
	public void removeConsumer(SessionManagementServiceUserConsumer consumer)
	{
		String msg = "SessionManagementServiceNullImpl>> removeConsumer(UserConsumer) is called.";
		System.out.println(msg);
	}

	/**
	 *  Remove consumer for component established/lost notifications.
	 *
	 *  @param consumer - The consumer instance to register.
	 */
	public void removeConsumer(SessionManagementServiceComponentConsumer consumer)
	{
		String msg = "SessionManagementServiceNullImpl>> removeConsumer(ComponentConsumer) is called.";
		System.out.println(msg);
	}

	public String[] getReferencedComponents()
	{
		String msg = "SessionManagementServiceNulImpl>> getReferenceComponents is called. ";
		System.out.println(msg);
		return new String[0];
	}
	
	/**
	 * the following two methods are added to deal with a very special situation where 
	 * the callback from FE to CAS for a particular user is broken. And all other 
	 * communications seem all right. In this case, user will continue to send in quotes/orders, 
	 * but he will not get any quote status/ order status. The following methods are designed 
	 * for FE to inform the SessionManagementService that a particular type of callback object 
	 * is created. SMS will keep track all live callbacks created for a user. If one callback object 
	 * fails, FE will inform SMS, SMS removes it from the live callback list, and logout user if 
	 * the number of live callbacks for any callback type is reduced to zero.  
	 * 
	 * @param userId
	 * @param callbackType, constant defined in interface UserCallbackTypes
	 * @param orbName, the orbName of the process which sends the registration and deregistration
	 * 		  request.
	 */

	public void registerUserCallbackType(String userId, short callbackType, String orbName)
	throws DataValidationException,CommunicationException,SystemException,AuthorizationException,TransactionFailedException
	{
		String msg = "SessionManagementServiceNullImpl>> registerUserCallbackType is called. ";
		msg = msg + "userId/callbackType/orbName=";
		msg = msg + userId + "/" + callbackType + "/" + orbName;
		System.out.println(msg);
	}
	
	/**
	 * @param userId
	 * @param callbackType, constant defined in interface UserCallbackTypes
	 * @param orbName, the orb name of the process which sends the registration and deregistration
	 * 			request.
	 * @param deregistrationMode, constant defined in interface UserCallbackDeregistrationModes
	 * @param message, a string which gives a textual description why the deregistration.
	 */
	public void deregisterUserCallbackType( String userId, short callbackType, String orbName, short deregistrationMode, String message)
	throws DataValidationException,CommunicationException,SystemException,AuthorizationException,TransactionFailedException
	{
		String msg = "SessionManagementServiceNullImpl>> deregisterUserCallbackType is called. ";
		msg = msg + "userId/callbackType/orbName/deregistrationMode/message=";
		msg = msg + userId + "/" + callbackType + "/" + orbName + "/" + deregistrationMode + "/" + message;
		System.out.println(msg);
	}
	
	/** 
	 * ==========================================================================================
	 * 
	 *  The following methods are introduced to handle special user session management
	 *  requirements from PAR. Basically the requirements are
	 *  
	 *  1. "Sticky Login": A user logged in through PAR will not be automatically logged out 
	 *     for any component failure. A sticky login session will be ended only through 
	 *     explicit logout interface call.
	 *  2. Process graph can be built dynamically by adding or removing a process component 
	 *  3. Provide querying function to return a collection of logged in users given a source 
	 *     component.
	 *     
	 * ==========================================================================================
	 *
	 */
	
	/**
	 * autoLogout: "sticky" flag. False indicates a sticky login. In this case, securitySessionId
	 * 		       could be null.
	 */
	public int createSession(String userId,String sourceComponentName,String securitySessionId,boolean autoLogout)
	throws DataValidationException,CommunicationException,SystemException,AuthorizationException,TransactionFailedException
	{
		String msg = "SessionManagementServiceNullImpl>> createSession is called. ";
		msg = msg + "userId/sourceComponentName/securitySessionId/autoLogout=";
		msg = msg + userId + "/" + sourceComponentName + "/" + securitySessionId + "/" + autoLogout;
		System.out.println(msg);		
		return 0;
	}
	
	/**
	 * Dynamically add a component to the process graph
	 */
	public void add(String componentName, int componentType, String parentComponentName, int currentState)
	throws DataValidationException,CommunicationException,SystemException,AuthorizationException,TransactionFailedException
	{
		String msg = "SessionManagementServiceNullImpl>> add is called. ";
		msg = msg + "componentName/componentType/parentComponentName/currentState=";
		msg = msg + componentName + "/" + componentType + "/" + parentComponentName + "/" + currentState;
		System.out.println(msg);			
	}
	
	/**
	 * Dynamically remove a component from the process graph
	 */
	public void remove(String componentName, String[] parentComponentName)
	throws DataValidationException,CommunicationException,SystemException,AuthorizationException,TransactionFailedException
	{
		String parents = "";
		for (int i = 0; i < parentComponentName.length; i++){
			parents = parents + " " + parentComponentName[i];
		}
		String msg = "SessionManagementServiceNullImpl>> remove is called. ";
		msg = msg + "componentName/state=";
		msg = msg + componentName + "/" + parents;
		System.out.println(msg);			
	}

	/**
	 * Set a component's state
	 */
	public void setState(String componentName, int state)
	throws DataValidationException,CommunicationException,SystemException,AuthorizationException,TransactionFailedException
	{
		String msg = "SessionManagementServiceNullImpl>> setState is called. ";
		msg = msg + "componentName/state=";
		msg = msg + componentName + "/" + state;
		System.out.println(msg);		
	}
	
	/**
	 * Return a collection of component names given the type
	 */
	public String[] getComponents(int componentType)
	throws DataValidationException,CommunicationException,SystemException,AuthorizationException,TransactionFailedException
	{
		String msg = "SessionManagementServiceNullImpl>> getComponents is called. ";
		msg = msg + "componentType=";
		msg = msg + componentType;
		System.out.println(msg);			
		String[] components = {"SMSComponent"};
		return components;
	}
	
	/**
	 * Return a collection of child component names given a component. The second parameter
	 * will determine how deep we should go. 
	 */
	public String[] getChildComponents(String parentComponentName, int depth)
	throws DataValidationException,CommunicationException,SystemException,AuthorizationException,TransactionFailedException
	{
		String msg = "SessionManagementServiceNullImpl>> getChildComponents is called. ";
		msg = msg + "parentComponentName/depth=";
		msg = msg + parentComponentName + "/" + depth;
		System.out.println(msg);			
		String[] components = {"SMSChildComponent"};
		return components;		
	}
	
	/**
	 * Return a collection of users who is logged in through a source component
	 */
	public String[] getLoggedInUsers(String componentName)
	throws DataValidationException,CommunicationException,SystemException,AuthorizationException,TransactionFailedException
	{
		String msg = "SessionManagementServiceNullImpl>> getLoggedInUsers is called. ";
		msg = msg + "componentName=";
		msg = msg + componentName;
		System.out.println(msg);		
		String[] users = {"User"};
		return users;	
	}
	
	/**
	 * Query SMS component name for own process
	 */
	public String getSMSComponent(){
		String msg = "SessionManagementServiceNullImpl>> getSMSComponent is called. ";
		System.out.println(msg);		
		return "SMSComponent";
	}
}
