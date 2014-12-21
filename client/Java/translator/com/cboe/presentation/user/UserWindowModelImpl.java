package com.cboe.presentation.user;

/**
 * ----------------------------------------------------------------------------
 * Concrete model class for windows that display User information.
 * Delegates to the UserModel, wrapping all its methods.
 *
 * ----------------------------------------------------------------------------
 * Copyright (c) 2002 The Chicago Board Options Exchange. All Rights Reserved.
 * ----------------------------------------------------------------------------
 */

import com.cboe.interfaces.presentation.user.*;
import com.cboe.interfaces.presentation.user.UserStructModel;
import com.cboe.interfaces.presentation.product.ProductClass;
import com.cboe.interfaces.presentation.dpm.DPMModel;

public class UserWindowModelImpl implements UserWindowModel
{
    //INSTANCE VARIABLES-------------------------------------------------------

    private UserStructModel delegate = null;

    //CONSTRUCTORS-------------------------------------------------------------

    public UserWindowModelImpl(UserStructModel delegate)
    {
        this.delegate = delegate;
    }

    //PUBLIC METHODS-----------------------------------------------------------

    /**
     * Gets the user Id that represents who the user logged in as.
     * @return user Id used to log in
     */
    public String getUserId()
    {
        return delegate.getUserId();
    }

    /**
     * Gets the users full name.
     * @return full name
     */
    public String getFullName()
    {
        return delegate.getFullName();
    }

    /**
     * Gets the list of executing or give up firms for this user.
     * @return array of Strings identifying firm labels
     */
    public ExchangeFirm[] getExecutingGiveUpFirms()
    {
        return delegate.getExecutingGiveUpFirms();
    }

    /**
     * Gets the Role that this user plays.
     * @return Role identifier from com.cboe.idl.cmiConstants.UserRoles
     */
    public Role getRole()
    {
        return delegate.getRole();
    }

    /**
     * Gets the ProfileStruct that is the default profile for this user.
     * @return default ProfileStruct
     */
    public ProfileModel getDefaultProfile()
    {
        return delegate.getDefaultProfile();
    }

    /**
     * Gets the profiles that this user contains.
     * @return array of ProfileStruct's that this user contains.
     */
    public ProfileModel[] getProfiles()
    {
        return delegate.getProfiles();
    }

    /**
     * Gets the users assigned classes.
     * @return array of class keys that represent the classes this user is assigned.
     */
    public int[] getClassKeys()
    {
        return delegate.getClassKeys();
    }

    /**
     * Gets the users assigned classes.
     * @return array of ProductClass's this user is assigned.
     */
    public ProductClass[] getProductClasses()
    {
        return delegate.getProductClasses();
    }

    /**
     * Gets the accounts this user belongs to.
     * @return accounts
     */
    public Account[] getAccounts()
    {
        return delegate.getAccounts();
    }

    /**
     * Determines if this user contains a class.
     * @param classKey to check for
     * @return 0 or greater represents that it was found and the number is the location in the array
     * of contained classes. Less than zero represents it was not found.
     */
    public int containsClassKey(int classKey)
    {
        return delegate.containsClassKey(classKey);
    }

    /**
     * Gets the DPM's that this user is assigned to.
     * @return an array of DPMStructModel's that this user is assigned to.
     */
    public DPMModel[] getAssignedDPMs()
    {
        return delegate.getAssignedDPMs();
    }

    /**
     * Determines if this user is assigned to a DPM.
     * @param dpm to check for
     * @return 0 or greater represents that it was found and the number is the location in the array
     * of assigned DPM's. Less than zero represents it was not found.
     */
    public int containsDPM(DPMModel dpm)
    {
        return delegate.containsDPM(dpm);
    }

    /**
     * Returns the User's Firm key
     * @return
     */
    public int getFirmKey()
    {
        int firmInt = 0;

        try
        {
            firmInt = Integer.parseInt(delegate.getUserStruct().firm.firmNumber);
        }
        catch(NumberFormatException e)
        {
            //should probably do something!
        }

        return firmInt;
    }

    /**
     * Returns the User Type (Individual Account, etc); TEMPORARY -- returns 0
     * //todo change this
     * @return
     */
    public short getUserType()
    {
        return 0;
    }

    /**
     * Returns the User Exchange.
     * @return
     */
    public String getExchange()
    {
        return delegate.getUserStruct().userAcronym.exchange;
    }

    /**
     * Returns the User Acronym.
     * @return
     */
    public String getAcronym()
    {
        return delegate.getUserStruct().userAcronym.acronym;
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
        return delegate.getUserStruct().firm.firmNumber;
    }
}
