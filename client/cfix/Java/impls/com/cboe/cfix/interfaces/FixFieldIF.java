package com.cboe.cfix.interfaces;

/**
 * FixFieldIF.java
 *
 * @author Dmitry Volpyansky
 *
 */

public interface FixFieldIF extends FixMessageWriterVisitorIF
{
    public static final String FIX_YES                    = "Y";
    public static final byte   FIX_YESbyte                = (byte) 'Y';
    public static final char   FIX_YESchar                = 'Y';
    public static final String FIX_NO                     = "N";
    public static final byte   FIX_NObyte                 = (byte) 'N';
    public static final char   FIX_NOchar                 = 'N';
    public static final String EQUALS                     = "=";
    public static final byte   EQUALSbyte                 = (byte) '=';
    public static final char   EQUALSchar                 = '=';
    public static final char   SOHchar                    = '\001';
    public static final byte   SOHbyte                    = (byte) '\001';
    public static final String SOH                        = "\001";
    public static final char   ZEROchar                   = '0';
    public static final char   ONEchar                    = '1';

    public static final int    FIX_SOH_LENGTH             = 1;                                                   // ^
    public static final int    FIX_TAG_8_LENGTH           = 10;                                                  // 8=FIX.4.2^
    public static final int    FIX_TAG_10_LENGTH          = 7;                                                   // 10=000^
    public static final int    FIX_TAG_9_EMPTY_LENGTH     = 3;                                                   // 9=^
    public static final int    FIX_TAG_9_OUTGOING_DIGITS  = 4;                                                   // 0000  -- If you ever change this, look for all places where it is used and see if any comments exist with updated arithmetic
    public static final int    FIX_TAG_9_OUTGOING_LENGTH  = FIX_TAG_9_OUTGOING_DIGITS +  FIX_TAG_9_EMPTY_LENGTH; // 9=0000^
    public static final int    FIX_TAG_35_OUTGOING_OFFSET = FIX_TAG_8_LENGTH + FIX_TAG_9_OUTGOING_LENGTH;

    public int     getTag();
    public String  getTagAsString();
    public String  getTagName();
    public boolean hasValue();
    public String  getValue();
    public String  getValueDescription();
}
