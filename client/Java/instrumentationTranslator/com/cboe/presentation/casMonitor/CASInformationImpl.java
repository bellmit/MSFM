//
// ------------------------------------------------------------------------
// FILE: CASInformationImpl.java
//
// PACKAGE: com.cboe.presentation.casMonitor
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2004 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//

package com.cboe.presentation.casMonitor;

import com.cboe.interfaces.casMonitor.CASInformation;
import com.cboe.client.xml.bind.GICASConfigurationType;

/**
 * @author torresl@cboe.com
 */
class CASInformationImpl implements CASInformation
{
    protected String casNumber;
    protected String firm;
    protected String hardwareManufacturer;
    protected String hardwareLocation;
    protected String hardwareModel;
    protected String ipAddress;
    protected String networkConnectivityProvider;
    protected String softwareProvider;
    protected String frontEnd;

    public CASInformationImpl()
    {
        super();
        initialize();
    }
    public CASInformationImpl(GICASConfigurationType casConfiguration)
    {
        this(casConfiguration.getCasNumber(),
             casConfiguration.getFirm(),
             casConfiguration.getHardwareManufacturer(),
             casConfiguration.getHardwareLocation(),
             casConfiguration.getHardwareModel(),
             casConfiguration.getIpAddress(),
             casConfiguration.getNetworkConnectivityProvider(),
             casConfiguration.getSoftwareProvider(),
             casConfiguration.getFrontEnd()
             );
    }
    public CASInformationImpl(
            String casNumber,
            String firm,
            String hardwareManufacturer,
            String hardwareLocation,
            String hardwareModel,
            String ipAddress,
            String networkConnectivityProvider,
            String softwareVendor,
            String productionFrontEnd
        )
    {
        this();
        initialize();
        this.casNumber = casNumber;
        this.firm = firm;
        this.hardwareManufacturer = hardwareManufacturer;
        this.hardwareLocation = hardwareLocation;
        this.hardwareModel = hardwareModel;
        this.ipAddress = ipAddress;
        this.networkConnectivityProvider = networkConnectivityProvider;
        this.softwareProvider = softwareVendor;
        if(productionFrontEnd != null) // optional parameter
        {
            this.frontEnd = productionFrontEnd;
        }
    }
    private void initialize()
    {
        casNumber = "";
        hardwareManufacturer = "";
        hardwareLocation = "";
        hardwareModel = "";
        ipAddress = "";
        networkConnectivityProvider = "";
        softwareProvider = "";
        frontEnd = "";
    }

    public String getCasNumber()
    {
        return casNumber;
    }

    public void setCasNumber(String casNumber)
    {
        this.casNumber = casNumber;
    }

    public String getHardwareManufacturer()
    {
        return hardwareManufacturer;
    }

    public void setHardwareManufacturer(String hardwareManufacturer)
    {
        this.hardwareManufacturer = hardwareManufacturer;
    }

    public String getHardwareLocation()
    {
        return hardwareLocation;
    }

    public void setHardwareLocation(String hardwareLocation)
    {
        this.hardwareLocation = hardwareLocation;
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

    public String getSoftwareProvider()
    {
        return softwareProvider;
    }

    public void setSoftwareProvider(String softwareProvider)
    {
        this.softwareProvider = softwareProvider;
    }

    public Object getKey()
    {
        return this.casNumber;
    }

    public String getFirm()
    {
        return firm;
    }

    public int hashCode()
    {
        return getKey().hashCode();
    }

    public String getFrontEnd()
    {
        return frontEnd;
    }

    public void setFrontEnd(String frontEnd)
    {
        this.frontEnd = frontEnd;
    }

    public Object clone() throws CloneNotSupportedException
    {
        CASInformationImpl impl = new CASInformationImpl();
        impl.casNumber = casNumber;
        impl.firm = firm;
        impl.hardwareManufacturer = hardwareManufacturer;
        impl.hardwareLocation = hardwareLocation;
        impl.hardwareModel = hardwareModel;
        impl.ipAddress = ipAddress;
        impl.networkConnectivityProvider = networkConnectivityProvider;
        impl.softwareProvider = softwareProvider;
        impl.frontEnd = frontEnd;

        return impl;
    }
}
