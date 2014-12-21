package com.cboe.interfaces.domain;

import com.cboe.exceptions.NotFoundException;

public interface TradingProductSync
{
    public void updateToSync(TradingProduct product);
    public void updateFromSync(TradingProduct product) throws NotFoundException;
}
