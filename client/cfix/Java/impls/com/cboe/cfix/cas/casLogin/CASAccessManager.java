package com.cboe.cfix.cas.casLogin;

import java.util.Date;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.ExceptionDetails;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiConstants.LoginSessionTypes;
import com.cboe.idl.cmiV3.UserSessionManagerV3;
import com.cboe.idl.cmiV4.UserSessionManagerV4;
import com.cboe.idl.cmiV5.UserSessionManagerV5;
import com.cboe.idl.cmiV6.UserSessionManagerV6;


public class CASAccessManager {

	protected org.omg.CORBA.ORB orb; // Orb provided to CASAccessManager when
										// it was constructed

	String userAccessIOR; // Stringified IOR returned by CAS

	String casVersion; // Version returned by CAS as a string

	protected static final boolean gmdTextMessaging = false;

	public static String USER_ACCESS_V2_REFERENCENAME = "/UserAccessV2.ior";

	public static String USER_ACCESS_V3_REFERENCENAME = "/UserAccessV3.ior";

	public static String USER_ACCESS_V4_REFERENCENAME = "/UserAccessV4.ior";

    public static String USER_ACCESS_V5_REFERENCENAME = "/UserAccessV5.ior";

    public static String USER_ACCESS_V6_REFERENCENAME = "/UserAccessV6.ior";

	String userAccessV2IOR;

	String userAccessV3IOR;

	String userAccessV4IOR;

    String userAccessV5IOR;

    String userAccessV6IOR;

	protected String casIPAddress; // Keep the CAS IP Address and TCP Port Number
							// around

	protected int casTCPPortNumber; // in case the user wants to access it

	//
	// References to the CAS Interfaces
	//
	// The CASAccessManager is responsible for obtaining the references
	// to the CAS interfaces on an as needed basis.
	//
	com.cboe.idl.cmi.Administrator administrator;

	com.cboe.idl.cmi.UserAccess userAccess;

	protected com.cboe.idl.cmi.UserSessionManager userSessionManager;
    protected com.cboe.idl.cmiV4.UserSessionManagerV4 userSessionManager4;
//    userSessionManager4.getMarketQueryV4()


    com.cboe.idl.cmi.OrderEntry orderEntry;

	com.cboe.idl.cmi.OrderQuery orderQuery;

	com.cboe.idl.cmi.TradingSession tradingSession;

	com.cboe.idl.cmi.ProductDefinition productDefinition;

	com.cboe.idl.cmi.MarketQuery marketQuery;

	com.cboe.idl.cmi.Quote quote;

	com.cboe.idl.cmi.ProductQuery productQuery;

	com.cboe.idl.cmi.UserPreferenceQuery userPreferenceQuery;

	com.cboe.idl.cmi.UserTradingParameters userTradingParameters;

	com.cboe.idl.cmi.UserHistory userHistory;

	// services defined in cmiV2
	com.cboe.idl.cmiV2.Quote quoteV2;

	com.cboe.idl.cmiV2.MarketQuery marketQueryV2;

	com.cboe.idl.cmiV2.OrderQuery orderQueryV2;

	com.cboe.idl.cmiV2.UserAccessV2 userAccessV2;

	com.cboe.idl.cmiV2.SessionManagerStructV2 sessionManagerStructV2;

	protected com.cboe.idl.cmiV2.UserSessionManagerV2 userSessionManagerV2;

	// service defined in cmiV3
	com.cboe.idl.cmiV3.OrderEntry orderEntryV3;

	com.cboe.idl.cmiV3.OrderQuery orderQueryV3;

	com.cboe.idl.cmiV3.Quote quoteV3;

	com.cboe.idl.cmiV3.MarketQuery marketQueryV3;

	com.cboe.idl.cmiV3.UserAccessV3 userAccessV3;

	protected com.cboe.idl.cmiV3.UserSessionManagerV3 userSessionManagerV3;

	// service defined in cmiV4
	com.cboe.idl.cmiV4.UserAccessV4 userAccessV4;

	protected com.cboe.idl.cmiV4.UserSessionManagerV4 userSessionManagerV4;

	com.cboe.idl.cmiV4.MarketQuery marketQueryV4;

    // service defined in cmiV5
    com.cboe.idl.cmiV5.OrderEntry orderEntryV5;

    com.cboe.idl.cmiV5.UserAccessV5 userAccessV5;

    com.cboe.idl.cmiV6.UserAccessV6 userAccessV6;

    protected com.cboe.idl.cmiV5.UserSessionManagerV5 userSessionManagerV5;

    protected com.cboe.idl.cmiV6.UserSessionManagerV6 userSessionManagerV6;

	/**
	 * The CASAccessManager is responsible for providing a CMIUserSessionAdmin
	 * interface to the CAS
	 */
	protected com.cboe.idl.cmiCallback.CMIUserSessionAdmin userSessionAdminCallback;

	/**
	 * Constructs a CAS Access Manager
	 * 
	 * @param orb
	 *            ORB provided by client
	 */
	public CASAccessManager(org.omg.CORBA.ORB orb) {
		this.orb = orb;
	}

	public org.omg.CORBA.ORB getOrb() {
		return this.orb;
	}

	/**
	 * Logon to the CAS
	 * 
	 * @param userLogonStruct
	 * @param userSessionAdminCallback
	 *            Provide a constructed userSession admin callback object. The
	 *            client is responsible
	 * @param casIPAddress
	 *            The IP Address or domain name of the CAS
	 * @param casTCPPortNumber
	 *            The TCP Port Number of the CAS HTTP Server
	 */
	public com.cboe.idl.cmi.UserSessionManager logon(
			com.cboe.idl.cmiUser.UserLogonStruct userLogonStruct,
			com.cboe.idl.cmiCallback.CMIUserSessionAdmin userSessionAdminCallback,
			String casIPAddress, int casTCPPortNumber)
			throws com.cboe.exceptions.SystemException,
			com.cboe.exceptions.CommunicationException,
			com.cboe.exceptions.AuthorizationException,
			com.cboe.exceptions.AuthenticationException,
			com.cboe.exceptions.DataValidationException
    {
		this.userSessionAdminCallback = userSessionAdminCallback;
		// orb.connect(userSessionAdminCallback); // We will connect the object
		// for the user
		this.casIPAddress = casIPAddress;
		this.casTCPPortNumber = casTCPPortNumber;

		UserAccessLocator locator = new UserAccessLocator(casIPAddress,
				casTCPPortNumber);

		//
		// UserLocator.obtainIOR() will throw a
		// com.cboe.exceptions.CommunicationException
		// if it cannot talk to the CAS. This will be handled by the
		// CASAccessManager client.
		//
		this.userAccessIOR = locator.obtainIOR();

		org.omg.CORBA.Object objRef;

		objRef = orb.string_to_object(userAccessIOR);

		//
		// The UserAccessHelper Class is generated by the CORBA IDL
		// compiler for Java.
		//

		userAccess = com.cboe.idl.cmi.UserAccessHelper.narrow(objRef);

		short sessionType = LoginSessionTypes.PRIMARY;
		//
		// Exceptions thrown by the UserAccess.logon() operation will be handled
		// by the CASAccessManager client.
		//
		userSessionManager = userAccess.logon(userLogonStruct, sessionType,
				userSessionAdminCallback, gmdTextMessaging);

		return userSessionManager;
	}

	/**
	 * Return the object reference for the UserSessionManager interface on the
	 * CAS
	 * 
	 */
	public com.cboe.idl.cmi.UserSessionManager getUserSessionManager()
			throws com.cboe.exceptions.AuthorizationException {

		if (userSessionManager == null) {
			com.cboe.exceptions.AuthorizationException authorizationException = new com.cboe.exceptions.AuthorizationException();
			short severity = 1;
			authorizationException.details = new com.cboe.exceptions.ExceptionDetails(
					"CASAccessManager Error: Trying to access UserSessionManager before being logged on or after being logged off",
					new Date().toString(), severity, 9999);
			throw authorizationException;
		}

		return userSessionManager;
	}

	/**
	 * Return the object reference for the Administrator interface on the CAS
	 * 
	 */
	public com.cboe.idl.cmi.Administrator getAdministrator()
			throws com.cboe.exceptions.SystemException,
			com.cboe.exceptions.CommunicationException,
			com.cboe.exceptions.AuthorizationException {
		if (administrator == null) {
			administrator = getUserSessionManager().getAdministrator();
		}
		return administrator;
	}

	/**
	 * Return the object reference for the MarketQuery interface on the CAS
	 * 
	 */
	public com.cboe.idl.cmi.MarketQuery getMarketQuery()
			throws com.cboe.exceptions.SystemException,
			com.cboe.exceptions.CommunicationException,
			com.cboe.exceptions.AuthorizationException {
		if (marketQuery == null) {
			marketQuery = getUserSessionManager().getMarketQuery();
		}
		return marketQuery;
	}

	/**
	 * Return the object reference for the OrderEntry interface on the CAS
	 * 
	 */
	public com.cboe.idl.cmi.OrderEntry getOrderEntry()
			throws com.cboe.exceptions.SystemException,
			com.cboe.exceptions.CommunicationException,
			com.cboe.exceptions.AuthorizationException {
		if (orderEntry == null) {
			orderEntry = getUserSessionManager().getOrderEntry();
		}
		return orderEntry;
	}

	public com.cboe.idl.cmiV3.OrderEntry getOrderEntryV3()
			throws com.cboe.exceptions.SystemException,
			com.cboe.exceptions.CommunicationException,
			com.cboe.exceptions.AuthorizationException {
		if (orderEntryV3 == null) {
			orderEntryV3 = getUserSessionManagerV3().getOrderEntryV3();
		}
		return orderEntryV3;
	}

    public com.cboe.idl.cmiV5.OrderEntry getOrderEntryV5()
			throws com.cboe.exceptions.SystemException,
			com.cboe.exceptions.CommunicationException,
			com.cboe.exceptions.AuthorizationException {
		if (orderEntryV5 == null) {
			orderEntryV5 = getUserSessionManagerV5().getOrderEntryV5();
		}
		return orderEntryV5;
	}

	/**
	 * Return the object reference for the OrderQuery interface on the CAS
	 * 
	 */
	public com.cboe.idl.cmi.OrderQuery getOrderQuery()
			throws com.cboe.exceptions.SystemException,
			com.cboe.exceptions.CommunicationException,
			com.cboe.exceptions.AuthorizationException {
		if (orderQuery == null) {
			orderQuery = getUserSessionManager().getOrderQuery();
		}
		return orderQuery;
	}

	public com.cboe.idl.cmiV3.OrderQuery getOrderQueryV3()
			throws com.cboe.exceptions.SystemException,
			com.cboe.exceptions.CommunicationException,
			com.cboe.exceptions.AuthorizationException {
		if (orderQueryV3 == null) {
			orderQueryV3 = getUserSessionManagerV3().getOrderQueryV3();
		}
		return orderQueryV3;
	}

	/**
	 * Return the object reference for the ProductDefinition interface on the
	 * CAS
	 * 
	 */
	public com.cboe.idl.cmi.ProductDefinition getProductDefinition()
			throws com.cboe.exceptions.SystemException,
			com.cboe.exceptions.CommunicationException,
			com.cboe.exceptions.AuthorizationException {
		if (productDefinition == null) {
			productDefinition = getUserSessionManager().getProductDefinition();
		}
		return productDefinition;
	}

	/**
	 * Return the object reference for the ProductQuery interface on the CAS
	 * 
	 */
	public com.cboe.idl.cmi.ProductQuery getProductQuery()
			throws com.cboe.exceptions.SystemException,
			com.cboe.exceptions.CommunicationException,
			com.cboe.exceptions.AuthorizationException {
		if (productQuery == null) {
			productQuery = getUserSessionManager().getProductQuery();
		}
		return productQuery;
	}

	/**
	 * Return the object reference for the Quote interface on the CAS
	 * 
	 */
	public com.cboe.idl.cmi.Quote getQuote()
			throws com.cboe.exceptions.SystemException,
			com.cboe.exceptions.CommunicationException,
			com.cboe.exceptions.AuthorizationException {
		if (quote == null) {
			quote = getUserSessionManager().getQuote();
		}
		return quote;
	}

	/**
	 * Return the object reference for the TradingSession interface on the CAS
	 * 
	 */
	public com.cboe.idl.cmi.TradingSession getTradingSession()
			throws com.cboe.exceptions.SystemException,
			com.cboe.exceptions.CommunicationException,
			com.cboe.exceptions.AuthorizationException {
		if (tradingSession == null) {
			tradingSession = getUserSessionManager().getTradingSession();
		}
		return tradingSession;
	}

	/**
	 * Return the object reference for the UserPreferenceQuery interface on the
	 * CAS
	 * 
	 */
	public com.cboe.idl.cmi.UserPreferenceQuery getUserPreferenceQuery()
			throws com.cboe.exceptions.SystemException,
			com.cboe.exceptions.CommunicationException,
			com.cboe.exceptions.AuthorizationException {
		if (userPreferenceQuery == null) {
			userPreferenceQuery = getUserSessionManager()
					.getUserPreferenceQuery();
		}
		return userPreferenceQuery;
	}

	/**
	 * Return object reference to UserTradingParameters service on the CAS.
	 */
	public com.cboe.idl.cmi.UserTradingParameters getUserTradingParameters()
			throws com.cboe.exceptions.SystemException,
			com.cboe.exceptions.CommunicationException,
			com.cboe.exceptions.AuthorizationException {
		if (userTradingParameters == null) {
			userTradingParameters = getUserSessionManager()
					.getUserTradingParameters();
		}
		return userTradingParameters;
	}

	/**
	 * Return object reference to UserHistory service on the CAS.
	 */
	public com.cboe.idl.cmi.UserHistory getUserHistory()
			throws com.cboe.exceptions.SystemException,
			com.cboe.exceptions.CommunicationException,
			com.cboe.exceptions.AuthorizationException {
		if (userHistory == null) {
			userHistory = getUserSessionManager().getUserHistory();
		}
		return userHistory;
	}

	/**
	 * Return the object reference for the Version interface on the CAS.
	 * 
	 */
	public String getVersion() throws com.cboe.exceptions.SystemException,
			com.cboe.exceptions.CommunicationException,
			com.cboe.exceptions.AuthorizationException {
		if (casVersion == null) {
			casVersion = getUserSessionManager().getVersion();
		}
		return casVersion;
	}

	/**
	 * Logoff the CAS - reinitialize this instance of the CASAccessManager
	 * 
	 */

	public void logoff() throws com.cboe.exceptions.SystemException,
			com.cboe.exceptions.CommunicationException,
			com.cboe.exceptions.AuthorizationException {

		getUserSessionManager().logout();
		clearReferences();

	}

	public void logoffV3() throws com.cboe.exceptions.SystemException,
			com.cboe.exceptions.CommunicationException,
			com.cboe.exceptions.AuthorizationException {

		getUserSessionManagerV3().logout();
		clearReferences();

	}

	public void logoffV4() throws com.cboe.exceptions.SystemException,
			com.cboe.exceptions.CommunicationException,
			com.cboe.exceptions.AuthorizationException {

		getUserSessionManagerV4().logout();
		clearReferences();

	}

    public void logoffV5() throws com.cboe.exceptions.SystemException,
			com.cboe.exceptions.CommunicationException,
			com.cboe.exceptions.AuthorizationException {

		getUserSessionManagerV5().logout();
		clearReferences();

	}

    public void logoffV6() throws com.cboe.exceptions.SystemException,
			com.cboe.exceptions.CommunicationException,
			com.cboe.exceptions.AuthorizationException {

		getUserSessionManagerV6().logout();
		clearReferences();
	}

    protected void clearReferences() {
		//
		// Clear out the CAS interface references
		//

		administrator = null;
		userSessionManager = null;
		userAccess = null;
		orderQuery = null;
		marketQuery = null;
		orderEntry = null;
		tradingSession = null;
		productQuery = null;
		productDefinition = null;
		userPreferenceQuery = null;
		quote = null;
		casVersion = null;
		userAccessIOR = null;
		userTradingParameters = null;
		userHistory = null;
		userSessionAdminCallback = null;
		casIPAddress = null;
		casTCPPortNumber = 0;

		userAccessV2IOR = null;
		quoteV2 = null;
		marketQueryV2 = null;
		orderQueryV2 = null;
		userAccessV2 = null;
		sessionManagerStructV2 = null;
		userSessionManagerV2 = null;

		userAccessV3IOR = null;
		orderEntryV3 = null;
		orderQueryV3 = null;
		quoteV3 = null;
		marketQueryV3 = null;
		userAccessV3 = null;
		userSessionManagerV3 = null;

		userAccessV4 = null;
		userSessionManagerV4 = null;

        userAccessV5 = null;
        userSessionManagerV5 = null;
        orderEntryV5 = null;
	}

	/**
	 * Provides the user with the Stringified IOR returned by the
	 * UserAccessLocator object
	 */
	public String getUserAccessIOR() {
		return userAccessIOR;
	}

	/**
	 * Provides the user with the Stringified IOR returned by the
	 * UserAccessLocator object
	 */
	public String getUserAccessV2IOR() {
		return userAccessV2IOR;
	}

	/**
	 * Provides the user with access to the CAS IP Address that was used to
	 * access the CAS
	 */
	public String getCASIPAddress() {
		return casIPAddress;
	}

	/**
	 * Provides the user with access to the CAS TCP Port Number that was used to
	 * access the CAS
	 */
	public int getCASTCPPortNumber() {
		return casTCPPortNumber;
	}

	/**
	 * Logon to the CAS
	 * 
	 * @param userLogonStruct
	 * @param userSessionAdminCallback
	 *            Provide a constructed userSession admin callback object. The
	 *            client is responsible
	 * @param casIPAddress
	 *            The IP Address or domain name of the CAS
	 * @param casTCPPortNumber
	 *            The TCP Port Number of the CAS HTTP Server
	 */
	public void logonV2(
			com.cboe.idl.cmiUser.UserLogonStruct userLogonStruct,
			com.cboe.idl.cmiCallback.CMIUserSessionAdmin userSessionAdminCallback,
			String casIPAddress, int casTCPPortNumber)
			throws com.cboe.exceptions.SystemException,
			com.cboe.exceptions.CommunicationException,
			com.cboe.exceptions.AuthorizationException,
			com.cboe.exceptions.AuthenticationException,
			com.cboe.exceptions.DataValidationException,
			com.cboe.exceptions.NotFoundException {

		this.userSessionAdminCallback = userSessionAdminCallback;

		this.casIPAddress = casIPAddress;
		this.casTCPPortNumber = casTCPPortNumber;

		UserAccessLocator locator = new UserAccessLocator(casIPAddress,
				casTCPPortNumber, USER_ACCESS_V2_REFERENCENAME);

		//
		// UserLocator.obtainIOR() will throw a
		// com.cboe.exceptions.CommunicationException
		// if it cannot talk to the CAS. This will be handled by the
		// CASAccessManager client.
		//
		this.userAccessV2IOR = locator.obtainIOR();

		org.omg.CORBA.Object objRef;

		objRef = orb.string_to_object(userAccessV2IOR);

		//
		// The UserAccessHelper Class is generated by the CORBA IDL
		// compiler for Java.
		//

		userAccessV2 = com.cboe.idl.cmiV2.UserAccessV2Helper.narrow(objRef);

		short sessionType = LoginSessionTypes.PRIMARY;
		//
		// Exceptions thrown by the UserAccess.logon() operation will be handled
		// by the CASAccessManager client.
		//
		sessionManagerStructV2 = userAccessV2.logon(userLogonStruct,
				sessionType, userSessionAdminCallback, gmdTextMessaging);
		userSessionManagerV2 = sessionManagerStructV2.sessionManagerV2;
		userSessionManager = sessionManagerStructV2.sessionManager;
		return;
	}

	public void logonV2WhileAlreadyLoggedin(
			com.cboe.idl.cmi.UserSessionManager userSessionManager)
			throws com.cboe.exceptions.SystemException,
			com.cboe.exceptions.CommunicationException,
			com.cboe.exceptions.AuthorizationException,
			com.cboe.exceptions.AuthenticationException,
			com.cboe.exceptions.DataValidationException,
			com.cboe.exceptions.NotFoundException {

		this.userSessionManager = userSessionManager;

		UserAccessLocator locator = new UserAccessLocator(casIPAddress,
				casTCPPortNumber, USER_ACCESS_V2_REFERENCENAME);

		//
		// UserLocator.obtainIOR() will throw a
		// com.cboe.exceptions.CommunicationException
		// if it cannot talk to the CAS. This will be handled by the
		// CASAccessManager client.
		//
		this.userAccessV2IOR = locator.obtainIOR();

		org.omg.CORBA.Object objRef;

		objRef = orb.string_to_object(userAccessV2IOR);

		userAccessV2 = com.cboe.idl.cmiV2.UserAccessV2Helper.narrow(objRef);

		userSessionManagerV2 = userAccessV2
				.getUserSessionManagerV2(this.userSessionManager);

		return;
	}

	/**
	 * Return the object reference for the UserSessionManager interface on the
	 * CAS
	 * 
	 */
	public com.cboe.idl.cmiV2.UserSessionManagerV2 getUserSessionManagerV2()
			throws com.cboe.exceptions.AuthorizationException {

		if (userSessionManagerV2 == null) {
			com.cboe.exceptions.AuthorizationException authorizationException = new com.cboe.exceptions.AuthorizationException();
			short severity = 1;
			authorizationException.details = new com.cboe.exceptions.ExceptionDetails(
					"CASAccessManager Error: Trying to access UserSessionManagerV2 before being logged on or after being logged off",
					new Date().toString(), severity, 9999);
			throw authorizationException;
		}

		return userSessionManagerV2;
	}

	/**
	 * Return the object reference for the MarketQuery interface on the CAS
	 * 
	 */
	public com.cboe.idl.cmiV2.MarketQuery getMarketQueryV2()
			throws com.cboe.exceptions.SystemException,
			com.cboe.exceptions.CommunicationException,
			com.cboe.exceptions.AuthorizationException {
		if (marketQueryV2 == null) {
			marketQueryV2 = getUserSessionManagerV2().getMarketQueryV2();
		}
		return marketQueryV2;
	}

	/**
	 * Return the object reference for the OrderQuery interface on the CAS
	 * 
	 */
	public com.cboe.idl.cmiV2.OrderQuery getOrderQueryV2()
			throws com.cboe.exceptions.SystemException,
			com.cboe.exceptions.CommunicationException,
			com.cboe.exceptions.AuthorizationException {
		if (orderQueryV2 == null) {
			orderQueryV2 = getUserSessionManagerV2().getOrderQueryV2();
		}
		return orderQueryV2;
	}

	/**
	 * Return the object reference for the Quote interface on the CAS
	 * 
	 */
	public com.cboe.idl.cmiV2.Quote getQuoteV2()
			throws com.cboe.exceptions.SystemException,
			com.cboe.exceptions.CommunicationException,
			com.cboe.exceptions.AuthorizationException {
		if (quoteV2 == null) {
			quoteV2 = getUserSessionManagerV2().getQuoteV2();
		}
		return quoteV2;
	}

	public com.cboe.idl.cmiV3.Quote getQuoteV3()
			throws com.cboe.exceptions.SystemException,
			com.cboe.exceptions.CommunicationException,
			com.cboe.exceptions.AuthorizationException {
		if (quoteV3 == null) {
			quoteV3 = getUserSessionManagerV3().getQuoteV3();
		}
		return quoteV3;
	}

	public void logonV3(
			com.cboe.idl.cmiUser.UserLogonStruct userLogonStruct,
			com.cboe.idl.cmiCallback.CMIUserSessionAdmin userSessionAdminCallback,
			String casIPAddress, int casTCPPortNumber)
			throws com.cboe.exceptions.SystemException,
			com.cboe.exceptions.CommunicationException,
			com.cboe.exceptions.AuthorizationException,
			com.cboe.exceptions.AuthenticationException,
			com.cboe.exceptions.DataValidationException,
			com.cboe.exceptions.NotFoundException {

		this.userSessionAdminCallback = userSessionAdminCallback;

		this.casIPAddress = casIPAddress;
		this.casTCPPortNumber = casTCPPortNumber;

		UserAccessLocator locator = new UserAccessLocator(casIPAddress,
				casTCPPortNumber, USER_ACCESS_V3_REFERENCENAME);

		//
		// UserLocator.obtainIOR() will throw a
		// com.cboe.exceptions.CommunicationException
		// if it cannot talk to the CAS. This will be handled by the
		// CASAccessManager client.
		//
		this.userAccessV3IOR = locator.obtainIOR();

		org.omg.CORBA.Object objRef;

		objRef = orb.string_to_object(userAccessV3IOR);

		//
		// The UserAccessHelper Class is generated by the CORBA IDL
		// compiler for Java.
		//

		userAccessV3 = com.cboe.idl.cmiV3.UserAccessV3Helper.narrow(objRef);

		short sessionType = LoginSessionTypes.PRIMARY;
		//
		// Exceptions thrown by the UserAccess.logon() operation will be handled
		// by the CASAccessManager client.
		//
		userSessionManagerV3 = userAccessV3.logon(userLogonStruct, sessionType,
				userSessionAdminCallback, gmdTextMessaging);
		userSessionManagerV2 = userSessionManagerV3;
		userSessionManager = userSessionManagerV3;
		return;
	}

	public UserSessionManagerV3 getUserSessionManagerV3()
			throws AuthorizationException {

		if (userSessionManagerV3 == null) {
			AuthorizationException authorizationException = new AuthorizationException();
			short severity = 1;
			authorizationException.details = new ExceptionDetails(
					"CASAccessManager Error: Trying to access UserSessionManagerV3 before being logged on or after being logged off",
					new Date().toString(), severity, 9999);
			throw authorizationException;
		}

		return userSessionManagerV3;
	}

	public com.cboe.idl.cmiV3.MarketQuery getMarketQueryV3()
			throws SystemException, CommunicationException,
			AuthorizationException {
		if (marketQueryV3 == null) {
			marketQueryV3 = getUserSessionManagerV3().getMarketQueryV3();
		}
		return marketQueryV3;
	}

	public com.cboe.idl.cmiV4.MarketQuery getMarketQueryV4()
			throws SystemException, CommunicationException,
			AuthorizationException {
		if (marketQueryV4 == null) {
			marketQueryV4 = getUserSessionManagerV4().getMarketQueryV4();
		}
		return marketQueryV4;
	}

   public void logonV4(
			com.cboe.idl.cmiUser.UserLogonStruct userLogonStruct,
			com.cboe.idl.cmiCallback.CMIUserSessionAdmin userSessionAdminCallback,
			String casIPAddress, int casTCPPortNumber)
			throws com.cboe.exceptions.SystemException,
			com.cboe.exceptions.CommunicationException,
			com.cboe.exceptions.AuthorizationException,
			com.cboe.exceptions.AuthenticationException,
			com.cboe.exceptions.DataValidationException,
			com.cboe.exceptions.NotFoundException
   {
            logonV4(userLogonStruct,userSessionAdminCallback,casIPAddress,casTCPPortNumber,LoginSessionTypes.PRIMARY);

   }


	public void logonV4(
			com.cboe.idl.cmiUser.UserLogonStruct userLogonStruct,
			com.cboe.idl.cmiCallback.CMIUserSessionAdmin userSessionAdminCallback,
			String casIPAddress, int casTCPPortNumber, short logonSessionType)
			throws com.cboe.exceptions.SystemException,
			com.cboe.exceptions.CommunicationException,
			com.cboe.exceptions.AuthorizationException,
			com.cboe.exceptions.AuthenticationException,
			com.cboe.exceptions.DataValidationException,
			com.cboe.exceptions.NotFoundException {

		this.userSessionAdminCallback = userSessionAdminCallback;

		this.casIPAddress = casIPAddress;
		this.casTCPPortNumber = casTCPPortNumber;

		UserAccessLocator locator = new UserAccessLocator(casIPAddress,
				casTCPPortNumber, USER_ACCESS_V4_REFERENCENAME);

		//
		// UserLocator.obtainIOR() will throw a
		// com.cboe.exceptions.CommunicationException
		// if it cannot talk to the CAS. This will be handled by the
		// CASAccessManager client.
		//
		this.userAccessV4IOR = locator.obtainIOR();

		org.omg.CORBA.Object objRef;

		objRef = orb.string_to_object(userAccessV4IOR);

		//
		// The UserAccessHelper Class is generated by the CORBA IDL
		// compiler for Java.
		//

		userAccessV4 = com.cboe.idl.cmiV4.UserAccessV4Helper.narrow(objRef);

		//
		// Exceptions thrown by the UserAccess.logon() operation will be handled
		// by the CASAccessManager client.
		//
		userSessionManagerV4 = userAccessV4.logon(userLogonStruct, logonSessionType,
				userSessionAdminCallback, gmdTextMessaging);
		userSessionManagerV3 = userSessionManagerV4;
		userSessionManagerV2 = userSessionManagerV4;
		userSessionManager = userSessionManagerV4;
		return;
	}

	public UserSessionManagerV4 getUserSessionManagerV4()
			throws AuthorizationException {

		if (userSessionManagerV4 == null) {
			AuthorizationException authorizationException = new AuthorizationException();
			short severity = 1;
			authorizationException.details = new ExceptionDetails(
					"CASAccessManager Error: Trying to access UserSessionManagerV4 before being logged on or after being logged off",
					new Date().toString(), severity, 9999);
			throw authorizationException;
		}

		return userSessionManagerV4;
	}

    public void logonV6(
			com.cboe.idl.cmiUser.UserLogonStruct userLogonStruct,
			com.cboe.idl.cmiCallback.CMIUserSessionAdmin userSessionAdminCallback,
			String casIPAddress, int casTCPPortNumber)
			throws com.cboe.exceptions.SystemException,
			com.cboe.exceptions.CommunicationException,
			com.cboe.exceptions.AuthorizationException,
			com.cboe.exceptions.AuthenticationException,
			com.cboe.exceptions.DataValidationException,
			com.cboe.exceptions.NotFoundException {

		this.userSessionAdminCallback = userSessionAdminCallback;

		this.casIPAddress = casIPAddress;
		this.casTCPPortNumber = casTCPPortNumber;

		UserAccessLocator locator = new UserAccessLocator(casIPAddress,
				casTCPPortNumber, USER_ACCESS_V6_REFERENCENAME);

		//
		// UserLocator.obtainIOR() will throw a
		// com.cboe.exceptions.CommunicationException
		// if it cannot talk to the CAS. This will be handled by the
		// CASAccessManager client.
		//
		this.userAccessV6IOR = locator.obtainIOR();

		org.omg.CORBA.Object objRef;

		objRef = orb.string_to_object(userAccessV6IOR);

		//
		// The UserAccessHelper Class is generated by the CORBA IDL
		// compiler for Java.
		//

		userAccessV6 = com.cboe.idl.cmiV6.UserAccessV6Helper.narrow(objRef);

		short sessionType = LoginSessionTypes.PRIMARY;
		//
		// Exceptions thrown by the UserAccess.logon() operation will be handled
		// by the CASAccessManager client.
		//
		userSessionManagerV6 = userAccessV6.logon(userLogonStruct, sessionType,
				userSessionAdminCallback, gmdTextMessaging);
        userSessionManagerV5 = userSessionManagerV6;
        userSessionManagerV4 = userSessionManagerV6;
        userSessionManagerV3 = userSessionManagerV6;
		userSessionManagerV2 = userSessionManagerV6;
		userSessionManager = userSessionManagerV6;
		return;
	}

    public void logonV5(
			com.cboe.idl.cmiUser.UserLogonStruct userLogonStruct,
			com.cboe.idl.cmiCallback.CMIUserSessionAdmin userSessionAdminCallback,
			String casIPAddress, int casTCPPortNumber)
                throws com.cboe.exceptions.SystemException,
                com.cboe.exceptions.CommunicationException,
                com.cboe.exceptions.AuthorizationException,
                com.cboe.exceptions.AuthenticationException,
                com.cboe.exceptions.DataValidationException,
                com.cboe.exceptions.NotFoundException {
        logonV5(userLogonStruct, userSessionAdminCallback,casIPAddress,casTCPPortNumber,LoginSessionTypes.PRIMARY);

    }

    public void logonV5(
			com.cboe.idl.cmiUser.UserLogonStruct userLogonStruct,
			com.cboe.idl.cmiCallback.CMIUserSessionAdmin userSessionAdminCallback,
			String casIPAddress, int casTCPPortNumber,short loginSessionTypes)
			throws com.cboe.exceptions.SystemException,
			com.cboe.exceptions.CommunicationException,
			com.cboe.exceptions.AuthorizationException,
			com.cboe.exceptions.AuthenticationException,
			com.cboe.exceptions.DataValidationException,
			com.cboe.exceptions.NotFoundException {

		this.userSessionAdminCallback = userSessionAdminCallback;

		this.casIPAddress = casIPAddress;
		this.casTCPPortNumber = casTCPPortNumber;

		UserAccessLocator locator = new UserAccessLocator(casIPAddress,
				casTCPPortNumber, USER_ACCESS_V5_REFERENCENAME);

		//
		// UserLocator.obtainIOR() will throw a
		// com.cboe.exceptions.CommunicationException
		// if it cannot talk to the CAS. This will be handled by the
		// CASAccessManager client.
		//
		this.userAccessV5IOR = locator.obtainIOR();

		org.omg.CORBA.Object objRef;

		objRef = orb.string_to_object(userAccessV5IOR);

		//
		// The UserAccessHelper Class is generated by the CORBA IDL
		// compiler for Java.
		//

		userAccessV5 = com.cboe.idl.cmiV5.UserAccessV5Helper.narrow(objRef);

		short sessionType = LoginSessionTypes.PRIMARY;
		//
		// Exceptions thrown by the UserAccess.logon() operation will be handled
		// by the CASAccessManager client.
		//
		userSessionManagerV5 = userAccessV5.logon(userLogonStruct, sessionType,
				userSessionAdminCallback, gmdTextMessaging);
		userSessionManagerV3 = userSessionManagerV5;
		userSessionManagerV2 = userSessionManagerV5;
		userSessionManager = userSessionManagerV5;
		return;
	}



    public UserSessionManagerV5 getUserSessionManagerV5()
			throws AuthorizationException {

		if (userSessionManagerV5 == null) {
			AuthorizationException authorizationException = new AuthorizationException();
			short severity = 1;
			authorizationException.details = new ExceptionDetails(
					"CASAccessManager Error: Trying to access UserSessionManagerV5 before being logged on or after being logged off",
					new Date().toString(), severity, 9999);
			throw authorizationException;
		}

		return userSessionManagerV5;
	}
    public UserSessionManagerV6 getUserSessionManagerV6()
			throws AuthorizationException {

		if (userSessionManagerV6 == null) {
			AuthorizationException authorizationException = new AuthorizationException();
			short severity = 1;
			authorizationException.details = new ExceptionDetails(
					"CASAccessManager Error: Trying to access UserSessionManagerV6 before being logged on or after being logged off",
					new Date().toString(), severity, 9999);
			throw authorizationException;
		}

		return userSessionManagerV6;
	}

}
