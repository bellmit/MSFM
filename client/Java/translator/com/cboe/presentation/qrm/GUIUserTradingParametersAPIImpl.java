package com.cboe.presentation.qrm;

import com.cboe.exceptions.*;

import com.cboe.idl.cmiQuote.*;

import com.cboe.presentation.api.APIHome;

/**
 *  This class can be used for ....
 *
 *
 *  @author Alex Brazhnichenko
 *  Creation date (3/8/00 11:12:17 AM)
 *  @version 03/08/2000
 */

public class GUIUserTradingParametersAPIImpl implements GUIUserTradingParametersAPI {
/**
 * GUIUserTradingPArametersAPIImpl constructor comment.
 */
public GUIUserTradingParametersAPIImpl() {
    super();
}
/**
 * getAllQuoteRiskProfiles method comment.
 */
public UserQuoteRiskManagementProfileStruct getAllQuoteRiskProfiles(String userId) throws CommunicationException, SystemException, AuthorizationException, NotFoundException {
    return APIHome.findUserTradingParametersAPI().getAllQuoteRiskProfiles();
}
/**
 * getDefaultQuoteRiskProfile method comment.
 */
public QuoteRiskManagementProfileStruct getDefaultQuoteRiskProfile(String userId) throws CommunicationException, SystemException, AuthorizationException, NotFoundException {
    return APIHome.findUserTradingParametersAPI().getDefaultQuoteRiskProfile();
}
/**
 * getQuoteRiskManagementEnabledStatus method comment.
 */
public boolean getQuoteRiskManagementEnabledStatus(String userId) throws CommunicationException, SystemException, AuthorizationException {
    return APIHome.findUserTradingParametersAPI().getQuoteRiskManagementEnabledStatus();
}
/**
 * getQuoteRiskManagementProfileByClass method comment.
 */
public QuoteRiskManagementProfileStruct getQuoteRiskManagementProfileByClass(String userId, int classKey) throws CommunicationException, SystemException, AuthorizationException, NotFoundException {
    return APIHome.findUserTradingParametersAPI().getQuoteRiskManagementProfileByClass(classKey);
}
/**
 * removeAllQuoteRiskProfiles method comment.
 */
public void removeAllQuoteRiskProfiles(String userId) throws CommunicationException, TransactionFailedException, SystemException, AuthorizationException
{
    APIHome.findUserTradingParametersAPI().removeAllQuoteRiskProfiles();
}
/**
 * removeQuoteRiskProfile method comment.
 */
public void removeQuoteRiskProfile(String userId, int classKey) throws CommunicationException, DataValidationException, TransactionFailedException, SystemException, AuthorizationException
{
    APIHome.findUserTradingParametersAPI().removeQuoteRiskProfile(classKey);
}
/**
 * setQuoteRiskManagementEnabledStatus method comment.
 */
public void setQuoteRiskManagementEnabledStatus(String userId, boolean status) throws CommunicationException, TransactionFailedException, SystemException, AuthorizationException
{
    APIHome.findUserTradingParametersAPI().setQuoteRiskManagementEnabledStatus(status);
}
/**
 * setQuoteRiskProfile method comment.
 */
public void setQuoteRiskProfile(String userId, QuoteRiskManagementProfileStruct quoteRiskProfile) throws CommunicationException, DataValidationException, TransactionFailedException, SystemException, AuthorizationException
{
    APIHome.findUserTradingParametersAPI().setQuoteRiskProfile(quoteRiskProfile);
}
}
