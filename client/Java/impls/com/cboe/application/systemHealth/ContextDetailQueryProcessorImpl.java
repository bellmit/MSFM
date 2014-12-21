package com.cboe.application.systemHealth;

import com.cboe.interfaces.application.SystemHealthQueryProcessor;
import com.cboe.client.xml.bind.GIContextDetailRequestType;
import com.cboe.client.xml.bind.GIContextDetailResponse;
import com.cboe.client.xml.bind.GIContextDetail;
import com.cboe.client.xml.bind.GIContextDetailType;
import com.cboe.client.xml.XmlBindingFacade;
import com.cboe.domain.instrumentorExtension.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.systemsManagementService.ApplicationPropertyHelper;

import javax.xml.bind.JAXBException;
import java.util.Vector;

class ContextDetailQueryProcessorImpl implements SystemHealthQueryProcessor
{
    private String xmlRequest;
    private static String orbName;
    private static String clusterName;

    public static void initialize()
    {
        try
        {
            orbName = System.getProperty("ORB.OrbName");
            clusterName = System.getProperty("prefixCluster");
        }
        catch(Exception e)
        {
            Log.alarm("Error initialization in ContextDetailQueryProcessorImpl.");
        }
    }

    ContextDetailQueryProcessorImpl(String xmlRequest)
    {
        this.xmlRequest = xmlRequest;
    }

    public String processRequest()
    {
        String xmlResult = null;
        try
        {
            if(xmlRequest == null || xmlRequest.equals(EMPTY_STRING))
            {
                throw new IllegalArgumentException("Request string cannot be empty; must be XML.");
            }

            GIContextDetailRequestType contextDetailRequest = XmlBindingFacade.getInstance().getGIContextDetailRequestType(xmlRequest);
            String[] requests = contextDetailRequest.getName();
            GIContextDetailResponse contextDetailResponse = XmlBindingFacade.getInstance().getObjectFactory().createGIContextDetailResponse();
            contextDetailResponse.setOrbName(orbName);
            contextDetailResponse.setClusterName(clusterName);
            GIContextDetail context = null;
            Vector contextDetails = new Vector();
            for(int i=0; i<requests.length; i++)
            {
                if (Log.isDebugOn()) { 
                    Log.debug("process request:"+requests[i]);
                }
                context = addQueueInstrumentorContext(requests[i]);
                if(context != null)
                {
                    if (Log.isDebugOn()) { 
                        Log.debug("found queue instrumentor");
                    }
                    contextDetails.add(context);
                    continue;
                }
                context = addThreadPoolInstrumentorContext(requests[i]);
                if(context != null)
                {
                    if (Log.isDebugOn()) { 
                        Log.debug("found threadPool instrumentor");
                    }
                    contextDetails.add(context);
                    continue;
                }
                context = addMethodInstrumentorContext(requests[i]);
                if(context != null)
                {
                    if (Log.isDebugOn()) { 
                        Log.debug("found method instrumentor");
                    }
                    contextDetails.add(context);
                    continue;
                }
                context = addAllAssociatedInstrumentorContext(requests[i]);
                if(context != null)
                {
                    if (Log.isDebugOn()) { 
                        Log.debug("found associated context detail");
                    }
                    contextDetails.add(context);
                    continue;
                }
            }
            contextDetailResponse.setContextDetails((GIContextDetailType[])contextDetails.toArray(new GIContextDetail[0]));
            return marshallResponse(contextDetailResponse);
        }
        catch(Exception e)
        {
            xmlResult = SystemHealthXMLHelper.logAndConvertException(xmlRequest, "contextDetailRequest", e);
        }

        return xmlResult;
    }

    public String marshallResponse(GIContextDetailResponse contextDetailResponse)
    {
        return XmlBindingFacade.getInstance().marshallResponse(contextDetailResponse);
    }

    private GIContextDetail addQueueInstrumentorContext(String request)
    {
        GIContextDetail contextDetail = null;
        QueueInstrumentorExtension qiExtension = QueueInstrumentorExtensionFactory.find(request);
        if(qiExtension != null)
        {
            contextDetail = qiExtension.getContextDetail();
        }
        return contextDetail;
    }

    private GIContextDetail addThreadPoolInstrumentorContext(String request)
    {
        GIContextDetail contextDetail = null;
        ThreadPoolInstrumentorExtension tiExtension = ThreadPoolInstrumentorExtensionFactory.find(request);
        if(tiExtension != null)
        {
            contextDetail = tiExtension.getContextDetail();
        }
        return contextDetail;
    }

    private GIContextDetail addMethodInstrumentorContext(String request)
    {
        GIContextDetail contextDetail = null;
        MethodInstrumentorExtension miExtension = MethodInstrumentorExtensionFactory.find(request);
        if(miExtension != null)
        {
            contextDetail = miExtension.getContextDetail();
        }
        return contextDetail;
    }

    private GIContextDetail addAllAssociatedInstrumentorContext(String request)
    {
        try
        {
            GIContextDetail contextDetail =  XmlBindingFacade.getInstance().getObjectFactory().createGIContextDetail();
            InstrumentorVisitorImpl visitor = new InstrumentorVisitorImpl(request, contextDetail);
            visitor.startVisit();
            return contextDetail;
        }
        catch (JAXBException e)
        {
            Log.exception(e);
            return null;
        }
    }
}
