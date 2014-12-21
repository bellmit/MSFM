package com.cboe.domain.product;

// Source file: com/cboe/domain/product/ProductClassImpl.java

import com.cboe.interfaces.domain.product.*;
import com.cboe.domain.util.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.product.*;
import com.cboe.idl.cmiConstants.ProductTypes;
import com.cboe.idl.cmiConstants.ListingStates;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.infrastructureServices.persistenceService.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.util.ExceptionBuilder;
import java.util.*;
import java.lang.reflect.*;

/**
 * A persistent implementation of <code>ProductClass</code>.
 *
 * <dl compact>
 * <dt>Persistence Method</dt>
 * <b>Persistence Summary</b>
 * <dd>Relation database using JavaGrinder for mapping.</dd>
 * <dt>Table Name</dt>
 * <dd>The database table used is <code>PROD_CLASS</code></dd>
 * <dt>Product Relation</dt>
 * <dd>There is a one-to-many relationship between a <code>ProductClass</code> and all of the
 *     <code>Product</code>'s that are related to it.</dd>
 * <dt>Reporting Class Relation</dt>
 * <dd>There is a one-to-many relationship between a <code>ProductClass</code> and all of the
 *     <code>ReportingClass</code>'s that are related to it.</dd>
 * </dl>
 *
 * @author John Wickberg
 */

public class ProductClassImpl extends DomainBaseImpl implements ProductClass
{
  /**
   * Table name used for product classes.
   */
  public static final String TABLE_NAME = "prod_class";
  
  /**
   * static defs for location formatting
   */
    public static final String  POST_PREFIX         = "Post_";
    public static final String  LOCATION_DELIM      = ".";
    public static final String  STATION_PREFIX      = "Station_";
  
  /**
   * The symbol of this class.  This symbol must be unique by product type.
   */
  private String symbol;
  /**
   * The product type of this class.
   */
  private short productType;
	/**
	 * The product description of this class.
	 */
	private ProductDescription productDescription;
  /**
   * The reporting classes that belong to this product class.
   */
  private Vector reportingClasses;
  /**
   * The underlying product for this class.  An underlying product is only needed for derivative
   * classes.
   */
  private Product underlyingProduct;
  /**
   * The predominant exchange for the product.
   */
  private String primaryExchange;
  /**
   * Trading session code for this class.
   */
  private String sessionCode;
  /**
   * The defautlt transaction fee code for reporting classes of this product class.
   */
  private String defaultTransactionFeeCode;
  /**
   * The products that belong to this product class.
   */
  private Vector products;
  /**
   * The listing state of the class.
   */
  private short listingState;
  /**
   * Indication that this is a test class.
   */
  private boolean isTestClass;
  /**
   * The date when the product class will become active or did become active.
   */
  private long activationDate;
  /**
   * The date when the product class will become inactive or did become inactive.
   */
  private long inactivationDate;
  /**
   * Settlement type code of this class.
   */
  private short settlementType;
  /**
   * The locaton for this class.
   */
  private String location;
    /**
     * Qpe rollout flag
      */
  private char qpeIndicator;
  /**
   * linkageIndicator
    */
private char linkageIndicator;

  /**
   * Cached reference to implement for product home.
   */
  private static ProductHome productHome;
  /**
   * Cached reference to implement for product description home.
   */
  private static ProductDescriptionHome productDescriptionHome;

  /*
   * Vector of column descritions for persistence mapping.
   */
  private static Vector classDescriptor;
  /*
   * Field declarations for attributes, used for transactions
   */
    /**
    * extensions field
    */
    private String extensions;

  /**
   * The multilist of this class.
  */
  private boolean multilist;
    
  private static Field _symbol;
  private static Field _productType;
  private static Field _productDescription;
  private static Field _reportingClasses;
  private static Field _products;
  private static Field _underlyingProduct;
  private static Field _primaryExchange;
  private static Field _sessionCode;
  private static Field _listingState;
  private static Field _isTestClass;
  private static Field _activationDate;
  private static Field _inactivationDate;
  private static Field _defaultTransactionFeeCode;
  private static Field _settlementType;
  private static Field _location;
  private static Field _qpeIndicator;
  private static Field _linkageIndicator;
  private static Field _extensions;
  
  private static Field _multilist;

  /**
  * This static block will be regenerated if persistence is regenerated.
  * Modified to add multilist.
  * 
  * Modified by Cognizant Technology Solutions.
  */
  static
  {
	/*NAME:fieldDefinition:*/
	try
	{
	  _symbol = ProductClassImpl.class.getDeclaredField("symbol");
      _symbol.setAccessible(true);
	  _productType = ProductClassImpl.class.getDeclaredField("productType");
      _productType.setAccessible(true);
	  _productDescription = ProductClassImpl.class.getDeclaredField("productDescription");
      _productDescription.setAccessible(true);
	  _reportingClasses = ProductClassImpl.class.getDeclaredField("reportingClasses");
      _reportingClasses.setAccessible(true);
	  _products = ProductClassImpl.class.getDeclaredField("products");
      _products.setAccessible(true);
	  _underlyingProduct = ProductClassImpl.class.getDeclaredField("underlyingProduct");
      _underlyingProduct.setAccessible(true);
	  _primaryExchange = ProductClassImpl.class.getDeclaredField("primaryExchange");
      _primaryExchange.setAccessible(true);
	  _sessionCode = ProductClassImpl.class.getDeclaredField("sessionCode");
      _sessionCode.setAccessible(true);
	  _listingState = ProductClassImpl.class.getDeclaredField("listingState");
      _listingState.setAccessible(true);
      _isTestClass = ProductClassImpl.class.getDeclaredField("isTestClass");
      _isTestClass.setAccessible(true);
	  _activationDate = ProductClassImpl.class.getDeclaredField("activationDate");
      _activationDate.setAccessible(true);
	  _inactivationDate = ProductClassImpl.class.getDeclaredField("inactivationDate");
      _inactivationDate.setAccessible(true);
      _defaultTransactionFeeCode = ProductClassImpl.class.getDeclaredField("defaultTransactionFeeCode");
      _defaultTransactionFeeCode.setAccessible(true);
      _settlementType = ProductClassImpl.class.getDeclaredField("settlementType");
      _settlementType.setAccessible(true);
      _location = ProductClassImpl.class.getDeclaredField("location");
      _location.setAccessible(true);
      _qpeIndicator = ProductClassImpl.class.getDeclaredField("qpeIndicator");
      _qpeIndicator.setAccessible(true);
      _linkageIndicator = ProductClassImpl.class.getDeclaredField("linkageIndicator");
      _linkageIndicator.setAccessible(true);
      _extensions = ProductClassImpl.class.getDeclaredField("extensions");
      _extensions.setAccessible(true);
      _multilist = ProductClassImpl.class.getDeclaredField("multilist");
      _multilist.setAccessible(true);
	}
	catch (NoSuchFieldException ex)
	{
	  System.out.println(ex);
	}
  }
/**
 * Creates an instance with default values.
 */
public ProductClassImpl()
{
  super();
  setUsing32bitId(true);
}
/**
 * Adds a product to this class.
 *
 * @see ProductClass#addProduct
 */
public void addProduct(Product newProduct)
{
    getProductCollection().addElement(newProduct);
    // relation is bi-directional
    newProduct.setProductClass(this);
}
/**
 * Adds reporting class to this class.
 *
 * @see ProductClass#addReportingClass
 */
public void addReportingClass(ReportingClass newClass)
{
  getReportingClassCollection().addElement(newClass);
  // relation is bi-directional, so set reference in reporting class
  newClass.setProductClass(this);
}
/**
 * Updates this class to be a valid instance.
 *
 * @see ProductClass#create
 */
public void create(ClassDefinitionStruct newClass) throws DataValidationException
{
  try
  {
	setSymbol(newClass.classSymbol);
	setProductType(newClass.productType);
	setListingState(newClass.listingState);
    //setIsTestClass(false);
    setIsTestClass(newClass.testClass);
	setActivationDate(DateWrapper.convertToMillis(newClass.activationDate));
	setInactivationDate(DateWrapper.convertToMillis(newClass.inactivationDate));
    setProductDescription(newClass.descriptionName);
    setDefaultTransactionFeeCode(newClass.defaultTransactionFeeCode);
    setSettlementType(newClass.settlementType);
	if (isUnderlyingRequired(newClass.productType))
	{
	  if (newClass.underlyingProduct != null)
	  {
		Product underlyingProduct = findProduct(newClass.underlyingProduct.productKeys.productKey, newClass.underlyingProduct.productName);
		setUnderlyingProduct(underlyingProduct);
	  }
	  else
	  {
		throw ExceptionBuilder.dataValidationException("Required underlying product not supplied for class: " + newClass.classSymbol, DataValidationCodes.INVALID_PRODUCT);
	  }
	}
	setPrimaryExchange(newClass.primaryExchange);
	setProductCollection(createCollection(0));
	setReportingClassCollection(createCollection(0));
    setLinkageIndicator(true);
  }
  catch (NotFoundException e)
  {
	throw ExceptionBuilder.dataValidationException("Underlying product not found: " + e, DataValidationCodes.INVALID_PRODUCT);
  }
}
/**
 * Creates a collection for the reporting classes.
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
 * Searches for product having given key.
 *
 * @param productKey key of requested product
 * @param productName name of requested product. Only used if key is 0.
 * @return reference to found product
 * @exception NotFoundException if search fails
 */
private Product findProduct(int productKey, ProductNameStruct productName) throws NotFoundException
{
  if (productHome == null)
  {
	    try
	    {
		    productHome = (ProductHome) HomeFactory.getInstance().findHome(ProductHome.HOME_NAME);
		}
		catch (com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException e)
		{
		    throw new NullPointerException("Cannot not find ProductHome");
		}
  }
  if (productKey > 0)
  {
	return productHome.findByKey(productKey);
  }
  else
  {
	return productHome.findByName(productName);
  }
}
/**
 * Gets activation date of this class.
 *
 * @see ProductClass#getActivationDate
 */
public long getActivationDate()
{
  return editor.get(_activationDate, activationDate);
}
/**
 * Gets key of this class.
 *
 * @see ProductClass#getClassKey
 */
public int getClassKey()
{
  return getObjectIdentifierAsInt();
}
/**
 * Gets JavaGrinder class descriptor.  Will only be used by sub-classes.
 *
 * @return JavaGrinder attribute descriptions
 */
protected static java.util.Vector getDescriptor()
{
  return (java.util.Vector) classDescriptor.clone();
}
/**
 * Gets the Exchange Prescribed Width values for this class.
 */
protected EPWStruct[] getEPW() {
    // EPW is now by trading session, so there is no value for the sessionless class.  This value will be
    // filled in when classes are obtained through the TradingSessionService.
    return new EPWStruct[0];
}
/**
 * Gets the EPW fast market multiplier for this class.
 */
protected double getFastMarketMultiplier() {
    // Fast market multiplier is now by trading session, so there is no value for the sessionless class.  This value
    // will be filled in when classes are obtained through the TradingSessionService.
    return 1.0;
}
/**
 * Gets inactivation date of this class.
 *
 * @see ProductClass#getInactivationDate
 */
public long getInactivationDate()
{
  return editor.get(_inactivationDate, inactivationDate);
}
/**
 * Gets listing state of this class.
 *
 * @see ProductClass#getListingState
 */
public short getListingState()
{
	return editor.get(_listingState, listingState);
}
/**
* Gets test class indicator for product class
*/
public boolean isTestClass()
{
	return (boolean)editor.get(_isTestClass, isTestClass);
}
/**
 * Gets primary exchange of this class.
 *
 * @see ProductClass#getPrimaryExchange
 */
public String getPrimaryExchange()
{
  return (String) editor.get(_primaryExchange, primaryExchange);
}
/**
 * Gets collection used for product collection.
 *
 * @return product collection vector
 */
private Vector getProductCollection()
{
  return (Vector) editor.get(_products, products);
}
/**
 * Gets description of this class
 *
 * @see ProductClass#getProductDescription
 */
public ProductDescription getProductDescription() {
	return (ProductDescription) editor.get(_productDescription, productDescription);
}
/**
 * Gets ProductDescription home.
 */
public ProductDescriptionHome getProductDescriptionHome() {
    if (productDescriptionHome == null) {
        try {
            productDescriptionHome = (ProductDescriptionHome) HomeFactory.getInstance().findHome(ProductDescriptionHome.HOME_NAME);
        }
        catch (Exception e) {
            Log.alarm(this, "Unable to find product description home");
        }
    }
	return productDescriptionHome;
}
/**
 * Gets products related to this class.
 *
 * @see ProductClass#getProducts
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
 * Gets product type of this class.
 *
 * @see ProductClass#getProductType
 */
public short getProductType()
{
  return editor.get(_productType, productType);
}
/**
 * Gets collection used for reporting classes owned by this product class.
 *
 * @return reporting class vector
 */
private Vector getReportingClassCollection()
{
  return (Vector) editor.get(_reportingClasses, reportingClasses);
}
/**
 * Gets reporting classes of this class.
 *
 * @see ProductClass#getReportingClasses
 */
public ReportingClass[] getReportingClasses(boolean activeOnly)
{
  Vector copy;
  Vector source = getReportingClassCollection();
  if (activeOnly)
  {
	copy = createCollection(source.size());
	Enumeration rptClassesEnum = source.elements();
	ReportingClass rc;
	while (rptClassesEnum.hasMoreElements())
	{
	  rc = (ReportingClass) rptClassesEnum.nextElement();
	  if (rc.isActive())
	  {
		copy.addElement(rc);
	  }
	}
  }
  else
  {
	copy = source;
  }
  ReportingClass[] classes = new ReportingClass[copy.size()];
  copy.copyInto(classes);
  return classes;
}
/**
 * Gets trading session code for this class.
 *
 * @see ProductClass#getSessionCode
 */
public String getSessionCode()
{
  return (String) editor.get(_sessionCode, sessionCode);
}
/**
 * Gets the settlement type code.
 *
 * @see ProductClass#getSettlementType
 */
public short getSettlementType() {
    return editor.get(_settlementType, settlementType);
}
/**
 * Gets symbol of this class.
 *
 * @see ProductClass#getSymbol
 */
public String getSymbol()
{
  return (String) editor.get(_symbol, symbol);
}
/**
 * Gets the location
 *
 * @see ProductClass getLocation
 */
protected String getLocation()
{
    return (String)editor.get(_location, location);
}


public boolean getQpeIndicator()
{
    return (editor.get(_qpeIndicator, qpeIndicator) == '1');
}
public boolean isLinkageDisabled()
{
    return !(editor.get(_linkageIndicator, linkageIndicator) == '1');
}


/**
 * Gets default transaction fee code.
 *
 * @see ProductClass#getDefaultTransactionFeeCode
 */
public String getDefaultTransactionFeeCode() {
    return (String) editor.get(_defaultTransactionFeeCode, defaultTransactionFeeCode);
}
/**
 * Gets underlying product of this class.
 *
 * @see ProductClass#getUnderlyingProduct
 */
public Product getUnderlyingProduct()
{
  return (Product) editor.get(_underlyingProduct, underlyingProduct);
}
/**
 * Creates JavaGrinder descriptor for the database record of this object.
 * Modified for VSDL Download
 * Added multilist
 * 
 * Modified by Cognizant Technology Solutions.
 */
private void initDescriptor()
{
  synchronized (ProductClassImpl.class)
  {
	if (classDescriptor != null)
	{
	  return;  // already initialized
	}
	Vector tempDescriptor = super.getDescriptor();
	tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("class_sym", _symbol));
	tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("prod_type_code", _productType));
	tempDescriptor.addElement(AttributeDefinition.getForeignRelation(ProductDescriptionImpl.class, "prod_desc", _productDescription));
	tempDescriptor.addElement(AttributeDefinition.getCollectionRelation(ProductImpl.class, _products));
	tempDescriptor.addElement(AttributeDefinition.getCollectionRelation(ReportingClassImpl.class, _reportingClasses));
	tempDescriptor.addElement(AttributeDefinition.getForeignRelation(ProductImpl.class, "undly_prod", _underlyingProduct));
	tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("prim_exch_sym", _primaryExchange));
	tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("sess_code", _sessionCode));
    tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("default_trans_fee_code", _defaultTransactionFeeCode));
	tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("list_state", _listingState));
    tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("is_test_class", _isTestClass));
    tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("settlement_type", _settlementType));
    tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("act_date", _activationDate));
	tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("inact_date", _inactivationDate));
    tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("location", _location));
    tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("qpe_flag", _qpeIndicator));
    tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("linkage_indicator", _linkageIndicator));
    tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("extensions", _extensions));
    tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("multilist", _multilist));
    classDescriptor = tempDescriptor;
  }
}
/**
 * Creates JavaGrinder editor for this object.
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
 * Gets active indicator for this class.
 *
 * @see ProductClass#isActive
 */
public boolean isActive()
{
	return getListingState() == ListingStates.ACTIVE;
}
/**
 * Checks if product type requires an underlying.  All derivative products require an underlying.
 *
 * @param type product type code
 * @return <code>true</code> if underlying is required
 */
private boolean isUnderlyingRequired(int type)
{
  switch (type)
  {
	case ProductTypes.EQUITY:
	case ProductTypes.DEBT:
	case ProductTypes.COMMODITY:
	case ProductTypes.INDEX:
	case ProductTypes.VOLATILITY_INDEX:
	  return false;
	case ProductTypes.OPTION:
	case ProductTypes.FUTURE:
	case ProductTypes.WARRANT:
	case ProductTypes.UNIT_INVESTMENT_TRUST:
	case ProductTypes.LINKED_NOTE:
	case ProductTypes.STRATEGY:		// this requirement may be a result of strategies having only options
	  return true;
	default:
	  throw new IllegalArgumentException("Unknown product type code");
  }
}
/**
 * Cascades delete of this class to all of its reporting classes.
 *
 */
public void markForDelete() throws PersistenceException
{
	super.markForDelete();
    ReportingClass[] allReportingClasses = getReportingClasses(false);
	for (int i = 0; i < allReportingClasses.length; i++)
	{
		((ReportingClassImpl) allReportingClasses[i]).markForDelete();
	}
}
/**
 * Removes product from this class.
 *
 * @see ProductClass#removeProduct
 */
public void removeProduct(Product oldProduct)
{
	if (getProductCollection().contains(oldProduct))
	{
		getProductCollection().removeElement(oldProduct);
		// remove reference in product
		oldProduct.setProductClass(null);
	}
	else
	{
		Log.information(this, "Attempted to remove product from product class that is not part of class");
		Log.information(this, "Product class = " + getSymbol() + ", product key = " + oldProduct.getProductKey());
	}
}
/**
 * Removes reporting class from this class.
 *
 * @see ProductClass#removeReportingClass
 */
public void removeReportingClass(ReportingClass oldClass)
{
  getReportingClassCollection().removeElement(oldClass);
  // relation is bi-directional, so reset reference in reporting class
  oldClass.setProductClass(null);
}
/**
 * Sets activation date of this class.
 *
 * @see ProductClass#setActivationDate
 */
public void setActivationDate(long newDate)
{
  editor.set(_activationDate, newDate, activationDate);
}
/**
 * Sets inactivation date of this class.
 *
 * @see ProductClass#setInactivationDate
 */
public void setInactivationDate(long newDate)
{
  editor.set(_inactivationDate, newDate, inactivationDate);
}
/**
 * Sets listing state of this class.
 *
 * @param newState new listing state
 */
public void setListingState(short newState)
{
	if (ProductStructBuilder.isValidListingState(newState))
	{
		editor.set(_listingState, newState, listingState);

        // Cascade INACTIVE and OBSOLETE states to reporting classes
        if (newState == ListingStates.INACTIVE || newState == ListingStates.OBSOLETE)
        {
            ReportingClass[] allReportingClasses = getReportingClasses(false);
        	for (int i = 0; i < allReportingClasses.length; i++)
        	{
        		allReportingClasses[i].setListingState(newState);
        	}
        }
	}
	else
	{
		throw new IllegalArgumentException("Invalid listing state value = " + newState);
	}
}
/**
* Sets test class indicator for product class
*/
public void setIsTestClass(boolean aValue)
{
	editor.set(_isTestClass, aValue, isTestClass);
}
/**
 * Sets primary exchange of this class.
 *
 * @see ProductClass#setPrimaryExchange
 */
public void setPrimaryExchange(String newSymbol)
{
  editor.set(_primaryExchange, newSymbol, primaryExchange);
}
/**
 * Sets collection used for product collection.
 *
 * @param newCollection new product collection vector
 */
private void setProductCollection(Vector newCollection)
{
  editor.set(_products, newCollection, products);
}
/**
 * Sets product description for this product class.
 *
 * @see ProductClass#setProductDescription
 */
private void setProductDescription(String newDescription) throws DataValidationException {
    if (newDescription != null && newDescription.length() > 0) {
        try {
            ProductDescription description = getProductDescriptionHome().findByName(newDescription);
            setProductDescription(description);
        }
        catch (NotFoundException e) {
            throw new DataValidationException(e.details);
        }
    }
}
/**
 * Sets product description for this product class.
 *
 * @see ProductClass#setProductDescription
 */
public void setProductDescription(ProductDescription newDescription) {
  editor.set(_productDescription, newDescription, productDescription);
}
/**
 * Sets product type for this product class.
 *
 * @param newType new product type
 */
public void setProductType(short newType)
{
  editor.set(_productType, newType, productType);
}
/**
 * Sets collection used for reporting classes owned by this product class.
 *
 * @param newCollection reporting class collection
 */
private void setReportingClassCollection(Vector newCollection)
{
  editor.set(_reportingClasses, newCollection, reportingClasses);
}
/**
 * Sets the defauls transaction fee code.
 */
private void setDefaultTransactionFeeCode(String newValue) {
    editor.set(_defaultTransactionFeeCode, newValue, defaultTransactionFeeCode);
}
/**
 * Sets trading session code for this class.
 *
 * @see ProductClass#setSessionCode
 */
public void setSessionCode(String newCode)
{
  editor.set(_sessionCode, newCode, sessionCode);
}
/**
 * Sets settlement type code.
 *
 * @see ProductClass#setSettlementType
 */
public void setSettlementType(short newCode) {
    // Not all sources of updates supply this value, so don't override existing values with 0 (which is not a
    // valid code
    if (newCode != 0) {
        editor.set(_settlementType, newCode, settlementType);
    }
}
/**
 * Sets symbol of this class.
 *
 * @see ProductClass#setSymbol
 */
public void setSymbol(String newSymbol)
{
  editor.set(_symbol, newSymbol, symbol);
}
/**
 * Sets underlying product of this class.
 *
 * @see ProductClass#setUnderlyingProduct
 */
public void setUnderlyingProduct(Product newUnderlying)
{
  editor.set(_underlyingProduct, newUnderlying, underlyingProduct);
}
/**
 * Sets location of this class.
 *
 * @see ProductClass#setLocation
 */
public void setLocation(String postNumber, String stationNumber)
{
    String  newLocation = POST_PREFIX + postNumber + LOCATION_DELIM + STATION_PREFIX + stationNumber;
    
    editor.set(_location, newLocation, location);
}

/**
 *
  * @param qpeInd
 */
public void setQpeIndicator(boolean qpeInd){
   char qpeFlag = qpeInd?'1':'0';
   editor.set(_qpeIndicator, qpeFlag, qpeIndicator);
}

public void setLinkageIndicator(boolean p_linkageIndicator){
    char setLinkageValue = p_linkageIndicator?'1':'0';
    editor.set(_linkageIndicator, setLinkageValue, linkageIndicator);
 }
/**
 * Converts this class to CORBA struct.
 *
 * @see ProductClass#toStruct
 */
public ProductClassStruct toStruct(boolean includeReportingClasses, boolean includeProducts, boolean includeActiveOnly)
{
  ProductClassStruct aStruct = ProductStructBuilder.buildProductClassStruct();
  aStruct.sessionCode = StructBuilder.nullToEmpty(getSessionCode());
  aStruct.defaultTransactionFeeCode = StructBuilder.nullToEmpty(getDefaultTransactionFeeCode());
  aStruct.settlementType = getSettlementType();
  aStruct.info.testClass = isTestClass();
  aStruct.info.classKey = getClassKey();
  aStruct.info.classSymbol = getSymbol();
  aStruct.info.productType = getProductType();
  Product underlyingProduct = getUnderlyingProduct();
  if (underlyingProduct != null)
  {
	aStruct.info.underlyingProduct = underlyingProduct.toStruct();
  }
  ProductDescription productDescription = getProductDescription();
  if (productDescription != null)
  {
  	aStruct.info.productDescription = productDescription.toStruct();
  }
  aStruct.info.epwValues = getEPW();
  aStruct.info.epwFastMarketMultiplier = getFastMarketMultiplier();
  aStruct.info.primaryExchange = StructBuilder.nullToEmpty(getPrimaryExchange());
  aStruct.info.listingState = getListingState();
  aStruct.info.activationDate = DateWrapper.convertToDate(getActivationDate());
  aStruct.info.inactivationDate = DateWrapper.convertToDate(getInactivationDate());
  aStruct.info.createdTime = DateWrapper.convertToDateTime(getCreatedTime());
  aStruct.info.lastModifiedTime = DateWrapper.convertToDateTime(getLastModifiedTime());
  if (includeReportingClasses)
  {
	ReportingClass[] classes = getReportingClasses(includeActiveOnly);
	aStruct.info.reportingClasses = new ReportingClassStruct[classes.length];
	for (int i = 0; i < classes.length; i++)
	{
	  aStruct.info.reportingClasses[i] = classes[i].toStruct();
	}
  }
  if (includeProducts)
  {
	Product[] products = getProducts(includeActiveOnly);
	aStruct.products = new ProductStruct[products.length];
	for (int i = 0; i < products.length; i++)
	{
	  aStruct.products[i] = products[i].toStruct();
	}
  }
  return aStruct;
}

private boolean isUnderlyingProvided(ProductStruct prod)
{
    if (prod == null || prod.productKeys.productKey == 0 || prod.productName.productSymbol == null ||prod.productName.productSymbol.equals(""))
    {
        return false;
    }

    return true;
}
/**
 * Updates this class from a CORBA struct.
 *
 * @see ProductClass#update
 */
public void update(ClassDefinitionStruct updatedClass) throws DataValidationException
{
    try
    {
        // Underlying product must be changed through adjustments
        setSymbol(updatedClass.classSymbol);
        setPrimaryExchange(updatedClass.primaryExchange);
        setProductDescription(updatedClass.descriptionName);
        setListingState(updatedClass.listingState);
        setDefaultTransactionFeeCode(updatedClass.defaultTransactionFeeCode);
        setIsTestClass(updatedClass.testClass);

        //if the underlying is not provided by checking the product key = 0 or product sym = "", not update the underlying
        if (isUnderlyingRequired(updatedClass.productType) && isUnderlyingProvided(updatedClass.underlyingProduct) )
        {
            Product underlyingProduct = findProduct(updatedClass.underlyingProduct.productKeys.productKey, updatedClass.underlyingProduct.productName);
            setUnderlyingProduct(underlyingProduct);
        }
        setSettlementType(updatedClass.settlementType);
        setActivationDate(DateWrapper.convertToMillis(updatedClass.activationDate));
        setInactivationDate(DateWrapper.convertToMillis(updatedClass.inactivationDate));
      }
      catch (NotFoundException e)
      {
        throw ExceptionBuilder.dataValidationException("Underlying product not found: " + e, DataValidationCodes.INVALID_PRODUCT);
      }
}

public int getExpirationStyle(){
    if (getProductType() == ProductTypes.FUTURE){
        return ExpirationDateFactory.FRIDAY_EXPIRATION;
    }
    return ExpirationDateFactory.SATURDAY_EXPIRATION;
}
    /**
     * get post and station location information from the product class
     * 
     * internal format is Post_n.Station_n
     */
    public String getPost()
    {
        String  returnPost = "";
        
        String  loc = getLocation();
        if ( loc != null )
        {
            int sepLoc = loc.indexOf( LOCATION_DELIM );
            returnPost = loc.substring( 0, sepLoc );
        }
        return returnPost;
    }
    
    public String getStation()
    {
        String  returnStation = "";
        
        String  loc = getLocation();
        if ( loc != null )
        {
            int sepLoc = loc.indexOf( LOCATION_DELIM );
            returnStation = loc.substring( sepLoc + 1 );
        }
        return returnStation;
    }

    public void setExtensions(String newExtensions)
    {
        editor.set(_extensions, newExtensions, extensions);
    }

    public String getExtensions()
    {
        return (String)editor.get(_extensions, extensions);
    }
       
    /**
     * Gets multilist indicator for product class
     */
    public boolean isMultilist()
    {
        return (boolean)editor.get(_multilist, multilist);
    }
    /**
     * Sets multilist of this class.
     *
     * @see ProductClass#setMultilist
     */
    public void setMultilist(boolean newMultilist)
    {
      editor.set(_multilist, newMultilist, multilist);
    }
    
    /**
     * This method builds the ProductClassStructV4 and returns back to product query service.
     * 
     * @param includeReportingClasses
     * @param includeProducts
     * @param includeActiveOnly
     * @return ProductClassStructV4
     * 
     * @author Cognizant Technology Solutions.
     */
    public ProductClassStructV4 toProductClassStructV4(boolean includeReportingClasses, boolean includeProducts, boolean includeActiveOnly)
    {
        ProductClassStructV4 result = ProductStructBuilder.buildProductClassStructV4();        
        result.productClass = new ProductClassStruct();
        result.productClass = toStruct(includeReportingClasses, includeProducts, includeActiveOnly);
        
        result.classSettlement = new ClassSettlementStructV3();
        result.classSettlement.productLocation = new ProductLocationStruct();
        result.classSettlement.productLocation.postNumber = getPost();
        result.classSettlement.productLocation.stationNumber = getStation();
        result.classSettlement.multilist = isMultilist();
        result.classSettlement.classKey = getClassKey();
        result.classSettlement.extension = getExtensions();
        if(includeProducts)
        {
            Product products[] = getProducts(includeActiveOnly);
            ProductSettlementStructV2[] productSettlement = new ProductSettlementStructV2[products.length];
            for(int j=0;j <products.length; j++)
            {
                productSettlement[j] = new ProductSettlementStructV2();
                productSettlement[j].productSettlementStruct = new ProductSettlementStruct();
                productSettlement[j].productSettlementStruct.productKeys = products[j].toKeysStruct();
                productSettlement[j].productSettlementStruct.settlementPrice = products[j].getSettlementPrice().toStruct();
                productSettlement[j].openInterest = products[j].getOpenInterest();
                productSettlement[j].settlementSuffix = products[j].getSettlementPriceSuffix();
            }
            result.classSettlement.productSettlements =  productSettlement;
        }
        else
        {
            result.classSettlement.productSettlements = new ProductSettlementStructV2[0]; 
        }
                return result;
    }
    
    public ProductClassStructV5 toStructV5(boolean p_activeOnly, boolean p_includeReportingClasses, boolean p_includeProducts)
    {
        ProductClassStructV5 aStruct = ProductStructBuilder.buildProductClassStructV5();        
        aStruct.defaultTransactionFeeCode = StructBuilder.nullToEmpty(getDefaultTransactionFeeCode());
        aStruct.settlementType = getSettlementType();
        aStruct.sessionCode = getSessionCode();
        aStruct.extensions = getExtensions();
        aStruct.multiList = isMultilist();
        aStruct.info.testClass = isTestClass();
        aStruct.info.classKey = getClassKey();
        aStruct.info.classSymbol = getSymbol();
        aStruct.info.productType = getProductType();
        Product underlyingProduct = getUnderlyingProduct();
        if (underlyingProduct != null)
        {
            aStruct.info.underlyingProduct = underlyingProduct.toStruct();
        }
        ProductDescription productDescription = getProductDescription();
        if (productDescription != null)
        {
            aStruct.info.productDescription = productDescription.toStruct();
        }
        aStruct.info.epwValues = getEPW();
        aStruct.info.epwFastMarketMultiplier = getFastMarketMultiplier();
        aStruct.info.primaryExchange = StructBuilder.nullToEmpty(getPrimaryExchange());
        aStruct.info.listingState = getListingState();
        aStruct.info.activationDate = DateWrapper.convertToDate(getActivationDate());
        aStruct.info.inactivationDate = DateWrapper.convertToDate(getInactivationDate());
        aStruct.info.createdTime = DateWrapper.convertToDateTime(getCreatedTime());
        aStruct.info.lastModifiedTime = DateWrapper.convertToDateTime(getLastModifiedTime());
        aStruct.info.primaryExchange = getPrimaryExchange();
        if (p_includeReportingClasses)
        {
            ReportingClass[] classes = getReportingClasses(p_activeOnly);
            aStruct.info.reportingClasses = new ReportingClassStructV2[classes.length];
            for (int i = 0; i < classes.length; i++)
            {
                ReportingClassStructV2 rptClass = ProductStructBuilder.buildReportingClassStructV2();
                rptClass.reportingClass =  classes[i].toStruct();
                rptClass.extensions = classes[i].getExtensions();
                aStruct.info.reportingClasses[i] =  rptClass;
            }
        }
        if (p_includeProducts)
        {
            Product[] products = getProducts(p_activeOnly);
            aStruct.products = new ProductStructV4[products.length];
            for (int i = 0; i < products.length; i++)
            {
                aStruct.products[i] = ProductStructBuilder.buildProductStructV4();
                aStruct.products[i].product = products[i].toStruct();
                aStruct.products[i].cusip = products[i].getCusip();
                aStruct.products[i].extensions = products[i].getExtensions();
                aStruct.products[i].restrictedProductIndicator = products[i].getRestrictedIndicator();
                aStruct.products[i].closingSuffix = products[i].getSettlementPriceSuffix();
                aStruct.products[i].closingPrice = products[i].getSettlementPrice().toStruct();
            }
        }
        aStruct.productLocation = new ProductLocationStruct();
        aStruct.productLocation.postNumber = getPost();
        aStruct.productLocation.stationNumber = getStation();
        return aStruct;
    }        
}
