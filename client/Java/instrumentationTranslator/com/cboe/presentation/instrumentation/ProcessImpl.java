//
// -----------------------------------------------------------------------------------
// Source file: ProcessImpl.java
//
// PACKAGE: com.cboe.presentation.instrumentation
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.instrumentation;

import java.text.ParseException;
import java.util.*;

import com.cboe.idl.processWatcher.PWEventCodes;

import com.cboe.interfaces.casMonitor.CAS;
import com.cboe.interfaces.domain.OrbNameAliasConstant;
import com.cboe.interfaces.instrumentation.CBOEProcessMutable;
import com.cboe.interfaces.instrumentation.HeapInstrumentor;
import com.cboe.interfaces.instrumentation.QueueInstrumentor;
import com.cboe.interfaces.instrumentation.Status;
import com.cboe.interfaces.presentation.processes.OrbNameAlias;
import com.cboe.interfaces.presentation.processes.ProcessInfo;
import com.cboe.interfaces.presentation.processes.ProcessInfoTypes;

import com.cboe.util.UserDataTypes;

import com.cboe.presentation.common.businessModels.AbstractMutableBusinessModel;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.common.formatters.ProcessTypes;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerINBusinessProperty;
import com.cboe.presentation.common.processes.ProcessPattern;
import com.cboe.presentation.common.properties.AppPropertiesFileFactory;
import com.cboe.presentation.environment.EnvironmentManagerFactory;

import com.cboe.domain.util.InstrumentorUserData;

public class ProcessImpl extends AbstractMutableBusinessModel implements CBOEProcessMutable
{
    private   static String    CATEGORY = "ProcessImpl";
    private   static String    STORAGE_CATEGORY = "Storage";
    private   static String    DERIVE_ALLOW="AllowDerivedProcessCluster";

    protected String            orbName;
    protected String[]          logicalNames;
    protected ProcessInfo       processInfo;
    protected CAS               cas;
    protected OrbNameAlias      alias;
    protected HeapInstrumentor  heapInstrumentor;
    protected QueueInstrumentor queueInstrumentor;

    protected long   currentUsersSize;
    protected long   currentMemoryUsage;
    protected String versionString;
    protected int    processType;
    protected String prefix;
    protected long   lastUpdateTimeMillis;
    protected Date   lastUpdateTime;
    protected String ipAddressPort;
    protected String clusterNameDerived;
    protected boolean isSubscribedInstrumentors;
    protected boolean allowDerivedCluster;
    protected String processMode;

    public ProcessImpl(String orbName)
    {
        this.orbName = orbName;
        processType = deriveProcessType(orbName);
        String deriveAllow = AppPropertiesFileFactory.find().getValue(STORAGE_CATEGORY,DERIVE_ALLOW);
        if (deriveAllow != null)
        {
            allowDerivedCluster = Boolean.parseBoolean(deriveAllow);
        }
        setProcessMode(DEFAULT_PROCESS_MODE);
        logicalNames = new String[0];
    }

    public void setCAS(CAS cas)
    {
        this.cas = cas;
        ipAddressPort = null;
    }

    public void setProcessInfo(ProcessInfo processInfo)
    {
        this.processInfo = processInfo;
    }

    public void setOrbNameAlias(OrbNameAlias orbNameAlias)
    {
        this.alias = orbNameAlias;
    }

    public void setHeapInstrumentor(HeapInstrumentor heapInstrumentor)
    {
        this.heapInstrumentor = heapInstrumentor;

        this.currentMemoryUsage = heapInstrumentor.getTotalMemory() - heapInstrumentor.getFreeMemory();
        parseUserData(heapInstrumentor);
        setLastUpdateTime(heapInstrumentor.getLastUpdatedTimeMillis());
    }

    public void setLargestQueue(QueueInstrumentor queueInstrumentor)
    {
        this.queueInstrumentor = queueInstrumentor;
    }

    /**
     *  Returns the process name for the process
     */
    public String getProcessName()
    {
        if (processInfo != null)
        {
            return processInfo.getProcessName();
        }
        else
        {
            return null;
        }
    }

    /**
     *  Returns the orbname for the process
     */
    public String getOrbName()
    {
        return orbName;
    }

    /**
     * Returns the logical name for the process
     * (Currently used only for ICS processes)
     */
    public String[] getLogicalNames()
    {
        return logicalNames;
    }

    /**
     *  Returns the hostname for the process
     */
    public String getHostName()
    {
        if (processInfo != null)
        {
            return processInfo.getHostName();
        }
        else
        {
            return "";
        }
    }

    /**
     *  Returns the orb port number
     */
    public int getPort()
    {
        if (processInfo != null)
        {
            return processInfo.getPort();
        }
        else
        {
            return 0;
        }
    }

    /**
     *  Returns the process type for the process.  This is based on an analysis
     *  of the name of the process.
     */
    public int getProcessType()
    {
        return processType;
    }

    /**
     *  Returns the online status of the process according to data from process watcher.
     */
    public short getOnlineStatus()
    {
        if (processInfo != null)
        {
            return processInfo.getOnlineStatus();
        }
        else
        {
            return Status.UNKNOWN;
        }
    }

    public String getOnlineStatusOriginator()
    {
        if(processInfo != null)
        {
            return processInfo.getOnlineStatusOriginator();
        }
        else
        {
            return "";
        }
    }

    public short getOnlineStatusReasonCode()
    {
        if(processInfo != null)
        {
            return processInfo.getOnlineStatusReasonCode();
        }
        else
        {
            return PWEventCodes.Unknown;
        }
    }

    /**
     *  Returns the master/slave status of the process.
     */
    public short getMasterSlaveStatus()
    {
        if (processInfo != null)
        {
            return processInfo.getMasterSlaveStatus();
        }
        else
        {
            return Status.UNKNOWN;
        }
    }

    /**
     *  Returns the status of the process's poa.
     */
    public short getPoaStatus()
    {
        if (processInfo != null)
        {
            return processInfo.getPoaStatus();
        }
        else
        {
            return Status.UNKNOWN;
        }
    }

    public String getPoaStatusOriginator()
    {
        if(processInfo != null)
        {
            return processInfo.getPoaStatusOriginator();
        }
        else
        {
            return "";
        }
    }

    public short getPoaStatusReasonCode()
    {
        if(processInfo != null)
        {
            return processInfo.getPoaStatusReasonCode();
        }
        else
        {
            return PWEventCodes.Unknown;
        }
    }

    /**
     *  Returns a combined status of the process.  If the poa has a status of master or slave,
     *  the poas status is returned, otherwise the online status is returned.
     */
    public short getOnlinePoaStatusCombo()
    {
        if (processInfo != null)
        {
            return processInfo.getOnlinePoaStatusCombo();
        }
        else
        {
            return Status.UNKNOWN;
        }
    }

    /**
     *  Returns the display name for the process
     */
    public String getDisplayName()
    {
        if (alias != null)
        {
            return alias.getDisplayName();
        }
        else
        {
            return getOrbName();
        }
    }

    /**
     *  Returns the cluster name for the process
     */
    public String getCluster()
    {
        String cluster = null;

        if (alias != null)
        {
            cluster = alias.getCluster();
        }
        else
        if (allowDerivedCluster)
        {
            if (heapInstrumentor != null)
            {
                cluster = heapInstrumentor.getClusterName();
            }
            else if (processInfo != null)
            {
                cluster = processInfo.getClusterName();
            }

            if ((cluster == null) || cluster.equals(""))
            {
                cluster = getClusterDerived();
            }
        }
        else
        {
            cluster = OrbNameAliasConstant.DEFAULT_CLUSTER_NAME;
        }
        return cluster;
    }

    /**
     *  Returns the sub cluster name for the process
     */
    public String getSubCluster()
    {
        if (alias != null)
        {
            return alias.getSubCluster();
        }
        else
        {
            return null;
        }
    }

    /**
     *  Returns the cluster and subcluster name as a combined string, separated by a colon.
     */
    public String getClusterSubClusterName()
    {
        if (alias != null)
        {
            return alias.getClusterSubClusterName();
        }
        else
        {
            return getCluster();
        }
    }

    /**
     *  Get the cluster based on the derived type.
     */
    protected String getClusterDerived()
    {
        if (clusterNameDerived == null)
        {
            clusterNameDerived = ProcessTypes.toString(processType);
        }
        return clusterNameDerived;
    }

    /**
     * Returns Firm name for this CAS.
     * @return firm name String
     */
    public String getFirmName()
    {
        if (cas != null)
        {
            return cas.getFirmName();
        }
        else
        {
            return "";
        }
    }

    /**
     * Returns number of current users for this CAS.
     * @return currentUsers long
     */
    public long getCurrentUsersSize()
    {
        return currentUsersSize;
    }

    public void setCurrentUsersSize(long usersSize)
    {
        this.currentUsersSize = usersSize;
    }

    /**
     * Returns current max wueue size for this CAS.
     * @return currentMaxQueueSize long
     */
    public long getCurrentMaxQueueSize()
    {
        if (queueInstrumentor != null)
        {
            return queueInstrumentor.getCurrentSize();
        }
        else
        {
            return 0;
        }
    }

    /**
     * Returns last status update time for this CAS.
     * @return lastStatusUpdate Date
     */
    public Date getLastStatusUpdateTime()
    {
        if (lastUpdateTime == null)
        {
            lastUpdateTime = new Date(lastUpdateTimeMillis);
        }
        return lastUpdateTime;
    }

    /**
     * Returns last status update time for this CAS.
     * @return lastStatusUpdate in milliseconds
     */
    public long getLastStatusUpdateTimeMillis()
    {
        return lastUpdateTimeMillis;
    }

    /**
     * Returns current memory usage for this CAS.
     * @return currentMemory long
     */
    public long getCurrentMemoryUsage()
    {
        return currentMemoryUsage;
    }

    /**
     * Returns heap memory size for this CAS.
     * @return heapMemorySize long
     */
    public long getHeapMemorySize()
    {
        if (heapInstrumentor != null)
        {
            return heapInstrumentor.getMaxMemory();
        }
        else
        {
            return 0;
        }
    }

    /**
     * Returns command line arguments for this CAS.
     * @return commanfLineArgs Properties
     */
    public Properties getCommandLineArgs()
    {
        if (cas != null)
        {
            return cas.getCommandLineArgs();
        }
        else
        {
            return null;
        }
    }

    /**
     * Returns XML configuration for this CAS as a String.
     * @return configuration String
     */
    public String getXMLConfiguration()
    {
        if (cas != null)
        {
            return cas.getXMLConfiguration();
        }
        else
        {
            return "";
        }
    }

    public String getVersionString()
    {
        return versionString;
    }

    public void setVersionString(String versionString)
    {
        this.versionString = versionString;
    }

    /**
     * Get the physical hardware location for this CAS.
     * @return physical hardware location firm, CBOE test floor, CBOE data center, etc
     */
    public String getHardwareLocation()
    {
        if (cas != null)
        {
            return cas.getHardwareLocation();
        }
        else
        {
            return "";
        }
    }

    /**
     * Gets the hardware manufacturer for this CAS.
     * @return hardware manufacturer (SUN, FUJITSU, etc).
     */
    public String getHardwareManufacturer()
    {
        if (cas != null)
        {
            return cas.getHardwareManufacturer();
        }
        else
        {
            return "";
        }
    }

    /**
     * Gets the hardware model for this CAS.
      * @return hardware model (E280R, E250, E220R, etc)
     */
    public String getHardwareModel()
    {
        if (cas != null)
        {
            return cas.getHardwareModel();
        }
        else
        {
            return "";
        }
    }

    /**
     * Gets the IP address for this CAS.
     */
    public String getIpAddress()
    {
        if (cas != null)
        {
            return cas.getIpAddress();
        }
        else
        {
            return "";
        }
    }

    /**
     * Gets the network connectivity provider for this CAS if this CAS is not located at CBOE.
     * i.e.: SAVVIS, NCC, etc
     */
    public String getNetworkConnectivityProvider()
    {
        if (cas != null)
        {
            return cas.getNetworkConnectivityProvider();
        }
        else
        {
            return "";
        }
    }

    /**
     * Gets the software vendor for this CAS.
     * @return software vendor (PROP, CBOE, ACTANT, RTS, etc)
     */
    public String getSoftwareVendor()
    {
        if (cas != null)
        {
            return cas.getSoftwareVendor();
        }
        else
        {
            return "";
        }
    }

    public String getIpAddressAndPort()
    {
        if (ipAddressPort == null)
        {
            String       ipAddress = null;

            if (cas != null)
            {
                ipAddress = cas.getIpAddress();
            }
            if (ipAddress != null && !(ipAddress.equals("")))
            {
                ipAddressPort = ipAddress + ":" + getPort();
            }
            else
            {
                ipAddressPort = "";
            }
        }
        return ipAddressPort;
    }

    public String getFrontEnd()
    {
        if( cas != null )
        {
            return cas.getFrontEnd();
        }
        return "";
    }

    public Object clone() throws CloneNotSupportedException
    {
        ProcessImpl newImpl = new ProcessImpl(orbName);

        if (cas != null)
        {
            newImpl.setCAS((CAS)cas.clone());
        }
        if (processInfo != null)
        {
            newImpl.setProcessInfo((ProcessInfo)processInfo.clone());
        }
        if (alias != null)
        {
            newImpl.setOrbNameAlias((OrbNameAlias)alias.clone());
        }
        if (heapInstrumentor != null)
        {
            newImpl.setHeapInstrumentor((HeapInstrumentor)heapInstrumentor.clone());
        }
        if (queueInstrumentor != null)
        {
            newImpl.setLargestQueue((QueueInstrumentor)queueInstrumentor.clone());
        }
        newImpl.isSubscribedInstrumentors = isSubscribedInstrumentors;
        if (processMode != null);
        {
            newImpl.setProcessMode(processMode);
        }
        if (logicalNames != null)
        {
   	        newImpl.setLogicalNames((String[])logicalNames.clone());
        }

        return newImpl;
    }

    public Object getKey()
    {
        return getOrbName();
    }

    protected void parseUserData(HeapInstrumentor heap)
    {
        String userData = heap.getUserData();

        if(userData != null && userData.length() > 0)
        {
            try
            {
                InstrumentorUserData parsedUserData = new InstrumentorUserData(userData);

                String[] values = parsedUserData.getValues(UserDataTypes.NUMBER_OF_USERS);
                if(values != null && values.length > 0)
                {
                    String numUsersString = values[0];
                    try
                    {
                        Long numUsers = new Long(numUsersString);
                        setCurrentUsersSize(numUsers.longValue());
                    }
                    catch( NumberFormatException e )
                    {
                        setCurrentUsersSize(0);
                        GUILoggerHome.find().exception(CATEGORY + ": parseUserData",
                            "Could not parse number of users:" +
                            numUsersString, e);
                    }
                }

                values = parsedUserData.getValues(UserDataTypes.CAS_VERSION);
                if( values != null && values.length > 0 )
                {
                    String version = values[0];
                    setVersionString(version);
                }
            }
            catch( ParseException e )
            {
                setCurrentUsersSize(0);
                setVersionString("Unavailable");

                GUILoggerHome.find().exception(CATEGORY + ": parseUserData",
                    "Could not parse userData:" + userData, e);
            }
        }
    }

    protected int deriveProcessType(String orbName)
    {
        int rv = ProcessInfoTypes.UNKNOWN_TYPE;

        ProcessPattern processPattern = ProcessPattern.getInstance();
        String casPattern    = processPattern.getCASPattern(getPrefix(),getHostName());
        String saCasPattern  = processPattern.getSACASPattern(getPrefix(),getHostName());
        String mdCasPattern  = processPattern.getMDCASPattern(getPrefix(),getHostName());
        String fixCasPattern = processPattern.getFIXCASPattern(getPrefix(),getHostName());
        String cFixPattern   = processPattern.getCFIXPattern(getPrefix(),getHostName());
        String icsPattern    = processPattern.getICSPattern(getPrefix(),getHostName());
        String dnPattern     = processPattern.getDNPattern(getPrefix(),getHostName());
        String bcPattern     = processPattern.getBCPattern(getPrefix(),getHostName());
        String gcPattern     = processPattern.getGCPattern(getPrefix(),getHostName());
        String frontendPattern  = processPattern.getFrontendPattern(getPrefix(),getHostName());
        String xtpPattern     = processPattern.getXTPPattern(getPrefix(),getHostName());
        String wbsPattern   = processPattern.getWBSPattern(getPrefix(),getHostName());
        String madPattern   = processPattern.getMADPattern(getPrefix(),getHostName());
        String itaPattern   = processPattern.getITAPattern(getPrefix(),getHostName());
        String tflPattern   = processPattern.getTFLPattern(getPrefix(),getHostName());
        String mrPattern    = processPattern.getMRPattern(getPrefix(),getHostName());
        String focusPattern = processPattern.getFocusPattern(getPrefix(),getHostName());
        String dcsPattern   = processPattern.getDCSPattern(getPrefix(),getHostName());
        String mmePattern   = processPattern.getMMEPattern(getPrefix(),getHostName());
        String tpsPattern   = processPattern.getTPSPattern(getPrefix(),getHostName());
        String boPattern    = processPattern.getBOPattern(getPrefix(),getHostName());
        String mppiPattern  = processPattern.getMPPIPattern(getPrefix(),getHostName());
        String asasPattern  = processPattern.getASASPattern(getPrefix(),getHostName());
        String bjcPattern   = processPattern.getBJCPattern(getPrefix(),getHostName());

        String checkName = orbName;

        if (checkName.matches(casPattern))
        {
            return ProcessInfoTypes.CAS_TYPE;
        }
        else
        if (checkName.matches(saCasPattern))
        {
            return ProcessInfoTypes.SACAS_TYPE;
        }
        else
        if (checkName.matches(mdCasPattern))
        {
            return ProcessInfoTypes.MDCAS_TYPE;
        }
        else
        if (checkName.matches(fixCasPattern))
        {
            return ProcessInfoTypes.FIXCAS_TYPE;
        }
        else
        if (checkName.matches(cFixPattern))
        {
            return ProcessInfoTypes.CFIX_TYPE;
        }
        else
        if (checkName.matches(dnPattern))
        {
            return ProcessInfoTypes.DN_TYPE;
        }
        else
        if (checkName.matches(bcPattern))
        {
            return ProcessInfoTypes.BC_TYPE;
        }
        else
        if (checkName.matches(gcPattern))
        {
            return ProcessInfoTypes.GC_TYPE;
        }
        else
        if (checkName.matches(frontendPattern))
        {
            return ProcessInfoTypes.FE_TYPE;
        }
        else
        if (checkName.matches(icsPattern))
        {
            return ProcessInfoTypes.ICS_TYPE;
        }
        else
        if (checkName.matches(xtpPattern))
        {
            return ProcessInfoTypes.XTP_TYPE;
        }
        else
        if (checkName.matches(wbsPattern))
        {
            return ProcessInfoTypes.WBS_TYPE;
        }
        else
        if (checkName.matches(madPattern))
        {
            return ProcessInfoTypes.MAD_TYPE;
        }
        else
        if (checkName.matches(itaPattern))
        {
            return ProcessInfoTypes.ITA_TYPE;
        }
        else
        if (checkName.matches(tflPattern))
        {
            return ProcessInfoTypes.TFL_TYPE;
        }
        else
        if (checkName.matches(mrPattern))
        {
            return ProcessInfoTypes.MR_TYPE;
        }
        else
        if (checkName.matches(focusPattern))
        {
            return ProcessInfoTypes.FOCUS_TYPE;
        }
        else
        if (checkName.matches(dcsPattern))
        {
            return ProcessInfoTypes.DCS_TYPE;
        }
        else
        if (checkName.matches(mmePattern))
        {
            return ProcessInfoTypes.MME_TYPE;
        }
        else
        if (checkName.matches(tpsPattern))
        {
            return ProcessInfoTypes.TPS_TYPE;
        }
        else
        if (checkName.matches(boPattern))
        {
            return ProcessInfoTypes.BO_TYPE;
        }
        else
        if (checkName.matches(mppiPattern))
        {
            return ProcessInfoTypes.MPPI_TYPE;
        }
        else
        if (checkName.matches(asasPattern))
        {
            return ProcessInfoTypes.ASAS_TYPE;
        }
        else
        if (checkName.matches(bjcPattern))
        {
            return ProcessInfoTypes.BJC_TYPE;
        }
        else
        {
            return rv;
        }
    }

  private String getPrefix()
    {
        if ( prefix == null )
        {
            try
            {
                //try to get file name from system properties
                prefix = EnvironmentManagerFactory.find().getCurrentEnvironment().getSBTPrefix();

                if (prefix != null)
                {
                    if ( GUILoggerHome.find().isDebugOn() &&
                         GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.PROCESSES) )
                    {
                        GUILoggerHome.find().debug("ProcessInfoImpl", GUILoggerINBusinessProperty.PROCESSES, "Prefix = " + prefix);
                    }
                }
                else
                {
                    GUILoggerHome.find().alarm("ProcessInfoImpl", "Unable to get SBTPrefix property.");
                }
            }
            catch (SecurityException e)
            {
                DefaultExceptionHandlerHome.find().process(e, "Unable to get SBTPrefix property");
            }
        }
        return prefix;
    }

    public boolean isCAS()
    {
        boolean retval = false;
        int type = getProcessType();
        if ( type == ProcessInfoTypes.CAS_TYPE ||
             type == ProcessInfoTypes.SACAS_TYPE ||
             type == ProcessInfoTypes.MDCAS_TYPE ||
             type == ProcessInfoTypes.FIXCAS_TYPE ||
             type == ProcessInfoTypes.CFIX_TYPE )
        {
            retval = true;
        }
        return retval;
    }

    public boolean isICS()
    {
        boolean retval = false;
        int type = getProcessType();
        if(type == ProcessInfoTypes.ICS_TYPE)
        {
            retval = true;
        }
        return retval;
    }

    protected void setLastUpdateTime(long millis)
    {
        lastUpdateTimeMillis = millis;
        lastUpdateTime = null;
    }

    public boolean isSubscribedForInstrumentors()
    {
        return isSubscribedInstrumentors;
    }

    public void markSubscribedForInstrumentors(boolean markSubscribed)
    {
        this.isSubscribedInstrumentors = markSubscribed;
    }

    public String getProcessMode()
    {
        return processMode;
    }

    public void setProcessMode(String processMode)
    {
        this.processMode = processMode;
    }

    public boolean isIcsManager()
    {
        return getProcessMode().equals(ICS_MANAGER);
    }

    public boolean isIcsWorker()
    {
        return getProcessMode().equals(ICS_WORKER);
    }

    public void setLogicalNames(String[] names)
    {
        logicalNames = names;
    }

    public synchronized void addLogicalName(String name)
    {
        ArrayList list = new ArrayList<String>(logicalNames.length);
        for (String aName : logicalNames)
        {
            list.add(aName);
        }
        list.add(name);
        String[] array = new String[list.size()];
        array = (String[])list.toArray(array);
        setLogicalNames(array);
    }

    public synchronized void removeLogicalName(String name)
    {
        ArrayList list = new ArrayList<String>(logicalNames.length);
        for(String aName : logicalNames)
        {
            list.add(aName);
        }
        list.remove(name);
        String[] array = new String[list.size()];
        array = (String[]) list.toArray(array);
        setLogicalNames(array);
    }
}
