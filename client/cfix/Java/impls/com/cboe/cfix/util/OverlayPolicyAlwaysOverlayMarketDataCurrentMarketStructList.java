package com.cboe.cfix.util;

/**
 * OverlayPolicyAlwaysOverlayMarketDataCurrentMarketStructList.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 *
 * List of mdReqIDs/CurrentMarketStructs, overlay by equality of CurrentMarketStructs's productKey
 *
 */

import com.cboe.client.util.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.interfaces.cfix.*;

public class OverlayPolicyAlwaysOverlayMarketDataCurrentMarketStructList extends OverlayPolicyAlwaysOverlayMarketDataStructList implements OverlayPolicyMarketDataCurrentMarketStructListIF
{
    protected CurrentMarketStruct[] structs  = CollectionHelper.EMPTY_CurrentMarketStruct_ARRAY;

    public OverlayPolicyAlwaysOverlayMarketDataCurrentMarketStructList(String mdReqID)
    {
        super(mdReqID);
    }

    public OverlayPolicyAlwaysOverlayMarketDataCurrentMarketStructList(String mdReqID, int capacity)
    {
        super(mdReqID);

        this.structs = new CurrentMarketStruct[capacity];
    }

    public void add(CfixMarketDataConsumer cfixMarketDataConsumer, CurrentMarketStruct[] structs, int offset, int length) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        boolean             shouldAdd;
        CurrentMarketStruct struct;
        CurrentMarketStruct queuedStruct;
        int                 queuedIndex;
        int                 index;

        synchronized(this)
        {
            if (size == 0)
            {
                if (this.structs.length < length)
                {
                    this.structs = new CurrentMarketStruct[length];
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

                        if (queuedStruct.productKeys.productKey == struct.productKeys.productKey)
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

    public void add(CfixMarketDataConsumer cfixMarketDataConsumer, CurrentMarketStruct struct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        synchronized(this)
        {
            if (size == 0)
            {
                if (this.structs.length < 1)
                {
                    this.structs = new CurrentMarketStruct[1];
                }

                this.structs[size] = struct;

                size++;
            }
            else
            {
                CurrentMarketStruct queuedStruct;

                for (int queuedIndex = 0; queuedIndex < size; queuedIndex++)
                {
                    queuedStruct = this.structs[queuedIndex];

                    if (queuedStruct.productKeys.productKey == struct.productKeys.productKey)
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

            if (cfixOverlayPolicyMarketDataHolder.getCurrentMarketStructs().length < size)
            {
                cfixOverlayPolicyMarketDataHolder.setCurrentMarketStructs(new CurrentMarketStruct[size]);
            }

            System.arraycopy(this.structs, 0, cfixOverlayPolicyMarketDataHolder.getCurrentMarketStructs(), 0, size);

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

        if (!(other instanceof OverlayPolicyAlwaysOverlayMarketDataCurrentMarketStructList))
        {
            return false;
        }

        return mdReqID.compareTo(((OverlayPolicyAlwaysOverlayMarketDataCurrentMarketStructList) other).mdReqID) == 0;
    }
}
