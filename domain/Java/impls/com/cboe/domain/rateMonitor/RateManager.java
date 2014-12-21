package com.cboe.domain.rateMonitor;

import com.cboe.domain.util.RateMonitorKeyContainer;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.idl.cmiErrorCodes.NotAcceptedCodes;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;
import com.cboe.infrastructureServices.systemsManagementService.NoSuchPropertyException;
import com.cboe.interfaces.domain.RateMonitor;
import com.cboe.interfaces.domain.RateMonitorHome;
import com.cboe.interfaces.domain.RateMonitorTypeConstants;
import com.cboe.util.ExceptionBuilder;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Collections;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Piyush Patel
 *         Date: August 20, 2010
 *         Time: 10:10:10 AM
 */
public class RateManager
{

    public static final String RATE_MONITOR_SESSIONS_PROPERTY_NAME = "rateMonitorSessions";
    public static final String START_TIME_PROPERTY_NAME = "rateMonitorStartTime";
    public static final String CALL_WINDOW_INTERVAL_PROPERTY_NAME = "rateMonitorInterval";
    public static final String CALL_WINDOW_SIZE_PROPERTY_NAME = "rateMonitorWindow";
    public static final String RATE_MONITOR_CACHE_SESSION_NAME = "rateMonitorCacheSessionName";
    public final static String QUOTE_WINDOW_INTERVAL_PROPERTY_NAME = "quoteRateMonitorInterval";
    public final static String QUOTE_WINDOW_SIZE_PROPERTY_NAME = "quoteRateMonitorWindow";
    public static final String QUOTE_SEQUENCE_SIZE_PROPERTY_NAME = "rateMonitorQuoteSequenceSize";
    public static final String RATE_MONITOR_CACHE_SESSION_NAME_DEFAULT = "";
    public static final String RATE_MONITOR_CACHE_SESSION_NAME_DELIMITATE = ",";
    public static final String TOKENIZER_DELIMITERS = ",; ";
    public static final Long EMPTY_LONG_CONSTRAINT = 0L;
    public static final Integer EMPTY_INTEGER_CONSTRAINT = 0;
    public static final String SPACE = " ";
    private Map sessionConstraints;
    private String userId;
    private String exchange;
    private String acronym;
    private RateMonitorHome rateMonitorHome;
    protected Map<String, EnhancedRateMonitor> rateMonitors = new HashMap();

    public RateManager(Map sessionConstraints, String userId, String exchange, String acronym, Short[] monitorTypes)
    {
        this.sessionConstraints = sessionConstraints;
        this.userId = userId;
        this.exchange = exchange;
        this.acronym = acronym;
        String rateMonitorCacheSession = System.getProperty(
                RATE_MONITOR_CACHE_SESSION_NAME,
                RATE_MONITOR_CACHE_SESSION_NAME_DEFAULT);
        if (rateMonitorCacheSession != null && rateMonitorCacheSession.length() != 0)
        {
            String[] cachedSessions = rateMonitorCacheSession
                    .split(RATE_MONITOR_CACHE_SESSION_NAME_DELIMITATE);
            for (int i = 0; i < cachedSessions.length; i++)
            {
                String sessionName = cachedSessions[i];
                for (short monitorType : monitorTypes)
                {
                    EnhancedRateMonitor monitor = createRateMonitor(sessionName, monitorType);
                    rateMonitors.put(getRateMonitorKey(sessionName, monitorType), monitor);
                }
            }
        }
    }

    public EnhancedRateMonitor findMonitor(String sessionName,short monitorType)
    {
        EnhancedRateMonitor erm = rateMonitors.get(getRateMonitorKey(sessionName, monitorType));
        // too bad rate monitor was not setup at login.
        if (erm == null)
        {
            StringBuffer msg = new StringBuffer(100);
            msg.append("RateManager was not initialized at login for UID:");
            msg.append(userId).append(SPACE);
            msg.append(exchange).append(SPACE);
            msg.append("UA:").append(acronym).append(SPACE);
            msg.append("SESN:").append(sessionName).append(SPACE);
            msg.append("MonitorType:").append(monitorType);
            Log.information(msg.toString());
            erm = createRateMonitor(sessionName, monitorType);
            rateMonitors.put(getRateMonitorKey(sessionName, monitorType), erm);
        }
        return erm;
    }


    public void monitorRate(String sessionName, long currentTime, String methodName, short monitorType) throws NotAcceptedException
    {

        EnhancedRateMonitor erm = findMonitor(sessionName, monitorType);
        if (currentTime < erm.getStartTime())
        {
            return;
        }
        RateMonitor rateMonitor = erm.getRateMonitor();
        if (rateMonitor.canAccept(currentTime) == false)
        {
            StringBuilder msg = new StringBuilder(120);
            msg.append(methodName).append(
                    " rejected. Call limit exceeded for ").append(
                    sessionName).append(" UA:").append(userId).append('.');
            msg.append(" Rate:").append(rateMonitor.getWindowSize())
                    .append(", Within:").append(
                    rateMonitor.getWindowMilliSecondPeriod());
            msg.append(" millis.");
            throw ExceptionBuilder.notAcceptedException(msg.toString(),
                                                        NotAcceptedCodes.RATE_EXCEEDED);
        }

    }

    public void monitorQuoteRate(String sessionName, int len) throws NotAcceptedException
    {
        long currentTime = System.currentTimeMillis();
        EnhancedRateMonitor erm = findMonitor(sessionName, RateMonitorTypeConstants.ACCEPT_QUOTE);

        if(currentTime<erm.getStartTime())
        {
            return;
        }
        // We need to check multiple RateMonitor objects. For all but the last
        // one call previewCanAccept so that if any RateMonitor prohibits the
        // operation now, we haven't changed any of the others. After the last
        // one succeeds, call monitorRate where we had previously called
        // previewCanAccept in order to update those RateMonitor objects.

        // Check call limit
        RateMonitor callRateMonitor = erm.getRateMonitor();
        if(!callRateMonitor.previewCanAccept(currentTime))
        {
               callLimitExceeded(sessionName,callRateMonitor);
        }
        // Check quote rate limit
        erm = findMonitor(sessionName, RateMonitorTypeConstants.QUOTES);
        RateMonitor qouteRateMonitor = erm.getRateMonitor();
        if(!qouteRateMonitor.canAccept(currentTime,len))
        {
            StringBuilder msg = new StringBuilder(120);
            msg.append("accepQuote/acceptQuotesForClass rejected. Quote rate limit exceeded for ")
                    .append(sessionName).append(". Rate:").append(qouteRateMonitor.getWindowSize())
                    .append(", Within:").append(qouteRateMonitor.getWindowMilliSecondPeriod())
                    .append(" millis.");
            throw ExceptionBuilder.notAcceptedException(msg.toString(),
                                                        NotAcceptedCodes.QUOTE_RATE_EXCEEDED);

        }
        // Call monitorRate where we called previewCanAccept before
        if(!callRateMonitor.canAccept(currentTime))
        {
            // Nothing should have changed between call to previewCanAccept and
            // this call to canAccept, but handle this impossible case anyway.
            callLimitExceeded(sessionName,callRateMonitor);
        }
    }

    public void canAcceptBlock(String sessionName, short monitorType, int blockSize) throws NotAcceptedException
    {

        EnhancedRateMonitor erm = findMonitor(sessionName, monitorType);
        RateMonitor rateMonitor = erm.getRateMonitor();
        if (!rateMonitor.canAcceptBlock(blockSize))
        {
            StringBuilder msg = new StringBuilder(85);
            msg.append("acceptQuotesForClass rejected. Sequence size limit exceeded for ").append(sessionName);
            msg.append('.');
            msg.append(" Size:").append(rateMonitor.getWindowSize());
            throw ExceptionBuilder.notAcceptedException(msg.toString(),
                                                        NotAcceptedCodes.SEQUENCE_SIZE_EXCEEDED);
        }
    }

    private void callLimitExceeded(String sessionName, RateMonitor callRateMonitor) throws NotAcceptedException
    {
        StringBuilder msg = new StringBuilder(120);
        msg.append("acceptQuote/acceptQuotesForClass rejected. Call limit exceeded for ")
           .append(sessionName).append(". Rate:").append(callRateMonitor.getWindowSize())
           .append(", Within:").append(callRateMonitor.getWindowMilliSecondPeriod()).append(" millis.");
        throw ExceptionBuilder.notAcceptedException(msg.toString(), NotAcceptedCodes.RATE_EXCEEDED);
    }

    private EnhancedRateMonitor createRateMonitor(String sessionName, short monitorType)
    {

        EnhancedRateMonitor result = null;
        int windowSize = 0;
        long windowMilliSecondPeriod = 0;
        long startTime = 0;
        if (sessionConstraints != null)
        {
            Map constraints = (Map) sessionConstraints.get(sessionName);
            if (monitorType == RateMonitorTypeConstants.QUOTE_BLOCK_SIZE)
            {
                Object constraint = constraints.get(QUOTE_SEQUENCE_SIZE_PROPERTY_NAME);
                if (constraint != null)
                {
                    int maxSequenceSize = ((Integer) constraint).intValue();
                    if (maxSequenceSize > 0)
                    {
                        windowSize = maxSequenceSize;
                        windowMilliSecondPeriod = 0L;
                    }
                }
            }
            else if (monitorType == RateMonitorTypeConstants.QUOTES)
            {
                Object constraint = constraints.get(QUOTE_WINDOW_INTERVAL_PROPERTY_NAME);
                if (constraint != null)
                {
                    windowMilliSecondPeriod = ((Long) constraint).longValue();
                }
                constraint = constraints.get(QUOTE_WINDOW_SIZE_PROPERTY_NAME);
                if (constraint != null)
                {
                    windowSize = ((Integer) constraint).intValue();
                }
            }
            else
            {
                Object constraint = constraints
                        .get(CALL_WINDOW_INTERVAL_PROPERTY_NAME);
                if (constraint != null)
                {
                    windowMilliSecondPeriod = ((Long) constraint).longValue();
                }
                constraint = constraints
                        .get(CALL_WINDOW_SIZE_PROPERTY_NAME);
                if (constraint != null)
                {
                    windowSize = ((Integer) constraint).intValue();
                }
                constraint = constraints
                        .get(START_TIME_PROPERTY_NAME);
                if (constraint != null)
                {
                    startTime = ((Long) constraint).longValue();
                }
            }

            RateMonitorKeyContainer rateMonitorKey = new RateMonitorKeyContainer(
                    userId, exchange, acronym, sessionName,
                    monitorType);

            RateMonitor rateMonitor
                    = getRateMonitorHome().find(rateMonitorKey,
                                                windowSize, windowMilliSecondPeriod);

            result = new EnhancedRateMonitor(rateMonitor, startTime);
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTimeInMillis(startTime);
            StringBuilder msg = new StringBuilder(150);
            msg.append("RateManager.createRateMonitor(")
                    .append("UID:").append(userId).append(SPACE)
                    .append(exchange).append(SPACE)
                    .append("UA:").append(acronym).append(SPACE)
                    .append("SESN:").append(sessionName).append(SPACE)
                    .append("StartTime:")
                    .append(cal.get(GregorianCalendar.HOUR_OF_DAY))
                    .append(":")
                    .append(cal.get(GregorianCalendar.MINUTE)).append(SPACE)
                    .append("MonitorType:").append(monitorType).append(SPACE)
                    .append("Rate:").append(rateMonitor.getWindowSize()).append(SPACE)
                    .append("Within:").append(rateMonitor.getWindowMilliSecondPeriod()).append(" millis")
                    .append(")");
            Log.information(msg.toString());
        }
        return result;
    }

    private RateMonitorHome getRateMonitorHome()
    {
        if (rateMonitorHome == null)
        {
            try
            {
                rateMonitorHome = (RateMonitorHome) HomeFactory.getInstance().findHome(RateMonitorHome.HOME_NAME);
            }
            catch (CBOELoggableException e)
            {
                Log.exception("Could not find RateMonitor Home", e);
                // a really ugly way to get around the missing exception in the interface...
                throw new NullPointerException("Could not find RateMonitor Home");
            }
        }

        return rateMonitorHome;
    }

    /**
     * SessionName property is important since we support one rate monitor per trading session
     * Otherwise use only monitor type to avoid extra string object creation.
     *
     * @param sessionName
     * @param monitorType
     * @return
     */
    private String getRateMonitorKey(String sessionName, short monitorType)
    {
        StringBuffer sf = new StringBuffer(15);
        sf.append(sessionName).append(monitorType);
        return sf.toString();
    }
    
    public RateMonitor getRateMonitor(String sessionName, short monitorType)
    {
        EnhancedRateMonitor erm = findMonitor(sessionName, monitorType);        
        return erm.getRateMonitor();        
    }

    private class EnhancedRateMonitor
    {

        private long startTime;
        private RateMonitor rateMonitor;

        public EnhancedRateMonitor(RateMonitor ratemonitor, long startTime)
        {
            this.rateMonitor = ratemonitor;
            this.startTime = startTime;
        }

        public long getStartTime()
        {
            return startTime;
        }

        public RateMonitor getRateMonitor()
        {
            return rateMonitor;
        }

    }
}