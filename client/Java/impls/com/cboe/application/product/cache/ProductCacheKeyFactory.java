package com.cboe.application.product.cache;

import java.util.*;

import com.cboe.application.cache.*;
import com.cboe.interfaces.application.CacheKeyGenerator;

import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiStrategy.*;
import com.cboe.client.util.Product;

public abstract class ProductCacheKeyFactory
{
    private static CacheKeyGenerator typeKey = null;
    private static CacheKeyGenerator classKey = null;
    private static CacheKeyGenerator classGroupByTypeKey = null;
    private static CacheKeyGenerator productKey = null;
    private static CacheKeyGenerator productNameKey = null;
    private static CacheKeyGenerator productGroupByClassKey = null;
    private static CacheKeyGenerator strategyKey = null;
    private static CacheKeyGenerator strategyGroupByClassKey = null;
    private static KeyGenStrategyStructByProductKeyGroupByLegProductKey strategyGroupByLegProductKey = null;

    public static CacheKeyGenerator getPrimaryTypeKey()
    {
        if (typeKey == null)
        {
            typeKey   = new AbstractCacheKeyGenerator()
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
        }
        return typeKey;
    }

    public static CacheKeyGenerator getPrimaryClassKey()
    {
        if (classKey == null)
        {
            classKey  = new AbstractCacheKeyGenerator()
                        {
                            public Object generateKey(Object fromObject)
                            {
                                return Integer.valueOf(((ClassStruct) fromObject).classKey);
                            }
                            public String toString()
                            {
                                return "PrimaryClassKey";
                            }
                        };
        }
        return classKey;
    }

    public static CacheKeyGenerator getClassGroupByTypeKey()
    {
        if (classGroupByTypeKey == null)
        {
            classGroupByTypeKey   = new AbstractCacheKeyGenerator()
                                    {
                                        public Object generateKey(Object fromObject)
                                        {
                                            return ((ClassStruct) fromObject).classSymbol;
                                        }

                                        class ClassGroupByTypeIterator extends AbstractGroupIterator
                                        {
                                            ClassGroupByTypeIterator(Object o) {super(o);}
                                            public Object next()
                                            {
                                                hasNext = false;
                                                return Integer.valueOf(((ClassStruct) fromObject).productType);
                                            }
                                        }

                                        public Iterator groupIterator(Object fromObject)
                                        {
                                            return new ClassGroupByTypeIterator(fromObject);
                                        }
                                        public String toString()
                                        {
                                            return "ClassGroupByTypeKey";
                                        }
                                        public boolean doesGroup() {return true;}
                                    };
        }
        return classGroupByTypeKey;
    }

    public static CacheKeyGenerator getPrimaryProductKey()
    {
        if (productKey == null)
        {
            productKey = new AbstractCacheKeyGenerator()
                        {
                            public Object generateKey(Object fromObject)
                            {
                                return Integer.valueOf(((ProductStruct) fromObject).productKeys.productKey);
                            }
                            public String toString()
                            {
                                return "PrimaryProductKey";
                            }
                        };
        }
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
                     return Product.createProduct(((ProductStruct) fromObject));
                }
                public String toString()
                {
                    return "PrimaryProductNameKey";
                }
            };
        }
        return productNameKey;
    }

    public static CacheKeyGenerator getProductGroupByClassKey()
    {
        if (productGroupByClassKey == null)
        {
            productGroupByClassKey = new AbstractCacheKeyGenerator()
                                    {
                                        public Object generateKey(Object fromObject)
                                        {
                                            return Integer.valueOf(((ProductStruct) fromObject).productKeys.productKey);
                                        }

                                        class ProductGroupByClassKeyIterator extends AbstractGroupIterator
                                        {
                                            ProductGroupByClassKeyIterator(Object o) {super(o);}
                                            public Object next()
                                            {
                                                hasNext = false;
                                                return Integer.valueOf(((ProductStruct) fromObject).productKeys.classKey);
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
        }
        return productGroupByClassKey;
    }

    public static CacheKeyGenerator getPrimaryStrategyKey()
    {
        if (strategyKey == null)
        {
            strategyKey   = new AbstractCacheKeyGenerator()
                            {
                                public Object generateKey(Object fromObject)
                                {
                                    return Integer.valueOf(((StrategyStruct) fromObject).product.productKeys.productKey);
                                }
                            public String toString()
                            {
                                return "PrimaryStrategyKey";
                            }
                            };
        }
        return strategyKey;
    }

    public static CacheKeyGenerator getStrategyGroupByClassKey()
    {
        if (strategyGroupByClassKey == null)
        {
            strategyGroupByClassKey  = new AbstractCacheKeyGenerator()
                                            {
                                                public Object generateKey(Object fromObject)
                                                {
                                                    return Integer.valueOf(((StrategyStruct) fromObject).product.productKeys.productKey);
                                                }

                                                class StrategyGroupByClassKeyIterator extends AbstractGroupIterator
                                                {
                                                    StrategyGroupByClassKeyIterator(Object o) {super(o);}
                                                    public Object next()
                                                    {
                                                        hasNext = false;
                                                        return Integer.valueOf(((StrategyStruct) fromObject).product.productKeys.classKey);
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
        }
        return strategyGroupByClassKey;
    }

    public static CacheKeyGenerator getStrategyGroupByLegProductKey()
    {
        if (strategyGroupByLegProductKey == null)
        {
            strategyGroupByLegProductKey = new KeyGenStrategyStructByProductKeyGroupByLegProductKey();
        }
        return strategyGroupByLegProductKey;
    }

}

// A little complicated to be an anonymous inner class
class KeyGenStrategyStructByProductKeyGroupByLegProductKey extends AbstractCacheKeyGenerator
{
    private static boolean hasLegs(StrategyStruct ss)
    {
        return ((ss.strategyLegs != null) && (ss.strategyLegs.length != 0));
    }

    public Object generateKey(Object fromObject)
    {
        StrategyStruct ss = (StrategyStruct) fromObject;

        if (hasLegs(ss))
        {
            return null;
        }
        else
        {
            return Integer.valueOf(ss.product.productKeys.productKey);
        }
    }

    class GroupIterator implements Iterator
    {
        private static final int NO_NEXT = -1;
        private StrategyStruct  ss      = null;
        private int             nextLeg = NO_NEXT;

        GroupIterator(Object o)
        {
            ss = (StrategyStruct) o;
            if (KeyGenStrategyStructByProductKeyGroupByLegProductKey.hasLegs(ss))
            {
                nextLeg = 0;
            }
        }

        public boolean hasNext()
        {
            return nextLeg != NO_NEXT;
        }

        public void remove()
        {
            throw new UnsupportedOperationException();
        }

        public Object next()
        {
            if (!hasNext())
            {
                throw new NoSuchElementException();
            }

            Object retObj = Integer.valueOf(ss.strategyLegs[nextLeg].product);
            nextLeg++;
            if (nextLeg == ss.strategyLegs.length)
            {
                nextLeg = NO_NEXT;
            }
            return retObj;
        }
    }

    public Iterator groupIterator(Object fromObject)
    {
        return new GroupIterator(fromObject);
    }
    public String toString()
    {
         return "KeyGenStrategyStructByProductKeyGroupByLegProductKey";
    }
    public boolean doesGroup() {return true;}
}
