//
// -----------------------------------------------------------------------------------
// Source file: ProductFormatStrategy.java
//
// PACKAGE: com.cboe.interfaces.presentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.common.formatters;

import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.interfaces.presentation.product.ProductName;

/**
 * Defines a contract for a class that formats Products
 */
public interface ProductFormatStrategy extends FormatStrategy
{
    public static final String FULL_PRODUCT_NAME = "Full Product Name";
    public static final String PRODUCT_NAME_WO_TYPE = "Full Product Name w/o Type";
    public static final String PRODUCT_NAME_BRIEF_TYPE = "Full Product Name with brief Type";
    public static final String FULL_PRODUCT_NAME_AND_STATE = "Full Product Name and State";
    public static final String PRODUCT_STATE = "Product State";
    public static final String OPRA_PRODUCT_NAME = "OPRA Product Name";
    public static final String FULL_PRODUCT_NAME_WITH_SESSION_AND_TYPE = "Full Product Name with Session and Type";
    public static final String FULL_PRODUCT_NAME_WITH_KEY = "Full Product Name with Product Key";
    public static final String FULL_PRODUCT_NAME_WITH_PRODUCT_TYPE = "Full Product Name with Product Type";
    public static final String FULL_PRODUCT_WITH_CLASS_AND_REPORTING_CLASS = "Full Product Name with Class and Reporting Class Symbols";

    public static final String PRODUCT_NAME_AND_EXPIRATION_DATE_WITH_OPTION_TYPE ="Product Name and expiration date with Product Type";
    public static final String FULL_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_OPTION_TYPE ="Full Product Name  and expiration date with Product Type and Expiration Type";
    public static final String SHORT_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_OPTION_TYPE ="Short Product Name and expiration date with Product Type and Expiration Type";
    public static final String PRODUCT_TYPE_PRODUCT_NAME_AND_EXPIRATION_DATE_WITH_OPTION_TYPE ="Product Type : Product Name and expiration date with Product Type";
    public static final String PRODUCT_TYPE_FULL_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_OPTION_TYPE ="Product Type : Full Product Name  and expiration date with Product Type and Expiration Type";
    public static final String PRODUCT_TYPE_SHORT_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_OPTION_TYPE ="Product Type : Short Product Name and expiration date with Product Type and Expiration Type";
    public static final String PRODUCT_NAME_AND_EXP_DATE_WITH_OPTION_TYPE_STATE ="Product Name and expiration date with Product Type and state";
    public static final String FULL_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_OPTION_TYPE_STATE ="Full Product Name  and expiration date with Product Type and Expiration Type and state";
    public static final String SHORT_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_OPTION_TYPE_STATE ="Short Product Name and expiration date with Product Type and Expiration Type and state";
    public static final String PRODUCT_NAME_AND_EXPIRATION_DATE_WO_TYPE = "Product Name and expiration date without option type";
    public static final String FULL_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_TYPE_WO_TYPE = "Full Product Name  and expiration date Expiration Type without option type";
    public static final String SHORT_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_TYPE_WO_TYPE = "Short Product Name and expiration date Expiration Type without option type";
    public static final String SHORT_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_TYPE_WITH_TYPE = "Short Product Name and expiration date Expiration Type with option type";


    public static final String PRODUCT_NAME_AND_EXP_DATE_WITH_OPTION_TYPE_DISCRIPTION ="Class Symbol with month, day, year, strike price and put/call ";
    public static final String FULL_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_OPTION_TYPE_DISCRIPTION ="Class Symbol with month, day, year, strike price, put/call and expiration type";
    public static final String SHORT_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_OPTION_TYPE_DISCRIPTION ="Class Symbol with month, day, year, strike price, put/call and single character expiration type";
    public static final String PRODUCT_TYPE_PRODUCT_NAME_AND_EXP_DATE_WITH_OPTION_TYPE_DISCRIPTION ="Product Type and Class Symbol with month, day, year, strike price and put/call ";
    public static final String PRODUCT_TYPE_FULL_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_OPTION_TYPE_DISCRIPTION ="Product Type and Class Symbol with month, day, year, strike price, put/call and expiration type";
    public static final String PRODUCT_TYPE_SHORT_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_OPTION_TYPE_DISCRIPTION ="Product Type and Class Symbol with month, day, year, strike price, put/call and single character expiration type";
    public static final String PRODUCT_NAME_AND_EXP_DATE_WITH_OPTION_TYPE_STATE_DISCRIPTION ="Class Symbol with month, day, year, strike price and put/call and product state";
    public static final String FULL_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_OPTION_TYPE_STATE_DISCRIPTION ="Class Symbol with month, day, year, strike price, put/call and expiration type and product state";
    public static final String SHORT_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_OPTION_TYPE_STATE_DISCRIPTION ="Class Symbol with month, day, year, strike price, put/call and single character expiration type and product state";
    public static final String PRODUCT_NAME_AND_EXP_DATE_WO_TYPE_DISCRIPTION = "Class Symbol with month, day, year, strike price without option type";
    public static final String FULL_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_TYPE_WO_TYPE_DISCRIPTION = "Class Symbol with month, day, year, strike price and expiration type without option type";
    public static final String SHORT_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_TYPE_WO_TYPE_DISCRIPTION = "Class Symbol with month, day, year, strike price and single character expiration type without option type";
    public static final String SHORT_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_TYPE_WITH_TYPE_DISCRIPTION = "Class Symbol with month, day, year, strike price and single character expiration type with option type";
    public static final String PRODUCT_STRATEGY_TYPE = "Product strategy type";
    
    public static final String FULL_PRODUCT_NAME_DESCRIPTION = "Reporting Class Symbol with month, strike, put/call added.";
    public static final String PRODUCT_NAME_WO_TYPE_DESCRIPTION = "Reporting Class Symbol with month and strike added.";
    public static final String PRODUCT_NAME_BRIEF_TYPE_DESCRIPTION = "Reporting Class Symbol with month, strike and C/P added.";
    public static final String FULL_PRODUCT_NAME_AND_STATE_DESCRIPTION = "Reporting Class Symbol with month, strike, put/call, and state.";
    public static final String PRODUCT_STATE_DESCRIPTION = "Product State.";
    public static final String OPRA_PRODUCT_NAME_DESCRIPTION = "Reporting Class Symbol with the OPRA codes added.";
    public static final String FULL_PRODUCT_NAME_WITH_SESSION_AND_TYPE_DESCRIPTION = "Reporting Class Symbol with month, strike, put/call preceeded by Session and Type";
    public static final String FULL_PRODUCT_NAME_WITH_KEY_DESCRIPTION = "Reporting Class Symbol with month, strike, put/call, key added.";
    public static final String FULL_PRODUCT_NAME_WITH_PRODUCT_TYPE_DESCRIPTION = "Reporting Class Symbol with month, strike, put/call preceeded by Product Type";
    public static final String FULL_PRODUCT_WITH_CLASS_AND_REPORTING_CLASS_DESCRIPTION = "Product Class and Reporting Class Symbols with month, strike, put/call added.";
    public static final String PRODUCT_STRATEGY_TYPE_DESCRIPTION = "Reporting product strategy type only.";
    
    
    /**
     * Defines a method for formatting Product
     * @param product to format
     * @return formatted string 
     */
    public String format(Product product);

    /**
     * Defines a method for formatting Product
     * @param product to format
     * @param useCache - boolean flag, if false it will force rebild of the formatted string even if it is already cached
     * @return formatted string
     */
    public String format(Product product, boolean useCache);

    /**
     * Defines a method for formatting Product
     * @param product to format
     * @param style - format style to use
     * @return formatted string
     */
    public String format(Product product, String style);

    /**
     * Defines a method for formatting Product
     * @param product to format
     * @param style - format style to use
     * @param useCache - boolean flag, if false it will force rebild of the formatted string even if it is already cached
     * @return formatted string
     */
    public String format(Product product, String style, boolean useCache);

    /**
     * Defines a method for formatting SessionProduct
     * @param sessionProduct to format
     * @return formatted string
     */
    public String format(SessionProduct sessionProduct);

    /**
     * Defines a method for formatting SessionProduct
     * @param sessionProduct to format
     * @param useCache - boolean flag, if false it will force rebild of the formatted string even if it is already cached
     * @return formatted string
     */
    public String format(SessionProduct sessionProduct, boolean useCache);

    /**
     * Defines a method for formatting SessionProduct
     * @param sessionProduct to format
     * @param style - format style to use
     * @return formatted string
     */
    public String format(SessionProduct sessionProduct, String style);

    /**
     * Defines a method for formatting SessionProduct
     * @param sessionProduct to format
     * @param style - format style to use
     * @param useCache - boolean flag, if false it will force rebild of the formatted string even if it is already cached
     * @return formatted string
     */
    public String format(SessionProduct sessionProduct, String style, boolean useCache);

    /**
     * Defines a method for formatting a ProductName
     * @param productName product name to format
     * @return formatted String
     */
    public String format(ProductName productName);
}
