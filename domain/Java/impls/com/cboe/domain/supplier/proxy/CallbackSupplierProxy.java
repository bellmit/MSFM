package com.cboe.domain.supplier.proxy;

import com.cboe.ORBInfra.IOPImpl.IORImpl;
import com.cboe.ORBInfra.ORB.DelegateImpl;
import com.cboe.domain.instrumentorExtension.QueueInstrumentorExtensionFactory;
import com.cboe.idl.cmiUtil.CallbackInformationStruct;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.interfaces.domain.supplier.SupplierChannelListener;
import com.cboe.util.channel.ChannelAdapter;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.channel.ChannelListenerProxy;

/**
 * @author Jing Chen
 */
public abstract class CallbackSupplierProxy extends BaseSupplierProxy
{
    protected String iorString;
    protected String typeId;
    protected ChannelListenerProxy proxyWrapper;
    protected CallbackInterceptor interceptor;

    public CallbackSupplierProxy(BaseSessionManager sessionManager, ChannelAdapter adapter, Object object)
    {
        super(sessionManager, adapter);
        setHashKey(object);
    }

    public CallbackSupplierProxy(BaseSessionManager sessionManager, ChannelAdapter adapter, org.omg.CORBA.Object object)
    {
        super(sessionManager, adapter);
        initialize(object);
    }

    public CallbackSupplierProxy(BaseSessionManager sessionManager, ChannelAdapter adapter, org.omg.CORBA.Object object, short queuePolicy)
    {
        super(sessionManager, adapter, queuePolicy);
        initialize(object);
    }

    private void initialize(org.omg.CORBA.Object object)
    {
        IORImpl iorImpl = ((DelegateImpl)((org.omg.CORBA.portable.ObjectImpl)object)._get_delegate()).getIOR();
        setHashKey(iorImpl.getStringDigest());
        iorString = iorImpl.stringify();
        typeId = iorImpl.getTypeId();
    }

    public SupplierChannelListener getProxyWrapper()
    {
        if (proxyWrapper == null)
        {
            proxyWrapper = this.getChannelAdapter().getProxyForDelegate(this);
        }
        return (SupplierChannelListener)proxyWrapper;
    }

    public CallbackInformationStruct getCallbackInformationStruct(ChannelEvent event)
    {
        String interfaceName = typeId;
        String method = getMethodName(event);
        CallbackInformationStruct callbackInfo = null;
        if(getProxyWrapper() != null)
        {
            String methodValue = getProxyWrapper().getListenerUserData().toString();

            callbackInfo = new CallbackInformationStruct(
                                                            interfaceName,
                                                            method,
                                                            methodValue,
                                                            iorString);
        }
        return callbackInfo;
    }

    public abstract String getMethodName(ChannelEvent event);

    public void startMethodInstrumentation(boolean privateOnly)
    {
        if(interceptor != null)
        {
            interceptor.startInstrumentation(getName(), privateOnly);
        }
    }

    public void stopMethodInstrumentation()
    {
        if(interceptor != null)
        {
            interceptor.removeInstrumentation();
        }
    }

    public void queueInstrumentationInitiated()
    {
        if(interceptor != null)
        {
            interceptor.addQueueInstrumentorRelation(QueueInstrumentorExtensionFactory.find(getName()));
        }
    }
}
