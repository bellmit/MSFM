/*
 * Created on Aug 4, 2004
 *
 */
package com.cboe.presentation.fix.session;

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

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmi.AMI_AdministratorHandler;
import com.cboe.idl.cmi.Administrator;
import com.cboe.idl.cmiAdmin.MessageStruct;
import com.cboe.presentation.fix.appia.EmailMapper;
import com.cboe.presentation.fix.appia.FixSessionImpl;
import com.javtech.appia.Email;

/**
 * Implementation of Administrator interface
 * @author Don Mendelson
 *
 */
public class AdministratorImpl implements Administrator {

	private FixSessionImpl fixSession;
	
	/**
	 * Creates an instance of AdministratorImpl
	 */
	public AdministratorImpl() {

	}
	
	/** 
	 * Set a reference to a FIX session
	 * @param fixSession a session for sending FIX messages
	 */
	public void setFixSession(FixSessionImpl fixSession) {
		this.fixSession = fixSession;
	}
	
	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.Administrator#sendc_sendMessage(com.cboe.idl.cmi.AMI_AdministratorHandler, com.cboe.idl.cmiAdmin.MessageStruct)
	 */
	public void sendc_sendMessage(AMI_AdministratorHandler arg0,
			MessageStruct arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmi.AdministratorOperations#sendMessage(com.cboe.idl.cmiAdmin.MessageStruct)
	 */
	public int sendMessage(MessageStruct messageStruct) throws SystemException,
			CommunicationException, AuthorizationException,
			DataValidationException {
		Email email = new Email(fixSession.getDoNotSendValue());
		EmailMapper.mapMessageToFix(messageStruct, email);
		fixSession.sendMessage(email);
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_release()
	 */
	public void _release() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_non_existent()
	 */
	public boolean _non_existent() {
		// TODO Auto-generated method stub
		return false;
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
	 * @see org.omg.CORBA.Object#_get_domain_managers()
	 */
	public DomainManager[] _get_domain_managers() {
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
	 * @see org.omg.CORBA.Object#_get_interface_def()
	 */
	public Object _get_interface_def() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_is_equivalent(org.omg.CORBA.Object)
	 */
	public boolean _is_equivalent(Object other) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.omg.CORBA.Object#_get_policy(int)
	 */
	public Policy _get_policy(int policy_type) {
		// TODO Auto-generated method stub
		return null;
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

}
