package com.cboe.delegates.callback;

import com.cboe.interfaces.callback.RecapV2Consumer;
import com.cboe.idl.cmiCallbackV2.POA_CMIRecapConsumer_tie;

public class RecapV2ConsumerDelegate extends POA_CMIRecapConsumer_tie {

    public RecapV2ConsumerDelegate(RecapV2Consumer delegate) {
        super(delegate);
    }
}
