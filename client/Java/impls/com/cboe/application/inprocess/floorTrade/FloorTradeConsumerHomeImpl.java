package com.cboe.application.inprocess.floorTrade;

import com.cboe.interfaces.application.inprocess.FloorTradeConsumerHome;
import com.cboe.interfaces.application.inprocess.FloorTradeConsumer;
import com.cboe.interfaces.application.inprocess.InProcessSessionManager;
import com.cboe.domain.startup.ClientBOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.application.inprocess.floorTrade.FloorTradeConsumerImpl;

/**
 * Created by IntelliJ IDEA.
 * User: mageem
 * Date: Jun 25, 2009
 * Time: 11:42:11 AM
 * To change this template use File | Settings | File Templates.
 */
public class FloorTradeConsumerHomeImpl extends ClientBOHome implements FloorTradeConsumerHome {
    public FloorTradeConsumer create(InProcessSessionManager theSession) {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Creating :FloorTradeConsumerImpl for " + theSession);
        }
        FloorTradeConsumerImpl bo = new FloorTradeConsumerImpl();
        //Every bo object must be added to the container BEFORE anything else.
        addToContainer(bo);
        bo.setInProcessSessionManager(theSession);

        //Every BOObject create MUST have a name...if the object is to be a managed object.
        bo.create(String.valueOf(bo.hashCode()));

        FloorTradeConsumerInterceptor boi = null;
        try {
            boi = (FloorTradeConsumerInterceptor) this.createInterceptor(bo);
            boi.setSessionManager(theSession);
        }
        catch (Exception ex) {
            Log.exception(this, ex);
        }
        return boi;
    }
}
