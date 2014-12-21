package com.cboe.domain.user;

//----------------------------------------------------------------------
// Source file: Java/com/cboe/domain/user/UserImpl.java
//
// PACKAGE: com.cboe.domain.user
//----------------------------------------------------------------------
// Copyright (c) 1999 The Chicago Board Options Exchange. All Rights Reserved.
//----------------------------------------------------------------------

import java.util.Vector;

import com.cboe.domain.util.DateWrapper;
import com.cboe.domain.util.StructBuilder;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.idl.cmiQuote.QuoteRiskManagementProfileStruct;
import com.cboe.idl.cmiQuote.UserQuoteRiskManagementProfileStruct;
import com.cboe.idl.cmiUser.AccountStruct;
import com.cboe.idl.cmiUser.ExchangeAcronymStruct;
import com.cboe.idl.cmiUser.SessionProfileUserStruct;
import com.cboe.idl.user.SessionProfileUserDefinitionStruct;
import com.cboe.idl.user.UserSummaryStruct;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.interfaces.domain.user.QuoteRiskManagementProfile;
import com.cboe.interfaces.domain.user.User;



/**
 * A persistent implementation of <code>User</code>.
 * 
 * <p><b>NOTE:</b>  This is the only implementation of the User interface.  It is
 *  a composite decorator class that will delegate the appropriate calls to acrUserImpl
 * and userIdImpl.
 * 
 * <p><b>NOTE:</b>  The profiles assiciated with this class have two roles
 *		First, there is a default profile
 * 		Second, there are profiles by class
 *		We store both of these in the "profiles" list
 * 		The structs have them separeted out so we join and filter these two when converting
 *		to and from structs.
 *
 * @author John Wickberg
 * @author Brad Samuels
 */
class UserCombinedImpl extends BObject implements User, UserInternal
{
    private AcronymUserImpl acrUserImpl;
    private UserIdImpl userIdImpl;
    
    AcronymUserImpl getAcronymUserImpl()
    {
        return acrUserImpl;
    }

    UserIdImpl getUserIdImpl()
    {
        return userIdImpl;
    }
    
    /**
     * Updates both userid and acronym user.  
     * (Acronym user update is for rollout of singleAcronym only)
     * 
     * @see com.cboe.interfaces.domain.user.User#setIsActive(boolean)
     */
    public void setIsActive(boolean isActive)
    {
        this.userIdImpl.setIsActive(isActive);
        this.acrUserImpl.setActive(isActive);
    }
    
    public UserCombinedImpl(AcronymUserImpl aUserImpl, UserIdImpl aUserIdImpl)
    {
        this.acrUserImpl = aUserImpl;
        this.userIdImpl = aUserIdImpl;
    }

    /**
     * @see com.cboe.interfaces.domain.user.UserIdentifier#getFullName()
     */
    public String getFullName()
    {
        return acrUserImpl.getFullName();
    }

    /**
     * @see com.cboe.interfaces.domain.user.UserIdentifier#getUserId()
     */
    public String getUserId()
    {
        return userIdImpl.getUserId();
    }

    /**
     * @see com.cboe.interfaces.domain.user.UserIdentifier#getUserKey()
     */
    public int getUserIdKey()
    {
        return userIdImpl.getUserIdKey();
    }

    public boolean getQuoteRiskManagementEnabled()
    {
        return acrUserImpl.getQuoteRiskManagementEnabled();
    }

    public Vector getQuoteRiskManagementProfileVector()
    {
        return acrUserImpl.getQuoteRiskManagementProfileVector();
    }

    public QuoteRiskManagementProfile[] getQuoteRiskManagementProfiles()
    {
        return acrUserImpl.getQuoteRiskManagementProfiles();
    }

    public void setQuoteRiskManagementEnabled(boolean aValue)
    {
        acrUserImpl.setQuoteRiskManagementEnabled(aValue);
    }

    public void addQuoteRiskManagementProfile(QuoteRiskManagementProfileStruct newProfileStruct)
    {
        acrUserImpl.addQuoteRiskManagementProfile(newProfileStruct);
    }

    public void setQuoteRiskManagementProfileVector(Vector newProfiles) throws TransactionFailedException
    {
        acrUserImpl.setQuoteRiskManagementProfileVector(newProfiles);
    }

    public QuoteRiskManagementProfile getQuoteRiskManagementProfileForClass(int classKey)
    {
        return acrUserImpl.getQuoteRiskManagementProfileForClass(classKey);
    }

    public void removeQuoteRiskManagementProfile(int classKey) throws TransactionFailedException
    {
        acrUserImpl.removeQuoteRiskManagementProfile(classKey);
    }

    public void updateQuoteRiskManagementProfiles(QuoteRiskManagementProfileStruct[] profileStructs) throws TransactionFailedException
    {
        acrUserImpl.updateQuoteRiskManagementProfiles(profileStructs);
    }

    public UserQuoteRiskManagementProfileStruct toQuoteRiskStruct()
    {
        return acrUserImpl.toQuoteRiskStruct();
    }

    /**
     * @see com.cboe.interfaces.domain.user.AcronymUser#getUserType()
     */
    public short getUserType()
    {
        return acrUserImpl.getUserType();
    }

    /**
     * @see com.cboe.interfaces.domain.user.AcronymUser#getUserAcronymKey()
     */
    public int getAcronymUserKey()
    {
        return acrUserImpl.getAcronymUserKey();
    }

    /**
     * @see com.cboe.interfaces.domain.user.AcronymUser#getRole()
     */
    public char getRole()
    {
        return acrUserImpl.getRole();
    }

    /**
     * @see com.cboe.interfaces.domain.user.AcronymUser#getAccounts()
     */
    public AccountStruct[] getAccounts()
    {
        return acrUserImpl.getAccounts();
    }

    /**
     * @see com.cboe.interfaces.domain.user.AcronymUser#getAssignedClasses()
     */
    public int[] getAssignedClasses()
    {
        return acrUserImpl.getAssignedClasses();
    }

    /**
     * @see com.cboe.interfaces.domain.user.AcronymUser#getAcronym()
     */
    public String getAcronym()
    {
        return acrUserImpl.getAcronym();
    }

    /**
     * @see com.cboe.interfaces.domain.user.UserIdentifier#isActive()
     */
    public boolean isActive()
    {
        return userIdImpl.isActive();
    }
    
    public String getExchangeAcronym()
    {
        return acrUserImpl.getExchangeAcronym();
    }

    /**
     * Creates definition struct containing user information.
     *
     * @return SessionProfileUserDefinitionStruct, definition struct representing user
     */
    public synchronized SessionProfileUserDefinitionStruct toDefinitionStruct()
    {
        synchronized (acrUserImpl) // careful with the nesting!!
        {
            // Convert all private data to a struct
            SessionProfileUserDefinitionStruct result = (SessionProfileUserDefinitionStruct)com.cboe.domain.util.ReflectiveStructBuilder.newStruct(SessionProfileUserDefinitionStruct.class);
            result.userKey = getUserIdKey();
            result.userId = getUserId();
            result.fullName = getFullName();
            result.userAcronym = new ExchangeAcronymStruct(acrUserImpl.getExchangeAcronym(),getAcronym());
            result.firmKey = acrUserImpl.getFirmKey();
            result.membershipKey = acrUserImpl.getMembershipKey();
            result.userType = getUserType();
            result.isActive = isActive();
            result.role = getRole();
            result.assignedClasses = acrUserImpl.getAssignedClassesV2();
            result.executingGiveupFirms = acrUserImpl.getExecutingGiveupFirmNumbers();
            result.accounts = acrUserImpl.getAccountDefinitions();
            result.sessionProfilesByClass = acrUserImpl.getSessionProfilesByClass();
            result.defaultSessionProfiles = acrUserImpl.getDefaultSessionProfiles();
            result.defaultProfile = acrUserImpl.getDefaultProfile().toStruct();  //the getDefaultProfile should not return null, if it does the null pointer exception is acceptable
            result.sessionClearingAcronyms = acrUserImpl.getSessionClearingAcrs();
            result.inactivationTime = StructBuilder.buildDateTimeStruct();
            result.lastModifiedTime = DateWrapper.convertToDateTime(acrUserImpl.getLastModifiedTime());
            result.versionNumber = acrUserImpl.getVersionNumber();
            result.dpms = acrUserImpl.createDpmStructs();
            return result; 
        }
    }
    /**
     * Creates definition struct containing user information.
     *
     * @return definition struct representing user
     */
    public synchronized SessionProfileUserStruct toUserStruct()
    {
        synchronized (acrUserImpl)// careful with the nesting!!
        {
            SessionProfileUserStruct result = (SessionProfileUserStruct)com.cboe.domain.util.ReflectiveStructBuilder.newStruct(SessionProfileUserStruct.class);
            result.userId = getUserId();
            result.userAcronym = new ExchangeAcronymStruct(acrUserImpl.getExchangeAcronym(), getAcronym());
            result.fullName = getFullName();
            result.executingGiveupFirms = acrUserImpl.getExecutingGiveupFirmNumbers();
            result.accounts = getAccounts();
            result.sessionProfilesByClass = acrUserImpl.getSessionProfilesByClass();
            result.defaultSessionProfiles = acrUserImpl.getDefaultSessionProfiles();
            result.defaultProfile = acrUserImpl.getDefaultProfile().toStruct(); //the getDefaultProfile should not return null, if it does the null pointer exception is acceptable
            result.firm = null; // firm number needs to be added by calling service - don't want a
            // dependency between the user domain and the firm service.
            result.role = getRole();
            result.assignedClasses = getAssignedClasses();
            result.dpms = acrUserImpl.createDpmStructs();
            return result;
        }
    }

    /**
     * @see com.cboe.interfaces.domain.user.User#fromStruct(com.cboe.idl.user.SessionProfileUserDefinitionStruct, boolean)
     */
    public void fromStruct(SessionProfileUserDefinitionStruct updatedValues, boolean membershipDefined) throws DataValidationException
    {
        acrUserImpl.fromStruct(updatedValues, membershipDefined);
        userIdImpl.setIsActive(updatedValues.isActive);
    }

    /**
     * @return
     */
    public AcronymUserImpl getUserImpl()
    {
        return acrUserImpl;
    }

    /**
     * @see com.cboe.interfaces.domain.user.User#updateBase(com.cboe.idl.user.SessionProfileUserDefinitionStruct, boolean)
     */
    public void updateBase(SessionProfileUserDefinitionStruct updatedValues, boolean membershipDefined)
        throws DataValidationException
    {
        acrUserImpl.updateBase(updatedValues, membershipDefined);
    }

    /**
     * @see com.cboe.interfaces.domain.user.User#setTestClassesOnly(boolean)
     */
    public void setTestClassesOnly(boolean testClassesOnlyFlag)
    {
        acrUserImpl.setTestClassesOnly(testClassesOnlyFlag);
    }

    /**
     * @see com.cboe.interfaces.domain.user.User#getTestClassesOnly()
     */
    public boolean getTestClassesOnly()
    {
        return acrUserImpl.getTestClassesOnly();
    }
    
    public String toString()
    {
        final String userId = (userIdImpl == null) ? "{userid=nul}" : "id="+userIdImpl.toString();
        final String acrUser = (acrUserImpl == null) 
            ? "{acruser=null}" 
            : ":acr="+acrUserImpl.getAcronym() + ":exch=" + acrUserImpl.getExchangeAcronym();
        return userId + acrUser;
    }

    /**
     * @return
     */
    public UserSummaryStruct toSummaryStruct()
    {
        UserSummaryStruct summary = new UserSummaryStruct();
        summary.userKey = this.getUserIdKey();
        summary.firmKey = this.getUserImpl().getFirmKey();
        summary.fullName = this.getFullName();
        summary.isActive = this.isActive();
        summary.role = this.getRole();
        summary.userAcronym = new ExchangeAcronymStruct();
        summary.userAcronym.exchange = this.getExchangeAcronym();
        summary.userAcronym.acronym = this.getAcronym();
        summary.userId = this.getUserId();
        summary.userType = this.getUserType();
        return summary;
    }

    
    public Profile getDefaultProfile()
    {
        return acrUserImpl.getDefaultProfile();
    }
}
