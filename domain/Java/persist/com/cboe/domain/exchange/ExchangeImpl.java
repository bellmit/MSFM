package com.cboe.domain.exchange;

import java.lang.reflect.Field;
import java.util.Vector;

import com.cboe.idl.exchange.ExchangeStruct;
import com.cboe.infrastructureServices.foundationFramework.PersistentBObject;
import com.cboe.infrastructureServices.persistenceService.AttributeDefinition;
import com.cboe.infrastructureServices.persistenceService.DBAdapter;
import com.cboe.infrastructureServices.persistenceService.ObjectChangesIF;
import com.cboe.interfaces.domain.exchange.Exchange;

/**
 *  A persistent implementation of an Exchange object.
 *
 *  @author Steven Sinclair
 */
public class ExchangeImpl extends PersistentBObject implements Exchange
{
	public static final String TABLE_NAME = "exchange";

	protected String name;
	protected String acronym;
        protected int membershipKey;

	private static Field _name;
	private static Field _acronym;
        private static Field _membershipKey;

	private static Vector classDescriptor;

	/**
	 *  This static block will be regenerated if persitence is regenerated
	 */
	static
	{
		try
		{
			_name = ExchangeImpl.class.getDeclaredField("name");
			_acronym = ExchangeImpl.class.getDeclaredField("acronym");
                        _membershipKey = ExchangeImpl.class.getDeclaredField("membershipKey");
            _name.setAccessible(true);
            _acronym.setAccessible(true);
            _membershipKey.setAccessible(true);
		}
		catch (NoSuchFieldException ex)
		{
			System.out.println(ex);
		}
	}

    public ExchangeImpl()
    {
      super();
      setUsing32bitId(true);
    }

	public String getName()
	{
		return (String)editor.get(_name, name);
	}

	public String getAcronym()
	{
		return (String)editor.get(_acronym, acronym);
	}

	public int getMembershipKey()
	{
		return (int)editor.get(_membershipKey, membershipKey);
	}

	public int getExchangeKey()
	{
		return getObjectIdentifierAsInt();
	}

	public void setName(String name)
	{
		editor.set(_name, name, this.name);
	}

	public void setAcronym(String acronym)
	{
		editor.set(_acronym, acronym, this.acronym);
	}

	public void setMembershipKey(int membershipKey)
	{
		editor.set(_membershipKey, membershipKey, this.membershipKey);
	}

	/**
	 *  Ignore the exchangeKey field: these unique ID's are assigned by this class.
	 */
	public void fromStruct(ExchangeStruct exchangeStruct)
	{
		setName(exchangeStruct.name);
		setAcronym(exchangeStruct.acronym);
                setMembershipKey(exchangeStruct.membershipKey);
	}

	public com.cboe.idl.exchange.ExchangeStruct toStruct()
	{
		return new ExchangeStruct(
			getExchangeKey(),
                        getMembershipKey(),
			getAcronym(),
			getName()
			);
	}

	/**
	 * Describe how this class relates to the relational database.
	 */
	public void initDescriptor()
	{
		synchronized (ExchangeImpl.class)
		{
			if (classDescriptor == null)
			{
				Vector tempVector = getSuperDescriptor();
				tempVector.addElement(AttributeDefinition.getAttributeRelation("exchange_name", _name));
				tempVector.addElement(AttributeDefinition.getAttributeRelation("acronym", _acronym));
                                tempVector.addElement(AttributeDefinition.getAttributeRelation("membershipKey", _membershipKey));
				classDescriptor = tempVector;
			}
		}
	}

	/**
	* Needed to define table name and the description of this class.
	*/
	public ObjectChangesIF initializeObjectEditor()
	{
		final DBAdapter result = (DBAdapter) super.initializeObjectEditor();
		if (classDescriptor == null)
			initDescriptor();
		result.setTableName(TABLE_NAME);
		result.setClassDescription(classDescriptor);
		return result;
	}
}
