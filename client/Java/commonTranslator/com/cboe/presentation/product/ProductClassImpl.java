//
// -----------------------------------------------------------------------------------
// Source file: ProductClassImpl.java
//
// PACKAGE: com.cboe.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.product;

import com.cboe.domain.util.ClientProductStructBuilder;
import com.cboe.domain.util.ProductStructBuilder;
import com.cboe.idl.cmiProduct.ClassStruct;
import com.cboe.idl.cmiProduct.EPWStruct;
import com.cboe.idl.cmiProduct.ProductDescriptionStruct;
import com.cboe.idl.cmiProduct.ReportingClassStruct;
import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.interfaces.presentation.common.formatters.ProductClassFormatStrategy;
import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.presentation.product.ProductClass;
import com.cboe.interfaces.presentation.product.ReportingClass;
import com.cboe.interfaces.presentation.productConfiguration.Group;
import com.cboe.interfaces.domain.dateTime.Date;
import com.cboe.interfaces.domain.dateTime.DateTime;
import com.cboe.interfaces.domain.Price;
import com.cboe.presentation.api.APIHome;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.common.formatters.CommonFormatFactory;
import com.cboe.presentation.common.formatters.DisplayPriceFactory;
import com.cboe.presentation.common.dateTime.DateTimeFactory;
import com.cboe.presentation.productConfiguration.GroupFactory;
import com.cboe.presentation.util.StringCache;
import org.omg.CORBA.UserException;

/**
 * ProductClass implementation for a ClassStruct from the API.
 */
class ProductClassImpl extends AbstractProductClass
{
    protected Product underlyingProduct = null;
    protected ProductClassFormatStrategy formatter;
    protected String cachedToString;
    protected String post;
    protected String station;

    // ClassStruct fields
    private int classKey;
    private short productType;
    private short listingState;
    private String classSymbol;

    private String primaryExchange;
    private Date activationDate;
    private Date inactivationDate;
    private DateTime createdTime;
    private DateTime lastModifiedTime;
    private EPWStruct[] epwValues;
    private double epwFastMarketMultiplier;
    private boolean testClass;
    private ReportingClass[] reportingClasses;

    // ProductDescriptionStruct fields
    private String name;
    private String baseDescriptionName;
    private Price minimumStrikePriceFraction;
    private Price maxStrikePrice;
    private Price premiumBreakPoint;
    private Price minimumAbovePremiumFraction;
    private Price minimumBelowPremiumFraction;
    private short priceDisplayType;
    private short premiumPriceFormat;
    private short strikePriceFormat;
    private short underlyingPriceFormat;

    /**
     * Constructor
     * @param classStruct to represent
     */
    protected ProductClassImpl(ClassStruct classStruct)
    {
        this();
        updateFromStruct(classStruct);
    }

    /**
     *  Default constructor.
     */
    protected ProductClassImpl()
    {
        super();
        cachedToString = null;
        formatter = CommonFormatFactory.getProductClassFormatStrategy();
    }

    protected void updateFromStruct(ClassStruct struct)
    {
        if(struct != null)
        {
            classKey = struct.classKey;
            productType = struct.productType;
            listingState = struct.listingState;
            classSymbol = StringCache.get(struct.classSymbol);
            primaryExchange = StringCache.get(struct.primaryExchange);

            activationDate = DateTimeFactory.getDate(struct.activationDate);
            inactivationDate = DateTimeFactory.getDate(struct.inactivationDate);
            createdTime = DateTimeFactory.getDateTime(struct.createdTime);
            lastModifiedTime = DateTimeFactory.getDateTime(struct.lastModifiedTime);
            epwValues = new EPWStruct[struct.epwValues.length];
            for(int i=0; i<struct.epwValues.length; i++)
            {
                epwValues[i] = new EPWStruct();
                epwValues[i].maximumAllowableSpread = struct.epwValues[i].maximumAllowableSpread;
                epwValues[i].maximumBidRange = struct.epwValues[i].maximumBidRange;
                epwValues[i].minimumBidRange = struct.epwValues[i].minimumBidRange;
            }
            epwFastMarketMultiplier = struct.epwFastMarketMultiplier;
            updateFromStruct(struct.productDescription);

            testClass = struct.testClass;
            updateFromStruct(struct.reportingClasses);

            underlyingProduct = ProductFactoryHome.find().create(struct.underlyingProduct);
        }
    }

    protected void updateFromStruct(ReportingClassStruct[] structs)
    {
        reportingClasses = new ReportingClass[structs.length];
        for (int i = 0; i < structs.length; i++)
        {
            reportingClasses[i] = ReportingClassFactoryHome.find().create(structs[i], this);
        }
    }

    protected void updateFromStruct(ProductDescriptionStruct struct)
    {
        name = StringCache.get(struct.name);
        baseDescriptionName = StringCache.get(struct.baseDescriptionName);

        minimumStrikePriceFraction = DisplayPriceFactory.create(struct.minimumStrikePriceFraction);
        maxStrikePrice = DisplayPriceFactory.create(struct.maxStrikePrice);
        premiumBreakPoint = DisplayPriceFactory.create(struct.premiumBreakPoint);
        minimumAbovePremiumFraction = DisplayPriceFactory.create(struct.minimumAbovePremiumFraction);
        minimumBelowPremiumFraction = DisplayPriceFactory.create(struct.minimumBelowPremiumFraction);
        priceDisplayType = struct.priceDisplayType;
        premiumPriceFormat = struct.premiumPriceFormat;
        strikePriceFormat = struct.strikePriceFormat;
        underlyingPriceFormat = struct.underlyingPriceFormat;
    }

    public boolean isAllSelectedProductClass()
    {
        return false;
    }

    public boolean isDefaultProductClass()
    {
        return false;
    }

    /**
     * Get the product type for this ProductClass.
     * @return product type from represented struct
     */
    public short getProductType()
    {
        return productType;
    }

    /**
     * Get the ClassStruct that this ProductClass represents.
     * @return ClassStruct
     * @deprecated
     */
    public ClassStruct getClassStruct()
    {
        ClassStruct retVal = new ClassStruct();
        retVal.classKey = getClassKey();
        retVal.productType = getProductType();
        retVal.listingState = getListingState();
        retVal.classSymbol = getClassSymbol();
        retVal.underlyingProduct = getUnderlyingProduct().getProductStruct();
        retVal.primaryExchange = getPrimaryExchange();
        retVal.activationDate = getActivationDate();
        retVal.inactivationDate = getInactivationDate();
        retVal.createdTime = getCreatedTime();
        retVal.lastModifiedTime = getLastModifiedTime();
        EPWStruct[] epwValues = getEPWValues();
        retVal.epwValues = new EPWStruct[epwValues.length];
        for(int i=0; i<epwValues.length; i++)
        {
            retVal.epwValues[i] = ClientProductStructBuilder.cloneEPWStruct(epwValues[i]);
        }
        retVal.epwFastMarketMultiplier = getEPWFastMarketMultiplier();
        retVal.productDescription = ClientProductStructBuilder.cloneProductDescriptionStruct(getProductDescription());
        retVal.testClass = isTestClass();
        ReportingClass[] repClasses = getReportingClasses();
        retVal.reportingClasses = new ReportingClassStruct[repClasses.length];
        for(int i=0; i<repClasses.length; i++)
        {

            retVal.reportingClasses[i] = ProductStructBuilder.cloneReportingClass(repClasses[i].getClassStruct());
        }
        return retVal;
    }

    /**
     * Get the class symbol for this ProductClass.
     * @return class symbol from represented struct
     */
    public String getClassSymbol()
    {
        return classSymbol;
    }

    /**
     * Get the class key for this ProductClass.
     * @return class key from represented struct
     */
    public int getClassKey()
    {
        return classKey;
    }

    /**
     * Gets the ListingState for this ProductClass.
     * @return listing state from represented struct
     */
    public short getListingState()
    {
        return listingState;
    }

    /**
     * Get the underlying product for this ProductClass.
     * @return underlying product from represented struct
     */
    public Product getUnderlyingProduct()
    {
        return underlyingProduct;
    }

    /**
     * Get the primary exchange for this ProductClass.
     * @return primary exchange from represented struct
     */
    public String getPrimaryExchange()
    {
        return primaryExchange;
    }

    /**
     * Get the activation date for this ProductClass.
     * @return activation date from represented struct
     */
    public DateStruct getActivationDate()
    {
        return activationDate.getDateStruct();
    }

    /**
     * Get the inactivation date for this ProductClass.
     * @return inactivation date from represented struct
     */
    public DateStruct getInactivationDate()
    {
        return inactivationDate.getDateStruct();
    }

    /**
     * Get the created time for this ProductClass.
     * @return creation time from represented struct
     */
    public DateTimeStruct getCreatedTime()
    {
        return createdTime.getDateTimeStruct();
    }

    /**
     * Get the last modified time for this ProductClass.
     * @return last modified time from represented struct
     */
    public DateTimeStruct getLastModifiedTime()
    {
        return lastModifiedTime.getDateTimeStruct();
    }

    /**
     * Get the EPW Struct values for this ProductClass.
     * @return EPWStruct elements from represented struct
     */
    public EPWStruct[] getEPWValues()
    {
        return epwValues;
    }

    /**
     * Get the EPW fast market multiplier for this ProductClass.
     * @return EPWFastMarketMultiplier from represented struct
     */
    public double getEPWFastMarketMultiplier()
    {
        return epwFastMarketMultiplier;
    }

    /**
     * Get the product description struct for this ProductClass.
     * @return ProductDescriptionStruct from represented struct
     */
    public ProductDescriptionStruct getProductDescription()
    {
        ProductDescriptionStruct retVal = new ProductDescriptionStruct();
        retVal.name = name;
        retVal.baseDescriptionName = baseDescriptionName;
        retVal.minimumStrikePriceFraction = minimumStrikePriceFraction.toStruct();
        retVal.maxStrikePrice = maxStrikePrice.toStruct();
        retVal.premiumBreakPoint = premiumBreakPoint.toStruct();
        retVal.minimumAbovePremiumFraction = minimumAbovePremiumFraction.toStruct();
        retVal.minimumBelowPremiumFraction = minimumBelowPremiumFraction.toStruct();
        retVal.priceDisplayType = priceDisplayType;
        retVal.premiumPriceFormat = premiumPriceFormat;
        retVal.strikePriceFormat = strikePriceFormat;
        retVal.underlyingPriceFormat = underlyingPriceFormat;

        return retVal;
    }

    /**
     * return true if this ProductClass is designated as a Test Class
     */
    public boolean isTestClass()
    {
        return testClass;
    }

    /**
     * Gets all the reporting classes for this product class
     * @return an array of reporting classes
     */
    public ReportingClass[] getReportingClasses()
    {
        return reportingClasses;
    }

    /**
     * getPost()
     */
    public String getPost()
    {
        if(post == null)
        {
            try
            {
                Group[] groups = GroupFactory.createGroups(APIHome.findProductConfigurationQueryAPI().getGroupsForProductClass(getClassKey()));
                for(Group group : groups)
                {
                    if(APIHome.findProductConfigurationQueryAPI().isPostGroup(group.getGroupStruct()))
                    {
                        post = String.valueOf(group.getPostNumber());
                    }
                }
            }
            catch(UserException ue)
            {
                DefaultExceptionHandlerHome.find().process(ue, "Error getting Post info for Product Class '" +
                                                                  toString() + "'");
            }
        }
        return post;
    }

    public String getStation()
    {
        if(station == null)
        {
            try
            {
                Group[] groups = GroupFactory.createGroups(APIHome.findProductConfigurationQueryAPI().getGroupsForProductClass(getClassKey()));
                for(Group group : groups)
                {
                    if(APIHome.findProductConfigurationQueryAPI().isStationGroup(group.getGroupStruct()))
                    {
                        station = String.valueOf(group.getStationNumber());
                    }
                }
            }
            catch(UserException ue)
            {
                DefaultExceptionHandlerHome.find().process(ue, "Error getting Station info for Product Class '" +
                                                                  toString() + "'");
            }
        }
        return station;
    }


    /**
     * Clones this class by returning another instance that represents a
     * ClassStruct that was also cloned.
     */
    public Object clone() throws CloneNotSupportedException
    {
        return new ProductClassImpl(getClassStruct());
    }

    /**
     * If <code>obj</code> is an instance of ProductClass and has the same
     * class key true is returned, false otherwise.
     * @param obj to compare
     * @return true if equal, false if not.
     */
    public boolean equals(Object obj)
    {
        boolean isEqual = super.equals(obj);

        if(!isEqual)
        {
            if( obj instanceof ProductClass )
            {
                isEqual = getClassKey() == (( ProductClass ) obj).getClassKey();
            }
            else
            {
                isEqual = false;
            }
        }

        return isEqual;
    }

    /**
     * Returns a String representation of this ProductClass.
     */
    public String toString()
    {
        if(cachedToString == null)
        {
            cachedToString = formatter.format(this, formatter.CLASS_TYPE_NAME);
        }
        return cachedToString;
    }
}