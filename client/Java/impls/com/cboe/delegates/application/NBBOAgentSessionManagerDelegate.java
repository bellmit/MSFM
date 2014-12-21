/*
 * Created by IntelliJ IDEA.
 * User: huange
 * Date: Sep 30, 2002
 * Time: 3:07:30 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cboe.delegates.application;

import com.cboe.interfaces.application.NBBOAgentSessionManager;

public class NBBOAgentSessionManagerDelegate
        extends com.cboe.idl.cmiIntermarket.POA_NBBOAgentSessionManager_tie {
    public NBBOAgentSessionManagerDelegate(NBBOAgentSessionManager delegate) {
        super(delegate);
    }
}