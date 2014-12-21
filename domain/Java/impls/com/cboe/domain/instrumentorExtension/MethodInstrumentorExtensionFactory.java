package com.cboe.domain.instrumentorExtension;

import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;
import com.cboe.instrumentationService.factories.MethodInstrumentorFactory;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;

import java.util.HashMap;
import java.util.Iterator;

/**
 * @author Jing Chen
 */
public class MethodInstrumentorExtensionFactory
{
    private static MethodInstrumentorFactory methodInstrumentorFactory;
    private static HashMap methodInstrumentorExtensions;

    public static MethodInstrumentorFactory getMethodInstrumentorFactory()
    {
        if(methodInstrumentorFactory == null)
        {
            methodInstrumentorFactory = FoundationFramework.getInstance().getInstrumentationService().getMethodInstrumentorFactory();
        }
        return methodInstrumentorFactory;
    }

    public static HashMap getMethodInstrumentorExtensions()
    {
        if(methodInstrumentorExtensions == null)
        {
            methodInstrumentorExtensions = new HashMap(11);
        }
        return methodInstrumentorExtensions;
    }

    public synchronized static MethodInstrumentorExtension createMethodInstrumentor(String name, Object userData, QueueInstrumentorExtension qiExtension, boolean privateOnly)
        throws InstrumentorAlreadyCreatedException
    {
        MethodInstrumentorExtension extension = (MethodInstrumentorExtension)getMethodInstrumentorExtensions().get(name);
        if(extension == null)
        {
            extension = createMethodInstrumentor(name, userData, privateOnly);
        }
        if(qiExtension!=null)
        {
            extension.addQueueInstrumentorRelation(qiExtension);
        }
        return extension;
    }

    public synchronized static MethodInstrumentorExtension createMethodInstrumentor(String name, Object userData, boolean privateOnly)
        throws InstrumentorAlreadyCreatedException
    {
        MethodInstrumentorExtension extension = (MethodInstrumentorExtension)getMethodInstrumentorExtensions().get(name);
        if(extension == null)
        {
            extension = new MethodInstrumentorExtension();
            MethodInstrumentor mi= getMethodInstrumentorFactory().create(name, userData);
            getMethodInstrumentorFactory().register(mi);
            mi.setPrivate(privateOnly);
            extension.setMethodInstrumentor(mi);
            getMethodInstrumentorExtensions().put(name, extension);
        }
        return extension;
    }

    public synchronized static MethodInstrumentorExtension find(String name)
    {
        return (MethodInstrumentorExtension)getMethodInstrumentorExtensions().get(name);
    }

    public static void accept(MethodInstrumentorExtensionFactoryVisitor visitor)
    {
        HashMap tempMap = null;
        synchronized( methodInstrumentorExtensions )
        {
            tempMap = (HashMap)methodInstrumentorExtensions.clone();
        }
        Iterator iter = tempMap.values().iterator();
        while( iter.hasNext() )
        {
            MethodInstrumentorExtension mi = (MethodInstrumentorExtension)iter.next();
            visitor.visit(mi);
        }
    }

    public synchronized static void removeMethodInstrumentor(String name)
    {
        MethodInstrumentorExtension extension = (MethodInstrumentorExtension)getMethodInstrumentorExtensions().get(name);
        if(extension != null)
        {
            MethodInstrumentor methodInstrumentor = extension.getMethodInstrumentor();
            getMethodInstrumentorFactory().unregister(methodInstrumentor);
            QueueInstrumentorExtension qiExtension = extension.getRelatedQueueInstrumentor();
            if(qiExtension != null)
            {
                qiExtension.removeMethodInstrumentorRelation(extension);
            }
            getMethodInstrumentorExtensions().remove(name);
        }
    }
}
