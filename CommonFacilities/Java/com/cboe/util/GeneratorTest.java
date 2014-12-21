package com.cboe.util;

/**
 * This type was created in VisualAge.
 */
public class GeneratorTest {
/**
 * GeneratorTest constructor comment.
 */
public GeneratorTest() {
	super();
}
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) 
{
	UniqueNumberGenerator gen = UniqueNumberGenerator.getNumberGenerator();
	System.out.println("next number is " + (gen.nextNumber()));
	System.out.println("next number is " + (gen.nextNumber()));
}
}
