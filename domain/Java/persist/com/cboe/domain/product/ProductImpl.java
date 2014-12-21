package com.cboe.domain.product;

// Source file: com/cboe/domain/product/ProductImpl.java

import com.cboe.interfaces.domain.dateTime.Date;
import com.cboe.interfaces.domain.product.*;
import com.cboe.interfaces.domain.ExpirationDate;
import com.cboe.interfaces.domain.Price;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiConstants.ProductTypes;
import com.cboe.idl.cmiConstants.ListingStates;
import com.cboe.idl.cmiStrategy.*;
import com.cboe.domain.util.*;
import com.cboe.infrastructureServices.persistenceService.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import java.lang.reflect.*;
import java.util.Vector;

/**
 * A persistent implementation of <code>Product</code>.  This implementation
 * contains a union of all attributes for all possible product types.  Editor
 * classes for each product type are used to select the attributes that are
 * applicable for each type.
 *
 * <p>
 * Note that all attributes
 * of this class are protected so that instances can be set when doing queries.
 * </p>
 *
 * <dl compact>
 * <b>Persistence Summary</b>
 * <dt>Persistence Method</dt>
 * <dd>Relation database using JavaGrinder for mapping.</dd>
 * <dt>Table Name</dt>
 * <dd>The database table used is <code>PRODUCT</code></dd>
 * <dt>Product Class Relation</dt>
 * <dd>A <code>Product</code> has a foreign key to its <code>ProductClass</code>.</dd>
 * <dt>Reporting Class Relation</dt>
 * <dd>A <code>Product</code> has a foreign key to its <code>ReportingClass</code>.</dd>
 * </dl>
 *
 * @author John Wickberg
 */
public class ProductImpl extends DomainBaseImpl implements Product
{
	/**
	 * Table name used for products.
	 */
	public static final String TABLE_NAME = "product";
	/**
	 * Product type code.  This value is needed so that persistent instances can be
	 * read.
	 */
	protected short productType;
        /**
         * Product sub-type code.
         */
        protected short productSubType;
	/**
	 * Current state of this product.
	 */
	protected short listingState;
	/**
	 * Owning product class of this product.
	 */
	protected ProductClass productClass;
	/**
	 * Owning reporting class of this product.
	 */
	protected ReportingClass reportingClass;
	/**
	 * Name of product converted to string.  Will be used for a uniqueness constraint.
	 */
	protected String productName;
	/**
	 * The date that the product was (or will be) made active for trading.
	 */
	protected long activationDate;
	/**
	 * The date that the product was (or will be) made inactive for trading.
	 */
	protected long inactivationDate;
	/**
	 * Trading symbol of this product.
	 */
	protected String symbol;
	/**
	 * A description of the commodity.
	 */
	protected String description;
	/**
	 * The units of measure in which the commodity is traded.
	 */
	protected String unitMeasure;
	/**
	 * The standard number of units in a single trade.
	 */
	protected double standardQuantity;
	/**
	 * The name of the company issuing the debt.
	 */
	protected String companyName;
	/**
	 * The date when the debt matures.
	 */
	protected long maturityDate;
	/**
	 * The date that this derivative product expires.
	 */
	protected ExpirationDateImpl expireDate;
	/**
	 * The price at which the underlying product can be bought (call) or sold (put)
	 * when this option is exercised.
	 */
	protected PriceSqlType exercisePrice;
    /**
     *
     */
	protected PriceSqlType settlementPrice;
    /**
     *
     */
    protected int openInterest;
    
    /**
     * The OpenInterestUpdateTime for the Product
     */
    protected long openInterestUpdateTime;
	/**
	 * The type of this option, call or put.
	 */
	protected OptionTypeImpl optionType;
	/**
	 * A code used by OPRA to designate the month and type of this option.
	 */
	protected char opraMonthCode;
	/**
	 * A code used by OPRA to designate the exercise price of this option.
	 */
	protected char opraPriceCode;

	/**
	 * CUSIP code for securities
	 */
	protected String cusip;
    /**
	 * extensions for this product
	 */
	protected String extensions;
	/**
	 * Editor of this product.
	 */
	private Product productEditor;
    
    /*
     * Restricted Product Indicator.
     */
    private boolean restrictedIndicator;    
	/**
	 * prev.day's closing price.
	 */
	private PriceSqlType yesterdaysClosePrice;
	/**
	 * prev.day's closing price suffix.
	 */
	private String yesterdaysClosePriceSuffix;
	/**
	 * today's closing price suffix.
	 */
	private String settlementPriceSuffix;

    // WARNING - need this here - for setListingState
    private static ProductHomeImpl cachedProductHome;

	/*
	 * Java Grinder attributes.
	 */
	private static Vector classDescriptor;
	private static Field _productType;
        private static Field _productSubType;
	private static Field _listingState;
	private static Field _productClass;
	private static Field _reportingClass;
	private static Field _productName;
	private static Field _activationDate;
	private static Field _inactivationDate;
	private static Field _symbol;
	private static Field _description;
	private static Field _unitMeasure;
	private static Field _standardQuantity;
	private static Field _companyName;
	private static Field _maturityDate;
	private static Field _expireDate;
	private static Field _exercisePrice;
	private static Field _settlementPrice;
	private static Field _openInterest;
	private static Field _openInterestUpdateTime;
	private static Field _optionType;
	private static Field _opraMonthCode;
	private static Field _opraPriceCode;
	private static Field _cusip;
    private static Field _extensions;
    private static Field _restrictedIndicator;
    private static Field _settlementPriceSuffix;
    private static Field _yesterdaysClosePrice;
    private static Field _yesterdaysClosePriceSuffix;

	/**
	* This static block will be regenerated if persistence is regenerated.
	*/
	static
	{
		/*NAME:fieldDefinition:*/
		try
		{
			_productType = ProductImpl.class.getDeclaredField("productType");
            _productType.setAccessible( true );
			_productSubType = ProductImpl.class.getDeclaredField("productSubType");
            _productSubType.setAccessible( true );
			_listingState = ProductImpl.class.getDeclaredField("listingState");
            _listingState.setAccessible( true );
			_productClass = ProductImpl.class.getDeclaredField("productClass");
            _productClass.setAccessible( true );
			_reportingClass = ProductImpl.class.getDeclaredField("reportingClass");
            _reportingClass.setAccessible( true );
			_productName = ProductImpl.class.getDeclaredField("productName");
            _productName.setAccessible( true );
			_activationDate = ProductImpl.class.getDeclaredField("activationDate");
            _activationDate.setAccessible( true );
			_inactivationDate = ProductImpl.class.getDeclaredField("inactivationDate");
            _inactivationDate.setAccessible( true );
			_symbol = ProductImpl.class.getDeclaredField("symbol");
            _symbol.setAccessible( true );
			_description = ProductImpl.class.getDeclaredField("description");
            _description.setAccessible( true );
			_unitMeasure = ProductImpl.class.getDeclaredField("unitMeasure");
            _unitMeasure.setAccessible( true );
			_standardQuantity = ProductImpl.class.getDeclaredField("standardQuantity");
            _standardQuantity.setAccessible( true );
			_companyName = ProductImpl.class.getDeclaredField("companyName");
            _companyName.setAccessible( true );
			_maturityDate = ProductImpl.class.getDeclaredField("maturityDate");
            _maturityDate.setAccessible( true );
			_expireDate = ProductImpl.class.getDeclaredField("expireDate");
            _expireDate.setAccessible( true );
			_exercisePrice = ProductImpl.class.getDeclaredField("exercisePrice");
            _exercisePrice.setAccessible( true );
			_settlementPrice = ProductImpl.class.getDeclaredField("settlementPrice");
            _settlementPrice.setAccessible( true );
			_openInterest = ProductImpl.class.getDeclaredField("openInterest");
            _openInterest.setAccessible( true );
			_optionType = ProductImpl.class.getDeclaredField("optionType");
            _optionType.setAccessible( true );
			_opraMonthCode = ProductImpl.class.getDeclaredField("opraMonthCode");
            _opraMonthCode.setAccessible( true );
			_opraPriceCode = ProductImpl.class.getDeclaredField("opraPriceCode");
            _opraPriceCode.setAccessible( true );
			_cusip = ProductImpl.class.getDeclaredField("cusip");
            _cusip.setAccessible( true );
            _extensions = ProductImpl.class.getDeclaredField("extensions");
            _extensions.setAccessible( true );
            _restrictedIndicator = ProductImpl.class.getDeclaredField("restrictedIndicator");
            _restrictedIndicator.setAccessible( true );
            _openInterestUpdateTime = ProductImpl.class.getDeclaredField("openInterestUpdateTime");
            _openInterestUpdateTime.setAccessible( true );
            _settlementPriceSuffix = ProductImpl.class.getDeclaredField("settlementPriceSuffix");
            _settlementPriceSuffix.setAccessible( true );
            _yesterdaysClosePrice = ProductImpl.class.getDeclaredField("yesterdaysClosePrice");
            _yesterdaysClosePrice.setAccessible( true );
            _yesterdaysClosePriceSuffix = ProductImpl.class.getDeclaredField("yesterdaysClosePriceSuffix");
            _yesterdaysClosePriceSuffix.setAccessible( true );
            
		}
		catch (NoSuchFieldException ex)
		{
			System.out.println(ex);
		}

        SqlScalarTypeInitializer.initTypes();
	}
    
    private transient String newReportingClassName = "";
    
/**
 * Creates new instance with default values.
 */
public ProductImpl()
{
	super();
    setUsing32bitId(true);
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
    setProductType(newProduct.productKeys.productType);
    setDescription(newProduct.description);
    getProductEditor().create(newProduct);
}

/**
 * Performs double dispatch handling for products.
 *
 * @see Product#dispatch
 *
 * @param handler double dispatch handler
 */
public Object dispatch(ProductDispatch handler, Object context)
{
	return getProductEditor().dispatch(handler, context);
}
/**
 * Compares to products for equality.  Products are equal if the keys are equal.
 *
 * @param object object to be compared to this product
 * @return true if products have same key
 */
public boolean equals(Object object)
{
	if (object instanceof ProductImpl)
	{
		return getProductKey() == ((ProductImpl) object).getProductKey();
	}
	else
	{
		return false;
	}
}
/**
 * Gets activation date of this product.
 *
 * @see Product#getActivationDate
 */
public long getActivationDate()
{
	return editor.get(_activationDate, activationDate);
}
/**
 * Gets company name of this product.
 *
 * @see Debt#getCompanyName
 */
protected String getCompanyName()
{
	return (String) editor.get(_companyName, companyName);
}
/**
 * Gets description of this product.
 */
public String getDescription()
{
	return (String) editor.get(_description, description);
}
/**
 * Gets a copy of the JavaGrinder attribute descriptor.
 *
 * @return JavaGrinder attribute descriptor
 */
public static Vector getDescriptor()
{
	return (Vector) classDescriptor.clone();
}
/**
 * Gets exercise price of this product.
 *
 * @see Option#getExercisePrice
 */
protected Price getExercisePrice()
{
	return (Price) editor.get(_exercisePrice, exercisePrice);
}
/**
 *
 */
public Price getSettlementPrice()
{

	Price result = (Price) editor.get(_settlementPrice, settlementPrice );
    // Don't return null value - change null to no price.  Prevents external
    // code from getting null pointer even on new products.
    if (result == null) {
        result = new NoPrice();
    }
    return result;
}
/* (non-Javadoc)
 * @see com.cboe.interfaces.domain.product.Product#getOpenInterestUpdateTime()
 */
public long getOpenInterestUpdateTime()
{
	return editor.get(_openInterestUpdateTime, openInterestUpdateTime );
}
/**
 *
 */
public int getOpenInterest()
{
	return editor.get(_openInterest, openInterest );
}
/**
 * Gets expiration date of this product.
 *
 * @see Derivative#getExpirationDate
 */
protected ExpirationDate getExpirationDate()
{
	return (ExpirationDate) editor.get(_expireDate, expireDate);
}
/**
 * Gets inactivation date of this product.
 *
 * @see Product#getInactivationDate
 */
public long getInactivationDate()
{
	return editor.get(_inactivationDate, inactivationDate);
}
/**
 * Gets state of this product.
 *
 * @return current product state
 */
public short getListingState()
{
	return (short) editor.get(_listingState, listingState);
}
/**
 * Gets maturity date of this product.
 *
 * @see Debt#getMaturityDate
 */
protected long getMaturityDate()
{
	return editor.get(_maturityDate, maturityDate);
}
/**
 * Gets OPRA month code of this product.
 *
 * @see Option#getOpraMonthCode
 */
protected char getOpraMonthCode()
{
	return editor.get(_opraMonthCode, opraMonthCode);
}
/**
 * Gets OPRA price code of this product.
 *
 * @see Option#getOpraPriceCode
 */
protected char getOpraPriceCode()
{
	return editor.get(_opraPriceCode, opraPriceCode);
}
/**
 * Gets CUSIP for securities. 
 *
 */
public String getCusip()
{
	return (String)editor.get(_cusip, cusip);
}
/**
 * Gets option type of this product.
 *
 * @see Option#getOptionType
 */
protected OptionType getOptionType()
{
	return (OptionType) editor.get(_optionType, optionType);
}
/**
 * Gets the <code>ProductClass</code> of this product.
 *
 * @see Product#getProductClass
 */
public ProductClass getProductClass()
{
	return (ProductClass) editor.get(_productClass, productClass);
}
/**
 * Gets the editor for this product.
 *
 * @return editor used to update this product
 */
public Product getProductEditor()
{
	if (productEditor == null)
	{
		switch (getProductType())
		{
			case ProductTypes.COMMODITY :
				productEditor = new CommodityEditor(this);
				break;
			case ProductTypes.DEBT :
				productEditor = new DebtEditor(this);
				break;
			case ProductTypes.EQUITY :
				productEditor = new EquityEditor(this);
				break;
			case ProductTypes.OPTION :
				productEditor = new OptionEditor(this);
				break;
			case ProductTypes.FUTURE :
				productEditor = new FutureEditor(this);
				break;
			case ProductTypes.LINKED_NOTE :
				productEditor = new LinkedNoteEditor(this);
				break;
			case ProductTypes.UNIT_INVESTMENT_TRUST :
				productEditor = new UnitInvestmentTrustEditor(this);
				break;
			case ProductTypes.WARRANT :
				productEditor = new WarrantEditor(this);
				break;
			case ProductTypes.INDEX :
				productEditor = new IndexEditor(this);
				break;
			case ProductTypes.VOLATILITY_INDEX :
				productEditor = new VolatilityIndexEditor(this);
				break;
		    case ProductTypes.STRATEGY :
				productEditor = new StrategyEditor(this);
				break;
			default :
				throw new IllegalArgumentException("Unexpected product type value: " + productType);
		}
	}
	return productEditor;
}
/**
 * Gets the key of this product.
 *
 * @see Product#getProductKey
 */
public int getProductKey()
{
	return getObjectIdentifierAsInt();
}
/**
 * Gets product name of this product.
 *
 * @see Product#getProductName
 *
 * @return struct containing product name
 */
public ProductNameStruct getProductName()
{
	ProductNameStruct result = getProductEditor().getProductName();
    if (!getNewReportingClassName().equals("") && !getNewReportingClassName().equals(result.reportingClass))
    {
        result.reportingClass = getNewReportingClassName();
        Log.information(this, "Might be done by IPD PRODUCT change: getProductName(), with new Reporting Class Name " + getNewReportingClassName() + ", for ProductKey = " + getProductKey());
    }
    return result;
}
/**
 * Gets product type value for this product.
 *
 * @return product type value
 */
protected short getProductType()
{
	return editor.get(_productType, productType);
}
/**
 * Gets product sub type value for this product.
 *
 * @return product sub type value
 */
public short getProductSubType()
{
	return editor.get(_productSubType, productSubType);
}

/**
 * Gets the reporting class of this product.
 *
 * @see Product#getReportingClass
 */
public ReportingClass getReportingClass()
{
	return (ReportingClass) editor.get(_reportingClass, reportingClass);
}
/**
 * Gets standard contract quantity of this product.
 *
 * @see Commodity#getStandardQuantity
 */
protected double getStandardQuantity()
{
	return editor.get(_standardQuantity, standardQuantity);
}
/**
 * Gets symbol of this product.
 */
protected String getSymbol()
{
	return (String) editor.get(_symbol, symbol);
}
/**
 * Gets unit of measure of this product.
 *
 * @see Commodity#getUnitMeasure
 */
protected String getUnitMeasure()
{
	return (String) editor.get(_unitMeasure, unitMeasure);
}
/**
 * Returns product key as hash code.
 */
public int hashCode()
{
	return getProductKey();
}
/**
 * Creates JavaGrinder descriptor for the database record of this object.
 */
private void initDescriptor()
{
	synchronized (ProductImpl.class)
	{
		if (classDescriptor != null)
		{
			return; // already initialized
		}
		Vector tempDescriptor = super.getDescriptor();
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("prod_type_code", _productType));
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("prod_sub_type_code", _productSubType));
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("list_state_code", _listingState));
		tempDescriptor.addElement(AttributeDefinition.getForeignRelation(ProductClassImpl.class, "prod_class", _productClass));
		tempDescriptor.addElement(AttributeDefinition.getForeignRelation(ReportingClassImpl.class, "rpt_class", _reportingClass));
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("prod_name", _productName));
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("act_date", _activationDate));
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("inact_date", _inactivationDate));
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("prod_sym", _symbol));
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("prod_desc", _description));
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("unit_meas", _unitMeasure));
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("std_qty", _standardQuantity));
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("comp_name", _companyName));
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("matur_date", _maturityDate));
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("expr_date", _expireDate));
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("exer_price", _exercisePrice));
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("opt_type_code", _optionType));
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("opra_month_code", _opraMonthCode));
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("opra_price_code", _opraPriceCode));
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("settlement_price", _settlementPrice ));
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("open_interest", _openInterest ));
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("cusip", _cusip ));
        tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("extensions", _extensions ));
        tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("restrictedIndicator", _restrictedIndicator ));
        tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("open_interest_update_time", _openInterestUpdateTime ));
        tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("settlement_price_suffix", _settlementPriceSuffix ));
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("yesterdays_close_price", _yesterdaysClosePrice ));
        tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("yesterdays_close_price_suffix", _yesterdaysClosePriceSuffix ));
        
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
 * Checks if product is active.
 *
 * @see Product#isActive
 */
public boolean isActive()
{
	return getListingState() == ListingStates.ACTIVE;
}
/**
 * Checks if product is of requested type.
 *
 * @see Product#isProductType
 */
public boolean isProductType(short productType)
{
    return getProductEditor().isProductType(productType);
}
/**
 * Removes this product from parent reporting class and product class.
 *
 */
public void markForDelete() throws PersistenceException
{
	super.markForDelete();
	getReportingClass().removeProduct(this);
	getProductClass().removeProduct(this);
}
/**
 * Saves product to database.  Overriding normal save method to set productName value
 * before actual database save.
 *
 * @exception PersistenceException if saving of this product fails
 */
public void save() throws PersistenceException
{
	// Create string version of name for uniqueness constraint    
    productName = ProductStructBuilder.toString(getProductName());
    	
    // Change product name of inactive product to contain time stamp.  This will prevent duplicate
    // unique key violations if a price adjustment is done and it changes an active product to have
    // the same same as an inactive product.
    if (!isActive()) {
        productName = productName + ":" + System.currentTimeMillis();
    }
	// need to continue saving process
	super.save();
}
/**
 * Sets activation date of this product.
 *
 * @see Product#setActivationDate
 */
public void setActivationDate(long newDate)
{
	editor.set(_activationDate, newDate, activationDate);
}
/**
 * Sets company name of this product.
 *
 * @see Debt#setCompanyName
 */
protected void setCompanyName(String newName)
{
	editor.set(_companyName, newName, companyName);
}
/**
 * Sets description of this product.
 *
 * @see Commodity#setDescription
 */
public void setDescription(String newDescription)
{
	editor.set(_description, newDescription, description);
}
/**
 * Sets exercise price of this product.
 *
 * @see Option#setExercisePrice
 */
protected void setExercisePrice(Price newPrice)
{
	// Convert Price's to PriceSqlType's
	if (!(newPrice instanceof PriceSqlType))
	{
		newPrice = new PriceSqlType(newPrice.toStruct());
	}
	editor.set(_exercisePrice, newPrice, exercisePrice);
}
/**
 * Sets expiration date of this product.
 *
 * @see Derivative#setExpirationDate
 */
public void setExpirationDate(ExpirationDate newDate)
{
	editor.set(_expireDate, newDate, expireDate);
}
/**
 * Sets inactivation date of this product.
 *
 * @see Product#setInactivationDate
 */
public void setInactivationDate(long newDate)
{
	editor.set(_inactivationDate, newDate, inactivationDate);
}
/**
 * Sets product state.
 *
 * @param newState the new state for the product
 */
public void setListingState(short newState)
{
	if (ProductStructBuilder.isValidListingState(newState))
	{
		editor.set(_listingState, newState, listingState);

        if(newState == ListingStates.INACTIVE)
        {
            updateCompositeProductListingStates(newState);
            updateDerivativeProductClassListingStates(newState);
        }
	}
	else
	{
		throw new IllegalArgumentException("Invalid listing state value = " + newState);
	}
}

private void updateCompositeProductListingStates(short newState)
{
    // MUST be done here.  Otherwise NullPointerExceptions will occur.
    ProductHomeImpl myHome = getProductHomeImpl();
    myHome.addToContainer(this);

    ProductComponent[] components = myHome.getProductComponentHome().findByComponent(this);

    if(Log.isDebugOn())
    {
        Log.debug("ProductImpl: Found " + components.length + " product components which contain product key = " +
            getProductKey());
    }

    for(int i = 0; i < components.length; i++)
    {
        Product product = components[i].getComposite();

        if(product != null)
        {
            if(Log.isDebugOn())
            {
                Log.debug("ProductImpl: Marking composite product with product key " + product.getProductKey() +
                    " inactive.");
            }

            // NOTE: Transaction should be handled outside this method
            product.setListingState(newState);
        }
    }            
}

private void updateDerivativeProductClassListingStates(short newState)
{
    // MUST be done here.  Otherwise NullPointerExceptions will occur.
    ProductHomeImpl myHome = getProductHomeImpl();
    myHome.addToContainer(this);

    ProductClass[] classes = myHome.getProductClassHome().findByUnderlying(this, true);

    if(Log.isDebugOn())
    {
        Log.debug(this, "ProductImpl: Found " + classes.length + " product classes which have underlying product key = " +
            getProductKey());
    }

    for(int i = 0; i < classes.length; i++)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "ProductImpl: Marking product class with class key " + classes[i].getClassKey() + " inactive.");
        }

        // NOTE: Transaction should be handled outside this method
        classes[i].setListingState(newState);
    }
}

private ProductHomeImpl getProductHomeImpl()
{
    if(cachedProductHome == null)
    {
        try
        {
            cachedProductHome = (ProductHomeImpl) HomeFactory.getInstance().findHome(ProductHome.HOME_NAME);
        }
        catch(Exception e)
        {
            Log.exception("ProductImpl: Unable to get product home.", e);
        }

        if(cachedProductHome == null)
        {
            Log.alarm("ProductImpl: HomeFactory returned a null product home.");
        }
    }

    return cachedProductHome;
}

/**
 * Sets maturity date of this product.
 *
 * @see Debt#setMaturityDate
 */
protected void setMaturityDate(long newDate)
{
	editor.set(_maturityDate, newDate, maturityDate);
}
/**
 * Sets OPRA month code of this product.
 *
 * @see Option#setOpraMonthCode
 */
protected void setOpraMonthCode(char newCode)
{
	editor.set(_opraMonthCode, newCode, opraMonthCode);
}
/**
 * Sets OPRA price code of this product.
 *
 * @see Option#setOpraPriceCode
 */
protected void setOpraPriceCode(char newCode)
{
	editor.set(_opraPriceCode, newCode, opraPriceCode);
}
/**
 * Sets CUSIP for securities.
 */
public void setCusip(String newCusip)
{
	editor.set(_cusip, newCusip, cusip);
}
/**
 * Sets OpenInterestUpdateTime for Product.
 */
public void setOpenInterestUpdateTime( long newOpenInterestUpdateTime )
{
	editor.set(_openInterestUpdateTime, newOpenInterestUpdateTime , openInterestUpdateTime);
}

public void setOpenInterest( int newInterest )
{
	editor.set(_openInterest, newInterest , openInterest);
}
/**
 * Sets option type code.
 *
 * @param newType new type code
 */
protected void setOptionType(OptionType newType)
{
	editor.set(_optionType, newType, optionType);
}
/**
 * Sets <code>ProductClass</code> of this product.
 *
 * @see Product#setProductClass
 */
public void setProductClass(ProductClass newClass)
{
	editor.set(_productClass, newClass, productClass);
}
/**
 * Sets product type value.
 *
 * @param newType product type value
 */
protected void setProductType(short newType)
{
	editor.set(_productType, newType, productType);
}
/**
 * Sets product sub type value.
 *
 * @param newSubType product sub type value
 */
public void setProductSubType(short newSubType)
{
	editor.set(_productSubType, newSubType, productSubType);
}

/**
 * Sets <code>ReportingClass</code> of this product.
 *
 * @see Product#setReportingClass
 */
public void setReportingClass(ReportingClass newClass)
{
	editor.set(_reportingClass, newClass, reportingClass);
}
/**
 *
 */
public void setSettlementPrice(Price newPrice)
{
	// Convert Price's to PriceSqlType's
	if (!(newPrice instanceof PriceSqlType))
	{
		newPrice = new PriceSqlType(newPrice.toStruct());
	}
	editor.set(_settlementPrice, newPrice, settlementPrice);
}
/**
 * Sets standard contract quantity of this product.
 *
 * @see Commodity#setStandardQuantity
 */
protected void setStandardQuantity(double newQuantity)
{
	editor.set(_standardQuantity, newQuantity, standardQuantity);
}
/**
 * @see Index#setSymbol
 */
protected void setSymbol(String newSymbol)
{
	editor.set(_symbol, newSymbol, symbol);
}
/**
 * Sets unit of measure for this product.
 *
 * @see Commodity#setUnitMeasure
 */
protected void setUnitMeasure(String newMeasure)
{
	editor.set(_unitMeasure, newMeasure, unitMeasure);
}
/**
 * Creates a struct containing the product keys of this product.
 *
 * @see Product#toKeysStruct
 */
public ProductKeysStruct toKeysStruct()
{
	ProductKeysStruct productKeys = new ProductKeysStruct();
	productKeys.productKey = getProductKey();
	productKeys.classKey = getProductClass().getClassKey();
	productKeys.reportingClass = getReportingClass().getClassKey();
	productKeys.productType = getProductType();
	return productKeys;
}
/**
 * Converts this product to a CORBA struct.
 *
 * @see Product#toStruct
 *
 * @return CORBA struct representing product
 */
public ProductStruct toStruct()
{
	ProductStruct productStruct = getProductEditor().toStruct();
    productStruct.productName = getProductName();
    productStruct.description = getDescription();
	return productStruct;
}
/**
 * Converts this product to a strategy struct.
 *
 * @see Product#toStruct
 *
 * @return CORBA struct representing a strategy
 */
public StrategyStruct toStrategyStruct()
{
	StrategyStruct result = getProductEditor().toStrategyStruct();
	return result;
}
/**
 * Updates this product.
 *
 * @see Product#update
 */
public void update(ProductStruct updatedProduct) throws DataValidationException
{
    setDescription(updatedProduct.description);
    getProductEditor().update(updatedProduct);
    if (!updatedProduct.productName.reportingClass.equals(""))
    {
        setNewReportingClassName(updatedProduct.productName.reportingClass);
    }    
}

/** Accesses attributes of this object for JavaGrinder. This method allows JavaGrinder
  * to get around security problems with updating an object from a generic framework.
  */
public void update(boolean get, Object[] data, Field[] fields)
{
	for (int i = 0; i < data.length; i++)
	{
		try
		{
			if (get)
				data[i] = fields[i].get(this);
			else
				fields[i].set(this, data[i]);
		}
		catch (IllegalAccessException ex)
		{
			System.out.println("Cannot set " + fields[i].getName() + " of " + this.getClass().getName() + ":" + ex);
		}
		catch (IllegalArgumentException ex)
		{
			System.out.println("Cannot set " + fields[i].getName() + " of " + this.getClass().getName() + ":" + ex);
		}
	}
}
/**
 * Updates this product.
 *
 * @see Product#updateName
 */
public void updateName(ProductNameStruct newName)
{
	getProductEditor().updateName(newName);
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
    return editor.get(_extensions, extensions);
}
public String getNewReportingClassName()
{
    return newReportingClassName;
}
public void setNewReportingClassName(String newReportingClassName)
{
    this.newReportingClassName = newReportingClassName;
}
public boolean getRestrictedIndicator()
{
    return editor.get(_restrictedIndicator, restrictedIndicator);
}
public void setRestrictedIndicator(boolean p_restrictedIndicator)
{
    editor.set(_restrictedIndicator, p_restrictedIndicator, restrictedIndicator);
}

/**
*Sets yesterday's closing price.
*
*@param Price yesterdaysClosePrice
*@return void
*/
public void setYesterdaysClosePrice(Price lastDaysClosePrice)
{
    // Convert Price's to PriceSqlType's
    if (!(lastDaysClosePrice instanceof PriceSqlType))
    {
        lastDaysClosePrice = new PriceSqlType(lastDaysClosePrice.toStruct());
    }
    editor.set(_yesterdaysClosePrice, lastDaysClosePrice, yesterdaysClosePrice);
}

/**
 * Sets yesterdays closing price suffix.
 *
 * @param String lastDaysClosePriceSuffix
 * @return void
 */
public void setYesterdaysClosePriceSuffix(String lastDaysClosePriceSuffix)
{
    editor.set(_yesterdaysClosePriceSuffix, lastDaysClosePriceSuffix, yesterdaysClosePriceSuffix);
}

/**
 * Sets todays closing price suffix.
 *
 * @param String lastDaysClosePriceSuffix
 * @return void
 */
public void setSettlementPriceSuffix(String todaysSettlementPriceSuffix)
{
    editor.set(_settlementPriceSuffix, todaysSettlementPriceSuffix, settlementPriceSuffix);
}

/**
 * Gets closing price suffix.
 *
 * @param String lastDaysClosePriceSuffix
 * @return void
 */
public String getSettlementPriceSuffix()
{
    return (String) editor.get(_settlementPriceSuffix, settlementPriceSuffix);
}
public Price getYesterdaysClosePrice() {
    Price result = (Price) editor.get(_yesterdaysClosePrice, yesterdaysClosePrice);
    // Don't return null value - change null to no price.  Prevents external
    
    if (result == null) {
        result = new NoPrice();
    }
    return result;
}
public String getYesterdaysClosePriceSuffix() {
    return (String) editor.get(_yesterdaysClosePriceSuffix, yesterdaysClosePriceSuffix);
}


}
