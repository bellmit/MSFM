package com.cboe.application.product;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.shared.consumer.UserSessionLogoutProcessorFactory;
import com.cboe.application.shared.consumer.UserSessionLogoutProcessor;
import com.cboe.domain.logout.LogoutServiceFactory;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiStrategy.StrategyStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.interfaces.application.*;
import com.cboe.interfaces.businessServices.ProductQueryService;
import com.cboe.util.event.EventChannelAdapterFactory;

/**
 * ProductQueryManagerImpl.  Provides PQ interface functionality on a user session basis.
 * All calls are delegated to the sessionless ProductQueryServiceAdapter. 
 */ 

public class ProductQueryManagerImpl extends BObject implements ProductQueryManager, UserSessionLogoutCollector
{
    private ProductQueryServiceAdapter serviceAdapter;
    
    private SessionManager currentSession;
    private UserSessionLogoutProcessor  logoutProcessor;
    
    public ProductQueryManagerImpl()
    {
        super();
    }

    protected void setSessionManager(SessionManager session)
    {
        currentSession = session;
        logoutProcessor = UserSessionLogoutProcessorFactory.create(this);
        EventChannelAdapterFactory.find().addChannelListener(this, logoutProcessor, session);
        LogoutServiceFactory.find().addLogoutListener(session, this);
    }

    public void acceptUserSessionLogout()
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptUserSessionLogout for " + currentSession);
        }
        EventChannelAdapterFactory.find().removeListenerGroup(this);
        LogoutServiceFactory.find().logoutComplete(currentSession,this);

        logoutProcessor.setParent(null);
        logoutProcessor = null;

        serviceAdapter = null;
        currentSession = null;
    }

  /////////////// IDL exported methods ////////////////////////////////////

    public StrategyStruct[] getStrategiesByComponent(int componentProductKey)
       throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
//        Log.debug(this, "calling getStrategiesByComponent for " + currentSession);
        return getServiceAdapter().getStrategiesByComponent(componentProductKey);
    }

    /**
     * Gets pending price adjustments for all products based on the
     * given sequence of class keys.
     *
     * @param classKey the class key to retrieve pending adjustments for.
     * @param includeProducts true to include products; false otherwise.
     *
     * @return a sequence of pending adjustment info structs.
     * @exception SystemException System Error
     * @exception CommunicationException Communication Error
     * @exception AuthorizationException Authorization Error
     * @exception DataValidationException Data Validation Error
     *
     * @author Derek T. Chambers-Boucher
     */
    public PendingAdjustmentStruct[] getPendingAdjustments(int classKey, boolean includeProducts)
                  throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling getPendingAdjustments for " + currentSession);
        }
        return getServiceAdapter().getPendingAdjustments(classKey, includeProducts);
    }

    /**
     * Gets pending price adjusted products for the given sequence of classes.
     *
     * @param classKey the class key to retrieve pending adjustment products for.
     *
     * @return a sequence of pending name structs.
     * @exception SystemException System Error
     * @exception CommunicationException Communication Error
     * @exception AuthorizationException Authorization Error
     * @exception DataValidationException Data Validation Error
     *
     * @author Derek T. Chambers-Boucher
     */
    public PendingNameStruct[] getPendingAdjustmentProducts(int classKey)
                   throws SystemException, CommunicationException, AuthorizationException, DataValidationException
   {
        if (Log.isDebugOn())
        {
           Log.debug(this, "calling getPendingAdjustmentProducts for " + currentSession);
        }
        return getServiceAdapter().getPendingAdjustmentProducts(classKey);
    }

    /**
     * Gets pending price adjustments for all products.
     *
     * @return a sequence of pending adjustment info structs.
     * @exception SystemException System Error
     * @exception CommunicationException Communication Error
     * @exception AuthorizationException Authorization Error
     *
     * @author Derek T. Chambers-Boucher
     */
    public PendingAdjustmentStruct[] getAllPendingAdjustments()
                  throws SystemException, CommunicationException, AuthorizationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling getAllPendingAdjustments for " + currentSession);
        }
        return getServiceAdapter().getAllPendingAdjustments();
    }
    
    /**
     * Retrieves all product types.
     * @return array of {@link ProductTypeStruct} objects representing the product types supported by CBOEdirect.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     */
    public ProductTypeStruct[] getProductTypes() throws SystemException, CommunicationException, AuthorizationException
    {
        return getServiceAdapter().getProductTypes();
    }

    /**
     * Retrieves all classes within a specific product type.
     * @param productType retrieve the valid classes for this product type
     * @return array of {@link ClassStruct} objects representing the classes within this product type
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public ClassStruct[] getProductClasses(short productType)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
//        Log.debug(this, "calling getProductClasses for " + productType);
        return getServiceAdapter().getProductClasses(productType);
    }

    /**
     * Retrieves all products within a specific class.
     * @param classKey retrieve the valid products for this class
     * @return array of {@link ProductStruct} objects representing the products within this class
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public ProductStruct[] getProductsByClass(int classKey)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        return getServiceAdapter().getProductsByClass(classKey);
    }

    /**
     * Determines whether a given product name is valid. Rather than use the <code>isValidProductName</code>
     * method of either {@link ProductQueryService} or {@link ProductQueryManagerImpl}, we check for a failure
     * on <code>getProductByName</code>. This is better because it forces a cache update if the cache can't
     * find a product but it does exist on the server.
     * @param productName name of the product to validate
     * @return <code>true</code> if this is the name of a valid product, <code>false</code> if not
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public boolean isValidProductName(ProductNameStruct productName)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        return getServiceAdapter().isValidProductName(productName);
    }

    /**
     * Retrieves a product using the product name.
     * @param productName name of the product to retrieve
     * @return {@link ProductStruct} object representing the named product
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotFoundException
     */
    public ProductStruct getProductByName(ProductNameStruct productName)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        //Log.debug(this, "calling getProductByName for " + productName.productSymbol);
        return getServiceAdapter().getProductByName(productName);
    }

    /**
     * Retrieves a product name of a product with the given product key. If we can't find that product using
     * the key, we throw a NotFoundException.
     * @param productKey unique key identifying a product
     * @return {@link ProductNameStruct} object containing the given product's name
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotFoundException
     */
    public ProductNameStruct getProductNameStruct(int productKey)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
//        Log.debug(this, "calling getProductNameStruct for productkey = " + productKey);
        
        return getServiceAdapter().getProductNameStruct(productKey);
   }


    /**
     * Retrieves a strategy based on its product key (strategies are also products).
     * @param productKey unique key identifying a product/strategy
     * @return {@link StrategyStruct} object representing the strategy
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotFoundException
     */
    public StrategyStruct getStrategyByKey(int productKey)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        return getServiceAdapter().getStrategyByKey(productKey);
    }

    /**
     * Retrieves a class based on its unique class key.
     * @param classKey unique key identifying a class
     * @return {@link ClassStruct} object representing the class
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotFoundException
     */
    public ClassStruct getClassByKey(int classKey)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
//        Log.debug(this, "calling getClassByKey for " + classKey);
        return getServiceAdapter().getClassByKey(classKey);
    }

    /**
     * Retrieves a product based on its unique product key.
     * @param productKey unique key identifying a product
     * @return {@link ProductStruct} object representing the product
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotFoundException
     */
    public ProductStruct getProductByKey(int productKey)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
//        Log.debug(this, "calling getProductByKey for " + productKey);
         return getServiceAdapter().getProductByKey(productKey);
    }

    /**
     * Retrieves a class based on product type and its ticker symbol. The "AOL" class ticker may
     * exist in several different asset categories (product types) - options, equities, futures.
     * @param productType identifies a specific asset category (stock, option, future, strategy, etc.)
     * @param classSymbol string by which all products in this class are generically known ("AOL", "IBM", etc.)
     * @return {@link ClassStruct} object representing the class
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotFoundException
     */
    public ClassStruct getClassBySymbol(short productType, String classSymbol)
           throws SystemException, CommunicationException, DataValidationException, AuthorizationException, NotFoundException
    {
//        Log.debug(this, "calling getClassBySymbol for " + classSymbol + "     " + productType);
        
        return getServiceAdapter().getClassBySymbol(productType, classSymbol);
    }

    /**
     * Retrieves all strategies within a specific class.
     * @param classKey retrieve the valid strategies for this class
     * @return an array of {@link StrategyStruct} objects representing the strategies within this class
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public StrategyStruct[] getStrategiesByClass(int classKey)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling getStrategiesByClass for " + classKey);
        }
        return getServiceAdapter().getStrategiesByClass(classKey);
    }
    
    private ProductQueryServiceAdapter getServiceAdapter()
    {
        if(serviceAdapter == null)
        {
            serviceAdapter = ServicesHelper.getProductQueryServiceAdapter();
        }
        return serviceAdapter;
    }

	public ProductStruct[] getProductsForReportingClassSymbol(String reportingClassSymbol, short type) throws SystemException, CommunicationException, AuthorizationException, DataValidationException {
		if (Log.isDebugOn())
        {
            Log.debug(this, "calling getProductsForReportingClassSymbol for " + reportingClassSymbol + " type:" + type);
        }
		return getServiceAdapter().getProductsForReportingClassSymbol(reportingClassSymbol, type);
	}
}
