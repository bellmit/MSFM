package com.cboe.interfaces.domain.eventInstrumentation;

import com.cboe.infrastructureServices.foundationFramework.BOInterceptor;

public interface InstrumentedProxyHome { 
	public String[] getInterceptorNames();
	public void registerInterceptor(BOInterceptor bObject);
	
}
