package com.cboe.externalIntegrationServices.msgCodec.useCase.tests.blocking;

import java.io.IOException;

import com.cboe.externalIntegrationServices.msgCodec.AbstractDataBuffer;
import com.cboe.externalIntegrationServices.msgCodec.useCase.CodecConstants;

public class MarketDataBuffer extends AbstractDataBuffer implements CodecConstants {

    static private ThreadLocal<MarketDataHeader> marketDataHeader;
    static {
        marketDataHeader = new ThreadLocal<MarketDataHeader>() {
            protected MarketDataHeader initialValue() {
                return new MarketDataHeader("marketDataHeader");
            }
        };
    }

    int                                          groupKey;
    int                                          subIdentifier;

    public MarketDataBuffer() {
        super(MarketDataBufferID, 1024);
    }

    protected void decodeHeader() throws IOException {
        MarketDataHeader header = marketDataHeader.get();
        if (!read(header))
            throw new IllegalArgumentException("Could not find header with codec Id = "
                    + header.getCodecId());
        setGroupKey(header.getGroupKey());
        setSubIdentifier(header.getSubIdentifier());
    }

    protected void encodeHeader() {
        MarketDataHeader header = marketDataHeader.get();
        header.setGroupKey(getGroupKey());
        header.setSubIdentifier(getSubIdentifier());
        write(header);
    }

    public AbstractDataBuffer getDuplicateInstance() {
        return null;
    }

    public int getGroupKey() {
        return groupKey;
    }

    public void setGroupKey(int groupKey) {
        this.groupKey = groupKey;
    }

    public int getSubIdentifier() {
        return subIdentifier;
    }

    public void setSubIdentifier(int subIdentifier) {
        this.subIdentifier = subIdentifier;
    }
}