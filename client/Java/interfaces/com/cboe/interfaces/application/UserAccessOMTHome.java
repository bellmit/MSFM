package com.cboe.interfaces.application;

/**
 * Created by IntelliJ IDEA.
 * User: mahoney
 * Date: May 3, 2007
 * Time: 10:28:44 AM
 * To change this template use File | Settings | File Templates.
 */
public interface UserAccessOMTHome
{
    public final static String HOME_NAME = "UserAccessOMTHome";
    public UserAccessOMT find();
    public UserAccessOMT create();
    public String objectToString();
}
