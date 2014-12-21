package com.cboe.application.marketData;

import com.cboe.domain.util.PriceFactory;
import com.cboe.domain.util.SideFactory;
import com.cboe.idl.cmiConstants.CurrentMarketViewTypes;
import com.cboe.interfaces.domain.MarketUpdateSide;
import com.cboe.interfaces.domain.MarketUpdateVolType;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.Side;
//import com.cboe.server.logger.MsgBuilder;

public final class ClientMarketUpdateSideImpl implements MarketUpdateSide
{
	private static final int NUM_VOLS = 8;
	private final boolean isBid;
	private final Side side;

	Price bestPrice;
	Price bestLimitPrice;
	Price bestPublicPrice;
	final int[] volumes = new int[NUM_VOLS];
	int multiplePartyBitmask;

	ClientMarketUpdateSideImpl(boolean p_isBid)
	{
		this.isBid = p_isBid;
		this.side = p_isBid ? SideFactory.getBuySide() : SideFactory.getSellSide();
	}

	/* (non-Javadoc)
     * @see com.cboe.businessServices.marketDataService.marketUpdate.MarketUpdateSide#getBestPrice()
     */
	public Price getBestPrice()
	{
		return bestPrice==null ? PriceFactory.getNoPrice() : bestPrice;
	}

	/* (non-Javadoc)
     * @see com.cboe.businessServices.marketDataService.marketUpdate.MarketUpdateSide#getBestLimitPrice()
     */
	public Price getBestLimitPrice()
	{
		return bestLimitPrice==null ? PriceFactory.getNoPrice() : bestLimitPrice;
	}

	/* (non-Javadoc)
     * @see com.cboe.businessServices.marketDataService.marketUpdate.MarketUpdateSide#getBestPublicPrice()
     */
	public Price getBestPublicPrice()
	{
		return bestPublicPrice==null ? PriceFactory.getNoPrice() : bestPublicPrice;
	}

	/* (non-Javadoc)
     * @see com.cboe.businessServices.marketDataService.marketUpdate.MarketUpdateSide#hasPublicPrice()
     */
	public boolean hasPublicPrice()
	{
	    return bestPublicPrice != null && bestPublicPrice.isValuedPrice();
	}

	public boolean hasLimitPrice()
	{
	    return bestLimitPrice != null && bestLimitPrice.isValuedPrice();
	}


	/* (non-Javadoc)
     * @see com.cboe.businessServices.marketDataService.marketUpdate.MarketUpdateSide#isMultiParty(com.cboe.businessServices.marketDataService.marketUpdate.MarketUpdateVolType)
     */
	public boolean isMultiParty(MarketUpdateVolType p_volType)
	{
		return p_volType.isBitSet(multiplePartyBitmask);
	}

	/* (non-Javadoc)
     * @see com.cboe.businessServices.marketDataService.marketUpdate.MarketUpdateSide#setIsMultiParty(com.cboe.businessServices.marketDataService.marketUpdate.MarketUpdateVolType)
     */
	public void setIsMultiParty(MarketUpdateVolType p_volType)
	{
		multiplePartyBitmask = p_volType.setBit(multiplePartyBitmask);
	}

	/* (non-Javadoc)
     * @see com.cboe.businessServices.marketDataService.marketUpdate.MarketUpdateSide#getTotalVolume(short)
     */
	public int getTotalVolume(short p_volType)
	{
		switch (p_volType)
		{
			case CurrentMarketViewTypes.BEST_PRICE:
				return getVolume(MarketUpdateVolType.Best_AON)
					+ getVolume(MarketUpdateVolType.Best_limit)
					+ getVolume(MarketUpdateVolType.Best_OddLot);
			case CurrentMarketViewTypes.BEST_LIMIT_PRICE:
				return getVolume(MarketUpdateVolType.BestLimit_limit);
			case CurrentMarketViewTypes.BEST_PUBLIC_ORDER_PRICE:
				return getVolume(MarketUpdateVolType.BestPublic_customer)
					+ getVolume(MarketUpdateVolType.BestPublic_professional);
			default:
				return 0;
		}
	}

	/* (non-Javadoc)
     * @see com.cboe.businessServices.marketDataService.marketUpdate.MarketUpdateSide#getVolume(com.cboe.businessServices.marketDataService.marketUpdate.MarketUpdateVolType)
     */
	public int getVolume(MarketUpdateVolType p_volType)
	{
		return p_volType.getVol(this.volumes);
	}

	/* (non-Javadoc)
     * @see com.cboe.businessServices.marketDataService.marketUpdate.MarketUpdateSide#setBestPrice(com.cboe.interfaces.domain.Price)
     */
	public void setBestPrice(Price p_price)
	{
		bestPrice = p_price;
	}

	/* (non-Javadoc)
     * @see com.cboe.businessServices.marketDataService.marketUpdate.MarketUpdateSide#setBestLimitPrice(com.cboe.interfaces.domain.Price)
     */
	public void setBestLimitPrice(Price p_price)
	{
		bestLimitPrice = p_price;
	}

	/* (non-Javadoc)
     * @see com.cboe.businessServices.marketDataService.marketUpdate.MarketUpdateSide#setBestPublicPrice(com.cboe.interfaces.domain.Price)
     */
	public void setBestPublicPrice(Price p_price)
	{
		bestPublicPrice = p_price;
	}

	/* (non-Javadoc)
     * @see com.cboe.businessServices.marketDataService.marketUpdate.MarketUpdateSide#setVolume(com.cboe.businessServices.marketDataService.marketUpdate.MarketUpdateVolType, int, boolean)
     */
	public void setVolume(MarketUpdateVolType p_volType, int p_volume, boolean p_isMultiParty)
	{
		p_volType.setVol(volumes, p_volume);
		if (p_isMultiParty)
			this.multiplePartyBitmask = p_volType.setBit(this.multiplePartyBitmask);
		else if (p_volType.isBitSet(this.multiplePartyBitmask))
			this.multiplePartyBitmask = p_volType.clearBit(this.multiplePartyBitmask);
	}

	/* (non-Javadoc)
     * @see com.cboe.businessServices.marketDataService.marketUpdate.MarketUpdateSide#clear()
     */
	public void clear()
	{
		bestPrice = null;
		bestLimitPrice = null;
		bestPublicPrice = null;
		multiplePartyBitmask = 0;
		for (int i = 0; i < volumes.length; i++)
			volumes[i] = 0;
	}

	/* (non-Javadoc)
     * @see com.cboe.businessServices.marketDataService.marketUpdate.MarketUpdateSide#isPublicPriceBest()
     */
	public boolean isPublicPriceBest()
	{
		return hasPublicPrice() && (
				bestPrice==null
				|| !bestPrice.isValuedPrice()
				|| side.isFirstBetterOrEqual(bestPublicPrice, bestPrice));
	}

	public boolean isLimitPriceBest()
	{
	    return hasLimitPrice() && (
                bestPrice==null
                || !bestPrice.isValuedPrice()
                || side.isFirstBetterOrEqual(bestLimitPrice, bestPrice));
	}

	/* (non-Javadoc)
     * @see com.cboe.businessServices.marketDataService.marketUpdate.MarketUpdateSide#isBid()
     */
	public boolean isBid()
	{
		return isBid;
	}

	public void copyFrom(MarketUpdateSide other)
	{
	    ClientMarketUpdateSideImpl p_other = (ClientMarketUpdateSideImpl)other;
		bestPrice = p_other.bestPrice;
		bestLimitPrice = p_other.bestLimitPrice;
		bestPublicPrice = p_other.bestPublicPrice;
		multiplePartyBitmask = p_other.multiplePartyBitmask;
		for (int i=0; i < NUM_VOLS; i++)
			volumes[i] = p_other.volumes[i];
	}
/*
	@Override
	public String toString()
	{
		MsgBuilder msg = MsgBuilder.get();
		msg.add("best", bestPrice);
		msg.add("bestLimit", bestLimitPrice);
		msg.add("bestPublic", bestPublicPrice);
		MarketUpdateVolType[] vTypes = MarketUpdateVolType.values();
		for (int i=0; i < vTypes.length; i++)
		{
			int vol = vTypes[i].getVol(volumes);
			if (vol!=0)
			{
				msg.add(vTypes[i].name(), vol + (vTypes[i].isBitSet(multiplePartyBitmask)?"*":""));
			}
		}
		return msg.toString();
	}
*/
	@Override
	public boolean equals(Object p_rhs)
	{
		if (p_rhs==null || p_rhs.getClass() != ClientMarketUpdateSideImpl.class)
			return false;
		final ClientMarketUpdateSideImpl rhs = (ClientMarketUpdateSideImpl)p_rhs;
		return equalPrices(getBestPrice(), rhs.getBestPrice())
			&& equalPrices(getBestLimitPrice(), rhs.getBestLimitPrice())
			&& equalPrices(getBestPublicPrice(), rhs.getBestPublicPrice())
			&& equalVolumes(volumes, rhs.volumes)
			&& (multiplePartyBitmask == rhs.multiplePartyBitmask);
	}

	private boolean equalPrices(Price p_lhs, Price p_rhs)
	{
		if (p_lhs==null || p_rhs==null)
			return (p_lhs==null && p_rhs==null);
		return p_lhs.equals(p_rhs);
	}

	private boolean equalVolumes(int[] p_lhs, int[] p_rhs)
	{
		if (p_lhs==null || p_rhs==null)
			return (p_lhs==null && p_rhs==null);
		if (p_lhs.length != p_rhs.length)
			return false;
		for (int i = 0; i < p_lhs.length; i++)
		{
			if (p_lhs[i] != p_rhs[i])
				return false;
		}
		return true;
	}

        public boolean hasLimitAndReserveVolume()
        {
                return getVolume(MarketUpdateVolType.BestLimit_limitAndReserve) > getVolume(MarketUpdateVolType.BestLimit_limit);
        }


    public Price getBestLimitRoundLotPrice()
    {
		return PriceFactory.getNoPrice();
	}

    public void setBestLimitRoundLotPrice(Price p_bestLimitRoundLotPrice)
    {
		//do not set - New MarketView is needed only for StockCFN Adapter.
	}

    public boolean hasRoundLotLimitMarket()
    {
		return false;
	}

    public void setRoundLotLimitMarket(boolean p_roundLotLimit)
    {
		//do not set - New MarketView is needed only for StockCFN Adapter.
	}

}
