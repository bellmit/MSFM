package com.cboe.presentation.qrm;

//------------------------------------------------------------------------------------------------------------------
// FILE:    GUIUserTradingParametersAPI.java
//
// PACKAGE: com.cboe.presentation.common
//
//-------------------------------------------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
//
//-------------------------------------------------------------------------------------------------------------------


// Imports
// java packages

// local packages
import com.cboe.exceptions.*;
import com.cboe.idl.cmiQuote.*;
import com.cboe.idl.cmiUtil.*;
import com.cboe.util.event.*;

/**
 *  This interface represents the UserTradingParameters application abstraction for use in
 *  the TraderGUI and SAGUIAPI applications.
 *
 *
 *  @author Alex Brazhnichenko
 *  Creation date <timestamp>
 *  @version 03/08/2000
 */
public interface GUIUserTradingParametersAPI
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
     * @exception DataValidationException
     */
     public UserQuoteRiskManagementProfileStruct getAllQuoteRiskProfiles(String userId)
        throws SystemException, CommunicationException, DataValidationException, AuthorizationException, NotFoundException;
    /**
     * Gets defauld QRM profile for this user
     *
     * @author Mike Pyatetsky
     *
     * @return QuoteRiskManagementProfileStruct
     * @exception SystemException
     * @exception CommunicationException
     * @exception DataValidationException
     * @exception AuthorizationException
     * @exception NotFoundException
     */
     public QuoteRiskManagementProfileStruct getDefaultQuoteRiskProfile(String userId)
        throws SystemException, CommunicationException, DataValidationException, AuthorizationException, NotFoundException;
    /**
     * Gets QRM global on/off switch status for this user
     *
     * @author Mike Pyatetsky
     *
     * @return  Boolean to enable/disable QRM globally
     * @exception SystemException
     * @exception CommunicationException
     * @exception DataValidationException
     * @exception AuthorizationException
     */
     public boolean getQuoteRiskManagementEnabledStatus(String userId)
        throws SystemException, CommunicationException, DataValidationException, AuthorizationException;
    /**
     * Queries all Quote Risk Management (QRM) Profile for given classKey
     *
     * @author Mike Pyatetsky
     *
     * @return QuoteRiskManagementProfileStruct
     *
     * @exception SystemException
     * @exception CommunicationException
     * @exception DataValidationException
     * @exception AuthorizationException
     * @exception NotFoundException
     */
     public QuoteRiskManagementProfileStruct getQuoteRiskManagementProfileByClass(String userId,int classKey)
        throws SystemException, CommunicationException, DataValidationException, AuthorizationException, NotFoundException;
    /**
     * Removes all quote risk profiles for this user
     *
     * @author Mike Pyatetsky
     *
     * @exception SystemException
     * @exception CommunicationException
     * @exception DataValidationException
     * @exception AuthorizationException
     * @exception TransactionFailedException
     */
     public void removeAllQuoteRiskProfiles(String userId)
        throws SystemException, CommunicationException, DataValidationException, AuthorizationException, TransactionFailedException;
    /**
     * Removes QRM profile for this user for given class key
     *
     * @author Mike Pyatetsky
     *
     * @param classKey int class key
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception TransactionFailedException
     */
     public void removeQuoteRiskProfile(String userId,int classKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException;
    /**
     * Sets QRM global on/off switch status for this user
     *
     * @author Mike Pyatetsky
     *
     * @param status Boolean to enable/disable QRM globally
     * @exception SystemException
     * @exception CommunicationException
     * @exception DataValidationException
     * @exception AuthorizationException
     * @exception TransactionFailedException
     */
     public void setQuoteRiskManagementEnabledStatus(String userId,boolean status)
        throws SystemException, CommunicationException, DataValidationException, AuthorizationException, TransactionFailedException;
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
     public void setQuoteRiskProfile(String userId,QuoteRiskManagementProfileStruct quoteRiskProfile)
        throws SystemException, CommunicationException, AuthorizationException, TransactionFailedException, DataValidationException;
}
