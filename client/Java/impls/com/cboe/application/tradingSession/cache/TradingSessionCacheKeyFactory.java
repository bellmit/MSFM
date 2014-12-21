package com.cboe.application.tradingSession.cache;

import java.util.*;

import com.cboe.application.cache.*;
import com.cboe.interfaces.application.CacheKeyGenerator;

import com.cboe.domain.util.ProductStructBuilder;

import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiSession.*;
import com.cboe.client.util.StrategyLegsWrapper;
import com.cboe.client.util.Product;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public abstract class TradingSessionCacheKeyFactory
{
    private static CacheKeyGenerator typeKey                 = null;
    private static CacheKeyGenerator classKey                = null;
    private static CacheKeyGenerator classGroupByTypeKey     = null;
    private static CacheKeyGenerator productKey              = null;
    private static CacheKeyGenerator productNameKey          = null;
    private static CacheKeyGenerator productGroupByClassKey  = null;
    private static CacheKeyGenerator strategyKey             = null;
    private static CacheKeyGenerator strategyGroupByClassKey = null;
    private static CacheKeyGenerator strategyKeyByLegs = null;

    // OSI flag - should only be set for FIX
    // (no other xml should have the property)
    private static boolean isOSI;
    private static String osiSessions = null;
    static {
        try {
            osiSessions = System.getProperty("OSIsessions");
            if ((osiSessions != null) && (osiSessions.length() > 0)) {
                isOSI = true;
            } else {
                isOSI = false;
            }
        } catch (Exception e) {
            Log.exception("OSI initialization exception", e);
            isOSI = false ;
        }
        Log.information("OSI:" + isOSI);
    }

    public static CacheKeyGenerator getPrimaryTypeKey()
    {
        if (typeKey == null)
            typeKey =  new AbstractCacheKeyGenerator()
                        {
                            public Object generateKey(Object fromObject)
                            {
                                return Integer.valueOf(((ProductTypeStruct) fromObject).type);
                            }
                            public String toString()
                            {
                                 return "PrimaryTypeKey";
                            }

                        };
        return typeKey;
    }

    public static CacheKeyGenerator getPrimaryClassKey()
    {
        if (classKey == null)
            classKey =  new AbstractCacheKeyGenerator()
                        {
                            public Object generateKey(Object fromObject)
                            {
                                return Integer.valueOf(((SessionClassStruct) fromObject).classStruct.classKey);
                            }
                            public String toString()
                            {
                                 return "PrimaryClassKey";
                            }
                        };
        return classKey;
    }

    public static CacheKeyGenerator getClassGroupByTypeKey()
    {
        if (classGroupByTypeKey == null)
            classGroupByTypeKey   = new AbstractCacheKeyGenerator()
                                    {
                                        public Object generateKey(Object fromObject)
                                        {
                                            return ((SessionClassStruct) fromObject).classStruct.classSymbol;
                                        }

                                        class ClassGroupByTypeKeyIterator extends AbstractGroupIterator
                                        {
                                            ClassGroupByTypeKeyIterator(Object o) {super(o);}
                                            public Object next()
                                            {
                                                hasNext = false;
                                                return Integer.valueOf(((SessionClassStruct) fromObject).classStruct.productType);
                                            }
                                        }

                                        public Iterator groupIterator(Object fromObject)
                                        {
                                            return new ClassGroupByTypeKeyIterator(fromObject);
                                        }
                                        public String toString()
                                        {
                                            return "ClassGroupByTypeKey";
                                        }

                                        public boolean doesGroup() {return true;}
                                    };
        return classGroupByTypeKey;
    }

    public static CacheKeyGenerator getPrimaryProductKey()
    {
        if (productKey == null)
            productKey = new AbstractCacheKeyGenerator()
                         {
                            public Object generateKey(Object fromObject)
                            {
                                return Integer.valueOf(((SessionProductStruct) fromObject).productStruct.productKeys.productKey);
                            }
                            public String toString()
                            {
                                 return "PrimaryProductKey";
                            }
                         };
        return productKey;
    }

    public static CacheKeyGenerator getProductNameKey()
    {
        if (productNameKey == null)
        {
            productNameKey = new AbstractCacheKeyGenerator()
            {
                public Object generateKey(Object fromObject)
                {
                    return Product.createProduct(((SessionProductStruct) fromObject).productStruct);
                }
                public String toString()
                {
                    return "ProductNameKey";
                }
            };
        }
        return productNameKey;
    }

    public static CacheKeyGenerator getProductGroupByClassKey()
    {
        if (productGroupByClassKey == null)
            productGroupByClassKey = new AbstractCacheKeyGenerator()
                                    {
                                        public Object generateKey(Object fromObject)
                                        {
                                            return Integer.valueOf(((SessionProductStruct) fromObject).productStruct.productKeys.productKey);
                                        }

                                        class ProductGroupByClassKeyIterator extends AbstractGroupIterator
                                        {
                                            ProductGroupByClassKeyIterator(Object o) {super(o);}
                                            public Object next()
                                            {
                                                hasNext = false;
                                                return Integer.valueOf(((SessionProductStruct) fromObject).productStruct.productKeys.classKey);
                                            }
                                        }

                                        public Iterator groupIterator(Object fromObject)
                                        {
                                            return new ProductGroupByClassKeyIterator(fromObject);
                                        }
                                        public String toString()
                                        {
                                             return "ProductGroupByClassKey";
                                        }

                                        public boolean doesGroup() {return true;}
                                    };
        return productGroupByClassKey;
    }

    public static CacheKeyGenerator getPrimaryStrategyKey()
    {
        if (strategyKey == null)
            strategyKey = new AbstractCacheKeyGenerator()
                          {
                            public Object generateKey(Object fromObject)
                            {
                                return Integer.valueOf(((SessionStrategyStruct) fromObject).sessionProductStruct.productStruct.productKeys.productKey);
                            }
                            public String toString()
                            {
                                 return "PrimaryStrategyKey";
                            }
                          };
        return strategyKey;
    }

    public static CacheKeyGenerator getStrategyGroupByClassKey()
    {
        if (strategyGroupByClassKey == null)
            strategyGroupByClassKey  = new AbstractCacheKeyGenerator()
                                            {
                                                public Object generateKey(Object fromObject)
                                                {
                                                    return Integer.valueOf(((SessionStrategyStruct) fromObject).sessionProductStruct.productStruct.productKeys.productKey);
                                                }

                                                class StrategyGroupByClassKeyIterator extends AbstractGroupIterator
                                                {
                                                    StrategyGroupByClassKeyIterator(Object o) {super(o);}
                                                    public Object next()
                                                    {
                                                        hasNext = false;
                                                        return Integer.valueOf(((SessionStrategyStruct) fromObject).sessionProductStruct.productStruct.productKeys.classKey);
                                                    }
                                                }

                                                public Iterator groupIterator(Object fromObject)
                                                {
                                                    return new StrategyGroupByClassKeyIterator(fromObject);
                                                }
                                                public String toString()
                                                {
                                                     return "StrategyGroupByClassKey";
                                                }

                                                public boolean doesGroup() {return true;}
                                            };
        return strategyGroupByClassKey;
    }

    public static CacheKeyGenerator getStrategyKeyByLegs()
    {
        if (strategyKeyByLegs == null)
        {
            strategyKeyByLegs = new AbstractCacheKeyGenerator(){
                public Object generateKey(Object fromObject){
                    try{
                        StrategyLegsWrapper myObject = new StrategyLegsWrapper(((SessionStrategyStruct)fromObject).sessionStrategyLegs);
                        return myObject;
                    }catch(NotFoundException nfe){
                        return null;
                    }catch(DataValidationException nfe){
                        return null;
                    }
                }
                public String toString()
                {
                      return "StrategyKeyByLegs";
                }
             };
         }
         return strategyKeyByLegs;
    }

}
