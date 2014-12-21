package com.cboe.delegates.floorApplication;

import com.cboe.interfaces.floorApplication.FloorSessionManager;

/**
 * Author: mahoney
 * Date: Jul 18, 2007
 */
public class FloorSessionManagerDelegate  extends com.cboe.idl.floorApplication.POA_FloorSessionManager_tie
{
    public FloorSessionManagerDelegate(FloorSessionManager delegate)
    {
        super(delegate);
    }
}
