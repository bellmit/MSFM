package com.cboe.interfaces.domain.product;

// Source file: com/cboe/interfaces/domain/product/ProductDispatch.java

/**
 * A double dispatch mechanism for product types.
 *
 * @author John Wickberg
 */
public interface ProductDispatch
{
/**
 * Implements processing for commodities.
 *
 * @param aCommodity the commodity to be processed
 * @param context an object that can serve an context info
 * @return result of double dispatch operation
 */
public Object handleCommodity(Commodity aCommodity, Object context);
/**
 * Implements processing for debt products.
 *
 * @param aDebt the debt product to be processed
 * @param context an object that can serve an context info
 * @return result of double dispatch operation
 */
public Object handleDebt(Debt aDebt, Object context);
/**
 * Implements processing for equities.
 *
 * @param anEquity the equity to be processed
 * @param context an object that can serve an context info
 * @return result of double dispatch operation
 */
public Object handleEquity(Equity anEquiry, Object context);
/**
 * Implements processing for futures.
 *
 * @param aFuture the future to be processed
 * @param context an object that can serve an context info
 * @return result of double dispatch operation
 */
public Object handleFuture(Future aFuture, Object context);
/**
 * Implements processing for indices.
 *
 * @param anIndex the index to be processed
 * @param context an object that can serve an context info
 * @return result of double dispatch operation
 */
public Object handleIndex(Index anIndex, Object context);
/**
 * Implements processing for linked note.
 *
 * @param aLinkedNote the linked note to be processed
 * @param context an object that can serve an context info
 * @return result of double dispatch operation
 */
public Object handleLinkedNote(LinkedNote aLinkedNote, Object context);
/**
 * Implements processing for options.
 *
 * @param anOption the option to be processed
 * @param context an object that can serve an context info
 * @return result of double dispatch operation
 */
public Object handleOption(Option anOption, Object context);
/**
 * Implements processing for strategies.
 *
 * @param aStrategy the strategy to be processed
 * @param context an object that can serve as context info
 * @return result of double dispatch operation
 */
public Object handleStrategy(Strategy aStrategy, Object context);
/**
 * Implements processing for unit investment trusts.
 *
 * @param aTrust the trust to be processed
 * @param context an object that can serve an context info
 * @return result of double dispatch operation
 */
public Object handleUnitInvestmentTrust(UnitInvestmentTrust aTrust, Object context);
/**
 * Implements processing for volatility indices.
 *
 * @param anIndex the index to be processed
 * @param context an object that can serve an context info
 * @return result of double dispatch operation
 */
public Object handleVolatilityIndex(VolatilityIndex anIndex, Object context);
/**
 * Implements processing for warrants.
 *
 * @param aWarrant the warrant to be processed
 * @param context an object that can serve an context info
 * @return result of double dispatch operation
 */
public Object handleWarrant(Warrant aWarrant, Object context);
}
