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

import com.cboe.client.xml.bind.*;
import com.cboe.client.xml.bind.impl.runtime.ValidatableObject;
import org.xml.sax.InputSource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.BufferedReader;


/**
 * @author torresl@cboe.com
 */
public class UnmarshallContextDetailResponse
{
    public UnmarshallContextDetailResponse()
    {
        super();
    }

    public static void main(String[] args)
    {
        try
        {
            if(args.length != 2)
            {
                System.out.println("UnmarshallContextDetailResponse packageName inputFile");
            }
            else
            {
                System.out.println("Starting...");
                long t1 = System.currentTimeMillis();
                JAXBContext jc = JAXBContext.newInstance(args[0]);
                Unmarshaller unmarshaller = jc.createUnmarshaller();
                UnmarshallContextDetailResponse ucdr = new UnmarshallContextDetailResponse();
                long t2 = System.currentTimeMillis();
                System.out.println("Initialization Time (ms)="+ (t2 - t1));
                t1 = t2;
                File inputFile = new File(args[1]);
                if(!inputFile.canRead())
                {
                    System.out.println("Cannot read from "+inputFile.getName());
                    System.exit(1);
                }
                FileInputStream fis = new FileInputStream(inputFile);
                StringBuffer buffer = new StringBuffer(5000);
                for(int i=0;(i=fis.available())>0;)
                {
                    byte[] b = new byte[i];
                    fis.read(b);
                    String s = new String(b);
                    buffer.append(s);
                }
                String inputXml = buffer.toString();
                t2 = System.currentTimeMillis();
                System.out.println("Read File (ms)="+ (t2 - t1));
                t1 = t2;
                unmarshaller.unmarshal(new InputSource(new StringReader(inputXml)));
                t2 = System.currentTimeMillis();
                System.out.println("First unmarshalling (ms)="+ (t2 - t1));
                t1 = t2;
//                ucdr.process(cdr);
//                t2 = System.currentTimeMillis();
//                System.out.println("Processed (ms)="+ (t2 - t1));
//                t1 = t2;
                for(int i=2; i<=20; i++)
                {
                    unmarshaller.unmarshal(new InputSource(new StringReader(inputXml)));
                    t2 = System.currentTimeMillis();
                    System.out.println("Unmarshalling "+i+"(ms)="+ (t2 - t1));
                    t1 = t2;
                }
                System.out.println("\n\nDone...");
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


    public void process(GIContextDetailResponse cdr)
    {
        System.out.println("OrbName: "+cdr.getOrbName());
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

    public void process(GIQueueInstrumentorType[] queueInstrumentorTypes)
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
            process(queueInstrumentor.getUserData());

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

            process(methodInstrumentor.getUserData());
            System.out.println("");
        }
    }
    public static void process(GIUserDataType userData)
    {
        if(userData != null)
        {
            System.out.println("UserData");
            String[] userDataElements = userData.getDataElements();
            for (int j = 0; j < userDataElements.length; j++)
            {
                System.out.println(userDataElements[j]);
            }
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
            process(threadInstrumentor.getUserData());
            System.out.println("");
        }
    }
    public void process(GIAssociatedContextType[] associatedContextTypes)
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

    public  void process(GIClassInfoType[] classInfoTypes)
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

}

