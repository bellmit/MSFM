package com.cboe.infrastructureServices.sessionManagementService;

import com.cboe.exceptions.*;

/**
 * This interface is not necessary as seperate new interface from SessionManagementService. 
 * The methods defined here could be put into SessionManagementService. But to minimize 
 * the efforts on rolling out, this interface is created.
 *
 */

public interface SessionManagementServiceV2 extends SessionManagementService {
	
	/**
	 * ==========================================================================================
	 * the following two methods are added to deal with a very special situation where 
	 * the callback from FE to CAS for a particular user is broken. And all other 
	 * communications seem all right. In this case, user will continue to send in quotes/orders, 
	 * but he will not get any quote status/ order status. The following methods are designed 
	 * for FE to inform the SessionManagementService that a particular type of callback object 
	 * is created. SMS will keep track all live callbacks created for a user. If one callback object 
	 * fails, FE will inform SMS, SMS removes it from the live callback list, and logout user if 
	 * the number of live callbacks for any callback type is reduced to zero.  
	 * 
	 * ==========================================================================================
	 * 
	 * @param userId
	 * @param callbackType, constant defined in interface UserCallbackTypes
	 * @param orbName, the orbName of the process which sends the registration and deregistration
	 * 		  request.
	 */

	public void registerUserCallbackType(String userId, short callbackType, String orbName)
	throws DataValidationException,
			CommunicationException,
			SystemException,
			AuthorizationException,
			TransactionFailedException;
	
	/**
	 * @param userId
	 * @param callbackType, constant defined in interface UserCallbackTypes
	 * @param orbName, the orb name of the process which sends the registration and deregistration
	 * 			request.
	 * @param deregistrationMode, constant defined in interface UserCallbackDeregistrationModes
	 * @param message, a string which gives a textual description why the deregistration.
	 */
	public void deregisterUserCallbackType(
			String userId, short callbackType, String orbName, short deregistrationMode, String message)
	throws DataValidationException,
			CommunicationException, 
			SystemException,
			AuthorizationException,
			TransactionFailedException;	
	
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
	public int createSession(
			String userId,String sourceComponentName,String securitySessionId,boolean autoLogout)
	throws DataValidationException,
			CommunicationException,
			SystemException,
			AuthorizationException,
			TransactionFailedException;
	
	/**
	 * Dynamically add a component to the process graph
	 * 
	 * Note1: Valid componentType is defined in a separate interface SMSComponentTypes
	 * Note2: Valid component state is defined in a separate interface SMSComponentStates.
	 */
	public void add(String componentName, int componentType, String parentComponentName, int currentState)
	throws DataValidationException,
			CommunicationException,
			SystemException,
			AuthorizationException,
			TransactionFailedException;	
	
	/**
	 * Dynamically remove a component from the process graph
	 */
	public void remove(String componentName, String[] parentComponentName)
	throws DataValidationException,
			CommunicationException,
			SystemException,
			AuthorizationException,
			TransactionFailedException;	

	/**
	 * Set a component's state
	 * 
	 * Note: Valid component state is defined in a separate interface SMSComponentStates
	 */
	public void setState(String componentName, int state)
	throws DataValidationException,
			CommunicationException,
			SystemException,
			AuthorizationException,
			TransactionFailedException;	
	
	/**
	 * Return a collection of component names given the type
	 * 
	 * Note: Valid component type is defined in a separate interface SMSComponentTypes
	 */
	public String[] getComponents(int componentType)
	throws DataValidationException,
			CommunicationException,
			SystemException,
			AuthorizationException,
			TransactionFailedException;	
	
	/**
	 * Return a collection of child component names given a components. The second parameter
	 * will determine how deep we should go. 
	 */
	public String[] getChildComponents(String parentComponentName, int depth)
	throws DataValidationException,
			CommunicationException,
			SystemException,
			AuthorizationException,
			TransactionFailedException;		
	
	/**
	 * Return a collection of users who is logged in through a source component
	 */
	public String[] getLoggedInUsers(String componentName)
	throws DataValidationException,
			CommunicationException,
			SystemException,
			AuthorizationException,
			TransactionFailedException;		
	
	/**
	 * Query SMS component name for own process
	 */
	public String getSMSComponent();
}
