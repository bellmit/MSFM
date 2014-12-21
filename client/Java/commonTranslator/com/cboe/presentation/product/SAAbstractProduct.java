//
// -----------------------------------------------------------------------------------
// Source file: AbstractProduct.java
//
// PACKAGE: com.cboe.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.product;

import com.cboe.idl.cmiConstants.PriceTypes;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.cmiUtil.PriceStruct;

import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.dateTime.Date;
import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.presentation.product.ExpirationType;

import com.cboe.presentation.common.businessModels.AbstractMutableBusinessModel;
import com.cboe.presentation.common.dateTime.DateImpl;
import com.cboe.presentation.common.formatters.DisplayPriceFactory;


/**
 * Abstract implementation of Product.
 */
abstract class SAAbstractProduct extends AbstractMutableBusinessModel implements Product
{
    private Price exercisePrice = null;
    protected Date expirationDate = null;

    /**
     *  Default constructor.
     */
    protected SAAbstractProduct()
    {
        super();
    }

    /**
     * Get the ProductStruct that this Product represents.
     * @deprecated
     */
    public abstract ProductStruct getProductStruct();

    /**
     * Get the product key for this Product.
     */
    public abstract int getProductKey();

    /**
     * Get the ProductKeysStruct for this Product.
     */
    public abstract ProductKeysStruct getProductKeysStruct();

    /**
     * Get the ProductNameStruct for this Product.
     */
    public abstract ProductNameStruct getProductNameStruct();

    /**
     * Get the listing state for this Product.
     */
    public abstract short getListingState();

    /**
     * Get the description for this Product.
     */
    public abstract String getDescription();

    /**
     * Get the company name for this Product.
     */
    public abstract String getCompanyName();

    /**
     * Get the unit of measure for this Product.
     */
    public abstract String getUnitMeasure();

    /**
     * Get the standard qty for this Product.
     */
    public abstract double getStandardQuantity();

    /**
     * Get the maturity date for this Product.
     */
    public abstract DateStruct getMaturityDate();

    /**
     * Get the activation date for this Product.
     */
    public abstract DateStruct getActivationDate();

    /**
     * Get the inactivation date for this Product.
     */
    public abstract DateStruct getInactivationDate();

    /**
     * Get the created time for this Product.
     */
    public abstract DateTimeStruct getCreatedTime();

    /**
     * Get the last modified time for this Product.
     */
    public abstract DateTimeStruct getLastModifiedTime();

    /**
     * Get the opra month code for this Product.
     */
    public abstract char getOpraMonthCode();

    /**
     * Get the opra price code for this Product.
     */
    public abstract char getOpraPriceCode();

    /**
     * Gets the exercise price of the product
     */
    public Price getExercisePrice()
    {
        if(exercisePrice == null && getProductNameStruct() != null)
        {
            PriceStruct price = this.getProductNameStruct().exercisePrice;
            if ( price.type == PriceTypes.NO_PRICE || price.type == PriceTypes.MARKET )
            {
                this.exercisePrice = DisplayPriceFactory.create(0.0);
            }
            else
            {
                this.exercisePrice = DisplayPriceFactory.create(price);
            }
        }
        return exercisePrice;
    }

    /**
     * Gets the expiration date of the product
     */
    public Date getExpirationDate()
    {
        if(expirationDate == null && getProductNameStruct() != null)
        {
            expirationDate = new DateImpl(getProductNameStruct().expirationDate);
        }
        return expirationDate;
    }

    public ExpirationType getExpirationType(){
        return ExpirationType.STANDARD;
    }

    public boolean getLeapIndicator()
    {
        return false;
    }

    /**
     * Returns a hash code for this Product
     */
    public int hashCode()
    {
        return getProductKey();
    }

}