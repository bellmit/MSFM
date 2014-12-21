package com.cboe.domain.util;

import java.util.ArrayList;

import com.cboe.domain.util.intMaps.IntHashMap;

abstract public class TLSStructArray<K> implements StructFactoryIF
{
    private IntHashMap<ArrayList<K[]>> hashmap = new IntHashMap<ArrayList<K[]>>();
    private ArrayList<K[]> list = new ArrayList<K[]>();
    private boolean addToStructFactoryList = false;

    public K[] acquire(int len)
    {
        addToStructFactoryList();

        ArrayList<K[]> arrayList = getArray(len);

        K[] rval = getElement(arrayList, len);

        list.add(rval);

        return rval;
    }

    private K[] getElement(ArrayList<K[]> arrayList, int len)
    {
        int size = arrayList.size();

        return (size > 0)
                ? arrayList.remove(size - 1)
                : createArray(len);
    }

    private ArrayList<K[]> getArray(int len)
    {
        ArrayList<K[]> arrayList = hashmap.get(len);

        if (arrayList == null)
        {
            arrayList = new ArrayList<K[]>();

            hashmap.put(len, arrayList);
        }

        return arrayList;
    }

    protected abstract K[] createArray(int p_len);

    private void addToStructFactoryList()
    {
        if (!addToStructFactoryList)
        {
            StructFactoryList.addToFactoryList(this);
            addToStructFactoryList = true;
        }
    }

    public void releaseAll()
    {
        addToStructFactoryList = false;

        for (K[] array : list)
        {
            for (int i = 0; i < array.length; i++)
            {
                array[i] = null;
            }

            hashmap.get(array.length).add(array);
        }

        list.clear();
    }
}
