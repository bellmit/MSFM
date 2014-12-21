//
// -----------------------------------------------------------------------------------
// Source file: ProductFormatter.java
//
// PACKAGE: com.cboe.presentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.formatters;

import java.util.ArrayList;

import com.cboe.idl.cmiConstants.ListingStates;
import com.cboe.idl.cmiConstants.OptionTypes;
import com.cboe.idl.cmiProduct.ProductNameStruct;

import com.cboe.interfaces.presentation.common.formatters.DateFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.FormatterCache;
import com.cboe.interfaces.presentation.common.formatters.ListingStateFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.ProductClassFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.ProductFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.TradingSessionFormatStrategy;
import com.cboe.interfaces.presentation.product.*;

import com.cboe.presentation.api.APIHome;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.product.SessionProductFactory;
import com.cboe.presentation.product.ProductFactoryHome;

/**
 * Implements the ProductFormatStrategy
 */
class ProductFormatter extends Formatter implements ProductFormatStrategy
{
    private static final String DEFAULT_TEXT = "Default";
    private static final String ALL_PRODUCTS_SELECTED_TEXT = "<All Products>";
    private static final DateFormatStrategy dateFormatter = CommonFormatFactory.getDateFormatStrategy();
    private static final ListingStateFormatStrategy listingStateFormatter = CommonFormatFactory.getListingStateFormatStrategy();

    private FormatterCache cache;

    /**
     * Constructor, defines styles and sets initial default style
     */
    public ProductFormatter()
    {
        super();
        addStyle(FULL_PRODUCT_NAME, FULL_PRODUCT_NAME_DESCRIPTION);
        addStyle(FULL_PRODUCT_NAME_AND_STATE, FULL_PRODUCT_NAME_AND_STATE_DESCRIPTION);
        addStyle(PRODUCT_STATE, PRODUCT_STATE_DESCRIPTION);
        addStyle(OPRA_PRODUCT_NAME, OPRA_PRODUCT_NAME_DESCRIPTION);
        addStyle(PRODUCT_NAME_WO_TYPE, PRODUCT_NAME_WO_TYPE_DESCRIPTION);
        addStyle(FULL_PRODUCT_NAME_WITH_SESSION_AND_TYPE, FULL_PRODUCT_NAME_WITH_SESSION_AND_TYPE_DESCRIPTION);
        addStyle(FULL_PRODUCT_NAME_WITH_KEY, FULL_PRODUCT_NAME_WITH_KEY_DESCRIPTION);
        addStyle(FULL_PRODUCT_NAME_WITH_PRODUCT_TYPE, FULL_PRODUCT_NAME_WITH_PRODUCT_TYPE_DESCRIPTION);
        addStyle(PRODUCT_NAME_BRIEF_TYPE, PRODUCT_NAME_BRIEF_TYPE_DESCRIPTION);
        addStyle(FULL_PRODUCT_WITH_CLASS_AND_REPORTING_CLASS,FULL_PRODUCT_WITH_CLASS_AND_REPORTING_CLASS_DESCRIPTION);
        addStyle(PRODUCT_NAME_AND_EXPIRATION_DATE_WITH_OPTION_TYPE,PRODUCT_NAME_AND_EXP_DATE_WITH_OPTION_TYPE_DISCRIPTION);
        addStyle(FULL_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_OPTION_TYPE,FULL_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_OPTION_TYPE_DISCRIPTION);
        addStyle(SHORT_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_OPTION_TYPE,SHORT_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_OPTION_TYPE_DISCRIPTION);
        addStyle(PRODUCT_NAME_AND_EXP_DATE_WITH_OPTION_TYPE_STATE,PRODUCT_NAME_AND_EXP_DATE_WITH_OPTION_TYPE_STATE_DISCRIPTION);
        addStyle(FULL_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_OPTION_TYPE_STATE,FULL_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_OPTION_TYPE_STATE_DISCRIPTION);
        addStyle(SHORT_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_OPTION_TYPE_STATE,SHORT_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_OPTION_TYPE_STATE_DISCRIPTION);
        addStyle(PRODUCT_TYPE_PRODUCT_NAME_AND_EXPIRATION_DATE_WITH_OPTION_TYPE,PRODUCT_TYPE_PRODUCT_NAME_AND_EXP_DATE_WITH_OPTION_TYPE_DISCRIPTION);
        addStyle(PRODUCT_TYPE_FULL_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_OPTION_TYPE,PRODUCT_TYPE_FULL_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_OPTION_TYPE_DISCRIPTION);
        addStyle(PRODUCT_TYPE_SHORT_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_OPTION_TYPE,PRODUCT_TYPE_SHORT_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_OPTION_TYPE_DISCRIPTION);

        addStyle(PRODUCT_NAME_AND_EXPIRATION_DATE_WO_TYPE, PRODUCT_NAME_AND_EXP_DATE_WO_TYPE_DISCRIPTION);
        addStyle(FULL_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_TYPE_WO_TYPE, FULL_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_TYPE_WO_TYPE_DISCRIPTION);
        addStyle(SHORT_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_TYPE_WO_TYPE, SHORT_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_TYPE_WO_TYPE_DISCRIPTION);
        addStyle(SHORT_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_TYPE_WITH_TYPE, SHORT_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_TYPE_WITH_TYPE_DISCRIPTION);

        addStyle(PRODUCT_STRATEGY_TYPE, PRODUCT_STRATEGY_TYPE_DESCRIPTION);

        setDefaultStyle(FULL_PRODUCT_NAME);

        cache = FormatterCacheFactory.create(2000, this.getFormatStyles().size());
    }

    public ProductFormatter(boolean cacheEnabled)
    {
        this();
        cache.setCacheEnabled(cacheEnabled);
    }

    /**
     * Formats a Product
     * @param product to format
     */
    public String format(Product product)
    {
        return format(product, getDefaultStyle());
    }

    /**
     * Method for formatting Product
     * @param product to format
     * @param useCache - boolean flag, if false it will force rebild of the formatted string even if it is already cached
     * @return formatted string
     */
    public String format(Product product, boolean useCache)
    {
        return format(product, getDefaultStyle(), useCache);
    }

    /**
     * Formats a Product
     * @param product to format
     */
    public String format(Product product, String style)
    {
        return format(product, style, true);
    }

    /**
     * Formats a Product
     * @param product to format
     */
    public String format(Product product, String style, boolean useCache)
    {
        String productText = null;
        if(useCache)
        {
            productText = cache.get(product, style);
        }

        if (productText == null)
        {
            if(ProductFactoryHome.find().createAllSelected().equals(product))
            {
                productText = ALL_PRODUCTS_SELECTED_TEXT;
            }
            else if(ProductFactoryHome.find().createDefault().equals(product))
            {
                productText = DEFAULT_TEXT;
            }
            else
            {
                validateStyle(style);
                if(style.equals(FULL_PRODUCT_NAME))
                {
                    productText = formatFullProductNameWithExpDateAndExperationType(product, true, true);
                }
                else if (style.equals(PRODUCT_STRATEGY_TYPE)){
                	productText = formatStrategyType(product, PRODUCT_STRATEGY_TYPE);
                }
                else if(style.equals(FULL_PRODUCT_NAME_AND_STATE))
                {
                    productText = formatFullProductNameWithExpDateAndExperationType(product, true, true);
                }
                else if(style.equals(PRODUCT_NAME_WO_TYPE))
                {
                    productText = formatFullProductNameWithExpDateAndExperationType(product, false, true);
                }
                else if(style.equals(PRODUCT_NAME_BRIEF_TYPE))
                {
                    productText = formatProductNameBriefType(product);
                }
                else if(style.equals(OPRA_PRODUCT_NAME) )
                {
                    productText = formatOPRAProductName(product);
                }
                else if(style.equals(FULL_PRODUCT_NAME_WITH_PRODUCT_TYPE))
                {
                    productText = formatFullProductNameWithExpDateAndExperationType(product, true, true);

                }
                else if(style.equals(FULL_PRODUCT_NAME_WITH_SESSION_AND_TYPE))
                {   // this style requires a SessionProduct, so format as default
                    // the other part of the formatting will be taken care by the method
                    // with the SessionProduct signature
                    productText = formatFullProductNameWithExpDateAndExperationType(product, true, true);
                }
                else if(style.equals(FULL_PRODUCT_NAME_WITH_KEY))
                {
                    StringBuffer sb = new StringBuffer(60);
                    sb.append(formatFullProductNameWithExpDateAndExperationType(product, true, true));
                    sb.append("  Key: ");
                    sb.append(String.valueOf(product.getProductKey()));
                    productText = sb.toString();
                }
                else if(style.equals(FULL_PRODUCT_WITH_CLASS_AND_REPORTING_CLASS))
                {
                    productText = formatProductNameWithReportingClass(product);
                }

                // The next three else cases are for formats
//                Full Expiration Type format - FULL_PRODUCT_NAME_AND_EXPIRATION_DATE_WITH_EXPIRATION_PRODUCT_TYPE:
//                <Class ><Month > - < Day > - < Year ><Strike price><Put / Call > ( < ExpirationType >)
//                Short Expiration Type format - SHORT_PRODUCT_NAME_AND_EXPIRATION_DATE_WITH_EXPIRATION_PRODUCT_TYPE:
//                <Class ><Month > - < Day > - < Year ><Strike price><Put / Call > ( < ExpirationType–single character>)
//                Standard format - PRODUCT_NAME_AND_EXPIRATION_DATE_WITH_PRODUCT_TYPE:
//                <Class ><Month > - < Day > - < Year ><Strike price><Put / Call >

                else if(style.equals(PRODUCT_NAME_AND_EXPIRATION_DATE_WITH_OPTION_TYPE))
                {
                    productText = formatProductNameWithExpDateAndProductType(product,true);
                }
                else if(style.equals(FULL_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_OPTION_TYPE))
                {
                    if(product.getProductType() != ProductTypes.STRATEGY){
                        productText = formatFullProductNameWithExpDateAndExperationType(product, true ,true);
                    }
                    else{
                        productText = formatFullProductName(product,true);
                    }
                }
                else if(style.equals(SHORT_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_OPTION_TYPE))
                {
                    productText = formatFullProductNameWithExpDateAndExperationType(product, true ,false);
                }


                // The next three else cases are for formats
//                Full Expiration Type format - FULL_PRODUCT_NAME_AND_EXPIRATION_DATE_WITH_EXPIRATION_PRODUCT_TYPE:
//                <Class ><Month > - < Day > - < Year ><Strike price><Put / Call > ( < ExpirationType >) ( < State >)
//                Short Expiration Type format - SHORT_PRODUCT_NAME_AND_EXPIRATION_DATE_WITH_EXPIRATION_PRODUCT_TYPE:
//                <Class ><Month > - < Day > - < Year ><Strike price><Put / Call > ( < ExpirationType–single character>) ( < State >)
//                Standard format - PRODUCT_NAME_AND_EXPIRATION_DATE_WITH_PRODUCT_TYPE:
//                <Class ><Month > - < Day > - < Year ><Strike price><Put / Call > ( < State >)

                else if(style.equals(PRODUCT_NAME_AND_EXP_DATE_WITH_OPTION_TYPE_STATE))
                {
                    productText = formatProductNameWithExpDateAndProductType(product, true);
                }
                else if(style.equals(FULL_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_OPTION_TYPE_STATE))
                {
                    productText = formatFullProductNameWithExpDateAndExperationTypeAndState(product, true);
                }
                else if(style.equals(SHORT_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_OPTION_TYPE_STATE))
                {
                    productText = formatFullProductNameWithExpDateAndExperationTypeAndState(product, false);
                }

                // The next three else cases are for formats
//                Full Expiration Type format - FULL_PRODUCT_NAME_AND_EXPIRATION_DATE_WITH_EXPIRATION_TYPE:
//                Product Type [Option|Future|Index]: <Class ><Month > - < Day > - < Year ><Strike price><Put / Call > ( < ExpirationType >)
//                Short Expiration Type format - SHORT_PRODUCT_NAME_AND_EXPIRATION_DATE_WITH_EXPIRATION_TYPE:
//                Product Type [Option|Future|Index]: <Class ><Month > - < Day > - < Year ><Strike price><Put / Call > ( < ExpirationType–single character>)
//                Standard format - PRODUCT_NAME_AND_EXPIRATION_DATE_WITH_PRODUCT_TYPE:
//                Product Type [Option|Future|Index]: <Class ><Month > - < Day > - < Year ><Strike price><Put / Call >

                else if(style.equals(PRODUCT_TYPE_PRODUCT_NAME_AND_EXPIRATION_DATE_WITH_OPTION_TYPE))
                {
                    productText = formatProductNameWithExpDateAndExpTypeAndProductType(product, true);
                }
                else if(style.equals(PRODUCT_TYPE_FULL_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_OPTION_TYPE))
                {
                    productText = formatFullProductNameWithExpDateAndExpTypeAndProductType(product,true,true);
                }
                else if(style.equals(PRODUCT_TYPE_SHORT_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_OPTION_TYPE))
                {
                    productText = formatFullProductNameWithExpDateAndExpTypeAndProductType(product,true,false);
                }


                else if (style.equals(PRODUCT_NAME_AND_EXPIRATION_DATE_WO_TYPE)) {
                    productText = formatProductNameWithExpDateAndProductType(product, false);
                }

                else if (style.equals(FULL_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_TYPE_WO_TYPE)) {
                    productText = formatFullProductNameWithExpDateAndExperationType(product, false,true);
                }

                else if (style.equals(SHORT_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_TYPE_WO_TYPE)) {
                    productText = formatFullProductNameWithExpDateAndExperationType(product, false,false);
                }

                else if (style.equals(SHORT_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_TYPE_WITH_TYPE)) {
                    productText = formatFullProductNameWithExpDateAndExperationType(product, true,false);
                }

            }
            cache.put(product, style, productText);
        }
        return productText;
    }

    private String formatProductNameWithExpDateAndExpTypeAndProductType(Product product, boolean includeProductType)
    {
        StringBuffer sb = new StringBuffer();
        if(includeProductType)
        {
            sb.append(ProductTypes.toString(product.getProductType()));
            sb.append(" : ");
        }
        sb.append(formatProductNameWithExpDateAndProductType(product, true));
        return sb.toString();
    }

    private String formatFullProductNameWithExpDateAndExpTypeAndProductType(Product product, boolean includeProductType, boolean fullFormat)
    {
        StringBuffer sb = new StringBuffer();
        if(includeProductType)
        {
            sb.append(ProductTypes.toString(product.getProductType()));
            sb.append(" : ");
        }
        sb.append(formatFullProductNameWithExpDateAndExperationType(product, true, fullFormat));
        return sb.toString();
    }

    /**
     * Formats a Product using FULL_PRODUCT_NAME style
     * @param product to format
     * @param includeOptionType True if option type is to be included, false to exclude it
     */
    private String formatFullProductNameWithExpDateAndExperationTypeAndState(Product product,boolean fullFormat)
    {
        StringBuffer seriesName = new StringBuffer(50);
        ProductNameStruct productNameStruct = product.getProductNameStruct();

        if(productNameStruct != null)
        {
            // the product is not a series - name is in the productSymbol
            if(!productNameStruct.productSymbol.equals(""))
            {
                seriesName.append(productNameStruct.productSymbol);
            }
            else if(productNameStruct.reportingClass.length() > 0 ||
                    product.getProductKeysStruct().productKey != 0)
            {
                seriesName.append(formatFullProductNameWithExpDateAndExperationType(product,true,fullFormat));
            }
            else  // no product name and product key is zero
            {
                return "";
            }

            short listingState = product.getListingState();
            if(listingState != ListingStates.ACTIVE)
            {
                seriesName.append('(').append(listingStateFormatter.format(listingState))
                        .append(')');
            }
        }
        return seriesName.toString();
    }

    private String formatFullProductNameWithExpDateAndExperationType(Product product, boolean displayOptionType ,boolean fullFormat){
        StringBuffer seriesName = new StringBuffer();
        if(product.getProductType() != ProductTypes.STRATEGY)
        {
            seriesName.append(formatProductNameWithExpDateAndProductType(product, displayOptionType));
        }
        else
        {
            seriesName.append(formatFullProductName(product,displayOptionType));
        }
        short productType = product.getProductKeysStruct().productType;

        
        if(productType == ProductTypes.OPTION)
        {
            
            seriesName.append(' ');

            if (product.getLeapIndicator()) {
                if(fullFormat){
                    seriesName.append(" (Leap)");
                }
                else{
                    seriesName.append(" (L)");
                }
            }
            else if(product.getExpirationType() != null && product.getExpirationType() != ExpirationType.STANDARD){
                seriesName.append('(');
                if(fullFormat){
                    seriesName.append(product.getExpirationType().toString());
                }
                else{
                    seriesName.append(product.getExpirationType().toChar());
                }
                seriesName.append(')');
            }
        }
        return seriesName.toString();
    }

    private String formatProductNameWithExpDateAndProductType(Product product , boolean displayOptionType)
    {
         ProductNameStruct productNameStruct = product.getProductNameStruct();
         short productType = product.getProductKeysStruct().productType;
         StringBuffer seriesName = new StringBuffer();

         if(productNameStruct != null)
         {
             String className = getReportingClassSymbol(product);
             seriesName.append(className);

             ArrayList <Short> noDatesList = new ArrayList<Short>();
             noDatesList.add(ProductTypes.EQUITY);
             noDatesList.add(ProductTypes.STRATEGY);
             noDatesList.add(ProductTypes.INDEX);
             noDatesList.add(ProductTypes.COMMODITY);
             
             // Equities do not have strike date
             if(!noDatesList.contains(productType))
             {
                    seriesName.append(' ');
                    seriesName.append(dateFormatter.format(product.getExpirationDate(),
                                                           DateFormatStrategy.DATE_FORMAT_MONTH_DATE_YEAR_STYLE));
             }
             
             ArrayList <Short> noPriceList = new ArrayList<Short>();
             noPriceList.add(ProductTypes.EQUITY);
             noPriceList.add(ProductTypes.STRATEGY);
             noPriceList.add(ProductTypes.INDEX);
             noPriceList.add(ProductTypes.FUTURE);
             noPriceList.add(ProductTypes.COMMODITY);
             if(!noPriceList.contains(productType))
             {
                seriesName.append(' ');
                seriesName.append(DisplayPriceFactory.create(productNameStruct.exercisePrice).toString());
                seriesName.append(' ');

                 if(displayOptionType){
                    if(productNameStruct.optionType == OptionTypes.CALL)
                    {
                      seriesName.append("CALL");
                    }
                    else if(productNameStruct.optionType == OptionTypes.PUT)
                    {
                      seriesName.append("PUT");
                    }
                 }
             }
        }
        return seriesName.toString();
    }



    private String formatProductNameWithReportingClass(Product product)
    {
        StringBuffer sb = new StringBuffer(60);
        appendSeriesDetails(product, sb, true, true);
        return sb.toString();
    }

    private void appendSeriesDetails(Product product, StringBuffer seriesName, boolean includeOptionType)
    {
        appendSeriesDetails(product, seriesName, includeOptionType, false);
    }

    // appends product class symbol, reporting class symbol, expiration date and optionType to the StringBuffer
    private void appendSeriesDetails(Product product, StringBuffer seriesName, boolean includeOptionType, boolean includeClassAndRepClass)
    {
        ProductNameStruct productNameStruct = product.getProductNameStruct();
        short productType = product.getProductKeysStruct().productType;

        if(productNameStruct != null)
        {
            if(includeClassAndRepClass)
            {
                String className = getProductClassName(product);
                seriesName.append(className);
                seriesName.append(" (").append(getReportingClassSymbol(product)).append(") ");
            }
            else
            {
                seriesName.append(getReportingClassSymbol(product)).append(' ');
            }

            ArrayList <Short> noDatesList = new ArrayList<Short>();
            noDatesList.add(ProductTypes.EQUITY);
            noDatesList.add(ProductTypes.STRATEGY);
            noDatesList.add(ProductTypes.INDEX);
            noDatesList.add(ProductTypes.COMMODITY);
            if(!noDatesList.contains(productType))
            {
                seriesName.append(dateFormatter.format(product.getExpirationDate(),
                                                   DateFormatStrategy.DATE_FORMAT_MONTH_DATE_YEAR_STYLE));
                seriesName.append(' ');
            }

            ArrayList <Short> noPriceList = new ArrayList<Short>();
            noPriceList.add(ProductTypes.EQUITY);
            noPriceList.add(ProductTypes.STRATEGY);
            noPriceList.add(ProductTypes.INDEX);
            noPriceList.add(ProductTypes.FUTURE);
            noPriceList.add(ProductTypes.COMMODITY);
            if(!noPriceList.contains(productType))
            {
                seriesName.append(DisplayPriceFactory.create(productNameStruct.exercisePrice).toString());
                seriesName.append(' ');

                if(includeOptionType)
                {
                    if(productNameStruct.optionType == OptionTypes.CALL)
                    {
                        seriesName.append("CALL");
                    }
                    else if(productNameStruct.optionType == OptionTypes.PUT)
                    {
                        seriesName.append("PUT");
                    }
                }
            }
        }
    }

    private String formatFullProductName(Product product, boolean includeOptionType, boolean includeProductType)
    {
        StringBuffer sb = new StringBuffer();
        if(includeProductType)
        {
            sb.append(ProductTypes.toString(product.getProductType()));
            sb.append(" : ");
        }
        sb.append(formatFullProductName(product, includeOptionType));
        return sb.toString();
    }

    /**
     * Formats a Product using FULL_PRODUCT_NAME style
     * @param product to format
     * @param includeOptionType True if option type is to be included, false to exclude it
     */
    private String formatFullProductName(Product product, boolean includeOptionType)
    {
        StringBuffer seriesName = new StringBuffer(50);
        ProductNameStruct productNameStruct = product.getProductNameStruct();

        if(productNameStruct != null)
        {
            // the product is not a series - name is in the productSymbol
            if(!productNameStruct.productSymbol.equals(""))
            {
                seriesName.append(productNameStruct.productSymbol);
            }
            else if(productNameStruct.reportingClass.length() > 0 || product.getProductKeysStruct().productKey != 0)
            {
                appendSeriesDetails(product, seriesName, includeOptionType);
            }
            else  // no product name and product key is zero
            {
                return "";
            }

            short listingState = product.getListingState();
            if(listingState != ListingStates.ACTIVE)
            {
                seriesName.append('(').append(listingStateFormatter.format(listingState)).append(')');
            }
        }
        return seriesName.toString();
    }

    /**
     * Formats a Product using PRODUCT_NAME_BRIEF_TYPE style
     * @param product to format
     */
    private String formatProductNameBriefType(Product product)
    {
        StringBuffer seriesName = new StringBuffer(50);
        ProductNameStruct productNameStruct = product.getProductNameStruct();

        if(productNameStruct != null)
        {
            // the product is not a series - name is in the productSymbol
            if(!productNameStruct.productSymbol.equals(""))
            {
                seriesName.append(productNameStruct.productSymbol);
            }
            else if(productNameStruct.reportingClass.length() > 0 ||
                    product.getProductKeysStruct().productKey != 0)
            {
                {
                    // Futures do not have strike price or option type
                    if(product.getProductKeysStruct().productType != ProductTypes.FUTURE)
                    {
                        seriesName.append(productNameStruct.optionType).append(' ');
                    }
                    seriesName.append(getReportingClassSymbol(product)).append(' ');

                    seriesName.append(dateFormatter.format(product.getExpirationDate(),
                                                           DateFormatStrategy.DATE_FORMAT_MONTH_DATE_YEAR_STYLE));
                    seriesName.append(' ');

                    // Futures do not have strike price or option type
                    if(product.getProductKeysStruct().productType != ProductTypes.FUTURE)
                    {
                        seriesName.append(DisplayPriceFactory
                                .create(productNameStruct.exercisePrice).toString());
                    }
                }
            }
            else  // no product name and product key is zero
            {
                return "";
            }

            short listingState = product.getListingState();
            if(listingState != ListingStates.ACTIVE)
            {
                seriesName.append(' ').append(listingStateFormatter.format(listingState).charAt(0));
            }
        }
        return seriesName.toString();
    }

    /**
     * Defines a method for formatting SessionProduct
     * @param sessionProduct to format
     * @return formatted string
     */
    public String format(SessionProduct sessionProduct)
    {
        return format(sessionProduct, getDefaultStyle());
    }

    /**
     * Method for formatting SessionProduct
     * @param sessionProduct to format
     * @param useCache - boolean flag, if false it will force rebild of the formatted string even if it is already cached
     * @return formatted string
     */
    public String format(SessionProduct sessionProduct, boolean useCache)
    {
        return format(sessionProduct, getDefaultStyle(), useCache);
    }

    /**
     * Defines a method for formatting SessionProduct
     * @
     * @param sessionProduct to format
     * @return formatted string
     */
    public String format(SessionProduct sessionProduct, String style)
    {
        return format(sessionProduct, style, true);
    }

    /**
     * Defines a method for formatting SessionProduct
     * @
     * @param sessionProduct to format
     * @return formatted string
     */
    public String format(SessionProduct sessionProduct, String style, boolean useCache)
    {
        String productText = null;
        if(SessionProductFactory.createAllSelected().equals(sessionProduct))
        {
            productText = ALL_PRODUCTS_SELECTED_TEXT;
        }
        else if(SessionProductFactory.createDefault().equals(sessionProduct))
        {
            productText = DEFAULT_TEXT;
        }
        else
        {
            validateStyle(style);

            if(style.equals(FULL_PRODUCT_NAME_AND_STATE))
            {
                productText = format((Product)sessionProduct, FULL_PRODUCT_NAME) + formatProductState(sessionProduct);
            }
            else if(style.equals(PRODUCT_STATE))
            {
                productText = formatProductState(sessionProduct);
            }
            else if(style.equals(FULL_PRODUCT_NAME_WITH_SESSION_AND_TYPE))
            {
                //Cache only this style.
                if ( useCache)
                {
                    productText = cache.get(sessionProduct, style);
                }

                if (productText == null)
                {
                    StringBuffer buffer = new StringBuffer(85);
                    buffer.append(getProductSession(sessionProduct));
                    buffer.append(" : ");
                    buffer.append(ProductTypes.toString(sessionProduct.getProductType()));
                    buffer.append(" : ");
                    String productClassName = getProductClassName(sessionProduct);
                    if(productClassName != null)
                    {
                        buffer.append(productClassName);
                        buffer.append(" : ");
                    }
                    buffer.append(formatFullProductName(sessionProduct, true));
                    productText = buffer.toString();
                    cache.put(sessionProduct, style, productText);
                }
            }
            else if(style.equals(FULL_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_OPTION_TYPE))
            {
               productText = formatFullProductNameWithExpDateAndExperationType(sessionProduct, true,true) + " " +
                    formatProductState(sessionProduct);
                
                
            }
            else if(style.equals(SHORT_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_OPTION_TYPE))
            {
                productText = formatFullProductNameWithExpDateAndExperationType(sessionProduct, true ,false) + " " +
                              formatProductState(sessionProduct);
            }
            else if(style.equals(PRODUCT_NAME_AND_EXP_DATE_WITH_OPTION_TYPE_STATE))
            {
                productText = formatProductNameWithExpDateAndProductType(sessionProduct, true) + " " +
                              formatProductState(sessionProduct);
            }
            else if(style.equals(FULL_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_OPTION_TYPE_STATE))
            {
                productText = formatFullProductNameWithExpDateAndExperationTypeAndState(sessionProduct, true) + " " +
                              formatProductState(sessionProduct);
            }
            else if(style.equals(SHORT_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_OPTION_TYPE_STATE))
            {
                productText = formatFullProductNameWithExpDateAndExperationTypeAndState(sessionProduct, false) + " " +
                              formatProductState(sessionProduct);
            }
            else
            {
                productText = format((Product)sessionProduct, style);
            }
        }
        return productText;
    }


    /**
     * Formats a Product using FULL_PRODUCT_NAME style
     * @param product to format
     */
    private String formatOPRAProductName(Product product)
    {
        StringBuffer seriesName = new StringBuffer();
        ProductNameStruct productNameStruct = product.getProductNameStruct();

        if(productNameStruct != null)
        {
            // the product is not a series - name is in the productSymbol
            if(!productNameStruct.productSymbol.equals(""))
            {
                seriesName.append(productNameStruct.productSymbol);
            }
            else
            {
                seriesName.append(getReportingClassSymbol(product));
                seriesName.append(product.getOpraMonthCode());
                seriesName.append(product.getOpraPriceCode());
            }
        }
        return seriesName.toString();
    }

    private String formatProductState(SessionProduct product)
    {
        short state = product.getState();
        StringBuffer sb = new StringBuffer("(");
        sb.append(ProductStates.toString(state, ProductStates.BRIEF_FORMAT));
        sb.append(")");

        return sb.toString();
    }

    /**
     * Gets the reporting class symbol
     * @param product to get symbol from
     * @return Class symbol
     */
    private String getReportingClassSymbol(Product product)
    {
        return product.getProductNameStruct().reportingClass;
    }

    private String getProductSession(SessionProduct sessionProduct)
    {
        if( sessionProduct.isDefaultSession() )
        {
            return TradingSessionFormatStrategy.ALL_SESSIONS_FORMATTED_NAME;
        }
        else
        {
            return sessionProduct.getTradingSessionName();
        }
    }

    private ProductClass getProductClass(int classKey)
    {
        ProductClass productClass = null;
        try
        {
            productClass = APIHome.findProductQueryAPI().getProductClassByKey(classKey);
        }
        catch(Exception e)
        {
            GUILoggerHome.find().exception(e);
        }
        return productClass;
    }
    private String getProductClassName(Product product)
    {
        ProductClass productClass = getProductClass(product.getProductKeysStruct().classKey);
        if(productClass != null)
        {
            return CommonFormatFactory.getProductClassFormatStrategy().format(productClass, ProductClassFormatStrategy.PLAIN_CLASS_NAME);
        }
        return null;
    }
    public String format(ProductName productName)
    {
        return format(productName, FULL_PRODUCT_NAME);
    }
    public String format(ProductName productName, String style)
    {
        validateStyle(style); // only one style for product name, but still validate it
        StringBuffer buffer = new StringBuffer(40);
        if(productName.getProductSymbol().length()!=0)
        {
            buffer.append(productName.getProductSymbol());
        }
        else
        {
            buffer.append(productName.getReportingClass()).append(" ");
            buffer.append(dateFormatter.format(productName.getExpirationDate(),
                                                   DateFormatStrategy.DATE_FORMAT_MONTH_DATE_YEAR_STYLE));
            if(productName.getExercisePrice().isNoPrice() == false)
            {
                buffer.append(" ");
                buffer.append(productName.getExercisePrice());
                buffer.append(" ");
                if (productName.getOptionType() == OptionTypes.CALL)
                {
                    buffer.append("CALL");
                }
                else if (productName.getOptionType() == OptionTypes.PUT)
                {
                    buffer.append("PUT");
                }
            }
        }
        return buffer.toString();
    }
    
    public String formatStrategyType(Product product, String style){
    	validateStyle(style); // only one style for product name, but still validate it
        StringBuffer buffer = new StringBuffer(25);
        if (style.equals(PRODUCT_STRATEGY_TYPE)){
        	String name = product.toString(); 
        	name = name.split(":|[0-9]{3,}")[0];
        	buffer.append(name);
        }
        return buffer.toString();
    }
    
}
