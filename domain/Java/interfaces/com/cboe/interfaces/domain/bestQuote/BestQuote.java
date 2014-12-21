package com.cboe.interfaces.domain.bestQuote;

import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.quote.*;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.Side;

/**
 * A quote that is either received from sources or sent to external
 * destinations.
 *
 * @author John Wickberg
 */
public interface BestQuote
{
/**
 * Gets the ask exchange & volume info for this quote.
 */
ExchangeVolumeStruct[] getAskExchangeVolumes();
/**
 * Gets the ask price of this quote.
 */
Price getAskPrice();
/**
 * Gets the bid exchange & volume info for this quote.
 */
ExchangeVolumeStruct[] getBidExchangeVolumes();
/**
 * Gets the bid price of this quote.
 */
Price getBidPrice();
/**
 * Converts this quote to a CORBA struct.
 * @param side - cmiConstants.Side.BID or cmiConstants.Side.ASK
 */
ExternalQuoteSideStruct toStruct(char side);
/**
 * Converts this quote to a CORBA NBBO struct.
 */
NBBOStruct toNBBOStruct();
/**
 * Updates this quote.
 */
void update(ExternalQuoteSideStruct newQuote);
/**
 * Updates this quote.
 */
void update(NBBOStruct newQuote);
/**
 * Updates the ask fields of this quote
 *
 * @param newPrice new ask price
 * @param newExchangeVolume exchange that is source of ask
 * @param extraVol an exchange volume to include in addition to newExchangeVolume.  Saves object allocation
 *  cost for NBBO updates when local markets are to be added to BOTR.
 */
void updateAskSide(Price newPrice, ExchangeVolumeStruct[] newExchangeVolume, ExchangeVolumeStruct extraVol);

/**
 * Updates the bid fields of this quote.
 *
 * @param newPrice new bid price
 * @param newExchangeVolume exchange that is source of bid
 * @param extraVol an exchange volume to include in addition to newExchangeVolume.  Saves object allocation
 *  cost for NBBO updates when local markets are to be added to BOTR.
 */
void updateBidSide(Price newPrice, ExchangeVolumeStruct[] newExchangeVolume, ExchangeVolumeStruct extraVol);
/**
 * Updates the ask fields of this quote
 *
 * @param newPrice new ask price
 * @param newExchangeVolume exchange that is source of ask
 */
void updateAskSide(Price newPrice, ExchangeVolumeStruct[] newExchangeVolume);

/**
 * Updates the bid fields of this quote.
 *
 * @param newPrice new bid price
 * @param newExchangeVolume exchange that is source of bid
 */
void updateBidSide(Price newPrice, ExchangeVolumeStruct[] newExchangeVolume);


/**
 * Updates the ask fields of this quote
 *
 * @param newPriceStruct new ask price
 * @param newExchangeVolume exchange that is source of ask
 */
void updateAskSide(PriceStruct newPriceStruct, ExchangeVolumeStruct[] newExchangeVolume);

/**
 * Updates the bid fields of this quote.
 *
 * @param newPriceStruct new bid price
 * @param newExchangeVolume exchange that is source of bid
 */
void updateBidSide(PriceStruct newPriceStruct, ExchangeVolumeStruct[] newExchangeVolume);

/**
 * Determine if the quote is crossed (bid>ask)
 */
boolean isCrossed();

public int getBotrQuantityBySide(Side side);

public boolean isAskDirty();
public void setAskDirty(boolean p_askDirty);

public boolean isBidDirty();
public void setBidDirty(boolean p_bidDirty);
}
