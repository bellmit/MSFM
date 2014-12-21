/*
 * Created on Nov 3, 2008
 */
package com.cboe.externalIntegrationServices.msgCodec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.cboe.externalIntegrationServices.msgCodec.MsgCodecPerfTestSample.AwayMarketBuffer;
import com.cboe.externalIntegrationServices.msgCodec.MsgCodecPerfTestSample.CodecIds;
import com.cboe.externalIntegrationServices.msgCodec.MsgCodecPerfTestSample.HeaderCodec;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class DataBufferBlockTest extends TestCase
{

    private boolean                         debug = false;                   
    private final StringBuilder             sb = new StringBuilder();
    private LimitWriteByteArrayOutputStream out;
    private DataBufferBlock                 block = new DataBufferBlock(1024);
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(DataBufferBlockTest.class);
    }

    public static Test suite() {
        return new TestSuite(DataBufferBlockTest.class);
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        System.out.println(getClass().getName() + ": " + getName());
    }

    public void testToString() {
        /*
         * Found a bug in toString(), so I wrote this test.  When setStorate
         * was called with length arg == to encodedLength in the data block,
         * it was truncating the output and reported in the toString value
         * the string "End position is greater than storage length".
         * Contrive case to make sure it no longer does that, and then contrive
         * case to make sure it does to it when appropriate. 
         */
        writeOutputBlocks(4096,1,1);
        LimitReadByteArrayInputStream in;
        in = new LimitReadByteArrayInputStream(out.getStorage(),out.size(),out.size());
        byte[] blockStorage = new byte[out.size()];
        int remaining       = blockStorage.length;
        int bytesRead       = 0;
        
        bytesRead = in.read(blockStorage,0,remaining);
        if (bytesRead > 0) {
            block.setStorage(blockStorage, blockStorage.length-2);

            
            System.out.println(block);
        }
        
    }
    
    
    public void testBlockEncodeDecode() throws Exception {
        int numBlocks           = 7;
        int numBuffsPerBlock    = 5;
        int outStorageSize      = 4096;
//        debug = true;
        writeOutputBlocks(outStorageSize,numBlocks,numBuffsPerBlock);

        // Use all possible read limits 1 through outStorageSize inclusive.
        for(int readLimit = 1; readLimit <= outStorageSize; readLimit++) {
            doStreamReaderTest(readLimit,numBlocks,numBuffsPerBlock);
        }
    }

    
    private void doStreamReaderTest(int readLimit,int expectedBlockCount,int expectedBuffCount) throws Exception {
        LimitReadByteArrayInputStream in;
        in = new LimitReadByteArrayInputStream(out.getStorage(),out.size(),readLimit);
        
        Buff buffer         = Buff.getThreadLocalInstance();
        byte[] blockStorage = new byte[1024];
        String expectName;
        int remaining       = blockStorage.length;
        int lenSoFar        = 0;
        int bytesRead       = 0;
        int blockCount      = 0;
        int buffReadCount   = 0;
        int buffSkipCount   = 0;

        while((bytesRead = in.read(blockStorage,lenSoFar,remaining)) > 0) {
            lenSoFar      += bytesRead;
            remaining   = blockStorage.length - lenSoFar; 
            block.setStorage(blockStorage, lenSoFar);
            if (debug) {
                sb.setLength(0);
                sb.append("Read Block: ").append(blockCount);
                sb.append(" BytesRead: ").append(bytesRead);
                sb.append(" LenSoFar: ").append(lenSoFar);
                sb.append(" Remain:").append(remaining);
                System.out.println(sb.toString());
                System.out.println(block);
            }
            while(block.isComplete()) {
                if (debug) {
                    sb.setLength(0);
                    sb.append("    BlockComplete: Length: ").append(block.getLength());
                    System.out.println(sb.toString());
                }
                buffReadCount = 0;
                while(block.read(buffer)) {
                    if (debug) {
                        sb.setLength(0);
                        sb.append("        ReadBuff: Key: ").append(buffer.getKey());
                        sb.append(" Name: ").append(buffer.getName());
                        System.out.println(sb.toString());
                    }
                    int expectKey = buildKey(blockCount,buffReadCount);
                    assertEquals("bufferKey",expectKey,buffer.getKey());
                    expectName = buildBufferName(blockCount,buffReadCount);
                    assertEquals("bufferName",expectName,buffer.getName());
                    buffReadCount++;
                }

                block.rewind();
                buffSkipCount = 0;
                short bufId;
                while((bufId = block.nextBufferId()) >= 0) {
                    assertEquals("bufferId", buffer.getBufferId(), bufId);
                    boolean skipVal = block.skip();
                    assertTrue("expecting skip to return true",skipVal);
                    if (debug) {
                        sb.setLength(0);
                        sb.append("        SkipBuff: ").append(skipVal);
                        System.out.println(sb.toString());
                    }
                    buffSkipCount++;
                }
                
                lenSoFar    = block.compact();
                remaining   = blockStorage.length - lenSoFar;
                blockCount++;
                if (debug) {
                    sb.setLength(0);
                    sb.append("    After compact: RemainingInBlock: ").append(lenSoFar);
                    sb.append(" BlockLen: ").append(block.getLength());
                    System.out.println(sb.toString());
                }
                assertEquals("buffReadCount",expectedBuffCount,buffReadCount);
                assertEquals("buffSkipCount",expectedBuffCount,buffSkipCount);
            }
        }
        assertEquals("blockCount",expectedBlockCount,blockCount);
    }

    private void writeOutputBlocks(int outStorageSize,int numBlocks, int numBuffsPerBlock) {
        Buff buff = Buff.getThreadLocalInstance();
        out = new LimitWriteByteArrayOutputStream(outStorageSize,outStorageSize);
        String buffName;
        for(int blockCnt = 0; blockCnt < numBlocks; blockCnt++) {
            block.reset();
            for(int buffCnt = 0; buffCnt < numBuffsPerBlock; buffCnt++) {
                buff.reset();
                buff.setKey(buildKey(blockCnt,buffCnt));
                buffName = buildBufferName(blockCnt, buffCnt);
                buff.setName(buffName);
                buff.write(null);
                block.write(buff);
                if (debug) {
                    System.out.println("WriteBuff: " + buffName);
                }
            }
            out.write(block.getStorage(),0,block.getLength());
            if (debug) {
                System.out.println("WriteBlock: Len: " + block.getLength() + 
                        " OutSizeSoFar: " + out.size()); 
            }
        }
        
    }

    private int buildKey(int blockCnt,int buffCnt) {
        return blockCnt * 1000 + buffCnt;
    }
    private String buildBufferName(int blockCnt,int buffCnt) {
        sb.setLength(0);
        sb.append("Buff[").append(blockCnt).append('.').append(buffCnt).append(']');
        return sb.toString();
    }
    
    private static class LimitReadByteArrayInputStream extends ByteArrayInputStream {
        private int                     limit;
        
        private LimitReadByteArrayInputStream(byte[] p_storage,int p_length,int p_limit) {
            super(p_storage,0,p_length);
            limit   = p_limit;
        }
        
        private void setLimit(int p_limit) {
            limit = p_limit;
        }
        
        public int read(byte[] buff,int offset, int maxLength) {
            int readLimit = (limit < maxLength) ? limit : maxLength;
            return super.read(buff,offset,readLimit);
        }
    }
    
    private static class LimitWriteByteArrayOutputStream extends ByteArrayOutputStream {
        private int                     limit;
        
        private LimitWriteByteArrayOutputStream(int p_size,int p_limit) {
            super(p_size);
            limit   = p_limit;
        }
        
        private void setLimit(int p_limit) {
            limit = p_limit;
        }
        
        private int limitedWrite(byte[] buff,int offset, int maxLength) {
            int writeLimit = (limit < maxLength) ? limit : maxLength;
            super.write(buff,offset,writeLimit);
            return writeLimit;
        }
        
        private byte[] getStorage() {
            return super.buf;
        }
    }
    
    private static class Buff extends AbstractDataBuffer {
        private int             key;
        private String          name;
        
        private static final ThreadLocal<Buff>   threadLocalInstance = new ThreadLocal<Buff>() {
            protected Buff initialValue() {
                return new Buff(1024);
            }
        };

        protected Buff(int p_buffSize) {
            super(CodecIds.BUFF_ID, p_buffSize);
        }

        public static final Buff getInstance(int p_size) {
            return new Buff(p_size);
        }
        
        public static final Buff getThreadLocalInstance() {
            return threadLocalInstance.get();
        }
        
        protected void decodeHeader() throws IOException {
            HeaderCodec header = HeaderCodec.getInstance();
            super.read(header);
            key         = header.getKey();
            name        = header.getBufferName();
        }

        protected void encodeHeader() {
            HeaderCodec header = HeaderCodec.getInstance();
            header.setKey(key);
            header.setBufferName(name);
            super.write(header);
        }

        public AbstractDataBuffer getDuplicateInstance() {
            Buff dupBuff = getInstance(super.length());
            dupBuff.copyContentsFrom(this);
            dupBuff.key     = this.key;
            dupBuff.name    = this.name;
            return dupBuff;
        }
        
        public final void setKey(int p_productClassKey) {
            key = p_productClassKey;
        }
        public final int getKey() {
            return key;
        }
        
        public final void setName(String p_sessionName) {
            name = p_sessionName;
        }
        public final String getName() {
            return name;
        }
    }
        
    private static final class HeaderCodec extends MsgCodec {
        private static final ThreadLocal<HeaderCodec>   threadLocalInstance = new ThreadLocal<HeaderCodec>() {
            protected HeaderCodec initialValue() {
                return new HeaderCodec();
            }
        };
        
        private final IntFieldCodec         key;
        private final ByteArrayFieldCodec   bufferName;

        private HeaderCodec() {
            super("HeaderCodec",CodecIds.BUFF_HEADER_CODEC_ID);
            key                 = new IntFieldCodec("Key");
            bufferName          = new ByteArrayFieldCodec("Name",32);
            add(key);
            add(bufferName);
        }

        public static final HeaderCodec getInstance() {
            return threadLocalInstance.get(); 
        }

        public ICodec newCopy() {
            return new HeaderCodec();
        }

        public void setKey(int p_value) {
            key.setValue(p_value);
        }
        public int getKey() {
            return key.getValue();
        }

        public void setBufferName(String p_value) {
            bufferName.setValue(p_value);
        }

        public String getBufferName() {
            return bufferName.getStringValue();
        }
    }
    
}
