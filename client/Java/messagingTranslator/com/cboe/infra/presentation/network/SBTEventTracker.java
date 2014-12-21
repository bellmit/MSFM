//
// -----------------------------------------------------------------------------------
// Source file: SBTEventTracker.java
//
// PACKAGE: com.cboe.infra.presentation.network
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.infra.presentation.network;

import java.util.concurrent.*;

import javax.jms.JMSException;
import javax.jms.TopicConnection;

import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerMMBusinessProperty;

import com.cboe.MOMTransport.TalarianTransport.TalarianMessageTypesHelper;
import com.cboe.MOMTransport.jms.ssJava.TopicConnectionTipcImpl;
import com.cboe.MOMTransport.jms.utils.JmsUtilsHelper;
import com.cboe.common.log.Logger;
import com.cboe.utils.ecUtils.JmsEventTracker;
import com.cboe.utils.ecUtils.TrackingResponse;
import com.cboe.utils.ecUtils.TrackingResponseListener;
import com.cboe.utils.monitoringService.TransportMonitor;
import com.cboe.utils.monitoringService.TransportPSClient;
import com.smartsockets.TipcCb;
import com.smartsockets.TipcException;
import com.smartsockets.TipcMsg;
import com.smartsockets.TipcMt;
import com.smartsockets.TipcProcessCb;
import com.smartsockets.TipcSrv;
import com.smartsockets.TipcSvc;

public class SBTEventTracker implements TipcProcessCb // For old-style event tracking
{
    private TipcSrv tipcSrv;
    private String compName = "SBTEventTracker::";
    private TrackingResponseListener callBackObject;
    TipcCb trackingMessageTipcCb;
    static SBTEventTracker instance;
    private boolean initialized = false;
    private String trackerName;
    private ScheduledExecutorService trackingMonitor;

    /**
     */
    public static SBTEventTracker create(TrackingResponseListener cb)
    {
        if (instance == null)
        {
            instance = new SBTEventTracker(cb);
            try
            {
                instance.initialize();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        return instance;
    }

    public static SBTEventTracker find()
    {
        return instance;
    }

    private SBTEventTracker(TrackingResponseListener cb)
    {
        this.callBackObject = cb;
        trackerName = System.getProperty("ORB.OrbName", "EventTracker");
        trackingMonitor = Executors.newSingleThreadScheduledExecutor();
    }

    public void process(TipcMsg msg, Object userObject)
    {
        String correlationId = "";
        String originalPublisher = "";
        String sender = "";
        String subjectChain = "";
        int responderType = JmsUtilsHelper.JMS_TRACKING_SENDER_TYPE;
        long responseSentTime = 0;
        
        try
        {
            msg.setCurrent(0);
            sender = msg.nextStr();
            originalPublisher = msg.nextStr();
            subjectChain = msg.nextStr();
            responseSentTime = msg.nextInt8();
            correlationId = msg.getCorrelationId();
            if (msg.getUserProp() == 2)
            {
                responderType = JmsUtilsHelper.JMS_TRACKING_CONSUMER_TYPE;
            }
        }
        catch (TipcException e)
        {
            GUILoggerHome.find().exception(e);
        }
        
        callBackObject.onTrackingResponse(new TrackingResponse(correlationId, 0, responderType, sender,
                                                               originalPublisher, subjectChain, responseSentTime));
    }

    public void startEventTracking(String clientToBeTracked, String subjectName, int trackingMessageCount)
            throws Exception
    {
        if (!initialized)
        {
            initialize();
        }
        try
        {
            TransportPSClient psClient = TransportMonitor.getInstance().getClient(clientToBeTracked);
            TopicConnection topicConn = TransportMonitor.getInstance().getTopicConnection(psClient.getServerName());
            if (topicConn != null && topicConn instanceof TopicConnectionTipcImpl)
            {
                sendOldEventTrackingRequest(topicConn, clientToBeTracked, subjectName, trackingMessageCount);
            }
            else
            {
                JmsEventTracker jmsEventTracker = new JmsEventTracker(topicConn, psClient.getJmsProviderName(),
                                                                      trackerName, clientToBeTracked, subjectName,
                                                                      trackingMessageCount, callBackObject);
                jmsEventTracker.sendTrackingRequest();

                trackingMonitor.schedule(new EventTrackingMonitor(jmsEventTracker), 5000, TimeUnit.MILLISECONDS);
            }
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(compName + "sendTrackinsMessage() - Exception while attempting to send a Tracking Message", e);
            throw e;
        }
    }

    private void sendOldEventTrackingRequest(TopicConnection topicConn, String clientToBeTracked, String subjectName,
                                             int trackingMessageCount) throws Exception
    {
        TopicConnectionTipcImpl tipcTopicConn = (TopicConnectionTipcImpl) topicConn;
        if (tipcSrv == null)
        {
            tipcSrv = tipcTopicConn.getTalarianConnection();
            trackingMessageTipcCb = tipcSrv.addProcessCb(this, TalarianMessageTypesHelper.getTrackingEventMessageType(), tipcSrv);

            if (trackingMessageTipcCb == null)
            {
                throw new Exception(compName + "sendOldEventTrackingRequest() - Failed to create a process CallBack "
                                    + " for Tracking Event Message Type on rt server Connection : ");
            }
        }

        TipcMt adminMt = TalarianMessageTypesHelper.getTrackingEventAdminMessageType();
        TipcMsg adminMsg = TipcSvc.createMsg(adminMt);
        adminMsg.setDest(clientToBeTracked);
        adminMsg.appendStr(subjectName);
        adminMsg.appendInt4(trackingMessageCount);
        
        GUILoggerHome.find().information(compName + "sendOldEventTrackingRequest() - Sending a Tracking Admin Message to <"
                             + clientToBeTracked + ">. Subject<"
                             + subjectName + "> and NumMessages<"
                             + trackingMessageCount + ">", GUILoggerMMBusinessProperty.MESSAGEMON);

        tipcSrv.send(adminMsg);
        tipcSrv.flush();
    }

    private void initialize() throws Exception
    {
        /*
          tipcSrv = MonitoringServiceProxy.getInstance().getTalarianMonitor().getTipcSrv();
          trackingMessageCb = tipcSrv.addProcessCb(callBackObject,
                  TalarianMessageTypesHelper.getTrackingEventMessageType(),
                  tipcSrv);
  
          if (trackingMessageCb == null)
          {
              throw new Exception(
                      compName
                              + "startTrackingMessages() - Failed to create a process CallBack "
                              + " for Tracking Event Message Type on server Connection : ");
  
          }
          */
        initialized = true;
    }


    public void removeCallbackObject()
    {
        try
        {
            tipcSrv.removeProcessCb(trackingMessageTipcCb);
            initialized = false;
            Logger.info("Callback removed");
        }
        catch (TipcException e)
        {
            GUILoggerHome.find().exception(e);
        }
    }

    protected String asHexString(byte[] bytes)
    {
        StringBuffer rv = new StringBuffer();
        for (int m = 0; m < bytes.length; ++m)
        {
            if (m % 16 == 0)
            {
                rv.append("\n");
            }
            else
            {
                if (m % 2 == 0)
                {
                    rv.append(" ");
                }
                if (m % 4 == 0)
                {
                    rv.append("  ");
                }
            }
            // do highest 4 bits first
            int b = bytes[m] & 0xF0;
            int val = b >> 4;
            rv.append(hexString(val));
            int b2 = bytes[m] & 0x0F;
            rv.append(hexString(b2));
        }
        return rv.toString();
    }

    protected String hexString(int val)
    {
        String rv = null;
        if (val < 10)
        {
            rv = Integer.toString(val);
        }
        else
        {
            switch (val)
            {
                case 10:
                    rv = "A";
                    break;
                case 11:
                    rv = "B";
                    break;
                case 12:
                    rv = "C";
                    break;
                case 13:
                    rv = "D";
                    break;
                case 14:
                    rv = "E";
                    break;
                case 15:
                    rv = "F";
                    break;
            }
        }
        return rv;
    }

    private class EventTrackingMonitor implements Runnable
    {
        private JmsEventTracker jmsEventTracker;
        private int numChecks = 0;

        public EventTrackingMonitor(JmsEventTracker pJmsEventTracker)
        {
            jmsEventTracker = pJmsEventTracker;
        }

        public void run()
        {
            numChecks++;
            if (jmsEventTracker.getNumMsgsTracked() >= jmsEventTracker.getNumMsgsToTrack())
            {
                try
                {
                    jmsEventTracker.close();
                    Logger.sysNotify("SBTEventTracker.EventTrackingMonitor: Finished tracking for(" 
                                     + jmsEventTracker + ").");
                }
                catch (JMSException e)
                {
                    Logger.sysNotify("SBTEventTracker.EventTrackingMonitor: Unable to close tracker(" 
                                     + jmsEventTracker + ").");
                }
            }
            else
            {
                if (numChecks == 12)
                {
                    try
                    {
                        jmsEventTracker.close();
                        Logger.sysNotify("SBTEventTracker.EventTrackingMonitor: Forced close for tracker(" 
                                         + jmsEventTracker + ").");
                    }
                    catch (JMSException e)
                    {
                        Logger.sysNotify("SBTEventTracker.EventTrackingMonitor: Unable to force-close tracker(" 
                                         + jmsEventTracker + ").");
                    }
                }
                else
                {
                    Logger.sysNotify("SBTEventTracker.EventTrackingMonitor: Continuing to wait for tracker(" 
                                     + jmsEventTracker + ") to complete.");
                    try
                    {
                        trackingMonitor.schedule(this, 5000, TimeUnit.MILLISECONDS);
                    }
                    catch (Exception e)
                    {
                        Logger.sysAlarm("SBTEventTracker.EventTrackingMonitor: Unable to reschedule timer for tracker(" 
                                        + jmsEventTracker + ").", e);
                    }
                }
            }
        }
    }
}
