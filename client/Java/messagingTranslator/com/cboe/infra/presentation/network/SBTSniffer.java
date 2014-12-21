//
// -----------------------------------------------------------------------------------
// Source file: SBTSniffer.java
//
// PACKAGE: com.cboe.infra.presentation.network
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.infra.presentation.network;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import javax.jms.Message;

import com.cboe.utils.monitoringService.TransportPSSnifferListener;
import com.cboe.utils.monitoringService.TransportPSSubject;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerMMBusinessProperty;

/**
 * This class subscribes to messages on a specific subject and
 * either broadcasts those messages to all SnifferListeners registered,
 * or writes the messages contents to a user-supplied OutputStream or both.
 */
public class SBTSniffer implements TransportPSSnifferListener
{
    protected class StopSniffingTask extends TimerTask
    {
        boolean shouldRun = true;

        public void stop()
        {
            shouldRun = false;
        }
        public void run()
        {
            if ( !shouldRun )
            {
                // do nothing
                return;
            }

            stopSniffing();
        }
    };

    protected PrintStream destination;
    protected TransportPSSubject subject;
    protected Collection<SnifferListener> listeners = new Vector<SnifferListener>();
    protected long duration;
    protected StopSniffingTask stopSniffingTask;
    protected Timer sniffTimer;
    protected long sniffCount;
    protected long maxSniffCount = -1;
    protected Logger snifferLog;

    protected boolean sniffing;
    public SBTSniffer(TransportPSSubject subj)
    {
        this(subj,null);
    }

    public SBTSniffer(TransportPSSubject subj,OutputStream out)
    {
        this.subject = subj;
        if( out instanceof PrintStream )
        {
            destination = ( PrintStream ) out;
        }
        else if ( out != null )
        {
            destination = new PrintStream(out);
        }

        sniffTimer = new Timer();

        snifferLog = Logger.getLogger("SBTSniffer");
        snifferLog.setLevel(java.util.logging.Level.ALL);
        snifferLog.setUseParentHandlers(false);
        try
        {
            snifferLog.addHandler(new FileHandler("SBTSniffer.log", true));
        }
        catch (IOException e)
        {
            System.out.println("SBTSniffer: unable to add FileHandler to snifferLog. " + e);
        }
    }

    public void setMaxSniffCount(long maxSniffCount)
    {
        this.maxSniffCount = maxSniffCount;
    }
    public long getMaxSniffCount()
    {
        return this.maxSniffCount;
    }

    public void setDuration(long sniffMillis)
    {
        this.maxSniffCount = -1;
        if ( stopSniffingTask != null )
        {
            stopSniffingTask.stop();
        }
        stopSniffingTask = new StopSniffingTask();
        sniffTimer.schedule(stopSniffingTask,sniffMillis);
    }

    public long getDuration()
    {
        return duration;
    }

    public void startSniffing()
    {
        sniffCount = 0;
        subject.addSnifferListener(this);
        sniffing = true;
        subject.startSniffing( MonitoringServiceProxy.getInstance().getTransportMonitor().getTopicConnection( subject.getParent().getServerName() ), snifferLog );
        fireSniffingStartedEvent();
    }

    public void stopSniffing()
    {
        sniffing = false;
        subject.stopSniffing( MonitoringServiceProxy.getInstance().getTransportMonitor().getTopicConnection( subject.getParent().getServerName() ) );
        fireSniffingEndedEvent();
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug("SBTSniffer::stopSniffing sniffed " + sniffCount + " Messages",GUILoggerMMBusinessProperty.SNIFFER);
        }
    }

    public void addSnifferListener(SnifferListener l)
    {
        if(! listeners.contains(l) )
        {
            listeners.add(l);
        }
    }

    public void removeSnifferListener(SnifferListener l)
    {
        listeners.remove(l);
    }


    public void setDestination(OutputStream out)
    {
        if (out != destination)
        {
            if ((destination != null) && (destination != System.out))
            {
                destination.flush();
                destination.close();
            }
            if ( out instanceof PrintStream )
            {
                destination = (PrintStream) out;
            }
            else
            {
                destination = new PrintStream(out);
            }
        }
    }

    public OutputStream getDestination()
    {
        return destination;
    }

    public void onMessage(Message sniffedMsg)
    {
        if (sniffing)
        {
            sniffCount++;
            String decodedMessage = JmsMsgDecoder.decodeMessage(sniffedMsg, subject.getSubject());
            if ( destination != null )
            {
                destination.println(decodedMessage);
            }
            fireMessageSniffedEvent(decodedMessage);

            if ((maxSniffCount != -1) && (sniffCount >= maxSniffCount))
            {
                stopSniffing();
            }
        }
    }

    protected void fireMessageSniffedEvent(String data)
    {
        for( Iterator iterator = listeners.iterator(); iterator.hasNext(); )
        {
            SnifferListener snifferListener = ( SnifferListener ) iterator.next();
            snifferListener.messageSniffed(subject.getSubject(), data );
        }
    }

    protected void fireSniffingStartedEvent()
    {
        for( Iterator iterator = listeners.iterator(); iterator.hasNext(); )
        {
            SnifferListener snifferListener = ( SnifferListener ) iterator.next();
            snifferListener.sniffingStarted();
        }
    }


    protected void fireSniffingEndedEvent()
    {
        for( Iterator iterator = listeners.iterator(); iterator.hasNext(); )
        {
            SnifferListener snifferListener = ( SnifferListener ) iterator.next();
            snifferListener.sniffingEnded();
        }
    }

    protected String asHexString(byte[] bytes)
    {
        StringBuffer rv = new StringBuffer();
        for ( int m = 0; m < bytes.length; ++m)
        {
            if ( m % 16 == 0)
            {
                rv.append("\n");
            }
            else
            {
                if ( m % 2 == 0 )
                {
                    rv.append(" ");
                }
                if ( m % 4 == 0 )
                {
                    rv.append( "  " );
                }
            }
            // do highest 4 bits first
            int b = bytes[m] & 0xF0;
            int val = b >> 4;
            rv.append( hexString(val) );
            int b2 = bytes[m] & 0x0F;
            rv.append(hexString(b2) );
        }
        return rv.toString();
    }

    protected String hexString(int val)
    {
        String rv = null;
        if ( val < 10 )
        {
            rv = Integer.toString(val);
        }
        else
        {
            switch (val)
            {
                case 10: rv = "A";break;
                case 11: rv = "B";break;
                case 12: rv = "C";break;
                case 13: rv = "D"; break;
                case 14: rv = "E"; break;
                case 15: rv = "F"; break;
            }
        }
        return rv;
    }

    public boolean isSniffing()
    {
        return sniffing;
    }
}
