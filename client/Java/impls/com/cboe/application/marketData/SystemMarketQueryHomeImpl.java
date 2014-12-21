//
// -----------------------------------------------------------------------------------
// Source file: MarketQueryHomeImpl.java
//
// PACKAGE: com.cboe.application.marketData;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.application.marketData;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.domain.startup.ClientBOHome;
import com.cboe.exceptions.SystemException;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.floorApplication.LastSaleService;
import com.cboe.interfaces.application.MarketQueryHome;
import com.cboe.interfaces.application.MarketQueryV3;
import com.cboe.interfaces.application.SessionManager;
import com.cboe.interfaces.application.SystemMarketQuery;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.ExceptionBuilder;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.StringTokenizer;

public class SystemMarketQueryHomeImpl extends ClientBOHome implements MarketQueryHome
{
    public static final String LOCAL_FILTER_ONLY_SESSIONS       = "LocalFilterOnlySessions";
    public static final String REMOTE_FILTER_ONLY_SESSIONS      = "RemoteFilterOnlySessions";
    public static final String MARKET_DATA_CALLBACK_TIME_OUT    = "marketDataCallbackTimeout";

    // These lists contain the names of trading sessions for which market data
    // subscribtions must be handled either _only_ locally or _only_ remotely.
    // These lists are mutually exclusive -- there must be _NO_ overlap of
    // session names between these two lists.
    private List localFilterOnlySessionsList;
    private List remoteFilterOnlySessionsList;
    private int marketDataCallbackTimeout;
    private  Map <SessionManager, SystemMarketQuery> sessionMap; 
    /**
     * QuoteEntryHomeImpl constructor comment.
     */
    public SystemMarketQueryHomeImpl()
    {
        super();
    }

    public MarketQueryV3 createMarketQuery(SessionManager sessionManager) {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Creating MarketQuery for " + sessionManager);
        }
        return (MarketQueryV3) findImpl(sessionManager);
    }
    
    public LastSaleService createLargeTradeLastSale(SessionManager sessionManager) {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Creating LastSale for " + sessionManager);
        }
        return (LastSaleService) findImpl(sessionManager);
	}

    public void clientInitialize() throws Exception
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "SMA Type = " + this.getSmaType());
        }
        marketDataCallbackTimeout = Integer.parseInt(getProperty(MARKET_DATA_CALLBACK_TIME_OUT));
        initLocalOrRemoteList();
        String originator = FoundationFramework.getInstance().getConfigService().getProperty("Process.name()");
        ChannelKey channelKey = new ChannelKey(ChannelType.MDCAS_CALLBACK_REMOVAL, originator);
        ServicesHelper.getRemoteCASCallbackRemovalConsumerHome().addFilter(channelKey);
        sessionMap = Collections.synchronizedMap(new HashMap<SessionManager, SystemMarketQuery>());
    }

    private void initLocalOrRemoteList() throws Exception
    {
        localFilterOnlySessionsList= new LinkedList();
        remoteFilterOnlySessionsList= new LinkedList();

        // Populate the lists of trading session names whose market data must be
        // handled either only locally or only remotely.  Any items appearing in
        // one list CAN NOT appear in the other list.  Since these properties are
        // optional, by default these lists will be empty.
        populateList(localFilterOnlySessionsList, LOCAL_FILTER_ONLY_SESSIONS);
        populateList(remoteFilterOnlySessionsList, REMOTE_FILTER_ONLY_SESSIONS);

        String[] overlappingNames = checkForOverlap();

        if (overlappingNames.length > 0)
        {
            reportOverlap(overlappingNames);
        }
        else
        {
            // Initialize the market query impl's static data members.
            SystemMarketQueryImpl.initStaticData(localFilterOnlySessionsList,
                                                 remoteFilterOnlySessionsList);
        }
    }

    //--------------------------------------------------------------------------
    // private methods
    //--------------------------------------------------------------------------
    private void populateList(List list, String propertyName) throws Exception
    {
        String string = System.getProperty(propertyName);

        if ((string != null) && (string.length() > 0))
        {
            StringTokenizer t = new StringTokenizer(string, ",");

            while (t.hasMoreTokens())
            {
                String s = t.nextToken();
                list.add(s);
            }
        }
    }

    /**
     * This method checks the two lists for any overlap, which is defined as any
     * string that appears in both of the lists.
     *
     * @return Returns an array of String objects that contains all the strings
     * that are found in both of the given lists.  If there is no overlap, this
     * array will have a length of zero, but in no circumstances will this method
     * ever return null.
     */
    private String[] checkForOverlap()
    {
        String[] overlappingNames = com.cboe.client.util.CollectionHelper.EMPTY_String_ARRAY;
        List overlapList = new LinkedList();

        // If either list is empty, there can't possibly be any overlap...
        if (!localFilterOnlySessionsList.isEmpty() &&
            !remoteFilterOnlySessionsList.isEmpty())
        {
            Iterator iter = localFilterOnlySessionsList.iterator();

            while (iter.hasNext())
            {
                String localOnlySessionName = (String) iter.next();

                if (remoteFilterOnlySessionsList.contains(localOnlySessionName))
                {
                    // This string is contained in both lists!
                    overlapList.add(localOnlySessionName);
                }
            }
        }

        if (!overlapList.isEmpty())
        {
            overlappingNames = (String[]) overlapList.toArray(overlappingNames);
        }

        return overlappingNames;
    }

    /**
     *
     */
    private void reportOverlap(String[] overlappingNames) throws SystemException
    {
        int numOverlaps = overlappingNames.length;
        StringBuilder buffer = new StringBuilder(128 + 32*numOverlaps);

        buffer.append("System properties '");
        buffer.append(LOCAL_FILTER_ONLY_SESSIONS);
        buffer.append("' and '");
        buffer.append(REMOTE_FILTER_ONLY_SESSIONS);
        buffer.append("' contain ");
        buffer.append(numOverlaps);
        buffer.append(" overlapping trading session name");

        if (numOverlaps > 1)
            buffer.append("s");

        buffer.append(": ");
        buffer.append(overlappingNames[0]);

        for (int i = 1; i < numOverlaps; ++i) {
            buffer.append(", ");
            buffer.append(overlappingNames[i]);
        }

        throw ExceptionBuilder.systemException(buffer.toString(), 0);
    }
	
	private SystemMarketQuery findImpl(SessionManager sessionManager) {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Creating SystemMarketQueryImpl for " + sessionManager);
        }
        SystemMarketQuery boiFor2 = null;
        synchronized(sessionMap) {
        	boiFor2 = sessionMap.get(sessionManager);
        	// create if not exists
        	if(boiFor2 == null) {
        		SystemMarketQueryImpl bo = new SystemMarketQueryImpl(marketDataCallbackTimeout);
        		       		
        		bo.setSessionManager(sessionManager);
        		addToContainer(bo);
        		bo.create(String.valueOf(bo.hashCode()));
        		try {
					bo.initialize();
					SystemMarketQueryInterceptor boi = (SystemMarketQueryInterceptor) this.createInterceptor(bo);
			        boi.setSessionManager(sessionManager);
			        if(getInstrumentationEnablementProperty())
			        {
			               boi.startInstrumentation(getInstrumentationProperty());
			        }
			        boiFor2 = (SystemMarketQuery) boi;
			        sessionMap.put(sessionManager, boiFor2);
				} catch (Exception e) {
					Log.exception(this, e);
				}	
    	    }
        }	
        return boiFor2;
    }

	public void removeSession(SessionManager sessionManager) {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Removing sessionMap for " + sessionManager);
        }
        synchronized(sessionMap) {
        	sessionMap.remove(sessionManager);
		}
	}
}