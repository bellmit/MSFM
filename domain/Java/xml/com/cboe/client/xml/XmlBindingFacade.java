//
// ------------------------------------------------------------------------
// FILE: XmlBindingFacade.java
//
// PACKAGE: com.cboe.domain.xml
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------
//

package com.cboe.client.xml;

import com.cboe.client.xml.bind.*;
import com.cboe.interfaces.domain.Delimeter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.PropertyException;

import org.xml.sax.InputSource;

import java.io.StringReader;
import java.io.StringWriter;

/**
 * @author torresl@cboe.com
 */
public class XmlBindingFacade
{
    public static final String xmlBindPackageName = "com.cboe.client.xml.bind";
    private ObjectFactory objectFactory;
    private JAXBContext context;
    private Marshaller marshaller;
    private Unmarshaller unmarshaller;
    private static XmlBindingFacade instance;
    private XmlBindingFacade()
    throws IllegalStateException
    {
        try
        {
            initialize();
        }
        catch (JAXBException e)
        {
            IllegalStateException ise = new IllegalStateException("Error initializing JAXB Context");
            ise.initCause(e);

            throw ise;
        }
    }

    public synchronized static final XmlBindingFacade getInstance()
    {
        if( instance == null)
        {
            instance = new XmlBindingFacade();
        }
        return instance;
    }

    private void initialize() throws JAXBException
    {
        context = JAXBContext.newInstance(xmlBindPackageName);
        marshaller = context.createMarshaller();
        unmarshaller = context.createUnmarshaller();
        unmarshaller.setValidating(false);
    }

    public synchronized ObjectFactory getObjectFactory()
    {
        if(objectFactory == null)
        {
            objectFactory = new ObjectFactory();
        }
        return objectFactory;
    }

    public String marshallResponse(GIContextDetailResponse contextDetailResponse)
    {
        try
        {
            getMarshaller().setProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION, "ContextDetailResponse.xsd");
            getMarshaller().setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            StringWriter response = new StringWriter();
            getMarshaller().marshal(contextDetailResponse, response);

            response.flush();
            return response.toString();
        }
        catch(JAXBException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public Object unmarshallXmlString(String xmlSource)
    {
        try
        {
            return getUnmarshaller().unmarshal(new InputSource(new StringReader(xmlSource)));
        }
        catch (JAXBException e)
        {
        }
        return null;
    }

    public String marshallObject(Object object, String schemaLocation, String encoding)
    {
        try
        {
            if (schemaLocation != null)
            {
                getMarshaller().setProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION, schemaLocation);
            }
            if (encoding == null)
            {
                encoding = "UTF-8";
            }
            getMarshaller().setProperty(Marshaller.JAXB_ENCODING, encoding);
            StringWriter writer = new StringWriter();
            try
            {
                getMarshaller().marshal(object, writer);
                writer.flush();
                return writer.toString();
            }
            catch (JAXBException e)
            {
            }
        }
        catch (PropertyException e)
        {
        }
        return null;
    }
    /*
     *  Create unmarshalled objects for each of the top level objects
     */

    public GIConfigurationRequestType getGIConfigurationRequestType(String xmlSource)
    {
        return (GIConfigurationRequestType) unmarshallXmlString(xmlSource);
    }

    public GIConfigurationResponseType getGIConfigurationResponseType(String xmlSource)
    {
        return (GIConfigurationResponseType) unmarshallXmlString(xmlSource);
    }

    public GIContextDetailRequestType getGIContextDetailRequestType(String xmlSource)
    {
        return (GIContextDetailRequestType) unmarshallXmlString(xmlSource);
    }

    public GIContextDetailResponseType getGIContextDetailResponseType(String xmlSource)
    {
        return (GIContextDetailResponseType) unmarshallXmlString(xmlSource);
    }

    public GIContextNameLookupType getGIContextNameLookupType(String xmlSource)
    {
        return (GIContextNameLookupType) unmarshallXmlString(xmlSource);
    }

    public GIContextNameResponseType getGIContextNameResponseType(String xmlSource)
    {
        return (GIContextNameResponseType) unmarshallXmlString(xmlSource);
    }

    public GIProductQueryOperationsType getGIProductQueryOperationsType(String xmlSource)
    {
        return (GIProductQueryOperationsType) unmarshallXmlString(xmlSource);
    }

    public GIProductQueryRequestType getGIProductQueryRequestType(String xmlSource)
    {
        return (GIProductQueryRequestType) unmarshallXmlString(xmlSource);
    }

    public GIProductQueryOperationsRequestType getGIProductQueryOperationsRequestType(String xmlSource)
    {
        return (GIProductQueryOperationsRequestType) unmarshallXmlString(xmlSource);
    }

    public GIUserExceptionType getGIUserExceptionType(String xmlSource)
    {
        return (GIUserExceptionType) unmarshallXmlString(xmlSource);
    }


    // Create Context Detail Request Methods
    public String createContextDetailRequest(String orbName, String[] contextNames)
    {
        try
        {
            GIContextDetailRequest request = getObjectFactory().createGIContextDetailRequest();
            request.setOrbName(orbName);
            request.setName(contextNames);
            StringWriter sw = new StringWriter();
            getMarshaller().marshal(request, sw);
            return sw.toString();
        }
        catch (JAXBException e)
        {
        }
        return null;
    }

    public String createConfigurationRequest(String orbName)
    {
        try
        {
            GIConfigurationRequest request = getObjectFactory().createGIConfigurationRequest();
            request.setOrbName(orbName);
            request.setRequestType(GIRequestTypeName.CAS_CONFIGURATION);
            StringWriter sw = new StringWriter();
            getMarshaller().marshal(request, sw);
            return sw.toString();
        }
        catch (JAXBException e)
        {
        }
        return null;
    }

    // Create Product Query Request Methods

    public String createClassBySessionForKeyRequest(String sessionName, int classKey)
    {
        try
        {
            GIProductQueryOperationsRequest pqor   = getObjectFactory().createGIProductQueryOperationsRequest();
            GIProductQueryOperationsRequestType.GIClassBySessionForKeyRequest operationRequest =
                    getObjectFactory().createGIProductQueryOperationsRequestTypeGIClassBySessionForKeyRequest();
            operationRequest.setMethodName(GIPQSMethodName.GET_CLASS_BY_SESSION_FOR_KEY);
            operationRequest.setSessionName(sessionName);
            operationRequest.setClassKey(classKey);
            pqor.setProductQueryRequest(operationRequest);
            StringWriter sw = new StringWriter();
            getMarshaller().marshal(pqor, sw);
            return sw.toString();
        }
        catch (JAXBException e)
        {
        }
        return null;
    }
    public String createClassesForSessionRequest(String sessionName, short productType)
    {
        try
        {
            GIProductQueryOperationsRequest pqor   = getObjectFactory().createGIProductQueryOperationsRequest();
            GIProductQueryOperationsRequestType.GIClassesForSessionRequest operationRequest =
                    getObjectFactory().createGIProductQueryOperationsRequestTypeGIClassesForSessionRequest();
            operationRequest.setMethodName(GIPQSMethodName.GET_CLASSES_FOR_SESSION);
            operationRequest.setSessionName(sessionName);
            operationRequest.setProductType(productType);
            pqor.setProductQueryRequest(operationRequest);
            StringWriter sw = new StringWriter();
            getMarshaller().marshal(pqor, sw);
            return sw.toString();
        }
        catch (JAXBException e)
        {
        }
        return null;
    }
    public String createCurrentTradingSessionsRequest()
    {
        try
        {
            GIProductQueryOperationsRequest pqor   = getObjectFactory().createGIProductQueryOperationsRequest();
            GIProductQueryOperationsRequestType.GICurrentTradingSessionsRequest operationRequest =
                    getObjectFactory().createGIProductQueryOperationsRequestTypeGICurrentTradingSessionsRequest();
            operationRequest.setMethodName(GIPQSMethodName.GET_CURRENT_TRADING_SESSIONS);
            pqor.setProductQueryRequest(operationRequest);
            StringWriter sw = new StringWriter();
            getMarshaller().marshal(pqor, sw);
            return sw.toString();
        }
        catch (JAXBException e)
        {
        }
        return null;
    }
    public String createProductBySessionForKeyRequest(String sessionName, int productKey)
    {
        try
        {
            GIProductQueryOperationsRequest pqor   = getObjectFactory().createGIProductQueryOperationsRequest();
            GIProductQueryOperationsRequestType.GIProductBySessionForKeyRequest operationRequest =
                    getObjectFactory().createGIProductQueryOperationsRequestTypeGIProductBySessionForKeyRequest();
            operationRequest.setMethodName(GIPQSMethodName.GET_PRODUCT_BY_SESSION_FOR_KEY);
            operationRequest.setSessionName(sessionName);
            operationRequest.setProductKey(productKey);
            pqor.setProductQueryRequest(operationRequest);
            StringWriter sw = new StringWriter();
            getMarshaller().marshal(pqor, sw);
            return sw.toString();
        }
        catch (JAXBException e)
        {
        }
        return null;
    }
    public String createProductsForSessionRequest(String sessionName, int classKey)
    {
        try
        {
            GIProductQueryOperationsRequest pqor   = getObjectFactory().createGIProductQueryOperationsRequest();
            GIProductQueryOperationsRequestType.GIProductsForSessionRequest operationRequest =
                    getObjectFactory().createGIProductQueryOperationsRequestTypeGIProductsForSessionRequest();
            operationRequest.setMethodName(GIPQSMethodName.GET_PRODUCTS_FOR_SESSION);
            operationRequest.setSessionName(sessionName);
            operationRequest.setClassKey(classKey);
            pqor.setProductQueryRequest(operationRequest);
            StringWriter sw = new StringWriter();
            getMarshaller().marshal(pqor, sw);
            return sw.toString();
        }
        catch (JAXBException e)
        {
        }
        return null;
    }
    public String createProductTypesForSessionRequest(String sessionName)
    {
        try
        {
            GIProductQueryOperationsRequest pqor   = getObjectFactory().createGIProductQueryOperationsRequest();
            GIProductQueryOperationsRequestType.GIProductTypesForSessionRequest operationRequest =
                    getObjectFactory().createGIProductQueryOperationsRequestTypeGIProductTypesForSessionRequest();
            operationRequest.setMethodName(GIPQSMethodName.GET_PRODUCT_TYPES_FOR_SESSION);
            operationRequest.setSessionName(sessionName);
            pqor.setProductQueryRequest(operationRequest);
            StringWriter sw = new StringWriter();
            getMarshaller().marshal(pqor, sw);
            return sw.toString();
        }
        catch (JAXBException e)
        {
        }
        return null;
    }
    public String createClassByKeyRequest(int classKey)
    {
        try
        {
            GIProductQueryOperationsRequest pqor   = getObjectFactory().createGIProductQueryOperationsRequest();
            GIProductQueryOperationsRequestType.GIClassByKeyRequest operationRequest =
                    getObjectFactory().createGIProductQueryOperationsRequestTypeGIClassByKeyRequest();
            operationRequest.setMethodName(GIPQSMethodName.GET_CLASS_BY_KEY);
            operationRequest.setClassKey(classKey);
            pqor.setProductQueryRequest(operationRequest);
            StringWriter sw = new StringWriter();
            getMarshaller().marshal(pqor, sw);
            return sw.toString();
        }
        catch (JAXBException e)
        {
        }
        return null;
    }
    public String createProductByKeyRequest(int productKey)
    {
        try
        {
            GIProductQueryOperationsRequest pqor   = getObjectFactory().createGIProductQueryOperationsRequest();
            GIProductQueryOperationsRequestType.GIProductByKeyRequest operationRequest =
                    getObjectFactory().createGIProductQueryOperationsRequestTypeGIProductByKeyRequest();
            operationRequest.setMethodName(GIPQSMethodName.GET_PRODUCT_BY_KEY);
            operationRequest.setProductKey(productKey);
            pqor.setProductQueryRequest(operationRequest);
            StringWriter sw = new StringWriter();
            getMarshaller().marshal(pqor, sw);
            return sw.toString();
        }
        catch (JAXBException e)
        {
        }
        return null;
    }
    public String createProductClassesRequest(short productType)
    {
        try
        {
            GIProductQueryOperationsRequest pqor   = getObjectFactory().createGIProductQueryOperationsRequest();
            GIProductQueryOperationsRequestType.GIProductClassesRequest operationRequest =
                    getObjectFactory().createGIProductQueryOperationsRequestTypeGIProductClassesRequest();
            operationRequest.setMethodName(GIPQSMethodName.GET_PRODUCT_CLASSES);
            operationRequest.setProductType(productType);
            pqor.setProductQueryRequest(operationRequest);
            StringWriter sw = new StringWriter();
            getMarshaller().marshal(pqor, sw);
            return sw.toString();
        }
        catch (JAXBException e)
        {
        }
        return null;
    }
    public String createProductTypesRequest()
    {
        try
        {
            GIProductQueryOperationsRequest pqor   = getObjectFactory().createGIProductQueryOperationsRequest();
            GIProductQueryOperationsRequestType.GIProductTypesRequest operationRequest =
                    getObjectFactory().createGIProductQueryOperationsRequestTypeGIProductTypesRequest();
            operationRequest.setMethodName(GIPQSMethodName.GET_PRODUCT_TYPES);
            pqor.setProductQueryRequest(operationRequest);
            StringWriter sw = new StringWriter();
            getMarshaller().marshal(pqor, sw);
            return sw.toString();
        }
        catch (JAXBException e)
        {
        }
        return null;
    }
    public String createProductsByClassRequest(int classKey)
    {
        try
        {
            GIProductQueryOperationsRequest pqor   = getObjectFactory().createGIProductQueryOperationsRequest();
            GIProductQueryOperationsRequestType.GIProductsByClassRequest operationRequest =
                    getObjectFactory().createGIProductQueryOperationsRequestTypeGIProductsByClassRequest();
            operationRequest.setMethodName(GIPQSMethodName.GET_PRODUCTS_BY_CLASS);
            operationRequest.setClassKey(classKey);
            pqor.setProductQueryRequest(operationRequest);
            StringWriter sw = new StringWriter();
            getMarshaller().marshal(pqor, sw);
            return sw.toString();
        }
        catch (JAXBException e)
        {
        }
        return null;
    }

    public String createStrategiesForSessionByClassRequest(String sessionName, int classKey)
    {
        try
        {
            GIProductQueryOperationsRequest pqor   = getObjectFactory().createGIProductQueryOperationsRequest();
            GIProductQueryOperationsRequestType.GIStrategiesForSessionByClassRequest operationRequest =
                    getObjectFactory().createGIProductQueryOperationsRequestTypeGIStrategiesForSessionByClassRequest();
            operationRequest.setMethodName(GIPQSMethodName.GET_STRATEGIES_FOR_SESSION_BY_CLASS);
            operationRequest.setSessionName(sessionName);
            operationRequest.setClassKey(classKey);
            pqor.setProductQueryRequest(operationRequest);
            StringWriter sw = new StringWriter();
            getMarshaller().marshal(pqor, sw);
            return sw.toString();
        }
        catch (JAXBException e)
        {
        }
        return null;
    }
    public String createStrategiesByClassRequest(int classKey)
    {
        try
        {
            GIProductQueryOperationsRequest pqor   = getObjectFactory().createGIProductQueryOperationsRequest();
            GIProductQueryOperationsRequestType.GIStrategiesByClassRequest operationRequest =
                    getObjectFactory().createGIProductQueryOperationsRequestTypeGIStrategiesByClassRequest();
            operationRequest.setMethodName(GIPQSMethodName.GET_STRATEGIES_BY_CLASS);
            operationRequest.setClassKey(classKey);
            pqor.setProductQueryRequest(operationRequest);
            StringWriter sw = new StringWriter();
            getMarshaller().marshal(pqor, sw);
            return sw.toString();
        }
        catch (JAXBException e)
        {
        }
        return null;
    }
    public String createStrategyByKeyRequest(int productKey)
    {
        try
        {
            GIProductQueryOperationsRequest pqor   = getObjectFactory().createGIProductQueryOperationsRequest();
            GIProductQueryOperationsRequestType.GIStrategyByKeyRequest operationRequest =
                    getObjectFactory().createGIProductQueryOperationsRequestTypeGIStrategyByKeyRequest();
            operationRequest.setMethodName(GIPQSMethodName.GET_STRATEGIES_BY_CLASS);
            operationRequest.setProductKey(productKey);
            pqor.setProductQueryRequest(operationRequest);
            StringWriter sw = new StringWriter();
            getMarshaller().marshal(pqor, sw);
            return sw.toString();
        }
        catch (JAXBException e)
        {
        }
        return null;
    }

    public String createStrategyBySessionForKeyRequest(String sessionName, int productKey)
    {
        try
        {
            GIProductQueryOperationsRequest pqor   = getObjectFactory().createGIProductQueryOperationsRequest();
            GIProductQueryOperationsRequestType.GIStrategyBySessionForKeyRequest operationRequest =
                    getObjectFactory().createGIProductQueryOperationsRequestTypeGIStrategyBySessionForKeyRequest();
            operationRequest.setMethodName(GIPQSMethodName.GET_STRATEGY_BY_SESSION_FOR_KEY);
            operationRequest.setSessionName(sessionName);
            operationRequest.setProductKey(productKey);
            pqor.setProductQueryRequest(operationRequest);
            StringWriter sw = new StringWriter();
            getMarshaller().marshal(pqor, sw);
            return sw.toString();
        }
        catch (JAXBException e)
        {
        }
        return null;
    }

    public String getDelimitedUserData(GIUserDataType userDataType)
    {
        StringBuffer buffer = new StringBuffer(500);
        if(userDataType != null)
        {
            String[] dataElements = userDataType.getDataElements();
            for (int i=0; i < dataElements.length; i++)
            {
                buffer.append(dataElements[i]).append(Delimeter.PROPERTY_DELIMETER);
            }
        }
        return buffer.toString();
    }

    public JAXBContext getContext()
    {
        return context;
    }

    public Marshaller getMarshaller()
    {
        return marshaller;
    }

    public Unmarshaller getUnmarshaller()
    {
        return unmarshaller;
    }
    public GIProductQueryOperationsType createGIProductQueryOperationsType()
    {
        try
        {
            GIProductQueryOperationsType giProductQueryOperationsType = getObjectFactory().createGIProductQueryOperations();
            giProductQueryOperationsType.setClassStructSequence(getObjectFactory().createGIClassStructSequence());
            giProductQueryOperationsType.setProductStructSequence(getObjectFactory().createGIProductStructSequence());
            giProductQueryOperationsType.setProductTypeStructSequence(getObjectFactory().createGIProductTypeStructSequence());
            giProductQueryOperationsType.setSessionClassStructSequence(getObjectFactory().createGISessionClassStructSequence());
            giProductQueryOperationsType.setSessionProductStructSequence(getObjectFactory().createGISessionProductStructSequence());
            giProductQueryOperationsType.setStrategyStructSequence(getObjectFactory().createGIStrategyStructSequence());
            giProductQueryOperationsType.setTradingSessionStructSequence(getObjectFactory().createGITradingSessionStructSequence());
            return giProductQueryOperationsType;
        }
        catch (JAXBException e)
        {
        }
        return null;
    }
    public GIUserExceptionType createUserExceptionType(String className, String message, String stackTraceText,
                                                       int error, short severity, String detailMessage, String dateTime)
    {
        try
        {
            GIUserExceptionType giUserExceptionType = getObjectFactory().createGIUserExceptionType();

            giUserExceptionType.setMessage(message);
            giUserExceptionType.setClassName(className);
            giUserExceptionType.setStackTraceText(stackTraceText);
            GIExceptionDetail giExceptionDetail = getObjectFactory().createGIExceptionDetail();
            giExceptionDetail.setDateTime(dateTime);
            giExceptionDetail.setError(error);
            giExceptionDetail.setMessage(detailMessage);
            giExceptionDetail.setSeverity(severity);
            giUserExceptionType.setDetail(giExceptionDetail);
            return giUserExceptionType;
        }
        catch (JAXBException e)
        {

        }
        return null;
    }

    public GIContextDetailResponse createGIContextDetailResponse(String clusterName, String orbName)
    {
        try
        {
            GIContextDetailResponse giContextDetailResponseType = getObjectFactory().createGIContextDetailResponse();
            giContextDetailResponseType.setClusterName(clusterName);
            giContextDetailResponseType.setOrbName(orbName);
            return giContextDetailResponseType;
        }
        catch (JAXBException e)
        {
        }
        return null;
    }

    public GIContextDetailType createGIContextDetailType(String fullName, String name)
    {
        try
        {
            GIContextDetailType giContextDetailType = getObjectFactory().createGIContextDetailType();
            giContextDetailType.setFullName(fullName);
            giContextDetailType.setName(name);
            return giContextDetailType;
        }
        catch (JAXBException e)
        {
        }
        return null;
    }
    public GIThreadInstrumentorType createGIThreadInstrumentorType(
            int pendingTaskCount,
            int pendingTaskCountHighWaterMark,
            int currentlyExecutingThreads,
            int pendingThreads,
            int startedThreads,
            int startedThreadsHighWaterMark,
            String[] userData
            )
    {
        try
        {
            GIThreadInstrumentorType giThreadInstrumentorType = getObjectFactory().createGIThreadInstrumentorType();
            giThreadInstrumentorType.setUserData(createGIUserDataType(userData));
            giThreadInstrumentorType.setPendingTaskCount(pendingTaskCount);
            giThreadInstrumentorType.setPendingTaskCountHighWaterMark(pendingTaskCountHighWaterMark);
            giThreadInstrumentorType.setCurrentlyExecutingThreads(currentlyExecutingThreads);
            giThreadInstrumentorType.setPendingThreads(pendingThreads);
            giThreadInstrumentorType.setStartedThreads(startedThreads);
            giThreadInstrumentorType.setStartedThreadsHighWaterMark(startedThreadsHighWaterMark);

            return giThreadInstrumentorType;
        }
        catch (JAXBException e)
        {
        }
        return null;
    }

    public GIUserDataType createGIUserDataType(String[] dataElements)
    {
        try
        {
            GIUserDataType giUserDataType = getObjectFactory().createGIUserDataType();
            giUserDataType.setDataElements(dataElements);
            return giUserDataType;
        }
        catch (JAXBException e)
        {
        }
        return null;
    }
    public GIMethodInstrumentorType createGIMethodInstrumentorType(
            long calls, long exceptions,
            double methodTime, long maxMethodTime, double sumOfSquareMethodTime,
            String[] userData)
    {
        try
        {
            GIMethodInstrumentorType giMethodInstrumentorType = XmlBindingFacade.getInstance().getObjectFactory().createGIMethodInstrumentorType();
            giMethodInstrumentorType.setUserData(createGIUserDataType(userData));
            giMethodInstrumentorType.setCalls(calls);
            giMethodInstrumentorType.setExceptions(exceptions);
            giMethodInstrumentorType.setMethodTime(methodTime);
            giMethodInstrumentorType.setMaxMethodTime(maxMethodTime);
            giMethodInstrumentorType.setSumOfSquareMethodTime(sumOfSquareMethodTime);
            return giMethodInstrumentorType;
        }
        catch (JAXBException e)
        {
        }
        return null;
    }
    public GIQueueInstrumentorType createGIQueueInstrumentorType(
            long currentSize, long dequeued, long enqueued,
            long flushed, long highWaterMark, long overlaid,
            short status, String[] userData
            )
    {
        try
        {
            GIQueueInstrumentorType giQueueInstrumentorType = XmlBindingFacade.getInstance().getObjectFactory().createGIQueueInstrumentorType();
            giQueueInstrumentorType.setUserData(createGIUserDataType(userData));
            giQueueInstrumentorType.setCurrentSize(currentSize);
            giQueueInstrumentorType.setDequeued(dequeued);
            giQueueInstrumentorType.setEnqueued(enqueued);
            giQueueInstrumentorType.setFlushed(flushed);
            giQueueInstrumentorType.setHighWaterMark(highWaterMark);
            giQueueInstrumentorType.setOverlaid(overlaid);
            giQueueInstrumentorType.setStatus(status);
            return giQueueInstrumentorType;
        }
        catch (JAXBException e)
        {
        }
        return null;
    }

    public GIAssociatedContextType createGIAssociatedContextType(
            String fullName, String name, String instrumentorType )
    {
        try
        {
            GIAssociatedContextType giAssociatedContextType = XmlBindingFacade.getInstance().getObjectFactory().createGIAssociatedContextType();
            giAssociatedContextType.setFullName(fullName);
            giAssociatedContextType.setName(name);
            giAssociatedContextType.setInstrumentor(instrumentorType);
            return giAssociatedContextType;
        }
        catch (JAXBException e)
        {
        }
        return null;
    }

}
