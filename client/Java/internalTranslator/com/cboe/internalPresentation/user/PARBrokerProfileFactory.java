//
// -----------------------------------------------------------------------------------
// Source file: PARBrokerProfileFactory.java
//
// PACKAGE: com.cboe.internalPresentation.user
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2011 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.user;

import com.cboe.interfaces.internalPresentation.user.PARBrokerProfile;
import com.cboe.idl.cmiUser.PreferenceStruct;

public interface PARBrokerProfileFactory
{
    /**
     * This constructor will not attempt to initialize by loading the user's
     * prefs from the server; it will create a new PreferenceCollection with
     * the specified version.
     */
    PARBrokerProfile create(String userID, String version);

    PARBrokerProfile create(String userID, String version, PreferenceStruct[] prefs);
}
