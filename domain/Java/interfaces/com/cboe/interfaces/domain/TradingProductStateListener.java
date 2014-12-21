package com.cboe.interfaces.domain;

import com.cboe.idl.cmiSession.ProductStateStruct;
import com.cboe.idl.cmiSession.ClassStateStruct;

/**
 * This interface is used to register a implementation with TradingClass so that 
 * when transaction is complete Trading class will call the registered impl on the methods listed below.
 */ 
public interface TradingProductStateListener
{
    void setProductStates(int classKey, String sessionName, ProductStateStruct[] productStateStructs, boolean classLevel);
    
    void setClassState(ClassStateStruct classStateStruct);
}
