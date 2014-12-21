//
// -----------------------------------------------------------------------------------
// Source file: ProductImpl.java
//
// PACKAGE: com.cboe.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.product;

import java.util.*;

import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.cmiConstants.PriceTypes;
import com.cboe.interfaces.domain.dateTime.Date;
import com.cboe.interfaces.domain.dateTime.DateTime;

import com.cboe.interfaces.presentation.common.formatters.ProductFormatStrategy;
import com.cboe.interfaces.presentation.product.ExpirationType;
import com.cboe.interfaces.presentation.product.Product;

import com.cboe.presentation.common.formatters.CommonFormatFactory;
import com.cboe.presentation.common.formatters.DisplayPriceFactory;
import com.cboe.presentation.common.dateTime.DateTimeFactory;
import com.cboe.presentation.util.StringCache;

/**
 * Product implementation for a ProductStruct from the API.
 */
class ProductImpl extends AbstractProduct
{
    private String productDisplayName = null;
    protected ExpirationType mExpirationType = ExpirationType.STANDARD;
    protected boolean mLeapIndicator = false;
    static private ProductFormatStrategy formatter = null;

    // ProductKeysStruct fields
    private int productKey;
    private int classKey;
    private int reportingClassKey;
    private short productType;

    // ProductNameStruct fields
    private String reportingClassName;
    private char optionType;
    private String productSymbol;

    // ProductStruct fields
    private short listingState;
    private String description;
    private String companyName;
    private String unitMeasure;
    private double standardQuantity;
    private Date maturityDate;
    private Date activationDate;
    private Date inactivationDate;
    private DateTime createdTime;
    private DateTime lastModifiedTime;
    private char opraMonthCode;
    private char opraPriceCode;

    /**
     * Constructor
     * @param productStruct to represent
     */
    protected ProductImpl(ProductStruct productStruct)
    {
        this();

        updateFromStruct(productStruct);
        parseDescription();
    }

    /**
     *  Default constructor.
     */
    protected ProductImpl()
    {
        super();
        if(ProductImpl.formatter == null)
        {
            ProductImpl.formatter = CommonFormatFactory.getProductFormatStrategy();
        }
    }

    protected void updateFromStruct(ProductStruct productStruct)
    {
        super.updateFromStruct(productStruct);

        productKey = productStruct.productKeys.productKey;
        classKey = productStruct.productKeys.classKey;
        reportingClassKey = productStruct.productKeys.reportingClass;
        productType = productStruct.productKeys.productType;

        optionType = productStruct.productName.optionType;

        reportingClassName = StringCache.get(productStruct.productName.reportingClass);
        productSymbol = StringCache.get(productStruct.productName.productSymbol);


        listingState = productStruct.listingState;

        description = StringCache.get(productStruct.description);
        companyName = StringCache.get(productStruct.companyName);
        unitMeasure = StringCache.get(productStruct.unitMeasure);

        standardQuantity = productStruct.standardQuantity;
        maturityDate = DateTimeFactory.getDate(productStruct.maturityDate);
        activationDate = DateTimeFactory.getDate(productStruct.activationDate);
        inactivationDate = DateTimeFactory.getDate(productStruct.inactivationDate);

        createdTime = DateTimeFactory.getDateTime(productStruct.createdTime);
        lastModifiedTime = DateTimeFactory.getDateTime(productStruct.lastModifiedTime);
        opraMonthCode = productStruct.opraMonthCode;
        opraPriceCode = productStruct.opraPriceCode;
    }

    public boolean isAllSelectedProduct()
    {
        return false;
    }

    public boolean isDefaultProduct()
    {
        return false;
    }

    public void updateProduct(Product newProduct)
    {
        updateFromStruct(newProduct.getProductStruct());
    }

    /**
     * Get the ProductStruct that this Product represents.
     * @return ProductStruct
     * @deprecated
     */
    public ProductStruct getProductStruct()
    {
        ProductStruct retVal = new ProductStruct();
        retVal.productKeys = getProductKeysStruct();
        retVal.productName = getProductNameStruct();
        retVal.listingState = getListingState();
        retVal.description = getDescription();
        retVal.companyName = getCompanyName();
        retVal.unitMeasure = getUnitMeasure();
        retVal.standardQuantity = getStandardQuantity();
        retVal.maturityDate = getMaturityDate();
        retVal.activationDate = getActivationDate();
        retVal.inactivationDate = getInactivationDate();
        retVal.createdTime = getCreatedTime();
        retVal.lastModifiedTime = getLastModifiedTime();
        retVal.opraMonthCode = getOpraMonthCode();
        retVal.opraPriceCode = getOpraPriceCode();

        return retVal;
    }

    /**
     * Get the product key for this Product.
     * @return product key from represented struct
     */
    public int getProductKey()
    {
        return productKey;
    }

    public int getClassKey()
    {
        return classKey;
    }

    public int getReportingClassKey()
    {
        return reportingClassKey;
    }

    public String getReportingClassName()
    {
        return reportingClassName;
    }

    public char getOptionType()
    {
        return optionType;
    }

    public String getProductSymbol()
    {
        return productSymbol;
    }

    /**
     * Get the ProductKeysStruct for this Product.
     * @return ProductKeysStruct from represented struct
     */
    public ProductKeysStruct getProductKeysStruct()
    {
        ProductKeysStruct retVal = new ProductKeysStruct();
        retVal.productKey = getProductKey();
        retVal.classKey = getClassKey();
        retVal.productType = getProductType();
        retVal.reportingClass = getReportingClassKey();
        return retVal;
    }

    /**
     * Get the ProductNameStruct for this Product.
     * @return ProductNameStruct from represented struct
     */
    public ProductNameStruct getProductNameStruct()
    {
        ProductNameStruct retVal = new ProductNameStruct();
        retVal.reportingClass = getReportingClassName();
        if(getOriginalStructExercisePriceType() == PriceTypes.NO_PRICE)
        {
            retVal.exercisePrice = DisplayPriceFactory.getNoPrice().toStruct();
        }
        else if(getOriginalStructExercisePriceType() == PriceTypes.MARKET)
        {
            retVal.exercisePrice = DisplayPriceFactory.getMarketPrice().toStruct();
        }
        else
        {
            retVal.exercisePrice = getExercisePrice().toStruct();
        }
        retVal.expirationDate = getExpirationDate().getDateStruct();
        retVal.optionType = getOptionType();
        retVal.productSymbol = getProductSymbol();

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
     * Clones this product by returning another instance that represents a
     * ProductStruct that was also cloned.
     */
    public Object clone() throws CloneNotSupportedException
    {
        ProductImpl dest;
        // dest = (ProductImpl)super.clone();
        dest = new ProductImpl(getProductStruct());
        dest.parseDescription();

        return dest;
    }
    /**
     * Get the product type of this product
     * @return short
     * @see com.cboe.idl.cmiConstants.ProductTypes
     */
    public short getProductType()
    {
        return productType;
    }

    /**
     * If <code>obj</code> is an instance of Product and has the same
     * product key true is returned, false otherwise.
     * @param obj to compare
     * @return true if equal, false if not.
     */
    public boolean equals(Object obj)
    {
        boolean isEqual = super.equals(obj);

        if(!isEqual)
        {
            if( obj instanceof Product )
            {
                isEqual = getProductKey() == (( Product ) obj).getProductKey();
            }
            else
            {
                isEqual = false;
            }
        }

        return isEqual;
    }

    public ExpirationType getExpirationType(){
        return mExpirationType;
    }

    public boolean getLeapIndicator()
    {
        return mLeapIndicator;
    }

    private void parseDescription(){
        String description = getDescription();
        char expirationChar;
        char leap_indicator;
        if(!description.equals("") && description.indexOf("expr") != -1){
            StringTokenizer tokenizer = new StringTokenizer(description, ";");
            String token = "";
            while(tokenizer.hasMoreTokens()){
                token = tokenizer.nextToken();
                if(token.contains("expr_class"))
                {
                    expirationChar = token.charAt(token.indexOf("=") + 1);
                    mExpirationType = ExpirationType.findExperationType(expirationChar);
                }
                else if(token.contains("leap_ind"))
                {
                    leap_indicator = token.charAt(token.indexOf("=") + 1);
                    if(leap_indicator == 'Y'){
                        mLeapIndicator = true;
                    }
                    else if(leap_indicator == 'N'){
                        mLeapIndicator = false;
                    }
                    else{
                        // recieved a malformed description from server
                    }
                }
                else{
                    // recieved a malformed description from server
                }
            }
        }
    }

    /**
     * Returns a String representation of this Product.
     */
    public String toString()
    {
        if(this.productDisplayName == null)
        {
           // this.productDisplayName = ProductImpl.formatter.format(this, formatter.FULL_PRODUCT_NAME);
           this.productDisplayName =ProductImpl.formatter.format(this, formatter.FULL_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_OPTION_TYPE);
        }
        return this.productDisplayName;
    }

}
