package com.cboe.application.order.common;

import com.cboe.domain.property.PropertyFactory;
import com.cboe.domain.rateMonitor.RateLimitsFactory;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.domain.util.RateMonitorKeyContainer;
import com.cboe.idl.property.PropertyGroupStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.UserOrderService;
import com.cboe.interfaces.application.UserOrderServiceHome;
import com.cboe.interfaces.domain.RateMonitorTypeConstants;
import com.cboe.interfaces.domain.property.PropertyCategoryTypes;
import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;
import com.cboe.interfaces.domain.rateMonitor.RateLimits;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.application.shared.ServicesHelper;

import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * @author Jing Chen
 */
public class UserOrderServiceHomeImpl extends ClientBOHome implements UserOrderServiceHome
{
    public static final String TOKENIZER_DELIMITERS = ",; ";
    public static final String RATE_MONITOR_SESSIONS_PROPERTY_NAME = "rateMonitorSessions";
    public static final String START_TIME_PROPERTY_NAME = "rateMonitorStartTime";
    public final static String CALL_WINDOW_INTERVAL_PROPERTY_NAME = "rateMonitorInterval";
    public final static String CALL_WINDOW_SIZE_PROPERTY_NAME = "rateMonitorWindow";
    public static final Long EMPTY_LONG_CONSTRAINT = 0L;
    public static final Integer EMPTY_INTEGER_CONSTRAINT = 0;
    protected Map sessionConstraints;
    protected static HashMap userOrderServices;

    public UserOrderServiceHomeImpl() {
        super();
    }

    public void clientInitialize() throws Exception
   {
       if (Log.isDebugOn())
       {
           Log.debug(this, "SMA Type = " + this.getSmaType());
       }

       sessionConstraints = new HashMap(11);
       userOrderServices = new HashMap(11);
       String sessionProperty = getProperty(RATE_MONITOR_SESSIONS_PROPERTY_NAME);
       String startTimeProperty = getProperty(UserOrderServiceHomeImpl.START_TIME_PROPERTY_NAME);
       String callWindowIntervalProperty = getProperty(CALL_WINDOW_INTERVAL_PROPERTY_NAME);
       String callWindowSizeProperty = getProperty(CALL_WINDOW_SIZE_PROPERTY_NAME);
       if(sessionProperty.length() > 0)
       {
           StringTokenizer sessionTokenizer = new StringTokenizer(sessionProperty, TOKENIZER_DELIMITERS, false);
           StringTokenizer startTimeTokenizer =
                   new StringTokenizer(startTimeProperty, UserOrderServiceHomeImpl.TOKENIZER_DELIMITERS, false);
           StringTokenizer callWindowIntervalTokenizer =
                   new StringTokenizer(callWindowIntervalProperty, TOKENIZER_DELIMITERS, false);
           StringTokenizer callWindowSizeTokenizer =
                   new StringTokenizer(callWindowSizeProperty, TOKENIZER_DELIMITERS, false);

           String sessionToken;
           Long startTimeToken;
           Long callWindowIntervalToken;
           Integer callWindowSizeToken;
           Map settingsMap;
           GregorianCalendar startCal = new GregorianCalendar();

           while(sessionTokenizer.hasMoreTokens())
           {
               sessionToken = sessionTokenizer.nextToken();
               settingsMap = new HashMap(6);

               startTimeToken = UserOrderServiceHomeImpl.EMPTY_LONG_CONSTRAINT;
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
               settingsMap.put(UserOrderServiceHomeImpl.START_TIME_PROPERTY_NAME, startTimeToken);

               if(callWindowIntervalTokenizer.hasMoreTokens())
               {
                   callWindowIntervalToken = Long.parseLong(callWindowIntervalTokenizer.nextToken());
               }
               else
               {
                   callWindowIntervalToken = EMPTY_LONG_CONSTRAINT;
               }
               settingsMap.put(CALL_WINDOW_INTERVAL_PROPERTY_NAME, callWindowIntervalToken);

               if(callWindowSizeTokenizer.hasMoreTokens())
               {
                   callWindowSizeToken = Integer.parseInt(callWindowSizeTokenizer.nextToken());
               }
               else
               {
                   callWindowSizeToken = EMPTY_INTEGER_CONSTRAINT;
               }
               settingsMap.put(CALL_WINDOW_SIZE_PROPERTY_NAME, callWindowSizeToken);

               sessionConstraints.put(sessionToken, settingsMap);

           }
       }
       sessionConstraints = Collections.unmodifiableMap(sessionConstraints);
       try{
    	   registerCommand(this, "orderRateMonitor", "orderRateMonitorArCallback","Display User Order RateMonitor per Session",
               new String[] { String.class.getName(),String.class.getName()},
               new String[] { "userId","session Name"});
       }catch (Exception e)
       {
           Log.exception(this, "Cannot register ar command. Ignoring exception", e);
       }
    }


    /**
     * Creates a new user instance of a OrderEntry Service.
     */
    public synchronized UserOrderService create(BaseSessionManager sessionMgr)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Creating UserOrderServiceImpl for " + sessionMgr);
        }
        UserOrderServiceImpl userOrder = (UserOrderServiceImpl)userOrderServices.get(sessionMgr);
        if(userOrder == null)
        {
            userOrder = new com.cboe.application.order.common.UserOrderServiceImpl(sessionMgr, sessionConstraints);
            addToContainer(userOrder);
            userOrderServices.put(sessionMgr, userOrder);
        }
        return userOrder;
    }

    public synchronized UserOrderService find(BaseSessionManager sessionManager)
    {
        return create(sessionManager);
    }

    public synchronized void remove(BaseSessionManager sessionManager)
    {
        userOrderServices.remove(sessionManager);
    }

    public Map getSessionConstraints()
    {
    	return sessionConstraints;
    }
    
    public HashMap getUserOrderServices()
    {
    	return userOrderServices;
    }
public String orderRateMonitorArCallback(String thisUserId, String sessionName)

    {
    	StringBuilder s = new StringBuilder();
    	int windowSize;
        long windowMilliSecondPeriod;
        String thisExchange = "CBOE";
        String thisAcronym = thisUserId;
        RateMonitorKeyContainer key = null;

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

		 			key = new RateMonitorKeyContainer(
							thisUserId, thisExchange, thisAcronym, sessionName,
							 RateMonitorTypeConstants.ACCEPT_ORDER);

			 		try
				    {
				        PropertyGroupStruct propertyGroupStruct = ServicesHelper.getPropertyService().getProperties(PropertyCategoryTypes.RATE_LIMITS
				                                                                                     ,RateLimitsFactory.getRateMonitorKey(key.getUserId()
			                                                                                                 , key.getExchange()
			                                                                                                 , key.getAcronym()));

				        PropertyServicePropertyGroup propertyServiceGroup = PropertyFactory.createPropertyGroup(propertyGroupStruct);
				        RateLimits rateLimit = RateLimitsFactory.getRateLimitBySessionType(propertyServiceGroup, key.getSession(), key.getType());
				        windowSize = rateLimit.getWindowSize();
				        windowMilliSecondPeriod = rateLimit.getWindowInterval();
				    	s.append("Order rate: ").append(windowSize).append(" per ")
			        	 .append(windowMilliSecondPeriod).append(" Millis")
			        	 .append("\n");

				    }
				    catch(Exception e)
				    {
				        s.append("Unable to get Order Rate Limit for user:")
				         .append(thisUserId)
				         .append(" sessionName: ")
				         .append(sessionName);

				    }
        	 }else if (sessionName.equalsIgnoreCase(""))
        	 {
        		 // list all session rate limit.
        		 s.append("Usage: casadmin orderRateMonitor UserName SessionName")
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




}
