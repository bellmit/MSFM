package com.cboe.interfaces.domain;
/**
 * an interface to hold the internalization pair orders and their relationship
 */
public interface InternalizationPair
{

    /**
     * get the match type that firm side order tries to match
     */
    public short getMatchType ();

    /**
     * get the primary order to be internalized
     */
    public Order getPrimaryOrder ();

    /**
     * get the firm side match order to internalize the primary order
     */
    public Order getSecondaryOrder ();

    /**
     * get the NBBO price of the opposite side of the primary order when the order is received by SBT from TPF
     */
    public Price getNBBOPrice();

    /**
     * set the NBBO price of the opposite side of the primary order when the order is received by SBT from TPF
     */
    public void setNBBOPrice(Price p);
    
    public void setDerivedQuote(DerivedQuote quote);
    
    public DerivedQuote getDerivedQuote();
    

    /**
     * get the NBBO price for a given side of the primary order 
     * when the order is received by SBT from TPF
     * @param isOppositeAuctionSide - indicates on the auctionSame side
     * @return the NBBO price
     */
    public Price getNBBOPrice(boolean isOppositeAuctionSide);

    /**
     * set the NBBO price for a given side of the primary order
     * when the order is received by SBT from TPF
     * @param p - Price
     * @param isOppositeAuctionSide - indicates on the auctionSame side
     */
    public void setNBBOPrice(Price p, boolean isOppositeAuctionSide);
    
    /**
     * 
     * @return true/false if ISO contingency is set for InternalizationPair
     */
    public boolean isISO();
    /**
     * 
     * @return true/false if  InternalizationOrder has SWEEP (option data field A:AIS) request
     */
    public boolean hasSWEEPRequest();
}
