package com.cboe.internalPresentation.qrm;

import com.cboe.exceptions.*;
import com.cboe.idl.cmiQuote.*;
//------------------------------------------------------------------------------------------------------------------
// FILE:    GUIUserTradingPArametersAPIImpl.java
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
import com.cboe.internalPresentation.api.SystemAdminAPIFactory;
import com.cboe.presentation.api.MarketMakerAPIFactory;
import com.cboe.presentation.qrm.GUIUserTradingParametersAPI;

/**
 *  This class can be used for  ....
 *
 *
 *  @author Alex Brazhnichenko
 *  Creation date (3/8/00 11:12:17 AM)
 *  @version 03/08/2000
 */

public class SAGUIUserTradingParametersAPIImpl implements GUIUserTradingParametersAPI {
/**
 * GUIUserTradingPArametersAPIImpl constructor comment.
 */
public SAGUIUserTradingParametersAPIImpl() {
    super();
}
/**
 * getAllQuoteRiskProfiles method comment.
 */
public UserQuoteRiskManagementProfileStruct getAllQuoteRiskProfiles(String userId) throws CommunicationException, SystemException, AuthorizationException, DataValidationException, NotFoundException {
    return SystemAdminAPIFactory.find().getAllQuoteRiskProfiles(userId);
}
/**
 * getDefaultQuoteRiskProfile method comment.
 */
public QuoteRiskManagementProfileStruct getDefaultQuoteRiskProfile(String userId) throws CommunicationException, SystemException, AuthorizationException, DataValidationException, NotFoundException {
    return SystemAdminAPIFactory.find().getDefaultQuoteRiskProfile(userId);
}
/**
 * getQuoteRiskManagementEnabledStatus method comment.
 */
public boolean getQuoteRiskManagementEnabledStatus(String userId) throws CommunicationException, DataValidationException, SystemException, AuthorizationException {
    return SystemAdminAPIFactory.find().getQuoteRiskManagementEnabledStatus(userId);
}
/**
 * getQuoteRiskManagementProfileByClass method comment.
 */
public QuoteRiskManagementProfileStruct getQuoteRiskManagementProfileByClass(String userId, int classKey) throws CommunicationException, SystemException, DataValidationException, AuthorizationException, NotFoundException {
    return SystemAdminAPIFactory.find().getQuoteRiskManagementProfileByClass(userId,classKey);
}
/**
 * removeAllQuoteRiskProfiles method comment.
 */
public void removeAllQuoteRiskProfiles(String userId) throws CommunicationException, TransactionFailedException, SystemException, DataValidationException, AuthorizationException
{
    SystemAdminAPIFactory.find().removeAllQuoteRiskProfiles(userId);
}
/**
 * removeQuoteRiskProfile method comment.
 */
public void removeQuoteRiskProfile(String userId, int classKey) throws CommunicationException, DataValidationException, TransactionFailedException, SystemException, AuthorizationException
{
    SystemAdminAPIFactory.find().removeQuoteRiskProfile(userId,classKey);
}
/**
 * setQuoteRiskManagementEnabledStatus method comment.
 */
public void setQuoteRiskManagementEnabledStatus(String userId, boolean status) throws CommunicationException, TransactionFailedException, SystemException, DataValidationException, AuthorizationException
{
    SystemAdminAPIFactory.find().setQuoteRiskManagementEnabledStatus(userId,status);
}
/**
 * setQuoteRiskProfile method comment.
 */
public void setQuoteRiskProfile(String userId, QuoteRiskManagementProfileStruct quoteRiskProfile) throws CommunicationException, DataValidationException, TransactionFailedException, SystemException, AuthorizationException
{
    SystemAdminAPIFactory.find().setQuoteRiskProfile(userId,quoteRiskProfile);
}
}
