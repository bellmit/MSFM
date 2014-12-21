//
// ------------------------------------------------------------------------
// FILE: LinkageMechanisms.java
// 
// PACKAGE: com.cboe.presentation.common.formatters
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
// 
// ------------------------------------------------------------------------
//

package com.cboe.presentation.common.formatters;

/**
 * @author torresl@cboe.com
 */
public class LinkageMechanisms
{
    public static final short   NON_CBOE_LINKAGE_MECHANISM = com.cboe.idl.cmiConstants.LinkageMechanisms.NON_CBOE_LINKAGE_MECHANISM;

    private static final String NON_CBOE_LINKAGE_MECHANISM_STRING = "Non-CBOE Linkage Mechanism";

    public static final String  TRADERS_FORMAT = "TRADERS_FORMAT";
    public static final String  VALUE_FORMAT = "VALUE_FORMAT";

    public static final String  INVALID_FORMAT = "ERROR: Invalid Format Specifier";
    public static final String  INVALID_LINKAGE_TYPE = "ERROR: Invalid Linkage Type Code";

    private LinkageMechanisms()
    {
    }

    public static String toString(short linkageMechanism)
    {
        return toString(linkageMechanism, TRADERS_FORMAT);
    }
    public static String toString(short linkageMechanism, String formatSpecifier)
    {
        if (formatSpecifier.equals(TRADERS_FORMAT))
        {
            switch(linkageMechanism)
            {
                case NON_CBOE_LINKAGE_MECHANISM:
                    return NON_CBOE_LINKAGE_MECHANISM_STRING;
                default:
                {
                    StringBuilder buffer = new StringBuilder(20);
                    buffer.append(INVALID_LINKAGE_TYPE).append(' ').append(linkageMechanism);
                    return buffer.toString();
                }
            }
        }
        else if (formatSpecifier.equals(VALUE_FORMAT))
        {
            switch (linkageMechanism)
            {
                case NON_CBOE_LINKAGE_MECHANISM:
                    return NON_CBOE_LINKAGE_MECHANISM_STRING;
                default:
                    {
                        StringBuilder buffer = new StringBuilder(20);
                        buffer.append(INVALID_LINKAGE_TYPE).append(' ').append(linkageMechanism);
                        return buffer.toString();
                    }
            }
        }
        return INVALID_FORMAT;
    }
}
