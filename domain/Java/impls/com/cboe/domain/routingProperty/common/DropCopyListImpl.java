//
// -----------------------------------------------------------------------------------
// Source file: DropCopyList.java
//
// PACKAGE: com.cboe.domain.routingProperty.common
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.routingProperty.common;

import com.cboe.interfaces.domain.routingProperty.common.Destination;
import com.cboe.interfaces.domain.routingProperty.common.DropCopyList;

public class DropCopyListImpl implements DropCopyList
{
    private Destination[] dropCopyList = new Destination[NUM_DESTINATIONS];
    private boolean[] isOptional = new boolean[NUM_DESTINATIONS];

    public DropCopyListImpl()
    {
        this(NO_DEST, NO_DEST, NO_DEST);
    }

    public DropCopyListImpl(Destination directRoute, Destination fillDropCopy, Destination cancelDropCopy)
    {
        setDirectRoute(directRoute);
        setFillDropCopy(fillDropCopy);
        setCancelDropCopy(cancelDropCopy);
        setDirectRouteOptional(false);
        setFillDropCopyOptional(true);
        setCancelDropCopyOptional(true);
    }

    public DropCopyListImpl(String directRoute, String fillDropCopy, String cancelDropCopy)
    {
        this (new DestinationImpl(directRoute), new DestinationImpl(fillDropCopy), new DestinationImpl(cancelDropCopy));
    }

    public Destination getDirectRoute()
    {
        return dropCopyList[DIRECT_ROUTE_INDEX];
    }

    public void setDirectRoute(Destination dest)
    {
        dropCopyList[DIRECT_ROUTE_INDEX] = dest;
    }

    public Destination getFillDropCopy()
    {
        return dropCopyList[FILL_DROP_COPY_INDEX];
    }

    public void setFillDropCopy(Destination dest)
    {
        dropCopyList[FILL_DROP_COPY_INDEX] = dest;
    }

    public Destination getCancelDropCopy()
    {
        return dropCopyList[CANCEL_DROP_COPY_INDEX];
    }

    public void setCancelDropCopy(Destination dest)
    {
        dropCopyList[CANCEL_DROP_COPY_INDEX] = dest;
    }

    public Destination[] getDestinations()
    {
        return dropCopyList;
    }

    public void setDestinations(Destination[] list)
    {
        dropCopyList = list;
    }

    public boolean isDirectRouteOptional()
    {
        return isOptional[DIRECT_ROUTE_INDEX];
    }

    public void setDirectRouteOptional(boolean optional)
    {
        isOptional[DIRECT_ROUTE_INDEX] = optional;
    }

    public boolean isFillDropCopyOptional()
    {
        return isOptional[FILL_DROP_COPY_INDEX];
    }

    public void setFillDropCopyOptional(boolean optional)
    {
        isOptional[FILL_DROP_COPY_INDEX] = optional;
    }

    public boolean isCancelDropCopyOptional()
    {
        return isOptional[CANCEL_DROP_COPY_INDEX];
    }

    public void setCancelDropCopyOptional(boolean optional)
    {
        isOptional[CANCEL_DROP_COPY_INDEX] = optional;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer(100);
        sb.append("[");
        sb.append("Direct Route=").append(getDirectRoute().getWorkstation());
        sb.append("]");
        sb.append(",[");
        sb.append("Fill Drop Copy=").append(getFillDropCopy().getWorkstation());
        sb.append("]");
        sb.append(",[");
        sb.append("Cancel Drop Copy=").append(getCancelDropCopy().getWorkstation());
        sb.append("]");
        return sb.toString();
    }

    public int compareTo(DropCopyList otherList)
    {
        return toString().compareTo(otherList.toString());
    }

    public Object clone() throws CloneNotSupportedException
    {
        DropCopyList list = new DropCopyListImpl(new DestinationImpl(this.getDirectRoute().getWorkstation()),
             new DestinationImpl(this.getFillDropCopy().getWorkstation()),
             new DestinationImpl(this.getCancelDropCopy().getWorkstation()));
        return list;
    }
}
