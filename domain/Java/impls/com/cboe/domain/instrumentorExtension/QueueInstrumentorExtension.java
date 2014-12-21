package com.cboe.domain.instrumentorExtension;

import com.cboe.instrumentationService.instrumentors.QueueInstrumentor;
import com.cboe.domain.instrumentorExtension.MethodInstrumentorExtension;
import com.cboe.domain.util.InstrumentorUserData;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.client.xml.bind.*;
import com.cboe.client.xml.XmlBindingFacade;

import javax.xml.bind.JAXBException;
import java.util.HashMap;
import java.util.Vector;
import java.util.Iterator;

/**
 * @author Jing Chen
 */
public class QueueInstrumentorExtension extends InstrumentorExtension
{
    protected QueueInstrumentor queueInstrumentor;
    protected ThreadPoolInstrumentorExtension relatedThreadPoolInstrumentorExtension;
    protected HashMap relatedMethodInstrumentors;

    public QueueInstrumentorExtension()
    {
        relatedMethodInstrumentors = new HashMap(11);
        INSTRUMENTOR_TYPE = "QI";
    }

    public String getName()
    {
        return queueInstrumentor.getName();
    }

    public void setQueueInstrumentor(QueueInstrumentor qi)
    {
        this.queueInstrumentor = qi;
    }

    public QueueInstrumentor getQueueInstrumentor()
    {
        return queueInstrumentor;
    }

    public void addThreadPoolInstrumentorRelation(ThreadPoolInstrumentorExtension threadPoolInstrumentor)
    {
        if(threadPoolInstrumentor != null)
        {
            this.relatedThreadPoolInstrumentorExtension = threadPoolInstrumentor;
            relatedThreadPoolInstrumentorExtension.addQueueInstrumentorRelation(this);
        }
    }

    public ThreadPoolInstrumentorExtension getRelatedThreadPoolInstrumentor()
    {
        return relatedThreadPoolInstrumentorExtension;
    }

    public void removeThreadPoolInstrumentorRelation()
    {
        relatedThreadPoolInstrumentorExtension = null;
    }

    public void addMethodInstrumentorRelation(MethodInstrumentorExtension methodInstrumentor)
    {
        synchronized(relatedMethodInstrumentors)
        {
            relatedMethodInstrumentors.put(methodInstrumentor.getName(), methodInstrumentor);
        }
    }

    public void removeMethodInstrumentorRelation(MethodInstrumentorExtension methodInstrumentor)
    {
        synchronized(relatedMethodInstrumentors)
        {
            relatedMethodInstrumentors.remove(methodInstrumentor.getName());
        }
    }

    public MethodInstrumentorExtension[] getRelatedMethodInstrumentors()
    {
        synchronized(relatedMethodInstrumentors)
        {
            return (MethodInstrumentorExtension[])(relatedMethodInstrumentors.values().toArray(new MethodInstrumentorExtension[0]));
        }
    }

    public GIContextDetail getContextDetail()
    {
        try
        {
            GIQueueInstrumentorType queueInstrumentor = getQueueInstrumentorDetails();
            GIContextDetail contextDetail = XmlBindingFacade.getInstance().getObjectFactory().createGIContextDetail();
            contextDetail.setName(getName());
            contextDetail.setFullName(getName());
            contextDetail.setQueueInstrumentors(new GIQueueInstrumentorType[]{queueInstrumentor});
            ThreadPoolInstrumentorExtension tiExtension = relatedThreadPoolInstrumentorExtension;
            Vector associatedContexts = new Vector();
            if(tiExtension != null)
            {
                GIAssociatedContext associatedContext = tiExtension.getAssociatedContext();
                associatedContexts.add(associatedContext);
            }
            Iterator i = relatedMethodInstrumentors.values().iterator();
            MethodInstrumentorExtension miExtension = null;
            while(i.hasNext())
            {
                miExtension = (MethodInstrumentorExtension)i.next();
                GIAssociatedContext associatedContext = miExtension.getAssociatedContext();
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

    protected GIQueueInstrumentorType getQueueInstrumentorDetails()
    {
        try
        {
            GIQueueInstrumentorType queueInstrumentor = XmlBindingFacade.getInstance().getObjectFactory().createGIQueueInstrumentor();
            QueueInstrumentor qi = getQueueInstrumentor();
            queueInstrumentor.setCurrentSize(qi.getCurrentSize());
            queueInstrumentor.setDequeued(qi.getDequeued());
            queueInstrumentor.setEnqueued(qi.getEnqueued());
            queueInstrumentor.setFlushed(qi.getFlushed());
            queueInstrumentor.setOverlaid(qi.getOverlaid());
            queueInstrumentor.setHighWaterMark(qi.getHighWaterMark());
            queueInstrumentor.setStatus(qi.getStatus());
            if(qi.getUserData() != null)
            {
                GIUserData userData = XmlBindingFacade.getInstance().getObjectFactory().createGIUserData();
                userData.setDataElements(((InstrumentorUserData)qi.getUserData()).toStringArray());
                queueInstrumentor.setUserData(userData);
            }
            return queueInstrumentor;
        }
        catch(JAXBException e)
        {
            Log.exception(e);
            return null;
        }
    }
}
