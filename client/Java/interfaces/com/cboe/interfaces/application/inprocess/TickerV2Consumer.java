package com.cboe.interfaces.application.inprocess;

/**
 * Created by IntelliJ IDEA.
 * User: mageem
 * Date: Oct 21, 2005
 * Time: 3:32:07 PM
 * To change this template use File | Settings | File Templates.
 */
public interface TickerV2Consumer extends com.cboe.interfaces.callback.TickerV2Consumer{
    public  com.cboe.idl.cmiCallbackV2.CMITickerConsumer getCmiTickerConsumer();
}
