package com.cboe.domain.util;

import com.objectwave.persist.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.interfaces.domain.Side;
import com.cboe.interfaces.domain.Price;
import com.cboe.exceptions.InvalidSideStringException;

/**
 * Represents buy/sell side.
 *
 * @author Mark Novak
 * @author Tom Lynch
 * @author John Wickberg
 */
public abstract class SideBaseImpl implements Side, SqlScalarType {

    protected static final Price zeroPrice = PriceFactory.create( 0.0 );

	static
	{
		/*
		 * Register anonymous inner class as generator for option types.
		 */
		SqlScalarTypeFactory.registerGenerator(new SqlScalarTypeGeneratorIF()
		{
			/*
			 * Creates an option type instance matching the input value.
			 */
			public SqlScalarType createInstance(String value)
			{
                try {
                    return SideFactory.getSide(value);
                }
                catch (InvalidSideStringException e) {
                    Log.alarm("Invalid side string read from database: " + value);
                    return null;
                }
			}
			/*
			 * Returns the OptionType class since it is the parent class of the
			 * generated instance types.
			 */
			public Class typeGenerated()
			{
				return Side.class;
			}
		});
	}

    /**
     * compare two prices to determine if first one is better. Note: a price being
     * better is in the market view. The buy side may have to reimplement this method
     * @return boolean
     */
    public boolean isFirstBetter(Price firstPrice, Price secondPrice) {
        if(firstPrice != null && secondPrice != null){
            if(firstPrice.isValuedPrice() && secondPrice.isValuedPrice()){
                return compareFirstWithSecond(firstPrice, secondPrice);
            }
            
            if(secondPrice.isMarketPrice()) return false;
            if(firstPrice.isNoPrice()) return false;
            if(secondPrice.isNoPrice()) return true;
        }
            if(firstPrice == null) return false;
            return true;
    }

    /**
     * test condition if firstPrice is less than the secondPrice.
     * @return boolean
     */
    protected boolean compareFirstWithSecond(Price firstPrice, Price secondPrice) {
        return firstPrice.lessThan(secondPrice);
    }
    
/**
 * This method will return whether the first price passed is better than or equal to the second price.
 * This is a convenience method to make code more readable.
 * @return boolean
 * @param firstPrice com.cboe.utils.Price
 * @param secondPrice com.cboe.utils.Price
 */
public boolean isFirstBetterOrEqual(Price firstPrice, Price secondPrice ) {
	// return the opposite of reversing the test.  if the second price is not
	// better than the first, the first is better or equal to the second.
	return !isFirstBetter(secondPrice, firstPrice);
}

/**
 * @return boolean
 */
public boolean isBuySide( ) {
	return false;
}

/**
 * @return boolean
 */
public boolean isSellSide() {
	return false;
}
/**
 * @return boolean
 */
public boolean isAsDefinedSide(){
        return false;
}

/**
 * @return boolean
 */
public boolean isOppositeSide(){
        return false;
}

/**
 * Return string for database.
 */
public String toDatabaseString() {
    return toString();
}

/**
 * This is the default implementation, which return -1, i.e. substract one tick
 * from the price. Subclass may have to override this method.
 *
 * @return int
 */
public int getMinTickAhead(){
        return -1;
}
}
