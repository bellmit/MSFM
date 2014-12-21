//
// -----------------------------------------------------------------------------------
// Source file: XtpGuiUserPrefFtpFileStorage.java
//
// PACKAGE: com.cboe.presentation.common.storage
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.storage;

public class XtpGuiUserPrefFtpFileStorage extends FtpFileStorage {
    protected static final String XTP_GUI_USER_PREF_SAVE_ALLOWED_PROPERTY_KEY = "SaveAllowedXtpGuiUserPref";

    protected String getSaveAllowedPropertyKey()
    {
        return XTP_GUI_USER_PREF_SAVE_ALLOWED_PROPERTY_KEY;
    }
}
