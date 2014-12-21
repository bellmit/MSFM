package com.cboe.test.application.cmiScript;

import java.util.ArrayList;

public class CommandLine
{
    private enum State
    {
        SPACE, STRING, STRINGQUOTE, WORD
    }

    public static String[] parse(String inputString)
    {
        // Force input[] to end with a space so that we don't need
        // special end-of-line handling.
        StringBuilder token = new StringBuilder(inputString.length()+1);
        token.append(inputString).append(' ');
        char input[] = token.toString().toCharArray();
        State state = State.SPACE;

        ArrayList<String> tokens = new ArrayList<String>();

        // Parse input into words
        for (char c : input)
        {
            switch (state)
            {
            case SPACE:
                if (c == '"')
                {
                    // Starting a string.
                    state = State.STRING;
                    token.setLength(0);
                }
                else if (c != ' ')
                {
                    // Starting a non-string token.
                    state = State.WORD;
                    token.setLength(0);
                    token.append(c);
                }
                // else more space which we'll ignore
                break;

            case STRING:
                if (c == '"')
                {
                    // End of the string. Save this token.
                    tokens.add(token.toString());
                    state = State.SPACE;
                }
                else if (c == '\\')
                {
                    // Next character is taken literally
                    state = State.STRINGQUOTE;
                }
                else
                {
                    // Part of string, accumulate it
                    token.append(c);
                }
                break;

            case STRINGQUOTE:
                // Accumulate this character, regardless of what it is
                token.append(c);
                state = State.STRING;   // resume normal string processing
                break;

            case WORD:
                if (c == ' ')
                {
                    // End of the word. Save this token.
                    tokens.add(token.toString());
                    state = State.SPACE;
                }
                else
                {
                    // Next character in the word.
                    token.append(c);
                }
            } // switch state
        } // for i
        return tokens.toArray(new String[tokens.size()]);
    } // parse

    /** Interpret a String into a boolean value.
     * @param s String to interpret.
     * @return true if s starts with t, T, y, Y or 1.
     */
    public static boolean booleanValue(String s)
    {
        char c = Character.toLowerCase(s.charAt(0));
        return c == 't' || c == 'y' || c == '1';
    }

}
