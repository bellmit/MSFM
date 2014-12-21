package com.cboe.externalIntegrationServices.msgCodec.useCase;

import com.cboe.externalIntegrationServices.msgCodec.ICodec;
import com.cboe.externalIntegrationServices.msgCodec.IntFieldCodec;
import com.cboe.externalIntegrationServices.msgCodec.MsgCodec;

/*
 * BESTPRACTICE A Header codec is the same as anyother codec. 
 */
public class MyBufferHeader extends MsgCodec implements CodecConstants {

    final private IntFieldCodec myHeaderInt;

    public MyBufferHeader(String p_name) {
        super(p_name, MyBufferHeaderID);
        myHeaderInt = new IntFieldCodec("myHeaderInt");
        add(myHeaderInt);
    }

    public ICodec newCopy() {
        return new MyBufferHeader(getName());
    }

    public void setMyHeaderInt(int myHeaderInt) {
        this.myHeaderInt.setValue(myHeaderInt);
        /*
         * Used in exception processing in use cases.
         */
        if (myHeaderInt == -3)
            throw new IndexOutOfBoundsException("-3");
    }

    public int getMyHeaderInt() {
        /*
         * Used in exception processing in use cases.
         */
        if (myHeaderInt.getValue() == -4)
            throw new IndexOutOfBoundsException("-4");
        return myHeaderInt.getValue();
    }
}