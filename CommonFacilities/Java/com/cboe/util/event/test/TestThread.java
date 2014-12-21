package com.cboe.util.event.test;

import com.cboe.util.event.*;

public class TestThread extends Thread {
    private String name;
    private EventChannelAdapter adapter;

    public TestThread(String name) {
        this.name = name;
        adapter = EventChannelAdapterFactory.find();
    };

    public void run() {
        System.out.println("Running " + name);
    	for (int i = 0; i < 2000; i++)
    	{
            adapter.dispatch(adapter.getChannelEvent(this, name, new Integer(i)));
//            try {
//                sleep(5);
//            } catch (Exception e) {
//
//            }
    	}
    }
}
