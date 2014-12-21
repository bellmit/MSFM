package com.cboe.directoryService.persist;

//import com.cboe.loggingService.Log;
import com.objectwave.persist.AttributeTypeColumn;
import com.objectwave.persist.DomainObject;
import com.objectwave.persist.RDBPersistentAdapter;
import com.objectwave.persist.QueryException;
import com.objectwave.transactionalSupport.ObjectEditingView;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Vector;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.IncarnationNumber;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.PropStruct;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.TypeStruct;

import com.cboe.common.log.Logger;
import com.cboe.common.log.InfraLoggingRb;
import java.util.*;


/**
* This class is set up to work with ObjectWave to hold all
* updates to the Trader Service Type table.
*/
public class TraderServiceType extends DomainObject {
	String name;
	String if_name;
	ArrayList properties;
	String[] superTypes;
	boolean masked;
	int incNumHigh;
	int incNumLow;

	// variables to hold reflexive field definitions

	static Field _name;
	static Field _if_name;
	static Field _properties;
	static Field _superTypes;
	static Field _masked;
	static Field _incNumHigh;
	static Field _incNumLow;

	/** Holds field definitions */
	static Vector classDescriptor;

	private static ResourceBundle rb = null;

	// Initialize field variables
	static {
		try {
			try {
				rb = ResourceBundle.getBundle( InfraLoggingRb.class.getName() );
				
			} catch( Exception e ) {
				Logger.sysAlarm( Logger.createLogMessageId( Logger.getDefaultLoggerName(),
												    "Unable to set Logging ResourceBundle({0}).",
												    "TraderServiceType", "" ),
							  new Object[] {InfraLoggingRb.class.getName()} );
			}	
			
			_name = TraderServiceType.class.getDeclaredField( "name" );
			_if_name = TraderServiceType.class.getDeclaredField( "if_name" );
			_properties = TraderServiceType.class.getDeclaredField( "properties" );
			_superTypes = TraderServiceType.class.getDeclaredField( "superTypes" );
			_masked = TraderServiceType.class.getDeclaredField( "masked" );
			_incNumHigh = TraderServiceType.class.getDeclaredField( "incNumHigh" );
			_incNumLow = TraderServiceType.class.getDeclaredField( "incNumLow" );
		}
		catch(NoSuchFieldException ex) {
			Logger.sysWarn( rb,
						 InfraLoggingRb.DS_TS_PERSIST_NO_SUCH_FIELD_EXCEPTION,
						 new java.lang.Object[] {"TraderServiceType", "" },
						 ex );

			// Log.logException(null, ex);  //  chanaka
		}
	}

	/**
	* Constructor
	*/
	public TraderServiceType()
	{
		setMasked(false);
	}

	/**
	* Constructor
	*/
	public TraderServiceType(String name)
	{
		this();
		setName(name);
	}

	/**
	* Describe how this class relates to the relational database.
	*/
	public void initDescriptor()
	{
		synchronized( TraderServiceType.class ) {
			if ( classDescriptor == null ) {
				Vector tmpClassDescriptor = getSuperDescriptor();
				tmpClassDescriptor.addElement( AttributeTypeColumn.getAttributeRelation( "name", _name ) );
				tmpClassDescriptor.addElement( AttributeTypeColumn.getAttributeRelation( "if_name", _if_name ) );
				tmpClassDescriptor.addElement( AttributeTypeColumn.getCollectionRelation( TraderPropJoin.class, _properties ) );
				tmpClassDescriptor.addElement( AttributeTypeColumn.getAttributeRelation( "superTypes", _superTypes ) );
				tmpClassDescriptor.addElement( AttributeTypeColumn.getAttributeRelation( "masked", _masked ) );
				tmpClassDescriptor.addElement( AttributeTypeColumn.getAttributeRelation( "incNumHigh", _incNumHigh ) );
				tmpClassDescriptor.addElement( AttributeTypeColumn.getAttributeRelation( "incNumLow", _incNumLow ) );
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
		result.setTableName( "TraderServiceType" );
		result.setClassDescription( classDescriptor );
		return result;
	}

	public String getName()
	{
		return (String)editor.get(_name, name);
	}

	public void setName(String aValue)
	{
		editor.set(_name, aValue, name);
	}

	public String getIfName()
	{
		return (String)editor.get(_if_name, if_name);
	}

	public void setIfName(String aValue)
	{
		editor.set(_if_name, aValue, if_name);
	}

	public ArrayList getProperties()
	{
		return (ArrayList)editor.get(_properties, properties);
	}

	public TraderProp[] getPropertyArray()
	{
		TraderProp[] retVal = null;
		ArrayList aList = getProperties();
		if (aList != null && aList.size() > 0) {
			retVal = new TraderProp[aList.size()];
			TraderPropJoin aJoin = null;
			for (int i=0; i<aList.size(); i++) {
				aJoin = (TraderPropJoin)aList.get(i);
				retVal[i] = aJoin.getProperty();
			}
		}

		return retVal;
	}

	public void setProperties(ArrayList aValue)
	{
		editor.set(_properties, aValue, properties);
	}

	public void setProperties(TraderProp[] aValue)
	{
		if (aValue != null && aValue.length > 0) {
			ArrayList aList = new ArrayList(aValue.length);
			TraderPropJoin aJoin = null;
			for (int i=0; i<aValue.length; i++) {
				aJoin = new TraderPropJoin();
				aJoin.setServiceType(this);
				aJoin.setProperty(aValue[i]);
				try {
					aJoin.save();
				}
				catch(QueryException qe) { 
					// ignore dups
				}

				aList.add(aJoin);
			}

			setProperties(aList);
		}
	}

	public String[] getSuperTypes()
	{
		return (String[])editor.get(_superTypes, superTypes);
	}

	public void setSuperTypes(String[] aValue)
	{
		editor.set(_superTypes, aValue, superTypes);
	}

	public IncarnationNumber getIncarnationNumber()
	{
		return new IncarnationNumber( (int)editor.get(_incNumHigh, incNumHigh), (int)editor.get(_incNumLow, incNumLow) );
	}

	public void setIncarnationNumber(IncarnationNumber aValue)
	{
		editor.set(_incNumHigh, aValue.high, incNumHigh);
		editor.set(_incNumLow, aValue.low, incNumLow);
	}

	public boolean isMasked()
	{
		return getMasked();
	}

	public boolean getMasked()
	{
		return (boolean)editor.get(_masked, masked);
	}

	public void setMasked(boolean aValue)
	{
		editor.set(_masked, aValue, masked);
	}

	public TypeStruct toStruct()
	{
		PropStruct[] tmpPropStruct = new PropStruct[0];
		TraderProp[] tmpProp = getPropertyArray();
		if ( tmpProp != null ) {
			int propLen = tmpProp.length;
			tmpPropStruct = new PropStruct[propLen];
			for (int i=0; i<propLen; i++) {
				tmpPropStruct[i] = tmpProp[i].toStruct();
			}
		}

		String[] supers = getSuperTypes();
		if ( supers == null ) {
			supers = new String[0];
		}

		return new TypeStruct(getIfName(), tmpPropStruct, supers, getMasked(), getIncarnationNumber());
	}
}
