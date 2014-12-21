package com.cboe.domain.product;

// Source file: com/cboe/domain/product/ReportingClassImpl.java

import com.cboe.interfaces.domain.product.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiConstants.ListingStates;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.domain.util.*;
import com.cboe.util.*;
import java.util.*;
import com.cboe.infrastructureServices.persistenceService.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import java.lang.reflect.*;

/**
 * A persistent implementation of <code>ReportingClass</code>.
 *
 * @author John Wickberg
 */
public class ReportingClassImpl extends DomainBaseImpl implements ReportingClass
{
  /**
   * Table name used for product classes.
   */
  public static final String TABLE_NAME = "rpt_class";
  /**
   * The class symbol.  Must be unique by product type.
   */
  private String symbol;
  /**
   * The type of products contained in the class.
   */
  private short productType;
  /**
   * The number of underlying products per contract.  This is only used for
   * derivative products.
   */
  private int contractSize;
  /**
   * The transaction fee code for this class.  If the code is not set for the
   * reporting class, the default code of it's product class will be used.
   */
  private String transactionFeeCode;
  /**
   * The date when the class will be or was made active.
   */
  private long activationDate;
  /**
   * The date when the class was or will be made inactive.
   */
  private long inactivationDate;
  /**
   * The listing state of the class.
   */
  private short listingState;
    /**
     *  Extensions field
     */
  private String extensions;
  /**
   * The product class that owns this reporting class.
   */
  private ProductClass productClass;
  /**
   * The products that belong to this reporting class.
   */
  private Vector reportedProducts;
  /**
   * Cached reference to the product class home implementation.
   */
  private static ProductClassHome productClassHome;
  /*
   * Vector of column descritions for persistence mapping.
   */
  private static Vector classDescriptor;
  /*
   * Field declarations for attributes, used for transactions
   */
  private static Field _symbol;
  private static Field _productType;
  private static Field _productClass;
  private static Field _reportedProducts;
  private static Field _contractSize;
  private static Field _listingState;
  private static Field _activationDate;
  private static Field _inactivationDate;
  private static Field _transactionFeeCode;
  private static Field _extensions;
  /**
  * This static block will be regenerated if persistence is regenerated.
  */
  static
  {
	/*NAME:fieldDefinition:*/
	try
	{
	  _symbol = ReportingClassImpl.class.getDeclaredField("symbol");
      _symbol.setAccessible(true);
	  _productType = ReportingClassImpl.class.getDeclaredField("productType");
      _productType.setAccessible(true);
	  _productClass = ReportingClassImpl.class.getDeclaredField("productClass");
      _productClass.setAccessible(true);
	  _reportedProducts = ReportingClassImpl.class.getDeclaredField("reportedProducts");
      _reportedProducts.setAccessible(true);
	  _contractSize = ReportingClassImpl.class.getDeclaredField("contractSize");
      _contractSize.setAccessible(true);
	  _listingState = ReportingClassImpl.class.getDeclaredField("listingState");
      _listingState.setAccessible(true);
	  _activationDate = ReportingClassImpl.class.getDeclaredField("activationDate");
      _activationDate.setAccessible(true);
	  _inactivationDate = ReportingClassImpl.class.getDeclaredField("inactivationDate");
      _inactivationDate.setAccessible(true);
      _transactionFeeCode = ReportingClassImpl.class.getDeclaredField("transactionFeeCode");
      _transactionFeeCode.setAccessible(true);
      _extensions = ReportingClassImpl.class.getDeclaredField("extensions");
      _extensions.setAccessible(true);
	}
	catch (NoSuchFieldException ex)
	{
	  System.out.println(ex);
	}
  }
/**
 * Creates an instance with default values.
 */
public ReportingClassImpl()
{
  super();
  setUsing32bitId(true);
}
/**
 * @see ReportingClass#addProduct
 */
public void addProduct(Product newProduct)
{
  // Won't check for duplication, since product name must be unique.
  getProductCollection().addElement(newProduct);
  // relation is bi-directional
  newProduct.setReportingClass(this);
}
/**
 * @see ReportingClass#create
 */
public void create(ReportingClassStruct newClass) throws DataValidationException
{
  try
  {
	setProductCollection(createCollection(0));
	ProductClass pc = findProductClass(newClass);
	pc.addReportingClass(this);
	setSymbol(newClass.reportingClassSymbol);
	setProductType(newClass.productType);
	setListingState(newClass.listingState);
	setActivationDate(DateWrapper.convertToMillis(newClass.activationDate));
	setInactivationDate(DateWrapper.convertToMillis(newClass.inactivationDate));
	setContractSize(newClass.contractSize);
    setTransactionFeeCode(newClass.transactionFeeCode);
  }
  catch (NotFoundException e)
  {
	throw ExceptionBuilder.dataValidationException(e.details.message, DataValidationCodes.INVALID_REPORTING_CLASS);
  }
}
/**
 * Creates a collection for the reported products.
 *
 * @param initialSize inital size of collection if > 0
 * @return created collection
 */
private Vector createCollection(int initialSize)
{
  if (initialSize > 0)
  {
	return new Vector(initialSize);
  }
  else
  {
	return new Vector();
  }
}
/**
 * Finds the requested product class.
 *
 * @param newClass ReportingClass
 * @return found product class
 */
private ProductClass findProductClass(ReportingClassStruct newClass) throws NotFoundException
{
	if (productClassHome == null)
	{
		try
		{
			productClassHome = (ProductClassHome) HomeFactory.getInstance().findHome(ProductClassHome.HOME_NAME);
		}
		catch (com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException e)
		{
			throw new NullPointerException("Cannot not find ProductClassHome");
		}
	}
	ProductClass result;
	if (newClass.productClassKey > 0) {
		result = productClassHome.findByKey(newClass.productClassKey);
	}
	else {
		result = productClassHome.findBySymbol(newClass.productClassSymbol, newClass.productType);
	}
	return result;
}
/**
 * @see ReportingClass#getActivationDate
 */
public long getActivationDate()
{
  return editor.get(_activationDate, activationDate);
}
/**
 * @see ReportingClass#getClassKey
 */
public int getClassKey()
{
  return getObjectIdentifierAsInt();
}
/**
 * @see ReportingClass#getContractSize
 */
public int getContractSize()
{
  return editor.get(_contractSize, contractSize);
}
/**
* A conveince method to get a copy of this static class descriptor.
*/
public static java.util.Vector getDescriptor()
{
  return (java.util.Vector) classDescriptor.clone();
}
/**
 * @see ReportingClass#getInactivationDate
 */
public long getInactivationDate()
{
  return editor.get(_inactivationDate, inactivationDate);
}
/**
 * Gets listing state of this class.
 *
 * @see ReportingClass#getListingState
 */
public short getListingState()
{
	return editor.get(_listingState, listingState);
}
/**
 * @see ReportingClass#getProductClass
 */
public ProductClass getProductClass()
{
  return (ProductClass) editor.get(_productClass, productClass);
}
/**
 * Gets collection used for product collection.
 *
 * @return product collection vector
 */
private Vector getProductCollection()
{
  return (Vector) editor.get(_reportedProducts, reportedProducts);
}
/**
 * @see ReportingClass#getProducts
 */
public Product[] getProducts(boolean activeOnly)
{
  Vector copy;
  Vector source = getProductCollection();
  if (activeOnly)
  {
	copy = createCollection(source.size());
	Enumeration productsEnum = source.elements();
	Product product;
	while (productsEnum.hasMoreElements())
	{
	  product = (Product) productsEnum.nextElement();
	  if (product.isActive())
	  {
		copy.addElement(product);
	  }
	}
  }
  else
  {
	copy = source;
  }
  Product[] products = new Product[copy.size()];
  copy.copyInto(products);
  return products;
}
/**
 * @see ReportingClass#getProductType
 */
public short getProductType()
{
  return editor.get(_productType, productType);
}
/**
 * @see ReportingClass#getSymbol
 */
public String getSymbol()
{
  return (String) editor.get(_symbol, symbol);
}
/**
 * Gets transaction fee code.  If code isn't set, use default of product class.
 */
private String getTransactionFeeCode() {
    String result = (String) editor.get(_transactionFeeCode, transactionFeeCode);
    if (result == null) {
        result = getProductClass().getDefaultTransactionFeeCode();
    }
    return result;
}
/**                                                        
* Describe how this class relates to the relational database.
*/
public void initDescriptor()
{
  synchronized (ReportingClassImpl.class)
  {
	if (classDescriptor != null)
	{
	  return; // already initialized
	}
	Vector tempDescriptor = super.getDescriptor();
	tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("class_sym", _symbol));
	tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("prod_type_code", _productType));
	tempDescriptor.addElement(AttributeDefinition.getForeignRelation(ProductClassImpl.class, "prod_class", _productClass));
	tempDescriptor.addElement(AttributeDefinition.getCollectionRelation(ProductImpl.class, _reportedProducts));
	tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("contr_size", _contractSize));
    tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("trans_fee_code", _transactionFeeCode));
	tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("list_state", _listingState));
	tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("act_date", _activationDate));
	tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("inact_date", _inactivationDate));
    tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("extensions", _extensions));
    classDescriptor = tempDescriptor;
  }
}
/**
* Needed to define table name and the description of this class.
*/
public ObjectChangesIF initializeObjectEditor()
{
  final DBAdapter result = (DBAdapter) super.initializeObjectEditor();
  if (classDescriptor == null)
	initDescriptor();
  result.setTableName(TABLE_NAME);
  result.setClassDescription(classDescriptor);
  return result;
}
/**
 * @see ReportingClass#isActive
 */
public boolean isActive()
{
  return getListingState() == ListingStates.ACTIVE;
}
/**
 * Cascades delete to all product of this reporting class.
 *
 */
public void markForDelete() throws PersistenceException
{
	super.markForDelete();
    // need to use array - enumerator will not be stable due to products deleting themselves.
	Product[] allProducts = getProducts(false);
	for (int i = 0; i < allProducts.length; i++)
	{
		((ProductImpl) allProducts[i]).markForDelete();
	}
	// remove this class from parent
	getProductClass().removeReportingClass(this);
}
/**
 * @see ReportingClass#removeProduct
 */
public void removeProduct(Product oldProduct)
{
  // Won't check for existence.
  getProductCollection().removeElement(oldProduct);
  // remove reference in product
  oldProduct.setReportingClass(null);
}
/**
 * @see ReportingClass#setActivationDate
 */
public void setActivationDate(long newDate)
{
  editor.set(_activationDate, newDate, activationDate);
}
/**
 * @see ReportingClass#setContractSize
 */
public void setContractSize(int newSize)
{
  editor.set(_contractSize, newSize, contractSize);
}
/**
 * @see ReportingClass#setInactivationDate
 */
public void setInactivationDate(long newDate)
{
  editor.set(_inactivationDate, newDate, inactivationDate);
}
/**
 * Sets listing state of this class.
 *
 * @see ReportingClass#setListingState
 */
public void setListingState(short newState)
{
	editor.set(_listingState, newState, listingState);
    // Cascade listing state to products if it is INACTIVE or OBSOLETE
    if (newState == ListingStates.INACTIVE || newState == ListingStates.OBSOLETE)
    {
        Product[] allProducts = getProducts(false);
        for (int i = 0; i < allProducts.length; i++)
        {
            allProducts[i].setListingState(newState);
        }
    }
}
/**
 * @see ReportingClass#setProductClass
 */
public void setProductClass(ProductClass newClass)
{
  editor.set(_productClass, newClass, productClass);
}
/**
 * Sets collection used for product collection.
 *
 * @param newCollection new product collection vector
 */
private void setProductCollection(Vector newCollection)
{
  editor.set(_reportedProducts, newCollection, reportedProducts);
}
/**
 * Sets product type of this reporting class.
 *
 * @param newType new product type
 */
protected void setProductType(short newType)
{
  editor.set(_productType, newType, productType);
}
/**
 * @see ReportingClass#setSymbol
 */
public void setSymbol(String newSymbol)
{
	if (isRetrievedFromDatabase() && !newSymbol.equals(getSymbol()))
	{
		// Since symbol is changing, need to insert products into transaction so that product
		// names will be updated.
		Enumeration productsEnum = getProductCollection().elements();
		ProductImpl product;

        while (productsEnum.hasMoreElements())
        {
            product = (ProductImpl) productsEnum.nextElement();
            try
            {
                if (!product.getNewReportingClassName().equals(""))
                {
                    // if the current reporting class name is same as 
                    // the value cached in the product name, reset the cachd name
                    if (product.getNewReportingClassName().equals(getSymbol()))
                    {
                        product.setNewReportingClassName(""); 
                        Log.information(this, "Resetting product's cached reporting class symbol, for productKey= " + product.getProductKey()); 
                    }
                }
                product.insert();
            }
            catch (PersistenceException e)
            {
                
            }
        }
	}
	editor.set(_symbol, newSymbol, symbol);
}
/**
 * Sets the tranasction fee code for this class.  If the new value is the same as the default value for the product
 * class, the value for the reporting class will be set to null.
 *
 * @param newValue new transaction fee code
 */
private void setTransactionFeeCode(String newValue) {
    String defaultValue = getProductClass().getDefaultTransactionFeeCode();
    String savedValue = newValue;
    if (defaultValue != null && newValue != null && defaultValue.equals(newValue)) {
        savedValue = null;
    }
    editor.set(_transactionFeeCode, savedValue, transactionFeeCode);
}
/**
 * @see ReportingClass#toStruct
 */
public ReportingClassStruct toStruct()
{
  ReportingClassStruct aStruct = ProductStructBuilder.buildReportingClassStruct();
  aStruct.classKey = getClassKey();
  aStruct.reportingClassSymbol = getSymbol();
  aStruct.productType = getProductType();
  aStruct.productClassSymbol = getProductClass().getSymbol();
  aStruct.productClassKey = getProductClass().getClassKey();
  aStruct.contractSize = getContractSize();
  aStruct.transactionFeeCode = getTransactionFeeCode();
  aStruct.listingState = getListingState();
  aStruct.activationDate = DateWrapper.convertToDate(getActivationDate());
  aStruct.inactivationDate = DateWrapper.convertToDate(getInactivationDate());
  aStruct.createdTime = DateWrapper.convertToDateTime(getCreatedTime());
  aStruct.lastModifiedTime = DateWrapper.convertToDateTime(getLastModifiedTime());
  return aStruct;
}
/**
 * @see ReportingClass#update
 */
public void update(ReportingClassStruct updatedClass)
{
  // Product class must be changed through adjustments
  setSymbol(updatedClass.reportingClassSymbol);
  setContractSize(updatedClass.contractSize);
  setListingState(updatedClass.listingState);
  setTransactionFeeCode(updatedClass.transactionFeeCode);
  setActivationDate(DateWrapper.convertToMillis(updatedClass.activationDate));
  setInactivationDate(DateWrapper.convertToMillis(updatedClass.inactivationDate));
}

/**
 * @param newExtensions
 */
public void setExtensions(String newExtensions)
{
    editor.set(_extensions, newExtensions, extensions);
}

/**
 * @return extensions
 */
public String getExtensions()
{
    return (String)editor.get(_extensions, extensions);
}
}
