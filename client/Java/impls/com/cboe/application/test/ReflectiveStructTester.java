package com.cboe.application.test;

import java.lang.reflect.*;
import java.io.*;
//import com.cboe.util.*;

public class ReflectiveStructTester
{
/**
 *  private class constructor.  The class is intended to be used on the static methods
 */
private ReflectiveStructTester()
{
}
/**
 * Validates the null values in a given structure's public fields (recursing references to other structures).
 *
 * @author Connie Feng
 * @return boolean indicating if the struct contains a null (uninitialized) value.
 * @param struct the struct to be tested
 */
public static boolean testNullStruct(Object struct)
{
    try
    {
        if ( struct == null )
        {
            // don't bother
            System.out.println("the object to be tested is null: " + struct.getClass().getName());
            return true;
        }

        Class type = struct.getClass();

        if ( type.isArray() )
        {
            Object array = struct;
            int len = Array.getLength(array);

            for ( int i = 0; i < len; i++)
            {
                try
                {
                    if (true == testNullStruct(Array.get(array, i)))
                    {
                        return true;
                    }
                }
                catch(NullPointerException ex)
                {
                  System.out.println("struct is null: " + array.getClass().getName() + " element: " + i );
                  return true;
                }

                continue;
            }
        }
        else
        {
            Field[] fields = struct.getClass().getFields();
            for (int i=0; i < fields.length; ++i)
            {
                type = fields[i].getType();
                if (type.isPrimitive())
                {
                    continue;
                }
                else if (type.isArray())
                {
                    try
                    {
                       if( !testNullStruct(fields[i].get(struct)))
                        {
                          continue;
                        }
                    }
                    catch(NullPointerException ex)
                    {
                      System.out.println("field is null" + fields[i].getName());
                      return true;
                    }


                }
                else if (String.class == type)
                {
                    try
                    {
                        if(fields[i].get(struct) == null)
                        {
                            System.out.println("field is null : " + fields[i].getName());
                            return true;
                        }
                    }
                    catch(NullPointerException ex)
                    {
                      System.out.println("field is null: " + fields[i].getName());
                      return true;
                    }
                    continue;
                }
                else    // other user defined struct type
                {
                    try
                    {
                        if (testNullStruct(fields[i].get(struct)) == true )
                        {
                            return true;
                        }
                    }
                    catch(NullPointerException ex)
                    {
                      System.out.println("field is null: " + fields[i].getName());
                      return true;
                    }
                continue;
                }
            }
        }
        return false;
    }
    catch (IllegalAccessException ex)
    {
        throw new IllegalArgumentException("Class " + struct.getClass().getName() + " doesn't seem to be an IDL-generated struct.");
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
 *                  though only it's public fields will be printed.
 *  @param prefix The prefix string to write before all output lines generated by this method. (usually the
 *      instance name of the struct being printed)
 *  @param writer The writer to write the structure's description to.
 *  @exeption java.io.IOException thrown if there's a problem using <code>writer</code>.
 */
public static void printStruct(Object struct, String prefix, Writer writer)
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
            return; // finished
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
            return;     // finished.
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
