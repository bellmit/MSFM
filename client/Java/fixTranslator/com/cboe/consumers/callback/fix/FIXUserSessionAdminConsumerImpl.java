package com.cboe.consumers.callback.fix;

import com.cboe.idl.cmiAdmin.*;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.*;
import com.cboe.idl.cmiUtil.*;
import com.cboe.idl.cmiCallback.CMIUserSessionAdmin;
import com.cboe.idl.cmiCallback.AMI_CMIUserSessionAdminHandler;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.consumers.callback.UserSessionAdminConsumerImpl;
import org.omg.CORBA.*;
import org.omg.CORBA.Object;

/**
 * This is the implementation of the CMIUserSessionAdmin callback object which
 * receives data from the FIX session and publishes it on a designated event channel.
 * The only callcack method it handles differently than the 
 * UserSessionAdminConsumerImpl is acceptLogout; the other methods just publish the 
 * event on the eventChannel with the same ChannelType as UserSessionAdminConsumerImpl.
 */
public class FIXUserSessionAdminConsumerImpl extends UserSessionAdminConsumerImpl implements CMIUserSessionAdmin
{

    /**
     * FIXUserSessionAdminConsumerImpl constructor.
     *
     * @param eventChannel the event channel to publish to.
     */
    public FIXUserSessionAdminConsumerImpl(EventChannelAdapter eventChannel)
    {
        super(eventChannel);
    }

    /**
     * The callback method used by the FIX Session to publish a forced logout.
     *
     * @param reason for logout
     */
    public void acceptLogout( String reason )
    {
        ChannelKey key = new ChannelKey(ChannelType.CB_LOGOUT, new Integer(0));
        ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, key, reason);
        eventChannel.dispatch(event);

        com.cboe.presentation.fix.api.FIXMarketMakerAPIFactory.shutdown();
    }

    // null impls of all CORBA methods.  The FIX User Session is in the same process, and doesn't use CORBA.
    public void sendc_acceptAuthenticationNotice(AMI_CMIUserSessionAdminHandler ami_cmiUserSessionAdminHandler)
    {
    }

    public void sendc_acceptCallbackRemoval(AMI_CMIUserSessionAdminHandler ami_cmiUserSessionAdminHandler, CallbackInformationStruct callbackInformationStruct, String s, int i)
    {
    }

    public void sendc_acceptHeartBeat(AMI_CMIUserSessionAdminHandler ami_cmiUserSessionAdminHandler, HeartBeatStruct heartBeatStruct)
    {
    }

    public void sendc_acceptLogout(AMI_CMIUserSessionAdminHandler ami_cmiUserSessionAdminHandler, String s)
    {
    }

    public void sendc_acceptTextMessage(AMI_CMIUserSessionAdminHandler ami_cmiUserSessionAdminHandler, MessageStruct messageStruct)
    {
    }

    public boolean _is_a(String repositoryIdentifier)
    {
        return false;
    }

    public boolean _is_equivalent(Object other)
    {
        return false;
    }

    public boolean _non_existent()
    {
        return false;
    }

    public int _hash(int maximum)
    {
        return 0;
    }

    public Object _duplicate()
    {
        return null;
    }

    public void _release()
    {
    }

    public Object _get_interface_def()
    {
        return null;
    }

    public Request _request(String operation)
    {
        return null;
    }

    public Request _create_request(Context ctx,
                                   String operation,
                                   NVList arg_list,
                                   NamedValue result)
    {
        return null;
    }

    public Request _create_request(Context ctx,
                                   String operation,
                                   NVList arg_list,
                                   NamedValue result,
                                   ExceptionList exclist,
                                   ContextList ctxlist)
    {
        return null;
    }

    public Policy _get_policy(int policy_type)
    {
        return null;
    }

    public DomainManager[] _get_domain_managers()
    {
        return new DomainManager[0];
    }

    public Object _set_policy_override(Policy[] policies,
                                       SetOverrideType set_add)
    {
        return null;
    }

}
