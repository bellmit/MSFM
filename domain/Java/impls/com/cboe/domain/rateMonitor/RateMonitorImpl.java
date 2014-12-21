package com.cboe.domain.rateMonitor;

import com.cboe.interfaces.domain.*;
import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;
import com.cboe.interfaces.domain.rateMonitor.RateLimits;
import com.cboe.interfaces.domain.userLoadManager.UserLoadManagerHome;

import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.domain.util.RateMonitorKeyContainer;
import com.cboe.domain.property.PropertyFactory;

import com.cboe.idl.property.PropertyGroupStruct;

import com.cboe.util.event.EventChannelListener;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;

/**
 *  Rate Monitor Implementation.
 *  Calling canAccept determines if an update is allowed for a specific
 *  window.
 */
public class RateMonitorImpl extends BObject implements RateMonitor, EventChannelListener {

    // actual window - holds time stamps of all quotes
    private long[] window;
    // window size
    private int windowSize;
    // window period
    private long windowMilliSecondPeriod;
    // index into the window array - for the next available slot
    private int windowCursor;

    private RateMonitorKeyContainer monitorKey;

    // default values - should the property service values be removed.
    private int defaultWindowSize = 0;
    private long defaultWindowMilliSecondPeriod = 0L;

    

    /**
     * Constructor
     * @param windowSize              maximum number of updates accepted in a given period
     * @param windowMilliSecondPeriod time period (milliseconds)
     */

    public RateMonitorImpl(RateMonitorKeyContainer key, int windowSize, long windowMilliSecondPeriod)
    {
        this(windowSize, windowMilliSecondPeriod);
        this.monitorKey = key;

        // new support for rate change/updates via IEC
        String rateMonitorKey = RateLimitsFactory.getRateMonitorKey(key.getUserId(), key.getExchange(), key.getAcronym());
        ChannelKey channelKey = new ChannelKey(ChannelType.UPDATE_PROPERTY_RATELIMIT, rateMonitorKey);
        EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);
    }

    /**
     * Constructor
     * @param windowSize              maximum number of updates accepted in a given period
     * @param windowMilliSecondPeriod time period (milliseconds)
     *
     * this constructor support the remove ratelimit IEC event by saving the defaults
     * for later use should a remove event occur
     */

    public RateMonitorImpl(RateMonitorKeyContainer key, int windowSize, int defaultWindowSize
                           ,long windowMilliSecondPeriod, long defaultWindowMilliSecondPeriod)
    {
        this(key, windowSize, windowMilliSecondPeriod);

        this.defaultWindowSize = defaultWindowSize;
        this.defaultWindowMilliSecondPeriod = defaultWindowMilliSecondPeriod;

        // new support for rate remove via IEC
        String rateMonitorKey = RateLimitsFactory.getRateMonitorKey(key.getUserId(), key.getExchange(), key.getAcronym());
        ChannelKey channelKey = new ChannelKey(ChannelType.REMOVE_PROPERTY_RATELIMIT, rateMonitorKey);
        EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);
    }

    private RateMonitorImpl(int windowSize, long windowMilliSecondPeriod)
    {
        acceptRateChange(windowSize, windowMilliSecondPeriod);
    }

    /**
     *  Determine if the update can be accepted
     * @param currentTime current timestamp in milliseconds
     * @return true if the update can be accepted
     */
    public synchronized boolean canAccept(long currentTime)
    {
        boolean retValue;

        if(windowMilliSecondPeriod < (currentTime - window[windowCursor]))
        {
            retValue = true;
            window[windowCursor] = currentTime;
            windowCursor = (windowCursor + 1) % windowSize;
        }
        else
        {
            retValue = false;
        }

        return retValue;
    }

    /**
     * Determine if we may perform the operation multiple times.
     * @param currentTime Current timestamp in milliseconds.
     * @param blockSize Number of operations to perform.
     * @return true if the operations may be performed.
     */
    public synchronized boolean canAccept(long currentTime, int blockSize)
    {
        int lastCursor = (windowCursor+blockSize-1) % windowSize;
        if (windowMilliSecondPeriod < (currentTime - window[lastCursor])
        &&  blockSize <= windowSize)
        {
            while (blockSize-- > 0)
            {
                window[windowCursor] = currentTime;
                windowCursor = (windowCursor + 1) % windowSize;
            }
            return true;
        }

        return false;
    }

    /**
     * Preview version of canAccept. Does not modify this object.
     * @param currentTime Current timestamp in milliseconds.
     * @return true if the operation may be performed.
     * @see #canAccept(long)
     */
    public synchronized boolean previewCanAccept(long currentTime)
    {
        return (windowMilliSecondPeriod < (currentTime - window[windowCursor]));
    }

    /**
     * Preview version of canAccept. Does not modify this object.
     * @param currentTime Current timestamp in milliseconds.
     * @param blockSize Number of operations to perform.
     * @return true if the operations may be performed.
     * @see #canAccept(long, int)
     */
    public synchronized boolean previewCanAccept(long currentTime, int blockSize)
    {
        int lastCursor = (windowCursor+blockSize-1) % windowSize;
        return (windowMilliSecondPeriod < (currentTime - window[lastCursor]))
            &&  blockSize <= windowSize;
    }

    /**
     * @param blockSize - size of window or "block"
     */
    public synchronized boolean canAcceptBlock(int blockSize)
    {
        return blockSize <= windowSize;
    }

    /**
     *  Change the current rate
     *
     *  NOTE : Rate changes are processed by blowing the previous window away.
     *
     *  @param windowSize maximum number of updates accepted in a given period
     *  @param windowMilliSecondPeriod time period (milliseconds)
     */
    private void acceptRateChange(int windowSize, long windowMilliSecondPeriod)
    {
        // blow current window away - and reset all variables
    	if (Log.isDebugOn())
    	{
    		Log.debug("RateMonitorImpl -> acceptRateChange()::" + monitorKey + ":" + windowSize + ":" + windowMilliSecondPeriod);
    	}
        this.windowSize = windowSize;
        // Set the window to the largest possible value so a check against the current time always fails.
        // This prevents an ArrayIndexOutofBoundsException from being thrown when the user's quote rate value is
        // set to zero. Also, set the windowSize to 1 to prevent division by zero exceptions.
        if (windowSize < 1)
        {
            window = new long[] {Long.MAX_VALUE};
            this.windowSize = 1;
        }
        else
        {
            window = new long[windowSize];
        }
        this.windowMilliSecondPeriod = windowMilliSecondPeriod;
        windowCursor = 0;
    }


    /**
     *  Return current window size
     */
    public synchronized int getWindowSize() {
        return windowSize;
    }

    /**
     *  Return current window period
     */
    public synchronized long getWindowMilliSecondPeriod() {
        return windowMilliSecondPeriod;
    }

    /**
     * handle property service based updates for 'this' rate impl
     * @param event
     */
    public void channelUpdate(ChannelEvent event)
    {
        ChannelKey channelKey = (ChannelKey)event.getChannel();

        if(Log.isDebugOn())
        {
            Log.debug("RateMonitorImpl -> : received event " + channelKey + " " + channelKey.key);
        }

        try
        {
            switch(channelKey.channelType)
            {
                case ChannelType.UPDATE_PROPERTY_RATELIMIT:
                    PropertyGroupStruct property = (PropertyGroupStruct) event.getEventData();
                    if(Log.isDebugOn())
                    {
                    	String sessionName = monitorKey.getSession();
                		short type = monitorKey.getType();
                        Log.debug("RateMonitorImpl.channelUpdate(UPDATE_RATELIMIT) -> " + sessionName + ":" + type + ":" + property.propertyKey + ":" + property.category);
                    }
                    updateRateChange(property);
                break;

                case ChannelType.REMOVE_PROPERTY_RATELIMIT:
                    String propertyKey = (String) event.getEventData();
                    if(Log.isDebugOn())
                    {
                    	String sessionName = monitorKey.getSession();
                		short type = monitorKey.getType();
                        Log.debug("RateMonitorImpl.channelUpdate(REMOVE_RATELIMIT) -> " + sessionName + ":" + type + ":" + propertyKey);
                    }
                    removeRate(propertyKey);
                break;

                default :
                    Log.alarm("User Based Rate Monitor Service -> Wrong Channel : " + channelKey.channelType);
                break;
            }
        }
        catch(Exception e)
        {
            Log.exception("RateMonitorImpl", e);
        }
    }

    private void updateRateChange(PropertyGroupStruct property)
    {
    	// remember the current size
    	int oldWindowSize = this.windowSize;
		long oldWindowMilliSecondPeriod = this.windowMilliSecondPeriod;

		int tmpWindowSize= 0;
		long tmpWindowMilliSecondPeriod = 0;
		String sessionName = monitorKey.getSession();
		short type = monitorKey.getType();

		synchronized(this){ // no value in between 
        // This is a hack to fix the problem that removing a property does not
		// result
		// in a remove event being sent. We set the property to it's default
		// before
		// doing the update in case this update is actually a remove.
		acceptRateChange(this.defaultWindowSize,
				this.defaultWindowMilliSecondPeriod);
       
        // must find entry by sessionName & type that matches this impls since an impl
        // is created for each sessionName/type AND all impls for this exchange/acr/userId
        // receive the same event
        //
        try
        {
            PropertyServicePropertyGroup propertyServiceGroup = PropertyFactory.createPropertyGroup(property);
            RateLimits rateLimit = RateLimitsFactory.getRateLimitBySessionType(propertyServiceGroup, sessionName, type);
            tmpWindowSize = rateLimit.getWindowSize();
            tmpWindowMilliSecondPeriod = rateLimit.getWindowInterval();
            acceptRateChange(tmpWindowSize,tmpWindowMilliSecondPeriod );
            
            
        }
        catch(Exception e)
        {
            Log.exception("RateMonitorImpl:updateRateChange:Unable to retrieve rate information for sessionName:type::" + sessionName + ":" + type, e);
        }
		}// end of synchronized
		
		if (Log.isDebugOn())
        {
            Log.debug("RateMonitorImpl -> updateRateChange()::" + property.propertyKey+":"+property.category+":"+sessionName + ":type#" + type + ":"+getWindowSize() + ":"+getWindowMilliSecondPeriod()+";old vaule:"+oldWindowSize+":"+oldWindowMilliSecondPeriod);
        }
        
		// update ULM
		if (RateMonitorHomeImpl.useManagedThreadPools) {
			
			//only update if value changed
			if ((type == RateMonitorTypeConstants.ACCEPT_ORDER
					|| type == RateMonitorTypeConstants.ACCEPT_LIGHT_ORDER
					|| type == RateMonitorTypeConstants.ACCEPT_QUOTE)&&(windowSize!=oldWindowSize ||windowMilliSecondPeriod!=oldWindowMilliSecondPeriod)) {
				UserLoadManagerHome userLoadManagerHome = getUserLoadManagerHome();
				if (userLoadManagerHome != null) {
					userLoadManagerHome.find().updateUserLimit(
							this.monitorKey, oldWindowSize,
							oldWindowMilliSecondPeriod);
				}

			}
		}
    }

    private void removeRate(String propertyKey)
    {
        String sessionName = monitorKey.getSession();
        short type = monitorKey.getType();
        
        //remember the current size
    	int oldWindowSize = this.windowSize;
		long oldWindowMilliSecondPeriod = this.windowMilliSecondPeriod;
		
        // all impls for this exchange/acr/userId receive the same event
        // so just have "this" one default to its default values regardless of
        // the sessionName/type
        //
        
        synchronized(this){
        	acceptRateChange(this.defaultWindowSize, this.defaultWindowMilliSecondPeriod);
        }
        if(Log.isDebugOn())
        {
            Log.debug("RateMonitorImpl -> removeRate()::" + propertyKey + ":"+sessionName+":type#" + type + ":"+getWindowSize() + ":"+getWindowMilliSecondPeriod()+";old vaule:"+oldWindowSize+":"+oldWindowMilliSecondPeriod);
        }
        // remove is to set it to default.
		if (RateMonitorHomeImpl.useManagedThreadPools) {
			if ((type == RateMonitorTypeConstants.ACCEPT_ORDER
					|| type == RateMonitorTypeConstants.ACCEPT_LIGHT_ORDER
					|| type == RateMonitorTypeConstants.ACCEPT_QUOTE)&&(windowSize!=oldWindowSize ||windowMilliSecondPeriod!=oldWindowMilliSecondPeriod)) {
				UserLoadManagerHome userLoadManagerHome = getUserLoadManagerHome();
				if (userLoadManagerHome != null) {
					userLoadManagerHome.find().updateUserLimit(this.monitorKey,
							oldWindowSize, oldWindowMilliSecondPeriod);
				}

			}
		}
    }
    
    // look for UserLoadManagerHome, if not found, return null
    private UserLoadManagerHome getUserLoadManagerHome()
    {
        
        UserLoadManagerHome userLoadManagerHome = null;
            try
            {
            	userLoadManagerHome = (UserLoadManagerHome) HomeFactory.getInstance().findHome(UserLoadManagerHome.HOME_NAME);
            }
            catch (CBOELoggableException e)
            {
                Log.exception("Could not find UserLoadManagerHome", e);
                // a really ugly way to get around the missing exception in the interface...
                //throw new NullPointerException("Could not find UserLoadManagerHome");
                return null;
            }
        
        return userLoadManagerHome;
    }

}
