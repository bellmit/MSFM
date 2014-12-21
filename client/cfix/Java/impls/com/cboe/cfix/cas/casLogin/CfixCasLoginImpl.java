package com.cboe.cfix.cas.casLogin;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;

import com.cboe.exceptions.AuthenticationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiCallbackV4.CMICurrentMarketConsumer;
import com.cboe.idl.cmiCallbackV4.CMITickerConsumer;
import com.cboe.idl.cmiCallbackV4.CMIRecapConsumer;
import com.cboe.idl.cmiV6.UserSessionManagerV6;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.cfix.CfixCasLogin;
import com.cboe.util.ExceptionBuilder;


/**
 * Created by IntelliJ IDEA.
 * User: lip
 * Date: May 6, 2010
 * Time: 11:01:53 AM
 * To change this template use File | Settings | File Templates.
 */

public class CfixCasLoginImpl extends BObject implements CfixCasLogin {
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
    public static final String CFIX_CAS_USERID = "Cfix.CasUserID";
    public static final String CFIX_CAS_PASSWORD = "Cfix.CasPassword";
    public static final String CFIX_BACKUP_CAS_IP = "Cfix.BackupCasIP";
    public static final String CFIX_BACKUP_CAS_PORT = "Cfix.BackupCasPort";
    public static final String CFIX_BACKUP_CAS_USERID = "Cfix.BackupCasUserID";
    public static final String CFIX_BACKUP_CAS_PASSWORD = "Cfix.BackupCasPassword";
    public static final String CFIX_CAS_LOGIN_MODE = "Cfix.CasLoginMode";
    
    private boolean isMDXEnabled = false;

    private UserSessionManagerV6 theUserSessionManagerV6;
    private UserSessionManagerV6 thePrimaryUserSessionManagerV6;
    private UserSessionManagerV6 theBackupUserSessionManagerV6;

    public CfixCasLoginImpl()
    {
        init();
    }

    public void init(){
        try{
            if(Log.isDebugOn()){
                Log.debug(this, "Start init CfixCasLoginImpl.");
            }

            String retnVal = System.getProperty(CFIX_SUB_MODE, "false");
            this.isMDXEnabled = (retnVal.equals("MDX"));

            if(!isMDXEnabled){
                if(Log.isDebugOn()){
                    Log.debug(this, "Not in MDX mode, stop init CfixCasLoginImpl.");
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

            connectToPrimaryCas();
            connectToBackupCas();

            theUserSessionManagerV6 = thePrimaryUserSessionManagerV6;
            if(theUserSessionManagerV6 ==null)
                theUserSessionManagerV6 = theBackupUserSessionManagerV6;
            if(theUserSessionManagerV6 ==null)
                Log.alarm("Can not logon to CAS.");
            else
                Log.information("Logon to CAS successfully.");
        }catch(org.omg.PortableServer.POAManagerPackage.AdapterInactive e){
            Log.exception(e);
        }catch(InvalidName e){
            Log.exception(e);
        }catch(Exception e){
            Log.exception(e);
        }
    }

    public void switchCAS(){
        if(theUserSessionManagerV6 == thePrimaryUserSessionManagerV6)
            theUserSessionManagerV6 = theBackupUserSessionManagerV6;
        //CfixCasLoginSubscriptionTest.TestSubscription(theRootPOA);
    }

    public void connectToPrimaryCas()
                    throws SystemException,
                    CommunicationException,
                    AuthorizationException,
                    AuthenticationException,
                    DataValidationException
    {
        thePrimaryUserSessionManagerV6 = loginToCas(theCasIp, theCasPort, theCasUserId, theCasPassword);
    }

    public void connectToBackupCas()
                    throws SystemException,
                    CommunicationException,
                    AuthorizationException,
                    AuthenticationException,
                    DataValidationException
    {
        theBackupUserSessionManagerV6 = loginToCas(theBackupCasIp, theBackupCasPort, theBackupCasUserId, theBackupCasPassword);
    }

    public boolean isLoggedinToCas(){
        return (theUserSessionManagerV6 !=null);
    }
    public UserSessionManagerV6 loginToCas(String aCasIP,
                                           int aCasPort,
                                           String aCasUserID,
                                           String aCasPassword)  throws SystemException,
                                                                        CommunicationException,
                                                                        AuthorizationException,
                                                                        AuthenticationException,
                                                                        DataValidationException
    {
        if(Log.isDebugOn()){
            Log.debug(this, "calling CfixCasLoginImpl::loginToCas");
        }

        com.cboe.idl.cmiUser.UserLogonStruct userLogonStruct = new com.cboe.idl.cmiUser.UserLogonStruct();

        CMIVersion version = new CMIVersion();

        UserSessionAdminCallback myUserSessionAdminCallback = null;
        myUserSessionAdminCallback = new UserSessionAdminCallback();
        myUserSessionAdminCallback.setVerbose(true);

        userLogonStruct.userId = aCasUserID;
        userLogonStruct.password = aCasPassword;
        userLogonStruct.version = version.getVersionNumber();
        userLogonStruct.loginMode = theLoginMode;//com.cboe.idl.cmiConstants.LoginSessionModes.NETWORK_TEST;

        myUserSessionAdminCallback.setCASAccessManager(theCasAccessMgr);
        myUserSessionAdminCallback.setCfixCasLogin(this);
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
            Log.exception("System Exception on Cas Login. Exiting!!", e);
            System.exit(1);
        }
        catch (com.cboe.exceptions.CommunicationException e) {
            //
            // Problem talking to the CAS http server or
            // problem communicating with the ORB
            //
            Log.exception("Communication Exception on Cas Login. Exiting!!", e);
            System.exit(2);
        }
        catch (com.cboe.exceptions.AuthorizationException e) {
            //
            // User is not authorized for the Role specified
            //
            Log.exception("Authorization Exception on Cas Login. Exiting!!", e);
            System.exit(3);
        }
        catch (com.cboe.exceptions.AuthenticationException e) {
            //
            // UserName or Password is incorrect
            //
            // In an interactive application - the user would be
            // re-prompted to enter userName and Password.
            //
            Log.exception("Authentication Exception on Cas Login. Exiting!!", e);
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
            Log.exception("Data Validation Exception on Cas Login. Exiting!!", e);
            System.exit(5);
        }catch(org.omg.CORBA.NO_PERMISSION e){
            Log.exception("CORBA.NO_PERMISSION Exception on Cas Login. Exiting!!", e);
            System.exit(6);
        }
        catch(Exception e){
            Log.exception("Exception on Cas Login. Exiting!!", e);
            System.exit(7);
        };
        return null;
    }

    public void logoutCas()
    {
        theUserSessionManagerV6 = null;
      //theCasAccessMgr.logoff();
    }

    /** After creating a CORBA Servant object, associate it with an ORB.
     * @param servant The CORBA Servant object.
     */
    public void associateWithOrb(Servant servant)
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


    public void subscribeCurrentMarket(int i,
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

    public void unsubscribeCurrentMarket(int i, CMICurrentMarketConsumer cmiCurrentMarketConsumer) throws SystemException, CommunicationException, AuthorizationException, DataValidationException {
        if(theUserSessionManagerV6 !=null)
            theUserSessionManagerV6.getMarketQueryV4().unsubscribeCurrentMarket(i, cmiCurrentMarketConsumer);
        else
            throw new SystemException();
    }

    public void subscribeTicker(int i, CMITickerConsumer cmiTickerConsumer, short i1) throws SystemException, CommunicationException, AuthorizationException, DataValidationException {
        if(theUserSessionManagerV6 !=null)
            theUserSessionManagerV6.getMarketQueryV4().subscribeTicker(i, cmiTickerConsumer, i1);
        else
            throw new SystemException();

    }

    public void unsubscribeTicker(int i, CMITickerConsumer cmiTickerConsumer) throws SystemException, CommunicationException, AuthorizationException, DataValidationException {
        if(theUserSessionManagerV6 !=null)
            theUserSessionManagerV6.getMarketQueryV4().unsubscribeTicker(i, cmiTickerConsumer);
        else
            throw new SystemException();

    }

    public void subscribeRecap(int i, CMIRecapConsumer cmiRecapConsumer, short i1) throws SystemException, CommunicationException, AuthorizationException, DataValidationException {
        if(theUserSessionManagerV6 !=null)
            theUserSessionManagerV6.getMarketQueryV4().subscribeRecap(i, cmiRecapConsumer, i1);
        else
            throw new SystemException();

    }

    public void unsubscribeRecap(int i, CMIRecapConsumer cmiRecapConsumer) throws SystemException, CommunicationException, AuthorizationException, DataValidationException {
        if(theUserSessionManagerV6 !=null)
            theUserSessionManagerV6.getMarketQueryV4().unsubscribeRecap(i, cmiRecapConsumer);
        else
            throw new SystemException();
    }

    public void subscribeExpectedOpeningPrice(java.lang.String s, int i, com.cboe.idl.cmiCallback.CMIExpectedOpeningPriceConsumer cmiExpectedOpeningPriceConsumer) throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if(theUserSessionManagerV6 !=null)
            theUserSessionManagerV6.getMarketQueryV2().subscribeExpectedOpeningPrice(s, i, cmiExpectedOpeningPriceConsumer);
        else
            throw new SystemException();
    }
    public void unsubscribeExpectedOpeningPrice(java.lang.String s, int i, com.cboe.idl.cmiCallback.CMIExpectedOpeningPriceConsumer cmiExpectedOpeningPriceConsumer) throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if(theUserSessionManagerV6 !=null)
            theUserSessionManagerV6.getMarketQueryV2().unsubscribeExpectedOpeningPrice(s, i, cmiExpectedOpeningPriceConsumer);
        else
            throw new SystemException();
    }

    public void subscribeNBBOForClass(java.lang.String s, int i, com.cboe.idl.cmiCallback.CMINBBOConsumer cminbboConsumer) throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if(theUserSessionManagerV6 !=null)
            theUserSessionManagerV6.getMarketQuery().subscribeNBBOForClass(s, i, cminbboConsumer);
        else
            throw new SystemException();
    }


    public void subscribeNBBOForProduct(java.lang.String s, int i, com.cboe.idl.cmiCallback.CMINBBOConsumer cminbboConsumer) throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if(theUserSessionManagerV6 !=null)
            theUserSessionManagerV6.getMarketQuery().subscribeNBBOForProduct(s, i, cminbboConsumer);
        else
            throw new SystemException();
    }
    public void unsubscribeNBBOForProduct(java.lang.String s, int i, com.cboe.idl.cmiCallback.CMINBBOConsumer cminbboConsumer) throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if(theUserSessionManagerV6 !=null)
            theUserSessionManagerV6.getMarketQuery().unsubscribeNBBOForProduct(s, i, cminbboConsumer);
        else
            throw new SystemException();
    }
    public void unsubscribeNBBOForClass(java.lang.String s, int i, com.cboe.idl.cmiCallback.CMINBBOConsumer cminbboConsumer) throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        if(theUserSessionManagerV6 !=null)
            theUserSessionManagerV6.getMarketQuery().unsubscribeNBBOForClass(s, i, cminbboConsumer);
        else
            throw new SystemException();
    }

    public void subscribeBookDepth(java.lang.String s, int i, com.cboe.idl.cmiCallback.CMIOrderBookConsumer cmiOrderBookConsumer) throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        throw ExceptionBuilder.systemException("Unsupported API subscribeBookDepth.", 0);
    }

    public void unsubscribeBookDepth(java.lang.String s, int i, com.cboe.idl.cmiCallback.CMIOrderBookConsumer cmiOrderBookConsumer) throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
    {
        throw ExceptionBuilder.systemException("Unsupported API unsubscribeBookDepth.", 0);
    }
}