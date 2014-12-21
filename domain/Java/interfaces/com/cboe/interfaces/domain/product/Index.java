package com.cboe.interfaces.domain.product;

// Source file: com/cboe/interfaces/domain/product/Index.java

/**
 * A product that is  the measure of the price of a collection of products.
 *
 * @author John Wickberg
 */
public interface Index extends Composite
{
/**
 * Gets full name of this index.
 * 
 * @return index name
 */
String getFullName();
/**
 * Sets full name of this index.
 * 
 * @param newName new index name
 */
void setFullName(String newName);
}
