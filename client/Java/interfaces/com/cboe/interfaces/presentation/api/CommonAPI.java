package com.cboe.interfaces.presentation.api;

import com.cboe.exceptions.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiQuote.*;
import com.cboe.util.event.*;
import com.cboe.idl.cmiUser.*;
import com.cboe.idl.cmiAdmin.*;
import com.cboe.idl.cmiUtil.*;

import com.cboe.interfaces.presentation.user.UserStructModel;


/**
 * This interface represents the Common application API to the CAS & SACAS.
 *
 * @author Keith A. Korecky
 */
public interface CommonAPI
    extends AdministratorAPI
{
    /**
     * Register the event channel listener for the logoff events
     * @usage can be used to subscribe for log off events
     * @param clientListener the listener to subscribe
     * @returns none
     */
    public void registerForLogoff(EventChannelListener clientListener);

    /**
     * Register the event channel listener for the text message events
     * @usage can be used to subscribe for log off events
     * @param clientListener the listener to subscribe
     * @returns none
     */
    public void registerForTextMessage(EventChannelListener clientListener);

    /**
     * Logoff of the Application Server
     * @returns void
     */
    void logout()
         throws SystemException, CommunicationException, AuthorizationException;

    /**
     * Change the password for the current user
     * @param oldPassword - old password
     * @param newPassword - confirmation of the new password
     * @usage used to change the user's password
     * @returns void
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public void changePassword(String oldPassword, String newPassword)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Obtain the reference to the the Validated User
     * @return Validated User Information
     */
    public UserStructModel getValidUser()
            throws SystemException, CommunicationException, AuthorizationException;

    /**
     * Get the current CMI version information
     * @usage can be used to verify or report API version
     * @returns string containing the version of the CMI interface
     * @raises Nothing
     */
    public String getVersion()
           throws SystemException, CommunicationException, AuthorizationException;



}
