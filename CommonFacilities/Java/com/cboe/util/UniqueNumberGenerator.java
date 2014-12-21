package com.cboe.util;

import java.util.Random;
import java.net.*;

/**
 * This class provides a system wide unique number.
 *
 * The number is made up of:
 * 1. the two low order bytes from host IP address
 * 2. the process ID of current VM
 * 3. a counter which will be initialized with a four byte random number
 * 
 * @author Werner Kubitsch
 * 
 */
public class UniqueNumberGenerator 
{
	private static UniqueNumberGenerator theGenerator;
	private long nextNumber;
	private int counter;

	// load the native library
	// on NT this is javautil.dll, 
	// on Solaris it is libjavautil.so
	//
	static 
	{
		System.loadLibrary("javautil");
	}	
	
/**
 * Private constructor for the number generator singleton,
 *
 * It gets the machines IP address and process Id and   
 * presets the sequential counter with a random number.
 * 
 */
private UniqueNumberGenerator() 
{
	byte[] 	IPAddress = new byte[4];
	int 	processID;
	
	try
	{
		IPAddress = InetAddress.getLocalHost().getAddress();
	}
	catch (UnknownHostException e)
	{
		System.out.println("getHostAddress failed\n" + e);
	}

	processID = getProcessID();
	counter = java.lang.Math.abs(new Random().nextInt());

	//System.out.println("IP is: " + IPAddress[2] + "." + IPAddress[3]);
	//System.out.println("PID: " + processID);
	//System.out.println("initial counter: " + counter + " = " + counter);

	// initialize the static high 32 bits of the unique number
	nextNumber = (long) IPAddress[2] << 56L;
	nextNumber |= (long) IPAddress[3] << 48L;
	nextNumber |= (long) processID << 32L;

	//System.out.println("nextNumber is " + nextNumber);
}
/**
 * This method returns the singleton.
 *
 * @return com.cboe.util.UniqueNumberGenerator
 */
public static UniqueNumberGenerator getNumberGenerator() 
{
	if (theGenerator == null)
	{
		theGenerator = new UniqueNumberGenerator();
	}
		
	return theGenerator;
}
/**
 * This will return the process ID of the current VM.
 *
 * @return int
 */
private native int getProcessID();
/**
 * This method returns the next unique number.
 * 
 * @return long
 */
public long nextNumber()
{
	synchronized (UniqueNumberGenerator.class)
	{
		// increment the counter and insert it into the low 32 bits.
		counter++;
		nextNumber &= 0xffffffff00000000L;
		nextNumber |= (long) counter;
	}
	
	return nextNumber;
}
}
