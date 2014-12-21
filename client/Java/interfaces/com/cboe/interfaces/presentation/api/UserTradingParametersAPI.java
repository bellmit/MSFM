package com.cboe.interfaces.presentation.api;

import com.cboe.exceptions.*;
import com.cboe.idl.cmiQuote.*;
import com.cboe.idl.cmiUtil.*;
import com.cboe.util.event.*;

/**
 * This interface represents the UserTradingParameters application API into the CAS,
 *
 * created on 2/15/2000
 *
 * @author Mike Pyatetsky
 * @version 2/15/2000
 */

public interface UserTradingParametersAPI
{
    /******************************************************************************************
     * The Following block of methods is Quote Risk Management (QRM) profile query methods
     */
    /**
     * Queries all User Quote Risk Management (QRM) Profile for this user including defaults and triggers
     *
     * @author Mike Pyatetsky
     *
     * @return UserQuoteRiskManagementProfileStruct
     *
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception NotFoundException
     */
     public UserQuoteRiskManagementProfileStruct getAllQuoteRiskProfiles()
        throws SystemException, CommunicationException, AuthorizationException, NotFoundException;

    /**
     * Queries all Quote Risk Management (QRM) Profile for given classKey
     *
     * @author Mike Pyatetsky
     *
     * @return QuoteRiskManagementProfileStruct
     *
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception NotFoundException
     */
     public QuoteRiskManagementProfileStruct getQuoteRiskManagementProfileByClass(int classKey)
        throws SystemException, CommunicationException, AuthorizationException, NotFoundException;



    /**
     * Sets QRM global on/off switch status for this user
     *
     * @author Mike Pyatetsky
     *
     * @param status Boolean to enable/disable QRM globally
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception TransactionFailedException
     */
     public void setQuoteRiskManagementEnabledStatus(boolean status)
        throws SystemException, CommunicationException, AuthorizationException, TransactionFailedException;

    /**
     * Gets QRM global on/off switch status for this user
     *
     * @author Mike Pyatetsky
     *
     * @return  Boolean to enable/disable QRM globally
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     */
     public boolean getQuoteRiskManagementEnabledStatus()
        throws SystemException, CommunicationException, AuthorizationException;

    /**
     * Gets defauld QRM profile for this user
     *
     * @author Mike Pyatetsky
     *
     * @return QuoteRiskManagementProfileStruct
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception NotFoundException
     */
     public QuoteRiskManagementProfileStruct getDefaultQuoteRiskProfile()
        throws SystemException, CommunicationException, AuthorizationException, NotFoundException;

    /**
     * Sets QRM profile for this user per class key passed in QuoteRiskManagementProfileStruct
     *
     * @author Mike Pyatetsky
     *
     * @param quoteRiskProfile Object of type QuoteRiskManagementProfileStruct
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception TransactionFailedException
     * @exception DataValidationException
     */
     public void setQuoteRiskProfile(QuoteRiskManagementProfileStruct quoteRiskProfile)
        throws SystemException, CommunicationException, AuthorizationException, TransactionFailedException, DataValidationException;

    /**
     * Removes QRM profile for this user for given class key
     *
     * @author Mike Pyatetsky
     *
     * @param classKey int class key
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception TransactionFailedException
     * @exception DataValidationException
     */
     public void removeQuoteRiskProfile(int classKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException;
    /**
     * Removes all quote risk profiles for this user
     *
     * @author Mike Pyatetsky
     *
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception TransactionFailedException
     */
     public void removeAllQuoteRiskProfiles()
        throws SystemException, CommunicationException, AuthorizationException, TransactionFailedException;

     /**********************  END of QRM profile handling methodes ***********************************************/

}
