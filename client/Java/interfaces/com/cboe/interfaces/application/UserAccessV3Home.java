package com.cboe.interfaces.application;


public interface UserAccessV3Home
{
    public final static String HOME_NAME = "UserAccessV3Home";
    public UserAccessV3 find();
    public UserAccessV3 create();
    public String objectToString();
}
