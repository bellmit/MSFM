package com.cboe.testDrive;

import com.cboe.idl.cmi.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.application.test.*;
import com.cboe.testDrive.*;
import com.cboe.infrastructureServices.foundationFramework.*;

import java.io.PrintWriter;
import java.io.FileWriter;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;

public class MarketDataCounter
{
    private int classkey;
    private long numOfMessage = 0;

    public MarketDataCounter(int classKey)
    {
        this.classkey = classKey;
    }

    public void acceptMessage(int classKey)
    {
//        if (classKey != this.classkey) return;
        numOfMessage++;
    }

    public void acceptAllMessages(int numberOfMsg)
    {
        numOfMessage = numOfMessage + numberOfMsg;
    }

    public int getClassKey()
    {
        return this.classkey;
    }

    public long getCount()
    {
        return this.numOfMessage;
    }

    public void resetCounter()
    {
        this.numOfMessage = 0;
    }
}
