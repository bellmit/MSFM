package com.cboe.cfix.fix.session;

/**
 * FixSessionTourist.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 *
 *
 */

import java.io.*;
import java.util.*;

import com.cboe.cfix.fix.fix42.generated.fields.*;
import com.cboe.cfix.fix.fix42.session.*;
import com.cboe.cfix.interfaces.*;
import com.cboe.cfix.startup.*;
import com.cboe.cfix.util.*;
import com.cboe.client.util.*;
import com.cboe.client.util.queue.*;
import com.cboe.client.util.threadpool.*;
import com.cboe.client.util.collections.*;
import com.cboe.client.util.tourist.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;

public final class FixSessionTourist extends AbstractTourist
{
    protected String[] mandatoryKeys = new String[]{"what"};
    protected int sleepSeconds = 0;

    public String[] getMandatoryKeys()
    {
        return mandatoryKeys;
    }

    public Writer visit(final Writer writer) throws Exception
    {
        String                  what         = getValue("what");
        String                  sessionName  = getValue("sessionName");  // careful, not always passed in, could contain engine after '.'
        String                  engineName   = getValue("engineName");   // careful, not always passed in, could be after sessionName + '.'
        FixSessionIF            fixSession;
        List                    fixSessionManagerList;
        FixSessionManagerIF     fixSessionManager;
        FixSessionInformationIF fixSessionInformation;
        int                     nRepeats = 0;
        String                  key;

        if (sessionName != null)
        {
            if (sessionName.trim().length() == 0)
            {
                sessionName = null;
            }
            else
            {
                int index = sessionName.indexOf('.');
                if (index > -1)
                {
                    engineName  = sessionName.substring(index + 1);
                    sessionName = sessionName.substring(0, index);

                    try
                    {
                        int port = Integer.parseInt(engineName);

                        if (engineName.equals("" + port))
                        {
                            engineName = FixSessionManagerLocator.getEngineNameForPort(port);
                        }
                    }
                    catch (Exception ex)
                    {

                    }
                }
            }
        }

        if ("viewCfixProperties".equals(what))
        {
            Map.Entry entry;
            Map sessionMap = new TreeMap();

            for (Enumeration propertiesEnum = CfixHomeImpl.cfixProperties.propertyNames(); propertiesEnum.hasMoreElements(); )
            {
                key = (String) propertiesEnum.nextElement();
                sessionMap.put(key, CfixHomeImpl.cfixProperties.getProperty(key));
            }

            writer.write("<cfixProperties count=\"" + sessionMap.size() + "\">");
            for (Iterator iterator = sessionMap.entrySet().iterator(); iterator.hasNext(); )
            {
                entry = (Map.Entry) iterator.next();

                writer.write("<cfixProperty action=\"view\" key=\"" + entry.getKey() + "\" value=\"" + entry.getValue() + "\"/>");
            }
            writer.write("</cfixProperties>");

            return writer;
        }

        if ("setCfixProperty".equals(what))
        {
            String value = getValue("value");

            key = getValue("key");

            if (key != null && key.length() > 0)
            {
                Object object;

                if (value == null || value.trim().length() == 0)
                {
                    object = CfixHomeImpl.cfixProperties.remove(key);
                    writer.write("<cfixProperty action=\"remove\" key=\"" + key + "\" oldvalue=\"" + object + "\"/>");
                }
                else
                {
                    object = CfixHomeImpl.cfixProperties.setProperty(key, value);
                    if (object == null)
                    {
                        writer.write("<cfixProperty action=\"add\" key=\"" + key + "\" value=\"" + CfixHomeImpl.cfixProperties.getProperty(key) + "\"/>");
                    }
                    else
                    {
                        writer.write("<cfixProperty action=\"add\" key=\"" + key + "\" value=\"" + CfixHomeImpl.cfixProperties.getProperty(key) + "\" oldvalue=\"" + object + "\"/>");
                    }
                }
            }

            return writer;
        }

        if ("viewConfiguredSessions".equals(what))
        {
            Set sessionSet = new TreeSet();

            for (Enumeration propertiesEnum = CfixHomeImpl.cfixProperties.propertyNames(); propertiesEnum.hasMoreElements(); )
            {
                key = (String) propertiesEnum.nextElement();
                if (key.startsWith("session.") && key.indexOf(".senderCompID") >= 0)
                {
                    sessionSet.add(CfixHomeImpl.cfixProperties.getProperty(key));
                }
            }

            writer.write("<sessions count=\"" + sessionSet.size() + "\">");
            for (Iterator iterator = sessionSet.iterator(); iterator.hasNext(); )
            {
                writeSessionNameInformation(writer, "<session", (String) iterator.next(), 0, "/>");
            }
            writer.write("</sessions>");

            return writer;
        }

        if ("viewSessionInformation".equals(what))
        {
            Map.Entry entry;
            List connectionInformationList = new ArrayList();
            FixConnectionInformationHolder fixConnectionInformationHolder;

            Map sessionInformationMap = new TreeMap();

            fixSessionManagerList = FixSessionManagerLocator.copyFixSessionManagerList(new ArrayList());

            writer.write("<sessionInformations>");

            for (Iterator iterator = fixSessionManagerList.iterator(); iterator.hasNext(); )
            {
                sessionInformationMap.clear();

                fixSessionManager = (FixSessionManagerIF) iterator.next();

                fixSessionManager.copyFixSessionInformationMap(sessionInformationMap);

                for (Iterator iterator2 = sessionInformationMap.entrySet().iterator(); iterator2.hasNext(); )
                {
                    entry = (Map.Entry) iterator2.next();

                    key = (String) entry.getKey();

                    if (sessionName != null && !key.equals(sessionName))
                    {
                        continue;
                    }

                    fixSessionInformation = (FixSessionInformationIF) entry.getValue();

                    writeSessionNameInformation(writer, "<sessionInformation", key, fixSessionInformation.getPort(), ">");

                    connectionInformationList.clear();

                    fixSessionInformation.copyConnectionInformation(connectionInformationList);

                    Collections.reverse(connectionInformationList);

                    for (Iterator iterator3 = connectionInformationList.iterator(); iterator3.hasNext(); )
                    {
                        fixConnectionInformationHolder = (FixConnectionInformationHolder) iterator3.next();

                        writer.write("<connection socket=\"" + fixConnectionInformationHolder.connectAddress + "\" connectedAt=\"" + DateHelper.stringizeDateInLogFormat(fixConnectionInformationHolder.connectTime) + "\"");

                        if (fixConnectionInformationHolder.disconnectTime != 0L)
                        {
                            writer.write(" disconnectedAt=\"" + DateHelper.stringizeDateInLogFormat(fixConnectionInformationHolder.disconnectTime) + "\" reason=\"" + fixConnectionInformationHolder.disconnectReason + "\"");
                        }

                        writer.write("/>");
                    }

                    writer.write("</sessionInformation>");
                }
            }

            writer.write("</sessionInformations>");

            return writer;
        }

        if ("viewLoggedInSessions".equals(what))
        {
            List list = new ArrayList(256);
            FixSessionInstrumentation fixSessionInstrumentation;

            FixSessionManagerLocator.copyFixSessionList(list);

            Collections.sort(list);

            writer.write("<sessions count=\"" + list.size() + "\">");
            if ("view".equals(getValue("details")))
            {
                String sFilter = getValue("filter", "est");

                int filter = 0;
                for (int i = 0; i < sFilter.length(); i++)
                {
                    switch (sFilter.charAt(i))
                    {
                        case 'e': case 'E': filter = BitHelper.setBits(filter, BitHelper.INT_SET_BIT_1); break;
                        case 's': case 'S': filter = BitHelper.setBits(filter, BitHelper.INT_SET_BIT_2); break;
                        case 't': case 'T': filter = BitHelper.setBits(filter, BitHelper.INT_SET_BIT_3); break;
                    }
                }

                for (Iterator iterator = list.iterator(); iterator.hasNext(); )
                {
                    fixSession = ((FixSessionIF) iterator.next());

                    key = fixSession.getSessionName();

                    writeSessionNameInformation(writer, "<session", key, fixSession.getPort(), " debugFlags=\"" + DebugFlagBuilder.stringizeFixSessionDebugFlags(fixSession.getDebugFlags()) + "\">");

                    if (BitHelper.isBitMaskSet(filter, BitHelper.INT_SET_BIT_1))
                    {
                        dumpFixEventChannelInstrumentation(writer, fixSession.getFixSessionWriter().getFixEventChannel().getEventChannelInstrumentation());
                    }

                    if (BitHelper.isBitMaskSet(filter, BitHelper.INT_SET_BIT_2))
                    {
                        fixSessionInstrumentation = (FixSessionInstrumentation) fixSession.getFixSessionInstrumentation().clone();
                        dumpFixSessionInstrumentation(writer, fixSession, fixSessionInstrumentation);
                    }

                    if (BitHelper.isBitMaskSet(filter, BitHelper.INT_SET_BIT_1))
                    {
                        dumpThreadPoolInstrumentation(writer, fixSession, fixSession.getThreadPool().getAdaptiveThreadPoolInstrumentation());
                    }

                    writer.write("</session>");
                }
            }
            else
            {
                for (Iterator iterator = list.iterator(); iterator.hasNext(); )
                {
                    fixSession = ((FixSessionIF) iterator.next());
                    key = fixSession.getSessionName();
                    writeSessionNameInformation(writer, "<session", key, fixSession.getPort(), "/>");
                }
            }
            writer.write("</sessions>");

            return writer;
        }

        String debugFlag;

        if ("setDebugFlags".equals(what))
        {
            if ("*".equals(sessionName))
            {
                List fixSessionList = FixSessionManagerLocator.copyFixSessionList(new ArrayList());
                debugFlag = getValue("debugFlag", "");
                int newDebugFlag = DebugFlagBuilder.buildFixSessionDebugFlags(debugFlag);
                if (newDebugFlag == IntegerHelper.INVALID_VALUE)
                {
                    writer.write("<error type=\"data\" text=\"can't parseInt debugFlag value '" + debugFlag + "'\"/>");
                    return writer;
                }

                for (Iterator iterator = fixSessionList.iterator(); iterator.hasNext(); )
                {
                    fixSession = (FixSessionIF) iterator.next();
                    if (fixSession == null)
                    {
                        writer.write("<error type=\"lookup\" text=\"no session named '" + fixSession.getSessionName() + "'\"/>");
                        continue;
                    }

                    if (debugFlag.length() > 0)
                    {
                        int oldDebugFlag = fixSession.setDebugFlags(newDebugFlag);
                        writeSessionNameInformation(writer, "<session", fixSession.getSessionName(), fixSession.getPort(), " newDebugFlag=\"" + DebugFlagBuilder.stringizeFixSessionDebugFlags(newDebugFlag) + "\" oldDebugFlag=\"" + DebugFlagBuilder.stringizeFixSessionDebugFlags(oldDebugFlag) + "\"/>");
                    }
                }
            }
            else
            {
                fixSession = FixSessionManagerLocator.getFixSessionByName(engineName, sessionName);
                if (fixSession == null)
                {
                    writer.write("<error type=\"lookup\" text=\"no session named '" + sessionName + "'\"/>");
                    return writer;
                }

                debugFlag = getValue("debugFlag", "");

                if (debugFlag.length() > 0)
                {
                    int newDebugFlag = DebugFlagBuilder.buildFixSessionDebugFlags(debugFlag);
                    if (newDebugFlag == IntegerHelper.INVALID_VALUE)
                    {
                        writer.write("<error type=\"data\" text=\"can't parseInt debugFlag value '" + debugFlag + "'\"/>");
                        return writer;
                    }

                    int oldDebugFlag = fixSession.setDebugFlags(newDebugFlag);
                    writeSessionNameInformation(writer, "<session", fixSession.getSessionName(), fixSession.getPort(), " newDebugFlag=\"" + DebugFlagBuilder.stringizeFixSessionDebugFlags(newDebugFlag) + "\" oldDebugFlag=\"" + DebugFlagBuilder.stringizeFixSessionDebugFlags(oldDebugFlag) + "\"/>");
                }
            }

            return writer;
        }

        if ("purgeOutboundQueue".equals(what))
        {
            fixSession = FixSessionManagerLocator.getFixSessionByName(engineName, sessionName);
            if (fixSession == null)
            {
                writer.write("<error type=\"lookup\" text=\"no session named '" + sessionName + "'\"/>");
                return writer;
            }

            DoublePriorityEventChannelIF sessionChannel = fixSession.getFixSessionWriter().getFixEventChannel();

            ObjectArrayHolder arrayHolder = new ObjectArrayHolder();

            sessionChannel.flush(arrayHolder);

            writeSessionNameInformation(writer, "<session", sessionName, fixSession.getPort(), " flushcount=\"" + arrayHolder.size() + "\"/>");

            return writer;
        }

        if ("suppressSentMsgTypes".equals(what) || "suppressRecvMsgTypes".equals(what))
        {
            if ("*".equals(sessionName))
            {
                List fixSessionList = FixSessionManagerLocator.copyFixSessionList(new ArrayList());
                for (Iterator iterator = fixSessionList.iterator(); iterator.hasNext(); )
                {
                    fixSession = (FixSessionIF) iterator.next();
                    if (fixSession == null)
                    {
                        continue;
                    }

                    fixSessionInformation = fixSession.getFixSessionInformation();

                    Collection collection;

                    if ("suppressSentMsgTypes".equals(what))
                    {
                        collection = fixSessionInformation.getSuppressedSentFixMsgTypes();
                    }
                    else
                    {
                        collection = fixSessionInformation.getSuppressedRecvFixMsgTypes();
                    }

                    String msgTypes = getValue("msgTypes");
                    if (msgTypes != null && msgTypes.length() > 0)
                    {
                        boolean bRemove = "ignore".equals(getValue("suppressIgnore"));

                        for (StringTokenizer tokenizer = new StringTokenizer(msgTypes, ","); tokenizer.hasMoreTokens(); )
                        {
                            if (bRemove)
                            {
                                collection.remove(tokenizer.nextToken().trim());
                            }
                            else
                            {
                                collection.add(tokenizer.nextToken().trim());
                            }
                        }
                    }
                }
            }
            else
            {
                fixSessionInformation = FixSessionManagerLocator.getFixSessionInformationByName(engineName, sessionName);
                if (fixSessionInformation == null)
                {
                    writer.write("<error type=\"lookup\" text=\"no session named '" + sessionName + "'\"/>");
                    return writer;
                }

                Collection collection;

                if ("suppressSentMsgTypes".equals(what))
                {
                    collection = fixSessionInformation.getSuppressedSentFixMsgTypes();
                }
                else
                {
                    collection = fixSessionInformation.getSuppressedRecvFixMsgTypes();
                }

                String msgTypes = getValue("msgTypes");
                if (msgTypes != null && msgTypes.length() > 0)
                {
                    boolean bRemove = "ignore".equals(getValue("suppressIgnore"));

                    for (StringTokenizer tokenizer = new StringTokenizer(msgTypes, ","); tokenizer.hasMoreTokens(); )
                    {
                        if (bRemove)
                        {
                            collection.remove(tokenizer.nextToken().trim());
                        }
                        else
                        {
                            collection.add(tokenizer.nextToken().trim());
                        }
                    }
                }
            }

            return writer;
        }

        if ("setThreadPoolDebugFlags".equals(what))
        {
            debugFlag = getValue("threadPoolDebugFlag");

            if (debugFlag != null && debugFlag.trim().length() > 0)
            {
                int newDebugFlag  = IntegerHelper.parseInt(debugFlag);
                if (newDebugFlag == IntegerHelper.INVALID_VALUE)
                {
                    writer.write("<error type=\"data\" text=\"can't parseInt threadPoolDebugFlag value '" + debugFlag + "'\"/>");
                    return writer;
                }

                if ("*".equals(sessionName))
                {
                    List fixSessionList = FixSessionManagerLocator.copyFixSessionList(new ArrayList());
                    for (Iterator iterator = fixSessionList.iterator(); iterator.hasNext(); )
                    {
                        fixSession = (FixSessionIF) iterator.next();
                        if (fixSession == null)
                        {
                            continue;
                        }

                        int oldDebugFlag = fixSession.getThreadPool().setDebugFlags(newDebugFlag);
                        writeSessionNameInformation(writer, "<session", fixSession.getSessionName(), fixSession.getPort(), " threadPool newDebugFlag=\"" + newDebugFlag + "\" oldDebugFlag=\"" + oldDebugFlag + "\"/>");
                    }
                }
                else
                {
                    fixSession = FixSessionManagerLocator.getFixSessionByName(engineName, sessionName);
                    if (fixSession == null)
                    {
                        writer.write("<error type=\"lookup\" text=\"no session named '" + sessionName + "'\"/>");
                        return writer;
                    }

                    int oldDebugFlag = fixSession.getThreadPool().setDebugFlags(newDebugFlag);

                    key = fixSession.getSessionName();
                    writeSessionNameInformation(writer, "<session", fixSession.getSessionName(), fixSession.getPort(), " threadPool newDebugFlag=\"" + newDebugFlag + "\" oldDebugFlag=\"" + oldDebugFlag + "\"/>");
                }
            }

            return writer;
        }

        if ("setSequenceNumbers".equals(what))
        {
            fixSessionInformation = FixSessionManagerLocator.getFixSessionInformationByName(engineName, sessionName);
            if (fixSessionInformation == null)
            {
                writer.write("<error type=\"lookup\" text=\"no session named '" + sessionName + "'\"/>");
                return writer;
            }

            writeSessionNameInformation(writer, "<session", sessionName, fixSessionInformation.getPort(), null);

            String nextFixReceiveSequenceNumber = getValue("nextFixReceiveSequenceNumber");
            String nextFixSendSequenceNumber    = getValue("nextFixSendSequenceNumber");

            writer.write(" prevNextReceiveMsgSeqNum=\"" + fixSessionInformation.getNextReceiveMsgSeqNum() + "\" prevNextSendMsgSeqNum=\"" + fixSessionInformation.getNextSendMsgSeqNum() + "\"");

            nRepeats = IntegerHelper.parseInt(nextFixReceiveSequenceNumber);
            if (nRepeats != IntegerHelper.INVALID_VALUE && nRepeats > 0)
            {
                fixSessionInformation.setNextReceiveMsgSeqNum(nRepeats);
                writer.write(" nextReceiveMsgSeqNum=\"" + fixSessionInformation.getNextReceiveMsgSeqNum() + "\"");
            }

            nRepeats = IntegerHelper.parseInt(nextFixSendSequenceNumber);
            if (nRepeats != IntegerHelper.INVALID_VALUE && nRepeats > 0)
            {
                fixSessionInformation.setSendMsgSeqNum(nRepeats - 1);
                writer.write(" nextSendMsgSeqNum=\"" + fixSessionInformation.getNextSendMsgSeqNum() + "\"");
            }

            writer.write("/>");

            return writer;
        }

        if ("terminateSession".equals(what))
        {
            String reason = getValue("reason", "Terminated By Operations");

            if ("*".equals(sessionName))
            {
                List fixSessionList = FixSessionManagerLocator.copyFixSessionList(new ArrayList());
                for (Iterator iterator = fixSessionList.iterator(); iterator.hasNext(); )
                {
                    fixSession = (FixSessionIF) iterator.next();
                    if (fixSession == null)
                    {
                        continue;
                    }

                    fixSession.acceptLogout(reason);

                    writeSessionNameInformation(writer, "<session", fixSession.getSessionName(), fixSession.getPort(), " action=\"terminated\"/>");
                }
            }
            else
            {
                fixSession = FixSessionManagerLocator.getFixSessionByName(engineName, sessionName);
                if (fixSession == null)
                {
                    writer.write("<error type=\"lookup\" text=\"no session named '" + sessionName + "'\"/>");
                    return writer;
                }

                fixSession.acceptLogout(reason);

                writeSessionNameInformation(writer, "<session", fixSession.getSessionName(), fixSession.getPort(), " action=\"terminated\"/>");
            }

            return writer;
        }

        if ("generateTestRequest".equals(what))
        {
            if ("*".equals(sessionName))
            {
                List fixSessionList = FixSessionManagerLocator.copyFixSessionList(new ArrayList());
                for (Iterator iterator = fixSessionList.iterator(); iterator.hasNext(); )
                {
                    fixSession = (FixSessionIF) iterator.next();
                    if (fixSession == null)
                    {
                        continue;
                    }

                    fixSession.externallyGenerateFixTestRequestMessage();

                    writeSessionNameInformation(writer, "<session", fixSession.getSessionName(), fixSession.getPort(), " action=\"enqueued TestRequest\"/>");
                }
            }
            else
            {
                fixSession = FixSessionManagerLocator.getFixSessionByName(engineName, sessionName);
                if (fixSession == null)
                {
                    writer.write("<error type=\"lookup\" text=\"no session named '" + sessionName + "'\"/>");
                    return writer;
                }

                fixSession.externallyGenerateFixTestRequestMessage();

                writeSessionNameInformation(writer, "<session", fixSession.getSessionName(), fixSession.getPort(), " action=\"enqueued TestRequest\"/>");
            }

            return writer;
        }

        if ("generateHeartBeat".equals(what))
        {
            if ("*".equals(sessionName))
            {
                List fixSessionList = FixSessionManagerLocator.copyFixSessionList(new ArrayList());
                for (Iterator iterator = fixSessionList.iterator(); iterator.hasNext(); )
                {
                    fixSession = (FixSessionIF) iterator.next();
                    if (fixSession == null)
                    {
                        continue;
                    }

                    fixSession.externallyGenerateFixHeartBeatMessage();

                    writeSessionNameInformation(writer, "<session", fixSession.getSessionName(), fixSession.getPort(), " action=\"enqueued HeartBeat\"/>");
                }
            }
            else
            {
                fixSession = FixSessionManagerLocator.getFixSessionByName(engineName, sessionName);
                if (fixSession == null)
                {
                    writer.write("<error type=\"lookup\" text=\"no session named '" + sessionName + "'\"/>");
                    return writer;
                }

                fixSession.externallyGenerateFixHeartBeatMessage();

                    writeSessionNameInformation(writer, "<session", fixSession.getSessionName(), fixSession.getPort(), " action=\"enqueued HeartBeat\"/>");
            }

            return writer;
        }

        if ("pauseSessionEventChannel".equals(what) || "resumeSessionEventChannel".equals(what) || "flushSessionEventChannel".equals(what))
        {
            if ("*".equals(sessionName))
            {
                List fixSessionList = FixSessionManagerLocator.copyFixSessionList(new ArrayList());
                for (Iterator iterator = fixSessionList.iterator(); iterator.hasNext(); )
                {
                    fixSession = (FixSessionIF) iterator.next();
                    if (fixSession == null)
                    {
                        continue;
                    }

                    writeSessionNameInformation(writer, "<session", fixSession.getSessionName(), fixSession.getPort(), null);

                    FixSessionWriter fixSessionWriter = (FixSessionWriter) fixSession.getFixSessionWriter();
                    DoublePriorityEventChannelIF doublePriorityEventChannel = fixSessionWriter.getFixEventChannel();

                    if ("pauseSessionEventChannel".equals(what) && !fixSessionWriter.getJustDequeueHighPriority())
                    {
                        Log.information(Thread.currentThread().getName() + " PAUSING FixSessionEventChannel(" + fixSession.getSessionName() + ") size: " + doublePriorityEventChannel.normalPrioritySize());
                        writer.write(" action=\"paused\"");
                        doublePriorityEventChannel.enqueueHighPriority(FixSessionWriterCommand.createHighPriorityBegin());
                    }
                    else if ("resumeSessionEventChannel".equals(what) && fixSessionWriter.getJustDequeueHighPriority())
                    {
                        Log.information(Thread.currentThread().getName() + " RESUMING FixSessionEventChannel(" + fixSession.getSessionName() + ") size: " + doublePriorityEventChannel.normalPrioritySize());
                        writer.write(" action=\"resumed\"");
                        doublePriorityEventChannel.enqueueHighPriority(FixSessionWriterCommand.createHighPriorityEnd());
                    }
                    else if ("flushSessionEventChannel".equals(what))
                    {
                        Log.information(Thread.currentThread().getName() + " FLUSHING FixSessionEventChannel(" + fixSession.getSessionName() + ") size: " + doublePriorityEventChannel.normalPrioritySize());
                        writer.write(" action=\"flushed\" count=\"" + doublePriorityEventChannel.normalPrioritySize() + "\"");
                        doublePriorityEventChannel.flushNormalPriority(ObjectArrayHolder.EmptyArrayHolder);
                    }
                    else
                    {
                        writer.write(" action=\"unchanged\"");
                    }

                    writer.write("/>");
                }
            }
            else
            {
                fixSession = FixSessionManagerLocator.getFixSessionByName(engineName, sessionName);
                if (fixSession == null)
                {
                    writer.write("<error type=\"lookup\" text=\"no session named '" + sessionName + "'\"/>");
                    return writer;
                }

                writeSessionNameInformation(writer, "<session", fixSession.getSessionName(), fixSession.getPort(), null);

                FixSessionWriter fixSessionWriter = (FixSessionWriter) fixSession.getFixSessionWriter();
                DoublePriorityEventChannelIF doublePriorityEventChannel = fixSessionWriter.getFixEventChannel();

                if ("pauseSessionEventChannel".equals(what) && !fixSessionWriter.getJustDequeueHighPriority())
                {
                    Log.information(Thread.currentThread().getName() + " PAUSING FixSessionEventChannel(" + fixSession.getSessionName() + ") size: " + doublePriorityEventChannel.normalPrioritySize());
                    writer.write(" action=\"paused\"");
                    doublePriorityEventChannel.enqueueHighPriority(FixSessionWriterCommand.createHighPriorityBegin());
                }
                else if ("resumeSessionEventChannel".equals(what) && fixSessionWriter.getJustDequeueHighPriority())
                {
                    Log.information(Thread.currentThread().getName() + " RESUMING FixSessionEventChannel(" + fixSession.getSessionName() + ") size: " + doublePriorityEventChannel.normalPrioritySize());
                    writer.write(" action=\"resumed\"");
                    doublePriorityEventChannel.enqueueHighPriority(FixSessionWriterCommand.createHighPriorityEnd());
                }
                else if ("flushSessionEventChannel".equals(what))
                {
                    Log.information(Thread.currentThread().getName() + " FLUSHING FixSessionEventChannel(" + fixSession.getSessionName() + ") size: " + doublePriorityEventChannel.normalPrioritySize());
                    writer.write(" action=\"flushed\" count=\"" + doublePriorityEventChannel.normalPrioritySize() + "\"");
                    doublePriorityEventChannel.flushNormalPriority(ObjectArrayHolder.EmptyArrayHolder);
                }
                else
                {
                    writer.write(" action=\"unchanged\"");
                }

                writer.write("/>");
            }

            return writer;
        }

        if ("viewSession".equals(what))
        {
            String sFilter = getValue("filter");
            if (sFilter == null)
            {
                sFilter = "est";
            }

            int filter = 0;
            for (int i = 0; i < sFilter.length(); i++)
            {
                switch (sFilter.charAt(i))
                {
                    case 'e': case 'E': filter = BitHelper.setBits(filter, BitHelper.INT_SET_BIT_1); break;
                    case 's': case 'S': filter = BitHelper.setBits(filter, BitHelper.INT_SET_BIT_2); break;
                    case 't': case 'T': filter = BitHelper.setBits(filter, BitHelper.INT_SET_BIT_3); break;
                }
            }

            fixSession = FixSessionManagerLocator.getFixSessionByName(engineName, sessionName);
            if (fixSession == null)
            {
                writer.write("<error type=\"lookup\" text=\"no session named '" + sessionName + "'\"/>");
                return writer;
            }

            writer.write("<session name=\"" + sessionName + "\" description=\"" + fixSession.getPropertiesHelper().getProperty("cfix.fixSession.description", "") + "\" port=\"" + fixSession.getPort() + "\" debugFlags=\"" + DebugFlagBuilder.stringizeFixSessionDebugFlags(fixSession.getDebugFlags()) + "\" overlayPolicy=\"" + fixSession.getOverlayPolicyFactory().getOverlayPolicyAsString() + "\">");

            String sleep = getValue("sleepSeconds");

            if (sleep != null)
            {
                sleepSeconds = Integer.parseInt(sleep);
            }

            String repeat = getValue("repeat");

            if (repeat != null)
            {
                nRepeats = Integer.parseInt(repeat);
            }

            if (sleepSeconds > 0)
            {
                int i = 0;

                FixSessionInstrumentation                                   fixSessionInstrumentationBEFORE;
                SinglePriorityEventChannelIF.EventChannelInstrumentationIF  fixEventChannelInstrumentationBEFORE;
                AdaptiveThreadPool.AdaptiveThreadPoolInstrumentationIF      adaptiveThreadPoolInstrumentationBEFORE;
                FixSessionInstrumentation                                   fixSessionInstrumentationAFTER;
                SinglePriorityEventChannelIF.EventChannelInstrumentationIF  fixEventChannelInstrumentationAFTER;
                AdaptiveThreadPool.AdaptiveThreadPoolInstrumentationIF      adaptiveThreadPoolInstrumentationAFTER;
                Date                                                        startTime;
                Date                                                        stopTime;
                long                                                        elapsed;

                do
                {
                    startTime = new Date();

                    fixEventChannelInstrumentationBEFORE    = fixSession.getFixSessionWriter().getFixEventChannel().getEventChannelInstrumentation();
                    fixSessionInstrumentationBEFORE         = (FixSessionInstrumentation) fixSession.getFixSessionInstrumentation().clone();
                    adaptiveThreadPoolInstrumentationBEFORE = fixSession.getThreadPool().getAdaptiveThreadPoolInstrumentation();

                    ThreadHelper.sleepSeconds(sleepSeconds);

                    stopTime = new Date();

                    fixEventChannelInstrumentationAFTER    = fixSession.getFixSessionWriter().getFixEventChannel().getEventChannelInstrumentation();
                    fixSessionInstrumentationAFTER         = (FixSessionInstrumentation) fixSession.getFixSessionInstrumentation().clone();
                    adaptiveThreadPoolInstrumentationAFTER = fixSession.getThreadPool().getAdaptiveThreadPoolInstrumentation();

                    writer.write("<iteration number=\"" + (i+1) + "\" firstSnapshotTime=\"" + DateHelper.stringizeDateInLogFormat(startTime) + "\" secondSnapshotTime=\"" + DateHelper.stringizeDateInLogFormat(stopTime) + "\">");

                    elapsed = stopTime.getTime() - startTime.getTime();

                    dumpBetweenFixEventChannelInstrumentation(writer, elapsed,             fixEventChannelInstrumentationBEFORE,    fixEventChannelInstrumentationAFTER);
                    dumpBetweenFixSessionInstrumentation(writer,      elapsed,             fixSessionInstrumentationBEFORE,         fixSessionInstrumentationAFTER);
                    dumpBetweenThreadPoolInstrumentation(writer,      elapsed, fixSession, adaptiveThreadPoolInstrumentationBEFORE, adaptiveThreadPoolInstrumentationAFTER);

                    writer.write("</iteration>");
                }
                while (++i < nRepeats);
            }
            else
            {
                FixSessionInstrumentation fixSessionInstrumentation = null;

                if (BitHelper.isBitMaskSet(filter, BitHelper.INT_SET_BIT_1))
                {
                    dumpFixEventChannelInstrumentation(writer, fixSession.getFixSessionWriter().getFixEventChannel().getEventChannelInstrumentation());
                }

                if (BitHelper.isBitMaskSet(filter, BitHelper.INT_SET_BIT_2))
                {
                    fixSessionInstrumentation = (FixSessionInstrumentation) fixSession.getFixSessionInstrumentation().clone();
                    dumpFixSessionInstrumentation(writer, fixSession, fixSessionInstrumentation);
                }

                if (BitHelper.isBitMaskSet(filter, BitHelper.INT_SET_BIT_3))
                {
                    dumpThreadPoolInstrumentation(writer, fixSession, fixSession.getThreadPool().getAdaptiveThreadPoolInstrumentation());
                }

                if ("view".equals(getValue("mdReqID")))
                {
                    if (fixSessionInstrumentation == null)
                    {
                        fixSessionInstrumentation = (FixSessionInstrumentation) fixSession.getFixSessionInstrumentation().clone();
                    }

                    ObjectReferenceCountMap referenceCountMap = fixSessionInstrumentation.getSentMDReqIDMap();

                    writer.write("<mdReqIDs count=\"" + referenceCountMap.size() + "\" type=\"sent\">");

                    final FixSessionIF finalFixSession = fixSession;
                    ObjectIntVisitorIF visitor = new ObjectIntVisitorIF()
                    {
                        public int visit(Object key, int value)
                        {
                            try
                            {
                                writer.write("<mdReqID id=\"" + key + "\" sentCount=\"" + value + "\" subscribedNow=\"" + finalFixSession.containsMDReqID((String) key) + "\"/>");
                            }
                            catch (Exception ex)
                            {

                            }

                            return ObjectIntVisitorIF.CONTINUE;
                        }
                    };

                    referenceCountMap.acceptVisitor(visitor);

                    writer.write("</mdReqIDs>");

                    referenceCountMap = fixSessionInstrumentation.getRejectedMDReqIDMap();

                    writer.write("<mdReqIDs count=\"" + referenceCountMap.size() + "\" type=\"rejected\">");

                    visitor = new ObjectIntVisitorIF()
                    {
                        public int visit(Object key, int value)
                        {
                            try
                            {
                                writer.write("<mdReqID id=\"" + key + "\" rejectedCount=\"" + value + "\"/>");
                            }
                            catch (Exception ex)
                            {

                            }

                            return ObjectIntVisitorIF.CONTINUE;
                        }
                    };

                    referenceCountMap.acceptVisitor(visitor);

                    writer.write("</mdReqIDs>");
                }
            }

            writer.write("</session>");

            return writer;
        }

        writer.write("<error>unknown what(" + what + ")</error>");

        return writer;
    }

    protected void dumpFixEventChannelInstrumentation(Writer writer, SinglePriorityEventChannelIF.EventChannelInstrumentationIF fixEventChannelInstrumentation) throws Exception
    {
        writer.write("<eventChannel currentDepth=\"" + fixEventChannelInstrumentation.currentDepth() + "\" currentSize=\"" + fixEventChannelInstrumentation.currentSize() + "\" totalEnqueued=\"" + fixEventChannelInstrumentation.totalEnqueued() + "\" totalDequeued=\"" + fixEventChannelInstrumentation.totalDequeued() + "\" totalFlushed=\"" + fixEventChannelInstrumentation.totalFlushed() + "\" highWatermark=\"" + fixEventChannelInstrumentation.highWaterMark() + "\"/>");
    }

    protected void dumpFixSessionInstrumentation(Writer writer, FixSessionIF fixSession, FixSessionInstrumentation fixSessionInstrumentation) throws Exception
    {
        fixSessionInstrumentation.stop();

        long elapsedMillis = fixSessionInstrumentation.endTime - fixSessionInstrumentation.startTime;

        long seconds = DateHelper.convertMillisecondsToSeconds(elapsedMillis);
        if (seconds == 0)
        {
            seconds = 1;
        }

        writer.write("<fixSessionSummary");
        writer.write(" firstRecvAt=\""            + DateHelper.stringizeDateInLogFormat(fixSessionInstrumentation.startTime) + "\"");
        writer.write(" lastRecvAt=\""             + DateHelper.stringizeDateInLogFormat(fixSessionInstrumentation.getNetworkConnectionInstrumentor().getLastTimeReceived()) + "\"");
        writer.write(" lastSentAt=\""             + DateHelper.stringizeDateInLogFormat(fixSessionInstrumentation.getNetworkConnectionInstrumentor().getLastTimeSent()) + "\"");
        writer.write(" connectedFor=\""           + StringHelper.zeroPad(DateHelper.getHourOfDay(elapsedMillis, DateHelper.TIMEZONE_OFFSET_UTC), 2)        + ":"
                                                  + StringHelper.zeroPad(DateHelper.getMinuteOfDay(elapsedMillis), 2)      + ":"
                                                  + StringHelper.zeroPad(DateHelper.getSecondOfDay(elapsedMillis), 2)      + "."
                                                  + StringHelper.zeroPad(DateHelper.getMillisecondOfDay(elapsedMillis), 2) + "\"");
        writer.write(" totalInvalidMessages=\""   + fixSessionInstrumentation.getNetworkConnectionInstrumentor().getInvalidPacketsReceived() + "\"");
        writer.write(" totalReceivedMessages=\""  + fixSessionInstrumentation.getNetworkConnectionInstrumentor().getMsgsReceived() + "\"");
        writer.write(" totalBytesReceived=\""     + fixSessionInstrumentation.getNetworkConnectionInstrumentor().getBytesReceived() + "\"");
        writer.write(" totalSentMessages=\""      + fixSessionInstrumentation.getNetworkConnectionInstrumentor().getMsgsSent() + "\"");
        writer.write(" totalBytesSent=\""         + fixSessionInstrumentation.getNetworkConnectionInstrumentor().getBytesSent() + "\"");
        writer.write(" nextSendMsgSeqNum=\""      + fixSession.getFixSessionInformation().getNextSendMsgSeqNum() + "\"");
        writer.write(" nextRecvMsgSeqNum=\""      + fixSession.getFixSessionInformation().getNextReceiveMsgSeqNum() + "\"");
        writer.write(">");

        Map.Entry entry;
        Iterator iterator;

        int count = 0;
        for (iterator = fixSessionInstrumentation.getRecvMsgTypesMap().entrySet().iterator(); iterator.hasNext(); )
        {
            entry = (Map.Entry) iterator.next();
            count += ((MutableInteger) entry.getValue()).intValue();
        }

        writer.write("<recvMsgTypes count=\"" + count + "\">");

        for (iterator = fixSessionInstrumentation.getRecvMsgTypesMap().entrySet().iterator(); iterator.hasNext(); )
        {
            entry = (Map.Entry) iterator.next();
            writer.write("<msgType fixtag=\"" + entry.getKey() + "\" name=\"" + FixMsgTypeField.create((String) entry.getKey()).getValueDescription() + "\" count=\"" + entry.getValue() + "\"/>");
        }

        writer.write("</recvMsgTypes>");

        count = 0;
        for (iterator = fixSessionInstrumentation.getSentMsgTypesMap().entrySet().iterator(); iterator.hasNext(); )
        {
            entry = (Map.Entry) iterator.next();
            count += ((MutableInteger) entry.getValue()).intValue();
        }

        writer.write("<sentMsgTypes count=\"" + count + "\">");

        for (iterator = fixSessionInstrumentation.getSentMsgTypesMap().entrySet().iterator(); iterator.hasNext(); )
        {
            entry = (Map.Entry) iterator.next();
            writer.write("<msgType fixtag=\"" + entry.getKey() + "\" name=\"" + FixMsgTypeField.create((String) entry.getKey()).getValueDescription() + "\" count=\"" + entry.getValue() + "\"/>");
        }

        writer.write("</sentMsgTypes>");

        writer.write("</fixSessionSummary>");
    }

    protected void dumpBetweenFixEventChannelInstrumentation(Writer writer, long elapsed, SinglePriorityEventChannelIF.EventChannelInstrumentationIF fixEventChannelInstrumentationBEFORE, SinglePriorityEventChannelIF.EventChannelInstrumentationIF  fixEventChannelInstrumentationAFTER) throws Exception
    {
        writer.write("<summary type=\"eventChannel\">");

        writer.write("<eventChannelGrowth sleep=\"" + sleepSeconds + "\"");
        int growthEnqueued = (fixEventChannelInstrumentationAFTER.totalEnqueued() - fixEventChannelInstrumentationBEFORE.totalEnqueued());
        int growthDequeued = (fixEventChannelInstrumentationAFTER.totalDequeued() - fixEventChannelInstrumentationBEFORE.totalDequeued());
        writer.write(" growthDepth=\"" + (fixEventChannelInstrumentationAFTER.currentSize() - fixEventChannelInstrumentationBEFORE.currentSize()) + "\" growthEnqueue=\""   + growthEnqueued + "\" growthDequeued=\"" + growthDequeued + "\" enqueuePerSec=\"" + (growthEnqueued / (elapsed / 1000)) + "\" dequeuePerSec=\"" + (growthDequeued / (elapsed / 1000)) + "\"/>");
        writer.write("<eventChannel type=\"BEFORE\" currentDepth=\"" + fixEventChannelInstrumentationBEFORE.currentDepth() + "\" currentSize=\"" + fixEventChannelInstrumentationBEFORE.currentSize() + "\" totalEnqueued=\"" + fixEventChannelInstrumentationBEFORE.totalEnqueued() + "\" totalDequeued=\"" + fixEventChannelInstrumentationBEFORE.totalDequeued() + "\" totalFlushed=\"" + fixEventChannelInstrumentationBEFORE.totalFlushed() + "\" highWatermark=\"" + fixEventChannelInstrumentationBEFORE.highWaterMark() + "\"/>");
        writer.write("<eventChannel type=\"AFTER\" currentDepth=\"" + fixEventChannelInstrumentationAFTER.currentDepth() + "\" currentSize=\""   + fixEventChannelInstrumentationAFTER.currentSize() + "\" totalEnqueued=\"" + fixEventChannelInstrumentationAFTER.totalEnqueued()  + "\" totalDequeued=\"" + fixEventChannelInstrumentationAFTER.totalDequeued()  + "\" totalFlushed=\"" + fixEventChannelInstrumentationAFTER.totalFlushed() + "\" highWatermark=\"" + fixEventChannelInstrumentationAFTER.highWaterMark()  + "\"/>");

        writer.write("</summary>");
    }

    protected void dumpBetweenFixSessionInstrumentation(Writer writer, long elapsed, FixSessionInstrumentation fixSessionInstrumentationBEFORE, FixSessionInstrumentation fixSessionInstrumentationAFTER) throws Exception
    {
        fixSessionInstrumentationAFTER.stop();

        long elapsedMillis = fixSessionInstrumentationAFTER.endTime - fixSessionInstrumentationBEFORE.startTime;

        writer.write("<summary type=\"session\">");

        writer.write("<fixSessionSummary");
        writer.write(" firstRecvAt=\""           + DateHelper.stringizeDateInLogFormat(fixSessionInstrumentationAFTER.startTime) + "\"");
        writer.write(" lastRecvAt=\""            + DateHelper.stringizeDateInLogFormat(fixSessionInstrumentationAFTER.getNetworkConnectionInstrumentor().getLastTimeReceived()) + "\"");
        writer.write(" lastSentAt=\""            + DateHelper.stringizeDateInLogFormat(fixSessionInstrumentationAFTER.getNetworkConnectionInstrumentor().getLastTimeSent()) + "\"");
        writer.write(" connectedFor=\""          + StringHelper.zeroPad(DateHelper.getHourOfDay(elapsedMillis, DateHelper.TIMEZONE_OFFSET_UTC), 2)        + ":"
                                                 + StringHelper.zeroPad(DateHelper.getMinuteOfDay(elapsedMillis), 2)      + ":"
                                                 + StringHelper.zeroPad(DateHelper.getSecondOfDay(elapsedMillis), 2)      + "."
                                                 + StringHelper.zeroPad(DateHelper.getMillisecondOfDay(elapsedMillis), 2) + "\"");
        writer.write(" totalInvalidMessages=\""  + fixSessionInstrumentationAFTER.getNetworkConnectionInstrumentor().getInvalidPacketsReceived() + "\"");
        writer.write(" totalReceivedMessages=\"" + fixSessionInstrumentationAFTER.getNetworkConnectionInstrumentor().getMsgsReceived() + "\"");
        writer.write(" totalBytesReceived=\""    + fixSessionInstrumentationAFTER.getNetworkConnectionInstrumentor().getBytesReceived() + "\"");
        writer.write(" totalSentMessages=\""     + fixSessionInstrumentationAFTER.getNetworkConnectionInstrumentor().getMsgsSent() + "\"");
        writer.write(" totalBytesSent=\""        + fixSessionInstrumentationAFTER.getNetworkConnectionInstrumentor().getBytesSent() + "\"");
        writer.write(" timedReceivedMessages=\"" + (fixSessionInstrumentationAFTER.getNetworkConnectionInstrumentor().getMsgsReceived()  - fixSessionInstrumentationBEFORE.getNetworkConnectionInstrumentor().getMsgsReceived()) + "\"");
        writer.write(" timedBytesReceived=\""    + (fixSessionInstrumentationAFTER.getNetworkConnectionInstrumentor().getBytesReceived()           - fixSessionInstrumentationBEFORE.getNetworkConnectionInstrumentor().getBytesReceived()) + "\"");
        writer.write(" timedSentMessages=\""     + (fixSessionInstrumentationAFTER.getNetworkConnectionInstrumentor().getMsgsSent()      - fixSessionInstrumentationBEFORE.getNetworkConnectionInstrumentor().getMsgsSent()) + "\"");
        writer.write(" timedBytesSent=\""        + (fixSessionInstrumentationAFTER.getNetworkConnectionInstrumentor().getBytesSent()               - fixSessionInstrumentationBEFORE.getNetworkConnectionInstrumentor().getBytesSent()) + "\"");
        writer.write(" timedPerSecondRecv=\""    + ((fixSessionInstrumentationAFTER.getNetworkConnectionInstrumentor().getMsgsReceived() - fixSessionInstrumentationBEFORE.getNetworkConnectionInstrumentor().getMsgsReceived()) / (elapsed / 1000)) + "\"");
        writer.write(" timedPerSecondSent=\""    + ((fixSessionInstrumentationAFTER.getNetworkConnectionInstrumentor().getMsgsSent() - fixSessionInstrumentationBEFORE.getNetworkConnectionInstrumentor().getMsgsSent()) / (elapsed / 1000)) + "\"");
        writer.write(">");

        Map.Entry entry;
        Iterator iterator;

        int count = 0;
        for (iterator = fixSessionInstrumentationBEFORE.getRecvMsgTypesMap().entrySet().iterator(); iterator.hasNext(); )
        {
            entry = (Map.Entry) iterator.next();
            count += ((MutableInteger) entry.getValue()).intValue();
        }

        writer.write("<recvMsgTypes type=\"before\" count=\"" + count + "\">");

        for (iterator = fixSessionInstrumentationBEFORE.getRecvMsgTypesMap().entrySet().iterator(); iterator.hasNext(); )
        {
            entry = (Map.Entry) iterator.next();
            writer.write("<msgType fixtag=\"" + entry.getKey() + "\" name=\"" + FixMsgTypeField.create((String) entry.getKey()).getValueDescription() + "\" count=\"" + entry.getValue() + "\"/>");
        }

        writer.write("</recvMsgTypes>");

        count = 0;
        for (iterator = fixSessionInstrumentationAFTER.getRecvMsgTypesMap().entrySet().iterator(); iterator.hasNext(); )
        {
            entry = (Map.Entry) iterator.next();
            count += ((MutableInteger) entry.getValue()).intValue();
        }

        writer.write("<recvMsgTypes type=\"after\" count=\"" + count + "\">");

        for (iterator = fixSessionInstrumentationAFTER.getRecvMsgTypesMap().entrySet().iterator(); iterator.hasNext(); )
        {
            entry = (Map.Entry) iterator.next();
            writer.write("<msgType fixtag=\"" + entry.getKey() + "\" name=\"" + FixMsgTypeField.create((String) entry.getKey()).getValueDescription() + "\" count=\"" + entry.getValue() + "\"/>");
        }

        writer.write("</recvMsgTypes>");

        count = 0;
        for (iterator = fixSessionInstrumentationBEFORE.getSentMsgTypesMap().entrySet().iterator(); iterator.hasNext(); )
        {
            entry = (Map.Entry) iterator.next();
            count += ((MutableInteger) entry.getValue()).intValue();
        }

        writer.write("<sentMsgTypes type=\"before\" count=\"" + count + "\">");

        for (iterator = fixSessionInstrumentationBEFORE.getSentMsgTypesMap().entrySet().iterator(); iterator.hasNext(); )
        {
            entry = (Map.Entry) iterator.next();
            writer.write("<msgType fixtag=\"" + entry.getKey() + "\" name=\"" + FixMsgTypeField.create((String) entry.getKey()).getValueDescription() + "\" count=\"" + entry.getValue() + "\"/>");
        }

        writer.write("</sentMsgTypes>");

        count = 0;
        for (iterator = fixSessionInstrumentationAFTER.getSentMsgTypesMap().entrySet().iterator(); iterator.hasNext(); )
        {
            entry = (Map.Entry) iterator.next();
            count += ((MutableInteger) entry.getValue()).intValue();
        }

        writer.write("<sentMsgTypes type=\"after\" count=\"" + count + "\">");

        for (iterator = fixSessionInstrumentationAFTER.getSentMsgTypesMap().entrySet().iterator(); iterator.hasNext(); )
        {
            entry = (Map.Entry) iterator.next();
            writer.write("<msgType fixtag=\"" + entry.getKey() + "\" name=\"" + FixMsgTypeField.create((String) entry.getKey()).getValueDescription() + "\" count=\"" + entry.getValue() + "\"/>");
        }

        writer.write("</sentMsgTypes>");

        writer.write("</fixSessionSummary>");

        writer.write("</summary>");
    }

    protected void dumpThreadPoolInstrumentation(Writer writer, FixSessionIF fixSession, AdaptiveThreadPool.AdaptiveThreadPoolInstrumentationIF adaptiveThreadPoolInstrumentation) throws Exception
    {
        writer.write("<threadPool name=\"" + fixSession.getThreadPool().getName() +
            "\" executingThreads=\"" +      adaptiveThreadPoolInstrumentation.getCurrentlyExecutingThreads()     +
            "\" startedThreads=\"" +        adaptiveThreadPoolInstrumentation.getStartedThreads()                +
            "\" pendingThreads=\"" +        adaptiveThreadPoolInstrumentation.getPendingThreads()                +
            "\" highWatermark=\"" +         adaptiveThreadPoolInstrumentation.getStartedThreadsHighWatermark()   +
            "\" queuedSize=\"" +            adaptiveThreadPoolInstrumentation.getPendingTaskCount()              +
            "\" queuedHighWatermark=\"" +   adaptiveThreadPoolInstrumentation.getPendingTaskCountHighWatermark() +
            "\"/>");
    }

    protected void dumpBetweenThreadPoolInstrumentation(Writer writer, long elapsed, FixSessionIF fixSession, AdaptiveThreadPool.AdaptiveThreadPoolInstrumentationIF adaptiveThreadPoolInstrumentationBEFORE, AdaptiveThreadPool.AdaptiveThreadPoolInstrumentationIF adaptiveThreadPoolInstrumentationAFTER) throws Exception
    {
        writer.write("<summary type=\"threadPool\">");

        writer.write("<threadPoolGrowth name=\"" + fixSession.getThreadPool().getName() +
            "\" growthExecutingThreads=\"" +       (adaptiveThreadPoolInstrumentationAFTER.getCurrentlyExecutingThreads()     - adaptiveThreadPoolInstrumentationBEFORE.getCurrentlyExecutingThreads())     +
            "\" growthStartedThreads=\"" +         (adaptiveThreadPoolInstrumentationAFTER.getStartedThreads()                - adaptiveThreadPoolInstrumentationBEFORE.getStartedThreads())                +
            "\" growthPendingThreads=\"" +         (adaptiveThreadPoolInstrumentationAFTER.getPendingThreads()                - adaptiveThreadPoolInstrumentationBEFORE.getPendingThreads())                +
            "\" growthHighWatermark=\"" +          (adaptiveThreadPoolInstrumentationAFTER.getStartedThreadsHighWatermark()   - adaptiveThreadPoolInstrumentationBEFORE.getStartedThreadsHighWatermark())   +
            "\" growthQueuedSize=\"" +             (adaptiveThreadPoolInstrumentationAFTER.getPendingTaskCount()              - adaptiveThreadPoolInstrumentationBEFORE.getPendingTaskCount())              +
            "\" growthQueuedHighWatermark=\"" +    (adaptiveThreadPoolInstrumentationAFTER.getPendingTaskCountHighWatermark() - adaptiveThreadPoolInstrumentationBEFORE.getPendingTaskCountHighWatermark()) +
            "\"/>");

        writer.write("<threadPool type=\"BEFORE" +
            "\" currentlyExecutingThreads=\"" +      adaptiveThreadPoolInstrumentationBEFORE.getCurrentlyExecutingThreads() +
            "\" startedThreads=\"" +                 adaptiveThreadPoolInstrumentationBEFORE.getStartedThreads() +
            "\" pendingThreads=\"" +                 adaptiveThreadPoolInstrumentationBEFORE.getPendingThreads() +
            "\" startedThreadsHighWatermark=\"" +    adaptiveThreadPoolInstrumentationBEFORE.getStartedThreadsHighWatermark() +
            "\" pendingTaskCount=\"" +               adaptiveThreadPoolInstrumentationBEFORE.getPendingTaskCount() +
            "\" pendingTaskCountHighWatermark=\"" +  adaptiveThreadPoolInstrumentationBEFORE.getPendingTaskCountHighWatermark() +
            "\"/>");

        writer.write("<threadPool type=\"AFTER" +
            "\" currentlyExecutingThreads=\"" +      adaptiveThreadPoolInstrumentationAFTER.getCurrentlyExecutingThreads() +
            "\" startedThreads=\"" +                 adaptiveThreadPoolInstrumentationAFTER.getStartedThreads() +
            "\" pendingThreads=\"" +                 adaptiveThreadPoolInstrumentationAFTER.getPendingThreads() +
            "\" startedThreadsHighWatermark=\"" +    adaptiveThreadPoolInstrumentationAFTER.getStartedThreadsHighWatermark() +
            "\" pendingTaskCount=\"" +               adaptiveThreadPoolInstrumentationAFTER.getPendingTaskCount() +
            "\" pendingTaskCountHighWatermark=\"" +  adaptiveThreadPoolInstrumentationAFTER.getPendingTaskCountHighWatermark() +
            "\"/>");

        writer.write("</summary>");
    }

    protected void writeSessionNameInformation(Writer writer, String prefix, String sessionName, int port, String suffix) throws Exception
    {
        if (prefix != null)
        {
            writer.write(prefix);
            if (!prefix.endsWith(" "))
            {
                writer.write(" ");
            }
        }

        writer.write("name=\""); writer.write(sessionName); writer.write("\" ");
        writer.write("description=\""); writer.write(PropertiesHelper.instance().getProperty(CfixHomeImpl.cfixProperties, "session." + sessionName, "cfix.fixSession.description", "")); writer.write("\" ");
        if (port == 0)
        {
            writer.write("fullName=\"\" ");
            writer.write("engineName=\"\" ");
            writer.write("enginePort=\"\" ");
        }
        else
        {
            writer.write("fullName=\""); writer.write(sessionName + "." + FixSessionManagerLocator.getEngineNameForPort(port)); writer.write("\" ");
            writer.write("engineName=\""); writer.write(FixSessionManagerLocator.getEngineNameForPort(port)); writer.write("\" ");
            writer.write("enginePort=\""); writer.write("" + port); writer.write("\" ");
        }

        if (suffix != null)
        {
            writer.write(suffix);
        }
    }
}
