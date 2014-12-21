package com.cboe.cfix.fix.util;

/**
 * FixMarketDataRejectStruct.java
 *
 * @author Dmitry Volpyansky
 *
 */

import com.cboe.interfaces.cfix.*;

public class FixMarketDataRejectStruct implements CfixMarketDataRejectStruct
{
    public String  mdReqID;
    public char    rejectReason;
    public String  text;
    public String  targetCompID;

    public FixMarketDataRejectStruct(char rejectReason, String text, String mdReqID)
    {
        this.rejectReason = rejectReason;
        this.text         = text;
        this.mdReqID      = mdReqID;
    }

    public FixMarketDataRejectStruct(char rejectReason, String text, String mdReqID, String targetCompID)
    {
        this.rejectReason = rejectReason;
        this.text         = text;
        this.mdReqID      = mdReqID;
        this.targetCompID = targetCompID;
    }

    public String getMdReqID()
    {
        return mdReqID;
    }

    public char getRejectReason()
    {
        return rejectReason;
    }

    public String getText()
    {
        return text;
    }

    public String getTargetCompID()
    {
        return targetCompID;
    }
}
