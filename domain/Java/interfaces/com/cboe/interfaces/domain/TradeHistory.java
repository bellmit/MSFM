package com.cboe.interfaces.domain;
public interface TradeHistory
{
    // Declare some constants for the entryType
    static final char ADD_NEW_EVENT = 'A'; // new entry created
    static final char BUST_EVENT = 'B';	// busted
    static final char REMOVE_EVENT = 'D'; // D for deleted
    static final char MODIFY_EVENT = 'M';	// M - History Entry for Trade Modification
    // Note that REMOVE_EVENT is treated separately even though
    // it is a state change, because it effectively removes the trade
    // from the system.

    public String getEntry();
    public long getEntryTime();
    public char getEntryType();
}
