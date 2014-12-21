package com.cboe.infrastructureServices.sessionManagementService;

import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;
import com.cboe.exceptions.*;

/**
 * The SessionManagementService facade.  Used for managing user logins to the SBT.
 *
 * @version 1.0
 */
public interface SessionManagementService
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
		throws
			DataValidationException,
			CommunicationException,
			SystemException,
			AuthorizationException,
			TransactionFailedException;


	/**
	 *  close a session using the sessionKey that was return from the login.
	 *
	 *  @param sessionKey - the key returned from createSession(String)
	 */
	public void closeSession(int sessionKey)
		throws
			DataValidationException,
			CommunicationException,
			SystemException,
			AuthorizationException,
			TransactionFailedException;

	/**
	 *  force close a session using the sessionKey that was return from the login.
	 *
	 *  @param sessionKey - the key returned from createSession(String)
	 *  @param message - message for user explaining why session is being forced close
	 */
	public void forceCloseSession(int sessionKey, String message)
		throws
			DataValidationException,
			CommunicationException,
			SystemException,
			AuthorizationException,
			TransactionFailedException;

	/**
	 *  close a session using the sessionKey that was return from the login.
	 *
	 *  @param sessionKey - the key returned from createSession(String)
	 */
	public void leaveSession(int sessionKey)
		throws
			DataValidationException,
			CommunicationException,
			SystemException,
			AuthorizationException,
			TransactionFailedException;

	/**
	 *  Join a session using the sessionKey that was returned from the login,
	 *  and the security id returned from authentication.
	 *
	 *  @param sessionKey - the session key to logout.
	 *  @param securitySessionId - auth. string issued by the security service.
	 */
	public void joinSession(int sessionKey, String userName, String securitySessionId)
		throws
			DataValidationException,
			CommunicationException,
			SystemException,
			AuthorizationException,
			TransactionFailedException;

	/**
	 *	Return the components that the current process has listed in it's SMSRelationName list
	 *  (part of the processe's XML configuraton)
	 */
	public String[] getReferencedComponents();

	/**
	 *  Register consumer for login/logout and session state
	 *  notifications for all processes monitored by SMS.
	 *
	 *  @param consumer - The consumer instance to register.
	 */
	public void registerConsumer(SessionManagementServiceUserConsumer consumer);

	/**
	 *  Register consumer for component established/lost
	 *  notifications for all processes monitored by SMS.  Note that consumers may get notified
	 *  with false-postitive ('componentEstablished' messages when the process is already running).
	 *
	 *  @param consumer - The consumer instance to register.
	 */
	public void registerConsumer(SessionManagementServiceComponentConsumer consumer) throws SystemException;


	/**
	 *  Register consumer for component established/lost
	 *  notifications.  Note that false-positive messages may occur, and
	 *  before the registerConsumer call returns the callback will be notified of the
	 *  current status of the given components.
	 *
	 *  @param consumer - The consumer instance to register.
	 *  @param components - listen only for the given components.
	 */
	public void registerConsumer(SessionManagementServiceComponentConsumer consumer, String[] components) throws SystemException;


	/**
	 *  Same as RegisterConsumer but receives component established/lost
	 *  notifications ONLY for it's referenced components
	 *  (ie, elements in the current process's SMSRelation list).
	 *  (ex, if called for a CAS's SMS facade, it will register the CAS's Frontend processes).
	 *
	 *  @param consumer - The consumer instance to register.
	 */
	public void registerConsumerForProcessReferences(SessionManagementServiceComponentConsumer consumer) throws SystemException;

	/**
	 *  Same as RegisterConsumer but receives component established/lost
	 *  notifications ONLY for it's back-referenced components
	 *  (ie, elements who's SMSRelation list includes the current process).
	 *  (ex, if called for a Frontend's SMS facade, it will register the CAS's which reference the Frontend).
	 *  As new components come online, this registration list will be automatically updated.
	 *
	 *  @param consumer - The consumer instance to register.
	 */
	public void registerConsumerForProcessBackReferences(SessionManagementServiceComponentConsumer consumer) throws SystemException;

	/**
	 *  Remove consumer for login/logout
	 *  notifications.
	 *
	 *  @param consumer - The consumer instance to register.
	 */
	public void removeConsumer(SessionManagementServiceUserConsumer consumer);

	/**
	 *  Remove consumer for component established/lost notifications.
	 *
	 *  @param consumer - The consumer instance to register.
	 */
	public void removeConsumer(SessionManagementServiceComponentConsumer consumer);

	/**
	 *  Initialize the fascade.
	 */
	public boolean initialize(ConfigurationService configService);

	/**
	 *   Return the session key for the given userId.
	 *  Throws NotFoundException if the user is not logged in.
	 */
	public int getSessionForUser(String userId)
		throws
			DataValidationException,
			CommunicationException,
			SystemException,
			AuthorizationException,
			NotFoundException;
	

}
