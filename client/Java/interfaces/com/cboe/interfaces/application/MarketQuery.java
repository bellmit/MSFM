package com.cboe.interfaces.application;

/**
 * This extends the CORBA Interface into a CBOE Common standard
 * @author Jeff Illian
 */
public interface MarketQuery extends com.cboe.idl.cmi.MarketQueryOperations {
    public static final int CHANNEL_FIELD = 0;
    public static final int SESSION_FIELD = 1;
    public static final int CLASSKEY_FIELD = 2;
    public static final int PRODUCTKEY_FIELD = 2;
    public static final int LISTENER_FIELD = 3;
}
