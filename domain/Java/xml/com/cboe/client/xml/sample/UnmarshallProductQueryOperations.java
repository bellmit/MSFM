//
// ------------------------------------------------------------------------
// FILE: UnmarshallProductQueryOperations.java
//
// PACKAGE: com.cboe.domain.xml.sample
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------
//

package com.cboe.client.xml.sample;

import org.xml.sax.InputSource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Marshaller;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.io.IOException;

import org.xml.sax.InputSource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;

import com.cboe.client.xml.bind.*;


/**
 * @author torresl@cboe.com
 */
public class UnmarshallProductQueryOperations
{
    public UnmarshallProductQueryOperations()
    {
        super();
    }

    public static void main(String[] args)
    {
        try
        {
            long t1 = System.currentTimeMillis();
            JAXBContext jc = JAXBContext.newInstance(args[0]);

            Unmarshaller unmarshaller = jc.createUnmarshaller();
            Marshaller marshaller = jc.createMarshaller();
            unmarshaller.setValidating(true);
            long t2 = System.currentTimeMillis();
            System.out.println("creation took "+ (t2-t1));
            for (int i = 1; i < args.length; i++)
            {
                String arg = args[i];
                File aFile = new File(arg);
                InputSource inputSource = null;
                if(aFile.exists())
                {
                    inputSource = new InputSource(new FileInputStream(aFile));
                    System.out.println("File:"+aFile.getName());
                }
                else
                {
                    inputSource = new InputSource(new StringReader(arg));
                    System.out.println("Input:"+arg);
                }
                t1 = System.currentTimeMillis();
                Object unmarshalledObject = unmarshaller.unmarshal(inputSource);
                t2 = System.currentTimeMillis();
                System.out.println("parse took: "+(t2-t1));
                process(unmarshalledObject);
                System.out.println("\n\n\n....MARSHALLING....\n\n");
                marshaller.marshal(unmarshalledObject, System.out);
            }
        }
        catch (JAXBException je)
        {
            je.printStackTrace();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }
    public static void process(Object object)
    {
        if(object instanceof GIContextDetailResponse)
        {
            process((GIContextDetailResponse) object);
        }
        else if(object instanceof GIProductQueryOperations)
        {
            process((GIProductQueryOperations) object);
        }
        else
        {
            System.out.println("UNKNOWN TYPE: "+object.getClass().getName());
        }

    }
    public static void process(GIContextDetailResponse cdr)
    {
        System.out.println("ProcessName: "+cdr.getOrbName());
        System.out.println("ClusterName: "+cdr.getClusterName());
        if(cdr.getContextDetailsLength()>0)
        {
            System.out.println("ContextDetails");
        }
        GIContextDetailType[] contextDetails = cdr.getContextDetails();
        for (int i = 0; i < contextDetails.length; i++)
        {
            GIContextDetailType contextDetail = contextDetails[i];
            System.out.println("ContextDetail ("+i+")");
            System.out.println("Name=    "+contextDetail.getName());
            System.out.println("FullName="+contextDetail.getFullName());

            if(contextDetail.getQueueInstrumentorsLength()>0)
            {
                process(contextDetail.getQueueInstrumentors());
            }
            if(contextDetail.getMethodInstrumentorsLength()>0)
            {
                process(contextDetail.getMethodInstrumentors());
            }
            if(contextDetail.getThreadInstrumentorsLength()>0)
            {
                process(contextDetail.getThreadInstrumentors());
            }
            if(contextDetail.getAssociatedContextsLength()>0)
            {
                process(contextDetail.getAssociatedContexts());
            }
            if(contextDetail.getClassInfoSequenceLength()>0)
            {
                process(contextDetail.getClassInfoSequence());
            }
            System.out.println("");
        }
        System.out.println("--");
    }

    public static void process(GIQueueInstrumentorType[] queueInstrumentorTypes)
    {
        for (int i = 0; i < queueInstrumentorTypes.length; i++)
        {
            System.out.println("QueueInstrumentor ("+i+")");
            GIQueueInstrumentorType queueInstrumentor = queueInstrumentorTypes[i];
            System.out.println("Enqueued="+queueInstrumentor.getEnqueued());
            System.out.println("Dequeued="+queueInstrumentor.getDequeued());
            System.out.println("Flushed= "+queueInstrumentor.getFlushed());
            System.out.println("Overlaid="+queueInstrumentor.getOverlaid());
            System.out.println("CurrSize="+queueInstrumentor.getCurrentSize());
            System.out.println("HighWtMk="+queueInstrumentor.getHighWaterMark());
            System.out.println("Status=  "+queueInstrumentor.getStatus());

            System.out.println("");
        }
    }

    public static void process(GIMethodInstrumentorType[] methodInstrumentorTypes)
    {
        for (int i = 0; i < methodInstrumentorTypes.length; i++)
        {
            System.out.println("MethodInstrumentor ("+i+")");
            GIMethodInstrumentorType methodInstrumentor = methodInstrumentorTypes[i];
            System.out.println("Calls   ="+methodInstrumentor.getCalls());
            System.out.println("MaxMthdT="+methodInstrumentor.getMaxMethodTime());
            System.out.println("MethodTm="+methodInstrumentor.getMethodTime());
            System.out.println("SSqrMthT="+methodInstrumentor.getSumOfSquareMethodTime());
            System.out.println("UserData="+methodInstrumentor.getUserData());
            System.out.println("");
        }
    }
    public static void process(GIThreadInstrumentorType[] threadInstrumentorTypes)
    {
        for (int i = 0; i < threadInstrumentorTypes.length; i++)
        {
            System.out.println("ThreadInstrumentor ("+i+")");
            GIThreadInstrumentorType threadInstrumentor = threadInstrumentorTypes[i];
            System.out.println("CurrExec="+threadInstrumentor.getCurrentlyExecutingThreads());
            System.out.println("Pending= "+threadInstrumentor.getPendingThreads());
            System.out.println("PndTskCn="+threadInstrumentor.getPendingTaskCount());
            System.out.println("PndHghWm="+threadInstrumentor.getPendingTaskCountHighWaterMark());
            System.out.println("Started= "+threadInstrumentor.getStartedThreads());
            System.out.println("SThHghWm= "+threadInstrumentor.getStartedThreadsHighWaterMark());
            System.out.println("");
        }
    }
    public static void process(GIAssociatedContextType[] associatedContextTypes)
    {
        for (int i = 0; i < associatedContextTypes.length; i++)
        {
            System.out.println("ThreadInstrumentor ("+i+")");
            GIAssociatedContextType associatedContext = associatedContextTypes[i];
            System.out.println("Name=    "+associatedContext.getName());
            System.out.println("FullName="+associatedContext.getFullName());
            System.out.println("Instrmnt="+associatedContext.getInstrumentor());
            System.out.println("");
        }
    }

    public static void process(GIClassInfoType[] classInfoTypes)
    {
        for (int i = 0; i < classInfoTypes.length; i++)
        {
            System.out.println("ClassInfoType ("+i+")");
            GIClassInfoType classInfo = classInfoTypes[i];
            System.out.println("Session= "+classInfo.getSessionName());
            System.out.println("PrdcType="+classInfo.getProductType());
            System.out.println("ClassKey="+classInfo.getClassKey());
            System.out.println("ClassKey="+classInfo.getProductKey());
            System.out.println("");
        }
    }

    public static void process(GIProductQueryOperations pqo)
    {

    }
}

