package com.cboe.application.systemHealth;

import com.cboe.idl.cmiSession.SessionClassStruct;
import com.cboe.idl.cmiSession.TradingSessionStruct;
import com.cboe.idl.cmiSession.SessionProductStruct;
import com.cboe.idl.cmiSession.SessionStrategyStruct;
import com.cboe.idl.cmiProduct.ProductTypeStruct;
import com.cboe.idl.cmiProduct.ClassStruct;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiStrategy.StrategyStruct;
import com.cboe.client.xml.bind.*;
import com.cboe.client.xml.XmlBindingFacade;
import com.cboe.client.xml.XmlProductBindingFacade;

abstract class SystemHealthProductQueryXMLHelper extends SystemHealthXMLHelper
{
    static String convertToXml(ProductTypeStruct[] productTypeStructs)
    {
        GIProductQueryOperationsType pqo = XmlBindingFacade.getInstance().createGIProductQueryOperationsType();
        if(productTypeStructs != null && productTypeStructs.length>0)
        {
            GIProductTypeStructType[] giProductTypeStructs = new GIProductTypeStructType[productTypeStructs.length];
            for (int i = 0; i < productTypeStructs.length; i++)
            {
                giProductTypeStructs[i] = XmlProductBindingFacade.getInstance().getGIProductTypeStructType(productTypeStructs[i]);
            }
            pqo.getProductTypeStructSequence().setProductTypeStructs(giProductTypeStructs);
        }
        return marshal(pqo);
    }


    static String convertToXml(TradingSessionStruct[] tradingSessionStructs)
    {
        GIProductQueryOperationsType pqo = XmlBindingFacade.getInstance().createGIProductQueryOperationsType();
        if(tradingSessionStructs != null && tradingSessionStructs.length>0)
        {
            GITradingSessionStructType[] giTradingSessionStructs = new GITradingSessionStructType[tradingSessionStructs.length];
            for (int i = 0; i < tradingSessionStructs.length; i++)
            {
                giTradingSessionStructs[i] = XmlProductBindingFacade.getInstance().getTradingSessionStructType(tradingSessionStructs[i]);
            }
            pqo.getTradingSessionStructSequence().setTradingSessionStructs(giTradingSessionStructs);
        }
        return marshal(pqo);
    }

    static String convertToXml(SessionClassStruct[] sessionClassStructs)
    {
        GIProductQueryOperationsType pqo = XmlBindingFacade.getInstance().createGIProductQueryOperationsType();
        if(sessionClassStructs != null && sessionClassStructs.length>0)
        {
            GISessionClassStructType[] giSessionClassStructs = new GISessionClassStructType[sessionClassStructs.length];
            for (int i = 0; i < sessionClassStructs.length; i++)
            {
                giSessionClassStructs[i] = XmlProductBindingFacade.getInstance().getGISessionClassStructType(sessionClassStructs[i]);
            }
            pqo.getSessionClassStructSequence().setSessionClassStructs(giSessionClassStructs);
        }
        return marshal(pqo);
    }
    
    static String convertToXml(SessionProductStruct[] sessionProductStructs)
    {
        GIProductQueryOperationsType pqo = XmlBindingFacade.getInstance().createGIProductQueryOperationsType();
        if(sessionProductStructs != null && sessionProductStructs.length>0)
        {
            GISessionProductStructType[] giSessionProductStructs = new GISessionProductStructType[sessionProductStructs.length];
            for(int i = 0; i < sessionProductStructs.length; i++)
            {
                giSessionProductStructs[i] = XmlProductBindingFacade.getInstance().getGISessionProductStructType(sessionProductStructs[i]);
            }
            pqo.getSessionProductStructSequence().setSessionProductStructs(giSessionProductStructs);
        }
        return marshal(pqo);
    }
    
    static String convertToXml(ClassStruct[] classStructs)
    {
        GIProductQueryOperationsType pqo = XmlBindingFacade.getInstance().createGIProductQueryOperationsType();
        if(classStructs != null && classStructs.length>0)
        {
            GIClassStructType[] giClassStructs = new GIClassStructType[classStructs.length];
            for(int i = 0; i < classStructs.length; i++)
            {
                giClassStructs[i] = XmlProductBindingFacade.getInstance().getGIClassStructType(classStructs[i]);
            }
            pqo.getClassStructSequence().setClassStructs(giClassStructs);
        }
        return marshal(pqo);
    }
    
    static String convertToXml(ProductStruct[] productStructs)
    {
        GIProductQueryOperationsType pqo = XmlBindingFacade.getInstance().createGIProductQueryOperationsType();
        if(productStructs != null && productStructs.length > 0)
        {
            GIProductStructType[] giProductStructs = new GIProductStructType[productStructs.length];
            for(int i = 0; i < productStructs.length; i++)
            {
                giProductStructs[i] = XmlProductBindingFacade.getInstance().getGIProductStructType(productStructs[i]);
            }
            pqo.getProductStructSequence().setProductStructs(giProductStructs);            
        }
        return marshal(pqo);
    }
    
    static String convertToXml(SessionStrategyStruct[] sessionStrategyStructs)
    {
        GIProductQueryOperationsType pqo = XmlBindingFacade.getInstance().createGIProductQueryOperationsType();
        if(sessionStrategyStructs != null && sessionStrategyStructs.length > 0)
        {
            GISessionStrategyStructType[] giSessionStrategyStructs = new GISessionStrategyStructType[sessionStrategyStructs.length];
            for(int i = 0; i < sessionStrategyStructs.length; i++)
            {
                giSessionStrategyStructs[i] = XmlProductBindingFacade.getInstance().getGISessionStrategyStructType(sessionStrategyStructs[i]);
            }
            pqo.getSessionStrategyStructSequence().setSessionStrategyStructs(giSessionStrategyStructs);
        }
        return marshal(pqo);
    }
    
    static String convertToXml(StrategyStruct[] strategyStructs)
    {
        GIProductQueryOperationsType pqo = XmlBindingFacade.getInstance().createGIProductQueryOperationsType();
        if(strategyStructs != null && strategyStructs.length > 0)
        {
            GIStrategyStructType[] giStrategyStructs = new GIStrategyStructType[strategyStructs.length];
            for(int i = 0; i < strategyStructs.length; i++)
            {
                giStrategyStructs[i] = XmlProductBindingFacade.getInstance().getGIStrategyStructType(strategyStructs[i]);
            }
            pqo.getStrategyStructSequence().setStrategyStructs(giStrategyStructs);
        }
        return marshal(pqo);
    }
}
