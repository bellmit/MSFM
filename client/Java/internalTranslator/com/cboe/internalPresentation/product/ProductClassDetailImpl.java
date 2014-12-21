package com.cboe.internalPresentation.product;

import com.cboe.idl.cmiProduct.ClassStruct;
import com.cboe.idl.cmiProduct.EPWStruct;
import com.cboe.idl.cmiProduct.ProductDescriptionStruct;
import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.product.ProductClassStruct;

import com.cboe.interfaces.internalPresentation.product.ProductClassDetail;
import com.cboe.interfaces.presentation.common.formatters.ProductClassFormatStrategy;
import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.presentation.product.ProductClass;
import com.cboe.interfaces.presentation.product.ReportingClass;

import com.cboe.presentation.common.formatters.FormatFactory;
import com.cboe.presentation.product.ProductFactoryHome;
import com.cboe.presentation.product.ProductClassFactoryHome;
import com.cboe.presentation.product.ReportingClassFactoryHome;

import com.cboe.domain.util.ProductStructBuilder;


public class ProductClassDetailImpl implements ProductClassDetail
{
    private ProductClassStruct productClassStruct;
    private ProductClass productClass;
    private Product[] products;
    private ReportingClass[] reportingClasses;
    private Product underlyingProduct = null;

    static private ProductClassFormatStrategy formatter = null;

    /**
     * Constructor
     * @param productClassStruct ProductClassStruct 
     */
    protected ProductClassDetailImpl(ProductClassStruct productClassStruct)
    {
        this();

        this.productClassStruct = productClassStruct;

        if (this.productClassStruct != null)
        {
            this.productClass = ProductClassFactoryHome.find().create(productClassStruct.info);

            this.reportingClasses = new ReportingClass[productClassStruct.info.reportingClasses.length];

            for(int i=0; i<productClassStruct.info.reportingClasses.length; i++)
            {
                this.reportingClasses[i] = ReportingClassFactoryHome.find().create(productClassStruct.info.reportingClasses[i]);
            }

            this.products = new Product[productClassStruct.products.length];

            for(int i=0; i<productClassStruct.products.length; i++)
            {
                this.products[i] = ProductFactoryHome.find().create(productClassStruct.products[i]);
            }

            if(productClassStruct.info != null)
            {
                underlyingProduct = ProductFactoryHome.find().create(productClassStruct.info.underlyingProduct);
            }
        }
    }

    /**
     *  Default constructor.
     */
    protected ProductClassDetailImpl()
    {
        super();
        if(formatter == null)
        {
            formatter = FormatFactory.getProductClassFormatStrategy();
        }
    }

    // force underlyingProduct to be regenerated from ClassStruct on next call to getUnderlyingProduct()
    public void resetUnderlyingProduct()
    {
        this.underlyingProduct = null;
    }

    public short getSettlementType()
    {
        return getProductClassStruct().settlementType;
    }

    // ProductClass methods
    public boolean isTestClass()
    {
        return getProductClass().isTestClass();
    }

    public boolean isAllSelectedProductClass()
    {
        return getProductClass().isAllSelectedProductClass();
    }

    public boolean isDefaultProductClass()
    {
        return getProductClass().isDefaultProductClass();
    }

    public short getProductType()
    {
        return getProductClass().getProductType();
    }

    public short getListingState()
    {
        return getProductClass().getListingState();
    }

    public String getClassSymbol()
    {
        return getProductClass().getClassSymbol();
    }

    public Product getUnderlyingProduct()
    {
        if(underlyingProduct == null)
        {
            if (this.getClassStruct() != null)
            {
                underlyingProduct = ProductFactoryHome.find().create(this.getClassStruct().underlyingProduct);
            }
        }

        return underlyingProduct;
    }

    public String getPrimaryExchange()
    {
        return getProductClass().getPrimaryExchange();
    }

    public DateStruct getActivationDate()
    {
        return getProductClass().getActivationDate();
    }

    public DateStruct getInactivationDate()
    {
        return getProductClass().getInactivationDate();
    }

    public DateTimeStruct getCreatedTime()
    {
        return getProductClass().getCreatedTime();
    }

    public DateTimeStruct getLastModifiedTime()
    {
        return getProductClass().getLastModifiedTime();
    }

    public EPWStruct[] getEPWValues()
    {
        return getProductClass().getEPWValues();
    }

    public double getEPWFastMarketMultiplier()
    {
        return getProductClass().getEPWFastMarketMultiplier();
    }

    public ProductDescriptionStruct getProductDescription()
    {
        return getProductClass().getProductDescription();
    }

    /*
    public Object clone() throws CloneNotSupportedException
    {
        ProductClassStruct newProductClassStruct = ProductStructBuilder.cloneProductClass(this.getProductClassStruct());
        return ProductClassDetailFactory.create(newProductClassStruct);
    }
    */


    /**
     * @deprecated Use public getters to get struct contents always
     */
    public ClassStruct getClassStruct()
    {
        return this.getProductClass().getClassStruct();
    }
    //

    public int getClassKey()
    {
        int key;
        if (getProductClass() != null)
        {
            key = getProductClass().getClassKey();
        }
        else
        {
            throw new IllegalStateException("ProductClass can not be null.");
        }
        return key;
    }
    public String getSessionCode()
    {
        return this.productClassStruct.sessionCode;
    }

    public String getDefaultTransactionFeeCode()
    {
        return this.productClassStruct.defaultTransactionFeeCode;
    }

    private ProductClass getProductClass()
    {
        return this.productClass;
    }

    public Product[] getProducts()
    {
        return this.products;
    }

    public ReportingClass[] getReportingClasses()
    {
        return this.reportingClasses;
    }

    /**
     * @deprecated Use public getters to get struct contents always
     */
    public ProductClassStruct getProductClassStruct()
    {
        return this.productClassStruct;
    }

    /**
     * Implements Cloneable
     */
    public Object clone() throws CloneNotSupportedException
    {
        ProductClassDetailImpl dest = (ProductClassDetailImpl)super.clone();
        if (getProductClassStruct() != null)
        {
            dest.productClassStruct = ProductStructBuilder.cloneProductClass(getProductClassStruct());
        }
        return dest;
    }

    /**
     * Returns a String representation of this ProductClass's ProductClass.
     */
    public String toString()
    {
        return formatter.format(this.getProductClass(), formatter.CLASS_TYPE_NAME);
    }

    /**
     * Returns a hash code for this ProductClassDetail
     */
    public int hashCode()
    {
        return getProductClass().getClassKey();
    }
    /**
     * Returns an object key
     * This implementation wraps hashCode() value into Integer object.
     * @return Object
     */
    public Object getKey()
    {
        return new Integer(hashCode());
    }

    /**
     * Determines if this ProductClass is invalid, either it has been marked inactive or has been removed from the
     * system, but some data structures still reference the classkey
     */
    public boolean isValid()
    {
        return true;
    }

    public String getPost()
    {
        return getProductClass().getPost();
    }

    public String getStation()
    {
        return getProductClass().getStation();
    }

}

