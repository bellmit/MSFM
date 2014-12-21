package com.cboe.interfaces.floorApplication;

/**
 * User: mahoney
 * Date: Jul 17, 2007
 */
public interface UserAccessFloorHome
{
    public final static String HOME_NAME = "UserAccessFloorHome";
    public UserAccessFloor find();
    public UserAccessFloor create();
    public String objectToString();
}
