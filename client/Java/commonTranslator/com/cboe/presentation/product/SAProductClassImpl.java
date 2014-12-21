//
// -----------------------------------------------------------------------------------
// Source file: SAProductClassImpl.java
//
// PACKAGE: com.cboe.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.product;

import com.cboe.domain.util.ClientProductStructBuilder;

import com.cboe.idl.cmiProduct.ClassStruct;
import com.cboe.idl.cmiProduct.EPWStruct;
import com.cboe.idl.cmiProduct.ProductDescriptionStruct;

import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.interfaces.presentation.common.formatters.ProductClassFormatStrategy;
import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.presentation.product.ProductClass;
import com.cboe.interfaces.presentation.product.ReportingClass;
import com.cboe.interfaces.presentation.productConfiguration.Group;



import com.cboe.presentation.api.APIHome;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.common.formatters.CommonFormatFactory;


import com.cboe.presentation.productConfiguration.GroupFactory;
import com.cboe.presentation.product.ProductFactoryHome;

import org.omg.CORBA.UserException;

/**
 * ProductClass implementation for a ClassStruct from the API.
 */
class SAProductClassImpl extends AbstractProductClass
{
    protected ClassStruct classStruct = null;
    protected Product underlyingProduct = null;
    protected ProductClassFormatStrategy formatter;
    protected String cachedToString;
    protected String post;
    protected String station;

    /**
     * Constructor
     * @param classStruct to represent
     */
    protected SAProductClassImpl(ClassStruct classStruct)
    {
        this();

        this.classStruct = classStruct;

        if (this.classStruct != null)
        {
            underlyingProduct = ProductFactoryHome.find().create(this.classStruct.underlyingProduct);
        }
    }

    /**
     *  Default constructor.
     */
    protected SAProductClassImpl()
    {
        super();
        cachedToString = null;
        formatter = CommonFormatFactory.getProductClassFormatStrategy();
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
        return getClassStruct().productType;
    }

    /**
     * Get the ClassStruct that this ProductClass represents.
     * @return ClassStruct
     * @deprecated
     */
    public ClassStruct getClassStruct()
    {
        return classStruct;
    }

    /**
     * Get the class symbol for this ProductClass.
     * @return class symbol from represented struct
     */
    public String getClassSymbol()
    {
        return getClassStruct().classSymbol;
    }

    /**
     * Get the class key for this ProductClass.
     * @return class key from represented struct
     */
    public int getClassKey()
    {
        int key;

        key = getClassStruct().classKey;
        return key;
    }

    /**
     * Gets the ListingState for this ProductClass.
     * @return listing state from represented struct
     */
    public short getListingState()
    {
        return getClassStruct().listingState;
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
        return getClassStruct().primaryExchange;
    }

    /**
     * Get the activation date for this ProductClass.
     * @return activation date from represented struct
     */
    public DateStruct getActivationDate()
    {
        return getClassStruct().activationDate;
    }

    /**
     * Get the inactivation date for this ProductClass.
     * @return inactivation date from represented struct
     */
    public DateStruct getInactivationDate()
    {
        return getClassStruct().inactivationDate;
    }

    /**
     * Get the created time for this ProductClass.
     * @return creation time from represented struct
     */
    public DateTimeStruct getCreatedTime()
    {
        return getClassStruct().createdTime;
    }

    /**
     * Get the last modified time for this ProductClass.
     * @return last modified time from represented struct
     */
    public DateTimeStruct getLastModifiedTime()
    {
        return getClassStruct().lastModifiedTime;
    }

    /**
     * Get the EPW Struct values for this ProductClass.
     * @return EPWStruct elements from represented struct
     */
    public EPWStruct[] getEPWValues()
    {
        return getClassStruct().epwValues;
    }

    /**
     * Get the EPW fast market multiplier for this ProductClass.
     * @return EPWFastMarketMultiplier from represented struct
     */
    public double getEPWFastMarketMultiplier()
    {
        return getClassStruct().epwFastMarketMultiplier;
    }

    /**
     * Get the product description struct for this ProductClass.
     * @return ProductDescriptionStruct from represented struct
     */
    public ProductDescriptionStruct getProductDescription()
    {
        return getClassStruct().productDescription;
    }

    /**
     * return true if this ProductClass is designated as a Test Class
     */
    public boolean isTestClass()
    {
        return getClassStruct().testClass;
    }

    /**
     * Gets all the reporting classes for this product class
     * @return an array of reporting classes
     */
    public ReportingClass[] getReportingClasses()
    {
        ReportingClass[] reportingClasses = new ReportingClass[getClassStruct().reportingClasses.length];

        for(int i = 0; i < getClassStruct().reportingClasses.length; i++)
        {
            reportingClasses[i] = ReportingClassFactoryHome.find().create(getClassStruct().reportingClasses[i], this);
        }

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
        SAProductClassImpl dest;
        dest = new SAProductClassImpl();
        if (getClassStruct() != null)
        {
            dest.classStruct = ClientProductStructBuilder.cloneClassStruct(getClassStruct());
        }

        return dest;
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