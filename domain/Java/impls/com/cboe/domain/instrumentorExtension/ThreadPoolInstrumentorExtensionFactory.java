package com.cboe.domain.instrumentorExtension;

import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;
import com.cboe.instrumentationService.factories.ThreadPoolInstrumentorFactory;
import com.cboe.instrumentationService.instrumentors.ThreadPoolInstrumentor;

import java.util.HashMap;
import java.util.Iterator;

/**
 * @author Jing Chen
 */
public class ThreadPoolInstrumentorExtensionFactory
{
    private static ThreadPoolInstrumentorFactory threadPoolInstrumentorFactory;
    private static HashMap threadPoolInstrumentorExtensions;

    public static ThreadPoolInstrumentorFactory getThreadPoolInstrumentorFactory()
    {
        if(threadPoolInstrumentorFactory == null)
        {
            threadPoolInstrumentorFactory = FoundationFramework.getInstance().getInstrumentationService().getThreadPoolInstrumentorFactory();
        }
        return threadPoolInstrumentorFactory;
    }

    public static HashMap getThreadPoolInstrumentorExtensions()
    {
        if(threadPoolInstrumentorExtensions == null)
        {
            threadPoolInstrumentorExtensions = new HashMap(11);
        }
        return threadPoolInstrumentorExtensions;
    }

    public synchronized static ThreadPoolInstrumentorExtension createThreadPoolInstrumentor(String name, int poolSize, Object userData, boolean privateOnly)
        throws InstrumentorAlreadyCreatedException
    {
        ThreadPoolInstrumentorExtension tpExtension = new ThreadPoolInstrumentorExtension();
        ThreadPoolInstrumentor tp = getThreadPoolInstrumentorFactory().create(name, userData);
        getThreadPoolInstrumentorFactory().register(tp);
        tp.setStartedThreads(poolSize);
        tp.setPrivate(privateOnly);
        tpExtension.setThreadPoolInstrumentor(tp);
        getThreadPoolInstrumentorExtensions().put(name,tpExtension);
        return tpExtension;
    }

    public synchronized static ThreadPoolInstrumentorExtension find(String name)
    {
        return (ThreadPoolInstrumentorExtension)getThreadPoolInstrumentorExtensions().get(name);
    }

    public static void accept(ThreadPoolInstrumentorExtensionFactoryVisitor visitor)
    {
        HashMap tempMap = null;
        synchronized( threadPoolInstrumentorExtensions )
        {
            tempMap = (HashMap)threadPoolInstrumentorExtensions.clone();
        }
        Iterator iter = tempMap.values().iterator();
        while( iter.hasNext() )
        {
            ThreadPoolInstrumentorExtension ti = (ThreadPoolInstrumentorExtension)iter.next();
            visitor.visit(ti);
        }
    }

    public synchronized static void removeThreadPoolInstrumentor(String name)
    {
        ThreadPoolInstrumentorExtension extension = (ThreadPoolInstrumentorExtension)getThreadPoolInstrumentorExtensions().get(name);
        if(extension != null)
        {
            ThreadPoolInstrumentor tp = getThreadPoolInstrumentorFactory().find(name);
            getThreadPoolInstrumentorFactory().unregister(tp);
            QueueInstrumentorExtension[] qiExtensions = extension.getRelatedQueueInstrumentors();
            for(int i=0; i<qiExtensions.length; i++)
            {
                QueueInstrumentorExtensionFactory.removeQueueInstrumentor(qiExtensions[i].getName());
            }
            getThreadPoolInstrumentorExtensions().remove(name);
        }
    }
}
