package com.cboe.delegates.callback;

import com.cboe.idl.cmiCallbackV2.POA_CMINBBOConsumer_tie;
import com.cboe.interfaces.callback.NBBOV2Consumer;

public class NBBOV2ConsumerDelegate extends POA_CMINBBOConsumer_tie {

    public NBBOV2ConsumerDelegate(NBBOV2Consumer delegate) {
        super(delegate);
    }
}

