package com.cboe.directoryService.persist;

//import com.cboe.loggingService.Log;
import com.objectwave.persist.AttributeTypeColumn;
import com.objectwave.persist.DomainObject;
import com.objectwave.persist.RDBPersistentAdapter;
import com.objectwave.transactionalSupport.ObjectEditingView;
import java.lang.reflect.Field;
import java.util.Vector;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.TCKind;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.PropStruct;
import org.omg.CosTradingRepos.ServiceTypeRepositoryPackage.PropertyMode;

import com.cboe.common.log.Logger;
import com.cboe.common.log.InfraLoggingRb;
import java.util.*;

/**
* This class is set up to work with ObjectWave to hold all
* updates to the Trader Property table.
*/
public class TraderProp extends DomainObject {
	String name;
	int typeCode;
	int propMode;
	org.omg.CORBA.ORB orb = null;

	// variables to hold reflexive field definitions

	static Field _name;
	static Field _typeCode;
	static Field _propMode;

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
											    "TraderOffer", "" ),
						  new Object[] {InfraLoggingRb.class.getName()} );
		}

		try {
			_name = TraderProp.class.getDeclaredField( "name" );
			_typeCode = TraderProp.class.getDeclaredField( "typeCode" );
			_propMode = TraderProp.class.getDeclaredField( "propMode" );
		}
		catch(NoSuchFieldException ex) {
			Logger.sysWarn( rb,
						 InfraLoggingRb.DS_TS_PERSIST_NO_SUCH_FIELD_EXCEPTION,
						 new java.lang.Object[] {"TraderOffer", "" },
						 ex );
			//Log.logException(null, ex); 
		}
	}

	/**
	* Constructor
	*/
	public TraderProp()
	{ }

	/**
	* Constructor
	*/
	public TraderProp(String name)
	{
		this();
		setName(name);
	}

	/**
	* Constructor
	*/
	public TraderProp(PropStruct aStruct)
	{
		setName( aStruct.name );
		setTypeCode( aStruct.value_type.kind().value() );
		setMode( aStruct.mode.value() );
	}

	/**
	* Describe how this class relates to the relational database.
	*/
	public void initDescriptor()
	{
		synchronized( TraderProp.class ) {
			if ( classDescriptor == null ) {
				Vector tmpClassDescriptor = getSuperDescriptor();
				tmpClassDescriptor.addElement( AttributeTypeColumn.getAttributeRelation( "name", _name ) );
				tmpClassDescriptor.addElement( AttributeTypeColumn.getAttributeRelation( "typeCode", _typeCode ) );
				tmpClassDescriptor.addElement( AttributeTypeColumn.getAttributeRelation( "propMode", _propMode ) );
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
		result.setTableName( "TraderProp" );
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

	public int getTypeCode()
	{
		return (int)editor.get(_typeCode, typeCode);
	}

	public void setTypeCode(int aValue)
	{
		editor.set(_typeCode, aValue, typeCode);
	}

	/**
	* Convenience method to set the internal typeCode from a TypeCode
	*/
	public void setTypeCode(TypeCode aValue)
	{
		setTypeCode(aValue.kind());
	}

	/**
	* Convenience method to set the internal typeCode from a TCKind
	*/
	public void setTypeCode(TCKind aValue)
	{
		editor.set(_typeCode, aValue.value(), typeCode);
	}

	public int getMode()
	{
		return (int)editor.get(_propMode, propMode);
	}

	public void setMode(int aValue)
	{
		editor.set(_propMode, aValue, propMode);
	}

	public PropStruct toStruct()
	{
		TypeCode tc = getORB().get_primitive_tc( TCKind.from_int(getTypeCode()) );
		PropertyMode aMode = PropertyMode.from_int( getMode() );
		PropStruct retVal = new PropStruct(getName(), tc, aMode);
		return retVal;
	}

	public boolean equals(TraderProp aProp)
	{
		return	aProp.getName().equals(getName()) &&
				aProp.getMode() == getMode() &&
				aProp.getTypeCode() == getTypeCode() ;
	}

	public org.omg.CORBA.ORB getORB()
	{
		if ( orb == null ) {
			orb = org.omg.CORBA.ORB.init();
		}

		return orb;
	}
}
