/*
 * Created on Oct 14, 2008
 */
package com.cboe.externalIntegrationServices.msgCodec;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class SequenceFieldCodecTest extends CodecTester
{
    private BestOfTheRestStruct         botrCodec;
    private SequenceFieldCodec          currentSeqCodec;
    
    public static void main(String[] args) {

        boolean doPerfTest = true;  // Make true to run perf test
        if (doPerfTest) {
            SequenceFieldCodecTest self = new SequenceFieldCodecTest();
            self.perfTest(1000000,5);
            return;
        }
        
        junit.textui.TestRunner.run(SequenceFieldCodecTest.class);
    }

    public static Test suite() {
        return new TestSuite(SequenceFieldCodecTest.class);
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        botrCodec   = new BestOfTheRestStruct();
    }

    public void testEncodeDecodeSequenceOfByte() {
        currentSeqCodec = new SequenceFieldCodec("SequenceOfByte",1,new ByteFieldCodec("ByteField"));
        String digits = "1234567890";
        setStorageSize(32771);          // Enough for 32767 + 2 length bytes + extra byte at end.
        
        
        CodecTester.ExpectedResultBuilder expect = new CodecTester.ExpectedResultBuilder(32768);

        // Zero-length sequence - has only length
        expect.clear().append(0x00);
        seqOfByteTest("",expect.toIntArray());

        // Length 3 - length == 1 byte
        expect.clear().append(0x03).append("ABC");
        seqOfByteTest(expect.rightStr(1),expect.toIntArray());

        // Length 127 - length == 1 byte (before boundary condition)
        expect.clear().append(0x7F).appendRepeat(digits, 127);
        seqOfByteTest(expect.rightStr(1),expect.toIntArray());
        
        // Length 128 - length == 2 byte (boundary condition)
        expect.clear().append(0x80,0x80).appendRepeat(digits, 128);
        seqOfByteTest(expect.rightStr(2),expect.toIntArray());

        // Remaining tests have 2 length bytes 
        expect.clear().append(0x81,0x00).appendRepeat(digits, 256);
        seqOfByteTest(expect.rightStr(2),expect.toIntArray());

        // Remaining tests have 2 length bytes 
        expect.clear().append(0xFF,0xFF).appendRepeat(digits, 32767);
        seqOfByteTest(expect.rightStr(2),expect.toIntArray());

        // Sequences > 32767 not allowed. Should throw exception 
        try {
            expect.clear().append(0xFF,0xFF).appendRepeat(digits,32768);
            seqOfByteTest(expect.rightStr(2),expect.toIntArray());
            fail("Expected exception not thrown attempting to assign value > length 32767");
        } catch (ArrayIndexOutOfBoundsException e) {
            // Success
        } catch (Throwable t) {
            fail("Unexpected exception thrown attempting to assign value > length 32767");
        }
    }

    private void seqOfByteTest(String value,int... expectedBytes) {
        ByteFieldCodec byteField; 

        currentSeqCodec.reset();
        for(int i = 0; i < value.length(); i++) {
            byteField = (ByteFieldCodec)currentSeqCodec.getElement(i);
            byteField.setValue((byte)value.charAt(i));
        }
        encodeDecodeTest(currentSeqCodec, 0, expectedBytes);

        assertEquals("numElements",value.length(),currentSeqCodec.getNumElements());
        for(int i = 0; i < currentSeqCodec.getNumElements(); i++) {
            byteField = (ByteFieldCodec)currentSeqCodec.getElement(i);
            assertEquals("SeqOfByte["+i+"]",value.charAt(i),(char)byteField.getValue());
        }
    }

    public void testEncodeDecodeSequenceOfByteArray() {
        currentSeqCodec = new SequenceFieldCodec("SequenceOfByte",1,new ByteArrayFieldCodec("ByteArrayField",32));
        CodecTester.ExpectedResultBuilder expect = new CodecTester.ExpectedResultBuilder(65000);
        setStorageSize(65001);
        
        String[] values = { "replacedWithLongString", "", null, "W_MAIN", "I-DJI", "ABCDEFGHIJK" };
        int i;

        // Use ExpectedResultBuilder to build a String close to 32K to force
        // overall storage to be > 32K, but individual items each < 32K.
        
        expect.clear().appendRepeat("1234567890", 32767);
        values[0] = expect.toString();
        
        expect.clear().append(values.length);   // Sequence numElems < 127 == 1 byte
        i = 0;

        // Element 0 String > 127 length requires 2 bytes to encode length.
        expect.append(0xFF,0xFF).append(values[i++]);
        // Element 1 empty String has 0x00 length
        expect.append(0x00).append(values[i++]);
        // Element 2 null String has special "-0" null-value length.
        expect.append(0x80,0x00);
        i++;
        // Element 3+ are normal 1 byte length.
        expect.append(values[i].length()).append(values[i++]);
        expect.append(values[i].length()).append(values[i++]);
        expect.append(values[i].length()).append(values[i++]);
        
        seqOfByteArrayTest(values,expect.toIntArray());

    }        

    
    private void seqOfByteArrayTest(String[] bytesAsStrings,int... expectedBytes) {
        ByteArrayFieldCodec byteArrayField; 
        
        currentSeqCodec.reset();
        for(int i = 0; i < bytesAsStrings.length; i++) {
            byteArrayField = (ByteArrayFieldCodec)currentSeqCodec.getElement(i);
            byteArrayField.setValue(bytesAsStrings[i]);
        }
        
        encodeDecodeTest(currentSeqCodec, 0, expectedBytes);

        assertEquals("numElements",bytesAsStrings.length,currentSeqCodec.getNumElements());
        for(int i = 0; i < currentSeqCodec.getNumElements(); i++) {
            byteArrayField = (ByteArrayFieldCodec)currentSeqCodec.getElement(i);
            assertEquals("SeqOfByteArray["+i+"]",bytesAsStrings[i],byteArrayField.getStringValue());
        }
        
    }
    
    /**
     * Tests an application-like multi-level structure where each struct
     * has a Sequence of other structs which in turn have Sequences.
     */
    public void testComplexCodecWithNestedSequencesStructure() {
        BestOfTheRestStruct         botr    = new BestOfTheRestStruct();
        BestOfTheRestSideStruct     side;
        ExchangeVolumeStruct        vol;
        int                         productKey, price, numVols;              
        // SIDE_TYPES.length sides.
        // Side[0] has 1 vol-exch, 
        // Side[1] has 2 vol-exch, 2-3, 3-4, etc. 
        productKey = 1000;
        price      = 10;
        botr.reset();
        botr.setSessionName("W_MAIN");
        for(int sideIndex = 0; sideIndex < SIDE_TYPES.length; sideIndex++) {
            productKey++;
            price++;
            numVols = sideIndex + 1;
            side = botr.getSide(sideIndex);
            side.setValues(SIDE_TYPES[sideIndex], productKey, price);
            for(int volIndex = 0; volIndex < numVols; volIndex++) {
                vol = side.getExchVolume(volIndex);
                vol.setValues(volIndex,EXCHANGES[volIndex]);
            }
        }
        
        byte[] storage = new byte[1024];    
        int encodeLen = botr.encode(storage, 0);
        
        productKey = 1000;
        price      = 10;
        botr.reset();
        
        int decodeLen = botr.decode(storage,0);
        
        assertEquals("encode/decodeLen",encodeLen,decodeLen);
        assertEquals("sessionName","W_MAIN",botr.getSessionName());
        assertEquals("numSides",SIDE_TYPES.length,botr.getNumSides());

        for(int sideIndex = 0; sideIndex < botr.getNumSides(); sideIndex++) {
            productKey++;
            price++;
            numVols = sideIndex + 1;
            side = botr.getSide(sideIndex);
            assertEquals("productKey["+sideIndex+"]",productKey,side.getProductKey());
            assertEquals("price["+sideIndex+"]",price,side.getPrice());
            assertEquals("numVols["+sideIndex+"]",numVols,side.getNumExchVols());
            for(int volIndex = 0; volIndex < numVols; volIndex++) {
                vol = side.getExchVolume(volIndex);
                String str = "Side[" + sideIndex + "] ExchVol[" + volIndex + "] ";
                assertEquals(str + "volume",volIndex,vol.getVolume());
                assertEquals(str + "exchange",EXCHANGES[volIndex],vol.getExchange());
            }
        }
    }
    
    public void testNewCopy() {
        ByteFieldCodec   seqType = new ByteFieldCodec("ByteField");
        
        SequenceFieldCodec  seq1 = new SequenceFieldCodec("SeqOfBytes1",4,seqType);
        SequenceFieldCodec  seq2 = (SequenceFieldCodec)seq1.newCopy();
        int elemCount = 4;  // Arbitrary, 4 sounded like enough.

        assertNotSame(seq1,seq2);

        for(int i1 = 0; i1 < elemCount; i1++) {
            for(int i2 = 0; i2 < elemCount; i2++) {
                assertNotSame("Seq1["+i1+"] Seq2["+i2+"]",
                              seq1.getElement(i1),
                              seq2.getElement(i2));
            }
        }
    }

    public void testReset() {
        ByteFieldCodec   seqType    = new ByteFieldCodec("ByteField");
        SequenceFieldCodec  seq     = new SequenceFieldCodec("SeqOfBytes",4,seqType);
        int numItems = 4;
        
        for(int i = 0; i < numItems; i++) {
            seqType = (ByteFieldCodec)seq.getElement(i);
            seqType.setValue((byte)(i+10));
        }
        assertEquals("numElements",numItems,seq.getNumElements());
        for(int i = 0; i < numItems; i++) {
            seqType = (ByteFieldCodec)seq.getElement(i);
            assertEquals("SeqByte["+i+"]",i+10,seqType.getValue());
        }
        
        seq.reset();
        assertEquals("numElements",0,seq.getNumElements());
        for(int i = 0; i < numItems; i++) {
            seqType = (ByteFieldCodec)seq.getElement(i);
            assertEquals("SeqByte["+i+"] (reset)",0,seqType.getValue());
        }
    }

    private static final String[]   EXCHANGES   = { "AMEX",
                                                    "BOX",
                                                    "ISE",
                                                    "NYSE",
                                                  };
    private static final byte[]     SIDE_TYPES  = { BestOfTheRestSideStruct.BUY_SIDE,
                                                    BestOfTheRestSideStruct.SELL_SIDE,
                                                    BestOfTheRestSideStruct.BUY_SIDE,
                                                    BestOfTheRestSideStruct.SELL_SIDE
                                                  };
    private static final int        NUM_ITEMS_MASK  = 0x03;
    
    protected void perfTestSetValue(long timestamp,int iterationCount, byte[] storage) {
        botrCodec.reset();
        botrCodec.setSessionName("W_MAIN");
        BestOfTheRestSideStruct side;
        byte                    sideType;
        ExchangeVolumeStruct    vol;
        final int               numVols         = iterationCount & NUM_ITEMS_MASK; 
        final int               numSideItems    = iterationCount & NUM_ITEMS_MASK * 2; 
        
        for(int i = 0; i < numSideItems; i++) {
            side = botrCodec.getSide(i);
            sideType = SIDE_TYPES[i & NUM_ITEMS_MASK];
            side.setValues(sideType, iterationCount, iterationCount+10);
            
            for(int volIndex = 0; volIndex < numVols; volIndex++) {
                vol = side.getExchVolume(volIndex);
                vol.setValues(iterationCount, EXCHANGES[volIndex]);
            }
        }
//        System.out.println(botrCodec);
    }

    protected ICodec getCodec() {
        return botrCodec;
    }

    
    static class BestOfTheRestStruct extends MsgCodec {
        private final ByteArrayFieldCodec   sessionName;
        private final SequenceFieldCodec    sides;
        
        BestOfTheRestStruct() {
            super("BestOfTheRest",(short)3);
            add(sessionName = new ByteArrayFieldCodec("SessionName",32));
            add(sides = new SequenceFieldCodec("Sides",10,new BestOfTheRestSideStruct()));
        }
        
        String getSessionName() {
            return sessionName.getStringValue();
        }
        
        void setSessionName(String p_value) {
            sessionName.setValue(p_value);
        }
        
        BestOfTheRestSideStruct getSide(int p_index) {
            return (BestOfTheRestSideStruct)sides.getElement(p_index);
        }

        int getNumSides() {
            return sides.getNumElements();
        }

        
        public ICodec newCopy() {
            return new BestOfTheRestStruct();
        }
    }
    
    
    
    static class BestOfTheRestSideStruct extends MsgCodec {
        static final byte       BUY_SIDE    = (byte)'B';
        static final byte       SELL_SIDE   = (byte)'S';
        
        private final IntFieldCodec         productKey;
        private final ByteFieldCodec        side;
        private final IntFieldCodec         price;
        private final SequenceFieldCodec    exchangeVolumes;
        
        BestOfTheRestSideStruct() {
            super("BestOfTheRestSideStruct",(short)2);
            add(productKey  = new IntFieldCodec("ProductKeys"));
            add(side        = new ByteFieldCodec("Side"));
            add(price       = new IntFieldCodec("Price"));
            exchangeVolumes = new SequenceFieldCodec("ExchangeVolumes",4,new ExchangeVolumeStruct());
            add(exchangeVolumes);
        }
        
        int getProductKey() {
            return productKey.getValue();
        }

        byte getSide() {
            return side.getValue();
        }
        
        int getPrice() {
            return price.getValue();
        }
        
        void setValues(byte p_side,int p_productKey,int p_price) {
            side.setValue(p_side);
            productKey.setValue(p_productKey);
            price.setValue(p_price);
        }

        ExchangeVolumeStruct getExchVolume(int p_index) {
            return (ExchangeVolumeStruct)exchangeVolumes.getElement(p_index);
        }

        int getNumExchVols() {
            return exchangeVolumes.getNumElements();
        }
        
        void setExchVolume(int p_index,int p_volume,String p_exchange) {
            ExchangeVolumeStruct volStruct = getExchVolume(p_index);
            volStruct.setValues(p_volume, p_exchange);
        }
                
        public ICodec newCopy() {
            return new BestOfTheRestSideStruct();
        }
        
        public void reset() {
            exchangeVolumes.reset();
        }
    }
    
    static class ExchangeVolumeStruct extends MsgCodec {

        private final IntFieldCodec         volume;
        private final ByteArrayFieldCodec   exchange;
        
        ExchangeVolumeStruct() {
            super("ExchangeVolumeStruct",(short)1);
            add(volume = new IntFieldCodec("Volume"));
            add(exchange = new ByteArrayFieldCodec("Exchange",32));
        }
        
        public ICodec newCopy() {
            return new ExchangeVolumeStruct();
        }
        
        int getVolume() {
            return volume.getValue();
        }
        
        String getExchange() {
            return exchange.getStringValue();
        }
        
        void setValues(int p_volume,String p_exchange) {
            volume.setValue(p_volume);
            exchange.setValue(p_exchange);
        }
        
        public void reset() {
            exchange.setValue(null);
        }
    }
}
