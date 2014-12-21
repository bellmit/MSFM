package com.cboe.infrastructureServices.securityService;

import org.omg.CORBA.IntHolder;
import org.omg.securityLevel2.ACLHolder;
import org.omg.security.UserPropertiesV2;
import com.cboe.idl.infrastructureServices.securityService.securityAdmin.SecurityAdminService;

/**
 * The SecurityService facade.
 * 
 * @author Chuck Maslowski
 * @version 3.2 - Increment 3 Infrastructure
 */
public interface SecurityService
{
	/**
	 * Basic Authentication Methods.
	 */
	public String authenticateWithCertificate();
	public String authenticateWithPassword();
	public String authenticateWithCertificate(String userName, String password , String certFile);
	public String authenticateWithPassword(String userName, String password);
	public String authenticateWithPassword(String userName, String password, String ipAddress)
		throws InvalidIPException, AuthenticationException;
	
	/** 
	 * Authentication with a union of Roles applied. 
	 */
	public String authenticateWithCertificateUnion();
	public String authenticateWithPasswordUnion();
	public String authenticateWithCertificateUnion(String userName, String password, String certFile);
	public String authenticateWithPasswordUnion(String userName, String password);
	public String authenticateWithPasswordUnion(String userName, String password, String ipAddress)
		throws InvalidIPException, AuthenticationException;
	
	/**
	 * Corresponding to the basic authentication methods, the following 
	 * authentication methods return a UserPropertiesV2 struct instead of 
	 * a sessionId string. 
	 */
	public UserPropertiesV2 authCertificate();
	public UserPropertiesV2 authPassword();
	public UserPropertiesV2 authCertificate(String userName, String password, String certFile);
	public UserPropertiesV2 authPassword(String userName, String password);	
	public UserPropertiesV2 authPassword(String userName, String password, String ipAddress)
		throws InvalidIPException, AuthenticationException;
	
	/**
	 * Corresponding to the authentication methods with union of roles, the following 
	 * authentication methods returns a UserPropertiesV2 struct, instead of 
	 * a sessionId string.
	 */
	public UserPropertiesV2 authCertificateUnion();
	public UserPropertiesV2 authPasswordUnion();
	public UserPropertiesV2 authCertificateUnion(String userName, String password, String certFile);
	public UserPropertiesV2 authPasswordUnion(String userName, String password);
	public UserPropertiesV2 authPasswordUnion(String userName, String password, String ipAddress)
		throws InvalidIPException, AuthenticationException;
	
	/**
	 * Service authorization methods.
	 */
	public boolean authorize(String serviceName);
	public boolean authorize(String serviceName, String sessionID);
	//public boolean authorize(String serviceName, ACLHolder anACLHolder, IntHolder loginTime);
	public boolean authorizeInterface(String serviceName, String interfaceName);
	public boolean authorizeInterface(String serviceName, String interfaceName, String sessionID);
	public boolean authorizeMethod(String serviceName, String interfaceName, String methodName);
	public boolean authorizeMethod(String serviceName, String interfaceName, String methodName, String sessionID);
	
	
	/** retrieves the SecurityAdmin interface */
	public SecurityAdminService getSecurityAdmin();

	/** createClientInterceptor creates an interceptor that fills in the
	 *  SecurityServiceContext (basically, the session ID of the caller)
	 */
	public boolean createClientInterceptor();

	/** createCasClientInterceptor creates an interceptor that fills in the
	 *  SecurityServiceContext and checks ACLs on outbound calls
	 */
	public boolean createCasClientInterceptor(String casSessionID);

	/** createServerInterceptor creates an interceptor that retrieves the
	 *  SecurityServiceContext and checks ACLs on inbound calls
	 */
	public boolean createServerInterceptor(String serviceName);

	/** getClientSessionId retrieves the session ID of the caller */
	public String getClientSessionId();

	/** getClientUid retrieves the User ID of the caller */
	public String getClientUid();

	/** get the User ID, given any Session ID */
	public String getUidForSession(String sessionID);

	/** change password */
	public boolean changePassword(String sessionID, String oldPassword, String newPassword);
	public boolean changePasswordV2(String sessionID, String oldPassword, String newPassword) throws WeakPasswordException, PasswordUpdateException;

	/** normal logoff */
	public boolean logoutUser();

	/** normal logoff */
	public boolean logoutUser(String sessionID);

	/** method to be called to clean up the cache on logout **/
	public void removeSessionFromCache(String sessionID);
	public boolean deactivateUser(String userName);		

	public boolean initialize(com.cboe.infrastructureServices.systemsManagementService.ConfigurationService configService) ;

	/** Get all Roles for a Session ID */
	public String[] getRolesForSession(String sessionID);

	/** Get all Roles for a User ID */
	public String[] getRolesForUid(String userID);
}
