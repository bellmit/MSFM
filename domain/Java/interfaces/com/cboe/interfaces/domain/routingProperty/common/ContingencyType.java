package com.cboe.interfaces.domain.routingProperty.common;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.cboe.idl.cmiConstants.ContingencyTypes;
import com.cboe.idl.cmiConstants.ContingencyTypesOperations;

// -----------------------------------------------------------------------------------
// Source file: ContingencyType
//
// PACKAGE: com.cboe.domain.routingProperty
// 
// Created: Aug 8, 2006 3:02:49 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
public class ContingencyType implements Comparable<ContingencyType> 
{
	public static final short ALL = 999;
    public int contingencyType;
    
    private static Map<Short,String> contingencyMap;
    static{
    	
    	contingencyMap = new HashMap<Short,String>();
    	
    	Class contingencyClass = ContingencyTypesOperations.class;
    	Field [] fields = contingencyClass.getDeclaredFields();
    	for(Field field: fields){
 
    		try
            {
    			Short s = field.getShort(field);
	            String st = field.getName();
	            
	            contingencyMap.put(s,st);
            }
            catch (IllegalArgumentException e)
            {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
            }
            catch (IllegalAccessException e)
            {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
            }
    		
    	}
    	
    	contingencyMap.put(ALL, "ALL");
    }

    public ContingencyType(int contingencyType)
    {
        this.contingencyType = contingencyType;
    }
    
    public String toString()
    {
        //return contingencyMap.get((short)contingencyType);
    	return Integer.toString(contingencyType);
    }
    
    public String toDisplayString(){
    	return contingencyMap.get((short)contingencyType);
    }

	public int compareTo(ContingencyType arg0)
    {
		return toString().compareTo(arg0.toString());
    }
}
