package com.cboe.interfaces.domain.product;

// Source file: com/cboe/interfaces/domain/product/Equity.java

/**
 * A product which represents ownership of a company, for example, a common stock or a preferred stock.
 * 
 * @author John Wickberg
 */
public interface Equity extends BaseProduct
{
/**
 * Gets issuing company name for this product.
 * 
 * @return company name
 *
 * @author John Wickberg
 * @roseuid 3623A11702AA
 */
public String getCompanyName();
/**
 * Sets issuing company name for this product.
 * 
 * @param newName new company name
 *
 * @author John Wickberg
 * @roseuid 362DF7FC03CE
 */
public void setCompanyName(String newName);
}
