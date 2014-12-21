package com.cboe.domain.util;

import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiConstants.*;

/**
 * A wrapper for the <code>ProductKeyStruct</code> so that it can be put into hash tables.
 * 
 * @author John Wickberg
 */
public class ProductKey
{
	/**
	 *  Struct holding product keys.
	 */
	private ProductKeysStruct productKeys;
/**
 * Creates a partial ProductKey.  Since only the productKey value
 * is supplied, other keys in ProductKeyStruct returned by toStruct
 * will not be set.
 *
 */
public ProductKey(int productKey)
{
	super();
	// only the productKey value of the ProductKeysStruct is
	// used within this class.
	ProductKeysStruct productKeys = new ProductKeysStruct();
	productKeys.productKey = productKey;
	setProductKeys(productKeys);
}
/**
 * ProductKey constructor comment.
 */
public ProductKey(ProductKeysStruct keys)
{
	super();
	setProductKeys(keys);
}
/**
 * ProductKey constructor comment.
 */
public ProductKey(ProductStruct product)
{
	super();
	setProductKeys(product.productKeys);
}
/**
 * Checks keys for equality.
 * 
 * @author John Wickberg
 */
public boolean equals(Object otherKey)
{
	if (!(otherKey instanceof ProductKey))
	{
		return false;
	}
	else
	{
		return getProductKey() == ((ProductKey) otherKey).getProductKey();
	}
}
/**
 * Returns key of product.
 * 
 * @author John Wickberg
 */
public int getProductKey() {
	return productKeys.productKey;
}
/**
 * Returns hash code for this key.
 * 
 * @author John Wickberg
 */
public int hashCode()
{
	// use product key for code.
	return getProductKey();
}
/**
 * Setter.
 * 
 * @author John Wickberg
 */
public void setProductKeys(ProductKeysStruct newKeys)
{
	productKeys = newKeys;
}
/**
 * Returns struct containing all keys for product.  Should not be
 * used if <code>ProductKey</code> was constructed with only the
 * product key and not a struct.
 * 
 * @author John Wickberg
 * @return com.cboe.idl.cmiProduct.ProductKeysStruct
 */
public ProductKeysStruct toStruct() {
	return productKeys;
}
}
