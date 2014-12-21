package com.cboe.delegates.callback;

import com.cboe.idl.cmiCallbackV4.POA_CMINBBOConsumer_tie;
import com.cboe.interfaces.callback.NBBOV4Consumer;

public class NBBOV4ConsumerDelegate extends POA_CMINBBOConsumer_tie 
{
	public NBBOV4ConsumerDelegate(NBBOV4Consumer delegate)
    {
        super(delegate);
    }
}
