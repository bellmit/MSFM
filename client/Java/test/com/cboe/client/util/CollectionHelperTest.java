package com.cboe.client.util;


import com.cboe.idl.cmiMarketData.BookDepthStruct;
import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
import com.cboe.idl.cmiMarketData.ExpectedOpeningPriceStruct;
import com.cboe.idl.cmiMarketData.NBBOStruct;
import com.cboe.idl.cmiMarketData.RecapStruct;
import com.cboe.idl.cmiMarketData.TickerStruct;
import com.cboe.idl.cmiProduct.ClassStruct;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.cmiSession.SessionClassStruct;
import com.cboe.idl.cmiSession.SessionProductStruct;
import junit.framework.JUnit4TestAdapter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;      // annotation

public class CollectionHelperTest
{
    // Allow this JUnit 4 test to run under JUnit3
    public static junit.framework.Test suite()
    {
        return new JUnit4TestAdapter(CollectionHelperTest.class);
    }

    private final static int sorted6keys[]   = { 5, 22, 39, 56, 73, 90 };
    private final static int unsorted6keys[] = { 22, 90, 5, 73, 39, 56 };
    private final static int sorted7keys[]   = { 5, 22, 39, 56, 73, 90, 107 };
    private final static int unsorted7keys[] = { 22, 90, 5, 107, 73, 39, 56 };
    private final static int missingKeys[] = { 0, 42, 151 };


    private BookDepthStruct bd6sorted[];
    private BookDepthStruct bd7sorted[];
    private BookDepthStruct bd6unsorted[];
    private BookDepthStruct bd7unsorted[];
    private BookDepthStruct bdbd[][];

    private void setupBookDepthStructArrays()
    {
        // Look for search errors: even or odd array size, sorted or unsorted 
        bd6sorted = new BookDepthStruct[6];
        bd7sorted = new BookDepthStruct[7];
        bd6unsorted = new BookDepthStruct[6];
        bd7unsorted = new BookDepthStruct[7];
        for (int i = 0; i < bd7sorted.length; ++i)
        {
            if (i < bd6sorted.length)
            {
                bd6sorted[i] = new BookDepthStruct();
                bd6sorted[i].productKeys = new ProductKeysStruct();
                bd6sorted[i].productKeys.productKey = sorted6keys[i];

                bd6unsorted[i] = new BookDepthStruct();
                bd6unsorted[i].productKeys = new ProductKeysStruct();
                bd6unsorted[i].productKeys.productKey = unsorted6keys[i];
            }
            bd7sorted[i] = new BookDepthStruct();
            bd7sorted[i].productKeys = new ProductKeysStruct();
            bd7sorted[i].productKeys.productKey = sorted7keys[i];

            bd7unsorted[i] = new BookDepthStruct();
            bd7unsorted[i].productKeys = new ProductKeysStruct();
            bd7unsorted[i].productKeys.productKey = unsorted7keys[i];
        }

        // Two dimensional array, total of 7 elements so we can use sorted7keys
        bdbd = new BookDepthStruct[3][];
        bdbd[0] = new BookDepthStruct[2];
        bdbd[1] = new BookDepthStruct[3];
        bdbd[2] = new BookDepthStruct[2];
        int index = 0;
        for (int one = 0; one < bdbd.length; ++one)
        {
            for (int two = 0; two < bdbd[one].length; ++two)
            {
                bdbd[one][two] = new BookDepthStruct();
                bdbd[one][two].productKeys = new ProductKeysStruct();
                bdbd[one][two].productKeys.productKey = sorted7keys[index++];
            }
        }
    }

    private boolean equals(BookDepthStruct a, BookDepthStruct b)
    {
        return a.productKeys.productKey == b.productKeys.productKey;
    }

    @Test public void testBinarySearchBD()
    {
        setupBookDepthStructArrays();
        for (int i = 0; i < bd7sorted.length; ++i)
        {
            String name = "i=" + i;
            if (i < bd6sorted.length)
            {
                assertEquals(name, i, CollectionHelper
                        .binarySearch(bd6sorted, sorted7keys[i]));
            }
            assertEquals(name, i, CollectionHelper
                    .binarySearch(bd7sorted, sorted7keys[i]));
        }

        for (int key : missingKeys)
        {
            String name = "key=" + key;
            assertTrue(name, 0 > CollectionHelper.binarySearch(bd6sorted, key));
            assertTrue(name, 0 > CollectionHelper.binarySearch(bd7sorted, key));
        }

        for (int length = 1; length <= bd7sorted.length; ++length)
        {
            String name1 = "length=" + length;
            for (int i = 0; i < length; i++)
            {
                String name2 = name1 + " i=" + i;
                assertEquals(name2, i, CollectionHelper
                        .binarySearch(bd7sorted, sorted7keys[i], length));
            }
        }
    }

    @Test public void testSortBD()
    {
        setupBookDepthStructArrays();
        CollectionHelper.sort(bd6unsorted);
        for (int i = 0; i < bd6unsorted.length; ++i)
        {
            assertTrue("i=" + i, equals(bd6sorted[i], bd6unsorted[i]));
        }
        CollectionHelper.sort(bd7unsorted);
        for (int i = 0; i < bd7unsorted.length; ++i)
        {
            assertTrue("i=" + i, equals(bd7sorted[i], bd7sorted[i]));
        }

        // Now that the "unsorted" arrays have been sorted, we can use them as
        // a sorted reference after we try sorting the already-sorted arrays.

        CollectionHelper.sort(bd6sorted);
        for (int i = 0; i < bd6unsorted.length; ++i)
        {
            assertTrue("i=" + i, equals(bd6sorted[i], bd6unsorted[i]));
        }
        CollectionHelper.sort(bd7sorted);
        for (int i = 0; i < bd7unsorted.length; ++i)
        {
            assertTrue("i=" + i, equals(bd7sorted[i], bd7unsorted[i]));
        }
    }

    @Test public void testArraycloneBD()
    {
        setupBookDepthStructArrays();
        BookDepthStruct result[] = CollectionHelper.arrayclone(bd6sorted[0]);
        assertEquals(1, result.length);
        assertTrue(equals(result[0], bd6sorted[0]));

        result = CollectionHelper.arrayclone((BookDepthStruct[]) null);
        assertNull(result);

        result = CollectionHelper.arrayclone(bd7unsorted);
        assertEquals(bd7unsorted.length, result.length);
        for (int i = 0; i < result.length; ++i)
        {
            assertTrue("i=" + i, equals(result[i], bd7unsorted[i]));
        }

        int toSize = bd7sorted.length+1;
        result = CollectionHelper.arrayclone(bd7sorted, toSize);
        assertEquals(toSize, result.length);
        for (int i = 0; i < bd7sorted.length; ++i)
        {
            assertTrue("i=" + i, equals(result[i], bd7sorted[i]));
        }
        assertNull(result[result.length-1]);

        toSize = 6;
        int fromSize = 5;
        int fromOffset = 1;
        result = CollectionHelper.arrayclone(bd7unsorted, fromOffset,
                    fromSize, toSize);
        assertEquals(toSize, result.length);
        for (int i = 0; i < fromSize; ++i)
        {
            assertTrue("i=" + i, equals(bd7unsorted[i+fromOffset], result[i]));
        }
    }

    @Test public void testArraycloneCombineBD()
    {
        setupBookDepthStructArrays();
        // Method arguments are
        // from - source array
        // startOffset - first copy from[startOffset..from.length-1]
        // endOffset - next copy from[0..endOffset]
        // toSize - size of result, must be
        //     at least from.length-startOffset + endOffset+1
        BookDepthStruct result[];
        result = CollectionHelper.arraycloneCombine(bd7sorted, 5, 1, 4);
        assertEquals(4, result.length);
        assertTrue(equals(bd7sorted[5], result[0]));
        assertTrue(equals(bd7sorted[6], result[1]));
        assertTrue(equals(bd7sorted[0], result[2]));
        assertNull(result[3]);
    }

    @Test public void testArraycloneExpandGapBD()
    {
        setupBookDepthStructArrays();

        // As implemented, arraycloneExpandGap makes sense when fromOffset == 0
        int gapLength = 3;
        int toSize = bd7unsorted.length+gapLength;
        BookDepthStruct result[] = CollectionHelper.arraycloneExpandGap(
                bd7unsorted, 0, bd7unsorted.length,
                toSize, 2, gapLength);

        assertEquals(toSize, result.length);
        assertTrue(equals(bd7unsorted[0], result[0]));
        assertTrue(equals(bd7unsorted[1], result[1]));
        assertNull(result[2]);
        assertNull(result[3]);
        assertNull(result[4]);
        assertTrue(equals(bd7unsorted[2], result[5]));
        assertTrue(equals(bd7unsorted[3], result[6]));
        assertTrue(equals(bd7unsorted[4], result[7]));
        assertTrue(equals(bd7unsorted[5], result[8]));
        assertTrue(equals(bd7unsorted[6], result[9]));
    }

    @Test public void testArraycloneBDBD()
    {
        setupBookDepthStructArrays();
        BookDepthStruct nullMatrix[][] = null;
        BookDepthStruct result[][];

        result = CollectionHelper.arrayclone(nullMatrix);
        assertNull(result);

        result = CollectionHelper.arrayclone(bdbd);
        assertNotNull(result);
        assertEquals(bdbd.length, result.length);
        for (int one = 0; one < bdbd.length; ++one)
        {
            assertEquals("["+one+"]", bdbd[one].length, result[one].length);
            for (int two = 0; two < bdbd[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(bdbd[one][two], result[one][two]));
            }
        }

        result = CollectionHelper.arrayclone(nullMatrix, 1);
        assertNull(result);

        result = CollectionHelper.arrayclone(bdbd, bdbd.length+1);
        assertNotNull(result);
        assertEquals(bdbd.length+1, result.length);
        assertNull(result[result.length-1]);
        for (int one = 0; one < bdbd.length; ++one)
        {
            assertEquals("["+one+"]", bdbd[one].length, result[one].length);
            for (int two = 0; two < bdbd[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(bdbd[one][two], result[one][two]));
            }
        }

        result = CollectionHelper.arrayclone(nullMatrix, 1, 1, 1);
        assertNull(result);

        result = CollectionHelper.arrayclone(bdbd, 2, 1, 1);
        assertNotNull(result);
        assertEquals(1, result.length);
        for (int two = 0; two < bdbd[2].length; ++two)
        {
            assertTrue("[][" + two + "]", equals(bdbd[2][two], result[0][two]));
        }
    }

    @Test public void testArraycloneDimensionsBDBD()
    {
        setupBookDepthStructArrays();
        BookDepthStruct result[][] = null;

        assertNull(CollectionHelper.arraycloneDimensions(result));

        result = CollectionHelper.arraycloneDimensions(bdbd);
        assertNotNull(result);
        assertEquals(bdbd.length, result.length);
        for (int one = 0; one < bdbd.length; ++one)
        {
            assertEquals("["+one+"]", bdbd[one].length, result[one].length);
            for (int two = 0; two < bdbd[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(bdbd[one][two], result[one][two]));
            }
        }
    }

    @Test public void testArraycloneCombineBDBD()
    {
        setupBookDepthStructArrays();
        BookDepthStruct nullMatrix[][] = null;
        assertNull(CollectionHelper.arraycloneCombine(nullMatrix, 3, 3, 3));

        BookDepthStruct result[][];
        // Copy bdbd[1..2] to result[0..1], bdbd[0..1] to result[2..3]
        result = CollectionHelper.arraycloneCombine(bdbd, 1, 2, 4);
        assertNotNull(result);
        assertEquals(4, result.length);
        for (int i = 0; i < bdbd[1].length; ++i)
        {
            assertTrue("i="+i, equals(bdbd[1][i], result[0][i]));
            assertTrue("i="+i, equals(bdbd[1][i], result[3][i]));
        }
        for (int i = 0; i < bdbd[2].length; ++i)
        {
            assertTrue("i="+i, equals(bdbd[2][i], result[1][i]));
        }
        for (int i = 0; i < bdbd[0].length; ++i)
        {
            assertTrue("i="+i, equals(bdbd[0][i], result[2][i]));
        }
    }

    @Test public void testArraycloneExpandGapBDBD()
    {
        setupBookDepthStructArrays();
        BookDepthStruct nullMatrix[][] = null;
        assertNull(CollectionHelper.arraycloneExpandGap(nullMatrix,5,5,5,5,5));

        // As implemented, arraycloneExpandGap makes sense when fromOffset == 0
        int gapLength = 2;
        int toSize = bdbd.length + gapLength;
        BookDepthStruct result[][] = CollectionHelper.arraycloneExpandGap(
                bdbd, 0, bdbd.length, toSize, 1, gapLength);
        assertNotNull(result);
        assertEquals(toSize, result.length);

        int r = 0;  // index into result[]
        for (int i = 0; i < bdbd[0].length; ++i)
        {
            assertTrue("i=" + i, equals(bdbd[0][i], result[0][i]));
        }
        r++;
        for (int i = 0; i < gapLength; ++i)
        {
            assertNull(result[r++]);
        }
        for (int b = 1; b < bdbd.length; ++b)
        {
            for (int i = 0; i < bdbd[b].length; ++i)
            {
                assertTrue("["+r+"]["+i+"]", equals(bdbd[b][i], result[r][i]));
            }
            ++r;
        }
    }

    @Test public void testArraycloneShrinkGapBD()
    {
        setupBookDepthStructArrays();
        BookDepthStruct result[] = null;
        assertNull(CollectionHelper.arraycloneShrinkGap(result, 2, 2));

        result = CollectionHelper.arraycloneShrinkGap(bd7sorted, 2, 3);
        assertNotNull(result);
        assertEquals(4, result.length);
        assertTrue(equals(result[0], bd7sorted[0]));
        assertTrue(equals(result[1], bd7sorted[1]));
        assertTrue(equals(result[2], bd7sorted[5]));
        assertTrue(equals(result[3], bd7sorted[6]));
    }

    @Test public void testArraycloneShrinkGapBDBD()
    {
        setupBookDepthStructArrays();
        BookDepthStruct result[][] = null;
        assertNull(CollectionHelper.arraycloneShrinkGap(result, 2, 2));

        result = CollectionHelper.arraycloneShrinkGap(bdbd, 0, 2);
        assertNotNull(result);
        assertEquals(1, result.length);
        assertEquals(bdbd[2].length, result[0].length);
        for (int i = 0; i < bdbd[2].length; ++i)
        {
            assertTrue("i="+i, equals(bdbd[2][i], result[0][i]));
        }
    }

    private CurrentMarketStruct cm6sorted[];
    private CurrentMarketStruct cm7sorted[];
    private CurrentMarketStruct cm6unsorted[];
    private CurrentMarketStruct cm7unsorted[];
    private CurrentMarketStruct cmcm[][];

    private void setupCurrentMarketStructArrays()
    {
        // Look for search errors: even or odd array size, sorted or unsorted
        cm6sorted = new CurrentMarketStruct[6];
        cm7sorted = new CurrentMarketStruct[7];
        cm6unsorted = new CurrentMarketStruct[6];
        cm7unsorted = new CurrentMarketStruct[7];
        for (int i = 0; i < cm7sorted.length; ++i)
        {
            if (i < cm6sorted.length)
            {
                cm6sorted[i] = new CurrentMarketStruct();
                cm6sorted[i].productKeys = new ProductKeysStruct();
                cm6sorted[i].productKeys.productKey = sorted6keys[i];

                cm6unsorted[i] = new CurrentMarketStruct();
                cm6unsorted[i].productKeys = new ProductKeysStruct();
                cm6unsorted[i].productKeys.productKey = unsorted6keys[i];
            }
            cm7sorted[i] = new CurrentMarketStruct();
            cm7sorted[i].productKeys = new ProductKeysStruct();
            cm7sorted[i].productKeys.productKey = sorted7keys[i];

            cm7unsorted[i] = new CurrentMarketStruct();
            cm7unsorted[i].productKeys = new ProductKeysStruct();
            cm7unsorted[i].productKeys.productKey = unsorted7keys[i];
        }

        // Two dimensional array, total of 7 elements so we can use sorted7keys
        cmcm = new CurrentMarketStruct[3][];
        cmcm[0] = new CurrentMarketStruct[2];
        cmcm[1] = new CurrentMarketStruct[3];
        cmcm[2] = new CurrentMarketStruct[2];
        int index = 0;
        for (int one = 0; one < cmcm.length; ++one)
        {
            for (int two = 0; two < cmcm[one].length; ++two)
            {
                cmcm[one][two] = new CurrentMarketStruct();
                cmcm[one][two].productKeys = new ProductKeysStruct();
                cmcm[one][two].productKeys.productKey = sorted7keys[index++];
            }
        }
    }

    private boolean equals(CurrentMarketStruct a, CurrentMarketStruct b)
    {
        return a.productKeys.productKey == b.productKeys.productKey;
    }

    @Test public void testBinarySearchCM()
    {
        setupCurrentMarketStructArrays();
        for (int i = 0; i < cm7sorted.length; ++i)
        {
            String name = "i=" + i;
            if (i < cm6sorted.length)
            {
                assertEquals(name, i, CollectionHelper
                        .binarySearch(cm6sorted, sorted7keys[i]));
            }
            assertEquals(name, i, CollectionHelper
                    .binarySearch(cm7sorted, sorted7keys[i]));
        }

        for (int key : missingKeys)
        {
            String name = "key=" + key;
            assertTrue(name, 0 > CollectionHelper.binarySearch(cm6sorted, key));
            assertTrue(name, 0 > CollectionHelper.binarySearch(cm7sorted, key));
        }

        for (int length = 1; length <= cm7sorted.length; ++length)
        {
            String name1 = "length=" + length;
            for (int i = 0; i < length; i++)
            {
                String name2 = name1 + " i=" + i;
                assertEquals(name2, i, CollectionHelper
                        .binarySearch(cm7sorted, sorted7keys[i], length));
            }
        }
    }

    @Test public void testSortCM()
    {
        setupCurrentMarketStructArrays();
        CollectionHelper.sort(cm6unsorted);
        for (int i = 0; i < cm6unsorted.length; ++i)
        {
            assertTrue("i=" + i, equals(cm6sorted[i], cm6unsorted[i]));
        }
        CollectionHelper.sort(cm7unsorted);
        for (int i = 0; i < cm7unsorted.length; ++i)
        {
            assertTrue("i=" + i, equals(cm7sorted[i], cm7sorted[i]));
        }

        // Now that the "unsorted" arrays have been sorted, we can use them as
        // a sorted reference after we try sorting the already-sorted arrays.

        CollectionHelper.sort(cm6sorted);
        for (int i = 0; i < cm6unsorted.length; ++i)
        {
            assertTrue("i=" + i, equals(cm6sorted[i], cm6unsorted[i]));
        }
        CollectionHelper.sort(cm7sorted);
        for (int i = 0; i < cm7unsorted.length; ++i)
        {
            assertTrue("i=" + i, equals(cm7sorted[i], cm7unsorted[i]));
        }
    }

    @Test public void testArraycloneCM()
    {
        setupCurrentMarketStructArrays();
        CurrentMarketStruct result[] = CollectionHelper.arrayclone(cm6sorted[0]);
        assertEquals(1, result.length);
        assertTrue(equals(result[0], cm6sorted[0]));

        result = CollectionHelper.arrayclone((CurrentMarketStruct[]) null);
        assertNull(result);

        result = CollectionHelper.arrayclone(cm7unsorted);
        assertEquals(cm7unsorted.length, result.length);
        for (int i = 0; i < result.length; ++i)
        {
            assertTrue("i=" + i, equals(result[i], cm7unsorted[i]));
        }

        int toSize = cm7sorted.length+1;
        result = CollectionHelper.arrayclone(cm7sorted, toSize);
        assertEquals(toSize, result.length);
        for (int i = 0; i < cm7sorted.length; ++i)
        {
            assertTrue("i=" + i, equals(result[i], cm7sorted[i]));
        }
        assertNull(result[result.length-1]);

        toSize = 6;
        int fromSize = 5;
        int fromOffset = 1;
        result = CollectionHelper.arrayclone(cm7unsorted, fromOffset,
                    fromSize, toSize);
        assertEquals(toSize, result.length);
        for (int i = 0; i < fromSize; ++i)
        {
            assertTrue("i=" + i, equals(cm7unsorted[i+fromOffset], result[i]));
        }
    }

    @Test public void testArraycloneCombineCM()
    {
        setupCurrentMarketStructArrays();
        // Method arguments are
        // from - source array
        // startOffset - first copy from[startOffset..from.length-1]
        // endOffset - next copy from[0..endOffset]
        // toSize - size of result, must be
        //     at least from.length-startOffset + endOffset+1
        CurrentMarketStruct result[];
        result = CollectionHelper.arraycloneCombine(cm7sorted, 5, 1, 4);
        assertEquals(4, result.length);
        assertTrue(equals(cm7sorted[5], result[0]));
        assertTrue(equals(cm7sorted[6], result[1]));
        assertTrue(equals(cm7sorted[0], result[2]));
        assertNull(result[3]);
    }

    @Test public void testArraycloneExpandGapCM()
    {
        setupCurrentMarketStructArrays();

        // As implemented, arraycloneExpandGap makes sense when fromOffset == 0
        int gapLength = 3;
        int toSize = cm7unsorted.length+gapLength;
        CurrentMarketStruct result[] = CollectionHelper.arraycloneExpandGap(
                cm7unsorted, 0, cm7unsorted.length,
                toSize, 2, gapLength);

        assertEquals(toSize, result.length);
        assertTrue(equals(cm7unsorted[0], result[0]));
        assertTrue(equals(cm7unsorted[1], result[1]));
        assertNull(result[2]);
        assertNull(result[3]);
        assertNull(result[4]);
        assertTrue(equals(cm7unsorted[2], result[5]));
        assertTrue(equals(cm7unsorted[3], result[6]));
        assertTrue(equals(cm7unsorted[4], result[7]));
        assertTrue(equals(cm7unsorted[5], result[8]));
        assertTrue(equals(cm7unsorted[6], result[9]));
    }

    @Test public void testArraycloneCMCM()
    {
        setupCurrentMarketStructArrays();
        CurrentMarketStruct nullMatrix[][] = null;
        CurrentMarketStruct result[][];

        result = CollectionHelper.arrayclone(nullMatrix);
        assertNull(result);

        result = CollectionHelper.arrayclone(cmcm);
        assertNotNull(result);
        assertEquals(cmcm.length, result.length);
        for (int one = 0; one < cmcm.length; ++one)
        {
            assertEquals("["+one+"]", cmcm[one].length, result[one].length);
            for (int two = 0; two < cmcm[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(cmcm[one][two], result[one][two]));
            }
        }

        result = CollectionHelper.arrayclone(nullMatrix, 1);
        assertNull(result);

        result = CollectionHelper.arrayclone(cmcm, cmcm.length+1);
        assertNotNull(result);
        assertEquals(cmcm.length+1, result.length);
        assertNull(result[result.length-1]);
        for (int one = 0; one < cmcm.length; ++one)
        {
            assertEquals("["+one+"]", cmcm[one].length, result[one].length);
            for (int two = 0; two < cmcm[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(cmcm[one][two], result[one][two]));
            }
        }

        result = CollectionHelper.arrayclone(nullMatrix, 1, 1, 1);
        assertNull(result);

        result = CollectionHelper.arrayclone(cmcm, 2, 1, 1);
        assertNotNull(result);
        assertEquals(1, result.length);
        for (int two = 0; two < cmcm[2].length; ++two)
        {
            assertTrue("[][" + two + "]", equals(cmcm[2][two], result[0][two]));
        }
    }

    @Test public void testArraycloneDimensionsCMCM()
    {
        setupCurrentMarketStructArrays();
        CurrentMarketStruct result[][] = null;

        assertNull(CollectionHelper.arraycloneDimensions(result));

        result = CollectionHelper.arraycloneDimensions(cmcm);
        assertNotNull(result);
        assertEquals(cmcm.length, result.length);
        for (int one = 0; one < cmcm.length; ++one)
        {
            assertEquals("["+one+"]", cmcm[one].length, result[one].length);
            for (int two = 0; two < cmcm[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(cmcm[one][two], result[one][two]));
            }
        }
    }

    @Test public void testArraycloneCombineCMCM()
    {
        setupCurrentMarketStructArrays();
        CurrentMarketStruct nullMatrix[][] = null;
        assertNull(CollectionHelper.arraycloneCombine(nullMatrix, 3, 3, 3));

        CurrentMarketStruct result[][];
        // Copy cmcm[1..2] to result[0..1], cmcm[0..1] to result[2..3]
        result = CollectionHelper.arraycloneCombine(cmcm, 1, 2, 4);
        assertNotNull(result);
        assertEquals(4, result.length);
        for (int i = 0; i < cmcm[1].length; ++i)
        {
            assertTrue("i="+i, equals(cmcm[1][i], result[0][i]));
            assertTrue("i="+i, equals(cmcm[1][i], result[3][i]));
        }
        for (int i = 0; i < cmcm[2].length; ++i)
        {
            assertTrue("i="+i, equals(cmcm[2][i], result[1][i]));
        }
        for (int i = 0; i < cmcm[0].length; ++i)
        {
            assertTrue("i="+i, equals(cmcm[0][i], result[2][i]));
        }
    }

    @Test public void testArraycloneExpandGapCMCM()
    {
        setupCurrentMarketStructArrays();
        CurrentMarketStruct nullMatrix[][] = null;
        assertNull(CollectionHelper.arraycloneExpandGap(nullMatrix,5,5,5,5,5));

        // As implemented, arraycloneExpandGap makes sense when fromOffset == 0
        int gapLength = 2;
        int toSize = cmcm.length + gapLength;
        CurrentMarketStruct result[][] = CollectionHelper.arraycloneExpandGap(
                cmcm, 0, cmcm.length, toSize, 1, gapLength);
        assertNotNull(result);
        assertEquals(toSize, result.length);

        int r = 0;  // index into result[]
        for (int i = 0; i < cmcm[0].length; ++i)
        {
            assertTrue("i=" + i, equals(cmcm[0][i], result[0][i]));
        }
        r++;
        for (int i = 0; i < gapLength; ++i)
        {
            assertNull(result[r++]);
        }
        for (int b = 1; b < cmcm.length; ++b)
        {
            for (int i = 0; i < cmcm[b].length; ++i)
            {
                assertTrue("["+r+"]["+i+"]", equals(cmcm[b][i], result[r][i]));
            }
            ++r;
        }
    }

    @Test public void testArraycloneShrinkGapCM()
    {
        setupCurrentMarketStructArrays();
        CurrentMarketStruct result[] = null;
        assertNull(CollectionHelper.arraycloneShrinkGap(result, 2, 2));

        result = CollectionHelper.arraycloneShrinkGap(cm7sorted, 2, 3);
        assertNotNull(result);
        assertEquals(4, result.length);
        assertTrue(equals(result[0], cm7sorted[0]));
        assertTrue(equals(result[1], cm7sorted[1]));
        assertTrue(equals(result[2], cm7sorted[5]));
        assertTrue(equals(result[3], cm7sorted[6]));
    }

    @Test public void testArraycloneShrinkGapCMCM()
    {
        setupCurrentMarketStructArrays();
        CurrentMarketStruct result[][] = null;
        assertNull(CollectionHelper.arraycloneShrinkGap(result, 2, 2));

        result = CollectionHelper.arraycloneShrinkGap(cmcm, 0, 2);
        assertNotNull(result);
        assertEquals(1, result.length);
        assertEquals(cmcm[2].length, result[0].length);
        for (int i = 0; i < cmcm[2].length; ++i)
        {
            assertTrue("i="+i, equals(cmcm[2][i], result[0][i]));
        }
    }

    private RecapStruct rp6sorted[];
    private RecapStruct rp7sorted[];
    private RecapStruct rp6unsorted[];
    private RecapStruct rp7unsorted[];
    private RecapStruct rprp[][];

    private void setupRecapStructArrays()
    {
        // Look for search errors: even or odd array size, sorted or unsorted
        rp6sorted = new RecapStruct[6];
        rp7sorted = new RecapStruct[7];
        rp6unsorted = new RecapStruct[6];
        rp7unsorted = new RecapStruct[7];
        for (int i = 0; i < rp7sorted.length; ++i)
        {
            if (i < rp6sorted.length)
            {
                rp6sorted[i] = new RecapStruct();
                rp6sorted[i].productKeys = new ProductKeysStruct();
                rp6sorted[i].productKeys.productKey = sorted6keys[i];

                rp6unsorted[i] = new RecapStruct();
                rp6unsorted[i].productKeys = new ProductKeysStruct();
                rp6unsorted[i].productKeys.productKey = unsorted6keys[i];
            }
            rp7sorted[i] = new RecapStruct();
            rp7sorted[i].productKeys = new ProductKeysStruct();
            rp7sorted[i].productKeys.productKey = sorted7keys[i];

            rp7unsorted[i] = new RecapStruct();
            rp7unsorted[i].productKeys = new ProductKeysStruct();
            rp7unsorted[i].productKeys.productKey = unsorted7keys[i];
        }

        // Two dimensional array, total of 7 elements so we can use sorted7keys
        rprp = new RecapStruct[3][];
        rprp[0] = new RecapStruct[2];
        rprp[1] = new RecapStruct[3];
        rprp[2] = new RecapStruct[2];
        int index = 0;
        for (int one = 0; one < rprp.length; ++one)
        {
            for (int two = 0; two < rprp[one].length; ++two)
            {
                rprp[one][two] = new RecapStruct();
                rprp[one][two].productKeys = new ProductKeysStruct();
                rprp[one][two].productKeys.productKey = sorted7keys[index++];
            }
        }
    }

    private boolean equals(RecapStruct a, RecapStruct b)
    {
        return a.productKeys.productKey == b.productKeys.productKey;
    }

    @Test public void testBinarySearchRP()
    {
        setupRecapStructArrays();
        for (int i = 0; i < rp7sorted.length; ++i)
        {
            String name = "i=" + i;
            if (i < rp6sorted.length)
            {
                assertEquals(name, i, CollectionHelper
                        .binarySearch(rp6sorted, sorted7keys[i]));
            }
            assertEquals(name, i, CollectionHelper
                    .binarySearch(rp7sorted, sorted7keys[i]));
        }

        for (int key : missingKeys)
        {
            String name = "key=" + key;
            assertTrue(name, 0 > CollectionHelper.binarySearch(rp6sorted, key));
            assertTrue(name, 0 > CollectionHelper.binarySearch(rp7sorted, key));
        }

        for (int length = 1; length <= rp7sorted.length; ++length)
        {
            String name1 = "length=" + length;
            for (int i = 0; i < length; i++)
            {
                String name2 = name1 + " i=" + i;
                assertEquals(name2, i, CollectionHelper
                        .binarySearch(rp7sorted, sorted7keys[i], length));
            }
        }
    }

    @Test public void testSortRP()
    {
        setupRecapStructArrays();
        CollectionHelper.sort(rp6unsorted);
        for (int i = 0; i < rp6unsorted.length; ++i)
        {
            assertTrue("i=" + i, equals(rp6sorted[i], rp6unsorted[i]));
        }
        CollectionHelper.sort(rp7unsorted);
        for (int i = 0; i < rp7unsorted.length; ++i)
        {
            assertTrue("i=" + i, equals(rp7sorted[i], rp7sorted[i]));
        }

        // Now that the "unsorted" arrays have been sorted, we can use them as
        // a sorted reference after we try sorting the already-sorted arrays.

        CollectionHelper.sort(rp6sorted);
        for (int i = 0; i < rp6unsorted.length; ++i)
        {
            assertTrue("i=" + i, equals(rp6sorted[i], rp6unsorted[i]));
        }
        CollectionHelper.sort(rp7sorted);
        for (int i = 0; i < rp7unsorted.length; ++i)
        {
            assertTrue("i=" + i, equals(rp7sorted[i], rp7unsorted[i]));
        }
    }

    @Test public void testArraycloneRP()
    {
        setupRecapStructArrays();
        RecapStruct result[] = CollectionHelper.arrayclone(rp6sorted[0]);
        assertEquals(1, result.length);
        assertTrue(equals(result[0], rp6sorted[0]));

        result = CollectionHelper.arrayclone((RecapStruct[]) null);
        assertNull(result);

        result = CollectionHelper.arrayclone(rp7unsorted);
        assertEquals(rp7unsorted.length, result.length);
        for (int i = 0; i < result.length; ++i)
        {
            assertTrue("i=" + i, equals(result[i], rp7unsorted[i]));
        }

        int toSize = rp7sorted.length+1;
        result = CollectionHelper.arrayclone(rp7sorted, toSize);
        assertEquals(toSize, result.length);
        for (int i = 0; i < rp7sorted.length; ++i)
        {
            assertTrue("i=" + i, equals(result[i], rp7sorted[i]));
        }
        assertNull(result[result.length-1]);

        toSize = 6;
        int fromSize = 5;
        int fromOffset = 1;
        result = CollectionHelper.arrayclone(rp7unsorted, fromOffset,
                    fromSize, toSize);
        assertEquals(toSize, result.length);
        for (int i = 0; i < fromSize; ++i)
        {
            assertTrue("i=" + i, equals(rp7unsorted[i+fromOffset], result[i]));
        }
    }

    @Test public void testArraycloneCombineRP()
    {
        setupRecapStructArrays();
        // Method arguments are
        // from - source array
        // startOffset - first copy from[startOffset..from.length-1]
        // endOffset - next copy from[0..endOffset]
        // toSize - size of result, must be
        //     at least from.length-startOffset + endOffset+1
        RecapStruct result[];
        result = CollectionHelper.arraycloneCombine(rp7sorted, 5, 1, 4);
        assertEquals(4, result.length);
        assertTrue(equals(rp7sorted[5], result[0]));
        assertTrue(equals(rp7sorted[6], result[1]));
        assertTrue(equals(rp7sorted[0], result[2]));
        assertNull(result[3]);
    }

    @Test public void testArraycloneExpandGapRP()
    {
        setupRecapStructArrays();

        // As implemented, arraycloneExpandGap makes sense when fromOffset == 0
        int gapLength = 3;
        int toSize = rp7unsorted.length+gapLength;
        RecapStruct result[] = CollectionHelper.arraycloneExpandGap(
                rp7unsorted, 0, rp7unsorted.length,
                toSize, 2, gapLength);

        assertEquals(toSize, result.length);
        assertTrue(equals(rp7unsorted[0], result[0]));
        assertTrue(equals(rp7unsorted[1], result[1]));
        assertNull(result[2]);
        assertNull(result[3]);
        assertNull(result[4]);
        assertTrue(equals(rp7unsorted[2], result[5]));
        assertTrue(equals(rp7unsorted[3], result[6]));
        assertTrue(equals(rp7unsorted[4], result[7]));
        assertTrue(equals(rp7unsorted[5], result[8]));
        assertTrue(equals(rp7unsorted[6], result[9]));
    }

    @Test public void testArraycloneRPRP()
    {
        setupRecapStructArrays();
        RecapStruct nullMatrix[][] = null;
        RecapStruct result[][];

        result = CollectionHelper.arrayclone(nullMatrix);
        assertNull(result);

        result = CollectionHelper.arrayclone(rprp);
        assertNotNull(result);
        assertEquals(rprp.length, result.length);
        for (int one = 0; one < rprp.length; ++one)
        {
            assertEquals("["+one+"]", rprp[one].length, result[one].length);
            for (int two = 0; two < rprp[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(rprp[one][two], result[one][two]));
            }
        }

        result = CollectionHelper.arrayclone(nullMatrix, 1);
        assertNull(result);

        result = CollectionHelper.arrayclone(rprp, rprp.length+1);
        assertNotNull(result);
        assertEquals(rprp.length+1, result.length);
        assertNull(result[result.length-1]);
        for (int one = 0; one < rprp.length; ++one)
        {
            assertEquals("["+one+"]", rprp[one].length, result[one].length);
            for (int two = 0; two < rprp[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(rprp[one][two], result[one][two]));
            }
        }

        result = CollectionHelper.arrayclone(nullMatrix, 1, 1, 1);
        assertNull(result);

        result = CollectionHelper.arrayclone(rprp, 2, 1, 1);
        assertNotNull(result);
        assertEquals(1, result.length);
        for (int two = 0; two < rprp[2].length; ++two)
        {
            assertTrue("[][" + two + "]", equals(rprp[2][two], result[0][two]));
        }
    }

    @Test public void testArraycloneDimensionsRPRP()
    {
        setupRecapStructArrays();
        RecapStruct result[][] = null;

        assertNull(CollectionHelper.arraycloneDimensions(result));

        result = CollectionHelper.arraycloneDimensions(rprp);
        assertNotNull(result);
        assertEquals(rprp.length, result.length);
        for (int one = 0; one < rprp.length; ++one)
        {
            assertEquals("["+one+"]", rprp[one].length, result[one].length);
            for (int two = 0; two < rprp[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(rprp[one][two], result[one][two]));
            }
        }
    }

    @Test public void testArraycloneCombineRPRP()
    {
        setupRecapStructArrays();
        RecapStruct nullMatrix[][] = null;
        assertNull(CollectionHelper.arraycloneCombine(nullMatrix, 3, 3, 3));

        RecapStruct result[][];
        // Copy rprp[1..2] to result[0..1], rprp[0..1] to result[2..3]
        result = CollectionHelper.arraycloneCombine(rprp, 1, 2, 4);
        assertNotNull(result);
        assertEquals(4, result.length);
        for (int i = 0; i < rprp[1].length; ++i)
        {
            assertTrue("i="+i, equals(rprp[1][i], result[0][i]));
            assertTrue("i="+i, equals(rprp[1][i], result[3][i]));
        }
        for (int i = 0; i < rprp[2].length; ++i)
        {
            assertTrue("i="+i, equals(rprp[2][i], result[1][i]));
        }
        for (int i = 0; i < rprp[0].length; ++i)
        {
            assertTrue("i="+i, equals(rprp[0][i], result[2][i]));
        }
    }

    @Test public void testArraycloneExpandGapRPRP()
    {
        setupRecapStructArrays();
        RecapStruct nullMatrix[][] = null;
        assertNull(CollectionHelper.arraycloneExpandGap(nullMatrix,5,5,5,5,5));

        // As implemented, arraycloneExpandGap makes sense when fromOffset == 0
        int gapLength = 2;
        int toSize = rprp.length + gapLength;
        RecapStruct result[][] = CollectionHelper.arraycloneExpandGap(
                rprp, 0, rprp.length, toSize, 1, gapLength);
        assertNotNull(result);
        assertEquals(toSize, result.length);

        int r = 0;  // index into result[]
        for (int i = 0; i < rprp[0].length; ++i)
        {
            assertTrue("i=" + i, equals(rprp[0][i], result[0][i]));
        }
        r++;
        for (int i = 0; i < gapLength; ++i)
        {
            assertNull(result[r++]);
        }
        for (int b = 1; b < rprp.length; ++b)
        {
            for (int i = 0; i < rprp[b].length; ++i)
            {
                assertTrue("["+r+"]["+i+"]", equals(rprp[b][i], result[r][i]));
            }
            ++r;
        }
    }

    @Test public void testArraycloneShrinkGapRP()
    {
        setupRecapStructArrays();
        RecapStruct result[] = null;
        assertNull(CollectionHelper.arraycloneShrinkGap(result, 2, 2));

        result = CollectionHelper.arraycloneShrinkGap(rp7sorted, 2, 3);
        assertNotNull(result);
        assertEquals(4, result.length);
        assertTrue(equals(result[0], rp7sorted[0]));
        assertTrue(equals(result[1], rp7sorted[1]));
        assertTrue(equals(result[2], rp7sorted[5]));
        assertTrue(equals(result[3], rp7sorted[6]));
    }

    @Test public void testArraycloneShrinkGapRPRP()
    {
        setupRecapStructArrays();
        RecapStruct result[][] = null;
        assertNull(CollectionHelper.arraycloneShrinkGap(result, 2, 2));

        result = CollectionHelper.arraycloneShrinkGap(rprp, 0, 2);
        assertNotNull(result);
        assertEquals(1, result.length);
        assertEquals(rprp[2].length, result[0].length);
        for (int i = 0; i < rprp[2].length; ++i)
        {
            assertTrue("i="+i, equals(rprp[2][i], result[0][i]));
        }
    }

    private TickerStruct tk6sorted[];
    private TickerStruct tk7sorted[];
    private TickerStruct tk6unsorted[];
    private TickerStruct tk7unsorted[];
    private TickerStruct tktk[][];

    private void setupTickerStructArrays()
    {
        // Look for search errors: even or odd array size, sorted or unsorted
        tk6sorted = new TickerStruct[6];
        tk7sorted = new TickerStruct[7];
        tk6unsorted = new TickerStruct[6];
        tk7unsorted = new TickerStruct[7];
        for (int i = 0; i < tk7sorted.length; ++i)
        {
            if (i < tk6sorted.length)
            {
                tk6sorted[i] = new TickerStruct();
                tk6sorted[i].productKeys = new ProductKeysStruct();
                tk6sorted[i].productKeys.productKey = sorted6keys[i];

                tk6unsorted[i] = new TickerStruct();
                tk6unsorted[i].productKeys = new ProductKeysStruct();
                tk6unsorted[i].productKeys.productKey = unsorted6keys[i];
            }
            tk7sorted[i] = new TickerStruct();
            tk7sorted[i].productKeys = new ProductKeysStruct();
            tk7sorted[i].productKeys.productKey = sorted7keys[i];

            tk7unsorted[i] = new TickerStruct();
            tk7unsorted[i].productKeys = new ProductKeysStruct();
            tk7unsorted[i].productKeys.productKey = unsorted7keys[i];
        }

        // Two dimensional array, total of 7 elements so we can use sorted7keys
        tktk = new TickerStruct[3][];
        tktk[0] = new TickerStruct[2];
        tktk[1] = new TickerStruct[3];
        tktk[2] = new TickerStruct[2];
        int index = 0;
        for (int one = 0; one < tktk.length; ++one)
        {
            for (int two = 0; two < tktk[one].length; ++two)
            {
                tktk[one][two] = new TickerStruct();
                tktk[one][two].productKeys = new ProductKeysStruct();
                tktk[one][two].productKeys.productKey = sorted7keys[index++];
            }
        }
    }

    private boolean equals(TickerStruct a, TickerStruct b)
    {
        return a.productKeys.productKey == b.productKeys.productKey;
    }

    @Test public void testBinarySearchTK()
    {
        setupTickerStructArrays();
        for (int i = 0; i < tk7sorted.length; ++i)
        {
            String name = "i=" + i;
            if (i < tk6sorted.length)
            {
                assertEquals(name, i, CollectionHelper
                        .binarySearch(tk6sorted, sorted7keys[i]));
            }
            assertEquals(name, i, CollectionHelper
                    .binarySearch(tk7sorted, sorted7keys[i]));
        }

        for (int key : missingKeys)
        {
            String name = "key=" + key;
            assertTrue(name, 0 > CollectionHelper.binarySearch(tk6sorted, key));
            assertTrue(name, 0 > CollectionHelper.binarySearch(tk7sorted, key));
        }

        for (int length = 1; length <= tk7sorted.length; ++length)
        {
            String name1 = "length=" + length;
            for (int i = 0; i < length; i++)
            {
                String name2 = name1 + " i=" + i;
                assertEquals(name2, i, CollectionHelper
                        .binarySearch(tk7sorted, sorted7keys[i], length));
            }
        }
    }

    @Test public void testSortTK()
    {
        setupTickerStructArrays();
        CollectionHelper.sort(tk6unsorted);
        for (int i = 0; i < tk6unsorted.length; ++i)
        {
            assertTrue("i=" + i, equals(tk6sorted[i], tk6unsorted[i]));
        }
        CollectionHelper.sort(tk7unsorted);
        for (int i = 0; i < tk7unsorted.length; ++i)
        {
            assertTrue("i=" + i, equals(tk7sorted[i], tk7sorted[i]));
        }

        // Now that the "unsorted" arrays have been sorted, we can use them as
        // a sorted reference after we try sorting the already-sorted arrays.

        CollectionHelper.sort(tk6sorted);
        for (int i = 0; i < tk6unsorted.length; ++i)
        {
            assertTrue("i=" + i, equals(tk6sorted[i], tk6unsorted[i]));
        }
        CollectionHelper.sort(tk7sorted);
        for (int i = 0; i < tk7unsorted.length; ++i)
        {
            assertTrue("i=" + i, equals(tk7sorted[i], tk7unsorted[i]));
        }
    }

    @Test public void testArraycloneTK()
    {
        setupTickerStructArrays();
        TickerStruct result[] = CollectionHelper.arrayclone(tk6sorted[0]);
        assertEquals(1, result.length);
        assertTrue(equals(result[0], tk6sorted[0]));

        result = CollectionHelper.arrayclone((TickerStruct[]) null);
        assertNull(result);

        result = CollectionHelper.arrayclone(tk7unsorted);
        assertEquals(tk7unsorted.length, result.length);
        for (int i = 0; i < result.length; ++i)
        {
            assertTrue("i=" + i, equals(result[i], tk7unsorted[i]));
        }

        int toSize = tk7sorted.length+1;
        result = CollectionHelper.arrayclone(tk7sorted, toSize);
        assertEquals(toSize, result.length);
        for (int i = 0; i < tk7sorted.length; ++i)
        {
            assertTrue("i=" + i, equals(result[i], tk7sorted[i]));
        }
        assertNull(result[result.length-1]);

        toSize = 6;
        int fromSize = 5;
        int fromOffset = 1;
        result = CollectionHelper.arrayclone(tk7unsorted, fromOffset,
                    fromSize, toSize);
        assertEquals(toSize, result.length);
        for (int i = 0; i < fromSize; ++i)
        {
            assertTrue("i=" + i, equals(tk7unsorted[i+fromOffset], result[i]));
        }
    }

    @Test public void testArraycloneCombineTK()
    {
        setupTickerStructArrays();
        // Method arguments are
        // from - source array
        // startOffset - first copy from[startOffset..from.length-1]
        // endOffset - next copy from[0..endOffset]
        // toSize - size of result, must be
        //     at least from.length-startOffset + endOffset+1
        TickerStruct result[];
        result = CollectionHelper.arraycloneCombine(tk7sorted, 5, 1, 4);
        assertEquals(4, result.length);
        assertTrue(equals(tk7sorted[5], result[0]));
        assertTrue(equals(tk7sorted[6], result[1]));
        assertTrue(equals(tk7sorted[0], result[2]));
        assertNull(result[3]);
    }

    @Test public void testArraycloneExpandGapTK()
    {
        setupTickerStructArrays();

        // As implemented, arraycloneExpandGap makes sense when fromOffset == 0
        int gapLength = 3;
        int toSize = tk7unsorted.length+gapLength;
        TickerStruct result[] = CollectionHelper.arraycloneExpandGap(
                tk7unsorted, 0, tk7unsorted.length,
                toSize, 2, gapLength);

        assertEquals(toSize, result.length);
        assertTrue(equals(tk7unsorted[0], result[0]));
        assertTrue(equals(tk7unsorted[1], result[1]));
        assertNull(result[2]);
        assertNull(result[3]);
        assertNull(result[4]);
        assertTrue(equals(tk7unsorted[2], result[5]));
        assertTrue(equals(tk7unsorted[3], result[6]));
        assertTrue(equals(tk7unsorted[4], result[7]));
        assertTrue(equals(tk7unsorted[5], result[8]));
        assertTrue(equals(tk7unsorted[6], result[9]));
    }

    @Test public void testArraycloneTKTK()
    {
        setupTickerStructArrays();
        TickerStruct nullMatrix[][] = null;
        TickerStruct result[][];

        result = CollectionHelper.arrayclone(nullMatrix);
        assertNull(result);

        result = CollectionHelper.arrayclone(tktk);
        assertNotNull(result);
        assertEquals(tktk.length, result.length);
        for (int one = 0; one < tktk.length; ++one)
        {
            assertEquals("["+one+"]", tktk[one].length, result[one].length);
            for (int two = 0; two < tktk[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(tktk[one][two], result[one][two]));
            }
        }

        result = CollectionHelper.arrayclone(nullMatrix, 1);
        assertNull(result);

        result = CollectionHelper.arrayclone(tktk, tktk.length+1);
        assertNotNull(result);
        assertEquals(tktk.length+1, result.length);
        assertNull(result[result.length-1]);
        for (int one = 0; one < tktk.length; ++one)
        {
            assertEquals("["+one+"]", tktk[one].length, result[one].length);
            for (int two = 0; two < tktk[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(tktk[one][two], result[one][two]));
            }
        }

        result = CollectionHelper.arrayclone(nullMatrix, 1, 1, 1);
        assertNull(result);

        result = CollectionHelper.arrayclone(tktk, 2, 1, 1);
        assertNotNull(result);
        assertEquals(1, result.length);
        for (int two = 0; two < tktk[2].length; ++two)
        {
            assertTrue("[][" + two + "]", equals(tktk[2][two], result[0][two]));
        }
    }

    @Test public void testArraycloneDimensionsTKTK()
    {
        setupTickerStructArrays();
        TickerStruct result[][] = null;

        assertNull(CollectionHelper.arraycloneDimensions(result));

        result = CollectionHelper.arraycloneDimensions(tktk);
        assertNotNull(result);
        assertEquals(tktk.length, result.length);
        for (int one = 0; one < tktk.length; ++one)
        {
            assertEquals("["+one+"]", tktk[one].length, result[one].length);
            for (int two = 0; two < tktk[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(tktk[one][two], result[one][two]));
            }
        }
    }

    @Test public void testArraycloneCombineTKTK()
    {
        setupTickerStructArrays();
        TickerStruct nullMatrix[][] = null;
        assertNull(CollectionHelper.arraycloneCombine(nullMatrix, 3, 3, 3));

        TickerStruct result[][];
        // Copy tktk[1..2] to result[0..1], tktk[0..1] to result[2..3]
        result = CollectionHelper.arraycloneCombine(tktk, 1, 2, 4);
        assertNotNull(result);
        assertEquals(4, result.length);
        for (int i = 0; i < tktk[1].length; ++i)
        {
            assertTrue("i="+i, equals(tktk[1][i], result[0][i]));
            assertTrue("i="+i, equals(tktk[1][i], result[3][i]));
        }
        for (int i = 0; i < tktk[2].length; ++i)
        {
            assertTrue("i="+i, equals(tktk[2][i], result[1][i]));
        }
        for (int i = 0; i < tktk[0].length; ++i)
        {
            assertTrue("i="+i, equals(tktk[0][i], result[2][i]));
        }
    }

    @Test public void testArraycloneExpandGapTKTK()
    {
        setupTickerStructArrays();
        TickerStruct nullMatrix[][] = null;
        assertNull(CollectionHelper.arraycloneExpandGap(nullMatrix,5,5,5,5,5));

        // As implemented, arraycloneExpandGap makes sense when fromOffset == 0
        int gapLength = 2;
        int toSize = tktk.length + gapLength;
        TickerStruct result[][] = CollectionHelper.arraycloneExpandGap(
                tktk, 0, tktk.length, toSize, 1, gapLength);
        assertNotNull(result);
        assertEquals(toSize, result.length);

        int r = 0;  // index into result[]
        for (int i = 0; i < tktk[0].length; ++i)
        {
            assertTrue("i=" + i, equals(tktk[0][i], result[0][i]));
        }
        r++;
        for (int i = 0; i < gapLength; ++i)
        {
            assertNull(result[r++]);
        }
        for (int b = 1; b < tktk.length; ++b)
        {
            for (int i = 0; i < tktk[b].length; ++i)
            {
                assertTrue("["+r+"]["+i+"]", equals(tktk[b][i], result[r][i]));
            }
            ++r;
        }
    }

    @Test public void testArraycloneShrinkGapTK()
    {
        setupTickerStructArrays();
        TickerStruct result[] = null;
        assertNull(CollectionHelper.arraycloneShrinkGap(result, 2, 2));

        result = CollectionHelper.arraycloneShrinkGap(tk7sorted, 2, 3);
        assertNotNull(result);
        assertEquals(4, result.length);
        assertTrue(equals(result[0], tk7sorted[0]));
        assertTrue(equals(result[1], tk7sorted[1]));
        assertTrue(equals(result[2], tk7sorted[5]));
        assertTrue(equals(result[3], tk7sorted[6]));
    }

    @Test public void testArraycloneShrinkGapTKTK()
    {
        setupTickerStructArrays();
        TickerStruct result[][] = null;
        assertNull(CollectionHelper.arraycloneShrinkGap(result, 2, 2));

        result = CollectionHelper.arraycloneShrinkGap(tktk, 0, 2);
        assertNotNull(result);
        assertEquals(1, result.length);
        assertEquals(tktk[2].length, result[0].length);
        for (int i = 0; i < tktk[2].length; ++i)
        {
            assertTrue("i="+i, equals(tktk[2][i], result[0][i]));
        }
    }

    private ExpectedOpeningPriceStruct eop6sorted[];
    private ExpectedOpeningPriceStruct eop7sorted[];
    private ExpectedOpeningPriceStruct eop6unsorted[];
    private ExpectedOpeningPriceStruct eop7unsorted[];
    private ExpectedOpeningPriceStruct eopeop[][];

    private void setupExpectedOpeningPriceStructArrays()
    {
        // Look for search errors: even or odd array size, sorted or unsorted
        eop6sorted = new ExpectedOpeningPriceStruct[6];
        eop7sorted = new ExpectedOpeningPriceStruct[7];
        eop6unsorted = new ExpectedOpeningPriceStruct[6];
        eop7unsorted = new ExpectedOpeningPriceStruct[7];
        for (int i = 0; i < eop7sorted.length; ++i)
        {
            if (i < eop6sorted.length)
            {
                eop6sorted[i] = new ExpectedOpeningPriceStruct();
                eop6sorted[i].productKeys = new ProductKeysStruct();
                eop6sorted[i].productKeys.productKey = sorted6keys[i];

                eop6unsorted[i] = new ExpectedOpeningPriceStruct();
                eop6unsorted[i].productKeys = new ProductKeysStruct();
                eop6unsorted[i].productKeys.productKey = unsorted6keys[i];
            }
            eop7sorted[i] = new ExpectedOpeningPriceStruct();
            eop7sorted[i].productKeys = new ProductKeysStruct();
            eop7sorted[i].productKeys.productKey = sorted7keys[i];

            eop7unsorted[i] = new ExpectedOpeningPriceStruct();
            eop7unsorted[i].productKeys = new ProductKeysStruct();
            eop7unsorted[i].productKeys.productKey = unsorted7keys[i];
        }

        // Two dimensional array, total of 7 elements so we can use sorted7keys
        eopeop = new ExpectedOpeningPriceStruct[3][];
        eopeop[0] = new ExpectedOpeningPriceStruct[2];
        eopeop[1] = new ExpectedOpeningPriceStruct[3];
        eopeop[2] = new ExpectedOpeningPriceStruct[2];
        int index = 0;
        for (int one = 0; one < eopeop.length; ++one)
        {
            for (int two = 0; two < eopeop[one].length; ++two)
            {
                eopeop[one][two] = new ExpectedOpeningPriceStruct();
                eopeop[one][two].productKeys = new ProductKeysStruct();
                eopeop[one][two].productKeys.productKey = sorted7keys[index++];
            }
        }
    }

    private boolean equals(ExpectedOpeningPriceStruct a, ExpectedOpeningPriceStruct b)
    {
        return a.productKeys.productKey == b.productKeys.productKey;
    }

    @Test public void testBinarySearchEOP()
    {
        setupExpectedOpeningPriceStructArrays();
        for (int i = 0; i < eop7sorted.length; ++i)
        {
            String name = "i=" + i;
            if (i < eop6sorted.length)
            {
                assertEquals(name, i, CollectionHelper
                        .binarySearch(eop6sorted, sorted7keys[i]));
            }
            assertEquals(name, i, CollectionHelper
                    .binarySearch(eop7sorted, sorted7keys[i]));
        }

        for (int key : missingKeys)
        {
            String name = "key=" + key;
            assertTrue(name, 0 > CollectionHelper.binarySearch(eop6sorted, key));
            assertTrue(name, 0 > CollectionHelper.binarySearch(eop7sorted, key));
        }

        for (int length = 1; length <= eop7sorted.length; ++length)
        {
            String name1 = "length=" + length;
            for (int i = 0; i < length; i++)
            {
                String name2 = name1 + " i=" + i;
                assertEquals(name2, i, CollectionHelper
                        .binarySearch(eop7sorted, sorted7keys[i], length));
            }
        }
    }

    @Test public void testSortEOP()
    {
        setupExpectedOpeningPriceStructArrays();
        CollectionHelper.sort(eop6unsorted);
        for (int i = 0; i < eop6unsorted.length; ++i)
        {
            assertTrue("i=" + i, equals(eop6sorted[i], eop6unsorted[i]));
        }
        CollectionHelper.sort(eop7unsorted);
        for (int i = 0; i < eop7unsorted.length; ++i)
        {
            assertTrue("i=" + i, equals(eop7sorted[i], eop7sorted[i]));
        }

        // Now that the "unsorted" arrays have been sorted, we can use them as
        // a sorted reference after we try sorting the already-sorted arrays.

        CollectionHelper.sort(eop6sorted);
        for (int i = 0; i < eop6unsorted.length; ++i)
        {
            assertTrue("i=" + i, equals(eop6sorted[i], eop6unsorted[i]));
        }
        CollectionHelper.sort(eop7sorted);
        for (int i = 0; i < eop7unsorted.length; ++i)
        {
            assertTrue("i=" + i, equals(eop7sorted[i], eop7unsorted[i]));
        }
    }

    @Test public void testArraycloneEOP()
    {
        setupExpectedOpeningPriceStructArrays();
        ExpectedOpeningPriceStruct result[] = CollectionHelper.arrayclone(eop6sorted[0]);
        assertEquals(1, result.length);
        assertTrue(equals(result[0], eop6sorted[0]));

        result = CollectionHelper.arrayclone((ExpectedOpeningPriceStruct[]) null);
        assertNull(result);

        result = CollectionHelper.arrayclone(eop7unsorted);
        assertEquals(eop7unsorted.length, result.length);
        for (int i = 0; i < result.length; ++i)
        {
            assertTrue("i=" + i, equals(result[i], eop7unsorted[i]));
        }

        int toSize = eop7sorted.length+1;
        result = CollectionHelper.arrayclone(eop7sorted, toSize);
        assertEquals(toSize, result.length);
        for (int i = 0; i < eop7sorted.length; ++i)
        {
            assertTrue("i=" + i, equals(result[i], eop7sorted[i]));
        }
        assertNull(result[result.length-1]);

        toSize = 6;
        int fromSize = 5;
        int fromOffset = 1;
        result = CollectionHelper.arrayclone(eop7unsorted, fromOffset,
                    fromSize, toSize);
        assertEquals(toSize, result.length);
        for (int i = 0; i < fromSize; ++i)
        {
            assertTrue("i=" + i, equals(eop7unsorted[i+fromOffset], result[i]));
        }
    }

    @Test public void testArraycloneCombineEOP()
    {
        setupExpectedOpeningPriceStructArrays();
        // Method arguments are
        // from - source array
        // startOffset - first copy from[startOffset..from.length-1]
        // endOffset - next copy from[0..endOffset]
        // toSize - size of result, must be
        //     at least from.length-startOffset + endOffset+1
        ExpectedOpeningPriceStruct result[];
        result = CollectionHelper.arraycloneCombine(eop7sorted, 5, 1, 4);
        assertEquals(4, result.length);
        assertTrue(equals(eop7sorted[5], result[0]));
        assertTrue(equals(eop7sorted[6], result[1]));
        assertTrue(equals(eop7sorted[0], result[2]));
        assertNull(result[3]);
    }

    @Test public void testArraycloneExpandGapEOP()
    {
        setupExpectedOpeningPriceStructArrays();

        // As implemented, arraycloneExpandGap makes sense when fromOffset == 0
        int gapLength = 3;
        int toSize = eop7unsorted.length+gapLength;
        ExpectedOpeningPriceStruct result[] = CollectionHelper
                .arraycloneExpandGap(eop7unsorted, 0, eop7unsorted.length,
                    toSize, 2, gapLength);

        assertEquals(toSize, result.length);
        assertTrue(equals(eop7unsorted[0], result[0]));
        assertTrue(equals(eop7unsorted[1], result[1]));
        assertNull(result[2]);
        assertNull(result[3]);
        assertNull(result[4]);
        assertTrue(equals(eop7unsorted[2], result[5]));
        assertTrue(equals(eop7unsorted[3], result[6]));
        assertTrue(equals(eop7unsorted[4], result[7]));
        assertTrue(equals(eop7unsorted[5], result[8]));
        assertTrue(equals(eop7unsorted[6], result[9]));
    }

    @Test public void testArraycloneEOPEOP()
    {
        setupExpectedOpeningPriceStructArrays();
        ExpectedOpeningPriceStruct nullMatrix[][] = null;
        ExpectedOpeningPriceStruct result[][];

        result = CollectionHelper.arrayclone(nullMatrix);
        assertNull(result);

        result = CollectionHelper.arrayclone(eopeop);
        assertNotNull(result);
        assertEquals(eopeop.length, result.length);
        for (int one = 0; one < eopeop.length; ++one)
        {
            assertEquals("["+one+"]", eopeop[one].length, result[one].length);
            for (int two = 0; two < eopeop[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(eopeop[one][two], result[one][two]));
            }
        }

        result = CollectionHelper.arrayclone(nullMatrix, 1);
        assertNull(result);

        result = CollectionHelper.arrayclone(eopeop, eopeop.length+1);
        assertNotNull(result);
        assertEquals(eopeop.length+1, result.length);
        assertNull(result[result.length-1]);
        for (int one = 0; one < eopeop.length; ++one)
        {
            assertEquals("["+one+"]", eopeop[one].length, result[one].length);
            for (int two = 0; two < eopeop[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(eopeop[one][two], result[one][two]));
            }
        }

        result = CollectionHelper.arrayclone(nullMatrix, 1, 1, 1);
        assertNull(result);

        result = CollectionHelper.arrayclone(eopeop, 2, 1, 1);
        assertNotNull(result);
        assertEquals(1, result.length);
        for (int two = 0; two < eopeop[2].length; ++two)
        {
            assertTrue("[][" + two + "]", equals(eopeop[2][two], result[0][two]));
        }
    }

    @Test public void testArraycloneDimensionsEOPEOP()
    {
        setupExpectedOpeningPriceStructArrays();
        ExpectedOpeningPriceStruct result[][] = null;

        assertNull(CollectionHelper.arraycloneDimensions(result));

        result = CollectionHelper.arraycloneDimensions(eopeop);
        assertNotNull(result);
        assertEquals(eopeop.length, result.length);
        for (int one = 0; one < eopeop.length; ++one)
        {
            assertEquals("["+one+"]", eopeop[one].length, result[one].length);
            for (int two = 0; two < eopeop[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(eopeop[one][two], result[one][two]));
            }
        }
    }

    @Test public void testArraycloneCombineEOPEOP()
    {
        setupExpectedOpeningPriceStructArrays();
        ExpectedOpeningPriceStruct nullMatrix[][] = null;
        assertNull(CollectionHelper.arraycloneCombine(nullMatrix, 3, 3, 3));

        ExpectedOpeningPriceStruct result[][];
        // Copy eopeop[1..2] to result[0..1], eopeop[0..1] to result[2..3]
        result = CollectionHelper.arraycloneCombine(eopeop, 1, 2, 4);
        assertNotNull(result);
        assertEquals(4, result.length);
        for (int i = 0; i < eopeop[1].length; ++i)
        {
            assertTrue("i="+i, equals(eopeop[1][i], result[0][i]));
            assertTrue("i="+i, equals(eopeop[1][i], result[3][i]));
        }
        for (int i = 0; i < eopeop[2].length; ++i)
        {
            assertTrue("i="+i, equals(eopeop[2][i], result[1][i]));
        }
        for (int i = 0; i < eopeop[0].length; ++i)
        {
            assertTrue("i="+i, equals(eopeop[0][i], result[2][i]));
        }
    }

    @Test public void testArraycloneExpandGapEOPEOP()
    {
        setupExpectedOpeningPriceStructArrays();
        ExpectedOpeningPriceStruct nullMatrix[][] = null;
        assertNull(CollectionHelper.arraycloneExpandGap(nullMatrix,5,5,5,5,5));

        // As implemented, arraycloneExpandGap makes sense when fromOffset == 0
        int gapLength = 2;
        int toSize = eopeop.length + gapLength;
        ExpectedOpeningPriceStruct result[][] = CollectionHelper
                .arraycloneExpandGap(eopeop, 0, eopeop.length,
                        toSize, 1, gapLength);
        assertNotNull(result);
        assertEquals(toSize, result.length);

        int r = 0;  // index into result[]
        for (int i = 0; i < eopeop[0].length; ++i)
        {
            assertTrue("i=" + i, equals(eopeop[0][i], result[0][i]));
        }
        r++;
        for (int i = 0; i < gapLength; ++i)
        {
            assertNull(result[r++]);
        }
        for (int b = 1; b < eopeop.length; ++b)
        {
            for (int i = 0; i < eopeop[b].length; ++i)
            {
                assertTrue("["+r+"]["+i+"]", equals(eopeop[b][i], result[r][i]));
            }
            ++r;
        }
    }

    @Test public void testArraycloneShrinkGapEOP()
    {
        setupExpectedOpeningPriceStructArrays();
        ExpectedOpeningPriceStruct result[] = null;
        assertNull(CollectionHelper.arraycloneShrinkGap(result, 2, 2));

        result = CollectionHelper.arraycloneShrinkGap(eop7sorted, 2, 3);
        assertNotNull(result);
        assertEquals(4, result.length);
        assertTrue(equals(result[0], eop7sorted[0]));
        assertTrue(equals(result[1], eop7sorted[1]));
        assertTrue(equals(result[2], eop7sorted[5]));
        assertTrue(equals(result[3], eop7sorted[6]));
    }

    @Test public void testArraycloneShrinkGapEOPEOP()
    {
        setupExpectedOpeningPriceStructArrays();
        ExpectedOpeningPriceStruct result[][] = null;
        assertNull(CollectionHelper.arraycloneShrinkGap(result, 2, 2));

        result = CollectionHelper.arraycloneShrinkGap(eopeop, 0, 2);
        assertNotNull(result);
        assertEquals(1, result.length);
        assertEquals(eopeop[2].length, result[0].length);
        for (int i = 0; i < eopeop[2].length; ++i)
        {
            assertTrue("i="+i, equals(eopeop[2][i], result[0][i]));
        }
    }

    private NBBOStruct nbbo6sorted[];
    private NBBOStruct nbbo7sorted[];
    private NBBOStruct nbbo6unsorted[];
    private NBBOStruct nbbo7unsorted[];
    private NBBOStruct nbbonbbo[][];

    private void setupNBBOStructArrays()
    {
        // Look for search errors: even or odd array size, sorted or unsorted
        nbbo6sorted = new NBBOStruct[6];
        nbbo7sorted = new NBBOStruct[7];
        nbbo6unsorted = new NBBOStruct[6];
        nbbo7unsorted = new NBBOStruct[7];
        for (int i = 0; i < nbbo7sorted.length; ++i)
        {
            if (i < nbbo6sorted.length)
            {
                nbbo6sorted[i] = new NBBOStruct();
                nbbo6sorted[i].productKeys = new ProductKeysStruct();
                nbbo6sorted[i].productKeys.productKey = sorted6keys[i];

                nbbo6unsorted[i] = new NBBOStruct();
                nbbo6unsorted[i].productKeys = new ProductKeysStruct();
                nbbo6unsorted[i].productKeys.productKey = unsorted6keys[i];
            }
            nbbo7sorted[i] = new NBBOStruct();
            nbbo7sorted[i].productKeys = new ProductKeysStruct();
            nbbo7sorted[i].productKeys.productKey = sorted7keys[i];

            nbbo7unsorted[i] = new NBBOStruct();
            nbbo7unsorted[i].productKeys = new ProductKeysStruct();
            nbbo7unsorted[i].productKeys.productKey = unsorted7keys[i];
        }

        // Two dimensional array, total of 7 elements so we can use sorted7keys
        nbbonbbo = new NBBOStruct[3][];
        nbbonbbo[0] = new NBBOStruct[2];
        nbbonbbo[1] = new NBBOStruct[3];
        nbbonbbo[2] = new NBBOStruct[2];
        int index = 0;
        for (int one = 0; one < nbbonbbo.length; ++one)
        {
            for (int two = 0; two < nbbonbbo[one].length; ++two)
            {
                nbbonbbo[one][two] = new NBBOStruct();
                nbbonbbo[one][two].productKeys = new ProductKeysStruct();
                nbbonbbo[one][two].productKeys.productKey = sorted7keys[index++];
            }
        }
    }

    private boolean equals(NBBOStruct a, NBBOStruct b)
    {
        return a.productKeys.productKey == b.productKeys.productKey;
    }

    @Test public void testBinarySearchNBBO()
    {
        setupNBBOStructArrays();
        for (int i = 0; i < nbbo7sorted.length; ++i)
        {
            String name = "i=" + i;
            if (i < nbbo6sorted.length)
            {
                assertEquals(name, i, CollectionHelper
                        .binarySearch(nbbo6sorted, sorted7keys[i]));
            }
            assertEquals(name, i, CollectionHelper
                    .binarySearch(nbbo7sorted, sorted7keys[i]));
        }

        for (int key : missingKeys)
        {
            String name = "key=" + key;
            assertTrue(name, 0 > CollectionHelper.binarySearch(nbbo6sorted, key));
            assertTrue(name, 0 > CollectionHelper.binarySearch(nbbo7sorted, key));
        }

        for (int length = 1; length <= nbbo7sorted.length; ++length)
        {
            String name1 = "length=" + length;
            for (int i = 0; i < length; i++)
            {
                String name2 = name1 + " i=" + i;
                assertEquals(name2, i, CollectionHelper
                        .binarySearch(nbbo7sorted, sorted7keys[i], length));
            }
        }
    }

    @Test public void testSortNBBO()
    {
        setupNBBOStructArrays();
        CollectionHelper.sort(nbbo6unsorted);
        for (int i = 0; i < nbbo6unsorted.length; ++i)
        {
            assertTrue("i=" + i, equals(nbbo6sorted[i], nbbo6unsorted[i]));
        }
        CollectionHelper.sort(nbbo7unsorted);
        for (int i = 0; i < nbbo7unsorted.length; ++i)
        {
            assertTrue("i=" + i, equals(nbbo7sorted[i], nbbo7sorted[i]));
        }

        // Now that the "unsorted" arrays have been sorted, we can use them as
        // a sorted reference after we try sorting the already-sorted arrays.

        CollectionHelper.sort(nbbo6sorted);
        for (int i = 0; i < nbbo6unsorted.length; ++i)
        {
            assertTrue("i=" + i, equals(nbbo6sorted[i], nbbo6unsorted[i]));
        }
        CollectionHelper.sort(nbbo7sorted);
        for (int i = 0; i < nbbo7unsorted.length; ++i)
        {
            assertTrue("i=" + i, equals(nbbo7sorted[i], nbbo7unsorted[i]));
        }
    }

    @Test public void testArraycloneNBBO()
    {
        setupNBBOStructArrays();
        NBBOStruct result[] = CollectionHelper.arrayclone(nbbo6sorted[0]);
        assertEquals(1, result.length);
        assertTrue(equals(result[0], nbbo6sorted[0]));

        result = CollectionHelper.arrayclone((NBBOStruct[]) null);
        assertNull(result);

        result = CollectionHelper.arrayclone(nbbo7unsorted);
        assertEquals(nbbo7unsorted.length, result.length);
        for (int i = 0; i < result.length; ++i)
        {
            assertTrue("i=" + i, equals(result[i], nbbo7unsorted[i]));
        }

        int toSize = nbbo7sorted.length+1;
        result = CollectionHelper.arrayclone(nbbo7sorted, toSize);
        assertEquals(toSize, result.length);
        for (int i = 0; i < nbbo7sorted.length; ++i)
        {
            assertTrue("i=" + i, equals(result[i], nbbo7sorted[i]));
        }
        assertNull(result[result.length-1]);

        toSize = 6;
        int fromSize = 5;
        int fromOffset = 1;
        result = CollectionHelper.arrayclone(nbbo7unsorted, fromOffset,
                    fromSize, toSize);
        assertEquals(toSize, result.length);
        for (int i = 0; i < fromSize; ++i)
        {
            assertTrue("i=" + i, equals(nbbo7unsorted[i+fromOffset], result[i]));
        }
    }

    @Test public void testArraycloneCombineNBBO()
    {
        setupNBBOStructArrays();
        // Method arguments are
        // from - source array
        // startOffset - first copy from[startOffset..from.length-1]
        // endOffset - next copy from[0..endOffset]
        // toSize - size of result, must be
        //     at least from.length-startOffset + endOffset+1
        NBBOStruct result[];
        result = CollectionHelper.arraycloneCombine(nbbo7sorted, 5, 1, 4);
        assertEquals(4, result.length);
        assertTrue(equals(nbbo7sorted[5], result[0]));
        assertTrue(equals(nbbo7sorted[6], result[1]));
        assertTrue(equals(nbbo7sorted[0], result[2]));
        assertNull(result[3]);
    }

    @Test public void testArraycloneExpandGapNBBO()
    {
        setupNBBOStructArrays();

        // As implemented, arraycloneExpandGap makes sense when fromOffset == 0
        int gapLength = 3;
        int toSize = nbbo7unsorted.length+gapLength;
        NBBOStruct result[] = CollectionHelper.arraycloneExpandGap(
                nbbo7unsorted, 0, nbbo7unsorted.length,
                toSize, 2, gapLength);

        assertEquals(toSize, result.length);
        assertTrue(equals(nbbo7unsorted[0], result[0]));
        assertTrue(equals(nbbo7unsorted[1], result[1]));
        assertNull(result[2]);
        assertNull(result[3]);
        assertNull(result[4]);
        assertTrue(equals(nbbo7unsorted[2], result[5]));
        assertTrue(equals(nbbo7unsorted[3], result[6]));
        assertTrue(equals(nbbo7unsorted[4], result[7]));
        assertTrue(equals(nbbo7unsorted[5], result[8]));
        assertTrue(equals(nbbo7unsorted[6], result[9]));
    }

    @Test public void testArraycloneNBBONBBO()
    {
        setupNBBOStructArrays();
        NBBOStruct nullMatrix[][] = null;
        NBBOStruct result[][];

        result = CollectionHelper.arrayclone(nullMatrix);
        assertNull(result);

        result = CollectionHelper.arrayclone(nbbonbbo);
        assertNotNull(result);
        assertEquals(nbbonbbo.length, result.length);
        for (int one = 0; one < nbbonbbo.length; ++one)
        {
            assertEquals("["+one+"]", nbbonbbo[one].length, result[one].length);
            for (int two = 0; two < nbbonbbo[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(nbbonbbo[one][two], result[one][two]));
            }
        }

        result = CollectionHelper.arrayclone(nullMatrix, 1);
        assertNull(result);

        result = CollectionHelper.arrayclone(nbbonbbo, nbbonbbo.length+1);
        assertNotNull(result);
        assertEquals(nbbonbbo.length+1, result.length);
        assertNull(result[result.length-1]);
        for (int one = 0; one < nbbonbbo.length; ++one)
        {
            assertEquals("["+one+"]", nbbonbbo[one].length, result[one].length);
            for (int two = 0; two < nbbonbbo[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(nbbonbbo[one][two], result[one][two]));
            }
        }

        result = CollectionHelper.arrayclone(nullMatrix, 1, 1, 1);
        assertNull(result);

        result = CollectionHelper.arrayclone(nbbonbbo, 2, 1, 1);
        assertNotNull(result);
        assertEquals(1, result.length);
        for (int two = 0; two < nbbonbbo[2].length; ++two)
        {
            assertTrue("[][" + two + "]", equals(nbbonbbo[2][two], result[0][two]));
        }
    }

    @Test public void testArraycloneDimensionsNBBONBBO()
    {
        setupNBBOStructArrays();
        NBBOStruct result[][] = null;

        assertNull(CollectionHelper.arraycloneDimensions(result));

        result = CollectionHelper.arraycloneDimensions(nbbonbbo);
        assertNotNull(result);
        assertEquals(nbbonbbo.length, result.length);
        for (int one = 0; one < nbbonbbo.length; ++one)
        {
            assertEquals("["+one+"]", nbbonbbo[one].length, result[one].length);
            for (int two = 0; two < nbbonbbo[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(nbbonbbo[one][two], result[one][two]));
            }
        }
    }

    @Test public void testArraycloneCombineNBBONBBO()
    {
        setupNBBOStructArrays();
        NBBOStruct nullMatrix[][] = null;
        assertNull(CollectionHelper.arraycloneCombine(nullMatrix, 3, 3, 3));

        NBBOStruct result[][];
        // Copy nbbonbbo[1..2] to result[0..1], nbbonbbo[0..1] to result[2..3]
        result = CollectionHelper.arraycloneCombine(nbbonbbo, 1, 2, 4);
        assertNotNull(result);
        assertEquals(4, result.length);
        for (int i = 0; i < nbbonbbo[1].length; ++i)
        {
            assertTrue("i="+i, equals(nbbonbbo[1][i], result[0][i]));
            assertTrue("i="+i, equals(nbbonbbo[1][i], result[3][i]));
        }
        for (int i = 0; i < nbbonbbo[2].length; ++i)
        {
            assertTrue("i="+i, equals(nbbonbbo[2][i], result[1][i]));
        }
        for (int i = 0; i < nbbonbbo[0].length; ++i)
        {
            assertTrue("i="+i, equals(nbbonbbo[0][i], result[2][i]));
        }
    }

    @Test public void testArraycloneExpandGapNBBONBBO()
    {
        setupNBBOStructArrays();
        NBBOStruct nullMatrix[][] = null;
        assertNull(CollectionHelper.arraycloneExpandGap(nullMatrix,5,5,5,5,5));

        // As implemented, arraycloneExpandGap makes sense when fromOffset == 0
        int gapLength = 2;
        int toSize = nbbonbbo.length + gapLength;
        NBBOStruct result[][] = CollectionHelper.arraycloneExpandGap(
                nbbonbbo, 0, nbbonbbo.length, toSize, 1, gapLength);
        assertNotNull(result);
        assertEquals(toSize, result.length);

        int r = 0;  // index into result[]
        for (int i = 0; i < nbbonbbo[0].length; ++i)
        {
            assertTrue("i=" + i, equals(nbbonbbo[0][i], result[0][i]));
        }
        r++;
        for (int i = 0; i < gapLength; ++i)
        {
            assertNull(result[r++]);
        }
        for (int b = 1; b < nbbonbbo.length; ++b)
        {
            for (int i = 0; i < nbbonbbo[b].length; ++i)
            {
                assertTrue("["+r+"]["+i+"]", equals(nbbonbbo[b][i], result[r][i]));
            }
            ++r;
        }
    }

    @Test public void testArraycloneShrinkGapNBBO()
    {
        setupNBBOStructArrays();
        NBBOStruct result[] = null;
        assertNull(CollectionHelper.arraycloneShrinkGap(result, 2, 2));

        result = CollectionHelper.arraycloneShrinkGap(nbbo7sorted, 2, 3);
        assertNotNull(result);
        assertEquals(4, result.length);
        assertTrue(equals(result[0], nbbo7sorted[0]));
        assertTrue(equals(result[1], nbbo7sorted[1]));
        assertTrue(equals(result[2], nbbo7sorted[5]));
        assertTrue(equals(result[3], nbbo7sorted[6]));
    }

    @Test public void testArraycloneShrinkGapNBBONBBO()
    {
        setupNBBOStructArrays();
        NBBOStruct result[][] = null;
        assertNull(CollectionHelper.arraycloneShrinkGap(result, 2, 2));

        result = CollectionHelper.arraycloneShrinkGap(nbbonbbo, 0, 2);
        assertNotNull(result);
        assertEquals(1, result.length);
        assertEquals(nbbonbbo[2].length, result[0].length);
        for (int i = 0; i < nbbonbbo[2].length; ++i)
        {
            assertTrue("i="+i, equals(nbbonbbo[2][i], result[0][i]));
        }
    }

    private SessionProductStruct sp6sorted[];
    private SessionProductStruct sp7sorted[];
    private SessionProductStruct sp6unsorted[];
    private SessionProductStruct sp7unsorted[];
    private SessionProductStruct spsp[][];

    private void setupSessionProductStructArrays()
    {
        // Look for search errors: even or odd array size, sorted or unsorted
        sp6sorted = new SessionProductStruct[6];
        sp7sorted = new SessionProductStruct[7];
        sp6unsorted = new SessionProductStruct[6];
        sp7unsorted = new SessionProductStruct[7];
        for (int i = 0; i < sp7sorted.length; ++i)
        {
            if (i < sp6sorted.length)
            {
                sp6sorted[i] = new SessionProductStruct();
                sp6sorted[i].productStruct = new ProductStruct();
                sp6sorted[i].productStruct.productKeys = new ProductKeysStruct();
                sp6sorted[i].productStruct.productKeys.productKey =
                        sorted6keys[i];

                sp6unsorted[i] = new SessionProductStruct();
                sp6unsorted[i].productStruct = new ProductStruct();
                sp6unsorted[i].productStruct.productKeys =
                        new ProductKeysStruct();
                sp6unsorted[i].productStruct.productKeys.productKey =
                        unsorted6keys[i];
            }
            sp7sorted[i] = new SessionProductStruct();
            sp7sorted[i].productStruct = new ProductStruct();
            sp7sorted[i].productStruct.productKeys = new ProductKeysStruct();
            sp7sorted[i].productStruct.productKeys.productKey = sorted7keys[i];

            sp7unsorted[i] = new SessionProductStruct();
            sp7unsorted[i].productStruct = new ProductStruct();
            sp7unsorted[i].productStruct.productKeys = new ProductKeysStruct();
            sp7unsorted[i].productStruct.productKeys.productKey =
                    unsorted7keys[i];
        }

        // Two dimensional array, total of 7 elements so we can use sorted7keys
        spsp = new SessionProductStruct[3][];
        spsp[0] = new SessionProductStruct[2];
        spsp[1] = new SessionProductStruct[3];
        spsp[2] = new SessionProductStruct[2];
        int index = 0;
        for (int one = 0; one < spsp.length; ++one)
        {
            for (int two = 0; two < spsp[one].length; ++two)
            {
                spsp[one][two] = new SessionProductStruct();
                spsp[one][two].productStruct = new ProductStruct();
                spsp[one][two].productStruct.productKeys =
                        new ProductKeysStruct();
                spsp[one][two].productStruct.productKeys.productKey =
                        sorted7keys[index++];
            }
        }
    }

    private boolean equals(SessionProductStruct a, SessionProductStruct b)
    {
        return a.productStruct.productKeys.productKey
            == b.productStruct.productKeys.productKey;
    }

    @Test public void testBinarySearchSP()
    {
        setupSessionProductStructArrays();
        for (int i = 0; i < sp7sorted.length; ++i)
        {
            String name = "i=" + i;
            if (i < sp6sorted.length)
            {
                assertEquals(name, i, CollectionHelper
                        .binarySearch(sp6sorted, sorted7keys[i]));
            }
            assertEquals(name, i, CollectionHelper
                    .binarySearch(sp7sorted, sorted7keys[i]));
        }

        for (int key : missingKeys)
        {
            String name = "key=" + key;
            assertTrue(name, 0 > CollectionHelper.binarySearch(sp6sorted, key));
            assertTrue(name, 0 > CollectionHelper.binarySearch(sp7sorted, key));
        }

        for (int length = 1; length <= sp7sorted.length; ++length)
        {
            String name1 = "length=" + length;
            for (int i = 0; i < length; i++)
            {
                String name2 = name1 + " i=" + i;
                assertEquals(name2, i, CollectionHelper
                        .binarySearch(sp7sorted, sorted7keys[i], length));
            }
        }
    }

    @Test public void testSortSP()
    {
        setupSessionProductStructArrays();
        CollectionHelper.sort(sp6unsorted);
        for (int i = 0; i < sp6unsorted.length; ++i)
        {
            assertTrue("i=" + i, equals(sp6sorted[i], sp6unsorted[i]));
        }
        CollectionHelper.sort(sp7unsorted);
        for (int i = 0; i < sp7unsorted.length; ++i)
        {
            assertTrue("i=" + i, equals(sp7sorted[i], sp7sorted[i]));
        }

        // Now that the "unsorted" arrays have been sorted, we can use them as
        // a sorted reference after we try sorting the already-sorted arrays.

        CollectionHelper.sort(sp6sorted);
        for (int i = 0; i < sp6unsorted.length; ++i)
        {
            assertTrue("i=" + i, equals(sp6sorted[i], sp6unsorted[i]));
        }
        CollectionHelper.sort(sp7sorted);
        for (int i = 0; i < sp7unsorted.length; ++i)
        {
            assertTrue("i=" + i, equals(sp7sorted[i], sp7unsorted[i]));
        }
    }

    @Test public void testArraycloneSP()
    {
        setupSessionProductStructArrays();
        SessionProductStruct result[] = CollectionHelper.arrayclone(sp6sorted[0]);
        assertEquals(1, result.length);
        assertTrue(equals(result[0], sp6sorted[0]));

        result = CollectionHelper.arrayclone((SessionProductStruct[]) null);
        assertNull(result);

        result = CollectionHelper.arrayclone(sp7unsorted);
        assertEquals(sp7unsorted.length, result.length);
        for (int i = 0; i < result.length; ++i)
        {
            assertTrue("i=" + i, equals(result[i], sp7unsorted[i]));
        }

        int toSize = sp7sorted.length+1;
        result = CollectionHelper.arrayclone(sp7sorted, toSize);
        assertEquals(toSize, result.length);
        for (int i = 0; i < sp7sorted.length; ++i)
        {
            assertTrue("i=" + i, equals(result[i], sp7sorted[i]));
        }
        assertNull(result[result.length-1]);

        toSize = 6;
        int fromSize = 5;
        int fromOffset = 1;
        result = CollectionHelper.arrayclone(sp7unsorted, fromOffset,
                    fromSize, toSize);
        assertEquals(toSize, result.length);
        for (int i = 0; i < fromSize; ++i)
        {
            assertTrue("i=" + i, equals(sp7unsorted[i+fromOffset], result[i]));
        }
    }

    @Test public void testArraycloneCombineSP()
    {
        setupSessionProductStructArrays();
        // Method arguments are
        // from - source array
        // startOffset - first copy from[startOffset..from.length-1]
        // endOffset - next copy from[0..endOffset]
        // toSize - size of result, must be
        //     at least from.length-startOffset + endOffset+1
        SessionProductStruct result[];
        result = CollectionHelper.arraycloneCombine(sp7sorted, 5, 1, 4);
        assertEquals(4, result.length);
        assertTrue(equals(sp7sorted[5], result[0]));
        assertTrue(equals(sp7sorted[6], result[1]));
        assertTrue(equals(sp7sorted[0], result[2]));
        assertNull(result[3]);
    }

    @Test public void testArraycloneExpandGapSP()
    {
        setupSessionProductStructArrays();

        // As implemented, arraycloneExpandGap makes sense when fromOffset == 0
        int gapLength = 3;
        int toSize = sp7unsorted.length+gapLength;
        SessionProductStruct result[] = CollectionHelper.arraycloneExpandGap(
                sp7unsorted, 0, sp7unsorted.length,
                toSize, 2, gapLength);

        assertEquals(toSize, result.length);
        assertTrue(equals(sp7unsorted[0], result[0]));
        assertTrue(equals(sp7unsorted[1], result[1]));
        assertNull(result[2]);
        assertNull(result[3]);
        assertNull(result[4]);
        assertTrue(equals(sp7unsorted[2], result[5]));
        assertTrue(equals(sp7unsorted[3], result[6]));
        assertTrue(equals(sp7unsorted[4], result[7]));
        assertTrue(equals(sp7unsorted[5], result[8]));
        assertTrue(equals(sp7unsorted[6], result[9]));
    }

    @Test public void testArraycloneSPSP()
    {
        setupSessionProductStructArrays();
        SessionProductStruct nullMatrix[][] = null;
        SessionProductStruct result[][];

        result = CollectionHelper.arrayclone(nullMatrix);
        assertNull(result);

        result = CollectionHelper.arrayclone(spsp);
        assertNotNull(result);
        assertEquals(spsp.length, result.length);
        for (int one = 0; one < spsp.length; ++one)
        {
            assertEquals("["+one+"]", spsp[one].length, result[one].length);
            for (int two = 0; two < spsp[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(spsp[one][two], result[one][two]));
            }
        }

        result = CollectionHelper.arrayclone(nullMatrix, 1);
        assertNull(result);

        result = CollectionHelper.arrayclone(spsp, spsp.length+1);
        assertNotNull(result);
        assertEquals(spsp.length+1, result.length);
        assertNull(result[result.length-1]);
        for (int one = 0; one < spsp.length; ++one)
        {
            assertEquals("["+one+"]", spsp[one].length, result[one].length);
            for (int two = 0; two < spsp[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(spsp[one][two], result[one][two]));
            }
        }

        result = CollectionHelper.arrayclone(nullMatrix, 1, 1, 1);
        assertNull(result);

        result = CollectionHelper.arrayclone(spsp, 2, 1, 1);
        assertNotNull(result);
        assertEquals(1, result.length);
        for (int two = 0; two < spsp[2].length; ++two)
        {
            assertTrue("[][" + two + "]", equals(spsp[2][two], result[0][two]));
        }
    }

    @Test public void testArraycloneDimensionsSPSP()
    {
        setupSessionProductStructArrays();
        SessionProductStruct result[][] = null;

        assertNull(CollectionHelper.arraycloneDimensions(result));

        result = CollectionHelper.arraycloneDimensions(spsp);
        assertNotNull(result);
        assertEquals(spsp.length, result.length);
        for (int one = 0; one < spsp.length; ++one)
        {
            assertEquals("["+one+"]", spsp[one].length, result[one].length);
            for (int two = 0; two < spsp[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(spsp[one][two], result[one][two]));
            }
        }
    }

    @Test public void testArraycloneCombineSPSP()
    {
        setupSessionProductStructArrays();
        SessionProductStruct nullMatrix[][] = null;
        assertNull(CollectionHelper.arraycloneCombine(nullMatrix, 3, 3, 3));

        SessionProductStruct result[][];
        // Copy spsp[1..2] to result[0..1], spsp[0..1] to result[2..3]
        result = CollectionHelper.arraycloneCombine(spsp, 1, 2, 4);
        assertNotNull(result);
        assertEquals(4, result.length);
        for (int i = 0; i < spsp[1].length; ++i)
        {
            assertTrue("i="+i, equals(spsp[1][i], result[0][i]));
            assertTrue("i="+i, equals(spsp[1][i], result[3][i]));
        }
        for (int i = 0; i < spsp[2].length; ++i)
        {
            assertTrue("i="+i, equals(spsp[2][i], result[1][i]));
        }
        for (int i = 0; i < spsp[0].length; ++i)
        {
            assertTrue("i="+i, equals(spsp[0][i], result[2][i]));
        }
    }

    @Test public void testArraycloneExpandGapSPSP()
    {
        setupSessionProductStructArrays();
        SessionProductStruct nullMatrix[][] = null;
        assertNull(CollectionHelper.arraycloneExpandGap(nullMatrix,5,5,5,5,5));

        // As implemented, arraycloneExpandGap makes sense when fromOffset == 0
        int gapLength = 2;
        int toSize = spsp.length + gapLength;
        SessionProductStruct result[][] = CollectionHelper.arraycloneExpandGap(
                spsp, 0, spsp.length, toSize, 1, gapLength);
        assertNotNull(result);
        assertEquals(toSize, result.length);

        int r = 0;  // index into result[]
        for (int i = 0; i < spsp[0].length; ++i)
        {
            assertTrue("i=" + i, equals(spsp[0][i], result[0][i]));
        }
        r++;
        for (int i = 0; i < gapLength; ++i)
        {
            assertNull(result[r++]);
        }
        for (int b = 1; b < spsp.length; ++b)
        {
            for (int i = 0; i < spsp[b].length; ++i)
            {
                assertTrue("["+r+"]["+i+"]", equals(spsp[b][i], result[r][i]));
            }
            ++r;
        }
    }

    @Test public void testArraycloneShrinkGapSP()
    {
        setupSessionProductStructArrays();
        SessionProductStruct result[] = null;
        assertNull(CollectionHelper.arraycloneShrinkGap(result, 2, 2));

        result = CollectionHelper.arraycloneShrinkGap(sp7sorted, 2, 3);
        assertNotNull(result);
        assertEquals(4, result.length);
        assertTrue(equals(result[0], sp7sorted[0]));
        assertTrue(equals(result[1], sp7sorted[1]));
        assertTrue(equals(result[2], sp7sorted[5]));
        assertTrue(equals(result[3], sp7sorted[6]));
    }

    @Test public void testArraycloneShrinkGapSPSP()
    {
        setupSessionProductStructArrays();
        SessionProductStruct result[][] = null;
        assertNull(CollectionHelper.arraycloneShrinkGap(result, 2, 2));

        result = CollectionHelper.arraycloneShrinkGap(spsp, 0, 2);
        assertNotNull(result);
        assertEquals(1, result.length);
        assertEquals(spsp[2].length, result[0].length);
        for (int i = 0; i < spsp[2].length; ++i)
        {
            assertTrue("i="+i, equals(spsp[2][i], result[0][i]));
        }
    }

    private SessionClassStruct sc6sorted[];
    private SessionClassStruct sc7sorted[];
    private SessionClassStruct sc6unsorted[];
    private SessionClassStruct sc7unsorted[];
    private SessionClassStruct scsc[][];

    private void setupSessionClassStructArrays()
    {
        // Look for search errors: even or odd array size, sorted or unsorted
        sc6sorted = new SessionClassStruct[6];
        sc7sorted = new SessionClassStruct[7];
        sc6unsorted = new SessionClassStruct[6];
        sc7unsorted = new SessionClassStruct[7];
        for (int i = 0; i < sc7sorted.length; ++i)
        {
            if (i < sc6sorted.length)
            {
                sc6sorted[i] = new SessionClassStruct();
                sc6sorted[i].classStruct = new ClassStruct();
                sc6sorted[i].classStruct.classKey = sorted6keys[i];

                sc6unsorted[i] = new SessionClassStruct();
                sc6unsorted[i].classStruct = new ClassStruct();
                sc6unsorted[i].classStruct.classKey = unsorted6keys[i];
            }
            sc7sorted[i] = new SessionClassStruct();
            sc7sorted[i].classStruct = new ClassStruct();
            sc7sorted[i].classStruct.classKey = sorted7keys[i];

            sc7unsorted[i] = new SessionClassStruct();
            sc7unsorted[i].classStruct = new ClassStruct();
            sc7unsorted[i].classStruct.classKey = unsorted7keys[i];
        }

        // Two dimensional array, total of 7 elements so we can use sorted7keys
        scsc = new SessionClassStruct[3][];
        scsc[0] = new SessionClassStruct[2];
        scsc[1] = new SessionClassStruct[3];
        scsc[2] = new SessionClassStruct[2];
        int index = 0;
        for (int one = 0; one < scsc.length; ++one)
        {
            for (int two = 0; two < scsc[one].length; ++two)
            {
                scsc[one][two] = new SessionClassStruct();
                scsc[one][two].classStruct = new ClassStruct();
                scsc[one][two].classStruct.classKey = sorted7keys[index++];
            }
        }
    }

    private boolean equals(SessionClassStruct a, SessionClassStruct b)
    {
        return a.classStruct.classKey == b.classStruct.classKey;
    }

    @Test public void testBinarySearchSC()
    {
        setupSessionClassStructArrays();
        for (int i = 0; i < sc7sorted.length; ++i)
        {
            String name = "i=" + i;
            if (i < sc6sorted.length)
            {
                assertEquals(name, i, CollectionHelper
                        .binarySearch(sc6sorted, sorted7keys[i]));
            }
            assertEquals(name, i, CollectionHelper
                    .binarySearch(sc7sorted, sorted7keys[i]));
        }

        for (int key : missingKeys)
        {
            String name = "key=" + key;
            assertTrue(name, 0 > CollectionHelper.binarySearch(sc6sorted, key));
            assertTrue(name, 0 > CollectionHelper.binarySearch(sc7sorted, key));
        }

        for (int length = 1; length <= sc7sorted.length; ++length)
        {
            String name1 = "length=" + length;
            for (int i = 0; i < length; i++)
            {
                String name2 = name1 + " i=" + i;
                assertEquals(name2, i, CollectionHelper
                        .binarySearch(sc7sorted, sorted7keys[i], length));
            }
        }
    }

    @Test public void testSortSC()
    {
        setupSessionClassStructArrays();
        CollectionHelper.sort(sc6unsorted);
        for (int i = 0; i < sc6unsorted.length; ++i)
        {
            assertTrue("i=" + i, equals(sc6sorted[i], sc6unsorted[i]));
        }
        CollectionHelper.sort(sc7unsorted);
        for (int i = 0; i < sc7unsorted.length; ++i)
        {
            assertTrue("i=" + i, equals(sc7sorted[i], sc7sorted[i]));
        }

        // Now that the "unsorted" arrays have been sorted, we can use them as
        // a sorted reference after we try sorting the already-sorted arrays.

        CollectionHelper.sort(sc6sorted);
        for (int i = 0; i < sc6unsorted.length; ++i)
        {
            assertTrue("i=" + i, equals(sc6sorted[i], sc6unsorted[i]));
        }
        CollectionHelper.sort(sc7sorted);
        for (int i = 0; i < sc7unsorted.length; ++i)
        {
            assertTrue("i=" + i, equals(sc7sorted[i], sc7unsorted[i]));
        }
    }

    @Test public void testArraycloneSC()
    {
        setupSessionClassStructArrays();
        SessionClassStruct result[] = CollectionHelper.arrayclone(sc6sorted[0]);
        assertEquals(1, result.length);
        assertTrue(equals(result[0], sc6sorted[0]));

        result = CollectionHelper.arrayclone((SessionClassStruct[]) null);
        assertNull(result);

        result = CollectionHelper.arrayclone(sc7unsorted);
        assertEquals(sc7unsorted.length, result.length);
        for (int i = 0; i < result.length; ++i)
        {
            assertTrue("i=" + i, equals(result[i], sc7unsorted[i]));
        }

        int toSize = sc7sorted.length+1;
        result = CollectionHelper.arrayclone(sc7sorted, toSize);
        assertEquals(toSize, result.length);
        for (int i = 0; i < sc7sorted.length; ++i)
        {
            assertTrue("i=" + i, equals(result[i], sc7sorted[i]));
        }
        assertNull(result[result.length-1]);

        toSize = 6;
        int fromSize = 5;
        int fromOffset = 1;
        result = CollectionHelper.arrayclone(sc7unsorted, fromOffset,
                    fromSize, toSize);
        assertEquals(toSize, result.length);
        for (int i = 0; i < fromSize; ++i)
        {
            assertTrue("i=" + i, equals(sc7unsorted[i+fromOffset], result[i]));
        }
    }

    @Test public void testArraycloneCombineSC()
    {
        setupSessionClassStructArrays();
        // Method arguments are
        // from - source array
        // startOffset - first copy from[startOffset..from.length-1]
        // endOffset - next copy from[0..endOffset]
        // toSize - size of result, must be
        //     at least from.length-startOffset + endOffset+1
        SessionClassStruct result[];
        result = CollectionHelper.arraycloneCombine(sc7sorted, 5, 1, 4);
        assertEquals(4, result.length);
        assertTrue(equals(sc7sorted[5], result[0]));
        assertTrue(equals(sc7sorted[6], result[1]));
        assertTrue(equals(sc7sorted[0], result[2]));
        assertNull(result[3]);
    }

    @Test public void testArraycloneExpandGapSC()
    {
        setupSessionClassStructArrays();

        // As implemented, arraycloneExpandGap makes sense when fromOffset == 0
        int gapLength = 3;
        int toSize = sc7unsorted.length+gapLength;
        SessionClassStruct result[] = CollectionHelper.arraycloneExpandGap(
                sc7unsorted, 0, sc7unsorted.length,
                toSize, 2, gapLength);

        assertEquals(toSize, result.length);
        assertTrue(equals(sc7unsorted[0], result[0]));
        assertTrue(equals(sc7unsorted[1], result[1]));
        assertNull(result[2]);
        assertNull(result[3]);
        assertNull(result[4]);
        assertTrue(equals(sc7unsorted[2], result[5]));
        assertTrue(equals(sc7unsorted[3], result[6]));
        assertTrue(equals(sc7unsorted[4], result[7]));
        assertTrue(equals(sc7unsorted[5], result[8]));
        assertTrue(equals(sc7unsorted[6], result[9]));
    }

    @Test public void testArraycloneSCSC()
    {
        setupSessionClassStructArrays();
        SessionClassStruct nullMatrix[][] = null;
        SessionClassStruct result[][];

        result = CollectionHelper.arrayclone(nullMatrix);
        assertNull(result);

        result = CollectionHelper.arrayclone(scsc);
        assertNotNull(result);
        assertEquals(scsc.length, result.length);
        for (int one = 0; one < scsc.length; ++one)
        {
            assertEquals("["+one+"]", scsc[one].length, result[one].length);
            for (int two = 0; two < scsc[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(scsc[one][two], result[one][two]));
            }
        }

        result = CollectionHelper.arrayclone(nullMatrix, 1);
        assertNull(result);

        result = CollectionHelper.arrayclone(scsc, scsc.length+1);
        assertNotNull(result);
        assertEquals(scsc.length+1, result.length);
        assertNull(result[result.length-1]);
        for (int one = 0; one < scsc.length; ++one)
        {
            assertEquals("["+one+"]", scsc[one].length, result[one].length);
            for (int two = 0; two < scsc[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(scsc[one][two], result[one][two]));
            }
        }

        result = CollectionHelper.arrayclone(nullMatrix, 1, 1, 1);
        assertNull(result);

        result = CollectionHelper.arrayclone(scsc, 2, 1, 1);
        assertNotNull(result);
        assertEquals(1, result.length);
        for (int two = 0; two < scsc[2].length; ++two)
        {
            assertTrue("[][" + two + "]", equals(scsc[2][two], result[0][two]));
        }
    }

    @Test public void testArraycloneDimensionsSCSC()
    {
        setupSessionClassStructArrays();
        SessionClassStruct result[][] = null;

        assertNull(CollectionHelper.arraycloneDimensions(result));

        result = CollectionHelper.arraycloneDimensions(scsc);
        assertNotNull(result);
        assertEquals(scsc.length, result.length);
        for (int one = 0; one < scsc.length; ++one)
        {
            assertEquals("["+one+"]", scsc[one].length, result[one].length);
            for (int two = 0; two < scsc[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(scsc[one][two], result[one][two]));
            }
        }
    }

    @Test public void testArraycloneCombineSCSC()
    {
        setupSessionClassStructArrays();
        SessionClassStruct nullMatrix[][] = null;
        assertNull(CollectionHelper.arraycloneCombine(nullMatrix, 3, 3, 3));

        SessionClassStruct result[][];
        // Copy scsc[1..2] to result[0..1], scsc[0..1] to result[2..3]
        result = CollectionHelper.arraycloneCombine(scsc, 1, 2, 4);
        assertNotNull(result);
        assertEquals(4, result.length);
        for (int i = 0; i < scsc[1].length; ++i)
        {
            assertTrue("i="+i, equals(scsc[1][i], result[0][i]));
            assertTrue("i="+i, equals(scsc[1][i], result[3][i]));
        }
        for (int i = 0; i < scsc[2].length; ++i)
        {
            assertTrue("i="+i, equals(scsc[2][i], result[1][i]));
        }
        for (int i = 0; i < scsc[0].length; ++i)
        {
            assertTrue("i="+i, equals(scsc[0][i], result[2][i]));
        }
    }

    @Test public void testArraycloneExpandGapSCSC()
    {
        setupSessionClassStructArrays();
        SessionClassStruct nullMatrix[][] = null;
        assertNull(CollectionHelper.arraycloneExpandGap(nullMatrix,5,5,5,5,5));

        // As implemented, arraycloneExpandGap makes sense when fromOffset == 0
        int gapLength = 2;
        int toSize = scsc.length + gapLength;
        SessionClassStruct result[][] = CollectionHelper.arraycloneExpandGap(
                scsc, 0, scsc.length, toSize, 1, gapLength);
        assertNotNull(result);
        assertEquals(toSize, result.length);

        int r = 0;  // index into result[]
        for (int i = 0; i < scsc[0].length; ++i)
        {
            assertTrue("i=" + i, equals(scsc[0][i], result[0][i]));
        }
        r++;
        for (int i = 0; i < gapLength; ++i)
        {
            assertNull(result[r++]);
        }
        for (int b = 1; b < scsc.length; ++b)
        {
            for (int i = 0; i < scsc[b].length; ++i)
            {
                assertTrue("["+r+"]["+i+"]", equals(scsc[b][i], result[r][i]));
            }
            ++r;
        }
    }

    @Test public void testArraycloneShrinkGapSC()
    {
        setupSessionClassStructArrays();
        SessionClassStruct result[] = null;
        assertNull(CollectionHelper.arraycloneShrinkGap(result, 2, 2));

        result = CollectionHelper.arraycloneShrinkGap(sc7sorted, 2, 3);
        assertNotNull(result);
        assertEquals(4, result.length);
        assertTrue(equals(result[0], sc7sorted[0]));
        assertTrue(equals(result[1], sc7sorted[1]));
        assertTrue(equals(result[2], sc7sorted[5]));
        assertTrue(equals(result[3], sc7sorted[6]));
    }

    @Test public void testArraycloneShrinkGapSCSC()
    {
        setupSessionClassStructArrays();
        SessionClassStruct result[][] = null;
        assertNull(CollectionHelper.arraycloneShrinkGap(result, 2, 2));

        result = CollectionHelper.arraycloneShrinkGap(scsc, 0, 2);
        assertNotNull(result);
        assertEquals(1, result.length);
        assertEquals(scsc[2].length, result[0].length);
        for (int i = 0; i < scsc[2].length; ++i)
        {
            assertTrue("i="+i, equals(scsc[2][i], result[0][i]));
        }
    }

    private ProductStruct prd6sorted[];
    private ProductStruct prd7sorted[];
    private ProductStruct prd6unsorted[];
    private ProductStruct prd7unsorted[];
    private ProductStruct prdprd[][];

    private void setupProductStructArrays()
    {
        // Look for search errors: even or odd array size, sorted or unsorted
        prd6sorted = new ProductStruct[6];
        prd7sorted = new ProductStruct[7];
        prd6unsorted = new ProductStruct[6];
        prd7unsorted = new ProductStruct[7];
        for (int i = 0; i < prd7sorted.length; ++i)
        {
            if (i < prd6sorted.length)
            {
                prd6sorted[i] = new ProductStruct();
                prd6sorted[i].productKeys = new ProductKeysStruct();
                prd6sorted[i].productKeys.productKey = sorted6keys[i];

                prd6unsorted[i] = new ProductStruct();
                prd6unsorted[i].productKeys = new ProductKeysStruct();
                prd6unsorted[i].productKeys.productKey = unsorted6keys[i];
            }
            prd7sorted[i] = new ProductStruct();
            prd7sorted[i].productKeys = new ProductKeysStruct();
            prd7sorted[i].productKeys.productKey = sorted7keys[i];

            prd7unsorted[i] = new ProductStruct();
            prd7unsorted[i].productKeys = new ProductKeysStruct();
            prd7unsorted[i].productKeys.productKey = unsorted7keys[i];
        }

        // Two dimensional array, total of 7 elements so we can use sorted7keys
        prdprd = new ProductStruct[3][];
        prdprd[0] = new ProductStruct[2];
        prdprd[1] = new ProductStruct[3];
        prdprd[2] = new ProductStruct[2];
        int index = 0;
        for (int one = 0; one < prdprd.length; ++one)
        {
            for (int two = 0; two < prdprd[one].length; ++two)
            {
                prdprd[one][two] = new ProductStruct();
                prdprd[one][two].productKeys = new ProductKeysStruct();
                prdprd[one][two].productKeys.productKey = sorted7keys[index++];
            }
        }
    }

    private boolean equals(ProductStruct a, ProductStruct b)
    {
        return a.productKeys.productKey == b.productKeys.productKey;
    }

    @Test public void testBinarySearchPRD()
    {
        setupProductStructArrays();
        for (int i = 0; i < prd7sorted.length; ++i)
        {
            String name = "i=" + i;
            if (i < prd6sorted.length)
            {
                assertEquals(name, i, CollectionHelper
                        .binarySearch(prd6sorted, sorted7keys[i]));
            }
            assertEquals(name, i, CollectionHelper
                    .binarySearch(prd7sorted, sorted7keys[i]));
        }

        for (int key : missingKeys)
        {
            String name = "key=" + key;
            assertTrue(name, 0 > CollectionHelper.binarySearch(prd6sorted, key));
            assertTrue(name, 0 > CollectionHelper.binarySearch(prd7sorted, key));
        }

        for (int length = 1; length <= prd7sorted.length; ++length)
        {
            String name1 = "length=" + length;
            for (int i = 0; i < length; i++)
            {
                String name2 = name1 + " i=" + i;
                assertEquals(name2, i, CollectionHelper
                        .binarySearch(prd7sorted, sorted7keys[i], length));
            }
        }
    }

    @Test public void testSortPRD()
    {
        setupProductStructArrays();
        CollectionHelper.sort(prd6unsorted);
        for (int i = 0; i < prd6unsorted.length; ++i)
        {
            assertTrue("i=" + i, equals(prd6sorted[i], prd6unsorted[i]));
        }
        CollectionHelper.sort(prd7unsorted);
        for (int i = 0; i < prd7unsorted.length; ++i)
        {
            assertTrue("i=" + i, equals(prd7sorted[i], prd7sorted[i]));
        }

        // Now that the "unsorted" arrays have been sorted, we can use them as
        // a sorted reference after we try sorting the already-sorted arrays.

        CollectionHelper.sort(prd6sorted);
        for (int i = 0; i < prd6unsorted.length; ++i)
        {
            assertTrue("i=" + i, equals(prd6sorted[i], prd6unsorted[i]));
        }
        CollectionHelper.sort(prd7sorted);
        for (int i = 0; i < prd7unsorted.length; ++i)
        {
            assertTrue("i=" + i, equals(prd7sorted[i], prd7unsorted[i]));
        }
    }

    @Test public void testArrayclonePRD()
    {
        setupProductStructArrays();
        ProductStruct result[] = CollectionHelper.arrayclone(prd6sorted[0]);
        assertEquals(1, result.length);
        assertTrue(equals(result[0], prd6sorted[0]));

        result = CollectionHelper.arrayclone((ProductStruct[]) null);
        assertNull(result);

        result = CollectionHelper.arrayclone(prd7unsorted);
        assertEquals(prd7unsorted.length, result.length);
        for (int i = 0; i < result.length; ++i)
        {
            assertTrue("i=" + i, equals(result[i], prd7unsorted[i]));
        }

        int toSize = prd7sorted.length+1;
        result = CollectionHelper.arrayclone(prd7sorted, toSize);
        assertEquals(toSize, result.length);
        for (int i = 0; i < prd7sorted.length; ++i)
        {
            assertTrue("i=" + i, equals(result[i], prd7sorted[i]));
        }
        assertNull(result[result.length-1]);

        toSize = 6;
        int fromSize = 5;
        int fromOffset = 1;
        result = CollectionHelper.arrayclone(prd7unsorted, fromOffset,
                    fromSize, toSize);
        assertEquals(toSize, result.length);
        for (int i = 0; i < fromSize; ++i)
        {
            assertTrue("i=" + i, equals(prd7unsorted[i+fromOffset], result[i]));
        }
    }

    @Test public void testArraycloneCombinePRD()
    {
        setupProductStructArrays();
        // Method arguments are
        // from - source array
        // startOffset - first copy from[startOffset..from.length-1]
        // endOffset - next copy from[0..endOffset]
        // toSize - size of result, must be
        //     at least from.length-startOffset + endOffset+1
        ProductStruct result[];
        result = CollectionHelper.arraycloneCombine(prd7sorted, 5, 1, 4);
        assertEquals(4, result.length);
        assertTrue(equals(prd7sorted[5], result[0]));
        assertTrue(equals(prd7sorted[6], result[1]));
        assertTrue(equals(prd7sorted[0], result[2]));
        assertNull(result[3]);
    }

    @Test public void testArraycloneExpandGapPRD()
    {
        setupProductStructArrays();

        // As implemented, arraycloneExpandGap makes sense when fromOffset == 0
        int gapLength = 3;
        int toSize = prd7unsorted.length+gapLength;
        ProductStruct result[] = CollectionHelper.arraycloneExpandGap(
                prd7unsorted, 0, prd7unsorted.length,
                toSize, 2, gapLength);

        assertEquals(toSize, result.length);
        assertTrue(equals(prd7unsorted[0], result[0]));
        assertTrue(equals(prd7unsorted[1], result[1]));
        assertNull(result[2]);
        assertNull(result[3]);
        assertNull(result[4]);
        assertTrue(equals(prd7unsorted[2], result[5]));
        assertTrue(equals(prd7unsorted[3], result[6]));
        assertTrue(equals(prd7unsorted[4], result[7]));
        assertTrue(equals(prd7unsorted[5], result[8]));
        assertTrue(equals(prd7unsorted[6], result[9]));
    }

    @Test public void testArrayclonePRDPRD()
    {
        setupProductStructArrays();
        ProductStruct nullMatrix[][] = null;
        ProductStruct result[][];

        result = CollectionHelper.arrayclone(nullMatrix);
        assertNull(result);

        result = CollectionHelper.arrayclone(prdprd);
        assertNotNull(result);
        assertEquals(prdprd.length, result.length);
        for (int one = 0; one < prdprd.length; ++one)
        {
            assertEquals("["+one+"]", prdprd[one].length, result[one].length);
            for (int two = 0; two < prdprd[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(prdprd[one][two], result[one][two]));
            }
        }

        result = CollectionHelper.arrayclone(nullMatrix, 1);
        assertNull(result);

        result = CollectionHelper.arrayclone(prdprd, prdprd.length+1);
        assertNotNull(result);
        assertEquals(prdprd.length+1, result.length);
        assertNull(result[result.length-1]);
        for (int one = 0; one < prdprd.length; ++one)
        {
            assertEquals("["+one+"]", prdprd[one].length, result[one].length);
            for (int two = 0; two < prdprd[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(prdprd[one][two], result[one][two]));
            }
        }

        result = CollectionHelper.arrayclone(nullMatrix, 1, 1, 1);
        assertNull(result);

        result = CollectionHelper.arrayclone(prdprd, 2, 1, 1);
        assertNotNull(result);
        assertEquals(1, result.length);
        for (int two = 0; two < prdprd[2].length; ++two)
        {
            assertTrue("[][" + two + "]", equals(prdprd[2][two], result[0][two]));
        }
    }

    @Test public void testArraycloneDimensionsPRDPRD()
    {
        setupProductStructArrays();
        ProductStruct result[][] = null;

        assertNull(CollectionHelper.arraycloneDimensions(result));

        result = CollectionHelper.arraycloneDimensions(prdprd);
        assertNotNull(result);
        assertEquals(prdprd.length, result.length);
        for (int one = 0; one < prdprd.length; ++one)
        {
            assertEquals("["+one+"]", prdprd[one].length, result[one].length);
            for (int two = 0; two < prdprd[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(prdprd[one][two], result[one][two]));
            }
        }
    }

    @Test public void testArraycloneCombinePRDPRD()
    {
        setupProductStructArrays();
        ProductStruct nullMatrix[][] = null;
        assertNull(CollectionHelper.arraycloneCombine(nullMatrix, 3, 3, 3));

        ProductStruct result[][];
        // Copy prdprd[1..2] to result[0..1], prdprd[0..1] to result[2..3]
        result = CollectionHelper.arraycloneCombine(prdprd, 1, 2, 4);
        assertNotNull(result);
        assertEquals(4, result.length);
        for (int i = 0; i < prdprd[1].length; ++i)
        {
            assertTrue("i="+i, equals(prdprd[1][i], result[0][i]));
            assertTrue("i="+i, equals(prdprd[1][i], result[3][i]));
        }
        for (int i = 0; i < prdprd[2].length; ++i)
        {
            assertTrue("i="+i, equals(prdprd[2][i], result[1][i]));
        }
        for (int i = 0; i < prdprd[0].length; ++i)
        {
            assertTrue("i="+i, equals(prdprd[0][i], result[2][i]));
        }
    }

    @Test public void testArraycloneExpandGapPRDPRD()
    {
        setupProductStructArrays();
        ProductStruct nullMatrix[][] = null;
        assertNull(CollectionHelper.arraycloneExpandGap(nullMatrix,5,5,5,5,5));

        // As implemented, arraycloneExpandGap makes sense when fromOffset == 0
        int gapLength = 2;
        int toSize = prdprd.length + gapLength;
        ProductStruct result[][] = CollectionHelper.arraycloneExpandGap(
                prdprd, 0, prdprd.length, toSize, 1, gapLength);
        assertNotNull(result);
        assertEquals(toSize, result.length);

        int r = 0;  // index into result[]
        for (int i = 0; i < prdprd[0].length; ++i)
        {
            assertTrue("i=" + i, equals(prdprd[0][i], result[0][i]));
        }
        r++;
        for (int i = 0; i < gapLength; ++i)
        {
            assertNull(result[r++]);
        }
        for (int b = 1; b < prdprd.length; ++b)
        {
            for (int i = 0; i < prdprd[b].length; ++i)
            {
                assertTrue("["+r+"]["+i+"]",equals(prdprd[b][i], result[r][i]));
            }
            ++r;
        }
    }

    @Test public void testArraycloneShrinkGapPRD()
    {
        setupProductStructArrays();
        ProductStruct result[] = null;
        assertNull(CollectionHelper.arraycloneShrinkGap(result, 2, 2));

        result = CollectionHelper.arraycloneShrinkGap(prd7sorted, 2, 3);
        assertNotNull(result);
        assertEquals(4, result.length);
        assertTrue(equals(result[0], prd7sorted[0]));
        assertTrue(equals(result[1], prd7sorted[1]));
        assertTrue(equals(result[2], prd7sorted[5]));
        assertTrue(equals(result[3], prd7sorted[6]));
    }

    @Test public void testArraycloneShrinkGapPRDPRD()
    {
        setupProductStructArrays();
        ProductStruct result[][] = null;
        assertNull(CollectionHelper.arraycloneShrinkGap(result, 2, 2));

        result = CollectionHelper.arraycloneShrinkGap(prdprd, 0, 2);
        assertNotNull(result);
        assertEquals(1, result.length);
        assertEquals(prdprd[2].length, result[0].length);
        for (int i = 0; i < prdprd[2].length; ++i)
        {
            assertTrue("i="+i, equals(prdprd[2][i], result[0][i]));
        }
    }

    private ClassStruct cl6sorted[];
    private ClassStruct cl7sorted[];
    private ClassStruct cl6unsorted[];
    private ClassStruct cl7unsorted[];
    private ClassStruct clcl[][];

    private void setupClassStructArrays()
    {
        // Look for search errors: even or odd array size, sorted or unsorted
        cl6sorted = new ClassStruct[6];
        cl7sorted = new ClassStruct[7];
        cl6unsorted = new ClassStruct[6];
        cl7unsorted = new ClassStruct[7];
        for (int i = 0; i < cl7sorted.length; ++i)
        {
            if (i < cl6sorted.length)
            {
                cl6sorted[i] = new ClassStruct();
                cl6sorted[i].classKey = sorted6keys[i];

                cl6unsorted[i] = new ClassStruct();
                cl6unsorted[i].classKey = unsorted6keys[i];
            }
            cl7sorted[i] = new ClassStruct();
            cl7sorted[i].classKey = sorted7keys[i];

            cl7unsorted[i] = new ClassStruct();
            cl7unsorted[i].classKey = unsorted7keys[i];
        }

        // Two dimensional array, total of 7 elements so we can use sorted7keys
        clcl = new ClassStruct[3][];
        clcl[0] = new ClassStruct[2];
        clcl[1] = new ClassStruct[3];
        clcl[2] = new ClassStruct[2];
        int index = 0;
        for (int one = 0; one < clcl.length; ++one)
        {
            for (int two = 0; two < clcl[one].length; ++two)
            {
                clcl[one][two] = new ClassStruct();
                clcl[one][two].classKey = sorted7keys[index++];
            }
        }
    }

    private boolean equals(ClassStruct a, ClassStruct b)
    {
        return a.classKey == b.classKey;
    }

    @Test public void testBinarySearchCL()
    {
        setupClassStructArrays();
        for (int i = 0; i < cl7sorted.length; ++i)
        {
            String name = "i=" + i;
            if (i < cl6sorted.length)
            {
                assertEquals(name, i, CollectionHelper
                        .binarySearch(cl6sorted, sorted7keys[i]));
            }
            assertEquals(name, i, CollectionHelper
                    .binarySearch(cl7sorted, sorted7keys[i]));
        }

        for (int key : missingKeys)
        {
            String name = "key=" + key;
            assertTrue(name, 0 > CollectionHelper.binarySearch(cl6sorted, key));
            assertTrue(name, 0 > CollectionHelper.binarySearch(cl7sorted, key));
        }

        for (int length = 1; length <= cl7sorted.length; ++length)
        {
            String name1 = "length=" + length;
            for (int i = 0; i < length; i++)
            {
                String name2 = name1 + " i=" + i;
                assertEquals(name2, i, CollectionHelper
                        .binarySearch(cl7sorted, sorted7keys[i], length));
            }
        }
    }

    @Test public void testSortCL()
    {
        setupClassStructArrays();
        CollectionHelper.sort(cl6unsorted);
        for (int i = 0; i < cl6unsorted.length; ++i)
        {
            assertTrue("i=" + i, equals(cl6sorted[i], cl6unsorted[i]));
        }
        CollectionHelper.sort(cl7unsorted);
        for (int i = 0; i < cl7unsorted.length; ++i)
        {
            assertTrue("i=" + i, equals(cl7sorted[i], cl7sorted[i]));
        }

        // Now that the "unsorted" arrays have been sorted, we can use them as
        // a sorted reference after we try sorting the already-sorted arrays.

        CollectionHelper.sort(cl6sorted);
        for (int i = 0; i < cl6unsorted.length; ++i)
        {
            assertTrue("i=" + i, equals(cl6sorted[i], cl6unsorted[i]));
        }
        CollectionHelper.sort(cl7sorted);
        for (int i = 0; i < cl7unsorted.length; ++i)
        {
            assertTrue("i=" + i, equals(cl7sorted[i], cl7unsorted[i]));
        }
    }

    @Test public void testArraycloneCL()
    {
        setupClassStructArrays();
        ClassStruct result[] = CollectionHelper.arrayclone(cl6sorted[0]);
        assertEquals(1, result.length);
        assertTrue(equals(result[0], cl6sorted[0]));

        result = CollectionHelper.arrayclone((ClassStruct[]) null);
        assertNull(result);

        result = CollectionHelper.arrayclone(cl7unsorted);
        assertEquals(cl7unsorted.length, result.length);
        for (int i = 0; i < result.length; ++i)
        {
            assertTrue("i=" + i, equals(result[i], cl7unsorted[i]));
        }

        int toSize = cl7sorted.length+1;
        result = CollectionHelper.arrayclone(cl7sorted, toSize);
        assertEquals(toSize, result.length);
        for (int i = 0; i < cl7sorted.length; ++i)
        {
            assertTrue("i=" + i, equals(result[i], cl7sorted[i]));
        }
        assertNull(result[result.length-1]);

        toSize = 6;
        int fromSize = 5;
        int fromOffset = 1;
        result = CollectionHelper.arrayclone(cl7unsorted, fromOffset,
                    fromSize, toSize);
        assertEquals(toSize, result.length);
        for (int i = 0; i < fromSize; ++i)
        {
            assertTrue("i=" + i, equals(cl7unsorted[i+fromOffset], result[i]));
        }
    }

    @Test public void testArraycloneCombineCL()
    {
        setupClassStructArrays();
        // Method arguments are
        // from - source array
        // startOffset - first copy from[startOffset..from.length-1]
        // endOffset - next copy from[0..endOffset]
        // toSize - size of result, must be
        //     at least from.length-startOffset + endOffset+1
        ClassStruct result[];
        result = CollectionHelper.arraycloneCombine(cl7sorted, 5, 1, 4);
        assertEquals(4, result.length);
        assertTrue(equals(cl7sorted[5], result[0]));
        assertTrue(equals(cl7sorted[6], result[1]));
        assertTrue(equals(cl7sorted[0], result[2]));
        assertNull(result[3]);
    }

    @Test public void testArraycloneExpandGapCL()
    {
        setupClassStructArrays();

        // As implemented, arraycloneExpandGap makes sense when fromOffset == 0
        int gapLength = 3;
        int toSize = cl7unsorted.length+gapLength;
        ClassStruct result[] = CollectionHelper.arraycloneExpandGap(
                cl7unsorted, 0, cl7unsorted.length,
                toSize, 2, gapLength);

        assertEquals(toSize, result.length);
        assertTrue(equals(cl7unsorted[0], result[0]));
        assertTrue(equals(cl7unsorted[1], result[1]));
        assertNull(result[2]);
        assertNull(result[3]);
        assertNull(result[4]);
        assertTrue(equals(cl7unsorted[2], result[5]));
        assertTrue(equals(cl7unsorted[3], result[6]));
        assertTrue(equals(cl7unsorted[4], result[7]));
        assertTrue(equals(cl7unsorted[5], result[8]));
        assertTrue(equals(cl7unsorted[6], result[9]));
    }

    @Test public void testArraycloneCLCL()
    {
        setupClassStructArrays();
        ClassStruct nullMatrix[][] = null;
        ClassStruct result[][];

        result = CollectionHelper.arrayclone(nullMatrix);
        assertNull(result);

        result = CollectionHelper.arrayclone(clcl);
        assertNotNull(result);
        assertEquals(clcl.length, result.length);
        for (int one = 0; one < clcl.length; ++one)
        {
            assertEquals("["+one+"]", clcl[one].length, result[one].length);
            for (int two = 0; two < clcl[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(clcl[one][two], result[one][two]));
            }
        }

        result = CollectionHelper.arrayclone(nullMatrix, 1);
        assertNull(result);

        result = CollectionHelper.arrayclone(clcl, clcl.length+1);
        assertNotNull(result);
        assertEquals(clcl.length+1, result.length);
        assertNull(result[result.length-1]);
        for (int one = 0; one < clcl.length; ++one)
        {
            assertEquals("["+one+"]", clcl[one].length, result[one].length);
            for (int two = 0; two < clcl[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(clcl[one][two], result[one][two]));
            }
        }

        result = CollectionHelper.arrayclone(nullMatrix, 1, 1, 1);
        assertNull(result);

        result = CollectionHelper.arrayclone(clcl, 2, 1, 1);
        assertNotNull(result);
        assertEquals(1, result.length);
        for (int two = 0; two < clcl[2].length; ++two)
        {
            assertTrue("[][" + two + "]", equals(clcl[2][two], result[0][two]));
        }
    }

    @Test public void testArraycloneDimensionsCLCL()
    {
        setupClassStructArrays();
        ClassStruct result[][] = null;

        assertNull(CollectionHelper.arraycloneDimensions(result));

        result = CollectionHelper.arraycloneDimensions(clcl);
        assertNotNull(result);
        assertEquals(clcl.length, result.length);
        for (int one = 0; one < clcl.length; ++one)
        {
            assertEquals("["+one+"]", clcl[one].length, result[one].length);
            for (int two = 0; two < clcl[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(clcl[one][two], result[one][two]));
            }
        }
    }

    @Test public void testArraycloneCombineCLCL()
    {
        setupClassStructArrays();
        ClassStruct nullMatrix[][] = null;
        assertNull(CollectionHelper.arraycloneCombine(nullMatrix, 3, 3, 3));

        ClassStruct result[][];
        // Copy clcl[1..2] to result[0..1], clcl[0..1] to result[2..3]
        result = CollectionHelper.arraycloneCombine(clcl, 1, 2, 4);
        assertNotNull(result);
        assertEquals(4, result.length);
        for (int i = 0; i < clcl[1].length; ++i)
        {
            assertTrue("i="+i, equals(clcl[1][i], result[0][i]));
            assertTrue("i="+i, equals(clcl[1][i], result[3][i]));
        }
        for (int i = 0; i < clcl[2].length; ++i)
        {
            assertTrue("i="+i, equals(clcl[2][i], result[1][i]));
        }
        for (int i = 0; i < clcl[0].length; ++i)
        {
            assertTrue("i="+i, equals(clcl[0][i], result[2][i]));
        }
    }

    @Test public void testArraycloneExpandGapCLCL()
    {
        setupClassStructArrays();
        ClassStruct nullMatrix[][] = null;
        assertNull(CollectionHelper.arraycloneExpandGap(nullMatrix,5,5,5,5,5));

        // As implemented, arraycloneExpandGap makes sense when fromOffset == 0
        int gapLength = 2;
        int toSize = clcl.length + gapLength;
        ClassStruct result[][] = CollectionHelper.arraycloneExpandGap(
                clcl, 0, clcl.length, toSize, 1, gapLength);
        assertNotNull(result);
        assertEquals(toSize, result.length);

        int r = 0;  // index into result[]
        for (int i = 0; i < clcl[0].length; ++i)
        {
            assertTrue("i=" + i, equals(clcl[0][i], result[0][i]));
        }
        r++;
        for (int i = 0; i < gapLength; ++i)
        {
            assertNull(result[r++]);
        }
        for (int b = 1; b < clcl.length; ++b)
        {
            for (int i = 0; i < clcl[b].length; ++i)
            {
                assertTrue("["+r+"]["+i+"]", equals(clcl[b][i], result[r][i]));
            }
            ++r;
        }
    }

    @Test public void testArraycloneShrinkGapCL()
    {
        setupClassStructArrays();
        ClassStruct result[] = null;
        assertNull(CollectionHelper.arraycloneShrinkGap(result, 2, 2));

        result = CollectionHelper.arraycloneShrinkGap(cl7sorted, 2, 3);
        assertNotNull(result);
        assertEquals(4, result.length);
        assertTrue(equals(result[0], cl7sorted[0]));
        assertTrue(equals(result[1], cl7sorted[1]));
        assertTrue(equals(result[2], cl7sorted[5]));
        assertTrue(equals(result[3], cl7sorted[6]));
    }

    @Test public void testArraycloneShrinkGapCLCL()
    {
        setupClassStructArrays();
        ClassStruct result[][] = null;
        assertNull(CollectionHelper.arraycloneShrinkGap(result, 2, 2));

        result = CollectionHelper.arraycloneShrinkGap(clcl, 0, 2);
        assertNotNull(result);
        assertEquals(1, result.length);
        assertEquals(clcl[2].length, result[0].length);
        for (int i = 0; i < clcl[2].length; ++i)
        {
            assertTrue("i="+i, equals(clcl[2][i], result[0][i]));
        }
    }

    private int int6sorted[];
    private int int7sorted[];
    private int int7unsorted[];
    private int intint[][];

    private void setupIntArrays()
    {
        // Look for search errors: even or odd array size, sorted or unsorted
        int6sorted = new int[6];
        int7sorted = new int[7];
        int7unsorted = new int[7];
        for (int i = 0; i < int7sorted.length; ++i)
        {
            if (i < int6sorted.length)
            {
                int6sorted[i] = sorted6keys[i];
            }
            int7sorted[i] = sorted7keys[i];
            int7unsorted[i] = unsorted7keys[i];
        }

        // Two dimensional array, total of 7 elements so we can use sorted7keys
        intint = new int[3][];
        intint[0] = new int[2];
        intint[1] = new int[3];
        intint[2] = new int[2];
        int index = 0;
        for (int one = 0; one < intint.length; ++one)
        {
            for (int two = 0; two < intint[one].length; ++two)
            {
                intint[one][two] = sorted7keys[index++];
            }
        }
    }

    private boolean equals(int a, int b)
    {
        return a == b;
    }

    @Test public void testBinarySearchINT()
    {
        setupIntArrays();
        for (int i = 0; i < int7sorted.length; ++i)
        {
            String name = "i=" + i;
            if (i < int6sorted.length)
            {
                assertEquals(name, i, CollectionHelper
                        .binarySearch(int6sorted, sorted7keys[i]));
            }
            assertEquals(name, i, CollectionHelper
                    .binarySearch(int7sorted, sorted7keys[i]));
        }

        for (int key : missingKeys)
        {
            String name = "key=" + key;
            assertTrue(name, 0 > CollectionHelper.binarySearch(int6sorted, key));
            assertTrue(name, 0 > CollectionHelper.binarySearch(int7sorted, key));
        }

        for (int length = 1; length <= int7sorted.length; ++length)
        {
            String name1 = "length=" + length;
            for (int i = 0; i < length; i++)
            {
                String name2 = name1 + " i=" + i;
                assertEquals(name2, i, CollectionHelper
                        .binarySearch(int7sorted, sorted7keys[i], length));
            }
        }
    }

    @Test public void testArraycloneINT()
    {
        setupIntArrays();
        int result[] = CollectionHelper.arrayclone(int6sorted[0]);
        assertEquals(1, result.length);
        assertTrue(equals(result[0], int6sorted[0]));

        result = CollectionHelper.arrayclone((int[]) null);
        assertNull(result);

        result = CollectionHelper.arrayclone(int7unsorted);
        assertEquals(int7unsorted.length, result.length);
        for (int i = 0; i < result.length; ++i)
        {
            assertTrue("i=" + i, equals(result[i], int7unsorted[i]));
        }

        int toSize = int7sorted.length+1;
        result = CollectionHelper.arrayclone(int7sorted, toSize);
        assertEquals(toSize, result.length);
        for (int i = 0; i < int7sorted.length; ++i)
        {
            assertTrue("i=" + i, equals(result[i], int7sorted[i]));
        }
        assertEquals(0, result[result.length-1]);

        toSize = 6;
        int fromSize = 5;
        int fromOffset = 1;
        result = CollectionHelper.arrayclone(int7unsorted, fromOffset,
                    fromSize, toSize);
        assertEquals(toSize, result.length);
        for (int i = 0; i < fromSize; ++i)
        {
            assertTrue("i=" + i, equals(int7unsorted[i+fromOffset], result[i]));
        }
    }

    @Test public void testArraycloneCombineINT()
    {
        setupIntArrays();
        // Method arguments are
        // from - source array
        // startOffset - first copy from[startOffset..from.length-1]
        // endOffset - next copy from[0..endOffset]
        // toSize - size of result, must be
        //     at least from.length-startOffset + endOffset+1
        int result[];
        result = CollectionHelper.arraycloneCombine(int7sorted, 5, 1, 4);
        assertEquals(4, result.length);
        assertTrue(equals(int7sorted[5], result[0]));
        assertTrue(equals(int7sorted[6], result[1]));
        assertTrue(equals(int7sorted[0], result[2]));
        assertEquals(0, result[3]);
    }

    @Test public void testArraycloneExpandGapINT()
    {
        setupIntArrays();

        // As implemented, arraycloneExpandGap makes sense when fromOffset == 0
        int gapLength = 3;
        int toSize = int7unsorted.length+gapLength;
        int result[] = CollectionHelper.arraycloneExpandGap(
                int7unsorted, 0, int7unsorted.length,
                toSize, 2, gapLength);

        assertEquals(toSize, result.length);
        assertTrue(equals(int7unsorted[0], result[0]));
        assertTrue(equals(int7unsorted[1], result[1]));
        assertEquals(0, result[2]);
        assertEquals(0, result[3]);
        assertEquals(0, result[4]);
        assertTrue(equals(int7unsorted[2], result[5]));
        assertTrue(equals(int7unsorted[3], result[6]));
        assertTrue(equals(int7unsorted[4], result[7]));
        assertTrue(equals(int7unsorted[5], result[8]));
        assertTrue(equals(int7unsorted[6], result[9]));
    }

    @Test public void testArraycloneINTINT()
    {
        setupIntArrays();
        int nullMatrix[][] = null;
        int result[][];

        result = CollectionHelper.arrayclone(nullMatrix);
        assertNull(result);

        result = CollectionHelper.arrayclone(intint);
        assertNotNull(result);
        assertEquals(intint.length, result.length);
        for (int one = 0; one < intint.length; ++one)
        {
            assertEquals("["+one+"]", intint[one].length, result[one].length);
            for (int two = 0; two < intint[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(intint[one][two], result[one][two]));
            }
        }

        result = CollectionHelper.arrayclone(nullMatrix, 1);
        assertNull(result);

        result = CollectionHelper.arrayclone(intint, intint.length+1);
        assertNotNull(result);
        assertEquals(intint.length+1, result.length);
        assertNull(result[result.length-1]);
        for (int one = 0; one < intint.length; ++one)
        {
            assertEquals("["+one+"]", intint[one].length, result[one].length);
            for (int two = 0; two < intint[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(intint[one][two], result[one][two]));
            }
        }

        result = CollectionHelper.arrayclone(nullMatrix, 1, 1, 1);
        assertNull(result);

        result = CollectionHelper.arrayclone(intint, 2, 1, 1);
        assertNotNull(result);
        assertEquals(1, result.length);
        for (int two = 0; two < intint[2].length; ++two)
        {
            assertTrue("[][" + two + "]", equals(intint[2][two], result[0][two]));
        }
    }

    @Test public void testArraycloneDimensionsINTINT()
    {
        setupIntArrays();
        int result[][] = null;

        assertNull(CollectionHelper.arraycloneDimensions(result));

        result = CollectionHelper.arraycloneDimensions(intint);
        assertNotNull(result);
        assertEquals(intint.length, result.length);
        for (int one = 0; one < intint.length; ++one)
        {
            assertEquals("["+one+"]", intint[one].length, result[one].length);
            for (int two = 0; two < intint[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(intint[one][two], result[one][two]));
            }
        }
    }

    @Test public void testArraycloneCombineINTINT()
    {
        setupIntArrays();
        int nullMatrix[][] = null;
        assertNull(CollectionHelper.arraycloneCombine(nullMatrix, 3, 3, 3));

        int result[][];
        // Copy intint[1..2] to result[0..1], intint[0..1] to result[2..3]
        result = CollectionHelper.arraycloneCombine(intint, 1, 2, 4);
        assertNotNull(result);
        assertEquals(4, result.length);
        for (int i = 0; i < intint[1].length; ++i)
        {
            assertTrue("i="+i, equals(intint[1][i], result[0][i]));
            assertTrue("i="+i, equals(intint[1][i], result[3][i]));
        }
        for (int i = 0; i < intint[2].length; ++i)
        {
            assertTrue("i="+i, equals(intint[2][i], result[1][i]));
        }
        for (int i = 0; i < intint[0].length; ++i)
        {
            assertTrue("i="+i, equals(intint[0][i], result[2][i]));
        }
    }

    @Test public void testArraycloneExpandGapINTINT()
    {
        setupIntArrays();
        int nullMatrix[][] = null;
        assertNull(CollectionHelper.arraycloneExpandGap(nullMatrix,5,5,5,5,5));

        // As implemented, arraycloneExpandGap makes sense when fromOffset == 0
        int gapLength = 2;
        int toSize = intint.length + gapLength;
        int result[][] = CollectionHelper.arraycloneExpandGap(
                intint, 0, intint.length, toSize, 1, gapLength);
        assertNotNull(result);
        assertEquals(toSize, result.length);

        int r = 0;  // index into result[]
        for (int i = 0; i < intint[0].length; ++i)
        {
            assertTrue("i=" + i, equals(intint[0][i], result[0][i]));
        }
        r++;
        for (int i = 0; i < gapLength; ++i)
        {
            assertNull(result[r++]);
        }
        for (int b = 1; b < intint.length; ++b)
        {
            for (int i = 0; i < intint[b].length; ++i)
            {
                assertTrue("["+r+"]["+i+"]", equals(intint[b][i], result[r][i]));
            }
            ++r;
        }
    }

    @Test public void testArraycloneShrinkGapINT()
    {
        setupIntArrays();
        int result[] = null;
        assertNull(CollectionHelper.arraycloneShrinkGap(result, 2, 2));

        result = CollectionHelper.arraycloneShrinkGap(int7sorted, 2, 3);
        assertNotNull(result);
        assertEquals(4, result.length);
        assertTrue(equals(result[0], int7sorted[0]));
        assertTrue(equals(result[1], int7sorted[1]));
        assertTrue(equals(result[2], int7sorted[5]));
        assertTrue(equals(result[3], int7sorted[6]));
    }

    @Test public void testArraycloneShrinkGapINTINT()
    {
        setupIntArrays();
        int result[][] = null;
        assertNull(CollectionHelper.arraycloneShrinkGap(result, 2, 2));

        result = CollectionHelper.arraycloneShrinkGap(intint, 0, 2);
        assertNotNull(result);
        assertEquals(1, result.length);
        assertEquals(intint[2].length, result[0].length);
        for (int i = 0; i < intint[2].length; ++i)
        {
            assertTrue("i="+i, equals(intint[2][i], result[0][i]));
        }
    }

    private long long6sorted[];
    private long long7sorted[];
    private long long7unsorted[];
    private long longlong[][];

    private void setupLongArrays()
    {
        // Look for search errors: even or odd array size, sorted or unsorted
        long6sorted = new long[6];
        long7sorted = new long[7];
        long7unsorted = new long[7];
        for (int i = 0; i < long7sorted.length; ++i)
        {
            if (i < long6sorted.length)
            {
                long6sorted[i] =  sorted6keys[i];
            }
            long7sorted[i] = sorted7keys[i];
            long7unsorted[i] = unsorted7keys[i];
        }

        // Two dimensional array, total of 7 elements so we can use sorted7keys
        longlong = new long[3][];
        longlong[0] = new long[2];
        longlong[1] = new long[3];
        longlong[2] = new long[2];
        int index = 0;
        for (int one = 0; one < longlong.length; ++one)
        {
            for (int two = 0; two < longlong[one].length; ++two)
            {
                longlong[one][two] = sorted7keys[index++];
            }
        }
    }

    private boolean equals(long a, long b)
    {
        return a == b;
    }

    @Test public void testBinarySearchLONG()
    {
        setupLongArrays();
        for (int i = 0; i < long7sorted.length; ++i)
        {
            String name = "i=" + i;
            if (i < long6sorted.length)
            {
                assertEquals(name, i, CollectionHelper
                        .binarySearch(long6sorted, sorted7keys[i]));
            }
            assertEquals(name, i, CollectionHelper
                    .binarySearch(long7sorted, sorted7keys[i]));
        }

        for (int key : missingKeys)
        {
            String name = "key=" + key;
            assertTrue(name, 0 > CollectionHelper.binarySearch(long6sorted, key));
            assertTrue(name, 0 > CollectionHelper.binarySearch(long7sorted, key));
        }

        for (int length = 1; length <= long7sorted.length; ++length)
        {
            String name1 = "length=" + length;
            for (int i = 0; i < length; i++)
            {
                String name2 = name1 + " i=" + i;
                assertEquals(name2, i, CollectionHelper
                        .binarySearch(long7sorted, sorted7keys[i], length));
            }
        }
    }

    @Test public void testArraycloneLONG()
    {
        setupLongArrays();
        long result[] = CollectionHelper.arrayclone(long6sorted[0]);
        assertEquals(1, result.length);
        assertTrue(equals(result[0], long6sorted[0]));

        result = CollectionHelper.arrayclone((long[]) null);
        assertNull(result);

        result = CollectionHelper.arrayclone(long7unsorted);
        assertEquals(long7unsorted.length, result.length);
        for (int i = 0; i < result.length; ++i)
        {
            assertTrue("i=" + i, equals(result[i], long7unsorted[i]));
        }

        int toSize = long7sorted.length+1;
        result = CollectionHelper.arrayclone(long7sorted, toSize);
        assertEquals(toSize, result.length);
        for (int i = 0; i < long7sorted.length; ++i)
        {
            assertTrue("i=" + i, equals(result[i], long7sorted[i]));
        }
        assertEquals(0, result[result.length-1]);

        toSize = 6;
        int fromSize = 5;
        int fromOffset = 1;
        result = CollectionHelper.arrayclone(long7unsorted, fromOffset,
                    fromSize, toSize);
        assertEquals(toSize, result.length);
        for (int i = 0; i < fromSize; ++i)
        {
            assertTrue("i=" + i, equals(long7unsorted[i+fromOffset], result[i]));
        }
    }

    @Test public void testArraycloneCombineLONG()
    {
        setupLongArrays();
        // Method arguments are
        // from - source array
        // startOffset - first copy from[startOffset..from.length-1]
        // endOffset - next copy from[0..endOffset]
        // toSize - size of result, must be
        //     at least from.length-startOffset + endOffset+1
        long result[];
        result = CollectionHelper.arraycloneCombine(long7sorted, 5, 1, 4);
        assertEquals(4, result.length);
        assertTrue(equals(long7sorted[5], result[0]));
        assertTrue(equals(long7sorted[6], result[1]));
        assertTrue(equals(long7sorted[0], result[2]));
        assertEquals(0, result[3]);
    }

    @Test public void testArraycloneExpandGapLONG()
    {
        setupLongArrays();

        // As implemented, arraycloneExpandGap makes sense when fromOffset == 0
        int gapLength = 3;
        int toSize = long7unsorted.length+gapLength;
        long result[] = CollectionHelper.arraycloneExpandGap(
                long7unsorted, 0, long7unsorted.length,
                toSize, 2, gapLength);

        assertEquals(toSize, result.length);
        assertTrue(equals(long7unsorted[0], result[0]));
        assertTrue(equals(long7unsorted[1], result[1]));
        assertEquals(0, result[2]);
        assertEquals(0, result[3]);
        assertEquals(0, result[4]);
        assertTrue(equals(long7unsorted[2], result[5]));
        assertTrue(equals(long7unsorted[3], result[6]));
        assertTrue(equals(long7unsorted[4], result[7]));
        assertTrue(equals(long7unsorted[5], result[8]));
        assertTrue(equals(long7unsorted[6], result[9]));
    }

    @Test public void testArraycloneLONGLONG()
    {
        setupLongArrays();
        long nullMatrix[][] = null;
        long result[][];

        result = CollectionHelper.arrayclone(nullMatrix);
        assertNull(result);

        result = CollectionHelper.arrayclone(longlong);
        assertNotNull(result);
        assertEquals(longlong.length, result.length);
        for (int one = 0; one < longlong.length; ++one)
        {
            assertEquals("["+one+"]", longlong[one].length, result[one].length);
            for (int two = 0; two < longlong[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(longlong[one][two], result[one][two]));
            }
        }

        result = CollectionHelper.arrayclone(nullMatrix, 1);
        assertNull(result);

        result = CollectionHelper.arrayclone(longlong, longlong.length+1);
        assertNotNull(result);
        assertEquals(longlong.length+1, result.length);
        assertNull(result[result.length-1]);
        for (int one = 0; one < longlong.length; ++one)
        {
            assertEquals("["+one+"]", longlong[one].length, result[one].length);
            for (int two = 0; two < longlong[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(longlong[one][two], result[one][two]));
            }
        }

        result = CollectionHelper.arrayclone(nullMatrix, 1, 1, 1);
        assertNull(result);

        result = CollectionHelper.arrayclone(longlong, 2, 1, 1);
        assertNotNull(result);
        assertEquals(1, result.length);
        for (int two = 0; two < longlong[2].length; ++two)
        {
            assertTrue("[][" + two + "]", equals(longlong[2][two], result[0][two]));
        }
    }

    @Test public void testArraycloneDimensionsLONGLONG()
    {
        setupLongArrays();
        long result[][] = null;

        assertNull(CollectionHelper.arraycloneDimensions(result));

        result = CollectionHelper.arraycloneDimensions(longlong);
        assertNotNull(result);
        assertEquals(longlong.length, result.length);
        for (int one = 0; one < longlong.length; ++one)
        {
            assertEquals("["+one+"]", longlong[one].length, result[one].length);
            for (int two = 0; two < longlong[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(longlong[one][two], result[one][two]));
            }
        }
    }

    @Test public void testArraycloneCombineLONGLONG()
    {
        setupLongArrays();
        long nullMatrix[][] = null;
        assertNull(CollectionHelper.arraycloneCombine(nullMatrix, 3, 3, 3));

        long result[][];
        // Copy longlong[1..2] to result[0..1], longlong[0..1] to result[2..3]
        result = CollectionHelper.arraycloneCombine(longlong, 1, 2, 4);
        assertNotNull(result);
        assertEquals(4, result.length);
        for (int i = 0; i < longlong[1].length; ++i)
        {
            assertTrue("i="+i, equals(longlong[1][i], result[0][i]));
            assertTrue("i="+i, equals(longlong[1][i], result[3][i]));
        }
        for (int i = 0; i < longlong[2].length; ++i)
        {
            assertTrue("i="+i, equals(longlong[2][i], result[1][i]));
        }
        for (int i = 0; i < longlong[0].length; ++i)
        {
            assertTrue("i="+i, equals(longlong[0][i], result[2][i]));
        }
    }

    @Test public void testArraycloneExpandGapLONGLONG()
    {
        setupLongArrays();
        long nullMatrix[][] = null;
        assertNull(CollectionHelper.arraycloneExpandGap(nullMatrix,5,5,5,5,5));

        // As implemented, arraycloneExpandGap makes sense when fromOffset == 0
        int gapLength = 2;
        int toSize = longlong.length + gapLength;
        long result[][] = CollectionHelper.arraycloneExpandGap(
                longlong, 0, longlong.length, toSize, 1, gapLength);
        assertNotNull(result);
        assertEquals(toSize, result.length);

        int r = 0;  // index into result[]
        for (int i = 0; i < longlong[0].length; ++i)
        {
            assertTrue("i=" + i, equals(longlong[0][i], result[0][i]));
        }
        r++;
        for (int i = 0; i < gapLength; ++i)
        {
            assertNull(result[r++]);
        }
        for (int b = 1; b < longlong.length; ++b)
        {
            for (int i = 0; i < longlong[b].length; ++i)
            {
                assertTrue("["+r+"]["+i+"]", equals(longlong[b][i], result[r][i]));
            }
            ++r;
        }
    }

    @Test public void testArraycloneShrinkGapLONG()
    {
        setupLongArrays();
        long result[] = null;
        assertNull(CollectionHelper.arraycloneShrinkGap(result, 2, 2));

        result = CollectionHelper.arraycloneShrinkGap(long7sorted, 2, 3);
        assertNotNull(result);
        assertEquals(4, result.length);
        assertTrue(equals(result[0], long7sorted[0]));
        assertTrue(equals(result[1], long7sorted[1]));
        assertTrue(equals(result[2], long7sorted[5]));
        assertTrue(equals(result[3], long7sorted[6]));
    }

    @Test public void testArraycloneShrinkGapLONGLONG()
    {
        setupLongArrays();
        long result[][] = null;
        assertNull(CollectionHelper.arraycloneShrinkGap(result, 2, 2));

        result = CollectionHelper.arraycloneShrinkGap(longlong, 0, 2);
        assertNotNull(result);
        assertEquals(1, result.length);
        assertEquals(longlong[2].length, result[0].length);
        for (int i = 0; i < longlong[2].length; ++i)
        {
            assertTrue("i="+i, equals(longlong[2][i], result[0][i]));
        }
    }

    private Object obj6sorted[];
    private Object obj7sorted[];
    private Object obj7unsorted[];
    private Object objobj[][];

    private void setupObjectArrays()
    {
        // Look for search errors: even or odd array size, sorted or unsorted
        obj6sorted = new Object[6];
        obj7sorted = new Object[7];
        obj7unsorted = new Object[7];
        for (int i = 0; i < obj7sorted.length; ++i)
        {
            if (i < obj6sorted.length)
            {
                obj6sorted[i] = sorted6keys[i];
            }
            obj7sorted[i] = sorted7keys[i];
            obj7unsorted[i] = unsorted7keys[i];
        }

        // Two dimensional array, total of 7 elements so we can use sorted7keys
        objobj = new Object[3][];
        objobj[0] = new Object[2];
        objobj[1] = new Object[3];
        objobj[2] = new Object[2];
        int index = 0;
        for (int one = 0; one < objobj.length; ++one)
        {
            for (int two = 0; two < objobj[one].length; ++two)
            {
                objobj[one][two] = sorted7keys[index++];
            }
        }
    }

    private boolean equals(Object a, Object b)
    {
        return a.equals(b);
    }

    @Test public void testArraycloneOBJ()
    {
        setupObjectArrays();
        Object result[] = CollectionHelper.arrayclone(obj6sorted[0]);
        assertEquals(1, result.length);
        assertTrue(equals(result[0], obj6sorted[0]));

        result = CollectionHelper.arrayclone((Object[]) null);
        assertNull(result);

        result = CollectionHelper.arrayclone(obj7unsorted);
        assertEquals(obj7unsorted.length, result.length);
        for (int i = 0; i < result.length; ++i)
        {
            assertTrue("i=" + i, equals(result[i], obj7unsorted[i]));
        }

        int toSize = obj7sorted.length+1;
        result = CollectionHelper.arrayclone(obj7sorted, toSize);
        assertEquals(toSize, result.length);
        for (int i = 0; i < obj7sorted.length; ++i)
        {
            assertTrue("i=" + i, equals(result[i], obj7sorted[i]));
        }
        assertNull(result[result.length-1]);

        toSize = 6;
        int fromSize = 5;
        int fromOffset = 1;
        result = CollectionHelper.arrayclone(obj7unsorted, fromOffset,
                    fromSize, toSize);
        assertEquals(toSize, result.length);
        for (int i = 0; i < fromSize; ++i)
        {
            assertTrue("i=" + i, equals(obj7unsorted[i+fromOffset], result[i]));
        }
    }

    @Test public void testArraycloneCombineOBJ()
    {
        setupObjectArrays();
        // Method arguments are
        // from - source array
        // startOffset - first copy from[startOffset..from.length-1]
        // endOffset - next copy from[0..endOffset]
        // toSize - size of result, must be
        //     at least from.length-startOffset + endOffset+1
        Object result[];
        result = CollectionHelper.arraycloneCombine(obj7sorted, 5, 1, 4);
        assertEquals(4, result.length);
        assertTrue(equals(obj7sorted[5], result[0]));
        assertTrue(equals(obj7sorted[6], result[1]));
        assertTrue(equals(obj7sorted[0], result[2]));
        assertNull(result[3]);
    }

    @Test public void testArraycloneExpandGapOBJ()
    {
        setupObjectArrays();

        // As implemented, arraycloneExpandGap makes sense when fromOffset == 0
        int gapLength = 3;
        int toSize = obj7unsorted.length+gapLength;
        Object result[] = CollectionHelper.arraycloneExpandGap(
                obj7unsorted, 0, obj7unsorted.length,
                toSize, 2, gapLength);

        assertEquals(toSize, result.length);
        assertTrue(equals(obj7unsorted[0], result[0]));
        assertTrue(equals(obj7unsorted[1], result[1]));
        assertNull(result[2]);
        assertNull(result[3]);
        assertNull(result[4]);
        assertTrue(equals(obj7unsorted[2], result[5]));
        assertTrue(equals(obj7unsorted[3], result[6]));
        assertTrue(equals(obj7unsorted[4], result[7]));
        assertTrue(equals(obj7unsorted[5], result[8]));
        assertTrue(equals(obj7unsorted[6], result[9]));
    }

    @Test public void testArraycloneOBJOBJ()
    {
        setupObjectArrays();
        Object nullMatrix[][] = null;
        Object result[][];

        result = CollectionHelper.arrayclone(nullMatrix);
        assertNull(result);

        result = CollectionHelper.arrayclone(objobj);
        assertNotNull(result);
        assertEquals(objobj.length, result.length);
        for (int one = 0; one < objobj.length; ++one)
        {
            assertEquals("["+one+"]", objobj[one].length, result[one].length);
            for (int two = 0; two < objobj[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(objobj[one][two], result[one][two]));
            }
        }

        result = CollectionHelper.arrayclone(nullMatrix, 1);
        assertNull(result);

        result = CollectionHelper.arrayclone(objobj, objobj.length+1);
        assertNotNull(result);
        assertEquals(objobj.length+1, result.length);
        assertNull(result[result.length-1]);
        for (int one = 0; one < objobj.length; ++one)
        {
            assertEquals("["+one+"]", objobj[one].length, result[one].length);
            for (int two = 0; two < objobj[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(objobj[one][two], result[one][two]));
            }
        }

        result = CollectionHelper.arrayclone(nullMatrix, 1, 1, 1);
        assertNull(result);

        result = CollectionHelper.arrayclone(objobj, 2, 1, 1);
        assertNotNull(result);
        assertEquals(1, result.length);
        for (int two = 0; two < objobj[2].length; ++two)
        {
            assertTrue("[][" + two + "]", equals(objobj[2][two], result[0][two]));
        }
    }

    @Test public void testArraycloneDimensionsOBJOBJ()
    {
        setupObjectArrays();
        Object result[][] = null;

        assertNull(CollectionHelper.arraycloneDimensions(result));

        result = CollectionHelper.arraycloneDimensions(objobj);
        assertNotNull(result);
        assertEquals(objobj.length, result.length);
        for (int one = 0; one < objobj.length; ++one)
        {
            assertEquals("["+one+"]", objobj[one].length, result[one].length);
            for (int two = 0; two < objobj[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(objobj[one][two], result[one][two]));
            }
        }
    }

    @Test public void testArraycloneCombineOBJOBJ()
    {
        setupObjectArrays();
        Object nullMatrix[][] = null;
        assertNull(CollectionHelper.arraycloneCombine(nullMatrix, 3, 3, 3));

        Object result[][];
        // Copy objobj[1..2] to result[0..1], objobj[0..1] to result[2..3]
        result = CollectionHelper.arraycloneCombine(objobj, 1, 2, 4);
        assertNotNull(result);
        assertEquals(4, result.length);
        for (int i = 0; i < objobj[1].length; ++i)
        {
            assertTrue("i="+i, equals(objobj[1][i], result[0][i]));
            assertTrue("i="+i, equals(objobj[1][i], result[3][i]));
        }
        for (int i = 0; i < objobj[2].length; ++i)
        {
            assertTrue("i="+i, equals(objobj[2][i], result[1][i]));
        }
        for (int i = 0; i < objobj[0].length; ++i)
        {
            assertTrue("i="+i, equals(objobj[0][i], result[2][i]));
        }
    }

    @Test public void testArraycloneExpandGapOBJOBJ()
    {
        setupObjectArrays();
        Object nullMatrix[][] = null;
        assertNull(CollectionHelper.arraycloneExpandGap(nullMatrix,5,5,5,5,5));

        // As implemented, arraycloneExpandGap makes sense when fromOffset == 0
        int gapLength = 2;
        int toSize = objobj.length + gapLength;
        Object result[][] = CollectionHelper.arraycloneExpandGap(
                objobj, 0, objobj.length, toSize, 1, gapLength);
        assertNotNull(result);
        assertEquals(toSize, result.length);

        int r = 0;  // index into result[]
        for (int i = 0; i < objobj[0].length; ++i)
        {
            assertTrue("i=" + i, equals(objobj[0][i], result[0][i]));
        }
        r++;
        for (int i = 0; i < gapLength; ++i)
        {
            assertNull(result[r++]);
        }
        for (int b = 1; b < objobj.length; ++b)
        {
            for (int i = 0; i < objobj[b].length; ++i)
            {
                assertTrue("["+r+"]["+i+"]", equals(objobj[b][i], result[r][i]));
            }
            ++r;
        }
    }

    @Test public void testArraycloneShrinkGapOBJ()
    {
        setupObjectArrays();
        Object result[] = null;
        assertNull(CollectionHelper.arraycloneShrinkGap(result, 2, 2));

        result = CollectionHelper.arraycloneShrinkGap(obj7sorted, 2, 3);
        assertNotNull(result);
        assertEquals(4, result.length);
        assertTrue(equals(result[0], obj7sorted[0]));
        assertTrue(equals(result[1], obj7sorted[1]));
        assertTrue(equals(result[2], obj7sorted[5]));
        assertTrue(equals(result[3], obj7sorted[6]));
    }

    @Test public void testArraycloneShrinkGapOBJOBJ()
    {
        setupObjectArrays();
        Object result[][] = null;
        assertNull(CollectionHelper.arraycloneShrinkGap(result, 2, 2));

        result = CollectionHelper.arraycloneShrinkGap(objobj, 0, 2);
        assertNotNull(result);
        assertEquals(1, result.length);
        assertEquals(objobj[2].length, result[0].length);
        for (int i = 0; i < objobj[2].length; ++i)
        {
            assertTrue("i="+i, equals(objobj[2][i], result[0][i]));
        }
    }

    private String str6sorted[];
    private String str7sorted[];
    private String str7unsorted[];
    private String strstr[][];

    private void setupStringArrays()
    {
        // Look for search errors: even or odd array size, sorted or unsorted
        str6sorted = new String[6];
        str7sorted = new String[7];
        str7unsorted = new String[7];
        for (int i = 0; i < str7sorted.length; ++i)
        {
            if (i < str6sorted.length)
            {
                str6sorted[i] = String.valueOf(sorted6keys[i]);
            }
            str7sorted[i] = String.valueOf(sorted7keys[i]);
            str7unsorted[i] = String.valueOf(unsorted7keys[i]);
        }

        // Two dimensional array, total of 7 elements so we can use sorted7keys
        strstr = new String[3][];
        strstr[0] = new String[2];
        strstr[1] = new String[3];
        strstr[2] = new String[2];
        int index = 0;
        for (int one = 0; one < strstr.length; ++one)
        {
            for (int two = 0; two < strstr[one].length; ++two)
            {
                strstr[one][two] = String.valueOf(sorted7keys[index++]);
            }
        }
    }

    private boolean equals(String a, String b)
    {
        return a.equals(b);
    }

    @Test public void testArraycloneSTR()
    {
        setupStringArrays();
        String result[] = CollectionHelper.arrayclone(str6sorted[0]);
        assertEquals(1, result.length);
        assertTrue(equals(result[0], str6sorted[0]));

        result = CollectionHelper.arrayclone((String[]) null);
        assertNull(result);

        result = CollectionHelper.arrayclone(str7unsorted);
        assertEquals(str7unsorted.length, result.length);
        for (int i = 0; i < result.length; ++i)
        {
            assertTrue("i=" + i, equals(result[i], str7unsorted[i]));
        }

        int toSize = str7sorted.length+1;
        result = CollectionHelper.arrayclone(str7sorted, toSize);
        assertEquals(toSize, result.length);
        for (int i = 0; i < str7sorted.length; ++i)
        {
            assertTrue("i=" + i, equals(result[i], str7sorted[i]));
        }
        assertNull(result[result.length-1]);

        toSize = 6;
        int fromSize = 5;
        int fromOffset = 1;
        result = CollectionHelper.arrayclone(str7unsorted, fromOffset,
                    fromSize, toSize);
        assertEquals(toSize, result.length);
        for (int i = 0; i < fromSize; ++i)
        {
            assertTrue("i=" + i, equals(str7unsorted[i+fromOffset], result[i]));
        }
    }

    @Test public void testArraycloneCombineSTR()
    {
        setupStringArrays();
        // Method arguments are
        // from - source array
        // startOffset - first copy from[startOffset..from.length-1]
        // endOffset - next copy from[0..endOffset]
        // toSize - size of result, must be
        //     at least from.length-startOffset + endOffset+1
        String result[];
        result = CollectionHelper.arraycloneCombine(str7sorted, 5, 1, 4);
        assertEquals(4, result.length);
        assertTrue(equals(str7sorted[5], result[0]));
        assertTrue(equals(str7sorted[6], result[1]));
        assertTrue(equals(str7sorted[0], result[2]));
        assertNull(result[3]);
    }

    @Test public void testArraycloneExpandGapSTR()
    {
        setupStringArrays();

        // As implemented, arraycloneExpandGap makes sense when fromOffset == 0
        int gapLength = 3;
        int toSize = str7unsorted.length+gapLength;
        String result[] = CollectionHelper.arraycloneExpandGap(
                str7unsorted, 0, str7unsorted.length,
                toSize, 2, gapLength);

        assertEquals(toSize, result.length);
        assertTrue(equals(str7unsorted[0], result[0]));
        assertTrue(equals(str7unsorted[1], result[1]));
        assertNull(result[2]);
        assertNull(result[3]);
        assertNull(result[4]);
        assertTrue(equals(str7unsorted[2], result[5]));
        assertTrue(equals(str7unsorted[3], result[6]));
        assertTrue(equals(str7unsorted[4], result[7]));
        assertTrue(equals(str7unsorted[5], result[8]));
        assertTrue(equals(str7unsorted[6], result[9]));
    }

    @Test public void testArraycloneSTRSTR()
    {
        setupStringArrays();
        String nullMatrix[][] = null;
        String result[][];

        result = CollectionHelper.arrayclone(nullMatrix);
        assertNull(result);

        result = CollectionHelper.arrayclone(strstr);
        assertNotNull(result);
        assertEquals(strstr.length, result.length);
        for (int one = 0; one < strstr.length; ++one)
        {
            assertEquals("["+one+"]", strstr[one].length, result[one].length);
            for (int two = 0; two < strstr[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(strstr[one][two], result[one][two]));
            }
        }

        result = CollectionHelper.arrayclone(nullMatrix, 1);
        assertNull(result);

        result = CollectionHelper.arrayclone(strstr, strstr.length+1);
        assertNotNull(result);
        assertEquals(strstr.length+1, result.length);
        assertNull(result[result.length-1]);
        for (int one = 0; one < strstr.length; ++one)
        {
            assertEquals("["+one+"]", strstr[one].length, result[one].length);
            for (int two = 0; two < strstr[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(strstr[one][two], result[one][two]));
            }
        }

        result = CollectionHelper.arrayclone(nullMatrix, 1, 1, 1);
        assertNull(result);

        result = CollectionHelper.arrayclone(strstr, 2, 1, 1);
        assertNotNull(result);
        assertEquals(1, result.length);
        for (int two = 0; two < strstr[2].length; ++two)
        {
            assertTrue("[][" + two + "]", equals(strstr[2][two], result[0][two]));
        }
    }

    @Test public void testArraycloneDimensionsSTRSTR()
    {
        setupStringArrays();
        String result[][] = null;

        assertNull(CollectionHelper.arraycloneDimensions(result));

        result = CollectionHelper.arraycloneDimensions(strstr);
        assertNotNull(result);
        assertEquals(strstr.length, result.length);
        for (int one = 0; one < strstr.length; ++one)
        {
            assertEquals("["+one+"]", strstr[one].length, result[one].length);
            for (int two = 0; two < strstr[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(strstr[one][two], result[one][two]));
            }
        }
    }

    @Test public void testArraycloneCombineSTRSTR()
    {
        setupStringArrays();
        String nullMatrix[][] = null;
        assertNull(CollectionHelper.arraycloneCombine(nullMatrix, 3, 3, 3));

        String result[][];
        // Copy strstr[1..2] to result[0..1], strstr[0..1] to result[2..3]
        result = CollectionHelper.arraycloneCombine(strstr, 1, 2, 4);
        assertNotNull(result);
        assertEquals(4, result.length);
        for (int i = 0; i < strstr[1].length; ++i)
        {
            assertTrue("i="+i, equals(strstr[1][i], result[0][i]));
            assertTrue("i="+i, equals(strstr[1][i], result[3][i]));
        }
        for (int i = 0; i < strstr[2].length; ++i)
        {
            assertTrue("i="+i, equals(strstr[2][i], result[1][i]));
        }
        for (int i = 0; i < strstr[0].length; ++i)
        {
            assertTrue("i="+i, equals(strstr[0][i], result[2][i]));
        }
    }

    @Test public void testArraycloneExpandGapSTRSTR()
    {
        setupStringArrays();
        String nullMatrix[][] = null;
        assertNull(CollectionHelper.arraycloneExpandGap(nullMatrix,5,5,5,5,5));

        // As implemented, arraycloneExpandGap makes sense when fromOffset == 0
        int gapLength = 2;
        int toSize = strstr.length + gapLength;
        String result[][] = CollectionHelper.arraycloneExpandGap(
                strstr, 0, strstr.length, toSize, 1, gapLength);
        assertNotNull(result);
        assertEquals(toSize, result.length);

        int r = 0;  // index into result[]
        for (int i = 0; i < strstr[0].length; ++i)
        {
            assertTrue("i=" + i, equals(strstr[0][i], result[0][i]));
        }
        r++;
        for (int i = 0; i < gapLength; ++i)
        {
            assertNull(result[r++]);
        }
        for (int b = 1; b < strstr.length; ++b)
        {
            for (int i = 0; i < strstr[b].length; ++i)
            {
                assertTrue("["+r+"]["+i+"]", equals(strstr[b][i], result[r][i]));
            }
            ++r;
        }
    }

    @Test public void testArraycloneShrinkGapSTR()
    {
        setupStringArrays();
        String result[] = null;
        assertNull(CollectionHelper.arraycloneShrinkGap(result, 2, 2));

        result = CollectionHelper.arraycloneShrinkGap(str7sorted, 2, 3);
        assertNotNull(result);
        assertEquals(4, result.length);
        assertTrue(equals(result[0], str7sorted[0]));
        assertTrue(equals(result[1], str7sorted[1]));
        assertTrue(equals(result[2], str7sorted[5]));
        assertTrue(equals(result[3], str7sorted[6]));
    }

    @Test public void testArraycloneShrinkGapSTRSTR()
    {
        setupStringArrays();
        String result[][] = null;
        assertNull(CollectionHelper.arraycloneShrinkGap(result, 2, 2));

        result = CollectionHelper.arraycloneShrinkGap(strstr, 0, 2);
        assertNotNull(result);
        assertEquals(1, result.length);
        assertEquals(strstr[2].length, result[0].length);
        for (int i = 0; i < strstr[2].length; ++i)
        {
            assertTrue("i="+i, equals(strstr[2][i], result[0][i]));
        }
    }

    private Comparable<Integer> cmp6sorted[];
    private Comparable<Integer> cmp7sorted[];
    private Comparable<Integer> cmp7unsorted[];
    private Comparable<Integer> cmpcmp[][];

    private void setupComparableArrays()
    {
        // Look for search errors: even or odd array size, sorted or unsorted
        cmp6sorted = new Integer[6];
        cmp7sorted = new Integer[7];
        cmp7unsorted = new Integer[7];
        for (int i = 0; i < cmp7sorted.length; ++i)
        {
            if (i < cmp6sorted.length)
            {
                cmp6sorted[i] = sorted6keys[i];
            }
            cmp7sorted[i] = sorted7keys[i];
            cmp7unsorted[i] = unsorted7keys[i];
        }

        // Two dimensional array, total of 7 elements so we can use sorted7keys
        cmpcmp = new Integer[3][];
        cmpcmp[0] = new Integer[2];
        cmpcmp[1] = new Integer[3];
        cmpcmp[2] = new Integer[2];
        int index = 0;
        for (int one = 0; one < cmpcmp.length; ++one)
        {
            for (int two = 0; two < cmpcmp[one].length; ++two)
            {
                cmpcmp[one][two] = sorted7keys[index++];
            }
        }
    }

    private boolean equals(Comparable<Integer> a, Comparable<Integer> b)
    {
        return a.equals(b);
    }

    @Test public void testArraycloneCMP()
    {
        setupComparableArrays();
        Comparable<Integer> result[] = CollectionHelper.arrayclone(cmp6sorted[0]);
        assertEquals(1, result.length);
        assertTrue(equals(result[0], cmp6sorted[0]));

        result = CollectionHelper.arrayclone((Comparable<Integer>[]) null);
        assertNull(result);

        result = CollectionHelper.arrayclone(cmp7unsorted);
        assertEquals(cmp7unsorted.length, result.length);
        for (int i = 0; i < result.length; ++i)
        {
            assertTrue("i=" + i, equals(result[i], cmp7unsorted[i]));
        }

        int toSize = cmp7sorted.length+1;
        result = CollectionHelper.arrayclone(cmp7sorted, toSize);
        assertEquals(toSize, result.length);
        for (int i = 0; i < cmp7sorted.length; ++i)
        {
            assertTrue("i=" + i, equals(result[i], cmp7sorted[i]));
        }
        assertNull(result[result.length-1]);

        toSize = 6;
        int fromSize = 5;
        int fromOffset = 1;
        result = CollectionHelper.arrayclone(cmp7unsorted, fromOffset,
                    fromSize, toSize);
        assertEquals(toSize, result.length);
        for (int i = 0; i < fromSize; ++i)
        {
            assertTrue("i=" + i, equals(cmp7unsorted[i+fromOffset], result[i]));
        }
    }

    @Test public void testArraycloneCombineCMP()
    {
        setupComparableArrays();
        // Method arguments are
        // from - source array
        // startOffset - first copy from[startOffset..from.length-1]
        // endOffset - next copy from[0..endOffset]
        // toSize - size of result, must be
        //     at least from.length-startOffset + endOffset+1
        Comparable<Integer> result[];
        result = CollectionHelper.arraycloneCombine(cmp7sorted, 5, 1, 4);
        assertEquals(4, result.length);
        assertTrue(equals(cmp7sorted[5], result[0]));
        assertTrue(equals(cmp7sorted[6], result[1]));
        assertTrue(equals(cmp7sorted[0], result[2]));
        assertNull(result[3]);
    }

    @Test public void testArraycloneExpandGapCMP()
    {
        setupComparableArrays();

        // As implemented, arraycloneExpandGap makes sense when fromOffset == 0
        int gapLength = 3;
        int toSize = cmp7unsorted.length+gapLength;
        Comparable<Integer> result[] = CollectionHelper.arraycloneExpandGap(
                cmp7unsorted, 0, cmp7unsorted.length,
                toSize, 2, gapLength);

        assertEquals(toSize, result.length);
        assertTrue(equals(cmp7unsorted[0], result[0]));
        assertTrue(equals(cmp7unsorted[1], result[1]));
        assertNull(result[2]);
        assertNull(result[3]);
        assertNull(result[4]);
        assertTrue(equals(cmp7unsorted[2], result[5]));
        assertTrue(equals(cmp7unsorted[3], result[6]));
        assertTrue(equals(cmp7unsorted[4], result[7]));
        assertTrue(equals(cmp7unsorted[5], result[8]));
        assertTrue(equals(cmp7unsorted[6], result[9]));
    }

    @Test public void testArraycloneCMPCMP()
    {
        setupComparableArrays();
        Comparable<Integer> nullMatrix[][] = null;
        Comparable<Integer> result[][];

        result = CollectionHelper.arrayclone(nullMatrix);
        assertNull(result);

        result = CollectionHelper.arrayclone(cmpcmp);
        assertNotNull(result);
        assertEquals(cmpcmp.length, result.length);
        for (int one = 0; one < cmpcmp.length; ++one)
        {
            assertEquals("["+one+"]", cmpcmp[one].length, result[one].length);
            for (int two = 0; two < cmpcmp[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(cmpcmp[one][two], result[one][two]));
            }
        }

        result = CollectionHelper.arrayclone(nullMatrix, 1);
        assertNull(result);

        result = CollectionHelper.arrayclone(cmpcmp, cmpcmp.length+1);
        assertNotNull(result);
        assertEquals(cmpcmp.length+1, result.length);
        assertNull(result[result.length-1]);
        for (int one = 0; one < cmpcmp.length; ++one)
        {
            assertEquals("["+one+"]", cmpcmp[one].length, result[one].length);
            for (int two = 0; two < cmpcmp[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(cmpcmp[one][two], result[one][two]));
            }
        }

        result = CollectionHelper.arrayclone(nullMatrix, 1, 1, 1);
        assertNull(result);

        result = CollectionHelper.arrayclone(cmpcmp, 2, 1, 1);
        assertNotNull(result);
        assertEquals(1, result.length);
        for (int two = 0; two < cmpcmp[2].length; ++two)
        {
            assertTrue("[][" + two + "]", equals(cmpcmp[2][two], result[0][two]));
        }
    }

    @Test public void testArraycloneDimensionsCMPCMP()
    {
        setupComparableArrays();
        Comparable<Integer> result[][] = null;

        assertNull(CollectionHelper.arraycloneDimensions(result));

        result = CollectionHelper.arraycloneDimensions(cmpcmp);
        assertNotNull(result);
        assertEquals(cmpcmp.length, result.length);
        for (int one = 0; one < cmpcmp.length; ++one)
        {
            assertEquals("["+one+"]", cmpcmp[one].length, result[one].length);
            for (int two = 0; two < cmpcmp[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(cmpcmp[one][two], result[one][two]));
            }
        }
    }

    @Test public void testArraycloneCombineCMPCMP()
    {
        setupComparableArrays();
        Comparable<Integer> nullMatrix[][] = null;
        assertNull(CollectionHelper.arraycloneCombine(nullMatrix, 3, 3, 3));

        Comparable<Integer> result[][];
        // Copy cmpcmp[1..2] to result[0..1], cmpcmp[0..1] to result[2..3]
        result = CollectionHelper.arraycloneCombine(cmpcmp, 1, 2, 4);
        assertNotNull(result);
        assertEquals(4, result.length);
        for (int i = 0; i < cmpcmp[1].length; ++i)
        {
            assertTrue("i="+i, equals(cmpcmp[1][i], result[0][i]));
            assertTrue("i="+i, equals(cmpcmp[1][i], result[3][i]));
        }
        for (int i = 0; i < cmpcmp[2].length; ++i)
        {
            assertTrue("i="+i, equals(cmpcmp[2][i], result[1][i]));
        }
        for (int i = 0; i < cmpcmp[0].length; ++i)
        {
            assertTrue("i="+i, equals(cmpcmp[0][i], result[2][i]));
        }
    }

    @Test public void testArraycloneExpandGapCMPCMP()
    {
        setupComparableArrays();
        Comparable<Integer> nullMatrix[][] = null;
        assertNull(CollectionHelper.arraycloneExpandGap(nullMatrix,5,5,5,5,5));

        // As implemented, arraycloneExpandGap makes sense when fromOffset == 0
        int gapLength = 2;
        int toSize = cmpcmp.length + gapLength;
        Comparable<Integer> result[][] = CollectionHelper.arraycloneExpandGap(
                cmpcmp, 0, cmpcmp.length, toSize, 1, gapLength);
        assertNotNull(result);
        assertEquals(toSize, result.length);

        int r = 0;  // index into result[]
        for (int i = 0; i < cmpcmp[0].length; ++i)
        {
            assertTrue("i=" + i, equals(cmpcmp[0][i], result[0][i]));
        }
        r++;
        for (int i = 0; i < gapLength; ++i)
        {
            assertNull(result[r++]);
        }
        for (int b = 1; b < cmpcmp.length; ++b)
        {
            for (int i = 0; i < cmpcmp[b].length; ++i)
            {
                assertTrue("["+r+"]["+i+"]", equals(cmpcmp[b][i], result[r][i]));
            }
            ++r;
        }
    }

    @Test public void testArraycloneShrinkGapCMP()
    {
        setupComparableArrays();
        Comparable<Integer> result[] = null;
        assertNull(CollectionHelper.arraycloneShrinkGap(result, 2, 2));

        result = CollectionHelper.arraycloneShrinkGap(cmp7sorted, 2, 3);
        assertNotNull(result);
        assertEquals(4, result.length);
        assertTrue(equals(result[0], cmp7sorted[0]));
        assertTrue(equals(result[1], cmp7sorted[1]));
        assertTrue(equals(result[2], cmp7sorted[5]));
        assertTrue(equals(result[3], cmp7sorted[6]));
    }

    @Test public void testArraycloneShrinkGapCMPCMP()
    {
        setupComparableArrays();
        Comparable<Integer> result[][] = null;
        assertNull(CollectionHelper.arraycloneShrinkGap(result, 2, 2));

        result = CollectionHelper.arraycloneShrinkGap(cmpcmp, 0, 2);
        assertNotNull(result);
        assertEquals(1, result.length);
        assertEquals(cmpcmp[2].length, result[0].length);
        for (int i = 0; i < cmpcmp[2].length; ++i)
        {
            assertTrue("i="+i, equals(cmpcmp[2][i], result[0][i]));
        }
    }

    private Integer integer6sorted[];
    private Integer integer7sorted[];
    private Integer integer7unsorted[];
    private Integer integerinteger[][];

    private void setupIntegerArrays()
    {
        // Look for search errors: even or odd array size, sorted or unsorted
        integer6sorted = new Integer[6];
        integer7sorted = new Integer[7];
        integer7unsorted = new Integer[7];
        for (int i = 0; i < integer7sorted.length; ++i)
        {
            if (i < integer6sorted.length)
            {
                integer6sorted[i] = sorted6keys[i];
            }
            integer7sorted[i] = sorted7keys[i];
            integer7unsorted[i] = unsorted7keys[i];
        }

        // Two dimensional array, total of 7 elements so we can use sorted7keys
        integerinteger = new Integer[3][];
        integerinteger[0] = new Integer[2];
        integerinteger[1] = new Integer[3];
        integerinteger[2] = new Integer[2];
        int index = 0;
        for (int one = 0; one < integerinteger.length; ++one)
        {
            for (int two = 0; two < integerinteger[one].length; ++two)
            {
                integerinteger[one][two] = sorted7keys[index++];
            }
        }
    }

    private boolean equals(Integer a, Integer b)
    {
        return a.equals(b);
    }

    @Test public void testArraycloneINTEGER()
    {
        setupIntegerArrays();
        Integer result[] = CollectionHelper.arrayclone(integer6sorted[0]);
        assertEquals(1, result.length);
        assertTrue(equals(result[0], integer6sorted[0]));

        result = CollectionHelper.arrayclone((Integer[]) null);
        assertNull(result);

        result = CollectionHelper.arrayclone(integer7unsorted);
        assertEquals(integer7unsorted.length, result.length);
        for (int i = 0; i < result.length; ++i)
        {
            assertTrue("i=" + i, equals(result[i], integer7unsorted[i]));
        }

        int toSize = integer7sorted.length+1;
        result = CollectionHelper.arrayclone(integer7sorted, toSize);
        assertEquals(toSize, result.length);
        for (int i = 0; i < integer7sorted.length; ++i)
        {
            assertTrue("i=" + i, equals(result[i], integer7sorted[i]));
        }
        assertNull(result[result.length-1]);

        toSize = 6;
        int fromSize = 5;
        int fromOffset = 1;
        result = CollectionHelper.arrayclone(integer7unsorted, fromOffset,
                    fromSize, toSize);
        assertEquals(toSize, result.length);
        for (int i = 0; i < fromSize; ++i)
        {
            assertTrue("i=" + i, equals(integer7unsorted[i+fromOffset], result[i]));
        }
    }

    @Test public void testArraycloneCombineINTEGER()
    {
        setupIntegerArrays();
        // Method arguments are
        // from - source array
        // startOffset - first copy from[startOffset..from.length-1]
        // endOffset - next copy from[0..endOffset]
        // toSize - size of result, must be
        //     at least from.length-startOffset + endOffset+1
        Integer result[];
        result = CollectionHelper.arraycloneCombine(integer7sorted, 5, 1, 4);
        assertEquals(4, result.length);
        assertTrue(equals(integer7sorted[5], result[0]));
        assertTrue(equals(integer7sorted[6], result[1]));
        assertTrue(equals(integer7sorted[0], result[2]));
        assertNull(result[3]);
    }

    @Test public void testArraycloneExpandGapINTEGER()
    {
        setupIntegerArrays();

        // As implemented, arraycloneExpandGap makes sense when fromOffset == 0
        int gapLength = 3;
        int toSize = integer7unsorted.length+gapLength;
        Integer result[] = CollectionHelper.arraycloneExpandGap(
                integer7unsorted, 0, integer7unsorted.length,
                toSize, 2, gapLength);

        assertEquals(toSize, result.length);
        assertTrue(equals(integer7unsorted[0], result[0]));
        assertTrue(equals(integer7unsorted[1], result[1]));
        assertNull(result[2]);
        assertNull(result[3]);
        assertNull(result[4]);
        assertTrue(equals(integer7unsorted[2], result[5]));
        assertTrue(equals(integer7unsorted[3], result[6]));
        assertTrue(equals(integer7unsorted[4], result[7]));
        assertTrue(equals(integer7unsorted[5], result[8]));
        assertTrue(equals(integer7unsorted[6], result[9]));
    }

    @Test public void testArraycloneINTEGERINTEGER()
    {
        setupIntegerArrays();
        Integer nullMatrix[][] = null;
        Integer result[][];

        result = CollectionHelper.arrayclone(nullMatrix);
        assertNull(result);

        result = CollectionHelper.arrayclone(integerinteger);
        assertNotNull(result);
        assertEquals(integerinteger.length, result.length);
        for (int one = 0; one < integerinteger.length; ++one)
        {
            assertEquals("["+one+"]", integerinteger[one].length, result[one].length);
            for (int two = 0; two < integerinteger[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(integerinteger[one][two], result[one][two]));
            }
        }

        result = CollectionHelper.arrayclone(nullMatrix, 1);
        assertNull(result);

        result = CollectionHelper.arrayclone(integerinteger, integerinteger.length+1);
        assertNotNull(result);
        assertEquals(integerinteger.length+1, result.length);
        assertNull(result[result.length-1]);
        for (int one = 0; one < integerinteger.length; ++one)
        {
            assertEquals("["+one+"]", integerinteger[one].length, result[one].length);
            for (int two = 0; two < integerinteger[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(integerinteger[one][two], result[one][two]));
            }
        }

        result = CollectionHelper.arrayclone(nullMatrix, 1, 1, 1);
        assertNull(result);

        result = CollectionHelper.arrayclone(integerinteger, 2, 1, 1);
        assertNotNull(result);
        assertEquals(1, result.length);
        for (int two = 0; two < integerinteger[2].length; ++two)
        {
            assertTrue("[][" + two + "]", equals(integerinteger[2][two], result[0][two]));
        }
    }

    @Test public void testArraycloneDimensionsINTEGERINTEGER()
    {
        setupIntegerArrays();
        Integer result[][] = null;

        assertNull(CollectionHelper.arraycloneDimensions(result));

        result = CollectionHelper.arraycloneDimensions(integerinteger);
        assertNotNull(result);
        assertEquals(integerinteger.length, result.length);
        for (int one = 0; one < integerinteger.length; ++one)
        {
            assertEquals("["+one+"]", integerinteger[one].length, result[one].length);
            for (int two = 0; two < integerinteger[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(integerinteger[one][two], result[one][two]));
            }
        }
    }

    @Test public void testArraycloneCombineINTEGERINTEGER()
    {
        setupIntegerArrays();
        Integer nullMatrix[][] = null;
        assertNull(CollectionHelper.arraycloneCombine(nullMatrix, 3, 3, 3));

        Integer result[][];
        // Copy integerinteger[1..2] to result[0..1], integerinteger[0..1] to result[2..3]
        result = CollectionHelper.arraycloneCombine(integerinteger, 1, 2, 4);
        assertNotNull(result);
        assertEquals(4, result.length);
        for (int i = 0; i < integerinteger[1].length; ++i)
        {
            assertTrue("i="+i, equals(integerinteger[1][i], result[0][i]));
            assertTrue("i="+i, equals(integerinteger[1][i], result[3][i]));
        }
        for (int i = 0; i < integerinteger[2].length; ++i)
        {
            assertTrue("i="+i, equals(integerinteger[2][i], result[1][i]));
        }
        for (int i = 0; i < integerinteger[0].length; ++i)
        {
            assertTrue("i="+i, equals(integerinteger[0][i], result[2][i]));
        }
    }

    @Test public void testArraycloneExpandGapINTEGERINTEGER()
    {
        setupIntegerArrays();
        Integer nullMatrix[][] = null;
        assertNull(CollectionHelper.arraycloneExpandGap(nullMatrix,5,5,5,5,5));

        // As implemented, arraycloneExpandGap makes sense when fromOffset == 0
        int gapLength = 2;
        int toSize = integerinteger.length + gapLength;
        Integer result[][] = CollectionHelper.arraycloneExpandGap(
                integerinteger, 0, integerinteger.length, toSize, 1, gapLength);
        assertNotNull(result);
        assertEquals(toSize, result.length);

        int r = 0;  // index into result[]
        for (int i = 0; i < integerinteger[0].length; ++i)
        {
            assertTrue("i=" + i, equals(integerinteger[0][i], result[0][i]));
        }
        r++;
        for (int i = 0; i < gapLength; ++i)
        {
            assertNull(result[r++]);
        }
        for (int b = 1; b < integerinteger.length; ++b)
        {
            for (int i = 0; i < integerinteger[b].length; ++i)
            {
                assertTrue("["+r+"]["+i+"]", equals(integerinteger[b][i], result[r][i]));
            }
            ++r;
        }
    }

    @Test public void testArraycloneShrinkGapINTEGER()
    {
        setupIntegerArrays();
        Integer result[] = null;
        assertNull(CollectionHelper.arraycloneShrinkGap(result, 2, 2));

        result = CollectionHelper.arraycloneShrinkGap(integer7sorted, 2, 3);
        assertNotNull(result);
        assertEquals(4, result.length);
        assertTrue(equals(result[0], integer7sorted[0]));
        assertTrue(equals(result[1], integer7sorted[1]));
        assertTrue(equals(result[2], integer7sorted[5]));
        assertTrue(equals(result[3], integer7sorted[6]));
    }

    @Test public void testArraycloneShrinkGapINTEGERINTEGER()
    {
        setupIntegerArrays();
        Integer result[][] = null;
        assertNull(CollectionHelper.arraycloneShrinkGap(result, 2, 2));

        result = CollectionHelper.arraycloneShrinkGap(integerinteger, 0, 2);
        assertNotNull(result);
        assertEquals(1, result.length);
        assertEquals(integerinteger[2].length, result[0].length);
        for (int i = 0; i < integerinteger[2].length; ++i)
        {
            assertTrue("i="+i, equals(integerinteger[2][i], result[0][i]));
        }
    }

    private Long longobj6sorted[];
    private Long longobj7sorted[];
    private Long longobj7unsorted[];
    private Long longobjlongobj[][];

    private void setupLongobjArrays()
    {
        // Look for search errors: even or odd array size, sorted or unsorted
        longobj6sorted = new Long[6];
        longobj7sorted = new Long[7];
        longobj7unsorted = new Long[7];
        for (int i = 0; i < longobj7sorted.length; ++i)
        {
            if (i < longobj6sorted.length)
            {
                longobj6sorted[i] = (long) sorted6keys[i];
            }
            longobj7sorted[i] = (long) sorted7keys[i];
            longobj7unsorted[i] = (long) unsorted7keys[i];
        }

        // Two dimensional array, total of 7 elements so we can use sorted7keys
        longobjlongobj = new Long[3][];
        longobjlongobj[0] = new Long[2];
        longobjlongobj[1] = new Long[3];
        longobjlongobj[2] = new Long[2];
        int index = 0;
        for (int one = 0; one < longobjlongobj.length; ++one)
        {
            for (int two = 0; two < longobjlongobj[one].length; ++two)
            {
                longobjlongobj[one][two] = (long) sorted7keys[index++];
            }
        }
    }

    private boolean equals(Long a, Long b)
    {
        return a.equals(b);
    }

    @Test public void testArraycloneLONGOBJ()
    {
        setupLongobjArrays();
        Long result[] = CollectionHelper.arrayclone(longobj6sorted[0]);
        assertEquals(1, result.length);
        assertTrue(equals(result[0], longobj6sorted[0]));

        result = CollectionHelper.arrayclone((Long[]) null);
        assertNull(result);

        result = CollectionHelper.arrayclone(longobj7unsorted);
        assertEquals(longobj7unsorted.length, result.length);
        for (int i = 0; i < result.length; ++i)
        {
            assertTrue("i=" + i, equals(result[i], longobj7unsorted[i]));
        }

        int toSize = longobj7sorted.length+1;
        result = CollectionHelper.arrayclone(longobj7sorted, toSize);
        assertEquals(toSize, result.length);
        for (int i = 0; i < longobj7sorted.length; ++i)
        {
            assertTrue("i=" + i, equals(result[i], longobj7sorted[i]));
        }
        assertNull(result[result.length-1]);

        toSize = 6;
        int fromSize = 5;
        int fromOffset = 1;
        result = CollectionHelper.arrayclone(longobj7unsorted, fromOffset,
                    fromSize, toSize);
        assertEquals(toSize, result.length);
        for (int i = 0; i < fromSize; ++i)
        {
            assertTrue("i=" + i, equals(longobj7unsorted[i+fromOffset], result[i]));
        }
    }

    @Test public void testArraycloneCombineLONGOBJ()
    {
        setupLongobjArrays();
        // Method arguments are
        // from - source array
        // startOffset - first copy from[startOffset..from.length-1]
        // endOffset - next copy from[0..endOffset]
        // toSize - size of result, must be
        //     at least from.length-startOffset + endOffset+1
        Long result[];
        result = CollectionHelper.arraycloneCombine(longobj7sorted, 5, 1, 4);
        assertEquals(4, result.length);
        assertTrue(equals(longobj7sorted[5], result[0]));
        assertTrue(equals(longobj7sorted[6], result[1]));
        assertTrue(equals(longobj7sorted[0], result[2]));
        assertNull(result[3]);
    }

    @Test public void testArraycloneExpandGapLONGOBJ()
    {
        setupLongobjArrays();

        // As implemented, arraycloneExpandGap makes sense when fromOffset == 0
        int gapLength = 3;
        int toSize = longobj7unsorted.length+gapLength;
        Long result[] = CollectionHelper.arraycloneExpandGap(
                longobj7unsorted, 0, longobj7unsorted.length,
                toSize, 2, gapLength);

        assertEquals(toSize, result.length);
        assertTrue(equals(longobj7unsorted[0], result[0]));
        assertTrue(equals(longobj7unsorted[1], result[1]));
        assertNull(result[2]);
        assertNull(result[3]);
        assertNull(result[4]);
        assertTrue(equals(longobj7unsorted[2], result[5]));
        assertTrue(equals(longobj7unsorted[3], result[6]));
        assertTrue(equals(longobj7unsorted[4], result[7]));
        assertTrue(equals(longobj7unsorted[5], result[8]));
        assertTrue(equals(longobj7unsorted[6], result[9]));
    }

    @Test public void testArraycloneLONGOBJLONGOBJ()
    {
        setupLongobjArrays();
        Long nullMatrix[][] = null;
        Long result[][];

        result = CollectionHelper.arrayclone(nullMatrix);
        assertNull(result);

        result = CollectionHelper.arrayclone(longobjlongobj);
        assertNotNull(result);
        assertEquals(longobjlongobj.length, result.length);
        for (int one = 0; one < longobjlongobj.length; ++one)
        {
            assertEquals("["+one+"]", longobjlongobj[one].length, result[one].length);
            for (int two = 0; two < longobjlongobj[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(longobjlongobj[one][two], result[one][two]));
            }
        }

        result = CollectionHelper.arrayclone(nullMatrix, 1);
        assertNull(result);

        result = CollectionHelper.arrayclone(longobjlongobj, longobjlongobj.length+1);
        assertNotNull(result);
        assertEquals(longobjlongobj.length+1, result.length);
        assertNull(result[result.length-1]);
        for (int one = 0; one < longobjlongobj.length; ++one)
        {
            assertEquals("["+one+"]", longobjlongobj[one].length, result[one].length);
            for (int two = 0; two < longobjlongobj[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(longobjlongobj[one][two], result[one][two]));
            }
        }

        result = CollectionHelper.arrayclone(nullMatrix, 1, 1, 1);
        assertNull(result);

        result = CollectionHelper.arrayclone(longobjlongobj, 2, 1, 1);
        assertNotNull(result);
        assertEquals(1, result.length);
        for (int two = 0; two < longobjlongobj[2].length; ++two)
        {
            assertTrue("[][" + two + "]", equals(longobjlongobj[2][two], result[0][two]));
        }
    }

    @Test public void testArraycloneDimensionsLONGOBJLONGOBJ()
    {
        setupLongobjArrays();
        Long result[][] = null;

        assertNull(CollectionHelper.arraycloneDimensions(result));

        result = CollectionHelper.arraycloneDimensions(longobjlongobj);
        assertNotNull(result);
        assertEquals(longobjlongobj.length, result.length);
        for (int one = 0; one < longobjlongobj.length; ++one)
        {
            assertEquals("["+one+"]", longobjlongobj[one].length, result[one].length);
            for (int two = 0; two < longobjlongobj[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(longobjlongobj[one][two], result[one][two]));
            }
        }
    }

    @Test public void testArraycloneCombineLONGOBJLONGOBJ()
    {
        setupLongobjArrays();
        Long nullMatrix[][] = null;
        assertNull(CollectionHelper.arraycloneCombine(nullMatrix, 3, 3, 3));

        Long result[][];
        // Copy longobjlongobj[1..2] to result[0..1], longobjlongobj[0..1] to result[2..3]
        result = CollectionHelper.arraycloneCombine(longobjlongobj, 1, 2, 4);
        assertNotNull(result);
        assertEquals(4, result.length);
        for (int i = 0; i < longobjlongobj[1].length; ++i)
        {
            assertTrue("i="+i, equals(longobjlongobj[1][i], result[0][i]));
            assertTrue("i="+i, equals(longobjlongobj[1][i], result[3][i]));
        }
        for (int i = 0; i < longobjlongobj[2].length; ++i)
        {
            assertTrue("i="+i, equals(longobjlongobj[2][i], result[1][i]));
        }
        for (int i = 0; i < longobjlongobj[0].length; ++i)
        {
            assertTrue("i="+i, equals(longobjlongobj[0][i], result[2][i]));
        }
    }

    @Test public void testArraycloneExpandGapLONGOBJLONGOBJ()
    {
        setupLongobjArrays();
        Long nullMatrix[][] = null;
        assertNull(CollectionHelper.arraycloneExpandGap(nullMatrix,5,5,5,5,5));

        // As implemented, arraycloneExpandGap makes sense when fromOffset == 0
        int gapLength = 2;
        int toSize = longobjlongobj.length + gapLength;
        Long result[][] = CollectionHelper.arraycloneExpandGap(
                longobjlongobj, 0, longobjlongobj.length, toSize, 1, gapLength);
        assertNotNull(result);
        assertEquals(toSize, result.length);

        int r = 0;  // index into result[]
        for (int i = 0; i < longobjlongobj[0].length; ++i)
        {
            assertTrue("i=" + i, equals(longobjlongobj[0][i], result[0][i]));
        }
        r++;
        for (int i = 0; i < gapLength; ++i)
        {
            assertNull(result[r++]);
        }
        for (int b = 1; b < longobjlongobj.length; ++b)
        {
            for (int i = 0; i < longobjlongobj[b].length; ++i)
            {
                assertTrue("["+r+"]["+i+"]", equals(longobjlongobj[b][i], result[r][i]));
            }
            ++r;
        }
    }

    @Test public void testArraycloneShrinkGapLONGOBJ()
    {
        setupLongobjArrays();
        Long result[] = null;
        assertNull(CollectionHelper.arraycloneShrinkGap(result, 2, 2));

        result = CollectionHelper.arraycloneShrinkGap(longobj7sorted, 2, 3);
        assertNotNull(result);
        assertEquals(4, result.length);
        assertTrue(equals(result[0], longobj7sorted[0]));
        assertTrue(equals(result[1], longobj7sorted[1]));
        assertTrue(equals(result[2], longobj7sorted[5]));
        assertTrue(equals(result[3], longobj7sorted[6]));
    }

    @Test public void testArraycloneShrinkGapLONGOBJLONGOBJ()
    {
        setupLongobjArrays();
        Long result[][] = null;
        assertNull(CollectionHelper.arraycloneShrinkGap(result, 2, 2));

        result = CollectionHelper.arraycloneShrinkGap(longobjlongobj, 0, 2);
        assertNotNull(result);
        assertEquals(1, result.length);
        assertEquals(longobjlongobj[2].length, result[0].length);
        for (int i = 0; i < longobjlongobj[2].length; ++i)
        {
            assertTrue("i="+i, equals(longobjlongobj[2][i], result[0][i]));
        }
    }

    private Short shortobj6sorted[];
    private Short shortobj7sorted[];
    private Short shortobj7unsorted[];
    private Short shortobjshortobj[][];

    private void setupShortobjArrays()
    {
        // Look for search errors: even or odd array size, sorted or unsorted
        shortobj6sorted = new Short[6];
        shortobj7sorted = new Short[7];
        shortobj7unsorted = new Short[7];
        for (int i = 0; i < shortobj7sorted.length; ++i)
        {
            if (i < shortobj6sorted.length)
            {
                shortobj6sorted[i] = (short) sorted6keys[i];
            }
            shortobj7sorted[i] = (short) sorted7keys[i];
            shortobj7unsorted[i] = (short) unsorted7keys[i];
        }

        // Two dimensional array, total of 7 elements so we can use sorted7keys
        shortobjshortobj = new Short[3][];
        shortobjshortobj[0] = new Short[2];
        shortobjshortobj[1] = new Short[3];
        shortobjshortobj[2] = new Short[2];
        int index = 0;
        for (int one = 0; one < shortobjshortobj.length; ++one)
        {
            for (int two = 0; two < shortobjshortobj[one].length; ++two)
            {
                shortobjshortobj[one][two] = (short) sorted7keys[index++];
            }
        }
    }

    private boolean equals(Short a, Short b)
    {
        return a.equals(b);
    }

    @Test public void testArraycloneSHORTOBJ()
    {
        setupShortobjArrays();
        Short result[] = CollectionHelper.arrayclone(shortobj6sorted[0]);
        assertEquals(1, result.length);
        assertTrue(equals(result[0], shortobj6sorted[0]));

        result = CollectionHelper.arrayclone((Short[]) null);
        assertNull(result);

        result = CollectionHelper.arrayclone(shortobj7unsorted);
        assertEquals(shortobj7unsorted.length, result.length);
        for (int i = 0; i < result.length; ++i)
        {
            assertTrue("i=" + i, equals(result[i], shortobj7unsorted[i]));
        }

        int toSize = shortobj7sorted.length+1;
        result = CollectionHelper.arrayclone(shortobj7sorted, toSize);
        assertEquals(toSize, result.length);
        for (int i = 0; i < shortobj7sorted.length; ++i)
        {
            assertTrue("i=" + i, equals(result[i], shortobj7sorted[i]));
        }
        assertNull(result[result.length-1]);

        toSize = 6;
        int fromSize = 5;
        int fromOffset = 1;
        result = CollectionHelper.arrayclone(shortobj7unsorted, fromOffset,
                    fromSize, toSize);
        assertEquals(toSize, result.length);
        for (int i = 0; i < fromSize; ++i)
        {
            assertTrue("i=" + i, equals(shortobj7unsorted[i+fromOffset], result[i]));
        }
    }

    @Test public void testArraycloneCombineSHORTOBJ()
    {
        setupShortobjArrays();
        // Method arguments are
        // from - source array
        // startOffset - first copy from[startOffset..from.length-1]
        // endOffset - next copy from[0..endOffset]
        // toSize - size of result, must be
        //     at least from.length-startOffset + endOffset+1
        Short result[];
        result = CollectionHelper.arraycloneCombine(shortobj7sorted, 5, 1, 4);
        assertEquals(4, result.length);
        assertTrue(equals(shortobj7sorted[5], result[0]));
        assertTrue(equals(shortobj7sorted[6], result[1]));
        assertTrue(equals(shortobj7sorted[0], result[2]));
        assertNull(result[3]);
    }

    @Test public void testArraycloneExpandGapSHORTOBJ()
    {
        setupShortobjArrays();

        // As implemented, arraycloneExpandGap makes sense when fromOffset == 0
        int gapLength = 3;
        int toSize = shortobj7unsorted.length+gapLength;
        Short result[] = CollectionHelper.arraycloneExpandGap(
                shortobj7unsorted, 0, shortobj7unsorted.length,
                toSize, 2, gapLength);

        assertEquals(toSize, result.length);
        assertTrue(equals(shortobj7unsorted[0], result[0]));
        assertTrue(equals(shortobj7unsorted[1], result[1]));
        assertNull(result[2]);
        assertNull(result[3]);
        assertNull(result[4]);
        assertTrue(equals(shortobj7unsorted[2], result[5]));
        assertTrue(equals(shortobj7unsorted[3], result[6]));
        assertTrue(equals(shortobj7unsorted[4], result[7]));
        assertTrue(equals(shortobj7unsorted[5], result[8]));
        assertTrue(equals(shortobj7unsorted[6], result[9]));
    }

    @Test public void testArraycloneSHORTOBJSHORTOBJ()
    {
        setupShortobjArrays();
        Short nullMatrix[][] = null;
        Short result[][];

        result = CollectionHelper.arrayclone(nullMatrix);
        assertNull(result);

        result = CollectionHelper.arrayclone(shortobjshortobj);
        assertNotNull(result);
        assertEquals(shortobjshortobj.length, result.length);
        for (int one = 0; one < shortobjshortobj.length; ++one)
        {
            assertEquals("["+one+"]", shortobjshortobj[one].length, result[one].length);
            for (int two = 0; two < shortobjshortobj[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(shortobjshortobj[one][two], result[one][two]));
            }
        }

        result = CollectionHelper.arrayclone(nullMatrix, 1);
        assertNull(result);

        result = CollectionHelper.arrayclone(shortobjshortobj, shortobjshortobj.length+1);
        assertNotNull(result);
        assertEquals(shortobjshortobj.length+1, result.length);
        assertNull(result[result.length-1]);
        for (int one = 0; one < shortobjshortobj.length; ++one)
        {
            assertEquals("["+one+"]", shortobjshortobj[one].length, result[one].length);
            for (int two = 0; two < shortobjshortobj[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(shortobjshortobj[one][two], result[one][two]));
            }
        }

        result = CollectionHelper.arrayclone(nullMatrix, 1, 1, 1);
        assertNull(result);

        result = CollectionHelper.arrayclone(shortobjshortobj, 2, 1, 1);
        assertNotNull(result);
        assertEquals(1, result.length);
        for (int two = 0; two < shortobjshortobj[2].length; ++two)
        {
            assertTrue("[][" + two + "]", equals(shortobjshortobj[2][two], result[0][two]));
        }
    }

    @Test public void testArraycloneDimensionsSHORTOBJSHORTOBJ()
    {
        setupShortobjArrays();
        Short result[][] = null;

        assertNull(CollectionHelper.arraycloneDimensions(result));

        result = CollectionHelper.arraycloneDimensions(shortobjshortobj);
        assertNotNull(result);
        assertEquals(shortobjshortobj.length, result.length);
        for (int one = 0; one < shortobjshortobj.length; ++one)
        {
            assertEquals("["+one+"]", shortobjshortobj[one].length, result[one].length);
            for (int two = 0; two < shortobjshortobj[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(shortobjshortobj[one][two], result[one][two]));
            }
        }
    }

    @Test public void testArraycloneCombineSHORTOBJSHORTOBJ()
    {
        setupShortobjArrays();
        Short nullMatrix[][] = null;
        assertNull(CollectionHelper.arraycloneCombine(nullMatrix, 3, 3, 3));

        Short result[][];
        // Copy shortobjshortobj[1..2] to result[0..1], shortobjshortobj[0..1] to result[2..3]
        result = CollectionHelper.arraycloneCombine(shortobjshortobj, 1, 2, 4);
        assertNotNull(result);
        assertEquals(4, result.length);
        for (int i = 0; i < shortobjshortobj[1].length; ++i)
        {
            assertTrue("i="+i, equals(shortobjshortobj[1][i], result[0][i]));
            assertTrue("i="+i, equals(shortobjshortobj[1][i], result[3][i]));
        }
        for (int i = 0; i < shortobjshortobj[2].length; ++i)
        {
            assertTrue("i="+i, equals(shortobjshortobj[2][i], result[1][i]));
        }
        for (int i = 0; i < shortobjshortobj[0].length; ++i)
        {
            assertTrue("i="+i, equals(shortobjshortobj[0][i], result[2][i]));
        }
    }

    @Test public void testArraycloneExpandGapSHORTOBJSHORTOBJ()
    {
        setupShortobjArrays();
        Short nullMatrix[][] = null;
        assertNull(CollectionHelper.arraycloneExpandGap(nullMatrix,5,5,5,5,5));

        // As implemented, arraycloneExpandGap makes sense when fromOffset == 0
        int gapLength = 2;
        int toSize = shortobjshortobj.length + gapLength;
        Short result[][] = CollectionHelper.arraycloneExpandGap(
                shortobjshortobj, 0, shortobjshortobj.length, toSize, 1, gapLength);
        assertNotNull(result);
        assertEquals(toSize, result.length);

        int r = 0;  // index into result[]
        for (int i = 0; i < shortobjshortobj[0].length; ++i)
        {
            assertTrue("i=" + i, equals(shortobjshortobj[0][i], result[0][i]));
        }
        r++;
        for (int i = 0; i < gapLength; ++i)
        {
            assertNull(result[r++]);
        }
        for (int b = 1; b < shortobjshortobj.length; ++b)
        {
            for (int i = 0; i < shortobjshortobj[b].length; ++i)
            {
                assertTrue("["+r+"]["+i+"]", equals(shortobjshortobj[b][i], result[r][i]));
            }
            ++r;
        }
    }

    @Test public void testArraycloneShrinkGapSHORTOBJ()
    {
        setupShortobjArrays();
        Short result[] = null;
        assertNull(CollectionHelper.arraycloneShrinkGap(result, 2, 2));

        result = CollectionHelper.arraycloneShrinkGap(shortobj7sorted, 2, 3);
        assertNotNull(result);
        assertEquals(4, result.length);
        assertTrue(equals(result[0], shortobj7sorted[0]));
        assertTrue(equals(result[1], shortobj7sorted[1]));
        assertTrue(equals(result[2], shortobj7sorted[5]));
        assertTrue(equals(result[3], shortobj7sorted[6]));
    }

    @Test public void testArraycloneShrinkGapSHORTOBJSHORTOBJ()
    {
        setupShortobjArrays();
        Short result[][] = null;
        assertNull(CollectionHelper.arraycloneShrinkGap(result, 2, 2));

        result = CollectionHelper.arraycloneShrinkGap(shortobjshortobj, 0, 2);
        assertNotNull(result);
        assertEquals(1, result.length);
        assertEquals(shortobjshortobj[2].length, result[0].length);
        for (int i = 0; i < shortobjshortobj[2].length; ++i)
        {
            assertTrue("i="+i, equals(shortobjshortobj[2][i], result[0][i]));
        }
    }

    private short short6sorted[];
    private short short7sorted[];
    private short short7unsorted[];
    private short shortshort[][];

    private void setupShortArrays()
    {
        // Look for search errors: even or odd array size, sorted or unsorted
        short6sorted = new short[6];
        short7sorted = new short[7];
        short7unsorted = new short[7];
        for (int i = 0; i < short7sorted.length; ++i)
        {
            if (i < short6sorted.length)
            {
                short6sorted[i] = (short) sorted6keys[i];
            }
            short7sorted[i] = (short) sorted7keys[i];
            short7unsorted[i] = (short) unsorted7keys[i];
        }

        // Two dimensional array, total of 7 elements so we can use sorted7keys
        shortshort = new short[3][];
        shortshort[0] = new short[2];
        shortshort[1] = new short[3];
        shortshort[2] = new short[2];
        int index = 0;
        for (int one = 0; one < shortshort.length; ++one)
        {
            for (int two = 0; two < shortshort[one].length; ++two)
            {
                shortshort[one][two] = (short) sorted7keys[index++];
            }
        }
    }

    private boolean equals(short a, short b)
    {
        return a == b;
    }

    @Test public void testArraycloneSHORT()
    {
        setupShortArrays();
        short result[] = CollectionHelper.arrayclone(short6sorted[0]);
        assertEquals(1, result.length);
        assertTrue(equals(result[0], short6sorted[0]));

        result = CollectionHelper.arrayclone((short[]) null);
        assertNull(result);

        result = CollectionHelper.arrayclone(short7unsorted);
        assertEquals(short7unsorted.length, result.length);
        for (int i = 0; i < result.length; ++i)
        {
            assertTrue("i=" + i, equals(result[i], short7unsorted[i]));
        }

        int toSize = short7sorted.length+1;
        result = CollectionHelper.arrayclone(short7sorted, toSize);
        assertEquals(toSize, result.length);
        for (int i = 0; i < short7sorted.length; ++i)
        {
            assertTrue("i=" + i, equals(result[i], short7sorted[i]));
        }
        assertEquals(0, result[result.length-1]);

        toSize = 6;
        int fromSize = 5;
        int fromOffset = 1;
        result = CollectionHelper.arrayclone(short7unsorted, fromOffset,
                    fromSize, toSize);
        assertEquals(toSize, result.length);
        for (int i = 0; i < fromSize; ++i)
        {
            assertTrue("i=" + i, equals(short7unsorted[i+fromOffset], result[i]));
        }
    }

    @Test public void testArraycloneCombineSHORT()
    {
        setupShortArrays();
        // Method arguments are
        // from - source array
        // startOffset - first copy from[startOffset..from.length-1]
        // endOffset - next copy from[0..endOffset]
        // toSize - size of result, must be
        //     at least from.length-startOffset + endOffset+1
        short result[];
        result = CollectionHelper.arraycloneCombine(short7sorted, 5, 1, 4);
        assertEquals(4, result.length);
        assertTrue(equals(short7sorted[5], result[0]));
        assertTrue(equals(short7sorted[6], result[1]));
        assertTrue(equals(short7sorted[0], result[2]));
        assertEquals(0, result[3]);
    }

    @Test public void testArraycloneExpandGapSHORT()
    {
        setupShortArrays();

        // As implemented, arraycloneExpandGap makes sense when fromOffset == 0
        int gapLength = 3;
        int toSize = short7unsorted.length+gapLength;
        short result[] = CollectionHelper.arraycloneExpandGap(
                short7unsorted, 0, short7unsorted.length,
                toSize, 2, gapLength);

        assertEquals(toSize, result.length);
        assertTrue(equals(short7unsorted[0], result[0]));
        assertTrue(equals(short7unsorted[1], result[1]));
        assertEquals(0, result[2]);
        assertEquals(0, result[3]);
        assertEquals(0, result[4]);
        assertTrue(equals(short7unsorted[2], result[5]));
        assertTrue(equals(short7unsorted[3], result[6]));
        assertTrue(equals(short7unsorted[4], result[7]));
        assertTrue(equals(short7unsorted[5], result[8]));
        assertTrue(equals(short7unsorted[6], result[9]));
    }

    @Test public void testArraycloneSHORTSHORT()
    {
        setupShortArrays();
        short nullMatrix[][] = null;
        short result[][];

        result = CollectionHelper.arrayclone(nullMatrix);
        assertNull(result);

        result = CollectionHelper.arrayclone(shortshort);
        assertNotNull(result);
        assertEquals(shortshort.length, result.length);
        for (int one = 0; one < shortshort.length; ++one)
        {
            assertEquals("["+one+"]", shortshort[one].length, result[one].length);
            for (int two = 0; two < shortshort[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(shortshort[one][two], result[one][two]));
            }
        }

        result = CollectionHelper.arrayclone(nullMatrix, 1);
        assertNull(result);

        result = CollectionHelper.arrayclone(shortshort, shortshort.length+1);
        assertNotNull(result);
        assertEquals(shortshort.length+1, result.length);
        assertNull(result[result.length-1]);
        for (int one = 0; one < shortshort.length; ++one)
        {
            assertEquals("["+one+"]", shortshort[one].length, result[one].length);
            for (int two = 0; two < shortshort[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(shortshort[one][two], result[one][two]));
            }
        }

        result = CollectionHelper.arrayclone(nullMatrix, 1, 1, 1);
        assertNull(result);

        result = CollectionHelper.arrayclone(shortshort, 2, 1, 1);
        assertNotNull(result);
        assertEquals(1, result.length);
        for (int two = 0; two < shortshort[2].length; ++two)
        {
            assertTrue("[][" + two + "]", equals(shortshort[2][two], result[0][two]));
        }
    }

    @Test public void testArraycloneDimensionsSHORTSHORT()
    {
        setupShortArrays();
        short result[][] = null;

        assertNull(CollectionHelper.arraycloneDimensions(result));

        result = CollectionHelper.arraycloneDimensions(shortshort);
        assertNotNull(result);
        assertEquals(shortshort.length, result.length);
        for (int one = 0; one < shortshort.length; ++one)
        {
            assertEquals("["+one+"]", shortshort[one].length, result[one].length);
            for (int two = 0; two < shortshort[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(shortshort[one][two], result[one][two]));
            }
        }
    }

    @Test public void testArraycloneCombineSHORTSHORT()
    {
        setupShortArrays();
        short nullMatrix[][] = null;
        assertNull(CollectionHelper.arraycloneCombine(nullMatrix, 3, 3, 3));

        short result[][];
        // Copy shortshort[1..2] to result[0..1], shortshort[0..1] to result[2..3]
        result = CollectionHelper.arraycloneCombine(shortshort, 1, 2, 4);
        assertNotNull(result);
        assertEquals(4, result.length);
        for (int i = 0; i < shortshort[1].length; ++i)
        {
            assertTrue("i="+i, equals(shortshort[1][i], result[0][i]));
            assertTrue("i="+i, equals(shortshort[1][i], result[3][i]));
        }
        for (int i = 0; i < shortshort[2].length; ++i)
        {
            assertTrue("i="+i, equals(shortshort[2][i], result[1][i]));
        }
        for (int i = 0; i < shortshort[0].length; ++i)
        {
            assertTrue("i="+i, equals(shortshort[0][i], result[2][i]));
        }
    }

    @Test public void testArraycloneExpandGapSHORTSHORT()
    {
        setupShortArrays();
        short nullMatrix[][] = null;
        assertNull(CollectionHelper.arraycloneExpandGap(nullMatrix,5,5,5,5,5));

        // As implemented, arraycloneExpandGap makes sense when fromOffset == 0
        int gapLength = 2;
        int toSize = shortshort.length + gapLength;
        short result[][] = CollectionHelper.arraycloneExpandGap(
                shortshort, 0, shortshort.length, toSize, 1, gapLength);
        assertNotNull(result);
        assertEquals(toSize, result.length);

        int r = 0;  // index into result[]
        for (int i = 0; i < shortshort[0].length; ++i)
        {
            assertTrue("i=" + i, equals(shortshort[0][i], result[0][i]));
        }
        r++;
        for (int i = 0; i < gapLength; ++i)
        {
            assertNull(result[r++]);
        }
        for (int b = 1; b < shortshort.length; ++b)
        {
            for (int i = 0; i < shortshort[b].length; ++i)
            {
                assertTrue("["+r+"]["+i+"]", equals(shortshort[b][i], result[r][i]));
            }
            ++r;
        }
    }

    @Test public void testArraycloneShrinkGapSHORT()
    {
        setupShortArrays();
        short result[] = null;
        assertNull(CollectionHelper.arraycloneShrinkGap(result, 2, 2));

        result = CollectionHelper.arraycloneShrinkGap(short7sorted, 2, 3);
        assertNotNull(result);
        assertEquals(4, result.length);
        assertTrue(equals(result[0], short7sorted[0]));
        assertTrue(equals(result[1], short7sorted[1]));
        assertTrue(equals(result[2], short7sorted[5]));
        assertTrue(equals(result[3], short7sorted[6]));
    }

    @Test public void testArraycloneShrinkGapSHORTSHORT()
    {
        setupShortArrays();
        short result[][] = null;
        assertNull(CollectionHelper.arraycloneShrinkGap(result, 2, 2));

        result = CollectionHelper.arraycloneShrinkGap(shortshort, 0, 2);
        assertNotNull(result);
        assertEquals(1, result.length);
        assertEquals(shortshort[2].length, result[0].length);
        for (int i = 0; i < shortshort[2].length; ++i)
        {
            assertTrue("i="+i, equals(shortshort[2][i], result[0][i]));
        }
    }

    private Byte byteobj6sorted[];
    private Byte byteobj7sorted[];
    private Byte byteobj7unsorted[];
    private Byte byteobjbyteobj[][];

    private void setupByteobjArrays()
    {
        // Look for search errors: even or odd array size, sorted or unsorted
        byteobj6sorted = new Byte[6];
        byteobj7sorted = new Byte[7];
        byteobj7unsorted = new Byte[7];
        for (int i = 0; i < byteobj7sorted.length; ++i)
        {
            if (i < byteobj6sorted.length)
            {
                byteobj6sorted[i] = (byte) sorted6keys[i];
            }
            byteobj7sorted[i] = (byte) sorted7keys[i];
            byteobj7unsorted[i] = (byte) unsorted7keys[i];
        }

        // Two dimensional array, total of 7 elements so we can use sorted7keys
        byteobjbyteobj = new Byte[3][];
        byteobjbyteobj[0] = new Byte[2];
        byteobjbyteobj[1] = new Byte[3];
        byteobjbyteobj[2] = new Byte[2];
        int index = 0;
        for (int one = 0; one < byteobjbyteobj.length; ++one)
        {
            for (int two = 0; two < byteobjbyteobj[one].length; ++two)
            {
                byteobjbyteobj[one][two] = (byte) sorted7keys[index++];
            }
        }
    }

    private boolean equals(Byte a, Byte b)
    {
        return a.equals(b);
    }

    @Test public void testArraycloneBYTEOBJ()
    {
        setupByteobjArrays();
        Byte result[] = CollectionHelper.arrayclone(byteobj6sorted[0]);
        assertEquals(1, result.length);
        assertTrue(equals(result[0], byteobj6sorted[0]));

        result = CollectionHelper.arrayclone((Byte[]) null);
        assertNull(result);

        result = CollectionHelper.arrayclone(byteobj7unsorted);
        assertEquals(byteobj7unsorted.length, result.length);
        for (int i = 0; i < result.length; ++i)
        {
            assertTrue("i=" + i, equals(result[i], byteobj7unsorted[i]));
        }

        int toSize = byteobj7sorted.length+1;
        result = CollectionHelper.arrayclone(byteobj7sorted, toSize);
        assertEquals(toSize, result.length);
        for (int i = 0; i < byteobj7sorted.length; ++i)
        {
            assertTrue("i=" + i, equals(result[i], byteobj7sorted[i]));
        }
        assertNull(result[result.length-1]);

        toSize = 6;
        int fromSize = 5;
        int fromOffset = 1;
        result = CollectionHelper.arrayclone(byteobj7unsorted, fromOffset,
                    fromSize, toSize);
        assertEquals(toSize, result.length);
        for (int i = 0; i < fromSize; ++i)
        {
            assertTrue("i=" + i, equals(byteobj7unsorted[i+fromOffset], result[i]));
        }
    }

    @Test public void testArraycloneCombineBYTEOBJ()
    {
        setupByteobjArrays();
        // Method arguments are
        // from - source array
        // startOffset - first copy from[startOffset..from.length-1]
        // endOffset - next copy from[0..endOffset]
        // toSize - size of result, must be
        //     at least from.length-startOffset + endOffset+1
        Byte result[];
        result = CollectionHelper.arraycloneCombine(byteobj7sorted, 5, 1, 4);
        assertEquals(4, result.length);
        assertTrue(equals(byteobj7sorted[5], result[0]));
        assertTrue(equals(byteobj7sorted[6], result[1]));
        assertTrue(equals(byteobj7sorted[0], result[2]));
        assertNull(result[3]);
    }

    @Test public void testArraycloneExpandGapBYTEOBJ()
    {
        setupByteobjArrays();

        // As implemented, arraycloneExpandGap makes sense when fromOffset == 0
        int gapLength = 3;
        int toSize = byteobj7unsorted.length+gapLength;
        Byte result[] = CollectionHelper.arraycloneExpandGap(
                byteobj7unsorted, 0, byteobj7unsorted.length,
                toSize, 2, gapLength);

        assertEquals(toSize, result.length);
        assertTrue(equals(byteobj7unsorted[0], result[0]));
        assertTrue(equals(byteobj7unsorted[1], result[1]));
        assertNull(result[2]);
        assertNull(result[3]);
        assertNull(result[4]);
        assertTrue(equals(byteobj7unsorted[2], result[5]));
        assertTrue(equals(byteobj7unsorted[3], result[6]));
        assertTrue(equals(byteobj7unsorted[4], result[7]));
        assertTrue(equals(byteobj7unsorted[5], result[8]));
        assertTrue(equals(byteobj7unsorted[6], result[9]));
    }

    @Test public void testArraycloneBYTEOBJBYTEOBJ()
    {
        setupByteobjArrays();
        Byte nullMatrix[][] = null;
        Byte result[][];

        result = CollectionHelper.arrayclone(nullMatrix);
        assertNull(result);

        result = CollectionHelper.arrayclone(byteobjbyteobj);
        assertNotNull(result);
        assertEquals(byteobjbyteobj.length, result.length);
        for (int one = 0; one < byteobjbyteobj.length; ++one)
        {
            assertEquals("["+one+"]", byteobjbyteobj[one].length, result[one].length);
            for (int two = 0; two < byteobjbyteobj[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(byteobjbyteobj[one][two], result[one][two]));
            }
        }

        result = CollectionHelper.arrayclone(nullMatrix, 1);
        assertNull(result);

        result = CollectionHelper.arrayclone(byteobjbyteobj, byteobjbyteobj.length+1);
        assertNotNull(result);
        assertEquals(byteobjbyteobj.length+1, result.length);
        assertNull(result[result.length-1]);
        for (int one = 0; one < byteobjbyteobj.length; ++one)
        {
            assertEquals("["+one+"]", byteobjbyteobj[one].length, result[one].length);
            for (int two = 0; two < byteobjbyteobj[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(byteobjbyteobj[one][two], result[one][two]));
            }
        }

        result = CollectionHelper.arrayclone(nullMatrix, 1, 1, 1);
        assertNull(result);

        result = CollectionHelper.arrayclone(byteobjbyteobj, 2, 1, 1);
        assertNotNull(result);
        assertEquals(1, result.length);
        for (int two = 0; two < byteobjbyteobj[2].length; ++two)
        {
            assertTrue("[][" + two + "]", equals(byteobjbyteobj[2][two], result[0][two]));
        }
    }

    @Test public void testArraycloneDimensionsBYTEOBJBYTEOBJ()
    {
        setupByteobjArrays();
        Byte result[][] = null;

        assertNull(CollectionHelper.arraycloneDimensions(result));

        result = CollectionHelper.arraycloneDimensions(byteobjbyteobj);
        assertNotNull(result);
        assertEquals(byteobjbyteobj.length, result.length);
        for (int one = 0; one < byteobjbyteobj.length; ++one)
        {
            assertEquals("["+one+"]", byteobjbyteobj[one].length, result[one].length);
            for (int two = 0; two < byteobjbyteobj[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(byteobjbyteobj[one][two], result[one][two]));
            }
        }
    }

    @Test public void testArraycloneCombineBYTEOBJBYTEOBJ()
    {
        setupByteobjArrays();
        Byte nullMatrix[][] = null;
        assertNull(CollectionHelper.arraycloneCombine(nullMatrix, 3, 3, 3));

        Byte result[][];
        // Copy byteobjbyteobj[1..2] to result[0..1], byteobjbyteobj[0..1] to result[2..3]
        result = CollectionHelper.arraycloneCombine(byteobjbyteobj, 1, 2, 4);
        assertNotNull(result);
        assertEquals(4, result.length);
        for (int i = 0; i < byteobjbyteobj[1].length; ++i)
        {
            assertTrue("i="+i, equals(byteobjbyteobj[1][i], result[0][i]));
            assertTrue("i="+i, equals(byteobjbyteobj[1][i], result[3][i]));
        }
        for (int i = 0; i < byteobjbyteobj[2].length; ++i)
        {
            assertTrue("i="+i, equals(byteobjbyteobj[2][i], result[1][i]));
        }
        for (int i = 0; i < byteobjbyteobj[0].length; ++i)
        {
            assertTrue("i="+i, equals(byteobjbyteobj[0][i], result[2][i]));
        }
    }

    @Test public void testArraycloneExpandGapBYTEOBJBYTEOBJ()
    {
        setupByteobjArrays();
        Byte nullMatrix[][] = null;
        assertNull(CollectionHelper.arraycloneExpandGap(nullMatrix,5,5,5,5,5));

        // As implemented, arraycloneExpandGap makes sense when fromOffset == 0
        int gapLength = 2;
        int toSize = byteobjbyteobj.length + gapLength;
        Byte result[][] = CollectionHelper.arraycloneExpandGap(
                byteobjbyteobj, 0, byteobjbyteobj.length, toSize, 1, gapLength);
        assertNotNull(result);
        assertEquals(toSize, result.length);

        int r = 0;  // index into result[]
        for (int i = 0; i < byteobjbyteobj[0].length; ++i)
        {
            assertTrue("i=" + i, equals(byteobjbyteobj[0][i], result[0][i]));
        }
        r++;
        for (int i = 0; i < gapLength; ++i)
        {
            assertNull(result[r++]);
        }
        for (int b = 1; b < byteobjbyteobj.length; ++b)
        {
            for (int i = 0; i < byteobjbyteobj[b].length; ++i)
            {
                assertTrue("["+r+"]["+i+"]", equals(byteobjbyteobj[b][i], result[r][i]));
            }
            ++r;
        }
    }

    @Test public void testArraycloneShrinkGapBYTEOBJ()
    {
        setupByteobjArrays();
        Byte result[] = null;
        assertNull(CollectionHelper.arraycloneShrinkGap(result, 2, 2));

        result = CollectionHelper.arraycloneShrinkGap(byteobj7sorted, 2, 3);
        assertNotNull(result);
        assertEquals(4, result.length);
        assertTrue(equals(result[0], byteobj7sorted[0]));
        assertTrue(equals(result[1], byteobj7sorted[1]));
        assertTrue(equals(result[2], byteobj7sorted[5]));
        assertTrue(equals(result[3], byteobj7sorted[6]));
    }

    @Test public void testArraycloneShrinkGapBYTEOBJBYTEOBJ()
    {
        setupByteobjArrays();
        Byte result[][] = null;
        assertNull(CollectionHelper.arraycloneShrinkGap(result, 2, 2));

        result = CollectionHelper.arraycloneShrinkGap(byteobjbyteobj, 0, 2);
        assertNotNull(result);
        assertEquals(1, result.length);
        assertEquals(byteobjbyteobj[2].length, result[0].length);
        for (int i = 0; i < byteobjbyteobj[2].length; ++i)
        {
            assertTrue("i="+i, equals(byteobjbyteobj[2][i], result[0][i]));
        }
    }

    private byte byte6sorted[];
    private byte byte7sorted[];
    private byte byte7unsorted[];
    private byte bytebyte[][];

    private void setupByteArrays()
    {
        // Look for search errors: even or odd array size, sorted or unsorted
        byte6sorted = new byte[6];
        byte7sorted = new byte[7];
        byte7unsorted = new byte[7];
        for (int i = 0; i < byte7sorted.length; ++i)
        {
            if (i < byte6sorted.length)
            {
                byte6sorted[i] = (byte) sorted6keys[i];
            }
            byte7sorted[i] = (byte) sorted7keys[i];
            byte7unsorted[i] = (byte) unsorted7keys[i];
        }

        // Two dimensional array, total of 7 elements so we can use sorted7keys
        bytebyte = new byte[3][];
        bytebyte[0] = new byte[2];
        bytebyte[1] = new byte[3];
        bytebyte[2] = new byte[2];
        int index = 0;
        for (int one = 0; one < bytebyte.length; ++one)
        {
            for (int two = 0; two < bytebyte[one].length; ++two)
            {
                bytebyte[one][two] = (byte) sorted7keys[index++];
            }
        }
    }

    private boolean equals(byte a, byte b)
    {
        return a == b;
    }

    @Test public void testArraycloneBYTE()
    {
        setupByteArrays();
        byte result[] = CollectionHelper.arrayclone(byte6sorted[0]);
        assertEquals(1, result.length);
        assertTrue(equals(result[0], byte6sorted[0]));

        result = CollectionHelper.arrayclone((byte[]) null);
        assertNull(result);

        result = CollectionHelper.arrayclone(byte7unsorted);
        assertEquals(byte7unsorted.length, result.length);
        for (int i = 0; i < result.length; ++i)
        {
            assertTrue("i=" + i, equals(result[i], byte7unsorted[i]));
        }

        int toSize = byte7sorted.length+1;
        result = CollectionHelper.arrayclone(byte7sorted, toSize);
        assertEquals(toSize, result.length);
        for (int i = 0; i < byte7sorted.length; ++i)
        {
            assertTrue("i=" + i, equals(result[i], byte7sorted[i]));
        }
        assertEquals(0, result[result.length-1]);

        toSize = 6;
        int fromSize = 5;
        int fromOffset = 1;
        result = CollectionHelper.arrayclone(byte7unsorted, fromOffset,
                    fromSize, toSize);
        assertEquals(toSize, result.length);
        for (int i = 0; i < fromSize; ++i)
        {
            assertTrue("i=" + i, equals(byte7unsorted[i+fromOffset], result[i]));
        }
    }

    @Test public void testArraycloneCombineBYTE()
    {
        setupByteArrays();
        // Method arguments are
        // from - source array
        // startOffset - first copy from[startOffset..from.length-1]
        // endOffset - next copy from[0..endOffset]
        // toSize - size of result, must be
        //     at least from.length-startOffset + endOffset+1
        byte result[];
        result = CollectionHelper.arraycloneCombine(byte7sorted, 5, 1, 4);
        assertEquals(4, result.length);
        assertTrue(equals(byte7sorted[5], result[0]));
        assertTrue(equals(byte7sorted[6], result[1]));
        assertTrue(equals(byte7sorted[0], result[2]));
        assertEquals(0, result[3]);
    }

    @Test public void testArraycloneExpandGapBYTE()
    {
        setupByteArrays();

        // As implemented, arraycloneExpandGap makes sense when fromOffset == 0
        int gapLength = 3;
        int toSize = byte7unsorted.length+gapLength;
        byte result[] = CollectionHelper.arraycloneExpandGap(
                byte7unsorted, 0, byte7unsorted.length,
                toSize, 2, gapLength);

        assertEquals(toSize, result.length);
        assertTrue(equals(byte7unsorted[0], result[0]));
        assertTrue(equals(byte7unsorted[1], result[1]));
        assertEquals(0, result[2]);
        assertEquals(0, result[3]);
        assertEquals(0, result[4]);
        assertTrue(equals(byte7unsorted[2], result[5]));
        assertTrue(equals(byte7unsorted[3], result[6]));
        assertTrue(equals(byte7unsorted[4], result[7]));
        assertTrue(equals(byte7unsorted[5], result[8]));
        assertTrue(equals(byte7unsorted[6], result[9]));
    }

    @Test public void testArraycloneBYTEBYTE()
    {
        setupByteArrays();
        byte nullMatrix[][] = null;
        byte result[][];

        result = CollectionHelper.arrayclone(nullMatrix);
        assertNull(result);

        result = CollectionHelper.arrayclone(bytebyte);
        assertNotNull(result);
        assertEquals(bytebyte.length, result.length);
        for (int one = 0; one < bytebyte.length; ++one)
        {
            assertEquals("["+one+"]", bytebyte[one].length, result[one].length);
            for (int two = 0; two < bytebyte[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(bytebyte[one][two], result[one][two]));
            }
        }

        result = CollectionHelper.arrayclone(nullMatrix, 1);
        assertNull(result);

        result = CollectionHelper.arrayclone(bytebyte, bytebyte.length+1);
        assertNotNull(result);
        assertEquals(bytebyte.length+1, result.length);
        assertNull(result[result.length-1]);
        for (int one = 0; one < bytebyte.length; ++one)
        {
            assertEquals("["+one+"]", bytebyte[one].length, result[one].length);
            for (int two = 0; two < bytebyte[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(bytebyte[one][two], result[one][two]));
            }
        }

        result = CollectionHelper.arrayclone(nullMatrix, 1, 1, 1);
        assertNull(result);

        result = CollectionHelper.arrayclone(bytebyte, 2, 1, 1);
        assertNotNull(result);
        assertEquals(1, result.length);
        for (int two = 0; two < bytebyte[2].length; ++two)
        {
            assertTrue("[][" + two + "]", equals(bytebyte[2][two], result[0][two]));
        }
    }

    @Test public void testArraycloneDimensionsBYTEBYTE()
    {
        setupByteArrays();
        byte result[][] = null;

        assertNull(CollectionHelper.arraycloneDimensions(result));

        result = CollectionHelper.arraycloneDimensions(bytebyte);
        assertNotNull(result);
        assertEquals(bytebyte.length, result.length);
        for (int one = 0; one < bytebyte.length; ++one)
        {
            assertEquals("["+one+"]", bytebyte[one].length, result[one].length);
            for (int two = 0; two < bytebyte[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(bytebyte[one][two], result[one][two]));
            }
        }
    }

    @Test public void testArraycloneCombineBYTEBYTE()
    {
        setupByteArrays();
        byte nullMatrix[][] = null;
        assertNull(CollectionHelper.arraycloneCombine(nullMatrix, 3, 3, 3));

        byte result[][];
        // Copy bytebyte[1..2] to result[0..1], bytebyte[0..1] to result[2..3]
        result = CollectionHelper.arraycloneCombine(bytebyte, 1, 2, 4);
        assertNotNull(result);
        assertEquals(4, result.length);
        for (int i = 0; i < bytebyte[1].length; ++i)
        {
            assertTrue("i="+i, equals(bytebyte[1][i], result[0][i]));
            assertTrue("i="+i, equals(bytebyte[1][i], result[3][i]));
        }
        for (int i = 0; i < bytebyte[2].length; ++i)
        {
            assertTrue("i="+i, equals(bytebyte[2][i], result[1][i]));
        }
        for (int i = 0; i < bytebyte[0].length; ++i)
        {
            assertTrue("i="+i, equals(bytebyte[0][i], result[2][i]));
        }
    }

    @Test public void testArraycloneExpandGapBYTEBYTE()
    {
        setupByteArrays();
        byte nullMatrix[][] = null;
        assertNull(CollectionHelper.arraycloneExpandGap(nullMatrix,5,5,5,5,5));

        // As implemented, arraycloneExpandGap makes sense when fromOffset == 0
        int gapLength = 2;
        int toSize = bytebyte.length + gapLength;
        byte result[][] = CollectionHelper.arraycloneExpandGap(
                bytebyte, 0, bytebyte.length, toSize, 1, gapLength);
        assertNotNull(result);
        assertEquals(toSize, result.length);

        int r = 0;  // index into result[]
        for (int i = 0; i < bytebyte[0].length; ++i)
        {
            assertTrue("i=" + i, equals(bytebyte[0][i], result[0][i]));
        }
        r++;
        for (int i = 0; i < gapLength; ++i)
        {
            assertNull(result[r++]);
        }
        for (int b = 1; b < bytebyte.length; ++b)
        {
            for (int i = 0; i < bytebyte[b].length; ++i)
            {
                assertTrue("["+r+"]["+i+"]", equals(bytebyte[b][i], result[r][i]));
            }
            ++r;
        }
    }

    @Test public void testArraycloneShrinkGapBYTE()
    {
        setupByteArrays();
        byte result[] = null;
        assertNull(CollectionHelper.arraycloneShrinkGap(result, 2, 2));

        result = CollectionHelper.arraycloneShrinkGap(byte7sorted, 2, 3);
        assertNotNull(result);
        assertEquals(4, result.length);
        assertTrue(equals(result[0], byte7sorted[0]));
        assertTrue(equals(result[1], byte7sorted[1]));
        assertTrue(equals(result[2], byte7sorted[5]));
        assertTrue(equals(result[3], byte7sorted[6]));
    }

    @Test public void testArraycloneShrinkGapBYTEBYTE()
    {
        setupByteArrays();
        byte result[][] = null;
        assertNull(CollectionHelper.arraycloneShrinkGap(result, 2, 2));

        result = CollectionHelper.arraycloneShrinkGap(bytebyte, 0, 2);
        assertNotNull(result);
        assertEquals(1, result.length);
        assertEquals(bytebyte[2].length, result[0].length);
        for (int i = 0; i < bytebyte[2].length; ++i)
        {
            assertTrue("i="+i, equals(bytebyte[2][i], result[0][i]));
        }
    }

    private Character character6sorted[];
    private Character character7sorted[];
    private Character character7unsorted[];
    private Character charactercharacter[][];

    private void setupCharacterArrays()
    {
        // Look for search errors: even or odd array size, sorted or unsorted
        character6sorted = new Character[6];
        character7sorted = new Character[7];
        character7unsorted = new Character[7];
        for (int i = 0; i < character7sorted.length; ++i)
        {
            if (i < character6sorted.length)
            {
                character6sorted[i] = (char) sorted6keys[i];
            }
            character7sorted[i] = (char) sorted7keys[i];
            character7unsorted[i] = (char) unsorted7keys[i];
        }

        // Two dimensional array, total of 7 elements so we can use sorted7keys
        charactercharacter = new Character[3][];
        charactercharacter[0] = new Character[2];
        charactercharacter[1] = new Character[3];
        charactercharacter[2] = new Character[2];
        int index = 0;
        for (int one = 0; one < charactercharacter.length; ++one)
        {
            for (int two = 0; two < charactercharacter[one].length; ++two)
            {
                charactercharacter[one][two] = (char) sorted7keys[index++];
            }
        }
    }

    private boolean equals(Character a, Character b)
    {
        return a.equals(b);
    }

    @Test public void testArraycloneCHARACTER()
    {
        setupCharacterArrays();
        Character result[] = CollectionHelper.arrayclone(character6sorted[0]);
        assertEquals(1, result.length);
        assertTrue(equals(result[0], character6sorted[0]));

        result = CollectionHelper.arrayclone((Character[]) null);
        assertNull(result);

        result = CollectionHelper.arrayclone(character7unsorted);
        assertEquals(character7unsorted.length, result.length);
        for (int i = 0; i < result.length; ++i)
        {
            assertTrue("i=" + i, equals(result[i], character7unsorted[i]));
        }

        int toSize = character7sorted.length+1;
        result = CollectionHelper.arrayclone(character7sorted, toSize);
        assertEquals(toSize, result.length);
        for (int i = 0; i < character7sorted.length; ++i)
        {
            assertTrue("i=" + i, equals(result[i], character7sorted[i]));
        }
        assertNull(result[result.length-1]);

        toSize = 6;
        int fromSize = 5;
        int fromOffset = 1;
        result = CollectionHelper.arrayclone(character7unsorted, fromOffset,
                    fromSize, toSize);
        assertEquals(toSize, result.length);
        for (int i = 0; i < fromSize; ++i)
        {
            assertTrue("i=" + i, equals(character7unsorted[i+fromOffset], result[i]));
        }
    }

    @Test public void testArraycloneCombineCHARACTER()
    {
        setupCharacterArrays();
        // Method arguments are
        // from - source array
        // startOffset - first copy from[startOffset..from.length-1]
        // endOffset - next copy from[0..endOffset]
        // toSize - size of result, must be
        //     at least from.length-startOffset + endOffset+1
        Character result[];
        result = CollectionHelper.arraycloneCombine(character7sorted, 5, 1, 4);
        assertEquals(4, result.length);
        assertTrue(equals(character7sorted[5], result[0]));
        assertTrue(equals(character7sorted[6], result[1]));
        assertTrue(equals(character7sorted[0], result[2]));
        assertNull(result[3]);
    }

    @Test public void testArraycloneExpandGapCHARACTER()
    {
        setupCharacterArrays();

        // As implemented, arraycloneExpandGap makes sense when fromOffset == 0
        int gapLength = 3;
        int toSize = character7unsorted.length+gapLength;
        Character result[] = CollectionHelper.arraycloneExpandGap(
                character7unsorted, 0, character7unsorted.length,
                toSize, 2, gapLength);

        assertEquals(toSize, result.length);
        assertTrue(equals(character7unsorted[0], result[0]));
        assertTrue(equals(character7unsorted[1], result[1]));
        assertNull(result[2]);
        assertNull(result[3]);
        assertNull(result[4]);
        assertTrue(equals(character7unsorted[2], result[5]));
        assertTrue(equals(character7unsorted[3], result[6]));
        assertTrue(equals(character7unsorted[4], result[7]));
        assertTrue(equals(character7unsorted[5], result[8]));
        assertTrue(equals(character7unsorted[6], result[9]));
    }

    @Test public void testArraycloneCHARACTERCHARACTER()
    {
        setupCharacterArrays();
        Character nullMatrix[][] = null;
        Character result[][];

        result = CollectionHelper.arrayclone(nullMatrix);
        assertNull(result);

        result = CollectionHelper.arrayclone(charactercharacter);
        assertNotNull(result);
        assertEquals(charactercharacter.length, result.length);
        for (int one = 0; one < charactercharacter.length; ++one)
        {
            assertEquals("["+one+"]", charactercharacter[one].length, result[one].length);
            for (int two = 0; two < charactercharacter[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(charactercharacter[one][two], result[one][two]));
            }
        }

        result = CollectionHelper.arrayclone(nullMatrix, 1);
        assertNull(result);

        result = CollectionHelper.arrayclone(charactercharacter, charactercharacter.length+1);
        assertNotNull(result);
        assertEquals(charactercharacter.length+1, result.length);
        assertNull(result[result.length-1]);
        for (int one = 0; one < charactercharacter.length; ++one)
        {
            assertEquals("["+one+"]", charactercharacter[one].length, result[one].length);
            for (int two = 0; two < charactercharacter[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(charactercharacter[one][two], result[one][two]));
            }
        }

        result = CollectionHelper.arrayclone(nullMatrix, 1, 1, 1);
        assertNull(result);

        result = CollectionHelper.arrayclone(charactercharacter, 2, 1, 1);
        assertNotNull(result);
        assertEquals(1, result.length);
        for (int two = 0; two < charactercharacter[2].length; ++two)
        {
            assertTrue("[][" + two + "]", equals(charactercharacter[2][two], result[0][two]));
        }
    }

    @Test public void testArraycloneDimensionsCHARACTERCHARACTER()
    {
        setupCharacterArrays();
        Character result[][] = null;

        assertNull(CollectionHelper.arraycloneDimensions(result));

        result = CollectionHelper.arraycloneDimensions(charactercharacter);
        assertNotNull(result);
        assertEquals(charactercharacter.length, result.length);
        for (int one = 0; one < charactercharacter.length; ++one)
        {
            assertEquals("["+one+"]", charactercharacter[one].length, result[one].length);
            for (int two = 0; two < charactercharacter[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(charactercharacter[one][two], result[one][two]));
            }
        }
    }

    @Test public void testArraycloneCombineCHARACTERCHARACTER()
    {
        setupCharacterArrays();
        Character nullMatrix[][] = null;
        assertNull(CollectionHelper.arraycloneCombine(nullMatrix, 3, 3, 3));

        Character result[][];
        // Copy charactercharacter[1..2] to result[0..1], charactercharacter[0..1] to result[2..3]
        result = CollectionHelper.arraycloneCombine(charactercharacter, 1, 2, 4);
        assertNotNull(result);
        assertEquals(4, result.length);
        for (int i = 0; i < charactercharacter[1].length; ++i)
        {
            assertTrue("i="+i, equals(charactercharacter[1][i], result[0][i]));
            assertTrue("i="+i, equals(charactercharacter[1][i], result[3][i]));
        }
        for (int i = 0; i < charactercharacter[2].length; ++i)
        {
            assertTrue("i="+i, equals(charactercharacter[2][i], result[1][i]));
        }
        for (int i = 0; i < charactercharacter[0].length; ++i)
        {
            assertTrue("i="+i, equals(charactercharacter[0][i], result[2][i]));
        }
    }

    @Test public void testArraycloneExpandGapCHARACTERCHARACTER()
    {
        setupCharacterArrays();
        Character nullMatrix[][] = null;
        assertNull(CollectionHelper.arraycloneExpandGap(nullMatrix,5,5,5,5,5));

        // As implemented, arraycloneExpandGap makes sense when fromOffset == 0
        int gapLength = 2;
        int toSize = charactercharacter.length + gapLength;
        Character result[][] = CollectionHelper.arraycloneExpandGap(
                charactercharacter, 0, charactercharacter.length, toSize, 1, gapLength);
        assertNotNull(result);
        assertEquals(toSize, result.length);

        int r = 0;  // index into result[]
        for (int i = 0; i < charactercharacter[0].length; ++i)
        {
            assertTrue("i=" + i, equals(charactercharacter[0][i], result[0][i]));
        }
        r++;
        for (int i = 0; i < gapLength; ++i)
        {
            assertNull(result[r++]);
        }
        for (int b = 1; b < charactercharacter.length; ++b)
        {
            for (int i = 0; i < charactercharacter[b].length; ++i)
            {
                assertTrue("["+r+"]["+i+"]", equals(charactercharacter[b][i], result[r][i]));
            }
            ++r;
        }
    }

    @Test public void testArraycloneShrinkGapCHARACTER()
    {
        setupCharacterArrays();
        Character result[] = null;
        assertNull(CollectionHelper.arraycloneShrinkGap(result, 2, 2));

        result = CollectionHelper.arraycloneShrinkGap(character7sorted, 2, 3);
        assertNotNull(result);
        assertEquals(4, result.length);
        assertTrue(equals(result[0], character7sorted[0]));
        assertTrue(equals(result[1], character7sorted[1]));
        assertTrue(equals(result[2], character7sorted[5]));
        assertTrue(equals(result[3], character7sorted[6]));
    }

    @Test public void testArraycloneShrinkGapCHARACTERCHARACTER()
    {
        setupCharacterArrays();
        Character result[][] = null;
        assertNull(CollectionHelper.arraycloneShrinkGap(result, 2, 2));

        result = CollectionHelper.arraycloneShrinkGap(charactercharacter, 0, 2);
        assertNotNull(result);
        assertEquals(1, result.length);
        assertEquals(charactercharacter[2].length, result[0].length);
        for (int i = 0; i < charactercharacter[2].length; ++i)
        {
            assertTrue("i="+i, equals(charactercharacter[2][i], result[0][i]));
        }
    }

    private char char6sorted[];
    private char char7sorted[];
    private char char7unsorted[];
    private char charchar[][];

    private void setupCharArrays()
    {
        // Look for search errors: even or odd array size, sorted or unsorted
        char6sorted = new char[6];
        char7sorted = new char[7];
        char7unsorted = new char[7];
        for (int i = 0; i < char7sorted.length; ++i)
        {
            if (i < char6sorted.length)
            {
                char6sorted[i] = (char) sorted6keys[i];
            }
            char7sorted[i] = (char) sorted7keys[i];
            char7unsorted[i] = (char) unsorted7keys[i];
        }

        // Two dimensional array, total of 7 elements so we can use sorted7keys
        charchar = new char[3][];
        charchar[0] = new char[2];
        charchar[1] = new char[3];
        charchar[2] = new char[2];
        int index = 0;
        for (int one = 0; one < charchar.length; ++one)
        {
            for (int two = 0; two < charchar[one].length; ++two)
            {
                charchar[one][two] = (char) sorted7keys[index++];
            }
        }
    }

    private boolean equals(char a, char b)
    {
        return a == b;
    }

    @Test public void testArraycloneCHAR()
    {
        setupCharArrays();
        char result[] = CollectionHelper.arrayclone(char6sorted[0]);
        assertEquals(1, result.length);
        assertTrue(equals(result[0], char6sorted[0]));

        result = CollectionHelper.arrayclone((char[]) null);
        assertNull(result);

        result = CollectionHelper.arrayclone(char7unsorted);
        assertEquals(char7unsorted.length, result.length);
        for (int i = 0; i < result.length; ++i)
        {
            assertTrue("i=" + i, equals(result[i], char7unsorted[i]));
        }

        int toSize = char7sorted.length+1;
        result = CollectionHelper.arrayclone(char7sorted, toSize);
        assertEquals(toSize, result.length);
        for (int i = 0; i < char7sorted.length; ++i)
        {
            assertTrue("i=" + i, equals(result[i], char7sorted[i]));
        }
        assertEquals(0, result[result.length-1]);

        toSize = 6;
        int fromSize = 5;
        int fromOffset = 1;
        result = CollectionHelper.arrayclone(char7unsorted, fromOffset,
                    fromSize, toSize);
        assertEquals(toSize, result.length);
        for (int i = 0; i < fromSize; ++i)
        {
            assertTrue("i=" + i, equals(char7unsorted[i+fromOffset], result[i]));
        }
    }

    @Test public void testArraycloneCombineCHAR()
    {
        setupCharArrays();
        // Method arguments are
        // from - source array
        // startOffset - first copy from[startOffset..from.length-1]
        // endOffset - next copy from[0..endOffset]
        // toSize - size of result, must be
        //     at least from.length-startOffset + endOffset+1
        char result[];
        result = CollectionHelper.arraycloneCombine(char7sorted, 5, 1, 4);
        assertEquals(4, result.length);
        assertTrue(equals(char7sorted[5], result[0]));
        assertTrue(equals(char7sorted[6], result[1]));
        assertTrue(equals(char7sorted[0], result[2]));
        assertEquals(0, result[3]);
    }

    @Test public void testArraycloneExpandGapCHAR()
    {
        setupCharArrays();

        // As implemented, arraycloneExpandGap makes sense when fromOffset == 0
        int gapLength = 3;
        int toSize = char7unsorted.length+gapLength;
        char result[] = CollectionHelper.arraycloneExpandGap(
                char7unsorted, 0, char7unsorted.length,
                toSize, 2, gapLength);

        assertEquals(toSize, result.length);
        assertTrue(equals(char7unsorted[0], result[0]));
        assertTrue(equals(char7unsorted[1], result[1]));
        assertEquals(0, result[2]);
        assertEquals(0, result[3]);
        assertEquals(0, result[4]);
        assertTrue(equals(char7unsorted[2], result[5]));
        assertTrue(equals(char7unsorted[3], result[6]));
        assertTrue(equals(char7unsorted[4], result[7]));
        assertTrue(equals(char7unsorted[5], result[8]));
        assertTrue(equals(char7unsorted[6], result[9]));
    }

    @Test public void testArraycloneCHARCHAR()
    {
        setupCharArrays();
        char nullMatrix[][] = null;
        char result[][];

        result = CollectionHelper.arrayclone(nullMatrix);
        assertNull(result);

        result = CollectionHelper.arrayclone(charchar);
        assertNotNull(result);
        assertEquals(charchar.length, result.length);
        for (int one = 0; one < charchar.length; ++one)
        {
            assertEquals("["+one+"]", charchar[one].length, result[one].length);
            for (int two = 0; two < charchar[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(charchar[one][two], result[one][two]));
            }
        }

        result = CollectionHelper.arrayclone(nullMatrix, 1);
        assertNull(result);

        result = CollectionHelper.arrayclone(charchar, charchar.length+1);
        assertNotNull(result);
        assertEquals(charchar.length+1, result.length);
        assertNull(result[result.length-1]);
        for (int one = 0; one < charchar.length; ++one)
        {
            assertEquals("["+one+"]", charchar[one].length, result[one].length);
            for (int two = 0; two < charchar[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(charchar[one][two], result[one][two]));
            }
        }

        result = CollectionHelper.arrayclone(nullMatrix, 1, 1, 1);
        assertNull(result);

        result = CollectionHelper.arrayclone(charchar, 2, 1, 1);
        assertNotNull(result);
        assertEquals(1, result.length);
        for (int two = 0; two < charchar[2].length; ++two)
        {
            assertTrue("[][" + two + "]", equals(charchar[2][two], result[0][two]));
        }
    }

    @Test public void testArraycloneDimensionsCHARCHAR()
    {
        setupCharArrays();
        char result[][] = null;

        assertNull(CollectionHelper.arraycloneDimensions(result));

        result = CollectionHelper.arraycloneDimensions(charchar);
        assertNotNull(result);
        assertEquals(charchar.length, result.length);
        for (int one = 0; one < charchar.length; ++one)
        {
            assertEquals("["+one+"]", charchar[one].length, result[one].length);
            for (int two = 0; two < charchar[one].length; ++two)
            {
                assertTrue("["+one+"]["+two+"]",
                        equals(charchar[one][two], result[one][two]));
            }
        }
    }

    @Test public void testArraycloneCombineCHARCHAR()
    {
        setupCharArrays();
        char nullMatrix[][] = null;
        assertNull(CollectionHelper.arraycloneCombine(nullMatrix, 3, 3, 3));

        char result[][];
        // Copy charchar[1..2] to result[0..1], charchar[0..1] to result[2..3]
        result = CollectionHelper.arraycloneCombine(charchar, 1, 2, 4);
        assertNotNull(result);
        assertEquals(4, result.length);
        for (int i = 0; i < charchar[1].length; ++i)
        {
            assertTrue("i="+i, equals(charchar[1][i], result[0][i]));
            assertTrue("i="+i, equals(charchar[1][i], result[3][i]));
        }
        for (int i = 0; i < charchar[2].length; ++i)
        {
            assertTrue("i="+i, equals(charchar[2][i], result[1][i]));
        }
        for (int i = 0; i < charchar[0].length; ++i)
        {
            assertTrue("i="+i, equals(charchar[0][i], result[2][i]));
        }
    }

    @Test public void testArraycloneExpandGapCHARCHAR()
    {
        setupCharArrays();
        char nullMatrix[][] = null;
        assertNull(CollectionHelper.arraycloneExpandGap(nullMatrix,5,5,5,5,5));

        // As implemented, arraycloneExpandGap makes sense when fromOffset == 0
        int gapLength = 2;
        int toSize = charchar.length + gapLength;
        char result[][] = CollectionHelper.arraycloneExpandGap(
                charchar, 0, charchar.length, toSize, 1, gapLength);
        assertNotNull(result);
        assertEquals(toSize, result.length);

        int r = 0;  // index into result[]
        for (int i = 0; i < charchar[0].length; ++i)
        {
            assertTrue("i=" + i, equals(charchar[0][i], result[0][i]));
        }
        r++;
        for (int i = 0; i < gapLength; ++i)
        {
            assertNull(result[r++]);
        }
        for (int b = 1; b < charchar.length; ++b)
        {
            for (int i = 0; i < charchar[b].length; ++i)
            {
                assertTrue("["+r+"]["+i+"]", equals(charchar[b][i], result[r][i]));
            }
            ++r;
        }
    }

    @Test public void testArraycloneShrinkGapCHAR()
    {
        setupCharArrays();
        char result[] = null;
        assertNull(CollectionHelper.arraycloneShrinkGap(result, 2, 2));

        result = CollectionHelper.arraycloneShrinkGap(char7sorted, 2, 3);
        assertNotNull(result);
        assertEquals(4, result.length);
        assertTrue(equals(result[0], char7sorted[0]));
        assertTrue(equals(result[1], char7sorted[1]));
        assertTrue(equals(result[2], char7sorted[5]));
        assertTrue(equals(result[3], char7sorted[6]));
    }

    @Test public void testArraycloneShrinkGapCHARCHAR()
    {
        setupCharArrays();
        char result[][] = null;
        assertNull(CollectionHelper.arraycloneShrinkGap(result, 2, 2));

        result = CollectionHelper.arraycloneShrinkGap(charchar, 0, 2);
        assertNotNull(result);
        assertEquals(1, result.length);
        assertEquals(charchar[2].length, result[0].length);
        for (int i = 0; i < charchar[2].length; ++i)
        {
            assertTrue("i="+i, equals(charchar[2][i], result[0][i]));
        }
    }
}
