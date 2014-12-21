package com.cboe.cfix.cas.marketData;

/**
 * CfixMarketDataCurrentMarketDispatcherImpl.java
 *
 * @author Dmitry Volpyansky
 *
 */

/*
 *  This object registers for and receives all CurrentMarket market data traffic for a specific session, and then forwards it to all listeners<br>
 *
 *  It contains the subscription maps for all listeners interested in receiving CurrentMarket subscriptions for this session<br>
 *
 */

import com.cboe.exceptions.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.interfaces.cfix.*;
import com.cboe.interfaces.domain.CurrentMarketContainer;
import com.cboe.util.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;


public final class CfixMarketDataCurrentMarketDispatcherImpl extends CfixMarketDataDispatcherImpl
{
    public static final String MARKET_DATA_TYPE_NAME = "CurrentMarket";

    public CfixMarketDataCurrentMarketDispatcherImpl(String name)
    {
        super(name);
    }

    public int getHandledChannelType()
    {
        return ChannelType.CURRENT_MARKET_BY_CLASS;
    }

    public int getHandledMarketDataType()
    {
        return CfixMarketDataDispatcherIF.MarketDataType_CurrentMarket;
    }

    public String getHandledMarketDataTypeName()
    {
        return MARKET_DATA_TYPE_NAME;
    }

    protected void acceptMarketData(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder, Object struct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        //if (Log.isDebugOn())
        //{
        //    Log.debug("CMDispatcherImpl -> acceptMarketData0, received currentMarketContainer");
        //}
        CurrentMarketContainer currentMarketContainer = (CurrentMarketContainer) struct;

        CurrentMarketStruct[] bestPublicMarketsAtTop  = currentMarketContainer.getBestPublicMarketsAtTop();

        if (bestPublicMarketsAtTop.length == 0)
        {
            //if (Log.isDebugOn())
            //{
            //    Log.debug("CMDispatcherImpl -> acceptMarketData0, received currentMarketContainer, empty bestPublicMarketsAtTop ");
            //}
            cfixFixMarketDataConsumerHolder.acceptMarketDataCurrentMarket(currentMarketContainer.getBestMarkets());

            return;
        }

        repackageCurrentMarketContainer(cfixFixMarketDataConsumerHolder, currentMarketContainer);
    }

    protected void acceptMarketData(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder, Object[] structs) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        //if (Log.isDebugOn())
        //{
        //    Log.debug("CMDispatcherImpl -> acceptMarketData1, received currentMarketContainer, size="+structs.length);
        //}
        CurrentMarketStruct[]    bestPublicMarketsAtTop;

        for (int i = 0; i < structs.length; i++)
        {

            bestPublicMarketsAtTop = ((CurrentMarketContainer)structs[i]).getBestPublicMarketsAtTop();

            if (bestPublicMarketsAtTop.length == 0)
            {
                //if (Log.isDebugOn())
                //{
                //    Log.debug("CMDispatcherImpl -> acceptMarketData1, received currentMarketContainer, empty bestPublicMarketsAtTop ");
                //}

                cfixFixMarketDataConsumerHolder.acceptMarketDataCurrentMarket(((CurrentMarketContainer)structs[i]).getBestMarkets());

                continue;
            }

            repackageCurrentMarketContainer(cfixFixMarketDataConsumerHolder, ((CurrentMarketContainer)structs[i]));
        }
    }

    protected int getClassKey(Object struct)
    {
        if (struct instanceof CurrentMarketStruct)
        {
            return ((CurrentMarketStruct) struct).productKeys.classKey;
        }

        CurrentMarketStruct[] bestMarket = ((CurrentMarketContainer) struct).getBestMarkets();

        return bestMarket[0].productKeys.classKey;
    }

    protected int getProductKey(Object struct)
    {
        if (struct instanceof CurrentMarketStruct)
        {
            return ((CurrentMarketStruct) struct).productKeys.productKey;
        }

        CurrentMarketStruct[] bestMarket = ((CurrentMarketContainer) struct).getBestMarkets();

        return bestMarket[0].productKeys.productKey;
    }

    protected void repackageCurrentMarketContainer(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder, CurrentMarketContainer currentMarketContainer) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        CurrentMarketStruct[] bestMarket                   = currentMarketContainer.getBestMarkets();
        CurrentMarketStruct[] bestPublicMarketsAtTop       = currentMarketContainer.getBestPublicMarketsAtTop();

        CurrentMarketStruct[] newBestMarketArray           = new CurrentMarketStruct[bestMarket.length];
        int                   bestPublicMarketsAtTopLength = bestPublicMarketsAtTop.length;
        CurrentMarketStruct   newBestMarket;
        CurrentMarketStruct   oldBestMarket;
        CurrentMarketStruct   oldBestPublicMarketsAtTop;
        int                   j;
        int                   k;

        for (int i = 0; i < newBestMarketArray.length; i++)
        {
            oldBestMarket = bestMarket[i];

            for (j = 0; j < bestPublicMarketsAtTopLength; j++)
            {
                if (oldBestMarket.productKeys.productKey == bestPublicMarketsAtTop[j].productKeys.productKey)
                {
                    break;
                }
            }

            if (j == bestPublicMarketsAtTop.length) // does not need to be modified
            {
                newBestMarketArray[i] = oldBestMarket;

                continue;
            }

            newBestMarket                 = new CurrentMarketStruct();
            newBestMarketArray[i]         = newBestMarket;
            oldBestPublicMarketsAtTop     = bestPublicMarketsAtTop[j];

            newBestMarket.productKeys     = oldBestMarket.productKeys;
            newBestMarket.sessionName     = oldBestMarket.sessionName;
            newBestMarket.exchange        = oldBestMarket.exchange;
            newBestMarket.bidPrice        = oldBestMarket.bidPrice;
            newBestMarket.bidIsMarketBest = oldBestMarket.bidIsMarketBest;
            newBestMarket.askPrice        = oldBestMarket.askPrice;
            newBestMarket.askIsMarketBest = oldBestMarket.askIsMarketBest;
            newBestMarket.sentTime        = oldBestMarket.sentTime;
            newBestMarket.legalMarket     = oldBestMarket.legalMarket;

            j = 0;
            newBestMarket.bidSizeSequence = new MarketVolumeStruct[oldBestMarket.bidSizeSequence.length + oldBestPublicMarketsAtTop.bidSizeSequence.length];
            for (k = 0; k < oldBestMarket.bidSizeSequence.length; k++, j++)
            {
                newBestMarket.bidSizeSequence[j] = oldBestMarket.bidSizeSequence[k];
            }
            for (k = 0; k < oldBestPublicMarketsAtTop.bidSizeSequence.length; k++, j++)
            {
                newBestMarket.bidSizeSequence[j] = oldBestPublicMarketsAtTop.bidSizeSequence[k];
            }

            j = 0;
            newBestMarket.askSizeSequence = new MarketVolumeStruct[oldBestMarket.askSizeSequence.length + oldBestPublicMarketsAtTop.askSizeSequence.length];
            for (k = 0; k < oldBestMarket.askSizeSequence.length; k++, j++)
            {
                newBestMarket.askSizeSequence[j] = oldBestMarket.askSizeSequence[k];
            }
            for (k = 0; k < oldBestPublicMarketsAtTop.askSizeSequence.length; k++, j++)
            {
                newBestMarket.askSizeSequence[j] = oldBestPublicMarketsAtTop.askSizeSequence[k];
            }
        }

        cfixFixMarketDataConsumerHolder.acceptMarketDataCurrentMarket(newBestMarketArray);
    }
}
