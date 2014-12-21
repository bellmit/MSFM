//
//
// -----------------------------------------------------------------------------------
// Source file: OrbNameAliasFactory
//
// PACKAGE: com.cboe.presentation.OrbNameAliasFactory;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.orbNameAlias;

import com.cboe.interfaces.presentation.processes.OrbNameAliasModel;


public class OrbNameAliasFactory 
{
    /**
     *  Create a new OrbNameAlias object with all the information.  This is not 
     *  put into the cache.
     */
    public static OrbNameAliasModel createOrbNameAlias(String orbName, String displayName, String cluster, String subCluster)
    {
        return new OrbNameAliasImpl(orbName,displayName,cluster,subCluster);
    }

    /**
     *  Create a new OrbNameAlias object with all just the orbname.  This is not 
     *  put into the cache.
     */
    public static OrbNameAliasModel createBlankOrbNameAlias(String orbName)
    {
        return new OrbNameAliasImpl(orbName);
    }
}

