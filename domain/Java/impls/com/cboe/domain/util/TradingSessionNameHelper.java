package com.cboe.domain.util;

import com.cboe.interfaces.businessServices.TradingSessionService;
import com.cboe.interfaces.businessServices.TradingSessionServiceHome;
import com.cboe.idl.cmiConstants.TradingSessionStates;
import com.cboe.idl.session.BusinessDayStruct;
import com.cboe.idl.session.TradingSessionElementStruct;
import com.cboe.idl.session.TradingSessionElementStructV2;
import com.cboe.idl.session.TradingSessionStruct;
import com.cboe.idl.session.TemplateClassStruct;
import com.cboe.idl.session.TradingSessionElementTemplateStruct;
import com.cboe.idl.cmiSession.SessionProductStruct;
import com.cboe.idl.cmiSession.SessionClassStruct;
import com.cboe.idl.cmiSession.SessionStrategyStruct;
import com.cboe.idl.cmiSession.TradingSessionStateStruct;
import com.cboe.idl.cmiSession.ClassStateStruct;
import com.cboe.idl.cmiSession.ProductStateStruct;
import com.cboe.idl.constants.TradingSessionDestinationCodes;
import com.cboe.idl.cmiUtil.TimeStruct;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;
import com.cboe.infrastructureServices.systemsManagementService.PropertyQuery;
import com.cboe.interfaces.domain.eventInstrumentation.EventInstrumentation;
import com.cboe.interfaces.domain.eventInstrumentation.InstrumentedConsumer;
import com.cboe.interfaces.domain.eventInstrumentation.InstrumentedConsumerHome;
import com.cboe.interfaces.events.TradingSessionConsumerHome;
import com.cboe.interfaces.events.TradingSessionConsumer;
import com.cboe.domain.eventInstrumentation.EventInstrumentationImpl;
import com.cboe.exceptions.*;
import com.cboe.util.ExceptionBuilder;
import com.cboe.util.ChannelKey;



import java.util.*;

/**
 * A utility class used to test trading session name to see if the
 * session is an internal or external session.
 */
public class TradingSessionNameHelper implements TradingSessionConsumer,InstrumentedConsumer {

    /**
     * TradingSessionConsumer object needed to listen for business day event,
     * and TradingSessionConsumerHome needed to register to receive event.
     */
     private static TradingSessionConsumer tradingSessionConsumer;
     private static TradingSessionConsumerHome tradingSessionConsumerHome;

    /**
     * Cached defined session structs from the TradingSessionService.
     */
    private static HashMap sessionList;
    private static TradingSessionStruct[] definedSessions;

    /**
     * Cached configured session names.
     */
    private static HashSet configuredSessions;

    /**
     * Cached mapping of classes and session.
     */
    private static HashMap classSessionMap;

    /**
     * True id the classSessionMap has been initialized.
     */
    private static boolean initialized = false;
    private EventInstrumentation[] instrumentation = new EventInstrumentation[1];
    /**
     * Creates trading session struct used for all session events.
     */
    public static TradingSessionStruct createAllSessionsStruct() {
        TradingSessionStruct allSessions = (TradingSessionStruct) ReflectiveStructBuilder.newStruct(TradingSessionStruct.class);
        allSessions.sessionName = TradingSessionService.ALL_SESSIONS;
        return allSessions;
    }

    /**
     * Returns configured sessions.
     */
    public static String[] getConfiguredSessionNames() {
        FoundationFramework ff = FoundationFramework.getInstance();
        ConfigurationService configService = ff.getConfigService();
        PropertyQuery pq = PropertyQuery.queryFor( "sessionNames" ).from( "Application" ).from( configService.getFullName( ff ) );
        return configService.getPropertyList(pq.queryString(), ",", "");
    }

    /**
     * Returns configured session names in a set.
     */
    private static synchronized HashSet getConfiguredSessionsSet() {
        if (configuredSessions == null) {
            configuredSessions = new HashSet();
            String[] sessionNames = getConfiguredSessionNames();
            for (int i = 0; i < sessionNames.length; i++) {
                configuredSessions.add(sessionNames[i]);
            }
        }
        return configuredSessions;
    }

    /**
     * Returns the open session configured for this process.  This assumes that only one configured
     * session can be open at a time.
     */
    public static String getOpenConfiguredSessionName() {
        TradingSessionStruct[] allSessions = getSessionListAsArray();
        for (int i = 0; i < allSessions.length; i++) 
        {
            if (allSessions[i].sessionState == TradingSessionStates.OPEN && isConfiguredSession(allSessions[i].sessionName))
            {
                return allSessions[i].sessionName;
            }
        }
        // no configured session is open
        return null;       
    }

    /**
     * Returns the not applicable session configured for stock, which should have only one.
     */
    public static String getNotApplicableSessionName()
    {
        TradingSessionStruct[] allSessions = getTradingSessions();
	    for (int i = 0; i < allSessions.length; i++)
        {
            if (isNotApplicableSession(allSessions[i].sessionName))
            {
                return allSessions[i].sessionName;
            }
        }
        // no not applicable session
        return null;
    }

    /**
     * Finds the session.
     */
    public static TradingSessionStruct getSession(String sessionName) {
        return (TradingSessionStruct) getSessionList().get(sessionName);
    }

    /**
     * Returns the defined sessions if present in cache else gets from TradingSessionService.
     */
    private static  HashMap getSessionList() {
    	if(sessionList == null)
	    {
            synchronized(TradingSessionNameHelper.class) {
                if (sessionList == null){
                    initializeSessionList();
                }
            }                
	    }
	    return sessionList;
    }
    
    private static TradingSessionStruct[] getSessionListAsArray() {
        if(definedSessions == null)
        {
            synchronized(TradingSessionNameHelper.class) {
                if (definedSessions == null){
                    initializeSessionList();
                }
            }                
        }
        return definedSessions;
    }    
    
    private static void initializeSessionList(){
        try {
            sessionList = new HashMap();
            String allNames = "";
            definedSessions = getTradingSessionService().getTradingSessions();
            for (int i = 0; i < definedSessions.length; i++) {
			    sessionList.put(definedSessions[i].sessionName, definedSessions[i]);
                allNames = allNames + ";" + definedSessions[i].sessionName;
            }
            Log.information("TradingSessionNameHelper >>> Sessions defined are "+ allNames);                
        } 
        catch (Exception e) {
		    Log.exception("TradingSessionNameHelper: Unable to load sessions, all sessions will be treated as external", e);
        }        
    }

    /**
     * Returns the mapping of classes and sessions.
     */
    private static synchronized HashMap getClassSessionMap() {
        if (classSessionMap == null) {
            buildClassSessionMap();
        }
        return classSessionMap;
    }

    /**
     * Gets sessions from TradingSessionService.
     */
    public static TradingSessionStruct[] getTradingSessions() {
		return getSessionListAsArray();
    }

    /**
     * Gets the trading session service
     */
    private static TradingSessionService getTradingSessionService()
        throws SystemException
    {
        try {
            TradingSessionServiceHome tssHome = (TradingSessionServiceHome) HomeFactory.getInstance().findHome(TradingSessionServiceHome.HOME_NAME);
            TradingSessionService tss = tssHome.find();
            return tss;
        }
        catch (Exception e) {
            Log.exception("Cannot get trading session service ", e);
            throw ExceptionBuilder.systemException("Cannot get trading session service ", 0);
        }
    }



    /**
     * Determines if given session name is configured for current process.
     *
     * @param sessionName name to be checked
     * @return true if session is configured
     */
    public static boolean isConfiguredSession(String sessionName) {
        HashSet theSessions = getConfiguredSessionsSet();
        // if no specific sessions are configured, assume all sessions are wanted
        return theSessions.isEmpty() || theSessions.contains(sessionName);
    }

    /**
     * Checks trading session name to see if it is for an external session.
     *
     * @param sessionName trading session name
     * @return true if name is for an external session
     */
    public static boolean isExternalSession(String sessionName) {
        TradingSessionStruct session = getSession(sessionName);
        return session != null && session.sessionDestinationCode == TradingSessionDestinationCodes.OPEN_OUTCRY;
    }

    /**
     * Checks trading session name to see if it is for an internal session.
     *
     * @param sessionName trading session name
     * @return true if name is for an internal session
     */
    public static boolean isInternalSession(String sessionName) {
        TradingSessionStruct session = getSession(sessionName);
        return session != null && session.sessionDestinationCode == TradingSessionDestinationCodes.SBT;
    }

    /**
     * Checks trading session name to see if it is for an underlying session.
     *
     * @param sessionName trading session name
     * @return true if name is for an underlying session
     */
    public static boolean isUnderlyingSession(String sessionName) {
        TradingSessionStruct session = getSession(sessionName);
        return session != null && session.sessionDestinationCode == TradingSessionDestinationCodes.UNDERLYING;
    }

    /**
     * Tests session name to see if it is for all sessions.
     */
    public static boolean isForAllSessions(String sessionName) {
        return sessionName.equals(TradingSessionService.ALL_SESSIONS);
    }

    /**
     * Checks trading session name to see if it is not applicable session for stock.
     */
    public static boolean isNotApplicableSession(String sessionName) {
        TradingSessionStruct session = getSession(sessionName);
        return session != null && session.sessionDestinationCode == TradingSessionDestinationCodes.NOT_APPLICABLE;
    }

    /**
     * Build the cache which holds the relationship between class and its sessions.
     * This cache is intended to improve the efficiency when this class is used in a
     * environment remote to the actual TradingSessionService
     *
     */
     private static void buildClassSessionMap(){
        Log.information("TradingSessionNameHelper: Building class session CACHE MAP.");
        classSessionMap = new HashMap();
        TradingSessionStruct[] sessions = getTradingSessions();
        Log.information("TradingSessionNameHelper: Building class session map for (" + sessions.length + ") sessions.");
        for ( int i = 0; i < sessions.length; i++ ){
            Log.information("TradingSessionNameHelper: Building class session map for Session (" + sessions[i].sessionName + ").");
            fillClassSessionMapForSession( sessions[i].sessionName);
        }
        Log.information("TradingSessionNameHelper: Completed building class session CACHE MAP.");
     }

     private static void fillClassSessionMapForSession(String sessionName) {
        try {
            long nbrClasses = 0;
            TradingSessionService tss = getTradingSessionService();
            TradingSessionElementTemplateStruct[] elementTemplates = tss.getTemplatesForSession(sessionName);
            for ( int i = 0; i < elementTemplates.length; i++ ) {
                TemplateClassStruct[] templateClasses = elementTemplates[i].templateClasses;
                nbrClasses += elementTemplates[i].templateClasses.length;
                for ( int j = 0; j < templateClasses.length; j++ ){
                    Integer key = new Integer( templateClasses[j].classStruct.classKey );
                    if (classSessionMap.get(key) == null) {
                        classSessionMap.put(key, ((TradingSessionNameHelper)tradingSessionConsumer).new SessionClassInfo());
                    }
                    OpenTimeRange openTimeRange = ((TradingSessionNameHelper)tradingSessionConsumer).new OpenTimeRange();
                    openTimeRange.productCloseTime = toMillisSinceMidnight(elementTemplates[i].productCloseTime);
                    openTimeRange.productPreOpenTime = toMillisSinceMidnight(elementTemplates[i].productPreOpenTime);
                    SessionClassInfo sessionClassInfo = ((SessionClassInfo) classSessionMap.get(key));
                    sessionClassInfo.namelist.add(sessionName);
                    sessionClassInfo.openTimeRange.add(openTimeRange);
                }
            }
            Log.information("TradingSessionNameHelper: Loaded (" + nbrClasses + ") classes for session (" + sessionName + ").");
        }
        catch (Exception e) {
            Log.exception("TradingSessionNameHelper: Cannot build class session map for session : " + sessionName, e);
        }
     }

    public static long toMillisSinceMidnight(TimeStruct time)
    {
        long millis = 0;

        millis += time.second*1000;
        millis += time.minute*60000;
        millis += time.hour*3600000;

        return millis;
    }

    /**
     *
     * @param millisSinceEpoch
     * @return millis since midnight the same day
     */
    public static long toMillisSinceMidnight(long millisSinceEpoch){


        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(millisSinceEpoch));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return millisSinceEpoch - cal.getTime().getTime();


    }

    private class OpenTimeRange{

        long productPreOpenTime;
        long productCloseTime;
    }
    private class SessionClassInfo{
        SessionClassInfo(){
            namelist = new ArrayList();
            openTimeRange = new ArrayList();
        }
        ArrayList namelist;
        ArrayList openTimeRange;
    }

     /**
      * Return the Session names in which a product class participates.
      * This method is intended to provide an efficient way to get the
      * sessions a particular product class is traded in when the requester
      * is remote to the real TradingSessionService, because without caching
      * such information by this class, the requester has to make remote method
      * invocation to TradingSessionService each time when it needs such information.
      */
    public static String[] getSessionNamesForClass(int classKey){
        SessionClassInfo sessionClass =  (SessionClassInfo) (getClassSessionMap().get( new Integer(classKey)));
        if(sessionClass == null){
            return new String[0];
        }
        ArrayList nameList = sessionClass.namelist;
        if ( nameList == null) {
            return new String[0];
        }
        String[] sessionNames = new String[nameList.size()];
        nameList.toArray(sessionNames);
        return sessionNames;
    }
    /**
     * Return the session name where the product class is currently trading.
     * @param classKey
     * @return sessionName
     */
    public static String getCurrentSessionNameForClass(int classKey){

        SessionClassInfo sessionClassInfo = (SessionClassInfo) (getClassSessionMap().get( new Integer(classKey)));
        if(sessionClassInfo == null) {
            if(Log.isDebugOn()) Log.debug("There is no sessionClassInfo for classKey=" + classKey);
            return null;
        }
        String sessionName = null;
        long currTime = System.currentTimeMillis();
        currTime = toMillisSinceMidnight(currTime);
        if(Log.isDebugOn()) Log.debug("Current time is: " + currTime);
        for(int i = 0; i < sessionClassInfo.namelist.size(); i++){
           if(Log.isDebugOn()) Log.debug("Session: " +  (String)sessionClassInfo.namelist.get(i) + " PreopenTime: " +
                   ((OpenTimeRange)sessionClassInfo.openTimeRange.get(i)).productPreOpenTime +
              " CloseTime: " + ((OpenTimeRange)sessionClassInfo.openTimeRange.get(i)).productCloseTime);
           if(currTime >((OpenTimeRange)sessionClassInfo.openTimeRange.get(i)).productPreOpenTime
              && currTime < ((OpenTimeRange)sessionClassInfo.openTimeRange.get(i)).productCloseTime){
               sessionName = (String)sessionClassInfo.namelist.get(i);
               break;
           }
        }
        if(Log.isDebugOn()) Log.debug("Returning session name: " + sessionName);
        return sessionName;
    }

    /**
     * Initialize this class, especially build the cache of class and sessions relationship.
     * This method is intended to provide an efficient way to get the sessions
     * a particular product class is traded in by caching all such information in
     * advance. This method should only be called if this class is running remotely
     * from the real TradingSessionService
     */
     public synchronized static void initialize(){
       if (!initialized) {
            TradingSessionNameHelper tsConsumer =
                new TradingSessionNameHelper();
            tradingSessionConsumer = tsConsumer;
            buildClassSessionMap();

            tsConsumer.connectToConsumer();



            initialized = true;
      }
     }

    private static TradingSessionConsumerHome getTradingSessionConsumerHome()
    {
        if(tradingSessionConsumerHome == null)
        {
            BOHome result = null;
            try
            {
                result = HomeFactory.getInstance().findHome(TradingSessionConsumerHome.HOME_NAME);
            }
            catch(Exception e)
            {
                throw new UnsupportedOperationException(
                    "No home configured with name = " + TradingSessionConsumerHome.HOME_NAME);
            }

            if(result == null)
            {
                throw new UnsupportedOperationException(
                    "TradingSessionNameHelper: " +
                    "TradingSessionConsumerHome not properly configured (" +
                    TradingSessionConsumerHome.HOME_NAME + ")");
            }
            tradingSessionConsumerHome = (TradingSessionConsumerHome) result;
        }
        return tradingSessionConsumerHome;
    }

    public synchronized static void resetCache()
    {
        // Make sessionList null to force it to go to TradingSessionService
        // // to get the trading sessions when requested for a refresh...
        sessionList = null;
        buildClassSessionMap();
    }

    ///// Implementation of TradingSessionConsumer interface

     private TradingSessionNameHelper()
     {
         instrumentation[0] = new EventInstrumentationImpl("TradingSessionNameHelper");
     }

     private void connectToConsumer()
     {
         // sign me up to listen for events
        try
        {

            // for bug fix 6897 removed the next two lines and replaced with EventService calls
            ChannelKey key = null;
            //ChannelKey key = new ChannelKey(ChannelType.ALL_TRADING_SESSION,new Integer(0));
            //getTradingSessionConsumerHome().addConsumer(this, key);

            // For bug fix cboqa06897 - the following two lines removed to remove dependence of domain package on server package
            // Used the same addConsumer interface method with key being passed as null
            //TradingSessionNameHelperConsumerImpl tsnConsumer = new TradingSessionNameHelperConsumerImpl(this);
            //tsnConsumer.connectToEventChannel();
            TradingSessionConsumerHome home = getTradingSessionConsumerHome();
            home.addConsumer(this,key);
            if (home instanceof InstrumentedConsumerHome)
            {
            	((InstrumentedConsumerHome)home).registerInstrumentedConsumer(this);
            }
            

        }
        catch(Exception e)
        {
            Log.alarm("TradingSessionNameHelper: " +
                "Unable to connect to ALL_TRADING_SESSION event channel.");
        }
    }

    // When business day event received, refresh cache.
    public void acceptBusinessDayEvent(BusinessDayStruct businessDayStruct)
    {
    	instrumentation[0].increment();
        synchronized(TradingSessionNameHelper.class)
        {
            Log.information("TradingSessionNameHelper received business day event.  Resetting cache.");
            resetCache();
            Log.information("TradingSessionNameHelper cache reset complete.");
        }
    }

    public void acceptTradingSessionState(TradingSessionStateStruct tradingSessionStateStruct)
    {
    	instrumentation[0].increment();
        // 02/27/03
        // Accept the Trading Session State from Event channel
        // and update the local cache (sessionList) with the session state for
        // that particular session. If session not found, add the session to the List...
        updateTradingSessionState(tradingSessionStateStruct.sessionName,
                                  tradingSessionStateStruct.sessionState);
    }



    public void updateProduct(SessionProductStruct sessionProductStruct)
    { // don't care
    	instrumentation[0].increment();
    }

    public void updateProductClass(SessionClassStruct sessionClassStruct)
    { // don't care
    	instrumentation[0].increment();
    }

    public void updateProductStrategy(SessionStrategyStruct sessionStrategyStruct)
    { // don't care
    	instrumentation[0].increment();
    }

    public void setClassState(ClassStateStruct classStateStruct)
    { // don't care
    	instrumentation[0].increment();
    }

    public void setProductStates(int classKey, String sessionName,
        ProductStateStruct[] productStateStructs)
    { // don't care
    	instrumentation[0].increment();
    }

    /**
     * Called in OrderBookService when it receives AMI call from global server
     * for start session. This will elimate the siutation that the cach is update
     * after the AMI call.
     */
    public static void onTradingSessionStarted(String sessionName)
    {
        updateTradingSessionState(sessionName, TradingSessionStates.OPEN);
    }

    /**
     * A private method to update session cache.
     */
    private static void updateTradingSessionState(String sessionName, short sessionState)
    {
        if (!isConfiguredSession(sessionName)){
            Log.information("TradingSessionNameHelper >> Session state event ignored for not configured session: " + sessionName);
            return;
        }
        synchronized (TradingSessionNameHelper.class) {
            TradingSessionStruct tradingSession = (TradingSessionStruct)getSession(sessionName);
            if (tradingSession != null)
            {
                tradingSession.sessionState = sessionState;
                Log.information("TradingSessionNameHelper >>> Session changes state. " + sessionName + ":"+ sessionState);
            }
        }
    }

	public void acceptTradingSessionElementUpdate(
			TradingSessionElementStruct sessionElement) {
		   Log.information("AcceptTradingSessionElementUpdate:"+sessionElement.sessionName+", "+sessionElement.elementName);
		
	}
    

	public String getConsumerName() {
		return "TradingSessionNameHelper";
	}

	public EventInstrumentation[] getEventInstrumentation() {
		return 	instrumentation;
	}

	public void acceptTradingSessionElementUpdateV2(TradingSessionElementStructV2 sessionElement) 
	{
		Log.information("AcceptTradingSessionElementUpdateV2:"+sessionElement.tradingSessionElementStruct.sessionName+", "+sessionElement.tradingSessionElementStruct.elementName);
	}

}
