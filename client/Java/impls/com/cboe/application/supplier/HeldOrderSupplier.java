/*
 * Created by IntelliJ IDEA.
 * User: huange
 * Date: Oct 4, 2002
 * Time: 4:15:43 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cboe.application.supplier;

import com.cboe.application.supplier.proxy.HeldOrderConsumerProxy;
import com.cboe.domain.instrumentedChannel.supplier.InstrumentedBaseSupplier;

public class HeldOrderSupplier extends InstrumentedBaseSupplier {
    public String getListenerClassName()
    {
        return HeldOrderConsumerProxy.class.getName();
    }

}
