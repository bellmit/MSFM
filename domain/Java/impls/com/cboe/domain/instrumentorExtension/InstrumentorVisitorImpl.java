package com.cboe.domain.instrumentorExtension;

import com.cboe.client.xml.bind.GIContextDetail;
import com.cboe.client.xml.bind.GIAssociatedContextType;
import com.cboe.client.xml.bind.GIAssociatedContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * @author Jing Chen
 */
public class InstrumentorVisitorImpl
        implements QueueInstrumentorExtensionFactoryVisitor, MethodInstrumentorExtensionFactoryVisitor, ThreadPoolInstrumentorExtensionFactoryVisitor
{
    protected String name;
    protected GIContextDetail contextDetail;
    protected Vector associatedContexts;

    public InstrumentorVisitorImpl(String name, GIContextDetail contextDetail)
    {
        this.name = name;
        this.contextDetail = contextDetail;
        associatedContexts = new Vector();
    }

    public void startVisit()
    {
        QueueInstrumentorExtensionFactory.accept(this);
        MethodInstrumentorExtensionFactory.accept(this);
        ThreadPoolInstrumentorExtensionFactory.accept(this);
        contextDetail.setAssociatedContexts((GIAssociatedContextType[])associatedContexts.toArray(new GIAssociatedContext[0]));
        contextDetail.setName(name);
        contextDetail.setFullName(name);
    }



    public void visit(QueueInstrumentorExtension q)
    {
        if(q.getName().substring(0,q.getName().lastIndexOf(q.getQueueInstrumentor().NAME_DELIMITER)+1).equals(name))
        {
            associatedContexts.add(q.getAssociatedContext());
        }
    }

    public void visit(MethodInstrumentorExtension m)
    {
        if(m.getName().substring(0,m.getName().lastIndexOf(m.getMethodInstrumentor().NAME_DELIMITER)+1).equals(name))
        {
            associatedContexts.add(m.getAssociatedContext());
        }
    }

    public void visit(ThreadPoolInstrumentorExtension t)
    {
        if(t.getName().substring(0,t.getName().lastIndexOf(t.getThreadPoolInstrumentor().NAME_DELIMITER)+1).equals(name))
        {
            associatedContexts.add(t.getAssociatedContext());
        }
    }
}
