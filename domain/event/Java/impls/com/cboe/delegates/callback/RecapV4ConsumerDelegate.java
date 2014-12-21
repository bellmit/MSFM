package com.cboe.delegates.callback;

import com.cboe.interfaces.callback.RecapV4Consumer;
import com.cboe.idl.cmiCallbackV4.POA_CMIRecapConsumer_tie;

public class RecapV4ConsumerDelegate extends POA_CMIRecapConsumer_tie
{
    public RecapV4ConsumerDelegate(RecapV4Consumer delegate)
    {
        super(delegate);
    }
}
