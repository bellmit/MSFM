package com.cboe.domain.instrumentorExtension;

import com.cboe.client.xml.XmlBindingFacade;
import com.cboe.client.xml.bind.*;
import com.cboe.domain.util.InstrumentorUserData;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.instrumentationService.instrumentors.ThreadPoolInstrumentor;

import javax.xml.bind.JAXBException;
import java.util.HashMap;
import java.util.Vector;
import java.util.Iterator;

public class ThreadPoolInstrumentorExtension extends InstrumentorExtension
{
    HashMap relatedQueueInstrumentors;
    ThreadPoolInstrumentor tp;

    public ThreadPoolInstrumentorExtension()
    {
        relatedQueueInstrumentors = new HashMap();
        INSTRUMENTOR_TYPE = "TI";
    }

    public String getName()
    {
        return tp.getName();
    }

    public void setThreadPoolInstrumentor(ThreadPoolInstrumentor tp)
    {
        this.tp = tp;
    }

    public ThreadPoolInstrumentor getThreadPoolInstrumentor()
    {
        return tp;
    }

    public void addQueueInstrumentorRelation(QueueInstrumentorExtension relatedQueueInstrumentor)
    {
        synchronized(relatedQueueInstrumentors)
        {
            relatedQueueInstrumentors.put(relatedQueueInstrumentor.getName(), relatedQueueInstrumentor);
        }
    }

    public void removeQueueInstrumentorRelation(QueueInstrumentorExtension relatedQueueInstrumentor)
    {
        synchronized(relatedQueueInstrumentors)
        {
            relatedQueueInstrumentors.remove(relatedQueueInstrumentor.getName());
        }
    }

    public QueueInstrumentorExtension[] getRelatedQueueInstrumentors()
    {
        synchronized(relatedQueueInstrumentors)
        {
            return (QueueInstrumentorExtension[])relatedQueueInstrumentors.values().toArray(new QueueInstrumentorExtension[0]);
        }
    }

	public void setCurrentlyExecutingThreads( int newAmount )
    {
        tp.setCurrentlyExecutingThreads(newAmount);
    }

	public int getCurrentlyExecutingThreads()
    {
        return tp.getCurrentlyExecutingThreads();
    }

	public void setStartedThreads( int newAmount )
    {
        tp.setStartedThreads(newAmount);
    }

    public int getStartedThreads()
    {
        return tp.getStartedThreads();
    }

    public GIContextDetail getContextDetail()
    {
        try
        {
            GIThreadInstrumentorType threadPoolInstrumentor = getThreadPoolInstrumentorDetails();
            GIContextDetail contextDetail = XmlBindingFacade.getInstance().getObjectFactory().createGIContextDetail();
            contextDetail.setName(getName());
            contextDetail.setFullName(getName());
            contextDetail.setThreadInstrumentors(new GIThreadInstrumentorType[]{threadPoolInstrumentor});
            Iterator i = relatedQueueInstrumentors.values().iterator();
            QueueInstrumentorExtension qiExtension = null;
            Vector associatedContexts = new Vector();
            while(i.hasNext())
            {
                qiExtension = (QueueInstrumentorExtension)i.next();
                GIAssociatedContext associatedContext = qiExtension.getAssociatedContext();
                associatedContexts.add(associatedContext);
            }
            contextDetail.setAssociatedContexts((GIAssociatedContextType[])associatedContexts.toArray(new GIAssociatedContext[0]));
            return contextDetail;
        }
        catch(JAXBException e)
        {
            Log.exception(e);
            return null;
        }
    }

    public GIThreadInstrumentorType getThreadPoolInstrumentorDetails()
    {
        try
        {
            GIThreadInstrumentorType threadInstrumentorType = XmlBindingFacade.getInstance().getObjectFactory().createGIThreadInstrumentor();
            ThreadPoolInstrumentor threadPoolInstrumentor = getThreadPoolInstrumentor();
            threadInstrumentorType.setCurrentlyExecutingThreads(threadPoolInstrumentor.getCurrentlyExecutingThreads());
            threadInstrumentorType.setPendingTaskCount(threadPoolInstrumentor.getPendingTaskCount());
            threadInstrumentorType.setPendingTaskCountHighWaterMark(threadPoolInstrumentor.getPendingTaskCountHighWaterMark());
            threadInstrumentorType.setPendingThreads(threadPoolInstrumentor.getPendingThreads());
            threadInstrumentorType.setStartedThreads(threadPoolInstrumentor.getStartedThreads());
            threadInstrumentorType.setStartedThreadsHighWaterMark(threadPoolInstrumentor.getStartedThreadsHighWaterMark());
            if(threadPoolInstrumentor.getUserData() != null)
            {
                GIUserData userData = XmlBindingFacade.getInstance().getObjectFactory().createGIUserData();
                userData.setDataElements(((InstrumentorUserData)threadPoolInstrumentor.getUserData()).toStringArray());
                threadInstrumentorType.setUserData(userData);
            }
            return threadInstrumentorType;
        }
        catch(JAXBException e)
        {
            Log.exception(e);
            return null;
        }
    }
}
