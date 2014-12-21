package com.cboe.domain.openInterest;

import java.lang.reflect.*;
import java.util.*;
import com.cboe.infrastructureServices.foundationFramework.PersistentBObject;
import com.cboe.infrastructureServices.persistenceService.*;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.OptionOpenInterestData;
import com.cboe.interfaces.domain.product.OptionType;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.domain.util.DateWrapper;
import com.cboe.domain.util.PriceSqlType;
import com.cboe.domain.util.SqlScalarTypeInitializer;
import com.cboe.domain.product.OptionTypeImpl;

/**
 *A persistent implementation of <code>AlloptionsOpenInterestData</code>.
 *@author  Cognizant Technology Solutions
 *@created  Aug 7, 2007
 */

public class AllOptionsOpenInterestDataImpl extends PersistentBObject implements OptionOpenInterestData 
{
	/**
	 * Table name used for SBT_OPT_DDS_OPEN_INT.
	 */
	public static final String TABLE_NAME = "SBT_OPT_DDS_OPEN_INT";

	/*
	 *  JavaGrinder variables
	 */
	static Field _classSym;
	static Field _exercisePrice;
	static Field _exprDate;
	static Field _optionType;
	static Field _openIntQty;
	static Field _downloadDate;
	
	static Vector classDescriptor;

	/**
	 * internal fields mapped to the database/
	 */
	private String classSym;
	private PriceSqlType exercisePrice;
	private java.sql.Timestamp exprDate;
	private OptionTypeImpl optionType;
	private int openIntQty;
	private java.sql.Timestamp downloadDate;
	
	static
	{
		try
		{
			_classSym = AllOptionsOpenInterestDataImpl.class.getDeclaredField( "classSym" );
			_classSym.setAccessible( true );

			_exercisePrice = AllOptionsOpenInterestDataImpl.class.getDeclaredField( "exercisePrice" );
			_exercisePrice.setAccessible( true );

			_exprDate = AllOptionsOpenInterestDataImpl.class.getDeclaredField( "exprDate" );
			_exprDate.setAccessible( true );

			_optionType = AllOptionsOpenInterestDataImpl.class.getDeclaredField( "optionType" );
			_optionType.setAccessible( true );

			_openIntQty = AllOptionsOpenInterestDataImpl.class.getDeclaredField( "openIntQty" );
			_openIntQty.setAccessible( true );
			
			_downloadDate = AllOptionsOpenInterestDataImpl.class.getDeclaredField( "downloadDate" );
			_downloadDate.setAccessible( true );
		}
		catch( NoSuchFieldException ex )
		{
			System.out.println(ex);
		}

        SqlScalarTypeInitializer.initTypes();
	}
	/**
	 *  AllOpenInterestDataImpl constructor.
	 */
	public AllOptionsOpenInterestDataImpl()
	{
		super();
	}

	 /**
	 * Gets expiration price of the product.
	 *@return  PriceStruct the expiration price.
	 */
	public Price getExercisePrice()
	{
		return (Price) editor.get(_exercisePrice, exercisePrice);
	}

	/**
	 * Gets expiration date of the product.
	 *@return  DateTimeStruct the expiration date.
	 */
	public DateTimeStruct getExpirationDate()
	{
		java.sql.Timestamp date = ( java.sql.Timestamp ) editor.get( _exprDate, exprDate);

		DateTimeStruct dateTimeStruct = DateWrapper.convertToDateTime(date.getTime());
		
		return dateTimeStruct;
	}
	

	/**
	 * Gets the OpenInterestUpdateTime of the product
	 *@return  DateTimeStruct the OpenInterestUpdateTime.
	 */
	public DateTimeStruct getOpenInterestUpdateTime()
	{
		java.sql.Timestamp date = ( java.sql.Timestamp ) editor.get( _downloadDate, downloadDate);

		DateTimeStruct dateTimeStruct = DateWrapper.convertToDateTime(date.getTime());
		
		return dateTimeStruct;
	}
	
	

	/**
	* Gets option type of the product.
	*@return  Option Type for the product.
	*/
	public OptionType getOptionType()
	{
		return (OptionType) editor.get(_optionType, optionType);
	}

	/**
	 * Gets the Openinterest Quantiy for the product
	 *@return  int The openInterest value
	 */
	public int getOpenInterest()
	{
		return ( int ) editor.get( _openIntQty, openIntQty);
	}

	/**
	 * Gets the symbol name  for the product
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
		synchronized( AllOptionsOpenInterestDataImpl.class )
		{
			if( classDescriptor != null )
			{
				return;
			}
			Vector tempDescriptor = getSuperDescriptor();
			tempDescriptor.addElement( AttributeDefinition.getAttributeRelation( "class_sym", _classSym) );
			tempDescriptor.addElement( AttributeDefinition.getAttributeRelation( "exer_price", _exercisePrice ) );
			tempDescriptor.addElement( AttributeDefinition.getAttributeRelation( "expr_date", _exprDate ) );
			tempDescriptor.addElement( AttributeDefinition.getAttributeRelation( "put_call_code", _optionType ) );
			tempDescriptor.addElement( AttributeDefinition.getAttributeRelation( "open_int_qty", _openIntQty ) );
			tempDescriptor.addElement( AttributeDefinition.getAttributeRelation( "dwnld_date", _downloadDate ) );
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
		result.setTableName( TABLE_NAME );
		result.setClassDescription( classDescriptor );
		return result;
	}

}
