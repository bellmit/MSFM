//
// ------------------------------------------------------------------------
// FILE: XmlBindingDataConversionUtility.java
// 
// PACKAGE: com.cboe.xmlParser
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
// 
// ------------------------------------------------------------------------
//

package com.cboe.xmlParser;

/**
 * @author torresl@cboe.com
 */
public class XmlBindingDataConversionUtility
{
    private XmlBindingDataConversionUtility()
    {
    }

    public static char getFirstChar(String text)
    {
        if(text != null && text.length()>0)
        {
            return text.charAt(0);
        }
        return (char)0;
    }

}
