//-----------------------------------------------------------------------
// FILE: TraderServer.java
//
// PACKAGE: com.cboe.directoryService.TraderServer
//
//-----------------------------------------------------------------------
//
// Copyright (c) 1998 The Chicago Board Options Exchange. All Rights Reserved.
//
//------------------------------------------------------------------------

package com.cboe.directoryService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ResourceBundle;

import org.omg.CORBA.ORB;
import org.omg.CosTrading.Admin;
import org.omg.CosTrading.AdminPOATie;
import org.omg.CosTrading.FollowOption;
import org.omg.CosTrading.Link;
import org.omg.CosTrading.LinkPOATie;
import org.omg.CosTrading.Lookup;
import org.omg.CosTrading.LookupHelper;
import org.omg.CosTrading.LookupPOATie;
import org.omg.CosTrading.Register;
import org.omg.CosTrading.RegisterPOATie;
import org.omg.CosTradingRepos.ServiceTypeRepository;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPOATie;
import org.omg.PortableServer.LifespanPolicyValue;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ObjectAlreadyActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import com.cboe.ImplementationRepository.LocatorAux;
import com.cboe.ORBInfra.ORB.InitialReferenceResolver;
import com.cboe.ORBInfra.PolicyAdministration.LOFT;
import com.cboe.ORBInfra.PolicyAdministration.SWUL;
import com.cboe.ORBInfra.PolicyAdministration.TOFT;
import com.cboe.common.log.InfraLoggingRb;
import com.cboe.common.log.Logger;
import com.cboe.common.log.StartupLogger;
import com.cboe.infrastructureUtility.InitRefUtil;
import com.cboe.instrumentationService.InstrumentorHome;
import com.cboe.msNegotiation.NegotiatorClient;
import com.cboe.msNegotiation.NegotiatorInstructionReceiver;

import static com.cboe.directoryService.TraderLogBuilder.*;
import com.cboe.processWatcher.ProcessWatcherRegistrationThread;

/**
*  This is the server class that sets up the Trader Service.
*  It can be started via System Management or command line.
*  The Trader should be contacted with
*  resolve_initial_references("TradingService")then
*  narrow using the LookupHelper.
*
*  The server starts up by first getting its properties, creates the POA that it
*  will use then creating its implementation objects for Admin, Register,
*  ServiceTypeRepository and the Lookup.    Then it instantiates its Tie objects,
*  and instantiates them, the Lookup tie object is the exception because it uses
*  the an id of "TradingService" while the other tie objects use activate.  The Lookup
*  is activated with an id because it needs to be found with resolve_initial_reference.
*  The next step connects all of the implementation objects and finally the
*  server is started.
*
* @author             Judd Herman
*/
public class TraderServer
{
	private static String TRADING_SERVICE_NAME = "TradingService";
	
	/** for logging, common name to give all log messages*/
	private static final String CLASS_ID = TraderServer.class.getSimpleName();
	/**
	*  reference to the ORB
	*/
	private static org.omg.CORBA.ORB orb;

	/**
	 *  reference to the properties from system management
	 */
	private static java.util.Properties traderProperties;

 	/**
	 *  String to denote the Bind Order for the LookupImpl target
	 */
	private static String bindOrder;

	/**
	 *  The Bind Order Value which maps to LOFT.value,TOFT.value etc.
	 */
	private static short bindOrderValue = -1;

	/**
	 *  rootPOA
	 */
	private static POA rootPOA = null;

	/**
	 *  POA under which the target is registered.
	 */
	private static POA initialPOA =  null;

	private static ResourceBundle rb = null;

	private static boolean isPrimary = Boolean.getBoolean("Trader.Primary");
	
    private static NegotiatorClient negotiatorClient;
	private static Lookup theLookup = null;
	private static Link theLink = null;

		
	protected static ResourceBundle initializeLoggingRb() {
		try {
			
			rb = ResourceBundle.getBundle( InfraLoggingRb.class.getName() );
			
		} catch( Exception e ) {
			Logger.sysAlarm( Logger.createLogMessageId( Logger.getDefaultLoggerName(),
										      "Unable to set Logging ResourceBundle({0}).",
										      "TraderServer", "" ),
						                                         new Object[] {InfraLoggingRb.class.getName()} );
		}
		
		return rb;
				
	}

	public static void main(String[] args) {

		final String METHOD_ID = "main";
		
		rb = initializeLoggingRb();
		Logger.sysNotify(formatEnter(CLASS_ID, METHOD_ID));


		traderProperties = System.getProperties();

 		orb = com.cboe.ORBInfra.ORB.Orb.init();

		bindOrder = System.getProperty("ORB.Servant.BindOrder", "SWUL");
		
		if (bindOrder.equals("LOFT")) {
			bindOrderValue = LOFT.value;
		} else if (bindOrder.equals("TOFT")) {
			bindOrderValue = TOFT.value;
		}else if (bindOrder.equals("SWUL")) {
			bindOrderValue = SWUL.value;
		}
		Logger.sysNotify(format(CLASS_ID, METHOD_ID, "Using %s for bind order", bindOrder));


		initialPOA = createInitialPOA(bindOrderValue, orb);

		LookupPOATie lookupTie  = null;
		AdminPOATie adminTie  = null;
		RegisterPOATie registerTie  = null;
		ServiceTypeRepositoryPOATie serviceTypeRepositoryTie = null;
        LinkPOATie linkTie = null;
		
		try {
			// Contruct implementation objects
			LookupImpl lookupImpl = new LookupImpl(traderProperties,initialPOA);
			AdminImpl adminImpl = new AdminImpl(traderProperties,initialPOA);
			ServiceTypeRepositoryImpl serviceTypeRepositoryImpl = 
				new ServiceTypeRepositoryImpl(traderProperties,initialPOA);
			RegisterImpl registerImpl = new RegisterImpl(traderProperties,initialPOA);
                        LinkImpl linkImpl = new LinkImpl(traderProperties,initialPOA);

			// Contruct Tie objects
			lookupTie = new LookupPOATie( lookupImpl, initialPOA );
			adminTie = new AdminPOATie( adminImpl, initialPOA );
			registerTie = new RegisterPOATie( registerImpl, initialPOA );
			serviceTypeRepositoryTie = 
				new ServiceTypeRepositoryPOATie( serviceTypeRepositoryImpl, initialPOA );
			linkTie = new LinkPOATie(linkImpl, initialPOA );
			
			StartupLogger.progress("Trader ties have been instantiated");
			

			// activate with orb
			String TraderServerName = System.getProperty("Trader.ServerName","TradingService");
			
			initialPOA.activate_object_with_id(TraderServerName.getBytes(), lookupTie);
			Logger.sysNotify(format(CLASS_ID, METHOD_ID, "Lookup %s activated", initialPOA.the_name()));

			initialPOA.activate_object_with_id("TradingService.Admin".getBytes(), adminTie);
			Logger.sysNotify(format(CLASS_ID, METHOD_ID, "Admin %s activated", initialPOA.the_name()));

			initialPOA.activate_object_with_id("TradingService.Register".getBytes(), registerTie);
			Logger.sysNotify(format(CLASS_ID, METHOD_ID, "Register %s activated", initialPOA.the_name()));

			initialPOA.activate_object_with_id("TradingService.ServiceTypeRepository".getBytes(), 
								      serviceTypeRepositoryTie);
			Logger.sysNotify(format(CLASS_ID, METHOD_ID, "ServiceTypeRepository %s activated", initialPOA.the_name()));

			initialPOA.activate_object_with_id("TradingService.Link".getBytes(), linkTie);
			Logger.sysNotify(format(CLASS_ID, METHOD_ID, "Link %s activated", initialPOA.the_name()));
			StartupLogger.progress("Activations complete");

			
			// *** connect all the references between implementations  **

			//set Admin refs
			Admin adminTemp = adminTie._this(orb);
			lookupImpl.setAdmin_if(adminTemp);
			registerImpl.setAdmin_if(adminTemp);
			linkImpl.setAdmin_if(adminTemp);
			
			//set Register refs
			Register registerTemp = registerTie._this(orb);
			lookupImpl.setRegister_if(registerTemp);
			adminImpl.setRegister_if(registerTemp);
			linkImpl.setRegister_if(registerTemp);
			
			//set ServiceTypeRepository refs
			ServiceTypeRepository serviceTypeRepositoryTemp = serviceTypeRepositoryTie._this(orb);
			lookupImpl.type_repos(serviceTypeRepositoryTemp);
			adminImpl.type_repos(serviceTypeRepositoryTemp);
			registerImpl.set_type_repos(serviceTypeRepositoryTemp);
			linkImpl.type_repos(serviceTypeRepositoryTemp);

			// set Link refs
			Link linkTemp = linkTie._this(orb);
			lookupImpl.setLink_if(linkTemp);
			adminImpl.setLink_if(linkTemp);
			registerImpl.setLink_if(linkTemp);

			StartupLogger.progress("References connected");

			
			// get Lookup ref
			theLookup = lookupTie._this(orb);
		
			//Activating the RootPOA .
			rootPOA.the_POAManager().activate();
			Logger.sysNotify(format(CLASS_ID, METHOD_ID, "Trader POA Manager %s activated", rootPOA.the_name()));

			org.omg.CORBA.Object obj = lookupTie._this(orb);
			
			if (isPrimary ) {
			
				if (getSysProp("ORB.InitRefURL")!= null && getSysProp("ORB.InitRefURL").trim().length() > 2) {
					
 					writeLookupRef(orb.object_to_string(obj), getSysProp("ORB.InitRefURL"),"TradingService");
  					Logger.sysNotify(format(CLASS_ID, METHOD_ID, "Orb IOR written to file %s", getSysProp( "ORB.InitRefURL" )));
				} 
			}
			// Go discarding first.  Let negotiation handle proper state.
			becomeInactive();
			
			// fix for event transport profile .
			resolveTransport();
			StartupLogger.progress("Initial setup complete");

 			
	   		InstrumentorHome.setupDefaultInstrumentorMonitorRegistrars();

 			Thread aHookThread = new Thread() {

										
					public void run() {
						if (orb != null) {
							Logger.sysNotify(format(CLASS_ID, "shutdownHook", "Trader server shutdown"));
							((com.cboe.ORBInfra.ORB.Orb)orb).shutdown(true);
						}
					}
				};
			
			
			Runtime.getRuntime().addShutdownHook(aHookThread);
			
			// Do negotiator here.
			startNegotiationClient();
			
			new ProcessWatcherRegistrationThread().start();
			((com.cboe.ORBInfra.ORB.Orb) orb).run();
		}
		catch (WrongPolicy wp) {
			Logger.sysAlarm(format(CLASS_ID, METHOD_ID, "Fatal problem: Wrong policy. shutting down"),wp);
			System.exit(1);
		} catch (AdapterInactive ai) {
			Logger.sysAlarm(format(CLASS_ID, METHOD_ID, "Fatal problem: Adapter inactive. shutting down"),ai);
			System.exit(1);
		} catch (ServantAlreadyActive saa) {
			Logger.sysAlarm(format(CLASS_ID, METHOD_ID, "Fatal problem: Servant already active. shutting down"),saa);
			System.exit(1);
		} catch (ObjectAlreadyActive oaa) {
			Logger.sysAlarm(format(CLASS_ID, METHOD_ID, "Fatal problem: Object already active. shutting down"),oaa);
			System.exit(1);
		} catch (org.omg.CORBA.SystemException se) {
			Logger.sysAlarm(format(CLASS_ID, METHOD_ID, "Fatal problem: CORBA system problem. shutting down"),se);
			System.exit(1);
		} catch( Throwable t ) {
			Logger.sysAlarm(format(CLASS_ID, METHOD_ID, "Unexpected error during startup. shutting down"),t);
			System.exit( 1 );
		}
		
		Logger.sysNotify(formatExit(CLASS_ID, METHOD_ID, "Startup complete"));
	}

	/**
	 * Try to start negotiation client.
	 */
	private static void startNegotiationClient() throws Exception {
		String myName = ((com.cboe.ORBInfra.ORB.Orb)orb).orbName();
		String partnerName = myName.substring(0, myName.length()-1 );
		if ( myName.endsWith("A") ) {
			partnerName = partnerName.concat("B");
		}
		else {
			partnerName = partnerName.concat("A");
		}
		System.getProperties().put("NegotiatorClient.partnersOrbName", partnerName);
		System.getProperties().put("NegotiatorClient.initRefSuffix", myName);	
		
		negotiatorClient = new NegotiatorClient(orb);
		negotiatorClient.registerInstructionReceiver(new NegotiatorInstructionReceiver() {
	    	public void haveNegotiatedToMasterState(){
	    		becomeActive();
	    	}
	    	
	    	public void haveNegotiatedToSlaveState(){
	    		becomeInactive();
	    	}

		});
		negotiatorClient.startNegotiation();	
		StartupLogger.progress("Negotiation client started");
	}
	
	private static void setupFederation(){
		final String METHOD_ID = "setupFederation";
		if ( ! Boolean.getBoolean("Trader.isLinkedTrader") ) {
			return;
		}
		if (theLink != null) {
			Logger.sysNotify(format(CLASS_ID, METHOD_ID, "Federation has been already setup. Do nothing here"));
			return;
		}	     
		Logger.sysNotify(format(CLASS_ID, METHOD_ID, "Trader Federation setup complete"));
		
		bootstrapTradertoFederation();			
	}

	private static void becomeActive() {
		final String METHOD_ID = "becomeActive";
		try {
			initialPOA.the_POAManager().activate();
			setupFederation();
			Logger.sysNotify(format(CLASS_ID, METHOD_ID, "POA Manager %s Activated", initialPOA.the_name()));
			StartupLogger.signalSystemReady("Master");
		} catch( Throwable t ) {
			Logger.sysAlarm(format(CLASS_ID, METHOD_ID, " IMPORTANT - exception changing POA state to ACTIVE."), t);
		}
	}
	
	
	/**
	 * Transition to discarding state
	 */
	private static void becomeInactive() {
		final String METHOD_ID = "becomeInactive";
		try {
			initialPOA.the_POAManager().discard_requests(false);
			Logger.sysNotify(format(CLASS_ID, METHOD_ID, "POA Manager %s discarding requests", initialPOA.the_name()));
			StartupLogger.signalSystemReady("Slave");
			
		} catch( Throwable t ) {
			Logger.sysAlarm(format(CLASS_ID, METHOD_ID, " IMPORTANT - exception changing POA state to DISCARDING."), t);
		}
	
	}



	/**
	 * Setup the Federation. 
	 * 
	 * It will go through a collection of configured initrefs.ior file to get the other trader's 
	 * references, and build the federation. If there is no problem on reading a particular file, 
	 * it will ignore that file, and move on to the next file.
	 */
	private static void bootstrapTradertoFederation() 
	{
		final String METHOD_ID = "bootstrapTradertoFederation";
		
		String initrefsFileNameStr = System.getProperty("ORB.Trader.TraderIORfiles");
		if (initrefsFileNameStr == null) {
			Logger.sysNotify(format(CLASS_ID, METHOD_ID, "System property ORB.Trader.TraderIORfiles NOT DEFINED. FAILED to setup Trader Federation" ));
		}
		else {
			theLink = theLookup.link_if();
			String[] initrefsFileNames = initrefsFileNameStr.split(",");
			for (int i = 0; i < initrefsFileNames.length; i ++) {
				addRemoteTraderToFederation(initrefsFileNames[i], i);
			}
		}
	}
	
	private static void addRemoteTraderToFederation(String initrefsFileName, int traderNumber){
		final String METHOD_ID = "addRemoteTraderToFederation";
		
	  	InitialReferenceResolver initRefResolver = new InitialReferenceResolver();
		org.omg.CORBA.Object linkedTraderobj = null;
		String iorString;
	    Lookup remoteLookupIf = null;
	    String remoteLinkName;
		try 
		{
			java.net.URL url = new java.net.URL( initrefsFileName );
			initRefResolver.loadFromDefaultURL(url);
			iorString = initRefResolver.getIORString(TRADING_SERVICE_NAME);
			linkedTraderobj = orb.string_to_object(iorString);
			remoteLookupIf = LookupHelper.narrow(linkedTraderobj);
			remoteLinkName = "remoteTrader" + traderNumber;
            theLink.add_link  (remoteLinkName, remoteLookupIf, FollowOption.always, FollowOption.always);
    	    
            Logger.sysNotify(format(CLASS_ID, METHOD_ID, "Add trader from %s to our Trader Federation", initrefsFileName));
		}
		catch (Exception e)
		{
			Logger.sysAlarm(format(CLASS_ID, METHOD_ID, "Failed to add trader from %s to Trader Federation", initrefsFileName),e);
		}
	}
	
	/**
	   This method creates the POA used for the Lookup interface
	*/
	public static POA createInitialPOA (short bindOrderValue, ORB orb) {
		final String METHOD_ID = "createInitialPOA";
		
		org.omg.CORBA.Policy[] policies = null;
		try {
			org.omg.CORBA.Object obj = orb.resolve_initial_references("RootPOA");
			rootPOA = POAHelper.narrow(obj);
			//create lifespan policy
			policies = new org.omg.CORBA.Policy[6];
			
			switch (bindOrderValue) {
			case TOFT.value:
				policies[0] = LocatorAux.createPolicy(TOFT.value,orb);
				break;
			case LOFT.value:
				policies[0] = LocatorAux.createPolicy(LOFT.value,orb);
				break;
			case SWUL.value:
					policies[0] = LocatorAux.createPolicy(SWUL.value,orb);
					break;
			default:
				/* Throw and Invalid Bind Order Exception TODO: <--- is this comment still valid*/
				System.exit(1);
				
				break;
			}
			
			org.omg.CORBA.Any a  = orb.create_any();
			a.insert_boolean(true);
			policies[1] = 
				com.cboe.ORBInfra.ORB.OrbAux.create_policy(com.cboe.ORBInfra.PolicyAdministration.INITIAL_REFERENCE_POA_TYPE.value, a);
			policies[2] = 
				rootPOA.create_id_assignment_policy(org.omg.PortableServer.IdAssignmentPolicyValue.USER_ID);
			policies[3] = 
				rootPOA.create_lifespan_policy(LifespanPolicyValue.PERSISTENT);
			policies[4] = 
				rootPOA.create_thread_policy( org.omg.PortableServer.ThreadPolicyValue.ORB_CTRL_MODEL);
			if ( Boolean.getBoolean( "Trader.UsePooledPOA" ) ) {
				policies[5] =
					((com.cboe.ORBInfra.PortableServer.POA_i)rootPOA).create_thread_model_policy( com.cboe.ORBInfra.PortableServer.ThreadModelPolicyValue.POOL_PER_POA);
			} else {
				policies[5] =
					((com.cboe.ORBInfra.PortableServer.POA_i)rootPOA).create_thread_model_policy( com.cboe.ORBInfra.PortableServer.ThreadModelPolicyValue.THREAD_PER_REQUEST);
			}
		
			initialPOA =  
				rootPOA.create_POA(System.getProperty("Trader.PoaName","TradingService"), null, policies);
			
			if (Logger.isLoggable( rb, InfraLoggingRb.DS_TS_TRADER_POA_CREATED, Logger.DEBUG ) ) {
				java.lang.Object[] params = new java.lang.Object[11];
				params[0] = "TraderServer";
				params[1] = "createInitialPOA";
				params[2] = "BindOrderValue";
				params[3] = new java.lang.Short(bindOrderValue);
				params[4] = "INITIAL_REFERENCE_POA_TYPE";
				params[5] = "true";
				params[6] = "IdAssignmentPolicyValue";
				params[7] = "USER_ID";
				params[8] = "LifespanPolicyValue";
				params[9] = "PERSISTENT";
				params[10] = System.getProperty( "Trader.PoaName", "TradingService" );

				Logger.traceEntry( rb,
							    InfraLoggingRb.DS_TS_TRADER_POA_CREATED,
							    params);
			}
				

		} catch (org.omg.CORBA.ORBPackage.InvalidName in) {
			Logger.sysAlarm(format(CLASS_ID, METHOD_ID, "Invalid resolve initreference name for RootPOA, exiting"), in);
			System.exit (1);

		} catch (org.omg.PortableServer.POAPackage.InvalidPolicy ip) {
			Logger.sysAlarm(format(CLASS_ID, METHOD_ID, "POA exception, exiting"), ip);
			System.exit (1);

		} catch (org.omg.PortableServer.POAPackage.AdapterAlreadyExists aae) {
			Logger.sysAlarm(format(CLASS_ID, METHOD_ID, "POA exception, exiting"), aae);
			System.exit (1);

		} catch (Throwable ex) {
			Logger.sysAlarm(format(CLASS_ID, METHOD_ID, "Uncaught exception, exiting"), ex);
			System.exit (1);

		}
		
		return initialPOA;
	}
	
	/**
	   Write the reference for the lookup object
	   @param prefix the prefix to append to the ior
	   @param objString - the ior
	*/
	private static void writeLookupRef (String objString, String iorFile,String IorLookupName) {
		String METHOD_ID = "writeLookupRef";
		try {
			InitRefUtil.write( iorFile, IorLookupName, objString);
		} catch(FileNotFoundException fnf) {
			Logger.sysAlarm(format(CLASS_ID, METHOD_ID, "File %s not found, exiting", iorFile),fnf);
			System.exit(1);
			
		} catch(IOException iox) {
			Logger.sysAlarm(format(CLASS_ID, METHOD_ID, "IO exception, exiting"),iox);
			System.exit(1);

		}
		catch(Throwable t){
			Logger.sysAlarm(format(CLASS_ID, METHOD_ID, "Uncaught exception, exiting"),t);
			System.exit(1);
		}
	}
	
	
 

		/**
		 *  Use this when you want to throw an generic exception 
		 *  (instead of a null pointer)from a non-existant property.
		 * Otherwise get the property value from the System Property
		 * @param propName - the name of the property
		 * @return the string value of the property
		 */
		private static String getSysProp(String propName) {
			final String METHOD_ID = "getSysProp";
			
			String returnVal = null;
			try {
				returnVal = (String)traderProperties.get(propName);
			}
			catch (NullPointerException np) {
				Logger.sysAlarm(format(CLASS_ID, METHOD_ID, "Failed to get system property %s, exiting", propName),np);
				System.exit(1);
			}
			return returnVal;
		}

		// Temporary Fix for Event Transport
		//--------------

		private static void resolveTransport() {
			final String METHOD_ID = "resolveTransport";
			try {
				orb.resolve_initial_references("EventTransport");
			} catch(org.omg.CORBA.BAD_INV_ORDER bioXcpt) {
				Logger.sysAlarm(format(CLASS_ID, METHOD_ID, "Bad invocation order for transport references, exiting"), bioXcpt);
				System.exit(-1);

			} catch(org.omg.CORBA.ORBPackage.InvalidName inXcpt) {
				Logger.sysAlarm(format(CLASS_ID, METHOD_ID, "No initial references for name, exiting"), inXcpt);
				System.exit(-2);
			}
		}
	}

