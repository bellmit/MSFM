package com.cboe.interfaces.domain;

import java.util.Iterator;

public interface AuctionBookPriceItem
{
    /**
     * Add a Tradable
     */
    public boolean addTradable(Tradable tradable);

    /**
     * Cancel partial quantity of a Tradable
     */
    public int cancelTradable(Tradable tradable, int quantity) throws AuctionBookTradableNotFoundException;

    /**
     * setter/getter methods of item price
     */
    public void setItemPrice(Price price);
    public Price getItemPrice();

    /**
     * Return total available quantity
     */
    public int getTotalAvailableQuantity();

    /**
     * Return the list of Tradable
     */
    public Iterator getTradables();

    /**
     * Return quantity allocated
     */
    public Iterator getPriceDetails();

    /**
     * setter/getter methods of next price item
     */
    public void setNextPriceItem(AuctionBookPriceItem priceItem);
    public AuctionBookPriceItem getNextPriceItem();
}
