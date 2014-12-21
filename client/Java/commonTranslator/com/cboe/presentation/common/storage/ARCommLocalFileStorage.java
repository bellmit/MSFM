//
// -----------------------------------------------------------------------------------
// Source file: ARCommLocalFileStorage.java
//
// PACKAGE: com.cboe.presentation.common.storage
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.storage;

public class ARCommLocalFileStorage extends LocalFileStorage
{
    protected String getSaveAllowedPropertyKey()
    {
        return ARCommFtpFileStorage.AR_SAVE_ALLOWED_PROPERTY_KEY;
    }
}
