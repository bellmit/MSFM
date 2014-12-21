//
// -----------------------------------------------------------------------------------
// Source file: LiveNodeRecorder.java
//
// PACKAGE: com.cboe.infra.presentation.network
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.infra.presentation.network;

import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.List;
import java.beans.PropertyChangeListener;

/**
 * A LiveNodeRecorder is used to record the stats for a node.
 */
public class LiveNodeRecorder
{
    // class constants
    public static final int MIN_SAMPLE_INTERVAL = 10; // fifteen seconds
    public static final int MAX_RECORD_DURATION = 12 * 60 * 60; // twelve hours
    public static final int DEFAULT_SAMPLE_INTERVAL = MIN_SAMPLE_INTERVAL;
    public static final int DEFAULT_RECORD_DURATION = 60 * 5; // five minutes

    public static final String IS_RECORDING = "isRecording";
//    public static final int DEFAULT_RECORD_SCOPE = 0xFFFFFFFF; // all

    // instance variables

    protected int samplingInterval;
    protected int duration;
    protected List listenerList = new Vector();

    protected boolean scheduled;
    protected Timer scheduler;
    protected TimerTask samplerTask;
    protected TimerTask endTask;

    /**
     * Create a record of node activity sampled at the supplied interval
     * for the duration specified.
     * To begin recording, see {@link #startRecording}; to terminate recording prior
     * to the expiration of the default duration, use {@link#stopRecording}.
     * @param node The node to record property data for
     * @param os Node statistics will be written here
     * @param period Sampling interval expressed in seconds.  Note that this value can
     * not be lower than LiveNodeRecorder.MIN_SAMPLE_INTERVAL
     * @param span The length of time to record the node, expressed in seconds.  Note that this
     * value can not be greater than LiveNodeRecorder.MAX_RECORD_DURATION
     */
    public LiveNodeRecorder(SBTLiveNode node, OutputStream os, int period, int span)
    {
        samplingInterval = period;
        duration = span;
        scheduled = false;

        scheduler = new Timer(true);
        samplerTask = new NodeSamplingTask(node,os);
        endTask = new TimerTask()
        {
            public void run()
            {
                stopRecording();
            }
        };
    }

    /**
     * Create a record of node activity for the <code>DEFAULT_RECORD_DURATION</code>.
     * To begin recording, see {@link #startRecording}; to terminate recording prior
     * to the expiration of the default duration, use {@link#stopRecording}.
     * @param node Node statistics will be written here
     * @param os os Node statistics will be written here
     * @param period period Sampling interval expressed in seconds
     */
    public LiveNodeRecorder(SBTLiveNode node, OutputStream os, int period )
    {
        this(node,os,period, 300 );
    }

    /**
     * Create a record of node activity sampled at <code>DEFAULT_SAMPLE_INTERVAL</code>
     * for <code>DEFAULT_RECORD_DURATION</code>.
     * To begin recording, see {@link #startRecording}; to terminate recording prior
     * to the expiration of the default duration, use {@link#stopRecording}.
     * @param node Node statistics will be written here
     * @param os os Node statistics will be written here
     */
    public LiveNodeRecorder(SBTLiveNode node, OutputStream os)
    {
        this(node,os,15,300);
    }

    /**
     * Begin recording.   It is from this point, <i><u>not object construction</u></i>,
     * that duration is measured.
     */
    public synchronized void startRecording()
    {
        try
        {
            if (! scheduled )
            {
                scheduler.schedule(samplerTask, 0, samplingInterval * 1000);
                scheduler.schedule(endTask, duration * 1000);
                scheduled = true;
            }
        }
        catch (IllegalStateException ise)
        {
            scheduler = new Timer();
            scheduler.schedule(samplerTask,0,samplingInterval*1000);
            scheduler.schedule(endTask, duration * 1000 );
        }
    }

    /**
     * Find out if the LiveNodeRecorder is currently recording.
     */
    public boolean isRecording()
    {
        return scheduled;
    }

    /**
     * Add a property change listener to this recorder.  The only current property
     * is the IS_RUNNING property.  Using this method, users can find out when the recorder
     * begins and when it stops.
     * @param pcl The PropertyChangeListener to add
     * @return boolean True if listener was not already registered and was therefore sucessfully added.
     * False otherwise.
     */
    public boolean addPropertyChangeListener(PropertyChangeListener pcl)
    {
        boolean rv = false;
        if (!  listenerList.contains(pcl) )
        {
            listenerList.add(pcl);
            rv = true;
        }
        return rv;
    }

    /**
     * Remove a property change listener.  Users cam use this method to de-register
     * from recorders they are no longer interested in monitoring.
     * @param pcl  The PropertyChangeListener to remove
     * @return boolean True if the supplied listener was found in our listener list
     * and was therefore successfully removed.  False otherwise.
     */
    public boolean removePropertyChangeListener(PropertyChangeListener pcl)
    {
        return listenerList.remove(pcl);
    }

    /**
     * Used to stop node recording prior to expiration.
     */
    public synchronized void stopRecording()
    {
        scheduler.cancel();
        scheduled = false;
    }

}
