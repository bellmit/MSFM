package com.cboe.cfix.util;

import com.cboe.idl.cmiMarketData.RecapStructV4;
import com.cboe.client.util.CollectionHelper;
import com.cboe.interfaces.cfix.CfixMarketDataConsumer;
import com.cboe.interfaces.cfix.OverlayPolicyMarketDataHolderIF;
import com.cboe.interfaces.cfix.CfixMarketDataDispatcherIF;
import com.cboe.exceptions.*;

/**
 * Created by IntelliJ IDEA.
 * User: Beniwalv
 * Date: Aug 2, 2010
 * Time: 10:59:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class OverlayPolicyAlwaysOverlayMarketDataRecapStructV4List extends OverlayPolicyAlwaysOverlayMarketDataStructList implements OverlayPolicyMarketDataRecapStructV4ListIF
{
    protected RecapStructV4[] structs  = CollectionHelper.EMPTY_RecapStructV4_ARRAY;

    public OverlayPolicyAlwaysOverlayMarketDataRecapStructV4List(String mdReqID)
    {
        super(mdReqID);
    }

    public OverlayPolicyAlwaysOverlayMarketDataRecapStructV4List(String mdReqID, int capacity)
    {
        super(mdReqID);

        this.structs = new RecapStructV4[capacity];
    }

    public void add(CfixMarketDataConsumer cfixMarketDataConsumer, RecapStructV4[] structs, int offset, int length) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        boolean         shouldAdd;
        RecapStructV4   struct;
        RecapStructV4   queuedStruct;
        int             queuedIndex;
        int             index;

        synchronized(this)
        {
            if (size == 0)
            {
                if (length > this.structs.length)
                {
                    this.structs = new RecapStructV4[length];
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

        cfixMarketDataConsumer.acceptMarketDataRecap(this);
    }

    public void add(CfixMarketDataConsumer cfixMarketDataConsumer, RecapStructV4 struct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        synchronized(this)
        {
            if (size == 0)
            {
                if (this.structs.length < 1)
                {
                    this.structs = new RecapStructV4[1];
                }

                this.structs[size] = struct;

                size++;
            }
            else
            {
                RecapStructV4 queuedStruct;

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

        cfixMarketDataConsumer.acceptMarketDataRecap(this);
    }

    public void remove(OverlayPolicyMarketDataHolderIF cfixOverlayPolicyMarketDataHolder)
    {
        super.remove(cfixOverlayPolicyMarketDataHolder, CfixMarketDataDispatcherIF.MarketDataType_Recap);

        synchronized(this)
        {
            cfixOverlayPolicyMarketDataHolder.setSize(size);

            cfixOverlayPolicyMarketDataHolder.setOverlaid(overlaid);

            if (cfixOverlayPolicyMarketDataHolder.getRecapStructsV4().length < size)
            {
                cfixOverlayPolicyMarketDataHolder.setRecapStructsV4(new RecapStructV4[size]);
            }

            System.arraycopy(this.structs, 0, cfixOverlayPolicyMarketDataHolder.getRecapStructsV4(), 0, size);

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

        if (!(other instanceof OverlayPolicyAlwaysOverlayMarketDataRecapStructV4List))
        {
            return false;
        }

        return mdReqID.compareTo(((OverlayPolicyAlwaysOverlayMarketDataRecapStructV4List) other).mdReqID) == 0;
    }
}
