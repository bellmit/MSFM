package com.cboe.domain.product;

// Source file: com/cboe/domain/product/ProductImpl.java

import com.cboe.interfaces.domain.product.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiConstants.ListingStates;
import com.cboe.idl.cmiStrategy.*;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.domain.util.*;
import com.cboe.util.ExceptionBuilder;
import com.cboe.interfaces.domain.Price;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.interfaces.domain.ExpirationDate;


/**
 * An editor for <code>Product</code>'s.  Editor classes are used to access the
 * attributes of <code>ProductImpl</code> that are applicable for the type of
 * product of the editor.
 *
 * @author John Wickberg
 */
public abstract class ProductEditor implements Product
{
	/**
	 * Reference to the product assigned to this editor.
	 */
	private ProductImpl product;
	/**
	 * A cached instance of the reporting class home.  Used to find the reporting class of new
	 * products.
	 */
	private static ReportingClassHome reportingClassHome;
/**
 * Creates new instance with default values.
 */
public ProductEditor(ProductImpl editedProduct)
{
	super();
	setProduct(editedProduct);
}
/**
 * Creates valid instance of <code>Product</code>.
 * <p>
 * Subclasses should extend this method using the following as the first line of code:
 * <code><pre>
 * super.create(newProduct);
 * </pre></code>
 * </p>
 *
 * @see Product#create
 */
public void create(ProductStruct newProduct) throws DataValidationException
{
	if (ProductStructBuilder.isDefaultState(newProduct.listingState))
	{
		getProduct().setListingState(ListingStates.UNLISTED);
	}
	else
	{
		getProduct().setListingState(newProduct.listingState);
	}
 	try
  	{
		ReportingClass rc = findReportingClass(newProduct.productName.reportingClass, newProduct.productKeys.productType);
		rc.addProduct(getProduct());
		rc.getProductClass().addProduct(getProduct());
 	}
  	catch (NotFoundException e)
   	{
		throw ExceptionBuilder.dataValidationException("Reporting class not found for new product", DataValidationCodes.INVALID_REPORTING_CLASS);
  	}
	getProduct().setActivationDate(DateWrapper.convertToMillis(newProduct.activationDate));
	getProduct().setInactivationDate(DateWrapper.convertToMillis(newProduct.inactivationDate));
}
/**
 * Fills in the product struct with values defined at this level.
 *
 * <p>
 * Subclasses should extend this method using the following as the first line of code:
 * <code><pre>
 * super.fillInStruct(aStruct);
 * </pre></code>
 * </p>
 *
 * @see #toStruct
 *
 * @param aStruct CORBA struct being filled in with values from this object
 */
protected void fillInStruct(ProductStruct aStruct)
{
	aStruct.productKeys = toKeysStruct();
	aStruct.listingState = getListingState();
	aStruct.productName = getProductName();
	aStruct.activationDate = DateWrapper.convertToDate(getActivationDate());
	aStruct.inactivationDate = DateWrapper.convertToDate(getInactivationDate());
	aStruct.createdTime = DateWrapper.convertToDateTime(getCreatedTime());
	aStruct.lastModifiedTime = DateWrapper.convertToDateTime(getLastModifiedTime());
}
/**
 * Finds reporting class for symbol and type.
 *
 * @param classSymbol reporting class symbol
 * @param productType product type of reporting class
 * @return found reporting class
 * @exception NotFoundException if reporting class search fails
 */
private static ReportingClass findReportingClass(String classSymbol, short productType) throws NotFoundException
{
	if (reportingClassHome == null)
	{
	    try
	    {
		    reportingClassHome = (ReportingClassHome) HomeFactory.getInstance().findHome(ReportingClassHome.HOME_NAME);
		}
		catch (com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException e)
		{
		    throw new NullPointerException("Cannot not find ReportingClassHome");
		}
	}
	return reportingClassHome.findBySymbol(classSymbol, productType);
}
/**
 * Gets activation date of this product.
 *
 * @see Product#getActivationDate
 */
public long getActivationDate()
{
	return getProduct().getActivationDate();
}
/**
 * Gets created time of this product.
 *
 * @return time product was created
 */
public long getCreatedTime()
{
	return getProduct().getCreatedTime();
}
/**
 * Gets inactivation date of this product.
 *
 * @see Product#getInactivationDate
 */
public long getInactivationDate()
{
	return getProduct().getInactivationDate();
}
/**
 * Gets time this product was last modified.
 *
 * @return time product was last modified
 */
public long getLastModifiedTime()
{
	return getProduct().getLastModifiedTime();
}
/**
 *
 */
public Price getSettlementPrice()
{
	return getProduct().getSettlementPrice();
}
/**
 *
 */
public String getCusip()
{
    return getProduct().getCusip( );
}

/**
 *
 */
public int getOpenInterest()
{
	return getProduct().getOpenInterest();
}
/**
* Gets OpenInterestUpdateTime for Product
*/
public long getOpenInterestUpdateTime()
{
	return getProduct().getOpenInterestUpdateTime();
}

/**
 *
 */
public void setSettlementPrice(Price newPrice)
{
    getProduct().setSettlementPrice( newPrice );
}

/**
 *
 */
public void setCusip(String newCusip)
{
    getProduct().setCusip( newCusip );
}

/**
 *
 */
public void setOpenInterest( int newInterest )
{
    getProduct().setOpenInterest( newInterest );
}
/**
* Sets OpenInterestUpdateTime for Product
*/
public void setOpenInterestUpdateTime( long openInterestUpdateTime )
{
   getProduct().setOpenInterestUpdateTime( openInterestUpdateTime );
}
/**
 * Gets state of this product.
 *
 * @return current product state
 */
public short getListingState()
{
	return getProduct().getListingState();
}
/**
 * Gets description of this product.
 */
public String getDescription()
{
    return getProduct().getDescription();
}

/**
 * Gets product of this editor.
 *
 * @return this editor's product
 */
protected ProductImpl getProduct()
{
	return product;
}
/**
 * Gets the <code>ProductClass</code> of this product.
 *
 * @see Product#getProductClass
 */
public ProductClass getProductClass()
{
	return getProduct().getProductClass();
}
/**
 * Gets editor for this product.
 *
 * @return self
 */
public Product getProductEditor()
{
	return this;
}
/**
 * Gets the key of this product.
 *
 * @see Product#getProductKey
 */
public int getProductKey()
{
	return getProduct().getProductKey();
}
/**
 * Gets product type value for this product.
 *
 * @return product type value
 */
private short getProductType()
{
	return getProduct().getProductType();
}
/**
 * Gets the reporting class of this product.
 *
 * @see Product#getReportingClass
 */
public ReportingClass getReportingClass()
{
	return getProduct().getReportingClass();
}
/**
 * Checks if product is active.
 *
 * @see Product#isActive
 */
public boolean isActive()
{
	return getProduct().isActive();
}
/**
 * Resets the last modified time of this product.
 *
 */
public void resetLastModifiedTime()
{
	getProduct().resetLastModifiedTime();
}
/**
 * Sets the activation date to the current time.
 */
private void setActivationDate()
{
	setActivationDate( System.currentTimeMillis() );
}
/**
 * Sets activation date of this product.
 *
 * @see Product#setActivationDate
 */
public void setActivationDate(long newDate)
{
	getProduct().setActivationDate(newDate);
}
/**
 * Sets inactivation date of this product.
 *
 * @see Product#setInactivationDate
 */
public void setInactivationDate(long newDate)
{
	getProduct().setInactivationDate(newDate);
}
/**
 * Sets product state.
 *
 * @param newState the new state for the product
 */
public void setListingState(short newState)
{
	getProduct().setListingState(newState);
}

/**
 * Sets description of this product.
 *
 * @see Commodity#setDescription
 */
public void setDescription(String newDescription)
{
    getProduct().setDescription(newDescription);
}

/**
 * Sets product of this editor.
 *
 * @param aProduct product assigned to editor
 */
private void setProduct(ProductImpl aProduct)
{
	product = aProduct;
}
/**
 * Sets <code>ProductClass</code> of this product.
 *
 * @see Product#setProductClass
 */
public void setProductClass(ProductClass newClass)
{
	getProduct().setProductClass(newClass);
}
/**
 * Sets product type value.
 *
 * @param newType product type value
 */
private void setProductType(short newType)
{
	getProduct().setProductType(newType);
}
/**
 * Sets <code>ReportingClass</code> of this product.
 *
 * @see Product#setReportingClass
 */
public void setReportingClass(ReportingClass newClass)
{
	getProduct().setReportingClass(newClass);
}
/**
 * Creates a struct containing the product keys of this product.
 *
 * @see Product#toKeysStruct
 */
public ProductKeysStruct toKeysStruct()
{
	return getProduct().toKeysStruct();
}
/**
 * Converts this product to a CORBA struct.
 * <p>
 * To extend this implementation for a new product type, the <code>fillInStruct</code> method of
 * the new type must be implemented.  The first line of this method should be:
 * <code><pre>
 * super.fillInStruct()
 * </pre></code>
 * </p>
 *
 * @see Product#toStruct
 *
 * @return CORBA struct representing product
 */
public final ProductStruct toStruct()
{
	ProductStruct productStruct = ProductStructBuilder.buildProductStruct();
	fillInStruct(productStruct);
	return productStruct;
}
/**
 * Converts this product to a strategy struct.  This method is a default
 * implementation so all products can create this struct.
 *
 * @see Product#toStrategyStruct
 */
public StrategyStruct toStrategyStruct() {
	// create a strategy struct with no legs
	StrategyStruct result = new StrategyStruct();
	result.product = toStruct();
	result.strategyLegs = new StrategyLegStruct[0];
	return result;
}
/**
 * Updates this product.
 *
 * @see Product#update
 */
public void update(ProductStruct updatedProduct) throws DataValidationException
{
    setListingState(updatedProduct.listingState);
    if (!StructBuilder.isDefault(updatedProduct.inactivationDate))
    {
        setInactivationDate(DateWrapper.convertToMillis(updatedProduct.inactivationDate));
    }
    if (!StructBuilder.isDefault(updatedProduct.activationDate))
    {
        setActivationDate(DateWrapper.convertToMillis(updatedProduct.activationDate));
    }
}
/**
 * Updates this product.
 *
 * @see Product#updateName
 */
public void updateName(ProductNameStruct newName)
{
	// No attributes at this level can be changed using this method
}

/**
 * @param newExtensions
 */
public void setExtensions(String newExtensions)
{
    getProduct().setExtensions(newExtensions);
}

/**
 * @return extensions
 */
public String getExtensions()
{
    return getProduct().getExtensions();
}

/**
 * Sets expiration date of this product
 */
public void setExpirationDate(ExpirationDate newDate)
{
   // getProduct().setExpirationDate(newDate);
}

public boolean getRestrictedIndicator()
{
    return getProduct().getRestrictedIndicator();
}

public void setRestrictedIndicator(boolean indicator)
{
    getProduct().setRestrictedIndicator(indicator);
}

public void setNewReportingClassName(String newRptClassSym)
{
   getProduct().setNewReportingClassName(newRptClassSym);
}

}
