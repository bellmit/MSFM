package com.cboe.cfix.util;

/**
 * OverlayPolicyAlwaysOverlayMarketDataExpectedOpeningPriceStructList.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 *
 * List of mdReqIDs/ExpectedOpeningPriceStructs, overlay by equality of ExpectedOpeningPriceStructs's productKey
 *
 */

import com.cboe.client.util.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.interfaces.cfix.*;

public class OverlayPolicyAlwaysOverlayMarketDataExpectedOpeningPriceStructList extends OverlayPolicyAlwaysOverlayMarketDataStructList implements OverlayPolicyMarketDataExpectedOpeningPriceStructListIF
{
    protected ExpectedOpeningPriceStruct[] structs  = CollectionHelper.EMPTY_ExpectedOpeningPriceStruct_ARRAY;

    public OverlayPolicyAlwaysOverlayMarketDataExpectedOpeningPriceStructList(String mdReqID)
    {
        super(mdReqID);
    }

    public OverlayPolicyAlwaysOverlayMarketDataExpectedOpeningPriceStructList(String mdReqID, int capacity)
    {
        super(mdReqID);

        this.structs = new ExpectedOpeningPriceStruct[capacity];
    }

    public void add(CfixMarketDataConsumer cfixMarketDataConsumer, ExpectedOpeningPriceStruct[] structs, int offset, int length) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        boolean                    shouldAdd;
        ExpectedOpeningPriceStruct struct;
        ExpectedOpeningPriceStruct queuedStruct;
        int                        queuedIndex;
        int                        index;

        synchronized(this)
        {
            if (size == 0)
            {
                if (length > this.structs.length)
                {
                    this.structs = new ExpectedOpeningPriceStruct[length];
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

        cfixMarketDataConsumer.acceptMarketDataExpectedOpeningPrice(this);
    }

    public void add(CfixMarketDataConsumer cfixMarketDataConsumer, ExpectedOpeningPriceStruct struct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        synchronized(this)
        {
            if (size == 0)
            {
                if (this.structs.length < 1)
                {
                    this.structs = new ExpectedOpeningPriceStruct[1];
                }

                this.structs[size] = struct;

                size++;
            }
            else
            {
                ExpectedOpeningPriceStruct queuedStruct;

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

        cfixMarketDataConsumer.acceptMarketDataExpectedOpeningPrice(this);
    }

    public void remove(OverlayPolicyMarketDataHolderIF cfixOverlayPolicyMarketDataHolder)
    {
        super.remove(cfixOverlayPolicyMarketDataHolder, CfixMarketDataDispatcherIF.MarketDataType_ExpectedOpeningPrice);

        synchronized(this)
        {
            cfixOverlayPolicyMarketDataHolder.setSize(size);

            cfixOverlayPolicyMarketDataHolder.setOverlaid(overlaid);

            if (cfixOverlayPolicyMarketDataHolder.getExpectedOpeningPriceStructs().length < size)
            {
                cfixOverlayPolicyMarketDataHolder.setExpectedOpeningPriceStructs(new ExpectedOpeningPriceStruct[size]);
            }

            System.arraycopy(this.structs, 0, cfixOverlayPolicyMarketDataHolder.getExpectedOpeningPriceStructs(), 0, size);

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

        if (!(other instanceof OverlayPolicyAlwaysOverlayMarketDataExpectedOpeningPriceStructList))
        {
            return false;
        }

        return mdReqID.compareTo(((OverlayPolicyAlwaysOverlayMarketDataExpectedOpeningPriceStructList) other).mdReqID) == 0;
    }
}
