/*
 * Created on Jul 14, 2004
 *
 */
package com.cboe.presentation.fix.appia;

import com.cboe.idl.cmiAdmin.MessageStruct;
import com.cboe.idl.cmiCallback.CMIUserSessionAdmin;
import com.cboe.idl.cmiErrorCodes.CommunicationFailureCodes;
import com.cboe.idl.cmiUser.AccountStruct;
import com.cboe.idl.cmiUser.DpmStruct;
import com.cboe.idl.cmiUser.ExchangeAcronymStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.idl.cmiUser.ProfileStruct;
import com.cboe.idl.cmiUser.UserLogonStruct;
import com.cboe.idl.cmiUser.UserStruct;

import com.cboe.exceptions.AuthenticationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.SystemException;

import com.cboe.interfaces.presentation.common.logging.IGUILogger;
import com.cboe.interfaces.presentation.user.Role;

import com.cboe.util.ExceptionBuilder;

import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.properties.AppPropertiesFileFactory;

import com.cboe.domain.util.fixUtil.FixUtilConstants;
import com.cboe.domain.util.fixUtil.FixUtilMapper;
import com.cboe.domain.util.fixUtil.FixUtilUserDefinedFieldTable;
import com.javtech.appia.LogonMsg;
import com.javtech.appia.Logout;
import com.javtech.appia.MessageObject;
import com.javtech.appia.config.AppiaConfigurationData;
import com.javtech.appia.config.SysConfig;
import com.javtech.appia.middleware.*;

/**
 * Appia-specific implementation of a FIX session.
 *
 * In the Appia configuration, session events should be ON, which is the default,
 *  in order to parse the logon and logoff messages.
 * <code>middleware_session_events=ON</code>
 *
 * @author Don Mendelson
 *
 */
public class FixSessionImpl implements MiddlewareEventListenerIF {
	// TODO: move constants to separate class

	/** Property key for EOD switch on startup */
	public static final String FIXEODENABLED = "FIXEODEnabled";

	/** Property key for name of Appia connection */
	public static final String FIXCONNECTION = "FIXConnection";

	/** Property key for name of Appia .ini file */
	public static final String FIXINIFILE = "FIXINIFile";

	/** Property key for switch to request user profile */
	private static final String FIXPROFILEENABLED = "FIXProfileEnabled";

    public final static String WITH_ORDER_PUBLISH = "WITHORDERPUBLISH";
    public final static String WITHOUT_ORDER_PUBLISH = "WITHOUTORDERPUBLISH";

	// Appia configuration file name -- initialized in startup()
	private String appiaIniFile;

	// Appia connection block name -- initialized in startup()
    private String connection;

    // Registered handlers for received message types
	private FixMessageDispatcherRegisty dispatchers;

    /*
     * Specifies the value that flags numeric fields that are
     * not to be sent or that were not received
     */
    private int doNotSendValue = 0;

    // Is the session logged in?
	private boolean isConnected = false;

	// Used to coordinate logon ack with request on different threads
	private Object logonSynchronizer = new Object();

    // Interface to FIX engine
    private MiddlewareInterface middleware = null;

    // Message counter
    private int msgID = 0;

    // Correlate response messages with requests
    private ResponseSynchronizer responseSynchronizer = new ResponseSynchronizer();

    // Reason for failure to log on
	private String sessionReason = "";

	// Callback for session events
    private CMIUserSessionAdmin userSessionAdminConsumer;

    // User profile
    private UserStruct validUser = new UserStruct();

    /**
     * Creates a new FixSessionImpl with standard message dispatchers
     */
    public FixSessionImpl() {
    	setDispatchers(new StandardDispatcherRegistry());

    	// Initialize user profile
    	validUser.userAcronym = new ExchangeAcronymStruct("", "");
    	validUser.userId = "";
    	validUser.firm = new ExchangeFirmStruct("", "");
    	validUser.fullName = "";
    	validUser.role = Role.MARKET_MAKER.getRoleChar();
    	validUser.executingGiveupFirms = new ExchangeFirmStruct[1];
    	validUser.accounts = new AccountStruct[1];
    	validUser.assignedClasses = new int[0];
    	validUser.dpms = new DpmStruct[0];
    	validUser.profilesByClass = new ProfileStruct[0];
    	validUser.defaultProfile = new ProfileStruct();
    	validUser.defaultProfile.account = "";
    	validUser.defaultProfile.classKey = 0;
    	validUser.defaultProfile.executingGiveupFirm = new ExchangeFirmStruct("", "");
    	validUser.defaultProfile.subAccount = "";
    }

	/**
	 * @return Returns the appiaIniFile.
	 */
	public String getAppiaIniFile() {
		return appiaIniFile;
	}

    /**
     * @return name of connection
     */
    public String getConnection() {
        return connection;
    }
	/**
	 * @return Returns the dispatchers.
	 */
	public FixMessageDispatcherRegisty getDispatchers() {
		return dispatchers;
	}

    /**
	 * Get the value that Appia uses for a null numeric field
	 * @return Appia's do-not-send value
	 */
	public int getDoNotSendValue()
	{
		if (doNotSendValue == 0) {
			// Get a reference to the Appia configuration instance
			SysConfig appiaSysConfig = SysConfig.instance();
			doNotSendValue = appiaSysConfig.getInt(AppiaConfigurationData.DO_NOT_SEND_VALUE);
		}
		return doNotSendValue;
	}

	/**
	 * @return Returns the userSessionAdminConsumer.
	 */
    public CMIUserSessionAdmin getUserSessionAdminConsumer() {
		return userSessionAdminConsumer;
	}

	/**
	 * Get user profile
	 * @return user structure
	 */
	public UserStruct getValidUser() {
		return validUser;
	}

	/**
	 * Set user profile
	 * @param user valid user profile for session, based on Logon
	 */
	public void setValidUser(UserStruct user) {
		validUser = user;
	}

	/**
	 * Send a logon message to the counterparty and wait for acknowledement.
	 * @param userLogonStruct
	 * @throws AuthenticationException if logon is rejected
	 * @throws CommunicationException
	 * @throws SystemException
	 */
	public void logon(UserLogonStruct userLogonStruct) throws SystemException, CommunicationException, AuthenticationException {
		if (middleware == null) {
			try {
				startup();
			} catch (Exception e) {
				throw ExceptionBuilder.communicationException(
								"Failed to connect to FIX engine; "
								+ e.getMessage(),
								CommunicationFailureCodes.TRANSPORT_FAILURE);
			}
		}

		// If EOD switch is true, reset sequence numbers in FIX engine before logon
		if ("true".equals(System.getProperty(FIXEODENABLED))) {
			sendEODCommand();
		}

		LogonMsg msg = new LogonMsg( getDoNotSendValue() );
		msg.EncryptionMethod = FixUtilConstants.EncryptMethod.NONE;
		//  A reasonable default for heartbeat interval - make configurable?
		msg.HeartBtInt = 30;
		msg.header.SenderSubID = userLogonStruct.userId + ":" + userLogonStruct.password;

		// Populate TargetSubID with prod/test mode + key word to publish order status
		msg.header.TargetSubID =
			FixUtilMapper.getFixLoginSessionMode(userLogonStruct.loginMode) +
			":" + WITH_ORDER_PUBLISH;

		// Add parameter to get user profile for the FIX session unless it is disabled
		// for back compatibility with older FIXCAS.
		String isGui = "true";
		if( AppPropertiesFileFactory.isAppPropertiesAvailable() ) {
			isGui = AppPropertiesFileFactory.find().getValue("Session",
					FIXPROFILEENABLED);
		}
		IGUILogger logger = GUILoggerHome.find();
		if (logger.isDebugOn()) {
			logger.debug("Property " + FIXPROFILEENABLED +
					" (Get user profile from FIXCAS): "
					+ isGui,
					GUILoggerBusinessProperty.USER_SESSION);
		}

		if ( !"false".equals(isGui) ) {
			msg.header.TargetSubID +=":GUI";
		}

        FixUtilUserDefinedFieldTable  udftable = new FixUtilUserDefinedFieldTable();
        udftable.setValue("6600","1");

        msg.UserDefined = udftable.getUDFString();


		sendMessage(msg);

		try {
			synchronized (logonSynchronizer) {
				// Timeout period is configurable in Appia
				logonSynchronizer.wait();
			}
		} catch (InterruptedException e) {
		}
		if (!isConnected) {
			throw ExceptionBuilder.authenticationException("FIX logon failed; "
					+ sessionReason, 0);
		}
	}

	/**
	 * Send a logout message to the counterparty
	 * @throws CommunicationException if connection is already broken
	 */
	public void logout() throws CommunicationException {
		Logout msg = new Logout( getDoNotSendValue() );
		sendMessage(msg);
	}

    /* (non-Javadoc)
     * @see com.javtech.appia.middleware.MiddlewareEventListenerIF#onMiddlewareEvent(com.javtech.appia.middleware.MiddlewareEvent)
     */
    public void onMiddlewareEvent(MiddlewareEvent event) throws Exception {
        try {
			switch (event.getEventType()) {
			case MiddlewareEvent.APPLICATION_MESSAGE_RECEIVED :
			case MiddlewareEvent.SESSION_MESSAGE_RECEIVED :
			    messageReceived((MessageObject)event.getEventData());
				break;
			case MiddlewareEvent.SESSION_DISCONNECTED :
			    sessionDisconnected(event.toInfoString());
				break;
			case MiddlewareEvent.SESSION_CONNECTED :
			    sessionConnected();
			    break;
			case MiddlewareEvent.SESSION_CONNECT_FAILURE :
			case MiddlewareEvent.SESSION_CONNECT_TIMEOUT :
			    sessionConnectionFailed(event.toInfoString());
				break;
			}
        } catch (Exception e) {
            // Prevent exception from being thrown back to Appia
        	GUILoggerHome.find().exception("Uncaught exception in MiddlewareEventListener; ", e);
        }

    }

    /**
     * Notify waiting requester that a response was received
     * @param requestID a unique ID for the request
     * @param response a FIX response to the request
     * @return returns the request that was waiting on this response, or <tt>null</tt>
     * if no request was registered
     */

    public MessageObject responseReceived(String requestID, MessageObject response) {
        MessageObject request = responseSynchronizer.enterResponse(requestID, response);
        if (request == null) {
        	GUILoggerHome.find().information("No request waiting for response with ID: " + requestID,
        			GUILoggerBusinessProperty.COMMON);
        }
        return request;
    }

    /**
     * Notify waiting requester that a response was received, which may be
     * a partial result.
     * @param requestID unique ID of the request
     * @param response a FIX response message
     * @param expectedResponses number of expected responses
     * @param partialResponses are accumulated. If they add to expectedResponses,
     * then this is the last response message.
     * @return returns the request that was waiting on this response, or <tt>null</tt>
     * if no request was registered
     */
    public MessageObject responseReceived(String requestID, MessageObject response,
            int expectedResponses, int partialResponses) {
        MessageObject request = responseSynchronizer.enterResponse(requestID, response,
            expectedResponses, partialResponses);
    	if (request == null) {
        	GUILoggerHome.find().information("No request waiting for response with ID: " + requestID,
        			GUILoggerBusinessProperty.COMMON);
        }
    	return request;
    }

    /**
     * Send command to Appia to perform end of day operation for this connection
     * @return result of command
     * @throws SystemException if no Appia middleware connectin is available
     * @throws CommunicationException if the command cannot be sent
     */
    public String sendEODCommand() throws SystemException, CommunicationException {
    	return sendOperatorCommand("eod " + getConnection());
    }

    /**
     * Send a message to Appia
     *
     * @param message  must be derived from MessageObject
     * @throws CommunicationException if the request cannot be sent
     */
    public void sendMessage(MessageObject message)
            throws CommunicationException {
        message.setSessionID(getConnection());
        message.setClientMsgID(Integer.toString(++msgID));

        try {
            middleware.postMiddlewareObject(message);
        } catch (Exception e) {
            throw ExceptionBuilder.communicationException(getConnection()
                    + "; " + e.getMessage(), 0);
        }
    }

    /**
     * Wrapper for FIX engine command interface
     * TODO: return result string to GUI
     * @param command an Appia operator command
     * @return command result from Appia
     * @throws SystemException if no Appia middleware connectin is available
     * @throws CommunicationException if the command cannot be sent
     */
    public String sendOperatorCommand(String command) throws SystemException,
            CommunicationException {
        String retValue = "";
        if (middleware != null) {
            try {
                retValue = middleware.operatorCommand(command);
            } catch (Exception e) {
                throw ExceptionBuilder.communicationException(getConnection()
                        + "; " + e.getMessage(),
						CommunicationFailureCodes.LOST_CONNECTION);
            }
            GUILoggerHome.find().information("Response from Appia Operator Command=(" +
                    retValue + ")",
					GUILoggerBusinessProperty.COMMON);
        } else {
            throw ExceptionBuilder.systemException(
                            "Failed to send operator command to Appia; No middleware interface available.",
                            0);
        }
        return retValue;
    }

    /**
     * Send a request message and wait for its response. Uses default timeout period.
     * @param requestID a unique ID for the request
     * @param request the request message
     * @return the response messages
     * @throws CommunicationException if the request cannot be sent
     * @throws SystemException if a response is not received within the timeout period
     */
    public MessageObject[] sendRequest(String requestID, MessageObject request)
            throws CommunicationException, SystemException {
        try {
            responseSynchronizer.enterRequest(requestID, request);
            sendMessage(request);
            return responseSynchronizer.waitForResponse(requestID);
        } catch (InterruptedException e) {
            throw ExceptionBuilder.systemException(
                    "Timed out waiting for response", 0);
        }
    }

    /**
     * Send a request message and wait for its response
     * @param requestID a unique ID for the request
     * @param request the request message
     * @param timeout period to wait for the response in milliseconds
     * @return the response messages
     * @throws CommunicationException if the request cannot be sent
     * @throws SystemException if a response is not received within the timeout period
     */
    public MessageObject[] sendRequest(String requestID, MessageObject request,
            long timeout) throws CommunicationException, SystemException {
        try {
            responseSynchronizer.enterRequest(requestID, request);
            sendMessage(request);

            return responseSynchronizer.waitForResponse(requestID, timeout);
        } catch (InterruptedException e) {
            throw ExceptionBuilder.systemException("Timed out waiting for response", 0);
        }
    }

    /**
     * Send a request message and wait for its response
     * @param requestID a unique ID for the request
     * @param request the request message
     * @param timeout period to wait for the response in milliseconds
     * @return the response messages
     * @throws CommunicationException if the request cannot be sent
     * @throws SystemException if a response is not received within the timeout period
     */
    public MessageObject[] sendOrderListRequest(String requestID, MessageObject request,
            long timeout) throws CommunicationException, SystemException {
        try {
            responseSynchronizer.enterRequest(requestID, request);
            sendMessage(request);

            return responseSynchronizer.waitForResponse(requestID, timeout);
        } catch (InterruptedException e) {
            throw ExceptionBuilder.systemException("Timed out waiting for response", 0);
        }
    }

    /**
     * Send command to Appia to get statistics for this connection
     * @return result of command
     * @throws SystemException if no Appia middleware connectin is available
     * @throws CommunicationException if the command cannot be sent
     */
    public String sendStatsCommand() throws SystemException, CommunicationException {
    	return sendOperatorCommand("stats " + getConnection());
    }

    /**
     * Send a text message
     * @param messageText content of the message
     */
    public void sendTextMessage(String messageText) {
        MessageStruct messageStruct = new MessageStruct();
        messageStruct.messageText = messageText;
        getUserSessionAdminConsumer().acceptTextMessage(messageStruct);
    }
	/**
	 * @param appiaIniFile The appiaIniFile to set.
	 */
	public void setAppiaIniFile(String appiaIniFile) {
		this.appiaIniFile = appiaIniFile;
	}
    /**
     * @param connection The connection to set.
     */
    public void setConnection(String connection) {
        this.connection = connection;
    }
	/**
	 * @param dispatchers The dispatchers to set.
	 */
	public void setDispatchers(FixMessageDispatcherRegisty dispatchers) {
		this.dispatchers = dispatchers;
	}
	/**
	 * @param userSessionAdminConsumer The userSessionAdminConsumer to set.
	 */
	public void setUserSessionAdminConsumer(
            CMIUserSessionAdmin userSessionAdminConsumer) {
		this.userSessionAdminConsumer = userSessionAdminConsumer;
	}

    /**
     * Close the connection to the FIX engine
     */
    public void shutdown() {
        try {
            middleware.close();
            middleware = null;
        } catch (Exception e) {
        	GUILoggerHome.find().exception("Exception during FIX engine shutdown", e);
        }
    }

	/**
	 * Opens connection to FIX engine and registers as event listener
	 * @throws Exception is thrown if fails to start engine
	 */
    public void startup() throws Exception {
    	// Get global properties
    	setAppiaIniFile(System.getProperty(FIXINIFILE));
    	if (getAppiaIniFile() == null) {
    		throw ExceptionBuilder.communicationException(
					"Failed to connect to FIX engine; missing FIXINIFile property",
					CommunicationFailureCodes.TRANSPORT_FAILURE);
    	}
    	setConnection(System.getProperty(FIXCONNECTION));
    	if (getConnection() == null) {
    		throw ExceptionBuilder.communicationException(
					"Failed to connect to FIX engine; missing FIXConnection property",
					CommunicationFailureCodes.TRANSPORT_FAILURE);
    	}

		// Now we construct the appia argument list.
		String[] appiaArgs = new String[1];
		appiaArgs[0] = getAppiaIniFile();
		IGUILogger logger = GUILoggerHome.find();
		if (logger.isDebugOn()) {
			logger.debug(
					"About to call MiddlewareInterfaceFactory.getInProcMiddlewareInterface with "
					+ appiaArgs[0] + " and connection "
					+ getConnection(),
					GUILoggerBusinessProperty.USER_SESSION);
		}

        middleware = MiddlewareInterfaceFactory.getInProcMiddlewareInterface(
				appiaArgs, getConnection(), 0, MiddlewareInterface.AUTO_ACKNOWLEDGE);

        // Make sure that middleware handles received session messages
        MiddlewareConfig config = middleware.getMiddlewareConfig();
        if (logger.isDebugOn()) {
			logger.debug("MiddlewareConfig.SessionMessages = " +
					(config.SessionMessages ? "ON" : "OFF"),
					GUILoggerBusinessProperty.USER_SESSION);
        }
        config.SessionMessages = true;
        middleware.setMiddlewareConfig(config);

        // wait for Appia to initialize before proceeding
		if (logger.isDebugOn()) {
			logger.debug("About to call MiddlewareInterface.waitForServer",
					GUILoggerBusinessProperty.USER_SESSION);
		}
        middleware.waitForServer();

		// Add the listner, i.e, register the event listener (the callback object that
		// will receive execution reports).
		middleware.setMiddlewareEventListener(this);

		// Start middleware event delivery
		if (logger.isDebugOn()) {
			logger.debug("About to call MiddlewareInterface.start",
					GUILoggerBusinessProperty.USER_SESSION);
		}
		middleware.start();
    }

    /**
     * An application message was received
     * @param message the received message
     */
    protected void messageReceived(MessageObject message) {
		IGUILogger logger = GUILoggerHome.find();
        dispatchers.dispatch(message, this);
    }

	/**
     * Notify all waiting threads that FIX session is connected
     */
    protected void sessionConnected() {
    	isConnected = true;
    	GUILoggerHome.find().information("FIX session connected",
    			GUILoggerBusinessProperty.USER_SESSION);
    	synchronized (logonSynchronizer) {
    		logonSynchronizer.notifyAll();
    	}
    }

    /**
     * Notify all waiting threads that FIX connection failed
     * @param reason for failure
     */
    protected void sessionConnectionFailed(String reason) {
    	isConnected = false;
    	GUILoggerHome.find().alarm("FIX session failed to connect; " + reason,
    			GUILoggerBusinessProperty.USER_SESSION);
    	sessionReason = reason;
    	synchronized (logonSynchronizer) {
    		logonSynchronizer.notifyAll();
    	}
    }

    /**
     * Notify UserSessionAdminConsumer of broken session
     * @param reason for disconnection
     */
    protected void sessionDisconnected(String reason) {
    	isConnected = false;
    	GUILoggerHome.find().information("FIX session disconnected; " + reason,
    			GUILoggerBusinessProperty.USER_SESSION);
    	getUserSessionAdminConsumer().acceptLogout(reason);
    }

    /**
     *  Notify UserSessionAdminConsumer that user is logged out
     * @param reason for being logged out
     */
    protected void sessionLoggedOut(String reason) {
    	isConnected = false;
    	GUILoggerHome.find().information("FIX session logged out; " + reason,
    			GUILoggerBusinessProperty.USER_SESSION);
    	sessionReason = reason;
    	// In case a Logout was received as a negative response to a Logon, notify
    	// the thread calling the logon() method.
    	synchronized (logonSynchronizer) {
    		logonSynchronizer.notifyAll();
    	}
    	getUserSessionAdminConsumer().acceptLogout(reason);
    }

    public SessionInfo getSessionInfo() {
        try {
            return middleware.getSessionInfo(getConnection()) ;
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
        return null;
    }

    public SessionInfo getSessionInfo(String s) {
        try {
            return middleware.getSessionInfo(s) ;
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
        return null;
    }

}
