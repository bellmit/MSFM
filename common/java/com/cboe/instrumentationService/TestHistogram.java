package com.cboe.instrumentationService;

import java.text.ParseException;
import java.util.concurrent.TimeUnit;

/**
 * Small test to make sure the histogram is built correctly
 * 
 * @author ryan
 * 
 */
public class TestHistogram
{

	public static void main(String[] args) throws Exception
	{
		
		/*
		 * Setup a new stopwatch, the values are in microseconds, although we could pick anything
		 */
		Stopwatch stopwatch = new HistogramStopwatch(950, 1050, 20,
				TimeUnit.MICROSECONDS);
		
		/*
		 * marking gives the output a name. the idea here is we might eventually allow multiple
		 * captures in one file to see progression, for now it just is a label.
		 */
		stopwatch.mark("Test"); // optional, names the test

		/*
		 * do some before after stuff
		 */
		for (int i = 0; i < 1000; i++)
		{
			long before = System.nanoTime();
			Thread.sleep(1);
			long after = System.nanoTime();

			stopwatch.record(before, after, TimeUnit.NANOSECONDS);
		}
		
		// print out the format to standard out. we could add this to print to file. 
		// note the visual vm expects .histro files.
		System.out.println(stopwatch);

	}

}
