package com.cboe.domain.product;

import java.lang.reflect.Field;
import java.util.Vector;

import com.cboe.domain.util.DomainBaseImpl;
import com.cboe.domain.util.ExpirationDateFactory;
import com.cboe.domain.util.ExpirationDateImpl;
import com.cboe.domain.util.PriceSqlType;
import com.cboe.domain.util.ProductStructBuilder;
import com.cboe.domain.util.SqlScalarTypeInitializer;
import com.cboe.domain.util.StructBuilder;
import com.cboe.exceptions.AlreadyExistsException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.idl.cmiConstants.ListingStates;
import com.cboe.idl.cmiConstants.PriceAdjustmentActions;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.idl.cmiErrorCodes.TransactionFailedCodes;
import com.cboe.idl.cmiProduct.PendingNameStruct;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiProduct.ReportingClassStruct;
import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.idl.product.PriceAdjustmentItemStruct;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.persistenceService.AttributeDefinition;
import com.cboe.infrastructureServices.persistenceService.DBAdapter;
import com.cboe.infrastructureServices.persistenceService.ObjectChangesIF;
import com.cboe.interfaces.domain.ExpirationDate;
import com.cboe.interfaces.domain.product.Product;
import com.cboe.interfaces.domain.product.ProductAdjustment;
import com.cboe.interfaces.domain.product.ProductHome;
import com.cboe.interfaces.domain.product.ReportingClass;
import com.cboe.interfaces.domain.product.ReportingClassAdjustment;
import com.cboe.interfaces.domain.product.ReportingClassHome;
import com.cboe.util.ExceptionBuilder;

/**
 * A persistent implementation of <code>ProductAdjustment<code>.
 *
 * @author John Wickberg
 */
public class ProductAdjustmentImpl extends DomainBaseImpl implements ProductAdjustment
{
	private static final char OPRA_CALL_START_MONTH_CODE = 'A';
    private static final char OPRA_PUT_START_MONTH_CODE = 'M';

    private static final char OPRA_MIN_PRICE_CODE = 'A';
    private static final char OPRA_MAX_PRICE_CODE = 'Z';
    
    /**
     * The product being adjusted.
     */
    private Product adjustedProduct;
    /**
     * The action that will be taken when this adjustment is applied.
     */
    private short actionType;
    /**
     * The parent of this adjustment.
     */
    private ReportingClassAdjustmentImpl reportingClassAdjustment;
    /**
     * The new reporting class symbol.
     */
    private String newClassSymbol;
    /**
     * The new exercise price.
     */
    private PriceSqlType newExercisePrice;
    /**
     * The new expiration date.  Only needed for created products.
     */
    private ExpirationDateImpl newExpirationDate;
    /**
     * The new option type.  Only needed for created products.
     */
    private char newOptionType;
    /**
     * The new OPRA month code.  Only needed for created products.
     */
    private char newOpraMonthCode;
    /**
     * The new OPRA price code.  Only needed for created products.
     */
    private char newOpraPriceCode;
    /**
     * Cached reference to the product home.
     */
    private static ProductHome productHome;
    /**
     * Cached reference to the reporting class home.
     */
    private static ReportingClassHome reportingClassHome;
    /*
     * Field declarations for JavaGrinder
     */
    static Field _newExpirationDate;
    static Field _newExercisePrice;
    static Field _newClassSymbol;
    static Field _newOpraPriceCode;
    static Field _newOpraMonthCode;
    static Field _newOptionType;
    static Field _reportingClassAdjustment;
    static Field _actionType;
    static Field _adjustedProduct;
    static Vector classDescriptor;
    /**
    * This static block will be regenerated if persistence is regenerated.
    */
    static { /*NAME:fieldDefinition:*/
        try{
            _adjustedProduct = ProductAdjustmentImpl.class.getDeclaredField("adjustedProduct");
            _actionType = ProductAdjustmentImpl.class.getDeclaredField("actionType");
            _reportingClassAdjustment = ProductAdjustmentImpl.class.getDeclaredField("reportingClassAdjustment");
            _newOptionType = ProductAdjustmentImpl.class.getDeclaredField("newOptionType");
            _newOpraMonthCode = ProductAdjustmentImpl.class.getDeclaredField("newOpraMonthCode");
            _newOpraPriceCode = ProductAdjustmentImpl.class.getDeclaredField("newOpraPriceCode");
            _newClassSymbol = ProductAdjustmentImpl.class.getDeclaredField("newClassSymbol");
            _newExercisePrice = ProductAdjustmentImpl.class.getDeclaredField("newExercisePrice");
            _newExpirationDate = ProductAdjustmentImpl.class.getDeclaredField("newExpirationDate");
        }
        catch (NoSuchFieldException ex) { System.out.println(ex); }

        SqlScalarTypeInitializer.initTypes();
    }
/**
 * ProductAdjustmentImpl constructor comment.
 */
public ProductAdjustmentImpl() {
    super();
    setUsing32bitId(true);
}
/**
 * Applies this adjustment.
 *
 * @see ProductAdjustment#apply
 */
public void apply() throws TransactionFailedException
{
    Product tempProduct = getAdjustedProduct();
    ProductNameStruct tempProductName = getNewProductName();
    if ( Log.isDebugOn() )
    {
        Log.debug(this, "============= Adjusting product to: " +
                tempProductName.reportingClass + " " +
                tempProductName.expirationDate.toString() + " " +
                tempProductName.exercisePrice.toString() + " " +
                tempProductName.optionType +
                " actionType: " + getActionType());
    }
    switch (getActionType())
    {
    case PriceAdjustmentActions.PRICE_ADJUSTMENT_UPDATE:
        // UD 01/07/05 ... if current product name is not same as new product name,
        // move current product to new product.
        boolean canMovetoNewReportingClass = false;
        String currentClasssymbol = adjustedProduct.getReportingClass().getSymbol();
        if(!currentClasssymbol.equals(tempProductName.reportingClass)) {
            canMovetoNewReportingClass = true;
        }

        // UD 02/13/06 Previously we made explicit call to update just the product name ...
        // Need to call the update method, which updates the product name and opra codes, expr date ...
        // When moving, pass the current class symobl as the product has already been updated with new info ...
        ProductStruct tempProductStruct = tempProduct.toStruct();
        tempProductStruct.productName = tempProductName;
        tempProductStruct.opraMonthCode = getNewOpraMonthCode();
        tempProductStruct.opraPriceCode = getNewOpraPriceCode();
        try {
            tempProduct.update(tempProductStruct);
        }
        catch(DataValidationException e) {
            tempProduct.setNewReportingClassName("");
            throw new TransactionFailedException(e.getMessage(), e.details);
        }
        if( canMovetoNewReportingClass ) {
            moveCurrentProductToNew(currentClasssymbol, tempProduct);
        }
        break;
    case PriceAdjustmentActions.PRICE_ADJUSTMENT_DELETE:
        tempProduct.setListingState(ListingStates.INACTIVE);
        break;
    case PriceAdjustmentActions.PRICE_ADJUSTMENT_CREATE:
        ProductStruct newProduct = ProductStructBuilder.buildProductStruct();
        newProduct.productKeys.productType = getReportingClassAdjustment().getProductType();
        newProduct.productName = getNewProductName();
        newProduct.opraMonthCode = getNewOpraMonthCode();
        newProduct.opraPriceCode = getNewOpraPriceCode();
        try
        {
            getProductHome().create(newProduct);
        }
        catch (AlreadyExistsException e)
        {
            throw new TransactionFailedException(e.getMessage(), e.details);
        }
        catch (DataValidationException e)
        {
            throw new TransactionFailedException(e.getMessage(), e.details);
        }
        break;
    case PriceAdjustmentActions.PRICE_ADJUSTMENT_MOVE:
        // 01/07/05, moved code to private method moveCurrentProductToNew
        // same code is used for PRICE_ADJUSTMENT_UPDATE. 
        moveCurrentProductToNew(adjustedProduct.getReportingClass().getSymbol(), tempProduct);
        break;
    default:
        throw ExceptionBuilder.transactionFailedException("Invalid action code (" + getActionType() + ") for product adjustment.", TransactionFailedCodes.UPDATE_FAILED);
    }
}

    /*
    *   UD 01/07/05
	*   checks to see if old product name and new product name are the same
    */
    private boolean matchOldAndNewProductName(ProductNameStruct oldName, ProductNameStruct newName)
    {
        if(oldName.reportingClass.equals(newName.reportingClass)
            && oldName.productSymbol.equals(newName.productSymbol)
            && oldName.optionType == newName.optionType
            && (oldName.exercisePrice.type == newName.exercisePrice.type
                    && oldName.exercisePrice.whole == newName.exercisePrice.whole
                    && oldName.exercisePrice.fraction == newName.exercisePrice.fraction)
            && (oldName.expirationDate.day ==  newName.expirationDate.day
                    && oldName.expirationDate.month == newName.expirationDate.month
                    && oldName.expirationDate.year == newName.expirationDate.year) )
        {
            return true;
        }
        return false;
    }
		
    /*
    *   UD 01/07/05
    *   Moves the specified product from current reporting class to a new reporting class.
    */
    private void moveCurrentProductToNew(String currentClasssymbol, Product tempProduct) throws TransactionFailedException
    {
        ReportingClass currentClass;
        ReportingClass newClass = null;
        boolean moveToNew = false;
        
        short prodType = getReportingClassAdjustment().getProductType();
        try {
            currentClass = getReportingClassHome().findBySymbol(currentClasssymbol, prodType);
        }
        catch (NotFoundException e) {
            throw ExceptionBuilder.transactionFailedException("Could not find source (" 
                + getReportingClassAdjustment().getClassSymbol() +
                  ") or destination class (" + getNewClassSymbol() + ") for product move.",
                  TransactionFailedCodes.UPDATE_FAILED);
        }
        
        try {
            newClass = getReportingClassHome().findBySymbol(getNewClassSymbol(), prodType);
            if(!currentClass.getSymbol().equals(newClass.getSymbol()) ) {
                moveToNew = true;
            }
        } 
        catch (NotFoundException e) {
            // Create the new reporting class.
            newClass = createNewReportingClass(currentClass);
            moveToNew = true;
        }
        
        if(moveToNew){
            currentClass.removeProduct(tempProduct);
            newClass.addProduct(tempProduct);
            logProductInfo(tempProduct, currentClass, newClass);
        }
    }
    
    /*
    *   UD 01/21/05 ...
    *   Creates a new reporting class.
    */
    private ReportingClass createNewReportingClass(ReportingClass currentClass) throws TransactionFailedException
    {
        ReportingClassStruct struct = currentClass.toStruct();
        struct.reportingClassSymbol = getNewClassSymbol();
        struct.contractSize = getReportingClassAdjustment().getAfterContractSize();
        struct.listingState = ListingStates.ACTIVE;
        try	{
            return getReportingClassHome().create(struct);
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
    
    private void logProductInfo(Product tempProduct, ReportingClass currentClass, ReportingClass newClass)
    {
        StringBuffer logBuf = new StringBuffer();
        logBuf.append("ProductAdjustmentImpl >>> Moving product ");
        logBuf.append(newClass.getSymbol());
        logBuf.append("-").append(tempProduct.getProductKey());;
        logBuf.append("-").append(StructBuilder.toString(tempProduct.getProductName().expirationDate));
        logBuf.append("-").append(StructBuilder.toString(tempProduct.getProductName().exercisePrice));
        logBuf.append("-").append(tempProduct.getProductName().optionType);
        logBuf.append(" from class ").append(currentClass.getSymbol());
        logBuf.append("-").append(currentClass.getClassKey());
        logBuf.append(" to new class ").append(newClass.getSymbol());
        logBuf.append("-").append(newClass.getClassKey());
        Log.information(logBuf.toString());        
    }
    
/**
 * Creates a valid adjustment.
 *
 * @see ProductAdjustment#create
 */
public void create(ReportingClassAdjustment parent, PriceAdjustmentItemStruct newAdjustmentItem) throws DataValidationException
{
    setActionType(newAdjustmentItem.action);
    setReportingClassAdjustment(parent);
    if (newAdjustmentItem.action != PriceAdjustmentActions.PRICE_ADJUSTMENT_CREATE)
    {
        try
        {
            Product tempProduct = getProductHome().findByName(newAdjustmentItem.currentName);
            setAdjustedProduct(tempProduct);
        }
        catch (NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Could not find product being updated by price adjustment: "
                + e + "(" + e.details.message + ")", DataValidationCodes.INVALID_PRODUCT);
        }
    }
    setNewClassSymbol(newAdjustmentItem.newName.reportingClass);
    // FOR IPD, don't convert to expiration style. use as is the passed date.
    setNewExpirationDate(ExpirationDateFactory.createStandardDate(
                    getAdjustedProduct().getProductName().expirationDate, ExpirationDateFactory.ANY_DAY_EXPIRATION));
    setNewExercisePrice(new PriceSqlType(newAdjustmentItem.newName.exercisePrice));
    setNewOptionType(newAdjustmentItem.newName.optionType);
    setNewOpraPriceCode(newAdjustmentItem.newOpraPriceCode);
    
    char newOpraMonthCode = newAdjustmentItem.newOpraMonthCode;    
    vaidateOpraMonthCode(newAdjustmentItem, newOpraMonthCode);
    setNewOpraMonthCode(newOpraMonthCode);
}

private void vaidateOpraMonthCode(PriceAdjustmentItemStruct newAdjustmentItem, char newOpraMonthCode) throws DataValidationException
{
	ExpirationDate expirationDate = ExpirationDateFactory.createStandardDate(
            getAdjustedProduct().getProductName().expirationDate, ExpirationDateFactory.ANY_DAY_EXPIRATION);
    DateStruct dateStruct = expirationDate.toStruct();
    byte month = dateStruct.month;
    if (newAdjustmentItem.newName.optionType == OptionTypeImpl.getCallType().toValue()) {
        if (newOpraMonthCode != OPRA_CALL_START_MONTH_CODE + month - 1) {
            throw ExceptionBuilder.dataValidationException("Invalid OPRA MONTH CODE: " +
                newOpraMonthCode + ".  "  + "It does not match with month number: " + month + ".", DataValidationCodes.INVALID_OPRA_MONTH_CODE);
        }
    }
    else
    {
        if (newOpraMonthCode != OPRA_PUT_START_MONTH_CODE + month - 1) {
            throw ExceptionBuilder.dataValidationException("Invalid OPRA MONTH CODE: " +
                newOpraMonthCode + ".  "  + "It does not match with month number: " + month + ".",DataValidationCodes.INVALID_OPRA_MONTH_CODE);
        }
    }
}

/**
 * Gets action type.
 */
private short getActionType()
{
    return (short) editor.get(_actionType, actionType);
}
/**
 * Gets adjusted product.
 *
 * @see ProductAdjustment#getAdjustedProduct
 */
public Product getAdjustedProduct()
{
    return (Product) editor.get(_adjustedProduct, adjustedProduct);
}
/**
 * Gets new class symbol.
 */
private String getNewClassSymbol()
{
    return (String) editor.get(_newClassSymbol, newClassSymbol);
}
/**
 * Gets new exercise price.
 */
private PriceSqlType getNewExercisePrice()
{
    return (PriceSqlType) editor.get(_newExercisePrice, newExercisePrice);
}
/**
 * Gets expiration date.
 */
private ExpirationDate getNewExpirationDate()
{
    return (ExpirationDate) editor.get(_newExpirationDate, newExpirationDate);
}
/**
 * Gets OPRA month code for created products.
 */
private char getNewOpraMonthCode()
{
    return (char) editor.get(_newOpraMonthCode, newOpraMonthCode);
}
/**
 * Gets new OPRA price code.
 */
private char getNewOpraPriceCode()
{
    return (char) editor.get(_newOpraPriceCode, newOpraPriceCode);
}
/**
 * Gets option type.
 */
private char getNewOptionType()
{
    return (char) editor.get(_newOptionType, newOptionType);
}
/**
 * Gets new product name of product being adjusted.
 *
 * @see ProductAdjustment#getNewProductName
 */
public ProductNameStruct getNewProductName()
{
    ProductNameStruct result = ProductStructBuilder.buildProductNameStruct();
    result.reportingClass = getNewClassSymbol();
    result.expirationDate = getNewExpirationDate().toStruct();
    result.exercisePrice = getNewExercisePrice().toStruct();
    result.optionType = getNewOptionType();
    return result;
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
 * Gets product name of product being adjusted.
 *
 * @see ProductAdjustment#getProductName
 */
public ProductNameStruct getProductName()
{
    ProductNameStruct result;
    if (getActionType() != PriceAdjustmentActions.PRICE_ADJUSTMENT_CREATE)
    {
        result = getAdjustedProduct().getProductName();
    }
    else
    {
        result = getNewProductName();
    }
    return result;
}
/**
 * Gets reporting class adjustment that is parent of this adjustment
 */
private ReportingClassAdjustmentImpl getReportingClassAdjustment()
{
    return (ReportingClassAdjustmentImpl) editor.get(_reportingClassAdjustment, reportingClassAdjustment);
}
/**
 * Gets home for reporting classes.
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
    synchronized (ProductAdjustmentImpl.class)
    {
        if (classDescriptor != null)
            return;
        Vector tempDescriptor = super.getDescriptor();
        tempDescriptor.addElement(AttributeDefinition.getForeignRelation(ProductImpl.class, "adj_prod_key", _adjustedProduct));
        tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("act_type_code", _actionType));
        tempDescriptor.addElement(AttributeDefinition.getForeignRelation(ReportingClassAdjustmentImpl.class, "rpt_class_adj_key", _reportingClassAdjustment));
        tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("new_opt_type_code", _newOptionType));
        tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("new_opra_month_code", _newOpraMonthCode));
        tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("new_opra_price_code", _newOpraPriceCode));
        tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("new_class_sym", _newClassSymbol));
        tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("new_exer_price", _newExercisePrice));
        tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("new_expr_date", _newExpirationDate));
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
    result.setTableName("PROD_ADJ");
    result.setClassDescription(classDescriptor);
    return result;
}
/**
 * Sets action type.
 */
private void setActionType(short aValue)
{
    editor.set(_actionType, aValue, actionType);
}
/**
 * Sets reference to adjusted product.
 */
private void setAdjustedProduct(Product aValue)
{
    editor.set(_adjustedProduct, aValue, adjustedProduct);
}
/**
 * Sets new class symbol.
 */
private void setNewClassSymbol(String aValue)
{
    editor.set(_newClassSymbol, aValue, newClassSymbol);
}
/**
 * Sets new exercise price.
 */
private void setNewExercisePrice(PriceSqlType aValue)
{
    editor.set(_newExercisePrice, aValue, newExercisePrice);
}
/**
 * Sets expiration date.  Only needed for created products.
 */
private void setNewExpirationDate(ExpirationDate aValue)
{
    editor.set(_newExpirationDate, aValue, newExpirationDate);
}
/**
 * Sets OPRA month code for new products.
 */
private void setNewOpraMonthCode(char aValue)
{
    editor.set(_newOpraMonthCode, aValue, newOpraMonthCode);
}
/**
 * Sets new OPRA price code.
 */
private void setNewOpraPriceCode(char aValue)
{
    editor.set(_newOpraPriceCode, aValue, newOpraPriceCode);
}
/**
 * Sets option type for created products.
 */
private void setNewOptionType(char aValue)
{
    editor.set(_newOptionType, aValue, newOptionType);
}
/**
 * Sets reference reporting class adjustment that is parent of this adjustment.
 */
private void setReportingClassAdjustment(ReportingClassAdjustment aValue)
{
    editor.set(_reportingClassAdjustment, (ReportingClassAdjustmentImpl) aValue, reportingClassAdjustment);
}
/**
 * Converts this adjustment to a pending name struct.
 *
 * @return CORBA struct containing information about name change.
 */
public PendingNameStruct toPendingName()
{
    PendingNameStruct result = new PendingNameStruct();
    result.action = getActionType();
    if (result.action != PriceAdjustmentActions.PRICE_ADJUSTMENT_CREATE)
    {
        result.productStruct = getAdjustedProduct().toStruct();
    }
    else
    {
        result.productStruct = ProductStructBuilder.buildProductStruct();
    }
    if (result.action != PriceAdjustmentActions.PRICE_ADJUSTMENT_DELETE)
    {
        result.pendingProductName = getNewProductName();
    }
    else
    {
        result.pendingProductName = ProductStructBuilder.buildProductNameStruct();
    }
    return result;
}
/**
 * Converts this product adjustment to a CORBA struct.
 *
 * @see ProductAdjustment#toStruct
 */
public PriceAdjustmentItemStruct toStruct()
{
    PriceAdjustmentItemStruct result = ProductStructBuilder.buildPriceAdjustmentItemStruct();
    result.action = getActionType();
    if (getActionType() != PriceAdjustmentActions.PRICE_ADJUSTMENT_CREATE)
    {
        result.currentName = getProductName();
    }
    result.newName = getNewProductName();
    result.newOpraMonthCode = getNewOpraMonthCode();
    result.newOpraPriceCode = getNewOpraPriceCode();
    return result;
}
/**
 * Updates item with values from CORBA struct.
 *
 * @see ProductAdjustment#update
 */
public void update(PriceAdjustmentItemStruct updatedItem)
{
    setActionType(updatedItem.action);
    // current values haven't been changed since item was found.
    //
    // new values may have been changed.
    int expirationStyle = getAdjustedProduct().getProductClass().getExpirationStyle();
    ExpirationDate standardDate = ExpirationDateFactory.createStandardDate(updatedItem.newName.expirationDate,expirationStyle);
    setNewClassSymbol(updatedItem.newName.reportingClass);
    setNewExpirationDate(standardDate);
    setNewExercisePrice(new PriceSqlType(updatedItem.newName.exercisePrice));
    setNewOptionType(updatedItem.newName.optionType);
    setNewOpraPriceCode(updatedItem.newOpraPriceCode);
    setNewOpraMonthCode(updatedItem.newOpraMonthCode);
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
}
