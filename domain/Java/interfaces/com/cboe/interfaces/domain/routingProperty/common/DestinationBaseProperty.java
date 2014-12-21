//
// -----------------------------------------------------------------------------------
// Source file: DestinationBaseProperty.java
//
// PACKAGE: com.cboe.interfaces.domain.routingProperty.common
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.domain.routingProperty.common;

import com.cboe.interfaces.domain.routingProperty.BaseProperty;

public interface DestinationBaseProperty extends BaseProperty
{
    Destination getDestination();
    void setDestination(Destination destination);
}
