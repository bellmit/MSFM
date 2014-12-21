package com.cboe.cfix.cas.marketData;

/**
 * CfixMarketDataConsumerHolder.java
 *
 * @author Dmitry Volpyansky
 *
 */

import com.cboe.cfix.util.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.cmiSession.*;
import com.cboe.interfaces.cfix.*;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;

public class CfixMarketDataConsumerHolderImpl implements CfixMarketDataConsumerHolder
{
    protected CfixMarketDataConsumer         cfixFixMarketDataConsumer;
    protected OverlayPolicyMarketDataListIF  cfixOverlayPolicyMarketDataList;
    protected SessionProductStruct           sessionProductStruct;
    protected SessionClassStruct             sessionClassStruct;
    private int hashCode;

    public CfixMarketDataConsumerHolderImpl(CfixMarketDataConsumer cfixFixMarketDataConsumer, OverlayPolicyMarketDataListIF cfixOverlayPolicyMarketDataList, SessionProductStruct sessionProductStruct)
    {
        this.cfixFixMarketDataConsumer       = cfixFixMarketDataConsumer;
        this.cfixOverlayPolicyMarketDataList = cfixOverlayPolicyMarketDataList;
        this.sessionProductStruct            = sessionProductStruct;
        this.hashCode = (cfixFixMarketDataConsumer.hashCode() + cfixOverlayPolicyMarketDataList.hashCode()) & Integer.MAX_VALUE;
    }

    public CfixMarketDataConsumerHolderImpl(CfixMarketDataConsumer cfixFixMarketDataConsumer, OverlayPolicyMarketDataListIF cfixOverlayPolicyMarketDataList, SessionClassStruct sessionClassStruct)
    {
        this.cfixFixMarketDataConsumer       = cfixFixMarketDataConsumer;
        this.cfixOverlayPolicyMarketDataList = cfixOverlayPolicyMarketDataList;
        this.sessionClassStruct              = sessionClassStruct;
        this.hashCode = (cfixFixMarketDataConsumer.hashCode() + cfixOverlayPolicyMarketDataList.hashCode()) & Integer.MAX_VALUE;
        
    }

    public CfixMarketDataConsumer getCfixMarketDataConsumer()
    {
        return cfixFixMarketDataConsumer;
    }

    public OverlayPolicyMarketDataListIF getOverlayPolicyMarketDataList()
    {
        return cfixOverlayPolicyMarketDataList;
    }

    public boolean containsSessionProductStruct()
    {
        return sessionProductStruct != null;
    }

    public SessionProductStruct getSessionProductStruct()
    {
        return sessionProductStruct;
    }

    public SessionClassStruct getSessionClassStruct()
    {
        return sessionClassStruct;
    }

    public MethodInstrumentor getMethodInstrumentor()
    {
        return null;
    }

    public void acceptMarketDataCurrentMarket(CurrentMarketStruct[] currentMarketStructs) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            ((OverlayPolicyMarketDataCurrentMarketStructListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, currentMarketStructs, 0, currentMarketStructs.length);
        }
    }

    public void acceptMarketDataCurrentMarket(CurrentMarketStruct[] currentMarketStructs, int offset, int length) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            ((OverlayPolicyMarketDataCurrentMarketStructListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, currentMarketStructs, offset, length);
        }
    }

    public void acceptMarketDataCurrentMarket(CurrentMarketStruct currentMarketStruct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            ((OverlayPolicyMarketDataCurrentMarketStructListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, currentMarketStruct);
        }
    }

    public void acceptMarketDataBookDepth(BookDepthStruct[] bookDepthStructs) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            ((OverlayPolicyMarketDataBookDepthStructListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, bookDepthStructs, 0, bookDepthStructs.length);
        }
    }

    public void acceptMarketDataBookDepth(BookDepthStruct[] bookDepthStructs, int offset, int length) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            ((OverlayPolicyMarketDataBookDepthStructListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, bookDepthStructs, offset, length);
        }
    }

    public void acceptMarketDataBookDepth(BookDepthStruct bookDepthStruct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            ((OverlayPolicyMarketDataBookDepthStructListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, bookDepthStruct);
        }
    }

    public void acceptMarketDataBookDepthUpdate(BookDepthStruct[] bookDepthStructs) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            ((OverlayPolicyMarketDataBookDepthStructListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, bookDepthStructs, 0, bookDepthStructs.length);
        }
    }

    public void acceptMarketDataBookDepthUpdate(BookDepthStruct[] bookDepthStructs, int offset, int length) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            ((OverlayPolicyMarketDataBookDepthStructListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, bookDepthStructs, offset, length);
        }
    }

    public void acceptMarketDataBookDepthUpdate(BookDepthStruct bookDepthStruct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            ((OverlayPolicyMarketDataBookDepthStructListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, bookDepthStruct);
        }
    }

    public void acceptMarketDataRecap(RecapStruct[] recapStructs) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            ((OverlayPolicyMarketDataRecapStructListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, recapStructs, 0, recapStructs.length);
        }
    }

    public void acceptMarketDataRecap(RecapStruct[] recapStructs, int offset, int length) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            ((OverlayPolicyMarketDataRecapStructListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, recapStructs, offset, length);
        }
    }

    public void acceptMarketDataRecap(RecapStruct recapStruct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            ((OverlayPolicyMarketDataRecapStructListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, recapStruct);
        }
    }

    public void acceptMarketDataTicker(TickerStruct[] tickerStructs) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            ((OverlayPolicyMarketDataTickerStructListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, tickerStructs, 0, tickerStructs.length);
        }
    }

    public void acceptMarketDataTicker(TickerStruct[] tickerStructs, int offset, int length) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            ((OverlayPolicyMarketDataTickerStructListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, tickerStructs, offset, length);
        }
    }

    public void acceptMarketDataTicker(TickerStruct tickerStruct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            ((OverlayPolicyMarketDataTickerStructListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, tickerStruct);
        }
    }

    public void acceptMarketDataNbbo(NBBOStruct[] nbboStructs) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            ((OverlayPolicyMarketDataNbboStructListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, nbboStructs, 0, nbboStructs.length);
        }
    }

    public void acceptMarketDataNbbo(NBBOStruct[] nbboStructs, int offset, int length) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            ((OverlayPolicyMarketDataNbboStructListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, nbboStructs, offset, length);
        }
    }

    public void acceptMarketDataNbbo(NBBOStruct nbboStruct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            ((OverlayPolicyMarketDataNbboStructListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, nbboStruct);
        }
    }

    public void acceptMarketDataExpectedOpeningPrice(ExpectedOpeningPriceStruct[] expectedOpeningPriceStructs) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            ((OverlayPolicyMarketDataExpectedOpeningPriceStructListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, expectedOpeningPriceStructs, 0, expectedOpeningPriceStructs.length);
        }
    }

    public void acceptMarketDataExpectedOpeningPrice(ExpectedOpeningPriceStruct[] expectedOpeningPriceStructs, int offset, int length) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            ((OverlayPolicyMarketDataExpectedOpeningPriceStructListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, expectedOpeningPriceStructs, offset, length);
        }
    }

    public void acceptMarketDataExpectedOpeningPrice(ExpectedOpeningPriceStruct expectedOpeningPriceStruct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            ((OverlayPolicyMarketDataExpectedOpeningPriceStructListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, expectedOpeningPriceStruct);
        }
    }

    public String toString()
    {
        String mdConsumer = cfixFixMarketDataConsumer.toString();
        String mdReqId = cfixOverlayPolicyMarketDataList.getMdReqID();
        StringBuilder result = new StringBuilder(mdConsumer.length()+mdReqId.length()+60);
        result.append("CfixMarketDataConsumerHolder[consumer(").append(mdConsumer)
              .append(") MDReqID(").append(mdReqId).append(")]");
        return result.toString();
    }

    public boolean equals(Object object)
    {
        if (object == this)
        {
            return true;
        }

        if (!(object instanceof CfixMarketDataConsumerHolderImpl) || object == null)
        {
            return false;
        }

        final CfixMarketDataConsumerHolderImpl other = (CfixMarketDataConsumerHolderImpl) object;

        return cfixFixMarketDataConsumer.equals(other.cfixFixMarketDataConsumer) &&
               cfixOverlayPolicyMarketDataList.equals(other.cfixOverlayPolicyMarketDataList);
    }

    public int hashCode()
    {
        return hashCode;
    }
}
