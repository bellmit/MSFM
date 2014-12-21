package com.cboe.directoryService;

import java.util.*;
import com.cboe.ORBInfra.ORB.Orb;
import com.cboe.infrastructureUtility.InitRefUtil;
import org.omg.CosTrading.*;
import org.omg.CosTrading.LookupPackage.*;
import org.omg.CosTradingRepos.*;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.*;
import com.cboe.ORBInfra.ORB.*;
import com.cboe.ORBInfra.IOPImpl.BinaryObjectKey;
import com.cboe.EventService.Transport.EventProfileImpl;
import com.cboe.ORBInfra.TIOP.TIOPProfileImpl;
import com.cboe.ORBInfra.PortableServer.POAObjectKey;
import com.cboe.ORBInfra.PortableServer.POAObjectId;
import com.cboe.ORBInfra.PolicyAdministration.BindOrderImpl;
import com.cboe.ORBInfra.PolicyAdministration.OrderValue;
import com.cboe.ORBInfra.PolicyAdministration.TOFT;
import com.cboe.ORBInfra.PolicyAdministration.LOFT;
import com.cboe.ORBInfra.PolicyAdministration.SOFT;
import com.cboe.ORBInfra.PolicyAdministration.SWUL;
import com.cboe.EventService.Transport.EventServiceTransport;
import com.cboe.EventService.Transport.EventServiceTransportHelper;

/**
 * DiffTraders.java
 *
 *
 * Created: Mon Apr 23 11:33:19 2001
 *
 * @author Kevin Yaussy
 * @version
 */

public class DiffTraders {
	
	private static final String ECSVCTYPENAME = "EventChannel";
	private static final String SVCTYPEREPOS = "IDL:omg.org/CosTradingRepos/ServiceTypeRepository:1.0";

	private static DiffTraders diff;

	private boolean debug = false;
	private String tabDiffs = "";
	private Orb myOrb;
	private Lookup primaryLookupStub;
	private ServiceTypeRepository primaryTypeStub;
	private Lookup secondaryLookupStub;
	private ServiceTypeRepository secondaryTypeStub;
	private ArrayList commonTypes = new ArrayList();

	public DiffTraders() throws Throwable {
		myOrb = (com.cboe.ORBInfra.ORB.Orb)Orb.init();
		org.omg.CORBA.Object esTransportRef = 
			myOrb.resolve_initial_references("EventTransport");
		EventServiceTransport esTransport = 
			EventServiceTransportHelper.narrow(esTransportRef);
		esTransport.setup();

		debug = (System.getProperty( "DebugOn" ) != null );
		if ( debug )
			tabDiffs = "\t";
	}
	
	public static void main(String[] args) {
		try {
			diff = new DiffTraders();
			diff.parseArgs( args );

			diff.setStubs();

			diff.diffTypes( );
			diff.diffOffers( );
		} catch ( Throwable e ) {
			System.out.println( e );
		}
		finally {
			diff.myOrb.shutdown( true );

			System.exit( 0 );
		}
	}

	/**
	 * Returns a stub object to the requested Trader Lookup interface.  The URL
	 * is used to find the trader service - we don't use resolve_initial_reference.
	 * This way we can get both the primary and secondary trader service references.
	 * The URL can be an FTP reference as well.
	 *
	 * @param iorURL a <code>String</code> value
	 * @param serviceName a <code>String</code> value
	 * @return a <code>Lookup</code> value
	 */
	private Lookup getTraderStub( String iorURL, String serviceName ) {
		Properties props = InitRefUtil.load( iorURL );

		String ior = (String)props.getProperty( serviceName );
		if ( ior == null ) {
			throw new java.lang.IllegalArgumentException("Invalid trader service name: " + serviceName );
		}

		org.omg.CORBA.Object obj = myOrb.string_to_object( ior );
		return LookupHelper.narrow( obj );
	}

	/**
	 * This method obtains the regular InitRefURL as well as the SecInitRefURL.
	 * It gets the primary trader (using InitRefURL) and the secondary trader
	 * (using the SecInitRefURL).  Both the Lookup and ServiceTypeRepository
	 * interfaces are obtained.
	 *
	 */
	private void setStubs( ) {
		org.omg.CORBA.Object obj;

		String primaryIORURL = System.getProperty( "ORB.InitRefURL" );
		if ( primaryIORURL == null || primaryIORURL.length() == 0 ) {
			throw new java.lang.IllegalArgumentException("No ORB.InitRefURL property." );
		}
		if ( debug )
			System.out.println("Connecting to primary trader at URL(" + primaryIORURL + ")...");
		primaryLookupStub = getTraderStub( primaryIORURL, "TradingService" );
		obj = primaryLookupStub.type_repos();
		primaryTypeStub = ServiceTypeRepositoryHelper.narrow( obj );

		String secondaryIORURL = System.getProperty( "ORB.SecInitRefURL" );
		if ( secondaryIORURL == null || secondaryIORURL.length() == 0 ) {
			throw new java.lang.IllegalArgumentException("No ORB.SecInitRefURL property." );
		}
		if ( debug )
			System.out.println("Connecting to secondary trader at URL(" + secondaryIORURL + ")...");
		secondaryLookupStub = getTraderStub( secondaryIORURL, "TradingServiceSecondary" );
		obj = secondaryLookupStub.type_repos();
		secondaryTypeStub = ServiceTypeRepositoryHelper.narrow( obj );
	}

	/**
	 * This method checks the status of the service types.  It will diff the service type
	 * lists, as well as the definition of the service types (for the service types the
	 * two traders have in common).
	 *
	 */
	private void diffTypes( ) throws Exception {
		ArrayList tempCommonTypes = new ArrayList();

		SpecifiedServiceTypes specifiedTypes = new SpecifiedServiceTypes(ListOption.all);
		String[] primaryTypes = primaryTypeStub.list_types( specifiedTypes );
		if ( primaryTypes == null || primaryTypes.length == 0 ) {
			System.out.println("Primary contains no service types.");
			return;
		}
		String[] secondaryTypes = secondaryTypeStub.list_types( specifiedTypes );
		if ( secondaryTypes == null || secondaryTypes.length == 0 ) {
			System.out.println("Secondary contains no service types.");
			return;
		}

		Arrays.sort( primaryTypes );
		Arrays.sort( secondaryTypes );

		// First, let's see if there are any types in primary which are not in
		// secondary.
		if ( debug )
			System.out.println( "Service-types in primary(" + primaryTypes.length + ") " +
							"trader not in secondary(" + secondaryTypes.length + "):" );
		for ( int i = 0; i < primaryTypes.length; i++ ) {
			int idx = Arrays.binarySearch( secondaryTypes, primaryTypes[i] );
			if ( idx < 0 ) {
				System.out.println( tabDiffs + "DIFF type not in secondary: " + primaryTypes[i] );
			}
			else {
				// Skip the TypeRepos type...
				if ( !primaryTypes[i].equals( SVCTYPEREPOS ) )
					tempCommonTypes.add( primaryTypes[i] ); // Hold on to it for later...
			}
		}

		// Second, let's see if there are any types in secondary which are not in
		// primary.  If the two lists are of equal size, then there won't be any
		// matches in this category, so don't populate the commonTypes.
		if ( debug )
			System.out.println( "Service-types in secondary(" + secondaryTypes.length + ") " +
							"trader not in primary(" + primaryTypes.length + "):" );
		for ( int i = 0; i < secondaryTypes.length; i++ ) {
			int idx = Arrays.binarySearch( primaryTypes, secondaryTypes[i] );
			if ( idx < 0 ) {
				System.out.println( tabDiffs + "DIFF type not in primary: " + secondaryTypes[i] );
			}
		}

		// Last, we compare the common types to see if they are defined differently.
		// If a type does not compare, then remove it from the master common list.
		commonTypes.addAll( tempCommonTypes );
		if ( debug )
			System.out.println("Discrepancies in definition of common types:");
		for ( int i = 0; i < tempCommonTypes.size(); i++ ) {
			String typeName = (String)tempCommonTypes.get( i );
			// Sanity check more to protect trader than me...
			if ( typeName == null || typeName.length() == 0 ) {
				System.out.println("Type-name from common list null or empty-string!");
				continue;
			}

			TypeStruct primaryTypeDef = null;
			TypeStruct secondaryTypeDef = null;
			try {
				primaryTypeDef = primaryTypeStub.describe_type( typeName );
			} catch ( Exception e ) {
				System.out.println("Exception during describe_type(primary) for: " + typeName );
				throw e;
			}

			try {
				secondaryTypeDef = secondaryTypeStub.describe_type( typeName );
			} catch ( Exception e ) {
				System.out.println("Exception during describe_type(secondary) for: " + typeName );
				throw e;
			}

			String typeDiffDescription = getTypeDiffDescription( typeName, primaryTypeDef, secondaryTypeDef );
			if ( typeDiffDescription != null ) {
				// If there is a difference, remove this type from the master list.
				// The master common-type list is used for comparing offers and
				// there isn't any point in comparing offers for a type which has
				// different properties, etc.
				commonTypes.remove( commonTypes.indexOf( typeName ) );
				System.out.println( typeDiffDescription );
			}
		}
	}

	/**
	 * This method looks at the specifics of the service-type
	 * definitions and compares primary to secondary.  Differences
	 * are returned in a printable string.
	 *
	 * @param typeName a <code>String</code> value
	 * @param primaryTypeDef a <code>TypeStruct</code> value
	 * @param secondaryTypeDef a <code>TypeStruct</code> value
	 * @return a <code>String</code> value
	 */
	private String getTypeDiffDescription( String typeName,
								    TypeStruct primaryTypeDef,
								    TypeStruct secondaryTypeDef ) {
		StringBuffer diffDesc = new StringBuffer();
		boolean diff = false;

		diffDesc.append( tabDiffs + "DIFF type definitions != for " + typeName + ":\n" );
		if ( !primaryTypeDef.if_name.equals( secondaryTypeDef.if_name ) ) {
			diff = true;
			diffDesc.append( tabDiffs + "\tif_name_primary(" + primaryTypeDef.if_name + ") != " +
						  "if_name_secondary(" + secondaryTypeDef.if_name + ")\n" );
		}

		if ( !(primaryTypeDef.incarnation.high == secondaryTypeDef.incarnation.high &&
			  primaryTypeDef.incarnation.low == secondaryTypeDef.incarnation.low) ) {
			diff = true;
			diffDesc.append( tabDiffs + "\tincarnation_primary(" + primaryTypeDef.incarnation.high +
						  " " + primaryTypeDef.incarnation.low + ") != " +
						  "incarnation_secondary(" + secondaryTypeDef.incarnation.high +
						  " " + secondaryTypeDef.incarnation.low + ")\n" );
		}

		// I am going to assume for now that if a property name exists in both primary
		// and secondary, they are equivalent - I will not compare the rest of the
		// property information.
		// To compare the properties, it may be the case that the property sets may be
		// returned in different orders.  Create a sorted-list for both primary and
		// secondary property sets.  Stuff the names into a string for each and
		// compare the two strings.
		TreeSet primaryPropNames = new TreeSet();
		TreeSet secondaryPropNames = new TreeSet();
		for ( int i = 0; i < primaryTypeDef.props.length; i++ )
			primaryPropNames.add( primaryTypeDef.props[i].name );
		for ( int i = 0; i < secondaryTypeDef.props.length; i++ )
			secondaryPropNames.add( secondaryTypeDef.props[i].name );
		
		String primaryPropStr = primaryPropNames.toString();
		String secondaryPropStr = secondaryPropNames.toString();
		if ( !primaryPropStr.equals( secondaryPropStr ) ) {
			diff = true;
			diffDesc.append( tabDiffs + "\tprops_primary(" + primaryPropStr + ") != " +
						  "props_secondary(" + secondaryPropStr + ")\n" );
		}

		if ( diff )
			return diffDesc.toString();
		else
			return null;
	}

	/**
	 * Checks for differences in offers of common service-types.
	 * For now, I am taking a sort-of odd approach to this.  Instead
	 * of picking through the guts and details of the offers and 
	 * attempting to make a comparison, I will just create an array of
	 * printable strings describing the pertinent details of the offers
	 * for a given common service-type for both primary and secondary.
	 * These arrays are then sorted and then I walk through each comparing
	 * with the other array - i.e. primary to secondary, then secondary to
	 * primary.  If I find any "offer strings" not in a list, then it is
	 * considered a difference.  Hopefully this approach proves to be
	 * "enough" to note any differences.
	 *
	 * @exception Throwable if an error occurs
	 */
	private void diffOffers( ) throws Throwable {

		for ( int i = 0; i < commonTypes.size(); i++ ) {
			String typeName = (String)commonTypes.get( i );

			OfferSeqHolder primaryOffers = new OfferSeqHolder( new Offer[0] );
			OfferSeqHolder secondaryOffers = new OfferSeqHolder( new Offer[0] );
			OfferIteratorHolder offerItr= new OfferIteratorHolder();
			PolicyNameSeqHolder limits = new PolicyNameSeqHolder( new String[0]);
			SpecifiedProps specProps = new SpecifiedProps(HowManyProps.all);

			primaryLookupStub.query( typeName, "", "", new org.omg.CosTrading.Policy[0],
								specProps, 0, primaryOffers, offerItr, limits );

			secondaryLookupStub.query( typeName, "", "", new org.omg.CosTrading.Policy[0],
								specProps, 0, secondaryOffers, offerItr, limits );

			DiffTradersOfferHolder[] primaryOffersArr = null;
			DiffTradersOfferHolder[] secondaryOffersArr = null;
			if ( typeName.equals( ECSVCTYPENAME ) ) {
				primaryOffersArr = getECOfferStrings( primaryOffers );
				secondaryOffersArr = getECOfferStrings( secondaryOffers );
			}
			else {
				primaryOffersArr = getOfferStrings( primaryOffers );
				secondaryOffersArr = getOfferStrings( secondaryOffers );
			}
			Arrays.sort( primaryOffersArr );
			Arrays.sort( secondaryOffersArr );

			boolean printFirstLine = true;
			for ( int j = 0; j < primaryOffersArr.length; j++ ) {
				int idx = Arrays.binarySearch( secondaryOffersArr, primaryOffersArr[j] );
				if ( idx < 0 ) {
					if ( printFirstLine ) {
						System.out.println("Discrepancies in offers of " + typeName );
						printFirstLine = false;
					}
					System.out.println("\tDIFF primary offer not in secondary: " + primaryOffersArr[j].offerString );
				}
			}

			for ( int j = 0; j < secondaryOffersArr.length; j++ ) {
				int idx = Arrays.binarySearch( primaryOffersArr, secondaryOffersArr[j] );
				if ( idx < 0 ) {
					if ( printFirstLine ) {
						System.out.println("Discrepancies in offers of " + typeName );
						printFirstLine = false;
					}
					System.out.println("\tDIFF secondary offer not in primary: " + secondaryOffersArr[j].offerString );
				}
			}

		}


	}

	/**
	 * Creates an array of strings describing event channel offers.
	 *
	 * @param offers an <code>OfferSeqHolder</code> value
	 * @return a <code>DiffTradersOfferHolder[]</code> value
	 * @exception Throwable if an error occurs
	 */
	private DiffTradersOfferHolder[] getECOfferStrings( OfferSeqHolder offers ) throws Throwable {
		DiffTradersOfferHolder[] retArr = new DiffTradersOfferHolder[offers.value.length];
		StringBuffer s = new StringBuffer();

		for ( int i = 0; i < offers.value.length; i++ ) {
			com.cboe.ORBInfra.IOPImpl.IORImpl ior = ( (com.cboe.ORBInfra.ORB.DelegateImpl) ((org.omg.CORBA.portable.ObjectImpl) offers.value[i].reference)._get_delegate() ).getIOR();

			EventProfileImpl eProfile;
			eProfile = (EventProfileImpl)ior.getProfile( EventProfileImpl.tag );

			s.setLength( 0 );
			s.append( eProfile.getChannelName() );
			s.append( "," + new Boolean( eProfile.isNotifyChannel() ).toString() );
			s.append( "," + new Boolean( eProfile.isChannelActive() ).toString() );
			s.append( "," + eProfile.qosToString( eProfile.getQoS() ) );

			retArr[i] = new DiffTradersOfferHolder( s.toString(), ior.toString(), offers.value[i] );
		}

		return retArr;
	}

	/**
	 * Creates an array of strings describing the offers for a given 
	 * service-type.
	 *
	 * @param offers an <code>OfferSeqHolder</code> value
	 * @return a <code>DiffTradersOfferHolder[]</code> value
	 * @exception Throwable if an error occurs
	 */
	private DiffTradersOfferHolder[] getOfferStrings( OfferSeqHolder offers ) throws Throwable {
		DiffTradersOfferHolder[] retArr = new DiffTradersOfferHolder[offers.value.length];
		StringBuffer s = new StringBuffer();

		for ( int i = 0; i < offers.value.length; i++ ) {
			com.cboe.ORBInfra.IOPImpl.IORImpl ior = ( (com.cboe.ORBInfra.ORB.DelegateImpl) ((org.omg.CORBA.portable.ObjectImpl) offers.value[i].reference)._get_delegate() ).getIOR();

			TIOPProfileImpl tProfile;
			tProfile = (TIOPProfileImpl)ior.getProfile( TIOPProfileImpl.tag );
			POAObjectKey pKey = tProfile.getObjectKey().getPOAObjectKey();
			String poaName = getPOAName( pKey );
			String isPersRef = getIsPersistent( pKey );
			String bOrder = getBindOrder( tProfile );
			String props = getOfferProps( offers.value[i].properties );

			s.setLength( 0 );
			s.append( tProfile.getUniqueName() );
			s.append( "," + tProfile.getHost() );
			s.append( "," + new Integer( tProfile.getPort() ).toString() );
			s.append( "," + poaName );
			s.append( "," + isPersRef );
			s.append( "," + bOrder );
			s.append( "," + props );

			retArr[i] = new DiffTradersOfferHolder( s.toString(), ior.toString() + "," + props, offers.value[i] );
		}

		return retArr;
	}


	// These get methods were taken from TestTraderFacade...


	private String getPOAName( POAObjectKey pKey ) {
		StringBuffer buff = new StringBuffer();
		String[] pNames = pKey.poaName();
		for (int i = 0; i < pNames.length; i++) {
			buff.append("/");
			buff.append(pNames[i]);
		}

		return buff.toString();
	}

	private String getIsPersistent( POAObjectKey pKey ) {
		byte[] transId = pKey.transId();
		return (transId == null || transId.length == 0) ? "true" : "false";
	}

	private String getBindOrder( TIOPProfileImpl tProfile ) {
		org.omg.CORBA.Policy[] policies = tProfile.getPolicyList();

		if ( policies == null || policies.length == 0 )
			return "NONE";
		for ( int i = 0; i < policies.length; i++ ) {
			if ( policies[i] instanceof BindOrderImpl ) {
				BindOrderImpl bOrder = (BindOrderImpl)policies[i];
				if ( bOrder.order_value().bindType == LOFT.value )
					return "LOFT";
				else if ( bOrder.order_value().bindType == TOFT.value )
					return "TOFT";
				else if ( bOrder.order_value().bindType == SOFT.value )
					return "SOFT";
				else if ( bOrder.order_value().bindType == SWUL.value )
					return "SWUL";
				else return "????";
			}
		}

		return "NONE";
	}

	private String getOfferProps( org.omg.CosTrading.Property[] props ) {
		StringBuffer buff = new StringBuffer();

		if ( props.length == 0 )
			return "";

		for (int i = 0; i < props.length ; i++){
			String propName = props[i].name;
			org.omg.CORBA.Any tempAny = myOrb.create_any();
			tempAny = props[i].value;
			String value = tempAny.extract_string();
			buff.append( propName + "=" + value + "," );
		}
		buff.setLength( buff.length() - 1 ); // trim off last comma
		return buff.toString();
	}


	private void parseArgs( String[] args ) {
		boolean usageNeeded = false;
		boolean done = false;
		int i = 0;

		if ( args == null || args.length == 0 ) {
			return;
		}

		while ( !done && i < args.length ) {
			if ( args[i].equalsIgnoreCase("-h") ||
				args[i].equalsIgnoreCase("-help") ||
				args[i].equalsIgnoreCase("-?") ) {
				usageNeeded = true;
				done = true;
			}
			else {
				System.out.println("Invalid option: " + args[i] );
				usageNeeded = true;
				done = true;
			}
			i++;
		}

		if ( usageNeeded ) {
			showUsage();
			System.exit( 1 );
		}
	}

	private void showUsage() {
		System.out.println("Usage: [-help]" );
		System.out.println("\t-help:\t\tPrints this message.");
	}


	class DiffTradersOfferHolder implements Comparable {
		public String offerString;
		public String comparableString;
		public Offer actualOffer;

		public DiffTradersOfferHolder( String offerString, String comparableString, Offer actualOffer ) {
			this.offerString = offerString;
			this.comparableString = comparableString;
			this.actualOffer = actualOffer;
		}

		public int compareTo( Object o ) {
			return comparableString.compareTo(((DiffTradersOfferHolder)o).comparableString);
		}
	}

} // DiffTraders
