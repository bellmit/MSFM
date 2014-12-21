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
class MyObserver implements Observer
{

	public MyObserver() { super(); }

	public void update(Observable o, Object arg)
	{
		// The FIX application object obtained
		// is supplied as the arg in this method.
		// The arg could be of type MessageObject,
		// which all the FIX applcation object
		// extends from, or of type OperatorData
		// which has the same data structure as
		// CSRemoteData.idl. OperatorData will
		// be published if you enable the flag
		// REMOTE_DOWN_NOTIFICATION to ON, and
		// whenever the connection is down, info
		// on the down connection will be published.

		// Please refer the IDL for specific FIX application
		// object such as Order, etc. for detail of the data 
		// structure. It is up to the application to implement
		// the specific observer behavior. The example shows 
		// you how to retrive the object.

		Object fix = arg;
		System.out.println("Receive data : " + fix.getClass().getName());

		// This is for when remote connection is down, 
		// Observer will be notified. The object is of
		// type OperatorData, which has the same data
		// structure as CSRemoteData.idl.
		if(fix instanceof OperatorData) {
			System.out.println("Receiving connection down in listener");
			System.out.println("Listener - Firm ID : " + ((OperatorData)fix).firm_id);
		}

	}
}

// Sample application.
public class ObserverBuy
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

		//  try { Thread.sleep(10000); } catch(Exception e) { e.printStackTrace(); }

		// Add the listner, i.e, register the Observer.
		MyObserver l = new MyObserver();
		try {
			System.out.println("Added listener 1");
			srv.addListener(l);
			System.out.println("Added listener Done");
		} catch(Exception e) {}

		// Just to sleep, no particular reason.
		try { Thread.sleep(5000); } catch(Exception e) { e.printStackTrace(); }

		// Just give it a room to breathe.
		// Do the connect from command prompt or GUI during
		// this period of time.
		//  	try { Thread.sleep(1000); } catch(Exception e) { e.printStackTrace(); }

		// Try sending order, for detail on
		// Order object's data structure,
		// please refer COrder.idl.
		int res = 0;
		Order o[] = new Order[1];

		for (int j = 0; j < o.length; j++) {
      o[j] = new Order();
			o[j].header = new Header();
			o[j].trailer = new Trailer();

			o[j].header.TargetCompID = "SBI"+j;
			o[j].header.SenderCompID = "SLGM"+j;
			o[j].header.MsgType = "D";
			o[j].header.SenderSubID = "GEORGE";

			o[j].Symbol = "ADVS";    //** ticker
			o[j].Side = "1";         //** 1=BUY, 2=SELL
			o[j].OrderQty = 1000;
			o[j].HandlInst = "1";    //** 1=best execution
			o[j].OrdType = "1";      //** 1=MKT, 2=LMT
		}

		// Sending Orders. Coppelia setup as CLIENT(buy side)
		for(int i=0; i<1000000; ++i) {
			for (int j = 0; j < o.length; j++) {
				o[j].ClOrdID = "" + (i+1);   //** this must be a unique identifier
				try {
					// Disable this by commenting the next line that
					// put(o) the object if Coppelia is setup as sell side.
					res = srv.post(o[j]);
					//System.out.print("#");
					//Thread.yield();
					// 	try { Thread.sleep(50); } catch(Exception e) { e.printStackTrace(); }
				}catch(Exception e) {
					try { Thread.sleep(1000); } catch(Exception f){}
					System.out.println("Sending order... exception " + e);}
			}
		}

		try { Thread.sleep(1000*1000);} catch (Exception e) {}
		// To get OperatorData
		// Operator interface. Please refer CSRemoteData.idl
		// for detail of OperatorData data structure.
		try {
			OperatorData[] d = srv.getOperatorData();
			System.out.println("size of array : " + d.length);
			System.out.println("Firm ID : " + d[0].firm_id);
			System.out.println("stats : \n" + srv.operatorCommand("stats"));
		} catch(Exception e) {}


		// Coppelia setup as SERVER(sell side)
		ExecutionReport exec1 = new ExecutionReport();
		exec1.header = new Header();
		exec1.trailer = new Trailer();
		exec1.header.TargetCompID = "SLGM";
		exec1.header.SenderCompID = "SBI";
		exec1.header.MsgType = "8";

		for(int i=0; i<10000; ++i) {
			exec1.OrderID = "Order # " + i;            //FIX 37
			exec1.ClOrdID = "" + (i+1);			   //FIX 11

			exec1.ExecID = "Exec ID " + i;             //FIX 17
			exec1.ExecTransType = "0";      //FIX 20
			exec1.ExecType = "3";
			exec1.OrdStatus = "0";          //FIX 39
			exec1.Symbol = "IBM";           //FIX 55
			exec1.Side = "2";				//FIX 54
			exec1.OrderQty = 100;
			exec1.LastShares = 100;        //FIX 32
			exec1.LastPx = 1.0;           //FIX 31
			exec1.LeavesQty = 0;
			exec1.CumQty = 100;               //FIX 14
			exec1.AvgPx = 1.0;             //FIX 6
			exec1.MiscFeeAmt = new double[0];
			exec1.MiscFeeCurr = new String[0];
			exec1.MiscFeeType = new String[0];

			try {
				// Disable this by commenting the next line that
				// put(o) the object if Coppelia is setup as buy side.
				//res = srv.put(exec1);
			} catch(Exception e) {}
		}

		// Send IOIs.
		IndicationOfInterest ioi = new IndicationOfInterest();
		ioi.header = new Header();
		ioi.trailer = new Trailer();
		ioi.header.TargetCompID = "SLGM";
		ioi.header.SenderCompID = "SBI";
		ioi.header.MsgType = "6";
		
		ioi.Symbol = "MSFT";    //** ticker
		ioi.Side = "2";         //** 1=BUY, 2=SELL
		ioi.IOIShares = "S";
		ioi.IOITransType = "N";
		ioi.NoIOIQualifiers = 0;
		ioi.IOIQualifier = new String[0];

		for(int i=0; i<100; ++i) {
			ioi.IOIID = "" + (i+1);    //** this must be a unique identifier
			try {
				// Disable this is interfacing to buy side server.
				//res = srv.put(ioi);
			} catch(Exception e) {}

		}
		
		try {
			Thread.sleep(10000);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
	}
}


