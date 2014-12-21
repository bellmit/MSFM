//
// -----------------------------------------------------------------------------------
// Source file: ProfileFactory.java
//
// PACKAGE: com.cboe.presentation.user;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.user;

import java.util.*;

import org.omg.CORBA.UserException;

import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.idl.cmiUser.SessionProfileStruct;

import com.cboe.interfaces.presentation.user.Profile;
import com.cboe.interfaces.presentation.product.SessionProductClass;

import com.cboe.presentation.api.APIHome;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;

public class ProfileFactory
{
    static public final SessionProfileStruct createDefaultProfileStruct()
    {
        SessionProductClass defaultProductClass = APIHome.findProductQueryAPI().getDefaultSessionProductClass();

        SessionProfileStruct struct = new SessionProfileStruct();
        struct.account = "";
        struct.classKey = defaultProductClass.getClassKey();
        struct.executingGiveupFirm = new ExchangeFirmStruct("", "");
        struct.sessionName = defaultProductClass.getTradingSessionName();
        struct.subAccount = "";
        struct.isAccountBlanked = false;
        struct.originCode = ' ';
        return struct;
    }

    static public final Profile createProfile ( SessionProfileStruct profileStruct )
    {
        ProfileModelImpl result = null;

        try
        {
            result = new ProfileModelImpl(profileStruct);
        }
        catch(UserException e)
        {
            DefaultExceptionHandlerHome.find ().process (e);
        }

        return result;
    }

    static public final Profile[] createProfiles ( SessionProfileStruct[] profileStructs )
    {
        ArrayList<Profile> profileList = new ArrayList<Profile>();

        if ( profileStructs != null )
        {
            int length = profileStructs.length;

            for ( int x = 0 ; x < length ; x++ )
            {
                Profile profile = createProfile( profileStructs[x] );
                // only add the profile to the ArrayList if it's not null
                if(profile != null)
                {
                    profileList.add(profile);
                }
            }
        }

        // the array of Profiles will not be null (could be zero size) and will not contain any nulls
        Profile[] profileArray = new ProfileModelImpl[profileList.size()];
        if(profileList.size() > 0)
        {
            profileArray = profileList.toArray(new ProfileModelImpl[0]);
        }
        
        return profileArray;
    }
}