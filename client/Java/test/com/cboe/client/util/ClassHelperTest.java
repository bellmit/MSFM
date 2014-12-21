package com.cboe.client.util;

import java.lang.reflect.Method;
import junit.framework.JUnit4TestAdapter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;  // annotation

public class ClassHelperTest
{
    // Allow this JUnit 4 test to run under JUnit3
    public static junit.framework.Test suite()
    {
        return new JUnit4TestAdapter(ClassHelperTest.class);
    }

    @Test public void testFindClass()
    {
        String packages[] =
                { "org.omg", "java.lang", "com.cboe.nothing", "java.util" };
        assertEquals(java.lang.String.class,
                ClassHelper.findClass("String", packages));
        assertNull(ClassHelper.findClass("Garbage", packages));
        assertEquals(java.util.GregorianCalendar.class,
                ClassHelper.findClass("GregorianCalendar", packages));
    }

    @Test public void testForName()
    {
        assertNull(ClassHelper.forName("ClassHelperTest"));
        assertEquals(com.cboe.client.util.ClassHelperTest.class,
                ClassHelper.forName("com.cboe.client.util.ClassHelperTest"));
        assertNull(ClassHelper.forName("java.util.NoUtil"));
        assertEquals(String.class, ClassHelper.forName("java.lang.String"));
    }

    @Test(expected=ClassNotFoundException.class)
    public void testForNameWithExceptions1() throws Exception
    {
        assertNull(ClassHelper.forNameWithExceptions("ClassHelperTest"));
    }

    @Test public void testForNameWithExceptions2() throws Exception
    {
        assertEquals(com.cboe.client.util.ClassHelperTest.class,
                ClassHelper.forNameWithExceptions(
                        "com.cboe.client.util.ClassHelperTest"));
    }

    @Test(expected=ClassNotFoundException.class)
    public void testForNameWithExceptions3() throws Exception
    {
        assertNull(ClassHelper.forNameWithExceptions("java.util.NoUtil"));
    }

    @Test public void testForNameWithExceptions4() throws Exception
    {
        assertEquals(String.class,
                ClassHelper.forNameWithExceptions("java.lang.String"));
    }

    @Test public void testGetClassMethods()
    {
        Method methods[];
        methods = ClassHelper.getClassMethods(java.lang.String.class, "concat");
        assertEquals(1, methods.length);
        assertEquals(String.class, methods[0].getReturnType());
        Class parms[] = methods[0].getParameterTypes();
        assertEquals(1, parms.length);
        assertEquals(String.class, parms[0]);

        methods = ClassHelper.getClassMethods(java.lang.Integer.class, "zip");
        assertEquals(0, methods.length);
    }

    public class NestedClass
    {
        int  field = 1;
        void func() { field = 2; }
    }

    @Test public void testgetClassName()
    {
        String str = "";
        assertEquals("java.lang.String", ClassHelper.getClassName(str));

        Byte b = 0;
        assertEquals("java.lang.Byte", ClassHelper.getClassName(b));

        byte bytes[] = new byte[0];
        assertEquals("byte[]", ClassHelper.getClassName(bytes));

        char chars[][] = new char[1][];
        chars[0] = new char[5];

        assertEquals("char[][]", ClassHelper.getClassName(chars));
        assertEquals("char[]", ClassHelper.getClassName(chars[0]));

        NestedClass nc = new NestedClass();
        assertEquals("com.cboe.client.util.ClassHelperTest$NestedClass",
                ClassHelper.getClassName(nc));
    }

    @Test public void testGetClassNameFinalPortion()
    {
        assertEquals("String",
                ClassHelper.getClassNameFinalPortion(String.class));
        
        String str = "";
        assertEquals("String", ClassHelper.getClassNameFinalPortion(str));

        NestedClass nc = new NestedClass();
        assertEquals("NestedClass", ClassHelper.getClassNameFinalPortion(nc));

        char chars[] = new char[0];
        assertEquals("[C", ClassHelper.getClassNameFinalPortion(chars));
    }

    @Test public void testLoadClass()
    {
        byte byteArray[] = new byte[3];
        byteArray[0] = 'T';
        byteArray[1] = 'H';
        byteArray[2] = 'X';
        Object parms[] = { byteArray };
        Object o = ClassHelper.loadClass("java.lang.String", parms);
        assertEquals("java.lang.String", o.getClass().getName());
        assertEquals("THX", o);

        String value = "3";
        parms[0] = value;
        o = ClassHelper.loadClass("java.lang.Integer", parms);
        assertEquals("java.lang.Integer", o.getClass().getName());
        assertEquals(3, ((Integer) o).intValue());

        Integer ival = 3;
        parms[0] = ival;
        // This call to loadClass fails and calls Log.exception, which
        // creates a lot of messy output in the middle of our test run.
        // TODO: If Log ever provides method setInstanceForTesting, use it!
        o = ClassHelper.loadClass("java.lang.Integer", parms);
        assertNull(o);
    }
}
