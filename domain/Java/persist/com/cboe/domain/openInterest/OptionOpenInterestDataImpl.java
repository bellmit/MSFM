package com.cboe.domain.openInterest;

import java.util.*;
import java.lang.reflect.*;

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
 *  A persistent implementation of <code>OptionOpenInterestData</code>.
 *  UD 10/06/05
 */
public class OptionOpenInterestDataImpl extends PersistentBObject implements OptionOpenInterestData
{
    /**
	 * Table name used for sbt_opt_fut_occ_open_int.
	 */
	public static final String TABLE_NAME = "SBT_OPT_FUT_OCC_OPEN_INT";

	/*
	 *  JavaGrinder variables
	 */
	static Field _classSym;
    static Field _exercisePrice;
	static Field _exprDate;
    static Field _optionType;
	static Field _openIntQty;
	
	static Vector classDescriptor;

	/**
	 * internal fields mapped to the database/
	 */
	private String classSym;
	private PriceSqlType exercisePrice;
	private java.sql.Timestamp exprDate;
	private OptionTypeImpl optionType;
	private int openIntQty;
	
	static
	{
		try
		{
			_classSym = OptionOpenInterestDataImpl.class.getDeclaredField( "classSym" );
			_classSym.setAccessible( true );

			_exercisePrice = OptionOpenInterestDataImpl.class.getDeclaredField( "exercisePrice" );
			_exercisePrice.setAccessible( true );

			_exprDate = OptionOpenInterestDataImpl.class.getDeclaredField( "exprDate" );
			_exprDate.setAccessible( true );

         		_optionType = OptionOpenInterestDataImpl.class.getDeclaredField( "optionType" );
			_optionType.setAccessible( true );

			_openIntQty = OptionOpenInterestDataImpl.class.getDeclaredField( "openIntQty" );
			_openIntQty.setAccessible( true );
		}
		catch( NoSuchFieldException ex )
		{
			ex.printStackTrace();
		}

        SqlScalarTypeInitializer.initTypes();
	}
	/**
	 *  OpenInterestDataImpl constructor comment.
	 */
	public OptionOpenInterestDataImpl()
	{
		super();
	}

    /**
	 *@return  PriceStruct the expiration date.
	 */
	public Price getExercisePrice()
	{
		return (Price) editor.get(_exercisePrice, exercisePrice);
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
    * Gets option type of this product.
    *@return  Option Type for the product.
    */
    public OptionType getOptionType()
    {
        return (OptionType) editor.get(_optionType, optionType);
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
		synchronized( OptionOpenInterestDataImpl.class )
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
