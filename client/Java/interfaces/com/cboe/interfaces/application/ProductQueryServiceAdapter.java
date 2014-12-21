package com.cboe.interfaces.application;

import com.cboe.idl.floorApplication.ProductQueryV2Operations;
import com.cboe.idl.cmiProduct.ClassStruct;
import com.cboe.exceptions.*;

public interface ProductQueryServiceAdapter extends ProductQueryV2Operations
{
    public void initializeProductCaches()
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * refreshProductCachesForClassKey -- refreshes product caches for class key
     * 
     * @param classKey
     * @throws SystemException
     * @throws CommunicationException
     * @throws AuthorizationException
     * @throws DataValidationException
     * @throws NotFoundException
     */ 
    public void refreshProductCachesForClassKey(int classKey) 
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;
    
    public void purgeAllProductCaches();

    /** If (sessionless) product cache for given class is not loaded, load it
     * so that we have product information for later messages.
     * @param classKey Class whose product information we want loaded in CAS.
     */
    public void checkProductCacheLoaded(int classKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * method to attempt Class Retrieval from Cache only
     */

    public ClassStruct getClassBySymbolFromCache(short productType, String classSymbol)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;

}
