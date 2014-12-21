//
// ------------------------------------------------------------------------
// FILE: MarshallContextDetailResponse.java
//
// PACKAGE: com.cboe.domain.xml.sample
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------
//

package com.cboe.client.xml.sample;
import com.cboe.client.xml.bind.*;
import com.cboe.interfaces.domain.Delimeter;

import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


/**
 * @author torresl@cboe.com
 */
public class MarshallContextDetailResponse
{
    public MarshallContextDetailResponse()
    {
        super();
    }

    public String testContextDetailResponse(ObjectFactory objectFactory, Marshaller marshaller)
            throws JAXBException
    {
        GIContextDetailResponse contextDetailResponse = objectFactory.createGIContextDetailResponse();
        contextDetailResponse.setOrbName("devcas100");
        contextDetailResponse.setClusterName("CAS");
        GIContextDetailType contextDetail = objectFactory.createGIContextDetail();
        GIQueueInstrumentorType queueInstrumentor = objectFactory.createGIQueueInstrumentor();
        queueInstrumentor.setCurrentSize(20);
        queueInstrumentor.setDequeued(50);
        queueInstrumentor.setEnqueued(100);
        queueInstrumentor.setFlushed(123);
        queueInstrumentor.setOverlaid(456);
        queueInstrumentor.setHighWaterMark(1234);
        queueInstrumentor.setStatus((short)0);
        StringBuffer userDataText = new StringBuffer();
        userDataText.append("<xml>\"").append("data").append("\"<xml>");
        GIUserData userData = objectFactory.createGIUserData();

        userData.setDataElements(new String[]{userDataText.toString()});
        queueInstrumentor.setUserData(userData);
        contextDetail.setQueueInstrumentors(new GIQueueInstrumentorType[]{queueInstrumentor});
        contextDetailResponse.setContextDetails(new GIContextDetailType[]{contextDetail});
        contextDetail.setName("Name...456@MMM");
        contextDetail.setFullName("FullName...456@MMM");
        marshaller.setProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION, "ContextDetailResponse.xsd");
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        StringWriter writer = new StringWriter();
        marshaller.marshal(contextDetailResponse, writer);
        writer.flush();
        return writer.toString();
    }
    public static void main(String[] args)
    {
        try
        {
            if(args.length != 2)
            {
                System.out.println("MarshallContextDetailResponse packageName outputFile");
            }
            else
            {
                JAXBContext jc = JAXBContext.newInstance(args[0]);
                Marshaller marshaller = jc.createMarshaller();
                ObjectFactory objectFactory = new ObjectFactory();
                MarshallContextDetailResponse mcdr = new MarshallContextDetailResponse();
                File outputFile = new File(args[1]);
                if(outputFile.exists())
                {
                    if(!outputFile.canWrite())
                    {
                        System.out.println("Cannot write to "+outputFile.getName());
                    }
                    else
                    {
                        outputFile.delete();
                    }
                }
                FileWriter writer = new FileWriter(outputFile);
                String output = mcdr.testContextDetailResponse(objectFactory, marshaller);
                writer.write(output);
                writer.flush();
                writer.close();
            }
        }
        catch (JAXBException je)
        {
            je.printStackTrace(System.out);
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace(System.out);
        }
    }

}
