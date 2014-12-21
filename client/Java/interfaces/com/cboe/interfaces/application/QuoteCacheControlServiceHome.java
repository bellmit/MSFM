package com.cboe.interfaces.application;

public interface QuoteCacheControlServiceHome
{
    public static final String HOME_NAME = "QuoteCacheControlService";
    
    public QuoteCacheControlService find();
    public QuoteCacheControlService create();
}
