package com.cboe.domain.openInterest;

// java classes
import java.util.*;
import java.lang.reflect.*;

// cboe classes
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.PersistentBObject;
import com.cboe.infrastructureServices.persistenceService.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.OpenInterestData;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.domain.util.DateWrapper;

/**
 *  A persistent implementation of <code>OpenInterestData</code>.
 *
 *@author  David Hoag
 *@author Ravi Vazirani - table changes.
 *@created  September 19, 2001
 */
public class OpenInterestDataImpl extends PersistentBObject implements OpenInterestData
{
	/*
	 *  JavaGrinder variables
	 */
	static Field _classSym;
	static Field _exprDate;
	static Field _openIntQty;
	
	static Vector classDescriptor;
	/**
	 * internal fields mapped to the database/
	 */
	private String classSym;
	private java.sql.Timestamp exprDate;
	private int openIntQty;
	
	static
	{
		try
		{
			_classSym = OpenInterestDataImpl.class.getDeclaredField( "classSym" );
			_classSym.setAccessible( true );
			
			_exprDate = OpenInterestDataImpl.class.getDeclaredField( "exprDate" );
			_exprDate.setAccessible( true );
			
			_openIntQty = OpenInterestDataImpl.class.getDeclaredField( "openIntQty" );
			_openIntQty.setAccessible( true );
		}
		catch( NoSuchFieldException ex )
		{
			ex.printStackTrace();
		}
	}
	/**
	 *  OpenInterestDataImpl constructor comment.
	 */
	public OpenInterestDataImpl()
	{
		super();
	}
	/**
	 *@return  DateTimeStruct the expiration date.
	 */
	public DateTimeStruct getExpirationDate()
	{
		java.sql.Timestamp date = ( java.sql.Timestamp ) editor.get( _exprDate, exprDate);

		DateTimeStruct dateTimeStruct = DateWrapper.convertToDateTime(date.getTime());
		
		return dateTimeStruct;
	}
	/**
	 *@return  int The openInterest value
	 */
	public int getOpenInterest()
	{
		return ( int ) editor.get( _openIntQty, openIntQty);
	}
	/**
	 *@return  String the report class symbol name
	 */
	public String getSymbol()
	{
		return ( String ) editor.get( _classSym, classSym);
	}
	/**
	 *  Describe how this class relates to the relational database.
	 */
	private void initDescriptor()
	{
		synchronized( OpenInterestDataImpl.class )
		{
			if( classDescriptor != null )
			{
				return;
			}
			Vector tempDescriptor = getSuperDescriptor();
			tempDescriptor.addElement( AttributeDefinition.getAttributeRelation( "class_sym", _classSym) );
			tempDescriptor.addElement( AttributeDefinition.getAttributeRelation( "expr_date", _exprDate ) );
			tempDescriptor.addElement( AttributeDefinition.getAttributeRelation( "open_int_qty", _openIntQty ) );
			classDescriptor = tempDescriptor;
		}
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
		result.setTableName( "SBT_FUT_OCC_OPEN_INT" );
		result.setClassDescription( classDescriptor );
		return result;
	}
}
