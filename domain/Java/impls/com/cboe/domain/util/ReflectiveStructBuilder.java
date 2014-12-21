package com.cboe.domain.util;

import java.lang.reflect.*;
import java.io.*;

import junit.framework.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmiUtil.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiQuote.*;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.product.*;
import com.cboe.util.ReflectiveObjectWriter;

/**
 *  Principally intended for the creation of any CORBA-generated struct, this class
 *  can be used to create any Java class via its default constuctor.  It will initilalize
 *  all of the class's public fields to default values, guaranteed to be non-null.
 *
 *  <p>Since the reflection API can be expensive, further functionality has been added to
 *  allow custom creation code to be used to create common structs, or structs which require
 *  non-default initialization values.  The CORBA structs which can currently be created
 *  without the invokation of reflection are:
 *  <pre>
 *        com.cboe.idl.cmiUtil.PriceStruct
 *        com.cboe.idl.cmiUtil.DateStruct
 *        com.cboe.idl.cmiUtil.DateTimeStruct
 *        com.cboe.idl.cmiUtil.TimeStruct
 *        com.cboe.idl.cmiProduct.ClassStruct
 *        com.cboe.idl.cmiProduct.ProductNameStruct
 *        com.cboe.idl.cmiProduct.ProductStruct
 *        com.cboe.idl.product.ProductClassStruct
 *        com.cboe.idl.product.ReportingClassStruct
 *        com.cboe.idl.cmiQuote.QuoteEntryStruct
 *        com.cboe.idl.cmiQuote.QuoteSideStruct
 * </pre>
 *
 * <p>There is also a clearStruct(Object) method to wipe a class's fields to default values.
 * This method is always a reflective invokation.
 *
 * <p>Finally, there is a printStruct(...) method which has proven to be quote useful for
 * testing and debugging since it will print the entire given structure.
 *
 * @author Steven Sinclair
 */
public class ReflectiveStructBuilder
{
	/**
	 *  To minimize the cost of reflective struct building, commonly
	 *  used struct classes are registered in the hashtable with
	 *  simple factory classes be be able to quickly produce new instances.
	 *  This funcationality can also be used if a class requires "special"
	 *  creation value.
	 */
	private static java.util.Hashtable prototypes;

	private static interface StructCreator
	{
		Object create();
	}

	private static class PriceStructCreator implements StructCreator
	{
		public Object create()
		{
			PriceStruct priceStruct = StructBuilder.buildPriceStruct();
			priceStruct.type = PriceTypes.NO_PRICE;
			return priceStruct;
		}
	}

	public static class TimeStructCreator implements StructCreator
	{
		public Object create()
		{
			return StructBuilder.buildTimeStruct();
		}
	}

	public static class DateTimeStructCreator implements StructCreator
	{
		public Object create()
		{
			return StructBuilder.buildDateTimeStruct();
		}
	}

	public static class ClassStructCreator implements StructCreator
	{
		public Object create()
		{
			return ProductStructBuilder.buildClassStruct();
		}
	}

	public static class ProductClassStructCreator implements StructCreator
	{
		public Object create()
		{
			return ProductStructBuilder.buildProductClassStruct();
		}
	}

	public static class ReportingClassStructCreator implements StructCreator
	{
		public Object create()
		{
			return ProductStructBuilder.buildReportingClassStruct();
		}
	}

	public static class ProductNameStructCreator implements StructCreator
	{
		public Object create()
		{
			return ProductStructBuilder.buildProductNameStruct();
		}
	}

	public static class ProductStructCreator implements StructCreator
	{
		public Object create()
		{
			return ProductStructBuilder.buildProductStruct();
		}
	}

	public static class QuoteEntryStructCreator implements StructCreator
	{
		public Object create()
		{
			return QuoteStructBuilder.buildQuoteEntryStruct();
		}
	}


	public static class OrderStructCreator implements StructCreator
	{
		public Object create()
		{
            return OrderStructBuilder.buildOrderStruct();
		}
	}

	public static class OrderIDStructCreator implements StructCreator
	{
		public Object create()
		{
            return OrderStructBuilder.buildOrderIdStruct();
		}
	}


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
			result.addTest(new Test("testCompareStructTrue1"));
			result.addTest(new Test("testCompareStructTrue2"));
			result.addTest(new Test("testCompareStructTrue3"));
			result.addTest(new Test("testCompareStructFalse1"));
			result.addTest(new Test("testCompareStructFalse2"));
			result.addTest(new Test("testCompareStructFalse3"));
			result.addTest(new Test("testCompareStructFalse4"));
			return result;
		}
        
        public void testCompareStructTrue1() throws Exception
        {
            TestStruct s1 = (TestStruct)ReflectiveStructBuilder.newStruct(TestStruct.class);
            TestStruct s2 = (TestStruct)ReflectiveStructBuilder.newStruct(TestStruct.class);
            StringBuffer buf = new StringBuffer();
            boolean same1 = compareStructs(s1, s2, buf);
            assertTrue("basic s1 != s2: " + buf, same1);
        }

        public void testCompareStructTrue2() throws Exception
        {
            TestStruct s1 = (TestStruct)ReflectiveStructBuilder.newStruct(TestStruct.class);
            TestStruct s2 = (TestStruct)ReflectiveStructBuilder.newStruct(TestStruct.class);
            s1.init();
            s2.init();
            StringBuffer buf = new StringBuffer();
            boolean same1 = compareStructs(s1, s2, buf);
            assertTrue("initialized s1 != s2: " + buf, same1);
        }
        
        public void testCompareStructTrue3() throws Exception
        {
            StringBuffer buf = new StringBuffer();
            boolean same1 = compareStructs(null, null, buf);
            assertTrue("null s1 != s2: " + buf, same1);
        }
        
        public void testCompareStructFalse1() throws Exception
        {
            TestStruct s1 = (TestStruct)ReflectiveStructBuilder.newStruct(TestStruct.class);
            TestStruct s2 = (TestStruct)ReflectiveStructBuilder.newStruct(TestStruct.class);
            s1.aFloat = 123;
            StringBuffer buf = new StringBuffer();
            boolean same1 = compareStructs(s1, s2, buf);
            assertTrue("basic s1 == s2 (but s1.aFloat changed): " + buf, !same1);
        }
        
        public void testCompareStructFalse2() throws Exception
        {
            TestStruct s1 = (TestStruct)ReflectiveStructBuilder.newStruct(TestStruct.class);
            TestStruct s2 = (TestStruct)ReflectiveStructBuilder.newStruct(TestStruct.class);
            s1.init();
            s2.init();
            s1.aTS2Array = new TestStruct2[0];
            StringBuffer buf = new StringBuffer();
            boolean same1 = compareStructs(s1, s2, buf);
            assertTrue("basic s1 == s2 (but s1.aTS2Array changed): " + buf, !same1);
        }
        
        public void testCompareStructFalse3() throws Exception
        {
            TestStruct s1 = (TestStruct)ReflectiveStructBuilder.newStruct(TestStruct.class);
            TestStruct s2 = (TestStruct)ReflectiveStructBuilder.newStruct(TestStruct.class);
            s1.init();
            s2.init();
            s1.aTS2.str = "different";
            StringBuffer buf = new StringBuffer();
            boolean same1 = compareStructs(s1, s2, buf);
            assertTrue("basic s1 == s2 (but s1.aTS2.str changed): " + buf, !same1);
        }
        
        public void testCompareStructFalse4() throws Exception
        {
            TestStruct s1 = (TestStruct)ReflectiveStructBuilder.newStruct(TestStruct.class);
            TestStruct s2 = (TestStruct)ReflectiveStructBuilder.newStruct(TestStruct.class);
            s1.init();
            s2.init();
            s1.aTS2Array[1].str = "different";
            StringBuffer buf = new StringBuffer();
            boolean same1 = compareStructs(s1, s2, buf);
            assertTrue("basic s1 == s2 (but s1.aTS2Array[1].str changed): " + buf, !same1);
        }
        
		public void testNewStruct()
		{
			TestStruct s = (TestStruct)ReflectiveStructBuilder.newStruct(TestStruct.class);
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


			// Test the auto-creation strategy: PriceStruct is a known creation class
			//
			PriceStruct p = (PriceStruct)ReflectiveStructBuilder.newStruct(PriceStruct.class);
			assertTrue("p.type should be NO_PRICE (" + PriceTypes.NO_PRICE + "), got " + p.type, p.type==PriceTypes.NO_PRICE);
			assertTrue("p.fraction should be 0, got " + p.fraction, p.fraction==0);
			assertTrue("p.whole should be 0, got " + p.whole, p.whole==0);
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
 *  Just used to prevent instances of this class from being created, since this
 *  is just a collection of static methods.
 */
private ReflectiveStructBuilder()
{
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
	// Static initialization block where the hashtable gets initialized and
	// filled.
	//
	protected static java.util.Hashtable getPrototypes()
	{
		if (prototypes == null)
		{
			synchronized(ReflectiveStructBuilder.class)
			{
				if (prototypes == null)
				{
					prototypes = new java.util.Hashtable();

					prototypes.put(PriceStruct.class, new PriceStructCreator());
					prototypes.put(TimeStruct.class, new TimeStructCreator());
					prototypes.put(DateTimeStruct.class, new DateTimeStructCreator());

					prototypes.put(ClassStruct.class, new ClassStructCreator());
					prototypes.put(ProductClassStruct.class, new ProductClassStructCreator());
					prototypes.put(ReportingClassStruct.class, new ReportingClassStructCreator());
					prototypes.put(ProductNameStruct.class, new ProductNameStructCreator());
					prototypes.put(ProductStruct.class, new ProductStructCreator());

					prototypes.put(QuoteEntryStruct.class, new QuoteEntryStructCreator());

					prototypes.put(OrderStruct.class, new OrderStructCreator());
					prototypes.put(OrderIdStruct.class, new OrderIDStructCreator());
				}
			}
		}
		return prototypes;
	}
/**
 * Initialize the given field, if it's non-primitive (except char), or is an array.
 *
 * @param field java.lang.reflect.Field
 * @param instance java.lang.Object, the new object the field is to applied to
 */
private static void initField(Field field, Object instance)
	throws IllegalAccessException
{
	Class type = field.getType();
	if (type.isArray())
	{
		field.set(instance, Array.newInstance(type.getComponentType(), 0));
	}
	else if (char.class == type)
	{
		field.setChar(instance, '\0');
	}
	else if (!type.isPrimitive())
	{
		field.set(instance, String.class==type ? "" : newStruct(type));
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
 * If the class requested has been mapped to the prototypes hashtable, then reflection will
 * not be used, but rather the custom creation code defined by the associated StructCreator
 * instance (a private interface type).
 *
 * @author Steven Sinclair
 * @return java.lang.Object
 * @param structClass java.lang.Class
 */
public static <T> T newStruct(Class<T> structClass)
{
	StructCreator creator = (StructCreator)getPrototypes().get(structClass);
	if (creator != null)
	{
		return (T)creator.create();
	}

	try
	{
		T obj = structClass.newInstance();
		Field[] fields = structClass.getFields();
		for (int i=0; i < fields.length; ++i)
		{
			initField(fields[i], obj);
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

public static String structToString(Object struct, String prefix)
{
    try
    {
        StringWriter aStringWriter = new StringWriter();
        ReflectiveObjectWriter.writeObject( struct, prefix, aStringWriter );
         
        return aStringWriter.toString();
    }
    catch (IOException ex)
    {
        throw new Error("Unexpected IO error using System.out: " + ex);
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
    ReflectiveObjectWriter.writeObject( struct, prefix );
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
    ReflectiveObjectWriter.writeObject( struct, prefix, writer );
}

/**
 * Return true if all public, non-static fields of the two given structs are identical.
 * 
 * @param o1
 * @param o2
 * @return true if all public, non-statuc fields are identical (by recursive .equals() comparisons).
 */
static public boolean compareStructs(Object o1, Object o2)
{
    return compareStructs(o1, o2, null);
}
/**
 * Return true if all public, non-static fields of the two given structs are identical.
 * 
 * @param o1
 * @param o2
 * @param verboseTo - a StringBuffer to which multi-line mismatch information is appended.  Null is allowed. 
 * @return true if all public, non-statuc fields are identical (by recursive .equals() comparisons).
 */
static public boolean compareStructs(Object o1, Object o2, StringBuffer verboseTo)
{
    if (o1==null || o2==null)
    {
        return (o1==null && o2 == null);
    }
    
    try
    {
        Class objectType = o1.getClass();
        Class o2Type = o2.getClass();
        if (o2Type != objectType)
        {
            if (verboseTo != null)
            {
                verboseTo.append("Type mismatch: o1=" + objectType + ", o2=" + o2Type + "\n");
            }
            return false; // not the same class!
        }
        
        if (objectType == String.class  ||
            objectType == Integer.class ||
            objectType == Boolean.class ||
            objectType == Character.class ||
            objectType == Byte.class ||
            objectType == Short.class ||
            objectType == Long.class ||
            objectType == Float.class ||
            objectType == Double.class
            )
        {
            if (!o1.equals(o2))
            {
                if (verboseTo != null)
                {
                    verboseTo.append(objectType.getName() + " value mismatch: " + o1 + " != " + o2  + "\n");
                }
                return false;
            }
            return true;
        }
        
        if (objectType.isArray())
        {
            // Handle arrays: compare simple or (recursively) struct arrays. 
            //
            int len1 = Array.getLength(o1);
            int len2 = Array.getLength(o2);
            Class arrayType = objectType.getComponentType();
            if (len1 != len2)
            {
                if (verboseTo != null)
                {
                    verboseTo.append("Array of " + arrayType.getName() + " length mismatch: o1 len " + len1 + " != o2 len " + len2 + "\n");
                }
                return false;
            }
            boolean simpleArray = (arrayType.isPrimitive() || arrayType == String.class);
            for (int j=0; j < len1; ++j)
            {
                if (simpleArray)
                {
                    if (!Array.get(o1, j).equals(Array.get(o2, j)))
                    {
                        if (verboseTo != null)
                        {
                            verboseTo.append("Array[" + j + "] of " + arrayType.getName() + " elements mismatched\n");
                        }
                        return false;
                    }
                }
                else
                {
                    if (!compareStructs(Array.get(o1, j), Array.get(o2, j), verboseTo))
                    {
                        if (verboseTo != null)
                        {
                            verboseTo.append("Array[" + j + "] of " + arrayType.getName() + " elements mismatched\n");
                        }
                        return false;
                    }
                }
            }
            return true;
        }
        
        // Compare the objects field-by-field
        //
        Field[] fields = objectType.getFields();
        for (int i=0; i < fields.length; ++i)
        {
            Class type = fields[i].getType();
            if (Modifier.isStatic(fields[i].getModifiers()))
            {
                continue; // skip static fields
            }
            if (type.isPrimitive())
            {
                // compare primitive types
                //
                if (!fields[i].get(o1).equals(fields[i].get(o2)))
                {
                    if (verboseTo != null)
                    {
                        verboseTo.append("Field " + fields[i].getName() + " mismatch: " + fields[i].get(o1) + " != " + fields[i].get(o2) + "\n");
                    }
                    return false;
                }
            }
            else
            {
                // compare "sub-structs"
                //
                if (!compareStructs(fields[i].get(o1), fields[i].get(o2), verboseTo))
                {
                    if (verboseTo != null)
                    {
                        verboseTo.append("Field " + fields[i].getName() + " mismatch: " + fields[i].get(o1) + " != " + fields[i].get(o2) + "\n");
                    }
                    return false;
                }
            }
        }
    }
    catch (IllegalAccessException ex)
    {
        throw new Error("Unexpected access exception comparing objects: " + ex);
    }
    return true;
}


}
