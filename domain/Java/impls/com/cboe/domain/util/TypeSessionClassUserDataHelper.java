package com.cboe.domain.util;
import java.text.ParseException;

/**
 * This class provides functions to encode and decode type/trading session/class value triplets stored
 * in InstrumentorUserData. 
 */ 
public final class TypeSessionClassUserDataHelper
{
    public static final char typeSessionClassDelimiter = ':';

    /**
     * Encode the type, session, and class triplet contained in this container into a string.
     * @param typeSessionClass
     * @return a String in the format <type>:<sessionName>:<classKey>
     * @throws IllegalArgumentException if session name is null or empty
     */ 
    public static String encode(TypeSessionClassContainer typeSessionClass)
    {
        if(typeSessionClass == null)
        {
            throw new IllegalArgumentException("Cannot encode null session key pair");
        }
        
        short type = typeSessionClass.getType();
        String sessionName = typeSessionClass.getSessionName().trim();
        int classKey = typeSessionClass.getClassKey();
        
        if(sessionName.length() == 0)
        {
            throw new IllegalArgumentException("Cannot encode null session name");
        }
        
        StringBuilder buffer = new StringBuilder(30);
        buffer.append(type);
        buffer.append(typeSessionClassDelimiter);
        buffer.append(sessionName);
        buffer.append(typeSessionClassDelimiter);
        buffer.append(classKey);
        return buffer.toString();
    }
    
    /**
     * Decode this string containing a type/session/classKey triplet (format: <type>:<sessionName>:<classKey>) into
     * a TypeSessionClassContainer object.
     * @param typeSessionClassString
     * @return
     * @throws IllegalArgumentException if a null or empty String is passed 
     * @throws java.text.ParseException if the delimiter ':' character is not found, or if the
     *         value to the right of the delimiter cannot be parsed as an integer.
     */ 
    public static TypeSessionClassContainer decode(String typeSessionClassString) throws ParseException
    {
        return decode(typeSessionClassString, 0);
    }
    
    private static TypeSessionClassContainer decode(String typeSessionClassString, int field) throws ParseException
    {
        if(typeSessionClassString == null)
        {
            throw new IllegalArgumentException("Cannot decode null type/session/classKey triplet at field: " + field);
        }
        
        typeSessionClassString = typeSessionClassString.trim();
        if(typeSessionClassString.length() == 0)
        {
            throw new IllegalArgumentException("Cannot decode empty type/session/classKey triplet at field: " + field);
        }
        
        String[] tokens = typeSessionClassString.split(":");
        if(tokens.length != 3)
        {
            throw new ParseException("Type/session/classKey fields cannot be parsed.", field);
        }
        
        String typeString = tokens[0].trim();
        String sessionName = tokens[1].trim();
        String classKeyString = tokens[2].trim();
        
        short type = 0;
        try
        {
            type = Short.parseShort(typeString);
        }
        catch(NumberFormatException e)
        {
            throw new ParseException("Cannot parse type.", field);
        }
        
        int classKey = 0;
        try
        {
            classKey = Integer.parseInt(classKeyString);
        }
        catch(NumberFormatException e)
        {
            throw new ParseException("Cannot parse class key.", field);
        }
        
        return new TypeSessionClassContainer(type, sessionName, classKey);
    }
    
    /**
     * Encode an array of type/session/classKey triplets to an array of strings.  The resulting String array
     * contains a parallel array of strings with the type/session/classKey pair encoded into String values.
     * 
     * @param typeSessionClasses
     * @return An array of strings containing the corresponding type/session/classKey triplet from the input array.
     * @throws IllegalArgumentException if the input array is null or a value in the input array is null.
     */ 
    public static String[] encode(TypeSessionClassContainer[] typeSessionClasses)
    {
        if(typeSessionClasses == null)
        {
            throw new IllegalArgumentException("Cannot encode from null array.");
        }
        
        String[] result = new String[typeSessionClasses.length];
        for(int i = 0; i < typeSessionClasses.length; i++)
        {
            result[i] = encode(typeSessionClasses[i]);
        }
        
        return result;
    }
    
    /**
     * Decode an array of type/session/classKey triplets into an array of TypeSessionClassContainer objects.
     * The resulting TypeSessionClassContainer array values are in parallel to the input array.
     * 
     * @param typeSessionClassStrings
     * @return An array of TypeSessionClassContainer objects containing the corresponding type/session/classKey triplets from
     *         the input String.
     * @throws java.text.ParseException if an error occurs in parsing a type/session/classKey triplet.
     * @throws IllegalArgumentException if the input array is null.
     */ 
    public static TypeSessionClassContainer[] decode(String[] typeSessionClassStrings) throws ParseException
    {
        if(typeSessionClassStrings == null)
        {
            throw new IllegalArgumentException("Cannod deocde from null array.");
        }
        
        TypeSessionClassContainer[] result = new TypeSessionClassContainer[typeSessionClassStrings.length];
        
        for(int i = 0; i < typeSessionClassStrings.length; i++)
        {
            result[i] = decode(typeSessionClassStrings[i], i);
        }
        
        return result;
    }
}
