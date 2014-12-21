//
// -----------------------------------------------------------------------------------
// Source file: ExampleProfile.java
//
// PACKAGE: com.cboe.presentation.exampleStructs;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.exampleStructs;

import com.cboe.idl.cmiUser.SessionProfileStruct;

import com.cboe.interfaces.presentation.user.Profile;
import com.cboe.interfaces.presentation.tradingSession.DefaultTradingSession;

import com.cboe.presentation.user.ProfileFactory;

public class ExampleProfile
{
    static final String defaultAccount = "AAA";
    static final String defaultSubAccoun = "SSS";
    static final boolean defaultIsAccountBlanked = false;
    public static final String DEFAULT_SESSION = DefaultTradingSession.DEFAULT;
    static final char defaultOrigin = ' ';

    public ExampleProfile ()
    {
        super ();
    }

    static public SessionProfileStruct getExampleDefaultProfileStruct ()
    {
        return new SessionProfileStruct ( ExampleClassStruct.getExampleClassStruct().classKey,
                                   defaultAccount,
                                   defaultSubAccoun,
                                   ExampleExchangeFirm.getExampleDefaultExchangeFirmStruct(),
                                   DEFAULT_SESSION,
                                   defaultIsAccountBlanked,
                                   defaultOrigin);
    }

    static public Profile getExampleDefaultProfile ()
    {
        return ProfileFactory.createProfile ( getExampleDefaultProfileStruct () );
    }

    static public Profile [] getExampleDefaultProfiles()
    {
        return ExampleProfile.getExampleDefaultProfiles();
    }
}
