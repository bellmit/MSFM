package com.cboe.directoryService.persist;

//import com.cboe.loggingService.Log;
import com.objectwave.persist.AttributeTypeColumn;
import com.objectwave.persist.DomainObject;
import com.objectwave.persist.RDBPersistentAdapter;
import com.objectwave.transactionalSupport.ObjectEditingView;
import java.lang.reflect.Field;
import java.util.Vector;

import com.cboe.common.log.Logger;
import com.cboe.common.log.InfraLoggingRb;
import java.util.*;


/**
* This class is set up to work with ObjectWave to hold all
* updates to the Trader Property Join table.
* It provide the linkage for the many-to-many relationship between
* Trader Service Types and Trader Properties
*/
public class TraderPropJoin extends DomainObject {
	/** reference to Trader Service Type */
	TraderServiceType service;

	/** reference to Trader Property */
	TraderProp property;

	// variables to hold reflexive field definitions

	static Field _service;
	static Field _property;

	/** Holds field definitions */
	static Vector classDescriptor;

	private static ResourceBundle rb = null;

	// Initialize field variables
	static {
		
		try {
			rb = ResourceBundle.getBundle( InfraLoggingRb.class.getName() );
			
		} catch( Exception e ) {
			Logger.sysAlarm( Logger.createLogMessageId( Logger.getDefaultLoggerName(),
											    "Unable to set Logging ResourceBundle({0}).",
											    "TraderPropJoin", "" ),
						  new Object[] {InfraLoggingRb.class.getName()} );
		}
		
		try {
			_service = TraderPropJoin.class.getDeclaredField( "service" );
			_property = TraderPropJoin.class.getDeclaredField( "property" );
		}
		catch(NoSuchFieldException ex) {
			Logger.sysWarn( rb,
						 InfraLoggingRb.DS_TS_PERSIST_NO_SUCH_FIELD_EXCEPTION,
						 new java.lang.Object[] {"TraderOffer", "" },
						 ex );
			
			
			//Log.logException(null, ex);  //  chanaka
		}
	}

	/**
	* Constructor
	*/
	public TraderPropJoin()
	{ }

	/**
	* Describe how this class relates to the relational database.
	*/
	public void initDescriptor()
	{
		synchronized( TraderOffer.class ) {
			if ( classDescriptor == null ) {
				Vector tmpClassDescriptor = getSuperDescriptor();
				tmpClassDescriptor.addElement( AttributeTypeColumn.getForeignRelation( TraderServiceType.class, "service", _service ) );
				tmpClassDescriptor.addElement( AttributeTypeColumn.getForeignRelation( TraderProp.class, "property", _property ) );
				classDescriptor = tmpClassDescriptor;
			}
		}
	}

	/**
	* The update method is necessary in order to allow any changes to private instance variables.
	*/
	public void update(boolean get, Object[] data, Field[] fields)
	{
		for(int i = 0; i < data.length; i++) {
			try {
				if (get) data[i] = fields[i].get(this);
				else fields[i].set(this, data[i]);
			} 
			catch(IllegalAccessException ex) { System.out.println(ex); }
			catch(IllegalArgumentException ex) { System.out.println(ex); }
		}
	}

	/**
	* Define the tablename and the description of the class.
	*/
	public ObjectEditingView initializeObjectEditor()
	{
		final RDBPersistentAdapter result = (RDBPersistentAdapter)super.initializeObjectEditor();
		if ( null == classDescriptor ) {
			initDescriptor();
		}
		result.setTableName( "TraderPropJoin" );
		result.setClassDescription( classDescriptor );
		return result;
	}

	public TraderServiceType getServiceType()
	{
		return (TraderServiceType)editor.get(_service, service);
	}

	public void setServiceType(TraderServiceType aValue)
	{
		editor.set(_service, aValue, service);
	}

	public TraderProp getProperty()
	{
		return (TraderProp)editor.get(_property, property);
	}

	public void setProperty(TraderProp aValue)
	{
		editor.set(_property, aValue, property);
	}
}
