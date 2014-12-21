package com.cboe.routing;

import java.util.HashSet;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiSession.SessionProductStruct;
import com.cboe.idl.cmiSession.SessionStrategyStruct;
import com.cboe.idl.cmiStrategy.StrategyLegStruct;
import com.cboe.idl.cmiStrategy.StrategyStruct;
import com.cboe.idl.product.GroupStruct;
import com.cboe.idl.product.GroupTypeStruct;
import com.cboe.idl.product.ProductClassStruct;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.FatalFoundationFrameworkException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.businessServices.ProductQueryService;
import com.cboe.interfaces.businessServices.TradingSessionService;
import com.cboe.interfaces.domain.instrumentedChannel.InstrumentedEventChannelListener;
import com.cboe.interfaces.internalBusinessServices.ProductConfigurationService;
import com.cboe.interfaces.internalBusinessServices.ProductRoutingService;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.domain.util.SessionStrategyStructHelper;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.ExceptionBuilder;
import com.cboe.util.MathExtensions;
import com.cboe.util.channel.ChannelEvent;

/**
 * This class is responsible for locating a route ( business server ) by
 * productkey or classkey.  The data is maintained in two name/value pair collections
 * 1. businessServer to product code map
 * 2. businessServer to class code map
 * These collections are built during initialization, when each product configuration service
 * offered by the Corba trader service is queried.
 * The ProductRoutingService is also a ProductQueryService consumer for potential updates. Any time
 * an update notification is received, the ProductRoutingService will update its internal map/collections
 * by querying each ProductConfigurationService known.
 *
 * @date November 17, 2008
 **/

public class ProductRoutingServiceClientImpl extends BObject implements ProductRoutingService, InstrumentedEventChannelListener
{
	private volatile Vector<ProductStruct> pendingAdds = null; // to handle and addProduct() calls made during a rebuild
	private volatile ConcurrentHashMap<Integer, HashSet<String>> productRoute;
	private volatile ConcurrentHashMap<Integer, HashSet<String>> productClassRoute;
	protected ProductConfigurationService productConfigurationService;
	private final String VALID_GROUP_TYPE = "ValidGroupType";
		
    // ProductQueryService implementation for use in building routing tables only
    protected ProductQueryService productQueryStartupService;

	/**
     * Default Constructor
     **/
    public	ProductRoutingServiceClientImpl()
    {
    	productRoute = new ConcurrentHashMap<Integer, HashSet<String>>();
    	productClassRoute = new ConcurrentHashMap<Integer, HashSet<String>>();
    }
    
    public void channelUpdate(ChannelEvent event)
    {
        ChannelKey channelKey = (ChannelKey)event.getChannel();
        
        if(Log.isDebugOn())
        {
            Log.debug( this, "ProductRoutingServiceClientImpl got channelUpdate event");
            Log.debug(this, "ProductRoutingServiceClientImpl.channelUpdate received event " + channelKey + ":" + event.getEventData());
        }
        
        switch (channelKey.channelType)
        {            
            case ChannelType.PQS_UPDATE_PRODUCT_BY_CLASS :
                this.addProduct((ProductStruct) event.getEventData());     
            break;
            
            case ChannelType.PQS_STRATEGY_UPDATE :
                addStrategy((StrategyStruct) event.getEventData());
            break;
            
            default:
                StringBuilder sb = new StringBuilder("ProductRoutingServiceClientImpl.channelUpdate does not handle this channel: ")
                        .append(channelKey.channelType);
                Log.information(this, sb.toString());
            break;
        }        
    }
    public void queueInstrumentationInitiated()
    { }
 
    /**
     * Populates the appropriate hash table with the name value pairs
     * Collection Populated : Service to BusinessServer Mapping
     * @param data - A sequence of ProductClassStructs ( Contains Product data )
     * @param route - The logical route name for a service
    **/
    private void createRouteMapForClass(ConcurrentHashMap<Integer, HashSet<String>> productClassRouteParam, ConcurrentHashMap<Integer, HashSet<String>> productRouteParam, ProductClassStruct data, String route )
    {
        Integer classKey = data.info.classKey;
        if ( productClassRouteParam.get(classKey) == null)
        {
            productClassRouteParam.put(classKey, new HashSet<String>());
        }
        productClassRouteParam.get(classKey).add(route);
    
    	for ( int j=0; j < data.products.length; j++ )	
    	{
            Integer productKey = data.products[j].productKeys.productKey;
            if ( productRouteParam.get(productKey) == null)
            {
                productRouteParam.put(productKey, new HashSet<String>());
            }
    		productRouteParam.get(productKey).add(route );
    	}
    	if (Log.isDebugOn())
    	{
    	    Log.debug( this,"* Adding Class Element: " + Integer.toString( data.info.classKey ) );
    	}     	
    }
    
    /**
     * Implementation method - Retrieves the Route name given a class key
     * @param classKey - Class code
     * @return String - Logical business server
     **/
    public HashSet <String> getRouteNamesByClass( int classKey )
    		throws DataValidationException
    {
    	HashSet<String> routes = productClassRoute.get( Integer.valueOf( classKey ) );
    	if ( routes  == null )
    	{
    		Log.alarm( this, "Client Route not found for class (" + classKey + ")" );
    		throw ExceptionBuilder.dataValidationException("Class Key (" + Integer.toString( classKey ) + ") not found", DataValidationCodes.INVALID_PRODUCT );
    	}
    	return routes;
    }
    /**
     * Implementation method - Retrieves the Route name given a product key
     * @param productKey - Product code
     * @return String - Logical business server name
    **/
    
    public HashSet <String> getRouteNamesByProduct(int productKey )
    		throws DataValidationException
    {
        HashSet <String> routes = productRoute.get( Integer.valueOf( productKey ) );
        if ( routes == null )
        {
            if (Log.isDebugOn()) 
            {
                Log.debug(this, "Client Route not found for productKey=" + productKey);
            }
            throw ExceptionBuilder.dataValidationException("Product Key (" + Integer.toString( productKey ) + ") not found", DataValidationCodes.INVALID_PRODUCT );
        }
    
        return routes;
    }
    
    /**
     * The initialize() method is called during startup.
     * A successful completion of this method will cause the PRS to
     * transition to a ready state At this point the PRS will query the TraderService for all known
     * PCSs and then recursively go to every PCS and get its configuration
    **/
    public void initialize()
    {
        Log.information( this, "Initializing ProductRoutingServiceClientImpl");
        productConfigurationService = ServicesHelper.getProductConfigurationService();
        //productQueryService = ServicesHelper.getProductQueryService();
        productQueryStartupService = ServicesHelper.getProductQueryStartupService();
        resetCache();
        Log.information( this, "ProductRoutingServiceImpl Initialization complete");
    }
    
    /**
     * Queries each Product Configuration Service to determine
     * its valid products for a type 'Prices' This can be sequential or in parallel
     * Note: Need to make sure the the group type is configured as a property
     * @param ProductConfigurationService svc
    **/
    private	void buildRoutingTables(ConcurrentHashMap<Integer, HashSet<String>> productClassRouteParam, ConcurrentHashMap<Integer, HashSet<String>> productRouteParam)
    {
    	int validGroupType=0;
    	GroupStruct[] validGroups = null;
    	if (Log.isDebugOn())
        {
    	    Log.debug( this,"Querying Product Configuration Service" );
        }
    	try	
    	{    	    
    		validGroupType = getValidGroupType();
    	    if (Log.isDebugOn())
    	    {
    	        Log.debug( this, "Locating groups of group type (" + validGroupType + ")" );  
    	    }    		
    		validGroups = productConfigurationService.getGroupsByType( validGroupType );
            StringBuilder ugn = new StringBuilder(60);
            for ( int j=0; j < validGroups.length; j++ )
    		{
                int[] tmpClassList = productConfigurationService.getProductClassesForGroupByKey( validGroups[j].groupKey );
                buildRoutingTablesForClasses(productClassRouteParam, productRouteParam, tmpClassList, validGroups[j].groupName);
                if (Log.isDebugOn())
                {
                    Log.debug( this, "Route Map for " + validGroups[j].groupName + " created" );
                }    
                // build routing entries for underlying group that will be paired with process group
                ugn.setLength(0);
                ugn.append(validGroups[j].groupName).append(ProductConfigurationService.UNDERLYING_GROUP_SUFFIX);
                String underlyingGroupName = ugn.toString();
                int[] underlyingClassList = productConfigurationService.getProductClassesForGroup(underlyingGroupName);
                buildRoutingTablesForClasses(productClassRouteParam, productRouteParam, underlyingClassList, underlyingGroupName);
                if (Log.isDebugOn())
                {
                    Log.debug( this, "Route Map for " + underlyingGroupName + " created" ); 
                }            	
    		}  
    	}
        catch ( org.omg.CORBA.SystemException e)
    	{
    	    throw new FatalFoundationFrameworkException(e,"Failed to build routing tables for group type " + validGroupType);
    	}
    	catch (Exception e)
    	{
    	    throw new FatalFoundationFrameworkException(e,"Failed to build routing tables for group type " + validGroupType);
    	}
    }
    
    /**
     * Build routing tables for a collection of classes
     **/
    private void buildRoutingTablesForClasses(ConcurrentHashMap<Integer, HashSet<String>> productClassRouteParam, ConcurrentHashMap<Integer, HashSet<String>> productRouteParam, int[] classKeys, String routeName)
    {
        for ( int m=0; m < classKeys.length; m++)
        {
              ProductClassStruct productClass = getProductClassForKey( classKeys[m] );
        	  if ( productClass != null )	
        	  {
                   createRouteMapForClass(productClassRouteParam, productRouteParam, productClass, routeName );    			
        	  }
        }
     }
    
    /**
     *	Get the Configured group type key from the property
     *	@return The group type key
     **/
    private int getValidGroupType() throws  DataValidationException, SystemException
    {
        int groupTypeKey = -1;
        
        try 
        {
            GroupTypeStruct[] validGroupTypes = productConfigurationService.getGroupTypes();
            
            String groupName = System.getProperty(VALID_GROUP_TYPE);
            
            if( groupName == null || groupName.trim().length()== 0 )
            {
                throw new FatalFoundationFrameworkException("Failed to get System property VALID_GROUP_TYPE");
            }
            
            for ( GroupTypeStruct validGroupType : validGroupTypes )
            {                
                if ( validGroupType.groupTypeDescription.equals(groupName))
                {
                    groupTypeKey = validGroupType.groupType;                                       
                }               
            }            
            if ( groupTypeKey == -1 )
            {
                throw new FatalFoundationFrameworkException("Failed to get a valid group type needed for Product Routing Service");
            }
        }
        catch ( org.omg.CORBA.SystemException e)
        {
            throw new FatalFoundationFrameworkException(e,"Failed to get Valid Group Type key from name " + VALID_GROUP_TYPE);
        }
        catch (Exception e)
        {
            throw new FatalFoundationFrameworkException(e,"Failed to get Valid Group Type key from name " + VALID_GROUP_TYPE);
        }    
        
        return groupTypeKey;
    }
    
    /**
     *	Queries the ProductQueryService for product level information
     *	@return  A sequence of products
     **/
    private	ProductClassStruct getProductClassForKey( int classKey )
    {
        if (Log.isDebugOn())
        {
            Log.debug( this,"* Querying ProductQueryStartupService for class: " + classKey );
        }    	
    	ProductClassStruct productClass = null;
    	try
    	{    	    
    		productClass = productQueryStartupService.getProductClassByKey( classKey, false, true, false );
    	}
    	catch ( Exception e )
    	{
    	    if (Log.isDebugOn())
    	    {
    	        Log.debug( this, "* Error on getting ProductClass for key " + classKey ); 
    	    }            
    		Log.exception( this,"* Error on getting ProductClass for key " + classKey, e );
    	}
    	return productClass;
    }

    /**
     * Add a product to the product route map
     */
    private synchronized void addProduct(ProductStruct product)
    {
        if (pendingAdds != null)
        {
            StringBuilder defer = new StringBuilder(85);
            defer.append("addProduct: rebuild in progress; defer adding product ")
                 .append(product.productKeys.productKey).append(" until after rebuild.");
            Log.information(this, defer.toString());
            pendingAdds.add(product);
            return;
        }
        
        if (Log.isDebugOn())
        {
            Log.debug(this, "addProduct called productKey=" + product.productKeys.productKey); 
        }        
        int productKey = product.productKeys.productKey;
        int classKey = product.productKeys.classKey;
        if (productRoute.containsKey(Integer.valueOf(productKey)))
        {
            if (Log.isDebugOn()) 
            {
                Log.debug(this, "Product " + classKey + "-" + productKey + " is already in routing table. Igonre it.");
            }
            return;
        }

        try
        {
            HashSet<String> routeNames = (HashSet<String>) getRouteNamesByClass(classKey);
            StringBuilder added = new StringBuilder(65);
            added.append("New product ").append(classKey).append("-").append(productKey).append(" is added to the routing table");
            Log.information( this, added.toString());
            if (Log.isDebugOn()) 
            {
                Log.debug( this, added.toString());
            }
            productRoute.put( Integer.valueOf(productKey), routeNames);
        }
        catch (DataValidationException e)
        {
            Log.alarm(this, "No routeName defined for class " + classKey);
        }
    }

    /**
     * Add a strategy to the product route map
     */
    private void addStrategy(StrategyStruct strategy)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "====addStrategy called");
        }        
        ProductStruct product = strategy.product;
        this.addProduct(product);
    }

    /**
     *  Rebuild routing tables.
     */
    public void resetCache()
    {
        // Rebuild the cache by building to temporary maps, then 'instantly' setting the new map.
        //
        final int hashBase = 5000; // roughly twice the number of classes
        ConcurrentHashMap<Integer, HashSet<String>> productClassRouteParam = new ConcurrentHashMap<Integer, HashSet<String>>(MathExtensions.nextPrime(hashBase));
        ConcurrentHashMap<Integer, HashSet<String>> productRouteParam = new ConcurrentHashMap<Integer, HashSet<String>>(MathExtensions.nextPrime(hashBase*50)); // roughly avg of 50 products per class
        pendingAdds = new Vector<ProductStruct>();
        buildRoutingTables(productClassRouteParam, productRouteParam);

        synchronized (this)
        {
            Vector<ProductStruct> addThese = pendingAdds;
            pendingAdds = null; // set to null here, since addProduct checks for pendingAdds==null.
            StringBuilder processing = new StringBuilder(75);
            for (int i=0; i < addThese.size(); i++)
            {
                ProductStruct prodStruct = (ProductStruct)addThese.get(i);
                processing.setLength(0);
                processing.append("Processing addProduct(").append(prodStruct.productKeys.productKey)
                          .append(" that was delayed due to cache rebuild");
                Log.information(this, processing.toString());
                addProduct(prodStruct);
            }
            productClassRoute = productClassRouteParam;
            productRoute = productRouteParam;
        }
    }

    /**
     * Implementation method - Update strategy product returned from global
     * product service.
     * @param sessionStrategy - strategy product to be added
     * in local hash set.
     **/
    public void saveStrategyProduct( SessionStrategyStruct sessionStrategy )
    {
        if (Log.isDebugOn()) 
        {
            Log.debug(this, "saveStrategyProduct called");
        }
        StrategyStruct strategy = SessionStrategyStructHelper.toStrategyStruct(sessionStrategy);
        this.addStrategy( strategy );
    }

    /**
     * Request product from Global Server and add it to the routing table
     * @param productKey
     */
    public void requestProductBySessionForKey(String sessionName, int productKey)
    {
        if (Log.isDebugOn()) 
        {
            Log.debug(this, "requestProductBySessionForKey for sessionName=" + sessionName + " productKey=" + productKey);
        }
        TradingSessionService tradingSessionService = ServicesHelper.getTradingSessionService();
        try 
        {
            SessionProductStruct sessionProduct = tradingSessionService.getProductBySessionForKey(sessionName, productKey);
            this.addProduct(sessionProduct.productStruct);
            if (Log.isDebugOn()) 
            {
                Log.debug(this, "Completed requestProductBySessionForKey ");
            }
        }
        catch (Exception e) 
        {
            if (Log.isDebugOn())
            {
                Log.debug(this, "Failed to requestProductForKey sessionName="
                        + sessionName + " productKey=" + productKey);
            }            
            Log.exception(this, e);
        }
    }

    /**
     * Print strategy struct for convenient debugging.
     */
    private void printStrategy(StrategyStruct aStrategy)
    {
        StrategyLegStruct[] legs = aStrategy.strategyLegs;
        if (Log.isDebugOn())
        {
            Log.debug(this, "===== aStrategy product=" + aStrategy.product.productKeys.productKey
                    + " classKey=" + aStrategy.product.productKeys.classKey
                    + " strategyType=" + aStrategy.strategyType);
        } 
        if (Log.isDebugOn())
        {
            for (int i=0; i < legs.length; i++)
            {
                Log.debug(this, "===== product-ratio-side Leg[" + i + "]=" + legs[i].product + "-" + legs[i].ratioQuantity + "-" + legs[i].side);
            }
        }        
    }

    /**
     * Print strategy struct for convenient debugging.
     */
    private void printSessionProduct(SessionProductStruct aSessionProduct)
    {
        ProductStruct product = aSessionProduct.productStruct;
        String sessionName = aSessionProduct.sessionName;
        if (Log.isDebugOn())
        {
            Log.debug(this, "===== sessionName=" + sessionName
                    + " product=" + product.productKeys.productKey
                    + " classKey=" + product.productKeys.classKey);
        }        
    } 
    
}//EOF
