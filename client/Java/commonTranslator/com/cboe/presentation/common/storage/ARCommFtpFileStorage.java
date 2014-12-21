//
// -----------------------------------------------------------------------------------
// Source file: ARCommFtpFileStorage.java
//
// PACKAGE: com.cboe.presentation.common.storage
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.storage;

public class ARCommFtpFileStorage extends FtpFileStorage
{
    protected static final String AR_SAVE_ALLOWED_PROPERTY_KEY = "SaveAllowedARCommandFav";

    protected String getSaveAllowedPropertyKey()
    {
        return AR_SAVE_ALLOWED_PROPERTY_KEY;
    }
}
