package com.cboe.externalIntegrationServices.msgCodec.useCase;
/**
 * BESTPRACTICE Use a constants interface for MsgCodec ids 
 * 
 * BESTPRACTICE Use a constants interface for Buffer ids
 * 
 * BESTPRACTICE Buffer IDs and MsgCodec IDs must be globally unique.
 */
public interface CodecConstants {
    /*
     * BESTPRACTICE Never change a CodecConstant that has been deployed
     */
    public final short MyBufferID = (short)1;
    public final short MyCodecID = (short)2;
    public final short MyBufferHeaderID = (short)3;
    public final short MyTopLevelCodecID = (short)4;
    public final short MyCodec2ID = (short)5;
    public final short MyZeroSizeBufferID = (short)6;
    public final short MyZeroSizeBufferWithHeaderID = (short)7;
    public final short MyBufferHeaderV2ID = (short)8;
    public final short PingBufferID = (short)9;
    public final short PingHeaderID = (short)10;
    public final short MarketDataBufferID = (short)11;
    public final short MarketDataHeaderID = (short)12;
    public final short BookDepthEntryCodecID = (short)13;
}
