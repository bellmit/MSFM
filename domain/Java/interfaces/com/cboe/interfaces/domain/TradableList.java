package com.cboe.interfaces.domain;

/**
 * this interface is designed to provide an interface to group a collection of tradable.
 */
public interface TradableList {

        public int size();
        public boolean isEmpty();
        public boolean contains(Tradable elem);
        public int indexOf(Tradable elem);
        public int lastIndexOf(Tradable elem);
        public Tradable[] toArray();
        public Tradable get(int index);
        public Tradable set(int index, Tradable element);
        public boolean add(Tradable tradable);
        public void add(int index, Tradable element);
        public Tradable remove(int index);
        public void clear();
        public boolean addAll(TradableList c);
        public boolean addAll(int index, TradableList c);
        public void removeRange(int fromIndex, int toIndex);
}
