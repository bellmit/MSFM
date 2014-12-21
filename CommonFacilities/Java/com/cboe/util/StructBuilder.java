package com.cboe.util;

import java.lang.reflect.*;
import java.io.*;
import junit.framework.*;

public class StructBuilder
{
	/**
	 * Inner class for support of automated unit testing.
	 */
	static public class Test extends TestCase
	{
		public static class TestStruct
		{
			void init()
			{
				aBoolean   = true;
				aByte      = 1;
				aChar      = '1';
				aShort     = 2;
				anInt      = 3;
				aLong      = 4;
				aFloat     = (float)5.5;
				aDouble    = 6.6;
				aString    = "a string";
				aTS2       = new TestStruct2("TS2 string", 2);
				intArray   = new int[]   { 1, 2, 3 };
				
// VA2.0 bug: can't handle 2D arrays via reflection
//				int2DArray = new int[][] { new int[] { 11, 12 }, new int[] { 21, 22 }, new int[] { 31, 32} };

				aTS2Array  = new TestStruct2[] { new TestStruct2("ts2a0", 0), new TestStruct2("ts2a0", 0) };
			}
			
			public boolean aBoolean;
			public byte    aByte;
			public char    aChar;
			public short   aShort;
			public int     anInt;
			public long    aLong;
			public float   aFloat;
			public double  aDouble;
			public String  aString;
			public TestStruct2   aTS2;
			public int[]         intArray;
// VA2.0 bug: can't handle 2D arrays via reflection
//			public int[][]       int2DArray;
			public TestStruct2[] aTS2Array;
		}

		public static class TestStruct2
		{
			public TestStruct2() {}
			public TestStruct2(String _str, int _x) { str = _str; x = _x; }
			public String str;
			public int    x;
		}
		
		public static TestSuite suite()
		{
			TestSuite result = new TestSuite();
			result.addTest(new Test("testNewStruct"));
			result.addTest(new Test("testClearStruct"));
			result.addTest(new Test("testPrintStruct"));
			return result;
		}

		/**
		 *  NOTE: Using VisualAge 2.0, using reflection to set the value of a 2D array causes
		 *  an internal error.
		 */
		public void testNewStruct()
		{
			TestStruct s = (TestStruct)StructBuilder.newStruct(TestStruct.class);
			assertTrue("newStruct returned null!", s != null);
			
			String err = " field is not the expected default value ";
			assertTrue("aBoolean" + err + "false", s.aBoolean == false);
			assertTrue("aByte"    + err + "0",     s.aByte    == 0);
			assertTrue("aChar"    + err + "'\\0'", s.aChar    == '\0');
			assertTrue("aShort"   + err + "0",     s.aShort   == 0);
			assertTrue("anInt"    + err + "0",     s.anInt    == 0);
			assertTrue("anLong"   + err + "0",     s.aLong    == 0);
			assertTrue("aFloat"   + err + "0.0",   s.aFloat   == 0.0);
			assertTrue("aDouble"  + err + "0.0",   s.aDouble  == 0.0);
			assertTrue("aString"  + err + "\"\"",  s.aString != null && s.aString.equals(""));
			assertTrue("aTS2"     + err + "{\"\", 0}", s.aTS2.str != null && s.aTS2.str.equals("") && s.aTS2.x==0);
			assertTrue("intArray" + err + " int[0]", s.intArray != null && s.intArray.length==0);
// VA2.0 bug: can't handle 2D arrays via reflection
//			assertTrue("int2DArray" + err + " int[][0]", s.int2DArray != null && s.int2DArray.length==0);
			assertTrue("aTS2Array"  + err + " TestStruct2[0]", s.aTS2Array != null && s.aTS2Array.length==0);
		}

		public void testClearStruct()
		{
			TestStruct s = new TestStruct();
			s.init();
			String preErr = "Test testup: ";
			String err = " field erroneously was the default value ";
			assertTrue(preErr + "aBoolean" + err + "false", s.aBoolean != false);
			assertTrue(preErr + "aByte"    + err + "0",     s.aByte    != 0);
			assertTrue(preErr + "aChar"    + err + "'\\0'", s.aChar    != '\0');
			assertTrue(preErr + "aShort"   + err + "0",     s.aShort   != 0);
			assertTrue(preErr + "anInt"    + err + "0",     s.anInt    != 0);
			assertTrue(preErr + "anLong"   + err + "0",     s.aLong    != 0);
			assertTrue(preErr + "aFloat"   + err + "0.0",   s.aFloat   != 0.0);
			assertTrue(preErr + "aDouble"  + err + "0.0",   s.aDouble  != 0.0);
			assertTrue(preErr + "aString"  + err + "\"\"",  s.aString != null && !s.aString.equals(""));
			assertTrue(preErr + "aTS2"     + err + "{\"\", 0}", s.aTS2.str != null && !s.aTS2.str.equals("") && s.aTS2.x!=0);
			assertTrue(preErr + "intArray" + err + " int[0]", s.intArray != null && s.intArray.length!=0);
// VA2.0 bug: can't handle 2D arrays via reflection
//			assertTrue("int2DArray" + err + " int[][0]", s.int2DArray != null && s.int2DArray.length==0);
			assertTrue("aTS2Array"  + err + " TestStruct2[0]", s.aTS2Array != null && s.aTS2Array.length!=0);

			clearStruct(s);
			assertTrue("aBoolean" + err + "false", s.aBoolean == false);
			assertTrue("aByte"    + err + "0",     s.aByte    == 0);
			assertTrue("aChar"    + err + "'\\0'", s.aChar    == '\0');
			assertTrue("aShort"   + err + "0",     s.aShort   == 0);
			assertTrue("anInt"    + err + "0",     s.anInt    == 0);
			assertTrue("anLong"   + err + "0",     s.aLong    == 0);
			assertTrue("aFloat"   + err + "0.0",   s.aFloat   == 0.0);
			assertTrue("aDouble"  + err + "0.0",   s.aDouble  == 0.0);
			assertTrue("aString"  + err + "\"\"",  s.aString != null && s.aString.equals(""));
			assertTrue("aTS2"     + err + "{\"\", 0}", s.aTS2.str != null && s.aTS2.str.equals("") && s.aTS2.x==0);
			assertTrue("intArray" + err + " int[0]", s.intArray != null && s.intArray.length==0);
// VA2.0 bug: can't handle 2D arrays via reflection
//			assertTrue("int2DArray" + err + " int[][0]", s.int2DArray != null && s.int2DArray.length==0);
			assertTrue("aTS2Array"  + err + " TestStruct2[0]", s.aTS2Array != null && s.aTS2Array.length==0);
		}
		
		public void testPrintStruct()
		{
			try
			{
				TestStruct s = new TestStruct();
				
				s.aBoolean   = false;
				s.aByte      = 0;
				s.aChar      = '1';
				s.aShort     = 2;
				s.anInt      = 3;
				s.aLong      = 4;
				s.aFloat     = 5;
				s.aDouble    = 6;
				s.aString    = null;
				s.aTS2       = null;
				s.intArray   = null;
	// VA2.0 bug: can't handle 2D arrays via reflection
	//			s.int2DArray = null;
				s.aTS2Array  = null;

				final String[] expectOutput =
				{
					"s.aBoolean = false",
					"s.aByte = 0",
					"s.aChar = 1",
					"s.aShort = 2",
					"s.anInt = 3",
					"s.aLong = 4",
					"s.aFloat = 5.0",
					"s.aDouble = 6.0",
					"s.aString = null",
					"s.aTS2 = null",
					"s.intArray = null",
	// VA2.0 bug: can't handle 2D arrays via reflection
	//				"s.int2DArray = null",
					"s.aTS2Array = null",
				};
				
				boolean writing = true;
				try
				{
					{
						ByteArrayOutputStream bytesOut = new ByteArrayOutputStream(10*expectOutput.length);
						OutputStreamWriter writer = new OutputStreamWriter(bytesOut);

						printStruct(s, "s", writer);
						
						writer.flush();
						writing = false;
						byte[] bytes = bytesOut.toByteArray();
						LineNumberReader reader = new LineNumberReader(new InputStreamReader(new ByteArrayInputStream(bytes)));
						for (int i=0; i < expectOutput.length; ++i)
						{
							String line = reader.readLine();
							String expect = expectOutput[i];
							assertTrue("Output ended premateurly. Expecting string \"" + expect + "\".", line!=null);

							if (!expect.equals(line))
								System.out.println("Expected ouput \"" + expect + "\", got \"" + line + "\"");
							//assertTrue("Expected ouput \"" + expect + "\", got \"" + line + "\"", expect.equals(line));
						}
					}

					s.aString = "a string";
					s.aTS2 = new TestStruct2("substruct's string", 1234);
					s.intArray = new int[] { 100, 101, 102 };
					s.aTS2Array = new TestStruct2[] 
					{
						new TestStruct2("first element", 1000),
						new TestStruct2("second element", 1001),
					};
					
					final String[] newExpectedOutput =
					{
						"s.aString = \"a string\"",
						"s.aTS2.str = \"substruct's string\"",
						"s.aTS2.x = 1234",
						"s.intArray[0] = 100",
						"s.intArray[1] = 101",
						"s.intArray[2] = 102",
						"s.aTS2Array[0].str = \"first element\"",
						"s.aTS2Array[0].x = 1000",
						"s.aTS2Array[1].str = \"second element\"",
						"s.aTS2Array[1].x = 1001",
					};
					int startNewAt = 8;

					{
						ByteArrayOutputStream bytesOut = new ByteArrayOutputStream(10*expectOutput.length);
						OutputStreamWriter writer = new OutputStreamWriter(bytesOut);

						printStruct(s, "s", writer);
						
						writer.flush();
						writing = false;
						byte[] bytes = bytesOut.toByteArray();
						LineNumberReader reader = new LineNumberReader(new InputStreamReader(new ByteArrayInputStream(bytes)));
						for (int i=0; i < startNewAt + newExpectedOutput.length; ++i)
						{
							String line = reader.readLine();
							String expect = i < startNewAt ? expectOutput[i] : newExpectedOutput[i-startNewAt];
							assertTrue("Output ended prematurely. Expecting string \"" + expect + "\".", line!=null);
							assertTrue("Expected ouput \"" + expect + "\", got \"" + line + "\"", expect.equals(line));
						}
					}
					writing = true;
				}
				catch (IOException ex)
				{
					assertTrue("Unexpected exception during " + (writing?"write":"read") + ": " + ex, false);
				}
			}
			catch (Error ex)
			{
				assertTrue("Unexpected exception " + ex, false);
			}
		}
		
		public Test(String methodName)
		{
			super(methodName);
		}
		public static void main(String args[])
		{
			System.out.println("Unit testing CustomInputStream");
			junit.textui.TestRunner.run(suite());
		}
	}

/**
 * Clear a given structure's public fields (recursing references to other structures).
 * All public data members will be cleared.  If a given field is a primitive type, then 
 * set it to it's default setting (0, 0.0, or false); if it's a string, then the 
 * value "" is used; if it's another class, then the method is recursed to implement
 * that method.  Note that a mutual dependency can cause infinite recursion (ie, class 
 * A contains a reference to B, and B contains a reference to A).  
 * 
 * @author Steven Sinclair
 * @return java.lang.Object
 * @param structClass java.lang.Class
 */
public static void clearStruct(Object struct)
{
	try
	{
		Field[] fields = struct.getClass().getFields();
		for (int i=0; i < fields.length; ++i)
		{
			Class type = fields[i].getType();
			if (String.class == type)
			{
				fields[i].set(struct, "");
				continue;
			}
			if (type.isArray())
			{
				fields[i].set(struct, Array.newInstance(type.getComponentType(), 0));
				continue;
			}
			if (!type.isPrimitive())
			{
				fields[i].set(struct, newStruct(type));
				continue;
			}
			if (int.class == type)
			{
				fields[i].setInt(struct, 0);
				continue;
			}
			if (char.class == type)
			{
				fields[i].setChar(struct, '\0');
				continue;
			}
			if (short.class == type)
			{
				fields[i].setShort(struct, (short)0);
				continue;
			}
			if (long.class == type)
			{
				fields[i].setLong(struct, 0L);
				continue;
			}
			if (boolean.class == type)
			{
				fields[i].setBoolean(struct, false);
				continue;
			}
			if (double.class == type)
			{
				fields[i].setDouble(struct, 0.0D);
				continue;
			}
			if (byte.class == type)
			{
				fields[i].setByte(struct, (byte)0);
				continue;
			}
			if (float.class == type)
			{
				fields[i].setFloat(struct, 0.0F);
				continue;
			}
		}
	}
	catch (IllegalAccessException ex)
	{
		throw new IllegalArgumentException("Class " + struct.getClass().getName() + " doesn't seem to be an IDL-generated struct.");
	}
}
	/**
	 *  Invoke selftest
	 */
	public static void main(String[] args)
	{
		Test.main(args);
	}
/**
 * Create a new instance of a given class.  This is most usefull for instantiating
 * CORBA structures.  All public data members will be initialized.  If a given field
 * is a primitive type, then set it to it's default setting (0, 0.0, or false); if
 * it's a string, then the value "" is used; if it's another class, then the method
 * is recursed to implement that method.  Note that a mutual dependency can cause
 * infinite recursion (ie, class A contains a reference to B, and B contains a reference
 * to A).  
 * 
 * @author Steven Sinclair
 * @return java.lang.Object
 * @param structClass java.lang.Class
 */
public static Object newStruct(Class structClass)
{
	try
	{
		Object obj = structClass.newInstance();
		Field[] fields = structClass.getFields();
		for (int i=0; i < fields.length; ++i)
		{
			Class type = fields[i].getType();
			if (type.isArray())
			{
				fields[i].set(obj, Array.newInstance(type.getComponentType(), 0));
			}
			else if (char.class == type)
			{
				fields[i].setChar(obj, '\0');
			}
			else if (!type.isPrimitive())
			{
				fields[i].set(obj, String.class==type ? "" : newStruct(type));
			}
		}
		return obj;
	}
	catch (IllegalAccessException ex)
	{
		throw new IllegalArgumentException("Class " + structClass.getName() + " doesn't seem to be an IDL-generated struct.");
	}
	catch (InstantiationException ex)
	{
		throw new IllegalArgumentException("Class " + structClass.getName() + " doesn't seem to be an IDL-generated struct.");
	}
}
/**
 * Calls <code>printStruct(Object, String, Writer)</code>, using a new
 * <code>PrintWriter</code> tied to <code>System.out</code>.  If there's
 * an IOException generated then it'll be rethrown as an Error.
 * <p>
 * Prints out a description of the fields of struct, line-by-line, recursing
 * non-primitive types, prefixing each line of output with the prefix string.
 *
 * @see #printStruct(Object, String, Writer)
 * @author Steven Sinclair
 * @param struct java.lang.Object
 * @param prefix java.lang.String
 */
public static void printStruct(Object struct, String prefix)
{
	try
	{
		PrintWriter writer = new PrintWriter(System.out);
		printStruct(struct, prefix, writer);
		writer.flush();
	}
	catch (IOException ex)
	{
		throw new Error("Unexpected IO error using System.out: " + ex);
	}
}
/**
 *  Print the values of the public fields of the given <code>struct</code> to 
 *  <code>writer</code> line-by-line, prefixing each line with the value of <code>prefix</code>. 
 *  If the object passed in is not a string or array, and if the prefix doesn't end in a '.', 
 *  then a '.' will be appended to the prefix.  Strings will be double-quote delimited and arrays will
 *  be iterated.
 *
 *  @param struct The object to print.  Any object can be passed in (including arrays),
 *					though only it's public fields will be printed.
 *  @param prefix The prefix string to write before all output lines generated by this method. (usually the
 *		instance name of the struct being printed)
 *  @param writer The writer to write the structure's description to.
 *  @exeption java.io.IOException thrown if there's a problem using <code>writer</code>.
 */
static public void printStruct(Object struct, String prefix, Writer writer)
	throws IOException
{
	try
	{
		if (struct == null)
		{
			writer.write(prefix + " = null\n");
			return; // finished
		}

		Class structType = struct.getClass();

		if (structType == String.class)
		{
			// A string should be wrapped with double quotes ('"').
			//
			writer.write(prefix + " = \"");
			writer.write(struct.toString());
			writer.write("\"\n");
			return;	// finished
		}
		if (structType.isArray())
		{
			// Handle arrays: print prefix + array index + element value
			// If the array type is not primitive, then recurse this method.
			//
			Object array = struct;
			int len = Array.getLength(array);
			if (len == 0)
			{
				writer.write(prefix + " = [empty]\n");
			}
			Class arrayType = structType.getComponentType();
			for (int j=0; j < len; ++j)
			{
				String pre = prefix + '[' + j + "] = ";
				if (arrayType.isPrimitive())
				{
					if (arrayType==byte.class || arrayType==short.class ||
					    arrayType==int.class || arrayType==long.class)
					{
						writer.write(pre + Array.getLong(array, j));
					}
					else if (arrayType==char.class)
						writer.write(pre + Array.getChar(array, j));
					else if (arrayType==float.class || arrayType==double.class)
						writer.write(pre + Array.getDouble(array, j));
					else if (arrayType==boolean.class)
						writer.write(pre + Array.getBoolean(array, j));
					writer.write('\n');
				}
				else
				{
					printStruct(Array.get(array, j), prefix + "[" + j + "]", writer);
				}
			}
			return;		// finished.
		}

		// Process the struct field-by-field
		//
		Field[] fields = structType.getFields();
		if (fields.length == 0)
		{
			writer.write(prefix);
			writer.write(" [struct has no public fields ]\n");
		}

		if (prefix.length() > 0 && prefix.charAt(prefix.length()-1) != '.')
			prefix += '.';

		for (int i=0; i < fields.length; ++i)
		{
			Class type = fields[i].getType();
			if (Modifier.isStatic(fields[i].getModifiers()))
			{
				continue; // skip static fields
			}
			String fieldPrefix = prefix + fields[i].getName();
			if (type.isPrimitive())
			{
				// Print primitive types
				//
				writer.write(fieldPrefix);
				if (type==byte.class || type==short.class || type==int.class || type==long.class)
					writer.write(" = "+fields[i].getLong(struct));
				else if (type==char.class)
					writer.write(" = "+fields[i].getChar(struct));
				else if (type==float.class || type==double.class)
					writer.write(" = "+fields[i].getDouble(struct));
				else if (type==boolean.class)
					writer.write(" = "+fields[i].getBoolean(struct));
				else
					writer.write(" = "+fields[i].get(struct));
				writer.write('\n');
			}
			else
			{
				// Recurse non-trivial structures!
				//
				printStruct(fields[i].get(struct), fieldPrefix, writer);
			}
		}
	}
	catch (IllegalAccessException ex)
	{
		throw new Error("Unexpected access exception printing structure: " + ex);
	}
}
}
