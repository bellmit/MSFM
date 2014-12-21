package com.cboe.securityService.securityClient;

/**
	Public name definitions for storing security object references in the trading service
	@author Basit Hussain
*/

public class SecurityNameDefinitions{

	public static String AUTHENTICATOR = System.getProperty("Security.Authenticator.ServiceType","SecurityServerAuthenticator");
	public static String AUTHENTICATOR_REMOTE = System.getProperty("Security.Authenticator.ServiceType.Remote","SecurityServerAuthenticator");
        public static String SECAUTHENTICATOR = System.getProperty("Security.SecAuthenRefLookUpName","SecurityServerAuthenticatorSecondary");
	//public static String AUTHPOANAME = System.getProperty("Security.AuthPoaName","SecurityServerAuthenticator");
	public static String AUTHPOANAME = "SecurityServerAuthenticator";
	public static String AUTHORIZOR = System.getProperty("Security.Authorizor.ServiceType","SecurityServerAuthorize");
	public static String AUTHORIZOR_REMOTE = System.getProperty("Security.Authorizor.ServiceType.Remote","SecurityServerAuthorize");
        public static String SECAUTHORIZOR  = System.getProperty("Security.SecAuthorRefLookUpName","SecurityServerAuthorizeSecondary");
	public static String LOGOUT =  System.getProperty("Security.Logout.ServiceType","SecurityServerLogout");
	public static String LOGOUT_REMOTE =  System.getProperty("Security.Logout.ServiceType.Remote","SecurityServerLogout");
        public static String SECLOGOUT = System.getProperty("Security.SecLogOutRefLookUpName","SecurityServerLogoutSecondary");
	public static String LOGOUTPOANAME = System.getProperty("Security.LogOutPoaName","SecurityServerLogout");
	public static String CASAUTHENTICATOR = "CASSecurityServerAuthenticator";
	public static String CASAUTHORIZOR = "CASSecurityServerAuthorize";
	public static String CASLOCKOUT = "CASSecurityServerLockout";
	public static String CASLOGOUT = "CASSecurityServerLogout";
	public static String CASBANK = "CASSecurityServerTestBank";
	public static String BANK = "SecurityServerTestBank";

	public static final String CONSTRAINT_HOSTNAME = "HostName";
        public static final String CONSTRAINT_HOSTNAME_VALUE = System.getProperty("Constraint.HostName","");
	public static final String REMOTE_HOSTNAME = "RemoteHostName";
        public static final String REMOTE_HOSTNAME_VALUE = System.getProperty("Constraint.RemoteHostName","");

}
