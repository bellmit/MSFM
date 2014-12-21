package com.cboe.delegates.callback;

import com.cboe.interfaces.callback.*;

public class AuctionConsumerDelegate extends com.cboe.idl.cmiCallbackV3.POA_CMIAuctionConsumer_tie {

    public AuctionConsumerDelegate(AuctionConsumer delegate) {
        super(delegate);
    }
}
