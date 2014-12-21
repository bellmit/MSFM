package com.cboe.externalIntegrationServices.msgCodec.useCase.tests.blocking;

import com.cboe.externalIntegrationServices.msgCodec.ByteFieldCodec;
import com.cboe.externalIntegrationServices.msgCodec.ICodec;
import com.cboe.externalIntegrationServices.msgCodec.IntFieldCodec;
import com.cboe.externalIntegrationServices.msgCodec.LongFieldCodec;
import com.cboe.externalIntegrationServices.msgCodec.MsgCodec;
import com.cboe.externalIntegrationServices.msgCodec.useCase.CodecConstants;

public class BookDepthEntryCodec extends MsgCodec implements CodecConstants {

    final private ByteFieldCodec side;
    final private ByteFieldCodec entryType;
    final private LongFieldCodec priceLevel;
    final private IntFieldCodec  priceLevelVol;

    public BookDepthEntryCodec(String p_name) {
        super(p_name, BookDepthEntryCodecID);

        side = new ByteFieldCodec("side");
        this.add(side);
        entryType = new ByteFieldCodec("entryType");
        this.add(entryType);
        priceLevel = new LongFieldCodec("priceLevel");
        this.add(priceLevel);
        priceLevelVol = new IntFieldCodec("priceLevelVol");
        this.add(priceLevelVol);
    }

    public ICodec newCopy() {
        return new BookDepthEntryCodec(getName());
    }

    public void setSide(byte side) {
        this.side.setValue(side);
    }

    public void setEntryType(byte entryType) {
        this.entryType.setValue(entryType);
    }

    public void setPriceLevel(long priceLevel) {
        this.priceLevel.setValue(priceLevel);
    }

    public void setPriceLevelVol(int priceLevelVol) {
        this.priceLevelVol.setValue(priceLevelVol);
    }

    public byte getSide() {
        return side.getValue();
    }

    public byte getEntryType() {
        return entryType.getValue();
    }

    public long getPriceLevel() {
        return priceLevel.getValue();
    }

    public int getPriceLevelVol() {
        return priceLevelVol.getValue();
    }
}