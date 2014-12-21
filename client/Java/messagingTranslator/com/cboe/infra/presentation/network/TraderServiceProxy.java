//-----------------------------------------------------------------------------------
//Copyright (c) 2005 The Chicago Board Options Exchange. All Rights Reserved.
//-----------------------------------------------------------------------------------
package com.cboe.infra.presentation.network;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;

import org.omg.CosTrading.Lookup;
import org.omg.CosTrading.Offer;
import org.omg.CosTrading.OfferIteratorHolder;
import org.omg.CosTrading.OfferSeqHolder;
import org.omg.CosTrading.PolicyNameSeqHolder;
import org.omg.CosTrading.Register;
import org.omg.CosTrading.LookupPackage.HowManyProps;
import org.omg.CosTrading.LookupPackage.SpecifiedProps;
import org.omg.CosTradingRepos.ServiceTypeRepository;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.ListOption;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.PropStruct;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.PropertyMode;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.SpecifiedServiceTypes;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.TypeStruct;
import org.omg.GIOP.LocateStatusType_1_2;
import org.omg.PortableServer.POAManagerPackage.State;

import com.cboe.CommandConsole.CommandConsole;
import com.cboe.CommandConsole.CommandConsoleHelper;
import com.cboe.EventService.Admin.Admin;
import com.cboe.EventService.Admin.AdminException;
import com.cboe.EventService.Admin.AdminHelper;
import com.cboe.EventService.Admin.AdminPropertyDefinitions;
import com.cboe.EventService.Admin.ChannelNotFoundException;
import com.cboe.EventService.Admin.CreateEventChannel;
import com.cboe.EventService.Admin.InterfaceValidator;
import com.cboe.EventService.Transport.EventProfileImpl;
import com.cboe.EventService.Transport.EventServiceTransport;
import com.cboe.EventService.Transport.EventServiceTransportHelper;
import com.cboe.ORBInfra.IOPImpl.BinaryObjectKey;
import com.cboe.ORBInfra.IOPImpl.IORImpl;
import com.cboe.ORBInfra.ORB.DelegateImpl;
import com.cboe.ORBInfra.ORB.LocalDelegateImpl;
import com.cboe.ORBInfra.ORB.LocateReply;
import com.cboe.ORBInfra.ORB.Orb;
import com.cboe.ORBInfra.PolicyAdministration.BindOrderImpl;
import com.cboe.ORBInfra.PolicyAdministration.LOFT;
import com.cboe.ORBInfra.PolicyAdministration.SOFT;
import com.cboe.ORBInfra.PolicyAdministration.SWUL;
import com.cboe.ORBInfra.PolicyAdministration.TOFT;
import com.cboe.ORBInfra.PortableServer.POAObjectId;
import com.cboe.ORBInfra.PortableServer.POAObjectKey;
import com.cboe.ORBInfra.TIOP.TIOPProfileImpl;
import com.cboe.infrastructureUtility.CBOETradingBinder;
import com.cboe.infrastructureUtility.InitRefUtil;

/**
 * Proxy to manipulate the TraderService. 
 * Useful for exploring the contexts of the Trader.
 * 
 * based on InfraVerity.java.com.cboe.utils.TraderUtility.java
 * version 2.0, which was authored by Kevin Yaussy
 * 
 * @author jwalton
 */
public class TraderServiceProxy
{
    /**
     * A handle to the unique TraderServiceProxy instance.
     */
    private static TraderServiceProxy instance = null;

    private static String EVENT_SERVICE_TRANSPORT_STR = "EventTransport";
    private static String EVENT_CHANNEL_STR = "EventChannel";
    private static String TRADING_SERVICE_STR = "TradingService";    
    private static String COMMAND_CONSOLE_STR = "CommandConsole";    
    private static String INIT_REF_URL_SYSTEM_PROPERTY = "ORB.InitRefURL";
    private static String initRefURL = System.getProperty(INIT_REF_URL_SYSTEM_PROPERTY);

    private Orb cboeOrb;
    private Lookup lookupIf = null;
    private Register registerIf = null;
    private ServiceTypeRepository reposIf = null;
    private Admin admin = null; // admin, used for delete event channel

    // original code vars
    private static boolean usePOA = false;
    private static boolean useDifferentIDs = false;
    private boolean queryVerbose = false;
    private static boolean ping = false;
    private static boolean initRefs = false;
    private static int exportIters = 1;
    private static long pingTimeout = 1000;
    private static final String PROPS_BLANKS = "                                                                               ";

    private TraderServiceProxyResultsWrapper resWrapper;
    
    private CreateEventChannel eventChannelCreator = null;

    private InterfaceValidator interfaceValidator = null;

    private String myClassName;
    
    ////////////////////////////////////
    // implement the singleton pattern
    ////////////////////////////////////
    protected TraderServiceProxy() 
    {
        this.myClassName = this.getClass().getName();
    }
    
    /**
     * @return The unique instance of this class.
     */
    static public TraderServiceProxy instance() {
       if ( instance == null ) {
          instance = new TraderServiceProxy();
       }
       return instance;
    }

    ////////////////////////////////////
    // init method for the proxy
    ////////////////////////////////////
    public void init() throws Exception
    {
        this.cboeOrb = (com.cboe.ORBInfra.ORB.Orb)com.cboe.ORBInfra.ORB.Orb.init();
        
        // this will load the event transport
        EventServiceTransport transport = EventServiceTransportHelper.narrow(
                this.cboeOrb.resolve_initial_references(EVENT_SERVICE_TRANSPORT_STR));
        
        org.omg.CORBA.Object ref = this.cboeOrb.resolve_initial_references(
                TRADING_SERVICE_STR);

        // init the InterfaceValidator
        this.interfaceValidator = new InterfaceValidator(this.cboeOrb);

        // init the EventChannelCreator
        this.eventChannelCreator = new CreateEventChannel();

        this.lookupIf = org.omg.CosTrading.LookupHelper.narrow(ref);
        if ( this.lookupIf == null ) {
            throw new Exception(this.myClassName+": Trader Lookup Reference is null.");
        } 
        else 
        {
            this.registerIf = this.lookupIf.register_if();
            if (this.registerIf == null)  { 
                throw new Exception(this.myClassName+": Trader Register Reference is null.");
            }
            else
            {
                ref = lookupIf.type_repos();
                this.reposIf = org.omg.CosTradingRepos.ServiceTypeRepositoryHelper.narrow(ref);
                if (this.reposIf == null)  {
                    throw new Exception(this.myClassName+": Trader ServiceTypeRepository Reference is null.");
                }
            }
        }
        this.initAdminIf();
    }

    public void initAdminIf() throws Exception
    {
        CBOETradingBinder binder = new CBOETradingBinder(this.cboeOrb);
        org.omg.CORBA.Object[] ops = binder.resolveFromString(
                AdminPropertyDefinitions.CHANNEL_ADMIN);
        
        if (ops.length > 0)  {
            org.omg.CORBA.Object obj = ops[0];
            this.admin = AdminHelper.narrow(obj);
        }
        else  {
            throw new Exception(this.myClassName+": Admin server not found.");
        }
    }
    
    //////////////////////////////////////////////////////////////////////////
    // begin: public query methods
    //////////////////////////////////////////////////////////////////////////
    /*
     * get a list of service types
     */
    public String[] getServiceTypes() 
    {
        return reposIf.list_types( new SpecifiedServiceTypes(ListOption.all) );
    }

    /*
     * describe the given service type
     */
    public TraderServiceProxyResultsWrapper describeType(
            String serviceType
            )
    {
        this.resWrapper = new TraderServiceProxyResultsWrapper();
        StringBuffer sbuf = this.resWrapper.getReportTextStrBuf();

        TypeStruct svcType = null;
        try {
            svcType = this.reposIf.describe_type( serviceType );
        }
        catch(Exception e) {
            sbuf.append( "Exception during describe_type: " + e );
        }
        
        if ( svcType != null ) {
            sbuf.append( "\nName: " );
            sbuf.append( serviceType );
            sbuf.append( "\nInterface: " );
            sbuf.append( svcType.if_name );
            if (svcType.props.length > 0) {
                sbuf.append( "\nProperties: " );
                for ( int i=0; i<svcType.props.length; i++ )
                {
                    if (i>0) 
                    {
                        sbuf.append( ':' );
                    }
                    sbuf.append( svcType.props[i].name );
                }
            }
            sbuf.append( "\nIncarnation: " );
            sbuf.append( svcType.incarnation.high );
            sbuf.append( ' ' );
            sbuf.append( svcType.incarnation.low );
        }
        else {
            sbuf.append( "<none>" );
        }
        return this.resWrapper;
    }

    public String[] getPropertiesForServiceType(String serviceType)
    {
        String[] props = new String[0];
        
        TypeStruct svcType = null;
        try {
            svcType = this.reposIf.describe_type( serviceType );
            if ( svcType != null ) {
                if (svcType.props.length > 0) {
                    props = new String[svcType.props.length];
                    for ( int i=0, j=svcType.props.length; i<j; i++ )
                    {
                        props[i] = new String( svcType.props[i].name );
                    }
                }
            }
        }
        catch(Exception e) {
        }
        return props;
    }
    
    public TraderServiceProxyResultsWrapper getOffersForServiceType(
            String serviceType
            ,String constraint
            ,boolean verbose
            )
    {
        this.queryVerbose = verbose;

        this.resWrapper = new TraderServiceProxyResultsWrapper();
        StringBuffer sbuf = this.resWrapper.getReportTextStrBuf();

        Offer[] dirResults = getQueryResults( serviceType, constraint );

        ArrayList regular = new ArrayList();
        ArrayList channels = new ArrayList();
        for ( int i=0; i<dirResults.length; i++) {
            IORImpl ior = getIORImpl(dirResults[i]);
            if (ior!=null)
            {
                boolean isEC = false;
                try {
                    EventProfileImpl eProfile = (EventProfileImpl)ior.getProfile( EventProfileImpl.tag );
                    isEC = true;
                }
                catch(com.cboe.ORBInfra.IOPImpl.ProfileNotPresent pnp) { 
                }
                if ( isEC ) {
                    channels.add(dirResults[i]);
                }
                else {
                    regular.add(dirResults[i]);
                }
            }
        }

        Offer[] regs = new Offer[regular.size()];
        regular.toArray(regs);
        Offer[] chans = new Offer[channels.size()];
        channels.toArray(chans);
        if ( regs.length > 0) {
            this.resWrapper.setQueryOffersList(
                    doQueryForOffers(regs, false));
            this.resWrapper.setQueryResultType(
                    TraderServiceConstants.QUERY_RESULTS_TYPE_NON_EC);
        }
        if ( chans.length > 0) {
            this.resWrapper.setQueryOffersList(
                    doQueryForOffers(chans, true));
            this.resWrapper.setQueryResultType(
                    TraderServiceConstants.QUERY_RESULTS_TYPE_EC);
        }
        return this.resWrapper;
    }
    
    public int getOffersCountForServiceType(
            String serviceType
            )
    {
        int offersCnt = 0;
        
        // get the offers for the service type
        Offer[] dirResults = getQueryResults( serviceType, "" );
        if (dirResults!=null)
        {
            offersCnt = dirResults.length;
        }
        return offersCnt;
    }

    public boolean isInterfaceValid(
            String interfaceName
            )
    {
        return this.interfaceValidator.isValidInterface(interfaceName);
    }

    //////////////////////////////////////////////////////////////////////////
    // end: public query methods
    //////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////
    // begin: public action methods
    //////////////////////////////////////////////////////////////////////////
    /**
     * Add the Service Type
     * @param typeName the Service Type name
     * @param props the Service's properties.
     * Note, properties are assumed to be String types, NORMAL mode
     */
    public void addType(
            String queryServiceType
            ,String properties
            )
    {
        PropStruct[] typeProps = new PropStruct[0];
        StringTokenizer aTokenizer = new StringTokenizer(properties, ":");
        int numProps = aTokenizer.countTokens();
        if ( numProps > 0 ) {
            int i=0;
            org.omg.CORBA.TypeCode aStringType = cboeOrb.create_string_tc(0);
            typeProps = new PropStruct[numProps];
            while ( aTokenizer.hasMoreTokens() ) {
                typeProps[i] = new PropStruct(aTokenizer.nextToken(), aStringType, PropertyMode.PROP_NORMAL);
                i++;
            }
        }
        
        try {
            reposIf.add_type(queryServiceType, queryServiceType /* if_name */, typeProps, new String[0] /* no supers */);
        }
        catch(Exception e) {
        }
    }
    
    /**
     * Remove the Service Type
     * @param typeName the Service Type name
     */
    public void removeType(
            String typeName
            )
    {
        try {
            reposIf.remove_type(typeName);
        }
        catch(Exception e) {
        }
    }
    
    /**
     * Withdraw the Service Type using constraint
     * @param typeName the Service Type name
     * @param constraint the constraint
     */
    public void withdraw(
            String typeName
            , String constraint 
            )
    {
        try {
            registerIf.withdraw_using_constraint( typeName, constraint );
        }
        catch(Exception e) {
        }
    }

    public void export(
            String typeName
            , String props
            )
    {
        org.omg.PortableServer.POA rootPOA = null;
        try {
            org.omg.CORBA.Object obj = cboeOrb.resolve_initial_references("RootPOA");
            rootPOA = org.omg.PortableServer.POAHelper.narrow(obj);
        }
        catch(Exception e) {
            return;
        }
        
        if ( usePOA ) {
            org.omg.PortableServer.Servant impl = new TestPOAImpl();
            org.omg.PortableServer.Servant impl2 = null;
            if ( useDifferentIDs ) {
                impl2 = new TestPOAImpl();
            }
            
            try {
                rootPOA.activate_object(impl);
                if ( useDifferentIDs ) {
                    rootPOA.activate_object(impl2);
                }
            }
            catch(Exception e) {
                return;
            }
            
            int i = 0;
            try {
                for ( i = 0; i < exportIters; i++ ) {
                    if ( useDifferentIDs ) {
                        if (i == 0 ) {
                            registerIf.export(rootPOA.servant_to_reference(impl), typeName, convertToCosProps(props));
                            
                        }
                        else if ( i==1 ) {
                            registerIf.export(rootPOA.servant_to_reference(impl2), typeName, convertToCosProps(props));
                        }
                    }
                    else {
                        registerIf.export(rootPOA.servant_to_reference(impl), typeName, convertToCosProps(props));
                    }
                }
            }
            catch( Exception e) {
                return;
            }
        }
        else {
            org.omg.PortableServer.Servant servant = new com.cboe.EventService.Admin.TypedEventChannelImpl();
            int i = 0;
            try {
                rootPOA.activate_object(servant);
                org.omg.CORBA.Object impl = rootPOA.servant_to_reference(servant);
                for ( i = 0; i < exportIters; i++ ) {
                    registerIf.export(impl, typeName, convertToCosProps(props));
                }
            }
            catch(Exception e) {
            }
        }
    }

    /*
     * use the EventChannelCreator to create an eventChannel
     */
    public void createEventChannel(
            String[] args
            )
    {
        this.eventChannelCreator.createEventChannel(args);
    }
   
    /*
     * delete the given eventChannel
     */
    public void deleteEventChannel(
            String channelName
            )
    {
        try  {
            this.admin.destroyChannel(channelName);
        }
        catch (ChannelNotFoundException cnf)  {
            cnf.printStackTrace();
            System.exit(-1);
        }
        catch (AdminException ae)  {
            ae.printStackTrace();
            System.exit(-1);
        }
        catch (Exception e)  {
            e.printStackTrace();
            System.exit(-1);
        }
        
    }
    
    //////////////////////////////////////////////////////////////////////////
    // end: public action methods
    //////////////////////////////////////////////////////////////////////////
    
    //////////////////////////////////////////////////////////////////////////
    // begin: private query methods
    //////////////////////////////////////////////////////////////////////////
    private ArrayList doQueryForOffers(Offer[] dirResults, boolean isEC)
    {
        ArrayList resultList = new ArrayList();
        if ( dirResults != null ) 
        {
            for ( int i=0; i<dirResults.length; i++ ) 
            {
                if ( isEC ) 
                {
                    resultList.add( this.displayECOffer(dirResults[i]) );
                }
                else 
                {
                    resultList.add( this.displayQueryOffer(dirResults[i]) );
                }
                if ( this.queryVerbose ) {
                    this.displayQueryVerbose( dirResults[i] );
                }
            }
        }
        return resultList;
    }

    private ECQueryResult displayECOffer(Offer r)
    {
        ECQueryResult queryResult = new ECQueryResult();
        try {
            IORImpl ior = getIORImpl(r);
            EventProfileImpl eProfile = (EventProfileImpl)ior.getProfile( 
                    EventProfileImpl.tag );
            queryResult.setEventChannelName( eProfile.getChannelName() );
            queryResult.setNotifyChannel( eProfile.isNotifyChannel() );
            queryResult.setChannelActive( eProfile.isChannelActive() );
            queryResult.setQualityOfService( eProfile.qosToString( eProfile.getQoS() ) );
            queryResult.setInterfaceIds( eProfile.getInterfaceIds() );
        }
        catch(com.cboe.ORBInfra.IOPImpl.ProfileNotPresent pnp) {
            this.resWrapper.getReportTextStrBuf().append("\nEC profile missing: " + pnp);
        }
        catch(Exception e) {
            this.resWrapper.getReportTextStrBuf().append("\nNo IOR information: " + e );
        }
        return queryResult;
    }
    
    private NonECQueryResult displayQueryOffer(Offer r)
    {
        NonECQueryResult queryResult = new NonECQueryResult();
        try {
            IORImpl ior = getIORImpl(r);
            TIOPProfileImpl tProfile = (TIOPProfileImpl)ior.getProfile( 
                    TIOPProfileImpl.tag );
    
            POAObjectKey pKey = tProfile.getObjectKey().getPOAObjectKey();
            
            queryResult.setOrbName( tProfile.getUniqueName() );
            queryResult.setHostName( tProfile.getHost() );
            queryResult.setPortNumber( new Integer( tProfile.getPort() ).toString() );
            queryResult.setPOAName( getPOAName( pKey ) );
            queryResult.setPOAState( getPOAState( r ) );
            queryResult.setIsPersistent( getIsPersistent( pKey ) );
            queryResult.setIsAlive( getIsAlive( tProfile ) );
            queryResult.setBindOrder( getBindOrder( tProfile ) );
            queryResult.setOfferProperties( getOfferPropsAsStringArray( r ) );
        }
        catch( com.cboe.ORBInfra.IOPImpl.ProfileNotPresent e ) {
            this.resWrapper.getReportTextStrBuf().append("\nTIOP profile missing: " + e );
        }
        catch( Exception e ) {
            this.resWrapper.getReportTextStrBuf().append("\nNo IOR information.");
        }
        return queryResult;
    }
    
    private String[] getOfferPropsAsStringArray( Offer r )
    {
        org.omg.CosTrading.Property[] props = r.properties;
        String[] propsArray = new String[props.length];
        
        for (int i = 0; i < props.length ; i++) {
            String propName = props[i].name;
            org.omg.CORBA.Any tempAny = cboeOrb.create_any();
            tempAny = props[i].value;
            String value = tempAny.extract_string();
            propsArray[i] = propName + "=" + value;
        }
        return propsArray;
    }

    private Offer[] getQueryResults(
            String serviceType
            ,String constraint
            )
    {
        Offer[] retVal = null;
        if ( initRefs ) {
            Properties initialRefs = null;
            try {
                initialRefs = InitRefUtil.load(initRefURL);
            }
            catch( Exception e ) {
                this.resWrapper.getReportTextStrBuf().append("Error loading initial references: " + e );
                return null;
            }
            
            retVal = new Offer[ initialRefs.size() ];
            try {
                Enumeration refKeys = initialRefs.propertyNames();
                int i = 0;
                while ( refKeys.hasMoreElements() ) {
                    String refName = (String)refKeys.nextElement();
                    String refIORStr = initialRefs.getProperty(refName);
                    retVal[i] = new Offer();
                    retVal[i].reference = cboeOrb.string_to_object(refIORStr);
                    i++;
                }
            }
            catch(Exception e) {
                this.resWrapper.getReportTextStrBuf().append("Failed to resolve initial references: " + e );
                retVal = null;
            }
        }
        else 
        {
            SpecifiedProps props = new SpecifiedProps(HowManyProps.all);
            OfferSeqHolder offers = new OfferSeqHolder( new Offer[0] );
            OfferIteratorHolder iter = new OfferIteratorHolder();
            PolicyNameSeqHolder limits = new PolicyNameSeqHolder( new String[0] );
            try {
                lookupIf.query( 
                        serviceType, 
                        constraint, 
                        new String(), /* preference */
                        new org.omg.CosTrading.Policy[0], /* policies */
                        props, 
                        0 /* numOffers */, 
                        offers, iter, limits);
            }
            catch(Exception e) {
                this.resWrapper.getReportTextStrBuf().append( "Exception during query: " + e );
            }
            retVal = offers.value;
        }
        return retVal;
    }
    
    private void displayQueryVerbose( Offer r )
    {
        StringBuffer sbuf = this.resWrapper.getReportTextStrBuf();
        sbuf.append( "\n+++++++++++++++++++++++++++++" );
        try {
            sbuf.append( getIORImpl(r).toString() );
        }
        catch( Exception e ) {
            sbuf.append("\nNo IOR information.");
        }
        
        try {
            sbuf.append(( (com.cboe.ORBInfra.ORB.DelegateImpl) 
                    ((org.omg.CORBA.portable.ObjectImpl)r.reference)
                        ._get_delegate())._get_policy_manager().toString());
        }
        catch ( Exception e ) {
            sbuf.append("\nNo Policy information.");
        }
        
        if ( r.properties != null && r.properties.length > 0 ) {
            sbuf.append("\nProperties...");
            for ( int i=0; i<r.properties.length; i++) {
                // org.omg.CosTrading.Property property's value is an org.omg.CORBA.Any
                // assume it's a string
                sbuf.append("\n" + r.properties[i].name + " = " + r.properties[i].value.extract_string() );
            }
        }
        else {
            sbuf.append("\nNo Properties.");
        }
    }
    
    private String getPOAName( POAObjectKey pKey )
    {
        StringBuffer buff = new StringBuffer(128);
        String[] pNames = pKey.poaName();
        for (int i = 0; i < pNames.length; i++) {
            buff.append("/");
            buff.append(pNames[i]);
        }
        return buff.toString();
    }
    
    private String getPOAState( Offer r )
    {
        String pStateStr = TraderServiceConstants.FAILED_STATE_STR;
        if ( ping ) {
            try {
                IORImpl ior = getIORImpl(r).copy();
                TIOPProfileImpl tProfile;
                tProfile = (TIOPProfileImpl)ior.getProfile( TIOPProfileImpl.tag );
                BinaryObjectKey pKey = tProfile.getObjectKey();
                String[] origPOAName = pKey.getPOAObjectKey().poaName();
                POAObjectId newId = new POAObjectId( COMMAND_CONSOLE_STR.getBytes() );
		POAObjectKey newKey = POAObjectKey.createObjectKey( new String[] { COMMAND_CONSOLE_STR }, newId, new byte[0]);
                pKey.setPOAObjectKey( newKey );
                ior.setTypeId( CommandConsoleHelper.id() );
                String newIORStr = ior.stringify();
                
                CommandConsole con = CommandConsoleHelper.narrow( 
                        cboeOrb.string_to_object( newIORStr ) );
                State pState = con.getPOAState( origPOAName );
                int state = pState.value();
                switch( state ) {
                case State._HOLDING:
                    pStateStr = TraderServiceConstants.HOLDING_STATE_STR;
                break;
                case State._DISCARDING:
                    pStateStr = TraderServiceConstants.DISCARDING_STATE_STR;
                break;
                case State._ACTIVE:
                    pStateStr = TraderServiceConstants.ACTIVE_STATE_STR;
                break;
                case State._INACTIVE:
                    pStateStr = TraderServiceConstants.INACTIVE_STATE_STR;
                break;
                default:
                    pStateStr = TraderServiceConstants.UNKNOWN_STATE_STR;
                break;
                }
            }
            catch( Throwable t ) {
                this.resWrapper.getReportTextStrBuf().append(
                        "Failed to get POA state: " + t.getMessage() );
            }
        }
        else {
            pStateStr = TraderServiceConstants.NOT_AVAILABLE_STATE_STR;
        }
        return pStateStr;
    }
    
    private String getIsPersistent( POAObjectKey pKey )
    {
        byte[] transId = pKey.transId();
        return (transId == null || transId.length == 0) ? "true" : "false";
    }
    
    private String getIsAlive( TIOPProfileImpl tProfile )
    {
        String isAlive = TraderServiceConstants.FAILED_ALIVE_STR;
        if ( ping ) {
            try {
                LocateReply lr = cboeOrb.deliver( tProfile, pingTimeout );
                int status = lr.getLocateStatusType().value();
                switch( status ) {
                case LocateStatusType_1_2._UNKNOWN_OBJECT:
                    isAlive = TraderServiceConstants.UNKNOWN_ALIVE_STR;
                break;
                case LocateStatusType_1_2._OBJECT_HERE:
                    isAlive = TraderServiceConstants.HERE_ALIVE_STR;
                break;
                case LocateStatusType_1_2._OBJECT_FORWARD:
                    isAlive = TraderServiceConstants.FORWARD_ALIVE_STR;
                break;
                case LocateStatusType_1_2._OBJECT_FORWARD_PERM:
                    isAlive = TraderServiceConstants.FORPERM_ALIVE_STR;
                break;
                case LocateStatusType_1_2._LOC_SYSTEM_EXCEPTION:
                    isAlive = TraderServiceConstants.LOCEX_ALIVE_STR;
                break;
                case LocateStatusType_1_2._LOC_NEEDS_ADDRESSING_MODE:
                    isAlive = TraderServiceConstants.LOCADDR_ALIVE_STR;
                break;
                default:
                    isAlive = TraderServiceConstants.INVALID_ALIVE_STR;
                break;
                }
            }
            catch( Throwable t ) {
                this.resWrapper.getReportTextStrBuf().append(
                        "\nPing Failed: Name(" + tProfile.getUniqueName() + "): "+ t.getMessage() );
            }
        }
        else {
            isAlive = TraderServiceConstants.NOT_AVAILABLE_ALIVE_STR;
        }
        return isAlive;
    }
    
    private String getBindOrder( TIOPProfileImpl tProfile )
    {
        org.omg.CORBA.Policy[] policies = tProfile.getPolicyList();
        
        if ( policies == null || policies.length == 0 ) 
        {
            return TraderServiceConstants.NONE_BIND_ORDER_STR;
        }
        
        for ( int i = 0; i < policies.length; i++ ) {
            if ( policies[i] instanceof BindOrderImpl ) {
                BindOrderImpl bOrder = (BindOrderImpl)policies[i];
                if ( bOrder.order_value().bindType == LOFT.value ) 
                    return TraderServiceConstants.LOFT_BIND_ORDER_STR;
                else if ( bOrder.order_value().bindType == TOFT.value ) 
                    return TraderServiceConstants.TOFT_BIND_ORDER_STR;
                else if ( bOrder.order_value().bindType == SOFT.value ) 
                    return TraderServiceConstants.SOFT_BIND_ORDER_STR;
                else if ( bOrder.order_value().bindType == SWUL.value ) 
                    return TraderServiceConstants.SWUL_BIND_ORDER_STR;
                else 
                    return TraderServiceConstants.UNKNOWN_BIND_ORDER_STR;
            }
        }
        return TraderServiceConstants.NONE_BIND_ORDER_STR;
    }
    
    private String getOfferProps( Offer r )
    {
        StringBuffer buff = new StringBuffer(132);
        org.omg.CosTrading.Property[] props = r.properties;
        
        for (int i = 0; i < props.length ; i++) {
            String propName = props[i].name;
            org.omg.CORBA.Any tempAny = cboeOrb.create_any();
            tempAny = props[i].value;
            String value = tempAny.extract_string();
            if ( buff.length() > 0 ) {
                buff.append('\n');
                buff.append(PROPS_BLANKS);
            }
            
            buff.append(propName);
            buff.append('=');
            buff.append(value);
        }
        
        return buff.toString();
    }

    private IORImpl getIORImpl(Offer r)
    {
        IORImpl iorImpl = null;
        try 
        {
            // note: delegate object could be a Delegate of LocalDelegate
            //iorImpl = ( (com.cboe.ORBInfra.ORB.DelegateImpl)(
                        //(com.cboe.ORBInfra.ORB.ObjectImpl)r.reference)._get_delegate() ).getIOR();
            Object delObj = ((org.omg.CORBA.portable.ObjectImpl)r.reference)._get_delegate();
            if (delObj instanceof DelegateImpl)
            {
                iorImpl = ((DelegateImpl)delObj).getIOR();
            }
            else if (delObj instanceof LocalDelegateImpl)
            {
                iorImpl = ((LocalDelegateImpl)delObj).getIOR();
            }
        }
        catch(ClassCastException e)
        {
            iorImpl = null;
        }
        return iorImpl;
    }
    //////////////////////////////////////////////////////////////////////////
    // end: private query methods
    //////////////////////////////////////////////////////////////////////////
    
    /** Convert the String properties to an array of CosTrading.Property objects.
     */
    private org.omg.CosTrading.Property[] convertToCosProps(String props)
    {
        org.omg.CosTrading.Property[] cosProps = null;
        if ( props != null ) {
            StringTokenizer propsTok = new StringTokenizer( props, ":" );
            int numProps = propsTok.countTokens();
            cosProps = new org.omg.CosTrading.Property[numProps];
            for ( int i = 0; i < numProps; i++ ) {
                String prop = propsTok.nextToken();
                StringTokenizer tok = new StringTokenizer(prop, "=" );
                String name = null;
                String value = null;
                if ( tok.hasMoreTokens() )
                    name = tok.nextToken();
                if ( tok.hasMoreTokens() )
                    value = tok.nextToken();
                
                if ( name != null ) {
                    org.omg.CORBA.Any tempAny = cboeOrb.create_any();
                    tempAny.insert_string(value);
                    cosProps[i] = new org.omg.CosTrading.Property( name, tempAny );
                }
            }
        }
        
        return cosProps;
    }  
    
    /** inner class for export method */
    public static class TestPOAImpl extends org.omg.CosEventChannelAdmin.POA_SupplierAdmin
    {
        public TestPOAImpl() { }
        
        public org.omg.CosEventChannelAdmin.ProxyPushConsumer obtain_push_consumer() { return null; }
        
        public org.omg.CosEventChannelAdmin.ProxyPullConsumer obtain_pull_consumer() { return null; }
    }

}
