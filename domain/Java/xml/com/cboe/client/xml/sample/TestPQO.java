//
// ------------------------------------------------------------------------
// FILE: TestPQO.java
// 
// PACKAGE: com.cboe.client.xml.sample
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
// 
// ------------------------------------------------------------------------
//

package com.cboe.client.xml.sample;

import com.cboe.client.xml.XmlBindingFacade;
import com.cboe.client.xml.bind.*;

import java.util.Iterator;
import java.util.Random;

/**
 * @author torresl@cboe.com
 */
public class TestPQO
{

    public TestPQO()
    {
        super();
    }
    public void doTest1(boolean print)
    {
        // marshalling
        int classKey = (int)(Math.random()*100000);
        String xml = XmlBindingFacade.getInstance().createClassBySessionForKeyRequest("W_AM1", classKey);
        if(print)
        {
            System.out.println("MARSHALLED:"+xml);
        }
        // unmarshalling...
        GIProductQueryOperationsRequestType pqor = XmlBindingFacade.getInstance().getGIProductQueryOperationsRequestType(xml);
        GIProductQueryRequestType pqr = pqor.getProductQueryRequest();
        GIPQSMethodName mn = pqr.getMethodName();
        if(mn.equals(GIPQSMethodName.GET_CLASS_BY_SESSION_FOR_KEY))
        {
            GISessionClassKeyRequestType sessionClassKeyRequestType = (GISessionClassKeyRequestType) pqr;
            if(print)
            {
                System.out.println("UNMARSHALL:"+mn.getValue()+
                                   " sessionName:"+sessionClassKeyRequestType.getSessionName()+
                                   " classKey:"+sessionClassKeyRequestType.getClassKey());
            }
        }
    }
    public void doTest2(boolean print)
    {
        int productKey = (int)(Math.random()*100000);
        // marshalling
        String xml = XmlBindingFacade.getInstance().createProductBySessionForKeyRequest("W_AM1", productKey);
        if(print)
        {
            System.out.println("MARSHALLED:"+xml);
        }
        // unmarshalling...
        GIProductQueryOperationsRequestType pqor = XmlBindingFacade.getInstance().getGIProductQueryOperationsRequestType(xml);
        GIProductQueryRequestType pqr = pqor.getProductQueryRequest();
        GIPQSMethodName mn = pqr.getMethodName();
        if(mn.equals(GIPQSMethodName.GET_CLASS_BY_SESSION_FOR_KEY))
        {
            GISessionClassKeyRequestType sessionClassKeyRequestType = (GISessionClassKeyRequestType) pqr;
            if(print)
            {
                System.out.println("UNMARSHALL:"+mn.getValue()+
                               " sessionName:"+sessionClassKeyRequestType.getSessionName()+
                               " classKey:"+sessionClassKeyRequestType.getClassKey());
            }

        }
    }
    public static void main(String[] args)
    {
        boolean print = false;
        if(args.length>0)
        {
            print = Boolean.valueOf(args[0]).booleanValue();
        }
        TestPQO p = new TestPQO();
        long t1 = System.currentTimeMillis();
        XmlBindingFacade.getInstance();//just to initialize
        long t2 = System.currentTimeMillis();
        System.out.println("init time:"+(t2-t1));
        StringBuffer buffer = new StringBuffer(5000);
        for(int i=1; i<=20; i++)
        {
            t1 = System.currentTimeMillis();
            p.doTest1(print);
            t2 = System.currentTimeMillis();
            buffer.append("test1:").append(t2-t1).append("\n");
            t1 = System.currentTimeMillis();
            p.doTest2(print);
            t2 = System.currentTimeMillis();
            buffer.append("test2:").append(t2-t1).append("\n");
        }
        System.out.println(buffer.toString());
    }
}
