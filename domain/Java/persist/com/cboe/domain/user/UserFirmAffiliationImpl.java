package com.cboe.domain.user;

import com.cboe.infrastructureServices.foundationFramework.PersistentBObject;
import com.cboe.infrastructureServices.persistenceService.AttributeDefinition;
import com.cboe.infrastructureServices.persistenceService.DBAdapter;
import com.cboe.infrastructureServices.persistenceService.ObjectChangesIF;
import com.cboe.idl.user.UserFirmAffiliationStruct;


import java.lang.reflect.Field;
import java.util.Vector;

/**
 * An association between an user and an affiliated firm (for preferred dpm)
 *
 */
public class UserFirmAffiliationImpl extends PersistentBObject implements com.cboe.interfaces.domain.user.UserFirmAffiliation {

	/**
	 * Table name used for object mapping.
	 */
	public static final String TABLE_NAME = "user_firm_affiliation";




	//User's trading userAcronym, if applicable
	private String userAcronym;
    //the exchange the user associated with
    private String exchangeAcronym;
    //affiliated firm for preferred dpm
	private String affiliatedFirm;

	/*
	 * Fields for JavaGrinder.
	 */
	private static Field _userAcronym;
    private static Field _exchangeAcronym;
	private static Field _affiliatedFirm;

	/*
	 * JavaGrinder attribute descriptions.
	 */
	private static Vector classDescriptor;

	/*
	 * Initialize fields
	 */
	static {
		try {
			_userAcronym = UserFirmAffiliationImpl.class.getDeclaredField("userAcronym");
			_exchangeAcronym = UserFirmAffiliationImpl.class.getDeclaredField("exchangeAcronym");
            _affiliatedFirm = UserFirmAffiliationImpl.class.getDeclaredField("affiliatedFirm");

		}
		catch (Exception e) {
			System.out.println("Unable to initialize JavaGrinder fields for UserFirmAffiliationImpl: " + e);
		}
	}

	/**
	 * Constructs a new relationship.  This constructor is needed for queries.
	 */
	public UserFirmAffiliationImpl() {
	}

	/**
	 * Constructs a new assignment.
	 *
	 */
	public UserFirmAffiliationImpl(String acronym,String exchangeAcronym,String affiliatedFirm) {
		super();
		setUserAcronym(acronym);
		setExchangeAcronym(exchangeAcronym);
        setAffiliatedFirm(affiliatedFirm);
	}


	public String getUserAcronym() {
		return (String)editor.get(_userAcronym, userAcronym);
	}

	public String getExchangeAcronym() {
		return (String)editor.get(_exchangeAcronym, exchangeAcronym);
	}

    public String getAffiliatedFirm() {
        return (String)editor.get(_affiliatedFirm, affiliatedFirm);
    }


	public void setUserAcronym(String aUserAcronym) {
		editor.set(_userAcronym, aUserAcronym,userAcronym);
	}

    public void setExchangeAcronym(String anExchangeAcronym) {
        editor.set(_exchangeAcronym, anExchangeAcronym,exchangeAcronym);
    }

    public void setAffiliatedFirm(String anAffiliatedFirm) {
        editor.set(_affiliatedFirm, anAffiliatedFirm,affiliatedFirm);
    }

	/**
	 * Describe how this class relates to the relational database.
	 */
	public void initDescriptor()
	{
		synchronized (UserFirmAffiliationImpl.class)
		{
			if (classDescriptor != null)
				return;
			Vector tempDescriptor = getSuperDescriptor();
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("user_acronym", _userAcronym));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("exchange_acronym", _exchangeAcronym));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("affiliated_firm", _affiliatedFirm));
			classDescriptor = tempDescriptor;
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

    /**
     *
     *
     */
    void fromStruct( UserFirmAffiliationStruct userFirmAffiliation )
    {
		setUserAcronym( userFirmAffiliation.userAcronym.acronym );
		setExchangeAcronym( userFirmAffiliation.userAcronym.exchange );
        setAffiliatedFirm( userFirmAffiliation.affiliatedFirm );
    }

    /**
     *
     *
     */
    UserFirmAffiliationStruct toStruct()
    {
        UserFirmAffiliationStruct struct = new UserFirmAffiliationStruct(); 

		struct.userAcronym.acronym = getUserAcronym();
		struct.userAcronym.exchange = getExchangeAcronym();
        struct.affiliatedFirm = getAffiliatedFirm();

        return struct;
    }

	/**
	 * Formats this relationship as a string.
	 */
	public String toString()
    {
		return "UserFirmAffiliationImpl = " + this.getUserAcronym() + ":" + this.getExchangeAcronym() + ":" + this.getAffiliatedFirm();
  	}


	/**
	 * This method allows me to get arounds security problems with updating
	 * and object from a generic framework.
	 */
	public void update(boolean get, Object[] data, Field[] fields)
	{
		for (int i = 0; i < data.length; i++)
		{
			try
			{
				if (get)
					data[i] = fields[i].get(this);
				else
					fields[i].set(this, data[i]);
			}
			catch (IllegalAccessException ex)
			{
				System.out.println(ex);
			}
			catch (IllegalArgumentException ex)
			{
				System.out.println(ex);
			}
		}
	}
}

