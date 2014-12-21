package com.cboe.instrumentationService;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import com.cboe.common.log.Logger;
import com.cboe.instrumentationService.aggregator.CountInstrumentorAggregatedFactory;
import com.cboe.instrumentationService.aggregator.EventChannelInstrumentorAggregatedFactory;
import com.cboe.instrumentationService.aggregator.JmxInstrumentorAggregatedFactory;
import com.cboe.instrumentationService.aggregator.JstatInstrumentorAggregatedFactory;
import com.cboe.instrumentationService.aggregator.MethodInstrumentorAggregatedFactory;
import com.cboe.instrumentationService.aggregator.NetworkConnectionInstrumentorAggregatedFactory;
import com.cboe.instrumentationService.aggregator.QueueInstrumentorAggregatedFactory;
import com.cboe.instrumentationService.aggregator.ThreadPoolInstrumentorAggregatedFactory;
import com.cboe.instrumentationService.factories.CountInstrumentorFactory;
import com.cboe.instrumentationService.factories.CountInstrumentorFactoryVisitor;
import com.cboe.instrumentationService.factories.EventChannelInstrumentorFactory;
import com.cboe.instrumentationService.factories.EventChannelInstrumentorFactoryVisitor;
import com.cboe.instrumentationService.factories.HeapInstrumentorFactory;
import com.cboe.instrumentationService.factories.HeapInstrumentorFactoryVisitor;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;
import com.cboe.instrumentationService.factories.JmxInstrumentorFactory;
import com.cboe.instrumentationService.factories.JmxInstrumentorFactoryVisitor;
import com.cboe.instrumentationService.factories.JstatInstrumentorFactory;
import com.cboe.instrumentationService.factories.JstatInstrumentorFactoryVisitor;
import com.cboe.instrumentationService.factories.MethodInstrumentorFactory;
import com.cboe.instrumentationService.factories.MethodInstrumentorFactoryVisitor;
import com.cboe.instrumentationService.factories.NetworkConnectionInstrumentorFactory;
import com.cboe.instrumentationService.factories.NetworkConnectionInstrumentorFactoryVisitor;
import com.cboe.instrumentationService.factories.OutlierInstrumentorFactory;
import com.cboe.instrumentationService.factories.QueueInstrumentorFactory;
import com.cboe.instrumentationService.factories.QueueInstrumentorFactoryVisitor;
import com.cboe.instrumentationService.factories.ThreadPoolInstrumentorFactory;
import com.cboe.instrumentationService.factories.ThreadPoolInstrumentorFactoryVisitor;
import com.cboe.instrumentationService.impls.CountInstrumentorDefaultFactory;
import com.cboe.instrumentationService.impls.EventChannelInstrumentorDefaultFactory;
import com.cboe.instrumentationService.impls.HeapInstrumentorDefaultFactory;
import com.cboe.instrumentationService.impls.JmxInstrumentorDefaultFactory;
import com.cboe.instrumentationService.impls.JstatInstrumentorDefaultFactory;
import com.cboe.instrumentationService.impls.MethodInstrumentorDefaultFactory;
import com.cboe.instrumentationService.impls.NetworkConnectionInstrumentorDefaultFactory;
import com.cboe.instrumentationService.impls.OutlierInstrumentorDefaultFactory;
import com.cboe.instrumentationService.impls.QueueInstrumentorDefaultFactory;
import com.cboe.instrumentationService.impls.ThreadPoolInstrumentorDefaultFactory;
import com.cboe.instrumentationService.instrumentors.CountInstrumentor;
import com.cboe.instrumentationService.instrumentors.QueueInstrumentor;
import com.cboe.instrumentationService.monitor.InstrumentorMonitorRegistrar;

/**
 * InstrumentorHome.java
 *
 *
 * Created: Thu Sep  4 12:59:39 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public class InstrumentorHome {

	private static HashMap ciFactoryMap = new HashMap();
	private static HashMap eciFactoryMap = new HashMap();
	private static HashMap qiFactoryMap = new HashMap();
	private static HashMap hiFactoryMap = new HashMap();
	private static HashMap tpiFactoryMap = new HashMap();
	private static HashMap nciFactoryMap = new HashMap();
	private static HashMap miFactoryMap = new HashMap();
    private static HashMap jmxiFactoryMap = new HashMap();
    private static HashMap jstatiFactoryMap = new HashMap();
    private static HashMap instMonitorRegsMap = new HashMap();
    private static HashMap oiFactoryMap = new HashMap();

	public static final String DEFAULT_CI_FACTORY = "DefaultCIFactory";
	public static final String DEFAULT_ECI_FACTORY = "DefaultECIFactory";
	public static final String DEFAULT_QI_FACTORY = "DefaultQIFactory";
	public static final String DEFAULT_HI_FACTORY = "DefaultHIFactory";
	public static final String DEFAULT_TPI_FACTORY = "DefaultTPIFactory";
	public static final String DEFAULT_NCI_FACTORY = "DefaultNCIFactory";
	public static final String DEFAULT_MI_FACTORY = "DefaultMIFactory";
    public static final String DEFAULT_JMXI_FACTORY = "DefaultJMXIFactory";
    public static final String DEFAULT_JSTATI_FACTORY = "DefaultJSTATIFactory";
    public static final String DEFAULT_OI_FACTORY = "DefaultOIFactory";
	public static final String AGGREGATED_CI_FACTORY = "AggregatedCIFactory";
	public static final String AGGREGATED_ECI_FACTORY = "AggregatedECIFactory";
	public static final String AGGREGATED_QI_FACTORY = "AggregatedQIFactory";
	public static final String AGGREGATED_TPI_FACTORY = "AggregatedTPIFactory";
	public static final String AGGREGATED_NCI_FACTORY = "AggregatedNCIFactory";
	public static final String AGGREGATED_MI_FACTORY = "AggregatedMIFactory";
    public static final String AGGREGATED_JMXI_FACTORY = "AggregatedJMXIFactory";
    public static final String AGGREGATED_JSTATI_FACTORY = "AggregatedJSTATIFactory";
    
	// Default InstrumentorMonitorRegistrar list.
	// The format for the InstrumentationService.DefaultInstMonRegs property is:
	// <MonRegName>:<classname>,...
	public static final String EC_INST_MON_REG_NAME = "EC";
	public static final String FILE_INST_MON_REG_NAME = "File";
	private static final String DEFAULT_INST_MON_REGS = System.getProperty( "InstrumentationService.DefaultInstMonRegs", EC_INST_MON_REG_NAME + ":com.cboe.instrumentationService.distribution.InstrumentorMonitorECRegistrar," + FILE_INST_MON_REG_NAME + ":com.cboe.instrumentationService.distribution.InstrumentorMonitorFileRegistrar" );
	private static boolean defaultMonRegsSetup = false;

    
    public static CountInstrumentor getCountInstrumentor( String p_name, Object p_monitor )
    {
        CountInstrumentor instrumentor = null;
        
        try
        {
            instrumentor = InstrumentorHome.findCountInstrumentorFactory().create( p_name,
                                                                                      null );
            instrumentor.setLockObject( p_monitor );
            InstrumentorHome.findCountInstrumentorFactory().register( instrumentor );
        }
        catch( InstrumentorAlreadyCreatedException ex )
        {
            instrumentor = InstrumentorHome.findCountInstrumentorFactory().find( p_name );
        }
        
        return instrumentor;
    }

    
    public static QueueInstrumentor getQueueInstrumentor( String p_baseName, Object p_userData )
    {
        QueueInstrumentor instrumentor = null;
        
        try
        {
            instrumentor = findQueueInstrumentorFactory().create( p_baseName,
                                                                  p_userData );
            findQueueInstrumentorFactory().register( instrumentor );
        }
        catch( InstrumentorAlreadyCreatedException ex )
        {
            instrumentor = findQueueInstrumentorFactory().find( p_baseName );
        }
        
        return instrumentor;
    }
    
    
	public static void setupDefaultInstrumentorMonitorRegistrars() {
		if ( defaultMonRegsSetup ) {
			return; // Setup already done.
		}

		defaultMonRegsSetup = true;
		// I'd prefer to use split, but not all processes are running 1.4...
		StringTokenizer defMonListTok = new StringTokenizer( DEFAULT_INST_MON_REGS, "," );
		while( defMonListTok.hasMoreTokens() ) {
			String monInfo = defMonListTok.nextToken();
			StringTokenizer monInfoTok = new StringTokenizer( monInfo, ":" );
			// I'm expecting two pieces.
			if ( monInfoTok.hasMoreTokens() ) {
				String monRegName = monInfoTok.nextToken();
				if ( monRegName != null && monRegName.length() != 0 && monInfoTok.hasMoreTokens() ) {
					String monRegClass = monInfoTok.nextToken();
					try {
						Class c = Class.forName( monRegClass );
						InstrumentorMonitorRegistrar reg = (InstrumentorMonitorRegistrar)c.newInstance();
						// Synchronize on the map here, instead of this whole
						// method.  The method is long-lived, and it may be
						// possible that the thread doing this method could
						// be dependent upon another thread which may attempt
						// to lock the InstrumentorHome, getting blocked and
						// causing a dead-lock.
						synchronized( instMonitorRegsMap ) {
							instMonitorRegsMap.put( monRegName, reg );
						}
					} catch( Throwable t ) {
						Logger.sysNotify( "InstrumentorHome.setupDefaultInstrumentorMonitorRegistrars: Exception creating InstrumentorMonitorRegistrar(" + monRegClass + ").", t );
					}
				} else { // Something not right
					Logger.sysNotify( "InstrumentorHome.setupDefaultInstrumentorMonitorRegistrars: Incorrect value for InstrumentationService.DefaultInstMonRegs: " + monInfo );
				}
			} else {
				Logger.sysNotify( "InstrumentorHome.setupDefaultInstrumentorMonitorRegistrars: Incorrect value for InstrumentationService.DefaultInstMonRegs: " + monInfo );
			}
		}
	}

	public static InstrumentorMonitorRegistrar findInstrumentorMonitorRegistrar( String name ) {
		synchronized( instMonitorRegsMap ) {
			return (InstrumentorMonitorRegistrar)instMonitorRegsMap.get( name );
		}
	}

	public static void registerInstrumentorMonitorRegistrar( String name, InstrumentorMonitorRegistrar registrar ) {
		synchronized( instMonitorRegsMap ) {
			instMonitorRegsMap.put( name, registrar );
		}
	}

	public static void unregisterInstrumentorMonitorRegistrar( String name ) {
		synchronized( instMonitorRegsMap ) {
			instMonitorRegsMap.remove( name );
		}
	}

	public static InstrumentorMonitorRegistrar[] listInstrumentorMonitorRegistrars() {
		synchronized( instMonitorRegsMap ) {
			InstrumentorMonitorRegistrar[] regs = new InstrumentorMonitorRegistrar[instMonitorRegsMap.size()];
			instMonitorRegsMap.values().toArray( regs );
			return regs;
		}
	}

	public static synchronized CountInstrumentorFactory findCountInstrumentorFactory() {
		CountInstrumentorFactory ciFactory = (CountInstrumentorFactory)ciFactoryMap.get( DEFAULT_CI_FACTORY );
		if ( ciFactory == null ) {
			ciFactory = new CountInstrumentorDefaultFactory();
			ciFactoryMap.put( DEFAULT_CI_FACTORY, ciFactory );
		}

		return ciFactory;
	}

	public static synchronized CountInstrumentorFactory findAggregatedCountInstrumentorFactory() {
		CountInstrumentorFactory ciFactory = (CountInstrumentorFactory)ciFactoryMap.get( AGGREGATED_CI_FACTORY );
		if ( ciFactory == null ) {
			ciFactory = new CountInstrumentorAggregatedFactory();
			ciFactoryMap.put( AGGREGATED_CI_FACTORY, ciFactory );
		}

		return ciFactory;
	}

	public static synchronized CountInstrumentorFactory findCountInstrumentorFactory( String name ) {
		return (CountInstrumentorFactory)ciFactoryMap.get( name );
	}

	public static synchronized void registerCountInstrumentorFactory( String name,
														 CountInstrumentorFactory ciFactory ) {
		ciFactoryMap.put( name, ciFactory );
	}

	public static synchronized void unregisterCountInstrumentorFactory( String name ) {
		ciFactoryMap.remove( name );
	}

	public static synchronized void accept( CountInstrumentorFactoryVisitor visitor, boolean exposeClonedMap ) {
		try {
			Iterator iter = ciFactoryMap.values().iterator();
			while( iter.hasNext() ) {
				CountInstrumentorFactory ciFactory = (CountInstrumentorFactory)iter.next();
				ciFactory.accept( visitor, exposeClonedMap );
			}
		} catch( ConcurrentModificationException e ) {
			Logger.sysNotify( "InstrumentorHome.acceptCIVisitor: exception.", e );
		}
	}

	public static synchronized EventChannelInstrumentorFactory findEventChannelInstrumentorFactory() {
		EventChannelInstrumentorFactory eciFactory = (EventChannelInstrumentorFactory)eciFactoryMap.get( DEFAULT_ECI_FACTORY );
		if ( eciFactory == null ) {
			eciFactory = new EventChannelInstrumentorDefaultFactory();
			eciFactoryMap.put( DEFAULT_ECI_FACTORY, eciFactory );
		}

		return eciFactory;
	}

	public static synchronized EventChannelInstrumentorFactory findAggregatedEventChannelInstrumentorFactory() {
		EventChannelInstrumentorFactory eciFactory = (EventChannelInstrumentorFactory)eciFactoryMap.get( AGGREGATED_ECI_FACTORY );
		if ( eciFactory == null ) {
			eciFactory = new EventChannelInstrumentorAggregatedFactory();
			eciFactoryMap.put( AGGREGATED_ECI_FACTORY, eciFactory );
		}

		return eciFactory;
	}

	public static synchronized EventChannelInstrumentorFactory findEventChannelInstrumentorFactory( String name ) {
		return (EventChannelInstrumentorFactory)eciFactoryMap.get( name );
	}

	public static synchronized void registerEventChannelInstrumentorFactory( String name,
															   EventChannelInstrumentorFactory eciFactory ) {
		eciFactoryMap.put( name, eciFactory );
	}

	public static synchronized void unregisterEventChannelInstrumentorFactory( String name ) {
		eciFactoryMap.remove( name );
	}

	public static synchronized void accept( EventChannelInstrumentorFactoryVisitor visitor, boolean exposeClonedMap ) {
		try {
			Iterator iter = eciFactoryMap.values().iterator();
			while( iter.hasNext() ) {
				EventChannelInstrumentorFactory eciFactory = (EventChannelInstrumentorFactory)iter.next();
				eciFactory.accept( visitor, exposeClonedMap );
			}
		} catch( ConcurrentModificationException e ) {
			Logger.sysNotify( "InstrumentorHome.acceptECIVisitor: exception.", e );
		}
	}

	public static synchronized HeapInstrumentorFactory findHeapInstrumentorFactory() {
		HeapInstrumentorFactory hiFactory = (HeapInstrumentorFactory)hiFactoryMap.get( DEFAULT_HI_FACTORY );
		if ( hiFactory == null ) {
			hiFactory = new HeapInstrumentorDefaultFactory();
			hiFactoryMap.put( DEFAULT_HI_FACTORY, hiFactory );
		}

		return hiFactory;
	}

	public static synchronized HeapInstrumentorFactory findHeapInstrumentorFactory( String name ) {
		return (HeapInstrumentorFactory)hiFactoryMap.get( name );
	}

	public static synchronized void registerHeapInstrumentorFactory( String name,
														HeapInstrumentorFactory hiFactory ) {
		hiFactoryMap.put( name, hiFactory );
	}

	public static synchronized void unregisterHeapInstrumentorFactory( String name ) {
		hiFactoryMap.remove( name );
	}

	public static synchronized void accept( HeapInstrumentorFactoryVisitor visitor, boolean exposeClonedMap ) {
		try {
			Iterator iter = hiFactoryMap.values().iterator();
			while( iter.hasNext() ) {
				HeapInstrumentorFactory hiFactory = (HeapInstrumentorFactory)iter.next();
				hiFactory.accept( visitor, exposeClonedMap );
			}
		} catch( ConcurrentModificationException e ) {
			Logger.sysNotify( "InstrumentorHome.acceptHIVisitor: exception.", e );
		}
	}

	public static synchronized NetworkConnectionInstrumentorFactory findNetworkConnectionInstrumentorFactory() {
		NetworkConnectionInstrumentorFactory nciFactory = (NetworkConnectionInstrumentorFactory)nciFactoryMap.get( DEFAULT_NCI_FACTORY );
		if ( nciFactory == null ) {
			nciFactory = new NetworkConnectionInstrumentorDefaultFactory();
			nciFactoryMap.put( DEFAULT_NCI_FACTORY, nciFactory );
		}

		return nciFactory;
	}

	public static synchronized NetworkConnectionInstrumentorFactory findAggregatedNetworkConnectionInstrumentorFactory() {
		NetworkConnectionInstrumentorFactory nciFactory = (NetworkConnectionInstrumentorFactory)nciFactoryMap.get( AGGREGATED_NCI_FACTORY );
		if ( nciFactory == null ) {
			nciFactory = new NetworkConnectionInstrumentorAggregatedFactory();
			nciFactoryMap.put( AGGREGATED_NCI_FACTORY, nciFactory );
		}

		return nciFactory;
	}

	public static synchronized NetworkConnectionInstrumentorFactory findNetworkConnectionInstrumentorFactory( String name ) {
		return (NetworkConnectionInstrumentorFactory)nciFactoryMap.get( name );
	}

	public static synchronized void registerNetworkConnectionInstrumentorFactory( String name,
																   NetworkConnectionInstrumentorFactory nciFactory ) {
		nciFactoryMap.put( name, nciFactory );
	}

	public static synchronized void unregisterNetworkConnectionInstrumentorFactory( String name ) {
		nciFactoryMap.remove( name );
	}

	public static synchronized void accept( NetworkConnectionInstrumentorFactoryVisitor visitor, boolean exposeClonedMap ) {
		try {
			Iterator iter = nciFactoryMap.values().iterator();
			while( iter.hasNext() ) {
				NetworkConnectionInstrumentorFactory nciFactory = (NetworkConnectionInstrumentorFactory)iter.next();
				nciFactory.accept( visitor, exposeClonedMap );
			}
		} catch( ConcurrentModificationException e ) {
			Logger.sysNotify( "InstrumentorHome.acceptNCIVisitor: exception.", e );
		}
	}

	public static synchronized QueueInstrumentorFactory findQueueInstrumentorFactory() {
		QueueInstrumentorFactory qiFactory = (QueueInstrumentorFactory)qiFactoryMap.get( DEFAULT_QI_FACTORY );
		if ( qiFactory == null ) {
			qiFactory = new QueueInstrumentorDefaultFactory();
			qiFactoryMap.put( DEFAULT_QI_FACTORY, qiFactory );
		}

		return qiFactory;
	}

	public static synchronized QueueInstrumentorFactory findAggregatedQueueInstrumentorFactory() {
		QueueInstrumentorFactory qiFactory = (QueueInstrumentorFactory)qiFactoryMap.get( AGGREGATED_QI_FACTORY );
		if ( qiFactory == null ) {
			qiFactory = new QueueInstrumentorAggregatedFactory();
			qiFactoryMap.put( AGGREGATED_QI_FACTORY, qiFactory );
		}

		return qiFactory;
	}

	public static synchronized QueueInstrumentorFactory findQueueInstrumentorFactory( String name ) {
		return (QueueInstrumentorFactory)qiFactoryMap.get( name );
	}

	public static synchronized void registerQueueInstrumentorFactory( String name,
														 QueueInstrumentorFactory qiFactory ) {
		qiFactoryMap.put( name, qiFactory );
	}

	public static synchronized void unregisterQueueInstrumentorFactory( String name ) {
		qiFactoryMap.remove( name );
	}

	public static synchronized void accept( QueueInstrumentorFactoryVisitor visitor, boolean exposeClonedMap ) {
		try {
			Iterator iter = qiFactoryMap.values().iterator();
			while( iter.hasNext() ) {
				QueueInstrumentorFactory qiFactory = (QueueInstrumentorFactory)iter.next();
				qiFactory.accept( visitor, exposeClonedMap );
			}
		} catch( ConcurrentModificationException e ) {
			Logger.sysNotify( "InstrumentorHome.acceptQIVisitor: exception.", e );
		}
	}

	public static synchronized ThreadPoolInstrumentorFactory findThreadPoolInstrumentorFactory() {
		ThreadPoolInstrumentorFactory tpiFactory = (ThreadPoolInstrumentorFactory)tpiFactoryMap.get( DEFAULT_TPI_FACTORY );
		if ( tpiFactory == null ) {
			tpiFactory = new ThreadPoolInstrumentorDefaultFactory();
			tpiFactoryMap.put( DEFAULT_TPI_FACTORY, tpiFactory );
		}

		return tpiFactory;
	}

	public static synchronized ThreadPoolInstrumentorFactory findAggregatedThreadPoolInstrumentorFactory() {
		ThreadPoolInstrumentorFactory tpiFactory = (ThreadPoolInstrumentorFactory)tpiFactoryMap.get( AGGREGATED_TPI_FACTORY );
		if ( tpiFactory == null ) {
			tpiFactory = new ThreadPoolInstrumentorAggregatedFactory();
			tpiFactoryMap.put( AGGREGATED_TPI_FACTORY, tpiFactory );
		}

		return tpiFactory;
	}

	public static synchronized ThreadPoolInstrumentorFactory findThreadPoolInstrumentorFactory( String name ) {
		return (ThreadPoolInstrumentorFactory)tpiFactoryMap.get( name );
	}

	public static synchronized void registerThreadPoolInstrumentorFactory( String name,
															 ThreadPoolInstrumentorFactory tpiFactory ) {
		tpiFactoryMap.put( name, tpiFactory );
	}

	public static synchronized void unregisterThreadPoolInstrumentorFactory( String name ) {
		tpiFactoryMap.remove( name );
	}

	public static synchronized void accept( ThreadPoolInstrumentorFactoryVisitor visitor, boolean exposeClonedMap ) {
		try {
			Iterator iter = tpiFactoryMap.values().iterator();
			while( iter.hasNext() ) {
				ThreadPoolInstrumentorFactory tpiFactory = (ThreadPoolInstrumentorFactory)iter.next();
				tpiFactory.accept( visitor, exposeClonedMap );
			}
		} catch( ConcurrentModificationException e ) {
			Logger.sysNotify( "InstrumentorHome.acceptTPIVisitor: exception.", e );
		}
	}

    public static synchronized JmxInstrumentorFactory findJmxInstrumentorFactory() {
        JmxInstrumentorFactory jmxiFactory = (JmxInstrumentorFactory)jmxiFactoryMap.get( DEFAULT_JMXI_FACTORY );
        if ( jmxiFactory == null ) {
            jmxiFactory = new JmxInstrumentorDefaultFactory();
            jmxiFactoryMap.put( DEFAULT_JMXI_FACTORY, jmxiFactory );
        }

        return jmxiFactory;
    }

    public static synchronized JmxInstrumentorFactory findAggregatedJmxInstrumentorFactory() {
        JmxInstrumentorFactory jmxiFactory = (JmxInstrumentorFactory)jmxiFactoryMap.get( AGGREGATED_JMXI_FACTORY );
        if ( jmxiFactory == null ) {
            jmxiFactory = new JmxInstrumentorAggregatedFactory();
            jmxiFactoryMap.put( AGGREGATED_JMXI_FACTORY, jmxiFactory );
        }

        return jmxiFactory;
    }

    public static synchronized JmxInstrumentorFactory findJmxInstrumentorFactory( String name ) {
        return (JmxInstrumentorFactory)jmxiFactoryMap.get( name );
    }

    public static synchronized void registerJmxInstrumentorFactory( String name,
                                                             JmxInstrumentorFactory jmxiFactory ) {
        jmxiFactoryMap.put( name, jmxiFactory );
    }

    public static synchronized void unregisterJmxInstrumentorFactory( String name ) {
        jmxiFactoryMap.remove( name );
    }

    public static synchronized void accept( JmxInstrumentorFactoryVisitor visitor, boolean exposeClonedMap ) {
        try {
            Iterator iter = jmxiFactoryMap.values().iterator();
            while( iter.hasNext() ) {
                JmxInstrumentorFactory jmxiFactory = (JmxInstrumentorFactory)iter.next();
                jmxiFactory.accept( visitor, exposeClonedMap );
            }
        } catch( ConcurrentModificationException e ) {
            Logger.sysNotify( "InstrumentorHome.acceptJMXIVisitor: exception.", e );
        }
    }

    public static synchronized JstatInstrumentorFactory findJstatInstrumentorFactory() {
        JstatInstrumentorFactory jstatiFactory = (JstatInstrumentorFactory)jstatiFactoryMap.get( DEFAULT_JSTATI_FACTORY );
        if ( jstatiFactory == null ) {
            jstatiFactory = new JstatInstrumentorDefaultFactory();
            jstatiFactoryMap.put( DEFAULT_JSTATI_FACTORY, jstatiFactory );
        }

        return jstatiFactory;
    }

    public static synchronized JstatInstrumentorFactory findAggregatedJstatInstrumentorFactory() {
        JstatInstrumentorFactory jstatiFactory = (JstatInstrumentorFactory)jstatiFactoryMap.get( AGGREGATED_JSTATI_FACTORY );
        if ( jstatiFactory == null ) {
            jstatiFactory = new JstatInstrumentorAggregatedFactory();
            jstatiFactoryMap.put( AGGREGATED_JSTATI_FACTORY, jstatiFactory );
        }

        return jstatiFactory;
    }

    public static synchronized JstatInstrumentorFactory findJstatInstrumentorFactory( String name ) {
        return (JstatInstrumentorFactory)jstatiFactoryMap.get( name );
    }

    public static synchronized void registerJstatInstrumentorFactory( String name,
                                                             JstatInstrumentorFactory jstatiFactory ) {
        jstatiFactoryMap.put( name, jstatiFactory );
    }

    public static synchronized void unregisterJstatInstrumentorFactory( String name ) {
        jstatiFactoryMap.remove( name );
    }

    public static synchronized void accept( JstatInstrumentorFactoryVisitor visitor, boolean exposeClonedMap ) {
        try {
            Iterator iter = jstatiFactoryMap.values().iterator();
            while( iter.hasNext() ) {
                JstatInstrumentorFactory jstatiFactory = (JstatInstrumentorFactory)iter.next();
                jstatiFactory.accept( visitor, exposeClonedMap );
            }
        } catch( ConcurrentModificationException e ) {
            Logger.sysNotify( "InstrumentorHome.acceptJSTATIVisitor: exception.", e );
        }
    }

    public static synchronized MethodInstrumentorFactory findMethodInstrumentorFactory() {
		MethodInstrumentorFactory miFactory = (MethodInstrumentorFactory)miFactoryMap.get( DEFAULT_MI_FACTORY );
		if ( miFactory == null ) {
			miFactory = new MethodInstrumentorDefaultFactory();
			miFactoryMap.put( DEFAULT_MI_FACTORY, miFactory );
		}

		return miFactory;
	}

	public static synchronized MethodInstrumentorFactory findAggregatedMethodInstrumentorFactory() {
		MethodInstrumentorFactory miFactory = (MethodInstrumentorFactory)miFactoryMap.get( AGGREGATED_MI_FACTORY );
		if ( miFactory == null ) {
			miFactory = new MethodInstrumentorAggregatedFactory();
			miFactoryMap.put( AGGREGATED_MI_FACTORY, miFactory );
		}

		return miFactory;
	}

	public static synchronized MethodInstrumentorFactory findMethodInstrumentorFactory( String name ) {
		return (MethodInstrumentorFactory)miFactoryMap.get( name );
	}

	public static synchronized void registerMethodInstrumentorFactory( String name,
														  MethodInstrumentorFactory miFactory ) {
		miFactoryMap.put( name, miFactory );
	}

	public static synchronized void unregisterMethodInstrumentorFactory( String name ) {
		miFactoryMap.remove( name );
	}

	public static void accept( MethodInstrumentorFactoryVisitor visitor, boolean exposeClonedMap ) {
		try {
			Iterator iter = miFactoryMap.values().iterator();
			while( iter.hasNext() ) {
				MethodInstrumentorFactory miFactory = (MethodInstrumentorFactory)iter.next();
				miFactory.accept( visitor, exposeClonedMap );
			}
		} catch( ConcurrentModificationException e ) {
			Logger.sysNotify( "InstrumentorHome.acceptMIVisitor: exception.", e );
		}
	}

    public static synchronized OutlierInstrumentorFactory findOutlierInstrumentorFactory() {
        OutlierInstrumentorFactory oiFactory = (OutlierInstrumentorFactory)oiFactoryMap.get( DEFAULT_OI_FACTORY );
        if ( oiFactory == null ) {
            oiFactory = new OutlierInstrumentorDefaultFactory();
            oiFactoryMap.put( DEFAULT_OI_FACTORY, oiFactory );
        }

        return oiFactory;
    }


    public static synchronized OutlierInstrumentorFactory findOutlierInstrumentorFactory( String name ) {
        return (OutlierInstrumentorFactory)oiFactoryMap.get( name );
    }

    public static synchronized void registerOutlierInstrumentorFactory( String name,
                                                             OutlierInstrumentorFactory oiFactory ) {
        oiFactoryMap.put( name, oiFactory );
    }

    public static synchronized void unregisterOutlierInstrumentorFactory( String name ) {
        oiFactoryMap.remove( name );
    }
 /*   
  * The OutlierInstrumentor does not need to aggregate instrumentors. 
  * 
    public static synchronized void accept( OutlierInstrumentorFactoryVisitor visitor, boolean exposeClonedMap ) {
        try {
            Iterator iter = oiFactoryMap.values().iterator();
            while( iter.hasNext() ) {
                OutlierInstrumentorFactory oiFactory = (OutlierInstrumentorFactory)iter.next();
                oiFactory.accept( visitor, exposeClonedMap );
            }
        } catch( ConcurrentModificationException e ) {
            Logger.sysNotify( "InstrumentorHome.acceptJMXIVisitor: exception.", e );
        }
    }
*/
	
} // InstrumentorHome
