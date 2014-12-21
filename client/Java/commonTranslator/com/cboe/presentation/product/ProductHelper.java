//
// -----------------------------------------------------------------------------------
// Source file: ProductHelper
//
// PACKAGE: com.cboe.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.product;

import org.omg.CORBA.UserException;

import com.cboe.idl.cmiSession.ProductStateStruct;
import com.cboe.idl.cmiProduct.ProductTypeStruct;
import com.cboe.idl.cmiProduct.ProductDescriptionStruct;

import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;

import com.cboe.interfaces.presentation.product.*;

import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.api.APIHome;
import com.cboe.presentation.api.ProductQueryCacheFactory;

/**
 * Provides helper methods for various product/product class helpers
 */
public class ProductHelper
{
    private final static String NO_SESSION = "No Session";

    private ProductHelper()
    {}

    public static int getHashCode(String sessionName, int key)
    {
        int hashCode;

        if(sessionName != null)
        {
            StringBuffer buff = new StringBuffer(12);
            buff.append(sessionName).append(key);
            hashCode = buff.toString().hashCode();
        }
        else
        {
            hashCode = key;
        }

        return hashCode;
    }

    public static int getHashCode(ProductStateStruct struct)
    {
        return getHashCode(struct.sessionName, struct.productKeys.productKey);
    }

    /**
     * Gets the Product for the passed productKey.
     * @param productKey to get Product for.
     * @return Struct obtained for productKey or null if not found or exception occured.
     */
    static public Product getProduct(int productKey)
    {
        Product aProduct = null;
        try
        {
            aProduct = APIHome.findProductQueryAPI().getProductByKey(productKey);
        }
        catch( UserException e )
        {
            GUILoggerHome.find().exception(e);
        }
        return aProduct;
    }

    /**
     * Gets the ProductClass for the passed classKey.
     * @param classKey int to get ProductClass for.
     * @return ProductClass
     */
    static public ProductClass getProductClass(int classKey)
    {
        ProductClass pClass = null;
        try
        {
            pClass = APIHome.findProductQueryAPI().getProductClassByKey(classKey);
        }
        catch( UserException e )
        {
            GUILoggerHome.find().exception(e);
        }
        return pClass;
    }

    static public SessionProductClass getSessionProductClass(String sessionName, int classKey)
    {
        SessionProductClass aProductClass = null;
        try
        {
            aProductClass = APIHome.findProductQueryAPI().getClassByKeyForSession(sessionName, classKey);
        }
        catch( UserException e )
        {
            GUILoggerHome.find().exception(e);
        }
        return aProductClass;
    }

    /**
     * Gets the ProductClass for the passed classKey
     * @param classKey
     * @return ProductClass is returned if found, else InvalidProductClassImpl
     * @throws SystemException
     * @throws DataValidationException
     * @throws CommunicationException
     * @throws AuthorizationException
     */
    static public ProductClass getProductClassCheckInvalid(int classKey)
            throws SystemException, DataValidationException, CommunicationException, AuthorizationException
    {
        ProductClass pClass;

        try
        {
            pClass = APIHome.findProductQueryAPI().getProductClassByKey(classKey);
        }
        catch (NotFoundException e)
        {
            pClass = ProductClassFactoryHome.find().createInvalid(classKey);
            ProductQueryCacheFactory.find().addClass(pClass, pClass.getProductType());
            GUILoggerHome.find().exception("Invalid ProductClass created...",e);
        }

        return pClass;
    }

    /**
     * Gets the SessionProductClass for the passed classKey and sessionName 
     * @param sessionName
     * @param classKey
     * @return SessionProductClass is returned if found, else InvalidSessionProductlmpl
     * @throws SystemException
     * @throws DataValidationException
     * @throws CommunicationException
     * @throws AuthorizationException
     */
    static public SessionProductClass getSessionProductClassCheckInvalid(String sessionName, int classKey)
            throws SystemException, DataValidationException, CommunicationException, AuthorizationException
    {
        SessionProductClass aProductClass = null;
        boolean failed = false;

        if(sessionName.equals(NO_SESSION))
        {
            failed = true;
            GUILoggerHome.find().information("ProductHelper.getSessionProductClassCheckInvalid()",
                                             GUILoggerBusinessProperty.PRODUCT_QUERY,
                                             "Invalid sessionName '" + sessionName + "' for classKey " +
                                             classKey);
        }
        else
        {
            try
            {
                aProductClass = APIHome.findProductQueryAPI().getClassByKeyForSession(sessionName, classKey);
            }
            catch(NotFoundException e)
            {
                failed = true;
                GUILoggerHome.find().exception("getSessionProductClassCheckInvalid()",e);
            }
            catch(DataValidationException e)
            {
                failed = true;
                GUILoggerHome.find().exception("getSessionProductClassCheckInvalid()", e);
            }
        }

        if (failed)  // if by session request failed then lookup sessionless
        {
            try
            {
                ProductClass pClass = APIHome.findProductQueryAPI().getProductClassByKey(classKey);

                // if found sessionless create inactive object with classStruct 
                aProductClass = SessionProductClassFactory.createInvalid(sessionName, pClass);
            }
            catch (NotFoundException e) // if not found sessionless
            {
                // create inactive with only the classkey and everything else empty
                aProductClass = SessionProductClassFactory.createInvalid(classKey);

                GUILoggerHome.find().exception("Invalid SessionProductClass created...",e);
            }
        }

        return aProductClass;
    }

    /**
     * Gets the Product for the passed productKey.
     * @param productKey to get SessionProduct for.
     * @return Struct obtained for productKey or null if not found or exception occured.
     */
    static public SessionProduct getSessionProduct(String sessionName, int productKey)
    {
        SessionProduct aProduct = null;
        try
        {
            aProduct = APIHome.findProductQueryAPI().getProductByKeyForSession(sessionName, productKey);
        }
        catch( UserException e )
        {
            GUILoggerHome.find().exception(e);
        }
        return aProduct;
    }

    /**
     * This method was created in VisualAge.
     *
     * @return com.cboe.idl.cmiProduct.ProductTypeStruct
     * @param productType int
     */
    static public ProductTypeStruct getProductTypeStruct(short productType)
    {
        ProductTypeStruct foundProductTypeStruct = null;
        
        try
        {
           ProductType  productTypeObject = APIHome.findProductQueryAPI().getProductType(productType);
            if(productTypeObject != null)
            {
                foundProductTypeStruct = productTypeObject.getProductTypeStruct();
            }
        }
        catch(UserException e)
        {
            GUILoggerHome.find().exception(e);
        }
        
        return foundProductTypeStruct;
    }

    /**
     * This method was created in VisualAge.
     *
     * @return int
     * @param aProductClass ProductClass
     */
    static public int getUnderlyingProductKeyforClass(ProductClass aProductClass)
    {
        int key = -1;
        if (aProductClass != null)
        {
            Product underlyingProduct = aProductClass.getUnderlyingProduct();
            key = underlyingProduct.getProductKeysStruct().productKey;
        }
        else
        {
            if (GUILoggerHome.find().isDebugOn())
            {
                GUILoggerHome.find().debug("In getKeyForProductClass aProductClass is null", GUILoggerBusinessProperty.PRODUCT_QUERY);
            }
        }
        return key;
    }

    /**
     * Convenience method for getting a ProductDescriptionStruct for the given Product
     * @param  product Product
     * @return ProductDescriptionStruct
     */
    public static ProductDescriptionStruct getProductDescriptionStruct(Product product)
    {
        ProductDescriptionStruct productDescriptionStruct = null;
        try
        {
            ProductClass productClass = APIHome.findProductQueryAPI().getProductClassByKey(product.getProductKeysStruct().classKey);
            productDescriptionStruct = productClass.getProductDescription();
        }
        catch( UserException e )
        {
            GUILoggerHome.find().exception(e);
        }
        return productDescriptionStruct;
    }
}