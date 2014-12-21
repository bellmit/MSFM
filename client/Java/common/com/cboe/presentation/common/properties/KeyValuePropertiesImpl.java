//
// -----------------------------------------------------------------------------------
// Source file: keyValueProperties.java
//
// PACKAGE: com.cboe.presentation.common.properties
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.properties;

import java.util.*;

import com.cboe.idl.cmiUtil.KeyValueStruct;

import com.cboe.interfaces.presentation.common.properties.KeyValueProperties;

public class KeyValuePropertiesImpl extends Properties implements KeyValueProperties
{
    public KeyValuePropertiesImpl()
    {
        super();
    }

    public KeyValuePropertiesImpl(Properties defaults)
    {
        super(defaults);
    }

    public static KeyValueProperties createKeyValueProperties(KeyValueStruct[] structs)
    {
        KeyValueProperties newProperties = new KeyValuePropertiesImpl();
        newProperties.addKeyValues(structs);
        return newProperties;
    }

    public Properties getProperties()
    {
        return this;
    }

    public void addKeyValues(KeyValueStruct[] structs)
    {
        for( int i = 0; i < structs.length; i++ )
        {
            KeyValueStruct struct = structs[i];
            setProperty(struct.key, struct.value);
        }
    }

    public Object setKeyValue(KeyValueStruct struct)
    {
        return setProperty(struct.key, struct.value);
    }

    public Object removeKeyValue(KeyValueStruct struct)
    {
        return remove(struct.key);
    }
}
