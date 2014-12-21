package com.cboe.externalIntegrationServices.msgCodec.useCase.tests.blocking;

import junit.framework.TestCase;

import com.cboe.externalIntegrationServices.msgCodec.DataBufferBlock;
import com.cboe.externalIntegrationServices.msgCodec.useCase.CodecConstants;

/**
 * These use cases show how to read a databufferblock.
 * 
 * @author degreefc
 * 
 */
public class DataBufferBlockTests extends TestCase implements CodecConstants {

    public void test_writeBuffersReadBuffers() throws Exception {
        DataBufferBlock blockOut = new DataBufferBlock(4096);

        MarketDataBuffer mdbuf = null;

        mdbuf = createBuffer();
        blockOut.write(mdbuf);
        mdbuf = createBuffer();
        blockOut.write(mdbuf);

        /*
         * Simulate the transmission to a client
         */
        DataBufferBlock blockIn = new DataBufferBlock(4096);
        blockIn.setStorage(blockOut.getStorage(), blockOut.getLength());

        // System.out.println(blockIn);

        MarketDataBuffer mdBufIn = new MarketDataBuffer();
        int countB = 0;
        while (blockIn.read(mdBufIn)) {
            countB++;
            // System.out.println(mdBufIn);

            BookDepthEntryCodec bdc = new BookDepthEntryCodec("received Codec");
            int count = 0;
            while (mdBufIn.read(bdc)) {
                count++;
                // System.out.println(bdc);

                assertEquals("groupKey", mdbuf.getGroupKey(), mdBufIn
                        .getGroupKey());
                assertEquals("subIdentifier", mdbuf.getSubIdentifier(), mdBufIn
                        .getSubIdentifier());

                assertEquals("side", (byte) 66, bdc.getSide());
                assertEquals("entryType", (byte) 2, bdc.getEntryType());
                assertEquals("priceLevel", 2000000000L, bdc.getPriceLevel());
                assertEquals("priceLevelVol", 220, bdc.getPriceLevelVol());
            }
            assertEquals("expected number of codecs (values)", 2, count);
        }
        assertEquals("expected number of buffers (values)", 2, countB);
    }

    public void test_SequenceNumberAndTimestamp()  throws Exception {
        DataBufferBlock blockOut = new DataBufferBlock(4096);

        MarketDataBuffer mdbuf = null;
        mdbuf = createBuffer();
        blockOut.write(mdbuf);

        DataBufferBlock blockIn = new DataBufferBlock(4096);
        blockIn.setStorage(blockOut.getStorage(1, 123), blockOut.getLength());

        assertEquals("seq", 1, blockIn.getSeqNumber());
        assertEquals("timestamp", 123, blockIn.getTimestamp());
    }

    public void test_SpecificProblemFromCboeDirect() {
        byte[] bytes = new byte[] { 0x00, 0x00, 0x00, 0x4a, 0x00, 0x00, 0x01,
                0x1d, (byte) 0x91, 0x6b, 0x7d, (byte) 0xff, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x36, 0x00, 0x01, 0x00, 0x10, 0x00, (byte) 0x0e,
                0x00, 0x03, 0x21, (byte) 0x84, (byte) 0xce, (byte) 0x5a, 0x04,
                0x20, 0x00, (byte) 0x0c, 0x07, 0x00, 0x00, 0x13, 0x00, 0x14,
                0x02, 0x42, 0x01, 0x00, 0x00, 0x00, 0x00, (byte) 0x3b,
                (byte) 0x9a, (byte) 0xca, 0x00, 0x00, 0x00, 0x00, 0x64, 0x00,
                0x13, 0x00, 0x14, 0x02, 0x53, 0x01, 0x00, 0x00, 0x00, 0x00,
                0x47, (byte) 0x86, (byte) 0x8c, 0x00, 0x00, 0x00, 0x00, 0x64 };
        DataBufferBlock block = new DataBufferBlock(4096);
        block.setStorage(bytes, bytes.length);
        short bufId;
        while ((bufId = block.nextBufferId()) >= 0) {
            assertEquals("bufferId", 1, bufId);
            block.skip();
        }
    }

    public void test_EmptyBlock() throws Exception {
        DataBufferBlock emptyBlock = new DataBufferBlock(4096);
        DataBufferBlock fullBlock = new DataBufferBlock(4096);
        DataBufferBlock blockIn = new DataBufferBlock(4096);
        blockIn.setStorage(emptyBlock.getStorage(), emptyBlock.getLength());
        while (blockIn.nextBufferId() != -1) {
            blockIn.skip();
        }
        /*
         * Then write a full block and test it
         */
        MarketDataBuffer mdbuf = null;

        mdbuf = createBuffer();
        fullBlock.write(mdbuf);
        mdbuf = createBuffer();
        fullBlock.write(mdbuf);

        blockIn.setStorage(fullBlock.getStorage(), fullBlock.getLength());

        MarketDataBuffer mdBufIn = new MarketDataBuffer();
        int countB = 0;
        while (blockIn.read(mdBufIn)) {
            countB++;
            // System.out.println(mdBufIn);

            BookDepthEntryCodec bdc = new BookDepthEntryCodec("received Codec");
            int count = 0;
            while (mdBufIn.read(bdc)) {
                count++;
                // System.out.println(bdc);

                assertEquals("groupKey", mdbuf.getGroupKey(), mdBufIn
                        .getGroupKey());
                assertEquals("subIdentifier", mdbuf.getSubIdentifier(), mdBufIn
                        .getSubIdentifier());

                assertEquals("side", (byte) 66, bdc.getSide());
                assertEquals("entryType", (byte) 2, bdc.getEntryType());
                assertEquals("priceLevel", 2000000000L, bdc.getPriceLevel());
                assertEquals("priceLevelVol", 220, bdc.getPriceLevelVol());
            }
            assertEquals("expected number of codecs (values)", 2, count);
        }
        assertEquals("expected number of buffers (values)", 2, countB);
    }

    public void test_largeBuffers() throws Exception {
        /*
         * Create BookDepthCodec, ask for it's typical maximum size, then create
         * N buffs where 2 of them are > 32K size, and 2 are < 32K size, place
         * into block, and re-read them back to test mixed buffer sizes in same
         * block (buff lengths in block use up 2 or 4 bytes depending on
         * buffSize).
         */
        BookDepthEntryCodec bdc = new BookDepthEntryCodec("BookDepthCodec");
        MarketDataBuffer buffOut = new MarketDataBuffer();
        int bdcSize = bdc.typicalMaximumSize();

        int MORE_THAN_32K_SIZE = 48000;
        int LESS_THAN_32K_SIZE = 4000;
        int[] BUFF_SIZES = { MORE_THAN_32K_SIZE, LESS_THAN_32K_SIZE,
                MORE_THAN_32K_SIZE, LESS_THAN_32K_SIZE };
        int blockSize = MORE_THAN_32K_SIZE * BUFF_SIZES.length;
        DataBufferBlock blockOut = new DataBufferBlock(blockSize);

        long price;
        int groupKey, subId, volume, totalSize, numItems;
        int sumBuffSizes = 0;
        /*
         * Reset block, write N buffers with alternating sizes. Re-use same
         * buffer and BookDepthCodec, putting in different values.
         */
        blockOut.reset();
        for (int i = 0; i < BUFF_SIZES.length; i++) {
            totalSize = 0;
            buffOut.reset();
            groupKey = i;
            subId = groupKey + 1;
            buffOut.setGroupKey(groupKey);
            buffOut.setSubIdentifier(subId);

            // Should cause us to create buffer whose totalSize > BUFF_SIZES[i]
            numItems = BUFF_SIZES[i] / bdcSize + 1;
            for (int item = 0; item < numItems; item++) {
                volume = item;
                price = item * 10;
                bdc.reset();
                setBookDepth(bdc, (byte) 66, (byte) 2, price, volume);
                totalSize += bdcSize;
                buffOut.write(bdc);
            }
            blockOut.write(buffOut);
            assertTrue("Expected totalSize(" + totalSize + ") > "
                    + BUFF_SIZES[i], totalSize > BUFF_SIZES[i]);
            sumBuffSizes += BUFF_SIZES[i];
        }
        assertTrue("Expected Block Length(" + blockOut.getLength() + ") > "
                + sumBuffSizes, blockOut.getLength() > sumBuffSizes);

        DataBufferBlock blockIn = new DataBufferBlock(4096);
        MarketDataBuffer buffIn = new MarketDataBuffer();
        blockIn.setStorage(blockOut.getStorage(), blockOut.getLength());

        assertTrue("Expected Block Length(" + blockIn.getLength() + ") > "
                + sumBuffSizes, blockIn.getLength() > sumBuffSizes);

        int buffCount = 0;
        while (blockIn.read(buffIn)) {
            int expectedNumCodecs = BUFF_SIZES[buffCount] / bdcSize + 1;
            int codecCount = 0;
            groupKey = buffCount;
            subId = groupKey + 1;
            while (buffIn.read(bdc)) {
                volume = codecCount;
                price = codecCount * 10;

                assertEquals("groupKey " + codecCount, groupKey, buffIn
                        .getGroupKey());
                assertEquals("subIdentifier " + codecCount, subId, buffIn
                        .getSubIdentifier());

                assertEquals("side " + codecCount, (byte) 66, bdc.getSide());
                assertEquals("entryType " + codecCount, (byte) 2, bdc
                        .getEntryType());
                assertEquals("price " + codecCount, price, bdc.getPriceLevel());
                assertEquals("volume " + codecCount, volume, bdc
                        .getPriceLevelVol());
                codecCount++;
            }
            buffCount++;
            assertEquals("Expected number of codecs", expectedNumCodecs,
                    codecCount);
        }
        assertEquals("Expected number of buffers", BUFF_SIZES.length, buffCount);

    }

    private void setBookDepth(BookDepthEntryCodec bdc, byte side,
            byte entryType, long price, int volume) {
        bdc.setSide(side);
        bdc.setEntryType(entryType);
        bdc.setPriceLevel(price);
        bdc.setPriceLevelVol(volume);
    }

    private MarketDataBuffer createBuffer() throws Exception {
        MarketDataBuffer mb = new MarketDataBuffer();
        /*
         * populate the header
         */
        mb.setGroupKey(339899746);
        mb.setSubIdentifier((short) 0);

        BookDepthEntryCodec bdc = null;

        bdc = new BookDepthEntryCodec("BookDepthEntryCodec");
        setBookDepth(bdc, (byte) 66, (byte) 2, 2000000000L, 220);

        mb.write(bdc);
        mb.write(bdc);

        mb.rewind();

        return mb;
    }
}
