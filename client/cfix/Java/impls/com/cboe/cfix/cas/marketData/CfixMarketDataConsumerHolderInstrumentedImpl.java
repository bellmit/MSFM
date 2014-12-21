package com.cboe.cfix.cas.marketData;

import com.cboe.interfaces.cfix.CfixMarketDataConsumerHolder;

/**
 * CfixMarketDataConsumerHolderInstrumentedImpl.java
 *
 * @author Vivek Beniwal
 */

import com.cboe.cfix.util.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.cmiSession.*;
import com.cboe.interfaces.cfix.*;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;

public class CfixMarketDataConsumerHolderInstrumentedImpl implements CfixMarketDataConsumerHolder
{
    protected CfixMarketDataConsumer         cfixFixMarketDataConsumer;
    protected OverlayPolicyMarketDataListIF  cfixOverlayPolicyMarketDataList;
    protected SessionProductStruct           sessionProductStruct;
    protected SessionClassStruct             sessionClassStruct;
    protected MethodInstrumentor             methodInstrumentor;
    private int hashCode;
    
    public CfixMarketDataConsumerHolderInstrumentedImpl(CfixMarketDataConsumer cfixFixMarketDataConsumer, OverlayPolicyMarketDataListIF cfixOverlayPolicyMarketDataList, SessionProductStruct sessionProductStruct, MethodInstrumentor methodInstrumentor)
    {
        this.cfixFixMarketDataConsumer       = cfixFixMarketDataConsumer;
        this.cfixOverlayPolicyMarketDataList = cfixOverlayPolicyMarketDataList;
        this.sessionProductStruct            = sessionProductStruct;
        this.methodInstrumentor              = methodInstrumentor;
        this.hashCode = (cfixFixMarketDataConsumer.hashCode() + cfixOverlayPolicyMarketDataList.hashCode()) & Integer.MAX_VALUE;
    }

    public CfixMarketDataConsumerHolderInstrumentedImpl(CfixMarketDataConsumer cfixFixMarketDataConsumer, OverlayPolicyMarketDataListIF cfixOverlayPolicyMarketDataList, SessionClassStruct sessionClassStruct, MethodInstrumentor methodInstrumentor)
    {
        this.cfixFixMarketDataConsumer       = cfixFixMarketDataConsumer;
        this.cfixOverlayPolicyMarketDataList = cfixOverlayPolicyMarketDataList;
        this.sessionClassStruct              = sessionClassStruct;
        this.methodInstrumentor              = methodInstrumentor;
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

    public void setMethodInstrumentor(MethodInstrumentor methodInstrumentor)
    {
        this.methodInstrumentor = methodInstrumentor;
    }

    public MethodInstrumentor getMethodInstrumentor()
    {
        return methodInstrumentor;
    }


    public void acceptMarketDataCurrentMarket(CurrentMarketStruct[] currentMarketStructs) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            methodInstrumentor.beforeMethodCall();
            ((OverlayPolicyMarketDataCurrentMarketStructListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, currentMarketStructs, 0, currentMarketStructs.length);
            methodInstrumentor.incCalls(1);
            methodInstrumentor.afterMethodCall();
        }
    }

    public void acceptMarketDataCurrentMarket(CurrentMarketStruct[] currentMarketStructs, int offset, int length) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            methodInstrumentor.beforeMethodCall();
            ((OverlayPolicyMarketDataCurrentMarketStructListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, currentMarketStructs, offset, length);
            methodInstrumentor.incCalls(1);
            methodInstrumentor.afterMethodCall();
        }
    }

    public void acceptMarketDataCurrentMarket(CurrentMarketStruct currentMarketStruct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            methodInstrumentor.beforeMethodCall();
            ((OverlayPolicyMarketDataCurrentMarketStructListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, currentMarketStruct);
            methodInstrumentor.incCalls(1);
            methodInstrumentor.afterMethodCall();
        }
    }

    public void acceptMarketDataBookDepth(BookDepthStruct[] bookDepthStructs) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            methodInstrumentor.beforeMethodCall();
            ((OverlayPolicyMarketDataBookDepthStructListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, bookDepthStructs, 0, bookDepthStructs.length);
            methodInstrumentor.incCalls(1);
            methodInstrumentor.afterMethodCall();
        }
    }

    public void acceptMarketDataBookDepth(BookDepthStruct[] bookDepthStructs, int offset, int length) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            methodInstrumentor.beforeMethodCall();
            ((OverlayPolicyMarketDataBookDepthStructListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, bookDepthStructs, offset, length);
            methodInstrumentor.incCalls(1);
            methodInstrumentor.afterMethodCall();
        }
    }

    public void acceptMarketDataBookDepth(BookDepthStruct bookDepthStruct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            methodInstrumentor.beforeMethodCall();
            ((OverlayPolicyMarketDataBookDepthStructListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, bookDepthStruct);
            methodInstrumentor.incCalls(1);
            methodInstrumentor.afterMethodCall();
        }
    }

    public void acceptMarketDataBookDepthUpdate(BookDepthStruct[] bookDepthStructs) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            methodInstrumentor.beforeMethodCall();
            ((OverlayPolicyMarketDataBookDepthStructListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, bookDepthStructs, 0, bookDepthStructs.length);
            methodInstrumentor.incCalls(1);
            methodInstrumentor.afterMethodCall();
        }
    }

    public void acceptMarketDataBookDepthUpdate(BookDepthStruct[] bookDepthStructs, int offset, int length) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            methodInstrumentor.beforeMethodCall();
            ((OverlayPolicyMarketDataBookDepthStructListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, bookDepthStructs, offset, length);
            methodInstrumentor.incCalls(1);
            methodInstrumentor.afterMethodCall();
        }
    }

    public void acceptMarketDataBookDepthUpdate(BookDepthStruct bookDepthStruct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            methodInstrumentor.beforeMethodCall();
            ((OverlayPolicyMarketDataBookDepthStructListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, bookDepthStruct);
            methodInstrumentor.incCalls(1);
            methodInstrumentor.afterMethodCall();
        }
    }

    public void acceptMarketDataRecap(RecapStruct[] recapStructs) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            methodInstrumentor.beforeMethodCall();
            ((OverlayPolicyMarketDataRecapStructListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, recapStructs, 0, recapStructs.length);
            methodInstrumentor.incCalls(1);
            methodInstrumentor.afterMethodCall();
        }
    }

    public void acceptMarketDataRecap(RecapStruct[] recapStructs, int offset, int length) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            methodInstrumentor.beforeMethodCall();
            ((OverlayPolicyMarketDataRecapStructListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, recapStructs, offset, length);
            methodInstrumentor.incCalls(1);
            methodInstrumentor.afterMethodCall();
        }
    }

    public void acceptMarketDataRecap(RecapStruct recapStruct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            methodInstrumentor.beforeMethodCall();
            ((OverlayPolicyMarketDataRecapStructListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, recapStruct);
            methodInstrumentor.incCalls(1);
            methodInstrumentor.afterMethodCall();
        }
    }

    public void acceptMarketDataTicker(TickerStruct[] tickerStructs) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            methodInstrumentor.beforeMethodCall();
            ((OverlayPolicyMarketDataTickerStructListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, tickerStructs, 0, tickerStructs.length);
            methodInstrumentor.incCalls(1);
            methodInstrumentor.afterMethodCall();
        }
    }

    public void acceptMarketDataTicker(TickerStruct[] tickerStructs, int offset, int length) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            methodInstrumentor.beforeMethodCall();
            ((OverlayPolicyMarketDataTickerStructListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, tickerStructs, offset, length);
            methodInstrumentor.incCalls(1);
            methodInstrumentor.afterMethodCall();
        }
    }

    public void acceptMarketDataTicker(TickerStruct tickerStruct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            methodInstrumentor.beforeMethodCall();
            ((OverlayPolicyMarketDataTickerStructListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, tickerStruct);
            methodInstrumentor.incCalls(1);
            methodInstrumentor.afterMethodCall();
        }
    }

    public void acceptMarketDataNbbo(NBBOStruct[] nbboStructs) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            methodInstrumentor.beforeMethodCall();
            ((OverlayPolicyMarketDataNbboStructListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, nbboStructs, 0, nbboStructs.length);
            methodInstrumentor.incCalls(1);
            methodInstrumentor.afterMethodCall();
        }
    }

    public void acceptMarketDataNbbo(NBBOStruct[] nbboStructs, int offset, int length) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            methodInstrumentor.beforeMethodCall();
            ((OverlayPolicyMarketDataNbboStructListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, nbboStructs, offset, length);
            methodInstrumentor.incCalls(1);
            methodInstrumentor.afterMethodCall();
        }
    }

    public void acceptMarketDataNbbo(NBBOStruct nbboStruct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            methodInstrumentor.beforeMethodCall();
            ((OverlayPolicyMarketDataNbboStructListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, nbboStruct);
            methodInstrumentor.incCalls(1);
            methodInstrumentor.afterMethodCall();
        }
    }

    public void acceptMarketDataExpectedOpeningPrice(ExpectedOpeningPriceStruct[] expectedOpeningPriceStructs) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            methodInstrumentor.beforeMethodCall();
            ((OverlayPolicyMarketDataExpectedOpeningPriceStructListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, expectedOpeningPriceStructs, 0, expectedOpeningPriceStructs.length);
            methodInstrumentor.incCalls(1);
            methodInstrumentor.afterMethodCall();
        }
    }

    public void acceptMarketDataExpectedOpeningPrice(ExpectedOpeningPriceStruct[] expectedOpeningPriceStructs, int offset, int length) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            methodInstrumentor.beforeMethodCall();
            ((OverlayPolicyMarketDataExpectedOpeningPriceStructListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, expectedOpeningPriceStructs, offset, length);
            methodInstrumentor.incCalls(1);
            methodInstrumentor.afterMethodCall();
        }
    }

    public void acceptMarketDataExpectedOpeningPrice(ExpectedOpeningPriceStruct expectedOpeningPriceStruct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            methodInstrumentor.beforeMethodCall();
            ((OverlayPolicyMarketDataExpectedOpeningPriceStructListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, expectedOpeningPriceStruct);
            methodInstrumentor.incCalls(1);
            methodInstrumentor.afterMethodCall();
        }
    }

    public String toString()
    {
        String mdConsumer = cfixFixMarketDataConsumer.toString();
        String mdReqId = cfixOverlayPolicyMarketDataList.getMdReqID();
        StringBuilder result = new StringBuilder(mdConsumer.length()+mdReqId.length()+70);
        result.append("CfixMarketDataConsumerHolderInstrumented[consumer(").append(mdConsumer)
              .append(") MDReqID(").append(mdReqId ).append(")]");
        return result.toString();
    }

    public boolean equals(Object object)
    {
        if (object == this)
        {
            return true;
        }

        if (!(object instanceof CfixMarketDataConsumerHolderInstrumentedImpl) || object == null)
        {
            return false;
        }

        final CfixMarketDataConsumerHolderInstrumentedImpl other = (CfixMarketDataConsumerHolderInstrumentedImpl) object;

        return cfixFixMarketDataConsumer.equals(other.cfixFixMarketDataConsumer) &&
               cfixOverlayPolicyMarketDataList.equals(other.cfixOverlayPolicyMarketDataList);
    }

    public int hashCode()
    {
        return hashCode;
    }
}
