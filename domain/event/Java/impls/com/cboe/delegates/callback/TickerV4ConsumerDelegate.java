package com.cboe.delegates.callback;

import com.cboe.interfaces.callback.TickerV4Consumer;
import com.cboe.idl.cmiCallbackV4.POA_CMITickerConsumer_tie;

public class TickerV4ConsumerDelegate extends POA_CMITickerConsumer_tie
{
    public TickerV4ConsumerDelegate(TickerV4Consumer delegate)
    {
        super(delegate);
    }
}
