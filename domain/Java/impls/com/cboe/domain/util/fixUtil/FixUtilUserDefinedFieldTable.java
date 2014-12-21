/**
 * Author: beniwalv
 * Date: Mar 5, 2003
 * Time: 6:56:59 PM
 */
package com.cboe.domain.util.fixUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Permits reading and writing of Appia user defined fields.
 *
 * Parses and stores user defined fields provided on a Appia FIX message object.
 * Class is constructed from a string of user defined fields that comply with the
 * Appia formatting rules. Each user defined field is separated with a "|".
 * The tag number and the value of the field are separated with the "=".
 *
 * Permits setting of values into hash table - provides a method to iterate hashtable
 * and create a new user defined field string.
 *
 * If only a single user-defined field is to be inserted, use the static method toUDFString
 * rather than constructing an instance of this class. 
 * If no fields are needed then do not use this class.<p>
 * This class is not thread-safe.
 *
 * <br><br>
 * Copyright © 1999 by the Chicago Board Options Exchange ("CBOE"), as an unpublished work.
 * The information contained in this software program constitutes confidential and/or trade
 * secret information belonging to CBOE. This software program is made available to
 * CBOE members and member firms to enable them to develop software applications using
 * the CBOE Market Interface (CMi), and its use is subject to the terms and conditions
 * of a Software License Agreement that governs its use. This document is provided "AS IS"
 * with all faults and without warranty of any kind, either express or implied.
 *
 * @author Jim Northey
 */
public class FixUtilUserDefinedFieldTable
{
    private static final char   FIELD_CHAR      = '|';
    private static final String FIELD_DELIMITER = "|";
    private static final char   TAG_CHAR      = '=';
    private static final String TAG_DELIMITER = "=";
    String udf; // User defined field string
    HashMap udfTable;
    boolean tableChanged = false;

    /**
     * Default constructor in case user wants to dynamically build
     * the user defined string. Create an empty udf table.
     */
    public FixUtilUserDefinedFieldTable()
    {
        udfTable = new HashMap(5);
    }

    /**
     * Constructor that builds a user defined field table from a
     * user defined field string
     *
     * @param aUdf user defined value string
     */
    public FixUtilUserDefinedFieldTable(String aUdf)
    {
        udfTable = new HashMap(5);
        setUDFString(aUdf);
    }

    /**
     * Return a value associated with a field tag value.
     * Returns an empty string if not found.
     *
     * @param tag tag value of field for which a value is requested
     */
    public String getValue(String tag)
    {
        Object val = udfTable.get(tag);

        if (val == null)
        {
            return "";
        }
        else{
            return (String)val;
        }
    }

    /**
     * Return a value associated with a field tag value.
     * Returns an empty string if not found.
     *
     * @param tag - The tag value of the field for which a value is requested.
     */
    public String getValue(int tag)
    {
        return getValue(Integer.toString(tag));
    }

    /**
     * Set a value into the user defined table. Useful when building a
     * user defined field string dynamically
     *
     * @param tag tag of field
     * @param value value of field
     */
    public void setValue(String tag, String value)
    {
        tableChanged = true;
        udfTable.put(tag,value);
    }

    /**
     * Set a value into the user defined table. Useful when building a
     * user defined field string dynamically
     *
     * @param tag - tag of field
     * @param value - value of field
     */
    public void setValue(int tag, String value)
    {
        setValue(Integer.toString(tag), value);
    }

    /**
     * Write udfTable to the udf string
     */
    public void writeUdfTableToString()
    {
        StringBuilder newUdf = new StringBuilder();
        Set entries = udfTable.entrySet();
        Iterator iter = entries.iterator();
        int count = udfTable.size();
        
        for (int i=0; iter.hasNext(); i++ ) {
            Map.Entry entry = (Map.Entry)iter.next();
            String tag = entry.getKey().toString();
            String value = entry.getValue().toString();

            if (i < count - 1)
            {
                appendUDFString(newUdf, tag, value, true);
            }
            else
            {
                appendUDFString(newUdf, tag, value, false);
                break;
            }
        }

        udf = newUdf.toString();
        tableChanged = false;
    }

    /**
    * Return the udf string after updates have been applied from the
    * hash table
    */
    public String getUDFString(){
        if(tableChanged) {
            writeUdfTableToString();
        }

        return udf;
    }

    /**
     * Return the udf string as it exists without performing an update
     * from the udf table.
     */
    public String getUDFStringNoUpdate(){
        return udf;
    }

    /**
     * Provide access to the number of elements in the table
     */
    public int size() {
        return udfTable.size();
    }

    /**
     *  Set the UDF string and parse it into the table
     */
    public void setUDFString(String aUdf){
        udf = aUdf; 
        parseIt();
        // parseItOld();
    }

    /**
     * Parse the udf string into a udf table. Called each time a new
     * udf string is provided.
     */
    protected void parseIt()
    {
       
        udfTable.clear();
        int tstart=0;
        int vstart=0;
        int vend=0;
        String tag=null;String value=null;
        for (int i=0; i < udf.length()-1; i++) {
            char c = udf.charAt(i);
            if (c == TAG_CHAR) {
                tag = udf.substring(tstart,i);
                vstart=i+1;
            } else if (c == FIELD_CHAR) {
                value=udf.substring(vstart,i);
                udfTable.put(tag,value);
                tstart=i+1;
            }
        }
        if (udf.length()>0) { 
            vend = udf.length()-1;
            if ( udf.charAt(vend) != FIELD_CHAR) {
                vend++;
            }
            value=udf.substring(vstart, vend);
            udfTable.put(tag,value);
        }
        return;
    }

    /**
    protected void parseItOld()
    {
        udfTable = new HashMap();
        StringTokenizer parser = new StringTokenizer(udf, FIELD_DELIMITER);
        StringTokenizer field;

        while(parser.hasMoreTokens())
        {
            field = new StringTokenizer(parser.nextToken(), TAG_DELIMITER);
            int numTokens = field.countTokens();

            switch (numTokens)
            {
                case 1: // tag only - Support blank values
                    udfTable.put(field.nextToken(),new String());
                    break;
                case 2: // tag and value
                    udfTable.put(field.nextToken(),field.nextToken());
                    break;
                default: // If no tag and value - do nothing
            }
        }
    }
    **/
    
    /**
     * Use this method to create a UserDefined String for a single field 
     * in the format specified by Javelin.
     * Since it is a static method, no object construction is required.
     * @param tag the FIX tag as a String
     * @param value the value of the FIX field as a String
     * @return a String that can be used to set the UserDefined field in an Appia
     * MessageObject.
     */
    public static String toUDFString(String tag, String value) {
        StringBuilder sb = new StringBuilder(tag);
        sb.append(TAG_DELIMITER);
        sb.append(value);
        return sb.toString();
    } 
    
    /**
     * Use this method to create a UserDefined String for a single field 
     * in the format specified by Javelin.
     * Since it is a static method, no object construction is required.
     * @param tag the FIX tag as an integer
     * @param value the value of the FIX field as a String
     * @return a String that can be used to set the UserDefined field in an Appia
     * MessageObject.
     */
    public static String toUDFString(int tag, String value) {
        StringBuilder sb = new StringBuilder();
        sb.append(tag);
        sb.append(TAG_DELIMITER);
        sb.append(value);
        return sb.toString();
    } 
        
    /**
     * Append a FIX field to a StringBuilder in the format specified by Javelin
     * @param sb buffer to append to
     * @param tag the FIX tag as a String
     * @param value the value of the FIX field as a String
     * @param more tells whether more fields follow this one, which controls
     * whether a field delimiter is appended.
     */
    protected void appendUDFString(StringBuilder sb, String tag, String value,
     boolean more) {
        sb.append(tag);
        sb.append(TAG_DELIMITER);
        sb.append(value);  
        if (more) {
            sb.append(FIELD_DELIMITER);
        }     
    }
}
