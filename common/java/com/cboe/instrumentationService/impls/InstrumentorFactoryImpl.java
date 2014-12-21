package com.cboe.instrumentationService.impls;

import java.util.*;
import com.cboe.instrumentationService.factories.InstrumentorFactory;
import com.cboe.instrumentationService.factories.InstrumentorFactoryObserver;
import com.cboe.instrumentationService.instrumentors.Instrumentor;

/**
 * InstrumentorFactoryImpl.java
 *
 *
 * Created: Fri May 28 14:26:27 2004
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public abstract class InstrumentorFactoryImpl implements InstrumentorFactory {

	private ArrayList observerList = new ArrayList();

	public synchronized void addObserver( InstrumentorFactoryObserver observer ) {
		observerList.add( observer );
	}

	public synchronized void removeObserver( InstrumentorFactoryObserver observer ) {
		observerList.remove( observer );
	}

	void notifyInstrumentorAdded( Instrumentor inst ) {
		Iterator iter = observerList.iterator();
		while( iter.hasNext() ) {
			InstrumentorFactoryObserver observer = (InstrumentorFactoryObserver)iter.next();
			observer.observeInstrumentorAdded( inst );
		}
	}

	void notifyInstrumentorRemoved( Instrumentor inst ) {
		Iterator iter = observerList.iterator();
		while( iter.hasNext() ) {
			InstrumentorFactoryObserver observer = (InstrumentorFactoryObserver)iter.next();
			observer.observeInstrumentorRemoved( inst );
		}
	}

} // InstrumentorFactoryImpl
