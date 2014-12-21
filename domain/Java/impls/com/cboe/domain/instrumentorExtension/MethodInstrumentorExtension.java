package com.cboe.domain.instrumentorExtension;

import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;
import com.cboe.client.xml.bind.*;
import com.cboe.client.xml.XmlBindingFacade;
import com.cboe.domain.util.InstrumentorUserData;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

import javax.xml.bind.JAXBException;

public class MethodInstrumentorExtension extends InstrumentorExtension
{
    QueueInstrumentorExtension relatedQueueInstrumentor;
    MethodInstrumentor methodInstrumentor;

    public MethodInstrumentorExtension()
    {
        INSTRUMENTOR_TYPE = "MI";
    }

    public String getName()
    {
        return methodInstrumentor.getName();
    }

    public void setMethodInstrumentor(MethodInstrumentor mi)
    {
        this.methodInstrumentor = mi;
    }

    public MethodInstrumentor getMethodInstrumentor()
    {
        return methodInstrumentor;
    }

    public void addQueueInstrumentorRelation(QueueInstrumentorExtension queueInstrumentor)
    {
        if(queueInstrumentor != null)
        {
            this.relatedQueueInstrumentor = queueInstrumentor;
            queueInstrumentor.addMethodInstrumentorRelation(this);
        }
    }

    public void removeQueueInstrumentorRelation(QueueInstrumentorExtension queueInstrumentor)
    {
        relatedQueueInstrumentor = null;
    }

    public QueueInstrumentorExtension getRelatedQueueInstrumentor()
    {
        return relatedQueueInstrumentor;
    }

    public void beforeMethodCall()
    {
        methodInstrumentor.beforeMethodCall();
    }

	public void incCalls( long incAmount )
    {
        methodInstrumentor.incCalls(incAmount);
    }

    public void afterMethodCall()
    {
        methodInstrumentor.afterMethodCall();
    }

    public void incExceptions( long incAmount )
    {
        methodInstrumentor.incExceptions(incAmount);
    }

    public GIContextDetail getContextDetail()
    {
        try
        {
            GIMethodInstrumentorType methodInstrumentor = getMethodInstrumentorDetails();
            GIContextDetail contextDetail = XmlBindingFacade.getInstance().getObjectFactory().createGIContextDetail();
            contextDetail.setName(getName());
            contextDetail.setFullName(getName());
            contextDetail.setMethodInstrumentors(new GIMethodInstrumentorType[]{methodInstrumentor});
            QueueInstrumentorExtension qiExtension = getRelatedQueueInstrumentor();
            GIAssociatedContext associatedContext = qiExtension.getAssociatedContext();
            contextDetail.setAssociatedContexts(new GIAssociatedContextType[]{associatedContext});
            return contextDetail;
        }
        catch(JAXBException e)
        {
            Log.exception(e);
            return null;
        }
    }

    public GIMethodInstrumentorType getMethodInstrumentorDetails()
    {
        try
        {
            GIMethodInstrumentorType methodInstrumentorType = XmlBindingFacade.getInstance().getObjectFactory().createGIMethodInstrumentor();
            MethodInstrumentor methodInstrumentor = getMethodInstrumentor();
            methodInstrumentorType.setCalls(methodInstrumentor.getCalls());
            methodInstrumentorType.setExceptions(methodInstrumentor.getExceptions());
            methodInstrumentorType.setMaxMethodTime(methodInstrumentor.getMaxMethodTime());
            methodInstrumentorType.setMethodTime(methodInstrumentor.getMethodTime());
            methodInstrumentorType.setSumOfSquareMethodTime((long)methodInstrumentor.getSumOfSquareMethodTime());
            if(methodInstrumentor.getUserData() != null)
            {
                GIUserData userData = XmlBindingFacade.getInstance().getObjectFactory().createGIUserData();
                userData.setDataElements(((InstrumentorUserData)methodInstrumentor.getUserData()).toStringArray());
                methodInstrumentorType.setUserData(userData);
            }
            return methodInstrumentorType;
        }
        catch(JAXBException e)
        {
            Log.exception(e);
            return null;
        }
    }
}
