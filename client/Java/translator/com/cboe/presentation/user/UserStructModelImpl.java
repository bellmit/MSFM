/**
 * -----------------------------------------------------------------------------------
 * Source file: UserStructModel.java
 *
 * PACKAGE: com.cboe.presentation.user;
 *
 * -----------------------------------------------------------------------------------
 * Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
 * -----------------------------------------------------------------------------------
 */
package com.cboe.presentation.user;

import java.util.*;

import org.omg.CORBA.UserException;

import com.cboe.idl.cmiUser.SessionProfileUserStruct;

import com.cboe.interfaces.presentation.dpm.DPMModel;
import com.cboe.interfaces.presentation.product.ProductClass;
import com.cboe.interfaces.presentation.user.Account;
import com.cboe.interfaces.presentation.user.ExchangeAcronym;
import com.cboe.interfaces.presentation.user.ExchangeFirm;
import com.cboe.interfaces.presentation.user.ProfileModel;
import com.cboe.interfaces.presentation.user.Role;
import com.cboe.interfaces.presentation.user.UserStructModel;

import com.cboe.presentation.api.APIHome;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.dpm.DPMStructModel;

/**
 * Provides containment of a UserStruct with some limited behaviour.
 */
public class UserStructModelImpl
    implements UserStructModel
{
    private SessionProfileUserStruct struct = null;
	private String firm;
	private ExchangeFirm [] executingGiveUpFirms;
	private ProfileModel defaultProfile;
	private ProfileModel [] profileModels;
	private ProductClass[] productClasses;
	private int [] sortedClassKeys;
	private Account[] accounts;
    private ExchangeAcronym exchangeAcronym;
    private ExchangeFirm exchangeFirm;

    public UserStructModelImpl(SessionProfileUserStruct struct)
    {
        this.struct = struct;
    }

    /**
     * Returns a hash code that represents this user for hashing.
     * @return hash code based on the user name
     */
    public int hashCode()
    {
        return getUserId().hashCode();
    }

    /**
     * Returns a String representation of this user.
     * @return user full name and login name
     */
    public String toString()
    {
        StringBuffer string = new StringBuffer(getFullName().length() + getUserId().length() + 3);
        string.append(getFullName()).append(" (").append(getUserId()).append(')');
        return string.toString();
    }

     /**
      * Determines if passed UserStructModel is equal based on login name.
      */
    public boolean equals(Object otherUserModel)
    {
        if(this == otherUserModel)
        {
            return true;
        }
        else if(otherUserModel == null)
        {
            return false;
        }
        else if(getClass() == otherUserModel.getClass())
        {
            UserStructModel castedObj = (UserStructModel)otherUserModel;

            if(getUserStruct() == castedObj.getUserStruct())
            {
                return true;
            }
            else
            {
                return getUserId().equals(castedObj.getUserId());
            }
        }
        else
        {
            return false;
        }
    }

    public ExchangeAcronym getExchangeAcronym()
    {
        if(exchangeAcronym == null)
        {
            exchangeAcronym = ExchangeAcronymFactory.createExchangeAcronym(getUserStruct().userAcronym);
        }
        return exchangeAcronym;
    }

    public ExchangeFirm getExchangeFirm()
    {
        if(exchangeFirm == null)
        {
            exchangeFirm = ExchangeFirmFactory.createExchangeFirm(getUserStruct().firm);
        }
        return exchangeFirm;
    }

    /**
     * Gets the user Id that represents who the user logged in as.
     * @return user Id used to log in
     */
    public String getUserId()
    {
        return getUserStruct().userId;
    }

    /**
     * Gets the users full name.
     * @return full name
     */
    public String getFullName()
    {
        return getUserStruct().fullName;
    }

    /**
     * Gets the list of executing or give up firms for this user.
     * @return array of Strings identifying firm labels
     */
    public ExchangeFirm[] getExecutingGiveUpFirms()
    {

		if(executingGiveUpFirms == null)
		{
			 executingGiveUpFirms = ExchangeFirmFactory.createExchangeFirms(getUserStruct().executingGiveupFirms);
		}
        return executingGiveUpFirms;
    }

    /**
     * Gets the firm for this user.
     * @return firm for this user.
     */
    public String getFirm()
    {
		if(firm == null)
		{
			firm = getUserStruct().firm.exchange+"."+getUserStruct().firm.firmNumber;
		}
        return firm;
    }

    /**
     * Gets the Role that this user plays.
     * @return Role identifier from com.cboe.idl.cmiConstants.UserRoles
     */
    public Role getRole()
    {
        return RoleFactory.getByChar(getUserStruct().role);

        // ETH HACK - role is based on the second char of the userName if the first char is '&'.
        // if not a valid letter then MarketMaker role is set
//        char result = getUserStruct().role;
//
//        if(getUserStruct().userId.charAt(0) == '&')
//        {
//            String roles = String.valueOf(UserRoles.CLASS_DISPLAY) + UserRoles.FIRM_DISPLAY +
//                           UserRoles.BROKER_DEALER + UserRoles.CUSTOMER_BROKER_DEALER +
//                           UserRoles.DPM_ROLE + UserRoles.FIRM + UserRoles.HELP_DESK +
//                           UserRoles.MARKET_MAKER + UserRoles.UNKNOWN_ROLE +
//                           UserRoles.CLASS_DISPLAY + UserRoles.PRODUCT_MAINTENANCE +
//                           UserRoles.TFL_ROLE + UserRoles.EXPECTED_OPENING_PRICE_ROLE;
//
//            result = getUserStruct().userId.charAt(1);
//
//            if(roles.indexOf(result) == -1)
//            {
//                result =  UserRoles.MARKET_MAKER;
//            }
//        }
//        return result;
        // end ETH hack
    }

    /**
     * Gets the ProfileStruct that is the default profile for this user.
     * @return default ProfileStruct
     */
    public ProfileModel getDefaultProfile()
    {
		if(defaultProfile == null)
		{
			 defaultProfile = ProfileModelFactory.createMutableProfile(getUserStruct().defaultProfile);
		}
        return defaultProfile;
    }

    /**
     * Gets the default ProfileModel for the tradingSession, if it exists.  If the user doesn't have
     * a default for the tradingSession, then null is returned.
     * @param tradingSession
     * @return default ProfileModel for tradingSession
     */
    public ProfileModel getDefaultProfileForSession(String tradingSession)
    {
        ProfileModel retVal = null;

        int classKey = APIHome.findProductQueryAPI().getDefaultSessionProductClass(tradingSession).getClassKey();
        ProfileModel[] profiles = getProfiles();
        for(int i=0; i<profiles.length; i++)
        {
            if(profiles[i].getSessionName().equals(tradingSession) &&
                    profiles[i].getProductClass().getClassKey() == classKey)
            {
                retVal = profiles[i];
                if(GUILoggerHome.find().isDebugOn() &&
                        GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.USER_SESSION))
                {
                    GUILoggerHome.find().debug(this.getClass().getName()+".getDefaultProfileForSession("+tradingSession+")",GUILoggerBusinessProperty.USER_SESSION,
                            profiles[i].getProfileStruct());
                }
                break;
            }
        }
        if(retVal == null && GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(this.getClass().getName()+".getDefaultProfileForSession()",GUILoggerBusinessProperty.USER_SESSION,
                    "No default profile found for session '"+tradingSession+"'");
        }
        return retVal;
    }

    /**
     * Gets the profiles that this user contains.
     * @return array of ProfileStruct's that this user contains.
     */
    public ProfileModel[] getProfiles()
    {
		if( profileModels == null)
		{
            SessionProfileUserStruct user = getUserStruct();
            profileModels = new ProfileModel[user.defaultSessionProfiles.length + user.sessionProfilesByClass.length];

            ProfileModel[] defaultSessionModels = null;

            if( user.defaultSessionProfiles.length > 0 )
            {
                defaultSessionModels = ProfileModelFactory.createMutableProfiles(
                        getUserStruct().defaultSessionProfiles);
                if( defaultSessionModels != null && defaultSessionModels.length > 0 )
                {
                    System.arraycopy(defaultSessionModels, 0, profileModels, 0, defaultSessionModels.length);
                }
            }

            if( user.sessionProfilesByClass.length > 0 )
            {
                ProfileModel[] sessionClassModels = ProfileModelFactory.createMutableProfiles(
                        getUserStruct().sessionProfilesByClass);
                if( sessionClassModels != null && sessionClassModels.length > 0 )
                {
                    int destPos = defaultSessionModels == null ? 0 : defaultSessionModels.length;
                    System.arraycopy(sessionClassModels, 0, profileModels, destPos, sessionClassModels.length);
                }
            }
		}

        return profileModels;
    }

    /**
     * Gets the users assigned classes.
     * @return array of class keys that represent the classes this user is assigned.
     */
    public int[] getClassKeys()
    {
        return getUserStruct().assignedClasses;
    }

    /**
     * Gets the users assigned classes.
     * @return array of ProductClass's this user is assigned.
     */
    public ProductClass[] getProductClasses()
    {
		if( productClasses == null)
		{
			int[] classKeys = getClassKeys();
			productClasses = new ProductClass[classKeys.length];

			for(int i = 0; i < classKeys.length; i++)
			{
				try
				{
					productClasses[i] = APIHome.findProductQueryAPI().getProductClassByKey(classKeys[i]);
				}
				catch(UserException e)
				{
					DefaultExceptionHandlerHome.find().process(e);
				}
			}
		}

        return productClasses;
    }

    /**
     * Determines if this user contains a class.
	 *
	 *
	 *
	 * ****** As a side effect of calling this method
     * ****** the class keys in the struct will be sorted.
	 *
	 * Nick DePasquale 1/4/02
	 * This side effect has been removed.
	 * It is not safe since the getClassKeys method returns the same instance to all callers
	 * Therefore when sorting, anyone nwho had a reference to it and may have been iterating would be screwed....
	 *
	 *
     * @param classKey to check for
     * @return 0 or greater represents that it was found and the number is the location in the array
     * of contained classes. Less than zero represents it was not found.
     */
    public int containsClassKey(int classKey)
    {
//        Arrays.sort(getClassKeys());
//        return Arrays.binarySearch(getClassKeys(), classKey);
		  return Arrays.binarySearch(getSotedClassKeys(), classKey);
    }

	public int [] getSotedClassKeys()
    {
        if(sortedClassKeys == null)
        {
            int [] list = getClassKeys();
			if(list != null)
			{
				sortedClassKeys = new int[list.length];
				for(int x = 0; x < list.length; x++)
				{
					sortedClassKeys[x] = list[x];
				}
				Arrays.sort(sortedClassKeys);
			}
        }
		return sortedClassKeys;
    }

    /**
     * Gets the DPM's that this user is assigned to.
     * @return an array of DPMStructModel's that this user is assigned to.
     */
    public DPMModel[] getAssignedDPMs()
    {
        DPMModel[] models = new DPMStructModel[getUserStruct().dpms.length];

        for(int i = 0; i < models.length; i++)
        {
            models[i] = new DPMStructModel(getUserStruct().dpms[i]);
        }

        return models;
    }

    /**
     * Determines if this user is assigned to a DPM.
     * @param dpm to check for
     * @return 0 or greater represents that it was found and the number is the location in the array
     * of assigned DPM's. Less than zero represents it was not found.
     */
    public int containsDPM(DPMModel dpm)
    {
        DPMModel[] dpms = getAssignedDPMs();
        for(int i = 0; i < dpms.length; i++)
        {
            if(dpms[i].equals(dpm))
            {
                return i;
            }
        }
        return -1;
    }

    /**
     * Gets the accounts this user belongs to.
     * @return accounts
     */
    public Account[] getAccounts()
    {

		if(accounts == null)
		{
			accounts = AccountFactory.createAccounts(getUserStruct().accounts);
		}
        return accounts;
    }

    /**
     * Gets the UserStruct that is contained by this model.
     * @return UserStruct
     */
    public SessionProfileUserStruct getUserStruct()
    {
        return struct;
    }

    /** Returns the UserStruct that backs the implemented model class */
    public void setUserStruct(SessionProfileUserStruct userStruct)
    {
        this.struct = userStruct;
    }
}