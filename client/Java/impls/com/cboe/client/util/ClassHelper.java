package com.cboe.client.util;

/**
 * ClassHelper.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 *
 * Helper for working with Java Class object
 *
 */

import java.lang.reflect.*;
import java.util.*;

import com.cboe.infrastructureServices.foundationFramework.utilities.*;

public final class ClassHelper
{
    private ClassHelper()
    {

    }

    public static Class findClass(String classAsString, String[] packagesAsString)
    {
        if (packagesAsString == null)
        {
            return forName(classAsString);
        }

        StringBuilder name = new StringBuilder(classAsString.length()+40);
        for (int i = 0; i < packagesAsString.length; i++)
        {
            try
            {
                name.setLength(0);
                name.append(packagesAsString[i]).append(".").append(classAsString);
                return Class.forName(name.toString());
            }
            catch (Exception ex)
            {

            }
        }

        return null;
    }

    public static Class forName(String classAsString)
    {
        try
        {
            return forNameWithExceptions(classAsString);
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    public static Class forNameWithExceptions(String classAsString) throws Exception
    {
        return Class.forName(classAsString);
    }

    public static Method[] getClassMethods(Class klass, String methodName)
    {
        Method[] methods = klass.getMethods();

        if (methods.length == 0)
        {
            return methods;
        }

        ArrayList arrayList = new ArrayList();

        for (int i = 0; i < methods.length; i++)
        {
            if (methods[i].getName().equals(methodName))
            {
                arrayList.add(methods[i]);
            }
        }

        methods = new Method[arrayList.size()];

        return (Method[]) arrayList.toArray(methods);
    }

    /**
     *  gets the object's full class name
     *
     */
    public static String getClassName(Object object)
    {
        Class klass = object.getClass();

        if (klass.isArray())
        {
            String name = klass.getName();
            if (name.length() > 1)
            {
                int i;

                for (i = 0; i < name.length();)
                {
                    if (name.charAt(i) != '[')
                    {
                        break;
                    }

                    i++;
                }

                String brackets = StringHelper.copies("[]", i);

                StringBuilder result = new StringBuilder(brackets.length()+7);
                switch (name.charAt(i))
                {
                    case 'B': return result.append("byte"   ).append(brackets).toString();
                    case 'C': return result.append("char"   ).append(brackets).toString();
                    case 'D': return result.append("double" ).append(brackets).toString();
                    case 'F': return result.append("float"  ).append(brackets).toString();
                    case 'I': return result.append("int"    ).append(brackets).toString();
                    case 'J': return result.append("long"   ).append(brackets).toString();
                    case 'S': return result.append("short"  ).append(brackets).toString();
                    case 'Z': return result.append("boolean").append(brackets).toString();
                    case 'V': return result.append("void"   ).append(brackets).toString();
                    case 'L': return name.substring(i + 1, name.length() - 1) + brackets;
                }
            }

            return name;
        }

        return klass.getName();
    }

    /**
     *  gets the object's class name portion (ex com.cboe.client.util.ClassHelper will return ClassHelper)
     *
     */
    public static String getClassNameFinalPortion(Class klass)
    {
        String s = klass.getName();

        s = s.substring(s.lastIndexOf('.') + 1);

        if (s.indexOf('$') >= 0)
        {
            return s.substring(s.lastIndexOf('$') + 1);
        }

        return s;
    }

    public static String getClassNameFinalPortion(Object object)
    {
        return getClassNameFinalPortion(object.getClass());
    }

    public static Object loadClass(String classAsString)
    {
        try
        {
            return loadClassWithExceptions(classAsString);
        }
        catch (Exception ex)
        {
            Log.exception("ClassHelper.loadClass(" + classAsString + ")", ex);
        }

        return null;
    }

    public static Object loadClass(String classAsString, Object[] classParameters)
    {
        if (classParameters == null)
        {
            return loadClass(classAsString);
        }

        try
        {
            Class claz = Class.forName(classAsString);

            Class parametersTypes[] = new Class[classParameters.length];

            for (int i = 0; i < classParameters.length; i++)
            {
                parametersTypes[i] = classParameters[i].getClass();
            }

            return claz.getConstructor(parametersTypes).newInstance(classParameters);
        }
        catch (Exception ex)
        {
            Log.exception("ClassHelper.loadClass(" + classAsString + ")", ex);
        }

        return null;
    }

    public static Object loadClassWithExceptions(String classAsString) throws Exception
    {
        return Class.forName(classAsString).newInstance();
    }
/*
    public static void main(String[] args)
    {
        int[]       a = new int[10];
        Integer[]   b = new Integer[] {Integer.valueOf(1)};
        Object      o = new Object();
        String      s = "s";
        char[][][]  c = new char[1][1][1];

        System.out.println("a=" + getClassName(a));
        System.out.println("b=" + getClassName(b));
        System.out.println("o=" + getClassName(o));
        System.out.println("s=" + getClassName(s));
        System.out.println("c=" + getClassName(c));
    }
*/
}
