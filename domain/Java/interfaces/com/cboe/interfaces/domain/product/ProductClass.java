package com.cboe.interfaces.domain.product;

// Source file: com/cboe/interfaces/domain/product/ProductClass.java

import com.cboe.interfaces.domain.DomainBase;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.product.*;

/**
 * A collection of products of the same product type. All derivative products
 * of a product class have a common underlying product.
 *
 * @author John Wickberg
 */
public interface ProductClass extends DomainBase
{
	/**
	 * Adds product to the product class.
	 *
	 * @param newProduct product to be added
	 */
	public void addProduct(Product newProduct);
	/**
	 * Adds a reporting class to the list of classes for this product class.
	 *
	 * @param newClass class to be added
	 */
	public void addReportingClass(ReportingClass newClass);
	/**
	 * Changes this instance to be an instance of the product class represented
	 * by the CORBA struct.
	 *
	 * @param newClass struct containing values for new class
	 * @exception DataValidationException if validation checks fail
	 */
	public void create(ClassDefinitionStruct newClass) throws DataValidationException;
	/**
	 * Gets activation date of this product class.
	 *
	 * @return activation date
	 */
	public long getActivationDate();
	/**
	 * Gets key of this product class.
	 *
	 * @return product class key
	 */
	public int getClassKey();
    /**
     * Gets the default transaction fee code for reporting classes of this product class.
     *
     * @return transaction fee code
     */
    public String getDefaultTransactionFeeCode();
	/**
	 * Gets inactivation date of this product class.
	 *
	 * @return inactivation date
	 */
	public long getInactivationDate();
	/**
	 * Gets the listing state of this class.
	 *
	 * @return listing state
	 */
	public short getListingState();
	/**
	 * Gets the primary exchange assigned to this product class.
	 *
	 * @return symbol of primary exchange
	 */
	public String getPrimaryExchange();
	/**
	 * Gets the product description for this class.
	 *
	 * @return reference to product description
	 */
	public ProductDescription getProductDescription();
	/**
	 * Gets the products that belong to this product class.
	 *
	 * @param activeOnly if <code>true</code> restricts the result to be only
	 * products that are active
	 * @return array of products
	 */
	public Product[] getProducts(boolean activeOnly);
	/**
	 * Gets product type of this product class.
	 *
	 * @return product type
	 */
	public short getProductType();
	/**
	 * Gets reporting classes belonging to this product class.
	 *
	 * @param activeOnly option to restrict result to active reporting classes.
	 * @return array of reporting classes
	 */
	public ReportingClass[] getReportingClasses(boolean activeOnly);
	/**
	 * Gets trading session code for this product class.
	 *
	 * @return trading session code
	 */
	public String getSessionCode();
    /**
     * Gets the settlement type code.  The code indictates how the settlement price for expiring products is
     * selected.  AM settled products do not trade on expiration Friday.
     *
     * @return settlement type code
     */
    public short getSettlementType();
	/**
	 * Gets the symbol of this product class.
	 *
	 * @return product class symbol
	 */
	public String getSymbol();
    /**
	 * Gets the underlying for this product class.  Only product classes for
	 * derivative products will have an underlying product.
	 *
	 * @return underlying product or <code>null</code> if product class is not
	 * for a derivative product.
	 */
	public Product getUnderlyingProduct();
	/**
	 * Checks to see if this product class is active for trading.
	 *
	 * @return <code>true</code> if this class is active
	 *
	 * @author John Wickberg
	 */
	public boolean isActive();
 	/**
	 * Checks to see if this product class is a test class.
	 *
	 * @return <code>true</code> if this class is a test class.
	 *
	 * @author Kevin Park
	 */
	public boolean isTestClass();
	/**
	 * Removes product from the product class.
	 *
	 * @param oldProduct product to be removed
	 */
	public void removeProduct(Product oldProduct);
	/**
	 * Removes a reporting class from the list of classes for this product class.
	 *
	 * @param oldClass class to be removed
	 */
	public void removeReportingClass(ReportingClass oldClass);
	/**
	 * Sets activation date of this product class.
	 *
	 * @param newDate new activation date
	 */
	public void setActivationDate(long newDate);
 	/**
	 * Sets isTestClass flag for this product class.
	 *
	 * @param isTestClass flag
	 */
	public void setIsTestClass(boolean isTestClass);
	/**
	 * Sets inactivation date of this product class.
	 *
	 * @param newDate new inactivation date
	 */
	public void setInactivationDate(long newDate);
	/**
	 * Sets listing state of this class.
	 *
	 * @param newState new state value
	 */
	public void setListingState(short newState);
	/**
	 * Sets the primary exchange assigned to this product class.
	 *
	 * @param newSymbol symbol of assigned primary exchange
	 */
	public void setPrimaryExchange(String newSymbol);
	/**
	 * Sets the product description for this class.
	 *
	 * @param newDescription reference to product description for this class
	 */
	public void setProductDescription(ProductDescription newDescription);
	/**
	 * Sets trading session code for this product class.
	 *
	 * @param newCode new trading session code for this product class
	 */
	public void setSessionCode(String newCode);
    /**
     * Sets the settlement type code.  The code indictates how the settlement price for expiring products is
     * selected.  AM settled products do not trade on expiration Friday.
     *
     * @param newCode new settlement type code
     */
    public void setSettlementType(short newCode);
	/**
	 * Sets the symbol of this product class.
	 *
	 * @param newSymbol symbol assigned to product class
	 */
	public void setSymbol(String newSymbol);
	/**
	 * Sets the underlying for this product class.  Only product classes for
	 * derivative products will have an underlying product.
	 *
	 * @param newUnderlying new underlying product
	 */
	public void setUnderlyingProduct(Product newUnderlying);
	/**
	 * Converts this product class to a CORBA struct.
	 *
	 * @param includeReportingClasses if <code>true</code>, reporting classes detail will be
	 *                                included in the result.
	 * @param includeReportedProducts if <code>true</code>, product detail will be included
	 *                                in reported class detail (if requested).
	 * @param includeActiveOnly if <code>true</code>, only active reporting classes and products
	 *                          will be included.
	 * @return product class struct
	 */
	public ProductClassStruct toStruct(boolean includeReportingClasses, boolean includeReportedProducts, boolean includeActiveOnly);
	/**
	 * Updates this product class with values from CORBA struct.
	 *
	 * @param updatedClass struct containing updated values
	 * @exception DataValidationException if updates fail validation checks
	 */
	public void update(ClassDefinitionStruct updatedClass) throws DataValidationException;

    /**
     * Return the expiration style for the class.
     *
     * Expiration style includes:
     *
     * 1. saturday expiration
     * 2. friday expiration
     */
    public int getExpirationStyle();
    
    /**
     * get/set post and station location information from the product class
     */
    public void setLocation(String postNumber, String stationNumber);
    public String getPost();
    public String getStation();

    public void setQpeIndicator(boolean qpeIndicator);
    public boolean getQpeIndicator();

    /*
     *	get/set extensions field
     */
    public void setExtensions(String extensions);
    public String getExtensions();
    
    /**
     * Indicator stating product class is traded at CBOE and also at other exchanges.
     *
     * @return <code>true</code> if this class traded
     *
     * @author Cognizant Technology Solutions.
     */
    public boolean isMultilist();
    
    /**
     * Sets multilist for the class.
     *
     * @param multilist multilist value
     *  
     * @author Cognizant Technology Solutions
     */
    public void setMultilist(boolean newMultilist);
    
    /**
     * This method builds ProductClassStructV4
     * @param includeReportingClasses
     * @param includeProducts
     * @param includeActiveOnly
     * @return ProductClassStructV4
     * @author Cognizant Technology Solutions.
     */
    public ProductClassStructV4 toProductClassStructV4(boolean includeReportingClasses, boolean includeProducts, boolean includeActiveOnly);
    
    public void setLinkageIndicator(boolean indicator);
    public boolean isLinkageDisabled();
    public ProductClassStructV5 toStructV5(boolean p_activeOnly, boolean p_includeReportingClasses, boolean p_includeProducts);

    
    
}
