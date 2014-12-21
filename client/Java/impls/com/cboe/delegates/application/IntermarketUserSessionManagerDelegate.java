/*
 * Created by IntelliJ IDEA.
 * User: HUANGE
 * Date: Sep 6, 2002
 * Time: 12:58:39 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cboe.delegates.application;

import com.cboe.interfaces.application.IntermarketUserSessionManager;

public class IntermarketUserSessionManagerDelegate extends com.cboe.idl.cmiIntermarket.POA_IntermarketUserSessionManager_tie{
    public IntermarketUserSessionManagerDelegate(IntermarketUserSessionManager delegate) {
        super(delegate);
    }
}

