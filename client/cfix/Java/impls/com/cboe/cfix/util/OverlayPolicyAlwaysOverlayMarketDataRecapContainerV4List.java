package com.cboe.cfix.util;

import com.cboe.client.util.CollectionHelper;
import com.cboe.interfaces.cfix.CfixMarketDataConsumer;
import com.cboe.interfaces.cfix.OverlayPolicyMarketDataHolderIF;
import com.cboe.interfaces.cfix.CfixMarketDataDispatcherIF;
import com.cboe.interfaces.domain.RecapContainerV4IF;
import com.cboe.exceptions.*;
import com.cboe.domain.util.RecapContainerV4;

/**
 * User: beniwalv
 */
public class OverlayPolicyAlwaysOverlayMarketDataRecapContainerV4List extends OverlayPolicyAlwaysOverlayMarketDataStructList implements OverlayPolicyMarketDataRecapContainerV4ListIF
{
    protected RecapContainerV4IF[] recapContainersV4  = CollectionHelper.EMPTY_RecapContainerV4_ARRAY;

    public OverlayPolicyAlwaysOverlayMarketDataRecapContainerV4List(String mdReqID)
    {
        super(mdReqID);
    }

    public OverlayPolicyAlwaysOverlayMarketDataRecapContainerV4List(String mdReqID, int capacity)
    {
        super(mdReqID);

        this.recapContainersV4 = new RecapContainerV4[capacity];
    }

    public void add(CfixMarketDataConsumer cfixMarketDataConsumer, RecapContainerV4IF[] recapContainersV4IF, int offset, int length) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        boolean                 shouldAdd;
        RecapContainerV4IF      recapContainerV4IF;
        RecapContainerV4IF      queuedRecapContainerV4IF;
        int                     queuedIndex;
        int                     index;

        synchronized(this)
        {
            if (size == 0)
            {
                if (length > this.recapContainersV4.length)
                {
                    this.recapContainersV4 = new RecapContainerV4[length];
                }

                System.arraycopy(recapContainersV4IF, offset, this.recapContainersV4, 0, length);

                size += length;
            }
            else
            {
                for (index = offset; index < length; index++)
                {
                    recapContainerV4IF = recapContainersV4IF[index];

                    shouldAdd = true;

                    for (queuedIndex = 0; queuedIndex < size; queuedIndex++)
                    {
                        queuedRecapContainerV4IF = this.recapContainersV4[queuedIndex];

                        if (queuedRecapContainerV4IF.getProductKey() == recapContainerV4IF.getProductKey())
                        {
                            this.recapContainersV4[queuedIndex] = recapContainerV4IF;
                            shouldAdd = false;
                            overlaid.set(queuedIndex);
                            break;
                        }
                    }

                    if (shouldAdd)
                    {
                        if (size == this.recapContainersV4.length)
                        {
                            this.recapContainersV4 = CollectionHelper.arrayclone(this.recapContainersV4, 0, size, this.recapContainersV4.length << 1);
                        }

                        this.recapContainersV4[size] = recapContainerV4IF;

                        size++;
                    }
                }
            }
        }

        cfixMarketDataConsumer.acceptMarketDataRecap(this);
    }

    public void add(CfixMarketDataConsumer cfixMarketDataConsumer, RecapContainerV4IF recapContainerV4IF) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        synchronized(this)
        {
            if (size == 0)
            {
                if (this.recapContainersV4.length < 1)
                {
                    this.recapContainersV4 = new RecapContainerV4[1];
                }

                this.recapContainersV4[size] = recapContainerV4IF;

                size++;
            }
            else
            {
                RecapContainerV4IF queuedRecapContainerV4IF;

                for (int queuedIndex = 0; queuedIndex < size; queuedIndex++)
                {
                    queuedRecapContainerV4IF = this.recapContainersV4[queuedIndex];

                    if (queuedRecapContainerV4IF.getProductKey() == recapContainerV4IF.getProductKey())
                    {
                        this.recapContainersV4[queuedIndex] = recapContainerV4IF;
                        overlaid.set(queuedIndex);
                        return;
                    }
                }

                if (size == this.recapContainersV4.length)
                {
                    this.recapContainersV4 = CollectionHelper.arrayclone(this.recapContainersV4, 0, size, this.recapContainersV4.length << 1);
                }

                this.recapContainersV4[size] = recapContainerV4IF;

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

            if (cfixOverlayPolicyMarketDataHolder.getRecapContainersV4().length < size)
            {
                cfixOverlayPolicyMarketDataHolder.setRecapContainersV4(new RecapContainerV4[size]);
            }

            System.arraycopy(this.recapContainersV4, 0, cfixOverlayPolicyMarketDataHolder.getRecapContainersV4(), 0, size);

            while (size-- > 0)
            {
                this.recapContainersV4[size] = null;
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
                this.recapContainersV4[size] = null;
            }

            size = 0;

            overlaid.clear();
        }
    }

    public int capacity()
    {
        return this.recapContainersV4.length;
    }

    public boolean equals(Object other)
    {
        if (this == other)
        {
            return true;
        }

        if (!(other instanceof OverlayPolicyAlwaysOverlayMarketDataRecapContainerV4List))
        {
            return false;
        }

        return mdReqID.compareTo(((OverlayPolicyAlwaysOverlayMarketDataRecapContainerV4List) other).mdReqID) == 0;
    }
}

