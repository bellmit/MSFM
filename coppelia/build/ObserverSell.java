/**
 * Copyright 1996-1999 Javelin Technologies, Inc..
 * All rights reserved.
 *
 * Example of Observer/Observable interface usage
 * in Coppelia. Send Order, IOIs, and ExecutionReport.
 */

import com.javtech.coppelia.*;
import com.javtech.coppelia.interfaces.*;
import java.util.*;

// This is a sample Observer implementation.
class SellObserver implements Observer
{

	public SellObserver() { super(); }

	long startTime = 0;
	int subTotal = 0;
	int orderCount = 0;
	int totalOrders =0;
	public void update(Observable o, Object arg)
	{
    System.out.println("REcv Order");
		if (totalOrders % 100 == 0) {
			if (startTime == 0) {
				startTime = System.currentTimeMillis();
				subTotal = totalOrders;
			}

			long cTime =  System.currentTimeMillis();
			long duration = cTime - startTime;

			int count = totalOrders - subTotal;

			int tps = ((int)(count / ((float)duration / 1000.0) ));
			startTime = cTime;
			subTotal = totalOrders;


			System.out.println("Orders received from orders total = "+totalOrders+" tps "+tps);
		}
		totalOrders++;
		Order co = null;
		try {
			co = (Order) arg;
		}
		catch (Exception e){System.out.println("bad cast "+e);}
		/*

		int count = (int) co.Commission;

		if (orderCount == 0) {
		System.out.println("Setting inital order count to "+count);
		orderCount = count;
		}
		else {
		orderCount ++;
		}
		System.out.println("<"+count+":"+orderCount+">");
		if (orderCount < count){
		System.out.println("\n\n\n\n\n\nLost order "+orderCount+" got "+count+"\n\n\n\n\n");
		orderCount = count;
		}
		*/
	}
}

// Sample application.
public class ObserverSell
{
	public static void main(String[] args)
	{
		System.out.println("Usage: java  ObserverExample <buy.dat>");

		// Construct the handle to CoppeliaSrv interface.
		// All the communication between the application and
		// Coppelia is done thru' this interface.
		// The factory will start the Coppelia process for
		// the first time if it hasn't started yet, subsequent
		// invocation to create will just return an existing
		// handle.
		CoppeliaSrv srv = CoppeliaSrvFactory.getInProcObject(args);
		System.out.println("Started Server");

		System.out.println("Added listener");

		// Add the listner, i.e, register the Observer.
		SellObserver l = new SellObserver();
		try {
			srv.addListener(l);
		} catch(Exception e) {System.out.println("Added listener Exception "+e);}

		while(true) {
			try {
				Thread.sleep(10000);
			}
			catch(Exception e) {
				e.printStackTrace();
			}

		}
	}
}


