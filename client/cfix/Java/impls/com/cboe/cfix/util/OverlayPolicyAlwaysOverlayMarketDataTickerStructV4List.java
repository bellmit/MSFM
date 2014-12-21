package com.cboe.cfix.util;

import com.cboe.client.util.CollectionHelper;
import com.cboe.interfaces.cfix.CfixMarketDataConsumer;
import com.cboe.interfaces.cfix.OverlayPolicyMarketDataHolderIF;
import com.cboe.interfaces.cfix.CfixMarketDataDispatcherIF;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiMarketData.TickerStructV4;

/**
 * Created by IntelliJ IDEA.
 * User: Beniwalv
 * Date: Aug 2, 2010
 * Time: 11:26:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class OverlayPolicyAlwaysOverlayMarketDataTickerStructV4List extends OverlayPolicyAlwaysOverlayMarketDataStructList implements OverlayPolicyMarketDataTickerStructV4ListIF
{
    protected TickerStructV4[] structs  = CollectionHelper.EMPTY_TickerStructV4_ARRAY;

    public OverlayPolicyAlwaysOverlayMarketDataTickerStructV4List(String mdReqID)
    {
        super(mdReqID);
    }

    public OverlayPolicyAlwaysOverlayMarketDataTickerStructV4List(String mdReqID, int capacity)
    {
        super(mdReqID);

        this.structs = new TickerStructV4[capacity];
    }

    public void add(CfixMarketDataConsumer cfixMarketDataConsumer, TickerStructV4[] structs, int offset, int length) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        boolean         shouldAdd;
        TickerStructV4  struct;
        TickerStructV4  queuedStruct;
        int             queuedIndex;
        int             index;

        synchronized(this)
        {
            if (size == 0)
            {
                if (length > this.structs.length)
                {
                    this.structs = new TickerStructV4[length];
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

        cfixMarketDataConsumer.acceptMarketDataTicker(this);
    }

    public void add(CfixMarketDataConsumer cfixMarketDataConsumer, TickerStructV4 struct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        synchronized(this)
        {
            if (size == 0)
            {
                if (this.structs.length < 1)
                {
                    this.structs = new TickerStructV4[1];
                }

                this.structs[size] = struct;

                size++;
            }
            else
            {
                TickerStructV4 queuedStruct;

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

        cfixMarketDataConsumer.acceptMarketDataTicker(this);
    }

    public void remove(OverlayPolicyMarketDataHolderIF cfixOverlayPolicyMarketDataHolder)
    {
        super.remove(cfixOverlayPolicyMarketDataHolder, CfixMarketDataDispatcherIF.MarketDataType_Ticker);

        synchronized(this)
        {
            cfixOverlayPolicyMarketDataHolder.setSize(size);

            cfixOverlayPolicyMarketDataHolder.setOverlaid(overlaid);

            if (cfixOverlayPolicyMarketDataHolder.getTickerStructsV4().length < size)
            {
                cfixOverlayPolicyMarketDataHolder.setTickerStructsV4(new TickerStructV4[size]);
            }

            System.arraycopy(this.structs, 0, cfixOverlayPolicyMarketDataHolder.getTickerStructsV4(), 0, size);

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

        if (!(other instanceof OverlayPolicyAlwaysOverlayMarketDataTickerStructV4List))
        {
            return false;
        }

        return mdReqID.compareTo(((OverlayPolicyAlwaysOverlayMarketDataTickerStructV4List) other).mdReqID) == 0;
    }
}
