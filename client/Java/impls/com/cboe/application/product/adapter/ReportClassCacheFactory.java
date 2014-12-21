package com.cboe.application.product.adapter;


import java.util.concurrent.ConcurrentHashMap;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.ExceptionDetails;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.idl.cmiProduct.ClassStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class ReportClassCacheFactory {
	private static ConcurrentHashMap <ReportingSymbolProductTypeKey, Integer> reportingClasses ;
	private static boolean isCacheReady = false;
    
    public static void load(ClassStruct[] classes)
        throws SystemException, CommunicationException, AuthorizationException
    {      
        if (reportingClasses == null)
        {
            reportingClasses = new ConcurrentHashMap<ReportingSymbolProductTypeKey, Integer>() ;
        }
        
        for ( int i = 0; i < classes.length; i ++)
        {      
            if ( classes[i].reportingClasses.length > 0) 
            {             
               for(int j = 0; j < classes[i].reportingClasses.length; j++) {
                   ReportingSymbolProductTypeKey key = new ReportingSymbolProductTypeKey(classes[i].reportingClasses[j].reportingClassSymbol, classes[i].productType);
            	   reportingClasses.put(key, Integer.valueOf(classes[i].classKey));
               }             
            }
        }
        isCacheReady = true;
        StringBuilder loaded = new StringBuilder(80);
        loaded.append("ReportClassCacheFactory -> load: classes : ").append(classes.length)
              .append( " reporting classes : ").append(reportingClasses.size());
        Log.information(loaded.toString());
    }
    
    public static Integer getClassKeyByReportClassSysmbol(String reportingClassSymbol, short productType) 
    	throws SystemException, DataValidationException {
    	if(isCacheReady) {
    		Integer classKey = reportingClasses.get(new ReportingSymbolProductTypeKey(reportingClassSymbol, productType));
    		if(classKey != null) 
    			return classKey;
    		else 
    		   throw new DataValidationException();
    	} else
    		throw new SystemException();
    }
}
