/*
 * Created by IntelliJ IDEA.
 * User: huange
 * Date: Oct 11, 2002
 * Time: 2:37:49 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cboe.application.supplier;

import com.cboe.application.supplier.proxy.NBBOAgentSessionAdminProxy;
import com.cboe.domain.instrumentedChannel.supplier.InstrumentedBaseSupplier;

public class NBBOAgentAdminSupplier extends InstrumentedBaseSupplier {
    public String getListenerClassName()
    {
        return NBBOAgentSessionAdminProxy.class.getName();
    }
}
