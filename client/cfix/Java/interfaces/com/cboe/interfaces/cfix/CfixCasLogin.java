package com.cboe.interfaces.cfix;

import com.cboe.exceptions.AuthenticationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiCallbackV4.CMICurrentMarketConsumer;
import com.cboe.idl.cmiCallbackV4.CMITickerConsumer;
import com.cboe.idl.cmiCallbackV4.CMIRecapConsumer;
import com.cboe.idl.cmiV4.UserSessionManagerV4;
import org.omg.PortableServer.Servant;

/**
 * Created by IntelliJ IDEA.
 * User: lip
 * Date: May 5, 2010
 * Time: 4:30:20 PM
 * To change this template use File | Settings | File Templates.
 */
public interface CfixCasLogin{
    public void connectToPrimaryCas() throws SystemException,
                                      CommunicationException,
                                      AuthorizationException,
                                      AuthenticationException,
                                      DataValidationException,
                                      NotFoundException;

    public void connectToBackupCas() throws SystemException,
                                     CommunicationException,
                                     AuthorizationException,
                                     AuthenticationException,
                                     DataValidationException,
                                     NotFoundException;

    public UserSessionManagerV4 loginToCas(String aCasIP, int aCasPort,
                                           String aCasUserID, String aCasPassword)
                                                            throws SystemException,
                                                                   CommunicationException,
                                                                   AuthorizationException,
                                                                   AuthenticationException,
                                                                   DataValidationException, NotFoundException;
    public void switchCAS();
    public void logoutCas();
    public void associateWithOrb(Servant servant);
    public boolean isLoggedinToCas();

    public void subscribeCurrentMarket(int i,
                                       CMICurrentMarketConsumer cmiCurrentMarketConsumer,
                                       short i1)
                                       throws SystemException,
                                       CommunicationException,
                                       AuthorizationException,
                                       DataValidationException;

    public void unsubscribeCurrentMarket(int i, CMICurrentMarketConsumer cmiCurrentMarketConsumer) throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void subscribeTicker(int i, CMITickerConsumer cmiTickerConsumer, short i1) throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void unsubscribeTicker(int i, CMITickerConsumer cmiTickerConsumer) throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void subscribeRecap(int i, CMIRecapConsumer cmiRecapConsumer, short i1) throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void unsubscribeRecap(int i, CMIRecapConsumer cmiRecapConsumer) throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void subscribeExpectedOpeningPrice(java.lang.String s, int i, com.cboe.idl.cmiCallback.CMIExpectedOpeningPriceConsumer cmiExpectedOpeningPriceConsumer) throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;
    public void unsubscribeExpectedOpeningPrice(java.lang.String s, int i, com.cboe.idl.cmiCallback.CMIExpectedOpeningPriceConsumer cmiExpectedOpeningPriceConsumer) throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void subscribeNBBOForClass(java.lang.String s, int i, com.cboe.idl.cmiCallback.CMINBBOConsumer cminbboConsumer) throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;
    public void subscribeNBBOForProduct(java.lang.String s, int i, com.cboe.idl.cmiCallback.CMINBBOConsumer cminbboConsumer) throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;
    public void unsubscribeNBBOForProduct(java.lang.String s, int i, com.cboe.idl.cmiCallback.CMINBBOConsumer cminbboConsumer) throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;
    public void unsubscribeNBBOForClass(java.lang.String s, int i, com.cboe.idl.cmiCallback.CMINBBOConsumer cminbboConsumer) throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void subscribeBookDepth(java.lang.String s, int i, com.cboe.idl.cmiCallback.CMIOrderBookConsumer cmiOrderBookConsumer) throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;
    public void unsubscribeBookDepth(java.lang.String s, int i, com.cboe.idl.cmiCallback.CMIOrderBookConsumer cmiOrderBookConsumer) throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

}
