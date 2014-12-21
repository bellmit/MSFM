package com.cboe.util;

/**
 * Some useful math functions that are not included in java.lang.Math.
 *
 * @author John Wickberg
 */
public class MathExtensions {

    /**
     * Deternines if a number is prime.
     *
     * @param value value to be checked
     * @result true if number is prime
     */
    public static boolean isPrime(int value) {
        int checkValue = Math.abs(value); // sign doesn't matter
        boolean result;
        if (checkValue > 3) {
            boolean divisorFound = (checkValue % 2) == 0;
            int i = 3;
            while (!divisorFound && i * i <= checkValue) {
                divisorFound = (checkValue % i) == 0;
                i += 2;
            }
            result = !divisorFound;
        }
        else {
            result = true;
        }
        return result;
    }

    /**
     * Finds next prime given a starting value.
     *
     * @param startValue number to start at
     * @return prime greater than starting value
     * @exception IllegalArgumentException if startValue is less than 0
     */
    public static int nextPrime(int startValue) {
        if (startValue < 0) {
            throw new IllegalArgumentException("Starting value must not be less than 0");
        }
        int nextValue = startValue + 1;
        if (nextValue > 2 && nextValue % 2 == 0) {
            nextValue++;
        }
        while (!isPrime(nextValue)) {
            nextValue += 2;
        }
        return nextValue;
    }
            
}
