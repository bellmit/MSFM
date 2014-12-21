//
// -----------------------------------------------------------------------------------
// Source file: VersionNumberComparator.java
//
// PACKAGE: com.cboe.util.version
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.util.version;

import java.util.*;

/*
 * Providesa comparator interface for comparing version numbers.
 */
public class VersionNumberComparator implements Comparator, VersionConstants
{
    /**
     * Default constructor
     */
    public VersionNumberComparator()
    {
        super();
    }

    /**
     * Allows processes to directly run this class for testing.
     */
    public static void main(String[] args)
    {
        VersionNumberComparator comp = new VersionNumberComparator();
        System.out.println("isAllDigits(\"17d\")=" + comp.isAllDigits("17d"));
        System.out.println("isAllDigits(\"17\")=" + comp.isAllDigits("17"));
        System.out.print("splitBetweenDigitsAndChars(\"17bd8f\")=");
        String[] tokens = comp.splitBetweenDigitsAndChars("17bd8f");
        for(int i = 0; i < tokens.length; i++)
        {
            System.out.print(tokens[i]);
            System.out.print(";");
        }
        System.out.println();

        StringTokenizer testString = new StringTokenizer(args[0], ";");

        while(testString.hasMoreTokens())
        {
            String token1 = testString.nextToken();
            String token2 = testString.nextToken();
            int result = comp.compare(token1, token2);

            System.out.print("\"" + token1 + "\" is ");
            if(result < 0)
            {
                System.out.print("less than ");
            }
            else if(result > 0)
            {
                System.out.print("greater than ");
            }
            else
            {
                System.out.print("equal to ");
            }
            System.out.println("\"" + token2 + "\"");
        }

        System.exit(0);
    }

    /**
     * Compares version numbers logically. Returns a negative integer,
     * zero, or a positive integer as the first argument is less than, equal
     * to, or greater than the second.
     * @param version1 must be a String
     * @param version2 must be a String
     * @return a negative integer, zero, or a positive integer as the
     * 	       first argument is less than, equal to, or greater than the
     *	       second.
     * @throws ClassCastException if the arguments' types prevent them from
     * 	       being compared by this Comparator.
     */
    public int compare(Object version1, Object version2)
    {
        int result = 0;

        if(version1 != null && version2 != null)
        {
            if(!version1.equals(version2))
            {
                if(version1 instanceof String && version2 instanceof String)
                {
                    StringTokenizer ver1Tokenizer = new StringTokenizer((String)version1, VERSION_SEGMENT_SEPARATOR, false);
                    StringTokenizer ver2Tokenizer = new StringTokenizer((String)version2, VERSION_SEGMENT_SEPARATOR, false);

                    while(ver1Tokenizer.hasMoreTokens() && ver2Tokenizer.hasMoreTokens() && result == 0)
                    {
                        String token1 = ver1Tokenizer.nextToken();
                        String token2 = ver2Tokenizer.nextToken();

                        result = compareCompoundTokens(token1, token2);
                    }

                    //they have compared up to this point.
                    if(result == 0)
                    {
                        if(ver1Tokenizer.hasMoreTokens())
                        {
                            //got here from break in while loop
                            //if version1 still have a token and version2 does not
                            //then version1 is greater than version2
                            result = 1;
                        }
                        else if(ver2Tokenizer.hasMoreTokens())
                        {
                            //if version1 does not have anymore tokens, and version2 does have more tokens, and
                            //they were still equal, then version1 is less than version2
                            result = -1;
                        }
                    }
                }
                else
                {
                    throw new ClassCastException("version1 and version2 must both be of type String.");
                }
            }
        }
        else
        {
            throw new IllegalArgumentException("version1 and version2 must not be null.");
        }

        return result;
    }

    /**
     * Compares two individual compound tokens. A compound token may contain both digits and letters and
     * will need to be split up into segments segregating the digits from the letters
     * @param token1 to compare
     * @param token2 to compare
     * @return same rules as compare(Object,Object) method
     */
    private int compareCompoundTokens(String token1, String token2)
    {
        int result = 0;

        if(token1 != null && token2 != null)
        {
            if(!token1.equals(token2))
            {
                if(isAllDigits(token1) && isAllDigits(token2))
                {
                    result = compareSimpleTokens(token1, token2);
                }
                else if(isAllDigits(token1) && !isAllDigits(token2))
                {
                    String[] token2tokens = splitBetweenDigitsAndChars(token2);

                    if(isAllDigits(token2tokens[0]))
                    {
                        result = compareSimpleTokens(token1, token2tokens[0]);
                        if(result == 0)
                        {
                            result = -1;
                        }
                    }
                    else
                    {
                        result = 1;
                    }
                }
                else if(!isAllDigits(token1) && isAllDigits(token2))
                {
                    String[] token1tokens = splitBetweenDigitsAndChars(token1);

                    if(isAllDigits(token1tokens[0]))
                    {
                        result = compareSimpleTokens(token1tokens[0], token2);
                        if(result == 0)
                        {
                            result = 1;
                        }
                    }
                    else
                    {
                        result = -1;
                    }
                }
                else if(!isAllDigits(token1) && !isAllDigits(token2))
                {
                    String[] token1tokens = splitBetweenDigitsAndChars(token1);
                    String[] token2tokens = splitBetweenDigitsAndChars(token2);

                    int i = 0;
                    while(i < token1tokens.length && i < token2tokens.length && result == 0)
                    {
                        String token1token = token1tokens[i];
                        String token2token = token2tokens[i];

                        result = compareSimpleTokens(token1token, token2token);
                        i++;
                    }

                    //they have compared up to this point.
                    if(result == 0)
                    {
                        if(i < token1tokens.length)
                        {
                            //if token1tokens still have a token and token2tokens does not
                            //then token1tokens is greater than token2tokens
                            result = 1;
                        }
                        else if(i < token2tokens.length)
                        {
                            //if token1tokens does not have anymore tokens, and token2tokens does have more tokens, and
                            //they were still equal, then token1tokens is less than token2tokens
                            result = -1;
                        }
                    }
                }
            }
        }
        else
        {
            throw new IllegalArgumentException("token1 and token2 must not be null.");
        }

        return result;
    }

    /**
     * Compares two individual simple tokens. A simple token may contain either digits or letters, but not
     * a combination of both.
     * @param token1 to compare
     * @param token2 to compare
     * @return same rules as compare(Object,Object) method
     */
    private int compareSimpleTokens(String token1, String token2)
    {
        int result = 0;

        if(token1 != null && token2 != null)
        {
            if(!token1.equals(token2))
            {
                String[] token1tokens = splitBetweenDigitsAndChars(token1);
                String[] token2tokens = splitBetweenDigitsAndChars(token2);

                if(token1tokens.length > 1)
                {
                    throw new IllegalArgumentException("token1 should not be a compound token:" + token2);
                }
                else if(token2tokens.length > 1)
                {
                    throw new IllegalArgumentException("token2 should not be a compound token:" + token2);
                }

                if(isAllDigits(token1) && isAllDigits(token2))
                {
                    Integer compare1 = Integer.valueOf(token1);
                    Integer compare2 = Integer.valueOf(token2);
                    result = compare1.compareTo(compare2);
                }
                else if(isAllDigits(token1) && !isAllDigits(token2))
                {
                    result = 1;
                }
                else if(!isAllDigits(token1) && isAllDigits(token2))
                {
                    result = -1;
                }
                else if(!isAllDigits(token1) && !isAllDigits(token2))
                {
                    result = token1.compareTo(token2);
                }
            }
        }
        else
        {
            throw new IllegalArgumentException("token1 and token2 must not be null.");
        }

        return result;
    }

    /**
     * Split the String into an array where each element is a substring that is either characters or a
     * continuous substring of digits. For example: 17bc856ed would be split into elements such as 17;bc;856;ed
     * @param token to split
     * @return each element is a String that is either all letters or all numbers
     */
    private String[] splitBetweenDigitsAndChars(String token)
    {
        String[] tokens = new String[0];

        if(token != null && token.length() > 0)
        {
            ArrayList tokensArray = new ArrayList(5);

            StringBuilder tokenBuffer = new StringBuilder(token.length());
            boolean parsingDigits = false;
            char[] chars = new char[token.length()];
            token.getChars(0, token.length(), chars, 0);

            for(int i = 0; i < chars.length; i++)
            {
                if(Character.isDigit(chars[i]))
                {
                    if(!parsingDigits)
                    {
                        if(tokenBuffer.length() > 0)
                        {
                            tokensArray.add(tokenBuffer.toString());
                            tokenBuffer.delete(0, tokenBuffer.length());
                        }
                    }
                    parsingDigits = true;
                }
                else
                {
                    if(parsingDigits)
                    {
                        if(tokenBuffer.length() > 0)
                        {
                            tokensArray.add(tokenBuffer.toString());
                            tokenBuffer.delete(0, tokenBuffer.length());
                        }
                    }
                    parsingDigits = false;
                }

                tokenBuffer.append(chars[i]);
            }

            if(tokenBuffer.length() > 0)
            {
                tokensArray.add(tokenBuffer.toString());
            }

            tokens = (String[])tokensArray.toArray(tokens);
        }
        return tokens;
    }

    /**
     * Determines if the String contains all digits
     * @param token to check
     * @return true if all numeric digits, false if mixed or otherwise
     */
    private boolean isAllDigits(String token)
    {
        boolean isAllDigits = true;

        if(token != null && token.length() > 0)
        {
            char[] chars = new char[token.length()];
            token.getChars(0, token.length(), chars, 0);

            for(int i = 0; i < chars.length; i++)
            {
                if(!Character.isDigit(chars[i]))
                {
                    isAllDigits = false;
                    break;
                }
            }
        }
        else
        {
            isAllDigits = false;
        }

        return isAllDigits;
    }
}
