package com.cboe.interfaces.domain;

import java.util.*;
//import com.objectspace.jgl.*;
/**
 * Contains a set of orders on one side of the market for a product
 * that is at the exact same execution priority.
 *
 * @version 0.50
 * @author Kevin Park
 */
public interface PriorityTradableSet extends Enumeration {
/**
 * Allocate trade quantity to this priority set.
 *
 * @return	int		quantity allocated to this priority set
 */
public int allocate( ParticipantList participantList, int tradeQuantity);

/**
 * Resets iterator back to the first element.
 *
 */
public void front();
/**
 * Returns price of tradables in this set.
 *
 * @return com.cboe.util.Price
 */
public Price getPrice();
/**
 * Returns total quantity of all tradables in this set.
 *
 * @return int		total quantity
 */
public int getQuantity();
/**
 * Returns the side of tradable in this set.
 *
 * @return com.cboe.domain.util.Side
 */
public Side getSide();
/**
 * Returns true if more elements are in this set.
 *
 * @return boolean
 */
public boolean hasMoreElements();
/**
 * Returns the next <code>Tradable</code> in this set.
 *
 * @return Object
 */
public Object nextElement();
/**
 * Returns number of tradables in this set.
 *
 * @return int
 */
public int size();

public void resetElements();

}
