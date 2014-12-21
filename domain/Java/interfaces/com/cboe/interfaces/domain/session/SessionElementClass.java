package com.cboe.interfaces.domain.session;

import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.TransactionFailedException;

/**
 * A session element class is a assigment of a class to a trading session
 * element.  A session element class is an instance of a template class for
 * a particular business day.
 *
 * @author John Wickberg
 */
public interface SessionElementClass {

    /**
     * Adds product to session class.  Used for intraday product adds and strategies.
     * 
     * @param productKeys keys of product to be added
     */
    SessionElementProduct addProduct(ProductKeysStruct productKeys) throws DataValidationException, TransactionFailedException;
    
    /**
     * Creates products based on template class definition.
     * 
     * @param sourceClass the template class that is the source of the products
     */
    void createProducts(TemplateClass sourceClass);
    
    /**
     * Finds a product by key.
     *
     * @param productKey key of requested product
     * @return found session product
     * @exception NotFoundException if product not active in session
     */
    SessionElementProduct findProduct(int productKey)
        throws NotFoundException;

    /**
     * Gets product class key for this session class.
     */
    int getClassKey();
    
    /**
    /**
     * Gets product class state for this session class.
     */
    short getClassState();
    
    /**
     * Gets a product by key.
     *
     * @param productKey key of requested product
     * @return found session product, null if product is not found
     */
    SessionElementProduct getProduct(int productKey);
    
    /**
     * Get all products active for this session class.
     *
     * @return all active products for session class
     */
    SessionElementProduct[] getProducts();
    
    /**
     * Sets the state for this class.
     * 
     * @param newState new state for this class
     */
    void setClassState(short newState);
}
