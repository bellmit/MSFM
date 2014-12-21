package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiCallback.CMIUserSessionAdminPOA;
import com.cboe.idl.cmiAdmin.HeartBeatStruct;
import com.cboe.idl.cmiAdmin.MessageStruct;
import com.cboe.idl.cmiUtil.CallbackInformationStruct;

/** Provide logging and basic actions for CMi callback UserSessionAdmin. */
public class UserSessionAdmin extends CMIUserSessionAdminPOA
{
    private CasAccess casAccess;

    public UserSessionAdmin(CasAccess casAccess)
    {
        this.casAccess = casAccess;
    }

    /** Respond to CAS heartbeat so CAS knows we're still here.
     * @param heartbeat Unique message from CAS.
     * @return Message that the CAS sent to us.
     */
    public HeartBeatStruct acceptHeartBeat(HeartBeatStruct heartbeat)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("UserSessionAdmin.acceptHeartBeat ")
           .append(Struct.toString(heartbeat));
        Log.message(sb);

        return heartbeat;
    }

    /** CAS tells us we're logging out or being logged out.
     * @param reason Description of reason for logout.
     */
    public void acceptLogout(String reason)
    {
        Log.message("UserSessionAdmin.acceptLogout reason:" + reason);
    }

    /** Display an arbitrary text message sent by the CAS.
     * @param message Message to display.
     */
    public void acceptTextMessage(MessageStruct message)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("UserSessionAdmin.acceptTextMessage ")
          .append(Struct.toString(message));
        Log.message(sb);
    }

    /** Re-authenticate our login. */
    public void acceptAuthenticationNotice()
    {
        Log.message("UserSessionAdmin.acceptAuthenticationNotice");
        casAccess.reauthenticate();
    }

    /** Report that CAS as removed a callback
     * @param callbackInfo Information about the removed callback.
     * @param reason Explanation for removal.
     * @param errorCode Numerical error code.
     */
    public void acceptCallbackRemoval(
        CallbackInformationStruct callbackInfo,
        String reason,
        int errorCode)
    {
        Log.message("UserSessionAdmin.acceptCallbackRemoval errorCode:"
                + errorCode
                + " reason:" + reason
                + " " + Struct.toString(callbackInfo));
    }
}