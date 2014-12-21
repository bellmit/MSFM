//
// -----------------------------------------------------------------------------------
// Source file: NBBOImpl.java
//
// PACKAGE: com.cboe.presentation.marketData;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.marketData;

import com.cboe.interfaces.domain.dateTime.Time;
import com.cboe.interfaces.presentation.marketData.NBBO;
import com.cboe.interfaces.presentation.marketData.ExchangeVolume;
import com.cboe.interfaces.presentation.product.ProductKeys;
import com.cboe.interfaces.domain.Price;
import com.cboe.idl.cmiMarketData.NBBOStruct;
import com.cboe.presentation.product.ProductKeysImpl;
import com.cboe.presentation.common.formatters.DisplayPriceFactory;
import com.cboe.presentation.common.dateTime.TimeImpl;
import com.cboe.presentation.common.businessModels.AbstractBusinessModel;

class NBBOImpl extends AbstractBusinessModel implements NBBO
{
    private NBBOStruct          nbboStruct;
    private ProductKeys         productKeys ;
    private Price               bidPrice;
    private Price               askPrice;
    private ExchangeVolume[]    bidExchangeVolumes;
    private ExchangeVolume[]    askExchangeVolumes;
    private Time                sentTime;
    private String              sessionName;

    public NBBOImpl(NBBOStruct  nbboStruct)
    {
        this.nbboStruct = nbboStruct;
        initialize();
    }
    private void initialize()
    {
        productKeys         = new ProductKeysImpl(nbboStruct.productKeys);
        bidPrice            = DisplayPriceFactory.create(nbboStruct.bidPrice);
        askPrice            = DisplayPriceFactory.create(nbboStruct.askPrice);
        sentTime            = new TimeImpl(nbboStruct.sentTime);
        sessionName         = new String(nbboStruct.sessionName);
        bidExchangeVolumes  = new ExchangeVolume[nbboStruct.bidExchangeVolume.length];
        askExchangeVolumes  = new ExchangeVolume[nbboStruct.askExchangeVolume.length];
        for(int i=0; i<nbboStruct.bidExchangeVolume.length; i++)
        {
            bidExchangeVolumes[i] = ExchangeVolumeFactory.createExchangeVolume(nbboStruct.bidExchangeVolume[i]);
        }
        for(int i=0; i<nbboStruct.askExchangeVolume.length; i++)
        {
            askExchangeVolumes[i] = ExchangeVolumeFactory.createExchangeVolume(nbboStruct.askExchangeVolume[i]);
        }
    }
    public ProductKeys getProductKeys()
    {
        return productKeys;
    }

    public String getSessionName()
    {
        return sessionName;
    }

    public Price getBidPrice()
    {
        return bidPrice;
    }

    public ExchangeVolume[] getBidExchangeVolume()
    {
        return bidExchangeVolumes;
    }

    public Price getAskPrice()
    {
        return askPrice;
    }

    public ExchangeVolume[] getAskExchangeVolume()
    {
        return askExchangeVolumes;
    }

    public Time getSentTime()
    {
        return sentTime;
    }

    /**
     * Gets the underlying struct
     * @return NBBOStruct
     * @deprecated Should not use underlying, but exposed interface.
     */
    public NBBOStruct getStruct()
    {
        return nbboStruct;
    }

    public int hashCode()
    {
        return getProductKeys().getProductKey();
    }
    public boolean equals( Object obj )
    {
        if(obj == null)
        {
            return false;
        }
        else if(obj instanceof NBBOImpl == false)
        {
            return false;
        }
        else
        {
            NBBOImpl nbbo = (NBBOImpl) obj;
            if( nbbo.getProductKeys().equals(getProductKeys()) &&
                nbbo.getSessionName().equals(getSessionName()) )
            {
                return true;
            }
        }
        return false;
    }

    public Object clone() throws CloneNotSupportedException
    {
        NBBOStruct dupNbboStruct = new NBBOStruct(nbboStruct.productKeys,
                                                  nbboStruct.sessionName,
                                                  nbboStruct.bidPrice,
                                                  nbboStruct.bidExchangeVolume,
                                                  nbboStruct.askPrice,
                                                  nbboStruct.askExchangeVolume,
                                                  nbboStruct.sentTime);
        NBBOImpl nbboImpl = new NBBOImpl(dupNbboStruct);
        return nbboImpl;
    }

}
