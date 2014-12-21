package com.cboe.cfix.util;

import com.cboe.idl.cmiMarketData.CurrentMarketStructV4;
import com.cboe.client.util.CollectionHelper;
import com.cboe.interfaces.cfix.CfixMarketDataConsumer;
import com.cboe.interfaces.cfix.OverlayPolicyMarketDataHolderIF;
import com.cboe.interfaces.cfix.CfixMarketDataDispatcherIF;
import com.cboe.exceptions.*;

/**
 * User: Beniwalv
 * To change this template use File | Settings | File Templates.
 */
public class OverlayPolicyAlwaysOverlayMarketDataCurrentMarketStructV4List extends OverlayPolicyAlwaysOverlayMarketDataStructList implements OverlayPolicyMarketDataCurrentMarketStructV4ListIF
{
    protected CurrentMarketStructV4[] structs  = CollectionHelper.EMPTY_CurrentMarketStructV4_ARRAY;

    public OverlayPolicyAlwaysOverlayMarketDataCurrentMarketStructV4List(String mdReqID)
    {
        super(mdReqID);
    }

    public OverlayPolicyAlwaysOverlayMarketDataCurrentMarketStructV4List(String mdReqID, int capacity)
    {
        super(mdReqID);

        this.structs = new CurrentMarketStructV4[capacity];
    }

    public void add(CfixMarketDataConsumer cfixMarketDataConsumer, CurrentMarketStructV4[] structs, int offset, int length) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        boolean                 shouldAdd;
        CurrentMarketStructV4   struct;
        CurrentMarketStructV4   queuedStruct;
        int                     queuedIndex;
        int                     index;

        synchronized(this)
        {
            if (size == 0)
            {
                if (this.structs.length < length)
                {
                    this.structs = new CurrentMarketStructV4[length];
                }

                System.arraycopy(structs, offset, this.structs, 0, length);

                size += length;
            }
            else
            {
                for (index = offset; index < length; index++)
                {
                    struct = structs[index];

                    shouldAdd = true;

                    for (queuedIndex = 0; queuedIndex < size; queuedIndex++)
                    {
                        queuedStruct = this.structs[queuedIndex];

                        if (queuedStruct.productKey == struct.productKey)
                        {
                            this.structs[queuedIndex] = struct;
                            shouldAdd = false;
                            overlaid.set(queuedIndex);
                            break;
                        }
                    }

                    if (shouldAdd)
                    {
                        if (size == this.structs.length)
                        {
                            this.structs = CollectionHelper.arrayclone(this.structs, 0, size, this.structs.length << 1);
                        }

                        this.structs[size] = struct;

                        size++;
                    }
                }
            }
        }

        cfixMarketDataConsumer.acceptMarketDataCurrentMarket(this);
    }

    public void add(CfixMarketDataConsumer cfixMarketDataConsumer, CurrentMarketStructV4 struct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        synchronized(this)
        {
            if (size == 0)
            {
                if (this.structs.length < 1)
                {
                    this.structs = new CurrentMarketStructV4[1];
                }

                this.structs[size] = struct;

                size++;
            }
            else
            {
                CurrentMarketStructV4 queuedStruct;

                for (int queuedIndex = 0; queuedIndex < size; queuedIndex++)
                {
                    queuedStruct = this.structs[queuedIndex];

                    if (queuedStruct.productKey == struct.productKey)
                    {
                        this.structs[queuedIndex] = struct;
                        overlaid.set(queuedIndex);
                        return;
                    }
                }

                if (size == this.structs.length)
                {
                    this.structs = CollectionHelper.arrayclone(this.structs, 0, size, this.structs.length << 1);
                }

                this.structs[size] = struct;

                size++;
            }
        }

        cfixMarketDataConsumer.acceptMarketDataCurrentMarket(this);
    }

    public void remove(OverlayPolicyMarketDataHolderIF cfixOverlayPolicyMarketDataHolder)
    {
        super.remove(cfixOverlayPolicyMarketDataHolder, CfixMarketDataDispatcherIF.MarketDataType_CurrentMarket);

        synchronized(this)
        {
            cfixOverlayPolicyMarketDataHolder.setSize(size);

            cfixOverlayPolicyMarketDataHolder.setOverlaid(overlaid);

            if (cfixOverlayPolicyMarketDataHolder.getCurrentMarketStructsV4().length < size)
            {
                cfixOverlayPolicyMarketDataHolder.setCurrentMarketStructsV4(new CurrentMarketStructV4[size]);
            }

            System.arraycopy(this.structs, 0, cfixOverlayPolicyMarketDataHolder.getCurrentMarketStructsV4(), 0, size);

            while (size-- > 0)
            {
                this.structs[size] = null;
            }

            size = 0;

            overlaid.clear();
        }
    }

    public void clear()
    {
        synchronized(this)
        {
            while (size-- > 0)
            {
                this.structs[size] = null;
            }

            size = 0;

            overlaid.clear();
        }
    }

    public int capacity()
    {
        return this.structs.length;
    }

    public boolean equals(Object other)
    {
        if (this == other)
        {
            return true;
        }

        if (!(other instanceof OverlayPolicyAlwaysOverlayMarketDataCurrentMarketStructV4List))
        {
            return false;
        }

        return mdReqID.compareTo(((OverlayPolicyAlwaysOverlayMarketDataCurrentMarketStructV4List) other).mdReqID) == 0;
    }

}
