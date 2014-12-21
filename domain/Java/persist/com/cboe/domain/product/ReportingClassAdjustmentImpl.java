package com.cboe.domain.product;

import com.cboe.idl.cmiProduct.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiConstants.PriceAdjustmentActions;
import com.cboe.idl.cmiConstants.ListingStates;
import com.cboe.idl.cmiConstants.PriceAdjustmentTypes;
import com.cboe.idl.cmiConstants.ProductTypes;
import com.cboe.idl.cmiErrorCodes.*;
import com.cboe.idl.product.*;
import com.cboe.idl.cmiUtil.OperationResultStruct;
import com.cboe.domain.util.*;
import com.cboe.util.*;
import com.cboe.interfaces.domain.product.*;
import com.cboe.infrastructureServices.persistenceService.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

import java.util.*;
import java.lang.reflect.*;

/**
 * A persistent implementation of <code>ReportingClassAdjustment</code>.  JavaGrinder is
 * used for O-R mapping.
 *
 * @author John Wickberg
 */
public class ReportingClassAdjustmentImpl extends DomainBaseImpl implements ReportingClassAdjustment
{
	/**
	 * The action to be taken when this item is applied.
	 */
	private short actionType;
	/**
	 * The reporting class being changed by this adjustment.
	 */
	private ReportingClassImpl reportingClass;
	/**
	 * The price adjustment that is the parent of this reporting class adjustment.
	 */
	private PriceAdjustmentImpl priceAdjustment;
	/**
	 * The new symbol for this reporting class.
	 */
	private String newClassSymbol;
    /**
     * Product type code of new class.
     */
    private short productType;
	/**
	 * The shares per contract after the adjustment.
	 */
	private int afterContractSize;
	/**
	 * The products of this reporting class that are being changed by this adjustment.
	 */
	private Vector adjustedProductCollection;
	/**
	 * Cached reference to the reporting class home.
	 */
	private static ReportingClassHome reportingClassHome;
	/**
	 * Cached reference to the product class home.
	 */
	private static ProductClassHome productClassHome;
	/*
	 * Field definitions required for JavaGrinder.
	 */
	private static Field _adjustedProductCollection;
	private static Field _afterContractSize;
    private static Field _productType;
	private static Field _newClassSymbol;
	private static Field _priceAdjustment;
	private static Field _reportingClass;
	private static Field _actionType;
	private static Vector classDescriptor;
	/**
	* This static block will be regenerated if persistence is regenerated.
	*/
	static { /*NAME:fieldDefinition:*/
		try{
			_actionType = ReportingClassAdjustmentImpl.class.getDeclaredField("actionType");
			_actionType.setAccessible(true);
			_reportingClass = ReportingClassAdjustmentImpl.class.getDeclaredField("reportingClass");
			_reportingClass.setAccessible(true);
			_priceAdjustment = ReportingClassAdjustmentImpl.class.getDeclaredField("priceAdjustment");
			_priceAdjustment.setAccessible(true);
			_newClassSymbol = ReportingClassAdjustmentImpl.class.getDeclaredField("newClassSymbol");
			_newClassSymbol.setAccessible(true);
			_productType = ReportingClassAdjustmentImpl.class.getDeclaredField("productType");
			_productType.setAccessible(true);
			_afterContractSize = ReportingClassAdjustmentImpl.class.getDeclaredField("afterContractSize");
			_afterContractSize.setAccessible(true);
			_adjustedProductCollection = ReportingClassAdjustmentImpl.class.getDeclaredField("adjustedProductCollection");
			_adjustedProductCollection.setAccessible(true);
		}
		catch (NoSuchFieldException ex) { System.out.println(ex); }
	}
/**
 * ReportingClassAdjustmentImpl constructor comment.
 */
public ReportingClassAdjustmentImpl() {
	super();
    setUsing32bitId(true);
}
/**
 * Applies this adjustment.
 *
 * @see ReportingClassAdjustment#apply
 */
public void apply() throws TransactionFailedException
{
	ReportingClass tempClass = getReportingClass();

    Log.information(this, "============= Applying Adjustment for: " + tempClass.getSymbol() + " actionType: " + getActionType());
	switch (getActionType())
	{
	case PriceAdjustmentActions.PRICE_ADJUSTMENT_UPDATE:
        // UD 10/07/05, set the contract size, move products not entire class
        // If the old reporting class and new reporting class symbols are diff, update contract size of new reporting class.
        // If the symbols are same, update the contract size on the old reporting class...
        ReportingClass newClass = null;
        try {
            newClass = getReportingClassHome().findBySymbol(getNewClassSymbol(), getProductType());

            if(!tempClass.getSymbol().equals(newClass.getSymbol()) ) {
                //  update the new reporting class contract size
                newClass.setContractSize(getAfterContractSize());
            }
            else {
                // else update the current reporting class contract size
                tempClass.setContractSize(getAfterContractSize());
            }
        }
        catch (NotFoundException e) {
            // Ignore this exception, new rpt class will get created during product adjustment move if from TPF ...
            // With IPD, new rpt class will always exist, so will we will never come here to this block ...
        }

		break;
	case PriceAdjustmentActions.PRICE_ADJUSTMENT_DELETE:
		tempClass.setListingState(ListingStates.INACTIVE);
		break;
	case PriceAdjustmentActions.PRICE_ADJUSTMENT_CREATE:
        applyPriceAdjustmentCreate();
		break;
	case PriceAdjustmentActions.PRICE_ADJUSTMENT_MOVE:
		// only products can be moved, not classes
		break;
	default:
		throw ExceptionBuilder.transactionFailedException("Reporting class adjustment has an invalid action code = " + this.getActionType(), TransactionFailedCodes.UPDATE_FAILED);
	}

    // 01/07/05, moved code to a private method to move products under a reporting class.
    // 02/09/06, this code facilitates product moves by a range as well, not just being able to
    // move entire set of products from one Rpt_class to another...
    adjustProducts();
}
    /*
    *   UD 01/07/05.
    *   Adjust the products for a reporting class.
    */
    private void adjustProducts() throws TransactionFailedException
    {
        Enumeration prodAdjustmentsEnum = getAdjustedProductCollection().elements();
        ProductAdjustment product;
        while (prodAdjustmentsEnum.hasMoreElements())
        {
            product = (ProductAdjustment) prodAdjustmentsEnum.nextElement();
            product.apply();
        }
    }
    
    /*
    *   UD 01/10/05, for range rollover need to inactivate the reporting class
    *   if there are no remanining series under the reporting class...
    */
    public void inactivateIfEmpty()
    {
        Product[] products = getReportingClass().getProducts(true);
        if(products.length == 0)
        {
            Log.information("ReportingClassAdjustmentImpl >>> Inactivating class as it contains no products " + getReportingClass().getSymbol());
            getReportingClass().setListingState(ListingStates.INACTIVE);    
        }
    }
      
/**
 * Creates instance from CORBA struct.
 *
 * @see ReportingClassAdjustment#create
 */
public void create(PriceAdjustment parent, PriceAdjustmentClassStruct newClassAdjustment) throws DataValidationException
{
    setAdjustmentCreateValues(parent, newClassAdjustment);
	Vector tempProducts = new Vector(newClassAdjustment.items.length);
	ProductAdjustmentImpl adjustment;
	for (int i = 0; i < newClassAdjustment.items.length; i++)
	{
		// Skip creates will be don or thru Manual adds
		// NOTE: THIS DOESNOT WORK (HENCE THE CHECKS - AS OLD PRODUCT NAME IS NOT GIVEN)
		if (newClassAdjustment.items[i].action == PriceAdjustmentActions.PRICE_ADJUSTMENT_CREATE)
		{
            String errStr = "PriceAdjustment: Skipping added product: due to price adjustment: Will get it in the next download or add it manually";
			Log.information(errStr);
			continue;
		}
		adjustment = new ProductAdjustmentImpl();
		// need to make the broker of the new product adjustment the same as this class's broker
		adjustment.setBrokerName(getBrokerName());
        try
        {
            adjustment.create(this, newClassAdjustment.items[i]);
        }
        catch (DataValidationException ex)
        {
            if (ex.details.error == DataValidationCodes.INVALID_PRODUCT)
            {
                adjustment.setAsTransient(true);
                Log.alarm(productNotFoundMessage(newClassAdjustment.items[i].currentName));
                continue;
            }
            throw ex;
        }
		tempProducts.addElement(adjustment);
	}
	setAdjustedProductCollection(tempProducts);
}

/*
*   UD 06/06/05
*   Create Reporting class adjustment and capture result [success or failure]
*   On Exception, need to set the object as transient , so it doesn't get added to the database
*   as a Zombie. This needs to be done, when we need to conitune processing next set of objects.
*/
public void create(PriceAdjustment parent, PriceAdjustmentClassStruct newClassAdjustment, PriceAdjustmentReportingClassResultStruct reportingClassAdjustmentResult) throws DataValidationException
{
    setAdjustmentCreateValues(parent, newClassAdjustment);
	Vector tempProducts = new Vector(newClassAdjustment.items.length);
	ProductAdjustmentImpl adjustment;
    int failedProductAdjustments = 0;
	for (int i = 0; i < newClassAdjustment.items.length; i++)
	{
        reportingClassAdjustmentResult.products[i] = new PriceAdjustmentProductResultStruct();

		// Skip creates will be don or thru Manual adds
		// NOTE: THIS DOESNOT WORK (HENCE THE CHECKS - AS OLD PRODUCT NAME IS NOT GIVEN)
		if (newClassAdjustment.items[i].action == PriceAdjustmentActions.PRICE_ADJUSTMENT_CREATE)
		{
            String errStr = "PriceAdjustment: Skipping added product: due to price adjustment: Will get it in the next download or add it manually";
			Log.information(errStr);
            ExceptionDetails details = new ExceptionDetails();
            details.error = DataValidationCodes.INVALID_PRICE_ADJUSTMENT;
            details.message = errStr;
            setAdjustmentResult(details, reportingClassAdjustmentResult.products[i], 0);
            failedProductAdjustments++;
			continue; // UD, capture the exception and conitue, don't add to product collection.
		}

		adjustment = new ProductAdjustmentImpl();
		// need to make the broker of the new product adjustment the same as this class's broker
		adjustment.setBrokerName(getBrokerName());

        try
        {
            adjustment.create(this, newClassAdjustment.items[i]);
            setAdjustmentResult(null, reportingClassAdjustmentResult.products[i], adjustment.getAdjustedProduct().getProductKey());
        }
        catch (DataValidationException ex)
        {
            adjustment.setAsTransient(true);
            setAdjustmentResult(ex.details, reportingClassAdjustmentResult.products[i], 0);
            Log.alarm(productNotFoundMessage(newClassAdjustment.items[i].currentName));
            failedProductAdjustments++;
            continue; // return; UD, capture the exception and return, don't add to product collection
        }
        tempProducts.addElement(adjustment);
	}

    // For futures, product adjustments are never created. So don't throw exception for Future...
    if(newClassAdjustment.productType != ProductTypes.FUTURE && failedProductAdjustments == newClassAdjustment.items.length) {
        // Don't create the adjustment for the reporting class. set this reporting class adjustment as transient
        // or the object gets created in the database like a zombie entry with no reference to other object/data.
        // throw exception, so that the reporting class adjustment is marked as error ...
        this.setAsTransient(true);
        throw ExceptionBuilder.dataValidationException("All products adjustments have errors for reporting class[" +
                + newClassAdjustment.classKey + "], adjustment not created", DataValidationCodes.INVALID_REPORTING_CLASS);
    }
	setAdjustedProductCollection(tempProducts);
}

/*
*   Return message string for product not found for adjustment
*/
private String productNotFoundMessage(ProductNameStruct name)
{
    return "PriceAdjustmentError: product cannot be found for item (will continue).  "
        + name.productSymbol + ":" + name.reportingClass + ":" + name.optionType + ":"
        + name.exercisePrice.whole + " " + name.exercisePrice.fraction + ":"
        + name.expirationDate.year + "/" + name.expirationDate.month + "/" + name.expirationDate.day;
}

private void setReportingClassToBeAdjusted(PriceAdjustmentClassStruct newClassAdjustment)
throws DataValidationException
{
	if (newClassAdjustment.action == PriceAdjustmentActions.PRICE_ADJUSTMENT_CREATE){
        return; //no reporting class to be adjusted. New creation
    }
    try
    {
        ReportingClass adjustedClass = findReportingClass(newClassAdjustment);
        setReportingClass(adjustedClass);
    }
    catch (NotFoundException e)
    {
        String msg = "ReportingClassAdjustmentImpl >>> setReportingClassToBeAdjusted. ";
        msg = msg + toString(newClassAdjustment) + " Could not find reporting class being adjusted";
        Log.alarm(msg);
        throw ExceptionBuilder.dataValidationException(msg, DataValidationCodes.INVALID_REPORTING_CLASS);
    }
}

public ProductAdjustment findProductAdjustment(int productKey) throws NotFoundException
{
	Enumeration prodAdjustmentsEnum = getAdjustedProductCollection().elements();
	ProductAdjustment tempProduct;
	while (prodAdjustmentsEnum.hasMoreElements())
	{
		tempProduct = (ProductAdjustment) prodAdjustmentsEnum.nextElement();
		if (tempProduct.getAdjustedProduct() != null && tempProduct.getAdjustedProduct().getProductKey() == productKey)
		{
			return tempProduct;
		}
	}
	throw ExceptionBuilder.notFoundException("Could not find requested product adjustment for product = " + productKey, NotFoundCodes.RESOURCE_DOESNT_EXIST);
}
/**
 * findProductAdjustment method comment.
 */
public ProductAdjustment findProductAdjustment(PriceAdjustmentItemStruct adjustedProduct) throws NotFoundException
{
	ProductNameStruct searchNameStruct;
	String searchName;
	Enumeration prodAdjustmentsEnum = getAdjustedProductCollection().elements();
	if (adjustedProduct.action != PriceAdjustmentActions.PRICE_ADJUSTMENT_CREATE)
	{
		searchNameStruct = adjustedProduct.currentName;
	}
	else
	{
		searchNameStruct = adjustedProduct.newName;
	}
	// convert expiration date to standard date before search.
    int expirationStyle = findProductClass(getProductType()).getExpirationStyle();
	searchNameStruct.expirationDate = ExpirationDateFactory.createStandardDate(searchNameStruct.expirationDate, expirationStyle).toStruct();
	searchName = ProductStructBuilder.toString(searchNameStruct);
	ProductAdjustment tempProduct;
	ProductNameStruct tempNameStruct;
	while (prodAdjustmentsEnum.hasMoreElements())
	{
		tempProduct = (ProductAdjustment) prodAdjustmentsEnum.nextElement();
		tempNameStruct = tempProduct.getProductName();
		if (ProductStructBuilder.toString(tempNameStruct).equals(searchName))
		{
			return tempProduct;
		}
	}
	throw ExceptionBuilder.notFoundException("Could not find requested product adjustment", NotFoundCodes.RESOURCE_DOESNT_EXIST);
}
/**
 * Find the product class to use as the parent of a new reporting class
 *
 * @return selected product class
 * @exception NotFoundException if no classes found for underlying
 */
private ProductClass findProductClass(short prodType) throws NotFoundException
{
    ProductClass found = null;
	Product underlyingProduct = getPriceAdjustment().getAdjustedProduct();
	ProductClass[] classes = getProductClassHome().findByUnderlying(underlyingProduct, true);
    for (int i = 0; i < classes.length; i ++ ){
        if (classes[i].getProductType() == prodType){
            found = classes[i];
            break;
        }
    }
	if (found == null) {
        String msg = "ReportingClassAdjustmentImpl >>> Could not find product class ";
        msg = msg + " with underlying product and product type = ";
        msg = msg + underlyingProduct.getProductKey() + ":" + prodType;
		throw ExceptionBuilder.notFoundException(msg,NotFoundCodes.RESOURCE_DOESNT_EXIST);
	}
	return found;
}
/**
 * Finds the reporting class being adjusted.
 *
 * @param adjustedClass CORBA struct of class being adjusted
 * @return found reporting class
 * @exception NotFoundException if reporting class isn't found
 */
private ReportingClass findReportingClass(PriceAdjustmentClassStruct adjustedClass) throws NotFoundException
{
	ReportingClass result;
	if (adjustedClass.classKey != 0)
	{
		result = getReportingClassHome().findByKey(adjustedClass.classKey);
	}
	else
	{
		result = getReportingClassHome().findBySymbol(adjustedClass.currentClassSymbol, getProductType());
	}
	return result;
}
/**
 * Gets action type code.
 */
private short getActionType()
{
	return (short) editor.get(_actionType, actionType);
}
/**
 * Gets collection of adjusted products.
 */
private Vector getAdjustedProductCollection()
{
	return (Vector) editor.get(_adjustedProductCollection, adjustedProductCollection);
}
/**
 * getAdjustedProducts method comment.
 */
public ProductAdjustment[] getAdjustedProducts()
{
	Vector temp = getAdjustedProductCollection();
	ProductAdjustment[] result;
	result = new ProductAdjustment[temp.size()];
	temp.copyInto(result);
	return result;
}
/**
 * Gets contract size after adjustment.
 */
public int getAfterContractSize()
{
	return (int) editor.get(_afterContractSize, afterContractSize);
}
/**
 * Gets symbol of adjusted class.
 *
 * @see ReportingClassAdjustment#getClassSymbol
 */
public String getClassSymbol()
{
	String symbol;
	if (getActionType() != PriceAdjustmentActions.PRICE_ADJUSTMENT_CREATE)
	{
		symbol = getReportingClass().getSymbol();
	}
	else
	{
		symbol = getNewClassSymbol();
	}
	return symbol;
}
/**
 * Gets new symbol of adjusted class.
 */
private String getNewClassSymbol()
{
	return (String) editor.get(_newClassSymbol, newClassSymbol);
}
/**
 * Gets the parent price adjustment.
 */
protected PriceAdjustmentImpl getPriceAdjustment()
{
	return (PriceAdjustmentImpl) editor.get(_priceAdjustment, priceAdjustment);
}
/**
 * Gets the product type.
 */
protected short getProductType()
{
	return (short) editor.get(_productType, productType);
}
/**
 * Gets the product class home.
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
 * Gets the reporting class being adjusted.
 */
private ReportingClassImpl getReportingClass()
{
	return (ReportingClassImpl) editor.get(_reportingClass, reportingClass);
}
/**
 * Gets the reporting class home.
 *
 * @return reporting class home
 */
private ReportingClassHome getReportingClassHome()
{
	if (reportingClassHome == null)
	{
		try
		{
			reportingClassHome = (ReportingClassHome) HomeFactory.getInstance().findHome(ReportingClassHome.HOME_NAME);
		}
		catch (Exception e)
		{
			Log.exception("Unable to find reporting class home", e);
			throw new NullPointerException("Unable to find reporting class home");
		}
	}
	return reportingClassHome;
}
/**
 * Describe how this class relates to the relational database.
 */
private void initDescriptor()
{
	synchronized (ReportingClassAdjustmentImpl.class)
	{
		if (classDescriptor != null)
			return;
		Vector tempDescriptor = super.getDescriptor();
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("act_type_code", _actionType));
		tempDescriptor.addElement(AttributeDefinition.getForeignRelation(ReportingClassImpl.class, "adj_class_key", _reportingClass));
		tempDescriptor.addElement(AttributeDefinition.getForeignRelation(PriceAdjustmentImpl.class, "price_adj_key", _priceAdjustment));
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("new_class_sym", _newClassSymbol));
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("prod_type_code", _productType));
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("after_contr_size", _afterContractSize));
		tempDescriptor.addElement(AttributeDefinition.getCollectionRelation(ProductAdjustmentImpl.class, _adjustedProductCollection));
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
	result.setTableName("RPT_CLASS_ADJ");
	result.setClassDescription(classDescriptor);
	return result;
}
/**
 * Checks to see if this adjustment will create a new class.
 *
 * @see ReportingClassAdjustment#isCreateAdjustment
 */
public boolean isCreateAdjustment()
{
	return getActionType() == PriceAdjustmentActions.PRICE_ADJUSTMENT_CREATE;
}
/**
 * Cascades the delete to the reporting class entries for this adjustment.
 *
 */
public void markForDelete() throws PersistenceException
{
	super.markForDelete();
	ProductAdjustmentImpl adjustment;
	Enumeration prodAdjustmentsEnum = getAdjustedProductCollection().elements();
	while (prodAdjustmentsEnum.hasMoreElements())
	{
		adjustment = (ProductAdjustmentImpl) prodAdjustmentsEnum.nextElement();
		adjustment.markForDelete();
	}
}
/**
 * Sets action type of this adjustment.
 */
private void setActionType(short aValue)
{
	editor.set(_actionType, aValue, actionType);
}
/**
 * Sets collection of adjusted products.
 */
private void setAdjustedProductCollection(Vector aValue)
{
	editor.set(_adjustedProductCollection, aValue, adjustedProductCollection);
}
/**
 * Sets after contract size.
 */
private void setAfterContractSize(int aValue)
{
	editor.set(_afterContractSize, aValue, afterContractSize);
}
/**
 * Sets home for this adjustment and adds all related product adjustments to the
 * container.
 *
 * @param newHome home for this adjustment
 */
public void setBOHome(BOHome newHome)
{
	super.setBOHome(newHome);
	Enumeration prodAdjustmentsEnum = getAdjustedProductCollection().elements();
	ProductAdjustmentImpl product;
	while (prodAdjustmentsEnum.hasMoreElements())
	{
		product = (ProductAdjustmentImpl) prodAdjustmentsEnum.nextElement();
		newHome.addToContainer(product);
	}
}
/**
 * Sets new class symbol.
 */
private void setNewClassSymbol(String aValue)
{
	editor.set(_newClassSymbol, aValue, newClassSymbol);
}
/**
 * Sets parent price adjustment.
 */
private void setPriceAdjustment(PriceAdjustment aValue)
{
	editor.set(_priceAdjustment, (PriceAdjustmentImpl) aValue, priceAdjustment);
}
/**
 * Sets the product type.
 */
private void setProductType(short aValue)
{
	editor.set(_productType, aValue, productType);
}
/**
 * Sets reporting class being adjusted.
 */
private void setReportingClass(ReportingClass aValue)
{
	editor.set(_reportingClass, (ReportingClassImpl) aValue, reportingClass);
}
/**
 * toStruct method comment.
 */
public PriceAdjustmentClassStruct toStruct()
{
	PriceAdjustmentClassStruct result = new PriceAdjustmentClassStruct();
	result.action = getActionType();
	if (getReportingClass() != null)
	{
		result.classKey = getReportingClass().getClassKey();
		result.currentClassSymbol = getReportingClass().getSymbol();
		result.beforeContractSize = getReportingClass().getContractSize();
	}
	result.newClassSymbol = getNewClassSymbol();
    result.productType = getProductType();
	result.afterContractSize = getAfterContractSize();
	ProductAdjustment[] products = getAdjustedProducts();
	result.items = new PriceAdjustmentItemStruct[products.length];
	for (int i = 0; i < products.length; i++)
	{
		result.items[i] = products[i].toStruct();
	}
	return result;
}

/**
 * Updates this adjustment.
 */
public void update(PriceAdjustmentClassStruct updatedClass) throws DataValidationException
{
    setAdjustmentUpdateValues(updatedClass);
	Vector tempProducts = getAdjustedProductCollection();
	ProductAdjustmentImpl adjustment;
	for (int i = 0; i < updatedClass.items.length; i++)
	{
		try
		{
			adjustment = (ProductAdjustmentImpl) findProductAdjustment(updatedClass.items[i]);
			adjustment.update(updatedClass.items[i]);
		}
		catch (NotFoundException e)
		{
            // Skip creates will be done in next download or thru Manual adds
            // NOTE: THIS DOESNOT WORK (HENCE THE CHECKS - AS OLD PRODUCT NAME IS NOT GIVEN)
            if (updatedClass.items[i].action == PriceAdjustmentActions.PRICE_ADJUSTMENT_CREATE)
            {
                Log.information("PriceAdjustment: Skipping added product: due to price adjustment: Will get it in the next download or add it manually");
            }
			else
			{
                adjustment = new ProductAdjustmentImpl();
                // need to make the broker of the new product adjustment the same as this class's broker
                adjustment.setBrokerName(getBrokerName());
                adjustment.create(this, updatedClass.items[i]);
                tempProducts.addElement(adjustment);
			}
		}
	}
}

/**
*   UD 06/06/05 ...
*   Updates this adjustment and captures the result of the adjustment.
*   On Exception, need to set the object as transient , so it doesn't get added to the database
*   as a Zombie. This needs to be done, then we need to conitune processing next set of objects.
*/
public void update(PriceAdjustmentClassStruct updatedClass, PriceAdjustmentReportingClassResultStruct reportingClassAdjustments) throws DataValidationException
{
    setAdjustmentUpdateValues(updatedClass);
	Vector tempProducts = getAdjustedProductCollection();
	ProductAdjustmentImpl adjustment;
    int failedProductAdjustments = 0;
	for (int i = 0; i < updatedClass.items.length; i++)
	{
        reportingClassAdjustments.products[i] = new PriceAdjustmentProductResultStruct();
        
        // Skip creates will be done in next download or thru Manual adds
        // NOTE: THIS DOESNOT WORK (HENCE THE CHECKS - AS OLD PRODUCT NAME IS NOT GIVEN)
        if (updatedClass.items[i].action == PriceAdjustmentActions.PRICE_ADJUSTMENT_CREATE)
        {
            String message = "PriceAdjustment: Skipping added product: due to price adjustment: Will get it in the next download or add it manually";
            Log.information(message);
            ExceptionDetails details = new ExceptionDetails();
            details.error = DataValidationCodes.INVALID_PRICE_ADJUSTMENT;
            details.message = message;
            setAdjustmentResult(details, reportingClassAdjustments.products[i],0);
            failedProductAdjustments++;
        }
		try
		{
			adjustment = (ProductAdjustmentImpl) findProductAdjustment(updatedClass.items[i]);
			adjustment.update(updatedClass.items[i]);
            reportingClassAdjustments.products[i].productKey = adjustment.getAdjustedProduct().getProductKey();
            setAdjustmentResult(null, reportingClassAdjustments.products[i], adjustment.getAdjustedProduct().getProductKey());
		}
		catch (NotFoundException e)
		{
            // Skip creates will be done in next download or thru Manual adds
            // NOTE: THIS DOESNOT WORK (HENCE THE CHECKS - AS OLD PRODUCT NAME IS NOT GIVEN)
            if (updatedClass.items[i].action != PriceAdjustmentActions.PRICE_ADJUSTMENT_CREATE)
            {
                adjustment = new ProductAdjustmentImpl();
                // need to make the broker of the new product adjustment the same as this class's broker
                adjustment.setBrokerName(getBrokerName());
                try {
                    adjustment.create(this, updatedClass.items[i]);
                    setAdjustmentResult(null, reportingClassAdjustments.products[i],reportingClassAdjustments.products[i].productKey);
                }
                catch (DataValidationException dve) {
                    adjustment.setAsTransient(true);
                    Log.alarm("PriceAdjustmentError: " + dve.details.message);
                    setAdjustmentResult(dve.details, reportingClassAdjustments.products[i],reportingClassAdjustments.products[i].productKey);
                    failedProductAdjustments++;
                    continue;
                }
                tempProducts.addElement(adjustment);
			}
		}
	}

    // For futures, product adjustments are never created. So don't throw exception for Future...
    if(updatedClass.productType != ProductTypes.FUTURE && failedProductAdjustments == updatedClass.items.length) {
        this.setAsTransient(true);
        throw ExceptionBuilder.dataValidationException("All products adjustments have errors for reporting class[" +
                + updatedClass.classKey + "], adjustment not created", DataValidationCodes.INVALID_REPORTING_CLASS);
    }
}

/**
 * Validate the adjustment request
 * 1. if currentClassSym = newClassSym, then verify if a reporting class with the same
 *    symbol and product type exists or not. If exists, verify its underlying product is the same
 *    as the price adjustment specifies
 * 2. if currentClassSym != newClassSym, then a reporting class with currentClassSym has to exist,
 *    and if a reporting class with the newClassSym, then reporting class has to have to same product
 *    class as the reporting class with currentClassSym.
 */
private void validateAdjustment(PriceAdjustmentClassStruct updatingRequest)
throws DataValidationException
{
    String curSym = updatingRequest.currentClassSymbol;
    String newSym = updatingRequest.newClassSymbol;
    short prodType = updatingRequest.productType;
    if (updatingRequest.action == PriceAdjustmentActions.PRICE_ADJUSTMENT_CREATE ){
        validateNewSymbolCreation(newSym,prodType);
    }
    else if (curSym.equals(newSym)){
        validateWhenNoSymbolChange(curSym,prodType);
    }
    else {
        validateWhenSymbolChange(curSym,newSym,prodType);
    }
}

/**
 * validate adjustment when there is no reporting class symbol changes
 * verify if there a reporting class exists, and the underyling product
 * for the existing reporting class should be the same as the adjusted
 * product for the price ajustment request.
 */
private void validateWhenNoSymbolChange(String rptClassSym, short prodType)
throws DataValidationException
{
    try {
        ReportingClass curRptClass = getReportingClassHome().findBySymbol(rptClassSym,prodType);
        Product underlying = curRptClass.getProductClass().getUnderlyingProduct();
        Product adjustedUnderlying = getPriceAdjustment().getAdjustedProduct();
        if (underlying == null ||
            underlying.getProductKey() != adjustedUnderlying.getProductKey())
        {
            int underlyingKey = underlying == null ? 0 : underlying.getProductKey();
            String msg = "ReportingClassAdjustmentImpl >>> Can not make adjustment. A reporting class with the same symbol exists but with different underlying product. ";
            msg = msg + "AdjustedUnderlying:ReportClassSym:UnderlyingForExistingRptClass = " ;
            msg = msg + adjustedUnderlying.getProductKey() + ":" + rptClassSym + ":" + underlyingKey;
            Log.alarm(msg);
            throw ExceptionBuilder.dataValidationException(msg, DataValidationCodes.INVALID_REPORTING_CLASS);
        }
    }
    catch (NotFoundException e1){
        //if not found, it is a new reporting class.
    }
}

/**
 * validte adjustment when reporting class symbol changes
 * 1. verify there exists a reporting class with the symbol as curSym and same product type
 * 2. verify if reporting classes with curSym and newSym exist and they should have
 *    the same product class.
 */
private void validateWhenSymbolChange(String curSym, String newSym, short prodType)
throws DataValidationException
{
    ReportingClass curRptClass;
    ReportingClass newRptClass;
    try {
        curRptClass = getReportingClassHome().findBySymbol(curSym,prodType);
    }
    catch (NotFoundException e1){
        String msg = "ReportingClassAjustmentImpl >>> Can not make adjustment. ";
        msg = msg + "The reporting class to be adjusted does not exists. ";
        msg = msg + "CurrnetSym:NewSym = " + curSym + ":" + newSym;
        Log.alarm(msg);
        throw ExceptionBuilder.dataValidationException(msg, DataValidationCodes.INVALID_REPORTING_CLASS);
    }
    try {
        newRptClass = getReportingClassHome().findBySymbol(newSym,prodType);
        if (curRptClass.getProductClass().getClassKey() != newRptClass.getProductClass().getClassKey()){
            String msg = "ReportingClassAjustmentImpl >>> Can not make adjustment. ";
            msg = msg + "Current and new reporting classes are not in the same product class: ";
            msg = msg + "CurrentSym:NewSym = " + curSym + ":" + newSym;
            Log.alarm(msg);
            throw ExceptionBuilder.dataValidationException(msg, DataValidationCodes.INVALID_REPORTING_CLASS);
        }
    }
    catch (NotFoundException e2){
        //if not found, it is a new reporting class
    }
}

/**
 * validte adjustment of new symbol creating
 * 1. trying to find if there exists a reporting class with same symbol and prod type
 */
private void validateNewSymbolCreation(String newSym, short prodType)
throws DataValidationException
{
    ReportingClass newRptClass;
    try {
        newRptClass = getReportingClassHome().findBySymbol(newSym,prodType);
        String msg = "ReportingClassAjustmentImpl >>> Can not create new reporting class. ";
        msg = msg + "A reporting class with same symbol and product type already exists.";
        msg = msg + "new symbol : productType = " + newSym + ":" + prodType;
        Log.alarm(msg);
        throw ExceptionBuilder.dataValidationException(msg, DataValidationCodes.INVALID_REPORTING_CLASS);
    }
    catch (NotFoundException e1){
        //if not found, because it is creating a new symbol
    }
}

/**
 * apply price adjustment create
 */
private void applyPriceAdjustmentCreate() throws TransactionFailedException {
    ReportingClassStruct struct = ProductStructBuilder.buildReportingClassStruct();
    struct.reportingClassSymbol = getNewClassSymbol();
    struct.productType = getProductType();
    try  {
        struct.productClassSymbol = findProductClass(getProductType()).getSymbol();
    }
    catch (NotFoundException e) {
        throw ExceptionBuilder.transactionFailedException("Could not find product class for new reporting class", TransactionFailedCodes.UPDATE_FAILED);
    }
    struct.listingState = ListingStates.ACTIVE;
    struct.contractSize = getAfterContractSize();
    try	{
        getReportingClassHome().create(struct);
    }
    catch (AlreadyExistsException e) {
        throw ExceptionBuilder.transactionFailedException(e.details.message, TransactionFailedCodes.UPDATE_FAILED);
    }
    catch (SystemException e) {
        throw ExceptionBuilder.transactionFailedException(e.details.message, TransactionFailedCodes.UPDATE_FAILED);
    }
    catch (DataValidationException e){
        throw ExceptionBuilder.transactionFailedException(e.details.message, TransactionFailedCodes.UPDATE_FAILED);
    }
}

private String toString(PriceAdjustmentClassStruct classAdjustment){
    String ret = "CurrentSymbol/ProductType/NewSymbol/Action/BeforeContractSize/AfterContractSize:" ;
    ret = ret + classAdjustment.currentClassSymbol + "/" + classAdjustment.productType + "/" ;
    ret = ret + classAdjustment.newClassSymbol + "/" + classAdjustment.action + "/" ;
    ret = ret + classAdjustment.beforeContractSize + "/" + classAdjustment.afterContractSize;
    return ret;
}

    /*
    *   UD 06/06/05
    *   set adjustment values on create.
    */
    private void setAdjustmentCreateValues(PriceAdjustment parent, PriceAdjustmentClassStruct newClassAdjustment) throws DataValidationException
    {
        Log.information("ReportingClassAdjustmentImpl >>> Creating ReportingClassAdjustment: " + toString(newClassAdjustment));
        setPriceAdjustment(parent);
        validateAdjustment(newClassAdjustment);
        setProductType(newClassAdjustment.productType);
        setActionType(newClassAdjustment.action);
        setReportingClassToBeAdjusted(newClassAdjustment);
        if (newClassAdjustment.newClassSymbol.length() == 0)
        {
            // set new symbol to current symbol, if it isn't being changed.
            setNewClassSymbol(getReportingClass().getSymbol());
        }
        else
        {
            setNewClassSymbol(newClassAdjustment.newClassSymbol);
        }
        setAfterContractSize(newClassAdjustment.afterContractSize);
    }

    /*
    *   UD 06/06/05
    *   set adjustment values on an update.
    */
    private void setAdjustmentUpdateValues(PriceAdjustmentClassStruct updatedClass) throws DataValidationException
    {
        validateAdjustment(updatedClass);
        setActionType(updatedClass.action);
        setNewClassSymbol(updatedClass.newClassSymbol);
        setProductType(updatedClass.productType);
        setAfterContractSize(updatedClass.afterContractSize);
    }

    /*
    *   UD 06/06/05
    *   set the failed product error details.
    */
    private void setAdjustmentResult(ExceptionDetails details, PriceAdjustmentProductResultStruct productResult, int productKey)
    {
        if(details == null) {
            details = new ExceptionDetails();
            details.error = 0;
            details.message = "";
        }
        productResult.productErrorResult = new OperationResultStruct();
        productResult.productErrorResult.errorCode = details.error;
        productResult.productErrorResult.errorMessage = details.message;
        productResult.productKey = productKey;

    }
}
