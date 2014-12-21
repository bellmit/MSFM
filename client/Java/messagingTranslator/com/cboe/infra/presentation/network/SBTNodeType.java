package com.cboe.infra.presentation.network;

import java.io.Serializable;

import java.util.List;
import java.util.Arrays;
import java.util.Comparator;

//
// -----------------------------------------------------------------------------------
// Source file: ${FILE_NAME}
//
// PACKAGE: com.cboe.infra.presentation.network;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

/**
 * This class enumerates all valid SBTNode types
 */
public class SBTNodeType implements Serializable
{
    private static int nodeId = 0;
    /**
     * Node type = UNDEFINED (Uninitialized)
     */
    public static final SBTNodeType UNDEFINED_TYPE = new SBTNodeType(-1, "Undefined");
    /**
     * Node type = Distribution Node
     */
    public static final SBTNodeType DN_NODE = new SBTNodeType(++nodeId, "Distribution Node");
    /**
     * NOde type = General Cluster or Business Cluster
     */
    public static final SBTNodeType OR_NODE = new SBTNodeType(nodeId *= 2, "GC Or BC Node");
    /**
     * Node type = Front End
     */
    public static final SBTNodeType FE_NODE = new SBTNodeType(nodeId *= 2, "Front End");
    /**
     * Node type = Client Application Server
     */
    public static final SBTNodeType CAS_NODE = new SBTNodeType(nodeId *= 2, "CAS");
    /**
     * Node type = Talarian RT Server and JMS Server
     */
    public static final SBTNodeType SERVER = new SBTNodeType(nodeId *= 2, "Server");
    /**
     * Node type = UNKNOWN (Error)
     */
    public static final SBTNodeType UNKNOWN_TYPE = new SBTNodeType(nodeId *= 2, "Unknown");
    /**
     * Server Network Connection
     */
    public static final SBTNodeType NETWORK_CONNECTION = new SBTNodeType(nodeId *= 2, "Network Connection");

    public static final SBTNodeType[] VALID_TYPES = new SBTNodeType[]
            {
                    DN_NODE,
                    OR_NODE,
                    FE_NODE,
                    CAS_NODE,
                    SERVER,
                    NETWORK_CONNECTION,
                    UNKNOWN_TYPE
            };

    public static final Comparator NAME_COMPARATOR = new Comparator()
    {
        public int compare(Object first, Object second)
        {
            int rv = 0;
            if (first instanceof SBTNodeType && second instanceof SBTNodeType)
            {
                rv = first.toString().compareTo(second.toString());
            }
            return rv;
        }
    };

    public static final List getValidTypes()
    {
        return Arrays.asList(VALID_TYPES);
    }

    public static SBTNodeType getNodeType(int id)
    {
        SBTNodeType rv = null;
        for (SBTNodeType type : VALID_TYPES)
        {
            if (type.intValue() == id)
            {
                rv = type;
                break;
            }
        }
        return rv;
    }

    private final int id;
    private final String displayName;

    private SBTNodeType(int value, String name)
    {
        id = value;
        displayName = name;
    }

    public String toString()
    {
        return displayName;
    }

    public final int intValue()
    {
        return id;
	}
}
