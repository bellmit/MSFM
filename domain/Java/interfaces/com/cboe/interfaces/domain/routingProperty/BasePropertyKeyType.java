package com.cboe.interfaces.domain.routingProperty;
// -----------------------------------------------------------------------------------
// Source file: BasePropertyKeyType
//
// PACKAGE: com.cboe.domain.firmRoutingProperty2.test2.key
// 
// Created: Jul 24, 2006 10:37:33 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import java.lang.reflect.Constructor;

public interface BasePropertyKeyType
{
    String getName();

    Class getClassType();

    Constructor getDefaultKeyConstructor();

    Constructor getPropertyKeyContructor();

    Constructor getPropertyTypeContructor();
}
