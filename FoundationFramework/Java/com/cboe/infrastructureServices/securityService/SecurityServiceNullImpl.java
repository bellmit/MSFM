package com.cboe.infrastructureServices.securityService;

import com.cboe.infrastructureServices.systemsManagementService.*;
import com.cboe.idl.infrastructureServices.securityService.securityAdmin.SecurityAdminService;
import org.omg.CORBA.IntHolder;
import org.omg.securityLevel2.ACLHolder;
import org.omg.security.UserPropertiesV2;

/**
 * The SecurityService null implementation. Use only for testing purposes.
 * 
 * @author Chuck Maslowski
 * @version 3.2
 */
public class SecurityServiceNullImpl
extends SecurityServiceBaseImpl
{
	private static String NULL = "null";

	public boolean initialize(ConfigurationService configService)
	{
		return true;
	}

        public SecurityAdminService getSecurityAdmin()
	{
		return null;
	}

        public UserPropertiesV2 authCertificate()
	{
		return new UserPropertiesV2(NULL, Thread.currentThread().getName(), new String[0], 0, NULL);
	}

        public UserPropertiesV2 authCertificate(String userName, String password, String certFile)
	{
		return new UserPropertiesV2(userName, Thread.currentThread().getName(), new String[0], 0, NULL);
	}

        public UserPropertiesV2 authCertificateUnion()
	{
		return new UserPropertiesV2(NULL, Thread.currentThread().getName(), new String[0], 0, NULL);
	}

        public UserPropertiesV2 authCertificateUnion(String userName, String password, String certFile)
	{
		return new UserPropertiesV2(userName, Thread.currentThread().getName(), new String[0], 0, NULL);
	}

        public UserPropertiesV2 authPassword()
	{
		return new UserPropertiesV2(NULL, Thread.currentThread().getName(), new String[0], 0, NULL);
	}

        public UserPropertiesV2 authPassword(String userName, String password)
	{
		return new UserPropertiesV2(userName, Thread.currentThread().getName(), new String[0], 0, NULL);
	}

        public UserPropertiesV2 authPasswordUnion()
	{
		return new UserPropertiesV2(NULL, Thread.currentThread().getName(), new String[0], 0, NULL);
	}

        public UserPropertiesV2 authPasswordUnion(String userName, String password)
	{
		return new UserPropertiesV2(userName, Thread.currentThread().getName(), new String[0], 0, NULL);
	}

	public String authenticateWithCertificate()
	{
		return Thread.currentThread().getName();
	}
	public String authenticateWithCertificate(String userName, String password , String certFile)
	{
		return  authenticateWithCertificate();
	}
	public String authenticateWithCertificateUnion()
	{
		return authenticateWithCertificate();
	}
	public String authenticateWithCertificateUnion(String userName, String password, String certFile)
	{
		return authenticateWithCertificate();
	}
	public String authenticateWithPassword(String userName, String password)
	{
		return authenticateWithPassword();
	}
	public String authenticateWithPasswordUnion()
	{
		return authenticateWithPassword();
	}
	public String authenticateWithPasswordUnion(String userName, String password)
	{
		return authenticateWithPassword();
	}
	public String authenticateWithPassword()
	{
		return Thread.currentThread().getName();
	}

	public String authenticateWithPassword(String userName, String password, String ipAddress)
	throws InvalidIPException, AuthenticationException
	{
		return authenticateWithPassword();
	}
	public String authenticateWithPasswordUnion(String userName, String password, String ipAddress)
	throws InvalidIPException, AuthenticationException
	{
		return authenticateWithPasswordUnion();
	}
	public UserPropertiesV2 authPassword(String userName, String password, String ipAddress)
	throws InvalidIPException, AuthenticationException
	{
		return authPassword();
	}
	public UserPropertiesV2 authPasswordUnion(String userName, String password, String ipAddress)
	throws InvalidIPException, AuthenticationException
	{
		return authPasswordUnion();
	}
	public boolean authorize(String serviceName, ACLHolder anACLHolder, IntHolder loginTime)
	{
		return true;
	}	
	public boolean authorize(String serviceName, String sessionID)
	{
		return true;
	}	
	public boolean authorize(String serviceName)
	{
		return true;
	}
	public boolean deactivateUser(String userName)
	{
		return true;
	}

        /** change password */
    public boolean changePassword(String sessionID, String oldPassword, String newPassword)
	{
		return true;
	}

    public boolean changePasswordV2(String sessionID, String oldPassword, String newPassword)
	{
		return true;
	}

	/** createClientInterceptor creates an interceptor that fills in the
	 *  SecurityServiceContext (basically, the session ID of the caller)
	 */
	public boolean createClientInterceptor()
	{
		return true;
	}

	/** createCasClientInterceptor creates an interceptor that fills in the
	 *  SecurityServiceContext and checks ACLs on outbound calls
	 */
	public boolean createCasClientInterceptor(String casSessionID)
	{
		return true;
	}

	/** createServerInterceptor creates an interceptor that retrieves the
	 *  SecurityServiceContext and checks ACLs on inbound calls
	 */
	public boolean createServerInterceptor(String serviceName)
	{
		return true;
	}

	/** getClientSessionId retrieves the session ID of the caller */
	public String getClientSessionId()
	{
		return NULL;
	}

	/** getClientUid retrieves the User ID of the caller */
	public String getClientUid()
	{
		return NULL;
	}

	/** get the User ID, given any Session ID */
	public String getUidForSession(String sessionID)
	{
		return NULL;
	}

	/** normal logoff */
	public boolean logoutUser()
	{
		return true;
	}

	/** normal logoff */
	public boolean logoutUser(String sessionID)
	{
		return true;
	}

	/** method to be called to clean up the cache on logout **/
	public void removeSessionFromCache(String sessionID)
	{
	}

	/** Interface Level authorization */
	public boolean authorizeInterface(String serviceName, String interfaceName)
	{
		return true;
	}

	/** Interface Level authorization */
	public boolean authorizeInterface(String serviceName, String interfaceName, String sessionID)
	{
		return true;
	}

	/** Method Level authorization */
	public boolean authorizeMethod(String serviceName, String interfaceName, String methodName)
	{
		return true;
	}

	/** Method Level authorization */
	public boolean authorizeMethod(String serviceName, String interfaceName, String methodName, String sessionID)
	{
		return true;
	}

	/** Return the Session's role list. */
	public String[] getRolesForSession(String sessionID)
	throws IllegalArgumentException
	{
		return new String[0];
	}

	/** Return the user's role list. */
	public String[] getRolesForUid(String userID)
	throws IllegalArgumentException
	{
		return new String[0];
	}
}
