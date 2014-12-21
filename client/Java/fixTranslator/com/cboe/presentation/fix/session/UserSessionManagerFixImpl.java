/*
 * Created on Jul 14, 2004
 *
 */
package com.cboe.presentation.fix.session;

import java.util.Date;

import org.omg.CORBA.Context;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.DomainManager;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.Object;
import org.omg.CORBA.Policy;
import org.omg.CORBA.Request;
import org.omg.CORBA.SetOverrideType;

import com.cboe.domain.util.fixUtil.FixUtilDateTimeHelper;
import com.cboe.exceptions.AuthenticationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmi.AMI_UserSessionManagerHandler;
import com.cboe.idl.cmi.Administrator;
import com.cboe.idl.cmi.OrderEntry;
import com.cboe.idl.cmi.ProductDefinition;
import com.cboe.idl.cmi.ProductQuery;
import com.cboe.idl.cmi.TradingSession;
import com.cboe.idl.cmi.UserHistory;
import com.cboe.idl.cmi.UserPreferenceQuery;
import com.cboe.idl.cmi.UserTradingParameters;
import com.cboe.idl.cmiCallback.CMIUserSessionAdmin;
import com.cboe.idl.cmiConstants.SessionNameValues;
import com.cboe.idl.cmiUser.ProfileStruct;
import com.cboe.idl.cmiUser.SessionProfileStruct;
import com.cboe.idl.cmiUser.SessionProfileUserStruct;
import com.cboe.idl.cmiUser.UserLogonStruct;
import com.cboe.idl.cmiUser.UserStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.cmiV2.AMI_UserSessionManagerV2Handler;
import com.cboe.idl.cmiV2.MarketQuery;
import com.cboe.idl.cmiV2.OrderQuery;
import com.cboe.idl.cmiV2.Quote;
import com.cboe.idl.cmiV3.AMI_UserSessionManagerV3Handler;

import com.cboe.interfaces.presentation.common.logging.IGUILogger;
import com.cboe.interfaces.presentation.userSession.FixUserSessionManager;
import com.cboe.interfaces.application.OrderEntryV3;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.fix.appia.FixSessionImpl;
import com.cboe.presentation.fix.order.OrderEntryImpl;
import com.cboe.presentation.fix.order.OrderQueryImpl;
import com.cboe.presentation.fix.quote.QuoteImpl;
import com.cboe.presentation.fix.quote.UserTradingParametersImpl;

import com.javtech.appia.middleware.SessionInfo;

/**
 * A user session that communicates using FIX protocol
 * @author Don Mendelson
 *
 */
public class UserSessionManagerFixImpl implements FixUserSessionManager{

	// Session in FIX engine
	private FixSessionImpl sessionImpl = new FixSessionImpl();

	private short sessionType;
	private CMIUserSessionAdmin userSessionAdmin;

	// CMi API implementation classes
	private OrderEntryImpl orderEntry = new OrderEntryImpl();
	private OrderQueryImpl orderQuery = new OrderQueryImpl();
	private AdministratorImpl administrator = new AdministratorImpl();
	private QuoteImpl quoteEntry = new QuoteImpl();
	private UserTradingParametersImpl qrm = new UserTradingParametersImpl();

	/**
	 * Creates a new UserSessionManagerFixImpl
	 */
	public UserSessionManagerFixImpl() {
		orderEntry.setFixSession(sessionImpl);
		orderQuery.setFixSession(sessionImpl);
		administrator.setFixSession(sessionImpl);
		quoteEntry.setFixSession(sessionImpl);
		qrm.setFixSession(sessionImpl);
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.UserSessionManagerOperations#authenticate(com.cboe.idl.cmiUser.UserLogonStruct)
	 */
	public void authenticate(UserLogonStruct userLogonStruct) throws SystemException, CommunicationException, AuthenticationException, AuthorizationException, DataValidationException {
		sessionImpl.logon(userLogonStruct);
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.UserSessionManagerOperations#changePassword(java.lang.String, java.lang.String)
	 */
	public void changePassword(String arg0, String arg1) throws SystemException, CommunicationException, AuthorizationException, DataValidationException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.UserSessionManagerOperations#getAdministrator()
	 */
	public Administrator getAdministrator() throws SystemException, CommunicationException, AuthorizationException {
		return administrator;
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.UserSessionManagerOperations#getMarketQuery()
	 */
	public com.cboe.idl.cmi.MarketQuery getMarketQuery() throws SystemException, CommunicationException, AuthorizationException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmiV2.UserSessionManagerV2Operations#getMarketQueryV2()
	 */
	public MarketQuery getMarketQueryV2() throws SystemException,
			CommunicationException, AuthorizationException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.UserSessionManagerOperations#getOrderEntry()
	 */
	public OrderEntry getOrderEntry() throws SystemException, CommunicationException, AuthorizationException {
		return orderEntry;
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.UserSessionManagerOperations#getOrderQuery()
	 */
	public com.cboe.idl.cmi.OrderQuery getOrderQuery() throws SystemException, CommunicationException, AuthorizationException {
		return orderQuery;
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmiV2.UserSessionManagerV2Operations#getOrderQueryV2()
	 */
	public OrderQuery getOrderQueryV2() throws SystemException,
			CommunicationException, AuthorizationException {
		return orderQuery;
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.UserSessionManagerOperations#getProductDefinition()
	 */
	public ProductDefinition getProductDefinition() throws SystemException, CommunicationException, AuthorizationException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.UserSessionManagerOperations#getProductQuery()
	 */
	public ProductQuery getProductQuery() throws SystemException, CommunicationException, AuthorizationException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.UserSessionManagerOperations#getQuote()
	 */
	public com.cboe.idl.cmi.Quote getQuote() throws SystemException, CommunicationException, AuthorizationException {
		return quoteEntry;
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmiV2.UserSessionManagerV2Operations#getQuoteV2()
	 */
	public Quote getQuoteV2() throws SystemException, CommunicationException,
			AuthorizationException {
		return quoteEntry;
	}
	/**
	 * @return Returns the sessionType.
	 */
	public short getSessionType() {
		return sessionType;
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.UserSessionManagerOperations#getSystemDateTime()
	 */
	public DateTimeStruct getSystemDateTime() throws SystemException, CommunicationException, AuthorizationException {
		return FixUtilDateTimeHelper.makeDateTimeStruct(new Date());
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.UserSessionManagerOperations#getTradingSession()
	 */
	public TradingSession getTradingSession() throws SystemException, CommunicationException, AuthorizationException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.UserSessionManagerOperations#getUserHistory()
	 */
	public UserHistory getUserHistory() throws SystemException, CommunicationException, AuthorizationException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.UserSessionManagerOperations#getUserPreferenceQuery()
	 */
	public UserPreferenceQuery getUserPreferenceQuery() throws SystemException, CommunicationException, AuthorizationException {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * @return Returns the userSessionAdmin.
	 */
	public CMIUserSessionAdmin getUserSessionAdmin() {
		return userSessionAdmin;
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.UserSessionManagerOperations#getUserTradingParameters()
	 */
	public UserTradingParameters getUserTradingParameters() throws SystemException, CommunicationException, AuthorizationException {
		return qrm;
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.UserSessionManagerOperations#getValidSessionProfileUser()
	 */
	public SessionProfileUserStruct getValidSessionProfileUser()
			throws SystemException, CommunicationException,
			AuthorizationException {
		SessionProfileUserStruct userStruct = toSessionProfileUserStruct( getValidUser() );
		
		IGUILogger logger = GUILoggerHome.find();
		if (logger.isDebugOn()) {
			logger.debug("UserSessionManagerFixImpl.getValidSessionProfileUser() - userId: " + userStruct.userId +
					" Default account: " + userStruct.defaultProfile.account + 
					" ExecutingGiveup exchange: " + userStruct.defaultProfile.executingGiveupFirm.exchange +
					" firm: " + userStruct.defaultProfile.executingGiveupFirm.firmNumber,
					GUILoggerBusinessProperty.USER_SESSION);
		}

		return userStruct;
	}
    
	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.UserSessionManagerOperations#getValidUser()
	 */
	public UserStruct getValidUser() throws SystemException,
			CommunicationException, AuthorizationException {
		UserStruct validUser = sessionImpl.getValidUser();
		return validUser;
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.UserSessionManagerOperations#getVersion()
	 */
	public String getVersion() throws SystemException, CommunicationException, AuthorizationException {
		return "FIX";
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.UserSessionManagerOperations#logout()
	 */
	public void logout() throws SystemException, CommunicationException, AuthorizationException {
		sessionImpl.logout();
	}

	/**
	 * @param sessionType The sessionType to set.
	 */
	public void setSessionType(short sessionType) {
		this.sessionType = sessionType;
	}
	/**
	 * @param userSessionAdmin The userSessionAdmin to set.
	 */
	public void setUserSessionAdmin(CMIUserSessionAdmin userSessionAdmin) {
		this.userSessionAdmin = userSessionAdmin;
        sessionImpl.setUserSessionAdminConsumer(userSessionAdmin);
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmiV3.UserSessionManagerV3Operations#getMarketQueryV3()
	 */
	public com.cboe.idl.cmiV3.MarketQuery getMarketQueryV3() throws SystemException, CommunicationException, AuthorizationException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmiV3.UserSessionManagerV3Operations#getQuoteV3()
	 */
	public com.cboe.idl.cmiV3.Quote getQuoteV3() throws SystemException, CommunicationException, AuthorizationException {
		return quoteEntry;
	}

    public com.cboe.idl.cmiV3.OrderQuery getOrderQueryV3() throws SystemException, CommunicationException, AuthorizationException
    {
        return null;
    }

    public com.cboe.idl.cmiV3.OrderEntry getOrderEntryV3() throws SystemException, CommunicationException, AuthorizationException
    {
        return orderEntry;
    }

    /**
	 * @return Returns the FIX session implementation
	 */
	public FixSessionImpl getFixSession() {
		return sessionImpl;
	}
	
	// *** From SessionProfileHelper ***
	//     to remove dependency on server IDL
    private SessionProfileUserStruct toSessionProfileUserStruct(UserStruct us)
    {
        SessionProfileUserStruct result = new SessionProfileUserStruct();
        result.userAcronym = us.userAcronym;
        result.userId = us.userId;
        result.firm = us.firm;
        result.fullName = us.fullName;
        result.role = us.role;
        result.executingGiveupFirms = us.executingGiveupFirms;
        result.defaultProfile = toSessionProfileStruct(us.defaultProfile);
        result.accounts = us.accounts;
        result.assignedClasses = us.assignedClasses;
        result.dpms = us.dpms;
        result.sessionProfilesByClass= toSessionProfileSturcts(us.profilesByClass);
        result.defaultSessionProfiles = new SessionProfileStruct[0]; // blank field.
        return result;
    }
    private SessionProfileStruct toSessionProfileStruct(ProfileStruct ps)
    {
        SessionProfileStruct result = new SessionProfileStruct();
        result.account = ps.account;
        result.classKey = ps.classKey;
        result.executingGiveupFirm = ps.executingGiveupFirm;
        result.subAccount = ps.subAccount;
        result.sessionName = SessionNameValues.ALL_SESSION_NAME;  // by default
        result.isAccountBlanked = false;  // by default
        result.originCode = ' ';
        return result;
    }
    private SessionProfileStruct[] toSessionProfileSturcts(ProfileStruct[] ps)
    {
        SessionProfileStruct[] result = new SessionProfileStruct[ps.length];
        for (int i = 0; i < ps.length; i++)
        {
            result[i] = toSessionProfileStruct(ps[i]);
        }
        return result;
    }
    //  *********************************
    
    public void sendc_authenticate(AMI_UserSessionManagerHandler ami_userSessionManagerHandler, UserLogonStruct userLogonStruct) {
    }

    public void sendc_changePassword(AMI_UserSessionManagerHandler ami_userSessionManagerHandler, String s, String s1) {
    }

    public void sendc_getAdministrator(AMI_UserSessionManagerHandler ami_userSessionManagerHandler) {
    }

    public void sendc_getMarketQuery(AMI_UserSessionManagerHandler ami_userSessionManagerHandler) {
    }

    public void sendc_getOrderEntry(AMI_UserSessionManagerHandler ami_userSessionManagerHandler) {
    }

    public void sendc_getOrderQuery(AMI_UserSessionManagerHandler ami_userSessionManagerHandler) {
    }

    public void sendc_getProductDefinition(AMI_UserSessionManagerHandler ami_userSessionManagerHandler) {
    }

    public void sendc_getProductQuery(AMI_UserSessionManagerHandler ami_userSessionManagerHandler) {
    }

    public void sendc_getQuote(AMI_UserSessionManagerHandler ami_userSessionManagerHandler) {
    }

    public void sendc_getSystemDateTime(AMI_UserSessionManagerHandler ami_userSessionManagerHandler) {
    }

    public void sendc_getTradingSession(AMI_UserSessionManagerHandler ami_userSessionManagerHandler) {
    }

    public void sendc_getUserHistory(AMI_UserSessionManagerHandler ami_userSessionManagerHandler) {
    }

    public void sendc_getUserPreferenceQuery(AMI_UserSessionManagerHandler ami_userSessionManagerHandler) {
    }

    public void sendc_getUserTradingParameters(AMI_UserSessionManagerHandler ami_userSessionManagerHandler) {
    }

    public void sendc_getValidSessionProfileUser(AMI_UserSessionManagerHandler ami_userSessionManagerHandler) {
    }

    public void sendc_getValidUser(AMI_UserSessionManagerHandler ami_userSessionManagerHandler) {
    }

    public void sendc_getVersion(AMI_UserSessionManagerHandler ami_userSessionManagerHandler) {
    }

    public void sendc_logout(AMI_UserSessionManagerHandler ami_userSessionManagerHandler) {
    }

    public void sendc_getMarketQueryV3(AMI_UserSessionManagerV3Handler ami_userSessionManagerV3Handler) {
    }

    public void sendc_getQuoteV3(AMI_UserSessionManagerV3Handler ami_userSessionManagerV3Handler) {
    }

    public void sendc_getOrderQueryV3(AMI_UserSessionManagerV3Handler ami_userSessionManagerV3Handler)
    {
        
    }

    public void sendc_getOrderEntryV3(AMI_UserSessionManagerV3Handler ami_userSessionManagerV3Handler)
    {
        
    }

    public void sendc_getMarketQueryV2(AMI_UserSessionManagerV2Handler ami_userSessionManagerV2Handler) {
    }

    public void sendc_getOrderQueryV2(AMI_UserSessionManagerV2Handler ami_userSessionManagerV2Handler) {
    }

    public void sendc_getQuoteV2(AMI_UserSessionManagerV2Handler ami_userSessionManagerV2Handler) {
    }

    public boolean _is_a(String repositoryIdentifier) {
        return false;
    }

    public boolean _is_equivalent(Object other) {
        return false;
    }

    public boolean _non_existent() {
        return false;
    }

    public int _hash(int maximum) {
        return 0;
    }

    public Object _duplicate() {
        return null;
    }

    public void _release() {
    }

    public Object _get_interface_def() {
        return null;
    }

    public Request _request(String operation) {
        return null;
    }

    public Request _create_request(Context ctx,
                                   String operation,
                                   NVList arg_list,
                                   NamedValue result) {
        return null;
    }

    public Request _create_request(Context ctx,
                                   String operation,
                                   NVList arg_list,
                                   NamedValue result,
                                   ExceptionList exclist,
                                   ContextList ctxlist) {
        return null;
    }

    public Policy _get_policy(int policy_type) {
        return null;
    }

    public DomainManager[] _get_domain_managers() {
        return new DomainManager[0];
    }

    public Object _set_policy_override(Policy[] policies,
                                       SetOverrideType set_add) {
        return null;
    }
    public SessionInfo getSessionInfo() {
        return sessionImpl.getSessionInfo();
    }

    public SessionInfo getSessionInfo(String s) {
        return sessionImpl.getSessionInfo(s);
    }

    public String getSessionId() {
        return sessionImpl.getSessionInfo().getSessionID();
    }
    public String getRemoteFirmID() {
        return sessionImpl.getSessionInfo().getRemoteFirmID();
    }

    public String getNetAddresses() {
        return sessionImpl.getSessionInfo().getNetAddresses()[0];
    }

    public int getPort() {
        return sessionImpl.getSessionInfo().getPort();
    }

    public int getInMsgSeqNum() {
        return sessionImpl.getSessionInfo().getInMsgSeqNum();
    }

    public int getOutMsgSeqNum() {
        return sessionImpl.getSessionInfo().getOutMsgSeqNum();
    }

    public int getConnectState() {
        return sessionImpl.getSessionInfo().getConnectState();
    }

}