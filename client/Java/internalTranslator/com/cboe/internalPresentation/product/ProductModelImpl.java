// -----------------------------------------------------------------------------------
// Source file: ProductModelImpl.java
//
// PACKAGE: com.cboe.internalPresentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.product;

import java.text.ParseException;
import java.util.*;

import org.omg.CORBA.UserException;

import com.cboe.idl.cmiConstants.ListingStates;
import com.cboe.idl.cmiConstants.ProductClass;
import com.cboe.idl.cmiConstants.ProductTypes;
import com.cboe.idl.cmiConstants.PriceTypes;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.idl.cmiUtil.TimeStruct;
import com.cboe.idl.product.ProductStructV4;

import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.dateTime.Date;
import com.cboe.interfaces.domain.dateTime.DateTime;
import com.cboe.interfaces.internalPresentation.product.ProductCusip;
import com.cboe.interfaces.internalPresentation.product.ProductModel;
import com.cboe.interfaces.presentation.product.ExpirationType;
import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.presentation.validation.ValidationErrorCodes;
import com.cboe.interfaces.presentation.validation.ValidationResult;

import com.cboe.util.ExceptionBuilder;

import com.cboe.presentation.common.businessModels.AbstractMutableBusinessModel;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.formatters.DisplayPriceFactory;
import com.cboe.presentation.common.dateTime.DateTimeFactory;
import com.cboe.presentation.validation.ValidationResultImpl;
import com.cboe.presentation.util.StringCache;
import com.cboe.presentation.product.ProductFactoryHome;

import com.cboe.internalPresentation.api.SystemAdminAPIFactory;

import com.cboe.domain.util.ExtensionsHelper;
import com.cboe.domain.util.PriceFactory;

/**
 * ProductModel implementation .
 */
class ProductModelImpl extends AbstractMutableBusinessModel implements ProductModel
{
    private ProductCusip cusip;
    private Price closingPrice;
    private String closingSuffix;
    private boolean restrictedProductIndicator;
    private ExtensionsHelper extensionsHelper;

    // Product fields
    private int               hashCode;
    private ExpirationType    expirationType; //  = ExpirationType.STANDARD;
    private boolean           isAllSelectedProduct;
    private boolean           isDefaultProduct;
    private boolean           leapIndicator;

    // ProductStruct fields
    // ProductKeysStruct and ProductNameStruct are not stored, but created on the fly
    private short             listingState;
    private String            description;
    private String            companyName;
    private String            unitMeasure;
    private double            standardQuantity;
    private Date              maturityDate;                                     // DateStruct in ProductStruct
    private Date              activationDate;                                   // DateStruct in ProductStruct
    private Date              inactivationDate;                                 // DateStruct in ProductStruct
    private DateTime          createdTime;                                      // DateTimeStruct in ProductStruct
    private DateTime          lastModifiedTime;                                 // DateTimeStruct in ProductStruct
    private char              opraMonthCode;
    private char              opraPriceCode;

    // ProductKeysStruct fields
    private int               productKey;
    private int               classKey;
    private short             productType;
    private int               reportingClassKey;

    // ProductNameStruct fields
    private String            reportingClassName;
    private Price             exercisePrice;                                    // PriceStruct in ProductNameStruct
    private Date              expirationDate;                                   // DateStruct in ProductNameStruct
    private char              optionType;
    private String            productSymbol;


    /**
     * Constructor
     * @param product struct to represent
     */
    protected ProductModelImpl(Product product)
    {
        this(product, null);

    }

    public ProductModelImpl(Product product, Comparator comparator)
    {
        super(comparator);
        updateFromProduct(product);
    }

    protected void updateFromProduct(Product product)
    {
        hashCode = product.hashCode();
        expirationType = product.getExpirationType();
        isAllSelectedProduct = product.isAllSelectedProduct();
        isDefaultProduct = product.isDefaultProduct();
        leapIndicator = product.getLeapIndicator();
        updateFromStruct(product.getProductStruct());
    }

    protected void updateFromStruct(ProductStruct struct)
    {
        updateFromStruct(struct.productKeys);
        updateFromStruct(struct.productName);

        listingState     = struct.listingState;
        description      = StringCache.get(struct.description);
        companyName      = StringCache.get(struct.companyName);
        unitMeasure      = StringCache.get(struct.unitMeasure);
        standardQuantity = struct.standardQuantity;
        maturityDate     = DateTimeFactory.getDate(struct.maturityDate);
        activationDate   = DateTimeFactory.getDate(struct.activationDate);
        inactivationDate = DateTimeFactory.getDate(struct.inactivationDate);
        createdTime      = DateTimeFactory.getDateTime(struct.createdTime);
        lastModifiedTime = DateTimeFactory.getDateTime(struct.lastModifiedTime);
        opraMonthCode    = struct.opraMonthCode;
        opraPriceCode    = struct.opraPriceCode;
    }

    protected void updateFromStruct(ProductKeysStruct struct)
    {
        productKey        = struct.productKey;
        classKey          = struct.classKey;
        productType       = struct.productType;
        reportingClassKey = struct.reportingClass;
    }

    protected void updateFromStruct(ProductNameStruct struct)
    {
        reportingClassName = StringCache.get(struct.reportingClass);
        optionType         = struct.optionType;
        productSymbol      = StringCache.get(struct.productSymbol);
        expirationDate     = DateTimeFactory.getDate(struct.expirationDate);
        exercisePrice      = convertExercisePrice(struct.exercisePrice);
    }

    public boolean isAllSelectedProduct()
    {
        return isAllSelectedProduct;
    }

    public boolean isDefaultProduct()
    {
        return isDefaultProduct;
    }

    /**
     * Get the ProductStruct that this Product represents.
     * @return ProductStruct
     * @deprecated
     */
    public ProductStruct getProductStruct()
    {
        ProductStruct retVal    = new ProductStruct();
        retVal.productKeys      = getProductKeysStruct();
        retVal.productName      = getProductNameStruct();

        retVal.listingState     = getListingState();
        retVal.description      = getDescription();
        
        retVal.companyName      = getCompanyName();
        retVal.unitMeasure      = getUnitMeasure();
        retVal.standardQuantity = getStandardQuantity();
        retVal.maturityDate     = getMaturityDate();
        retVal.activationDate   = getActivationDate();
        retVal.inactivationDate = getInactivationDate();
        retVal.createdTime      = getCreatedTime();
        retVal.lastModifiedTime = getLastModifiedTime();
        retVal.opraMonthCode    = getOpraMonthCode();
        retVal.opraPriceCode    = getOpraPriceCode();
        return retVal;
    }

    /**
     * Get the type that this Product represents.
     * @return short
     */
    public short getProductType()
    {
        return productType;
    }

    /**
     * Get the product key for this Product.
     * @return product key from represented struct
     */
    public int getProductKey()
    {
        return productKey;
    }

    /**
     * Get the ProductKeysStruct for this Product.
     * @return ProductKeysStruct from represented struct
     */
    public ProductKeysStruct getProductKeysStruct()
    {
        ProductKeysStruct retVal = new ProductKeysStruct();
        retVal.productKey        = getProductKey();
        retVal.classKey          = classKey;
        retVal.productType       = getProductType();
        retVal.reportingClass    = reportingClassKey;
        return retVal;
    }

    /**
     * Get the ProductNameStruct for this Product.
     * @return ProductNameStruct from represented struct
     */
    public ProductNameStruct getProductNameStruct()
    {
        ProductNameStruct retVal = new ProductNameStruct();
        retVal.reportingClass    = reportingClassName;
        retVal.optionType        = optionType;
        retVal.productSymbol     = productSymbol;
        retVal.expirationDate    = expirationDate.getDateStruct();
        retVal.exercisePrice     = convertExercisePrice(exercisePrice);
        return retVal;
    }

    /**
     * Get the listing state for this Product.
     * @return listing state from represented struct
     */
    public short getListingState()
    {
        return listingState;
    }

    /**
     * Get the description for this Product.
     * @return description from represented struct
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Get the company name for this Product.
     * @return company name from represented struct
     */
    public String getCompanyName()
    {
        return companyName;
    }

    /**
     * Get the unit of measure for this Product.
     * @return unit of measure from represented struct
     */
    public String getUnitMeasure()
    {
        return unitMeasure;
    }

    /**
     * Get the standard qty for this Product.
     * @return standard qty from represented struct
     */
    public double getStandardQuantity()
    {
        return standardQuantity;
    }

    /**
     * Get the maturity date for this Product.
     * @return maturity date from represented struct
     */
    public DateStruct getMaturityDate()
    {
        return maturityDate.getDateStruct();
    }

    /**
     * Get the activation date for this Product.
     * @return activation date from represented struct
     */
    public DateStruct getActivationDate()
    {
        return activationDate.getDateStruct();
    }

    /**
     * Get the inactivation date for this Product.
     * @return inactivation date from represented struct
     */
    public DateStruct getInactivationDate()
    {
        return inactivationDate.getDateStruct();
    }

    /**
     * Get the created time for this Product.
     * @return created time from represented struct
     */
    public DateTimeStruct getCreatedTime()
    {
        return createdTime.getDateTimeStruct();
    }

    /**
     * Get the last modified time for this Product.
     * @return last modified time from represented struct
     */
    public DateTimeStruct getLastModifiedTime()
    {
        return lastModifiedTime.getDateTimeStruct();
    }

    /**
     * Get the opra month code for this Product.
     * @return opra month code from represented struct
     */
    public char getOpraMonthCode()
    {
        return opraMonthCode;
    }

    /**
     * Get the opra price code for this Product.
     * @return opra price code from represented struct
     */
    public char getOpraPriceCode()
    {
        return opraPriceCode;
    }

    /**
     * Get the excersize price for this Product.
     * @return excersize price
     */
    public Price getExercisePrice()
    {
        return exercisePrice;
    }

    /**
     * Gets the expiration date of the product
     */
    public Date getExpirationDate()
    {
        return expirationDate;
    }

    /**
     * Clones this product by returning another instance that represents a
     * ProductStruct that was also cloned.
     */
    public Object clone() throws CloneNotSupportedException
    {
        ProductModelImpl dest = new ProductModelImpl(this, getComparator());

        // the cloned ProductModel is built from data in Product, but Product does not contain cusip, so we'll  add that separately
        ProductCusip clonedCusip = null;
        ProductCusip myCusip     = getCusip();
        if (myCusip != null)  // it can be empty string
        {
            clonedCusip = (ProductCusip) myCusip.clone();
        }
        dest.setCusip(clonedCusip, false);

        Price closingPrice = getClosingPrice();
        if(closingPrice != null)
        {
            dest.setClosingPrice(closingPrice.toStruct(), false);
        }

        dest.setClosingSuffix(getClosingSuffix(), false);
        dest.setRestrictedProduct(isRestrictedProduct(), false);
        dest.setExtensions(getExtensions(), false);
        return dest;
    }

    public int compareTo(Object obj)
    {
        int retVal = -1;

        if ( this == obj )
        {
            retVal = 0;
        }
        else if ( obj instanceof  ProductModel )
        {
            retVal = getProductKey() - ((ProductModel)obj).getProductKey();
        }

        return retVal;
    }

    /**
     * If <code>obj</code> is an instance of ProductModel and has the same
     * product key true is returned, false otherwise.
     * @param obj to compare
     * @return true if equal, false if not.
     */
    public boolean equals(Object obj)
    {
        return compareTo(obj)==0;
    }

//    /**
//     * Returns a String representation of this Product.
//     */
//    public String toString()
//    {
//        return super.toString();
//    }

    /**
     * Set the Product that this ProductModel represents.
     * @param product
     */
    public void setProduct(Product product)
    {
        setProduct(product, true);
    }

    /**
     * If the original struct's exercise price was a NO_PRICE or MARKET, the
     * exercisePrice wrapper will be a ValuedPrice with value 0.0.
     *
     * This will return the actual price type of the original ProductNameStruct,
     * to be used when creating a new ProductNameStruct that has the same type
     * as the original.
     */
    private Price convertExercisePrice(PriceStruct priceStruct)
    {
        Price price;
        if (priceStruct.type == PriceTypes.NO_PRICE || priceStruct.type == PriceTypes.MARKET)
        {
            price = DisplayPriceFactory.create(0.0);
        }
        else
        {
            price = DisplayPriceFactory.create(priceStruct);
        }
        return price;
    }

    private PriceStruct convertExercisePrice(Price price)
    {
        PriceStruct priceStruct;
        if (price.isNoPrice())
        {
            priceStruct = DisplayPriceFactory.getNoPrice().toStruct();
        }
        else if(price.isMarketPrice())
        {
            priceStruct = DisplayPriceFactory.getMarketPrice().toStruct();
        }
        else // if (price.isValuedPrice())
        {
            priceStruct = price.toStruct();
        }
        return priceStruct;
    }

    /**
     * Set the Product that this ProductModel represents.
     * @param product
     * @param fireEvent boolean - setModified and fire DATA_CHANGE_EVENT if true
     */
    private void setProduct(Product product, boolean fireEvent)
    {
        if ( product == null )
        {
            throw new IllegalArgumentException("Product can not be null");
        }
        else
        {
            updateFromProduct(product);

            if ( fireEvent )
            {
                setModified(true);
                firePropertyChange(DATA_CHANGE_EVENT, null, product);   // don't have reference to the old product
            }
        }
    }

    public void setClassKey(int key)
    {
        if ( classKey != key )
        {
            int oldValue = classKey;
            classKey = key;
            setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, key);
        }
    }

    public void setProductKey(int key)
    {
        if ( productKey != key )
        {
            int oldValue = productKey;
            productKey = key;
            setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, key);
        }
    }

    public void setProductType(short type)
    {
        if ( productType != type )
        {
            int oldValue = productType;
            productType = type;
            setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, type);
        }
    }

    public void setReportingClass(int key)
    {
        if ( reportingClassKey != key )
        {
            int oldValue = reportingClassKey;
            reportingClassKey = key;
            setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, key);
        }
    }

    public void setExercisePrice(PriceStruct priceStruct)
    {
        if ( priceStruct == null )
        {
            throw new IllegalArgumentException("PriceStruct can not be null");
        }
        else if ( !isEqualPrice(exercisePrice.toStruct(), priceStruct) )
        {
            PriceStruct oldValue = exercisePrice.toStruct();
            exercisePrice = DisplayPriceFactory.create(priceStruct);
            setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, priceStruct);
        }
    }

    public void setExpirationDate(DateStruct dateStruct)
    {
        if ( dateStruct == null )
        {
            throw new IllegalArgumentException("DateStruct can not be null");
        }
        else if ( !isEqualDate(expirationDate.getDateStruct(), dateStruct) )
        {
            DateStruct oldValue = expirationDate.getDateStruct();
            expirationDate = DateTimeFactory.getDate(dateStruct);
            setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, dateStruct);
        }
    }

    public void setOptionType(char type)
    {
        if ( optionType != type )
        {
            char oldValue = optionType;
            optionType = type;
            setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, type);
        }
    }

    public void setProductSymbol(String symbol)
    {
        if ( !productSymbol.equals(symbol) )
        {
            String oldValue = productSymbol;
            productSymbol = symbol;
            setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, symbol);
        }
    }

    public void setReportingClass(String reportingClass)
    {
        if ( !reportingClassName.equals(reportingClass) )
        {
            String oldValue = reportingClassName;
            reportingClassName = reportingClass;
            setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, reportingClass);
        }
    }

    public void setListingState(short state)
    {
        if ( state != ListingStates.ACTIVE &&
             state != ListingStates.INACTIVE &&
             state != ListingStates.OBSOLETE &&
             state != ListingStates.UNLISTED )
        {
            throw new IllegalArgumentException("Invalid Listing State = " + state);
        }
        else if ( listingState != state )
        {
            short oldValue = listingState;
            listingState = state;
            setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, state);
        }
    }
    public void setDescription(String desc)
    {
        if ( desc == null )
        {
            throw new IllegalArgumentException("Description can not be null");
        }
        else if ( !description.equals(desc) )
        {
            String oldValue = description;
            description = desc;
            setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, desc);
        }
    }
    public void setCompanyName(String name)
    {
        if ( name == null )
        {
            throw new IllegalArgumentException("Company Name can not be null");
        }
        else if ( !companyName.equals(name) )
        {
            String oldValue = companyName;
            companyName = name;
            setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, name);
        }
    }

    public void setExpirationType(ExpirationType type)
    {
        if(type == null)
        {
            throw new IllegalArgumentException("Expiration Type can not be null");
        }
        else if(!getExpirationType().equals(type))
        {
            // no need to firePropertyChange(DATA_CHANGE_EVENT, oldValue, type)
            // because setDescription(description) call below fires an event, and all events are the same which can't
            // distinguish between different old and new data types.
            expirationType = type;
            String description = getDescription();
            if(description.equals("")){
                description = "expr_class=" + type.toChar() + ";";
            }
            else if(description.contains("expr_class="))
            {
                description = description.replaceFirst("expr_class=.", "expr_class=" + type.toChar());
            }
            else{
                description += "expr_class=" + type.toChar();
            }

            setDescription(description);
        }
    }

    public void setLeapIndicator(boolean indicator)
    {
        if(getLeapIndicator() != indicator)
        {
            // no need to firePropertyChange(DATA_CHANGE_EVENT, oldValue, indicator)
            // because setDescription(description) call below fires an event, and all events are the same which can't
            // distinguish between different old and new data types.
            leapIndicator = indicator;
            String description = getDescription();
            if(description.equals(""))
            {
                if(leapIndicator){
                    description = "leap_ind=Y;" ;
                }
                else{
                    description = "leap_ind=N;";
                }
            }
            else if (description.contains("leap_ind="))
            {
                if(leapIndicator)
                {
                    description = description.replaceFirst("leap_ind=.", "leap_ind=Y");
                }
                else
                {
                    description = description.replaceFirst("leap_ind=.", "leap_ind=N");
                }
            }
            else{
                if (leapIndicator) {
                    description = "leap_ind=Y";
                } else {
                    description = "leap_ind=N";
                }
            }
            setDescription(description);
        }
    }

    public void setUnitMeasure(String uMeasure)
    {
        if ( uMeasure == null )
        {
            throw new IllegalArgumentException("UnitMeasure can not be null");
        }
        else if ( !unitMeasure.equals(uMeasure) )
        {
            String oldValue = unitMeasure;
            unitMeasure = uMeasure;
            setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, uMeasure);
        }
    }
    public void setStandardQuantity(double stdQuantity)
    {
        if ( standardQuantity != stdQuantity )
        {
            Double oldValue = standardQuantity;
            standardQuantity = stdQuantity;
            setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, stdQuantity);
        }
    }
    public void setMaturityDate(DateStruct maturity)
    {
        if ( maturity == null )
        {
            throw new IllegalArgumentException("Maturity Date can not be null");
        }
        else if ( maturityDate.getYear () != maturity.year  &&
                  maturityDate.getMonth() != maturity.month &&
                  maturityDate.getDay  () != maturity.day )
        {
            DateStruct oldValue = getMaturityDate();
            maturityDate = DateTimeFactory.getDate(maturity);
            setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, maturity);
        }
    }
    public void setActivationDate(DateStruct activation)
    {
        if ( activation == null )
        {
            throw new IllegalArgumentException("Activation Date can not be null");
        }
        else if ( !isEqualDate(getActivationDate(), activation) )
        {
            DateStruct oldValue = getActivationDate();
            activationDate = DateTimeFactory.getDate(activation);
            setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, activation);
        }
    }
    public void setInactivationDate(DateStruct inactivation)
    {
        if ( inactivation == null )
        {
            throw new IllegalArgumentException("inActivation Date can not be null");
        }
        else if ( !isEqualDate(getInactivationDate(), inactivation) )
        {
            DateStruct oldValue = getInactivationDate();
            inactivationDate = DateTimeFactory.getDate(inactivation);
            setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, inactivation);
        }
    }
    public void setCreatedTime(DateTimeStruct cTime)
    {
        if ( cTime == null )
        {
            throw new IllegalArgumentException("Created Time can not be null");
        }
        else if ( !isEqualDateTime(getCreatedTime(), cTime) )
        {
            DateTimeStruct oldValue = getCreatedTime();
            createdTime = DateTimeFactory.getDateTime(cTime);
            setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, cTime);
        }
    }
    public void setLastModifiedTime(DateTimeStruct modifiedTime)
    {
        if ( modifiedTime == null )
        {
            throw new IllegalArgumentException("LastModifiedTime can not be null");
        }
        else if ( !isEqualDateTime(getLastModifiedTime(), modifiedTime) )
        {
            DateTimeStruct oldValue = getLastModifiedTime();
            lastModifiedTime = DateTimeFactory.getDateTime(modifiedTime);
            setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, modifiedTime);
        }
    }
    public void setOpraMonthCode(char monthCode)
    {
        if ( opraMonthCode != monthCode )
        {
            char oldValue = opraMonthCode;
            opraMonthCode = monthCode;
            setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, monthCode);
        }
    }
    public void setOpraPriceCode(char priceCode)
    {
        if ( opraPriceCode != priceCode )
        {
            char oldValue = opraPriceCode;
            opraPriceCode = priceCode;
            setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, priceCode);
        }
    }

    public ProductCusip getCusip()
    {
        return this.cusip;
    }

    public void setCusip(ProductCusip cusip)
    {
        setCusip(cusip, true);
    }

    public void setCusip(ProductCusip theCusip, boolean fireEvents)
    {
        ProductCusip oldValue = getCusip();
        cusip = theCusip;
        if(fireEvents)
        {
            setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, theCusip);
        }
    }

    protected ExtensionsHelper getExtensionsHelper()
    {
        if(extensionsHelper == null)
        {
            extensionsHelper = new ExtensionsHelper();
        }
        return extensionsHelper;
    }

    public Price getClosingPrice()
    {
        return closingPrice;
    }

    public String getClosingSuffix()
    {
        return closingSuffix;
    }

    public String getExtensions()
    {
        return getExtensionsHelper().toString();
    }

    public boolean isRestrictedProduct()
    {
        return restrictedProductIndicator;
    }

    public void setClosingPrice(PriceStruct closingPrice)
    {
        setClosingPrice(closingPrice, true);
    }

    public void setClosingPrice(PriceStruct closingPrice, boolean fireEvents)
    {
        Price oldValue = getClosingPrice();
        if(closingPrice == null)
        {
            this.closingPrice = PriceFactory.getNoPrice();
        }
        else
        {
            this.closingPrice = PriceFactory.create(closingPrice);
        }
        if(fireEvents)
        {
            setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, this.closingPrice);
        }
    }

    public void setClosingSuffix(String closingSuffix)
    {
        setClosingSuffix(closingSuffix, true);
    }

    public void setClosingSuffix(String closingSuffix, boolean fireEvents)
    {
        String oldValue = getClosingSuffix();
        this.closingSuffix = closingSuffix;
        if(fireEvents)
        {
            setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, closingSuffix);
        }
    }

    public void setExtensions(String extensions)
    {
        setExtensions(extensions, true);
    }

    public void setExtensions(String extensions, boolean fireEvents)
    {
        String oldValue = getExtensions();
        try
        {
            getExtensionsHelper().setExtensions(extensions);
            if(fireEvents)
            {
                setModified(true);
                firePropertyChange(DATA_CHANGE_EVENT, oldValue, extensions);
            }
        }
        catch(ParseException e)
        {
            DefaultExceptionHandlerHome.find().process(e, "ParseException trying to update ExtensionsHelper from extensions string '"+extensions+"'");
        }
    }

    public void setRestrictedProduct(boolean restrictedProductIndicator)
    {
        setRestrictedProduct(restrictedProductIndicator, true);
    }

    public void setRestrictedProduct(boolean restrictedProductIndicator, boolean fireEvents)
    {
        boolean oldValue = isRestrictedProduct();
        this.restrictedProductIndicator = restrictedProductIndicator;
        if(fireEvents)
        {
            setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, restrictedProductIndicator);
        }
    }

    public int hashCode()
    {
        return hashCode;
    }

    //Implementing abstract methods form AbstractTransactionalBusinessModel
    public void saveChanges() throws UserException
    {
        if ( isModified() )
        {
            boolean isNewProduct = getProductKey() == com.cboe.idl.cmiConstants.ProductClass.DEFAULT_CLASS_KEY;
            ProductStruct newProductStruct;

            if( isNewProduct )
            {
                newProductStruct = this.getProductStruct();
                GUILoggerHome.find().debug("Creating New Product...", GUILoggerBusinessProperty.PRODUCT_DEFINITION, newProductStruct);

                newProductStruct = SystemAdminAPIFactory.find().addProduct( newProductStruct );
            }
            else // Update existing Product
            {
                GUILoggerHome.find().debug("Updating Product...", GUILoggerBusinessProperty.PRODUCT_DEFINITION, this);
                newProductStruct = this.getProductStruct( );

                newProductStruct = SystemAdminAPIFactory.find().updateProduct( newProductStruct );
            }

            GUILoggerHome.find().debug("Product Saved. New Struct: ", GUILoggerBusinessProperty.PRODUCT_DEFINITION, newProductStruct);
            setProduct(ProductFactoryHome.find().create(newProductStruct));
            firePropertyChange(SAVED_EVENT,null,this);
        }
    }

    public void saveChangesV4() throws UserException
    {
        if(isModified())
        {
            boolean isNewProduct = getProductKey() == ProductClass.DEFAULT_CLASS_KEY;

            String cusipValue = "";
            if(getCusip() != null)
            {
                cusipValue = getCusip().getCusipValue();
            }

            ProductStruct newProductStruct = this.getProductStruct();

            ProductStructV4 newProductStructV4 = new ProductStructV4();
            newProductStructV4.product = newProductStruct;
            newProductStructV4.cusip = cusipValue;
            newProductStructV4.closingPrice = getClosingPrice().toStruct();
            newProductStructV4.closingSuffix = getClosingSuffix();
            newProductStructV4.restrictedProductIndicator = isRestrictedProduct();
            newProductStructV4.extensions = getExtensionsHelper().toString();

            if(isNewProduct)
            {
                newProductStructV4 = SystemAdminAPIFactory.find().addProductV4(newProductStructV4);
            }
            else
            {
                newProductStructV4 = SystemAdminAPIFactory.find().updateProductV4(newProductStructV4);
            }

            GUILoggerHome.find().debug("ProductModelImpl.saveChangesV4(): Product Saved. New Struct: ",
                                       GUILoggerBusinessProperty.PRODUCT_DEFINITION,
                                       newProductStructV4);
            setProduct(ProductFactoryHome.find().create(newProductStructV4.product));
            setCusip(new ProductCusipImpl(newProductStructV4.cusip, new java.util.Date()), false);
            setClosingPrice(newProductStructV4.closingPrice, false);
            setClosingSuffix(newProductStructV4.closingSuffix, false);
            setRestrictedProduct(newProductStructV4.restrictedProductIndicator, false);
            firePropertyChange(SAVED_EVENT, null, this);
        }
    }

    public ValidationResult validateData()
    {
        ValidationResult result = validateReportingClass();
        if ( result.isValid() )
        {
            if(this.getProductType() == ProductTypes.OPTION)
                result = validateOPRACodes();
        }
        return result;
    }

    private ValidationResult validateReportingClass()
    {
        ValidationResult result = new ValidationResultImpl();
        if ( reportingClassName == null || reportingClassName.length() == 0)
        {
            result.setErrorCode(ValidationErrorCodes.PRODUCT_NO_REPORTING_CLASS);
            result.setErrorMessage("NO REPORTING CLASS SET FOR PRODUCT.");
        }
        else
        {
            result.setErrorCode(ValidationErrorCodes.NO_ERROR);
        }
        return result;
    }

    private ValidationResult validateOPRACodes()
    {
        ValidationResult result = new ValidationResultImpl();
        if(getOpraMonthCode() == ' ')
        {
            result.setErrorCode(ValidationErrorCodes.PRODUCT_INVALID_OPRA_MONTH_CODE);
            result.setErrorMessage("Invalid OPRA Month Code.");
        }
        else if(getOpraPriceCode() == ' ')
        {
            result.setErrorCode(ValidationErrorCodes.PRODUCT_INVALID_OPRA_PRICE_CODE);
            result.setErrorMessage("Invalid OPRA Price Code.");
        }
        else
        {
            result.setErrorCode(ValidationErrorCodes.NO_ERROR);
        }

        return result;
    }

    public void refreshData() throws UserException
    {
        // if refreshData() is called for a default Product (when productKey = 0) then the user has cancelled creation of a new Product
        if(getProductKey() != com.cboe.idl.cmiConstants.ProductClass.DEFAULT_CLASS_KEY)
        {
            Product newProduct = SystemAdminAPIFactory.find().getProductByKey(getProductKey());
            setProduct(newProduct);
            firePropertyChange(RELOADED_EVENT,null,this);
        }
        else
        {
            firePropertyChange(DELETED_EVENT, this, null);
        }
    }

    public void refreshDataV4() throws UserException
    {
        // if refreshData() is called for a default Product (when productKey = 0) then the user has cancelled creation of a new Product
        if(getProductKey() != ProductClass.DEFAULT_CLASS_KEY)
        {
            int[] keys = new int[1];
            keys[0] = getProductKey();
            ProductStructV4[] productStructs = SystemAdminAPIFactory.find().getProductsByKeyV4(keys);
            if(productStructs.length > 0)
            {
                Product newProduct = ProductFactoryHome.find().create(productStructs[0].product);
                setProduct(newProduct);
                setCusip(new ProductCusipImpl(productStructs[0].cusip, new java.util.Date()));
                setClosingPrice(productStructs[0].closingPrice);
                setClosingSuffix(productStructs[0].closingSuffix);
                setRestrictedProduct(productStructs[0].restrictedProductIndicator);
                setExtensions(productStructs[0].extensions);

                firePropertyChange(RELOADED_EVENT, null, this);
            }
            else
            {
                throw ExceptionBuilder.notFoundException("GUI-generated NotFoundException because ProductMaintenanceService.getProductsByKeyV4('" + getProductKey() +
                                                         "') returned a zero-length array; V4 Product data could not be loaded", 0);
            }
        }
        else
        {
            firePropertyChange(DELETED_EVENT, this, null);
        }
    }

    public void handleUserException(UserException e)
    {
        DefaultExceptionHandlerHome.find().process(e);
    }

    private boolean isEqualDate(DateStruct d1, DateStruct d2)
    {
        boolean isEqual = false;
        if ( d1.year == d2.year && d1.month == d2.month && d1.day == d2.day )
        {
            isEqual = true;
        }
        return isEqual;
    }
    private boolean isEqualTime(TimeStruct t1, TimeStruct t2)
    {
        boolean isEqual = false;
        if ( t1.hour == t2.hour && t1.minute == t2.minute && t1.second == t2.second && t1.fraction == t2.fraction)
        {
            isEqual = true;
        }
        return isEqual;
    }
    private boolean isEqualDateTime(DateTimeStruct dt1, DateTimeStruct dt2)
    {
        boolean isEqual = false;
        if ( isEqualDate(dt1.date, dt2.date) )
        {
            isEqual = isEqualTime(dt1.time, dt2.time);
        }
        return isEqual;
    }
    private boolean isEqualPrice(PriceStruct price1, PriceStruct price2)
    {
        boolean isEqual = false;
        if ( price1.type == price2.type && price1.whole == price2.whole && price1.fraction == price2.fraction)
        {
            isEqual = true;
        }
        return isEqual;
    }

    public ExpirationType getExpirationType(){
        return expirationType;
    }

    public boolean getLeapIndicator()
    {
        return leapIndicator;
    }
}
