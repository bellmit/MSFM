package com.cboe.domain.instrumentorExtension;

import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;
import com.cboe.instrumentationService.factories.QueueInstrumentorFactory;
import com.cboe.instrumentationService.instrumentors.QueueInstrumentor;

import java.util.HashMap;
import java.util.Iterator;

/**
 * @author Jing Chen
 */
public class QueueInstrumentorExtensionFactory
{
    private static QueueInstrumentorFactory queueInstrumentorFactory;
    private static HashMap queueInstrumentorExtensions;

    public static QueueInstrumentorFactory getQueueInstrumentorFactory()
    {
        if(queueInstrumentorFactory == null)
        {
            queueInstrumentorFactory = FoundationFramework.getInstance().getInstrumentationService().getQueueInstrumentorFactory();
        }
        return queueInstrumentorFactory;
    }

    public static HashMap getQueueInstrumentorExtensions()
    {
        if(queueInstrumentorExtensions == null)
        {
            queueInstrumentorExtensions = new HashMap(11);
        }
        return queueInstrumentorExtensions;
    }

    public synchronized static QueueInstrumentorExtension createQueueInstrumentor(String name, Object userData, ThreadPoolInstrumentorExtension tiExtension, boolean privateOnly)
        throws InstrumentorAlreadyCreatedException
    {
        QueueInstrumentorExtension extension = createQueueInstrumentor(name, userData, privateOnly);
        if(tiExtension != null)
        {
            extension.addThreadPoolInstrumentorRelation(tiExtension);
        }
        return extension;
    }

    public synchronized static QueueInstrumentorExtension createQueueInstrumentor(String name, Object userData, boolean privateOnly)
        throws InstrumentorAlreadyCreatedException
    {
        QueueInstrumentorExtension extension = new QueueInstrumentorExtension();
        QueueInstrumentor qi= getQueueInstrumentorFactory().create(name, userData);
        getQueueInstrumentorFactory().register(qi);
        qi.setPrivate(privateOnly);
        extension.setQueueInstrumentor(qi);
        getQueueInstrumentorExtensions().put(name, extension);
        return extension;
    }

    public synchronized static QueueInstrumentorExtension find(String name)
    {
        return (QueueInstrumentorExtension)getQueueInstrumentorExtensions().get(name);
    }

    public static void accept(QueueInstrumentorExtensionFactoryVisitor visitor)
    {
        HashMap tempMap = null;
        synchronized( queueInstrumentorExtensions )
        {
            tempMap = (HashMap)queueInstrumentorExtensions.clone();
        }
        Iterator iter = tempMap.values().iterator();
        while( iter.hasNext() )
        {
            QueueInstrumentorExtension qi = (QueueInstrumentorExtension)iter.next();
            visitor.visit(qi);
        }
    }

    public synchronized static void removeQueueInstrumentor(String name)
    {
        QueueInstrumentorExtension extension = (QueueInstrumentorExtension)getQueueInstrumentorExtensions().get(name);
        if(extension != null)
        {
            QueueInstrumentor qi = extension.getQueueInstrumentor();
            getQueueInstrumentorFactory().unregister(qi);
            ThreadPoolInstrumentorExtension tpExtension = extension.getRelatedThreadPoolInstrumentor();
            if(tpExtension !=null)
            {
                tpExtension.removeQueueInstrumentorRelation(extension);
            }
            MethodInstrumentorExtension[] methodInstrumentors = extension.getRelatedMethodInstrumentors();
            for (int i=0; i<methodInstrumentors.length; i++)
            {
                MethodInstrumentorExtensionFactory.removeMethodInstrumentor(methodInstrumentors[i].getName());
            }
            getQueueInstrumentorExtensions().remove(name);
        }
    }
}
