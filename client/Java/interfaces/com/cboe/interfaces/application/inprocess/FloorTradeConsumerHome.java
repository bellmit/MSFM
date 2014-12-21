package com.cboe.interfaces.application.inprocess;


/**
 * Created by IntelliJ IDEA.
 * User: mageem
 * Date: Jun 25, 2009
 * Time: 9:38:38 AM
 * To change this template use File | Settings | File Templates.
 */
public interface FloorTradeConsumerHome {
    public final static String HOME_NAME = "FloorTradeConsumerHome";
    public FloorTradeConsumer create(InProcessSessionManager sessionManager);
}
