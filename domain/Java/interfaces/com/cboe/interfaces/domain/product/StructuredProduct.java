package com.cboe.interfaces.domain.product;

// Source file: com/cboe/interfaces/domain/product/StructuredProduct.java

/**
 * A derivative product that is issued in limited quantities.
 *
 * @author John Wickberg
 */
public interface StructuredProduct extends Derivative
{
/**
 * Gets description of this structured product.
 *
 * @return product description
 *
 * @author John Wickberg
 */
String getDescription();
/**
 * Gets company that is issuer or underwriter of structured product.
 *
 * @return name of company
 *
 * @author John Wickberg
 */
String getIssuingCompany();
/**
 * Gets symbol of this structured product.
 *
 * @return product symobl
 *
 * @author John Wickberg
 * @roseuid 362DF7200327
 */
public String getSymbol();
/**
 * Sets description of this structured product.
 *
 * @param newDescription new product description
 *
 * @author John Wickberg
 */
void setDescription(String newDescription);
/**
 * Sets company that is issuer or underwriter of structured product.
 *
 * @param newCompany new name of company
 *
 * @author John Wickberg
 */
void setIssuingCompany(String newCompany);
/**
 * Sets symbol of this structured product.
 *
 * @return newSymbol new product symobl
 *
 * @author John Wickberg
 * @roseuid 362DF71C032C
 */
public void setSymbol(String newSymbol);
}
