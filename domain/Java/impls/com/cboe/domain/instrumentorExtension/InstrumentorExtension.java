package com.cboe.domain.instrumentorExtension;

import com.cboe.client.xml.bind.GIAssociatedContext;
import com.cboe.client.xml.bind.GIContextDetail;
import com.cboe.client.xml.XmlBindingFacade;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

import javax.xml.bind.JAXBException;

/**
 * @author Jing Chen
 */
public abstract class InstrumentorExtension
{
    protected String INSTRUMENTOR_TYPE = null;

    public abstract String getName();
    public abstract GIContextDetail getContextDetail();

    public GIAssociatedContext getAssociatedContext()
    {
        try
        {
            GIAssociatedContext associatedContext = XmlBindingFacade.getInstance().getObjectFactory().createGIAssociatedContext();
            associatedContext.setName(getName());
            associatedContext.setFullName(getName());
            associatedContext.setInstrumentor(INSTRUMENTOR_TYPE);
            return associatedContext;
        }
        catch(JAXBException e)
        {
            Log.exception(e);
            return null;
        }
    }
}
