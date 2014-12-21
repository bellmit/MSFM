package com.cboe.delegates.callback;

import com.cboe.interfaces.callback.*;

public class ExpectedOpeningPriceConsumerDelegate extends com.cboe.idl.cmiCallback.POA_CMIExpectedOpeningPriceConsumer_tie {

    public ExpectedOpeningPriceConsumerDelegate(ExpectedOpeningPriceConsumer delegate) {
        super(delegate);
    }
}
