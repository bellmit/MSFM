package com.cboe.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A filtered interator is an abstract class to decorate an existing Iterator.
 *
 * @author John Wickberg
 */
public abstract class FilteredIterator implements Iterator {
    
    /**
     * Base iterator being decorated with a filter.
     */ 
    private Iterator baseIterator;
    
    /**
     * Next result to be returned by this iterator.
     */ 
    private Object nextResult;
    
    /**
     * Need to have base iterator for this filtered iterator.
     */ 
    public FilteredIterator(Iterator baseIterator) {
        this.baseIterator = baseIterator;
    }
    
    /**
     * Filter method to be implemented by implementing classes.
     * 
     * @param nextValue next value from base iterator that needs to be tested
     * @return true if value should be included
     */
    public abstract boolean filter(Object nextValue);

    /**
     * Determines if iterator has more data.
     */ 
    public boolean hasNext() {
        if (nextResult == null) {
            while (baseIterator.hasNext()) {
                Object nextValue = baseIterator.next();
                if (filter(nextValue)) {
                    nextResult = nextValue;
                    break;
                }
            }
        }
        return nextResult != null;
    }

    /**
     * Gets next element from iterator.
     */ 
    public Object next() {
        if (hasNext()) {
            Object result = nextResult;
            nextResult = null;
            return result;
        }
        else {
            throw new NoSuchElementException("Attempted to go beyond end of iterator");
        }
    }

    /**
     * Passes remove to base iterator.
     */ 
    public void remove() {
        baseIterator.remove();
    }

}
