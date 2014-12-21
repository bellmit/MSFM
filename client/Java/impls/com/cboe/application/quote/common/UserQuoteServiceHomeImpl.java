package com.cboe.application.quote.common;


import com.cboe.idl.property.PropertyGroupStruct;
import com.cboe.domain.rateMonitor.RateLimitsFactory;
import com.cboe.domain.startup.ClientBOHome;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.UserQuoteServiceHome;
import com.cboe.interfaces.application.UserQuoteService;
import com.cboe.interfaces.businessServices.PropertyService;
import com.cboe.interfaces.businessServices.PropertyServiceHome;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.interfaces.domain.RateMonitorTypeConstants;
import com.cboe.interfaces.domain.property.PropertyCategoryTypes;
import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;
import com.cboe.interfaces.domain.rateMonitor.RateLimits;


import com.cboe.domain.property.PropertyFactory;
import com.cboe.domain.util.RateMonitorKeyContainer;
import com.cboe.util.ExceptionBuilder;

import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * @author Jing Chen
 */
public class UserQuoteServiceHomeImpl extends ClientBOHome implements UserQuoteServiceHome
{
    public static final String TOKENIZER_DELIMITERS = ",; ";
    public static final String RATE_MONITOR_SESSIONS_PROPERTY_NAME = "rateMonitorSessions";
    public static final String START_TIME_PROPERTY_NAME = "rateMonitorStartTime";
    public final static String CALL_WINDOW_INTERVAL_PROPERTY_NAME = "rateMonitorInterval";
    public final static String CALL_WINDOW_SIZE_PROPERTY_NAME = "rateMonitorWindow";
    public final static String QUOTE_WINDOW_INTERVAL_PROPERTY_NAME = "quoteRateMonitorInterval";
    public final static String QUOTE_WINDOW_SIZE_PROPERTY_NAME = "quoteRateMonitorWindow";
    public static final String QUOTE_SEQUENCE_SIZE_PROPERTY_NAME = "rateMonitorQuoteSequenceSize";
    public static final String QUOTE_DELETE_REPORT_PROPERTY_NAME = "quoteDeleteReportDispatch";
    public static final Long EMPTY_LONG_CONSTRAINT = 0L;
    public static final Integer EMPTY_INTEGER_CONSTRAINT = 0;
    protected Map sessionConstraints;
    protected Map userQuoteServices;
    private PropertyService propertyService = null;

    public UserQuoteServiceHomeImpl()
    {
        super();
    }

    public UserQuoteService find(BaseSessionManager sessionManager)
    {
        return create(sessionManager);
    }

    public UserQuoteService create(BaseSessionManager sessionManager)
    {
        UserQuoteServiceImpl userQuote = (UserQuoteServiceImpl)userQuoteServices.get(sessionManager);
        if(userQuote == null)
        {
            userQuote = new UserQuoteServiceImpl(sessionManager, sessionConstraints);
            addToContainer(userQuote);
            userQuoteServices.put(sessionManager, userQuote);
        }
        return userQuote;
    }

    public void remove(BaseSessionManager sessionManager)
    {
        userQuoteServices.remove(sessionManager);
    }

    public void clientInitialize() throws Exception
    {
        Boolean isQuoteDeleteReportDispatch = null;
        boolean defaultValue = true;
        
        if (Log.isDebugOn())
        {
            Log.debug(this, "SMA Type = " + this.getSmaType());
        }

        sessionConstraints = new HashMap(11);
        userQuoteServices = new HashMap(11);
        
        //new property for quote delete report dispatch. The default value will always "true", send to report back to user.
        isQuoteDeleteReportDispatch = defaultValue;
        String quoteDeleteReportDispatch = getProperty(UserQuoteServiceHomeImpl.QUOTE_DELETE_REPORT_PROPERTY_NAME);
        StringBuilder sb = new StringBuilder(quoteDeleteReportDispatch.length()+40);
        sb.append("quoteDeleteReportDispatch set to: ").append(quoteDeleteReportDispatch);
        Log.information(this, sb.toString());
        isQuoteDeleteReportDispatch = Boolean.valueOf(quoteDeleteReportDispatch);
        //add quote delete report dispatch property to the map. This property will be valid for all session.
        sessionConstraints.put(UserQuoteServiceHomeImpl.QUOTE_DELETE_REPORT_PROPERTY_NAME, isQuoteDeleteReportDispatch); 
        
        String sessionProperty = getProperty(UserQuoteServiceHomeImpl.RATE_MONITOR_SESSIONS_PROPERTY_NAME);
        String startTimeProperty = getProperty(UserQuoteServiceHomeImpl.START_TIME_PROPERTY_NAME);
        String callWindowIntervalProperty = getProperty(UserQuoteServiceHomeImpl.CALL_WINDOW_INTERVAL_PROPERTY_NAME);
        String callWindowSizeProperty = getProperty(UserQuoteServiceHomeImpl.CALL_WINDOW_SIZE_PROPERTY_NAME);
        String quoteWindowIntervalProperty = getProperty(UserQuoteServiceHomeImpl.QUOTE_WINDOW_INTERVAL_PROPERTY_NAME);
        String quoteWindowSizeProperty = getProperty(UserQuoteServiceHomeImpl.QUOTE_WINDOW_SIZE_PROPERTY_NAME);
        String quoteSequenceSizeProperty = getProperty(UserQuoteServiceHomeImpl.QUOTE_SEQUENCE_SIZE_PROPERTY_NAME);    
        
        if(sessionProperty.length() > 0)
        {
            StringTokenizer sessionTokenizer = new StringTokenizer(sessionProperty, UserQuoteServiceHomeImpl.TOKENIZER_DELIMITERS, false);
            StringTokenizer startTimeTokenizer =
                    new StringTokenizer(startTimeProperty, UserQuoteServiceHomeImpl.TOKENIZER_DELIMITERS, false);
            StringTokenizer callWindowIntervalTokenizer =
                    new StringTokenizer(callWindowIntervalProperty, UserQuoteServiceHomeImpl.TOKENIZER_DELIMITERS, false);
            StringTokenizer callWindowSizeTokenizer =
                    new StringTokenizer(callWindowSizeProperty, UserQuoteServiceHomeImpl.TOKENIZER_DELIMITERS, false);
            StringTokenizer quoteWindowIntervalTokenizer =
                    new StringTokenizer(quoteWindowIntervalProperty, UserQuoteServiceHomeImpl.TOKENIZER_DELIMITERS, false);
            StringTokenizer quoteWindowSizeTokenizer =
                    new StringTokenizer(quoteWindowSizeProperty, UserQuoteServiceHomeImpl.TOKENIZER_DELIMITERS, false);
            StringTokenizer quoteSequenceSizeTokenizer =
                    new StringTokenizer(quoteSequenceSizeProperty, UserQuoteServiceHomeImpl.TOKENIZER_DELIMITERS, false);
            
            
            String sessionToken;
            Long startTimeToken;
            Long callWindowIntervalToken;
            Integer callWindowSizeToken;
            Long quoteWindowIntervalToken;
            Integer quoteWindowSizeToken;
            Integer quoteSequenceSizeToken;
      
            Map settingsMap;
            GregorianCalendar startCal = new GregorianCalendar();

            while(sessionTokenizer.hasMoreTokens())
            {
                sessionToken = sessionTokenizer.nextToken();
                settingsMap = new HashMap(6);

                startTimeToken = UserQuoteServiceHomeImpl.EMPTY_LONG_CONSTRAINT;
                if (startTimeTokenizer.hasMoreTokens())
                {
                    String timeToken = startTimeTokenizer.nextToken();
                    String[] hhmm = timeToken.split(":");
                    if (hhmm.length == 2)
                    {
                        int hours = Integer.valueOf(hhmm[0]);
                        int minutes = Integer.valueOf(hhmm[1]);
                        startCal.setTimeInMillis(System.currentTimeMillis());
                        startCal.set(GregorianCalendar.HOUR_OF_DAY, hours);
                        startCal.set(GregorianCalendar.MINUTE, minutes);
                        startCal.set(GregorianCalendar.SECOND, 0);
                        startCal.set(GregorianCalendar.MILLISECOND, 0);
                        startTimeToken = startCal.getTimeInMillis();
                    }
                    else if (!(hhmm.length == 1 && hhmm[0].equals("0")))
                    {
                        Log.information(this, "Invalid rateMonitorStartTime:" + timeToken + " for session:" + sessionToken);
                    }
                }
                settingsMap.put(UserQuoteServiceHomeImpl.START_TIME_PROPERTY_NAME, startTimeToken);

                if(callWindowIntervalTokenizer.hasMoreTokens())
                {
                    callWindowIntervalToken = Long.valueOf(callWindowIntervalTokenizer.nextToken());
                }
                else
                {
                    callWindowIntervalToken = UserQuoteServiceHomeImpl.EMPTY_LONG_CONSTRAINT;
                }
                settingsMap.put(UserQuoteServiceHomeImpl.CALL_WINDOW_INTERVAL_PROPERTY_NAME, callWindowIntervalToken);

                if(callWindowSizeTokenizer.hasMoreTokens())
                {
                    callWindowSizeToken = Integer.valueOf(callWindowSizeTokenizer.nextToken());
                }
                else
                {
                    callWindowSizeToken = UserQuoteServiceHomeImpl.EMPTY_INTEGER_CONSTRAINT;
                }
                settingsMap.put(UserQuoteServiceHomeImpl.CALL_WINDOW_SIZE_PROPERTY_NAME, callWindowSizeToken);

                if(quoteWindowIntervalTokenizer.hasMoreTokens())
                {
                    quoteWindowIntervalToken = Long.valueOf(quoteWindowIntervalTokenizer.nextToken());
                }
                else
                {
                    quoteWindowIntervalToken = UserQuoteServiceHomeImpl.EMPTY_LONG_CONSTRAINT;
                }
                settingsMap.put(UserQuoteServiceHomeImpl.QUOTE_WINDOW_INTERVAL_PROPERTY_NAME, quoteWindowIntervalToken);

                if(quoteWindowSizeTokenizer.hasMoreTokens())
                {
                    quoteWindowSizeToken = Integer.valueOf(quoteWindowSizeTokenizer.nextToken());
                }
                else
                {
                    quoteWindowSizeToken = UserQuoteServiceHomeImpl.EMPTY_INTEGER_CONSTRAINT;
                }
                settingsMap.put(UserQuoteServiceHomeImpl.QUOTE_WINDOW_SIZE_PROPERTY_NAME, quoteWindowSizeToken);

                if(quoteSequenceSizeTokenizer.hasMoreTokens())
                {
                    quoteSequenceSizeToken = Integer.valueOf(quoteSequenceSizeTokenizer.nextToken());
                }
                else
                {
                    quoteSequenceSizeToken = UserQuoteServiceHomeImpl.EMPTY_INTEGER_CONSTRAINT;
                }
                settingsMap.put(UserQuoteServiceHomeImpl.QUOTE_SEQUENCE_SIZE_PROPERTY_NAME, quoteSequenceSizeToken);
                    
                sessionConstraints.put(sessionToken, settingsMap);
            }
            
        }
        sessionConstraints = Collections.unmodifiableMap(sessionConstraints);
        try{
        	registerCommand(this, "quoteRateMonitor", "quoteRateMonitorArCallback","Display User Quote RateMonitor per Session",
                new String[] { String.class.getName(), String.class.getName()},           
                new String[] { "userId","session Name"});
        }catch (Exception e)
        {
            Log.exception(this, "Cannot register ar command. Ignoring exception", e);
        }
        		               
    }
   
    
    public String quoteRateMonitorArCallback(String thisUserId, String sessionName)
    
    {
    	StringBuilder s = new StringBuilder();
    	int windowSize;
        long windowMilliSecondPeriod;
        String thisExchange = "CBOE";
        String thisAcronym = thisUserId;
        RateMonitorKeyContainer key = null;
        

    	Log.debug("userRateMonitorArCallback userId: "+thisUserId+"sessionName: "+sessionName);
        if (!(thisUserId.equalsIgnoreCase("")))
        {
        	 if ((sessionName.equalsIgnoreCase("CFE_MAIN"))
        			 ||(sessionName.equalsIgnoreCase("W_MAIN"))
        			 ||(sessionName.equalsIgnoreCase("ONE_MAIN"))
        			 ||(sessionName.equalsIgnoreCase("W_STOCK"))
        			 ||(sessionName.equalsIgnoreCase("C2_MAIN")))
        	 {
        		 s.append("User: ")
	              .append(thisUserId)
	              .append(" sessionName: ")
	              .append(sessionName).append("\n");
		 		 short[] rateLimitTypeLists = {2,3,7};
		 	
		 		 for(int i =0; i<rateLimitTypeLists.length; i++)
		 		 {
		 			key = new RateMonitorKeyContainer(
							thisUserId, thisExchange, thisAcronym, sessionName,
							rateLimitTypeLists[i]);
			 		try
				    {
				        PropertyGroupStruct propertyGroupStruct = getPropertyService().getProperties(PropertyCategoryTypes.RATE_LIMITS
				                                                                                     ,RateLimitsFactory.getRateMonitorKey(key.getUserId()
			                                                                                                 , key.getExchange()
			                                                                                                 , key.getAcronym()));
				        PropertyServicePropertyGroup propertyServiceGroup = PropertyFactory.createPropertyGroup(propertyGroupStruct);
				        RateLimits rateLimit = RateLimitsFactory.getRateLimitBySessionType(propertyServiceGroup, key.getSession(), key.getType());
				        windowSize = rateLimit.getWindowSize();
				        windowMilliSecondPeriod = rateLimit.getWindowInterval();
				         
				        
				        if (rateLimitTypeLists[i] ==  RateMonitorTypeConstants.QUOTES)
				        {
				        	s.append("Quote rate: ").append(windowSize).append(" per ")
				        	.append(windowMilliSecondPeriod).append(" Millis")
				        	.append("\n");
				        }else if (rateLimitTypeLists[i] == RateMonitorTypeConstants.ACCEPT_QUOTE)
				        {
				        	s.append("Quote calls: ").append(windowSize).append(" per ")
				        	.append(windowMilliSecondPeriod).append(" Millis")
				        	.append("\n");
				        }else
				        {
				        	s.append("Quote Block Size: ").append(windowSize).append("\n");
				        }
				        
				    }
            		catch(Exception e)
            	    {
    			        s.append("Unable to get Rate Limit for user:")
    			         .append(thisUserId)
    			         .append(" sessionName: ")
    			         .append(sessionName)
    			         .append(" type: ")
    			         .append(rateLimitTypeLists[i])
    			         .append("\n");
            			         
            		}	
        		 }
        	 }else if (sessionName.equalsIgnoreCase(""))
        	 {
        		 // list all session rate limit.
        		 s.append("Usage: casadmin quoteRateMonitor UserName SessionName")
        		  .append("\n")
        		  .append("       Username: e.g. X01")
        		  .append("\n")
        		  .append("       SessionName: e.g. W_MAIN");
        	 }else
        	 {
        		 s.append("invalid session name: ").append(sessionName);
        	 }
        }else
        {
            s.append("invalid user id: ").append(thisUserId);	
        }
        
	    return s.toString();
    }

    
    public Map getSessionConstraints()
    {
    	return sessionConstraints;
    }
    
    
    private PropertyService getPropertyService()
    {
        if ( propertyService == null )
        {
            try
            {
                PropertyServiceHome home = (PropertyServiceHome) HomeFactory.getInstance().findHome(PropertyServiceHome.HOME_NAME);
                propertyService = home.find();
            }
            catch(CBOELoggableException e)
            {
                Log.exception("Could not find PropertyServiceHome", e);
                // a really ugly way to get around the missing exception in the interface...
//                throw new NullPointerException("Could not find PropertyServiceHome");
            }
        }
        return propertyService;
    }
}
