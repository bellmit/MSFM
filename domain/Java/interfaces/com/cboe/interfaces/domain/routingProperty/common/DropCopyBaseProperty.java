//
// -----------------------------------------------------------------------------------
// Source file: DropCopyBaseProperty.java
//
// PACKAGE: com.cboe.interfaces.domain.routingProperty.common
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.domain.routingProperty.common;

import com.cboe.interfaces.domain.routingProperty.BaseProperty;

import com.cboe.interfaces.domain.routingProperty.common.DropCopyList;

public interface DropCopyBaseProperty extends BaseProperty
{
    DropCopyList getDropCopyListValue();

    void setDropCopyListValue(DropCopyList destinations);

    Destination getDirectRoute();
    Destination getFillDropCopy();
    Destination getCancelDropCopy();
    void setDirectRoute(Destination dest);
    void setFillDropCopy(Destination dest);
    void setCancelDropCopy(Destination dest);
    void setDirectRouteOptional(boolean optional);
    void setFillDropCopyOptional(boolean optional);
    void setCancelDropCopyOptional(boolean optional);
    boolean isDirectRouteOptional();
    boolean isFillDropCopyOptional();
    boolean isCancelDropCopyOptional();
}