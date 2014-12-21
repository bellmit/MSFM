package com.cboe.directoryService.persist;

import static com.cboe.directoryService.TraderLogBuilder.format;

import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.omg.CosTrading.IllegalConstraint;
import org.omg.CosTrading.Offer;
import org.omg.CosTrading.Property;
import org.omg.CosTrading.PropertyTypeMismatch;
import org.omg.CosTrading.UnknownOfferId;
import org.omg.CosTrading.UnknownServiceType;
import org.omg.CosTrading.RegisterPackage.OfferInfo;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.IncarnationNumber;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.PropStruct;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.ServiceTypeExists;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.ValueTypeRedefinition;

import com.cboe.common.log.InfraLoggingRb;
import com.cboe.common.log.Logger;
import com.cboe.directoryService.parser.ASTconstraint;
import com.cboe.directoryService.parser.DirectoryConstraintNodeVisitor;
import com.cboe.directoryService.parser.DirectoryParser;
import com.cboe.directoryService.parser.TraderConstraint;
import com.objectwave.persist.Broker;
import com.objectwave.persist.BrokerFactory;
import com.objectwave.persist.QueryException;
import com.objectwave.persist.SQLQuery;
import com.objectwave.transactionalSupport.Session;
import com.objectwave.transactionalSupport.UpdateException;

/**
 * Responsible for business operations on the database for {@link TraderServiceType},
 * {@link TraderProp}, and {@link TraderOffer} objects.
 * 
 */
public class TraderDBUtil
{
	/** reference to the broker */   
	private static Broker broker; 

	/** reference to the singleton */   
	private static TraderDBUtil instance = new TraderDBUtil();
	
	/** default size for our caches */
	private static final int CACHE_SIZE = 2500;

	/** resource bundle used for logging */
	private ResourceBundle rb = null;
    
	/** used to decide which TraderOffer to instantiate */
	private String traderWithBothIORReferences;

	/**
	 *  caches service types to reduce database overhead, even though the DB broker is using caching.
	 *  
	 *  The reason for this is subtle. We are currently locking the traderServiceType DB, we only add to
	 *  this cache in a protected lock. The cached object is fetched before it enters the lock, so this speeds
	 *  us up by not entering the lock. Essentially, the locks mean that caching outside of the database
	 *  has a performance boost, even though there is a second cache inside the DB. We of course
	 *  trader memory for speed here.
	 *  
	 *  
	 */
	private final ConcurrentHashMap<String, TraderServiceType> traderServiceTypeCache; 
	private final ReentrantReadWriteLock traderServiceTypeLock;

	/**
	 * Caches DB records of the offer type, accessed with a simple lock
	 */
	private final ConcurrentHashMap<String,Vector> offerCache;
	private final Lock offerCacheLock;

	
	
	/**
	 * Constructs a typical DB Util.
	*  private because we're singleton access. 
	*/
	private TraderDBUtil()
	{
		Logger.sysNotify(format("TraderDBUtil", "ctor", "building maps"));
		traderServiceTypeCache = new ConcurrentHashMap<String, TraderServiceType>(CACHE_SIZE);
		offerCache = new ConcurrentHashMap<String, Vector>(CACHE_SIZE);

		if (Boolean.getBoolean("useFairLocks")) {
			traderServiceTypeLock = new ReentrantReadWriteLock(true);
		}
		else {
			traderServiceTypeLock = new ReentrantReadWriteLock();
		}
		if (Boolean.getBoolean("useFairLocks")) {
			offerCacheLock = new ReentrantLock(true);
		}
		else {
			offerCacheLock = new ReentrantLock();
		}
		
		
		Logger.sysNotify(format("TraderDBUtil","ctor", "attaching to database"));
		BrokerFactory.useDatabase();
		broker = BrokerFactory.getDatabaseBroker();
		Logger.sysNotify(format("TraderDBUtil","ctor", "Done connecting to db."));
        
		traderWithBothIORReferences = System.getProperty("TraderService.MirroredReferenceColumn", "false");
		Logger.debug("running TraderDBUtil with MirrorColumn set to: " + traderWithBothIORReferences);

		try {
			rb = ResourceBundle.getBundle( InfraLoggingRb.class.getName() );
		}
		catch( Exception e ) {
			Logger.sysAlarm( Logger.createLogMessageId( Logger.getDefaultLoggerName(), "Unable to set Logging ResourceBundle({0}).", "TraderDBUtil", "" ), new Object[] { InfraLoggingRb.class.getName() } );
		}
	}


	/**
	* Accessor for this singleton
	* @return TraderDBUtil reference
	*/
	public static TraderDBUtil getInstance() {
		return instance;
	}

	/**
	* Accessor for Broker reference
	* @return Broker reference
	*/
	public Broker getBroker() {
		return broker;
	}

	/**
	 * The trader service objects {@link TraderServiceType} {@link TraderProp}
	 * {@link TraderPropJoin} should be accessed only through this lock.
	 * 
	 * @return The lock to gain access to TraderService data
	 */
	protected final ReentrantReadWriteLock getTraderServiceTypeLock() {
		return traderServiceTypeLock;
	}
	
	/**
	 * Lock to only be used when accessing or modifying data surrounding the offer cache.
	 * @return
	 */
	protected final Lock getOfferCacheLock() {
		return offerCacheLock;
	}

	/**
	 * Return all Offers for the Service Type, constrained by the filter
	 * 
	 * @param type
	 *            the Service Type name
	 * @param filter
	 *            optional search constraints
	 * @return TraderOffer objects matching type and filter If using a filter then
	 *         UnknownServiceType should not be thrown; just return an empty offer sequence
	 * @exception QueryException
	 *                if there's a database problem
	 * @exception UnknownServiceType
	 *                the service type doesn't exist
	 * @exception PropertyTypeMismatch
	 *                an actual property value is not appropriate for the property type as defined
	 *                by the service type.
	 */
	public TraderOffer[] getTraderOffersForType(String type, String filter)
	throws QueryException, PropertyTypeMismatch, UnknownServiceType, IllegalConstraint
	{
		if (Logger.isLoggable( rb, InfraLoggingRb.METHOD_ENTRY_2, Logger.TRACE ) ) {
			java.lang.Object[] params = { "TraderDBUtil", "getTraderOffersForType", "ServiceTypeName", type, "Search Constraint", filter };
			Logger.traceEntry( rb, InfraLoggingRb.METHOD_ENTRY_2, params);
		}

		if ( getServiceTypeByName(type) == null ) {
			throw new UnknownServiceType(type);
		}

		boolean haveFilters = false;
		TraderConstraint aConstraint = null;

		// build the constraint if a filter string was supplied
		if ( filter != null && filter.length() > 0) {
			DirectoryConstraintNodeVisitor visitor = null;
			try {
				DirectoryParser parser = new DirectoryParser(filter);
				ASTconstraint n = parser.constraint();
				visitor = new DirectoryConstraintNodeVisitor();
				n.jjtAccept(visitor, null);
			}
			catch(Exception e) {
				Logger.sysWarn("Could not parse constraints for " + filter);
				throw new IllegalConstraint(filter);
			}

			aConstraint = visitor.getTraderConstraint();
			haveFilters = aConstraint.haveConstraints();
		}


		TraderOffer[] retVal = null;
		Vector results;
		try{
			getOfferCacheLock().lock();
			results = offerCache.get(type);

			if (results == null) {
				TraderOffer anOffer = TraderOffer.createTraderOffer(traderWithBothIORReferences);
				anOffer.setServiceType(type);
				SQLQuery.setDefaultBroker(broker);
				SQLQuery q = new SQLQuery(anOffer);
				results = (Vector)broker.find(q);
				offerCache.put(type, results);
				Logger.debug("TraderDBUtil.getTraderOffersForType: populate offerCache for serviceType(" + type + ") with " + results.size() + " entries.");
			}
			
		}finally{
			getOfferCacheLock().unlock();
		}


		if ( results != null && !results.isEmpty() ) {
			int resultLen = results.size();
			ArrayList finalResults = new ArrayList();

			// loop over each result
			for (int i=0; i<resultLen; i++) {
				TraderOffer testOffer = (TraderOffer)results.elementAt(i);
				if ( haveFilters ) { // do constraint test
					// keep the result if all the constraints tested true
					if ( aConstraint.evaluateAgainst(testOffer) ) {
						finalResults.add(testOffer);
					}
				}
				else { // no filters, just add to list
					finalResults.add(testOffer);
				}
			}

			if ( !finalResults.isEmpty() ) {
				retVal = new TraderOffer[finalResults.size()];
				finalResults.toArray(retVal);
			}
		}

		if (Logger.isLoggable ( rb, InfraLoggingRb.METHOD_EXIT, Logger.TRACE ) ) {
			java.lang.Object[] params = { "TraderDBUtil", "getTraderOffersForType", "ServiceTypeName", type, "Search Constraint", filter };
			Logger.traceExit( rb, InfraLoggingRb.METHOD_EXIT, params );
		}

		return retVal;
	}

	/**
	 * Return all Offers for the Service Type, constrained by the filter
	 * 
	 * @param type
	 *            the Service Type name
	 * @param filter
	 *            optional search constraints
	 * @return Offers matching type and filter If using a filter, then UnknownServiceType should not
	 *         be thrown; just return an empty offer sequence
	 * @exception QueryException
	 *                if there's a database problem
	 * @exception UnknownServiceType
	 *                the service type doesn't exist
	 * @exception PropertyTypeMismatch
	 *                an actual property value is not appropriate for the property type as defined
	 *                by the service type.
	 * 
	 */
	public Offer[] getOffersForType(String type, String filter)
	throws QueryException, PropertyTypeMismatch, UnknownServiceType, IllegalConstraint
	{
		if (Logger.isLoggable( rb, InfraLoggingRb.METHOD_ENTRY_2, Logger.TRACE ) ) {
			java.lang.Object[] params = { "TraderDBUtil", "getOffersForType", "ServiceTypeName", type, "Search Constraint", filter };
			Logger.traceEntry( rb, InfraLoggingRb.METHOD_ENTRY_2, params);
		}

		Offer[] retVal = null;
		TraderOffer[] results = getTraderOffersForType(type, filter);
		if ( results != null && results.length > 0 ) {
			int len = results.length;
			retVal = new Offer[len];
			for (int i=0; i<len; i++) {
				retVal[i] = results[i].toOffer();
			}
		}

		if (Logger.isLoggable ( rb, InfraLoggingRb.METHOD_EXIT, Logger.TRACE ) ) {
			java.lang.Object[] params = { "TraderDBUtil", "getOffersForType", "ServiceTypeName", type, "Search Constraint", filter };
			Logger.traceExit( rb, InfraLoggingRb.METHOD_EXIT, params );
		}

		return retVal;
	}

	/**
	* Return a list of all Offer IDs in the database
	* @return all OfferIDs
	* @exception QueryException if there's a database problem
	*/
	public String[] getAllOfferIDs()
	throws QueryException
	{
		if (Logger.isLoggable( rb, InfraLoggingRb.METHOD_ENTRY_0, Logger.TRACE ) ) {
			java.lang.Object[] params = { "TraderDBUtil", "getAllOfferIDs" };
			Logger.debug( rb, InfraLoggingRb.METHOD_ENTRY_0, params);
		}

		// Not sure if this method is used, really.  Not going to bother using the offerCache for this...
		TraderOffer anOffer = TraderOffer.createTraderOffer(traderWithBothIORReferences);
		SQLQuery.setDefaultBroker(broker);
		SQLQuery q = new SQLQuery(anOffer);
		String[] retVal = null;
		Vector results = (Vector)broker.find(q);
		if (results != null && !results.isEmpty() ) {
			int len = results.size();
			retVal = new String[len];
			for (int i=0; i<len; i++) {
				retVal[i] = ((TraderOffer)results.elementAt(i)).getOfferID();
			}
		}

		if (Logger.isLoggable ( rb, InfraLoggingRb.METHOD_EXIT, Logger.TRACE ) ) {
			java.lang.Object[] params = { "TraderDBUtil", "getAllOfferIDs" };
			Logger.traceExit( rb, InfraLoggingRb.METHOD_EXIT, params );
		}

		return retVal;
	}

	/**
	* Return a TraderOffer object for an Offer matching the ID
	* @param anID the Offer ID to match
	* @return a TraderOffer object
	* @exception QueryException if there's a database problem
	*/
	public TraderOffer getTraderOfferWithID(String anID)
	throws QueryException
	{
		if (Logger.isLoggable( rb, InfraLoggingRb.METHOD_ENTRY_1, Logger.TRACE ) ) {
			java.lang.Object[] params = { "TraderDBUtil", "getTraderOfferWithID", "Offer", anID };
			Logger.traceEntry( rb, InfraLoggingRb.METHOD_ENTRY_1, params);
		}

		// Don't think it is necessary to have a cache for this call.
		TraderOffer anOffer = TraderOffer.createTraderOffer(traderWithBothIORReferences);
		anOffer.setOfferID(anID);
		SQLQuery.setDefaultBroker(broker);
		SQLQuery q = new SQLQuery(anOffer);
		TraderOffer retVal = null;
		Vector results = (Vector)broker.find(q);
		if (results != null && !results.isEmpty() ) {
			retVal = (TraderOffer)results.firstElement();
		}

		if (Logger.isLoggable ( rb, InfraLoggingRb.METHOD_EXIT, Logger.TRACE ) ) {
			java.lang.Object[] params = { "TraderDBUtil", "getTraderOfferWithID", "Offer", anID };
			Logger.traceExit( rb, InfraLoggingRb.METHOD_EXIT, params );
		}
	
        return retVal;
    }

	/**
	* Return an OfferInfo object for an Offer matching the ID
	* @param anID the Offer ID to match
	* @return an OfferInfo object
	* @exception QueryException if there's a database problem
	* @exception PropertyTypeMismatch an actual property value is not appropriate for the
	* property type as defined by the service type.
	*/
	public OfferInfo getOfferWithID(String anID)
	throws QueryException, PropertyTypeMismatch
	{
		if (Logger.isLoggable( rb, InfraLoggingRb.METHOD_ENTRY_1, Logger.TRACE ) ) {
			java.lang.Object[] params = { "TraderDBUtil", "getOfferWithID", "Offer", anID };
			Logger.traceEntry( rb, InfraLoggingRb.METHOD_ENTRY_1, params);
		}

		TraderOffer anOffer = getTraderOfferWithID(anID);
		OfferInfo retVal = null;
		if (anOffer != null ) {
			retVal = anOffer.toOfferInfo();
		}

		if (Logger.isLoggable ( rb, InfraLoggingRb.METHOD_EXIT, Logger.TRACE ) ) {
			java.lang.Object[] params = { "TraderDBUtil", "getOfferWithID", "Offer", anID };
			Logger.traceExit( rb, InfraLoggingRb.METHOD_EXIT, params );
		}

		return retVal;
	}

	/**
	* Return a list of all Service Types in the database
	* @return all Service Type namess
	* @exception QueryException if there's a database problem
    *
	*/
	public String[] getAllServiceTypeNames()
	throws QueryException
	{
		if (Logger.isLoggable( rb, InfraLoggingRb.METHOD_ENTRY_0, Logger.TRACE ) ) {
			java.lang.Object[] params = { "TraderDBUtil", "getAllServiceTypeNames" };
			Logger.traceEntry( rb, InfraLoggingRb.METHOD_ENTRY_0, params);
		}

		String[] retVal = null;
		Vector<?> results;
		try{
			getTraderServiceTypeLock().readLock().lock();

			TraderServiceType aType = new TraderServiceType();
			SQLQuery.setDefaultBroker(broker);
			SQLQuery q = new SQLQuery(aType);

			results = (Vector<?>)broker.find(q);
		}finally {
			getTraderServiceTypeLock().readLock().unlock();
		}

		if (results != null && !results.isEmpty()) {
			int len = results.size();
			retVal = new String[len];
			for (int i = 0; i < len; i++) {
				retVal[i] = ((TraderServiceType) results.elementAt(i)).getName();
			}
		}   
	

		if (Logger.isLoggable ( rb, InfraLoggingRb.METHOD_EXIT, Logger.TRACE ) ) {
			java.lang.Object[] params = { "TraderDBUtil", "getAllServiceTypeNames" };
			Logger.traceExit( rb, InfraLoggingRb.METHOD_EXIT, params );
		}

		return retVal;
	}

	/**
	* Create a Service Type object
	*  
	* @param name Service Type name
	* @param if_name Interface ID
	* @param props PropStruct sequence
	* @param super_types names of super types
	* @param inc Incarnation Number object
	* @exception ServiceTypeExists the type already exists
	* @exception ValueTypeRedefinition if two service types incompatibly declare value
	* types for the same property name
	* @exception QueryException if there's a database problem
	* 
	* 
	*/
	public void addServiceType(String name, String if_name, PropStruct[] props, String[] super_types, IncarnationNumber inc)
	throws QueryException, ServiceTypeExists, ValueTypeRedefinition
	{
		java.lang.Object[] params = { "TraderDBUtil", "addServiceType", "ServiceTypeName", name, "Interface ID", if_name }; 
		if (Logger.isLoggable( rb, InfraLoggingRb.METHOD_ENTRY_2, Logger.TRACE ) ) {
			Logger.traceEntry( rb, InfraLoggingRb.METHOD_ENTRY_2, params);
		}

		try{
			getTraderServiceTypeLock().writeLock().lock();

			// see if it already exists
			TraderServiceType aType = new TraderServiceType(name);
			SQLQuery.setDefaultBroker(broker);
			SQLQuery q = new SQLQuery(aType);
			Vector results = (Vector)broker.find(q);
			if (results != null && !results.isEmpty() ) { // exists, throw exception
				if (Logger.isLoggable( rb, InfraLoggingRb.DS_TS_DBUTIL_SERVICE_TYPE_EXISTS_EXCEPTION, Logger.DEBUG ) ) {
					Logger.debug( rb, InfraLoggingRb.DS_TS_DBUTIL_SERVICE_TYPE_EXISTS_EXCEPTION, params);
				}
	
				Logger.sysWarn("Service name already exists " + name);
				throw new ServiceTypeExists(name);
			}

			
			aType = new TraderServiceType(name);
			aType.setIfName(if_name);
			aType.setSuperTypes(super_types);
			aType.setIncarnationNumber(inc);
			aType.save();
	
			TraderProp[] tProp = null;
			int len = props.length;
			if (len != 0) {
				tProp = new TraderProp[len];
				for (int i=0; i<len; i++) {
					TraderProp aProp = getPropByName(props[i].name);
					TraderProp tmp = new TraderProp(props[i]);
					if (aProp == null) { // create a new property
						aProp = tmp;
						aProp.save();
						if (Logger.isLoggable( rb, InfraLoggingRb.DS_TS_DBUTIL_ADD_SERVICE_TYPE_PROPERTY, Logger.DEBUG ) ) {
							java.lang.Object[] newParams = new java.lang.Object[7];
							System.arraycopy( params, 0, newParams, 0, 6 );
							newParams[6] = props[i].name; 
							Logger.debug( rb, InfraLoggingRb.DS_TS_DBUTIL_ADD_SERVICE_TYPE_PROPERTY, newParams);
						}
					}
					else { // check for redefinitions (illegal)
						if ( !aProp.equals(tmp) ) {
							if (Logger.isLoggable( rb, InfraLoggingRb.DS_TS_DBUTIL_VALUE_TYPE_REDEFINITION_EXCEPTION, Logger.DEBUG ) ) {
								Logger.debug( rb, InfraLoggingRb.DS_TS_DBUTIL_VALUE_TYPE_REDEFINITION_EXCEPTION, params);
							}
							Logger.sysWarn("Attepted value type redefinition " + aProp + " " + tmp);
							throw new ValueTypeRedefinition();
						}
					}
					tProp[i] = aProp;
				}
			}
	
			aType.setProperties(tProp);
			traderServiceTypeCache.remove(name);
			
		}finally {
			getTraderServiceTypeLock().writeLock().unlock();
		}

		if (Logger.isLoggable ( rb, InfraLoggingRb.METHOD_EXIT, Logger.TRACE ) ) {
			Logger.traceExit( rb, InfraLoggingRb.METHOD_EXIT, params );
		}
	}

	/**
	* Return a Trader Property object for the selected name
	* @param name the Property name
	* @return the matching Trader Property object
	* @exception QueryException if there's a database problem
	*/
	public TraderProp getPropByName(String name)
	throws QueryException
	{
		TraderProp aProp = new TraderProp(name);
		SQLQuery.setDefaultBroker(broker);
		SQLQuery q = new SQLQuery(aProp);
		TraderProp retVal = null;
		Vector results = (Vector)broker.find(q);
		if (results != null && !results.isEmpty() ) {
			retVal = (TraderProp)results.firstElement();
		}

		return retVal;
	}

	/**
	* Delete the Offer object matching Offer ID
	* @param id Offer ID
	* @exception QueryException if there's a database problem
	*/
	public void deleteOfferWithID(String id)
	throws QueryException
	{
		TraderOffer anOffer = getTraderOfferWithID(id);
		if ( anOffer != null ) {
			try{
				getOfferCacheLock().lock();
				anOffer.delete();
				/*
				 * instead of update the cache just clear it. The next request for this object can cache it.
				 */
				offerCache.remove( anOffer.getServiceType() );
			
				Logger.debug( "TraderDBUtil.deleteOfferWithID: clearing offerCache for serviceType(" + anOffer.getServiceType() + ").");
			}finally {
				getOfferCacheLock().unlock();
			}

		}
	}

	/**
	* Delete the supplied Service Type object
	* @param Service Type object
	* @exception QueryException if there's a database problem
	* 
	*/
	public void deleteServiceType(TraderServiceType aType)
	throws QueryException
	{
		
		Session ses = null;
		try {
			getTraderServiceTypeLock().writeLock().lock();

			ses = Session.createAndJoin("Link");
			ses.startTransaction("RDB");
			Integer dbID = aType.getObjectIdentifier();
			aType.markForDelete();

			// remove this service type's rows from the linkage table
			TraderPropJoin aJoin = new TraderPropJoin();
			aJoin.setServiceType(aType);
			SQLQuery.setDefaultBroker(broker);
			SQLQuery q = new SQLQuery(aJoin);
			Vector results = (Vector)broker.find(q);
			if (results != null && !results.isEmpty() ) {
				TraderPropJoin tmpJoin = null;
				for (int i=0; i<results.size(); i++) {
					tmpJoin = (TraderPropJoin)results.elementAt(i);
					tmpJoin.markForDelete();
				}
			}

			ses.commit();
			ses.leave();
			traderServiceTypeCache.remove(aType.getName());

		}
		catch(UpdateException ue) {
			ses.rollback();
			Logger.sysWarn("Update failed ", ue);
			throw new QueryException("update failed", ue);
		}
		catch(QueryException qe) {
			ses.rollback();
			Logger.sysWarn("query problem in delete service", qe);
			throw qe; 
			
		}finally {
			getTraderServiceTypeLock().writeLock().unlock();
		}
	}

	/**
	 * Set the supplied Service Type object as Masked
	 * 
	 * @param Service
	 *            Type object
	 * @exception QueryException
	 *                if there's a database problem
	 * 
	 */
	public void maskServiceType(TraderServiceType aType)
	throws QueryException
	{
		if (Logger.isLoggable( rb, InfraLoggingRb.METHOD_ENTRY_0, Logger.TRACE ) ) {
			java.lang.Object[] params = { "TraderDBUtil", "maskServiceType" };
			Logger.debug( rb, InfraLoggingRb.METHOD_ENTRY_0, params);
		}

		try {
			getTraderServiceTypeLock().writeLock().lock();
			aType.setMasked(true);
			aType.save();
			traderServiceTypeCache.remove(aType.getName());
		}
		finally {
			getTraderServiceTypeLock().writeLock().unlock();
		}

		if (Logger.isLoggable ( rb, InfraLoggingRb.METHOD_EXIT, Logger.TRACE ) ) {
			java.lang.Object[] params = { "TraderDBUtil", "maskServiceType" };
			Logger.traceExit( rb, InfraLoggingRb.METHOD_EXIT, params );
		}
	}

	/**
	 * Set the supplied Service Type object as UnMasked
	 * 
	 * @param Service
	 *            Type object
	 * @exception QueryException
	 *                if there's a database problem
	 * 
	 */
	public void unmaskServiceType(TraderServiceType aType)
	throws QueryException
	{
		if (Logger.isLoggable( rb, InfraLoggingRb.METHOD_ENTRY_0, Logger.TRACE ) ) {
			java.lang.Object[] params = { "TraderDBUtil", "unmaskServiceType" };
			Logger.debug( rb, InfraLoggingRb.METHOD_ENTRY_0, params);
		}
		
		try {
			getTraderServiceTypeLock().writeLock().lock();
			aType.setMasked(false);
			aType.save();
			traderServiceTypeCache.remove(aType.getName());

		}
		finally {
			getTraderServiceTypeLock().writeLock().unlock();
		}

		if (Logger.isLoggable ( rb, InfraLoggingRb.METHOD_EXIT, Logger.TRACE ) ) {
			java.lang.Object[] params = { "TraderDBUtil", "unmaskServiceType" };
			Logger.traceExit( rb, InfraLoggingRb.METHOD_EXIT, params );
		}
	}

	/**
	* Return a Service Type object for the selected interface name
	* @param name the interface name
	* @return the matching Service Type object
	* @exception QueryException if there's a database problem
	* 
	*/
	public TraderServiceType getServiceTypeByName(String name)
	throws QueryException
	{
		if (Logger.isLoggable( rb, InfraLoggingRb.METHOD_ENTRY_1, Logger.TRACE ) ) {
			java.lang.Object[] params = { "TraderDBUtil", "getServiceTypeByName", "ServiceTypeName", name };
			Logger.traceEntry( rb, InfraLoggingRb.METHOD_ENTRY_1, params);
		}
				
		if(name == null || name.length() <= 0) {
			return null;
		}

		/*
		 * cache can be outside of the lock because we aggressively remove the object before 
		 * we change it in the write methods. This method is safe to return the cache if it
		 * finds one.
		 */
		TraderServiceType retVal = traderServiceTypeCache.get(name);
		if(retVal != null) {
			if (Logger.isLoggable ( rb, InfraLoggingRb.METHOD_EXIT, Logger.TRACE ) ) {
				java.lang.Object[] params = { "TraderDBUtil", "getServiceTypeByName", "ServiceTypeName", name };
				Logger.traceExit( rb, InfraLoggingRb.METHOD_EXIT, params );
			}
			return retVal;
		}

		try {
			getTraderServiceTypeLock().readLock().lock();
			
			TraderServiceType aType = new TraderServiceType(name);
			SQLQuery.setDefaultBroker(broker);
			SQLQuery q = new SQLQuery(aType);
			Vector<?> results = (Vector<?>) broker.find(q);
			if (results != null && !results.isEmpty()) {
				retVal = (TraderServiceType) results.firstElement();

				/*
				 * this could be called concurrently however, we don't care which one "wins' as long
				 * as something is cached
				 */
				if (retVal != null) {
					traderServiceTypeCache.put(name, retVal);
				}
			}
		}
		finally {
			getTraderServiceTypeLock().readLock().unlock();
		}
		
		if (Logger.isLoggable ( rb, InfraLoggingRb.METHOD_EXIT, Logger.TRACE ) ) {
			java.lang.Object[] params = { "TraderDBUtil", "getServiceTypeByName", "ServiceTypeName", name };
			Logger.traceExit( rb, InfraLoggingRb.METHOD_EXIT, params );
		}

		return retVal;
	}

	/**
	* Return a Service Type object for the selected Offer ID
	* @param id the Offer ID
	* @return the matching Service Type object
	* @exception QueryException if there's a database problem
	* 
	*/
	public TraderServiceType getServiceTypeForOffer(String id)
	throws QueryException
	{
		TraderServiceType retVal = null;
		TraderOffer anOffer = getTraderOfferWithID(id);
		if ( anOffer != null) {
			String svcType = anOffer.getServiceType();
			retVal = getServiceTypeByName(svcType);
		}

		return retVal;
	}

	/**
	* Export the offer to the database.
	* <b>Note</b>: the Spec doesn't mention what to do if there are duplicates
	* as far as the Offer ID is concerned.
	* So, if the combination of offerID, ServiceType, and IOR is already in the database,
	* just replace it.
	* @param id the Offer ID
	* @param info the Offer's informational field
	* @param reference the Offer's CORBA reference
	* @param type the Offer's Service Type
	* @param properties the Offer's Propery set
	* @exception QueryException if there's a database problem
	*/
	public void exportOffer(String offerId, String info, org.omg.CORBA.Object reference, String type, Property[] properties)
	throws QueryException
	{
		if (Logger.isLoggable( rb, InfraLoggingRb.METHOD_ENTRY_3, Logger.TRACE ) ) {
			java.lang.Object[] params = { "TraderDBUtil", "exportOffer", "ServiceType", type, "OfferID", offerId, "Info", info };
			Logger.traceEntry(rb, InfraLoggingRb.METHOD_ENTRY_3, params);
		}

		
		TraderOffer anOffer = getTraderOfferWithID(offerId);
		if ( anOffer == null ) {
			TraderOffer newOffer = TraderOffer.createTraderOffer(traderWithBothIORReferences); 
			newOffer.setOfferID(offerId);
			newOffer.setInformation(info);
			newOffer.setReference(reference);
			newOffer.setServiceType(type);
			newOffer.setProperties(properties);
			newOffer.save();
		}
		else { // don't overwrite the type!!
			anOffer.setInformation(info);
			anOffer.setReference(reference);
			anOffer.setProperties(properties);
			anOffer.save();
		}

		/*
		 * since we are just modifying the record and not deleting it we can just wrap the cache operation
		 * in our atomic lock.
		 */
		try{
			getOfferCacheLock().lock();
			offerCache.remove( type );
		}finally{
			getOfferCacheLock().unlock();
		}
		
		Logger.debug( "TraderDBUtil.exportOffer: clearing offerCache for serviceType(" + type + ").");
		

		if (Logger.isLoggable ( rb, InfraLoggingRb.METHOD_EXIT, Logger.TRACE ) ) {
			java.lang.Object[] params = { "TraderDBUtil", "exportOffer", "ServiceType", type, "OfferID", offerId, "Info", info };
			Logger.traceExit( rb, InfraLoggingRb.METHOD_EXIT, params );
		}
	}

	/**
	* Modify an offer in the database.
	* @param id the Offer ID
	* @param properties the Offer's Propery set
	* @return the matching Service Type object
	* @exception UnknownOfferId thrown if the offer is not found
	* @exception QueryException if there's a database problem
	*/
	public void modifyOfferWithID(String offerId, Property[] properties)
	throws QueryException, UnknownOfferId
	{
		if (Logger.isLoggable( rb, InfraLoggingRb.METHOD_ENTRY_1, Logger.TRACE ) ) {
			java.lang.Object[] params = { "TraderDBUtil", "modifyOfferWithID", "OfferID", offerId };
			Logger.traceEntry( rb, InfraLoggingRb.METHOD_ENTRY_1, params);
		}

		TraderOffer anOffer = getTraderOfferWithID(offerId);
		if ( anOffer == null ) {
			Logger.sysWarn("Unknown offer " + offerId);
			throw new UnknownOfferId(offerId);
		}

		anOffer.setProperties(properties);
		anOffer.save();
		
		/*
		 * since we just modifying an offer we can just make the cache operation atomic, any calls
		 * made between with save and this clear might be old
		 */
		try{
			getOfferCacheLock().lock();
			offerCache.remove( anOffer.getServiceType() );
			Logger.debug( "TraderDBUtil.modifyOfferWithID: clearing offerCache for serviceType(" + anOffer.getServiceType() + ").");
		}finally{
			getOfferCacheLock().unlock();
		}


		if (Logger.isLoggable ( rb, InfraLoggingRb.METHOD_EXIT, Logger.TRACE ) ) {
			java.lang.Object[] params = { "TraderDBUtil", "modifyOfferWithID", "OfferID", offerId };
			Logger.traceExit( rb, InfraLoggingRb.METHOD_EXIT, params );
		}
	}
}
