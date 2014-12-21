/*
 * Created on Jul 14, 2004
 *
 */
package com.cboe.presentation.fix.session;

import com.cboe.exceptions.AuthenticationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiCallback.CMIUserSessionAdmin;
import com.cboe.idl.cmiUser.UserLogonStruct;
import com.cboe.idl.cmiV3.UserSessionManagerV3;
import com.cboe.idl.cmiV3.UserAccessV3;
import com.cboe.idl.cmiV3.AMI_UserAccessV3Handler;
import org.omg.CORBA.*;
import org.omg.CORBA.Object;

/**
 * A source of new user sessions that communicate using FIX protocol
 * @author Don Mendelson
 *
 */
public class UserAccessFixImpl implements UserAccessV3{

	/**
	 * Creates a new UserAccessFixImpl
	 */
	public UserAccessFixImpl() {
	}

	/* (non-Javadoc)
	 * @see com.cboe.idl.cmiV2.UserAccessV2Operations#logon(com.cboe.idl.cmiUser.UserLogonStruct, short, com.cboe.idl.cmiCallback.CMIUserSessionAdmin, boolean)
	 */
	public UserSessionManagerV3 logon(UserLogonStruct userLogonStruct, short sessionType,
			CMIUserSessionAdmin userSessionAdmin, boolean gmdTextMessaging) throws SystemException,
			CommunicationException, AuthorizationException,
			AuthenticationException, DataValidationException, NotFoundException {
		UserSessionManagerV3 sessionManager = null;
		//UserSessionManagerFixHome home = (UserSessionManagerFixHome) HomeFactory.getInstance().findHome(UserSessionManagerFixHome.HOME_NAME);
        UserSessionManagerFixHome home = new UserSessionManagerFixHome();
	    sessionManager = home.createSession(userLogonStruct, sessionType, userSessionAdmin);
		sessionManager.authenticate(userLogonStruct);


		return sessionManager;
	}

    public void sendc_logon(AMI_UserAccessV3Handler ami_userAccessV3Handler, UserLogonStruct userLogonStruct, short i, CMIUserSessionAdmin cmiUserSessionAdmin, boolean b) {
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


}
