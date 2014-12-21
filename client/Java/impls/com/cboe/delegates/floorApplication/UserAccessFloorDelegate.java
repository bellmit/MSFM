package com.cboe.delegates.floorApplication;

import com.cboe.interfaces.floorApplication.UserAccessFloor;

/**
 * Author: mahoney
 * Date: Jul 18, 2007
 */
public class UserAccessFloorDelegate extends com.cboe.idl.floorApplication.POA_UserAccessFloor_tie
{
    public UserAccessFloorDelegate(UserAccessFloor delegate)
    {
        super(delegate);
    }
}