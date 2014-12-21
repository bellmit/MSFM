//
// -----------------------------------------------------------------------------------
// Source file: ProductClassModelImpl.java
//
// PACKAGE: com.cboe.internalPresentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.product;

import java.util.*;

import org.omg.CORBA.UserException;

import com.cboe.idl.cmiProduct.ClassStruct;
import com.cboe.idl.cmiProduct.EPWStruct;
import com.cboe.idl.cmiProduct.ProductDescriptionStruct;
import com.cboe.idl.cmiProduct.ReportingClassStruct;
import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.cmiUtil.TimeStruct;
import com.cboe.idl.product.ClassDefinitionStruct;
import com.cboe.idl.product.ProductClassStruct;

import com.cboe.exceptions.AlreadyExistsException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;

import com.cboe.interfaces.internalPresentation.product.ProductClassDetail;
import com.cboe.interfaces.internalPresentation.product.ProductClassModel;
import com.cboe.interfaces.internalPresentation.product.ProductDescription;
import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.presentation.product.ReportingClass;
import com.cboe.interfaces.presentation.validation.ValidationErrorCodes;
import com.cboe.interfaces.presentation.validation.ValidationResult;

import com.cboe.presentation.common.businessModels.AbstractMutableBusinessModel;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.validation.ValidationResultImpl;
import com.cboe.presentation.product.ProductFactoryHome;
import com.cboe.presentation.product.ReportingClassFactoryHome;

import com.cboe.internalPresentation.api.SystemAdminAPIFactory;
import com.cboe.internalPresentation.common.formatters.SettlementTypes;

import com.cboe.domain.util.ProductStructBuilder;

/**
 * ProductClassModel implementation
 */
class ProductClassModelImpl extends AbstractMutableBusinessModel implements ProductClassModel
{
    private ProductClassDetail productClassDetail = null;
    private Object key = null;

    private ProductClassStruct productClassStruct;
    private Product[] products;
    private ReportingClass[] reportingClasses;

//    /**
//     * Constructor
//     * @param classStruct to represent
//     */
    /*
    protected ProductClassModelImpl(ProductClass productClass)
    {
        super();
        this.productClass = productClass;
    }
    */
    protected ProductClassModelImpl(ProductClassStruct productClassStruct)
    {
        super();
        // update cache of ReportingClasses since this is a new impl; don't fire an event
        setProductClassStruct(productClassStruct, false);
    }

    public ProductClassModelImpl(ProductClassStruct productClassStruct, Comparator comparator)
    {
        super(comparator);
        // update cache of ReportingClasses since this is a new impl; don't fire an event
        setProductClassStruct(productClassStruct, false);
    }

    public void setProductClassStruct(ProductClassStruct productClassStruct, boolean fireEvent)
    {
        // will default to updating cache of ReportingClasses
        setProductClassStruct(productClassStruct, true, false, fireEvent);
    }

    private void setProductClassStruct(ProductClassStruct productClassStruct, boolean setReportingClasses, boolean setProducts, boolean fireEvent)
    {
        if (productClassStruct == null)
        {
            throw new IllegalArgumentException("ProductClassStruct can not be null");
        }
        else
        {
            if ( this.productClassStruct == null || this.productClassStruct != productClassStruct )
            {
                ProductClassStruct oldValue = this.productClassStruct;
                this.productClassStruct = productClassStruct;
                this.productClassDetail = ProductClassDetailFactory.create(productClassStruct);
                key = null;

                if(setReportingClasses)
                {
                    this.reportingClasses = new ReportingClass[productClassStruct.info.reportingClasses.length];

                    for(int i=0; i<productClassStruct.info.reportingClasses.length; i++)
                    {
                        this.reportingClasses[i] = ReportingClassFactoryHome.find().create(productClassStruct.info.reportingClasses[i]);
                    }
                }

                // not planning on saving a cache of Products here, but will keep this to be consistent with the ProductClassStruct
                if(setProducts)
                {
                    this.products = new Product[productClassStruct.products.length];

                    for(int i=0; i<productClassStruct.products.length; i++)
                    {
                        this.products[i] = ProductFactoryHome.find().create(productClassStruct.products[i]);
                    }
                }

                if ( fireEvent )
                {
                    setModified(true);
                    firePropertyChange(DATA_CHANGE_EVENT, oldValue, productClassStruct);
                }
            }
        }
    }

    public ProductClassStruct getProductClassStruct()
    {
        return this.productClassStruct;
    }

    /**
     * Get the product type for this ProductClass.
     * @return product type from represented struct
     */
    public short getProductType()
    {
        return getProductClassDetail().getProductType();
    }

    /**
     * Get the ClassStruct that this ProductClass represents.
     * @return ClassStruct
     * @deprecated
     */
    public ClassStruct getClassStruct()
    {
        return getProductClassDetail().getClassStruct();
    }

    /**
     * Get the class symbol for this ProductClass.
     * @return class symbol from represented struct
     */
    public String getClassSymbol()
    {
        return getProductClassDetail().getClassSymbol();
    }

    /**
     * Get the class key for this ProductClass.
     * @return class key from represented struct
     */
    public int getClassKey()
    {
        return getProductClassDetail().getClassKey();
    }

    /**
     * Gets the ListingState for this ProductClass.
     * @return listing state from represented struct
     */
    public short getListingState()
    {
        return getProductClassDetail().getListingState();
    }

    /**
     * Get the underlying product for this ProductClass.
     * @return underlying product from represented struct
     */
    public Product getUnderlyingProduct()
    {
        return getProductClassDetail().getUnderlyingProduct();
    }

    /**
     * Get the primary exchange for this ProductClass.
     * @return primary exchange from represented struct
     */
    public String getPrimaryExchange()
    {
        return getProductClassDetail().getPrimaryExchange();
    }

    /**
     * Get the activation date for this ProductClass.
     * @return activation date from represented struct
     */
    public DateStruct getActivationDate()
    {
        return getProductClassDetail().getActivationDate();
    }

    /**
     * Get the inactivation date for this ProductClass.
     * @return inactivation date from represented struct
     */
    public DateStruct getInactivationDate()
    {
        return getProductClassDetail().getInactivationDate();
    }

    /**
     * Get the created time for this ProductClass.
     * @return creation time from represented struct
     */
    public DateTimeStruct getCreatedTime()
    {
        return getProductClassDetail().getCreatedTime();
    }

    /**
     * Get the last modified time for this ProductClass.
     * @return last modified time from represented struct
     */
    public DateTimeStruct getLastModifiedTime()
    {
        return getProductClassDetail().getLastModifiedTime();
    }

    /**
     * Get the EPW Struct values for this ProductClass.
     * @return EPWStruct elements from represented struct
     */
    public EPWStruct[] getEPWValues()
    {
        return getProductClassDetail().getEPWValues();
    }

    /**
     * Get the EPW fast market multiplier for this ProductClass.
     * @return EPWFastMarketMultiplier from represented struct
     */
    public double getEPWFastMarketMultiplier()
    {
        return getProductClassDetail().getEPWFastMarketMultiplier();
    }

    /**
     * Get the product description struct for this ProductClass.
     * @return ProductDescriptionStruct from represented struct
     */
    public ProductDescriptionStruct getProductDescription()
    {
        return getProductClassDetail().getProductDescription();
    }

    /**
     * Get the product description struct for this ProductClass.
     * @return ProductDescriptionStruct from represented struct
     */
    public String getDefaultTransactionFeeCode()
    {
        return getProductClassDetail().getDefaultTransactionFeeCode();
    }

    /**
     * return true if this ProductClass is designated as a Test Class
     */
    public boolean isTestClass()
    {
        return getProductClassDetail().isTestClass();
    }

    public boolean isDefaultProductClass()
    {
        return getProductClassDetail().isDefaultProductClass();
    }

    public boolean isAllSelectedProductClass()
    {
        return getProductClassDetail().isAllSelectedProductClass();
    }


    /**
     * Post and station
     */
    public String getPost()
    {
        return getProductClassDetail().getPost();
    }

    public String getStation()
    {
        return getProductClassDetail().getStation();
    }


    /**
     * Clones this class by returning another instance that represents a
     * ClassStruct that was also cloned.
     */
    public Object clone() throws CloneNotSupportedException
    {
        ProductClassModelImpl dest = new ProductClassModelImpl(ProductStructBuilder.cloneProductClass(getProductClassStruct()), getComparator());
//        dest = (ProductClassModelImpl) super.clone();
//        if (getProductClassStruct() != null)
//        {
//            // make sure ProductClassStruct implements Cloneable
//            // don't fireEvent
//            dest.setProductClassStruct(ProductStructBuilder.cloneProductClass(getProductClassStruct()), false);
//        }

        return dest;
    }

    /**
     * If <code>obj</code> is an instance of ProductClassModel and has the same
     * class key true is returned, false otherwise.
     * @param obj to compare
     * @return true if equal, false if not.
     */
    public boolean equals(Object obj)
    {
        return compareTo(obj)==0;
    }

    public int compareTo(Object obj)
    {
        int retVal = -1;

        if ( this == obj )
        {
            retVal = 0;
        }
        else if ( obj instanceof  ProductClassModel )
        {
            retVal = getClassKey() - ((ProductClassModel)obj).getClassKey();
        }

        return retVal;
    }

    /**
     * Returns a String representation of this ProductClass.
     */
    public String toString()
    {
        return getProductClassDetail().toString();
    }

    /**
     * Set the ProductClass that this ProductClassModel represents.
     * @param ProductClass
     */
     /*
    public void setProductClass(ProductClass productClass)
    {
        setProductClass(productClass, true);
    }
    */

    /**
     * Set the ProductClassDetail that this ProductClassModel represents.
     * @param productClassDetail ProductClassDetail
     * @param fireEvent boolean - setModified and fire DATA_CHANGE_EVENT if true
     */
    private void setProductClassDetail(ProductClassDetail productClassDetail, boolean fireEvent)
    {
        if ( productClassDetail == null )
        {
            throw new IllegalArgumentException("ProductClassDetail can not be null");
        }
        else if ( this.productClassDetail == null || this.productClassDetail != productClassDetail )
        {
            ProductClassDetail oldValue = this.productClassDetail;
            this.productClassDetail = productClassDetail;
            key = null;
            if ( fireEvent )
            {
                setModified(true);
                firePropertyChange(DATA_CHANGE_EVENT, oldValue, productClassDetail);
            }
        }
    }

    private ProductClassDetail getProductClassDetail()
    {
        return this.productClassDetail;
    }

    // used for SystemAdminAPI.addProductClass(ClassDefinitionStruct) and updateProductClass(ClassDefinitionStruct)
    private ClassDefinitionStruct getClassDefinitionStruct()
    {
        ClassDefinitionStruct cStruct = ProductStructBuilder.buildClassDefinitionStruct();

        cStruct.activationDate = getActivationDate();
        cStruct.classKey = getClassKey();
        cStruct.classSymbol = getClassSymbol();
        cStruct.inactivationDate = getInactivationDate();
        cStruct.listingState = getListingState();
        cStruct.primaryExchange = getPrimaryExchange();
        cStruct.productType = getProductType();
        cStruct.underlyingProduct = getUnderlyingProduct().getProductStruct();
        cStruct.defaultTransactionFeeCode = getDefaultTransactionFeeCode();
        cStruct.testClass = isTestClass();
        cStruct.settlementType = getSettlementType();
        if(getProductClassDetail().getProductDescription() != null)
        {
            cStruct.descriptionName = getProductDescription().name;
        }

        return cStruct;
    }

    public int hashCode()
    {
        return getProductClassDetail().hashCode();
    }

    //Provide setter methods in addition to getters defined in Product interface
    public void setClassKey(int classKey)
    {
        if ( getClassKey() != classKey )
        {
            int oldValue = getClassKey();
            getClassStruct().classKey = classKey;
            setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, classKey);
        }
    }

    public void setIsTestClass(boolean isTest)
    {
        if(isTestClass() != isTest)
        {
            boolean oldValue = isTestClass();
            getClassStruct().testClass = isTest;
            setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, isTest);
        }
    }

    public void setProductType(short productType)
    {
        if ( getProductType() != productType )
        {
            short oldValue = getProductType();
            getClassStruct().productType = productType;
            setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, productType);
        }
    }
    public void setListingState(short listingState)
    {
        if ( getListingState() != listingState )
        {
            short oldValue = getListingState();
            getClassStruct().listingState = listingState;
            setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, listingState);
        }
    }
    public void setClassSymbol(String classSymbol)
    {
        if ( classSymbol == null )
        {
            throw new IllegalArgumentException("ClassSymbol can not be null");
        }
        else if ( !getClassSymbol().equals(classSymbol) )
        {
            String oldValue = getClassSymbol();
            getClassStruct().classSymbol = classSymbol;
            setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, classSymbol);
        }
    }
    public void setUnderlyingProduct(Product underlyingProduct)
    {
        if ( underlyingProduct == null )
        {
            throw new IllegalArgumentException("UnderlyingProduct can not be null");
        }
        else if(!getUnderlyingProduct().equals(underlyingProduct))
        {
GUILoggerHome.find().debug("\n********************************\nProductClassModelImpl.setUnderlyingProduct() - setting Class '"+this.getProductClassDetail().getClassSymbol()+"' (classKey="+this.getClassKey()+") Underlying to '"+underlyingProduct.toString()+"'", GUILoggerBusinessProperty.PRODUCT_DEFINITION, underlyingProduct.getProductStruct());
            Product oldValue = getUnderlyingProduct();
            getClassStruct().underlyingProduct = underlyingProduct.getProductStruct();
//            this.getProductClassDetail().underlyingProduct = null;
            // force underlyingProduct to be recreated from ProductFactory on next call to getUnderlyingProduct()
            this.resetUnderlyingProduct();
GUILoggerHome.find().debug("\n********************************\nProductClassModelImpl.setUnderlyingProduct() - Set Underlying to:", GUILoggerBusinessProperty.PRODUCT_DEFINITION, getClassStruct().underlyingProduct);
GUILoggerHome.find().debug("\n********************************\n********************************\n", GUILoggerBusinessProperty.PRODUCT_DEFINITION);
            setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, underlyingProduct);
        }
    }

    // force underlyingProduct to be regenerated from ClassStruct on next call to getUnderlyingProduct()
    public void resetUnderlyingProduct()
    {
        this.getProductClassDetail().resetUnderlyingProduct();
    }

    public void setPrimaryExchange(String primaryExchange)
    {
        if ( primaryExchange == null )
        {
            throw new IllegalArgumentException("PrimaryExchange can not be null");
        }
        else if(!getPrimaryExchange().equals(primaryExchange))
        {
            String oldValue = getPrimaryExchange();
            getClassStruct().primaryExchange = primaryExchange;
            setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, primaryExchange);
        }
    }
    public void setDefaultTransactionFeeCode(String defaultTransactionFeeCode)
    {
        if ( defaultTransactionFeeCode == null )
        {
            throw new IllegalArgumentException("defaultTransactionFeeCode can not be null");
        }
        else if(getDefaultTransactionFeeCode() == null || !getDefaultTransactionFeeCode().equals(defaultTransactionFeeCode))
        {
            String oldValue = getDefaultTransactionFeeCode();
            getProductClassStruct().defaultTransactionFeeCode = defaultTransactionFeeCode;
            setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, defaultTransactionFeeCode);
        }
    }

    public void setActivationDate(DateStruct activationDate)
    {
        if ( activationDate == null )
        {
            throw new IllegalArgumentException("Activation Date can not be null");
        }
        else if ( !isEqualDate(getActivationDate(), activationDate) )
        {
            DateStruct oldValue = getActivationDate();
            getClassStruct().activationDate = activationDate;
            setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, activationDate);
        }
    }
    public void setInactivationDate(DateStruct inactivationDate)
    {
        if ( inactivationDate == null )
        {
            throw new IllegalArgumentException("Inactivation Date can not be null");
        }
        else if ( !isEqualDate(getInactivationDate(), inactivationDate) )
        {
            DateStruct oldValue = getInactivationDate();
            getClassStruct().inactivationDate = inactivationDate;
            setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, inactivationDate);
        }
    }
    public void setCreatedTime(DateTimeStruct createdTime)
    {
        if ( createdTime == null )
        {
            throw new IllegalArgumentException("Created Time can not be null");
        }
        else if ( !isEqualDateTime(getCreatedTime(), createdTime) )
        {
            DateTimeStruct oldValue = getCreatedTime();
            getClassStruct().createdTime = createdTime;
            setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, createdTime);
        }
    }
    public void setLastModifiedTime(DateTimeStruct lastModifiedTime)
    {
        if ( lastModifiedTime == null )
        {
            throw new IllegalArgumentException("LastModifiedTime can not be null");
        }
        else if ( !isEqualDateTime(getLastModifiedTime(), lastModifiedTime) )
        {
            DateTimeStruct oldValue = getLastModifiedTime();
            getClassStruct().lastModifiedTime = lastModifiedTime;
            setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, lastModifiedTime);
        }
    }
    public void setEPWValues(EPWStruct[] epwValues)
    {
        if ( epwValues == null )
        {
            throw new IllegalArgumentException("EPWValues can not be null");
        }
//        else if(
    }
    public void setEPWFastMarketMultiplier(double multiplier)
    {
        if ( getEPWFastMarketMultiplier() != multiplier )
        {
            Double oldValue = new Double(getEPWFastMarketMultiplier());
            getClassStruct().epwFastMarketMultiplier = multiplier;
            setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, new Double(multiplier));
        }
    }
    public void setProductDescription(ProductDescription productDescription)
    {
        if ( productDescription == null )
        {
            throw new IllegalArgumentException("ProductDescription can not be null");
        }
        else
        {
            ProductDescriptionStruct productDescriptionStruct = productDescription.getProductDescriptionStruct();

            if ( !getProductDescription().equals(productDescriptionStruct) )
            {
                ProductDescriptionStruct oldValue = getProductDescription();

                getClassStruct().productDescription = productDescriptionStruct;
                setModified(true);
                firePropertyChange(DATA_CHANGE_EVENT, oldValue, productDescriptionStruct);
            }
        }
    }

    public void addReportingClass(ReportingClassStruct repClassStruct)
        throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, AuthorizationException, AlreadyExistsException
    {
        SystemAdminAPIFactory.find().addReportingClass(repClassStruct);
        try
        {
            this.refreshData();
        }
        catch(UserException e)
        {
            DefaultExceptionHandlerHome.find().process(e);
        }
    }

    public void updateReportingClass(ReportingClassStruct repClassStruct)
        throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, AuthorizationException, AlreadyExistsException
    {
        ReportingClassStruct newStruct = SystemAdminAPIFactory.find().updateReportingClass(repClassStruct);
GUILoggerHome.find().debug("ProductClassModelImpl.updateReportingClass",GUILoggerBusinessProperty.PRODUCT_DEFINITION, newStruct);
        try
        {
            this.refreshData();
        }
        catch(UserException e)
        {
            DefaultExceptionHandlerHome.find().process(e);
        }
    }

    public void setReportingClasses(ReportingClass[] repClasses)
    {
        if(repClasses == null)
        {
            throw new IllegalArgumentException("ReportingClass array cannot be null");
        }
        else
        {
            ReportingClass[] oldValue = this.reportingClasses;
            this.reportingClasses = repClasses;
            // if reportingClasses are changed, then need to fire event for the class so the gui can update
            setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, repClasses);
        }
    }

    public void setSessionCode(String sessionCode)
    {
        if ( sessionCode == null )
        {
            throw new IllegalArgumentException("sessionCode cannot be null");
        }
        else if(getSessionCode() == null || !getSessionCode().equals(sessionCode))
        {
            String oldValue = getSessionCode();
            getProductClassStruct().sessionCode = sessionCode;
            setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, sessionCode);
        }
    }

    public void setProducts(Product[] products)
    {
        if(products == null)
        {
            throw new IllegalArgumentException("Product array cannot be null");
        }
        else
        {
            Product[] oldValue = this.products;
            this.products = products;
            // if products are changed, then need to fire event for the class so the gui can update
            setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, products);
        }
    }
    private ReportingClassStruct buildReportingClassStruct()
    {
        ReportingClassStruct reportingClassStruct = ProductStructBuilder.buildReportingClassStruct();
        reportingClassStruct.listingState = getListingState();
        reportingClassStruct.productType = getProductType();
        reportingClassStruct.reportingClassSymbol = getClassSymbol();
        reportingClassStruct.productClassKey = getClassKey();
        reportingClassStruct.productClassSymbol = getClassSymbol();
        reportingClassStruct.activationDate = getActivationDate();
        reportingClassStruct.inactivationDate = getInactivationDate();
        return reportingClassStruct;
    }
    //Implementing abstract methods form AbstractTransactionalBusinessModel
    public void saveChanges() throws UserException
    {
        if ( isModified() )
        {
            boolean isNewProductClass = getClassKey() == com.cboe.idl.cmiConstants.ProductClass.DEFAULT_CLASS_KEY;
            ProductClassStruct newProductClassStruct;
            ReportingClassStruct reportingClassStruct;

            if( isNewProductClass )
            {
                //GUILoggerHome.find().debug("Creating New ProductClass...", GUILoggerBusinessProperty.PRODUCT_DEFINITION, getProductClassDetail());
                GUILoggerHome.find().debug("Creating New ClassDefinitionStruct...", GUILoggerBusinessProperty.PRODUCT_DEFINITION, getClassDefinitionStruct());
                newProductClassStruct = SystemAdminAPIFactory.find().addProductClassWithSessionCode(getClassDefinitionStruct(), getSessionCode());
                //GUILoggerHome.find().debug("Created New ProductClass...", GUILoggerBusinessProperty.PRODUCT_DEFINITION, getProductClassDetail());
                GUILoggerHome.find().debug("Created New ProductClassStruct...", GUILoggerBusinessProperty.PRODUCT_DEFINITION, newProductClassStruct);
                reportingClassStruct = SystemAdminAPIFactory.find().addReportingClass(buildReportingClassStruct());
                GUILoggerHome.find().debug("Created New ReportingClassStruct...", GUILoggerBusinessProperty.PRODUCT_DEFINITION, reportingClassStruct);

                // get ProductClassStruct again, this time it should have reporting class
                newProductClassStruct = SystemAdminAPIFactory.find().getProductClassByKey(newProductClassStruct.info.classKey, true, false, false);
            }
            else // Update existing ProductClass
            {
                GUILoggerHome.find().debug("Updating ProductClass...", GUILoggerBusinessProperty.PRODUCT_DEFINITION, getClassDefinitionStruct());
                newProductClassStruct = SystemAdminAPIFactory.find().updateProductClassWithSessionCode(getClassDefinitionStruct(), getSessionCode());
                GUILoggerHome.find().debug("ProductClass Updatedd. New ProductClassStruct: ", GUILoggerBusinessProperty.PRODUCT_DEFINITION, newProductClassStruct);
            }

            // setReportingClasses=true, setProducts=false, fireEvent=true
            this.setProductClassStruct(newProductClassStruct, true, false, true);

            firePropertyChange(SAVED_EVENT,null,this);
        }
    }

    public ValidationResult validateData()
    {
        // call methods to verify fields, if necessary
        /*
        ValidationResult result = validate???();
        if ( result.isValid() )
        {
            result = validate???();
        }
        return result;
        */
        ValidationResult result = new ValidationResultImpl();
        result.setErrorCode(ValidationErrorCodes.NO_ERROR);
        return result;
    }

    public void refreshData() throws UserException
    {
        // if refreshData() is called for a default ProductClass (when classKey = 0) then the user has cancelled creation of a new ProductClass
        if(getProductClassDetail().getClassKey() != com.cboe.idl.cmiConstants.ProductClass.DEFAULT_CLASS_KEY)
        {
            ProductClassStruct pcStruct = SystemAdminAPIFactory.find().getProductClassByKey(getProductClassDetail().getClassKey(), true, false, false);
GUILoggerHome.find().debug("Reloaded ProductClassStruct...", GUILoggerBusinessProperty.PRODUCT_DEFINITION, pcStruct);
            ProductClassModel newProductClassModel = ProductClassModelFactory.create(pcStruct);
            this.setProductClassDetail(newProductClassModel, false);
            this.setReportingClasses(newProductClassModel.getReportingClasses());
            firePropertyChange(RELOADED_EVENT,null,this);
        }
        else
        {
            firePropertyChange(DELETED_EVENT, this, null);
        }
    }

    // these used to be in ProductClassDetailImpl
    public String getSessionCode()
    {
        return getProductClassStruct().sessionCode;
    }
    public Product[] getProducts()
    {
        return this.products;
    }
    public ReportingClass[] getReportingClasses()
    {
        return this.reportingClasses;
    }


    // convenience methods
    public void handleUserException(UserException e)
    {
        DefaultExceptionHandlerHome.find().process(e);
    }

    public short getSettlementType()
    {
        return getProductClassStruct().settlementType;
    }

    public void setSettlementType(short settlementType)
    {
        if(settlementType != getSettlementType())
        {
            if(SettlementTypes.validateSettlementType(settlementType))
            {
                short oldValue = getSettlementType();
                getProductClassStruct().settlementType = settlementType;
                setModified(true);
                firePropertyChange(DATA_CHANGE_EVENT, oldValue, settlementType);
            }
            else
            {
                throw new IllegalArgumentException("settlementType was not a valid type:" + settlementType);
            }
        }
    }

    /**
     * Determines if this ProductClass is invalid, either it has been marked inactive or has been removed from the
     * system, but some data structures still reference the classkey
     */
    public boolean isValid()
    {
        return true;
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


}
