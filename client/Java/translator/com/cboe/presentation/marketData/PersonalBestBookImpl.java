//
// -----------------------------------------------------------------------------------
// Source file: PersonalBestBookImpl.java
//
// PACKAGE: com.cboe.presentation.marketData;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.marketData;

import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiUtil.*;
import com.cboe.idl.cmiMarketData.*;

import com.cboe.domain.util.*;

import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;

import com.cboe.interfaces.presentation.marketData.PersonalBestBook;
import com.cboe.presentation.common.formatters.DisplayPriceFactory;

public class PersonalBestBookImpl implements PersonalBestBook
{
    private int productKey;
    private MarketVolumeStruct[] bidSizeSequence;
    private MarketVolumeStruct[] askSizeSequence;
    private PriceStruct bidPrice;
    private PriceStruct askPrice;
    private String sessionName;
    private String displayValue;

    private PersonalBestBookImpl()
    {}

    public PersonalBestBookImpl(String sessionName, int productKey, PriceStruct bidPrice, MarketVolumeStruct[] bidSizeSequence, PriceStruct askPrice, MarketVolumeStruct[] askSizeSequence)
    {
        this.sessionName = sessionName;
        this.productKey = productKey;
        this.bidPrice = bidPrice;
        this.bidSizeSequence = bidSizeSequence;
        this.askPrice = askPrice;
        this.askSizeSequence = askSizeSequence;
        this.displayValue = null;
    }

    public String getSessionName()
    {
        return sessionName;
    }

    public int getProductKey()
    {
        return productKey;
    }

    public MarketVolumeStruct[] getBidSizeSequence()
    {
        return bidSizeSequence;
    }

    public MarketVolumeStruct[] getAskSizeSequence()
    {
        return askSizeSequence;
    }

    public PriceStruct getBidPrice()
    {
        return bidPrice;
    }

    public PriceStruct getAskPrice()
    {
        return askPrice;
    }

    public boolean equals(Object obj)
    {
        boolean result = false;

        if (obj != null && obj instanceof PersonalBestBookImpl)
        {
            PersonalBestBook personalBestBook = (PersonalBestBook)obj;

            if ( personalBestBook.getProductKey() == productKey &&
                 PriceFactory.create(personalBestBook.getBidPrice()).equals(PriceFactory.create(bidPrice)) &&
                 PriceFactory.create(personalBestBook.getAskPrice()).equals(PriceFactory.create(askPrice)) &&
                 (personalBestBook.getBidSizeSequence()).equals(bidSizeSequence) &&
                 (personalBestBook.getAskSizeSequence()).equals(bidSizeSequence))
            {
                result = true;
            }
        }

        return result;
    }

    public String toString()
    {
        if (displayValue == null)
        {
            StringBuffer buffer = new StringBuffer();
            buffer.append("Personal Best Book ").append(sessionName);
            buffer.append(" Product Key=").append(productKey);
            buffer.append(" Bid:");
            for (int i = 0; i < bidSizeSequence.length; i++)
            {
                if (i!=0)
                {
                    buffer.append('+');
                }
                buffer.append(bidSizeSequence[i].quantity);
            }
            buffer.append('@').append(DisplayPriceFactory.create(bidPrice).toString());

            buffer.append(" Ask:");
            for (int i = 0; i < askSizeSequence.length; i++)
            {
                if (i != 0)
                {
                    buffer.append('+');
                }
                buffer.append(askSizeSequence[i].quantity);
            }
            buffer.append('@').append(DisplayPriceFactory.create(askPrice).toString());

            displayValue = buffer.toString();
        }

        return displayValue;
    }
}
