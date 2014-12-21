/*
 * Created on Oct 27, 2008
 */
package com.cboe.externalIntegrationServices.msgCodec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

import com.cboe.externalIntegrationServices.msgCodec.AbstractDataBuffer;
import com.cboe.externalIntegrationServices.msgCodec.MsgCodec;

/**
 * This class measures the performance of a sample usage of a Buffer and
 * Codec that simulates an application.  An AwayMarketBuffer and 
 * AwayMarketQuote is simulated, as well as Buffer Blocking. 
 * @author hammj
 */
public class MsgCodecPerfTestSample extends MsgCodecPerfTester
{
    private static final String             SESSION_NAME    = "W_MAIN";
    private static final int                BLOCK_SIZE      = 1024;
    private int                             BUFF_SIZE       = 1024;
    private int                             ITERS           = 5000000; 
    private boolean                         useThreadLocals;
    private boolean                         sleepAfter;
    private boolean                         testBlocking;
    private boolean                         readAndWriteBlock;
    private AwayMarketQuoteStruct           quoteStruct;
    private ByteArrayOutputStream           out;
    private ModifiableByteArrayInputStream  in;
    private DataBufferBlock                 block;
    private byte[]                          readBuff;
    private int                             writeCount;
    private int                             readCount;

    // Analysis of quote sizes shows bid and ask sizes are 91% < 120
    // The remaining are > 120 but only a very tiny few are > 8192, so for
    // our purposes, we'll say 90% are < 120, and 10% are > and none are > 8192.
    // We'll make 3 parallel arrays for sizes, exchange ids, and bestExchanges
    
    private static final int            LAST_INDEX = 9;
    private int[]                       sizes =     {  9, 10, 11, 12, 90, 100, 110, 120, 127, 5000};
    private byte[]                      exchanges = { 'A','B','C','D','E','F','G','H','I','J' };
    private byte[][]                    bestExchanges = { null,
                                                          "ABC".getBytes(),
                                                          "DEF".getBytes(),
                                                          "GH".getBytes(),
                                                          "IJ".getBytes(),
                                                          "ABCD".getBytes(),
                                                          "E".getBytes(),
                                                          "FG".getBytes(),
                                                          "H".getBytes(),
                                                          "IJ".getBytes()
                                                        };
    private byte[][]                    storageForRead = new byte[LAST_INDEX+1][];
    private int                         index = 0;
    

    public static void main(String[] args) throws Exception {
        MsgCodecPerfTestSample self = new MsgCodecPerfTestSample(args);
        self.setupForReadAndGetValuesTest();
        self.testBlocking = true;
        self.doPerfTest("Buff Encode/Decode And Blocking Test:",5);
        self.testBlocking = false;
        self.doPerfTest("Buff Encode/Decode Only Test:",5);
        if (self.sleepAfter) {
            try {
                System.out.println("Sleeping...");
                Thread.sleep(10000000);
            } catch (InterruptedException e) {
                System.out.println("Sleep interrupted");
            }
        }
        System.out.println("Done");
    }

    public MsgCodecPerfTestSample(String[] args) {
        BUFF_SIZE               = 1024;
        useThreadLocals         = true;
        readAndWriteBlock          = true;
        if (args != null && args.length > 0) {
            for(int i = 0; i < args.length; i++) {
                String arg = args[i];
                if ("-usage".equals(arg)) {
                    System.out.println("Usage: [buffSize] [-useThreadLocals] [-sleep]");
                    System.exit(1);
                }
                else if ("-useThreadLocals".equals(arg)) {
                    useThreadLocals = true;
                } 
                else if ("-noUseThreadLocals".equals(arg)) {
                    useThreadLocals = false;
                } 
                else if ("-readAndWriteBlock".equals(arg)) {
                    readAndWriteBlock = true;
                } 
                else if ("-noReadAndWriteBlock".equals(arg)) {
                    readAndWriteBlock = false;
                } 
                else if ("-sleep".equals(arg)) {
                    sleepAfter = true;
                }
                else if ("-iters".equals(arg)) {
                    i++;
                    if (i >= args.length) {
                        System.out.println("Required argument is missing. Missing number after -iters");
                        System.exit(1);
                    }
                    try {
                        ITERS = Integer.parseInt(args[i]);
                    } catch (Exception e) {
                        System.out.println("Error parsing iters");
                        System.exit(1);
                    }
                }
                else {
                    try {
                        BUFF_SIZE = Integer.parseInt(arg);
                    } catch (Exception e) {
                        System.out.println("Error parsing buffSize");
                        System.exit(1);
                    }
                }
            }
        }
        System.out.println("BuffSize:           " + BUFF_SIZE);
        System.out.println("UseThreadLocals:    " + useThreadLocals);
        System.out.println("ReadAndWriteBlock:  " + readAndWriteBlock);
        System.out.println("SleepAfter:         " + sleepAfter);

        out     = new ByteArrayOutputStream(BLOCK_SIZE);
        block   = new DataBufferBlock(BLOCK_SIZE);
        readBuff = new byte[BLOCK_SIZE];
    }

    private void doPerfTest(String msg,int numSamples) throws Exception {
        writeCount = 0;
        readCount  = 0;
        System.out.println("\n" + msg);
        int actualIters = (testBlocking) ? ITERS / 10 : ITERS;
        perfTest(actualIters, numSamples);
        System.out.println("WriteCnt: " + writeCount + " ReadCnt: " + readCount);
    }
    
    protected int perfTestReadAndGetValues(long timestamp, int iterationCount) throws Exception {
        AwayMarketBuffer        buff = AwayMarketBuffer.getThreadLocalInstance();

        if (testBlocking) {
            try {
                in.rewind();
                if (readAndWriteBlock) {
                    int bytesRead = in.read(readBuff);
                    block.setStorage(readBuff,bytesRead);
                }
                else {
                    block.setStorage(readBuff,readBuff.length);
                }
                int count = 0;
                while(block.read(buff)) {
                    readAndGetValues(buff);
                    readCount++;
                    count++;
                }
                return count;
            } catch (IOException e) {
                System.out.println("IOException in read");
                System.exit(0);
            }
            return 1;
        }
        else {
            final int nextIndex = getNextIndex();
            final byte[] storage = storageForRead[nextIndex];
            buff.setStorage(storage, 0, storage.length);
            readAndGetValues(buff);
            readCount++;
            return 1;
        }
    }

    protected int perfTestSetValuesAndWrite(long timestamp, int iterationCount) {
        AwayMarketBuffer buff = setValuesAndWrite(timestamp,iterationCount);

        if (testBlocking) {
            block.reset();
            int count = 0;
            while(block.write(buff)) {
                writeCount++;
                count++;
                buff = setValuesAndWrite(timestamp,iterationCount);
            }
            final byte[] storage = block.getStorage();
            final int len = block.getLength();
            if (readAndWriteBlock) {
                out.reset();
                out.write(storage, 0, len);
            }
            return count;
        }
        else {
            writeCount++;
            return 1;
        }
    }
    
    private final void readAndGetValues(AwayMarketBuffer buff) throws Exception {
        AwayMarketQuoteCodec   quoteCodec = AwayMarketQuoteCodec.getInstance();
        buff.read(quoteCodec);

        AwayMarketQuoteStruct  quoteStruct;
        if (useThreadLocals) { 
            quoteStruct = AwayMarketQuoteStruct.getThreadLocalInstance();
        }
        else {
            quoteStruct = AwayMarketQuoteStruct.getInstance();
        }
        
        quoteStruct.productKey            = buff.getProductKey();
        quoteStruct.exchangeId            = quoteCodec.getExchangeId();
        quoteStruct.bidPrice              = quoteCodec.getBidPrice();
        quoteStruct.askPrice              = quoteCodec.getAskPrice();
        quoteStruct.bidSize               = quoteCodec.getBidSize();
        quoteStruct.askSize               = quoteCodec.getAskSize();
        quoteStruct.bestBidExchanges      = quoteCodec.getBestBidExchanges();
        quoteStruct.bestBidExchangesLength= quoteCodec.getBestBidExchangesLength();
        quoteStruct.bestAskExchanges      = quoteCodec.getBestAskExchanges();
        quoteStruct.bestAskExchangesLength= quoteCodec.getBestAskExchangesLength();
        doSomethingWith(quoteStruct);
    }
    
    /**
     * Simulate application that gets a buffer, fills in buffer info,
     * then simulate BOTR message and write into buffer.
     */
    private final AwayMarketBuffer setValuesAndWrite(long timestamp, int iterationCount) {
        AwayMarketBuffer        buff;
        if (useThreadLocals) { 
            buff = AwayMarketBuffer.getThreadLocalInstance();
            buff.reset();
        }
        else {
            buff = AwayMarketBuffer.getInstance(BUFF_SIZE);
        }

        buff.setProductClassKey(iterationCount);
        buff.setProductKey(iterationCount+1);
        buff.setSessionName(SESSION_NAME);
        
        final int nextIndex = getNextIndex();
        final int size      = sizes[nextIndex];
        final byte exch     = exchanges[nextIndex];
        final byte[] bestEx = bestExchanges[nextIndex];

        AwayMarketQuoteCodec   quote = AwayMarketQuoteCodec.getInstance();

        quote.setExchangeId(exch);
        
        quote.setBidPrice(timestamp);
        quote.setAskPrice(timestamp+1);

        quote.setBidSize(size);
        quote.setAskSize(size+1);
        
        int len = (bestEx == null) ? 0 : bestEx.length;
        quote.setBestBidExchanges(bestEx, len);
        quote.setBestAskExchanges(bestEx, len);
        
        buff.write(quote);
        doSomethingWith(buff);
        return buff;
    }

    private void setupForReadAndGetValuesTest() {
        AwayMarketBuffer buff;
        for(int i = 0; i <= LAST_INDEX; i++) {
            buff = setValuesAndWrite(System.currentTimeMillis(),10);
            storageForRead[i] = new byte[buff.length()];
            buff.appendTo(storageForRead[i], 0);
        }
        
        int i = 0;
        buff = setValuesAndWrite(System.currentTimeMillis(),i++);
        while(block.write(buff)) {
            buff = setValuesAndWrite(System.currentTimeMillis(),i++);
        }
        System.out.println("Msg Per Block: " + i + " BlockLen: " + block.getLength());
        out.write(block.getStorage(),0,block.getLength());
        byte[] outBytes = out.toByteArray();
        in = new ModifiableByteArrayInputStream(outBytes,outBytes.length);
        try {
            in.read(readBuff);
        } catch (IOException e) {
            System.out.println("IOException in read");
            System.exit(0);
        }
    }

    private final void doSomethingWith(AwayMarketBuffer p_buff) {
        if (p_buff.getProductKey() < 0) {
            System.out.println("Buffer productKey < 0");
        }
    }

    private final void doSomethingWith(AwayMarketQuoteStruct p_quote) {
        if (p_quote.askSize < 0) {
            System.out.println("AskSize < 0");
        }
    }

    private final int getNextIndex() {
        int nextIndex = index-1;
        if (nextIndex < 0)
            nextIndex = LAST_INDEX;
        index = nextIndex;
        return nextIndex;
    }

    
    // ************************************************************************
    // Inner classes to implement various application components.
    // There are 4 classes below:  
    //  CodecIds        Contains the constants for buffer and codecIds.
    //  MarketBuffer    The Buffer into which we encode/decode data.
    //  HeaderCodec     A Header codec for the Buffer
    //  BotrMsg         A BestOfTheRest (BOTR) Message Codec.
    //  BotrStruct      A place to store the contents of the BotrCodec
    // ************************************************************************
    
    public static class CodecIds {
        public static final short               BUFF_ID                 = (short)1;
        public static final short               BUFF_HEADER_CODEC_ID    = (short)2;
        public static final short               AWAY_MKT_CODEC_ID       = (short)3;
    }
    
    /**
     * MarketBuffer class to emulate an application Buffer with 
     * productClassKey, productKey, and sessionName.
     */
    
    public static class AwayMarketBuffer extends AbstractDataBuffer {
        private int             productClassKey;
        private int             productKey;
        private byte[]          sessionName;
        private String          sessionNameString;
        
        private static final ThreadLocal<AwayMarketBuffer>   threadLocalInstance = new ThreadLocal<AwayMarketBuffer>() {
            protected AwayMarketBuffer initialValue() {
                return new AwayMarketBuffer(1024);
            }
        };

        protected AwayMarketBuffer(int p_buffSize) {
            super(CodecIds.BUFF_ID, p_buffSize);
        }

        public static final AwayMarketBuffer getInstance(int p_size) {
            return new AwayMarketBuffer(p_size);
        }
        
        public static final AwayMarketBuffer getThreadLocalInstance() {
            return threadLocalInstance.get();
        }
        
        protected void decodeHeader() throws IOException {
            HeaderCodec header = HeaderCodec.getInstance();
            super.read(header);
            productClassKey     = header.getProductClassKey();
            productKey          = header.getProductKey();
            sessionName         = header.getSessionName();
        }

        protected void encodeHeader() {
            HeaderCodec header = HeaderCodec.getInstance();
            header.setProductClassKey(productClassKey);
            header.setProductKey(productKey);
            header.setSessionName(sessionNameString);
            super.write(header);
        }

        public AbstractDataBuffer getDuplicateInstance() {
            AwayMarketBuffer dupBuff = getInstance(super.length());
            dupBuff.copyContentsFrom(this);
            dupBuff.productClassKey = this.productClassKey;
            dupBuff.productKey      = this.productKey;
            System.arraycopy(this.sessionName,0,dupBuff.sessionName,0,this.sessionName.length);
            dupBuff.sessionNameString = this.sessionNameString;
            return dupBuff;
        }
        
        public final void setProductClassKey(int p_productClassKey) {
            productClassKey = p_productClassKey;
        }
        
        public final void setProductKey(int p_productKey) {
            productKey = p_productKey;
        }
        
        public final void setSessionName(String p_sessionName) {
            sessionNameString = p_sessionName;
        }
        
        public final int getProductClassKey() {
            return productClassKey;
        }
        
        public final int getProductKey() {
            return productKey;
        }
        public final byte[] getSessionName() {
            return sessionName;
        }
    }
        
    public static final class HeaderCodec extends MsgCodec {
        private static final ThreadLocal<HeaderCodec>   threadLocalInstance = new ThreadLocal<HeaderCodec>() {
            protected HeaderCodec initialValue() {
                return new HeaderCodec();
            }
        };
        
        private final IntFieldCodec         productClassKey;
        private final IntFieldCodec         productKey;
        private final ByteArrayFieldCodec   sessionName;

        private HeaderCodec() {
            super("HeaderCodec",CodecIds.BUFF_HEADER_CODEC_ID);
            productClassKey         = new IntFieldCodec("ProductClassKey");
            productKey              = new IntFieldCodec("ProductKey");
            sessionName             = new ByteArrayFieldCodec("SessionName",32);
            add(productClassKey);
            add(productKey);
            add(sessionName);
        }

        public static final HeaderCodec getInstance() {
            return threadLocalInstance.get(); 
        }

        public ICodec newCopy() {
            return new HeaderCodec();
        }

        public void setProductClassKey(int p_value) {
            productClassKey.setValue(p_value);
        }
        public int getProductClassKey() {
            return productClassKey.getValue();
        }

        public void setProductKey(int p_value) {
            this.productKey.setValue(p_value);
        }

        public int getProductKey() {
            return productKey.getValue();
        }
        
        public void setSessionName(String p_value) {
            sessionName.setValue(p_value);
        }

        public byte[] getSessionName() {
            return sessionName.getValue();
        }
    }
    
    public static final class AwayMarketQuoteCodec extends MsgCodec {
        private static final ThreadLocal<AwayMarketQuoteCodec>   threadLocalInstance = new ThreadLocal<AwayMarketQuoteCodec>() {
            protected AwayMarketQuoteCodec initialValue() {
                return new AwayMarketQuoteCodec();
            }
        };
        
        private final ByteFieldCodec            exchange;
        private final LongFieldCodec            bidPrice;
        private final LongFieldCodec            askPrice;
        private final CompressedIntFieldCodec   bidSize;
        private final CompressedIntFieldCodec   askSize;
        private final ByteArrayFieldCodec       bestBidExchs;   
        private final ByteArrayFieldCodec       bestAskExchs;   

        private AwayMarketQuoteCodec() {
            super("BotrCodec",CodecIds.AWAY_MKT_CODEC_ID);
            add(exchange        = new ByteFieldCodec("Exchange"));
            add(bidPrice        = new LongFieldCodec("BidPrice"));
            add(askPrice        = new LongFieldCodec("AskPrice"));
            add(bidSize         = new CompressedIntFieldCodec("BidSize"));
            add(askSize         = new CompressedIntFieldCodec("AskSize"));
            add(bestBidExchs    = new ByteArrayFieldCodec("BestBidExchanges",16));
            add(bestAskExchs    = new ByteArrayFieldCodec("BestAskExchanges",16));
        }

        public static final AwayMarketQuoteCodec getInstance() {
            return threadLocalInstance.get(); 
        }

        public ICodec newCopy() {               return new HeaderCodec();   }

        public void setExchangeId(byte val) {   exchange.setValue(val);     }
        public byte getExchangeId() {           return exchange.getValue(); }

        public void setBidPrice(long val) {     this.bidPrice.setValue(val);}
        public long getBidPrice() {             return bidPrice.getValue(); }

        public void setAskPrice(long val) {     askPrice.setValue(val);     }

        public long getAskPrice() {             return askPrice.getValue(); }

        public void setBidSize(int val) {       bidSize.setValue(val);      }
        public int getBidSize() {               return bidSize.getValue();  }

        public void setAskSize(int val) {       askSize.setValue(val);      }
        public int getAskSize() {               return askSize.getValue();  }

        public void setBestBidExchanges(byte[] val,int p_len) {
            this.bestBidExchs.setValue(val, p_len);
        }
        public byte[] getBestBidExchanges() {       return bestBidExchs.getValue();     }
        public int getBestBidExchangesLength() {    return bestBidExchs.getLength();    }

        public void setBestAskExchanges(byte[] val,int p_len) {
            this.bestAskExchs.setValue(val, p_len);
        }
        public byte[] getBestAskExchanges() {       return bestAskExchs.getValue();     }
        public int getBestAskExchangesLength() {    return bestAskExchs.getLength();    }
    }
    
    public static final class AwayMarketQuoteStruct {
        private static final ThreadLocal<AwayMarketQuoteStruct>   threadLocalInstance = new ThreadLocal<AwayMarketQuoteStruct>() {
            protected AwayMarketQuoteStruct initialValue() {
                return new AwayMarketQuoteStruct();
            }
        };

        public static final AwayMarketQuoteStruct getInstance() {
            return new AwayMarketQuoteStruct();
        }
        
        public static final AwayMarketQuoteStruct getThreadLocalInstance() {
            return threadLocalInstance.get();
        }
        
        public int          productKey;
        public byte         exchangeId;
        public long         bidPrice;
        public long         askPrice;
        public int          bidSize;
        public int          askSize;
        public byte[]       bestBidExchanges;
        public int          bestBidExchangesLength;
        public byte[]       bestAskExchanges;
        public int          bestAskExchangesLength;
    }

    public static class ModifiableByteArrayInputStream extends ByteArrayInputStream {
        public ModifiableByteArrayInputStream(byte[] buf, int length) {
            super(buf, 0, length);
        }

        public void rewind() {
            super.pos = 0;
        }
    }
}
