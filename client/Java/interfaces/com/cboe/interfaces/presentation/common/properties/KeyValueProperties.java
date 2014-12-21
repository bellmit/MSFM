//
// -----------------------------------------------------------------------------------
// Source file: keyValueProperties.java
//
// PACKAGE: com.cboe.interfaces.presentation.common.properties;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.common.properties;

import java.util.*;

import com.cboe.idl.cmiUtil.KeyValueStruct;

public interface KeyValueProperties
{
    public Properties getProperties();

    public void addKeyValues(KeyValueStruct[] structs);

    public Object setKeyValue(KeyValueStruct struct);

    public Object removeKeyValue(KeyValueStruct struct);
}
