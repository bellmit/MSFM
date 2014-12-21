package com.cboe.interfaces.cfix;

import com.cboe.exceptions.*;
import com.cboe.idl.cmiV4.UserSessionManagerV4;
import com.cboe.idl.cmiCallbackV4.CMICurrentMarketConsumer;
import com.cboe.idl.cmiCallbackV4.CMITickerConsumer;
import com.cboe.idl.cmiCallbackV4.CMIRecapConsumer;
import org.omg.PortableServer.Servant;

/**
 * Created by IntelliJ IDEA.
 * User: beniwalv
 * Date: Mar 21, 2011
 * Time: 2:40:41 PM
 * To change this template use File | Settings | File Templates.
 */
public interface CfixCasExternalLogin
{
    public void connectExternalUserToPrimaryCas()
            throws SystemException, CommunicationException, AuthorizationException, AuthenticationException, DataValidationException, NotFoundException;

    public void connectExternalUserToBackupCas()
            throws SystemException, CommunicationException, AuthorizationException, AuthenticationException, DataValidationException, NotFoundException;

    public UserSessionManagerV4 loginExternalUserToCas(String aCasIP, int aCasPort, String aCasUserID, String aCasPassword)
            throws SystemException, CommunicationException, AuthorizationException, AuthenticationException, DataValidationException, NotFoundException;

    public void switchExternalUserCAS();
    public void logoutExternalUserCas();
    public void associateExternalUserWithOrb(Servant servant);
    public boolean isExternalUserLoggedinToCas();

    public void subscribeExternalUserCurrentMarket(int i, CMICurrentMarketConsumer cmiCurrentMarketConsumer, short i1)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void unsubscribeExternalUserCurrentMarket(int i, CMICurrentMarketConsumer cmiCurrentMarketConsumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void subscribeExternalUserTicker(int i, CMITickerConsumer cmiTickerConsumer, short i1)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void unsubscribeExternalUserTicker(int i, CMITickerConsumer cmiTickerConsumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void subscribeExternalUserRecap(int i, CMIRecapConsumer cmiRecapConsumer, short i1)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void unsubscribeExternalUserRecap(int i, CMIRecapConsumer cmiRecapConsumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void subscribeExternalUserExpectedOpeningPrice(java.lang.String s, int i, com.cboe.idl.cmiCallback.CMIExpectedOpeningPriceConsumer cmiExpectedOpeningPriceConsumer)
            throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;
    public void unsubscribeExternalUserExpectedOpeningPrice(java.lang.String s, int i, com.cboe.idl.cmiCallback.CMIExpectedOpeningPriceConsumer cmiExpectedOpeningPriceConsumer)
            throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;
    public void subscribeExternalUserNBBOForClass(java.lang.String s, int i, com.cboe.idl.cmiCallback.CMINBBOConsumer cminbboConsumer)
            throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;
    public void subscribeExternalUserNBBOForProduct(java.lang.String s, int i, com.cboe.idl.cmiCallback.CMINBBOConsumer cminbboConsumer)
            throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;
    public void unsubscribeExternalUserNBBOForProduct(java.lang.String s, int i, com.cboe.idl.cmiCallback.CMINBBOConsumer cminbboConsumer)
            throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;
    public void unsubscribeExternalUserNBBOForClass(java.lang.String s, int i, com.cboe.idl.cmiCallback.CMINBBOConsumer cminbboConsumer)
            throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;
    public void subscribeExternalUserBookDepth(java.lang.String s, int i, com.cboe.idl.cmiCallback.CMIOrderBookConsumer cmiOrderBookConsumer)
            throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;
    public void unsubscribeExternalUserBookDepth(java.lang.String s, int i, com.cboe.idl.cmiCallback.CMIOrderBookConsumer cmiOrderBookConsumer) 
            throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

}
