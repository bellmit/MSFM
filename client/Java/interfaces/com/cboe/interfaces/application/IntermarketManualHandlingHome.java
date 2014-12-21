package com.cboe.interfaces.application;


public interface IntermarketManualHandlingHome {

    public final static String HOME_NAME = "IntermarketManualHandlingHome";

    public IntermarketManualHandling create(SessionManager sessionManager);
}
