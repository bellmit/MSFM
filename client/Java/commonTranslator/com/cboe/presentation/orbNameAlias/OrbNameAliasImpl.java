//
//
// -----------------------------------------------------------------------------------
// Source file: OrbNameAlias
//
// PACKAGE: com.cboe.presentation.OrbNameAlias;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.orbNameAlias;

import com.cboe.interfaces.presentation.common.businessModels.MutableBusinessModel;
import com.cboe.interfaces.presentation.processes.OrbNameAliasModel;
import com.cboe.presentation.common.businessModels.AbstractMutableBusinessModel;

public class OrbNameAliasImpl extends AbstractMutableBusinessModel implements OrbNameAliasModel
{
    protected String orbName;
    protected String displayName;
    protected String cluster;
    protected String subCluster;

    protected String clusterSubClusterName;

    public OrbNameAliasImpl(String orbName)
    {
        this.orbName = orbName;
    }
    public OrbNameAliasImpl(String orbName, String displayName, String cluster, String subCluster)
    {
        this.orbName = orbName;
        this.displayName = displayName;
        this.cluster = cluster;
        this.subCluster = subCluster;
    }

    public String getOrbName()
    {
        return orbName;
    }
    public void setOrbName(String orbName)
    {
        this.orbName = orbName;
    }

    public String getDisplayName()
    {
        return displayName;
    }
    public void setDisplayName(String displayName)
    {
        if (this.displayName != displayName)
        {
            String oldValue = this.displayName;
            this.displayName = displayName;
            setModified(true);
            firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldValue, displayName);
        }
    }

    public String getCluster()
    {
        return cluster;
    }
    public void setCluster(String cluster)
    {
        if (this.cluster != cluster)
        {
            String oldValue = this.cluster;
            this.cluster = cluster;
            setModified(true);
            firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldValue, cluster);
        }
        this.clusterSubClusterName = null;
    }

    public String getSubCluster()
    {
        return subCluster;
    }
    public void setSubCluster(String subCluster)
    {
        if (this.subCluster != subCluster)
        {
            String oldValue = this.subCluster;
            this.subCluster = subCluster;
            setModified(true);
            firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldValue, subCluster);
        }
        this.clusterSubClusterName = null;
    }

    public String getClusterSubClusterName()
    {
        if (clusterSubClusterName == null)
        {
            if (subCluster == null || subCluster.equals(""))
            {
                clusterSubClusterName = cluster;    
            }
            else
            {
                clusterSubClusterName = cluster + ":" + subCluster;
            }
        }

        return clusterSubClusterName;
    }

    public Object clone() throws CloneNotSupportedException
    {
        OrbNameAliasImpl impl = new OrbNameAliasImpl(orbName);
        impl.cluster = cluster;
        impl.subCluster = subCluster;
        impl.displayName = displayName;
        return impl;
    }

    public Object getKey()
    {
        return orbName;
    }
}

