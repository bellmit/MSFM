// -----------------------------------------------------------------------------------
// Source file: DetailOrderBookPriceImpl.java
//
// PACKAGE: com.cboe.presentation.bookDepth;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.bookDepth;

import com.cboe.interfaces.presentation.bookDepth.DetailOrderBookPrice;
import com.cboe.interfaces.presentation.bookDepth.OrderBookPriceView;
import com.cboe.interfaces.presentation.bookDepth.OrderBookPriceViewType;
import com.cboe.interfaces.presentation.marketData.MarketVolume;
import com.cboe.interfaces.presentation.marketData.VolumeType;
import com.cboe.interfaces.presentation.common.formatters.VolumeFormatStrategy;
import com.cboe.presentation.common.businessModels.AbstractBusinessModel;
import com.cboe.presentation.common.formatters.DisplayPriceFactory;
import com.cboe.presentation.marketData.VolumeTypeImpl;
import com.cboe.interfaces.domain.Price;
import com.cboe.idl.cmiMarketData.OrderBookPriceStructV2;
import com.cboe.domain.util.BookDepthStructBuilder;
import com.cboe.presentation.common.formatters.FormatFactory;

public class DetailOrderBookPriceImpl extends AbstractBusinessModel implements DetailOrderBookPrice
{
    private OrderBookPriceStructV2 struct;
    private Price price;
    private OrderBookPriceView[] views;
    private Integer totalVolume;
    private String totalVolumeString;
    private Integer brokerDealerVolume;
    private String brokerDealerVolumeString;
    private Integer customerVolume;
    private String customerVolumeString;
    private Integer marketMakerVolume;
    private String marketMakerVolumeString;
    private Integer contingencyVolume;
    private String contingencyVolumeString;
    private Integer limitVolume;
    private String limitVolumeString;
    private Integer AONVolume;
    private String AONVolumeString;
    private Integer FOKVolume;
    private String FOKVolumeString;
    private Integer IOCVolume;
    private String IOCVolumeString;
    private Integer noContingencyVolume;
    private String noContingencyVolumeString;

    protected static VolumeFormatStrategy volumeFormatter;


    DetailOrderBookPriceImpl(OrderBookPriceStructV2 struct)
    {
        this();
        checkParam(struct, "OrderBookPriceStructV2");
        this.struct = struct;
    }

    private DetailOrderBookPriceImpl()
    {
        super();
        volumeFormatter = FormatFactory.getVolumeFormatStrategy();
    }

    /**
     * Gets the price for this book entry
     */
    public Price getPrice()
    {
        if (price == null)
        {
            price = DisplayPriceFactory.create(struct.price);
        }
        return price;
    }

    /**
     * Gets the total volume at this price
     */
    public Integer getTotalVolume()
    {
        if (totalVolume == null)
        {
            int totalQty = 0;
            MarketVolume[] marketVolume = getViewByType(OrderBookPriceViewTypeImpl.BY_ORIGIN_TYPE).getMarketVolume();
            for (int i = 0; i < marketVolume.length; i++)
            {
                totalQty += marketVolume[i].getQuantity().intValue();
            }
            totalVolume = new Integer(totalQty);
        }
        return totalVolume;
    }

    /**
     * Gets the total volume at this price formatted as a String
     */
    public String getTotalVolumeString()
    {
        if (totalVolumeString == null)
        {
            totalVolumeString = volumeFormatter.format(getTotalVolume().intValue());
        }
        return totalVolumeString;
    }

    /**
     * Gets the total volume at this price formatted as a String
     */
    public OrderBookPriceView[] getPriceViews()
    {
        if (views == null)
        {
            views = OrderBookFactory.createOrderBookPriceViews(struct.views);
        }
        return views;
    }

    /**
     * Gets the broker/dealer volume at this price
     */
    public Integer getBrokerDealerVolume()
    {
        if ( brokerDealerVolume == null)
        {
            OrderBookPriceView view = getViewByType(OrderBookPriceViewTypeImpl.BY_ORIGIN_TYPE);
            brokerDealerVolume = getVolumeByType(view, VolumeTypeImpl.PROFESSIONAL_ORDER);
        }
        return brokerDealerVolume;
    }


    /**
     * Gets the broker/dealer volume at this price formatted as a String
     */
    public String getBrokerDealerVolumeString()
    {
        if (brokerDealerVolumeString == null)
        {
            brokerDealerVolumeString = volumeFormatter.format(getBrokerDealerVolume().intValue());
        }
        return brokerDealerVolumeString;
    }

    /**
     * Gets the contigency volume at this price
     */
    public Integer getContingencyVolume()
    {
        if (contingencyVolume == null)
        {
            int volume = 0;
            OrderBookPriceView view = getViewByType(OrderBookPriceViewTypeImpl.BY_ORIGIN_TYPE);
            if (view != null)
            {
                MarketVolume[] volumes = view.getMarketVolume();
                for (int i = 0; i < volumes.length; i++)
                {
                    VolumeType volumeType = volumes[i].getVolumeType();
                    if ( VolumeTypeImpl.isContingencyVolumeType(volumeType) )
                    {
                        volume += volumes[i].getQuantity().intValue();
                    }
                }
            }
           contingencyVolume = new Integer(volume);
        }
        return contingencyVolume;
    }

    /**
     * Gets the contigency volume at this price formatted as a String
     */
    public String getContingencyVolumeString()
    {
        if (contingencyVolumeString == null)
        {
            contingencyVolumeString = volumeFormatter.format(getContingencyVolume().intValue());
        }
        return contingencyVolumeString;
    }

    /**
     * Gets the customer volume at this price
     */
    public Integer getCustomerVolume()
    {
        if (customerVolume == null)
        {
            OrderBookPriceView view = getViewByType(OrderBookPriceViewTypeImpl.BY_ORIGIN_TYPE);
            customerVolume = getVolumeByType(view, VolumeTypeImpl.CUSTOMER_ORDER);
        }
        return customerVolume;
    }

    /**
     * Gets the customer volume at this price formatted as a String
     */
    public String getCustomerVolumeString()
    {
        if (customerVolumeString == null)
        {
            customerVolumeString = volumeFormatter.format(getCustomerVolume().intValue());
        }
        return customerVolumeString;
    }

    /**
     * Gets the market maker volume at this price
     */
    public Integer getMarketMakerVolume()
    {
        if (marketMakerVolume == null)
        {
            OrderBookPriceView view = getViewByType(OrderBookPriceViewTypeImpl.BY_ORIGIN_TYPE);
            marketMakerVolume = getVolumeByType(view, VolumeTypeImpl.QUOTES);
        }
        return marketMakerVolume;
    }

    /**
     * Gets the market maker volume at this price formatted as a String
     */
    public String getMarketMakerVolumeString()
    {
        if (marketMakerVolumeString == null)
        {
            marketMakerVolumeString = volumeFormatter.format(getMarketMakerVolume().intValue());
        }
        return marketMakerVolumeString;
    }

    public OrderBookPriceView getViewByType(OrderBookPriceViewType type)
    {
        OrderBookPriceView view = null;
        OrderBookPriceView [] views = getPriceViews();
        if (views != null)
        {
            for (int i = 0; i < views.length; i++)
            {
                if (views[i].getViewType() == type)
                {
                    view = views[i];
                    break;
                }
            }
        }
        return view;
    }

    public Integer getAONVolume()
    {
        if (AONVolume == null)
        {
            OrderBookPriceView view = getViewByType(OrderBookPriceViewTypeImpl.BY_ORIGIN_TYPE);
            AONVolume = getVolumeByType(view, VolumeTypeImpl.AON);
        }
        return AONVolume;
    }

    public String getAONVolumeString()
    {
        if (AONVolumeString == null)
        {
            AONVolumeString = volumeFormatter.format(getAONVolume().intValue());
        }
        return AONVolumeString;
    }

    public Integer getFOKVolume()
    {
        if (FOKVolume == null)
        {
            OrderBookPriceView view = getViewByType(OrderBookPriceViewTypeImpl.BY_ORIGIN_TYPE);
            FOKVolume = getVolumeByType(view, VolumeTypeImpl.FOK);
        }
        return FOKVolume;
    }

    public String getFOKVolumeString()
    {
        if (FOKVolumeString == null)
        {
            FOKVolumeString = volumeFormatter.format(getFOKVolume().intValue());
        }
        return FOKVolumeString;
    }

    public Integer getIOCVolume()
    {
        if (IOCVolume == null)
        {
            OrderBookPriceView view = getViewByType(OrderBookPriceViewTypeImpl.BY_ORIGIN_TYPE);
            IOCVolume = getVolumeByType(view, VolumeTypeImpl.IOC);
        }
        return IOCVolume;
    }

    public String getIOCVolumeString()
    {
        if (IOCVolumeString == null)
        {
            IOCVolumeString = volumeFormatter.format(getIOCVolume().intValue());
        }
        return IOCVolumeString;
    }

    public Integer getLimitVolume()
    {
        if (limitVolume == null)
        {
            OrderBookPriceView view = getViewByType(OrderBookPriceViewTypeImpl.BY_ORIGIN_TYPE);
            limitVolume = getVolumeByType(view, VolumeTypeImpl.LIMIT);
        }
        return limitVolume;
    }

    public String getLimitVolumeString()
    {
        if (limitVolumeString == null)
        {
            limitVolumeString = volumeFormatter.format(getLimitVolume().intValue());
        }
       return limitVolumeString;
    }

    public Integer getNoContingencyVolume()
    {
        if (noContingencyVolume == null)
        {
            OrderBookPriceView view = getViewByType(OrderBookPriceViewTypeImpl.BY_ORIGIN_TYPE);
            noContingencyVolume = getVolumeByType(view, VolumeTypeImpl.NO_CONTINGENCY);
        }
        return noContingencyVolume;
    }

    public String getNoContingencyVolumeString()
    {
        if (noContingencyVolumeString == null)
        {
            noContingencyVolumeString = volumeFormatter.format(getNoContingencyVolume().intValue());
        }
       return noContingencyVolumeString;
    }


    private Integer getVolumeByType(OrderBookPriceView view, VolumeType type)
    {
        Integer volume = null;
        if (view != null)
        {
            MarketVolume[] volumes = view.getMarketVolume();
            for (int i = 0; i < volumes.length; i++)
            {
                if (volumes[i].getVolumeType() == type)
                {
                    volume = volumes[i].getQuantity();
                    break;
                }
            }
        }
        // If it is still null, i.e. we did not find this volume type, make it 0 Integer
        if (volume == null)
        {
            volume = new Integer(0);
        }

        return volume;
    }

    public Object clone() throws CloneNotSupportedException
    {
        OrderBookPriceStructV2 clonedStruct = BookDepthStructBuilder.cloneOrderBookPriceStructV2(this.struct);
        return OrderBookFactory.createDetailOrderBookPrice(clonedStruct);
    }
}
