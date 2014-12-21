package com.cboe.cfix.cas.marketData;

import com.cboe.interfaces.cfix.*;
import com.cboe.interfaces.domain.RecapContainerV4IF;
import com.cboe.idl.cmiSession.SessionProductStruct;
import com.cboe.idl.cmiSession.SessionClassStruct;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;
import com.cboe.exceptions.*;
import com.cboe.cfix.util.*;
import com.cboe.domain.util.RecapContainerV4;

/**
 * @author Vivek Beniwal
 */
public class CfixMDXMarketDataConsumerHolderInstrumentedImpl extends CfixMarketDataConsumerHolderInstrumentedImpl implements CfixMDXMarketDataConsumerHolder
{
    public CfixMDXMarketDataConsumerHolderInstrumentedImpl(CfixMarketDataConsumer cfixFixMarketDataConsumer, OverlayPolicyMarketDataListIF cfixOverlayPolicyMarketDataList, SessionProductStruct sessionProductStruct, MethodInstrumentor methodInstrumentor)
    {
        super(cfixFixMarketDataConsumer, cfixOverlayPolicyMarketDataList, sessionProductStruct, methodInstrumentor);
    }

    public CfixMDXMarketDataConsumerHolderInstrumentedImpl(CfixMarketDataConsumer cfixFixMarketDataConsumer, OverlayPolicyMarketDataListIF cfixOverlayPolicyMarketDataList, SessionClassStruct sessionClassStruct, MethodInstrumentor methodInstrumentor)
    {
        super(cfixFixMarketDataConsumer, cfixOverlayPolicyMarketDataList, sessionClassStruct, methodInstrumentor);
    }

    public void acceptMarketDataCurrentMarket(CurrentMarketStructV4[] currentMarketStructsV4) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            methodInstrumentor.beforeMethodCall();
            ((OverlayPolicyMarketDataCurrentMarketStructV4ListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, currentMarketStructsV4, 0, currentMarketStructsV4.length);
            methodInstrumentor.incCalls(1);
            methodInstrumentor.afterMethodCall();
        }
    }

    public void acceptMarketDataCurrentMarket(CurrentMarketStructV4[] currentMarketStructsV4, int offset, int length) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            methodInstrumentor.beforeMethodCall();
            ((OverlayPolicyMarketDataCurrentMarketStructV4ListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, currentMarketStructsV4, offset, length);
            methodInstrumentor.incCalls(1);
            methodInstrumentor.afterMethodCall();
        }
    }

    public void acceptMarketDataCurrentMarket(CurrentMarketStructV4 currentMarketStructV4) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            methodInstrumentor.beforeMethodCall();
            ((OverlayPolicyMarketDataCurrentMarketStructV4ListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, currentMarketStructV4);
            methodInstrumentor.incCalls(1);
            methodInstrumentor.afterMethodCall();
        }
    }

    public void acceptMarketDataTicker(TickerStructV4[] tickerStructsV4) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            methodInstrumentor.beforeMethodCall();
            ((OverlayPolicyMarketDataTickerStructV4ListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, tickerStructsV4, 0, tickerStructsV4.length);
            methodInstrumentor.incCalls(1);
            methodInstrumentor.afterMethodCall();
        }
    }

    public void acceptMarketDataTicker(TickerStructV4[] tickerStructsV4, int offset, int length) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            methodInstrumentor.beforeMethodCall();
            ((OverlayPolicyMarketDataTickerStructV4ListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, tickerStructsV4, offset, length);
            methodInstrumentor.incCalls(1);
            methodInstrumentor.afterMethodCall();
        }
    }

    public void acceptMarketDataTicker(TickerStructV4 tickerStructV4) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            methodInstrumentor.beforeMethodCall();
            ((OverlayPolicyMarketDataTickerStructV4ListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, tickerStructV4);
            methodInstrumentor.incCalls(1);
            methodInstrumentor.afterMethodCall();
        }
    }

    public void acceptMarketDataRecap(RecapContainerV4IF[] recapContainersV4IF) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            methodInstrumentor.beforeMethodCall();
            ((OverlayPolicyMarketDataRecapContainerV4ListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, recapContainersV4IF, 0, recapContainersV4IF.length);
            methodInstrumentor.incCalls(1);
            methodInstrumentor.afterMethodCall();
        }
    }

    public void acceptMarketDataRecap(RecapContainerV4IF[] recapContainersV4IF, int offset, int length) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            methodInstrumentor.beforeMethodCall();
            ((OverlayPolicyMarketDataRecapContainerV4ListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, recapContainersV4IF, offset, length);
            methodInstrumentor.incCalls(1);
            methodInstrumentor.afterMethodCall();
        }
    }

    public void acceptMarketDataRecap(RecapContainerV4IF recapContainerV4IF) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            methodInstrumentor.beforeMethodCall();
            ((OverlayPolicyMarketDataRecapContainerV4ListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, recapContainerV4IF);
            methodInstrumentor.incCalls(1);
            methodInstrumentor.afterMethodCall();
        }
    }

    public String toString()
    {
        return "CfixMDXMarketDataConsumerHolderInstrumented[consumer(" + cfixFixMarketDataConsumer + ") MDReqID(" + cfixOverlayPolicyMarketDataList.getMdReqID() + ")]";
    }

    public boolean equals(Object object)
    {
        if (object == this)
        {
            return true;
        }

        if (!(object instanceof CfixMDXMarketDataConsumerHolderInstrumentedImpl) || object == null)
        {
            return false;
        }

        final CfixMDXMarketDataConsumerHolderInstrumentedImpl other = (CfixMDXMarketDataConsumerHolderInstrumentedImpl) object;

        return cfixFixMarketDataConsumer.equals(other.cfixFixMarketDataConsumer) &&
               cfixOverlayPolicyMarketDataList.equals(other.cfixOverlayPolicyMarketDataList);
    }

    public int hashCode()
    {
        return (cfixFixMarketDataConsumer.hashCode() + cfixOverlayPolicyMarketDataList.hashCode()) & Integer.MAX_VALUE;
    }

}
