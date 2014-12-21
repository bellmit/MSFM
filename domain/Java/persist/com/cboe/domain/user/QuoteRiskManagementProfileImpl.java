package com.cboe.domain.user;

import java.lang.reflect.Field;
import java.util.Vector;

import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.idl.cmiConstants.ProductTypes;
import com.cboe.idl.cmiQuote.QuoteRiskManagementProfileStruct;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.PersistentBObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.persistenceService.AttributeDefinition;
import com.cboe.infrastructureServices.persistenceService.DBAdapter;
import com.cboe.infrastructureServices.persistenceService.ObjectChangesIF;
import com.cboe.interfaces.domain.product.ProductClass;
import com.cboe.interfaces.domain.product.ProductClassHome;
import com.cboe.interfaces.domain.user.QRMUser;
import com.cboe.interfaces.domain.user.QuoteRiskManagementProfile;

/**
 * A persistent implementation of <code>QuoteRiskManagementProfile</code>.
 *
 * @author Steven Sinclair
 */
public class QuoteRiskManagementProfileImpl extends PersistentBObject implements QuoteRiskManagementProfile
{
	/**
	 * Name of database table.
	 */
	public static final String TABLE_NAME = "user_quote_risk_profiles";

	/**
	 *  The user associated with this profile.  This must be a persistent implementation of the User interface (ie, UserImpl).
	 */
	private AcronymUserImpl user;

	private int classKey;

	private int volumeThreshold;

	private int timeWindowMillis;

	private boolean profileEnabled;
	
	/* cache ProductClassHome */
	 private static ProductClassHome productClassHome;
	
	// JavaGrinder variables
	static Field _user;
	static Field _classKey;
	static Field _volumeThreshold;
	static Field _timeWindowMillis;
	static Field _profileEnabled;

	static Vector classDescriptor;
	/**
	* This static block will be regenerated if persistence is regenerated. 
	*/
	static { /*NAME:fieldDefinition:*/
		try{
			_user = QuoteRiskManagementProfileImpl.class.getDeclaredField("user");
			_classKey = QuoteRiskManagementProfileImpl.class.getDeclaredField("classKey");
			_volumeThreshold = QuoteRiskManagementProfileImpl.class.getDeclaredField("volumeThreshold");
			_timeWindowMillis = QuoteRiskManagementProfileImpl.class.getDeclaredField("timeWindowMillis");
			_profileEnabled = QuoteRiskManagementProfileImpl.class.getDeclaredField("profileEnabled");
		}
		catch (NoSuchFieldException ex) { System.out.println(ex); }
	}

	/**
	 * Creates a default user instance.
	 *
	 */
	public QuoteRiskManagementProfileImpl()
	{
		super();
	}

	/**
	 * Sets all values for the profile from the definition struct.
	 *
	 * @param userValues struct containing values for profile (except for User)
	 * @exception DataValidationException if validation checks fail
	 */
	public void fromStruct(QuoteRiskManagementProfileStruct profileStruct) 
	{
		setClassKey(profileStruct.classKey);
		setVolumeThreshold(profileStruct.volumeThreshold);
		setTimeWindowMillis(profileStruct.timeWindow);
		setProfileEnabled(profileStruct.quoteRiskManagementEnabled);	
	}
	
	/**
	 *  This method is used to pass information to be printed in log for QRM report
	 *  @param status denotes whether a QRM has been created,updated, or removed 
	 */
	public void createQRMLogMessage(String status)
	{

	    //This message will be used to create report for HelpDesk

	    int key = getClassKey();
	    ProductClass pClass = null;
	    try
	    {
	        pClass = getProductClassHome().findByKey(key);
	    }
	    catch (NotFoundException e)
	    {
            Log.information(" Product Class key " + key + " cannot be found when accessing from QRM process ");
	    }
	    
	    String symbol = "Default";
	    String type = "Default";

	    if (key > 0)
	    {
	        symbol = pClass.getSymbol();
	        switch(pClass.getProductType())
	        {
	            case ProductTypes.OPTION:
	                type = "Option";
	                break;
	            case ProductTypes.FUTURE:
	                type = "Future";
	                break;
	            case ProductTypes.STRATEGY:
	                type = "Strategy";
	                break;
	            case ProductTypes.EQUITY:
	                type = "Equity";
	                break;
	            case ProductTypes.COMMODITY:
	                type = "Commodty";
	                break;
	            case ProductTypes.INDEX:
	                type = "Index";
	                break;
	            case ProductTypes.DEBT:
	                type = "Debt";
	                break;
	            case ProductTypes.INTEREST_RATE_COMPOSITE:
	                type = "IntRate";
	                break;
	            case ProductTypes.LINKED_NOTE:
	                type = "LinkNote";
	                break;
	            case ProductTypes.UNIT_INVESTMENT_TRUST:
	                type = "InvTrust";
	                break;
	            case ProductTypes.VOLATILITY_INDEX:
	                type = "VolIndex";
	                break;
	        }
	    }

	    Log.information(" QuoteRisk Profile " + status + 
	            " " + getUser().getAcronym() +
	            " " + symbol +
	            " " + type +
	            " " + getTimeWindowMillis() / 1000 +
	            " " + getVolumeThreshold() +  
	            " " + getProfileEnabled() + " ");		
	}

	public QRMUser getUser()
	{
		return (AcronymUserImpl) editor.get(_user, user);
	}

	public void setUser(QRMUser aValue)
	{
        if (aValue instanceof AcronymUserImpl)
        {
            editor.set(_user, aValue, user);
        }
        else
        {
            editor.set(_user, ((UserCombinedImpl)aValue).getAcronymUserImpl(), user);
        }
	}

	public int getClassKey()
	{
		return editor.get(_classKey, classKey);
	}

	public void setClassKey(int aValue)
	{
		editor.set(_classKey, aValue, classKey);
	}

	public int getVolumeThreshold()
	{
		return editor.get(_volumeThreshold, volumeThreshold);
	}

	public void setVolumeThreshold(int aValue)
	{
		editor.set(_volumeThreshold, aValue, volumeThreshold);
	}

	public int getTimeWindowMillis()
	{
		return editor.get(_timeWindowMillis, timeWindowMillis);
	}

	public void setTimeWindowMillis(int aValue)
	{
		editor.set(_timeWindowMillis, aValue, timeWindowMillis);
	}

	public boolean getProfileEnabled()
	{
		return editor.get(_profileEnabled, profileEnabled);
	}

	public void setProfileEnabled(boolean aValue)
	{
		editor.set(_profileEnabled, aValue, profileEnabled);
	}

	/**
	 * Describe how this class relates to the relational database.
	 */
	public void initDescriptor()
	{
		synchronized (AcronymUserImpl.class)
		{
			if (classDescriptor != null)
				return;
			Vector tempDescriptor = getSuperDescriptor();
			tempDescriptor.addElement(AttributeDefinition.getForeignRelation(AcronymUserImpl.class, "sbt_user", _user));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("classKey", _classKey));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("volumeThreshold", _volumeThreshold));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("timeWindow", _timeWindowMillis));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("enabled", _profileEnabled));
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
	 * Creates profile struct containing the profile information (this does not include the User).
	 *
	 * @return profile struct
	 */
	public QuoteRiskManagementProfileStruct toStruct()
	{
		QuoteRiskManagementProfileStruct profileStruct = new QuoteRiskManagementProfileStruct();
		profileStruct.classKey = getClassKey();
		profileStruct.volumeThreshold = getVolumeThreshold();
		profileStruct.timeWindow = getTimeWindowMillis();
		profileStruct.quoteRiskManagementEnabled = getProfileEnabled();
		return profileStruct;
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
	
	private ProductClassHome getProductClassHome()
	{
	    if (productClassHome == null) {
	        try {
	            productClassHome = (ProductClassHome) HomeFactory.getInstance().findHome(ProductClassHome.HOME_NAME);
	        }
	        catch (Exception e) {
	            throw new NullPointerException("Unable to get product class home");
	        }
	    }
	    return productClassHome;
	}
}
