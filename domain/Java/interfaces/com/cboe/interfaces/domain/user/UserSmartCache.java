package com.cboe.interfaces.domain.user;

import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.idl.user.SessionProfileUserDefinitionStruct;
import com.cboe.idl.cmiQuote.UserQuoteRiskManagementProfileStruct;
import com.cboe.idl.cmiQuote.QuoteRiskManagementProfileStruct;

/**
 * A cache for user 
 */
public interface UserSmartCache {
    
    public int getUserKey();
    public String getUserId();
    public long getLastActionTime(); 
    public byte getTradeServerId(); 
    
    /**
     * return structs related to this cache.
     */ 
    public SessionProfileUserStructV2 getUserStruct();
    public SessionProfileUserDefinitionStruct getUserDefinitionStruct();
    public UserQuoteRiskManagementProfileStruct getUserQRMStruct();
    
    public void setUserStruct(SessionProfileUserStructV2 aUserStruct);
    public void setUserDefinitionStruct(SessionProfileUserDefinitionStruct aUserDefinitionStruct);
    public void setUserQRMStruct(UserQuoteRiskManagementProfileStruct aUserQRMStruct);
    public QuoteRiskManagementProfileStruct getQRMForClass(int aClassKey);
    public void addOrUpdateQRMProfile(QuoteRiskManagementProfileStruct aQRMProfile);
    public void removeQRMProfileForClass(int aClassKey);
    
    /**
     * 1. return -1 if User has no dpm account matching aDpmAccountId
     * 2. return the actual index to dpms account array if there is one matching aDpmAccountId 
     */ 
    public int getDpmAccountIndex(String aDpmAccountId);
}
