package com.cboe.domain.util;

import java.text.ParseException;

/**
 * This class provides functions to encode and decode trading session/key value pairs stored
 * in InstrumentorUserData. 
 */ 
public final class SessionKeyUserDataHelper
{
    public static final char sessionKeyDelimiter = ':';

    /**
     * Encode the session and key pair contained in this container into a string.
     * @param sessionKey
     * @return a String in the format <sessionName>:<key>
     * @throws IllegalArgumentException if session name is null or empty
     */ 
    public static String encode(SessionKeyContainer sessionKey)
    {
        if(sessionKey == null)
        {
            throw new IllegalArgumentException("Cannot encode null session key pair");
        }
        
        String sessionName = sessionKey.getSessionName().trim();
        int key = sessionKey.getKey();
        
        if(sessionName.length() == 0)
        {
            throw new IllegalArgumentException("Cannot encode null session name");
        }
        
        StringBuilder buffer = new StringBuilder(15);
        buffer.append(sessionName)
        .append(sessionKeyDelimiter)
        .append(key);
        return buffer.toString();
    }
    
    /**
     * Decode this string containing a session/key pair (format: <sessionName>:<key>) into
     * a SessionKeyContainer object.
     * @param sessionKeyPair
     * @return
     * @throws IllegalArgumentException if a null or empty String is passed 
     * @throws ParseException if the delimiter ':' character is not found, or if the
     *         value to the right of the delimiter cannot be parsed as an integer.
     */ 
    public static SessionKeyContainer decode(String sessionKeyPair) throws ParseException
    {
        return decode(sessionKeyPair, 0);
    }
    
    private static SessionKeyContainer decode(String sessionKeyPair, int field) throws ParseException
    {
        if(sessionKeyPair == null)
        {
            throw new IllegalArgumentException("Cannot decode null session/key pair at field: " + field);
        }
        
        sessionKeyPair = sessionKeyPair.trim();
        if(sessionKeyPair.length() == 0)
        {
            throw new IllegalArgumentException("Cannot decode empty session/key pair at field: " + field);
        }
        
        int splitIndex = sessionKeyPair.indexOf(sessionKeyDelimiter);
        
        if(splitIndex == -1)
        {
            throw new ParseException("No session/key delimiter character found at field: ", field);
        }
        
        String sessionName = (sessionKeyPair.substring(0, splitIndex)).trim();
        String keyString = (sessionKeyPair.substring(splitIndex + 1, sessionKeyPair.length())).trim();
        
        int key;
        try
        {
            key = Integer.parseInt(keyString);
        }
        catch(NumberFormatException e)
        {
            throw new ParseException("Integer string to the right of session/key delimiter is invalid; field: " + field, field);
        }
        
        SessionKeyContainer sessionKey = new SessionKeyContainer(sessionName, key);
        
        return sessionKey;
    }
    
    /**
     * Encode an array of session/key pairs to an array of strings.  The resulting String array
     * contains a parallel array of strings with the session/key pair encoded into String values.
     * 
     * @param sessionKeys
     * @return An array of strings containing the corresponding session/key pair from the input array.
     * @throws IllegalArgumentException if the input array is null or a value in the input array is null.
     */ 
    public static String[] encode(SessionKeyContainer[] sessionKeys)
    {
        if(sessionKeys == null)
        {
            throw new IllegalArgumentException("Cannot encode from null array.");
        }
        
        String[] result = new String[sessionKeys.length];
        for(int i = 0; i < sessionKeys.length; i++)
        {
            result[i] = encode(sessionKeys[i]);
        }
        
        return result;
    }
    
    /**
     * Decode an array of session key pairs into an array of SessionKeyContainer objects.
     * The resulting SessionKeyContainer array values are in parallel to the input array.
     * 
     * @param sessionKeyPairs
     * @return An array of SessionKeyContainer objects containing the corresponding session/key pairs from
     *         the input String.
     * @throws ParseException if an error occurs in parsing a session/key pair.
     * @throws IllegalArgumentException if the input array is null.
     */ 
    public static SessionKeyContainer[] decode(String[] sessionKeyPairs) throws ParseException
    {
        if(sessionKeyPairs == null)
        {
            throw new IllegalArgumentException("Cannod deocde from null array.");
        }
        
        SessionKeyContainer[] result = new SessionKeyContainer[sessionKeyPairs.length];
        
        for(int i = 0; i < sessionKeyPairs.length; i++)
        {
            result[i] = decode(sessionKeyPairs[i], i);
        }
        
        return result;
    }
}
