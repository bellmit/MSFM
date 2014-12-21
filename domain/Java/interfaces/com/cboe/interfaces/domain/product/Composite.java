package com.cboe.interfaces.domain.product;

// Source file: com/cboe/interfaces/domain/product/Composite.java

/**
 * A product that is based on a collection of products.
 *
 * @author John Wickberg
 */
public interface Composite extends Product
{
/**
 * Gets the components that are used in this composite.
 *
 * @return the components of this composite
 */
public ProductComponent[] getComponents();
/**
 * Gets identifying symbol of this index.
 *
 * @return index symbol
 */
public String getSymbol();
/**
 * Sets identifying symbol of this index.
 *
 * @param newSymbol new index symbol
 */
public void setSymbol(String newSymbol);
}
