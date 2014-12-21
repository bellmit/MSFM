package com.cboe.cfix.cas.casLogin;

import com.cboe.interfaces.cfix.CfixCasLogin;
import com.cboe.interfaces.cfix.CfixCasExternalLogin;
import com.cboe.idl.cmiAdmin.HeartBeatStruct;
import com.cboe.idl.cmiAdmin.MessageStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by IntelliJ IDEA.
 * User: beniwalv
 * Date: Mar 22, 2011
 * Time: 9:17:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class ExternalUserSessionAdminCallback extends com.cboe.idl.cmiCallback.CMIUserSessionAdminPOA
{
    CASAccessManager theCasAccessMgr;
    CfixCasExternalLogin theCfixCasExternalLogin;
    com.cboe.idl.cmiUser.UserLogonStruct theUserLogonStruct;

    boolean verbose=false;

    Timer theHeartBeatCheckTimer = null;
    boolean hbFlag = false;

    int theMissedHBCounter = 0;

    private final int MAX_MISSED_HEARTBEAT = Integer.parseInt(System.getProperty("CFix.MAX_MISSED_HEARTBEAT"));
    private final int CAS_HEARTBEAT_TIMER_INTERVAL = Integer.parseInt(System.getProperty("CFix.CAS_HEARTBEAT_TIMER_INTERVAL"));

    public ExternalUserSessionAdminCallback() {
        super();
    }
   /**
    * Part of the CMIUserSessionAdmin interface. This implementation
    * will acknowledge the heartbeat and also write a message to System.out
    * if the verbose flag is set to true.
    *
    * @param heartbeat struct from CAS
    */
    public HeartBeatStruct acceptHeartBeat(HeartBeatStruct heartbeat){
        hbFlag = true;
        if(theHeartBeatCheckTimer ==null)
        {
            TimerTask myCheckHeartBeat  = new checkHeartBeat();
            theHeartBeatCheckTimer = new Timer();
            theHeartBeatCheckTimer.schedule(myCheckHeartBeat, 0, CAS_HEARTBEAT_TIMER_INTERVAL);
        }
        //if (getVerbose()&&Log.isDebugOn())
        //{
        //  Log.debug("Just received heartbeat from CAS...");
        //}
        return heartbeat;
    }
   /**
    * Part of the CMIUserSessionAdmin interface.
    *
    * @param reason Reason CAS logged the client off.
    */
    public void acceptLogout (String reason ) {
        if (getVerbose()&& Log.isDebugOn()){
            Log.debug("External User Logged out of the CAS for the following reason: "+reason);
        }
    }
  /**
   * Part of the CMIUserSessionAdmin interface. This implementation
   * will print to System.out the text message received from the CAS if the
   * verbose flag is turned on.
   *
   * Note that this example DOES NOT automatically respond to the message.
   * If the CAS sets the Message.ReplyRequested and there is no acknowledgement
   * from the client - the client will be forcibly logged off.
   *
   * @param message message sent from CAS
   */
   //public void acceptTextMessage(String destination , MessageStruct msg ){
   public void acceptTextMessage(MessageStruct message ){
      if (getVerbose()&&Log.isDebugOn()){
          StringBuffer outMsg = new StringBuffer();
          outMsg.append("Message from CAS:\n");
          outMsg.append("Message ID(");
          outMsg.append(message.messageKey);
          outMsg.append(")\nTimeSent(");
          outMsg.append(")\nSender(");
          outMsg.append(message.sender);
          outMsg.append(")\nReplyRequested(");
          outMsg.append(message.replyRequested);
          outMsg.append(")\nMessageText(");
          outMsg.append(message.messageText);
          outMsg.append(")");
          Log.debug(outMsg.toString());
      }
   }

   /**
    * When an accept authentication notice is received - you must
    * re-authenticate your application. It is strongly, strongly
    * recommended that this be done via a pop up window to the user
    * asking them to re-login for interactive applications.
    * For automated trading or "black box" systems - you can
    * reauthenticate within this callback operation.
    */
   public void acceptAuthenticationNotice() {

     if (theCasAccessMgr != null && theUserLogonStruct != null  ) {

       try {
           theCasAccessMgr.getUserSessionManager().authenticate(theUserLogonStruct);
       }
       catch (com.cboe.exceptions.SystemException e) {
           Log.exception(e);
       }
       catch (com.cboe.exceptions.CommunicationException e) {
           Log.exception(e);       }
       catch (com.cboe.exceptions.AuthorizationException e) {
           Log.exception(e);       }
       catch (com.cboe.exceptions.AuthenticationException e) {
           Log.exception(e);       }
       catch (com.cboe.exceptions.DataValidationException e) {
           Log.exception(e);
       };
     }
   }
  /**
   * The CAS invokes this operation when a client Callback operation
   * is no longer responding or is behaving improperly. The CAS
   * de-registers (unsubscribes) the callback operation and reports
   * the error to the client - via this callback.
   *
   * @param callbackInfo  struct that contains the callback information
   * @param reason  reason callback was de-registered
   * @param errorCode  error code from the CAS
   *
   */
  public void acceptCallbackRemoval(com.cboe.idl.cmiUtil.CallbackInformationStruct callbackInfo, java.lang.String reason, int errorCode)
  {
    // This is a very serious error - most likely an application problem
    // has occurred within your application.
    StringBuffer report = new StringBuffer();
    report.append("\n*** Received callback removal notification ***");
    report.append("\n  Reason: ").append(reason);
    report.append("\n  Error code: ").append(errorCode);
    report.append("\n  Callback interface: ").append(callbackInfo.subscriptionInterface);
    report.append("\n  Callback operation: ").append(callbackInfo.subscriptionOperation);
    report.append("\n  Additional value: ").append(callbackInfo.subscriptionValue);
    report.append("\n  Callback IOR: ").append(callbackInfo.ior);
    Log.alarm(report.toString());
    this.switchExternalUserCAS();
  }
  /**
   * This setter is not part of the CMIUserSessionAdmin interface.
   * It was added to allow an application to set the
   * UserLogonStruct reference in this call back object.
   * The UserLogonStruct is used to re-authenticate the client application
   * to the CAS when acceptAuthenticationNotice() is invoked by the CAS.
   */
   public void setUserLogonStruct(com.cboe.idl.cmiUser.UserLogonStruct userLogonStruct ){
      this.theUserLogonStruct = userLogonStruct;
   }

  /**
   * This setter is not part of the CMIUserSessionAdmin interface.
   * It was added to allow an application to set a CASAccessManager
   * reference in this call back object so that
   * the UserSessionManager can be called to re-authorize this client
   * when the CAS invokes the acceptAuthenticationNotice() on this object.
   */
   public void setCASAccessManager(CASAccessManager casAccessMgr) {
      this.theCasAccessMgr = casAccessMgr;
   }


   public void setCfixCasExternalLogin(CfixCasExternalLogin aCfixCasLogin){
      this.theCfixCasExternalLogin = aCfixCasLogin;
   }
  /**
   * This setter is not part of the CMIUserSessionAdmin interface.
   * It was added to allow an application to indicate if the client
   * wants output written to System.out when the callbacks are invoked.
   */
   public void setVerbose(boolean verbose){
      this.verbose = verbose;
   }

  /**
   * This setter is not part of the CMIUserSessionAdmin interface.
   * It was added to allow an application to return the current
   * value of the verbose flag.
   */
   public boolean getVerbose(){
      return this.verbose;
   }

    private void switchExternalUserCAS(){
        theCfixCasExternalLogin.switchExternalUserCAS();
    }

    public final class checkHeartBeat extends TimerTask {
      public void run(){
        if (hbFlag){ //received heart beat
            hbFlag = false;
            theMissedHBCounter = 0;
        }else{ // not receving heart beat
             theMissedHBCounter++;
             if (theMissedHBCounter>=MAX_MISSED_HEARTBEAT){
                 //missing too many heartbeat, CAS is considered as down.
                 theHeartBeatCheckTimer.cancel();
                 theHeartBeatCheckTimer = null;
                 theMissedHBCounter = 0;
                 switchExternalUserCAS();
             }
        }
      }
    }



}
