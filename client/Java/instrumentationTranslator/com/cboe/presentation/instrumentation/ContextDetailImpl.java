// -----------------------------------------------------------------------------------
// Source file: ContextDetailImpl.java
//
// PACKAGE: com.cboe.presentation.instrumentation
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.instrumentation;

import java.util.*;

import com.cboe.idl.cmiConstants.ProductTypes;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;

import com.cboe.interfaces.domain.SessionKeyWrapper;
import com.cboe.interfaces.instrumentation.AssociatedContext;
import com.cboe.interfaces.instrumentation.ContextDetail;
import com.cboe.interfaces.instrumentation.MethodInstrumentor;
import com.cboe.interfaces.instrumentation.QueueInstrumentor;
import com.cboe.interfaces.instrumentation.ThreadPoolInstrumentor;
import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.presentation.product.ProductClass;
import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.interfaces.presentation.product.SessionStrategy;
import com.cboe.interfaces.presentation.product.Strategy;

import com.cboe.presentation.api.APIHome;
import com.cboe.presentation.common.businessModels.AbstractBusinessModel;
import com.cboe.presentation.common.logging.GUILoggerHome;

import com.cboe.client.xml.bind.GIClassInfoType;
import com.cboe.client.xml.bind.GIContextDetailType;
import com.cboe.domain.util.SessionKeyContainer;

public class ContextDetailImpl extends AbstractBusinessModel implements ContextDetail
{
    private String orbName;
    private String clusterName;
    private String name;
    private String fullName;
    private QueueInstrumentor[] queueInstrumentors;
    private ThreadPoolInstrumentor[] threadInstrumentors;
    private MethodInstrumentor[] methodInstrumentors;
    private AssociatedContext[] associatedContexts;
    private SessionProductClass[] sessionProductClasses;
    private ProductClass[] productClasses;
    private SessionProduct[] sessionProducts;
    private Product[] products;
    private SessionStrategy[] sessionStrategies;
    private Strategy[] strategies;
    private SessionKeyWrapper[] sessionKeys;
    private String rawXML;

    private ProductClass[] allProductClasses;
    private SessionProductClass[] allSessionProductClasses;

    protected ContextDetailImpl()
    {
        super();
        initialize();
    }

    protected ContextDetailImpl(String rawXml, String orbName, String clusterName, GIContextDetailType contextDetailType)
    {
        this();
        this.orbName = orbName;
        this.clusterName = clusterName;
        if(rawXml != null)
        {
            this.rawXML = rawXml;
        }
        initialize(contextDetailType);
        initializeAllClasses();
    }

    private void initialize()
    {
        queueInstrumentors = new QueueInstrumentor[0];
        threadInstrumentors = new ThreadPoolInstrumentor[0];
        methodInstrumentors = new MethodInstrumentor[0];
        associatedContexts = new AssociatedContext[0];
        sessionProductClasses = new SessionProductClass[0];
        productClasses = new ProductClass[0];
        sessionProducts = new SessionProduct[0];
        products = new Product[0];
        sessionStrategies = new SessionStrategy[0];
        strategies = new Strategy[0];
        sessionKeys = new SessionKeyWrapper[0];
        orbName = "";
        clusterName = "";
        name = "";
        fullName = "";
        rawXML = "";
    }
    private void initialize(GIContextDetailType contextDetailType)
    {
        this.name = contextDetailType.getName();
        this.fullName = contextDetailType.getFullName();

        try
        {
            initializeClassInfo(contextDetailType.getClassInfoSequence());
        }
        catch (SystemException e)
        {
            GUILoggerHome.find().exception(e, e.details.message);
        }
        catch (AuthorizationException e)
        {
            GUILoggerHome.find().exception(e, e.details.message);
        }
        catch (DataValidationException e)
        {
            GUILoggerHome.find().exception(e, e.details.message);
        }
        catch (NotFoundException e)
        {
            GUILoggerHome.find().exception(e, e.details.message);
        }
        catch (CommunicationException e)
        {
            GUILoggerHome.find().exception(e, e.details.message);
        }
        queueInstrumentors = InstrumentorFactory.createQueueInstrumentors(orbName, clusterName, contextDetailType);
        methodInstrumentors = MethodInstrumentorFactory.createMethodInstrumentors(orbName, clusterName, contextDetailType);
        threadInstrumentors = ThreadPoolInstrumentorFactory.createThreadPoolInstrumentors(orbName, clusterName, contextDetailType);
        associatedContexts = AssociatedContextFactory.createAssociatedContexts(orbName, clusterName, contextDetailType);
    }

    public String getOrbName()
    {
        return this.orbName;
    }

    public String getClusterName()
    {
        return this.clusterName;
    }

    /**
     * Returns name for this context detail.
     * @return name String
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Returns full name for this context detail.
     * @return full name String
     */
    public String getFullName()
    {
        return this.fullName;
    }

    /**
     * Returns queue instrumentors for this context detail.
     * @return queue instrumentors QueueInstrumentor[]
     */
    public QueueInstrumentor[] getQueueInstrumentors()
    {
        return this.queueInstrumentors;
    }

    /**
     * Returns thread pool instrumentors for this context detail.
     * @return thread pool instrumentors ThreadPoolInstrumentor[]
     */
    public ThreadPoolInstrumentor[] getThreadInstrumentors()
    {
        return this.threadInstrumentors;
    }

    /**
     * Returns method instrumentors for this context detail.
     * @return method instrumentors MethodInstrumentor[]
     */
    public MethodInstrumentor[] getMethodInstrumentors()
    {
        return this.methodInstrumentors;
    }

    /**
     * Returns an array of associated contexts for this context detail.
     * @return associated contexts AssociatedContext[]
     */
    public AssociatedContext[] getAssociatedContexts()
    {
        return this.associatedContexts;
    }

    /**
     * Returns SessionProductClasses for this context detail.
     * @return Session Product Classes SessionProductClass[]
     */
    public SessionProductClass[] getSessionProductClasses()
    {
        return this.allSessionProductClasses;
    }

    /**
     * Returns ProductClasses for this context detail.
     * @return Product Classes ProductClass[]
     */
    public ProductClass[] getProductClasses()
    {
        return this.allProductClasses;
    }

    /**
     * Returns SessionProducts for this context detail.
     * @return Session Products SessionProduct[]
     */
    public SessionProduct[] getSessionProducts()
    {
        return this.sessionProducts;
    }

    /**
     * Returns Products for this context detail.
     * @return Products Product[]
     */
    public Product[] getProducts()
    {
        return this.products;
    }

    /**
     * Returns a raw XML of this context detail.
     * @return raw XML String
     */
    public String getRawXML()
    {
        return this.rawXML;
    }

    public SessionStrategy[] getSessionStrategies()
    {
        return this.sessionStrategies;
    }

    public Strategy[] getStrategies()
    {
        return this.strategies;
    }

    protected SessionKeyWrapper[] getSessionKeys()
    {
        return sessionKeys;
    }
    protected void initializeClassInfo(GIClassInfoType[] classInfoTypes) throws SystemException, AuthorizationException, DataValidationException, NotFoundException, CommunicationException
    {
        List<SessionProductClass> sessionProductClassesList = new ArrayList<SessionProductClass>(10);
        List<ProductClass> productClassesList = new ArrayList<ProductClass>(10);
        List<SessionProduct> sessionProductsList = new ArrayList<SessionProduct>(10);
        List<Product> productsList = new ArrayList<Product>(10);
        List<SessionStrategy> sessionStrategiesList = new ArrayList<SessionStrategy>(10);
        List<Strategy> strategiesList = new ArrayList<Strategy>(10);
        List<SessionKeyContainer> sessionKeysList = new ArrayList<SessionKeyContainer>(10);
        for (int i = 0; i < classInfoTypes.length; i++)
        {
            GIClassInfoType classInfoType = classInfoTypes[i];
            boolean isStrategy = classInfoType.getProductType() == ProductTypes.STRATEGY;
            String sessionName = classInfoType.getSessionName();
            int classKey = classInfoType.getClassKey();
            int productKey = classInfoType.getProductKey();
            boolean isSessionBased = false;
            if( sessionName != null && sessionName.length()>0)
            {
                isSessionBased = true;
            }
            if(classKey != 0) // got a class key
            {
                if(isSessionBased)
                {

                    // got a session product class key
                    sessionProductClassesList.add(APIHome.findProductQueryAPI().getClassByKeyForSession(sessionName, classKey));
                    sessionKeysList.add(new SessionKeyContainer(sessionName, classKey));
                }
                else
                {
                    // got a product class key
                    productClassesList.add(APIHome.findProductQueryAPI().getProductClassByKey(classKey));
                }
            }
            else if(productKey != 0)
            {
                if(isSessionBased)
                {
                    if(isStrategy)
                    {
                        sessionStrategiesList.add(APIHome.findProductQueryAPI().getStrategyByKeyForSession(sessionName, productKey));
                    }
                    else
                    {
                        sessionProductsList.add(APIHome.findProductQueryAPI().getProductByKeyForSession(sessionName, productKey));
                    }
                }
                else
                {
                    if(isStrategy)
                    {
                        strategiesList.add(APIHome.findProductQueryAPI().getStrategyByKey(productKey));
                    }
                    else
                    {
                        productsList.add(APIHome.findProductQueryAPI().getProductByKey(productKey));
                    }
                }
            }
        }
        sessionProductClasses = sessionProductClassesList.toArray(new SessionProductClass[sessionProductClassesList.size()]);
        productClasses = productClassesList.toArray(new ProductClass[productClassesList.size()]);
        sessionProducts = sessionProductsList.toArray(new SessionProduct[sessionProductsList.size()]);
        products = productsList.toArray(new Product[productsList.size()]);
        sessionStrategies = sessionStrategiesList.toArray(new SessionStrategy[sessionStrategiesList.size()]);
        strategies = strategiesList.toArray(new Strategy[strategiesList.size()]);
        sessionKeys = sessionKeysList.toArray(new SessionKeyWrapper[sessionKeysList.size()]);
    }

    private void initializeAllClasses()
    {
        Set<ProductClass> pcSet = new HashSet<ProductClass>(100);
        Set<SessionProductClass> pscSet = new HashSet<SessionProductClass>(100);
        if( queueInstrumentors.length > 0 )
        {
            for (int i = 0; i < queueInstrumentors.length; i++)
            {
                QueueInstrumentor queueInstrumentor = queueInstrumentors[i];
                ProductClass[] productClasses = queueInstrumentor.getProductClasses();
                for (int j = 0; j < productClasses.length; j++)
                {
                    pcSet.add(productClasses[j]);
                }
                SessionProductClass[] sessionProductClasses = queueInstrumentor.getSessionProductClasses();
                for (int j = 0; j < sessionProductClasses.length; j++)
                {
                    pscSet.add(sessionProductClasses[j]);
                }
            }
        }
        if( methodInstrumentors.length > 0 )
        {
            for (int i = 0; i < methodInstrumentors.length; i++)
            {
                MethodInstrumentor methodInstrumentor = methodInstrumentors[i];
                ProductClass[] productClasses = methodInstrumentor.getProductClasses();
                for (int j = 0; j < productClasses.length; j++)
                {
                    pcSet.add(productClasses[j]);
                }
                SessionProductClass[] sessionProductClasses = methodInstrumentor.getSessionProductClasses();
                for (int j = 0; j < sessionProductClasses.length; j++)
                {
                    pscSet.add(sessionProductClasses[j]);
                }
            }
        }
        for (int i = 0; i < productClasses.length; i++)
        {
            pcSet.add(productClasses[i]);
        }
        for (int i = 0; i < sessionProductClasses.length; i++)
        {
            pscSet.add(sessionProductClasses[i]);
        }
        allProductClasses = pcSet.toArray(new ProductClass[0]);
        allSessionProductClasses = pscSet.toArray(new SessionProductClass[0]);
    }

}
