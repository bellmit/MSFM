package com.cboe.cfix.cas.casLogin;

import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.cfix.CfixCasLogin;
import com.cboe.interfaces.cfix.CfixCasExternalLogin;
import com.cboe.idl.cmiV6.UserSessionManagerV6;
import com.cboe.idl.cmiCallbackV4.CMICurrentMarketConsumer;
import com.cboe.idl.cmiCallbackV4.CMITickerConsumer;
import com.cboe.idl.cmiCallbackV4.CMIRecapConsumer;
import com.cboe.exceptions.*;
import com.cboe.util.ExceptionBuilder;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

/**
 * Created by IntelliJ IDEA.
 * User: beniwalv
 * Date: Mar 21, 2011
 * Time: 4:32:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class CfixCasExternalLoginImpl extends BObject implements CfixCasExternalLogin
{
    static ORB theOrb;
    public POA theRootPOA;
    CASAccessManager theCasAccessMgr;

	private String theCasIp;
    private int theCasPort;
    private String theCasUserId;
    private String theCasPassword;

    private String theBackupCasIp;
    private int theBackupCasPort;
    private String theBackupCasUserId;
    private String theBackupCasPassword;

    private char theLoginMode;

    public static final String CFIX_SUB_MODE = "cfixSubscriptionMode";
    public static final String CFIX_CAS_IP = "Cfix.CasIP";
    public static final String CFIX_CAS_PORT = "Cfix.CasPort";
    public static final String CFIX_CAS_USERID = "Cfix.CasExternalUserID";
    public static final String CFIX_CAS_PASSWORD = "Cfix.CasExternalPassword";
    public static final String CFIX_BACKUP_CAS_IP = "Cfix.BackupCasIP";
    public static final String CFIX_BACKUP_CAS_PORT = "Cfix.BackupCasPort";
    public static final String CFIX_BACKUP_CAS_USERID = "Cfix.BackupCasExternalUserID";
    public static final String CFIX_BACKUP_CAS_PASSWORD = "Cfix.BackupCasExternalPassword";
    public static final String CFIX_CAS_LOGIN_MODE = "Cfix.CasLoginMode";

    private boolean isMDXEnabled = false;

    private UserSessionManagerV6 theUserSessionManagerV6;
    private UserSessionManagerV6 thePrimaryUserSessionManagerV6;
    private UserSessionManagerV6 theBackupUserSessionManagerV6;

    public CfixCasExternalLoginImpl()
    {
        init();
    }

    public void init(){
        try{
            if(Log.isDebugOn()){
                Log.debug(this, "Start init CfixCasExternalLoginImpl.");
            }

            String retnVal = System.getProperty(CFIX_SUB_MODE, "false");
            this.isMDXEnabled = (retnVal.equals("MDX"));

            if(!isMDXEnabled){
                if(Log.isDebugOn()){
                    Log.debug(this, "Not in MDX mode, stop init CfixCasExternalLoginImpl.");
                }
                return;
            }

            theCasIp = System.getProperty(CFIX_CAS_IP);
            theCasPort = Integer.parseInt(System.getProperty(CFIX_CAS_PORT));
            theCasUserId = System.getProperty(CFIX_CAS_USERID);
            theCasPassword = System.getProperty(CFIX_CAS_PASSWORD);

            theBackupCasIp = System.getProperty(CFIX_BACKUP_CAS_IP);
            theBackupCasPort = Integer.parseInt(System.getProperty(CFIX_BACKUP_CAS_PORT));
            theBackupCasUserId = System.getProperty(CFIX_BACKUP_CAS_USERID);
            theBackupCasPassword = System.getProperty(CFIX_BACKUP_CAS_PASSWORD);

            theLoginMode = System.getProperty(CFIX_CAS_LOGIN_MODE).toCharArray()[0];

            theOrb = com.cboe.ORBInfra.ORB.Orb.init();
            theRootPOA = POAHelper.narrow(theOrb.resolve_initial_references("RootPOA"));
            theRootPOA.the_POAManager().activate();
            theCasAccessMgr = new CASAccessManager(theOrb);

            connectExternalUserToPrimaryCas();
            connectExternalUserToBackupCas();

            theUserSessionManagerV6 = thePrimaryUserSessionManagerV6;
            if(theUserSessionManagerV6 ==null)
                theUserSessionManagerV6 = theBackupUserSessionManagerV6;
            if(theUserSessionManagerV6 ==null)
                Log.alarm("Can not logon External User to CAS.");
            else
                Log.information("External User Logon to CAS successfully.");
        }catch(org.omg.PortableServer.POAManagerPackage.AdapterInactive e){
            Log.exception(e);
        }catch(InvalidName e){
            Log.exception(e);
        }catch(Exception e){
            Log.exception(e);
        }
    }

    public void switchExternalUserCAS(){
        if(theUserSessionManagerV6 == thePrimaryUserSessionManagerV6)
            theUserSessionManagerV6 = theBackupUserSessionManagerV6;
        //CfixCasLoginSubscriptionTest.TestSubscription(theRootPOA);
    }

    public void connectExternalUserToPrimaryCas()
                    throws SystemException,
            CommunicationException,
            AuthorizationException,
            AuthenticationException,
            DataValidationException
    {
        thePrimaryUserSessionManagerV6 = loginExternalUserToCas(theCasIp, theCasPort, theCasUserId, theCasPassword);
    }

    public void connectExternalUserToBackupCas()
                    throws SystemException,
                    CommunicationException,
                    AuthorizationException,
                    AuthenticationException,
                    DataValidationException
    {
        theBackupUserSessionManagerV6 = loginExternalUserToCas(theBackupCasIp, theBackupCasPort, theBackupCasUserId, theBackupCasPassword);
    }

    public boolean isExternalUserLoggedinToCas(){
        return (theUserSessionManagerV6 !=null);
    }
    public UserSessionManagerV6 loginExternalUserToCas(String aCasIP,
                                           int aCasPort,
                                           String aCasUserID,
                                           String aCasPassword)  throws SystemException,
                                                                        CommunicationException,
                                                                        AuthorizationException,
                                                                        AuthenticationException,
                                                                        DataValidationException
    {
        if(Log.isDebugOn()){
            Log.debug(this, "calling CfixCasExternalLoginImpl::loginToCas");
        }

        com.cboe.idl.cmiUser.UserLogonStruct userLogonStruct = new com.cboe.idl.cmiUser.UserLogonStruct();

        CMIVersion version = new CMIVersion();

        ExternalUserSessionAdminCallback myUserSessionAdminCallback = null;
        myUserSessionAdminCallback = new ExternalUserSessionAdminCallback();
        myUserSessionAdminCallback.setVerbose(true);

        userLogonStruct.userId = aCasUserID;
        userLogonStruct.password = aCasPassword;
        userLogonStruct.version = version.getVersionNumber();
        userLogonStruct.loginMode = theLoginMode;//com.cboe.idl.cmiConstants.LoginSessionModes.NETWORK_TEST;

        myUserSessionAdminCallback.setCASAccessManager(theCasAccessMgr);
        myUserSessionAdminCallback.setCfixCasExternalLogin(this);
        myUserSessionAdminCallback.setUserLogonStruct(userLogonStruct);
        try {
            theRootPOA.activate_object(myUserSessionAdminCallback);
        } catch (Exception e) {
                Log.exception(e);
                return null;
        }
        try {
            theCasAccessMgr.logonV6(userLogonStruct, myUserSessionAdminCallback._this(), aCasIP, aCasPort);
            return theCasAccessMgr.getUserSessionManagerV6();
            //return theCasAccessMgr.getUserSessionManagerV6();
        }
        catch (com.cboe.exceptions.SystemException e) {
            Log.exception("System Exception on Cas External Login. Exiting!!", e);
            System.exit(1);
        }
        catch (com.cboe.exceptions.CommunicationException e) {
            //
            // Problem talking to the CAS http server or
            // problem communicating with the ORB
            //
            Log.exception("Communication Exception on Cas External Login. Exiting!!", e);
            System.exit(2);
        }
        catch (com.cboe.exceptions.AuthorizationException e) {
            //
            // User is not authorized for the Role specified
            //
            Log.exception("Authorization Exception on Cas External Login. Exiting!!", e);
            System.exit(3);
        }
        catch (com.cboe.exceptions.AuthenticationException e) {
            //
            // UserName or Password is incorrect
            //
            // In an interactive application - the user would be
            // re-prompted to enter userName and Password.
            //
            Log.exception("Authentication Exception on Cas External Login. Exiting!!", e);
            System.exit(4);
        }
        catch (com.cboe.exceptions.DataValidationException e) {
            //
            // Possible problems:
            // 1) Incorrect Version of the IDL was encountered
            // The client program may be running against the wrong
            // CAS or the wrong version of the CMi IDL was used to
            // build the client application.
            //
            // 2) Invalid Role Specified.
            // In an interactive application the user would be prompted
            // to specify a different role. Developers should use the
            // cmiConstants.
            //
            Log.exception("Data Validation Exception on Cas External Login. Exiting!!", e);
            System.exit(5);
        }catch(org.omg.CORBA.NO_PERMISSION e){
            Log.exception("CORBA.NO_PERMISSION Exception on Cas External Login. Exiting!!", e);
            System.exit(6);
        }
        catch(Exception e){
            Log.exception("Exception on Cas External Login. Exiting!!", e);
            System.exit(7);
        };
        return null;
    }

    public void logoutExternalUserCas()
    {
        theUserSessionManagerV6 = null;
      //theCasAccessMgr.logoff();
    }

    /** After creating a CORBA Servant object, associate it with an ORB.
     * @param servant The CORBA Servant object.
     */
    public void associateExternalUserWithOrb(Servant servant)
    {
        try
        {
            theRootPOA.activate_object(servant);
        }
        catch (ServantAlreadyActive saa)
        {
            Log.exception(saa);
        }
        catch (WrongPolicy wp)
        {
            Log.exception(wp);
        }
    }


    public void subscribeExternalUserCurrentMarket(int i,
                                       CMICurrentMarketConsumer cmiCurrentMarketConsumer,
                                       short i1)
                                       throws SystemException,
                                       CommunicationException,
                                       AuthorizationException,
                                       DataValidationException
    {
//      try{
        if(theUserSessionManagerV6 !=null)
            theUserSessionManagerV6.getMarketQueryV4().subscribeCurrentMarket(i, cmiCurrentMarketConsumer, i1);
        else
            throw new SystemException();
//      }catch(Throwable t){
//            t.printStackTrace();
//      }
     }

    public void unsubscribeExternalUserCurrentMarket(int i, CMICurrentMarketConsumer cmiCurrentMarketConsumer) throws SystemException, CommunicationException, AuthorizationException, DataValidationException {
        if(theUserSessionManagerV6 !=null)
            theUserSessionManagerV6.getMarketQueryV4().unsubscribeCurrentMarket(i, cmiCurrentMarketConsumer);
        else
            throw new SystemException();
    }

    public void subscribeExternalUserTicker(int i, CMITickerConsumer cmiTickerConsumer, short i1) throws SystemException, CommunicationException, AuthorizationException, DataValidationException {
        if(theUserSessionManagerV6 !=null)
            theUserSessionManagerV6.getMarketQueryV4().subscribeTicker(i, cmiTickerConsumer, i1);
        else
            throw new SystemException();

    }

    public void unsubscribeExternalUserTicker(int i, CMITickerConsumer cmiTickerConsumer) throws SystemException, CommunicationException, AuthorizationException, DataValidationException {
        if(theUserSessionManagerV6 !=null)
            theUserSessionManagerV6.getMarketQueryV4().unsubscribeTicker(i, cmiTickerConsumer);
        else
            throw new SystemException();

    }

    public void subscribeExternalUserRecap(int i, CMIRecapConsumer cmiRecapConsumer, short i1) throws SystemException, CommunicationException, AuthorizationException, DataValidationException {
        if(theUserSessionManagerV6 !=null)
            theUserSessionManagerV6.getMarketQueryV4().subscribeRecap(i, cmiRecapConsumer, i1);
        else
            throw new SystemException();

    }

    public void unsubscribeExternalUserRecap(int i, CMIRecapConsumer cmiRecapConsumer) throws SystemException, CommunicationException, AuthorizationException, DataValidationException {
        if(theUserSessionManagerV6 !=null)
            theUserSessionManagerV6.getMarketQueryV4().unsubscribeRecap(i, cmiRecapConsumer);
        else
            throw new SystemException();
    }

    public void subscribeExternalUserExpectedOpeningPrice(java.lang.String s, int i, com.cboe.idl.cmiCallback.CMIExpectedOpeningPriceConsumer cmiExpectedOpeningPriceConsumer) throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if(theUserSessionManagerV6 !=null)
            theUserSessionManagerV6.getMarketQueryV2().subscribeExpectedOpeningPrice(s, i, cmiExpectedOpeningPriceConsumer);
        else
            throw new SystemException();
    }
    public void unsubscribeExternalUserExpectedOpeningPrice(java.lang.String s, int i, com.cboe.idl.cmiCallback.CMIExpectedOpeningPriceConsumer cmiExpectedOpeningPriceConsumer) throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if(theUserSessionManagerV6 !=null)
            theUserSessionManagerV6.getMarketQueryV2().unsubscribeExpectedOpeningPrice(s, i, cmiExpectedOpeningPriceConsumer);
        else
            throw new SystemException();
    }

    public void subscribeExternalUserNBBOForClass(java.lang.String s, int i, com.cboe.idl.cmiCallback.CMINBBOConsumer cminbboConsumer) throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if(theUserSessionManagerV6 !=null)
            theUserSessionManagerV6.getMarketQuery().subscribeNBBOForClass(s, i, cminbboConsumer);
        else
            throw new SystemException();
    }


    public void subscribeExternalUserNBBOForProduct(java.lang.String s, int i, com.cboe.idl.cmiCallback.CMINBBOConsumer cminbboConsumer) throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if(theUserSessionManagerV6 !=null)
            theUserSessionManagerV6.getMarketQuery().subscribeNBBOForProduct(s, i, cminbboConsumer);
        else
            throw new SystemException();
    }
    public void unsubscribeExternalUserNBBOForProduct(java.lang.String s, int i, com.cboe.idl.cmiCallback.CMINBBOConsumer cminbboConsumer) throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if(theUserSessionManagerV6 !=null)
            theUserSessionManagerV6.getMarketQuery().unsubscribeNBBOForProduct(s, i, cminbboConsumer);
        else
            throw new SystemException();
    }
    public void unsubscribeExternalUserNBBOForClass(java.lang.String s, int i, com.cboe.idl.cmiCallback.CMINBBOConsumer cminbboConsumer) throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if(theUserSessionManagerV6 !=null)
            theUserSessionManagerV6.getMarketQuery().unsubscribeNBBOForClass(s, i, cminbboConsumer);
        else
            throw new SystemException();
    }

    public void subscribeExternalUserBookDepth(java.lang.String s, int i, com.cboe.idl.cmiCallback.CMIOrderBookConsumer cmiOrderBookConsumer) throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        throw ExceptionBuilder.systemException("Unsupported API subscribeBookDepth.", 0);
    }

    public void unsubscribeExternalUserBookDepth(java.lang.String s, int i, com.cboe.idl.cmiCallback.CMIOrderBookConsumer cmiOrderBookConsumer) throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        throw ExceptionBuilder.systemException("Unsupported API unsubscribeBookDepth.", 0);
    }
}
