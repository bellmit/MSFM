package com.cboe.interfaces.domain;

/**
 * This interface is designed to abstract a side of Quote. Quote has two sides.
 */
public interface QuoteSide extends Tradable {

    //return the quote this QuoteSide belongs to.
    public Quote getQuote();

    public void setTobeHALTriggered(boolean canTrigger);
    public boolean canCreateHALTrigger();

    public void setQuantity(int quantity);
}
