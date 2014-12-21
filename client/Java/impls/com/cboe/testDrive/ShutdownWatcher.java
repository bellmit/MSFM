package com.cboe.testDrive;

import com.cboe.testDrive.*;
import java.io.*;
import java.util.*;

public class ShutdownWatcher extends Thread
{

    private TestParameter _myTestParm = null;
    /*
    */
    public ShutdownWatcher (TestParameter myTestParm) 
    {
	this._myTestParm = myTestParm;
    } 

    public void run()
    {
		boolean looping = true;
		while (looping) 
		{
            try {
                Thread.currentThread().sleep(10000);
            } catch (Exception e)
            {
            }

	       try  {

	        File outputFile = new File("Shutdown");
                FileReader out = new FileReader(outputFile);
                out.close();
                System.out.println ("Found command file Shutdown, shutting down the test agent");
	            _myTestParm.getInstance().threadDone = true;
				looping = false;
            }
	        catch (FileNotFoundException e)
	        {
//		System.out.println("Shutdown file not Found");
            }
	        catch (IOException e)
	        {
                e.printStackTrace();
	        }
		}
     }
}
