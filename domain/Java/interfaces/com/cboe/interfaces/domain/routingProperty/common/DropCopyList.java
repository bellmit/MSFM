//
// -----------------------------------------------------------------------------------
// Source file: DropCopyList.java
//
// PACKAGE: com.cboe.interfaces.domain.routingProperty.common
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.domain.routingProperty.common;

public interface DropCopyList extends Cloneable
{
    public static final int DIRECT_ROUTE_INDEX = 0;
    public static final int FILL_DROP_COPY_INDEX = 1;
    public static final int CANCEL_DROP_COPY_INDEX = 2;
    public static final int NUM_DESTINATIONS = 3;
    public static final String NO_DEST = "";

    Destination getDirectRoute();
    void setDirectRoute(Destination dest);
    Destination getFillDropCopy();
    void setFillDropCopy(Destination dest);
    Destination getCancelDropCopy();
    void setCancelDropCopy(Destination dest);
    Destination[] getDestinations();
    void setDestinations(Destination[] list);
    boolean isDirectRouteOptional();
    void setDirectRouteOptional(boolean optional);
    boolean isFillDropCopyOptional();
    void setFillDropCopyOptional(boolean optional);
    boolean isCancelDropCopyOptional();
    void setCancelDropCopyOptional(boolean optional);
    int compareTo(DropCopyList otherList);
    Object clone() throws CloneNotSupportedException;
}