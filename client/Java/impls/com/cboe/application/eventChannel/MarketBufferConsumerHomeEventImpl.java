// Copyright 2009 CBOE. All Rights Reserved.

package com.cboe.application.eventChannel;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.consumers.eventChannel.EventChannelFilterHelper;
import com.cboe.domain.startup.ClientBOHome;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.externalIntegrationServices.msgCodec.DataBufferBlock;
import com.cboe.idl.product.GroupStruct;
import com.cboe.idl.product.GroupTypeStruct;
import com.cboe.infrastructureServices.eventService.EventService;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.FatalFoundationFrameworkException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.events.MarketBufferConsumer;
import com.cboe.interfaces.events.IECMarketBufferConsumerHome;
import com.cboe.interfaces.internalBusinessServices.ClientProductConfigurationService;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.ExceptionBuilder;
import java.text.DecimalFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.regex.Pattern;

/** The Current Market Listener class.
 */

public class MarketBufferConsumerHomeEventImpl extends ClientBOHome implements IECMarketBufferConsumerHome
{
    private EventChannelFilterHelper eventChannelFilterHelper;
    private EventService eventService;
    private int numChannels;
    private boolean preferCurrentMarket;
    private boolean useMarketBufferAndCurrentMarket;
    private MarketBufferConsumerIECImpl[] marketBufferConsumerIECImpl;
    private MarketBufferEventConsumerInterceptor[] marketBufferEventConsumerInterceptor;
    private MarketBufferEventConsumerImpl[] marketBufferEvent;
    private Map<TwoKeyContainer, Integer> filters;
    private GregorianCalendar loggingEndTime;
    private Timer logTimer;
    private Pattern hmsPattern;
    private Pattern colonPattern;
    private ClientProductConfigurationService pcs;
    private int serversGroupTypeKey;
    private int subscriptionSpacing;
    private final static String CHANNEL_NAME = "MarketBuffer";     // incomplete, append a number
    private final static String NUM_CHANNELS = "numChannels";
    private final static String MARKET_DATA_SOURCE = "MarketData";
    private final static String CONFIGURATION_GROUPS = "ConfigurationGroups";
    private static final String GROUP_PROPERTY = "ValidGroupType";
    private static final String SUBSCRIPTION_SPACING= "MarketBuffer.SubscriptionSpacing";


    public MarketBufferConsumerHomeEventImpl()
    {
        setSmaType("GlobalMarketBufferConsumerHome.MarketBufferConsumerHomeImpl");
    }

    protected class TwoKeyContainer implements Comparable<TwoKeyContainer>
    {
        private int serverGroupKey;
        private int mdcassetGroupKey;
        public TwoKeyContainer(int serverGK, int mdcassetGK)
        {
            serverGroupKey = serverGK;
            mdcassetGroupKey = mdcassetGK;
        }

        /** Initialize object from a parsed string
         * @param s serverKey: or serverKey:mdcassetKey or :mdcassetKey
         */
        public TwoKeyContainer(String s)
        {
            String[] parts = s.split(":");
            if (parts.length < 1)
            {
                serverGroupKey = mdcassetGroupKey = 0;
            }
            else
            {
                serverGroupKey = Integer.parseInt(parts[0]);
                mdcassetGroupKey = (parts.length < 2) ? 0 : Integer.parseInt(parts[1]);
            }
        }
        public int getServerGroupKey()
        {
            return serverGroupKey;
        }
        public int getMdcassetGroupKey()
        {
            return mdcassetGroupKey;
        }
        public String toString()
        {
            if (serverGroupKey == 0)
            {
                return (mdcassetGroupKey == 0) ? "0:0" : ":" + mdcassetGroupKey;
            }
            return (mdcassetGroupKey == 0) ? serverGroupKey + ":" : serverGroupKey + ":" + mdcassetGroupKey;
        }

        /** Determine whether two objects have the same content.
         * @param obj Object to compare against this object.
         * @return true if the same, false if different.
         */
        public boolean equals(Object obj)
        {
            if (obj != null && obj instanceof TwoKeyContainer)
            {
                TwoKeyContainer other = (TwoKeyContainer) obj;
                return (serverGroupKey == other.serverGroupKey)
                    && (mdcassetGroupKey == other.mdcassetGroupKey);
            }
            return false;
        }

        /** Produce a hash code such that if a.equals(b),
         *  then a.hashCode() == b.hashCode().
         * @return Number based on content of this object.
         */
        public int hashCode()
        {
            return serverGroupKey ^ mdcassetGroupKey;
        }

        /** Establish an ordering between two objects.
         * @param other The object to compare against this one.
         * @return negative if this object is less/before other object,
         *    0 if objects are equal, positive if this object is greater/after other.
         */
        public int compareTo(TwoKeyContainer other)
        {
            if (serverGroupKey != other.serverGroupKey)
            {
                return serverGroupKey < other.serverGroupKey ? -1 : 1;
            }
            if (mdcassetGroupKey != other.mdcassetGroupKey)
            {
                return mdcassetGroupKey < other.mdcassetGroupKey ? -1 : 1;
            }
            return 0;
        }
    } // class TwoKeyContainer

    /**
     * Hold group key and channel name for use in ChannelKey. We don't use the
     * channel number but it distinguishes otherwise-identifcal subscription
     * keys. EventChannelFilterHelper discards addEventFilter calls if the
     * ChannelKey matches an existing filter, regardless of what channel
     * that filter is applied to.
     */
    protected class GroupChannelContainer
    {
        private int groupKey;
        private int channelNumber;

        public GroupChannelContainer(int key, int num)
        {
            groupKey = key;
            channelNumber = num;
        }

        public int getGroupKey()
        {
            return groupKey;
        }

        public int getChannelNumber()
        {
            return channelNumber;
        }

        /** Determine whether two objects have the same content.
         * @param obj Object to compare against this object.
         * @return true if the same, false if different.
         */
        public boolean equals(Object obj)
        {
            if (obj != null && obj instanceof GroupChannelContainer)
            {
                GroupChannelContainer other = (GroupChannelContainer) obj;
                return (groupKey == other.groupKey)
                    && (channelNumber == other.channelNumber);
            }
            return false;
        }

        /** Produce a hash code such that if a.equals(b),
         *  then a.hashCode() == b.hashCode().
         * @return Number based on content of this object.
         */
        public int hashCode()
        {
            return channelNumber ^ groupKey;
        }

        /** Establish an ordering between two objects.
         * @param other The object to compare against this one.
         * @return negative if this object is less/before other object,
         *    0 if objects are equal, positive if this object is greater/after other.
         */
        public int compareTo(GroupChannelContainer other)
        {
            if (groupKey != other.groupKey)
            {
                return groupKey < other.groupKey ? -1 : 1;
            }
            if (channelNumber != other.channelNumber)
            {
                return channelNumber < other.channelNumber ? -1 : 1;
            }
            return 0;
        }
    } // class GroupChannelContainer

    /** Produce a constraint parameter string.
     * @param channelKey the event channel key.
     * @return String based on channelKey.
     */
    private String getParmName(ChannelKey channelKey)
    {
        GroupChannelContainer key = (GroupChannelContainer) channelKey.key;
        switch (channelKey.channelType)
        {
            case ChannelType.MARKET_BUFFER_CM_BY_SERVER:
                return "acceptMarketBuffer.groupKey == " + key.getGroupKey();
            case ChannelType.MARKET_BUFFER_CM_BY_MDCASSET:
                return "acceptMarketBuffer.subIdentifier == " + key.getGroupKey();
            default:
                Log.alarm(this, "Invalid Channel Type for filtering for context: " + channelKey.channelType);
                return EventChannelFilterHelper.NO_EVENTS_CONSTRAINT;
        }
    }

    /** Produce a constraint parameter string.
     * @param channelKey the event channel key.
     * @return String based on channelKey, or null if channelKey is null.
     */
    private String getConstraintString(ChannelKey channelKey)
    {
        if (channelKey == null)
        {
            return null;
        }

        String parm = getParmName(channelKey);

        if (parm.equals(EventChannelFilterHelper.ALL_EVENTS_CONSTRAINT)
        ||  parm.equals(EventChannelFilterHelper.NO_EVENTS_CONSTRAINT))
        {
            return parm;
        }

        StringBuilder buf = new StringBuilder("$.");
        buf.append(parm);
        return buf.toString();
    }

    /** Make a ChannelKey from the server portion of a key pair.
     * @param keys PCS group keys, server (or 0) and MDCASSET (or 0).
     * @param channelNumber Channel that key will be used for.
     * @return key that is distinctive in this context, or null if Server key is 0.
     */
    private ChannelKey makeServerKey(TwoKeyContainer keys, int channelNumber)
    {
        int groupKey = keys.getServerGroupKey();
        if (groupKey != 0)
        {
            GroupChannelContainer value = new GroupChannelContainer(groupKey, channelNumber);
            return new ChannelKey(ChannelType.MARKET_BUFFER_CM_BY_SERVER, value);
        }
        return null;
    }

    /** Make a ChannelKey from the MDCASSET portion of a key pair.
     * @param keys PCS group keys, server (or 0) and MDCASSET (or 0).
     * @param channelNumber Channel that key will be used for.
     * @return key that is distinctive in this context, or null if MDCASSET key is 0.
     */
    private ChannelKey makeMdcassetKey(TwoKeyContainer keys, int channelNumber)
    {
        int groupKey = keys.getMdcassetGroupKey();
        if (groupKey != 0)
        {
            GroupChannelContainer value = new GroupChannelContainer(groupKey, channelNumber);
            return new ChannelKey(ChannelType.MARKET_BUFFER_CM_BY_MDCASSET, value);
        }
        return null;
    }

    /**
     * Adds constraint based on the desired PCS groupKeys
     * @param keys the PCS groupKeys. We expect each key to have one 0 value
     *    and one non-zero value.
     */
    private void addConstraint(TwoKeyContainer keys)
        throws SystemException
    {
        for (int index = 0; index < numChannels; ++index)
        {
            if (find(index) != null)
            {
                try
                {
                    // Delay before the filter call
                    Thread.sleep(subscriptionSpacing);
                }
                catch (InterruptedException ie)
                { /* ignore */ }

                String channelName = CHANNEL_NAME + (index+1);
                ChannelKey serverKey = makeServerKey(keys, index);
                if (serverKey != null)
                {
                    String serverConstraint = getConstraintString(serverKey);
                    eventChannelFilterHelper.addEventFilter(marketBufferEvent[index], serverKey,
                            eventChannelFilterHelper.getChannelName(channelName), serverConstraint);
                }
                ChannelKey mdcassetKey = makeMdcassetKey(keys, index);
                if (mdcassetKey != null)
                {
                    String mdcassetConstraint = getConstraintString(mdcassetKey);
                    eventChannelFilterHelper.addEventFilter(marketBufferEvent[index], mdcassetKey,
                            eventChannelFilterHelper.getChannelName(channelName), mdcassetConstraint);
                }
            }
        }
    }

    private void removeConstraint(TwoKeyContainer keys)
        throws SystemException
    {
        for (int index = 0; index < numChannels; ++index)
        {
            if (find(index) != null)
            {
                try
                {
                    // Delay before the filter call
                    Thread.sleep(subscriptionSpacing);
                }
                catch (InterruptedException ie)
                { /* ignore */ }

                ChannelKey serverKey = makeServerKey(keys, index);
                if (serverKey != null)
                {
                    String serverConstraint = getConstraintString(serverKey);
                    eventChannelFilterHelper.removeEventFilter(serverKey, serverConstraint);
                }
                ChannelKey mdcassetKey = makeMdcassetKey(keys, index);
                if (mdcassetKey != null)
                {
                    String mdcassetConstraint = getConstraintString(mdcassetKey);
                    eventChannelFilterHelper.removeEventFilter(mdcassetKey, mdcassetConstraint);
                }
            }
        }
    }

    /**
     * Add filters for MarketBuffer messages in specified MDCASSETs.
     * @param groupNames One or more MDCASSET names.
     */
    private void setupMdcassetFilters(String[] groupNames)
        throws AuthorizationException, CommunicationException, DataValidationException, NotFoundException, SystemException
    {
        for (String groupName : groupNames)
        {
            int mdcassetGroupKey = pcs.getGroupKey(groupName);
            addGroupKeyFilter(0, mdcassetGroupKey);
        }
    }

    /** Groups representing servers are all under one GroupType. Get
     * the number which is the key of that GroupType.
     * Developed from ProductRoutingServiceClientImpl.getValidGroupType().
     * @return groupTypeKey of the GroupType representing servers.
     */
    private int getServersGroupType()
    {
        String serversGroupTypeName = System.getProperty(GROUP_PROPERTY);
        GroupTypeStruct[] groupTypes;
        try
        {
            groupTypes = pcs.getGroupTypes();
        }
        catch (Exception e)
        {
            throw new FatalFoundationFrameworkException(e, "Failed to get PCS GroupTypes");
        }

        for (GroupTypeStruct groupType : groupTypes)
        {
            if (groupType.groupTypeDescription.equals(serversGroupTypeName))
            {
                return groupType.groupType;
            }
        }

        throw new FatalFoundationFrameworkException("Failed to get groupTypeKey for " + serversGroupTypeName);
    }

    private int getServerGroupKey(int classKey)
    {
        GroupStruct[] groups;
        try
        {
            groups = pcs.getGroupsForProductClass(classKey);
        }
        catch (Exception e)
        {
            Log.exception(this, "Cannot find server for classKey:" + classKey, e);
            return 0;
        }

        for (GroupStruct group : groups)
        {
            if (group.groupType.groupType == serversGroupTypeKey)
            {
                // This group belongs to the servers groupType
                return group.groupKey;
            }
        }

        Log.exception(this, ExceptionBuilder.notFoundException("No server found for classKey:" + classKey, 0));
        return 0;  // nothing found
    }

    //////////
    // Functions from IECMarketBufferConsumerHome
    //////////

    /**
     * Adds a Filter to the CBOE event channel. Constraints based on the
     * ChannelKey will be added as well.
     * @param serverGroupKey PCS groupKey representing the server (or 0).
     * @param mdcassetGroupKey PCS groupKey representing the MDCASSET (or 0).
     */
    public void addGroupKeyFilter(int serverGroupKey, int mdcassetGroupKey)
        throws SystemException
    {
        TwoKeyContainer keys = new TwoKeyContainer(serverGroupKey, mdcassetGroupKey);
        synchronized (filters)
        {
            if (!filters.containsKey(keys))
            {
                filters.put(keys, 1);
                if (!preferCurrentMarket)
                {
                    // If MarketBuffer is turned on (instead of CurrentMarket turned on)
                    // then add our filter now.
                    addConstraint(keys);
                }
            }
            else
            {
                // Filter is already applied, keep track of this additional request
                filters.put(keys, 1 + filters.get(keys));
            }
        }
    }

    public void addClassKeyFilter(int classKey)
        throws SystemException
    {
        int serverGroupKey = getServerGroupKey(classKey);
        addGroupKeyFilter(serverGroupKey, 0);
    }

    /**
     * Removes the event channel Filter from the CBOE event channel.
     * @param serverGroupKey PCS groupKey representing the server (or 0).
     * @param mdcassetGroupKey PCS groupKey representing the MDCASSET (or 0).
     */
    public void removeGroupKeyFilter(int serverGroupKey, int mdcassetGroupKey)
        throws SystemException
    {
        TwoKeyContainer keys = new TwoKeyContainer(serverGroupKey, mdcassetGroupKey);
        synchronized (filters)
        {
            if (filters.containsKey(keys))
            {
                int subscribers = filters.get(keys);
                if (subscribers <= 1)
                {
                    filters.remove(keys);
                    if (!preferCurrentMarket)
                    {
                        // If MarketBuffer is turned on (instead of CurrentMarket turned on)
                        // then remove our filter now.
                        removeConstraint(keys);
                    }
                }
                else
                {
                    // Decrement subscriber count, leave the filter in place 
                    filters.put(keys, subscribers-1);
                }
            }
        }
    }

    public void removeClassKeyFilter(int classKey)
        throws SystemException
    {
        int serverGroupKey = getServerGroupKey(classKey);
        removeGroupKeyFilter(serverGroupKey, 0);
    }

    //////////
    // Functions from interface MarketBufferConsumerHome
    //////////

    /**
     * Return the specified MarketBuffer listener.
     * @param index Which listener to return, value 0 .. numChannels-1
     * @return Specified listener.
     */
    public MarketBufferConsumer find(int index)
    {
        return marketBufferEventConsumerInterceptor[index];
    }

    public void addUnfilteredConsumer(MarketBufferConsumer consumer) { }
    public void addUnfilteredConsumer(int index, MarketBufferConsumer consumer) { }

    //////////
    // Functions from BufferConsumerHome
    //////////

    public int getNumChannels()
    {
        return numChannels;
    }

    public int getChannelIndexForHash(int classKey)
    {
        return classKey % numChannels;
    }

    public void publishDataToChannel(DataBufferBlock p_block, int p_subIdentifier, int p_channelIdx) { }

    //////////
    // Functions from class ClientBOHome
    //////////

    public void clientInitialize() throws Exception
    {
        super.clientInitialize();
        filters = new HashMap<TwoKeyContainer, Integer>();
        loggingEndTime = new GregorianCalendar();
        hmsPattern = Pattern.compile("^(\\d+s|\\d+m\\d+s|\\d+m|\\d+h\\d+m\\d+s|\\d+h\\d+m|\\d+h\\d+s|\\d+h)$");
        colonPattern = Pattern.compile("^(\\d+|\\d+:\\d+|\\d+:\\d+:\\d+)$");

        pcs = ServicesHelper.getProductConfigurationService();
        serversGroupTypeKey = getServersGroupType();

        // Bug in 8.3 DN causes it to deadlock when it gets the same filter request applied to different
        // channels almost simultaneously. Command line can specify a delay (in milliseconds) to insert
        // between the near-simultaneous calls (see addConstraint and removeConstraint).
        String s = System.getProperty(SUBSCRIPTION_SPACING);
        subscriptionSpacing = (s == null) ? 0 : Integer.parseInt(s);

        numChannels = Integer.parseInt(getProperty(NUM_CHANNELS));
        String cmSource = System.getProperty(MARKET_DATA_SOURCE);
        if (cmSource.equals("CurrentMarket"))
        {
            preferCurrentMarket = true;
            useMarketBufferAndCurrentMarket = false;
            ServicesHelper.getCurrentMarketConsumerHome().activateSubscription(preferCurrentMarket);
        }
        else if (cmSource.equals("MarketBuffer"))
        {
            preferCurrentMarket = false;
            useMarketBufferAndCurrentMarket = false;
            ServicesHelper.getCurrentMarketConsumerHome().activateSubscription(preferCurrentMarket);
        }
        else if (cmSource.equals("CurrentMarket,MarketBuffer")
             ||  cmSource.equals("MarketBuffer,CurrentMarket"))
        {
            preferCurrentMarket = false;
            useMarketBufferAndCurrentMarket = true;
            ServicesHelper.getCurrentMarketConsumerHome().activateSubscription(true);
        }
        else
        {
            throw ExceptionBuilder.dataValidationException("Invalid value '" + cmSource + "' for " + MARKET_DATA_SOURCE, 0);
        }

        eventChannelFilterHelper = new EventChannelFilterHelper();
        marketBufferEventConsumerInterceptor = new MarketBufferEventConsumerInterceptor[numChannels];
        marketBufferConsumerIECImpl = new MarketBufferConsumerIECImpl[numChannels];
        marketBufferEvent = new MarketBufferEventConsumerImpl[numChannels];

        for (int channelIndex = 0; channelIndex < numChannels; ++channelIndex)
        {
            marketBufferConsumerIECImpl[channelIndex] = new MarketBufferConsumerIECImpl();
            marketBufferConsumerIECImpl[channelIndex].create(String.valueOf(marketBufferConsumerIECImpl[channelIndex].hashCode()));
            addToContainer(marketBufferConsumerIECImpl[channelIndex]);   // Every BObject must be added to a container.
            marketBufferEventConsumerInterceptor[channelIndex] = new MarketBufferEventConsumerInterceptor(marketBufferConsumerIECImpl[channelIndex]);
            if (getInstrumentationEnablementProperty())
            {
                marketBufferEventConsumerInterceptor[channelIndex].startInstrumentation(getInstrumentationProperty(), channelIndex+1);
            }
            marketBufferEvent[channelIndex] = new MarketBufferEventConsumerImpl(marketBufferEventConsumerInterceptor[channelIndex]);
        }

        try
        {
            StringBuilder commandList = new StringBuilder();
            if (! useMarketBufferAndCurrentMarket)
            {
                // Only using MarketBuffer or CurrentMarket (not both at once), we can switch between them.
                commandList.append("OFF | ON | ");
            }
            commandList.append("LOG | SUBSCRIPTIONS");
            registerCommand(this, "marketBuffer", "adminMarketBuffer", "Handling of MarketBuffer channels",
                new String[] {"java.lang.String", "java.lang.String", "java.lang.String"},
                new String[] {commandList.toString(),
                              "(LOG) duration | (LOG) OFF | (LOG) SHOW | (SUBSCRIPTIONS) SHOW | (SUBSCRIPTIONS) ADD | (SUBSCRIPTIONS) REMOVE",
                              "(SUBSCRIPTIONS ADD) subscriptionKey | (SUBSCRIPTIONS REMOVE) subscriptionKey"});
        }
        catch (Exception ex)
        {
            Log.exception(this, "Failed to register callback", ex);
        }
    }

    public void clientStart()
        throws Exception
    {
        super.clientStart();
        if (eventService == null)
        {
            eventService = eventChannelFilterHelper.connectEventService();
        }

        String interfaceRepId = com.cboe.idl.events.MarketBufferEventConsumerHelper.id();
        for (int channelIndex = 0; channelIndex < numChannels; ++channelIndex)
        {
            // connect to the event channel now, call addConstraint later to add filters.
            String channelName = CHANNEL_NAME + (channelIndex+1);
            eventChannelFilterHelper.connectConsumer(channelName, interfaceRepId, marketBufferEvent[channelIndex]);
        }

        if (useMarketBufferAndCurrentMarket)
        {
            // This is a CFIX or MDCAS engine; get PCS groups of interest and
            // apply filters for those groups.
            String[] mdcassetGroupNames = System.getProperty(CONFIGURATION_GROUPS).split(",");
            setupMdcassetFilters(mdcassetGroupNames);
        }
    }

    //////////////////// admin commands ////////////////////

    /**
     * Format a date into a string representation.
     * @param cal Date to format.
     * @return Date in yyyy-mm-dd hh:mm:ss format. The hours may have 1 or digits.
     */
    private String format(GregorianCalendar cal)
    {
        DecimalFormat twoDigit = new DecimalFormat("00");
        StringBuilder result = new StringBuilder();
        result.append(cal.get(GregorianCalendar.YEAR)).append('-')
              .append(twoDigit.format(cal.get(GregorianCalendar.MONTH)+1)).append('-')
              .append(twoDigit.format(cal.get(GregorianCalendar.DAY_OF_MONTH))).append(' ')
              .append(cal.get(GregorianCalendar.HOUR_OF_DAY)).append(':')
              .append(twoDigit.format(cal.get(GregorianCalendar.MINUTE))).append(':')
              .append(twoDigit.format(cal.get(GregorianCalendar.SECOND)));

        return result.toString();
    }

    /**
     * Parse duration string separated by : or by h m s
     * @param duration Input string to parse.
     * @return duration in seconds, or 0 on error.
     */
    private int parseDuration(String duration)
    {
        int hours = 0, minutes = 0, seconds = 0;
        String[] parts = duration.split("[HMS:hms]");

        if (colonPattern.matcher(duration).matches())
        {
            int nextPart = 0;
            if (parts.length >= 3)  // can be 3, should never be greater
            {
                hours = Integer.parseInt(parts[nextPart++]);
            }
            if (parts.length >= 2)
            {
                minutes = Integer.parseInt(parts[nextPart++]);
            }
            seconds = Integer.parseInt(parts[nextPart]);
        }
        else if (hmsPattern.matcher(duration).matches())
        {
            int nextPart = 0;
            if (duration.contains("h") || duration.contains("H"))
            {
                hours = Integer.parseInt(parts[nextPart++]);
            }
            if (duration.contains("m") || duration.contains("M"))
            {
                minutes = Integer.parseInt(parts[nextPart++]);
            }
            if (duration.contains("s") || duration.contains("S"))
            {
                seconds = Integer.parseInt(parts[nextPart]);
            }
        }
        else
        {
            // Badly formatted input, leave
            return 0;
        }

        return (hours*60 + minutes)*60 + seconds;
    }

    public String adminMarketBuffer(String command, String arg1, String arg2)
    {
        StringBuilder result = new StringBuilder();
        if (command.equalsIgnoreCase("off"))
        {
            if (useMarketBufferAndCurrentMarket)
            {
                return "(invalid command)";
            }
            if (preferCurrentMarket)
            {
                return "(already off)";
            }
            preferCurrentMarket = true;

            for (TwoKeyContainer keys : filters.keySet())
            {
                try
                {
                    removeConstraint(keys);
                }
                catch (Exception e)
                {
                    String s = keys.toString();
                    Log.exception(this, "Can't remove constraint for " + s, e);
                    result.append(' ').append(s);
                }
            }

            // Add filters for CurrentMarket channel
            ServicesHelper.getCurrentMarketConsumerHome().activateSubscription(true);

            if (result.length() > 0)
            {
                // result contains a list of failures; add explanatory text
                result.insert(0, "Done, but could not remove constraints for");
            }
        }
        else if (command.equalsIgnoreCase("on"))
        {
            if (useMarketBufferAndCurrentMarket)
            {
                return "(invalid command)";
            }
            if (!preferCurrentMarket)
            {
                return "(already on)";
            }
            preferCurrentMarket = false;

            // Remove filters for CurrentMarket channel
            ServicesHelper.getCurrentMarketConsumerHome().activateSubscription(false);

            for (TwoKeyContainer keys : filters.keySet())
            {
                try
                {
                    addConstraint(keys);
                }
                catch (Exception e)
                {
                    String s = keys.toString();
                    Log.exception(this, "Can't add constraint for " + s, e);
                    result.append(' ').append(s);
                }
            }
            if (result.length() > 0)
            {
                // result contains a list of failures; add explanatory text
                result.insert(0, "Done, but could not add constraints for");
            }
        }
        else if (command.equalsIgnoreCase("log"))
        {
            GregorianCalendar now = new GregorianCalendar();
            if (arg1.equalsIgnoreCase("show"))
            {
                if (loggingEndTime.before(now))
                {
                    return "(logging currently off)";
                }
                result.append("Logging until ").append(format(loggingEndTime));
            }
            else if (arg1.equalsIgnoreCase("off"))
            {
                if (logTimer != null)
                {
                    // If we have an end-of-logging alarm pending, cancel it
                    logTimer.cancel();
                }
                for (MarketBufferConsumerIECImpl marketBufferConsumer : marketBufferConsumerIECImpl)
                {
                    marketBufferConsumer.logMessages(false);
                }
                // Give logTimer a fair chance to execute its cancel, then discard it
                Thread.yield();
                logTimer = null;
                loggingEndTime = now;
                result.append("Turned logging off");
            }
            else // duration, how long we should log
            {
                int duration = parseDuration(arg1);
                if (duration == 0)
                {
                    return "(invalid duration)";
                }
                if (logTimer != null)
                {
                    // If we're currently logging, we have a timer set to end it.
                    // Stop that timer before we set up a new one.
                    logTimer.cancel();
                }
                for (MarketBufferConsumerIECImpl marketBufferConsumer : marketBufferConsumerIECImpl)
                {
                    marketBufferConsumer.logMessages(true);
                }
                loggingEndTime = now;
                loggingEndTime.add(GregorianCalendar.SECOND, duration);
                logTimer = new Timer("endMarketBufferLogging", true);
                logTimer.schedule(
                    new TimerTask() { public void run() { adminMarketBuffer("log", "off", ""); } },
                    duration*1000);
                result.append("Logging enabled until ").append(format(loggingEndTime));
            }
        }
        else if (command.equalsIgnoreCase("subscriptions"))
        {
            if (arg1.equalsIgnoreCase("show"))
            {
                SortedMap<TwoKeyContainer, Integer> sortedFilters;
                synchronized (filters)
                {
                    sortedFilters = new TreeMap<TwoKeyContainer, Integer>(filters);
                }
                for (TwoKeyContainer filter: sortedFilters.keySet())
                {
                    result.append(' ').append(filter)
                          .append('x').append(sortedFilters.get(filter));
                }
            }
            else if (arg1.equalsIgnoreCase("add"))
            {
                TwoKeyContainer keys = new TwoKeyContainer(arg2);
                if (keys.getServerGroupKey() != 0 || keys.getMdcassetGroupKey() != 0)
                {
                    try
                    {
                        addGroupKeyFilter(keys.getServerGroupKey(), keys.getMdcassetGroupKey());
                    }
                    catch (Exception e)
                    {
                        Log.exception(this, "Could not add subscription for " + keys, e);
                        result.append("Failed, ").append(e.getMessage());
                    }
                }
                else
                {
                    result.append("Missing subscription key");
                }
            }
            else if (arg1.equalsIgnoreCase("remove"))
            {
                TwoKeyContainer keys = new TwoKeyContainer(arg2);
                if (keys.getServerGroupKey() != 0 || keys.getMdcassetGroupKey() != 0)
                {
                    try
                    {
                        removeGroupKeyFilter(keys.getServerGroupKey(), keys.getMdcassetGroupKey());
                    }
                    catch (Exception e)
                    {
                        Log.exception(this, "Could not remove subscription for " + keys, e);
                        result.append("Failed, ").append(e.getMessage());
                    }
                }
                else
                {
                    result.append("Missing subscription key");
                }
            }
            else if (arg1.equalsIgnoreCase("help"))
            {
                result.append("marketBuffer subscriptions { add <subscriptionKey> | remove <subscriptionKey> | show }\n")
                      .append("   subscriptionKey is <serverGroupKey>:<mdcassetGroupKey>\n")
                      .append("   You may omit either key (thus, serverGroupKey: or :mdcassetGroupKey)");
            }
            else
            {
                result.append("Unknown command 'subscriptions ").append(arg1).append("'");
            }
        }
        else
        {
            return "(unknown request, try 'marketBuffer help')";
        }
        return result.toString();
    }

}
