package com.cboe.externalIntegrationServices.msgCodec.useCase;

import com.cboe.externalIntegrationServices.msgCodec.ICodec;
import com.cboe.externalIntegrationServices.msgCodec.LongFieldCodec;
import com.cboe.externalIntegrationServices.msgCodec.MsgCodec;

public class MyTopLevelCodec extends MsgCodec implements CodecConstants {

    final private LongFieldCodec myLong;
    /*
     * BESTPRACTICE MsgCodecs can be fields in other MsgCodecs (embedded).
     */
    final private MyCodec        embeddedCodec;

    public MyTopLevelCodec(String p_name) {
        super(p_name, MyTopLevelCodecID);
        myLong = new LongFieldCodec("myLong");
        this.add(myLong);
        embeddedCodec = new MyCodec("myEmbeddedCodec");
        this.add(embeddedCodec);
    }

    public ICodec newCopy() {
        return new MyTopLevelCodec(getName());
    }

    public void setMyLong(long myLong) {
        this.myLong.setValue(myLong);
    }

    public long getMyLong() {
        return myLong.getValue();
    }

    public MyCodec getEmbeddedCodec() {
        /*
         * BESTPRACTICE The embedded MsgCodec's getter returns the MsgCodec. It
         * does not delegate anything.
         */
        return embeddedCodec;
    }
}