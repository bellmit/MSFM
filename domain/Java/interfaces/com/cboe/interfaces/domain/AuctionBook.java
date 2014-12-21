package com.cboe.interfaces.domain;

import java.util.ArrayList;
import java.util.Iterator;

import com.cboe.exceptions.AlreadyExistsException;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.interfaces.domain.TradableList;

public interface AuctionBook
{

    /**
     * Start a new auction with a order pair. For a single order auction, secondaryOrder will be null
     */
    public void startNewAuction(Order primaryOrder, Order secondaryOrder, Price auctionPrice)
    throws AlreadyExistsException, NotAcceptedException;

    /**
     * expire current auction
     */
    public void expireCurrentAuction() throws DataValidationException;

    /**
     * Add a Tradable to AuctionBook
     */
    public void bookTradable(Tradable tradable) throws AlreadyExistsException, NotAcceptedException;

    /**
     * Cancel part of a Tradable from AuctionBook
     */
    public int cancelTradable(Tradable tradable, int quantity) throws AuctionBookTradableNotFoundException;

    /**
     * Return the side of the auction order
     */
    public Side getAuctionSide();

    /**
     * Return the internalized order price item
     */
    public AuctionBookPriceItem getAuctionPriceItem();

    /**
     * Return the firm matched order price item
     */
    public AuctionBookPriceItem getFirmMatchedPriceItem();

    /**
     * Return the list of auction response price item
     */
    public Iterator getAuctionResponsePriceItems();

    /**
     * Return the best auction response price item
     */
    public AuctionBookPriceItem getBestAuctionResponsePriceItem();

    /**
     * Return the total volume for all auction responses in the AuctionBook
     * that is at or better than the specified price.
     */
    public int getQuantityAhead(Price price);

    /**
     * Return tradables of auction responses in this auction book that is
     * at or better than the specified price.
     */
    public Iterator getTradables(Price price);

    /**
     * Return true if the price is marketable against:
     *          the auction starting price if the side is on the opposite side of auction side
     *          or   the best auction response price or the auction starting price
     *               if the side is on the same side of auction side
     */
    public boolean crossesAuction(Side side, Price price);

    /**
     * Return true if the side is opposite of RFP, and marketabale against best RFP if there is any
              or return false
     */
    public boolean crossesRFP(Side side, Price price);

    /**
     * Add a Tradable to AuctionBook
     */
    public void joinAuction(Tradable tradable) throws AlreadyExistsException, NotAcceptedException, DataValidationException;

    /**
     * Get the list of auctioned order and joined auctioned orders sorted by price item
     */
    public TradableList getAuctionOrderSortedList();

    /**
     * Get the list of joined auctioned orders
     */
    public TradableList getJoiningAuctionOrderList();

    /**
     * Set the auction starting price
     */
    public void setAuctionStartingPrice(Price price);

    /**
     * Clear everything when auction ends
     */
    public void clear();

    /**
     * return the orderBook that this AuctionBook is associated with
     */
    public OrderBook getOrderBook();

    /**
     * return the TradingProduct that this auctionBook is created for.
     */
    public TradingProduct getTradingProduct();
    
    public ArrayList<Order> getAuctionResponses();
    
    public void removeAuctionResponses();
    
    public Order getAuctionedOrder();
}
