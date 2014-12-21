package com.cboe.externalIntegrationServices.msgCodec.useCase;

import com.cboe.externalIntegrationServices.msgCodec.ICodec;
import com.cboe.externalIntegrationServices.msgCodec.IntFieldCodec;
import com.cboe.externalIntegrationServices.msgCodec.LongFieldCodec;
import com.cboe.externalIntegrationServices.msgCodec.MsgCodec;

/**
 * 
 * @author degreefc
 *
 */
public class MyCodec2 extends MsgCodec implements CodecConstants {

    final private LongFieldCodec myLong;
    final private IntFieldCodec myInt;

    public MyCodec2(String p_name) {
        super(p_name, MyCodec2ID);
        myLong = new LongFieldCodec("myLong");
        this.add(myLong);
        
        myInt = new IntFieldCodec("myInt");
        this.add(myInt);
    }

    public ICodec newCopy() {
        return new MyCodec2(getName());
    }

    public void setMyLong(int myLong) {
        this.myLong.setValue(myLong);
    }

    public long getMyLong() {
        return myLong.getValue();
    }

    public void setMyInt(int myInt) {
        /*
         * Used in exception processing in use cases.
         */
        if (myInt == -3)
            throw new IndexOutOfBoundsException("-3");
        this.myInt.setValue(myInt);
    }

    public int getMyInt() {
        /*
         * Used in exception processing in use cases.
         */
        if (myInt.getValue() == -4)
            throw new IndexOutOfBoundsException("-4");
        return myInt.getValue();
    }
}