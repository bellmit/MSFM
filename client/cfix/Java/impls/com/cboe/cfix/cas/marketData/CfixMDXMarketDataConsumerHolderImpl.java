package com.cboe.cfix.cas.marketData;

import com.cboe.interfaces.cfix.CfixMarketDataConsumer;
import com.cboe.interfaces.cfix.OverlayPolicyMarketDataListIF;
import com.cboe.interfaces.cfix.CfixMDXMarketDataConsumerHolder;
import com.cboe.interfaces.domain.RecapContainerV4IF;
import com.cboe.idl.cmiSession.SessionProductStruct;
import com.cboe.idl.cmiSession.SessionClassStruct;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.exceptions.*;
import com.cboe.cfix.util.*;
import com.cboe.domain.util.RecapContainerV4;

/**
 * @author Vivek Beniwal
 */
public class CfixMDXMarketDataConsumerHolderImpl  extends CfixMarketDataConsumerHolderImpl implements CfixMDXMarketDataConsumerHolder
{

    public CfixMDXMarketDataConsumerHolderImpl(CfixMarketDataConsumer cfixFixMarketDataConsumer, OverlayPolicyMarketDataListIF cfixOverlayPolicyMarketDataList, SessionProductStruct sessionProductStruct)
    {
        super(cfixFixMarketDataConsumer, cfixOverlayPolicyMarketDataList, sessionProductStruct);
    }

    public CfixMDXMarketDataConsumerHolderImpl(CfixMarketDataConsumer cfixFixMarketDataConsumer, OverlayPolicyMarketDataListIF cfixOverlayPolicyMarketDataList, SessionClassStruct sessionClassStruct)
    {
        super(cfixFixMarketDataConsumer, cfixOverlayPolicyMarketDataList, sessionClassStruct);
    }

    public void acceptMarketDataCurrentMarket(CurrentMarketStructV4[] currentMarketStructsV4) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            ((OverlayPolicyMarketDataCurrentMarketStructV4ListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, currentMarketStructsV4, 0, currentMarketStructsV4.length);
        }
    }

    public void acceptMarketDataCurrentMarket(CurrentMarketStructV4[] currentMarketStructsV4, int offset, int length) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            ((OverlayPolicyMarketDataCurrentMarketStructV4ListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, currentMarketStructsV4, offset, length);
        }
    }

    public void acceptMarketDataCurrentMarket(CurrentMarketStructV4 currentMarketStructV4) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            ((OverlayPolicyMarketDataCurrentMarketStructV4ListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, currentMarketStructV4);
        }
    }

    public void acceptMarketDataTicker(TickerStructV4[] tickerStructsV4) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            ((OverlayPolicyMarketDataTickerStructV4ListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, tickerStructsV4, 0, tickerStructsV4.length);
        }
    }

    public void acceptMarketDataTicker(TickerStructV4[] tickerStructsV4, int offset, int length) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            ((OverlayPolicyMarketDataTickerStructV4ListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, tickerStructsV4, offset, length);
        }
    }

    public void acceptMarketDataTicker(TickerStructV4 tickerStructV4) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            ((OverlayPolicyMarketDataTickerStructV4ListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, tickerStructV4);
        }
    }

    public void acceptMarketDataRecap(RecapContainerV4IF[] recapContainersV4IF) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            ((OverlayPolicyMarketDataRecapContainerV4ListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, recapContainersV4IF, 0, recapContainersV4IF.length);
        }
    }

    public void acceptMarketDataRecap(RecapContainerV4IF[] recapContainersV4IF, int offset, int length) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            ((OverlayPolicyMarketDataRecapContainerV4ListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, recapContainersV4IF, offset, length);
        }
    }

    public void acceptMarketDataRecap(RecapContainerV4IF recapContainerV4IF) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        if (cfixFixMarketDataConsumer.isAcceptingMarketData())
        {
            ((OverlayPolicyMarketDataRecapContainerV4ListIF) cfixOverlayPolicyMarketDataList).add(cfixFixMarketDataConsumer, recapContainerV4IF);
        }
    }


    public String toString()
    {
        return "CfixMDXMarketDataConsumerHolder[consumer(" + cfixFixMarketDataConsumer + ") MDReqID(" + cfixOverlayPolicyMarketDataList.getMdReqID() + ")]";
    }

    public boolean equals(Object object)
    {
        if (object == this)
        {
            return true;
        }

        if (!(object instanceof CfixMDXMarketDataConsumerHolderImpl) || object == null)
        {
            return false;
        }

        final CfixMDXMarketDataConsumerHolderImpl other = (CfixMDXMarketDataConsumerHolderImpl) object;

        return cfixFixMarketDataConsumer.equals(other.cfixFixMarketDataConsumer) &&
               cfixOverlayPolicyMarketDataList.equals(other.cfixOverlayPolicyMarketDataList);
    }

    public int hashCode()
    {
        return (cfixFixMarketDataConsumer.hashCode() + cfixOverlayPolicyMarketDataList.hashCode()) & Integer.MAX_VALUE;
    }
}
