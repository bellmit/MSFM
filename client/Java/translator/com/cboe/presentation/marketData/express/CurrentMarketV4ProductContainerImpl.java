//
// -----------------------------------------------------------------------------------
// Source file: CurrentMarketV4ProductContainerImpl.java
//
// PACKAGE: com.cboe.presentation.marketData.express
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.marketData.express;

import com.cboe.idl.cmiMarketData.CurrentMarketStructV4;

import com.cboe.interfaces.presentation.marketData.express.CurrentMarketV4ProductContainer;
import com.cboe.interfaces.presentation.marketData.express.CurrentMarketV4;

public class CurrentMarketV4ProductContainerImpl extends AbstractV4MarketData implements CurrentMarketV4ProductContainer
{
    private CurrentMarketV4 bestMarket;
    private CurrentMarketV4 bestPublicMarket;

    public CurrentMarketV4ProductContainerImpl()
    {
        this(-1);
    }

    public CurrentMarketV4ProductContainerImpl(int messageSequenceNumber)
    {
        super(messageSequenceNumber);
    }

    public CurrentMarketV4ProductContainerImpl(int messageSequenceNumber, CurrentMarketStructV4 bestMarket, CurrentMarketStructV4 bestPublicMarket)
    {
        this(messageSequenceNumber, new CurrentMarketV4Impl(bestMarket), new CurrentMarketV4Impl(bestPublicMarket));
    }

    public CurrentMarketV4ProductContainerImpl(int messageSequenceNumber, CurrentMarketV4 bestMarket, CurrentMarketV4 bestPublicMarket)
    {
        this(messageSequenceNumber);
        validate(bestMarket, bestPublicMarket);
        this.bestMarket = bestMarket;
        this.bestPublicMarket = bestPublicMarket;
        this.product = null;
    }

    public CurrentMarketV4 getBestMarket()
    {
        return bestMarket;
    }

    public CurrentMarketV4 getBestPublicMarketAtTop()
    {
        return bestPublicMarket;
    }

    public int getProductClassKey()
    {
        return bestMarket.getClassKey();
    }

    public String getExchange()
    {
        return bestMarket.getExchange();
    }

    public int getProductKey()
    {
        return bestMarket.getProductKey();
    }

    public void setBestMarket(CurrentMarketStructV4 bestMarket)
    {
        validate(bestMarket, getBestPublicMarketAtTop() == getBestPublicMarketAtTop() ? null : getBestPublicMarketAtTop().getCurrentMarketStructV4());
        if (this.bestMarket != null)
        {
            this.bestMarket.setCurrentMarketStructV4(bestMarket);
            if (bestMarket.productKey != this.bestMarket.getProductKey())
            {
                this.product = null;
            }
        }
        else
        {
            this.bestMarket = new CurrentMarketV4Impl(bestMarket);
        }
        identifierString = null;
    }

    public void setBestMarket(CurrentMarketV4 bestMarket)
    {
        validate(bestMarket, getBestPublicMarketAtTop());
        if(this.bestMarket != null)
        {
            this.bestMarket.setCurrentMarketStructV4(bestMarket.getCurrentMarketStructV4());
            if(bestMarket.getProductKey() != this.bestMarket.getProductKey())
            {
                this.product = null;
            }
        }
        else
        {
            this.bestMarket = bestMarket;
        }
        identifierString = null;
    }

    public void setBestPublicMarketAtTop(CurrentMarketStructV4 bestPublicMarket)
    {
        validate(getBestMarket().getCurrentMarketStructV4(), bestPublicMarket);
        if (this.bestPublicMarket != null)
        {
            this.bestPublicMarket.setCurrentMarketStructV4(bestPublicMarket);
        }
        else
        {
            this.bestPublicMarket = new CurrentMarketV4Impl(bestPublicMarket);
        }
        identifierString = null;
    }

    public void setBestPublicMarketAtTop(CurrentMarketV4 bestPublicMarket)
    {
        validate(getBestMarket(), bestPublicMarket);
        if(this.bestPublicMarket != null)
        {
            this.bestPublicMarket.setCurrentMarketStructV4(bestPublicMarket.getCurrentMarketStructV4());
        }
        else
        {
            this.bestPublicMarket = bestPublicMarket;
        }
        identifierString = null;
    }

    protected void validate(CurrentMarketStructV4 bestMarket, CurrentMarketStructV4 bestPublicMarket)
    {
        // have to at least have a bestMarket
        if(bestMarket == null)
        {
            throw new IllegalArgumentException("Best Market struct cannot be null");
        }
        else if(bestPublicMarket != null && bestMarket.productKey != bestPublicMarket.productKey)
        {
            StringBuilder sb = new StringBuilder("Best Market (productKey=").append(bestMarket.productKey);
            sb.append(") and Best Public Market (productKey=").append(bestPublicMarket.productKey);
            sb.append(") must be for the same productKey");
            throw new IllegalArgumentException(sb.toString());
        }
        else if(bestPublicMarket != null && !bestMarket.exchange.equals(bestPublicMarket.exchange))
        {
            StringBuilder sb = new StringBuilder("Best Market (exchange=").append(bestMarket.exchange);
            sb.append(") and Best Public Market (exchange=").append(bestPublicMarket.exchange);
            sb.append(") must be for the same exchange");
            throw new IllegalArgumentException(sb.toString());
        }
    }

    protected void validate(CurrentMarketV4 bestMarket, CurrentMarketV4 bestPublicMarket)
    {
        // have to at least have a bestMarket
        if(bestMarket == null)
        {
            throw new IllegalArgumentException("Best Market cannot be null");
        }
        else if(bestPublicMarket != null && bestMarket.getProductKey() != bestPublicMarket.getProductKey())
        {
            StringBuilder sb = new StringBuilder("Best Market (productKey=").append(bestMarket.getProductKey());
            sb.append(") and Best Public Market (productKey=").append(bestPublicMarket.getProductKey());
            sb.append(") must be for the same productKey");
            throw new IllegalArgumentException(sb.toString());
        }
        else if(bestPublicMarket != null && !bestMarket.getExchange().equals(bestPublicMarket.getExchange()))
        {
            StringBuilder sb = new StringBuilder("Best Market (exchange=").append(bestMarket.getExchange());
            sb.append(") and Best Public Market (exchange=").append(bestPublicMarket.getExchange());
            sb.append(") must be for the same exchange");
            throw new IllegalArgumentException(sb.toString());
        }
    }

    /**
     * Two instances of CurrentMarketV4ProductContainer are considered equal if they're for the same exchange and product.
     * @param obj
     * @return true if exchanges and productKeys are equal
     */
    public boolean equals(Object obj)
    {
        boolean retVal = false;
        if(obj != null && obj instanceof CurrentMarketV4ProductContainer)
        {
            retVal = super.equals(obj);
        }
        return retVal;
    }
}
