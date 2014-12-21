/*
 * Created on Sep 7, 2004
 *
 */
package com.cboe.presentation.fix.quote;

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

import com.cboe.domain.util.fixUtil.FixUtilConstants;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.idl.cmi.AMI_UserTradingParametersHandler;
import com.cboe.idl.cmi.UserTradingParameters;
import com.cboe.idl.cmiQuote.QuoteRiskManagementProfileStruct;
import com.cboe.idl.cmiQuote.UserQuoteRiskManagementProfileStruct;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.fix.appia.FixSessionImpl;
import com.cboe.util.ExceptionBuilder;
import com.javtech.appia.MessageObject;
import com.javtech.appia.QuoteAcknowledgement;
import com.javtech.appia.QuoteStatusRequest;

/**
 * @author Mendelso
 *
 */
public class UserTradingParametersImpl implements UserTradingParameters {

	/** Quote ID prefix for a QRM-only quote status request */
	public static final String QRM_PREFIX = "QR";
	
	/** 
	 * Default timeout for receiving an quote ack in millis
	 */
	public static final int DEFAULT_REQUEST_TIMEOUT = 60000;

	private int requestID = 0;
	private FixSessionImpl fixSession;

	/**
	 * Creates an instance of UserTradingParametersImpl
	 */
	public UserTradingParametersImpl() {
	}

	/**
	 * Set a reference to a FIX session
	 * @param fixSession a session for sending FIX messages
	 */
	public void setFixSession(FixSessionImpl fixSession) {
		this.fixSession = fixSession;
	}

	/**
	 * Assign a unique request ID
	 * @return a unique ID
	 */
	protected synchronized String assignRequestID() {
		StringBuffer sb = new StringBuffer(QRM_PREFIX);
		sb.append(++requestID);
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.UserTradingParametersOperations#getAllQuoteRiskProfiles()
	 */
	public UserQuoteRiskManagementProfileStruct getAllQuoteRiskProfiles()
			throws SystemException, CommunicationException,
			AuthorizationException, NotFoundException {
		QuoteStatusRequest qsr = new QuoteStatusRequest(fixSession.getDoNotSendValue());
		CmiQuoteRiskProfileToFixMapper.mapGetAllQuoteRiskProfiles(qsr);
		qsr.QuoteID = assignRequestID();
		// Send the request and wait for a response message
		String xmlResponse = enterQRMRequest(qsr);
		UserQuoteRiskManagementProfileStruct profile = 
			CmiQuoteRiskProfileToFixMapper.mapUserQuoteRiskManagementProfile(xmlResponse);
		return profile;
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.UserTradingParametersOperations#getDefaultQuoteRiskProfile()
	 */
	public QuoteRiskManagementProfileStruct getDefaultQuoteRiskProfile()
			throws SystemException, CommunicationException,
			AuthorizationException, NotFoundException {
		QuoteStatusRequest qsr = new QuoteStatusRequest(fixSession.getDoNotSendValue());
		CmiQuoteRiskProfileToFixMapper.mapGetDefaultQuoteRiskProfile(qsr);
		qsr.QuoteID = assignRequestID();
		// Send the request and wait for a response message
		String xmlResponse = enterQRMRequest(qsr);
		QuoteRiskManagementProfileStruct profile = 
			CmiQuoteRiskProfileToFixMapper.mapQuoteRiskManagementProfile(xmlResponse);
		return profile;
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.UserTradingParametersOperations#getQuoteRiskManagementEnabledStatus()
	 */
	public boolean getQuoteRiskManagementEnabledStatus()
			throws SystemException, CommunicationException,
			AuthorizationException {
		QuoteStatusRequest qsr = new QuoteStatusRequest(fixSession.getDoNotSendValue());
		CmiQuoteRiskProfileToFixMapper.mapQuoteRiskManagementEnabled(qsr);
		qsr.QuoteID = assignRequestID();
		// Send the request and wait for a response message
		String xmlResponse = enterQRMRequest(qsr);
		// Extract value of status from received XML response
		return CmiQuoteRiskProfileToFixMapper.getEnabledStatus(xmlResponse);
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.UserTradingParametersOperations#getQuoteRiskManagementProfileByClass(int)
	 */
	public QuoteRiskManagementProfileStruct getQuoteRiskManagementProfileByClass(
			int classKey) throws SystemException, CommunicationException,
			AuthorizationException, NotFoundException {
		QuoteStatusRequest qsr = new QuoteStatusRequest(fixSession.getDoNotSendValue());
		try {
			CmiQuoteRiskProfileToFixMapper.mapGetQuoteRiskProfile(classKey, qsr);
		} catch (DataValidationException e) {
			throw ExceptionBuilder.notFoundException(e.getMessage(), 0);
		}
		qsr.QuoteID = assignRequestID();
		// Send the request and wait for a response message
		String xmlResponse = enterQRMRequest(qsr);
		QuoteRiskManagementProfileStruct profile = 
			CmiQuoteRiskProfileToFixMapper.mapQuoteRiskManagementProfile(xmlResponse);
		return profile;
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.UserTradingParametersOperations#removeAllQuoteRiskProfiles()
	 */
	public void removeAllQuoteRiskProfiles() throws SystemException,
			CommunicationException, AuthorizationException,
			TransactionFailedException {
		QuoteStatusRequest qsr = new QuoteStatusRequest(fixSession.getDoNotSendValue());
		CmiQuoteRiskProfileToFixMapper.mapRemoveAllQuoteRiskProfiles(qsr);
		qsr.QuoteID = assignRequestID();
		enterQRMRequest(qsr);	
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.UserTradingParametersOperations#removeQuoteRiskProfile(int)
	 */
	public void removeQuoteRiskProfile(int classKey) throws SystemException,
			CommunicationException, AuthorizationException,
			DataValidationException, TransactionFailedException {
		QuoteStatusRequest qsr = new QuoteStatusRequest(fixSession.getDoNotSendValue());
		try {
			CmiQuoteRiskProfileToFixMapper.mapRemoveQuoteRiskProfile(classKey, qsr);
		} catch (NotFoundException e) {
			throw ExceptionBuilder.systemException("Product class not found: " +
					e.getMessage(), 0);
		}
		qsr.QuoteID = assignRequestID();
		enterQRMRequest(qsr);	
	}
	
	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.UserTradingParametersOperations#setQuoteRiskManagementEnabledStatus(boolean)
	 */
	public void setQuoteRiskManagementEnabledStatus(boolean status)
			throws SystemException, CommunicationException,
			AuthorizationException, TransactionFailedException {
		QuoteStatusRequest qsr = new QuoteStatusRequest(fixSession.getDoNotSendValue());
		CmiQuoteRiskProfileToFixMapper.mapQuoteRiskManagementEnabled(status, qsr);
		qsr.QuoteID = assignRequestID();
		enterQRMRequest(qsr);	
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.UserTradingParametersOperations#setQuoteRiskProfile(com.cboe.idl.cmiQuote.QuoteRiskManagementProfileStruct)
	 */
	public void setQuoteRiskProfile(QuoteRiskManagementProfileStruct quoteRiskProfile)
			throws SystemException, CommunicationException,
			AuthorizationException, TransactionFailedException,
			DataValidationException {
		QuoteStatusRequest qsr = new QuoteStatusRequest(fixSession.getDoNotSendValue());
		try {
			CmiQuoteRiskProfileToFixMapper.mapQuoteRiskProfile(quoteRiskProfile, qsr);
		} catch (NotFoundException e) {
			throw ExceptionBuilder.systemException("Product class not found: " +
					e.getMessage(), 0);
		}
		qsr.QuoteID = assignRequestID();
		enterQRMRequest(qsr);
	}

	/**
	 * Send a FIX quote status request and wait for acknowledgement
	 * @param qsr a FIX quote status request message
	 * @return XML result returned by QRM operation. May be null.
	 * @throws CommunicationException
	 * @throws SystemException
	 */
	private String enterQRMRequest(QuoteStatusRequest qsr)
			throws CommunicationException, SystemException {
		String result = null;
		// Send the FIX request and wait for acknowledgement. 
		// sendRequest() throws an exception if it times out or can't send request. 
		MessageObject responses[] = fixSession.sendRequest(qsr.QuoteID,
				qsr, DEFAULT_REQUEST_TIMEOUT);
		
		if (responses.length == 0) {
			throw ExceptionBuilder.systemException(
					"No response received for QRM request " + qsr.QuoteID, 0);
		} else for (int i=0; i < responses.length; i++ ) {
			if (responses[i] instanceof QuoteAcknowledgement) {
				QuoteAcknowledgement quoteAck = (QuoteAcknowledgement) responses[i];
				result = quoteAck.header.XmlData;
			} else {
				MessageObject msg = responses[i];
				GUILoggerHome.find().alarm("Unexpected response for QRM request " 
						+ qsr.QuoteID
						+ " of type " + msg.getMsgType(),
						GUILoggerBusinessProperty.QUOTE);
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_create_request(org.omg.CORBA.Context, java.lang.String, org.omg.CORBA.NVList, org.omg.CORBA.NamedValue)
	 */
	public Request _create_request(Context ctx, String operation,
			NVList arg_list, NamedValue result) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_create_request(org.omg.CORBA.Context, java.lang.String, org.omg.CORBA.NVList, org.omg.CORBA.NamedValue, org.omg.CORBA.ExceptionList, org.omg.CORBA.ContextList)
	 */
	public Request _create_request(Context ctx, String operation,
			NVList arg_list, NamedValue result, ExceptionList exclist,
			ContextList ctxlist) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_duplicate()
	 */
	public Object _duplicate() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_get_domain_managers()
	 */
	public DomainManager[] _get_domain_managers() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_get_interface_def()
	 */
	public Object _get_interface_def() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_get_policy(int)
	 */
	public Policy _get_policy(int policy_type) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_hash(int)
	 */
	public int _hash(int maximum) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_is_a(java.lang.String)
	 */
	public boolean _is_a(String repositoryIdentifier) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_is_equivalent(org.omg.CORBA.Object)
	 */
	public boolean _is_equivalent(Object other) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_non_existent()
	 */
	public boolean _non_existent() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_release()
	 */
	public void _release() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_request(java.lang.String)
	 */
	public Request _request(String operation) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_set_policy_override(org.omg.CORBA.Policy[], org.omg.CORBA.SetOverrideType)
	 */
	public Object _set_policy_override(Policy[] policies,
			SetOverrideType set_add) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.UserTradingParameters#sendc_getAllQuoteRiskProfiles(com.cboe.idl.cmi.AMI_UserTradingParametersHandler)
	 */
	public void sendc_getAllQuoteRiskProfiles(
			AMI_UserTradingParametersHandler arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.UserTradingParameters#sendc_getDefaultQuoteRiskProfile(com.cboe.idl.cmi.AMI_UserTradingParametersHandler)
	 */
	public void sendc_getDefaultQuoteRiskProfile(
			AMI_UserTradingParametersHandler arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.UserTradingParameters#sendc_getQuoteRiskManagementEnabledStatus(com.cboe.idl.cmi.AMI_UserTradingParametersHandler)
	 */
	public void sendc_getQuoteRiskManagementEnabledStatus(
			AMI_UserTradingParametersHandler arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.UserTradingParameters#sendc_getQuoteRiskManagementProfileByClass(com.cboe.idl.cmi.AMI_UserTradingParametersHandler, int)
	 */
	public void sendc_getQuoteRiskManagementProfileByClass(
			AMI_UserTradingParametersHandler arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.UserTradingParameters#sendc_removeAllQuoteRiskProfiles(com.cboe.idl.cmi.AMI_UserTradingParametersHandler)
	 */
	public void sendc_removeAllQuoteRiskProfiles(
			AMI_UserTradingParametersHandler arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.UserTradingParameters#sendc_removeQuoteRiskProfile(com.cboe.idl.cmi.AMI_UserTradingParametersHandler, int)
	 */
	public void sendc_removeQuoteRiskProfile(
			AMI_UserTradingParametersHandler arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.UserTradingParameters#sendc_setQuoteRiskManagementEnabledStatus(com.cboe.idl.cmi.AMI_UserTradingParametersHandler, boolean)
	 */
	public void sendc_setQuoteRiskManagementEnabledStatus(
			AMI_UserTradingParametersHandler arg0, boolean arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.UserTradingParameters#sendc_setQuoteRiskProfile(com.cboe.idl.cmi.AMI_UserTradingParametersHandler, com.cboe.idl.cmiQuote.QuoteRiskManagementProfileStruct)
	 */
	public void sendc_setQuoteRiskProfile(
			AMI_UserTradingParametersHandler arg0,
			QuoteRiskManagementProfileStruct arg1) {
		// TODO Auto-generated method stub

	}

}
