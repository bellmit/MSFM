package com.cboe.domain.util;

import com.cboe.interfaces.domain.Side;
import com.cboe.exceptions.InvalidSideStringException;

/**
 * Represents buy/sell side.
 *
 * @author Mark Novak
 * @author Tom Lynch
 * @author John Wickberg
 */
public abstract class SideFactory {

    private static SideBaseImpl buySide;
    private static SideBaseImpl sellSide;
    private static SideBaseImpl definedSide;
    private static SideBaseImpl oppSide;
    
    static{
        buySide = new BuySide();
        sellSide = new SellSide();
        definedSide = new AsDefinedSide();
        oppSide = new OppositeSide();
    }
/**
 * This returns the proper object for the buy side.
 * @return com.cboe.utils.Side
 */
public static SideBaseImpl getBuySide( ) {
	return buySide;
}
/**
 * This returns the proper object for the sell side.
 * @return com.cboe.utils.Side
 */
public static SideBaseImpl getSellSide( ) {
	return sellSide;
}
/**
 * This returns the proper object for the As Defined side.
 * @return com.cboe.utils.Side
 */
public static SideBaseImpl getAsDefinedSide( ) {
	return definedSide;
}
/**
 * This returns the proper object for the As Reversed side.
 * @return com.cboe.utils.Side
 */
public static SideBaseImpl getOppositeSide( ) {
	return oppSide;
}
/**
 * This returns the proper side object.  You ask for anything else you get an exception.
 * @return com.cboe.utils.Side
 * @param theSide java.lang.String
 * @exception InvalidSideStringException
 */
public static SideBaseImpl getSide( String theSide ) throws InvalidSideStringException {
        if ( theSide.equals( Side.BUY_STRING ) ) return getBuySide();
        if ( theSide.equals( Side.SELL_STRING ) ) return getSellSide();
        if ( theSide.equals( Side.AS_DEFINED_STRING ) ) return getAsDefinedSide();
        if ( theSide.equals( Side.OPPOSITE_STRING ) ) return getOppositeSide();
        if ( theSide.equals( Side.SELL_SHORT_STRING ) ) return getSellSide();
        if ( theSide.equals( Side.SELL_SHORT_EXEMPT_STRING ) ) return getSellSide();
	throw new InvalidSideStringException();
}
}
