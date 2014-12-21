package com.cboe.interfaces.application;

public interface SystemHealthQueryDispatcherHome
{
    public static final String HOME_NAME = "SystemHealthQueryDispatcherHome";
    
    public SystemHealthQueryDispatcher find();
    public SystemHealthQueryDispatcher create();
}
