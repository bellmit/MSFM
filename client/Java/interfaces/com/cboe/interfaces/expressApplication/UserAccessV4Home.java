package com.cboe.interfaces.expressApplication;

public interface UserAccessV4Home
{
    public final static String HOME_NAME = "UserAccessV4Home";
    public UserAccessV4 find();
    public UserAccessV4 create();
    public String objectToString();
}

