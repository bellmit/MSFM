package com.cboe.domain.product;

import com.cboe.infrastructureServices.persistenceService.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.product.*;
import com.cboe.interfaces.domain.Price;
import com.cboe.domain.util.*;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.idl.cmiErrorCodes.AlreadyExistCodes;
import com.cboe.idl.cmiErrorCodes.NotFoundCodes;
import com.cboe.idl.cmiOrder.PendingOrderStruct;
import com.cboe.idl.cmiProduct.*;
import com.cboe.exceptions.*;
import com.cboe.idl.product.*;
import com.cboe.idl.cmiConstants.PriceAdjustmentActions;
import com.cboe.idl.cmiConstants.PriceAdjustmentTypes;
import com.cboe.idl.cmiConstants.ProductTypes;
import com.cboe.idl.constants.PriceAdjustmentSources;
import com.cboe.idl.constants.AdjustmentOrderActions;
import com.cboe.idl.cmiUtil.OperationResultStruct;
import com.cboe.util.*;
import java.util.*;
import java.lang.reflect.*;

/**
 * A persistent implementation of <code>PriceAdjustment</code>.  JavaGrinder is
 * used for O-R mapping.
 *
 * @author John Wickberg
 */
public class PriceAdjustmentImpl extends DomainBaseImpl implements PriceAdjustment
{
	/**
	 * The product being adjusted.  Need protected access for use as query example.
	 */
	protected Product adjustedProduct;
	/**
	 * The type of price adjustment being done.
	 */
	private short adjustmentType;
    /**
     * The source of the adjustment (manual or TPF download).
     */
    private short source;
   /**
     * The orderAction of the adjustment (Normal or Cancel).
     */
    private short orderAction;
	/**
	 * New symbol for product, if it is being changed.
	 */
	private String newProductSymbol;
	/**
	 * Active indicator for this adjustment.
	 */
	private boolean active;
	/**
	 * The trading day when the adjustment will be effective.
	 */
	private long effectiveDate;
	/**
	 * The date when the adjustment will be applied.
	 */
	private long runDate;
	/**
	 * The number of new shares that will be issued for each <code>splitDenominator</code>
	 * number of existing shares.
	 */
	private short splitNumerator;
	/**
	 * The number of existing shares that will be exchanged for each <code>splitNumerator</code>
	 * number of new shares.
	 */
	private short splitDenominator;
	/**
	 * Cash dividend amount.
	 */
	private PriceSqlType cashDividend;
	/**
	 * Stock dividend amount.
	 */
	private PriceSqlType stockDividend;
	/**
	 * Lowest exercise price of options being moved to new reporting class.
	 */
	private PriceSqlType lowRange;
	/**
	 * Highest exercise price of options being moved to new reporting class.
	 */
	private PriceSqlType highRange;
	/**
	 * Collection of reporting classes affected by this adjustment.
	 */
	private Vector adjustedClasses;
	/**
	 * Cached reference to product home.
	 */
	private static ProductHome productHome;
	/**
	 * Cached reference to product class home.
	 */
	private static ProductClassHome productClassHome;
	/**
	 * Cached reference to price adjustment home.
	 */
	private static PriceAdjustmentHome priceAdjustmentHome;
    /**
     * Constant used to check whether or not stock percentage dividend requires Futures adjustment
     */
    public static Price STOCK_DIVIDEND_CUTOFF = PriceFactory.create(5.0);
	/*
	 * Fields and descriptor for JavaGrinder.
	 */
	private static Field _active;
	private static Field _adjustedClasses;
	private static Field _highRange;
	private static Field _lowRange;
	private static Field _stockDividend;
	private static Field _cashDividend;
	private static Field _splitDenominator;
	private static Field _splitNumerator;
	private static Field _runDate;
	private static Field _effectiveDate;
	private static Field _newProductSymbol;
	private static Field _adjustmentType;
    private static Field _source;
    private static Field _orderAction;
	private static Field _adjustedProduct;
	private static Vector classDescriptor;
	/**
	* This static block will be regenerated if persistence is regenerated.
	*/
	static { /*NAME:fieldDefinition:*/
		try{
			_active = PriceAdjustmentImpl.class.getDeclaredField("active");
            _active.setAccessible(true);
			_adjustedProduct = PriceAdjustmentImpl.class.getDeclaredField("adjustedProduct");
            _adjustedProduct.setAccessible(true);
			_adjustmentType = PriceAdjustmentImpl.class.getDeclaredField("adjustmentType");
            _adjustmentType.setAccessible(true);
			_source = PriceAdjustmentImpl.class.getDeclaredField("source");
            _source.setAccessible(true);
			_orderAction = PriceAdjustmentImpl.class.getDeclaredField("orderAction");
            _orderAction.setAccessible(true);
			_newProductSymbol = PriceAdjustmentImpl.class.getDeclaredField("newProductSymbol");
            _newProductSymbol.setAccessible(true);
			_effectiveDate = PriceAdjustmentImpl.class.getDeclaredField("effectiveDate");
            _effectiveDate.setAccessible(true);
			_runDate = PriceAdjustmentImpl.class.getDeclaredField("runDate");
            _runDate.setAccessible(true);
			_splitNumerator = PriceAdjustmentImpl.class.getDeclaredField("splitNumerator");
            _splitNumerator.setAccessible(true);
			_splitDenominator = PriceAdjustmentImpl.class.getDeclaredField("splitDenominator");
            _splitDenominator.setAccessible(true);
			_cashDividend = PriceAdjustmentImpl.class.getDeclaredField("cashDividend");
            _cashDividend.setAccessible(true);
			_stockDividend = PriceAdjustmentImpl.class.getDeclaredField("stockDividend");
            _stockDividend.setAccessible(true);
			_lowRange = PriceAdjustmentImpl.class.getDeclaredField("lowRange");
            _lowRange.setAccessible(true);
			_highRange = PriceAdjustmentImpl.class.getDeclaredField("highRange");
            _highRange.setAccessible(true);
			_adjustedClasses = PriceAdjustmentImpl.class.getDeclaredField("adjustedClasses");
            _adjustedClasses.setAccessible(true);

		}
		catch (NoSuchFieldException ex) { System.out.println(ex); }
	}

/**
 * PriceAdjustmentImpl constructor comment.
 */
public PriceAdjustmentImpl() {
	super();
    setUsing32bitId(true);
}

/**
* Adds adjusted class to price adjustment.
*/
private void addAdjustedClass(PriceAdjustmentClassStruct newClassAdjustment) throws DataValidationException
{
    ReportingClassAdjustmentImpl adjustedClass = new ReportingClassAdjustmentImpl();
    // need to make the broker of the new class adjustment the same as this adjustment's broker
    adjustedClass.setBrokerName(getBrokerName());
    adjustedClass.create(this, newClassAdjustment);
    getAdjustedClassCollection().addElement(adjustedClass);
}

/**
 * Adds adjusted class to price adjustment and captures exception in to result struct.
 * On Exception, need to set the object as transient , so it doesn't get added to the database
 * as a Zombie. This needs to be done, when we need to conitune processing next set of objects.
*/
private boolean addAdjustedClass(PriceAdjustmentClassStruct newClassAdjustment, PriceAdjustmentReportingClassResultStruct reportingClassAdjustmentResult)
{
    ReportingClassAdjustmentImpl adjustedClass = new ReportingClassAdjustmentImpl();
    // need to make the broker of the new class adjustment the same as this adjustment's broker
    adjustedClass.setBrokerName(getBrokerName());
    try {
        adjustedClass.create(this, newClassAdjustment, reportingClassAdjustmentResult);
        setAdjustmentResult(null, reportingClassAdjustmentResult, newClassAdjustment.classKey);
    }
    catch (DataValidationException dve){
        adjustedClass.setAsTransient(true);
        Log.alarm("PriceAdjustmentError: " + dve.details.message);
        setAdjustmentResult(dve.details, reportingClassAdjustmentResult, newClassAdjustment.classKey);
        return false; // UD 06/06/05, capture the exception and return, don't add to collection of adjusted classes
    }
    getAdjustedClassCollection().addElement(adjustedClass);
    return true;
}

/**
 * Applies adjustment.
 *
 * @see PriceAdjustment#apply
 *
 */
public void apply() throws TransactionFailedException
{
	Product tempProduct = getAdjustedProduct();
	// Change symbol of product, assume that it is not an option or future.
	ProductNameStruct newName = ProductStructBuilder.buildProductNameStruct();
	newName.productSymbol = getNewProductSymbol();
	tempProduct.updateName(newName);
	// Do first pass for classes being created.
	Enumeration adjustmentsEnum = getAdjustedClassCollection().elements();
	ReportingClassAdjustment reportingClass;
	while (adjustmentsEnum.hasMoreElements())
	{
		reportingClass = (ReportingClassAdjustment) adjustmentsEnum.nextElement();
		if (reportingClass.isCreateAdjustment())
		{
			reportingClass.apply();
		}
	}
	// Do a second pass for all other class actions
	adjustmentsEnum = getAdjustedClassCollection().elements();
	while (adjustmentsEnum.hasMoreElements())
	{
		reportingClass = (ReportingClassAdjustment) adjustmentsEnum.nextElement();
		if (!reportingClass.isCreateAdjustment())
		{
			reportingClass.apply();
		}
	}
    // UD, 01/10/05, inactivate the reporting classes
    // UD, 06/14/05, don't inactivate reporting classes, during adjustments we might get products for the inactivated RC.
    // Inactivation now done as EOD anyways...
    // inactivateReportingClasses(); /* commented 06/14/05 */
    // Mark adjustment inactive so it isn't applied twice
    setActive(false);
}
   
/*
*   UD 01/10/05, for range rollover need to inactivate a reporting class 
*   if all the series under the reporting class have changed...
*/
private void inactivateReportingClasses()
{
    Enumeration adjustmentsEnum = getAdjustedClassCollection().elements();
    while (adjustmentsEnum.hasMoreElements())
    {
        ReportingClassAdjustment reportingClass = (ReportingClassAdjustment) adjustmentsEnum.nextElement();
        reportingClass.inactivateIfEmpty();          
    }             
}

     
/**
 * Creates a reporting class adjustment entry.
 *
 * @param adjustedClass reportingClass being adjusted
 */
private void createClassAdjustment(ReportingClass adjustedClass) throws DataValidationException
{
    PriceAdjustmentClassStruct newClassAdjustment = new PriceAdjustmentClassStruct();
    newClassAdjustment.action = PriceAdjustmentActions.PRICE_ADJUSTMENT_UPDATE;
    newClassAdjustment.classKey = adjustedClass.getClassKey();
    newClassAdjustment.productType = adjustedClass.getProductType();
    newClassAdjustment.currentClassSymbol = adjustedClass.getSymbol();
    // user will have to manually update symbol.
    newClassAdjustment.newClassSymbol = adjustedClass.getSymbol();
    newClassAdjustment.beforeContractSize = adjustedClass.getContractSize();
    newClassAdjustment.afterContractSize = getNewSharesPerContract(adjustedClass.getContractSize());
    newClassAdjustment.items = new PriceAdjustmentItemStruct[0];
    addAdjustedClass(newClassAdjustment);
}

/**
 * Creates reporting class adjustment entries for futures.
 */
private void createClassAdjustmentsForFutures() throws DataValidationException
{
    if (isClassAdjustmentRequired())
    {
        // get all active product classes derived from adjusted product
        ProductClass[] derivedClasses = getProductClassHome().findByUnderlying(getAdjustedProduct(), true);
        for (int i = 0; i < derivedClasses.length; i++)
        {
            // if class is a future, need to create entries for reporting classes
            if (derivedClasses[i].getProductType() == ProductTypes.FUTURE)
            {
                ReportingClass[] reportingClasses = derivedClasses[i].getReportingClasses(true);
                for (int j = 0; j < reportingClasses.length; j++)
                {
                    createClassAdjustment(reportingClasses[j]);
                }
            }
        }
    }
}
/**
 * Creates instance from a CORBA struct.
 *
 * @see PriceAdjustment#create
 */
public void create(PriceAdjustmentStruct newAdjustment) throws AlreadyExistsException, DataValidationException
{
    Product tempProduct;
    try
    {
        tempProduct = findProduct(newAdjustment);
    }
    catch (NotFoundException e)
    {
        throw ExceptionBuilder.dataValidationException("Unable to find product being adjusted", DataValidationCodes.INVALID_PRODUCT);
    }
    if (!hasPriceAdjustment(tempProduct))
    {
        setAdjustedProduct(tempProduct);
        if (newAdjustment.newProductSymbol.length() == 0)
        {
            // set new symbol to current symbol if it isn't being changed.
            setNewProductSymbol(tempProduct.getProductName().productSymbol);
        }
        else
        {
            setNewProductSymbol(newAdjustment.newProductSymbol);
        }
        // UD 06/06/05. moved setting of adjustment values to a private method.
        setAdjustmentValues(newAdjustment);
        Vector tempClasses = new Vector(newAdjustment.adjustedClasses.length);
        setAdjustedClassCollection(tempClasses);
        for (int i = 0; i < newAdjustment.adjustedClasses.length; i++)
        {
            addAdjustedClass(newAdjustment.adjustedClasses[i]);
        }
        createClassAdjustmentsForFutures();
    }
    else
    {
        throw ExceptionBuilder.alreadyExistsException("Adjustment already exists for product", AlreadyExistCodes.ADJUSTMENT_ALREADY_EXISTS);
    }
}

/*
*   UD 06/06/05
*   set the failed product error details.
*/
public void create(PriceAdjustmentStruct newAdjustment, PriceAdjustmentReportingClassResultStruct[] reportingClassAdjustmentsResults) throws AlreadyExistsException, DataValidationException
{
	Product tempProduct;
 	try
  	{
		tempProduct = findProduct(newAdjustment);
  	}
   	catch (NotFoundException e)
    {
        String msg = "PriceAdjustmentError: Unable to find product being adjusted" + newAdjustment.productKey;
        Log.information(msg);
    	throw ExceptionBuilder.dataValidationException(msg, DataValidationCodes.INVALID_PRODUCT);
    }
	if (!hasPriceAdjustment(tempProduct))
	{
		setAdjustedProduct(tempProduct);
		if (newAdjustment.newProductSymbol.length() == 0) {
			// set new symbol to current symbol if it isn't being changed.
			setNewProductSymbol(tempProduct.getProductName().productSymbol);
		}
		else {
			setNewProductSymbol(newAdjustment.newProductSymbol);
		}
        // UD 06/06/05... changes for IPD.
		setAdjustmentValues(newAdjustment);
		Vector tempClasses = new Vector(newAdjustment.adjustedClasses.length);
		setAdjustedClassCollection(tempClasses);
        int failedReportingClassAdjustments = 0;
        for (int i = 0; i < newAdjustment.adjustedClasses.length; i++)
        {
            ExceptionDetails details = new ExceptionDetails();
            reportingClassAdjustmentsResults[i] = initializeReportingClassResultStruct(newAdjustment.adjustedClasses[i].items.length, newAdjustment.productKey);
            if(!validateReportingClassAdjustment(newAdjustment.adjustedClasses[i], details)) {
                setAdjustmentResult(details, reportingClassAdjustmentsResults[i], newAdjustment.adjustedClasses[i].classKey);
                failedReportingClassAdjustments++;
                Log.alarm(reportingClassAdjustmentsResults[i].reportingClassErrorResult.errorMessage);
                continue;
            }
            if(!addAdjustedClass(newAdjustment.adjustedClasses[i], reportingClassAdjustmentsResults[i])) {
                // increment failed reporting classes count
                failedReportingClassAdjustments++;
            }
        }
        if(failedReportingClassAdjustments == newAdjustment.adjustedClasses.length) {
            // If there are errors on all reporting class for this underlying, throw exception.
            throw ExceptionBuilder.dataValidationException("All reporting class adjustments have exceptions for[" +
                + newAdjustment.productKey + "], product adjustment not created", DataValidationCodes.INVALID_PRODUCT);
        }
	}
	else
	{
        String msg = "PriceAdjustmentError: Adjustment already exists for product" + newAdjustment.productKey;
        Log.information(msg);
		throw ExceptionBuilder.alreadyExistsException(msg, AlreadyExistCodes.ADJUSTMENT_ALREADY_EXISTS);
	}
}
/**
 * Searches for class adjustment.
 *
 * @see PriceAdjustment#findClassAdjustment
 */
public ReportingClassAdjustment findClassAdjustment(String classSymbol, short productType) throws NotFoundException
{
	Enumeration adjustmentsEnum = getAdjustedClassCollection().elements();
	ReportingClassAdjustmentImpl adjustedClass;
	while (adjustmentsEnum.hasMoreElements())
	{
		adjustedClass = (ReportingClassAdjustmentImpl) adjustmentsEnum.nextElement();
		if (adjustedClass.getClassSymbol().equals(classSymbol) && adjustedClass.getProductType() == productType)
		{
			return adjustedClass;
		}
	}
	throw ExceptionBuilder.notFoundException("No adjustment found for class symbol = " + classSymbol, NotFoundCodes.RESOURCE_DOESNT_EXIST);
}
/**
 * Finds product being adjusted.
 *
 * @return found product
 * @exception NotFoundException if product is not found
 */
private Product findProduct(PriceAdjustmentStruct adjustment) throws NotFoundException
{
	Product result;
	if (adjustment.productKey > 0)
	{
		result = getProductHome().findByKey(adjustment.productKey);
	}
	else
	{
		ProductNameStruct nameStruct = ProductStructBuilder.buildProductNameStruct();
		nameStruct.productSymbol = adjustment.productSymbol;
		result = getProductHome().findByName(nameStruct);
	}
	return result;
}
/**
 * Gets collection of adjusted classes.
 */
private Vector getAdjustedClassCollection()
{
	return (Vector) editor.get(_adjustedClasses, adjustedClasses);
}
/**
 * Gets classes being changed by this adjustment.
 *
 * @see PriceAdjustment#getAdjustedClasses
 */
public ReportingClassAdjustment[] getAdjustedClasses()
{
	Vector temp = getAdjustedClassCollection();
	ReportingClassAdjustmentImpl[] result;
	if (temp != null)
	{
		result = new ReportingClassAdjustmentImpl[temp.size()];
		temp.copyInto(result);
	}
	else
	{
		result = new ReportingClassAdjustmentImpl[0];
	}
	return result;
}
/**
 * Gets product being adjusted.
 */
public Product getAdjustedProduct()
{
	return (Product) editor.get(_adjustedProduct, adjustedProduct);
}
/**
 * Gets adjustment type.
 */
private short getAdjustmentType()
{
	return (short) editor.get(_adjustmentType, adjustmentType);
}
/**
 * Gets cash dividend amount.
 */
private PriceSqlType getCashDividend()
{
	return (PriceSqlType) editor.get(_cashDividend, cashDividend);
}
/**
 * Gets effective date of adjustment.
 */
private long getEffectiveDate()
{
	return (long) editor.get(_effectiveDate, effectiveDate);
}
/**
 * Gets high end of exercise price range.
 */
private PriceSqlType getHighRange()
{
	return (PriceSqlType) editor.get(_highRange, highRange);
}
/**
 * Gets low end of exercise price range.
 */
private PriceSqlType getLowRange()
{
	return (PriceSqlType) editor.get(_lowRange, lowRange);
}
/**
 * Gets new symbol assigned to product.
 */
private String getNewProductSymbol()
{
	return (String) editor.get(_newProductSymbol, newProductSymbol);
}
/**
 * Gets the pending price adjustment information for a product class.
 *
 * @param adjustedClass product class being adjusted
 * @return struct containing price adjustment info
 */
private PendingAdjustmentStruct getPendingInfoForClass(ProductClass adjustedClass, boolean includeProducts)
{
	PendingAdjustmentStruct result = new PendingAdjustmentStruct();
	result.classKey = adjustedClass.getClassKey();
	result.type = getAdjustmentType();
	result.active = isActive();
	result.effectiveDate = DateWrapper.convertToDate(getEffectiveDate());
	result.submittedDate = DateWrapper.convertToDate(getCreatedTime());
	if (includeProducts)
	{
		result.productsPending = getPendingNamesForClass(adjustedClass);
	}
	else
	{
		result.productsPending = new PendingNameStruct[0];
	}
	return result;
}
/**
 * Gets pending names for all adjusted products of the adjusted product class.
 *
 * @param adjustedClass class being adjusted
 * @return pending name structs for adjusted products of class
 */
private PendingNameStruct[] getPendingNamesForClass(ProductClass adjustedClass)
{
	ReportingClass[] reportingClasses = adjustedClass.getReportingClasses(true);
	Vector tempResult = new Vector(reportingClasses.length * 200);
	ReportingClassAdjustment[] allClassAdjustments;
	ProductAdjustment[] adjustedProducts;
	try
	{
		allClassAdjustments = findClassAdjustments(adjustedClass.getSymbol(), adjustedClass.getProductType());
		for(int k = 0; k < allClassAdjustments.length ; k++) 
		{
			adjustedProducts = allClassAdjustments[k].getAdjustedProducts();
			for (int j = 0; j < adjustedProducts.length; j++)
			{
				tempResult.addElement(adjustedProducts[j].toPendingName());
			}
		}
	}
	catch (NotFoundException e)
	{
		// class may not be changed by adjustment, assume all is OK
	}
	PendingNameStruct[] result = new PendingNameStruct[tempResult.size()];
	tempResult.copyInto(result);
	return result;
}
/**
 * Gets home for price adjustments.
 *
 * @return product home
 */
private PriceAdjustmentHome getPriceAdjustmentHome()
{
	if (priceAdjustmentHome == null)
	{
		try
		{
			priceAdjustmentHome = (PriceAdjustmentHome) HomeFactory.getInstance().findHome(PriceAdjustmentHome.HOME_NAME);
		}
		catch (Exception e)
		{
			Log.exception("Unable to find price adjustment home", e);
			throw new NullPointerException("Unable to find price adjustment home");
		}
	}
	return priceAdjustmentHome;
}
/**
 * Gets home for product classes.
 *
 * @return product class home
 */
private ProductClassHome getProductClassHome()
{
	if (productClassHome == null)
	{
		try
		{
			productClassHome = (ProductClassHome) HomeFactory.getInstance().findHome(ProductClassHome.HOME_NAME);
		}
		catch (Exception e)
		{
			Log.exception("Unable to find product class home", e);
			throw new NullPointerException("Unable to find product class home");
		}
	}
	return productClassHome;
}
/**
 * Gets home for products.
 *
 * @return product home
 */
private ProductHome getProductHome()
{
	if (productHome == null)
	{
		try
		{
			productHome = (ProductHome) HomeFactory.getInstance().findHome(ProductHome.HOME_NAME);
		}
		catch (Exception e)
		{
			Log.exception("Unable to find product home", e);
			throw new NullPointerException("Unable to find product home");
		}
	}
	return productHome;
}
/**
 * Gets run date of adjustment.
 */
private long getRunDate()
{
	return (long) editor.get(_runDate, runDate);
}
/**
 * Gets the contract multiplier for reporting classes.
 */
private int getNewSharesPerContract(int oldShares)
{
    int newShares = oldShares;
    switch (getAdjustmentType())
    {
    case PriceAdjustmentTypes.SPLIT:
        if (getSplitDenominator() != 1)
        {
            newShares = (oldShares * getSplitNumerator()) / getSplitDenominator();
        }
        break;
    case PriceAdjustmentTypes.DIVIDEND_CASH:
        // no affect on shares per contract
        break;
    case PriceAdjustmentTypes.DIVIDEND_PERCENT:
        // convert percentage to decimal
        newShares = (int) (1.00 + getStockDividend().toDouble() / 100.0) * oldShares;
        break;
    case PriceAdjustmentTypes.DIVIDEND_STOCK:
        // add in new shares
        newShares = oldShares + (int) getStockDividend().toDouble();
        break;
    case PriceAdjustmentTypes.LEAP_ROLLOVER:
        // no affect on shares per contract
        break;
    case PriceAdjustmentTypes.MERGER:
        // no affect on shares per contract
        break;
    case PriceAdjustmentTypes.SYMBOL_CHANGE:
        // no affect on shares per contract
        break;
    case PriceAdjustmentTypes.COMMON_DISTRIBUTION:
        // no affect on shares per contract
        break;
    default:
        Log.alarm(this, "Unknown adjustment type (" + getAdjustmentType() + ") assuming doesn't affect shares per contract");
    }
    return newShares;
}
/**
 * Gets denominator of stock split.
 */
private short getSplitDenominator()
{
	return (short) editor.get(_splitDenominator, splitDenominator);
}
/**
 * Gets numerator of stock split.
 */
private short getSplitNumerator()
{
	return (short) editor.get(_splitNumerator, splitNumerator);
}
/**
 * Gets stock dividend amount.
 */
private PriceSqlType getStockDividend()
{
	return (PriceSqlType) editor.get(_stockDividend, stockDividend);
}
/**
 * Gets adjustment source.
 */
public short getSource()
{
	return (short) editor.get(_source, source);
}

/**
 * Gets adjustment orderAction.
 */
public short getOrderAction()
{
	return (short) editor.get(_orderAction, orderAction);
}

/**
 * Tests if product has existing price adjustment.
 *
 * @param product product being checked
 * @return true if product has an existing adjustment
 */
private boolean hasPriceAdjustment(Product product)
{
	try
	{
		getPriceAdjustmentHome().findByProduct(product);
		return true;
	}
	catch (NotFoundException e)
	{
		return false;
	}
}
/**
 * Creates JavaGrinder descriptor for the database record of this object.
 */
private void initDescriptor()
{
	synchronized (PriceAdjustmentImpl.class)
	{
		if (classDescriptor != null)
			return;
		Vector tempDescriptor = super.getDescriptor();
		tempDescriptor.addElement(AttributeDefinition.getForeignRelation(ProductImpl.class, "adj_prod_key", _adjustedProduct));
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("adj_type_code", _adjustmentType));
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("source", _source));
        tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("orderAction", _orderAction));
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("new_prod_sym", _newProductSymbol));
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("act_ind", _active));
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("eff_date", _effectiveDate));
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("run_date", _runDate));
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("split_num", _splitNumerator));
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("split_denom", _splitDenominator));
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("cash_div", _cashDividend));
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("stock_div", _stockDividend));
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("low_range", _lowRange));
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("high_range", _highRange));
		tempDescriptor.addElement(AttributeDefinition.getCollectionRelation(ReportingClassAdjustmentImpl.class, _adjustedClasses));
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
	result.setTableName("PRICE_ADJ");
	result.setClassDescription(classDescriptor);
	return result;
}
/**
 * Gets active indicator of this adjustment.
 *
 * @return active status indicator
 */
private boolean isActive()
{
	return editor.get(_active, active);
}
/**
 * Determines if a class adjsutment is required for future reporting classes.
 */
private boolean isClassAdjustmentRequired()
{
    boolean adjustmentRequired = false;
    switch (getAdjustmentType())
    {
    case PriceAdjustmentTypes.SPLIT:
        adjustmentRequired = (getSplitDenominator() != 1);
        break;
    case PriceAdjustmentTypes.DIVIDEND_CASH:
        adjustmentRequired = true;
        break;
    case PriceAdjustmentTypes.DIVIDEND_PERCENT:
        adjustmentRequired = true;
        break;
    case PriceAdjustmentTypes.DIVIDEND_STOCK:
        adjustmentRequired = true;
        break;
    case PriceAdjustmentTypes.LEAP_ROLLOVER:
        break;
    case PriceAdjustmentTypes.MERGER:
        adjustmentRequired = true;
        break;
    case PriceAdjustmentTypes.SYMBOL_CHANGE:
        adjustmentRequired = true;
        break;
    case PriceAdjustmentTypes.COMMON_DISTRIBUTION:
        adjustmentRequired = true;
        break;
    default:
        Log.alarm(this, "Unknown adjustment type (" + getAdjustmentType() + ") assuming class adjustment not required");
    }
    return adjustmentRequired;
}
/**
 * Checks to see if adjustment should be applied at this time.
 *
 * @see PriceAdjustment#isTimeToApply
 */
public boolean isTimeToApply()
{
	return isActive() && getRunDate() < System.currentTimeMillis();
}
/**
 * Cascades the delete to the reporting class entries for this adjustment.
 *
 */
public void markForDelete() throws PersistenceException
{
	super.markForDelete();
	setActive(false);
	ReportingClassAdjustmentImpl classAdjustment;
	Enumeration adjustmentsEnum = getAdjustedClassCollection().elements();
	while (adjustmentsEnum.hasMoreElements())
	{
		classAdjustment = (ReportingClassAdjustmentImpl) adjustmentsEnum.nextElement();
		classAdjustment.markForDelete();
	}
}
/**
 * Sets active indicator for this adjustment.
 *
 * @param newValue new active indicator
 */
private void setActive(boolean newValue)
{
	editor.set(_active, newValue, active);
}
/**
 * Sets collection of adjusted classes.
 */
private void setAdjustedClassCollection(Vector aValue)
{
	editor.set(_adjustedClasses, aValue, adjustedClasses);
}
/**
 * Sets product being adjusted.
 */
private void setAdjustedProduct(Product aValue)
{
	editor.set(_adjustedProduct, aValue, adjustedProduct);
}
/**
 * Sets adjustment type.
 */
private void setAdjustmentType(short aValue)
{
	editor.set(_adjustmentType, aValue, adjustmentType);
}
/**
 * Sets home for this adjustment and adds all related reporting class adjustments
 * to the container.
 *
 * @param newHome home for this adjustment
 */
public void setBOHome(BOHome newHome)
{
	super.setBOHome(newHome);
	ReportingClassAdjustmentImpl[] reportingClasses = (ReportingClassAdjustmentImpl[]) getAdjustedClasses();
	for (int i = 0; i < reportingClasses.length; i++)
	{
		newHome.addToContainer(reportingClasses[i]);
	}
}
/**
 * Sets cash dividend amount.
 */
private void setCashDividend(PriceSqlType aValue)
{
	editor.set(_cashDividend, aValue, cashDividend);
}
/**
 * Sets effective date of adjustment.
 */
private void setEffectiveDate(long aValue)
{
	editor.set(_effectiveDate, aValue, effectiveDate);
}
/**
 * Sets high end of exercise price range.
 */
private void setHighRange(PriceSqlType aValue)
{
	editor.set(_highRange, aValue, highRange);
}
/**
 * Sets low end of exercise price range.
 */
private void setLowRange(PriceSqlType aValue)
{
	editor.set(_lowRange, aValue, lowRange);
}
/**
 * Sets new symbol for product.
 */
private void setNewProductSymbol(String aValue)
{
	editor.set(_newProductSymbol, aValue, newProductSymbol);
}
/**
 * Sets the run date of this adjustment.
 */
private void setRunDate(long aValue)
{
	editor.set(_runDate, aValue, runDate);
}
/**
 * Sets adjustment source.
 */
private void setSource(short aValue)
{
    // Manual update cannot change status of downloaded adjustment - ignore change in this case
    // UD 06/06/05, IPD update cannot change status of download adjustment[TPF] - ignore change in this case as well.
    if (!( (aValue == PriceAdjustmentSources.MANUAL || aValue == PriceAdjustmentSources.IPD )
            && getSource() == PriceAdjustmentSources.TPF_DOWNLOAD))
    {
	    editor.set(_source, aValue, source);
    }
}

protected void validateOrderAction(PriceAdjustmentStruct priceAdjustment) throws DataValidationException
{

    // validate the value is a right order action
	    if (priceAdjustment.orderAction == AdjustmentOrderActions.NORMAL_ADJUSTMENT)
	    {
		    return;
	    }
	    else if (priceAdjustment.orderAction == AdjustmentOrderActions.CANCEL_ALL_ORDERS)
	    {
	        if (priceAdjustment.type == PriceAdjustmentTypes.DIVIDEND_CASH)
	        {
	            return;
	        }
	        else
	        {
	             Log.alarm("the input OrderAction(" + priceAdjustment.orderAction + ") is NOT valid for Type (" + priceAdjustment.type + ")");
	             throw ExceptionBuilder.dataValidationException(" The specified OrderAction is NOT valid for the specified Adjustment Type ", DataValidationCodes.INVALID_PRICE_ADJUSTMENT);
	        }
	    }
	    else
	    {
	        Log.alarm("the OrderAction(" + priceAdjustment.orderAction + ") is NOT valid");
	        throw ExceptionBuilder.dataValidationException("The specified OrderAction is NOT valid", DataValidationCodes.INVALID_PRICE_ADJUSTMENT);
        }

}

/**
 *  sets adjustment orderAction.
 */
private void setOrderAction(short aValue)
{
   editor.set(_orderAction, aValue, orderAction);
}

/**
 * Set the denominator of a stock split.
 */
private void setSplitDenominator(short aValue)
{
	editor.set(_splitDenominator, aValue, splitDenominator);
}
/**
 * Sets the numerator of a stock split.
 */
private void setSplitNumerator(short aValue)
{
	editor.set(_splitNumerator, aValue, splitNumerator);
}
/**
 * Sets the stock dividend amount.
 */
private void setStockDividend(PriceSqlType aValue)
{
	editor.set(_stockDividend, aValue, stockDividend);
}
/**
 * Converts adjustment to pending adjustment information struct used for display.
 *
 * @param includeProducts if set, result will include pending name info for the
 *                        products of each product class
 * @return a pending info struct for each product class affected by this adjustment
 */
public PendingAdjustmentStruct[] toPendingInfo(boolean includeProducts)
{
	ProductClass[] derivativeClasses;
	derivativeClasses = getProductClassHome().findByUnderlying(getAdjustedProduct(), true);
	PendingAdjustmentStruct[] result = new PendingAdjustmentStruct[derivativeClasses.length + 1];
	result[0] = getPendingInfoForClass(getAdjustedProduct().getProductClass(), includeProducts);
	for (int i = 0; i < derivativeClasses.length; i++)
	{
		result[i + 1] = getPendingInfoForClass(derivativeClasses[i], includeProducts);
	}
	return result;
}
/**
 * Creates pending name for product directly affected by this adjustment.
 *
 * @return pending name values
 */
public PendingNameStruct toPendingName()
{
	PendingNameStruct result = new PendingNameStruct();
	result.action = PriceAdjustmentActions.PRICE_ADJUSTMENT_UPDATE;
	result.productStruct = getAdjustedProduct().toStruct();
	result.pendingProductName = ProductStructBuilder.buildProductNameStruct();
	result.pendingProductName.productSymbol = getNewProductSymbol();
	return result;
}
/**
 * Converts this adjustment to a CORBA struct.
 *
 * @see PriceAdjustment#toStruct
 */
public PriceAdjustmentStruct toStruct(boolean includeDetail)
{
	PriceAdjustmentStruct result = ProductStructBuilder.buildPriceAdjustmentStruct();
	result.adjustmentNumber = getObjectIdentifierAsInt();
	result.type = getAdjustmentType();
    result.source = getSource();
    result.orderAction = getOrderAction();
	result.cashDividend = getCashDividend().toStruct();
	result.createdTime = DateWrapper.convertToDateTime(getCreatedTime());
	result.effectiveDate = DateWrapper.convertToDate(getEffectiveDate());
	result.highRange = getHighRange().toStruct();
	result.lastModifiedTime = DateWrapper.convertToDateTime(getLastModifiedTime());
	result.lowRange = getLowRange().toStruct();
	result.newProductSymbol = getNewProductSymbol();
	result.productKey = getAdjustedProduct().getProductKey();
	result.productSymbol = getAdjustedProduct().getProductName().productSymbol;
	result.runDate = DateWrapper.convertToDate(getRunDate());
	result.splitDenominator = getSplitDenominator();
	result.splitNumerator = getSplitNumerator();
	result.stockDividend = getStockDividend().toStruct();
	if (includeDetail)
	{
		ReportingClassAdjustment[] classes = getAdjustedClasses();
		result.adjustedClasses = new PriceAdjustmentClassStruct[classes.length];
		for (int i = 0; i < classes.length; i++)
		{
			result.adjustedClasses[i] = classes[i].toStruct();
		}
	}
	return result;
}
/**
 * Updates this adjustment.
 *
 * @see PriceAdjustment#update
 */
public void update(PriceAdjustmentStruct updatedAdjustment) throws DataValidationException
{
    // UD 06/06/05. moved setting of adjustment values to a private method.
    setAdjustmentValues(updatedAdjustment);
    ReportingClassAdjustmentImpl adjustedClass = null;
    boolean found;
    for (int i = 0; i < updatedAdjustment.adjustedClasses.length; i++)
    {
        try
        {
            adjustedClass = (ReportingClassAdjustmentImpl) findClassAdjustment(updatedAdjustment.adjustedClasses[i].currentClassSymbol, updatedAdjustment.adjustedClasses[i].productType);
            // need to do update outside of try, it may throw a NotFoundException
            found = true;
        }
        catch (NotFoundException e)
        {
            found = false;
            addAdjustedClass(updatedAdjustment.adjustedClasses[i]);
        }
        if (found)
        {
            adjustedClass.update(updatedAdjustment.adjustedClasses[i]);
        }
    }
}

/*
*   UD 06/06/05
*   Update this adjustment...
*   Capture the result in the ResultStruct [failure or sucessful]
*/
public void update(PriceAdjustmentStruct updatedAdjustment, PriceAdjustmentReportingClassResultStruct[] reportingClassAdjustmentsResults) throws DataValidationException
{
    setAdjustmentValues(updatedAdjustment);
    ReportingClassAdjustmentImpl adjustedClass = null;
	boolean found;
    int failedReportingClassAdjustments = 0;
	for (int i = 0; i < updatedAdjustment.adjustedClasses.length; i++)
	{
        ExceptionDetails details = new ExceptionDetails();
        reportingClassAdjustmentsResults[i] = initializeReportingClassResultStruct(updatedAdjustment.adjustedClasses[i].items.length, updatedAdjustment.productKey);
        if(!validateReportingClassAdjustment(updatedAdjustment.adjustedClasses[i], details)) {
            setAdjustmentResult(details, reportingClassAdjustmentsResults[i], updatedAdjustment.adjustedClasses[i].classKey);
            failedReportingClassAdjustments++;
            Log.alarm(reportingClassAdjustmentsResults[i].reportingClassErrorResult.errorMessage);
            continue;
        }
		try
		{
			adjustedClass = (ReportingClassAdjustmentImpl) findClassAdjustment(updatedAdjustment.adjustedClasses[i].currentClassSymbol, updatedAdjustment.adjustedClasses[i].productType);
			// need to do update outside of try, it may throw a NotFoundException
			found = true;
		}
		catch (NotFoundException e)
		{
			found = false;
            // no try-catch as the exception is captured to result, in call to addAdjustClass
            if(!addAdjustedClass(updatedAdjustment.adjustedClasses[i], reportingClassAdjustmentsResults[i])) {
                failedReportingClassAdjustments++;
            }
		}
		if (found)
		{
            try {
                adjustedClass.update(updatedAdjustment.adjustedClasses[i], reportingClassAdjustmentsResults[i]);
                setAdjustmentResult(null, reportingClassAdjustmentsResults[i], updatedAdjustment.adjustedClasses[i].classKey);
            }
            catch (DataValidationException dve) {
                failedReportingClassAdjustments++;
                Log.alarm("PriceAdjustmentError: " + dve.details.message);
                setAdjustmentResult(dve.details, reportingClassAdjustmentsResults[i], updatedAdjustment.adjustedClasses[i].classKey);
            }
		}
	}
    // If there are exception on all reporting class for this underlying, throw exception.
    if(failedReportingClassAdjustments == updatedAdjustment.adjustedClasses.length) {
        throw ExceptionBuilder.dataValidationException("All reporting classes adjustments have errors for["
            + updatedAdjustment.productKey + "] product adjustment not updated", DataValidationCodes.INVALID_PRODUCT);
    }
}

    /*
    *   UD 06/06/05
    *   values set on the price adjusment
    */
    private void setAdjustmentValues(PriceAdjustmentStruct priceAdjustment) throws DataValidationException
    {
        validateOrderAction(priceAdjustment);
        //Added to have Db ID same as IPD to allow the removes.
        if(priceAdjustment.adjustmentNumber>0)
            setObjectIdentifier(new Integer (priceAdjustment.adjustmentNumber));
        setAdjustmentType(priceAdjustment.type);
        setSource(priceAdjustment.source);
        setOrderAction(priceAdjustment.orderAction);
        setActive(true);
        setEffectiveDate(DateWrapper.convertToMillis(priceAdjustment.effectiveDate));
        setRunDate(DateWrapper.convertToMillis(priceAdjustment.runDate));
        setSplitDenominator(priceAdjustment.splitDenominator);
        setSplitNumerator(priceAdjustment.splitNumerator);
        setCashDividend(new PriceSqlType(priceAdjustment.cashDividend));
        setStockDividend(new PriceSqlType(priceAdjustment.stockDividend));
        setHighRange(new PriceSqlType(priceAdjustment.highRange));
        setLowRange(new PriceSqlType(priceAdjustment.lowRange));
    }

    /*
    *   UD 06/06/05
    *   set the failed product error details.
    */
    private void setAdjustmentResult(ExceptionDetails details, PriceAdjustmentReportingClassResultStruct resultStruct, int reportingClassKey)
    {
        if(details == null) {
            details = new ExceptionDetails();
            details.error = 0;
            details.message = "";
        }
        resultStruct.reportingClassKey = reportingClassKey;
        resultStruct.reportingClassErrorResult.errorCode = details.error;
        resultStruct.reportingClassErrorResult.errorMessage = details.message;
        // if there is an exception at reporting class level, then products are not processed.
        // so the product result need to be empty and of 0 len
        if(details.error != 0) {
            resultStruct.products = new PriceAdjustmentProductResultStruct[0];
        }
    }

    /**
    *   UD Validates Price Adjustment.
    *   Returns true if there are no errors on the current and new reporting class
    *   Errors ->
    *       If the current reporting class key being adjusted is not provided
    *       If the new reporting class symbol doesn't exist in SBT, needs to be created first by IPD.
    *
    */
    private boolean validateReportingClassAdjustment(PriceAdjustmentClassStruct adjustment, ExceptionDetails details)
    {
        boolean flag = false;
        if(adjustment.classKey <= 0) {
            details.message = "PriceAdjustmentError: Reporting class key is not provided for[" + adjustment.currentClassSymbol + "]";
            details.error = DataValidationCodes.INVALID_REPORTING_CLASS;
        }
        try {
            DomainObjectReferenceHelper.getReportingClassHome().findByKey(adjustment.classKey);
            DomainObjectReferenceHelper.getReportingClassHome().findBySymbol(adjustment.newClassSymbol, adjustment.productType);
            flag = true;
        }
        catch (NotFoundException e) {
            details.message = "PriceAdjustmentError:" + e.details.message;
            details.error = DataValidationCodes.INVALID_REPORTING_CLASS;
        }
        if (adjustment.action == PriceAdjustmentActions.PRICE_ADJUSTMENT_CREATE){
            details.message = "PriceAdjustmentError: Invalid reporting class action PRICE_ADJUSTMENT_CREATE" + adjustment.currentClassSymbol + "]";
            details.error = DataValidationCodes.INVALID_PRICE_ADJUSTMENT;
        }
        return flag;
    }

    /**
    *   UD initializes PriceAdjustmentReportingClassResultStruct...
    */
    private PriceAdjustmentReportingClassResultStruct initializeReportingClassResultStruct(int adjustedProducts, int productKey)
    {
        PriceAdjustmentReportingClassResultStruct reportingClassAdjustmentsResult = new PriceAdjustmentReportingClassResultStruct();
        reportingClassAdjustmentsResult.reportingClassErrorResult = new OperationResultStruct();
        reportingClassAdjustmentsResult.products = new PriceAdjustmentProductResultStruct[adjustedProducts];
        reportingClassAdjustmentsResult.underlyingProductKey = productKey;
        return reportingClassAdjustmentsResult;
    }

    /**
     * Searches for class adjustment.
     *
     * @see PriceAdjustment#findClassAdjustment
     */
    public ReportingClassAdjustment[] findClassAdjustments(String classSymbol, short productType) throws NotFoundException
    {
    	Enumeration adjustmentsEnum = getAdjustedClassCollection().elements();
    	ReportingClassAdjustmentImpl adjustedClass;
    	int i = 0;
    	ArrayList<ReportingClassAdjustment> rptClassesList = new ArrayList<ReportingClassAdjustment>();
    	while (adjustmentsEnum.hasMoreElements())
    	{
    		adjustedClass = (ReportingClassAdjustmentImpl) adjustmentsEnum.nextElement();
    		if(adjustedClass.getProductType() == productType) 
			{
    			rptClassesList.add(adjustedClass);
    			i++;
			}
    	}
    	if( i == 0 || rptClassesList.isEmpty() ) {
    		throw ExceptionBuilder.notFoundException("No adjustment found for class symbol = " + classSymbol, NotFoundCodes.RESOURCE_DOESNT_EXIST);
    	}
    	return rptClassesList.toArray(new ReportingClassAdjustment[rptClassesList.size()]);
    }
}
