//
// -----------------------------------------------------------------------------------
// Source file: Process.java
//
// PACKAGE: com.cboe.interfaces.instrumentation
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.processes;

import java.util.Date;
import java.util.Properties;

import com.cboe.interfaces.presentation.common.businessModels.BusinessModel;

public interface CBOEProcess extends BusinessModel
{
    public static String DEFAULT_PROCESS_MODE = "Unknown";
    public static String ICS_MANAGER = "Manager";
    public static String ICS_WORKER = "Worker";

    /**
     *  Returns the process name for the process
     */
    public String getProcessName();
    /**
     *  Returns the orbname for the process
     */
    public String getOrbName();
    /**
     *  Returns the Prefix for the process
     */
    public String getHostName();
    /**
     *  Returns the orb port number
     */
    public int getPort();
    /**
     *  Returns the process type for the process.  This is based on an analysis
     *  of the name of the process.
     */
    public int getProcessType();
    /**
     *  Returns the online status of the process according to data from process watcher.
     */
    public short getOnlineStatus();

    /**
     * Returns the originator for the ProcessWatcher online status event.
     */
    public String getOnlineStatusOriginator();

    /**
     * Returns the reason code for the ProcessWatcher online status event.
     */
    public short getOnlineStatusReasonCode();

    /**
     *  Returns the master/slave status of the process.
     */
    public short getMasterSlaveStatus();
    /**
     *  Returns the status of the process's poa.
     */
    public short getPoaStatus();

    /**
     * Returns the originator for the ProcessWatcher poa status event.
     */
    public String getPoaStatusOriginator();

    /**
     * Returns the reason code for the ProcessWatcher poa status event.
     */
    public short getPoaStatusReasonCode();

    /**
     *  Returns a combined status of the process.  If the poa has a status of master or slave,
     *  the poas status is returned, otherwise the online status is returned.
     */
    public short getOnlinePoaStatusCombo();
    /**
     *  Identifies if the process is a CAS.  Returns true for a CAS, MDCAS, CFIX or FIXCAS.  Returns false
     *  for a SACAS.
     */
    public boolean isCAS();

    /**
     * Identifies if the process is an ICS.
     */
    public boolean isICS();

    /**
     *  Returns the display name for the process
     */
    public String getDisplayName();
    /**
     *  Returns the cluster name for the process
     */
    public String getCluster();
    /**
     *  Returns the sub cluster name for the process
     */
    public String getSubCluster();
    /**
     *  Returns the cluster and subcluster name as a combined string, separated by a colon.
     */
    public String getClusterSubClusterName();


    /**
     * Returns Firm name for this CAS.
     * @return firm name String
     */
    public String getFirmName();

    /**
     * Returns number of current users for this CAS.
     * @return currentUsers long
     */
    public long getCurrentUsersSize();

    /**
     * Returns current max wueue size for this CAS.
     * @return currentMaxQueueSize long
     */
    public long getCurrentMaxQueueSize();

    /**
     * Returns last status update time for this CAS.
     * @return lastStatusUpdate Date
     */
    public Date getLastStatusUpdateTime();

    /**
     * Returns last status update time for this CAS.
     * @return lastStatusUpdate in milliseconds
     */
    public long getLastStatusUpdateTimeMillis();


    /**
     * Returns current memory usage for this CAS.
     * @return currentMemory long
     */
    public long getCurrentMemoryUsage();

    /**
     * Returns heap memory size for this CAS.
     * @return heapMemorySize long
     */
    public long getHeapMemorySize();

    /**
     * Returns command line arguments for this CAS.
     * @return commanfLineArgs Properties
     */
    public Properties getCommandLineArgs();

    /**
     * Returns XML configuration for this CAS as a String.
     * @return configuration String
     */
    public String getXMLConfiguration();

    public String getVersionString();


    /**
     * Get the physical hardware location for this CAS.
     * @return physical hardware location firm, CBOE test floor, CBOE data center, etc
     */
    public String getHardwareLocation();
    /**
     * Gets the hardware manufacturer for this CAS.
     * @return hardware manufacturer (SUN, FUJITSU, etc).
     */
    public String getHardwareManufacturer();
    /**
     * Gets the hardware model for this CAS.
      * @return hardware model (E280R, E250, E220R, etc)
     */
    public String getHardwareModel();
    /**
     * Gets the IP address for this CAS.
     * @param ipAddress
     */
    public String getIpAddress();
    /**
     * Gets the network connectivity provider for this CAS if this CAS is not located at CBOE.
     * i.e.: SAVVIS, NCC, etc
     * @param networkConnectivityProvider
     */
    public String getNetworkConnectivityProvider();
    /**
     * Gets the software vendor for this CAS.
     * @return software vendor (PROP, CBOE, ACTANT, RTS, etc)
     */
    public String getSoftwareVendor();


    public String getIpAddressAndPort();

    /**
     * Gets the Front End
     * @return Front End (1, 2, 3)
     */
    public String getFrontEnd();

    public boolean isSubscribedForInstrumentors();

    public String getProcessMode();

    public boolean isIcsManager();

    public boolean isIcsWorker();

    public String[] getLogicalNames();
}

