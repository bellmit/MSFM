package com.cboe.interfaces.domain;

import com.cboe.util.CboeException;

public class AuctionBookTradableNotFoundException extends CboeException
{
    public AuctionBookTradableNotFoundException()
    {
        super();
    }

    public AuctionBookTradableNotFoundException(String s)
    {
        super(s);
    }
}
