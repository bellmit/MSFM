package com.cboe.domain.eventInstrumentation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.cboe.instrumentationService.factories.MethodInstrumentorFactoryVisitor;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;

public class MethodInstrumentorVisitor implements
		MethodInstrumentorFactoryVisitor {
	private ConcurrentHashMap<String, ArrayList<MethodInstrumentor>> methodInstrumentors = new ConcurrentHashMap<String, ArrayList<MethodInstrumentor>>(); 

	 

	
	public MethodInstrumentorVisitor() {
	}

	public boolean visit(MethodInstrumentor mi) {
		String instrumentorName = mi.getName();
		int idx = instrumentorName.indexOf('/');
		String interfaceName = instrumentorName.substring(0,idx);
        ArrayList<MethodInstrumentor> miList = methodInstrumentors.get(interfaceName);

        if(miList == null)
        {
        	miList = new ArrayList<MethodInstrumentor>();
        	methodInstrumentors.put(interfaceName, miList);
        }
        miList.add(mi);
        return true;

     }


	public void end() {
	}

	public boolean start(Map unmodifiableCloneOfInstrumentorMap) {
		return true;
	}
	
	public ArrayList<MethodInstrumentor> getMethodInstrumentor(String[] names)
	{
		ArrayList<MethodInstrumentor> result = new ArrayList<MethodInstrumentor>();
		for (String name:names)
		{
			ArrayList<MethodInstrumentor> methodInstrumentor = methodInstrumentors.get(name);
			if (methodInstrumentor !=null)
			{
				Iterator<MethodInstrumentor> iter = methodInstrumentor.iterator();
				while (iter.hasNext())
				{
					MethodInstrumentor mi = iter.next();
					result.add(mi);
				}
			}
		}
	
		return result;
	}
}
