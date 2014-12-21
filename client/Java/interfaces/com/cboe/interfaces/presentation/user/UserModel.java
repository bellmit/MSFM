//
// -----------------------------------------------------------------------------------
// Source file: UserModel.java
//
// PACKAGE: com.cboe.interfaces.presentation.user;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.user;

import com.cboe.interfaces.presentation.product.ProductClass;

import com.cboe.interfaces.presentation.dpm.DPMModel;

/**
 * Defines a contract that a UserModel that represents a User should provide.
 */
public interface UserModel
{
    /**
     * Returns a hash code that represents this user for hashing.
     * @return hash code that should represents this user uniquely in some way.
     */
    int hashCode();

    /**
     * Returns a String representation of this user.
     * @return user information as a String
     */
    String toString();

     /**
      * Used as a tag interface method to remind implementors to implement this.
      */
    boolean equals(Object otherUserModel);

    /**
     * Gets the user Id that represents who the user logged in as.
     * @return user Id used to log in
     */
    String getUserId();

    /**
     * Gets the users full name.
     * @return full name
     */
    String getFullName();

    /**
     * Gets the list of executing or give up firms for this user.
     * @return array of Strings identifying firm labels
     */
    ExchangeFirm[] getExecutingGiveUpFirms();

    /**
     * Gets the Role that this user plays.
     * @return Role identifier from com.cboe.idl.cmiConstants.UserRoles
     */
    Role getRole();

    /**
     * Gets the ProfileStruct that is the default profile for this user.
     * @return default ProfileStruct
     */
    ProfileModel getDefaultProfile();

    /**
     * Gets the default ProfileModel for the trading session
     * @param tradingSession
     * @return default ProfileModel for tradingSession
     */ 
    ProfileModel getDefaultProfileForSession(String tradingSession);

    /**
     * Gets the profiles that this user contains.
     * @return array of ProfileStruct's that this user contains.
     */
    ProfileModel[] getProfiles();

    /**
     * Gets the users assigned classes.
     * @return array of class keys that represent the classes this user is assigned.
     */
    int[] getClassKeys();

    /**
     * Gets the users assigned classes.
     * @return array of ProductClass's this user is assigned.
     */
    ProductClass[] getProductClasses();

    /**
     * Determines if this user contains a class.
     * @param classKey to check for
     * @return 0 or greater represents that it was found and the number is the location in the array
     * of contained classes. Less than zero represents it was not found.
     */
    int containsClassKey(int classKey);

    /**
     * Gets the DPM's that this user is assigned to.
     * @return an array of DPMStructModel's that this user is assigned to.
     */
    DPMModel[] getAssignedDPMs();

    /**
     * Determines if this user is assigned to a DPM.
     * @param dpm to check for
     * @return 0 or greater represents that it was found and the number is the location in the array
     * of assigned DPM's. Less than zero represents it was not found.
     */
    int containsDPM(DPMModel dpm);

    ExchangeAcronym getExchangeAcronym();

    ExchangeFirm getExchangeFirm();
}