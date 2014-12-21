package com.cboe.cfix.util;

import com.cboe.client.util.CollectionHelper;
import com.cboe.interfaces.cfix.CfixMarketDataConsumer;
import com.cboe.interfaces.cfix.OverlayPolicyMarketDataHolderIF;
import com.cboe.interfaces.cfix.CfixMarketDataDispatcherIF;
import com.cboe.interfaces.domain.RecapContainerV4IF;
import com.cboe.exceptions.*;
import com.cboe.domain.util.RecapContainerV4;

/**
 * Created by IntelliJ IDEA.
 * User: beniwalv
 * To change this template use File | Settings | File Templates.
 */
public class OverlayPolicyNeverOverlayMarketDataRecapContainerV4List extends OverlayPolicyNeverOverlayMarketDataStructList implements OverlayPolicyMarketDataRecapContainerV4ListIF
{
    protected RecapContainerV4IF[] recapContainersV4 = CollectionHelper.EMPTY_RecapContainerV4_ARRAY;

    public OverlayPolicyNeverOverlayMarketDataRecapContainerV4List(String mdReqID)
    {
        super(mdReqID);
    }

    private OverlayPolicyNeverOverlayMarketDataRecapContainerV4List(String mdReqID, RecapContainerV4IF[] recapContainersV4IF, int offset, int length)
    {
        super(mdReqID);

        if (recapContainersV4IF.length != length)
        {
            this.recapContainersV4 = CollectionHelper.arrayclone(recapContainersV4IF, offset, length, length);
        }
        else
        {
            this.recapContainersV4 = recapContainersV4IF;
        }
    }

    private OverlayPolicyNeverOverlayMarketDataRecapContainerV4List(String mdReqID, RecapContainerV4IF recapContainerV4IF)
    {
        super(mdReqID);

        this.recapContainersV4 = CollectionHelper.arrayclone(recapContainerV4IF);
    }

    public void add(CfixMarketDataConsumer cfixMarketDataConsumer, RecapContainerV4IF[] recapContainersV4IF, int offset, int length) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        cfixMarketDataConsumer.acceptMarketDataRecap(new OverlayPolicyNeverOverlayMarketDataRecapContainerV4List(mdReqID, recapContainersV4IF, offset, length));
    }

    public void add(CfixMarketDataConsumer cfixMarketDataConsumer, RecapContainerV4IF recapContainerV4IF) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        cfixMarketDataConsumer.acceptMarketDataRecap(new OverlayPolicyNeverOverlayMarketDataRecapContainerV4List(mdReqID, recapContainerV4IF));
    }

    public void remove(OverlayPolicyMarketDataHolderIF cfixOverlayPolicyMarketDataHolder)
    {
        super.remove(cfixOverlayPolicyMarketDataHolder);

        cfixOverlayPolicyMarketDataHolder.setMarketDataType(CfixMarketDataDispatcherIF.MarketDataType_Recap);

        cfixOverlayPolicyMarketDataHolder.setSize(recapContainersV4.length);

        if (cfixOverlayPolicyMarketDataHolder.getRecapContainersV4().length < recapContainersV4.length)
        {
            cfixOverlayPolicyMarketDataHolder.setRecapContainersV4(new RecapContainerV4[recapContainersV4.length]);
        }

        System.arraycopy(this.recapContainersV4, 0, cfixOverlayPolicyMarketDataHolder.getRecapContainersV4(), 0, recapContainersV4.length);
    }

    public boolean equals(Object other)
    {
        return this == other &&
               other instanceof OverlayPolicyNeverOverlayMarketDataRecapContainerV4List &&
               mdReqID.compareTo(((OverlayPolicyNeverOverlayMarketDataRecapContainerV4List) other).mdReqID) == 0;
    }

    public int size()
    {
        return recapContainersV4.length;
    }
}
