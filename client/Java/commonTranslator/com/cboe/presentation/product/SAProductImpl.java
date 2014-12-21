//
// -----------------------------------------------------------------------------------
// Source file: SAProductImpl.java
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
import com.cboe.interfaces.presentation.common.formatters.ProductFormatStrategy;
import com.cboe.interfaces.presentation.product.ExpirationType;
import com.cboe.interfaces.presentation.product.Product;
import com.cboe.presentation.common.formatters.CommonFormatFactory;
import com.cboe.domain.util.ClientProductStructBuilder;

/**
 * Product implementation for a ProductStruct from the API.
 */
class SAProductImpl extends SAAbstractProduct
{
    private String productDisplayName = null;
    protected ProductStruct productStruct = null;
    protected ExpirationType mExpirationType = ExpirationType.STANDARD;
    protected boolean mLeapIndicator = false;
    static private ProductFormatStrategy formatter = null;


    /**
     * Constructor
     * @param productStruct to represent
     */
    protected SAProductImpl(ProductStruct productStruct)
    {
        this();
        this.productStruct = productStruct;

        parseDescription();
    }

    /**
     *  Default constructor.
     */
    protected SAProductImpl()
    {
        super();
        if(SAProductImpl.formatter == null)
        {
            SAProductImpl.formatter = CommonFormatFactory.getProductFormatStrategy();
        }
    }

    public boolean isAllSelectedProduct()
    {
        return false;
    }

    public boolean isDefaultProduct()
    {
        return false;
    }

    /**
     * Get the ProductStruct that this Product represents.
     * @return ProductStruct
     * @deprecated
     */
    public ProductStruct getProductStruct()
    {
        return productStruct;
    }

    /**
     * Get the product key for this Product.
     * @return product key from represented struct
     */
    public int getProductKey()
    {
        return getProductKeysStruct().productKey;
    }

    /**
     * Get the ProductKeysStruct for this Product.
     * @return ProductKeysStruct from represented struct
     */
    public ProductKeysStruct getProductKeysStruct()
    {
        return getProductStruct().productKeys;
    }

    /**
     * Get the ProductNameStruct for this Product.
     * @return ProductNameStruct from represented struct
     */
    public ProductNameStruct getProductNameStruct()
    {
        return getProductStruct().productName;
    }

    /**
     * Get the listing state for this Product.
     * @return listing state from represented struct
     */
    public short getListingState()
    {
        return getProductStruct().listingState;
    }

    /**
     * Get the description for this Product.
     * @return description from represented struct
     */
    public String getDescription()
    {
        return getProductStruct().description;
    }

    /**
     * Get the company name for this Product.
     * @return company name from represented struct
     */
    public String getCompanyName()
    {
        return getProductStruct().companyName;
    }

    /**
     * Get the unit of measure for this Product.
     * @return unit of measure from represented struct
     */
    public String getUnitMeasure()
    {
        return getProductStruct().unitMeasure;
    }

    /**
     * Get the standard qty for this Product.
     * @return standard qty from represented struct
     */
    public double getStandardQuantity()
    {
        return getProductStruct().standardQuantity;
    }

    /**
     * Get the maturity date for this Product.
     * @return maturity date from represented struct
     */
    public DateStruct getMaturityDate()
    {
        return getProductStruct().maturityDate;
    }

    /**
     * Get the activation date for this Product.
     * @return activation date from represented struct
     */
    public DateStruct getActivationDate()
    {
        return getProductStruct().activationDate;
    }

    /**
     * Get the inactivation date for this Product.
     * @return inactivation date from represented struct
     */
    public DateStruct getInactivationDate()
    {
        return getProductStruct().inactivationDate;
    }

    /**
     * Get the created time for this Product.
     * @return created time from represented struct
     */
    public DateTimeStruct getCreatedTime()
    {
        return getProductStruct().createdTime;
    }

    /**
     * Get the last modified time for this Product.
     * @return last modified time from represented struct
     */
    public DateTimeStruct getLastModifiedTime()
    {
        return getProductStruct().lastModifiedTime;
    }

    /**
     * Get the opra month code for this Product.
     * @return opra month code from represented struct
     */
    public char getOpraMonthCode()
    {
        return getProductStruct().opraMonthCode;
    }

    /**
     * Get the opra price code for this Product.
     * @return opra price code from represented struct
     */
    public char getOpraPriceCode()
    {
        return getProductStruct().opraPriceCode;
    }

    /**
     * Clones this product by returning another instance that represents a
     * ProductStruct that was also cloned.
     */
    public Object clone() throws CloneNotSupportedException
    {
        SAProductImpl dest;
        // dest = (ProductImpl)super.clone();
        dest = new SAProductImpl();
        if (getProductStruct() != null)
        {
            dest.productStruct = ClientProductStructBuilder.cloneProduct(getProductStruct());
            dest.parseDescription();
        }

        return dest;
    }
    /**
     * Get the product type of this product
     * @return short
     * @see com.cboe.idl.cmiConstants.ProductTypes
     */
    public short getProductType()
    {
        return getProductStruct().productKeys.productType;
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
           this.productDisplayName =SAProductImpl.formatter.format(this, formatter.FULL_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_OPTION_TYPE);
        }
        return this.productDisplayName;
    }
}
