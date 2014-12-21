//
// -----------------------------------------------------------------------------------
// Source file: ProductQueryAPI.java
//
// PACKAGE: com.cboe.interfaces.presentation.api;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.api;

import java.util.Comparator;
import java.util.Calendar;
import java.util.List;

import com.cboe.idl.cmiProduct.ProductNameStruct;

import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;

import com.cboe.util.event.EventChannelListener;

import com.cboe.interfaces.presentation.product.ProductClass;
import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.interfaces.presentation.product.ProductAdjustmentContainer;
import com.cboe.interfaces.presentation.product.PendingNameContainer;

import com.cboe.interfaces.presentation.product.SessionStrategy;
import com.cboe.interfaces.presentation.product.Strategy;
import com.cboe.interfaces.presentation.product.ReportingClass;
import com.cboe.interfaces.presentation.product.SessionReportingClass;
import com.cboe.interfaces.presentation.product.ProductType;
import com.cboe.interfaces.domain.Price;
import org.omg.CORBA.UserException;

public interface ProductQueryAPI
{
    public ProductType[] getAllProductTypes()
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public ProductType getProductType(short type)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;

    public ProductClass[] getAllClassesForType(short productType)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public Product[] getAllProductsForClass(int classKey, boolean activeOnly)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * A convenience method to get all SessionProductClass'es for all product types.
     * @param sessionName to get all classes for all types
     * @return and array of SessionProductClass that are contained by passed sessionName for all product types
     */
    public SessionProductClass[] getAllProductClassesForSession(String sessionName)
            throws SystemException, DataValidationException, CommunicationException, AuthorizationException;

    public Product[] getAllProductsByClass(int classKey, boolean activeOnly,EventChannelListener clientListener)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void subscribeProductsByClass(int classKey, EventChannelListener clientListener)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void unsubscribeProductsByClass(int classKey, EventChannelListener clientListener)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    
    public SessionProductClass[] getProductClassesForSession(String sessionName, short productType, EventChannelListener clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public ProductType[] getProductTypesForSession(String sessionName)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public SessionProduct[] getProductsForSession(String sessionName, int classKey, EventChannelListener clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void subscribeProductsForSession(String sessionName, int classKey, EventChannelListener clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void subscribeSessionClassByType(String sessionName, short productKey, EventChannelListener clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void unsubscribeClassesByTypeForSession(String sessionName, short productType, EventChannelListener clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void unsubscribeProductsByClassForSession(String sessionName, int classKey, EventChannelListener clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void unsubscribeStrategiesByClassForSession(String sessionName, int classKey, EventChannelListener clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void unsubscribeTradingSessionStatus(EventChannelListener clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public SessionProductClass getClassByKeyForSession(String sessionName, int classKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;

    public SessionProduct getProductByKeyForSession(String sessionName, int productKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;

    public SessionProduct getProductByNameForSession(String sessionName, ProductNameStruct productName)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;

    public SessionProductClass getClassBySymbolForSession(String sessionName, short productType, String className)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;

    public SessionProductClass getAllSelectedSessionProductClass();

    public SessionProduct getAllSelectedSessionProduct();

    public SessionProductClass getAllSelectedSessionProductClass(String sessionName);

    public SessionProduct getAllSelectedSessionProduct(String sessionName);

    public ProductClass getAllSelectedProductClass();

    public Product getAllSelectedProduct();

    public SessionProductClass getDefaultSessionProductClass();

    public SessionProduct getDefaultSessionProduct();

    public SessionProductClass getDefaultSessionProductClass(String sessionName);

    public SessionProduct getDefaultSessionProduct(String sessionName);

    public SessionStrategy getDefaultSessionStrategy();

    public ProductClass getDefaultProductClass();

    public Product getDefaultProduct();

    public Strategy getDefaultStrategy();

    /**
     * Check to see if the given product name is valid.  The product name
     * should be formatted as follows: XXXXX
     *
     * @return true if valid; false if invalid.
     * @param productName the ProductNameStruct to verify.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public boolean isValidProductName(ProductNameStruct productName)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Get full product information by name.  The product name should
     * be formatted as follows: XXXXX
     *
     * @return The Product for the given ProductNameStruct.
     * @param productName the ProductNameStruct
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotFoundException
     */
    public Product getProductByName(ProductNameStruct productName)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;

    /**
     * Gets pending price adjustments for all products.
     *
     * @return a sequence of pending adjustment info structs.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     */
    public ProductAdjustmentContainer[] getAllPendingAdjustments()
           throws SystemException, CommunicationException, AuthorizationException;

    /**
     * Gets pending price adjustments for all products based on the
     * given sequence of class keys.
     *
     * @param classKeys the sequence of class keys to retrieve pending adjustments for.
     * @param includeProducts true to include products; false otherwise.
     * @return a sequence of pending adjustment info structs.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public ProductAdjustmentContainer[] getPendingAdjustments(int classKey, boolean includeProducts)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Gets pending price adjusted products for the given sequence of classes.
     *
     * @param classKeys the sequence of class keys to retrieve pending adjustment products for.
     * @return a sequence of pending name structs.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public PendingNameContainer[] getPendingAdjustmentProducts(int classKey)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Gets product strategy for the given product.
     *
     * @param productKey the product key for the strategy to be retrieved.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotFoundException
     * @return StrategyStruct Strategy struct.
     */
    public Strategy getStrategyByKey(int productKey)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;

    /**
     * Gets the reporting class with the passed key
     * @param reportingClassKey to get reporting class for
     * @return reporting class with matching key
     * @exception NotFoundException a reporting class with the passed key could not be found
     */
    public ReportingClass getReportingClassByKey(int reportingClassKey) throws NotFoundException;

    /**
     * Gets the reporting class with the passed symbol
     * @param symbol to get reporting class for
     * @return reporting class with matching symbol
     * @exception NotFoundException a reporting class with the passed symbol could not be found
     */
    public ReportingClass getReportingClassBySymbol(String symbol) throws NotFoundException;

    /**
     * Get the product with the reporting class symbol, month opra code and price opra code
     * @param reportingClassSymbol
     * @param monthCode
     * @param priceCode
     * @return product
     */
    public Product getProductBySymbolAndOpraCode(String reportingClassSymbol, char monthCode, char priceCode) 
            throws NotFoundException, UserException;

    /**
     * Gets the session reporting class with the passed key for the passed session
     * @param reportingClassKey to get reporting class for
     * @param sessionName to get reporting class for
     * @return reporting class with matching key
     * @exception NotFoundException a reporting class with the passed key could not be found
     * @exception DataValidationException the passed sessionName was invalid
     */
    public SessionReportingClass getReportingClassByKeyForSession(int reportingClassKey, String sessionName)
            throws NotFoundException, DataValidationException;

    /**
     * Gets all the Products for the passed ReportingClass
     * @param reportingClass to get products for
     * @param activeOnly True to get only active classes, false to get active and inactive.
     * @return only Products that have the passed ReportingClass
     */
    public Product[] getProductsForReportingClass(ReportingClass reportingClass, boolean activeOnly)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Gets all the SessionProducts for the passed SessionReportingClass
     * @param reportingClass to get products for
     * @return only SessionProducts that have the passed ReportingClass
     */
    public SessionProduct[] getProductsForReportingClass(SessionReportingClass reportingClass)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Gets a product class by key.
     *
     * @param classKey the class
     * @param clientListener the subscribing listener
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotFoundException
     * @return the product class struct.
     */
    public ProductClass getProductClassByKey(int classKey)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;

    /**
     * Gets a product by key.
     *
     * @param productKey the product
     * @param clientListener the subscribing listener
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotFoundException
     * @return the product class struct.
     */
    public Product getProductByKey(int productKey)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;

    /**
     * Gets a product class by type and symbol.
     *
     * @param productType the product type
     * @param className the class name
     * @param activeOnly active products only
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotFoundException
     * @return the product class struct.
     */
    public ProductClass getClassBySymbol(short productType, String className)
           throws SystemException, CommunicationException, DataValidationException, AuthorizationException, NotFoundException;

    /**
     * Gets strategies by product key that is used in the strategy legs.
     *
     * @param componentProductKey the product key to get strategies for
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @return the requested strategies.
     */
    public SessionStrategy[] getStrategiesByComponent(String sessionName, int componentProductKey)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public SessionStrategy getStrategyByKeyForSession(String sessionName, int productKey)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Gets all product classes whose reporting class symbols start with
     * the given String prefix.
     *
     * <B>NOTE:</B>  This method is a ProductQueryAPI local method.  There
     * is no CAS support for this request on the ProductQuery interface.
     *
     * @return a sequence of ProductClasss that met the selection criteria.
     * @param prefix the selection criteria all classes must start with to be included.
     * @param activeOnly set to true to receive only active classes.
     */
    public ProductClass[] getProductClassesByAlpha(String prefix);

    public boolean isStrategy(int classKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;

    public Product getProductByKeyFromCache(int productKey) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;

}
