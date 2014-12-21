package com.cboe.domain.util;

import java.util.ArrayList;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

abstract public class TLSObjectPool<K> implements StructFactoryIF
{
    public static class TLSElement<K>
    {
        private ArrayList<K> tlsArray = new ArrayList<K>();
        private int offset = 0;
        private boolean addToStructFactory = false;
        private boolean arraySizeChanged = false;
        private int maxCount = -1;

        public int getMaxCount()
        {
            return maxCount;
        }

        public void setMaxCount(int p_maxCount)
        {
            maxCount = p_maxCount;
        }

        public ArrayList<K> getTlsArray()
        {
            return tlsArray;
        }

        public void setTlsArray(ArrayList<K> p_tlsArray)
        {
            tlsArray = p_tlsArray;
        }

        public int getOffset()
        {
            return offset;
        }

        public void setOffset(int p_offset)
        {
            offset = p_offset;
        }

        public boolean isAddToStructFactory()
        {
            return addToStructFactory;
        }

        public void setAddToStructFactory(boolean p_addToStructFactory)
        {
            addToStructFactory = p_addToStructFactory;
        }

        public boolean isArraySizeChanged()
        {
            return arraySizeChanged;
        }

        public void setArraySizeChanged(boolean p_arraySizeChanged)
        {
            arraySizeChanged = p_arraySizeChanged;
        }
    }

    private ThreadLocal<TLSElement<K>> tlsElement = new ThreadLocal<TLSElement<K>>()
    {
        protected TLSElement<K> initialValue()
        {
            return new TLSElement<K>();
        }
    };

    /*
     * Create a new struct
     */
    public abstract K createNewInstance();

    /*
     * Clear fields in the struct
     */
    public abstract void clear(K value);

    private TLSElement<K> getElement()
    {
        return tlsElement.get();
    }

    private static final int UPPER_BOUND = 100;

    private int getMaxElements(TLSElement<K> element, K p_instance)
    {
        int rval = element.getMaxCount();

        if (rval <= 0)
        {
            String defaultPropertyName = "TLSObjectPool.default";
            String classProperty = "TLSObjectPool" + "." + p_instance.getClass().getName();

            String value = System.getProperty(classProperty, System.getProperty(
                    defaultPropertyName, String.valueOf(UPPER_BOUND)));
            int propertyValue = 0;

            try
            {
                propertyValue = Integer.parseInt(value);
            }
            catch (Exception e)
            {
                propertyValue = UPPER_BOUND;
            }

            if (propertyValue <= 0)
            {
                propertyValue = UPPER_BOUND;
            }

            rval = propertyValue;

            element.setMaxCount(rval);

            Log.information(Thread.currentThread().getName() + "/" + Thread.currentThread().getId()
                    + ": TLSObjectPool maxCount for class " + p_instance.getClass().getName()
                    + " set to " + rval);
        }

        return rval;
    }

    final public K acquire()
    {
        TLSElement<K> element = getElement();
        int offset = element.getOffset();
        ArrayList<K> array = element.getTlsArray();
        int arraySize = array.size();
        K rval = null;

        addToFactoryList(element);

        if (offset < arraySize)
        {
            rval = array.get(offset);
        }
        else
        {
            rval = createNewInstance();

            if (arraySize < getMaxElements(element, rval))
            {
                element.setArraySizeChanged(true);

                array.add(rval);
            }
        }

        element.setOffset(offset + 1);

        return rval;
    }

    private void addToFactoryList(TLSElement<K> p_element)
    {
        if (p_element.isAddToStructFactory() == false)
        {
            p_element.setAddToStructFactory(true);
            com.cboe.domain.util.StructFactoryList.addToFactoryList(this);
        }
    }

    final public void releaseAll()
    {
        TLSElement<K> element = getElement();

        try
        {
            int offset = element.getOffset();
            ArrayList<K> array = element.getTlsArray();
            int arraySize = array.size();

            element.setOffset(0);

            if (Log.isDebugOn())
            {
                if ((offset > element.getMaxCount()) && (offset > 0))
                {
                    Log.information(Thread.currentThread().getName() + "/"
                            + Thread.currentThread().getId()
                            + " TLSObjectPool - Number of elements used in current txn = " + offset
                            + " exceeded max #elems = " + arraySize + " for class: "
                            + array.get(0).getClass().getName() + " increased to " + array.size());
                }
            }

            if (element.isArraySizeChanged() && (arraySize > 0))
            {
                Log.information(Thread.currentThread().getName() + "/"
                        + Thread.currentThread().getId()
                        + ": TLSObjectPool - size of thread local array for class: "
                        + array.get(0).getClass().getName() + " increased to " + array.size());

                element.setArraySizeChanged(false);
            }

            for (K value : array)
            {
                clear(value);
            }
        }
        finally
        {
            element.setAddToStructFactory(false);
        }
    }
}
