// -----------------------------------------------------------------------------------
// Source file: AuctionTypeContainer
//
// PACKAGE: com.cboe.presentation.auction
// 
// Created: Dec 22, 2004 10:54:15 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.auction;

public class AuctionTypeContainer
{
    private String sessionName;
    private int key;
    private String displayString;
    private short type;

    public AuctionTypeContainer(String sessionName, int key, short type)
    {
        this.sessionName = sessionName;
        this.key = key;
        this.displayString = null;
        this.type = type;
    }

    public short getType()
    {
        return type;
    }

    public String getSessionName()
    {
        return sessionName;
    }

    public int getKey()
    {
        return key;
    }

    public int hashCode()
    {
        return key + sessionName.hashCode() + type;
    }

    public boolean equals(Object obj)
    {
        if ((obj != null) && (obj instanceof AuctionTypeContainer))
        {
            AuctionTypeContainer otherContainer = (AuctionTypeContainer) obj;

            String sessionName = otherContainer.getSessionName();
            int otherKey = otherContainer.getKey();
            short otherType = otherContainer.getType();

            if (this.key == otherKey &&
                this.sessionName.equals(sessionName) &&
                this.type == otherType)
            {
                return true;
            }
        }

        return false;
    }

    public String toString()
    {
        if (displayString == null)
        {
            StringBuffer buffer = new StringBuffer(45);
            buffer.append(sessionName);
            buffer.append('-');
            buffer.append(key);
            buffer.append(':');
            buffer.append(type);

            displayString = buffer.toString();
        }

        return displayString;
    }
}