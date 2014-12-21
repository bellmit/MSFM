//
// -----------------------------------------------------------------------------------
// Source file: NodeSamplingTask.java
//
// PACKAGE: com.cboe.infra.presentation.network
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.infra.presentation.network;

import com.cboe.utils.monitoringService.TransportPSSubject;
import com.cboe.utils.monitoringService.EventChannel;

import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerMMBusinessProperty;

import java.util.*;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.NumberFormat;


/**
 *
 */
public class NodeSamplingTask extends TimerTask
{

    SBTLiveNode datasource;

    // static snapshots of datasource taken at different times:
    SBTLiveNode initialData;

    SBTLiveNode lastData;
    long lastSample = 0;
    private static final int RATE_MSGS_RECV_HWM = 0;
    private static final int RATE_BYTES_RECV_HWM = 1;
    private static final int RATE_MSGS_SENT_HWM = 2;
    private static final int RATE_BYTES_SENT_HWM = 3;

    Map highWaterMarks = new HashMap();

    private long clntQdMsgsHWM = 0;
    private long clntQdBytesHWM = 0;
    private long clntRdBufferHWM = 0;
    private long clntWrtBufferHWM = 0;
    private long srvrQdMsgsHWM = 0;
    private long srvrQdBytesHWM = 0;
    private long srvrRdBufferHWM = 0;
    private long srvrWrtBufferHWM = 0;


    Collection destinations = new ArrayList();
    NumberFormat fmt;
    String taskIdentifier;


    public NodeSamplingTask(SBTLiveNode node, OutputStream sink)
    {
        this(node);
        addDestinationImpl(sink);
    }

    public NodeSamplingTask(SBTLiveNode node)
    {
        datasource = node;
        initialData = (SBTLiveNode) datasource.clone();
        fmt = NumberFormat.getInstance();
        fmt.setMaximumFractionDigits(3);

        if (GUILoggerHome.find().isPropertyOn(GUILoggerMMBusinessProperty.RECORDING))
        {
            destinations.add( System.out );
        }
    }


    public void addDestination(OutputStream sink)
    {
        addDestinationImpl(sink);
    }

    /**
     * The action to be performed by this timer task.
     */
    public void run()
    {
        // do nothing without an audience
        if ( destinations.size() == 0 )
        {
            return;
        }

        long currentSample = System.currentTimeMillis();
        boolean doDeltas = false;
        if ( lastData != null )
        {
            doDeltas = true;
        }
        // by default record the following:
        // Sampling parameters:
        //      Sample interval  (??)
        //      Sampling duration (??)
        //      (this-sample) date-time
        // Node general properties:
        //      Name
        //      Host
        //      RT Server
        //      Status
        //      Client-side queues & buffers
        //      Server-side queues & buffers
        // For each channel:
        //      Name
        //      All Filters
        // For each subject:
        //      Messages Sent, Rate(last), Rate(Cumulative)
        //      Bytes Sent, Rate(last), Rate(Cumulative)
        //      Messages Received, Rate(last), Rate(Cumulative)
        //      Bytes Received, Rate(last), Rate(Cumulative)
        Date sampleTs = new Date();
        StringBuffer logEntry = new StringBuffer(5000);
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug("SAMPLING AT " + sampleTs,GUILoggerMMBusinessProperty.RECORDING);
        }
        logEntry.append( "\nSample @ = " ).append( sampleTs ).append("\n");
        if ( lastData == null )
        {
            logNodeHeader(logEntry);
            logEntry.append("\n\t~~~~~~~~~~ FILTER DATA ~~~~~~~~~~\n");
            Collection nodeChannels = datasource.getChannels();
            if ( nodeChannels.size() == 0 )
            {
                logEntry.append("\t\tNo filter data to report");
            }
            for (Iterator channelIter = nodeChannels.iterator(); channelIter.hasNext();) {
                EventChannel channel = (EventChannel) channelIter.next();
                logFilterData(logEntry,channel);
            }
        }
        logClientBuffersAndQueues(logEntry, doDeltas);
        logServerBuffersAndQueues(logEntry, doDeltas);

        Collection nodeSubjects = datasource.getSubjects();
        logEntry.append("\n\t~~~~~~~~~~ SUBJECT DATA ~~~~~~~~~~\n");
        if ( nodeSubjects.size() == 0 )
        {
            logEntry.append("\t\t No subject data to report");
        }
        for (Iterator iterator = nodeSubjects.iterator(); iterator.hasNext();)
        {
            if ( lastData != null )
            {
                doDeltas = true;
            }

            TransportPSSubject subject = (TransportPSSubject) iterator.next();
            SubjectData sd = null;
            if ( doDeltas )
            {
                sd = (SubjectData) lastData.getLastSubjects().get(subject.getSubject());
                if ( sd == null )
                {
                    // new subject, no deltas possible.
                    doDeltas = false;
                }
            }
            logSubjectData(logEntry, subject, sd, doDeltas, currentSample);
        }

        logEntry.append("==================================================\n");
        for (Iterator destinationIter = destinations.iterator(); destinationIter.hasNext();)
        {
            PrintStream destination = (PrintStream) destinationIter.next();
            destination.println( logEntry );
            destination.flush();
        }

        lastData = (SBTLiveNode) datasource.clone();
        lastSample = currentSample;
    }

    private void logFilterData(StringBuffer logEntry, EventChannel channel) {
        try
        {
            logEntry.append("\nChannel : ").append( channel.getEcName() ).append( "\n");
            Collection filters = datasource.getFilters(channel.getEcName());
            for (Iterator filterIter = filters.iterator(); filterIter.hasNext();) {
                String[] filterSegments = (String[]) filterIter.next();
                logEntry.append("\n\t");
                for (int idx = 0; idx < filterSegments.length; idx++) {
                    String segment = filterSegments[idx];
                    if ( segment.length() > 0 )
                    {
                        logEntry.append(segment);
                        if ( idx < filterSegments.length - 1 )
                        {
                            logEntry.append("/");
                        }
                    }
                }
            }
            logEntry.append("\n");
        } catch (ServiceNotAvailableException snae)
        {
            logEntry.append("Error logging filter data: " + snae.getMessage() );
        }
    }

    private void logSubjectData(StringBuffer logEntry, TransportPSSubject subject, SubjectData last, boolean doDeltas, long currentSample) {
        logEntry.append( "\tSubject " + subject.getSubject() + "\n");
        if ( doDeltas )
            logEntry.append("\t\tLast Messages Sent " + (subject.getMsgsSent() - last.getMsgsSent()) );
        logEntry.append( "\t\tCumulative Messages Sent " + subject.getMsgsSent() + "\n" );

        if ( doDeltas ) logEntry.append("\t\tLast Bytes Sent " + (subject.getBytesSent() - last.getBytesSent()) );
        logEntry.append( "\t\tCumulative Bytes Sent " + subject.getBytesSent() + "\n" );

        long receiveDelta = 0;
        if ( doDeltas )
        {
            receiveDelta = (subject.getMsgsRecv() - last.getMsgsReceived());
            logEntry.append("\t\tLast Messages Received " + receiveDelta );
        }
        logEntry.append( "\t\tCumulative Messages Received " + subject.getMsgsRecv() + "\n" );

        if ( doDeltas ) logEntry.append("\t\tLast Bytes Received " + (subject.getBytesRecv() - last.getBytesReceived()) );
        logEntry.append( "\t\tCumulative Bytes Received " + subject.getBytesRecv() + "\n" );

        if ( doDeltas )
        {
            double elapsed = (currentSample - lastSample) / 1000;
            double rateMsgRcv = (subject.getMsgsRecv() - last.getMsgsReceived()) /  elapsed;
            double rateBytesRcv = (subject.getBytesRecv() - last.getBytesReceived()) / elapsed;
            double rateMsgSent = (subject.getMsgsSent() - last.getMsgsSent()) /  elapsed;
            double rateBytesSent = (subject.getBytesSent() - last.getBytesSent()) / elapsed;

            double[] hwms = (double[]) highWaterMarks.get( subject.getSubject() );
            if ( hwms == null )
            {
                hwms = new double[4];
                Arrays.fill(hwms, 0.0);
                highWaterMarks.put( subject.getSubject(), hwms );
            }
            if ( rateMsgRcv > hwms[ RATE_MSGS_RECV_HWM ] )
            {
               hwms[ RATE_MSGS_RECV_HWM ] = rateMsgRcv;
            }

            if ( rateBytesRcv > hwms[ RATE_BYTES_RECV_HWM ] )
            {
               hwms[ RATE_BYTES_RECV_HWM ] = rateBytesRcv;
            }

            if ( rateMsgSent > hwms[ RATE_MSGS_SENT_HWM ] )
            {
               hwms[ RATE_MSGS_SENT_HWM ] = rateMsgSent;
            }

            if ( rateBytesSent > hwms[ RATE_BYTES_SENT_HWM ] )
            {
               hwms[ RATE_BYTES_SENT_HWM ] = rateBytesSent;
            }

            logEntry.append("\n\t\tRate (Messages Received) " + fmt.format(rateMsgRcv) + "/sec\n" );
            logEntry.append("\t\tRate (Bytes Received) " + fmt.format(rateBytesRcv) + "/sec\n" );
            logEntry.append("\t\tRate (Messages Sent) " + fmt.format(rateMsgSent) + "/sec\n" );
            logEntry.append("\t\tRate (Bytes Sent) " + fmt.format(rateBytesSent) + "/sec\n" );

            logEntry.append("\n\t\tHWM for Rate (Messages Received) " + fmt.format(hwms[ RATE_MSGS_RECV_HWM ]) + "/ sec\n" );
            logEntry.append("\t\tHWM for Rate (Bytes Received) " + fmt.format(hwms[ RATE_BYTES_RECV_HWM ]) + "/ sec\n" );
            logEntry.append("\t\tHWM for Rate (Messages Sent) " + fmt.format(hwms[ RATE_MSGS_SENT_HWM ]) + "/ sec\n" );
            logEntry.append("\t\tHWM for Rate (Bytes Sent) " + fmt.format(hwms[ RATE_BYTES_SENT_HWM ]) + "/ sec\n" );
        }
    }

    private void logNodeHeader(StringBuffer logEntry) {
        logEntry.append( "Node General Properties" ).append( "\n" );
        logEntry.append( "\tName = " ).append( datasource.getName() ).append( "\n" );
        logEntry.append( "\tHost = " ).append( datasource.getHost() ).append( "\n" );
        logEntry.append( "\tServer = " ).append( datasource.getServerName() ).append( "\n");
        logEntry.append( "\tStatus = " ).append( (datasource.isAlive() ? "Active":"INACTIVE") ).append( "\n");
    }

    private void logClientBuffersAndQueues(StringBuffer logEntry, boolean doDeltas) {
        long cqmc = datasource.getClientQueuedMsgCount();
        if ( cqmc > clntQdMsgsHWM )
        {
           clntQdMsgsHWM = cqmc;
        }

        long cqbc = datasource.getClientQueuedBytesCount();
        if ( cqbc > clntQdBytesHWM )
        {
            clntQdBytesHWM = cqbc;
        }

        long cwb = datasource.getClientWriteBufferSize();
        if ( cwb > clntQdBytesHWM )
        {
            clntWrtBufferHWM = cwb;
        }

        long crb = datasource.getClientQueuedBytesCount();
        if ( crb > clntRdBufferHWM )
        {
            clntRdBufferHWM = crb;
        }
        logEntry.append( "\nClient Buffer Data" ).append( "\n" );
        logEntry.append( "\tQueued Message Count = " ).append( cqmc ).append( "\n" );
        if ( doDeltas ) logEntry.append( "\t\tChange = " ).append(cqmc - lastData.getClientQueuedMsgCount()).append( "\n");
        logEntry.append( "\tHWM(Client Queued Message Count) = " + clntQdMsgsHWM + "\n\n" );

        logEntry.append( "\tQueued Bytes Count = " ).append( cqbc ).append( "\n" );
        if ( doDeltas ) logEntry.append( "\t\tChange = " ).append( cqbc  - lastData.getClientQueuedBytesCount()).append( "\n");
        logEntry.append( "\tHWM(Client Queued Bytes Count) = " + clntQdBytesHWM  + "\n\n" );

        logEntry.append( "\tRead Buffer Byte Count = " ).append( crb ).append( "\n");
        if ( doDeltas ) logEntry.append( "\t\tChange = " ).append( crb  - lastData.getClientReadBufferSize()  ).append( "\n");
        logEntry.append( "\tHWM(Client Read Buffer) = " + clntRdBufferHWM  + "\n\n" );

        logEntry.append( "\tWrite Buffer Byte Count = " ).append( cwb ).append( "\n");
        if ( doDeltas ) logEntry.append( "\t\tChange = " ).append( cwb  - lastData.getClientWriteBufferSize() ).append( "\n");
        logEntry.append( "\tHWM(Client Write Buffer) = " + clntWrtBufferHWM  + "\n\n" );

    }


    private void logServerBuffersAndQueues(StringBuffer logEntry, boolean doDeltas) {
        long sqmc = datasource.getServerQueuedMsgCount();
        if ( sqmc > srvrQdMsgsHWM )
        {
           srvrQdMsgsHWM = sqmc;
        }

        long sqbc = datasource.getServerQueuedBytesCount();
        if ( sqbc > srvrQdBytesHWM )
        {
            srvrQdBytesHWM = sqbc;
        }

        long swb = datasource.getServerWriteBufferSize();
        if ( swb > srvrQdBytesHWM )
        {
            srvrWrtBufferHWM = swb;
        }

        long srb = datasource.getServerQueuedBytesCount();
        if ( srb > srvrRdBufferHWM )
        {
            srvrRdBufferHWM = srb;
        }
        logEntry.append( "\nServer Buffer Data" ).append( "\n" );
        logEntry.append( "\tQueued Message Count = " ).append( sqmc ).append( "\n" );
        if ( doDeltas ) logEntry.append( "\t\tChange = " ).append(sqmc - lastData.getServerQueuedMsgCount()).append( "\n");
        logEntry.append( "\tHWM(Server Queued Message Count) = " + srvrQdMsgsHWM  + "\n\n" );

        logEntry.append( "\tQueued Bytes Count = " ).append( sqbc ).append( "\n" );
        if ( doDeltas ) logEntry.append( "\t\tChange = " ).append( sqbc  - lastData.getServerQueuedBytesCount()).append( "\n");
        logEntry.append( "\tHWM(Server Queued Bytes Count) = " + srvrQdBytesHWM  + "\n\n" );

        logEntry.append( "\tRead Buffer Byte Count = " ).append( srb ).append( "\n");
        if ( doDeltas ) logEntry.append( "\t\tChange = " ).append( srb  - lastData.getServerReadBufferSize()  ).append( "\n");
        logEntry.append( "\tHWM(Server Read Buffer) = " + srvrRdBufferHWM  + "\n\n" );

        logEntry.append( "\tWrite Buffer Byte Count = " ).append( swb ).append( "\n");
        if ( doDeltas ) logEntry.append( "\t\tChange = " ).append( swb  - lastData.getServerWriteBufferSize() ).append( "\n");
        logEntry.append( "\tHWM(Server Write Buffer) = " + srvrWrtBufferHWM  + "\n\n" );

    }

    protected void addDestinationImpl(OutputStream sink)
    {
        if (!(sink instanceof PrintStream)) {
            destinations.add(new PrintStream(sink));
        } else {
            destinations.add(sink);
        }
    }
}
