//-----------------------------------------------------------------------
// FILE: TraderServerProxy.java
//
// PACKAGE: com.cboe.directoryService.proxy
//
//-----------------------------------------------------------------------

package com.cboe.directoryService.proxy;

// java packages
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

import org.omg.CORBA.ORB;
import org.omg.CosTrading.Admin;
import org.omg.CosTrading.Lookup;
import org.omg.CosTrading.POA_Admin_tie;
import org.omg.CosTrading.POA_Lookup_tie;
import org.omg.CosTrading.POA_Register_tie;
import org.omg.CosTrading.Register;
import org.omg.CosTradingRepos.POA_ServiceTypeRepository_tie;
import org.omg.CosTradingRepos.ServiceTypeRepository;
import org.omg.PortableServer.LifespanPolicyValue;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ObjectAlreadyActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import com.cboe.ORBInfra.ORB.OrbAux;
import com.cboe.ORBInfra.PolicyAdministration.BIND_ORDER_POLICY_TYPE;
import com.cboe.ORBInfra.PolicyAdministration.OrderValue;
import com.cboe.ORBInfra.PolicyAdministration.OrderValueHolder;
import com.cboe.ORBInfra.PolicyAdministration.SWUL;
import com.cboe.common.log.Logger;
import com.cboe.infrastructureUtility.CBOETradingLocatorBinder;
import com.cboe.infrastructureUtility.InitRefUtil;
import com.cboe.systemsManagementService.managedObjectFramework.managedObjectBaseClasses.MBean;
import com.cboe.systemsManagementService.managedObjectFramework.managedObjectBaseClasses.MethodInvocationEvent;
import com.cboe.systemsManagementService.managedObjectFramework.managedObjectBaseClasses.MethodInvocationListener;
import com.cboe.systemsManagementService.managedObjectFramework.systemManagementAdapter.SystemManagementAdapter;
import com.sun.jaw.reference.common.Debug;
import com.sun.jaw.reference.common.InstanceNotFoundException;


/**
*  This is the server class that sets up the Trader Service Proxy.
*  The Trader Proxy should be contacted with
*  resolve_initial_references("TradingService")then
*  narrow using the LookupHelper.
*
*  The server starts up by first creating the POA that it
*  will use then creating its implementation objects for Admin, Register,
*  ServiceTypeRepository and the Lookup.  It gets the actual
*  objects from the Locator through Locate requests
*  Then it instantiates its Tie objects,
*  and instantiates them, the Lookup tie object is the exception because it uses
*  the an id of "TradingService" while the other tie objects use activate.  The Lookup
*  is activated with an id because it needs to be found with resolve_initial_reference.
*  The next step connects all of the implementation objects and finally the
*  server is started.
*
* @author             Murali Yellepeddy
*/
public class TraderServerProxy
{

	/*
	* reference to the actual objects
	*/
        private static ServiceTypeRepository traderServiceTypeRepos;
        private static Register traderRegister;
        private static Lookup traderLookup;
        private static Admin traderAdmin;

	/**
	*  reference to the ORB
	*/
	private static org.omg.CORBA.ORB orb;

	/**
	*  reference to the properties from system management
	*/
	private static java.util.Properties traderProperties;

	/**
	*  String to denote the Trading Service name and servant id
	*/
	private static String trader;

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
	static POA initialPOA =  null;

	/**
 	*  Process Bean to update the Process Status.
 	*/
	private static MBean ProcessBean = null;

	/**
 	* Property Name for the POA State in the Process Bean
 	*/
	private static final String POA_STATE_PROP_NAME = "POAState";


  public static void main(String[] args)
    {
        Debug.parseDebugProperties();
        traderProperties = System.getProperties();


        if (args.length == 0) {
          	traderProperties = getPropertiesFromSMA(traderProperties);
        } else {
	    	try {
			traderProperties.load(new FileInputStream(args[0]));
	   	} catch(FileNotFoundException fnfe) {
		    System.out.println("unable to locate file :" + fnfe.toString());
		    System.exit(1);
		}
		catch(IOException ioe) {
		    System.out.println("I/O error :" + ioe.toString());
		    System.exit(1);
		}
        }

	// initialize the Orb
        orb = com.cboe.ORBInfra.ORB.Orb.init();

        bindOrder = System.getProperty("ORB.Servant.BindOrder", "SWUL");


	// initialize the Orb
        initialPOA = createInitialPOA(bindOrder, orb);

        POA_Lookup_tie lookupProxyTie  = null;
        POA_Admin_tie adminProxyTie  = null;
        POA_Register_tie registerProxyTie  = null;
        POA_ServiceTypeRepository_tie serviceTypeRepositoryProxyTie = null;

	// get references to the actual objects from the actual trader

	if (!initializeTradingObjects(orb))
		System.exit(1);
	

        try {

      	    Logger.debug("Starting trader server Proxy" + trader );
	    // Construct implementation objects
	    LookupProxyImpl lookupProxyImpl = new LookupProxyImpl(traderProperties,initialPOA, traderLookup);
  	    AdminProxyImpl adminProxyImpl = new AdminProxyImpl(traderProperties,initialPOA,traderAdmin);
    	    ServiceTypeRepositoryProxyImpl serviceTypeRepositoryProxyImpl = new ServiceTypeRepositoryProxyImpl(traderProperties,initialPOA,traderServiceTypeRepos);
	    RegisterProxyImpl registerProxyImpl = new RegisterProxyImpl(traderProperties,initialPOA,traderRegister);

	    // Construct Tie objects
	    lookupProxyTie = new POA_Lookup_tie( lookupProxyImpl, initialPOA );
	    adminProxyTie = new POA_Admin_tie( adminProxyImpl, initialPOA );
	    registerProxyTie = new POA_Register_tie( registerProxyImpl, initialPOA );
	    serviceTypeRepositoryProxyTie = new POA_ServiceTypeRepository_tie( serviceTypeRepositoryProxyImpl, initialPOA );
	    Logger.debug("tie objects instantiated\n" + trader);

            // activate with orb
            String TraderServerName = System.getProperty("Trader.ServerName","TradingService");
	    initialPOA.activate_object_with_id(TraderServerName.getBytes(),lookupProxyTie);
	    initialPOA.activate_object_with_id("TradingService.Admin".getBytes(),adminProxyTie);
	    initialPOA.activate_object_with_id("TradingService.Register".getBytes(),registerProxyTie);
	    initialPOA.activate_object_with_id("TradingService.ServiceTypeRepository".getBytes(),serviceTypeRepositoryProxyTie);

            //  connect all the references between implementations  

            //set Admin refs
            Admin adminTemp = adminProxyTie._this(orb);
            lookupProxyImpl.setAdmin_if(adminTemp);
            registerProxyImpl.setAdmin_if(adminTemp);

            //set Register refs
            Register registerTemp = registerProxyTie._this(orb);
            lookupProxyImpl.setRegister_if(registerTemp);
            adminProxyImpl.setRegister_if(registerTemp);

            //set ServiceTypeRepository refs
            ServiceTypeRepository serviceTypeRepositoryTemp = serviceTypeRepositoryProxyTie._this(orb);
            lookupProxyImpl.type_repos(serviceTypeRepositoryTemp);
            adminProxyImpl.type_repos(serviceTypeRepositoryTemp);
            registerProxyImpl.set_type_repos(serviceTypeRepositoryTemp);


            //Activating the RootPOA .
            rootPOA.the_POAManager().activate();

            Logger.debug("references set" );
            Logger.debug(getSysProp("PrimaryService"));

            if (getSysProp("PrimaryService") != null){
                initialPOA.the_POAManager().activate();
		org.omg.CORBA.Object obj = lookupProxyTie._this(orb);
                if (getSysProp("ORB.InitRefURL")!= null && getSysProp("ORB.InitRefURL").trim().length() > 2){
		    Logger.debug("writing server ref to file...");
		    writeLookupRef(orb.object_to_string(obj));
	        }
		Logger.debug("\n Trader Server Proxy in Active Mode");

	    } else {

           	if ("ACTIVE".equals(System.getProperty("ORB.Servant.InitialPOAState","DISCARDING"))) {
                	initialPOA.the_POAManager().activate();
                  	Logger.debug("\n Trader Server proxy in Slave Mode. Accepting Requests. POA State : ACTIVE");
                } else {
                  	// Transitioning into Discarding State.
                  	initialPOA.the_POAManager().discard_requests(false);
                  	Logger.debug("\n Trader Server in Slave Mode. Discarding Requests. POA State : DISCARDING");
                }
            }

		// fix for event transport profile .
            resolveTransport();

            /* Calling the Orb.run() . */ 

            ((com.cboe.ORBInfra.ORB.Orb) orb).run();

        } catch(WrongPolicy wp) {
		Logger.debug("The exception is: ", wp);
	    	System.exit(1);
	}
	catch(AdapterInactive ai) {
		Logger.debug("The exception is: ", ai);
	    	System.exit(1);
        } catch(ServantAlreadyActive saa) {
		Logger.debug("The exception is: ", saa);
	    	System.exit(1);
	} catch(ObjectAlreadyActive oaa) {
		Logger.debug("The exception is: ", oaa);
  		System.exit(1);
        } catch(org.omg.CORBA.SystemException se) {
		Logger.debug("The exception is: ", se);
	    	System.exit(1);
        } catch(Throwable t) {
		Logger.debug("The exception is: ", t);
	    	System.exit(1);
	}

	Logger.debug("Server exiting....");
    }

	/*
	* This method will get the actual references of the trader
	* service
	* @param com.cboe.ORBInfra.ORB.Orb
	* @return boolean
	*/
	public static boolean initializeTradingObjects(ORB _orb)
	{
	 	CBOETradingLocatorBinder binder = new CBOETradingLocatorBinder( _orb );
         	if ( binder == null )
         	{
         	   Logger.debug("TraderServerProxy: unable to obtain TradingBinder reference." );
               return false;
          	}
          	traderLookup = binder.getLookup();
          	traderServiceTypeRepos = binder.getRepository();
          	traderRegister = binder.getRegister();
          	traderAdmin = binder.getAdmin();
          	Logger.debug("traderLookup ref is " + traderLookup);
          	boolean result = true;
          	if(traderLookup == null) result = false;
          		Logger.debug("serviceTypeRepos ref is " + traderServiceTypeRepos);
          	if(traderServiceTypeRepos == null) result = false;
          	Logger.debug("register ref is " + traderRegister);
          	if(traderRegister == null) result = false;
          		return result;

	}
	
        /**
         *  This method gets the properties from the System Management Adapter
	 */
	public static Properties getPropertiesFromSMA (Properties props) {
	        try {
	            Logger.debug("starting getPropertiesFromSMA....");
	            // Get SMA singleton
                    SystemManagementAdapter systemManagementAdapter = SystemManagementAdapter.getInstance();

                    // Get the Process MBean.
                    ProcessBean = systemManagementAdapter.getMBean("Process");

			
    		} catch(ClassNotFoundException ex) {
    		    System.out.println(ex.toString());
    		    System.exit(1);
    		} catch(InstantiationException ex) {
    		    System.out.println(ex.toString());
    		    System.exit(1);
    		} catch(IllegalAccessException ex) {
    		    System.out.println(ex.toString());
    		    System.exit(1);
    		} catch(InstanceNotFoundException ex) {
    		    System.out.println(ex.toString());
    		    System.exit(1);
                } catch(Exception ex) {
    		    ex.printStackTrace();
    		    System.exit(1);
                }
            return props;
	}

	/**
	    This method creates the POA used for the Lookup interface
	*/
	public static POA createInitialPOA (String bindOrder, ORB orb) {

	   	Logger.debug("entering createInitialPOA....");
	    	org.omg.CORBA.Policy[] policies = null;
	    	try {
	        	org.omg.CORBA.Object obj = orb.resolve_initial_references("RootPOA");
	        	rootPOA = POAHelper.narrow(obj);
	        	//create lifespan policy
	        	policies = new org.omg.CORBA.Policy[4];

                	if (bindOrder.equals("SWUL")) {
				Logger.debug("BindOrder Value is SWUL = " + bindOrder);
				org.omg.CORBA.Any policyType = orb.create_any();
				OrderValue ov = new OrderValue(SWUL.value,"","");
				policyType.create_output_stream();
				policyType.insert_Streamable(new OrderValueHolder(ov));
	                    	policies[0] = OrbAux.create_policy(BIND_ORDER_POLICY_TYPE.value ,policyType);
			}else{
				Logger.debug("BindOrder Value is not SWUL = " + bindOrder);
                               /* Throw and Invalid Bind Order Exception*/
				Logger.debug("Shutting Down Trader. Invalid BindOrder Policy. bindOrderValue : " + bindOrderValue);
                System.exit(1);

            }

	        	org.omg.CORBA.Any a  = orb.create_any();
	        	a.insert_boolean(true);
	        	policies[1] = com.cboe.ORBInfra.ORB.OrbAux.create_policy(com.cboe.ORBInfra.PolicyAdministration.INITIAL_REFERENCE_POA_TYPE.value, a);
	        	policies[2] = rootPOA.create_id_assignment_policy(org.omg.PortableServer.IdAssignmentPolicyValue.USER_ID);
	        	policies[3] = rootPOA.create_lifespan_policy(LifespanPolicyValue.PERSISTENT);
			Logger.debug("after creating policiies");
	     	   	initialPOA =  rootPOA.create_POA("TradingService", null, policies);
			Logger.debug("after creating POA");

	    	} catch (org.omg.CORBA.ORBPackage.InvalidName in) {
			in.printStackTrace();
			Logger.debug("The exception is: ", in);
	        	System.exit (1);
	    	} catch (org.omg.PortableServer.POAPackage.InvalidPolicy ip) {
			ip.printStackTrace();
			Logger.debug("The exception is: ", ip);
	        	System.exit (1);
	    	} catch (org.omg.PortableServer.POAPackage.AdapterAlreadyExists aae) {
			aae.printStackTrace();
			Logger.debug("The exception is: ", aae);
	        	System.exit (1);
	    	} catch (Throwable ex) {
	        	ex.printStackTrace();
			Logger.debug("The exception is: ", ex);
	        	System.exit (1);
	    	}

	    	return initialPOA;
	}

	/**
	Write the reference for the lookup object
	@param objString - the ior
	*/
    private static void writeLookupRef(String  objString){
            	try {
                	InitRefUtil.write( getSysProp("ORB.InitRefURL"), "TradingService", objString);
		} catch(FileNotFoundException fnf){
			Logger.debug("The exception is: ", fnf);
			System.out.println("exception :"+fnf.toString());
			System.exit(1);

		} catch(IOException iox){
			Logger.debug("The exception is: ", iox);
			System.out.println("exception :"+iox.toString());
			System.exit(1);
	        }
		catch(Throwable t){
		    	t.printStackTrace();
			Logger.debug("The exception is: ", t);
		  	System.exit(1);
	        }

	  }


    /* Use this when you want to exit the system (instead of a null pointer)from a non-existant property
    * Otherwise get the property value from the System Property
    * @param propName - the name of the property
    * @return the string value of the property
    */
    private static String getSysProp(String propName) {
        String returnVal = null;
        try {
            returnVal = (String)traderProperties.get(propName);
        }
        catch (NullPointerException np) {
		Logger.debug("The exception is: ", np);
            System.out.println("Shutting Down Trader...Cannot find System Property:" +propName +" exception:" + np.toString());
            System.exit(1);
        }
        return returnVal;
    }

    // Temporary Fix for Event Transport
    //--------------

    private static void resolveTransport(){
        try {
		org.omg.CORBA.Object consTransRef = orb.resolve_initial_references("EventTransport");
        } catch(org.omg.CORBA.BAD_INV_ORDER bioXcpt) {
            	bioXcpt.printStackTrace();
            	System.exit(-1);
        } catch(org.omg.CORBA.ORBPackage.InvalidName inXcpt) {
            	inXcpt.printStackTrace();
            	System.exit(-2);
        }
    }

    public static void TogglePOAstate ( String POAcommand) {

      if (POAcommand.equals("activate")) {
        Logger.debug("Swicthing the POA to ACTIVE state...");
	try {
        	initialPOA.the_POAManager().activate();
	} catch(AdapterInactive ai) {
		Logger.debug("The exception is: ", ai);
	}

      } else if (POAcommand.equals("deactivate")) {
        Logger.debug("Swicthing the POA to DISCARDING state...");
	try {
        	initialPOA.the_POAManager().discard_requests(true);
	} catch(AdapterInactive ai) {
		Logger.debug("The exception is: ", ai);
	}

      } else {
        Logger.debug("UnKnown State Change Request. Command Invoked: " + POAcommand);
      }

    }


   class POAStateToggleInvocationListener implements MethodInvocationListener {


	public void methodInvoked(MethodInvocationEvent evt) {
		String msgSource = "TradeServerProxy.POAStateToggleInvocationListener.methodInvoked()";

		MBean source = (MBean)evt.getSource();
		String commandName = source.getName();
		try  {
			TogglePOAstate(commandName);
		}
		catch ( Throwable tps)  {
			StringWriter sw = new StringWriter();
			tps.printStackTrace(new PrintWriter(sw));
			String msg = msgSource + "\n" + tps.getMessage() + "\n" + sw.toString();
			RuntimeException re = new RuntimeException(msg);
			throw re;
		}
	}
  }


}
