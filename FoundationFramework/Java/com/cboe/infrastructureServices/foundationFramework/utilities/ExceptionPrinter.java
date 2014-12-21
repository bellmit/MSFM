package com.cboe.infrastructureServices.foundationFramework.utilities;

public class ExceptionPrinter {

	public static void print(Exception e) {
		System.out.println(e.toString());
		e.printStackTrace();
	}
}