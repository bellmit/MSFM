// -----------------------------------------------------------------------------------
// Source file: CASImpl.java
//
// PACKAGE: com.cboe.presentation.casMonitor
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.casMonitor;

import com.cboe.interfaces.casMonitor.CASModel;
import com.cboe.interfaces.casMonitor.CAS;
import com.cboe.interfaces.casMonitor.CASInformation;
import com.cboe.interfaces.presentation.common.businessModels.MutableBusinessModel;
import com.cboe.interfaces.presentation.processes.CBOEProcess;

import com.cboe.presentation.common.businessModels.AbstractMutableBusinessModel;

import java.util.Properties;

public class CASImpl extends AbstractMutableBusinessModel implements CASModel
{
    private String orbName;
    private String firmName;
    private Properties commandLineArgs;
    private String XMLConfiguration;

    private String hardwareLocation;
    private String hardwareManufacturer;
    private String hardwareModel;
    private String ipAddress;
    private String networkConnectivityProvider;
    private String softwareVendor;
    private String frontEnd;

    protected CASImpl()
    {
        super();
        initialize();
    }

    protected CASImpl(CAS cas)
    {
        super();
        initializeFromCAS(cas);
    }

    protected CASImpl(CBOEProcess process)
    {
        if(!process.isCAS())
        {
            throw new IllegalArgumentException("process must be a valid CAS type. process.isCAS() must return true.");
        }
        initialize();
        this.orbName = process.getOrbName();
    }

    protected CASImpl(CBOEProcess process, CASInformation casInfo)
    {
        this(process);
        this.orbName = process.getOrbName();
        initializeCASInformation(casInfo);
    }

    protected CASImpl(String processName, String orbName, String hostName, int port, short onlineStatus,
                      short poaStatus, String clusterName)
    {
/*
        super(processName, orbName, hostName, port, onlineStatus, poaStatus, clusterName);
*/
        this.orbName = orbName;
        if( !isCAS() )
        {
            throw new IllegalArgumentException("process must be a valid CAS type. isCAS() must return true.");
        }
        initialize();
    }

    protected CASImpl(String processName, String orbName, String hostName, int port, short onlineStatus,
                      short poaStatus, String clusterName, CAS cas)
    {
/*
        super(processName, orbName, hostName, port, onlineStatus, poaStatus, clusterName);
*/
        if( !isCAS() )
        {
            throw new IllegalArgumentException("process must be a valid CAS type. isCAS() must return true.");
        }
        initializeFromCAS(cas);
    }

    protected void initialize()
    {
        this.firmName = "";
        this.hardwareLocation = "";
        this.hardwareManufacturer = "";
        this.hardwareModel = "";
        this.ipAddress = "";
        this.networkConnectivityProvider = "";
        this.softwareVendor = "";
        this.frontEnd = "";
    }

    private void initializeFromCAS(CAS cas)
    {
        this.orbName = cas.getOrbName();
        this.firmName = cas.getFirmName();

        this.hardwareLocation = cas.getHardwareLocation();
        this.hardwareManufacturer = cas.getHardwareManufacturer();
        this.hardwareModel = cas.getHardwareModel();
        this.networkConnectivityProvider = cas.getNetworkConnectivityProvider();
        this.softwareVendor = cas.getSoftwareVendor();
        this.frontEnd = cas.getFrontEnd();
        setIpAddress(cas.getIpAddress());
    }
    private void initializeCASInformation(CASInformation casInformation)
    {
        this.firmName = casInformation.getFirm();
        this.hardwareLocation = casInformation.getHardwareLocation();
        this.hardwareManufacturer = casInformation.getHardwareManufacturer();
        this.hardwareModel = casInformation.getHardwareModel();
        this.networkConnectivityProvider = casInformation.getNetworkConnectivityProvider();
        this.softwareVendor = casInformation.getSoftwareProvider();
        this.frontEnd = casInformation.getFrontEnd();
        setIpAddress(casInformation.getIpAddress());
    }


    public boolean isCAS()
    {
        return true;
    }

    public String getOrbName()
    {
        return orbName;
    }
    /**
     * Returns Firm name for this CAS.
     * @return firm name String
     */
    public String getFirmName()
    {
        return this.firmName;
    }


    /**
     * Returns command line arguments for this CAS.
     * @return commanfLineArgs Properties
     */
    public Properties getCommandLineArgs()
    {
        return this.commandLineArgs;
    }

    /**
     * Returns XML configuration for this CAS as a String.
     * @return configuration String
     */
    public String getXMLConfiguration()
    {
        return this.XMLConfiguration;
    }

    /**
     * Sets Firm name for this CAS.
     */
    public void setFirmName(String firmName)
    {
        if( (firmName == null && getFirmName() != null) ||
                (firmName != null && getFirmName() == null) ||
                (!firmName.equals(getFirmName())) )
        {
            String oldValue = getFirmName();
            this.firmName = firmName;
            setModified(true);
            firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldValue, firmName);
        }
    }


    /**
     * Implements Cloneable
     */
    public Object clone() throws CloneNotSupportedException
    {
        CASModel clonedObject = CASFactory.createCASModel(this);
        return clonedObject;
    }

    public String getHardwareLocation()
    {
        return hardwareLocation;
    }

    public void setHardwareLocation(String hardwareLocation)
    {
        this.hardwareLocation = hardwareLocation;
    }

    public String getHardwareManufacturer()
    {
        return hardwareManufacturer;
    }

    public void setHardwareManufacturer(String hardwareManufacturer)
    {
        this.hardwareManufacturer = hardwareManufacturer;
    }

    public String getHardwareModel()
    {
        return hardwareModel;
    }

    public void setHardwareModel(String hardwareModel)
    {
        this.hardwareModel = hardwareModel;
    }

    public String getIpAddress()
    {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress)
    {
        this.ipAddress = ipAddress;
    }

    public String getNetworkConnectivityProvider()
    {
        return networkConnectivityProvider;
    }

    public void setNetworkConnectivityProvider(String networkConnectivityProvider)
    {
        this.networkConnectivityProvider = networkConnectivityProvider;
    }

    public String getSoftwareVendor()
    {
        return softwareVendor;
    }

    public void setSoftwareVendor(String softwareVendor)
    {
        this.softwareVendor = softwareVendor;
    }

    public String getFrontEnd()
    {
        return frontEnd;
    }

    public void setFrontEnd(String frontEnd)
    {
        this.frontEnd = frontEnd;
    }
}
