package com.cboe.interfaces.domain.product;

// Source file: com/cboe/interfaces/domain/product/Debt.java

/**
 * A product that gives the holder the right to receive specified payments
 * for a specified period.
 *
 * @author John Wickberg
 */
public interface Debt extends BaseProduct
{
/**
 * Gets name of company issuing this debt.
 *
 * @return issuing company name
 *
 * @author John Wickberg
 * @roseuid 362DF7A201F8
 */
public String getCompanyName();
/**
 * Gets the maturity date of this debt.
 * 
 * @return maturity date
 *
 * @author John Wickberg
 */
long getMaturityDate();
/**
 * Sets name of company issuing this debt.
 *
 * @param newName new issuing company name
 *
 * @author John Wickberg
 * @roseuid 362DF7AC010C
 */
public void setCompanyName(String newName);
/**
 * Sets the maturity date of this debt.
 * 
 * @param newDate new maturity date
 *
 * @author John Wickberg
 */
void setMaturityDate(long newDate);
}
