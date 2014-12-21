package com.cboe.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 */
public class ReflectiveObjectWriter
{
    /**
     * Inner class for support of automated unit testing.
     */
    static public class Test extends TestCase
    {
        public static class TestObject
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
                aTS2       = new TestObject2("TS2 string", 2);
                intArray   = new int[]   { 1, 2, 3 };

// VA2.0 bug: can't handle 2D arrays via reflection
//				int2DArray = new int[][] { new int[] { 11, 12 }, new int[] { 21, 22 }, new int[] { 31, 32} };

                aTS2Array  = new TestObject2[] { new TestObject2("ts2a0", 0), new TestObject2("ts2a0", 0) };
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
            public TestObject2   aTS2;
            public int[]         intArray;
// VA2.0 bug: can't handle 2D arrays via reflection
//			public int[][]       int2DArray;
            public TestObject2[] aTS2Array;
        }

        public static class TestObject2
        {
            public TestObject2() {}
            public TestObject2(String _str, int _x) { str = _str; x = _x; }
            public String str;
            public int    x;
        }

        public static TestSuite suite()
        {
            TestSuite result = new TestSuite();
            result.addTest(new Test("testWriteObject"));
            return result;
        }

        public void testWriteObject()
        {
            try
            {
                TestObject s = new TestObject();

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

                        writeObject(s, "s", writer);

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
                    s.aTS2 = new TestObject2("subobject's string", 1234);
                    s.intArray = new int[] { 100, 101, 102 };
                    s.aTS2Array = new TestObject2[]
                    {
                        new TestObject2("first element", 1000),
                        new TestObject2("second element", 1001),
                    };

                    final String[] newExpectedOutput =
                    {
                        "s.aString = \"a string\"",
                        "s.aTS2.str = \"subobject's string\"",
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

                        writeObject(s, "s", writer);

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
            System.out.println("Unit testing ReflectiveObjectWriter");
            junit.textui.TestRunner.run(suite());
        }
    }

/**
 *  Just used to prevent instances of this class from being created, since this
 *  is just a collection of static methods.
 */
private ReflectiveObjectWriter()
{
}


/**
 * Calls <code>writeObject(Object, String, Writer)</code>, using a new
 * <code>PrintWriter</code> tied to <code>System.out</code>.  If there's
 * an IOException generated then it'll be rethrown as an Error.
 * <p>
 * Prints out a description of the fields of object, line-by-line, recursing
 * non-primitive types, prefixing each line of output with the prefix string.
 *
 * @see #writeObject(Object, String, Writer)
 * @author Steven Sinclair
 * @author Matt Sochacki
 * @param object java.lang.Object
 * @param prefix java.lang.String
 */
public static void writeObject(Object object, String prefix)
{
    try
    {
        PrintWriter writer = new PrintWriter(System.out);
        writeObject(object, prefix, writer);
        writer.flush();
    }
    catch (IOException ex)
    {
        throw new Error("Unexpected IO error using System.out: " + ex);
    }
}
/**
 *  Write out the values of the public fields of the given <code>object</code> to
 *  <code>writer</code> line-by-line, prefixing each line with the value of <code>prefix</code>.
 *  If the object passed in is not a string or array, and if the prefix doesn't end in a '.',
 *  then a '.' will be appended to the prefix.  Strings will be double-quote delimited and arrays will
 *  be iterated.
 *
 *  @param object The object to write out.  Any object can be passed in (including arrays),
 *					though only it's public fields will be written.
 *  @param prefix The prefix string to write before all output lines generated by this method. (usually the
 *		instance name of the object being written)
 *  @param writer The writer to write the object's description to.
 *  @exeption java.io.IOException thrown if there's a problem using <code>writer</code>.
 */
static public void writeObject(Object object, String prefix, Writer writer)
    throws IOException
{
    try
    {
        if (object == null)
        {
            writer.write(prefix + " = null\n");
            return; // finished
        }

        Class objectType = object.getClass();

        if (objectType == String.class )
        {
            // A string should be wrapped with double quotes ('"').
            //
            writer.write(prefix + " = \"");
            writer.write(object.toString());
            writer.write("\"\n");
            return;	// finished
        }
        //else if (objectType.isPrimitive() )
        else if (objectType == Integer.class ||
                 objectType == Boolean.class ||
                 objectType == Character.class ||
                 objectType == Byte.class ||
                 objectType == Short.class ||
                 objectType == Long.class ||
                 objectType == Float.class ||
                 objectType == Double.class
                )
        {
            writer.write(prefix + " = ");
            writer.write(object.toString());
            writer.write("\n");
            return;
        }

        if (objectType.isArray())
        {
            // Handle arrays: write out  prefix + array index + element value
            // If the array type is not primitive, then recurse this method.
            //
            Object array = object;
            int len = Array.getLength(array);
            if (len == 0)
            {
                writer.write(prefix + " = [empty]\n");
            }
            Class arrayType = objectType.getComponentType();
            for (int j=0; j < len; ++j)
            {
                String pre = prefix + '[' + j + "] = ";
                if (arrayType.isPrimitive())
                {
//					if (arrayType==byte.class || arrayType==short.class ||
//					    arrayType==int.class || arrayType==long.class)
//					{
//						writer.write(pre + Array.getLong(array, j));
//					}
//					else if (arrayType==char.class)
//						writer.write(pre + Array.getChar(array, j));
//					else if (arrayType==float.class || arrayType==double.class)
//						writer.write(pre + Array.getDouble(array, j));
//					else if (arrayType==boolean.class)
//						writer.write(pre + Array.getBoolean(array, j));

                    writer.write(pre + Array.get(object,j));
                    writer.write('\n');
                }
                else
                {
                    writeObject(Array.get(array, j), prefix + "[" + j + "]", writer);
                }
            }
            return;		// finished.
        }

        // Process the object field-by-field
        //
        Field[] fields = objectType.getFields();
        if (fields.length == 0)
        {
            writer.write(prefix);
            writer.write(" [object: "+object.getClass().getName()+" has no public fields ]\n");

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
                // write out primitive types
                //
                writer.write(fieldPrefix);
//				if (type==byte.class || type==short.class || type==int.class || type==long.class)
//					writer.write(" = "+fields[i].getLong(object));
//				else if (type==char.class)
//					writer.write(" = "+fields[i].getChar(object));
//				else if (type==float.class || type==double.class)
//					writer.write(" = "+fields[i].getDouble(object));
//				else if (type==boolean.class)
//					writer.write(" = "+fields[i].getBoolean(object));
//				else
//					writer.write(" = "+fields[i].get(object));

                writer.write(" = "+fields[i].get(object));
                writer.write('\n');
            }
            else
            {
                // Recurse non-trivial objects!
                //
                writeObject(fields[i].get(object), fieldPrefix, writer);
            }
        }
    }
    catch (IllegalAccessException ex)
    {
        throw new Error("Unexpected access exception writing object: " + ex);
    }
}

/**
 *  Invoke selftest
 */
public static void main(String[] args)
{
    Test.main(args);
}

}
