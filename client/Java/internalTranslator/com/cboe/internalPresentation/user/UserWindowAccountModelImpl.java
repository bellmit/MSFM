package com.cboe.internalPresentation.user;

/**
 * ----------------------------------------------------------------------------
 * Implementation of model for windows that display User information for SA GUI.
 * Essentially a wrapper around UserAccountModel.
 *
 * ----------------------------------------------------------------------------
 * Copyright (c) 2002 The Chicago Board Options Exchange. All Rights Reserved.
 * ----------------------------------------------------------------------------
 */

import java.beans.PropertyChangeListener;
import java.util.*;

import com.cboe.idl.user.SessionProfileUserDefinitionStruct;
import com.cboe.idl.user.UserSummaryStruct;

import com.cboe.exceptions.AlreadyExistsException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;

import com.cboe.interfaces.internalPresentation.firm.FirmModel;
import com.cboe.interfaces.internalPresentation.user.JointAccount;
import com.cboe.interfaces.internalPresentation.user.UserAccountModel;
import com.cboe.interfaces.internalPresentation.user.UserWindowAccountModel;
import com.cboe.interfaces.presentation.dpm.DPMModel;
import com.cboe.interfaces.presentation.product.ProductClass;
import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.interfaces.presentation.qrm.UserQuoteRiskManagementProfile;
import com.cboe.interfaces.presentation.user.Account;
import com.cboe.interfaces.presentation.user.ExchangeAcronym;
import com.cboe.interfaces.presentation.user.ExchangeFirm;
import com.cboe.interfaces.presentation.user.Profile;
import com.cboe.interfaces.presentation.user.ProfileModel;
import com.cboe.interfaces.presentation.user.Role;


public class UserWindowAccountModelImpl implements UserWindowAccountModel
{
    //INSTANCE VARIABLES-------------------------------------------------------

    private UserAccountModel delegate = null;

    //CONSTRUCTORS-------------------------------------------------------------

    public UserWindowAccountModelImpl(UserAccountModel delegate)
    {
        this.delegate = delegate;
    }

    //PUBLIC METHODS-----------------------------------------------------------

    /**
     * Should refresh the current SMS data.
     */
    public void refreshSMSData()
    {
        delegate.refreshSMSData();
    }

    /**
     * Determines if this user is logged in.
     * @return SMS_LOGGED_IN if the user is logged in, SMS_LOGGED_OUT if logged out, and
     *         SMS_NOT_LOADED if SMS has not yet been queried for the status
     */
    public short getLoggedInStatus()
    {
        return delegate.getLoggedInStatus();
    }

    /**
     * Determines if this user is logged in.
     * @param refreshFromSMS - If true and if the user's SMS session isn't already cached, SMS will be queried for the user's session
     * @return SMS_LOGGED_IN if the user is logged in, SMS_LOGGED_OUT if logged out, and
     *         SMS_NOT_LOADED if SMS has not yet been queried for the status
     */
    public short getLoggedInStatus(boolean refreshFromSMS)
    {
        return delegate.getLoggedInStatus(refreshFromSMS);
    }

    /**
     * Gets the list of CAS's this user is logged into.
     * @return String array of CAS's this user is logged into
     */
    public String[] getCASList()
    {
        return delegate.getCASList();
    }

    public String[] getCASList(boolean refreshFromSMS)
    {
        return delegate.getCASList(refreshFromSMS);
    }

    /**
     * Gets the users default profile
     * @return com.cboe.interfaces.presentation.user.Profile
     */
    public ProfileModel getDefaultProfile()
    {
        return delegate.getDefaultProfile();
    }

    /**
     * Sets the users default profile
     * @param profile com.cboe.interfaces.presentation.user.Profile
     */
    public void setDefaultProfile(Profile profile)
    {
        delegate.setDefaultProfile(profile);
    }

    /**
     * Gets the executing/give up firms
     * @return give up firms
     */
    public ExchangeFirm[] getExecutingGiveUpFirms()
    {
        return delegate.getExecutingGiveUpFirms();
    }

    /**
     * Sets the array of executing or give up firms
     * @param firms
     */
    public void setExecutingGiveUpFirms(ExchangeFirm[] firms)
    {
        delegate.setExecutingGiveUpFirms(firms);
    }

    /**
     * Gets the firm key
     * @return firm key
     */
    public int getFirmKey()
    {
        return delegate.getFirmKey();
    }

    /**
     * Gets the firm model for this user
     * @return model representing firm key
     */
    public FirmModel getFirmModel()
    {
        return delegate.getFirmModel();
    }

    /**
     * Sets the firm key
     * @param firmKey new firm key
     */
    public void setFirmKey(int firmKey)
    {
        delegate.setFirmKey(firmKey);
    }

    /**
     * Gets the user full name
     * @return user full name
     */
    public String getFullName()
    {
        return delegate.getFullName();
    }

    /**
     * Sets the user full name
     * @param name new user full name
     */
    public void setFullName(String name)
    {
        delegate.setFullName(name);
    }

    /**
     * Gets the inactivation time
     * @return Calendar
     */
    public Calendar getInactivationTime()
    {
        return delegate.getInactivationTime();
    }

    /**
     * Sets the inactivation time
     * @param newDateTime inactivation time
     */
    public void setInactivationTime(Calendar newDateTime)
    {
        delegate.setInactivationTime(newDateTime);
    }

    /**
     * Gets the last modified time
     * @return Calendar
     */
    public Calendar getLastModifiedTime()
    {
        return delegate.getLastModifiedTime();
    }

    /**
     * Sets the last modified time
     * @param newDateTime last modified time
     */
    public void setLastModifiedTime(Calendar newDateTime)
    {
        delegate.setLastModifiedTime(newDateTime);
    }

    /**
     * Gets the membership key
     * @return membership key
     */
    public int getMembershipKey()
    {
        return delegate.getMembershipKey();
    }

    /**
     * Sets the membership key
     * @param membershipKey new membership key
     */
    public void setMembershipKey(int membershipKey)
    {
        delegate.setMembershipKey(membershipKey);
    }

    public String getUserId()
    {
        return delegate.getUserId();
    }

    public void setUserId(String userId)
    {
        delegate.setUserId(userId);
    }

    /**
     * Gets the Profiles
     * @return
     */
    public ProfileModel[] getProfiles()
    {
        return delegate.getProfiles();
    }

    /**
     * Gets the mutable Profiles
     * @return
     */
    public ProfileModel[] getProfileModels()
    {
        return delegate.getProfileModels();
    }

    /**
     * Sets the Profiles
     * @param profiles
     */
    public void setProfiles(Profile[] profiles)
    {
        delegate.setProfiles(profiles);
    }

    /**
     * Updates or adds a profile.
     * @param profile Profile to add or update.
     */
    public void setProfile(Profile profile)
    {
        delegate.setProfile(profile);
    }

    /**
     * Removes a profile.
     * @param profile ProfileModel to remove.
     */
    public void removeProfile(Profile profile)
    {
        delegate.removeProfile(profile);
    }

    /**
     * Gets the user role code
     * @return user rol code
     */
    public Role getRole()
    {
        return delegate.getRole();
    }

    /**
     * Sets the user role code
     * @param role new user role code
     */
    public void setRole(Role role)
    {
        delegate.setRole(role);
    }

    /**
     * Get the accounts assigned for this user.
     * @return JointAccount[]
     */
    public JointAccount[] getUserAccounts()
    {
        return delegate.getUserAccounts();
    }

    /**
     * Adds the passed account to this users assigned accounts.
     * @param account to add to the user.
     */
    public void addUserAccount(JointAccount account)
    {
        delegate.addUserAccount(account);
    }

    /**
     * Determines if the passed account is assigned to this user.
     * @param account to find
     * @return Index into collection of found accounts. Will be >=0 if found, <0 if not found.
     */
    public int containsUserAccount(JointAccount account)
    {
        return delegate.containsUserAccount(account);
    }

    /**
     * Removes all accounts
     */
    public void removeAllUserAccounts()
    {
        delegate.removeAllUserAccounts();
    }

    /**
     * Removes the passed account from this users assigned accounts.
     * @param account to remove from the user
     */
    public void removeUserAccount(JointAccount account)
    {
        delegate.removeUserAccount(account);
    }

    public boolean isUserDetailsAvailable()
    {
        return delegate.isUserDetailsAvailable();
    }

    public UserSummaryStruct getUserSummaryStruct()
    {
        return delegate.getUserSummaryStruct();
    }

    /**
     * Gets the UserDefinitionStruct that this model represents.
     * @return
     */
    public SessionProfileUserDefinitionStruct getUserDefinitionStruct()
    {
        return delegate.getUserDefinitionStruct();
    }

    /**
     * Sets the UserDefinitionStruct that this model represents.
     * @param newUser New struct for this model to represent.
     */
    public void setUserDefinitionStruct(SessionProfileUserDefinitionStruct newUser)
    {
        delegate.setUserDefinitionStruct(newUser);
    }

    /**
     * Gets the assigned classes
     * @return array of ProductClass
     */
    public ProductClass[] getProductClasses()
    {
        return delegate.getProductClasses();
    }

    /**
     * Gets the user key
     * @return user key
     */
    public int getUserKey()
    {
        return delegate.getUserKey();
    }

    /**
     * Gets the users Quote risk management profile.
     * @return profile
     */
    public UserQuoteRiskManagementProfile getUserQRMProfile()
    {
        return delegate.getUserQRMProfile();
    }

    /**
     * Sets the users Quote risk management profile.
     * @param newUserQRMProfile
     */
    public void setUserQRMProfile(UserQuoteRiskManagementProfile newUserQRMProfile)
    {
        delegate.setUserQRMProfile(newUserQRMProfile);
    }

    /**
     * Sets the user type code
     * @param userType new user type code
     */
    public void setUserType(short userType)
    {
        delegate.setUserType(userType);
    }

    /**
     * Gets the assigned class keys
     * @return int[] a sequence of user assigned class keys
     */
    public int[] getClassKeys()
    {
        return delegate.getClassKeys();
    }

    /**
     * Adds the passed class key to this users assign classes.
     * @param spc SessionProductClass to add to the user.
     */
    public void addClass(SessionProductClass spc)
    {
        delegate.addClass(spc);
    }

    /**
     * Determines if the passed classKey is assigned to this user.
     * @param classKey to find
     * @return Index into collection of found class. Will be >=0 if found, <0 if not found.
     */
    public int containsClassKey(int classKey)
    {
        return delegate.containsClassKey(classKey);
    }

    /**
     * Removes all classes
     */
    public void removeAllClasses()
    {
        delegate.removeAllClasses();
    }

    /**
     * Removes the passed class key from this users assigned classes.
     * @param spc SessionProductClass to remove from the user
     */
    public void removeClass(SessionProductClass spc)
    {
        delegate.removeClass(spc);
    }

    /**
     * Gets the DPM accounts that this user is assigned to.
     * @return DPMModel sequence
     */
    public DPMModel[] getAssignedDPMs()
    {
        return delegate.getAssignedDPMs();
    }

    /**
     * Adds the passed DPM to this users assigned DPM's.
     * @param dpm to add to the user.
     */
    public void addDPM(DPMModel dpm)
    {
        delegate.addDPM(dpm);
    }

    /**
     * Determines if the passed DPM is assigned to this user.
     * @param dpm to find
     * @return Index into collection of found accounts. Will be >=0 if found, <0 if not found.
     */
    public int containsDPM(DPMModel dpm)
    {
        return delegate.containsDPM(dpm);
    }

    /**
     * Removes all DPM's from this struct.
     */
    public void removeAllDPMs()
    {
        delegate.removeAllDPMs();
    }

    /**
     * Removes the passed DPM from this users assigned DPM's.
     * @param dpm to remove from the user
     */
    public void removeDPM(DPMModel dpm)
    {
        delegate.removeDPM(dpm);
    }

    /**
     * Gets the Session isActive flag
     * @return boolean True if active, false otherwise.
     */
    public boolean isActive()
    {
        return delegate.isActive();
    }

    /**
     * Sets the Session isActive flag
     * @param active True if template is enabled to become active, false otherwise.
     */
    public void setActive(boolean active)
    {
        delegate.setActive(active);
    }

    /**
     * Determines if the user has been modified.
     * @return True if it has been modified, false otherwise.
     */
    public boolean isModified()
    {
        return delegate.isModified();
    }

    /**
     * Determines if the user has never been saved.
     * @return True if it has never been saved to the API, false otherwise.
     */
    public boolean isNeverBeenSaved()
    {
        return delegate.isNeverBeenSaved();
    }

    /**
     * Aborts any changes in the user within this model to the API.
     * @exception CommunicationException
     * @exception DataValidationException
     * @exception SystemException
     * @exception AuthorizationException
     * @exception NotFoundException
     */
    public void abortChanges() throws CommunicationException, DataValidationException, NotFoundException, SystemException, AuthorizationException
    {
        delegate.abortChanges();
    }

    /**
     * Saves any changes in the user within this model to the API.
     * @exception CommunicationException
     * @exception DataValidationException
     * @exception TransactionFailedException
     * @exception SystemException
     * @exception AuthorizationException
     * @exception NotFoundException
     * @exception AlreadyExistsException
     */
    public void saveChanges() throws CommunicationException, DataValidationException, TransactionFailedException, SystemException, AuthorizationException, NotFoundException, AlreadyExistsException
    {
        delegate.saveChanges();
    }

    /**
     * Add the listener for property changes to the User attributes.
     * @param listener PropertyChangeListener to receive a callback when a User
     * property is changed.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        delegate.addPropertyChangeListener(listener);
    }

    /**
     * Removes the listener for property changes to the User attributes.
     * @param listener PropertyChangeListener to remove from receiving callbacks when a User
     * property is changed.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        delegate.removePropertyChangeListener(listener);
    }

    /**
     * Returns the delegate class.
     * @return
     */
    public UserAccountModel getUserAccountModel()
    {
        return delegate;
    }

    //USERWINDOWMODEL METHODS--------------------------------------------------

    /**
     * Returns the User Type (Individual Account, etc).
     * @return
     */
    public short getUserType()
    {
        return delegate.getUserType();
    }

    /**
     * Returns the User Exchange.
     * @return
     */
    public String getExchange()
    {
        return delegate.getExchangeAcronym().getExchange();
    }

    /**
     * Returns the User Acronym.
     * @return
     */
    public String getAcronym()
    {
        return delegate.getExchangeAcronym().getAcronym();
    }

    public ExchangeAcronym getExchangeAcronym()
    {
        return delegate.getExchangeAcronym();
    }

    public ExchangeFirm getExchangeFirm()
    {
        return delegate.getExchangeFirm();
    }

    /**
     * Returns the Firm number of the user's Firm.
     * @return
     */
    public String getFirmNumber()
    {
        Integer firmNumber = new Integer(delegate.getFirmKey());
        return firmNumber.toString();
    }

    /**
     * Sets the exchangeAcronym for this user
     * @param exchangeAcronym new exhangeAcronym for this user
     */
    public void setExchangeAcronym(ExchangeAcronym exchangeAcronym)
    {
        delegate.setExchangeAcronym(exchangeAcronym);
    }

    /**
     * Gets the accounts this user belongs to. Currently returns null; use
     * getUserAccounts() instead.
     * @return accounts
     */
    public Account[] getAccounts()
    {
        return null;
    }
}
