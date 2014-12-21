//
// -----------------------------------------------------------------------------------
// Source file: ProfileModelFactory.java
//
// PACKAGE: com.cboe.presentation.user;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.user;

import com.cboe.presentation.user.ProfileFactory;
import com.cboe.interfaces.presentation.user.ProfileModel;
import com.cboe.interfaces.presentation.user.Profile;
import com.cboe.idl.cmiUser.SessionProfileStruct;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;

public class ProfileModelFactory extends ProfileFactory
{
    static public final ProfileModel createMutableProfile(SessionProfileStruct profileStruct)
    {
        return (ProfileModel)ProfileModelFactory.createProfile(profileStruct);
    }

    static public final ProfileModel[] createMutableProfiles(SessionProfileStruct[] profileStructs)
    {
        return (ProfileModel[])ProfileModelFactory.createProfiles(profileStructs);
    }

    static public final ProfileModel createMutableProfile(Profile aProfile)
    {
        ProfileModel retVal = null;
        if(aProfile instanceof ProfileModel)
        {
            try
            {
                retVal = (ProfileModel)aProfile.clone();
            }
            catch(CloneNotSupportedException e)
            {
                DefaultExceptionHandlerHome.find().process(e, "ProfileModelFactory can't clone ProfileModel");
            }
        }
        else
            retVal = (ProfileModel)ProfileModelFactory.createProfile(aProfile.getProfileStruct());

        return retVal;
    }
}
