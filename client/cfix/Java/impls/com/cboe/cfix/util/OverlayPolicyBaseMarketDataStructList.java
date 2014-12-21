package com.cboe.cfix.util;

/**
 * OverlayPolicyBaseMarketDataStructList.java
 *
 * @author Dmitry Volpyansky
 */

import com.cboe.client.util.*;

public abstract class OverlayPolicyBaseMarketDataStructList implements HasSizeIF
{
    protected String mdReqID;
    private int hashCode;

    public static final BitArrayIF NeverOverlaidBitArray = new BitArray()
    {
        public void set(int bitIndex)             {}
        public void clear(int bitIndex)           {}
        public void copyFrom(BitArrayIF bitArray) {}
    };

    public OverlayPolicyBaseMarketDataStructList(String mdReqID)
    {
        this.mdReqID = mdReqID;
        this.hashCode = mdReqID.hashCode();
    }

    public String getMdReqID()
    {
        return mdReqID;
    }

    public int hashCode()
    {
        return hashCode;
    }

    public String toString()
    {
        String className = ClassHelper.getClassNameFinalPortion(this);
        StringBuilder sb = new StringBuilder(className.length()+mdReqID.length()+20);
        sb.append(className).append("{").append(mdReqID).append("} size(").append(size()).append(")");
        return sb.toString();
    }
}
