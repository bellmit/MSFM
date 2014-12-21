package com.cboe.domain.user;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Vector;

import com.cboe.domain.util.ExternalizationHelper;
import com.cboe.idl.cmiConstants.ProductClass;
import com.cboe.idl.cmiQuote.QuoteRiskManagementProfileStruct;
import com.cboe.idl.cmiQuote.UserQuoteRiskManagementProfileStruct;
import com.cboe.idl.cmiUser.DpmStruct;
import com.cboe.idl.user.SessionProfileUserDefinitionStruct;
import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.infrastructureServices.foundationFramework.PersistentBObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.persistenceService.AttributeDefinition;
import com.cboe.infrastructureServices.persistenceService.DBAdapter;
import com.cboe.infrastructureServices.persistenceService.ObjectChangesIF;
import com.cboe.interfaces.domain.migratePersistence.Migratable;
import com.cboe.interfaces.domain.user.UserSmartCache;
import com.cboe.util.ReflectiveObjectWriter;

/**
 * A cache for User object. This object only caches information necessary for TradeServer's processing.
 * There are a lot of user related information which are not cached here. So if in the future, need for
 * new information related to user rises, this object will have to be modified to accomodate.
 */
public class UserSmartCacheImpl extends PersistentBObject implements UserSmartCache, Migratable{
    
    public static final String TABLE_NAME = "user_smart_cache";
    
    private SessionProfileUserStructV2 userStruct;
    private SessionProfileUserDefinitionStruct userDefinitionStruct;
    private UserQuoteRiskManagementProfileStruct userQRMStruct;

    public void clear()
    {
        // do nothing - this class isn't actually pooled, yet it implements Copyable
    }

    private static String sClassName = UserSmartCacheImpl.class.getName();
    
    // followings are persistent attributes
    private String userId;
    private int userKey;
    private long lastActionTime;
    private byte tradeServerId;
    static Field _userId;
    static Field _userKey;
    static Field _lastActionTime;
    static Field _tradeServerId;
    static Vector classDescriptor;
    // above are persistent attributes    
    
	static { /*NAME:fieldDefinition:*/
		try{
			_userId = UserSmartCacheImpl.class.getDeclaredField("userId");
            _userId.setAccessible(true);
			_userKey = UserSmartCacheImpl.class.getDeclaredField("userKey");
            _userKey.setAccessible(true);
			_lastActionTime = UserSmartCacheImpl.class.getDeclaredField("lastActionTime");
            _lastActionTime.setAccessible(true);
			_tradeServerId = UserSmartCacheImpl.class.getDeclaredField("tradeServerId");
			_tradeServerId.setAccessible(true);
		}
		catch (NoSuchFieldException ex) 
        { 
            System.out.println(ex); 
        }
	}   
    
    public UserSmartCacheImpl(){
        super();
    }    
    
    private void initDescriptor()
    {
        synchronized (UserSmartCacheImpl.class)
        {
            if (classDescriptor != null) return;          
            Vector tempDescriptor = getSuperDescriptor();
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("userId", _userId));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("userKey", _userKey));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("lastActionTime", _lastActionTime));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("tradeServerId", _tradeServerId));
            classDescriptor = tempDescriptor;
        }
    }
    
    /**
     * Creates JavaGrinder editor for this object.
     */
    public ObjectChangesIF initializeObjectEditor()
    {
//        final DBAdapter result = (DBAdapter) super.initializeObjectEditor();
//        if (classDescriptor == null) initDescriptor();
//        result.setTableName(TABLE_NAME);
//        result.setClassDescription(classDescriptor);
//        return result;
		boolean useMigratableFeature = System.getProperty(Migratable.DASH_D_PROPERTY, Migratable.PROPERTY_NO).compareToIgnoreCase(Migratable.PROPERTY_NO) == 0; 
		DBAdapter result =  useMigratableFeature ? 
				new com.cboe.domain.util.BOObjectEditorInterceptor(this, sClassName): 
				(DBAdapter) super.initializeObjectEditor();
		
		if (classDescriptor == null)
			initDescriptor();

		result.setTableName(TABLE_NAME); 
		result.setClassDescription(classDescriptor);

		return result;
    }    
    
	public String getUserId()
	{
		return (String) editor.get(_userId, userId);
	}   
    
	public int getUserKey()
	{
		return (int) editor.get(_userKey, userKey);
	}    
    
	public long getLastActionTime()
	{
		return (long) editor.get(_lastActionTime, lastActionTime);
	}       

	public byte getTradeServerId()
	{
		return (byte) editor.get(_tradeServerId, tradeServerId);
	}       
    
    
	public void setUserId(String aValue)
	{
		editor.set(_userId, aValue, userId);
	}  

	public void setUserKey(int aValue)
	{
		editor.set(_userKey, aValue, userKey);
	}      
    
	public void setLastActionTime(long aValue)
	{
		editor.set(_lastActionTime, aValue, lastActionTime);
	} 

	public void setTradeServerId(byte aValue)
	{
		editor.set(_tradeServerId, aValue, tradeServerId);
	} 
	
    public SessionProfileUserStructV2 getUserStruct()
    {
        return userStruct;
    }
    
    public void setUserStruct(SessionProfileUserStructV2 aStruct){
        userStruct = aStruct;
    }
    
    public SessionProfileUserDefinitionStruct getUserDefinitionStruct()
    {
        return userDefinitionStruct;
    }
    
    public void setUserDefinitionStruct(SessionProfileUserDefinitionStruct aStruct){
        userDefinitionStruct = aStruct;
    }    
    
   public UserQuoteRiskManagementProfileStruct getUserQRMStruct()
    {
        return userQRMStruct;
    }
    
    public void setUserQRMStruct(UserQuoteRiskManagementProfileStruct aStruct){
        userQRMStruct = aStruct;
    }      
    
    public QuoteRiskManagementProfileStruct getQRMForClass(int aClassKey)
    {
        if (aClassKey == ProductClass.DEFAULT_CLASS_KEY){
            return getUserQRMStruct().defaultQuoteRiskProfile;
        }
        QuoteRiskManagementProfileStruct[] profiles = getUserQRMStruct().quoteRiskProfiles;
        QuoteRiskManagementProfileStruct result = null;
        for (int i = 0; i < profiles.length; i++){
            if (profiles[i].classKey == aClassKey){
                result = profiles[i]; 
                break;
            }
        }
        return result;
    }
    
    public void addOrUpdateQRMProfile(QuoteRiskManagementProfileStruct aQRMProfile)
    {
        if (aQRMProfile.classKey == ProductClass.DEFAULT_CLASS_KEY){
            getUserQRMStruct().defaultQuoteRiskProfile = aQRMProfile;
            return;
        }
        QuoteRiskManagementProfileStruct[] profiles = getUserQRMStruct().quoteRiskProfiles;
        boolean updated = false;
        for (int i = 0; i < profiles.length; i++){
            if (profiles[i].classKey == aQRMProfile.classKey){
                profiles[i] = aQRMProfile; 
                updated = true;
                break;
            }
        }
        if (! updated ){ //new profile
            QuoteRiskManagementProfileStruct[] newProfiles = new QuoteRiskManagementProfileStruct[profiles.length + 1];
            System.arraycopy(profiles, 0, newProfiles, 0, profiles.length);
            newProfiles[profiles.length] = aQRMProfile;
            getUserQRMStruct().quoteRiskProfiles = newProfiles;
        }
    }
    
    public void removeQRMProfileForClass(int aClassKey)
    {
        if (aClassKey == ProductClass.DEFAULT_CLASS_KEY){
            return;  // can not remove the default one
        }
        QuoteRiskManagementProfileStruct[] profiles = getUserQRMStruct().quoteRiskProfiles;
        boolean removed = false;
        ArrayList tempCollection = new ArrayList();
        for (int i = 0; i < profiles.length; i++){
            if (profiles[i].classKey != aClassKey){
                tempCollection.add(profiles[i]);
            }
            else {
                removed = true;
            }
        }
        if (removed){ 
            QuoteRiskManagementProfileStruct[] newProfiles = new QuoteRiskManagementProfileStruct[tempCollection.size()];
            tempCollection.toArray(newProfiles);
            getUserQRMStruct().quoteRiskProfiles = newProfiles;
        }        
    }
    
    /**
     * 1. return -1 if User has no dpm account matching aDpmAccountId
     * 2. return the actual index to dpms account array if there is one matching aDpmAccountId 
     */ 
    public int getDpmAccountIndex(String aDpmAccountId)
    {
        int result = -1;
        DpmStruct[] dpms = getUserDefinitionStruct().dpms;
        for (int i = 0; i < dpms.length; i++){
            if (dpms[i].dpmUserId.equals(aDpmAccountId)){
                result = i;
                break;
            }
        }
        return result;        
    }

	public int getKey()
	{
		return this.userKey;
	}

	public void turnOffPersistentBObjectInterceptor(boolean turnOff)
	{
		if (this.getObjectEditor() instanceof com.cboe.domain.util.BOObjectEditorInterceptor)
		{
			((com.cboe.domain.util.BOObjectEditorInterceptor) this.getObjectEditor()).setStatus(!turnOff);
		}
	}

	public long getAcquiringThreadId()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public void setAcquiringThreadId(long acquiringThreadId)
	{
		// TODO Auto-generated method stub
		
	}

	public Object copy()
	{
		UserSmartCacheImpl rval = new UserSmartCacheImpl();
		rval.objectIdentifier = this.objectIdentifier;
		rval.userId = this.userId; /* String */
		rval.userKey = this.userKey; /* int */
		rval.lastActionTime = this.lastActionTime; /* long */
		rval.tradeServerId = this.tradeServerId; /* byte */
		return rval;
	}

	public String toStringDatabaseFields()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("objectIdentifier=").append(objectIdentifier).append(", ");
		try
		{
			StringWriter userIdwriter = new StringWriter();
			ReflectiveObjectWriter.writeObject(userId, "userId", userIdwriter);
			buffer.append(userIdwriter.toString()).append(", "); /* String */
		}
		catch (IOException e)
		{
			Log.exception(e);
		}
		buffer.append("userKey=").append(userKey).append(", "); /* int */
		buffer.append("lastActionTime=").append(lastActionTime).append(", "); /* long */
		buffer.append("tradeServerId=").append(tradeServerId).append(", "); /* long */
		return buffer.toString();
	}

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
	{
		this.objectIdentifier = in.readObject();
		this.userId = ExternalizationHelper.readString(in); /* String */
		this.userKey = in.readInt(); /* int */
		this.lastActionTime = in.readLong(); /* long */
		this.tradeServerId = in.readByte(); /* byte */
	}

	public void writeExternal(ObjectOutput out) throws IOException
	{
		out.writeObject(this.objectIdentifier);
		ExternalizationHelper.writeString(out, userId); /* String */
		out.writeInt(userKey); /* int */
		out.writeLong(lastActionTime); /* long */
		out.writeByte(tradeServerId); /* byte */
	}
	
    public void postCommit(boolean success)
    {
    	
    }

}
