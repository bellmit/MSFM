package com.cboe.domain.order;

import com.cboe.interfaces.domain.Price;
import com.cboe.server.domain.BaseDomainObjectImpl;
import com.cboe.server.domain.BaseTXChangeLog;
import static com.cboe.domain.order.OrderHandlingInstructionTXFieldEnum.*;

public class OrderHandlingInstructionTXChangeLog extends BaseTXChangeLog
{   
    private Price oppositeSideBOTR;
    private Price executionPrice;
    private Price originalExecutionPrice;
    private int maximumExecutionVolume;
    private short maximumExecutionVolumeReason;
    private short tradingRestriction;
    private short remainderHandlingMode;
    private boolean ignoreContingency = false;
    private int orderQuantityAtReceiveTime;
    private int tradedVolume = 0;
    private boolean expressOrder = false;
    private double bookableOrderMarketLimit = 0.0;  
    private int maxTradableQtyForIndexHybrid;
    private short overRideReasonForIndexHybrid;

    long changeFieldBitMask = 0;
    
    public Price getOppositeSideBOTR()
    {
        return oppositeSideBOTR;
    }

    public void setOppositeSideBOTR(Price p_oppositeSideBOTR)
    {
        if(!OPPOSITESIDEBOTR.isBitSet(changeFieldBitMask))
        {
            changeFieldBitMask = OPPOSITESIDEBOTR.setBit(changeFieldBitMask);
            oppositeSideBOTR = p_oppositeSideBOTR;
        }
    }

    public Price getExecutionPrice()
    {
        return executionPrice;
    }

    public void setExecutionPrice(Price p_executionPrice)
    {
        if(!EXECUTIONPRICE.isBitSet(changeFieldBitMask))
        {
            changeFieldBitMask = EXECUTIONPRICE.setBit(changeFieldBitMask);
            executionPrice = p_executionPrice;
        }
    }

    public Price getOriginalExecutionPrice()
    {
        return originalExecutionPrice;
    }

    public void setOriginalExecutionPrice(Price p_originalExecutionPrice)
    {
        if(!ORIGINALEXECUTIONPRICE.isBitSet(changeFieldBitMask))
        {
            changeFieldBitMask = ORIGINALEXECUTIONPRICE.setBit(changeFieldBitMask);
            originalExecutionPrice = p_originalExecutionPrice;
        }
    }

    public int getMaximumExecutionVolume()
    {
        return maximumExecutionVolume;
    }

    public void setMaximumExecutionVolume(int p_maximumExecutionVolume)
    {
        if(!MAXIMUMEXECUTIONVOLUME.isBitSet(changeFieldBitMask))
        {
            changeFieldBitMask = MAXIMUMEXECUTIONVOLUME.setBit(changeFieldBitMask);
            maximumExecutionVolume = p_maximumExecutionVolume;
        }
    }

    public short getMaximumExecutionVolumeReason()
    {
        return maximumExecutionVolumeReason;
    }

    public void setMaximumExecutionVolumeReason(short p_maximumExecutionVolumeReason)
    {
        if(!MAXIMUMEXECUTIONVOLUMEREASON.isBitSet(changeFieldBitMask))
        {
            changeFieldBitMask = MAXIMUMEXECUTIONVOLUMEREASON.setBit(changeFieldBitMask);
            maximumExecutionVolumeReason = p_maximumExecutionVolumeReason;
        }
    }

    public short getTradingRestriction()
    {
        return tradingRestriction;
    }

    public void setTradingRestriction(short p_tradingRestriction)
    {
        if(!TRADINGRESTRICTION.isBitSet(changeFieldBitMask))
        {
            changeFieldBitMask = TRADINGRESTRICTION.setBit(changeFieldBitMask);
            tradingRestriction = p_tradingRestriction;
        }
    }

    public short getRemainderHandlingMode()
    {
        return remainderHandlingMode;
    }

    public void setRemainderHandlingMode(short p_remainderHandlingMode)
    {
        if(!REMAINDERHANDLINGMODE.isBitSet(changeFieldBitMask))
        {
            changeFieldBitMask = REMAINDERHANDLINGMODE.setBit(changeFieldBitMask);
            remainderHandlingMode = p_remainderHandlingMode;
        }
    }

    public boolean isIgnoreContingency()
    {
        return ignoreContingency;
    }

    public void setIgnoreContingency(boolean p_ignoreContingency)
    {
        if(!IGNORECONTINGENCY.isBitSet(changeFieldBitMask))
        {
            changeFieldBitMask = IGNORECONTINGENCY.setBit(changeFieldBitMask);
            ignoreContingency = p_ignoreContingency;
        }
    }

    public int getOrderQuantityAtReceiveTime()
    {
        return orderQuantityAtReceiveTime;
    }

    public void setOrderQuantityAtReceiveTime(int p_orderQuantityAtReceiveTime)
    {
        if(!ORDERQUANTITYATRECEIVETIME.isBitSet(changeFieldBitMask))
        {
            changeFieldBitMask = ORDERQUANTITYATRECEIVETIME.setBit(changeFieldBitMask);
            orderQuantityAtReceiveTime = p_orderQuantityAtReceiveTime;
        }
    }

    public int getTradedVolume()
    {
        return tradedVolume;
    }

    public void setTradedVolume(int p_tradedVolume)
    {
        if(!TRADEDVOLUME.isBitSet(changeFieldBitMask))
        {
            changeFieldBitMask = TRADEDVOLUME.setBit(changeFieldBitMask);
            tradedVolume = p_tradedVolume;
        }
    }

    public boolean isExpressOrder()
    {
        return expressOrder;
    }

    public void setExpressOrder(boolean p_expressOrder)
    {
        if(!EXPRESSORDER.isBitSet(changeFieldBitMask))
        {
            changeFieldBitMask = EXPRESSORDER.setBit(changeFieldBitMask);
            expressOrder = p_expressOrder;
        }
    }

    public double getBookableOrderMarketLimit()
    {
        return bookableOrderMarketLimit;
    }

    public void setBookableOrderMarketLimit(double p_bookableOrderMarketLimit)
    {
        if(!BOOKABLEORDERMARKETLIMIT.isBitSet(changeFieldBitMask))
        {
            changeFieldBitMask = BOOKABLEORDERMARKETLIMIT.setBit(changeFieldBitMask);
            bookableOrderMarketLimit = p_bookableOrderMarketLimit;
        }
    }

    public int getMaxTradableQtyForIndexHybrid()
    {
        return maxTradableQtyForIndexHybrid;
    }

    public void setMaxTradableQtyForIndexHybrid(int p_maxTradableQtyForIndexHybrid)
    {
        if(!MAXTRADABLEQTYFORINDEXHYBRID.isBitSet(changeFieldBitMask))
        {
            changeFieldBitMask = MAXTRADABLEQTYFORINDEXHYBRID.setBit(changeFieldBitMask);
            maxTradableQtyForIndexHybrid = p_maxTradableQtyForIndexHybrid;
        }
    }

    public short getOverRideReasonForIndexHybrid()
    {
        return overRideReasonForIndexHybrid;
    }

    public void setOverRideReasonForIndexHybrid(short p_overRideReasonForIndexHybrid)
    {
        if(!OVERRIDEREASONFORINDEXHYBRID.isBitSet(changeFieldBitMask))
        {
            changeFieldBitMask = OVERRIDEREASONFORINDEXHYBRID.setBit(changeFieldBitMask);
            overRideReasonForIndexHybrid = p_overRideReasonForIndexHybrid;
        }
    }

    public void clearChangeLog()
    {
        this.changeFieldBitMask = 0;
    }
    
    /**
     * 
     * @param parentOrderImplTXChangeLog
     */
    public void rollbackChangesToParentTXLog(BaseDomainObjectImpl domainObject)
    {
        if(changeFieldBitMask <= 0)
        {
            return;
        }

        OrderHandlingInstructionNoReflectionImpl orderHI = (OrderHandlingInstructionNoReflectionImpl) domainObject;

        if(OPPOSITESIDEBOTR.isBitSet(changeFieldBitMask))
        {
            orderHI.setOppositeSideBOTR(getOppositeSideBOTR());
        }

        if(EXECUTIONPRICE.isBitSet(changeFieldBitMask))
        {
            orderHI.setExecutionPrice(getExecutionPrice());
        }

        if(ORIGINALEXECUTIONPRICE.isBitSet(changeFieldBitMask))
        {
            orderHI.setOriginalExecutionPrice(getOriginalExecutionPrice());
        }

        if(MAXIMUMEXECUTIONVOLUME.isBitSet(changeFieldBitMask))
        {
            orderHI.setMaximumExecutionVolume(getMaximumExecutionVolume());
        }

        if(MAXIMUMEXECUTIONVOLUMEREASON.isBitSet(changeFieldBitMask))
        {
            orderHI.setMaximumExecutionVolumeReason(getMaximumExecutionVolumeReason());
        }

        if(TRADINGRESTRICTION.isBitSet(changeFieldBitMask))
        {
            orderHI.setTradingRestriction(getTradingRestriction());
        }

        if(REMAINDERHANDLINGMODE.isBitSet(changeFieldBitMask))
        {
            orderHI.setRemainderHandlingMode(getRemainderHandlingMode());
        }

        if(IGNORECONTINGENCY.isBitSet(changeFieldBitMask))
        {
            orderHI.setIgnoreContingency(isIgnoreContingency());
        }

        if(ORDERQUANTITYATRECEIVETIME.isBitSet(changeFieldBitMask))
        {
            orderHI.setOrderQuantityReceived(getOrderQuantityAtReceiveTime());
        }

        if(TRADEDVOLUME.isBitSet(changeFieldBitMask))
        {
            orderHI.setTradedVolume(getTradedVolume());
        }

        if(EXPRESSORDER.isBitSet(changeFieldBitMask))
        {
            orderHI.setExpressOrder(isExpressOrder());
        }

        if(BOOKABLEORDERMARKETLIMIT.isBitSet(changeFieldBitMask))
        {
            orderHI.setBookableOrderMarketLimit(getBookableOrderMarketLimit());
        }

        if(MAXTRADABLEQTYFORINDEXHYBRID.isBitSet(changeFieldBitMask))
        {
            orderHI.setMaxTradableQtyForIndexHybrid(getMaxTradableQtyForIndexHybrid());
        }

        if(OVERRIDEREASONFORINDEXHYBRID.isBitSet(changeFieldBitMask))
        {
            orderHI.setOverRideReasonForIndexHybrid(getOverRideReasonForIndexHybrid());
        }

        changeFieldBitMask = 0;
    }
}
