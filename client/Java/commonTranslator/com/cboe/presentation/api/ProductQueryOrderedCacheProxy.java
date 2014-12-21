//
// -----------------------------------------------------------------------------------
// Source file: ProductQueryOrderedCacheProxy.java
//
// PACKAGE: com.cboe.presentation.api;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api;

import java.util.*;

import com.cboe.idl.cmiProduct.ProductTypeStruct;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiConstants.OptionTypes;
import com.cboe.idl.cmiConstants.ListingStates;

import com.cboe.domain.util.PriceFactory;
import com.cboe.domain.util.ClassSymbolComparator;

import com.cboe.interfaces.presentation.product.ProductClass;
import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.presentation.product.Strategy;
import com.cboe.interfaces.presentation.product.ReportingClass;

import com.cboe.presentation.common.formatters.Utility;

/**
 * This class is a helper that provides limited product query interfaces to facilitate
 * caching of products, product types, and product classes. It will keep a cached
 * collection of these so that round trips to the CAS are not always required.
 * All collections used are non-synchronized. This codebase handles all synchronization
 * on a larger granular level. Care must be taken in rearranging or changing code
 * fragments, as you may disturb the synchronization.
 * @version 07/15/1999
 * @author Troy Wehrle
 */
public class ProductQueryOrderedCacheProxy
{
    private final ProductClass[] classPrototype = new ProductClass[0];
    private final Product[] productPrototype = new Product[0];
    private final ProductTypeStruct[] productTypePrototype = new ProductTypeStruct[0];
    private final Strategy[] strategyPrototype = new Strategy[0];
    private static final int DEFAULT_SIZE = 200;

    /**
     * Serves getProductTypes(boolean)
     */
    protected Map productTypesActive;
    /**
     * Serves getProductTypes(boolean)
     */
    protected Map productTypesInactive;

    /**
     * Serves getProductClassesByAlpha(String, boolean)
     * There will be an element for every letter of the alphabet and an extra for
     * non-alpha. Each SortedSet element should only contain classes who's symbols
     * all begin with the same letter, except for the extra non-alpha one.
     */
    protected SortedSet[] alphaSortedClassActive = new TreeSet[27];
    protected SortedSet[] alphaSortedClassInactive = new TreeSet[27];
    /**
     * Will contain as values one of the SortedSet's above, keyed by class key. The
     * SortedSet returned by the class key will contain in it somewhere the ProductClass.
     */
    protected Map classKeyToAlphaSortedSet;

    /**
     * Serves getProductByName(ProductNameStruct)
     */
    protected Map productNameToProduct;

    /**
     * Serves getProductName(int)
     */
    protected Map productKeyToProductName;

    /**
     * Serves getProductStrategy(int)
     */
//    protected Map productKeyToStrategy = null;

    /**
     * Serves getProducts(int, boolean, EventChannelListener)
     */
    protected Map classKeyToProductActive;
    protected Map classKeyToProductInactive;

//    protected Map productComponentToStrategies = null;

    /**
     * Serves getProductClasses(short, boolean, EventChannelListener)
     */
    protected Map productTypeToClassActive;
    protected Map productTypeToClassInactive;

    /**
     * Used for get quick access to a ProductClass that the other collections contain
     * in order to update it and thus update the other collections.
     */
    protected Map classKeyToClass;

    /**
     * Used for get quick access to a Product that the other collections contain
     * in order to update it and thus update the other collections.
     */
    protected Map productKeyToProduct;

    /**
     * Used for quick access to reporting class by hash since functionality is not in API.
     */
    protected Map reportingClassMap;
    protected Map reportingClassSymbolMap;

    /**
     * Used for quick access to products by reporting class hash since functionality is not in API.
     */
    protected Map reportingClassToActiveProduct;

    /**
     * Used for quick access to products by reporting class hash since functionality is not in API.
     */
    protected Map reportingClassToInactiveProduct;

    /**
     * Constructor
     */
    public ProductQueryOrderedCacheProxy()
    {
        super();
    }

    public synchronized void addClass(ProductClass productClass, short type)
    {
        ProductClass[] productClasses = {productClass};
        addClasses(productClasses, type);
    }

    /**
     * Adds a sequence of class structs to the cache.
     * @param productClasses sequence of ProductClasses to be added to the cache
     * @param type Product type short that all the associated class structs belong to.
     */
    public synchronized void addClasses(ProductClass[] productClasses, short type)
    {
        Short typeObject = new Short(type);

        HashMap activeClassesMap;
        HashMap inActiveClassesMap;

        //if map already exists
        if(getActiveProductTypeToClassCollection().containsKey(typeObject))
        {
            //get them, to continue adding
            activeClassesMap = (HashMap)getActiveProductTypeToClassCollection().get(typeObject);
        }
        else
        {
            //else, start a new one
            activeClassesMap = new HashMap();
        }

        //if map already exists
        if(getInactiveProductTypeToClassCollection().containsKey(typeObject))
        {
            //get them, to continue adding
            inActiveClassesMap = (HashMap)getInactiveProductTypeToClassCollection().get(typeObject);
        }
        else
        {
            //else, start a new one
            inActiveClassesMap = new HashMap();
        }

        SortedSet alphaClassesSet;
        ProductClass productClass;

        for(int i = 0; i < productClasses.length; i++)
        {
            alphaClassesSet = null;

            productClass = productClasses[i];

            if(productClass.getListingState() == ListingStates.ACTIVE)
            {
                //add to active list
                Integer classKey = new Integer(productClass.getClassKey());
                activeClassesMap.put(classKey, productClass);

                //since we have a class, add it to the active SortedSet that represents the
                //first letter of the class symbol
                alphaClassesSet = getActiveClasses(productClass.getClassSymbol().charAt(0));
                alphaClassesSet.add(productClass);
            }
            else if(productClass.getListingState() == ListingStates.INACTIVE)
            {
                //add to inactive list
                Integer classKey = new Integer(productClass.getClassKey());
                inActiveClassesMap.put(classKey, productClass);

                //since we have a class, add it to the inactive SortedSet that represents the
                //first letter of the class symbol
                alphaClassesSet = getInactiveClasses(productClass.getClassSymbol().charAt(0));
                alphaClassesSet.add(productClass);
            }

            Integer classKeyObject = new Integer(productClass.getClassKey());

            //record the SortedSet we added this to, keyed by the class. will be used later
            //to quickly look up SortedSet that references it to remove. keeps us from iterating
            //when the update later may have changed the class symbol and we cannot pinpoint
            //easily
            if(alphaClassesSet != null)
            {
                getClassKeyToAlphaSortedSetCollection().put(classKeyObject, alphaClassesSet);
            }

            //record the ProductClass in a separate collection that is keyed by class key.
            //allows us to refer to the ProductClass that all the other collections contain
            //quickly, so that we can alter something in the ProductClass and effect all other
            //collections
            getFullClassCollection().put(classKeyObject, productClass);

            ReportingClass[] reportingClasses = productClass.getReportingClasses();
            addReportingClasses(reportingClasses);
        }

        //finally add all active & inactive ProductClass's to cache, keyed by product type
        getActiveProductTypeToClassCollection().put(typeObject, activeClassesMap);
        getInactiveProductTypeToClassCollection().put(typeObject, inActiveClassesMap);
    }

    /**
     * Adds reporting classes to the cache
     * @param reportingClasses - an array of reporting classes to cache
     */
    private void addReportingClasses(ReportingClass[] reportingClasses)
    {
        for(int i = 0; i < reportingClasses.length; i++)
        {
            ReportingClass reportingClass = reportingClasses[i];
            getFullReportingClassCollection().put(reportingClass.getClassKey(), reportingClass);
            getFullReportingClassSymbolCollection().put(reportingClass.getReportingClassSymbol(), reportingClass);
        }
    }

    /**
     * Maps the ProductNameStruct to the Product for retrieval later by the
     * ProductNameStruct.
     * @param product Product to get ProductNameStruct from.
     */
    private void addNameToProductMapping(Product product)
    {
        synchronized(getProductNameToProductCollection())
        {
            //add product to collection, keyed by concantenation of all product name
            //elements. used later when we want to get product by ProductNameStruct.
            getProductNameToProductCollection().
                put(buildSeriesName(product.getProductNameStruct()), product);
        }
    }

    /**
     * Adds the passed Product to the mapping of ReportingClass to Product
     * @param product to map to
     */
    private void addReportingClassToProductMapping(Product product)
    {
        if(product != null &&
                (product.getListingState() == ListingStates.ACTIVE ||
                product.getListingState() == ListingStates.INACTIVE))
        {
            ReportingClass reportingClass = getReportingClassByKey(product.getProductKeysStruct().reportingClass);

            if(reportingClass != null)
            {
                ArrayList productsList;
                if(product.getListingState() == ListingStates.ACTIVE)
                {
                    productsList = (ArrayList)getReportingClassToActiveProductCollection().get(reportingClass);
                }
                else
                {
                    productsList = (ArrayList)getReportingClassToInactiveProductCollection().get(reportingClass);
                }

                if(productsList == null)
                {
                    productsList = new ArrayList(20);
                }

                if(!productsList.contains(product))
                {
                    productsList.add(product);
                    if(product.getListingState() == ListingStates.ACTIVE)
                    {
                        getReportingClassToActiveProductCollection().put(reportingClass, productsList);
                    }
                    else
                    {
                        getReportingClassToInactiveProductCollection().put(reportingClass, productsList);
                    }
                }
            }
        }
    }

    /**
     * Maps the product key to the ProductNameStruct
     * @param productKey To use as key for collection lookup
     * @param productName ProductNameStruct for collection to contain.
     */
    public synchronized void addProductKeyToNameMapping(int productKey, ProductNameStruct productName)
    {
        getProductKeyToProductNameCollection().put(new Integer(productKey), productName);
    }
    /**
     * Maps the product key in the Product to the ProductNameStruct
     * @param product Product to get product key and ProductNameStruct from
     */
    private void addProductKeyToNameMapping(Product product)
    {
        addProductKeyToNameMapping(product.getProductKeysStruct().productKey, product.getProductNameStruct());
    }

    public synchronized void addProduct(Product product, int classKey) {
        Product[] products = {product};
        addProducts(products, classKey);
    }

    public synchronized void addStrategy(Strategy strategy, int classKey) {
    Strategy[] strategies = {strategy};
    addStrategies(strategies, classKey);
    }

    public synchronized void addStrategies(Strategy[] strategies, int classKey) {
    addProducts(strategies, classKey);
    }

    /**
     * Adds a sequence of product structs to the cache.
     * @param products Sequence of Products to be added to the cache
     * @param classKey Classes key that these products belong to.
     */
    public synchronized void addProducts(Product[] products, int classKey)
    {
        Integer classKeyObject = new Integer(classKey);

        ArrayList productListActive;
        ArrayList productListInactive;

        //if list already existed
        if(getActiveClassKeyToProductCollection().containsKey(classKeyObject))
        {
            //get them, to continue adding
            productListActive = (ArrayList)getActiveClassKeyToProductCollection().get(classKeyObject);
        }
        else
        {
            //else, start a new one
            productListActive = new ArrayList(products.length);
        }

        //if list already existed
        if(getInactiveClassKeyToProductCollection().containsKey(classKeyObject))
        {
            //get them, to continue adding
            productListInactive = (ArrayList)getInactiveClassKeyToProductCollection().get(classKeyObject);
        }
        else
        {
            //else, start a new one
            productListInactive = new ArrayList(products.length);
        }

        //keeps us from resizing during adding.
        //since array will contain active & inactive, this number will be larger than needed,
        //but it will do.
        productListActive.ensureCapacity(productListActive.size() + products.length);
        productListInactive.ensureCapacity(productListInactive.size() + products.length);

        for(int i = 0; i < products.length; i++)
        {
            if (!getFullProductCollection().containsKey(new Integer(products[i].getProductKey()))) {

                //add to active list
                if(products[i].getListingState() == ListingStates.ACTIVE)
                    productListActive.add(products[i]);
                else if(products[i].getListingState() == ListingStates.INACTIVE)
                    productListInactive.add(products[i]);

                //map all the access ways to products
                addProductKeyToNameMapping(products[i]);
                addNameToProductMapping(products[i]);
                addReportingClassToProductMapping(products[i]);

                //record the Product in a separate collection that is keyed by product key.
                //allows us to refer to the Product that all the other collections contain
                //quickly, so that we can alter something in the Product and effect all other
                //collections
                getFullProductCollection().put(new Integer(products[i].getProductKey()), products[i]);
            }
        }

        //finally add all active & inactive Product's to cache, keyed by class key
        getActiveClassKeyToProductCollection().put(classKeyObject, productListActive);
        getInactiveClassKeyToProductCollection().put(classKeyObject, productListInactive);
    }
    /**
     * Adds a sequence of product type structs to the cache.
     * @param productTypes sequence of ProductTypeStructs to be added to the cache
     */
    public synchronized void addProductTypes(ProductTypeStruct[] productTypes)
    {
        Short key;
        Map productTypesActive = getActiveProductTypeList();

        for(int i = 0; i < productTypes.length; i++)
        {
            key = new Short(productTypes[i].type);
            //add to active list
            if (!productTypesActive.containsKey(key))
                productTypesActive.put(key, productTypes[i]);
        }
    }
    /**
     * This method will convert a ProductNameStruct to a concantenated string.
     * It will not be formatted in any special user readable way.
     * @param productNameStruct Product Name
     * @return Contents of ProductNameStruct concantenated together.
     */
    private String buildSeriesName(ProductNameStruct productNameStruct)
    {
        StringBuffer seriesName = new StringBuffer();

        seriesName.append(productNameStruct.reportingClass);
        seriesName.append(productNameStruct.expirationDate.month);
        seriesName.append(productNameStruct.expirationDate.year);
        seriesName.append(PriceFactory.create(productNameStruct.exercisePrice).toString());

        if(productNameStruct.optionType == OptionTypes.CALL)
        {
            seriesName.append("CALL");
        }
        else if(productNameStruct.optionType == OptionTypes.PUT)
        {
            seriesName.append("PUT");
        }

        seriesName.append(productNameStruct.productSymbol);

        return seriesName.toString();
    }

    /**
     * Retrieves collection of Active ProductClass's who's class symbols all begin with
     * the passed char. If the passed char is not an alpha character, then the returned
     * Set contains class symbols that begin with all non-alpha char's. You will
     * have to filter through that collection for the specific non-alpha starting symbol.
     * Performs lazy initialization of collection.
     * @return SortedSet Set of classes who's class symbol's all begin with the passed char.
     * It is sorted by the class symbol.
     */
    public synchronized SortedSet getActiveClasses(char startLetter)
    {
        int charValue = Character.getNumericValue(startLetter);

        if(Character.isLetter(startLetter))
        {
            //letter 'A' & 'a' start at char value 10
            if(alphaSortedClassActive[charValue - 10] == null)
            {
                alphaSortedClassActive[charValue - 10] = new TreeSet(new ClassSymbolComparator());
            }

            return alphaSortedClassActive[charValue - 10];
        }
        else
        {
            //element 27, zero based
            if(alphaSortedClassActive[26] == null)
            {
                alphaSortedClassActive[26] = new TreeSet(new ClassSymbolComparator());
            }

            return alphaSortedClassActive[26];
        }
    }

    /**
     * Retrieves collection of Active Product's, keyed by the class key. Performs lazy
     * initialization of collection.
     * @return Map
     */
    private Map getActiveClassKeyToProductCollection()
    {
        if(classKeyToProductActive == null)
        {
            classKeyToProductActive = new HashMap(DEFAULT_SIZE);
        }

        return classKeyToProductActive;
    }
    /**
     * Retrieves collection of Active Product Types. Performs lazy initialization of collection.
     * @return ArrayList
     */
    private Map getActiveProductTypeList()
    {
        if(productTypesActive == null)
        {
            productTypesActive = new HashMap();
        }
        return productTypesActive;
    }
    /**
     * Retrieves collection of Active ProductClass's, keyed by the product type. Performs lazy
     * initialization of collection.
     * @return Map
     */
    private Map getActiveProductTypeToClassCollection()
    {
        if(productTypeToClassActive == null)
        {
            productTypeToClassActive = new HashMap(101);
        }

        return productTypeToClassActive;
    }
    /**
     * Retrieves collection of SortedSets of ProductClass's, keyed by the class key.
     * Performs lazy initialization of collection.
     * @return Map
     */
    private Map getClassKeyToAlphaSortedSetCollection()
    {
        if(classKeyToAlphaSortedSet == null)
        {
            classKeyToAlphaSortedSet = new HashMap(101);
        }

        return classKeyToAlphaSortedSet;
    }

    /**
     * Retrieves collection of ReportingClass'es keyed by class key.
     * @return Map
     */
    private Map getFullReportingClassCollection()
    {
        if(reportingClassMap == null)
        {
            reportingClassMap = new HashMap(101);
        }

        return reportingClassMap;
    }

    /**
     * Retrieves collection of ReportingClass'es keyed by symbol.
     * @return Map
     */
    private Map getFullReportingClassSymbolCollection()
    {
        if(reportingClassSymbolMap == null)
        {
            reportingClassSymbolMap = new HashMap(101);
        }

        return reportingClassSymbolMap;
    }

    /**
     * Retrieves collection of active Product's by ReportingClass'es
     * @return Map
     */
    private Map getReportingClassToActiveProductCollection()
    {
        if(reportingClassToActiveProduct == null)
        {
            reportingClassToActiveProduct = new HashMap(DEFAULT_SIZE);
        }

        return reportingClassToActiveProduct;
    }

    /**
     * Retrieves collection of inactive Product's by ReportingClass'es
     * @return Map
     */
    private Map getReportingClassToInactiveProductCollection()
    {
        if(reportingClassToInactiveProduct == null)
        {
            reportingClassToInactiveProduct = new HashMap(101);
        }

        return reportingClassToInactiveProduct;
    }

    /**
     * Retrieves collection of ProductClass's keyed by class key.
     * @return Map
     */
    private Map getFullClassCollection()
    {
        if(classKeyToClass == null)
        {
            classKeyToClass = new HashMap(101);
        }

        return classKeyToClass;
    }

    /**
     * Retrieves collection of Product's keyed by product key.
     * @return Map
     */
    private Map getFullProductCollection()
    {
        if(productKeyToProduct == null)
        {
            productKeyToProduct = new HashMap(DEFAULT_SIZE);
        }

        return productKeyToProduct;
    }

    /**
     * Retrieves collection of Inactive ProductClass's who's class symbols all begin with
     * the passed char. If the passed char is not an alpha character, then the return value
     * collection contains class symbols that begin with all non-alpha char's. You will
     * have to filter through that collection for the specific non-alpha starting symbol.
     * Performs lazy initialization of collection.
     * @return SortedSet Set of classes who's class symbol's all begin with the passed char.
     * It is sorted by the class symbol.
     */
    public synchronized SortedSet getInactiveClasses(char startLetter)
    {
        int charValue = Character.getNumericValue(startLetter);

        if(Character.isLetter(startLetter))
        {
            //letter 'A' & 'a' start at char value 10
            if(alphaSortedClassInactive[charValue - 10] == null)
            {
                alphaSortedClassInactive[charValue - 10] = new TreeSet(new ClassSymbolComparator());
            }

            return alphaSortedClassInactive[charValue - 10];
        }
        else
        {
            //element 27, zero based
            if(alphaSortedClassInactive[26] == null)
            {
                alphaSortedClassInactive[26] = new TreeSet(new ClassSymbolComparator());
            }

            return alphaSortedClassInactive[26];
        }
    }
    /**
     * Retrieves collection of Inactive Product's, keyed by the class key. Performs lazy
     * initialization of collection.
     * @return Map
     */
    private Map getInactiveClassKeyToProductCollection()
    {
        if(classKeyToProductInactive == null)
        {
            classKeyToProductInactive = new HashMap(101);
        }

        return classKeyToProductInactive;
    }

    /**
     * Retrieves collection of Inactive ProductClass's, keyed by the product type. Performs lazy
     * initialization of collection.
     * @return Map
     */
    private Map getInactiveProductTypeToClassCollection()
    {
        if(productTypeToClassInactive == null)
        {
            productTypeToClassInactive = new HashMap(101);
        }

        return productTypeToClassInactive;
    }
    /**
     * Get a product struct by its product key.
     * @param productKey the products key
     * @return the requested product struct or null if not found
     */
    public synchronized Product getProductByKey(int productKey)
    {
        //must lock between contains check and get
        synchronized(getFullProductCollection())
        {
            return (Product)getFullProductCollection().get(new Integer(productKey));
        }
    }

    public synchronized Strategy getStrategyByKey(int productKey)
    {
        synchronized(getFullProductCollection())
        {
            return (Strategy)getFullProductCollection().get(new Integer(productKey));
        }
    }
    /**
     * Get full product information by name.
     * @param productName ProductNameStruct used to find associated product. All the
     * contents of this name struct are used to do a lookup.
     * @return The Product for the given ProductNameStruct.
     */
    public synchronized Product getProductByName(ProductNameStruct productName)
    {
        //must lock between contains check and get
        synchronized(getProductNameToProductCollection())
        {
            //keyed by concantenation of all product name elements.
            if(getProductNameToProductCollection().containsKey(buildSeriesName(productName)))
            {
                return (Product)getProductNameToProductCollection().get(buildSeriesName(productName));
            }
        }

        return null;
    }

    public synchronized Strategy getStrategyByName(ProductNameStruct productName)
    {
        //must lock between contains check and get
        synchronized(getProductNameToProductCollection())
        {
            //keyed by concantenation of all product name elements.
            if(getProductNameToProductCollection().containsKey(buildSeriesName(productName)))
            {
                return (Strategy)getProductNameToProductCollection().get(buildSeriesName(productName));
            }
        }

        return null;
    }

    /**
     * Gets the reporting class with the passed key
     * @param reportingClassKey to get reporting class for
     * @return reporting class with matching key
     */
    public synchronized ReportingClass getReportingClassByKey(int reportingClassKey)
    {
        return (ReportingClass)getFullReportingClassCollection().get(new Integer(reportingClassKey));
    }
    
    public synchronized ReportingClass getReportingClassBySymbol(String symbol)
    {
        return (ReportingClass)getFullReportingClassSymbolCollection().get(symbol);
    }

    public synchronized ProductClass getProductClassByKey(int classKey)
    {
        Integer classKeyObject = new Integer(classKey);
        ProductClass classStruct = (ProductClass) getFullClassCollection().get(classKeyObject);
        return classStruct;
    }

    /**
     * Retrieve the product classes for a product type and class name.
     * @param productType Product type of class to be returned.
     * @param className Product class name of class to be returned.
     * @return class for a product type.
     */
    public synchronized ProductClass getClassBySymbol(short productType, String className)
    {
        ProductClass[] classesForType = getProductClasses(productType, true);

        if ( classesForType.length == 0 )
        {
            return null;
        }
        else
        {
            for (int i = 0; i < classesForType.length; i++ )
            {
                if ( classesForType[i].getClassSymbol().equals(className) )
                {
                    return classesForType[i];
                }
            }

            return null;
        }
    }
    /**
     * Retrieve the product classes for a product type.
     * @param productType Product type of classes to be returned.
     * @param activeOnly set to true to receive only active classes.
     * @return A sequence of classes for a product type.
     */
    public synchronized ProductClass[] getProductClasses(short productType, boolean activeOnly)
    {
        Short productTypeObject = new Short(productType);

        //only want active
        if(activeOnly)
        {
            //do we have active classes for this product type
            if(!getActiveProductTypeToClassCollection().containsKey(productTypeObject))
            {
                //no
                return classPrototype;
            }
            else
            {
                //then just return them from cache
                HashMap activeClassesMap = (HashMap)(getActiveProductTypeToClassCollection().get(productTypeObject));
                Collection values = activeClassesMap.values();
                return (ProductClass[])values.toArray(classPrototype);
            }
        }
        else
        {
            //want active & inactive
            //do we have active or inactive classes for this product type
            if(!getActiveProductTypeToClassCollection().containsKey(productTypeObject) &&
                 !getInactiveProductTypeToClassCollection().containsKey(productTypeObject))
            {
                //no
                return classPrototype;
            }
            else
            {
                //then just return all of them from the caches.
                //must build an ArrayList as a union combining other two Map's,
                //then perform a toArray on new combined ArrayList.
                ArrayList union = new ArrayList();

                HashMap activeClassesMap;
                HashMap inActiveClassesMap;

                if(getActiveProductTypeToClassCollection().containsKey(productTypeObject))
                {
                    activeClassesMap = (HashMap)getActiveProductTypeToClassCollection().get(productTypeObject);
                    Collection values = activeClassesMap.values();
                    union.addAll(values);
                }

                if(getInactiveProductTypeToClassCollection().containsKey(productTypeObject))
                {
                    inActiveClassesMap = (HashMap)getInactiveProductTypeToClassCollection().get(productTypeObject);
                    Collection values = inActiveClassesMap.values();
                    union.addAll(values);
                }

                return (ProductClass[])union.toArray(classPrototype);
            }
        }
    }
    /**
     * Gets all product classes whose reporting class symbols start with
     * the given String prefix.
     * @param prefix The selection criteria all classes must start with to be included. The
     * best performance will be obtained when the prefix only contains one character.
     * @return A sequence of ProductClasss that met the selection criteria.
     */
    public synchronized ProductClass[] getProductClassesByAlpha(String prefix)
    {
        if(prefix != null && prefix.length() > 0)
        {
            SortedSet classSet;
            ProductClass[] classArray;

            //get classes, active or inactive
            classSet = getActiveClasses(prefix.charAt(0));

            if(prefix.length() == 1)
            {
                //since prefix only one character, can just return set contents, they all match
                classArray = (ProductClass[])classSet.toArray(classPrototype);
            }
            else
            {
                //caller wants a set that matches with more than 1 character
                ArrayList classList = new ArrayList(classSet.size());
                ProductClass classStruct;
                boolean foundFirst = false;

                //must iterate through collection to find all pattern matches
                for(Iterator i = classSet.iterator(); i.hasNext();)
                {
                    classStruct = (ProductClass)i.next();

                    if(classStruct.getClassSymbol().toUpperCase().startsWith(prefix.toUpperCase()))
                    {
                        //and if match add them to temp ArrayList
                        classList.add(classStruct);
                        //since the set is in alphabetical order and we found first one
                        foundFirst = true;
                    }
                    else
                    {
                        //we can stop checking after we found the last one.
                        if(foundFirst)
                        {
                            break;
                        }
                    }
                }

                classArray = (ProductClass[])classList.toArray(classPrototype);
            }

            return classArray;
        }
        else
        {
            //no prefix sent at all
            return new ProductClass[0];
        }
    }
    /**
     * Retrieves collection of ProductNameStruct's, keyed by the product key. Performs lazy
     * initialization of collection.
     * @return Map
     */
    private Map getProductKeyToProductNameCollection()
    {
        if(productKeyToProductName == null)
        {
            productKeyToProductName = new HashMap(DEFAULT_SIZE);
        }

        return productKeyToProductName;
    }
    /**
     * Get full product name information by product key.
     * @param productKey the product key.
     * @return The ProductNameStruct for the given product key.
     */
    public synchronized ProductNameStruct getProductNameStruct(int productKey)
    {
        Integer productKeyObject = new Integer(productKey);

        if(getProductKeyToProductNameCollection().containsKey(productKeyObject))
        {
            return (ProductNameStruct)getProductKeyToProductNameCollection().get(productKeyObject);
        }
        else
        {
            //did not already have it in cache
            return null;
        }
    }
    /**
     * Retrieves collection of Products, keyed by the Product Name. Performs lazy
     * initialization of collection.
     * @return Map
     */
    private Map getProductNameToProductCollection()
    {
        if(productNameToProduct == null)
        {
            productNameToProduct = new HashMap(DEFAULT_SIZE);
        }

        return productNameToProduct;
    }

    /**
     * Gets all the Products for the passed ReportingClass
     * @param reportingClass to get products for
     * @param activeOnly True to get only active classes, false to get active and inactive.
     * @return only Products that have the passed ReportingClass
     */
    public synchronized Product[] getProductsForReportingClass(ReportingClass reportingClass, boolean activeOnly)
    {
        Product[] products = productPrototype;

        if(activeOnly)
        {
            if(getReportingClassToActiveProductCollection().containsKey(reportingClass))
            {
                ArrayList list = (ArrayList)getReportingClassToActiveProductCollection().get(reportingClass);
                products = (Product[])list.toArray(productPrototype);
            }
        }
        else
        {
            ArrayList union = new ArrayList(200);
            ArrayList list;

            if(getReportingClassToActiveProductCollection().containsKey(reportingClass))
            {
                list = (ArrayList)getReportingClassToActiveProductCollection().get(reportingClass);
                union.addAll(list);
            }
            if(getReportingClassToInactiveProductCollection().containsKey(reportingClass))
            {
                list = (ArrayList)getReportingClassToInactiveProductCollection().get(reportingClass);
                union.addAll(list);
            }
            products = (Product[])union.toArray(productPrototype);
        }
        return products;
    }

    /**
     * Retrieve all product information for the given class identifier of the
     * given type.
     * @param classKey product class of products to be returned.
     * @param activeOnly true to retrieve active products only.
     * @return A sequence of products.
     */
    public synchronized Product[] getProducts(int classKey, boolean activeOnly)
    {
        Product[] prototype = null;
        boolean isStrategy = false;
        if (Utility.isStrategy(classKey)) {
            isStrategy = true;
        }
        if (isStrategy) {
            prototype = strategyPrototype;
        } else {
            prototype = productPrototype;
        }
        Integer classKeyObject = new Integer(classKey);

        //only want active
        if(activeOnly)
        {
            //do we have active products for this class key
            if(!getActiveClassKeyToProductCollection().containsKey(classKeyObject))
            {
                //no
                return prototype;
            }
            else
            {
                //then just return them from cache
                ArrayList activeProductList = (ArrayList)(getActiveClassKeyToProductCollection().get(classKeyObject));
                return (Product[])activeProductList.toArray(prototype);
            }
        }
        else
        {
            //want active & inactive
            //do we have active or inactive products for this class key
            if(!getActiveClassKeyToProductCollection().containsKey(classKeyObject) &&
                 !getInactiveClassKeyToProductCollection().containsKey(classKeyObject))
            {
                //no
                return prototype;
            }
            else
            {
                //then just return all of them from the caches.
                //must build an ArrayList as a union combining other two ArrayList's,
                //then perform a toArray on new combined ArrayList.
                ArrayList union = new ArrayList();
                ArrayList productList;

                if(getActiveClassKeyToProductCollection().containsKey(classKeyObject))
                {
                    productList = (ArrayList)getActiveClassKeyToProductCollection().get(classKeyObject);
                    union.addAll(productList);
                }

                if(getInactiveClassKeyToProductCollection().containsKey(classKeyObject))
                {
                    productList = (ArrayList)getInactiveClassKeyToProductCollection().get(classKeyObject);
                    union.addAll(productList);
                }
                if (isStrategy) {
                    return (Strategy[])union.toArray(prototype);
                } else {
                    return (Product[])union.toArray(prototype);
                }
            }
        }
    }

    public synchronized Strategy[] getStrategies(int classKey, boolean activeOnly)
    {
        return (Strategy[]) getProducts(classKey, activeOnly);
    }
    /**
     * Retrieve the product type definitions.  You can choose to retrieve
     * only product types that are active for trading.
     * @return a sequence of one or more product types.
     */
    public synchronized ProductTypeStruct[] getProductTypes()
    {
        Map productTypeList = getActiveProductTypeList();

        if(productTypeList.size() < 1)
        {
            return productTypePrototype;
        }
        else
        {
            return (ProductTypeStruct[])productTypeList.values().toArray(productTypePrototype);
        }
    }

    /**
     * Callback method that updates the class with the matching class key in all data
     * structures. Removes the existing ProductClass, based on class key from all
     * collections, then calls add to add this new one.
     * @param productClass ProductClass to update
     */
    public synchronized void updateClass(ProductClass productClass)
    {
        Short productTypeObject = new Short(productClass.getProductType());

        HashMap activeClassesMap;
        HashMap inActiveClassesMap;

        //find active Map for this classes product type
        if(getActiveProductTypeToClassCollection().containsKey(productTypeObject))
        {
            activeClassesMap = (HashMap)getActiveProductTypeToClassCollection().get(productTypeObject);

            if(activeClassesMap != null)
            {
                Iterator it = activeClassesMap.values().iterator();
                while ( it.hasNext() )
                {
                    if(((ProductClass)it.next()).getClassKey() == productClass.getClassKey())
                    {
                        it.remove();
                        break;
                    }
                }
            }
        }

        //find inactive Map for this classes product type
        if(getInactiveProductTypeToClassCollection().containsKey(productTypeObject))
        {
            inActiveClassesMap = (HashMap)getInactiveProductTypeToClassCollection().get(productTypeObject);

            if(inActiveClassesMap != null)
            {
                Iterator it = inActiveClassesMap.values().iterator();
                while ( it.hasNext() )
                {
                    if(((ProductClass)it.next()).getClassKey() == productClass.getClassKey())
                    {
                        it.remove();
                        break;
                    }
                }
            }
        }


        //get SortedSet that references this class key
        SortedSet classSet;

        //existing ProductClass for this class key is in this SortedSet among others.
        //this SortedSet may be referenced by many class keys.
        classSet = (SortedSet)getClassKeyToAlphaSortedSetCollection().get(new Integer(productClass.getClassKey()));

        if(classSet != null)
        {
            for(Iterator i = classSet.iterator(); i.hasNext();)
            {
                //iterate until class key matches in this set and remove
                if(((ProductClass)i.next()).getClassKey() == productClass.getClassKey())
                {
                    i.remove();
                    break;
                }
            }
        }

        //add this new/updated in the collections.
        ProductClass[] classes = new ProductClass[1];
        classes[0] = productClass;

        addClasses(classes, productClass.getProductType());
    }

    /**
     * Callback method that updates the product with the matching product key in all data
     * structures. Removes the existing Product, based on product key from all
     * collections, then calls add to add this new one.
     * @param updatedProduct the product struct to update the cache.
     */
    public synchronized void updateProduct(Product updatedProduct)
    {
        Integer classKeyObject = new Integer(updatedProduct.getProductKeysStruct().classKey);

        ArrayList productListActive;
        ArrayList productListInactive;

        //find active ArrayList for this products class key
        if(getActiveClassKeyToProductCollection().containsKey(classKeyObject))
        {
            productListActive = (ArrayList)getActiveClassKeyToProductCollection().get(classKeyObject);

            if(productListActive != null)
            {
                for(Iterator i = productListActive.iterator(); i.hasNext();)
                {
                    //iterate through all active products for this class key
                    //and remove matching product
                    if(((Product)i.next()).getProductKey() == updatedProduct.getProductKey())
                    {
                        i.remove();
                        break;
                    }
                }
            }
        }

        //find inactive ArrayList for this products class key
        if(getInactiveClassKeyToProductCollection().containsKey(classKeyObject))
        {
            productListInactive = (ArrayList)getInactiveClassKeyToProductCollection().get(classKeyObject);

            if(productListInactive != null)
            {
                for(Iterator i = productListInactive.iterator(); i.hasNext();)
                {
                    //iterate through all inactive products for this class key
                    //and remove matching product
                    if(((Product)i.next()).getProductKey() == updatedProduct.getProductKey())
                    {
                        i.remove();
                        break;
                    }
                }
            }
        }


        Integer productKeyObject = new Integer(updatedProduct.getProductKey());

        //remove from other cross references.
        getProductKeyToProductNameCollection().remove(productKeyObject);
  //      getProductKeyToStrategyCollection().remove(productKeyObject);

        //must iterate through this whole collection indexed by ProductNameStruct contents
        //concantenated. then when matched, remove.
        synchronized(getProductNameToProductCollection())
        {
            for(Iterator i = getProductNameToProductCollection().values().iterator(); i.hasNext();)
            {
                Product product = (Product)i.next();

                if(product.getProductKey() == updatedProduct.getProductKey())
                {
                    i.remove();
                    break;
                }
            }
        }

        getFullProductCollection().remove(productKeyObject);

        //add this new/updated in the collections
        Product[] products = {updatedProduct};
        addProducts(products, products[0].getProductKeysStruct().classKey);
    }

    public synchronized void updateStrategy(Strategy updatedStrategy)
    {
        updateProduct(updatedStrategy);
    }

}
