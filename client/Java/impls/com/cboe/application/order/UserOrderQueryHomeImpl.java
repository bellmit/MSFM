package com.cboe.application.order;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.SessionManager;
import com.cboe.interfaces.application.UserOrderQueryHome;
import com.cboe.interfaces.application.OrderQueryV6;

/**
 * The User Order Home Impl.  This time for Orders.
 * @author Thomas Lynch
 */
public class UserOrderQueryHomeImpl extends ClientBOHome implements UserOrderQueryHome {

    // Config parameter to indicate which IEC to listen to for Auction announcements
    private static final String FILTER_AUCTION_EVENTS_BY_USER = "filterAuctionEventsByUser";
    // Config parameter to indicate how long to wait before timing out an Auction message.
    private static final String AUCTION_CALLBACK_TIMEOUT = "auctionCallbackTimeout";
    // When true, get Auction events from channel AUCTION_USER; when false, channel AUCTION.
    private boolean userAuctionFilter;
    // Number of milliseconds to wait before timing out an Auction message.
    private int auctionCallbackTimeout;

    /**
      * Follows the prescribed method for creating and generating a impl class.
      * Sets the Session Manager parent class and initializes the Order Query.
      * @param theSession com.cboe.application.session.SessionManager
      */
    public OrderQueryV6 create(SessionManager theSession) {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Creating UserOrderQueryImpl for " + theSession);
        }
        UserOrderQueryImpl bo = new UserOrderQueryImpl();
        //Every bo object must be added to the container BEFORE anything else.
        addToContainer(bo);
        bo.setSessionManager(theSession);
        bo.setUserAuctionFilter(userAuctionFilter);
        bo.setAuctionCallbackTimeout(auctionCallbackTimeout);
        bo.initialize(false);

        //Every BOObject create MUST have a name...if the object is to be a managed object.
        bo.create(String.valueOf(bo.hashCode()));

        UserOrderQueryInterceptor boi = null;
        try {
            boi = (UserOrderQueryInterceptor) this.createInterceptor(bo);
            boi.setSessionManager(theSession);
            if(getInstrumentationEnablementProperty())
            {
                boi.startInstrumentation(getInstrumentationProperty());
            }
        }
        catch (Exception ex) {
            Log.exception(this, ex);
        }

        return boi;
    }

    /**
     *
     * @throws Exception
     */
    public void clientInitialize() throws Exception
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "SMA Type = " + this.getSmaType());
        }
        String smatype = getSmaType();
        StringBuilder calling = new StringBuilder(smatype.length()+75);
        calling.append("UserOrderQueryHomeImpl::clientInit()>> !!!calling getProperty with session=")
               .append(getSmaType());
        Log.information(calling.toString());
        userAuctionFilter = getProperty(FILTER_AUCTION_EVENTS_BY_USER).equals("true");
        auctionCallbackTimeout = Integer.parseInt(getProperty(AUCTION_CALLBACK_TIMEOUT));
    }
}
