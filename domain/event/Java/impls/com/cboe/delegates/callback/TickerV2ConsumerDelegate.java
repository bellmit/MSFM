package com.cboe.delegates.callback;

import com.cboe.interfaces.callback.TickerV2Consumer;
import com.cboe.idl.cmiCallbackV2.POA_CMITickerConsumer_tie;

public class TickerV2ConsumerDelegate extends POA_CMITickerConsumer_tie {

    public TickerV2ConsumerDelegate(TickerV2Consumer delegate) {
        super(delegate);
    }
}
