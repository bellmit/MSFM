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
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiUtil.PriceStruct;

import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.dateTime.Date;
import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.presentation.product.ExpirationType;

import com.cboe.presentation.common.businessModels.AbstractMutableBusinessModel;
import com.cboe.presentation.common.formatters.DisplayPriceFactory;
import com.cboe.presentation.common.dateTime.DateTimeFactory;

/**
 * Abstract implementation of Product.
 */
abstract class AbstractProduct extends AbstractMutableBusinessModel implements Product
{
    private Price exercisePrice = null;
    private Date expirationDate = null;
    private short structExercisePriceType = PriceTypes.NO_PRICE;

    /**
     *  Default constructor.
     */
    protected AbstractProduct()
    {
        super();
    }

    protected void updateFromStruct(ProductStruct productStruct)
    {
        PriceStruct price = productStruct.productName.exercisePrice;
        structExercisePriceType = price.type;
        if (price.type == PriceTypes.NO_PRICE || price.type == PriceTypes.MARKET)
        {
            this.exercisePrice = DisplayPriceFactory.create(0.0);
        }
        else
        {
            this.exercisePrice = DisplayPriceFactory.create(price);
        }

        expirationDate = DateTimeFactory.getDate(productStruct.productName.expirationDate);
    }

    /**
     * If the original struct's exercise price was a NO_PRICE or MARKET, the
     * exercisePrice wrapper will be a ValuedPrice with value 0.0.
     *
     * This will return the actual price type of the original ProductNameStruct,
     * to be used when creating a new ProductNameStruct that has the same type
     * as the original.
     */
    protected short getOriginalStructExercisePriceType()
    {
        return structExercisePriceType;
    }

    /**
     * Gets the exercise price of the product
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
