package com.cboe.interfaces.cfix;

/**
 * OverlayPolicyMarketDataHolderIF.java
 *
 * @author Dmitry Volpyansky
 *
 */

import com.cboe.client.util.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.interfaces.domain.RecapContainerV4IF;

public interface OverlayPolicyMarketDataHolderIF extends HasSizeIF
{
    public String                           getMdReqID();
    public void                             setMdReqID(String mdReqID);

    public int                              getMarketDataType();
    public void                             setMarketDataType(int marketDataType);

    public int                              size();
    public void                             setSize(int size);

    public void                             clear();

    public void                             setOverlaid(BitArrayIF overlaid);
    public BitArrayIF                       getOverlaid();

    public void                             setPolicyType(int policyType);
    public int                              getPolicyType();

    public void                             setBookDepthStructs(BookDepthStruct[] bookDepthStructs);
    public BookDepthStruct[]                getBookDepthStructs();
    public BookDepthStruct                  getBookDepthStruct(int index);

    public void                             setCurrentMarketStructs(CurrentMarketStruct[] currentMarketStructs);
    public CurrentMarketStruct[]            getCurrentMarketStructs();
    public CurrentMarketStruct              getCurrentMarketStruct(int index);

    public void                             setCurrentMarketStructsV4(CurrentMarketStructV4[] currentMarketStructsV4);
    public CurrentMarketStructV4[]          getCurrentMarketStructsV4();
    public CurrentMarketStructV4            getCurrentMarketStructV4(int index);

    public void                             setExpectedOpeningPriceStructs(ExpectedOpeningPriceStruct[] expectedOpeningPriceStructs);
    public ExpectedOpeningPriceStruct[]     getExpectedOpeningPriceStructs();
    public ExpectedOpeningPriceStruct       getExpectedOpeningPriceStruct(int index);

    public void                             setNbboStructs(NBBOStruct[] nBBOStructs);
    public NBBOStruct[]                     getNbboStructs();
    public NBBOStruct                       getNbboStruct(int index);

    public void                             setRecapStructs(RecapStruct[] recapStructs);
    public RecapStruct[]                    getRecapStructs();
    public RecapStruct                      getRecapStruct(int index);

    public void                             setRecapStructsV4(RecapStructV4[] recapStructsV4);
    public RecapStructV4[]                  getRecapStructsV4();
    public RecapStructV4                    getRecapStructV4(int index);

    public void                             setRecapContainersV4(RecapContainerV4IF[] recapContainersV4);
    public RecapContainerV4IF[]             getRecapContainersV4();
    public RecapContainerV4IF               getRecapContainerV4(int index);

    public void                             setTickerStructs(TickerStruct[] tickerStructs);
    public TickerStruct[]                   getTickerStructs();
    public TickerStruct                     getTickerStruct(int index);

    public void                             setTickerStructsV4(TickerStructV4[] tickerStructsV4);
    public TickerStructV4[]                 getTickerStructsV4();
    public TickerStructV4                   getTickerStructV4(int index);
}
