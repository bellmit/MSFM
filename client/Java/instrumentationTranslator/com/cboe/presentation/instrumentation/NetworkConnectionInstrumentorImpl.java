// -----------------------------------------------------------------------------------
// Source file: NetworkConnectionInstrumentorImpl.java
//
// PACKAGE: com.cboe.presentation.instrumentation;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

package com.cboe.presentation.instrumentation;

import java.util.Date;

import com.cboe.interfaces.instrumentation.CalculatedNetworkConnectionInstrumentor;
import com.cboe.interfaces.instrumentation.CalculatedNetworkConnectionInstrumentorMutable;
import com.cboe.interfaces.instrumentation.InstrumentorTypes;
import com.cboe.presentation.common.formatters.ProcessStatus;

public class NetworkConnectionInstrumentorImpl
        extends AbstractInstrumentor
        implements CalculatedNetworkConnectionInstrumentorMutable
{
    public static final String[] INFRA_NAMES =
            {
                "TIOP", "IIOP"
            };
    private long bytesSent;
    private long bytesReceived;
    private long msgsSent;
    private long msgsReceived;
    private long packetsSent;
    private long packetsReceived;
    private long invalidPacketsReceived;
    private long garbageBytesReceived;
    private long connects;
    private long disconnects;
    private long exceptions;

    private long lastTimeSentMillis;
    private long lastTimeReceivedMillis;
    private long lastConnectTimeMillis;
    private long lastDisconnectTimeMillis;
    private long lastExceptionTimeMillis;

    private Date lastTimeSent;
    private Date lastTimeReceived;
    private Date lastConnectTime;
    private Date lastDisconnectTime;
    private Date lastExceptionTime;

    private Throwable lastException;
    private String lastExceptionMessage;
    private short status;

    //Calculated Instrumentor Data
    private long peakBytesSent;
    private long peakBytesReceived;
    private long peakMsgsSent;
    private long peakMsgsReceived;
    private long peakPacketsSent;
    private long peakPacketsReceived;
    private long peakInvalidPacketsReceived;
    private long peakGarbageBytesReceived;
    private long peakConnects;
    private long peakDisconnects;
    private long peakExceptions;
    private long intervalBytesSent;
    private long intervalBytesReceived;
    private long intervalMsgsSent;
    private long intervalMsgsReceived;
    private long intervalPacketsSent;
    private long intervalPacketsReceived;
    private long intervalInvalidPacketsReceived;
    private long intervalGarbageBytesReceived;
    private long intervalConnects;
    private long intervalDisconnects;
    private long intervalExceptions;
    private double peakBytesSentRate;
    private double peakBytesReceivedRate;
    private double peakMsgsSentRate;
    private double peakMsgsReceivedRate;
    private double peakPacketsSentRate;
    private double peakPacketsReceivedRate;
    private double peakInvalidPacketsReceivedRate;
    private double peakGarbageBytesReceivedRate;
    private double peakConnectsRate;
    private double peakDisconnectsRate;
    private double peakExceptionsRate;
    private double avgBytesSentRate;
    private double avgBytesReceivedRate;
    private double avgMsgsSentRate;
    private double avgMsgsReceivedRate;
    private double avgPacketsSentRate;
    private double avgPacketsReceivedRate;
    private double avgInvalidPacketsReceivedRate;
    private double avgGarbageBytesReceivedRate;
    private double avgConnectsRate;
    private double avgDisconnectsRate;
    private double avgExceptionsRate;

    // New "High Water Mark" fields for all-day peak values
	private double bytesSentRateHWM = 0.0;
	private long bytesSentRateHWMTimeMillis = 0;
	private double bytesReceivedRateHWM = 0.0;
	private long bytesReceivedRateHWMTimeMillis = 0;
	private double msgsSentRateHWM = 0.0;
	private long msgsSentRateHWMTimeMillis = 0;
	private double msgsReceivedRateHWM = 0.0;
	private long msgsReceivedRateHWMTimeMillis = 0;
	private double packetsSentRateHWM = 0.0;
	private long packetsSentRateHWMTimeMillis = 0;
	private double packetsReceivedRateHWM = 0.0;
	private long packetsReceivedRateHWMTimeMillis = 0;
    
    // Lazily initialize object versions of everything
    private Long bytesSentLong;
    private Long bytesReceivedLong;
    private Long msgsSentLong;
    private Long msgsReceivedLong;
    private Long packetsSentLong;
    private Long packetsReceivedLong;
    private Long invalidPacketsReceivedLong;
    private Long garbageBytesReceivedLong;
    private Long connectsLong;
    private Long disconnectsLong;
    private Long exceptionsLong;

    private Short statusShort;

    private Long peakBytesSentLong;
    private Long peakBytesReceivedLong;
    private Long peakMsgsSentLong;
    private Long peakMsgsReceivedLong;
    private Long peakPacketsSentLong;
    private Long peakPacketsReceivedLong;
    private Long peakInvalidPacketsReceivedLong;
    private Long peakGarbageBytesReceivedLong;
    private Long peakConnectsLong;
    private Long peakDisconnectsLong;
    private Long peakExceptionsLong;
    private Long intervalBytesSentLong;
    private Long intervalBytesReceivedLong;
    private Long intervalMsgsSentLong;
    private Long intervalMsgsReceivedLong;
    private Long intervalPacketsSentLong;
    private Long intervalPacketsReceivedLong;
    private Long intervalInvalidPacketsReceivedLong;
    private Long intervalGarbageBytesReceivedLong;
    private Long intervalConnectsLong;
    private Long intervalDisconnectsLong;
    private Long intervalExceptionsLong;
    private Double peakBytesSentRateDouble;
    private Double peakBytesReceivedRateDouble;
    private Double peakMsgsSentRateDouble;
    private Double peakMsgsReceivedRateDouble;
    private Double peakPacketsSentRateDouble;
    private Double peakPacketsReceivedRateDouble;
    private Double peakInvalidPacketsReceivedRateDouble;
    private Double peakGarbageBytesReceivedRateDouble;
    private Double peakConnectsRateDouble;
    private Double peakDisconnectsRateDouble;
    private Double peakExceptionsRateDouble;
    private Double avgBytesSentRateDouble;
    private Double avgBytesReceivedRateDouble;
    private Double avgMsgsSentRateDouble;
    private Double avgMsgsReceivedRateDouble;
    private Double avgPacketsSentRateDouble;
    private Double avgPacketsReceivedRateDouble;
    private Double avgInvalidPacketsReceivedRateDouble;
    private Double avgGarbageBytesReceivedRateDouble;
    private Double avgConnectsRateDouble;
    private Double avgDisconnectsRateDouble;
    private Double avgExceptionsRateDouble;

    // New "High Water Mark" objects for all-day peak values
	private Double bytesSentRateHWMDouble;
	private Date bytesSentRateHWMTime;
	private Double bytesReceivedRateHWMDouble;
	private Date bytesReceivedRateHWMTime;
	private Double msgsSentRateHWMDouble;
	private Date msgsSentRateHWMTime;
	private Double msgsReceivedRateHWMDouble;
	private Date msgsReceivedRateHWMTime;
	private Double packetsSentRateHWMDouble;
	private Date packetsSentRateHWMTime;
	private Double packetsReceivedRateHWMDouble;
	private Date packetsReceivedRateHWMTime;

    /**
     * Default Constructor
     */
    protected NetworkConnectionInstrumentorImpl()
    {
        super();
    }

    public NetworkConnectionInstrumentorImpl(String orbName, String clusterName, com.cboe.instrumentationService.instrumentors.NetworkConnectionInstrumentor connectionInstrumentor)
    {
        this();
        setData(orbName, clusterName, connectionInstrumentor);
    }

    public NetworkConnectionInstrumentorImpl(String orbName, String clusterName,
                                             com.cboe.instrumentationService.instrumentors.NetworkConnectionInstrumentor connectionInstrumentor,
                                             com.cboe.instrumentationService.instrumentors.CalculatedNetworkConnectionInstrumentor calculatedConnectionInstrumentor)
    {
        this(orbName, clusterName, connectionInstrumentor);
        setCalculatedData(calculatedConnectionInstrumentor);
    }

    /**
     * Sets type of the instrumentor.
     */
    protected void setType()
    {
        this.type = InstrumentorTypes.NETWORK_CONNECTION;
    }

    public void setData(String orbName, String clusterName, com.cboe.instrumentationService.instrumentors.NetworkConnectionInstrumentor connectionInstrumentor)
    {
        super.setData(orbName, clusterName, connectionInstrumentor);
        this.bytesSent = connectionInstrumentor.getBytesSent();
        this.bytesReceived = connectionInstrumentor.getBytesReceived();
        this.msgsSent = connectionInstrumentor.getMsgsSent();
        this.msgsReceived = connectionInstrumentor.getMsgsReceived();
        this.packetsSent = connectionInstrumentor.getPacketsSent();
        this.packetsReceived = connectionInstrumentor.getPacketsReceived();
        this.invalidPacketsReceived = connectionInstrumentor.getInvalidPacketsReceived();
        this.garbageBytesReceived = connectionInstrumentor.getGarbageBytesReceived();
        this.connects = connectionInstrumentor.getConnects();
        this.disconnects = connectionInstrumentor.getDisconnects();
        this.exceptions = connectionInstrumentor.getExceptions();

        this.lastTimeSentMillis = connectionInstrumentor.getLastTimeSent();
        this.lastTimeReceivedMillis = connectionInstrumentor.getLastTimeReceived();
        this.lastConnectTimeMillis = connectionInstrumentor.getLastConnectTime();
        this.lastDisconnectTimeMillis = connectionInstrumentor.getLastDisconnectTime();
        this.lastExceptionTimeMillis = connectionInstrumentor.getLastExceptionTime();

        this.lastException = connectionInstrumentor.getLastException();
        this.status = connectionInstrumentor.getStatus();
        this.clearDataObjects();
    }

    public void setCalculatedData(com.cboe.instrumentationService.instrumentors.CalculatedNetworkConnectionInstrumentor calculatedConnectionInstrumentor)
    {
        this.peakBytesSent = calculatedConnectionInstrumentor.getPeakBytesSent();
        this.peakBytesReceived = calculatedConnectionInstrumentor.getPeakBytesReceived();
        this.peakMsgsSent = calculatedConnectionInstrumentor.getPeakMsgsSent();
        this.peakMsgsReceived = calculatedConnectionInstrumentor.getPeakMsgsReceived();
        this.peakPacketsSent = calculatedConnectionInstrumentor.getPeakPacketsSent();
        this.peakPacketsReceived = calculatedConnectionInstrumentor.getPeakPacketsReceived();
        this.peakInvalidPacketsReceived = calculatedConnectionInstrumentor.getPeakInvalidPacketsReceived();
        this.peakGarbageBytesReceived = calculatedConnectionInstrumentor.getPeakGarbageBytesReceived();
        this.peakConnects = calculatedConnectionInstrumentor.getPeakConnects();
        this.peakDisconnects = calculatedConnectionInstrumentor.getPeakDisconnects();
        this.peakExceptions = calculatedConnectionInstrumentor.getPeakExceptions();
        this.intervalBytesSent = calculatedConnectionInstrumentor.getIntervalBytesSent();
        this.intervalBytesReceived = calculatedConnectionInstrumentor.getIntervalBytesReceived();
        this.intervalMsgsSent = calculatedConnectionInstrumentor.getIntervalMsgsSent();
        this.intervalMsgsReceived = calculatedConnectionInstrumentor.getIntervalMsgsReceived();
        this.intervalPacketsSent = calculatedConnectionInstrumentor.getIntervalPacketsSent();
        this.intervalPacketsReceived = calculatedConnectionInstrumentor.getIntervalPacketsReceived();
        this.intervalInvalidPacketsReceived = calculatedConnectionInstrumentor.getIntervalInvalidPacketsReceived();
        this.intervalGarbageBytesReceived = calculatedConnectionInstrumentor.getIntervalGarbageBytesReceived();
        this.intervalConnects = calculatedConnectionInstrumentor.getIntervalConnects();
        this.intervalDisconnects = calculatedConnectionInstrumentor.getIntervalDisconnects();
        this.intervalExceptions = calculatedConnectionInstrumentor.getIntervalExceptions();
        this.peakBytesSentRate = calculatedConnectionInstrumentor.getPeakBytesSentRate();
        this.peakBytesReceivedRate = calculatedConnectionInstrumentor.getPeakBytesReceivedRate();
        this.peakMsgsSentRate = calculatedConnectionInstrumentor.getPeakMsgsSentRate();
        this.peakMsgsReceivedRate = calculatedConnectionInstrumentor.getPeakMsgsReceivedRate();
        this.peakPacketsSentRate = calculatedConnectionInstrumentor.getPeakPacketsSentRate();
        this.peakPacketsReceivedRate = calculatedConnectionInstrumentor.getPeakPacketsReceivedRate();
        this.peakInvalidPacketsReceivedRate = calculatedConnectionInstrumentor.getPeakInvalidPacketsReceivedRate();
        this.peakGarbageBytesReceivedRate = calculatedConnectionInstrumentor.getPeakGarbageBytesReceivedRate();
        this.peakConnectsRate = calculatedConnectionInstrumentor.getPeakConnectsRate();
        this.peakDisconnectsRate = calculatedConnectionInstrumentor.getPeakDisconnectsRate();
        this.peakExceptionsRate = calculatedConnectionInstrumentor.getPeakExceptionsRate();
        this.avgBytesSentRate = calculatedConnectionInstrumentor.getAvgBytesSentRate();
        this.avgBytesReceivedRate = calculatedConnectionInstrumentor.getAvgBytesReceivedRate();
        this.avgMsgsSentRate = calculatedConnectionInstrumentor.getAvgMsgsSentRate();
        this.avgMsgsReceivedRate = calculatedConnectionInstrumentor.getAvgMsgsReceivedRate();
        this.avgPacketsSentRate = calculatedConnectionInstrumentor.getAvgPacketsSentRate();
        this.avgPacketsReceivedRate = calculatedConnectionInstrumentor.getAvgPacketsReceivedRate();
        this.avgInvalidPacketsReceivedRate = calculatedConnectionInstrumentor.getAvgInvalidPacketsReceivedRate();
        this.avgGarbageBytesReceivedRate = calculatedConnectionInstrumentor.getAvgGarbageBytesReceivedRate();
        this.avgConnectsRate = calculatedConnectionInstrumentor.getAvgConnectsRate();
        this.avgDisconnectsRate = calculatedConnectionInstrumentor.getAvgDisconnectsRate();
        this.avgExceptionsRate = calculatedConnectionInstrumentor.getAvgExceptionsRate();
        
        // New "High Water Mark" calcuated fields for all-day peak values
        this.bytesSentRateHWM = calculatedConnectionInstrumentor.getBytesSentRateHWM();
        this.bytesReceivedRateHWM = calculatedConnectionInstrumentor.getBytesReceivedRateHWM();
        this.msgsSentRateHWM = calculatedConnectionInstrumentor.getMsgsSentRateHWM();
        this.msgsReceivedRateHWM = calculatedConnectionInstrumentor.getMsgsReceivedRateHWM();
        this.packetsSentRateHWM = calculatedConnectionInstrumentor.getPacketsSentRateHWM();
        this.packetsReceivedRateHWM = calculatedConnectionInstrumentor.getPacketsReceivedRateHWM();
        this.bytesSentRateHWMTimeMillis = calculatedConnectionInstrumentor.getBytesSentRateHWMTime();
        this.bytesReceivedRateHWMTimeMillis = calculatedConnectionInstrumentor.getBytesReceivedRateHWMTime();
        this.msgsSentRateHWMTimeMillis = calculatedConnectionInstrumentor.getMsgsSentRateHWMTime();
        this.msgsReceivedRateHWMTimeMillis = calculatedConnectionInstrumentor.getMsgsReceivedRateHWMTime();
        this.packetsSentRateHWMTimeMillis = calculatedConnectionInstrumentor.getPacketsSentRateHWMTime();
        this.packetsReceivedRateHWMTimeMillis = calculatedConnectionInstrumentor.getPacketsReceivedRateHWMTime();
        this.clearDataObjects();
    }
    /**
     * Returns BytesSent value for the network connection instrumentor object.
     * @return BytesSent long
     */
    public long getBytesSent()
    {
        return bytesSent;
    }

    /**
     * Returns BytesReceived value for the network connection instrumentor object.
     * @return BytesReceived long
     */
    public long getBytesReceived()
    {
        return bytesReceived;
    }

    /**
     * Returns MsgsSent value for the network connection instrumentor object.
     * @return MsgsSent long
     */
    public long getMsgsSent()
    {
        return msgsSent;
    }

    /**
     * Returns MsgsReceived value for the network connection instrumentor object.
     * @return MsgsReceived long
     */
    public long getMsgsReceived()
    {
        return msgsReceived;
    }

    /**
     * Returns PacketsSen value for the network connection instrumentor object.
     * @return PacketsSen long
     */
    public long getPacketsSent()
    {
        return packetsSent;
    }

    /**
     * Returns PacketsReceived value for the network connection instrumentor object.
     * @return PacketsReceived long
     */
    public long getPacketsReceived()
    {
        return packetsReceived;
    }

    /**
     * Returns InvalidPacketsReceived value for the network connection instrumentor object.
     * @return InvalidPacketsReceived long
     */
    public long getInvalidPacketsReceived()
    {
        return invalidPacketsReceived;
    }

    /**
     * Returns GarbageBytesReceived value for the network connection instrumentor object.
     * @return GarbageBytesReceived long
     */
    public long getGarbageBytesReceived()
    {
        return garbageBytesReceived;
    }

    /**
     * Returns Connects value for the network connection instrumentor object.
     * @return Connects long
     */
    public long getConnects()
    {
        return connects;
    }

    /**
     * Returns Disconnects value for the network connection instrumentor object.
     * @return Disconnects long
     */
    public long getDisconnects()
    {
        return disconnects;
    }

    /**
     * Returns Exceptions value for the network connection instrumentor object.
     * @return Exceptions long
     */
    public long getExceptions()
    {
        return exceptions;
    }

    /**
     * Returns LastTimeSent Date value for the network connection instrumentor object.
     * @return LastTimeSent Date
     */
    public Date getLastTimeSent()
    {
        if(lastTimeSent == null && lastTimeSentMillis > 0)
        {
            lastTimeSent = new Date(lastTimeSentMillis);
        }
        return lastTimeSent;
    }

    /**
     * Returns LastTimeReceived Date value for the network connection instrumentor object.
     * @return LastTimeReceived Date
     */
    public Date getLastTimeReceived()
    {
        if(lastTimeReceived == null && lastTimeReceivedMillis > 0)
        {
            lastTimeReceived = new Date(lastTimeReceivedMillis);
        }
        return lastTimeReceived;
    }

    /**
     * Returns LastConnectTime Date for the network connection instrumentor object.
     * @return LastConnectTime Date
     */
    public Date getLastConnectTime()
    {
        if(lastConnectTime == null && lastConnectTimeMillis > 0)
        {
            lastConnectTime = new Date(lastConnectTimeMillis);
        }
        return lastConnectTime;
    }

    /**
     * Returns LastDisconnectTime Date value for the network connection instrumentor object.
     * @return LastDisconnectTime Date
     */
    public Date getLastDisconnectTime()
    {
        if(lastDisconnectTime == null && lastDisconnectTimeMillis > 0)
        {
            lastDisconnectTime = new Date(lastDisconnectTimeMillis);
        }
        return lastDisconnectTime;
    }

    /**
     * Returns LastExceptionTime Date value for the network connection instrumentor object.
     * @return LastExceptionTime Date
     */
    public Date getLastExceptionTime()
    {
        if(lastExceptionTime == null && lastExceptionTimeMillis > 0)
        {
            lastExceptionTime = new Date(lastExceptionTimeMillis);
        }
        return lastExceptionTime;
    }

    /**
     * Returns LastException for the network connection instrumentor object.
     * @return LastException Throwable
     */
    public Throwable getLastException()
    {
        return lastException;
    }

    /**
     * Returns LastExceptionMessage for the network connection instrumentor object.
     * @return LastExceptionMessage String
     */
    public String getLastExceptionMessage()
    {
        if ( lastExceptionMessage == null && lastException != null)
        {
            lastExceptionMessage = lastException.getMessage();
        }
        return lastExceptionMessage == null ? "" : lastExceptionMessage;
    }

    /**
     * Returns Status value for the network connection instrumentor object.
     * @return Status Short
     */
    public short getStatus()
    {
        return status;
    }

    public long getPeakBytesSent()
    {
        return this.peakBytesSent;
    }

    public long getPeakBytesReceived()
    {
        return this.peakBytesReceived;
    }

    public long getPeakMsgsSent()
    {
        return this.peakMsgsSent;
    }

    public long getPeakMsgsReceived()
    {
        return this.peakMsgsReceived;
    }

    public long getPeakPacketsSent()
    {
        return this.peakPacketsSent;
    }

    public long getPeakPacketsReceived()
    {
        return this.peakPacketsReceived;
    }

    public long getPeakInvalidPacketsReceived()
    {
        return this.peakInvalidPacketsReceived;
    }

    public long getPeakGarbageBytesReceived()
    {
        return this.peakGarbageBytesReceived;
    }

    public long getPeakConnects()
    {
        return this.peakConnects;
    }

    public long getPeakDisconnects()
    {
        return this.peakDisconnects;
    }

    public long getPeakExceptions()
    {
        return this.peakExceptions;
    }

    public long getIntervalBytesSent()
    {
        return this.intervalBytesSent;
    }

    public long getIntervalBytesReceived()
    {
        return this.intervalBytesReceived;
    }

    public long getIntervalMsgsSent()
    {
        return this.intervalMsgsSent;
    }

    public long getIntervalMsgsReceived()
    {
        return this.intervalMsgsReceived;
    }

    public long getIntervalPacketsSent()
    {
        return this.intervalPacketsSent;
    }

    public long getIntervalPacketsReceived()
    {
        return this.intervalPacketsReceived;
    }

    public long getIntervalInvalidPacketsReceived()
    {
        return this.intervalInvalidPacketsReceived;
    }

    public long getIntervalGarbageBytesReceived()
    {
        return this.intervalGarbageBytesReceived;
    }

    public long getIntervalConnects()
    {
        return this.intervalConnects;
    }

    public long getIntervalDisconnects()
    {
        return this.intervalDisconnects;
    }

    public long getIntervalExceptions()
    {
        return this.intervalExceptions;
    }

    public double getPeakBytesSentRate()
    {
        return this.peakBytesSentRate;
    }

    public double getPeakBytesReceivedRate()
    {
        return this.peakBytesReceivedRate;
    }

    public double getPeakMsgsSentRate()
    {
        return this.peakMsgsSentRate;
    }

    public double getPeakMsgsReceivedRate()
    {
        return this.peakMsgsReceivedRate;
    }

    public double getPeakPacketsSentRate()
    {
        return this.peakPacketsSentRate;
    }

    public double getPeakPacketsReceivedRate()
    {
        return this.peakPacketsReceivedRate;
    }

    public double getPeakInvalidPacketsReceivedRate()
    {
        return this.peakInvalidPacketsReceivedRate;
    }

    public double getPeakGarbageBytesReceivedRate()
    {
        return this.peakGarbageBytesReceivedRate;
    }

    public double getPeakConnectsRate()
    {
        return this.peakConnectsRate;
    }

    public double getPeakDisconnectsRate()
    {
        return this.peakDisconnectsRate;
    }

    public double getPeakExceptionsRate()
    {
        return this.peakExceptionsRate;
    }

    public double getAvgBytesSentRate()
    {
        return this.avgBytesSentRate;
    }

    public double getAvgBytesReceivedRate()
    {
        return this.avgBytesReceivedRate;
    }

    public double getAvgMsgsSentRate()
    {
        return this.avgMsgsSentRate;
    }

    public double getAvgMsgsReceivedRate()
    {
        return this.avgMsgsReceivedRate;
    }

    public double getAvgPacketsSentRate()
    {
        return this.avgPacketsSentRate;
    }

    public double getAvgPacketsReceivedRate()
    {
        return this.avgPacketsReceivedRate;
    }

    public double getAvgInvalidPacketsReceivedRate()
    {
        return this.avgInvalidPacketsReceivedRate;
    }

    public double getAvgGarbageBytesReceivedRate()
    {
        return this.avgGarbageBytesReceivedRate;
    }

    public double getAvgConnectsRate()
    {
        return this.avgConnectsRate;
    }

    public double getAvgDisconnectsRate()
    {
        return this.avgDisconnectsRate;
    }

    public double getAvgExceptionsRate()
    {
        return this.avgExceptionsRate;
    }

    public double getBytesSentRateHWM()
    {
        return this.bytesSentRateHWM;
    }

    /**
     * Returns bytesSentRateHWM Double object for the network connection instrumentor object.
     * @return bytesSentRateHWMDouble 
     */
    public Double getBytesSentRateHWMDouble()
    {
        if (bytesSentRateHWMDouble == null)
        {
            bytesSentRateHWMDouble = new Double(bytesSentRateHWM);
        }
        return bytesSentRateHWMDouble;
    }

    public long getBytesSentRateHWMTimeMillis()
    {
        return bytesSentRateHWMTimeMillis;
    }

    /**
     * Returns bytesSentRateHWMTime Date value for the network connection instrumentor object.
     * @return bytesSentRateHWMTime Date
     */
    public Date getBytesSentRateHWMTime()
    {
        if( bytesSentRateHWMTime == null &&  bytesSentRateHWMTimeMillis > 0)
        {
        	 bytesSentRateHWMTime = new Date( bytesSentRateHWMTimeMillis);
        }
        return  bytesSentRateHWMTime;
    }
    
    public double getBytesReceivedRateHWM()
    {
        return this.bytesReceivedRateHWM;
    }

    /**
     * Returns bytesReceivedRateHWM Double object for the network connection instrumentor object.
     * @return bytesReceivedRateHWMDouble 
     */
    public Double getBytesReceivedRateHWMDouble()
    {
        if (bytesReceivedRateHWMDouble == null)
        {
            bytesReceivedRateHWMDouble = new Double(bytesReceivedRateHWM);
        }
        return bytesReceivedRateHWMDouble;
    }

    public long getBytesReceivedRateHWMTimeMillis()
    {
        return bytesReceivedRateHWMTimeMillis;
    }

    /**
     * Returns bytesReceivedRateHWMTime Date value for the network connection instrumentor object.
     * @return bytesReceivedRateHWMTime Date
     */
    public Date getBytesReceivedRateHWMTime()
    {
        if( bytesReceivedRateHWMTime == null &&  bytesReceivedRateHWMTimeMillis > 0)
        {
        	 bytesReceivedRateHWMTime = new Date( bytesReceivedRateHWMTimeMillis);
        }
        return  bytesReceivedRateHWMTime;
    }

    public double getMsgsSentRateHWM()
    {
        return this.msgsSentRateHWM;
    }

    /**
     * Returns msgsSentRateHWM Double object for the network connection instrumentor object.
     * @return msgsSentRateHWMDouble 
     */
    public Double getMsgsSentRateHWMDouble()
    {
        if (msgsSentRateHWMDouble == null)
        {
            msgsSentRateHWMDouble = new Double(msgsSentRateHWM);
        }
        return msgsSentRateHWMDouble;
    }

    public long getMsgsSentRateHWMTimeMillis()
    {
        return msgsSentRateHWMTimeMillis;
    }

    /**
     * Returns msgsSentRateHWMTime Date value for the network connection instrumentor object.
     * @return msgsSentRateHWMTime Date
     */
    public Date getMsgsSentRateHWMTime()
    {
        if( msgsSentRateHWMTime == null &&  msgsSentRateHWMTimeMillis > 0)
        {
        	 msgsSentRateHWMTime = new Date( msgsSentRateHWMTimeMillis);
        }
        return  msgsSentRateHWMTime;
    }

    public double getMsgsReceivedRateHWM()
    {
        return this.msgsReceivedRateHWM;
    }

    /**
     * Returns msgsReceivedRateHWM Double object for the network connection instrumentor object.
     * @return msgsReceivedRateHWMDouble 
     */
    public Double getMsgsReceivedRateHWMDouble()
    {
        if (msgsReceivedRateHWMDouble == null)
        {
            msgsReceivedRateHWMDouble = new Double(msgsReceivedRateHWM);
        }
        return msgsReceivedRateHWMDouble;
    }

    public long getMsgsReceivedRateHWMTimeMillis()
    {
        return msgsReceivedRateHWMTimeMillis;
    }

    /**
     * Returns msgsReceivedRateHWMTime Date value for the network connection instrumentor object.
     * @return msgsReceivedRateHWMTime Date
     */
    public Date getMsgsReceivedRateHWMTime()
    {
        if( msgsReceivedRateHWMTime == null &&  msgsReceivedRateHWMTimeMillis > 0)
        {
        	 msgsReceivedRateHWMTime = new Date( msgsReceivedRateHWMTimeMillis);
        }
        return  msgsReceivedRateHWMTime;
    }

    public double getPacketsSentRateHWM()
    {
        return this.packetsSentRateHWM;
    }

    /**
     * Returns packetsSentRateHWM Double object for the network connection instrumentor object.
     * @return packetsSentRateHWMDouble 
     */
    public Double getPacketsSentRateHWMDouble()
    {
        if (packetsSentRateHWMDouble == null)
        {
            packetsSentRateHWMDouble = new Double(packetsSentRateHWM);
        }
        return packetsSentRateHWMDouble;
    }

    public long getPacketsSentRateHWMTimeMillis()
    {
        return packetsSentRateHWMTimeMillis;
    }

    /**
     * Returns packetsSentRateHWMTime Date value for the network connection instrumentor object.
     * @return packetsSentRateHWMTime Date
     */
    public Date getPacketsSentRateHWMTime()
    {
        if( packetsSentRateHWMTime == null &&  packetsSentRateHWMTimeMillis > 0)
        {
        	 packetsSentRateHWMTime = new Date( packetsSentRateHWMTimeMillis);
        }
        return  packetsSentRateHWMTime;
    }

    public double getPacketsReceivedRateHWM()
    {
        return this.packetsReceivedRateHWM;
    }

    /**
     * Returns packetsReceivedRateHWM Double object for the network connection instrumentor object.
     * @return packetsReceivedRateHWMDouble 
     */
    public Double getPacketsReceivedRateHWMDouble()
    {
        if (packetsReceivedRateHWMDouble == null)
        {
            packetsReceivedRateHWMDouble = new Double(packetsReceivedRateHWM);
        }
        return packetsReceivedRateHWMDouble;
    }

    public long getPacketsReceivedRateHWMTimeMillis()
    {
        return packetsReceivedRateHWMTimeMillis;
    }

    /**
     * Returns packetsReceivedRateHWMTime Date value for the network connection instrumentor object.
     * @return packetsReceivedRateHWMTime Date
     */
    public Date getPacketsReceivedRateHWMTime()
    {
        if( packetsReceivedRateHWMTime == null &&  packetsReceivedRateHWMTimeMillis > 0)
        {
        	 packetsReceivedRateHWMTime = new Date( packetsReceivedRateHWMTimeMillis);
        }
        return  packetsReceivedRateHWMTime;
    }
    
    /**
     *  Clears the data elements of the instrumentor.
     */
    public void clearData()
    {
        this.bytesSent = 0;
        this.bytesReceived = 0;
        this.msgsSent = 0;
        this.msgsReceived = 0;
        this.packetsSent = 0;
        this.packetsReceived = 0;
        this.invalidPacketsReceived = 0;
        this.garbageBytesReceived = 0;
        this.connects = 0;
        this.disconnects = 0;
        this.exceptions = 0;
        this.status = 0;

        this.peakBytesSent = 0;
        this.peakBytesReceived = 0;
        this.peakMsgsSent = 0;
        this.peakMsgsReceived = 0;
        this.peakPacketsSent = 0;
        this.peakPacketsReceived = 0;
        this.peakInvalidPacketsReceived = 0;
        this.peakGarbageBytesReceived = 0;
        this.peakConnects = 0;
        this.peakDisconnects = 0;
        this.peakExceptions = 0;
        this.intervalBytesSent = 0;
        this.intervalBytesReceived = 0;
        this.intervalMsgsSent = 0;
        this.intervalMsgsReceived = 0;
        this.intervalPacketsSent = 0;
        this.intervalPacketsReceived = 0;
        this.intervalInvalidPacketsReceived = 0;
        this.intervalGarbageBytesReceived = 0;
        this.intervalConnects = 0;
        this.intervalDisconnects = 0;
        this.intervalExceptions = 0;
        this.peakBytesSentRate = 0.0;
        this.peakBytesReceivedRate = 0.0;
        this.peakMsgsSentRate = 0.0;
        this.peakMsgsReceivedRate = 0.0;
        this.peakPacketsSentRate = 0.0;
        this.peakPacketsReceivedRate = 0.0;
        this.peakInvalidPacketsReceivedRate = 0.0;
        this.peakGarbageBytesReceivedRate = 0.0;
        this.peakConnectsRate = 0.0;
        this.peakDisconnectsRate = 0.0;
        this.peakExceptionsRate = 0.0;
        this.avgBytesSentRate = 0.0;
        this.avgBytesReceivedRate = 0.0;
        this.avgMsgsSentRate = 0.0;
        this.avgMsgsReceivedRate = 0.0;
        this.avgPacketsSentRate = 0.0;
        this.avgPacketsReceivedRate = 0.0;
        this.avgInvalidPacketsReceivedRate = 0.0;
        this.avgGarbageBytesReceivedRate = 0.0;
        this.avgConnectsRate = 0.0;
        this.avgDisconnectsRate = 0.0;
        this.avgExceptionsRate = 0.0;
        this.clearDataObjects();
    }


    /**
     *  Increment the instrumentor with the values from
     *  the instrumentor passed in.
     */
    public void instrumentorPlusPlus(CalculatedNetworkConnectionInstrumentor networkConnectionInstrumentor)
    {
        this.bytesSent += networkConnectionInstrumentor.getBytesSent();
        this.bytesReceived += networkConnectionInstrumentor.getBytesReceived();
        this.msgsSent += networkConnectionInstrumentor.getMsgsSent();
        this.msgsReceived += networkConnectionInstrumentor.getMsgsReceived();
        this.packetsSent += networkConnectionInstrumentor.getPacketsSent();
        this.packetsReceived += networkConnectionInstrumentor.getPacketsReceived();
        this.invalidPacketsReceived += networkConnectionInstrumentor.getInvalidPacketsReceived();
        this.garbageBytesReceived += networkConnectionInstrumentor.getGarbageBytesReceived();
        this.connects += networkConnectionInstrumentor.getConnects();
        this.disconnects += networkConnectionInstrumentor.getDisconnects();
        this.exceptions += networkConnectionInstrumentor.getExceptions();


        // Set the status to the worst status
        short niStatus = networkConnectionInstrumentor.getStatus();
        if (niStatus != ProcessStatus.NOT_REPORTED)
        {
            if (status == ProcessStatus.NOT_REPORTED)
            {
                if (niStatus == ProcessStatus.THREAD_NOT_STARTED || niStatus == ProcessStatus.YELLOW)
                {
                    status = ProcessStatus.YELLOW;
                }
                else if (niStatus == ProcessStatus.THREAD_EXITED || niStatus == ProcessStatus.DOWN || niStatus == ProcessStatus.RED)
                {
                    status = ProcessStatus.RED;
                }
                else
                {
                    status = ProcessStatus.GREEN;
                }

            }
            else if (status == ProcessStatus.GREEN)
            {
                if (niStatus == ProcessStatus.THREAD_NOT_STARTED)
                {
                    status = ProcessStatus.YELLOW;
                }
                else if (niStatus == ProcessStatus.THREAD_EXITED || niStatus == ProcessStatus.DOWN)
                {
                    status = ProcessStatus.RED;
                }
            }
            else if (status == ProcessStatus.YELLOW)
            {
                if (niStatus == ProcessStatus.THREAD_EXITED || niStatus == ProcessStatus.DOWN)
                {
                    status = ProcessStatus.RED;
                }
            }
        }

        if (networkConnectionInstrumentor.getLastUpdatedTimeMillis() > lastUpdatedTimeMillis)
        {
            lastUpdatedTimeMillis = networkConnectionInstrumentor.getLastUpdatedTimeMillis();
        }

        if (networkConnectionInstrumentor.getLastTimeSentMillis() > lastTimeSentMillis)
        {
            lastTimeSentMillis = networkConnectionInstrumentor.getLastTimeSentMillis();
        }
        if (networkConnectionInstrumentor.getLastTimeReceivedMillis() > lastTimeReceivedMillis)
        {
            lastTimeReceivedMillis = networkConnectionInstrumentor.getLastTimeReceivedMillis();
        }
        if (networkConnectionInstrumentor.getLastConnectTimeMillis() > lastConnectTimeMillis)
        {
            lastConnectTimeMillis = networkConnectionInstrumentor.getLastConnectTimeMillis();
        }
        if (networkConnectionInstrumentor.getLastDisconnectTimeMillis() > lastDisconnectTimeMillis)
        {
            lastDisconnectTimeMillis = networkConnectionInstrumentor.getLastDisconnectTimeMillis();
        }
        if (networkConnectionInstrumentor.getLastExceptionTimeMillis() > lastExceptionTimeMillis)
        {
            lastExceptionTimeMillis = networkConnectionInstrumentor.getLastExceptionTimeMillis();
        }

        if (networkConnectionInstrumentor.getPeakBytesSent() > peakBytesSent)
        {
            this.peakBytesSent = networkConnectionInstrumentor.getPeakBytesSent();
        }
        if (networkConnectionInstrumentor.getPeakBytesReceived() > peakBytesReceived)
        {
            this.peakBytesReceived = networkConnectionInstrumentor.getPeakBytesReceived();
        }
        if (networkConnectionInstrumentor.getPeakMsgsSent() > peakMsgsSent)
        {
            this.peakMsgsSent = networkConnectionInstrumentor.getPeakMsgsSent();
        }
        if (networkConnectionInstrumentor.getPeakMsgsReceived() > peakMsgsReceived)
        {
            this.peakMsgsReceived = networkConnectionInstrumentor.getPeakMsgsReceived();
        }
        if (networkConnectionInstrumentor.getPeakPacketsSent() > peakPacketsSent)
        {
            this.peakPacketsSent = networkConnectionInstrumentor.getPeakPacketsSent();
        }
        if (networkConnectionInstrumentor.getPeakPacketsReceived() > peakPacketsReceived)
        {
            this.peakPacketsReceived = networkConnectionInstrumentor.getPeakPacketsReceived();
        }
        if (networkConnectionInstrumentor.getPeakInvalidPacketsReceived() > peakInvalidPacketsReceived)
        {
            this.peakInvalidPacketsReceived = networkConnectionInstrumentor.getPeakInvalidPacketsReceived();
        }
        if (networkConnectionInstrumentor.getPeakGarbageBytesReceived() > peakGarbageBytesReceived)
        {
            this.peakGarbageBytesReceived = networkConnectionInstrumentor.getPeakGarbageBytesReceived();
        }
        if (networkConnectionInstrumentor.getPeakConnects() > peakConnects)
        {
            this.peakConnects = networkConnectionInstrumentor.getPeakConnects();
        }
        if (networkConnectionInstrumentor.getPeakDisconnects() > peakDisconnects)
        {
            this.peakDisconnects = networkConnectionInstrumentor.getPeakDisconnects();
        }
        if (networkConnectionInstrumentor.getPeakExceptions() > peakExceptions)
        {
            this.peakExceptions = networkConnectionInstrumentor.getPeakExceptions();
        }

        this.intervalBytesSent += networkConnectionInstrumentor.getIntervalBytesSent();
        this.intervalBytesReceived += networkConnectionInstrumentor.getIntervalBytesReceived();
        this.intervalMsgsSent += networkConnectionInstrumentor.getIntervalMsgsSent();
        this.intervalMsgsReceived += networkConnectionInstrumentor.getIntervalMsgsReceived();
        this.intervalPacketsSent += networkConnectionInstrumentor.getIntervalPacketsSent();
        this.intervalPacketsReceived += networkConnectionInstrumentor.getIntervalPacketsReceived();
        this.intervalInvalidPacketsReceived += networkConnectionInstrumentor.getIntervalInvalidPacketsReceived();
        this.intervalGarbageBytesReceived += networkConnectionInstrumentor.getIntervalGarbageBytesReceived();
        this.intervalConnects += networkConnectionInstrumentor.getIntervalConnects();
        this.intervalDisconnects += networkConnectionInstrumentor.getIntervalDisconnects();
        this.intervalExceptions += networkConnectionInstrumentor.getIntervalExceptions();

        if (networkConnectionInstrumentor.getPeakBytesSentRate() > peakBytesSentRate)
        {
            this.peakBytesSentRate = networkConnectionInstrumentor.getPeakBytesSentRate();
        }
        if (networkConnectionInstrumentor.getPeakBytesReceivedRate() > peakBytesReceivedRate)
        {
            this.peakBytesReceivedRate = networkConnectionInstrumentor.getPeakBytesReceivedRate();
        }
        if (networkConnectionInstrumentor.getPeakMsgsSentRate() > peakMsgsSentRate)
        {
            this.peakMsgsSentRate = networkConnectionInstrumentor.getPeakMsgsSentRate();
        }
        if (networkConnectionInstrumentor.getPeakMsgsReceivedRate() > peakMsgsReceivedRate)
        {
            this.peakMsgsReceivedRate = networkConnectionInstrumentor.getPeakMsgsReceivedRate();
        }
        if (networkConnectionInstrumentor.getPeakPacketsSentRate() > peakPacketsSentRate)
        {
            this.peakPacketsSentRate = networkConnectionInstrumentor.getPeakPacketsSentRate();
        }
        if (networkConnectionInstrumentor.getPeakPacketsReceivedRate() > peakPacketsReceivedRate)
        {
            this.peakPacketsReceivedRate = networkConnectionInstrumentor.getPeakPacketsReceivedRate();
        }
        if (networkConnectionInstrumentor.getPeakInvalidPacketsReceivedRate() > peakInvalidPacketsReceivedRate)
        {
            this.peakInvalidPacketsReceivedRate = networkConnectionInstrumentor.getPeakInvalidPacketsReceivedRate();
        }
        if (networkConnectionInstrumentor.getPeakGarbageBytesReceivedRate() > peakGarbageBytesReceivedRate)
        {
            this.peakGarbageBytesReceivedRate = networkConnectionInstrumentor.getPeakGarbageBytesReceivedRate();
        }
        if (networkConnectionInstrumentor.getPeakConnectsRate() > peakConnectsRate)
        {
            this.peakConnectsRate = networkConnectionInstrumentor.getPeakConnectsRate();
        }
        if (networkConnectionInstrumentor.getPeakDisconnectsRate() > peakDisconnectsRate)
        {
            this.peakDisconnectsRate = networkConnectionInstrumentor.getPeakDisconnectsRate();
        }
        if (networkConnectionInstrumentor.getPeakExceptionsRate() > peakExceptionsRate)
        {
            this.peakExceptionsRate = networkConnectionInstrumentor.getPeakExceptionsRate();
        }

        this.avgBytesSentRate += networkConnectionInstrumentor.getAvgBytesSentRate();
        this.avgBytesReceivedRate += networkConnectionInstrumentor.getAvgBytesReceivedRate();
        this.avgMsgsSentRate += networkConnectionInstrumentor.getAvgMsgsSentRate();
        this.avgMsgsReceivedRate += networkConnectionInstrumentor.getAvgMsgsReceivedRate();
        this.avgPacketsSentRate += networkConnectionInstrumentor.getAvgPacketsSentRate();
        this.avgPacketsReceivedRate += networkConnectionInstrumentor.getAvgPacketsReceivedRate();
        this.avgInvalidPacketsReceivedRate += networkConnectionInstrumentor.getAvgInvalidPacketsReceivedRate();
        this.avgGarbageBytesReceivedRate += networkConnectionInstrumentor.getAvgGarbageBytesReceivedRate();
        this.avgConnectsRate += networkConnectionInstrumentor.getAvgConnectsRate();
        this.avgDisconnectsRate += networkConnectionInstrumentor.getAvgDisconnectsRate();
        this.avgExceptionsRate += networkConnectionInstrumentor.getAvgExceptionsRate();

        long now = System.currentTimeMillis();	// set to the current time in millis
        
        if (this.avgBytesSentRate > bytesSentRateHWM)
        {
            this.bytesSentRateHWM = this.avgBytesSentRate;
            this.bytesSentRateHWMTimeMillis = now;
        }
        if (this.avgBytesReceivedRate > bytesReceivedRateHWM)
        {
            this.bytesReceivedRateHWM = this.avgBytesReceivedRate;
            this.bytesReceivedRateHWMTimeMillis = now;
        }
        if (this.avgMsgsSentRate > msgsSentRateHWM)
        {
            this.msgsSentRateHWM = this.avgMsgsSentRate;
            this.msgsSentRateHWMTimeMillis = now;
        }
        if (this.avgMsgsReceivedRate > msgsReceivedRateHWM)
        {
            this.msgsReceivedRateHWM = this.avgMsgsReceivedRate;
            this.msgsReceivedRateHWMTimeMillis = now;
        }
        if (this.avgPacketsSentRate > packetsSentRateHWM)
        {
            this.packetsSentRateHWM = this.avgPacketsSentRate;
            this.packetsSentRateHWMTimeMillis = now;
        }
        if (this.avgPacketsReceivedRate > packetsReceivedRateHWM)
        {
            this.packetsReceivedRateHWM = this.avgPacketsReceivedRate;
            this.packetsReceivedRateHWMTimeMillis = now;
        }
        
        this.userData = null;
        
        this.clearDataObjects();
    }

    /**
     *  Clone all the parts of the object.  Clone is used in a very
     *  heavy use method, so not everything is cloned.
     */
    public Object clone() throws CloneNotSupportedException
    {
        NetworkConnectionInstrumentorImpl networkConnectionInstrumentor = new NetworkConnectionInstrumentorImpl();

        networkConnectionInstrumentor.userData = getUserData();
        networkConnectionInstrumentor.name = getName();
        networkConnectionInstrumentor.instrumentorKey = getInstrumentorKey();
        networkConnectionInstrumentor.orbName = getOrbName();
        networkConnectionInstrumentor.clusterName = getClusterName();
        networkConnectionInstrumentor.lastUpdatedTimeMillis = getLastUpdatedTimeMillis();

        networkConnectionInstrumentor.setInstrumentedData(this);

        networkConnectionInstrumentor.clearDataObjects();

        return networkConnectionInstrumentor;
    }
    public void setInstrumentedData(CalculatedNetworkConnectionInstrumentor networkConnectionInstrumentor)
    {
        this.lastUpdatedTimeMillis = networkConnectionInstrumentor.getLastUpdatedTimeMillis();
        this.lastUpdatedTime = null;

        this.bytesSent = networkConnectionInstrumentor.getBytesSent();
        this.bytesReceived = networkConnectionInstrumentor.getBytesReceived();
        this.msgsSent = networkConnectionInstrumentor.getMsgsSent();
        this.msgsReceived = networkConnectionInstrumentor.getMsgsReceived();
        this.packetsSent = networkConnectionInstrumentor.getPacketsSent();
        this.packetsReceived = networkConnectionInstrumentor.getPacketsReceived();
        this.invalidPacketsReceived = networkConnectionInstrumentor.getInvalidPacketsReceived();
        this.garbageBytesReceived = networkConnectionInstrumentor.getGarbageBytesReceived();
        this.connects = networkConnectionInstrumentor.getConnects();
        this.disconnects = networkConnectionInstrumentor.getDisconnects();
        this.exceptions = networkConnectionInstrumentor.getExceptions();

        this.lastTimeSentMillis = networkConnectionInstrumentor.getLastTimeSentMillis();
        this.lastTimeReceivedMillis = networkConnectionInstrumentor.getLastTimeReceivedMillis();
        this.lastConnectTimeMillis = networkConnectionInstrumentor.getLastConnectTimeMillis();
        this.lastDisconnectTimeMillis = networkConnectionInstrumentor.getLastDisconnectTimeMillis();
        this.lastExceptionTimeMillis = networkConnectionInstrumentor.getLastExceptionTimeMillis();
        this.lastTimeSentMillis = networkConnectionInstrumentor.getLastTimeSentMillis();

        this.lastException = networkConnectionInstrumentor.getLastException();
        this.status = networkConnectionInstrumentor.getStatus();

        this.peakBytesSent = networkConnectionInstrumentor.getPeakBytesSent();
        this.peakBytesReceived = networkConnectionInstrumentor.getPeakBytesReceived();
        this.peakMsgsSent = networkConnectionInstrumentor.getPeakMsgsSent();
        this.peakMsgsReceived = networkConnectionInstrumentor.getPeakMsgsReceived();
        this.peakPacketsSent = networkConnectionInstrumentor.getPeakPacketsSent();
        this.peakPacketsReceived = networkConnectionInstrumentor.getPeakPacketsReceived();
        this.peakInvalidPacketsReceived = networkConnectionInstrumentor.getPeakInvalidPacketsReceived();
        this.peakGarbageBytesReceived = networkConnectionInstrumentor.getPeakGarbageBytesReceived();
        this.peakConnects = networkConnectionInstrumentor.getPeakConnects();
        this.peakDisconnects = networkConnectionInstrumentor.getPeakDisconnects();
        this.peakExceptions = networkConnectionInstrumentor.getPeakExceptions();
        this.intervalBytesSent = networkConnectionInstrumentor.getIntervalBytesSent();
        this.intervalBytesReceived = networkConnectionInstrumentor.getIntervalBytesReceived();
        this.intervalMsgsSent = networkConnectionInstrumentor.getIntervalMsgsSent();
        this.intervalMsgsReceived = networkConnectionInstrumentor.getIntervalMsgsReceived();
        this.intervalPacketsSent = networkConnectionInstrumentor.getIntervalPacketsSent();
        this.intervalPacketsReceived = networkConnectionInstrumentor.getIntervalPacketsReceived();
        this.intervalInvalidPacketsReceived = networkConnectionInstrumentor.getIntervalInvalidPacketsReceived();
        this.intervalGarbageBytesReceived = networkConnectionInstrumentor.getIntervalGarbageBytesReceived();
        this.intervalConnects = networkConnectionInstrumentor.getIntervalConnects();
        this.intervalDisconnects = networkConnectionInstrumentor.getIntervalDisconnects();
        this.intervalExceptions = networkConnectionInstrumentor.getIntervalExceptions();
        this.peakBytesSentRate = networkConnectionInstrumentor.getPeakBytesSentRate();
        this.peakBytesReceivedRate = networkConnectionInstrumentor.getPeakBytesReceivedRate();
        this.peakMsgsSentRate = networkConnectionInstrumentor.getPeakMsgsSentRate();
        this.peakMsgsReceivedRate = networkConnectionInstrumentor.getPeakMsgsReceivedRate();
        this.peakPacketsSentRate  = networkConnectionInstrumentor.getPeakPacketsSentRate();
        this.peakPacketsReceivedRate = networkConnectionInstrumentor.getPeakPacketsReceivedRate();
        this.peakInvalidPacketsReceivedRate = networkConnectionInstrumentor.getPeakInvalidPacketsReceivedRate();
        this.peakGarbageBytesReceivedRate = networkConnectionInstrumentor.getPeakGarbageBytesReceivedRate();
        this.peakConnectsRate = networkConnectionInstrumentor.getPeakConnectsRate();
        this.peakDisconnectsRate = networkConnectionInstrumentor.getPeakDisconnectsRate();
        this.peakExceptionsRate = networkConnectionInstrumentor.getPeakExceptionsRate();
        this.avgBytesSentRate = networkConnectionInstrumentor.getAvgBytesSentRate();
        this.avgBytesReceivedRate = networkConnectionInstrumentor.getAvgBytesReceivedRate();
        this.avgMsgsSentRate = networkConnectionInstrumentor.getAvgMsgsSentRate();
        this.avgMsgsReceivedRate = networkConnectionInstrumentor.getAvgMsgsReceivedRate();
        this.avgPacketsSentRate = networkConnectionInstrumentor.getAvgPacketsSentRate();
        this.avgPacketsReceivedRate = networkConnectionInstrumentor.getAvgPacketsReceivedRate();
        this.avgInvalidPacketsReceivedRate = networkConnectionInstrumentor.getAvgInvalidPacketsReceivedRate();
        this.avgGarbageBytesReceivedRate = networkConnectionInstrumentor.getAvgGarbageBytesReceivedRate();
        this.avgConnectsRate = networkConnectionInstrumentor.getAvgConnectsRate();
        this.avgDisconnectsRate = networkConnectionInstrumentor.getAvgDisconnectsRate();
        this.avgExceptionsRate = networkConnectionInstrumentor.getAvgExceptionsRate();

        this.bytesSentRateHWM = networkConnectionInstrumentor.getBytesSentRateHWM();
        this.bytesReceivedRateHWM = networkConnectionInstrumentor.getBytesReceivedRateHWM();
        this.msgsSentRateHWM = networkConnectionInstrumentor.getMsgsSentRateHWM();
        this.msgsReceivedRateHWM = networkConnectionInstrumentor.getMsgsReceivedRateHWM();
        this.packetsSentRateHWM  = networkConnectionInstrumentor.getPacketsSentRateHWM();
        this.packetsReceivedRateHWM = networkConnectionInstrumentor.getPacketsReceivedRateHWM();
        this.bytesSentRateHWMTimeMillis = networkConnectionInstrumentor.getBytesSentRateHWMTimeMillis();
        this.bytesReceivedRateHWMTimeMillis = networkConnectionInstrumentor.getBytesReceivedRateHWMTimeMillis();
        this.msgsSentRateHWMTimeMillis = networkConnectionInstrumentor.getMsgsSentRateHWMTimeMillis();
        this.msgsReceivedRateHWMTimeMillis = networkConnectionInstrumentor.getMsgsReceivedRateHWMTimeMillis();
        this.packetsSentRateHWMTimeMillis = networkConnectionInstrumentor.getPacketsSentRateHWMTimeMillis();
        this.packetsReceivedRateHWMTimeMillis = networkConnectionInstrumentor.getPacketsReceivedRateHWMTimeMillis();
        
        this.clearDataObjects();
    }

    public long getLastTimeSentMillis()
    {
        return lastTimeSentMillis;
    }

    public long getLastTimeReceivedMillis()
    {
        return lastTimeReceivedMillis;
    }

    public long getLastConnectTimeMillis()
    {
        return lastConnectTimeMillis;
    }

    public long getLastDisconnectTimeMillis()
    {
        return lastDisconnectTimeMillis;
    }

    public long getLastExceptionTimeMillis()
    {
        return lastExceptionTimeMillis;
    }


    public Long getBytesSentLong()
    {
        if (bytesSentLong == null)
        {
            bytesSentLong = new Long(bytesSent);
        }
        return bytesSentLong;
    }
    public Long getBytesReceivedLong()
    {
        if (bytesReceivedLong == null)
        {
            bytesReceivedLong = new Long(bytesReceived);
        }
        return bytesReceivedLong;
    }

    public Long getMsgsSentLong()
    {
        if (msgsSentLong == null)
        {
            msgsSentLong = new Long(msgsSent);
        }
        return msgsSentLong;
    }
    public Long getMsgsReceivedLong()
    {
        if (msgsReceivedLong == null)
        {
            msgsReceivedLong = new Long(msgsReceived);
        }
        return msgsReceivedLong;
    }
    public Long getPacketsSentLong()
    {
        if (packetsSentLong == null)
        {
            packetsSentLong = new Long(packetsSent);
        }
        return packetsSentLong;
    }
    public Long getPacketsReceivedLong()
    {
        if (packetsReceivedLong == null)
        {
            packetsReceivedLong = new Long(packetsReceived);
        }
        return packetsReceivedLong;
    }
    public Long getInvalidPacketsReceivedLong()
    {
        if (invalidPacketsReceivedLong == null)
        {
            invalidPacketsReceivedLong = new Long(invalidPacketsReceived);
        }
        return invalidPacketsReceivedLong;
    }
    public Long getGarbageBytesReceivedLong()
    {
        if (garbageBytesReceivedLong == null)
        {
            garbageBytesReceivedLong = new Long(garbageBytesReceived);
        }
        return garbageBytesReceivedLong;
    }
    public Long getConnectsLong()
    {
        if (connectsLong == null)
        {
            connectsLong = new Long(connects);
        }
        return connectsLong;
    }
    public Long getDisconnectsLong()
    {
        if (disconnectsLong == null)
        {
            disconnectsLong = new Long(disconnects);
        }
        return disconnectsLong;
    }
    public Long getExceptionsLong()
    {
        if (exceptionsLong == null)
        {
            exceptionsLong = new Long(exceptions);
        }
        return exceptionsLong;
    }
    public Short getStatusShort()
    {
        if (statusShort == null)
        {
            statusShort = new Short(status);
        }
        return statusShort;
    }
    public Long getPeakBytesSentLong()
    {
        if (peakBytesSentLong == null)
        {
            peakBytesSentLong = new Long(peakBytesSent);
        }
        return peakBytesSentLong;
    }
    public Long getPeakBytesReceivedLong()
    {
        if (peakBytesReceivedLong == null)
        {
            peakBytesReceivedLong = new Long(peakBytesReceived);
        }
        return peakBytesReceivedLong;
    }
    public Long getPeakMsgsSentLong()
    {
        if (peakMsgsSentLong == null)
        {
            peakMsgsSentLong = new Long(peakMsgsSent);
        }
        return peakMsgsSentLong;
    }
    public Long getPeakMsgsReceivedLong()
    {
        if (peakMsgsReceivedLong == null)
        {
            peakMsgsReceivedLong = new Long(peakMsgsReceived);
        }
        return peakMsgsReceivedLong;
    }
    public Long getPeakPacketsSentLong()
    {
        if (peakPacketsSentLong == null)
        {
            peakPacketsSentLong = new Long(peakPacketsSent);
        }
        return peakPacketsSentLong;
    }
    public Long getPeakPacketsReceivedLong()
    {
        if (peakPacketsReceivedLong == null)
        {
            peakPacketsReceivedLong = new Long(peakPacketsReceived);
        }
        return peakPacketsReceivedLong;
    }
    public Long getPeakInvalidPacketsReceivedLong()
    {
        if (peakInvalidPacketsReceivedLong == null)
        {
            peakInvalidPacketsReceivedLong = new Long(peakInvalidPacketsReceived);
        }
        return peakInvalidPacketsReceivedLong;
    }
    public Long getPeakGarbageBytesReceivedLong()
    {
        if (peakGarbageBytesReceivedLong == null)
        {
            peakGarbageBytesReceivedLong = new Long(peakGarbageBytesReceived);
        }
        return peakGarbageBytesReceivedLong;
    }

    public Long getPeakConnectsLong()
    {
        if (peakConnectsLong == null)
        {
            peakConnectsLong = new Long(peakConnects);
        }
        return peakConnectsLong;
    }

    public Long getPeakDisconnectsLong()
    {
        if (peakDisconnectsLong == null)
        {
            peakDisconnectsLong = new Long(peakDisconnects);
        }
        return peakDisconnectsLong;
    }
    public Long getPeakExceptionsLong()
    {
        if (peakExceptionsLong == null)
        {
            peakExceptionsLong = new Long(peakExceptions);
        }
        return peakExceptionsLong;
    }

    public Long getIntervalBytesSentLong()
    {
        if (intervalBytesSentLong == null)
        {
            intervalBytesSentLong = new Long(intervalBytesSent);
        }
        return intervalBytesSentLong;
    }
    public Double getPeakBytesSentRateDouble()
    {
        if (peakBytesSentRateDouble == null)
        {
            peakBytesSentRateDouble = new Double(peakBytesSentRate);
        }
        return peakBytesSentRateDouble;
    }
    public Double getAvgBytesSentRateDouble()
    {
        if (avgBytesSentRateDouble == null)
        {
            avgBytesSentRateDouble = new Double(avgBytesSentRate);
        }
        return avgBytesSentRateDouble;
    }
    public Long getIntervalBytesReceivedLong()
    {
        if (intervalBytesReceivedLong == null)
        {
            intervalBytesReceivedLong = new Long(intervalBytesReceived);
        }
        return intervalBytesReceivedLong;
    }
    public Double getPeakBytesReceivedRateDouble()
    {
        if (peakBytesReceivedRateDouble == null)
        {
            peakBytesReceivedRateDouble = new Double(peakBytesReceivedRate);
        }
        return peakBytesReceivedRateDouble;
    }
    public Double getAvgBytesReceivedRateDouble()
    {
        if (avgBytesReceivedRateDouble == null)
        {
            avgBytesReceivedRateDouble = new Double(avgBytesReceivedRate);
        }
        return avgBytesReceivedRateDouble;
    }
    public Long getIntervalMsgsSentLong()
    {
        if (intervalMsgsSentLong == null)
        {
            intervalMsgsSentLong = new Long(intervalMsgsSent);
        }
        return intervalMsgsSentLong;
    }
    public Double getPeakMsgsSentRateDouble()
    {
        if (peakMsgsSentRateDouble == null)
        {
            peakMsgsSentRateDouble = new Double(peakMsgsSentRate);
        }
        return peakMsgsSentRateDouble;
    }
    public Double getAvgMsgsSentRateDouble()
    {
        if (avgMsgsSentRateDouble == null)
        {
            avgMsgsSentRateDouble = new Double(avgMsgsSentRate);
        }
        return avgMsgsSentRateDouble;
    }
    public Long getIntervalMsgsReceivedLong()
    {
        if (intervalMsgsReceivedLong == null)
        {
            intervalMsgsReceivedLong = new Long(intervalMsgsReceived);
        }
        return intervalMsgsReceivedLong;
    }
    public Double getPeakMsgsReceivedRateDouble()
    {
        if (peakMsgsReceivedRateDouble == null)
        {
            peakMsgsReceivedRateDouble = new Double(peakMsgsReceivedRate);
        }
        return peakMsgsReceivedRateDouble;
    }
    public Double getAvgMsgsReceivedRateDouble()
    {
        if (avgMsgsReceivedRateDouble == null)
        {
            avgMsgsReceivedRateDouble = new Double(avgMsgsReceivedRate);
        }
        return avgMsgsReceivedRateDouble;
    }
    public Long getIntervalPacketsSentLong()
    {
        if (intervalPacketsSentLong == null)
        {
            intervalPacketsSentLong = new Long(intervalPacketsSent);
        }
        return intervalPacketsSentLong;
    }
    public Double getPeakPacketsSentRateDouble()
    {
        if (peakPacketsSentRateDouble == null)
        {
            peakPacketsSentRateDouble = new Double(peakPacketsSentRate);
        }
        return peakPacketsSentRateDouble;
    }
    public Double getAvgPacketsSentRateDouble()
    {
        if (avgPacketsSentRateDouble == null)
        {
            avgPacketsSentRateDouble = new Double(avgPacketsSentRate);
        }
        return avgPacketsSentRateDouble;
    }
    public Long getIntervalPacketsReceivedLong()
    {
        if (intervalPacketsReceivedLong == null)
        {
            intervalPacketsReceivedLong = new Long(intervalPacketsReceived);
        }
        return intervalPacketsReceivedLong;
    }
    public Double getPeakPacketsReceivedRateDouble()
    {
        if (peakPacketsReceivedRateDouble == null)
        {
            peakPacketsReceivedRateDouble = new Double(peakPacketsReceivedRate);
        }
        return peakPacketsReceivedRateDouble;
    }
    public Double getAvgPacketsReceivedRateDouble()
    {
        if (avgPacketsReceivedRateDouble == null)
        {
            avgPacketsReceivedRateDouble = new Double(avgPacketsReceivedRate);
        }
        return avgPacketsReceivedRateDouble;
    }
    public Long getIntervalInvalidPacketsReceivedLong()
    {
        if (intervalInvalidPacketsReceivedLong == null)
        {
            intervalInvalidPacketsReceivedLong = new Long(intervalInvalidPacketsReceived);
        }
        return intervalInvalidPacketsReceivedLong;
    }
    public Double getPeakInvalidPacketsReceivedRateDouble()
    {
        if (peakInvalidPacketsReceivedRateDouble == null)
        {
            peakInvalidPacketsReceivedRateDouble = new Double(peakInvalidPacketsReceivedRate);
        }
        return peakInvalidPacketsReceivedRateDouble;
    }
    public Double getAvgInvalidPacketsReceivedRateDouble()
    {
        if (avgInvalidPacketsReceivedRateDouble == null)
        {
            avgInvalidPacketsReceivedRateDouble = new Double(avgInvalidPacketsReceivedRate);
        }
        return avgInvalidPacketsReceivedRateDouble;
    }
    public Long getIntervalGarbageBytesReceivedLong()
    {
        if (intervalGarbageBytesReceivedLong == null)
        {
            intervalGarbageBytesReceivedLong = new Long(intervalGarbageBytesReceived);
        }
        return intervalGarbageBytesReceivedLong;
    }
    public Double getPeakGarbageBytesReceivedRateDouble()
    {
        if (peakGarbageBytesReceivedRateDouble == null)
        {
            peakGarbageBytesReceivedRateDouble = new Double(peakGarbageBytesReceivedRate);
        }
        return peakGarbageBytesReceivedRateDouble;
    }
    public Double getAvgGarbageBytesReceivedRateDouble()
    {
        if (avgGarbageBytesReceivedRateDouble == null)
        {
            avgGarbageBytesReceivedRateDouble = new Double(avgGarbageBytesReceivedRate);
        }
        return avgGarbageBytesReceivedRateDouble;
    }


    public Long getIntervalConnectsLong()
    {
        if (intervalConnectsLong == null)
        {
            intervalConnectsLong = new Long(intervalConnects);
        }
        return intervalConnectsLong;
    }
    public Double getPeakConnectsRateDouble()
    {
        if (peakConnectsRateDouble == null)
        {
            peakConnectsRateDouble = new Double(peakConnectsRate);
        }
        return peakConnectsRateDouble;
    }
    public Double getAvgConnectsRateDouble()
    {
        if (avgConnectsRateDouble == null)
        {
            avgConnectsRateDouble = new Double(avgConnectsRate);
        }
        return avgConnectsRateDouble;
    }
    public Long getIntervalDisconnectsLong()
    {
        if (intervalDisconnectsLong == null)
        {
            intervalDisconnectsLong = new Long(intervalDisconnects);
        }
        return intervalDisconnectsLong;
    }
    public Double getPeakDisconnectsRateDouble()
    {
        if (peakDisconnectsRateDouble == null)
        {
            peakDisconnectsRateDouble = new Double(peakDisconnectsRate);
        }
        return peakDisconnectsRateDouble;
    }
    public Double getAvgDisconnectsRateDouble()
    {
        if (avgDisconnectsRateDouble == null)
        {
            avgDisconnectsRateDouble = new Double(avgDisconnectsRate);
        }
        return avgDisconnectsRateDouble;
    }
    public Long getIntervalExceptionsLong()
    {
        if (intervalExceptionsLong == null)
        {
            intervalExceptionsLong = new Long(intervalExceptions);
        }
        return intervalExceptionsLong;
    }
    public Double getPeakExceptionsRateDouble()
    {
        if (peakExceptionsRateDouble == null)
        {
            peakExceptionsRateDouble = new Double(peakExceptionsRate);
        }
        return peakExceptionsRateDouble;
    }
    public Double getAvgExceptionsRateDouble()
    {
        if (avgExceptionsRateDouble == null)
        {
            avgExceptionsRateDouble = new Double(avgExceptionsRate);
        }
        return avgExceptionsRateDouble;
    }


    private void clearDataObjects()
    {
        this.bytesSentLong = null;
        this.bytesReceivedLong = null;
        this.msgsSentLong = null;
        this.msgsReceivedLong = null;
        this.packetsSentLong = null;
        this.packetsReceivedLong = null;
        this.invalidPacketsReceivedLong = null;
        this.garbageBytesReceivedLong = null;
        this.connectsLong = null;
        this.disconnectsLong = null;
        this.exceptionsLong = null;

        this.statusShort = null;

        this.peakBytesSentLong = null;
        this.peakBytesReceivedLong = null;
        this.peakMsgsSentLong = null;
        this.peakMsgsReceivedLong = null;
        this.peakPacketsSentLong = null;
        this.peakPacketsReceivedLong = null;
        this.peakInvalidPacketsReceivedLong = null;
        this.peakGarbageBytesReceivedLong = null;
        this.peakConnectsLong = null;
        this.peakDisconnectsLong = null;
        this.peakExceptionsLong = null;
        this.intervalBytesSentLong = null;
        this.intervalBytesReceivedLong = null;
        this.intervalMsgsSentLong = null;
        this.intervalMsgsReceivedLong = null;
        this.intervalPacketsSentLong = null;
        this.intervalPacketsReceivedLong = null;
        this.intervalInvalidPacketsReceivedLong = null;
        this.intervalGarbageBytesReceivedLong = null;
        this.intervalConnectsLong = null;
        this.intervalDisconnectsLong = null;
        this.intervalExceptionsLong = null;
        this.peakBytesSentRateDouble = null;
        this.peakBytesReceivedRateDouble = null;
        this.peakMsgsSentRateDouble = null;
        this.peakMsgsReceivedRateDouble = null;
        this.peakPacketsSentRateDouble = null;
        this.peakPacketsReceivedRateDouble = null;
        this.peakInvalidPacketsReceivedRateDouble = null;
        this.peakGarbageBytesReceivedRateDouble = null;
        this.peakConnectsRateDouble = null;
        this.peakDisconnectsRateDouble = null;
        this.peakExceptionsRateDouble = null;
        this.avgBytesSentRateDouble = null;
        this.avgBytesReceivedRateDouble = null;
        this.avgMsgsSentRateDouble = null;
        this.avgMsgsReceivedRateDouble = null;
        this.avgPacketsSentRateDouble = null;
        this.avgPacketsReceivedRateDouble = null;
        this.avgInvalidPacketsReceivedRateDouble = null;
        this.avgGarbageBytesReceivedRateDouble = null;
        this.avgConnectsRateDouble = null;
        this.avgDisconnectsRateDouble = null;
        this.avgExceptionsRateDouble = null;
        this.bytesSentRateHWMDouble = null;
        this.bytesReceivedRateHWMDouble = null;
        this.msgsSentRateHWMDouble = null;
        this.msgsReceivedRateHWMDouble = null;
        this.packetsSentRateHWMDouble = null;
        this.packetsReceivedRateHWMDouble = null;

        this.lastUpdatedTime = null;
        this.lastTimeSent = null;
        this.lastTimeReceived = null;
        this.lastConnectTime = null;
        this.lastDisconnectTime = null;
        this.lastExceptionTime = null;
        this.lastException = null;
        this.lastExceptionMessage = null;
        
        this.bytesSentRateHWMTime = null;
        this.bytesReceivedRateHWMTime = null;
        this.msgsSentRateHWMTime = null;
        this.msgsReceivedRateHWMTime = null;
        this.packetsSentRateHWMTime = null;
        this.packetsReceivedRateHWMTime = null;
    }

    protected String[] getInfraNames()
    {
        return INFRA_NAMES;
    }


}
