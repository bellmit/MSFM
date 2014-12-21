package com.cboe.infrastructureServices.traderService;

import java.util.Properties;

import org.omg.CORBA.SystemException;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextHelper;
import org.omg.CosNaming.NamingContextPackage.AlreadyBound;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.CosTrading.Offer;
import org.omg.CosTrading.OfferSeqHolder;

import com.cboe.infrastructureServices.orbService.OrbServiceBaseImpl;

/**
 * A trader service impl that uses the Name Service.
 * @version 1.1
 */
public class NsTraderServiceImpl extends TraderServiceBaseImpl
{
	private static final String CONTEXT_NAME = "TraderInfo";
	private NamingContext rootContext = null;
	private NamingContext traderContext = null;

	/**
	 * @fixme Do we need to implement this method?
	 */
    public void withdraw( String type, String constraints )
    {
    }

	public String export(org.omg.CORBA.Object objectRef, String serviceTypeName, String constraints)
	{
		System.out.println("Tader Service is exporting " + serviceTypeName + " constraints " + constraints);
		// Make sure the serviceTypeContext exists
	    NameComponent[] serviceTypeContext = new NameComponent[2] ;
		serviceTypeContext[0] = new NameComponent (CONTEXT_NAME,"");
		serviceTypeContext[1] = new NameComponent (serviceTypeName,"");
		try {
			getRootContext().bind_new_context( serviceTypeContext);
		} catch( NotFound ex ) {
		} catch ( CannotProceed ex ) {
		} catch ( InvalidName ex ) {
		} catch ( AlreadyBound ex ) {
			// Ignore this error, just override
		}
			
	    NameComponent[] offerContext = new NameComponent[3] ;
		offerContext[0] = new NameComponent (CONTEXT_NAME,"");
		offerContext[1] = new NameComponent (serviceTypeName,"");
		if ( constraints == null) 
			offerContext[2] = new NameComponent ("*","");
		else            
			offerContext[2] = new NameComponent (constraints,"");
			
		boolean rebindFlag = false;
		try {
			getRootContext().bind(offerContext, objectRef);
		} catch( NotFound se ) {
			System.out.println ("Exception - NotFound : " + se.toString());
			System.exit(1);
		} catch( CannotProceed se ) {
			System.out.println ("Exception - CannotProceed : " + se.toString());
			System.exit(1);
		} catch( InvalidName se ) {
			System.out.println ("Exception - InvalidName : " + se.toString());
			System.exit(1);
		} catch( AlreadyBound se ) {
			// System.out.println ("AlreadyBound, replacing existing entry");
			rebindFlag = true;
		}
		
		if ( rebindFlag == true ) {
			try {
				getRootContext().rebind(offerContext, objectRef);
			} catch( NotFound se ) {
				System.out.println ("Exception - NotFound : " + se.toString());
				System.exit(1);
			} catch( CannotProceed se ) {
				System.out.println ("Exception - CannotProceed : " + se.toString());
				System.exit(1);
			} catch( InvalidName se ) {
				System.out.println ("Exception - InvalidName : " + se.toString());
				System.exit(1);
			} 
		}
		return OrbServiceBaseImpl.getInstance().getOrb().object_to_string(objectRef);
	}
	/**
	 * This version of query only return one Offer.  But potentially, this should return
	 * all the offers matched with the constraints
	 */
	public OfferSeqHolder query(String serviceTypeName, String constraints)
	{
		System.out.println("Tader Service is querying " + serviceTypeName + " constraints " + constraints);
		org.omg.CORBA.Object objRef = null;
	    Offer[] offers = null;

	    NameComponent[] offerContext = new NameComponent[3] ;
		offerContext[0] = new NameComponent (CONTEXT_NAME,"");
		offerContext[1] = new NameComponent (serviceTypeName,"");
		if ( constraints != null)
			offerContext[2] = new NameComponent (constraints,"");
		else    
			offerContext[2] = new NameComponent ("*","");

		try {
			objRef = getRootContext().resolve(offerContext);
		} catch( NotFound se ) {           
			System.out.println ("Exception - NotFound : " + serviceTypeName );
			return new OfferSeqHolder( new Offer[0] );
		} catch( CannotProceed se ) {
			System.out.println ("Exception - CannotProceed : " + se.toString());
			System.exit(1);
		} catch( InvalidName se ) {
			System.out.println ("Exception - InvalidName : " + se.toString());
			System.exit(1);
		}
	    
	    offers = new Offer[1];
	    offers[0] = new Offer( objRef, new org.omg.CosTrading.Property[0]);
		return new OfferSeqHolder( offers );

	}
	/**
	 */
	public NamingContext getRootContext()
	{
	    if ( rootContext != null ) 
	        return rootContext;
	    
	    /*
	    ** NOTE (Yaussy): The code below only works with the naming service
	    ** running on localhost, which may not be the case.  Don't know why
	    ** this code was done, versus the resolve_initial_references coded
	    ** below, which used to be commented out...
		try
		{
  	        rootContext = NamingContextHelper.bind (":NS","localhost");
		}
		catch (SystemException se) {
			System.out.println ("Exception during bind : " + se.toString() );
		}
	    */

		try {
			org.omg.CORBA.Object initNCRef = org.omg.CORBA.ORB.init(new String[0], null ).resolve_initial_references ("NameService");
			rootContext = NamingContextHelper.narrow (initNCRef);
		}
		catch (SystemException se) {
			System.out.println ("Exception during resolve initial reference to the Naming Service : " + se.toString());
			System.exit (1);
		}
		catch (org.omg.CORBA.ORBPackage.InvalidName in) {
			System.out.println ("Exception during narrow of initial reference : " + in.toString());
			System.exit (1);
		}

		// Make sure the trader context is set up
		NameComponent[] NC = new NameComponent[1];
		NC[0]  = new NameComponent (CONTEXT_NAME,"");  
		try {
			traderContext = rootContext.bind_new_context(NC);
			System.out.println("Created a new trader context\n");
		} catch (InvalidName se) {
			System.out.println ("Exception - InvalidName : " + se.toString());
			System.exit(1);
		} catch (CannotProceed se) {
			System.out.println ("Exception - CannotProceed : " + se.toString());
			System.exit(1);
		} catch (NotFound se) {
			System.out.println ("Exception - NotFound : " + se.toString());
			System.exit(1);
		} catch (AlreadyBound se) {
			// Ignore this if it is already there
		} catch(org.omg.CORBA.SystemException  se) {
			System.out.println ("Unexpected exception in bind : " + se.toString());
			System.exit(1);
		}
		
		return rootContext;
	}

	public String export(org.omg.CORBA.Object objectRef, String serviceTypeName, Properties props) {
		return null;
	}

	public String exportPropertiesWithMultipleValues(org.omg.PortableServer.Servant objectRef, org.omg.PortableServer.POA poa, String serviceTypeName, Properties props) {
		return null;
	}

	public String export(org.omg.PortableServer.Servant objectRef, org.omg.PortableServer.POA poa, String serviceTypeName, Properties props) {
		return null;
	}

	public DirectoryQueryResult[] queryDirectory(String serviceTypeName, String constraints) {
		return null;
	}

	public DirectoryQueryResult[] queryDirectoryForEC(String serviceTypeName, String constraints) {
		return null;
	}

	public String describeType( String serviceTypeName ) {
		return null;
	}

	public String[] listTypes() {
		return null;
	}
}
