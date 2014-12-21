package com.cboe.interfaces.domain.product;

// Source file: com/cboe/interfaces/domain/product/BaseProduct.java

/**
 * A product that is not dependent on any other product.
 *
 * @author John Wickberg
 */

public interface BaseProduct extends Product
{
/**
 * Gets identifying symbol of this product.
 *
 * @return product symbol
 *
 * @author John Wickberg
 * @roseuid 361A8E740255
 */
public String getSymbol();
/**
 * Sets identifying symbol of this product.
 * 
 * @author John Wickberg
 * @roseuid 362DF6F8010D
 */
public void setSymbol(String newSymbol);
}
