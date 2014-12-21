package com.cboe.domain.marketDataSummary;
import com.cboe.domain.marketDataSummary.SummaryRecapImpl;
import com.cboe.domain.util.PriceSqlType;
import com.cboe.domain.util.SqlScalarTypeInitializer;
import com.cboe.idl.cmiMarketData.RecapStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.PersistentBObject;
import com.cboe.infrastructureServices.persistenceService.*;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.marketData.Recap;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.marketDataSummary.MarketDataSummary;
import java.lang.reflect.*;
import java.util.*;
/**
 *  A persistent implementation of <code>MarketDataSummary</code>.
 *
 *@author  David Hoag
 *@created  September 19, 2001
 */
public class MarketDataSummaryImpl extends PersistentBObject implements MarketDataSummary
{
    /*
     *  JavaGrinder variables
     */
    static Field _sessionName;
    static Field _recap;
    static Field _openInterest;
    static Field _productKey;
    static Field _classKey;
    static Field _underlyingPrice;
    static Field _productType;
    static Vector classDescriptor;
    protected short productType;
    /**
     *  Session name the market data is assiociated with
     */
    private String sessionName;
    /**
     *  Key of the product.
     */
    private int productKey;
    /**
     *  Key of the product's class.
     */
    private int classKey;
    private int openInterest;
    /**
     *  Reference to Underlying Recap.
     */
    private SummaryRecapImpl recap;
    /**
     *  Underlying Price
     */
    private PriceSqlType underlyingPrice;
    /**
     *  MarketDataSummaryImpl constructor comment.
     */
    public MarketDataSummaryImpl()
    {
        super();
    }
    /**
     *  Sets home for self and dependent objects.
     *
     *@param  newHome The new bOHome value
     */
    public void setBOHome( BOHome newHome )
    {
        super.setBOHome( newHome );
        BObject dependent;
        dependent = ( ( BObject ) getRecap() );
        if( dependent != null )
        {
            newHome.addToContainer( dependent );
        }
    }

    /**
     *  Getter for sessionName.
     *
     *@return  The sessionName value
     */
    public String getSessionName()
    {
        return ( String ) editor.get( _sessionName, sessionName );
    }

    /**
     *  Getter for class key.
     *
     *@return  The classKey value
     */
    public int getClassKey()
    {
        return ( int ) editor.get( _classKey, classKey );
    }
    /**
     *@return  The openInterest value
     */
    public int getOpenInterest()
    {
        return ( int ) editor.get( _openInterest, openInterest );
    }
    /**
     *  Getter for product key.
     *
     *@return  The productKey value
     */
    public int getProductKey()
    {
        return ( int ) editor.get( _productKey, productKey );
    }
    /**
     *  Getter for recap.
     *
     *@return  The recap value
     */
    public Recap getRecap()
    {
        return ( SummaryRecapImpl ) editor.get( _recap, recap );
    }
    /**
     *  Getter for last sale price.
     *
     *@return  The underlyingPrice value
     */
    public Price getUnderlyingPrice()
    {
        return ( PriceSqlType ) editor.get( _underlyingPrice, underlyingPrice );
    }
    /**
     *  Creates a new instance.
     *
     *@param  sessionName
     *@param  productKey key of product
     *@param  classKey key of product's class
     */
    public void create( String sessionName, int productKey, int classKey )
    {
        setSessionName( sessionName );
        setProductKey( productKey );
        setClassKey( classKey );
        SummaryRecapImpl tempRecap = new SummaryRecapImpl();
        tempRecap.setBOHome( getBOHome() );
        tempRecap.setBrokerName( getBrokerName() );
        tempRecap.create();
        setRecap( tempRecap );
    }
    /**
     *  Needed to define table name and the description of this class.
     *
     *@return
     */
    public ObjectChangesIF initializeObjectEditor()
    {
        final DBAdapter result = ( DBAdapter ) super.initializeObjectEditor();
        initDescriptor();
        result.setTableName( "mkt_data_summary" );
        result.setClassDescription( classDescriptor );
        return result;
    }
    /**
     *  Creates underlying recap struct. Product keys is not completely filled
     *  in, don't want to make call to product service from the domain layer.
     *
     *@return
     */
    public RecapStruct toRecapStruct()
    {
        RecapStruct struct = getRecap().toStruct();
        struct.productKeys = new ProductKeysStruct();
        struct.productKeys.productKey = getProductKey();
        struct.productKeys.classKey = getClassKey();
        struct.sessionName = getSessionName();
        struct.productKeys.productType = getProductType();
        return struct;
    }
    /**
     *  Updates underlying recap.
     *
     *@param  recapUpdate
     *@param  newOpenInterest
     *@param  underlying
     */
    public void updateInterestRecapPrice( int newOpenInterest, RecapStruct recapUpdate, PriceStruct underlying )
    {
        setOpenInterest( newOpenInterest );
        PriceSqlType price = new PriceSqlType( underlying );
        setUnderlyingPrice( price );
        setProductType( recapUpdate.productKeys.productType );
        SummaryRecapImpl recap = (SummaryRecapImpl) getRecap();
        if( recap == null )
        {
            Log.debug( this, "Failed to find an existing SummaryRecapImpl. Not sure why." );
            SummaryRecapImpl tempRecap = new SummaryRecapImpl();
            tempRecap.setBrokerName( getBrokerName() );
            tempRecap.setBOHome( getBOHome() );
            tempRecap.create();
            setRecap( tempRecap );
            recap = tempRecap;
        }
        recap.updateForMarketDataSummary( recapUpdate );
    }
    
    /**
     *  Updates underlying recap with suffix.
     *
     *@param  recapUpdate
     *@param  newOpenInterest
     *@param  underlying
     *@param  closingSuffix
     *@param  prevClosingSuffix
     */
    public void updateInterestRecapPrice( int newOpenInterest, RecapStruct recapUpdate, PriceStruct underlying, String closingSuffix, String prevClosingSuffix)
    {
    	setOpenInterest( newOpenInterest );
        PriceSqlType price = new PriceSqlType( underlying );
        setUnderlyingPrice( price );
        setProductType( recapUpdate.productKeys.productType );
    	SummaryRecapImpl recap = (SummaryRecapImpl) getRecap();
        if( recap == null )
        {
            SummaryRecapImpl tempRecap = new SummaryRecapImpl();
            tempRecap.setBrokerName( getBrokerName() );
            tempRecap.setBOHome( getBOHome() );
            tempRecap.create();
            setRecap( tempRecap );
            recap = tempRecap;
        }
    	recap.updateForMarketDataSummary( recapUpdate, closingSuffix, prevClosingSuffix );
    }

    /**
     *  Setter for sessionName.
     *
     *@param  aValue The new sessionName value
     */
    protected void setSessionName( String aValue )
    {
        editor.set( _sessionName, aValue, sessionName );
    }

    /**
     *  Setter for class key.
     *
     *@param  aValue The new classKey value
     */
    protected void setClassKey( int aValue )
    {
        editor.set( _classKey, aValue, classKey );
    }
    /**
     *  Setter for class key.
     *
     *@param  aValue The new openInterest value
     */
    protected void setOpenInterest( int aValue )
    {
        editor.set( _openInterest, aValue, openInterest );
    }
    /**
     *  Setter for product key.
     *
     *@param  aValue The new productKey value
     */
    protected void setProductKey( int aValue )
    {
        editor.set( _productKey, aValue, productKey );
    }
    /**
     *  Setter for last sale price.
     *
     *@param  aValue The new underlyingPrice value
     */
    protected void setUnderlyingPrice( PriceSqlType aValue )
    {
        editor.set( _underlyingPrice, aValue, underlyingPrice );
    }
    /**
     *  Sets product type value.
     *
     *@param  newType product type value
     */
    protected void setProductType( short newType )
    {
        editor.set( _productType, newType, productType );
    }
    /**
     *  Gets product type value for this product.
     *
     *@return  product type value
     */
    protected short getProductType()
    {
        return editor.get( _productType, productType );
    }
    /**
     *  Setter for recap.
     *
     *@param  aValue The new recap value
     */
    private void setRecap( SummaryRecapImpl aValue )
    {
        editor.set( _recap, aValue, recap );
    }
    /**
     *  Describe how this class relates to the relational database.
     */
    private void initDescriptor()
    {
        synchronized( MarketDataSummaryImpl.class )
        {
            if( classDescriptor != null )
            {
                return;
            }
            Vector tempDescriptor = getSuperDescriptor();
            tempDescriptor.addElement( AttributeDefinition.getAttributeRelation( "session_name", _sessionName ) );
            tempDescriptor.addElement( AttributeDefinition.getAttributeRelation( "prod_key", _productKey ) );
            tempDescriptor.addElement( AttributeDefinition.getAttributeRelation( "class_key", _classKey ) );
            tempDescriptor.addElement( AttributeDefinition.getAttributeRelation( "open_interest", _openInterest ) );
            tempDescriptor.addElement( AttributeDefinition.getAttributeRelation( "underlying_price", _underlyingPrice ) );
            tempDescriptor.addElement( AttributeDefinition.getAttributeRelation( "prod_type_code", _productType ) );
            tempDescriptor.addElement( AttributeDefinition.getForeignRelation( SummaryRecapImpl.class, "recap", _recap ) );
            classDescriptor = tempDescriptor;
        }
    }

    static
    {
        try
        {
            _sessionName = MarketDataSummaryImpl.class.getDeclaredField( "sessionName" );
            _sessionName.setAccessible( true );
            _classKey = MarketDataSummaryImpl.class.getDeclaredField( "classKey" );
            _classKey.setAccessible( true );
            _productKey = MarketDataSummaryImpl.class.getDeclaredField( "productKey" );
            _productKey.setAccessible( true );
            _openInterest = MarketDataSummaryImpl.class.getDeclaredField( "openInterest" );
            _openInterest.setAccessible( true );
            _recap = MarketDataSummaryImpl.class.getDeclaredField( "recap" );
            _recap.setAccessible( true );
            _underlyingPrice = MarketDataSummaryImpl.class.getDeclaredField( "underlyingPrice" );
            _underlyingPrice.setAccessible( true );
            _productType= MarketDataSummaryImpl.class.getDeclaredField( "productType" );
            _productType.setAccessible( true );
        }
        catch( NoSuchFieldException ex )
        {
            ex.printStackTrace();
        }

        SqlScalarTypeInitializer.initTypes();
    }
}
