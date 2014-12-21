package com.cboe.interfaces.domain;

import com.cboe.interfaces.domain.Price;
import com.cboe.idl.cmiMarketData.NBBOStruct;

public class HALStruct
{
    public short halOrderType;
    public Price halFlashPrice;
    public boolean isCBOEWithinNTicksFromNBBO;
    public boolean isCurrentMarketNBBO;
    public Price sameSideBOTRPrice;
    public Price oppositeSideBOTRPrice;
    public int oppositeSideBOTRQuantity;
    public Price oppositeSideCBOEPrice;
    public short auctionType;
//    public NBBOStruct nbboStruct;

    public HALStruct (short aHALOrderType,
                      Price aHALFlashPrice,
                      boolean aIsCBOEWithinNTicksFromNBBO,
                      boolean aIsCurrentMarketNBBO,
                      Price aSameSideBOTRPrice,
                      Price aOppositeBOTRPrice,
                      int aOppositeSideBOTRQuantity,
                      Price aOppositeSideCBOEPrice,
                      short aAuctionType)
    {
    	this(aHALOrderType, aHALFlashPrice, aIsCBOEWithinNTicksFromNBBO,
    			aIsCurrentMarketNBBO, aSameSideBOTRPrice, aOppositeBOTRPrice, aOppositeSideBOTRQuantity, 
    			aOppositeSideCBOEPrice);
        auctionType = aAuctionType;
    }
    
    public HALStruct (short aHALOrderType,
            Price aHALFlashPrice,
            boolean aIsCBOEWithinNTicksFromNBBO,
            boolean aIsCurrentMarketNBBO,
            Price aSameSideBOTRPrice,
            Price aOppositeBOTRPrice,
            int aOppositeSideBOTRQuantity,
            Price aOppositeSideCBOEPrice)
	{
		halOrderType = aHALOrderType;
		halFlashPrice = aHALFlashPrice;
		isCBOEWithinNTicksFromNBBO = aIsCBOEWithinNTicksFromNBBO;
		isCurrentMarketNBBO = aIsCurrentMarketNBBO;
		sameSideBOTRPrice = aSameSideBOTRPrice;
		oppositeSideBOTRPrice = aOppositeBOTRPrice;
		oppositeSideBOTRQuantity = aOppositeSideBOTRQuantity;
		oppositeSideCBOEPrice = aOppositeSideCBOEPrice;
	}

    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append("\nHALOrderType: " + halOrderType);
        buf.append("\nHALFlashPrice: " + halFlashPrice);
        buf.append("\nisCBOEWithinNTicksFromNBBO: " + isCBOEWithinNTicksFromNBBO);
        buf.append("\nisCurrentMarketNBBO: " + isCurrentMarketNBBO);
        buf.append("\nsameSideBOTRPrice: " + sameSideBOTRPrice);
        buf.append("\noppositeSideBOTRPrice: " + oppositeSideBOTRPrice);
        buf.append("\noppositeSideBOTRQuantity: " + oppositeSideBOTRQuantity);
        buf.append("\noppositeSideCBOEPrice: " + oppositeSideCBOEPrice);
        buf.append("\nauctionType: " + auctionType);

        return buf.toString();
    }
}
