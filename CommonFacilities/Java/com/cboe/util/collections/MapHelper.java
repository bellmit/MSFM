package com.cboe.util.collections;

/**
 * MapHelper.java
 *
 * @author Dmitry Volpyansky
 *
 */

public class MapHelper
{
    public static final int BINARY_SEARCH_NOT_FOUND = -1; // has to be -1 for the normalization math to work

// BINARY SEARCH HELPERS

    public static int normalizeBinarySearchPosition(int position)
    {
        if (position < 0)
        {
            return (-position) + BINARY_SEARCH_NOT_FOUND;
        }

        return position;
    }

    public static int binarySearch(long[] array, long key)
    {
        return binarySearch(array, key, array.length);
    }

    public static int binarySearch(long[] array, long key, int length)
    {
	    int high = length - 1;
        if (high < 0)
        {
            return BINARY_SEARCH_NOT_FOUND;
        }

	    int low = 0;
	    int mid;
	    long midVal;

	    while (low <= high)
        {
	        mid    = (low + high) >> 1;
	        midVal = array[mid];

	        if (midVal < key)
            {
		        low = mid + 1;
            }
	        else if (midVal > key)
            {
		        high = mid - 1;
            }
	        else
            {
		        return mid; // key found
            }
	    }

	    return -(low - BINARY_SEARCH_NOT_FOUND);  // key not found (will be a negative number).
    }

    public static int binarySearch(int[] array, int key)
    {
        return binarySearch(array, key, array.length);
    }

    public static int binarySearch(int[] array, int key, int length)
    {
	    int high = length - 1;
        if (high < 0)
        {
            return BINARY_SEARCH_NOT_FOUND;
        }

	    int low = 0;
	    int mid;
	    int midVal;

	    while (low <= high)
        {
	        mid    = (low + high) >> 1;
	        midVal = array[mid];

	        if (midVal < key)
            {
		        low = mid + 1;
            }
	        else if (midVal > key)
            {
		        high = mid - 1;
            }
	        else
            {
		        return mid; // key found
            }
	    }

	    return -(low - BINARY_SEARCH_NOT_FOUND);  // key not found (will be a negative number).
    }

    public static int binarySearch(char[] array, char key)
    {
        return binarySearch(array, key, array.length);
    }

    public static int binarySearch(char[] array, char key, int length)
    {
	    int high = length - 1;
        if (high < 0)
        {
            return BINARY_SEARCH_NOT_FOUND;
        }

	    int low = 0;
	    int mid;
	    char midVal;

	    while (low <= high)
        {
	        mid    = (low + high) >> 1;
	        midVal = array[mid];

	        if (midVal < key)
            {
		        low = mid + 1;
            }
	        else if (midVal > key)
            {
		        high = mid - 1;
            }
	        else
            {
		        return mid; // key found
            }
	    }

	    return -(low - BINARY_SEARCH_NOT_FOUND);  // key not found (will be a negative number).
    }

    public static int binarySearch(byte[] array, byte key)
    {
        return binarySearch(array, key, array.length);
    }

    public static int binarySearch(byte[] array, byte key, int length)
    {
	    int high = length - 1;
        if (high < 0)
        {
            return BINARY_SEARCH_NOT_FOUND;
        }

	    int low = 0;
	    int mid;
	    byte midVal;

	    while (low <= high)
        {
	        mid    = (low + high) >> 1;
	        midVal = array[mid];

	        if (midVal < key)
            {
		        low = mid + 1;
            }
	        else if (midVal > key)
            {
		        high = mid - 1;
            }
	        else
            {
		        return mid; // key found
            }
	    }

	    return -(low - BINARY_SEARCH_NOT_FOUND);  // key not found (will be a negative number).
    }

    public static int binarySearch(Comparable[] array, Comparable key)
    {
        return binarySearch(array, key, array.length);
    }

    public static int binarySearch(Comparable[] array, Comparable key, int length)
    {
	    int high = length - 1;
        if (high < 0)
        {
            return BINARY_SEARCH_NOT_FOUND;
        }

	    int low = 0;
	    int mid;
        int cmp;

	    while (low <= high)
        {
	        mid = (low + high) >> 1;

	        cmp = ((Comparable) array[mid]).compareTo(key);

	        if (cmp < 0)
            {
		        low = mid + 1;
            }
	        else if (cmp > 0)
            {
		        high = mid - 1;
            }
	        else
            {
		        return mid; // key found
            }
	    }

	    return -(low - BINARY_SEARCH_NOT_FOUND);  // key not found (will be a negative number).
    }

// ARRAYCLONE HELPERS

    public static byte[] arrayclone(byte from)
    {
        byte[] to = new byte[1];

        to[0] = from;

        return to;
    }

    public static byte[] arrayclone(byte[] from)
    {
        if (from == null)
        {
            return null;
        }

        return (byte[]) from.clone();
    }

    public static byte[] arrayclone(byte[] from, int toSize)
    {
        if (from == null)
        {
            return null;
        }

        byte[] to = new byte[toSize];
        System.arraycopy(from, 0, to, 0, from.length);
        return to;
    }

    public static byte[] arrayclone(byte[] from, int fromOffset, int fromSize, int toSize)
    {
        if (from == null)
        {
            return null;
        }

        byte[] to = new byte[toSize];
        System.arraycopy(from, fromOffset, to, 0, fromSize);
        return to;
    }

    public static byte[] arraycloneCombine(byte[] from, int startOffset, int endOffset, int toSize)
    {
        if (from == null)
        {
            return null;
        }

        byte[] to = new byte[toSize];
        int firstPortion = from.length - startOffset;
        System.arraycopy(from, startOffset, to, 0,            firstPortion);
        System.arraycopy(from, 0,           to, firstPortion, endOffset);
        return to;
    }

    public static byte[] arraycloneExpandGap(byte[] from, int fromOffset, int fromSize, int toSize, int gapOffset, int gapLength)
    {
        if (from == null)
        {
            return null;
        }

        byte[] to = new byte[toSize];
        int gap = gapOffset + gapLength;
        System.arraycopy(from, fromOffset, to, 0,   gapOffset);
        System.arraycopy(from, gapOffset,  to, gap, fromSize - gapOffset);
        return to;
    }

    public static char[] arrayclone(char from)
    {
        char[] to = new char[1];

        to[0] = from;

        return to;
    }

    public static char[] arrayclone(char[] from)
    {
        if (from == null)
        {
            return null;
        }

        return (char[]) from.clone();
    }

    public static char[] arrayclone(char[] from, int toSize)
    {
        if (from == null)
        {
            return null;
        }

        char[] to = new char[toSize];
        System.arraycopy(from, 0, to, 0, from.length);
        return to;
    }

    public static char[] arrayclone(char[] from, int fromOffset, int fromSize, int toSize)
    {
        if (from == null)
        {
            return null;
        }

        char[] to = new char[toSize];
        System.arraycopy(from, fromOffset, to, 0, fromSize);
        return to;
    }

    public static char[] arraycloneCombine(char[] from, int startOffset, int endOffset, int toSize)
    {
        if (from == null)
        {
            return null;
        }

        char[] to = new char[toSize];
        int firstPortion = from.length - startOffset;
        System.arraycopy(from, startOffset, to, 0,            firstPortion);
        System.arraycopy(from, 0,           to, firstPortion, endOffset);
        return to;
    }

    public static char[] arraycloneExpandGap(char[] from, int fromOffset, int fromSize, int toSize, int gapOffset, int gapLength)
    {
        if (from == null)
        {
            return null;
        }

        char[] to = new char[toSize];
        int gap = gapOffset + gapLength;
        System.arraycopy(from, fromOffset, to, 0,   gapOffset);
        System.arraycopy(from, gapOffset,  to, gap, fromSize - gapOffset);
        return to;
    }

    public static long[] arrayclone(long from)
    {
        long[] to = new long[1];

        to[0] = from;

        return to;
    }

    public static long[] arrayclone(long[] from)
    {
        if (from == null)
        {
            return null;
        }

        return (long[]) from.clone();
    }

    public static long[] arrayclone(long[] from, int toSize)
    {
        if (from == null)
        {
            return null;
        }

        long[] to = new long[toSize];
        System.arraycopy(from, 0, to, 0, from.length);
        return to;
    }

    public static long[] arrayclone(long[] from, int fromOffset, int fromSize, int toSize)
    {
        if (from == null)
        {
            return null;
        }

        long[] to = new long[toSize];
        System.arraycopy(from, fromOffset, to, 0, fromSize);
        return to;
    }

    public static long[] arraycloneCombine(long[] from, int startOffset, int endOffset, int toSize)
    {
        if (from == null)
        {
            return null;
        }

        long[] to = new long[toSize];
        int firstPortion = from.length - startOffset;
        System.arraycopy(from, startOffset, to, 0,            firstPortion);
        System.arraycopy(from, 0,           to, firstPortion, endOffset);
        return to;
    }

    public static long[] arraycloneExpandGap(long[] from, int fromOffset, int fromSize, int toSize, int gapOffset, int gapLength)
    {
        if (from == null)
        {
            return null;
        }

        long[] to = new long[toSize];
        int gap = gapOffset + gapLength;
        System.arraycopy(from, fromOffset, to, 0,   gapOffset);
        System.arraycopy(from, gapOffset,  to, gap, fromSize - gapOffset);
        return to;
    }

    public static int[] arrayclone(int from)
    {
        int[] to = new int[1];

        to[0] = from;

        return to;
    }

    public static int[] arrayclone(int[] from)
    {
        if (from == null)
        {
            return null;
        }

        return (int[]) from.clone();
    }

    public static int[] arrayclone(int[] from, int toSize)
    {
        if (from == null)
        {
            return null;
        }

        int[] to = new int[toSize];
        System.arraycopy(from, 0, to, 0, from.length);
        return to;
    }

    public static int[] arrayclone(int[] from, int fromOffset, int fromSize, int toSize)
    {
        if (from == null)
        {
            return null;
        }

        int[] to = new int[toSize];
        System.arraycopy(from, fromOffset, to, 0, fromSize);
        return to;
    }

    public static int[] arraycloneCombine(int[] from, int startOffset, int endOffset, int toSize)
    {
        if (from == null)
        {
            return null;
        }

        int[] to = new int[toSize];
        int firstPortion = from.length - startOffset;
        System.arraycopy(from, startOffset, to, 0,            firstPortion);
        System.arraycopy(from, 0,           to, firstPortion, endOffset);
        return to;
    }

    public static int[] arraycloneExpandGap(int[] from, int fromOffset, int fromSize, int toSize, int gapOffset, int gapLength)
    {
        if (from == null)
        {
            return null;
        }

        int[] to = new int[toSize];
        int gap = gapOffset + gapLength;
        System.arraycopy(from, fromOffset, to, 0,   gapOffset);
        System.arraycopy(from, gapOffset,  to, gap, fromSize - gapOffset);
        return to;
    }

    public static String[] arrayclone(String from)
    {
        String[] to = new String[1];

        to[0] = from;

        return to;
    }

    public static String[] arrayclone(String[] from)
    {
        if (from == null)
        {
            return null;
        }

        return (String[]) from.clone();
    }

    public static String[] arrayclone(String[] from, int toSize)
    {
        if (from == null)
        {
            return null;
        }

        String[] to = new String[toSize];
        System.arraycopy(from, 0, to, 0, from.length);
        return to;
    }

    public static String[] arrayclone(String[] from, int fromOffset, int fromSize, int toSize)
    {
        if (from == null)
        {
            return null;
        }

        String[] to = new String[toSize];
        System.arraycopy(from, fromOffset, to, 0, fromSize);
        return to;
    }

    public static String[] arraycloneCombine(String[] from, int startOffset, int endOffset, int toSize)
    {
        if (from == null)
        {
            return null;
        }

        String[] to = new String[toSize];
        int firstPortion = from.length - startOffset;
        System.arraycopy(from, startOffset, to, 0,            firstPortion);
        System.arraycopy(from, 0,           to, firstPortion, endOffset);
        return to;
    }

    public static String[] arraycloneExpandGap(String[] from, int fromOffset, int fromSize, int toSize, int gapOffset, int gapLength)
    {
        if (from == null)
        {
            return null;
        }

        String[] to = new String[toSize];
        int gap = gapOffset + gapLength;
        System.arraycopy(from, fromOffset, to, 0,   gapOffset);
        System.arraycopy(from, gapOffset,  to, gap, fromSize - gapOffset);
        return to;
    }

}
