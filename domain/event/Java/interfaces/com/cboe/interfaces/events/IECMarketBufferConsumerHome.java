package com.cboe.interfaces.events;

import com.cboe.exceptions.SystemException;

/**
 * This is the common interface for the MarketBuffer Home
 */
public interface IECMarketBufferConsumerHome extends MarketBufferConsumerHome
{
    void addGroupKeyFilter(int serverGroupKey, int mdcassetGroupKey) throws SystemException;
    void addClassKeyFilter(int classKey) throws SystemException;
    void removeGroupKeyFilter(int serverGroupKey, int mdcassetGroupKey) throws SystemException;
    void removeClassKeyFilter(int classKey) throws SystemException;
}
