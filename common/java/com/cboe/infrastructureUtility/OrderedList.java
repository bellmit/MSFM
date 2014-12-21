package com.cboe.infrastructureUtility;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Collections;

public class OrderedList {
	

	private ArrayList list;	
	
	public OrderedList() {
		list = new ArrayList();	
	}

	public OrderedList(int size) {
		list = new ArrayList(size);
	}

	public OrderedList(int size, Object object) {
		list = new ArrayList();
		for (int i = 0; i < size; i++ ){
			list.add(object);
		}
	}

	public OrderedList(OrderedList inpList) {
		list = new ArrayList(inpList.list);
	}

	public boolean equals(Object object) {
		return list.equals(object);
	}


	public synchronized boolean equals(OrderedList inpList) {
		return list.equals(inpList.list);
	}

	public synchronized int hashCode() {
		return list.hashCode();
	}

	public synchronized String toString() {
		return list.toString();
	}

	public synchronized Object add(Object object) {
		int listSize = list.size();
		for(int i = 0; i < listSize; i++) {
			ObjectComparator comp = (ObjectComparator)(list.get(i));
			int result = comp.orderCompare(object);
			if(result < 0) {
				list.add(i, object);
				return object;
			}
		}	
		return list.add(object);
	}

	public synchronized Object at(int index) {
		return list.get(index);
	}

	public synchronized void clear() {
		list.clear();
	}

	public synchronized boolean contains(Object object) {
		return list.contains(object);
	}

	public synchronized Enumeration elements() {
		return Collections.enumeration(list);
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}

	public int size() {
		return list.size();
	}

	public int maxSize() {
		return Integer.MAX_VALUE;
	}

	public synchronized int remove(Object object) {
		int numRemoved = 0;
		while (list.remove(object)) {
			numRemoved ++;
		}
		return numRemoved;
	}

	public synchronized int remove(Object object, int count) {
		if (count <= 0) {
			return 0;
		}
		int numRemoved = 0;
		while (list.remove(object)) {
			numRemoved ++;
			if (numRemoved >= count) {
				break;
			}
		}
		return numRemoved;
	}

	public synchronized int indexOf(Object object) {
		return list.indexOf(object);
	}	
}
