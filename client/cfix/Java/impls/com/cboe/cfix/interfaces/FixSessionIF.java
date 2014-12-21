package com.cboe.cfix.interfaces;

/**
 * FixSessionIF.java
 *
 * @author Dmitry Volpyansky
 *
 */

import java.net.*;
import java.util.*;

import com.cboe.cfix.util.*;
import com.cboe.client.util.threadpool.*;
import com.cboe.client.util.*;
import com.cboe.interfaces.cfix.*;

public interface FixSessionIF extends Runnable
{
    public static final int             MESSAGE_REJECTED_FIRST_MESSAGE_NOT_LOGON = 1 << 1;
    public static final int             MESSAGE_REJECTED_LOGON_UNDERSEQUENCE     = 1 << 2;
    public static final int             MESSAGE_REJECTED_INVALID_COMP_ID         = 1 << 3;
    public static final int             MESSAGE_REJECTED_SUBSEQUENT_LOGON        = 1 << 4;
    public static final int             MESSAGE_REJECTED_INVALID_TAG             = 1 << 5;
    public static final int             MESSAGE_REJECTED_INVALID_TAG_VALUE       = 1 << 6;
    public static final int             MESSAGE_REJECTED_MALFORMED_MESSAGE       = 1 << 7;

    public FixSessionIF                 initialize(FixSessionManagerIF fixSessionManager, String propertyPrefix, Properties sessionProperties) throws Exception;
    public void                         setPort(int port);
    public int                          getPort();
    public void                         setTargetCompID(String targetCompID);
    public String                       getTargetCompID();
    public void                         setSenderCompID(String senderCompID);
    public String                       getSenderCompID();
    public PropertiesHelper             getPropertiesHelper();
    public FixMessageFactoryIF          getFixMessageFactory();
    public FixSessionWriterIF           getFixSessionWriter();
    public FixSessionInstrumentationIF  getFixSessionInstrumentation();
    public FixSessionInformationIF      getFixSessionInformation();
    public void                         setFixSessionInformation(FixSessionInformationIF fixSessionInformation);
    public boolean                      enqueueOutboundFixMessage(Object object);
    public int                          getQueueDepth();
    public void                         resetSocket(Socket socket);
    public void                         addFixSessionListener(FixSessionListenerIF fixSessionListener);
    public void                         removeFixSessionListener(FixSessionListenerIF fixSessionListener);
    public void                         terminate(boolean immediate);
    public CfixSessionManager           getCfixSessionManager();
    public int                          setDebugFlags(int debugFlags);
    public int                          getDebugFlags();
    public String                       getSessionName();
    public void                         blockUntilSessionTerminated();
    public void                         setThreadPool(AdaptiveThreadPool threadPool);
    public AdaptiveThreadPool           getThreadPool();
    public Properties                   getSessionProperties();
    public boolean                      containsMDReqID(String mdReqID);
    public void                         acceptLogout(String reason);
    public void                         externallyGenerateFixTestRequestMessage();
    public void                         externallyGenerateFixHeartBeatMessage();
    public OverlayPolicyFactory         getOverlayPolicyFactory();
}