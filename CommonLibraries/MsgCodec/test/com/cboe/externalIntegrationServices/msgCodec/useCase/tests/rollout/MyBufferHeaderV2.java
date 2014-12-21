package com.cboe.externalIntegrationServices.msgCodec.useCase.tests.rollout;

import com.cboe.externalIntegrationServices.msgCodec.ICodec;
import com.cboe.externalIntegrationServices.msgCodec.IntFieldCodec;
import com.cboe.externalIntegrationServices.msgCodec.MsgCodec;
import com.cboe.externalIntegrationServices.msgCodec.useCase.CodecConstants;

/*
 * BESTPRACTICE A Header codec is the same as any other codec. 
 */
public class MyBufferHeaderV2 extends MsgCodec implements CodecConstants {

    final private IntFieldCodec myHeaderInt;
    final private IntFieldCodec myHeaderInt2;

    public MyBufferHeaderV2(String p_name) {
        super(p_name, MyBufferHeaderV2ID);
        
        myHeaderInt = new IntFieldCodec("myHeaderInt");
        add(myHeaderInt);
        
        myHeaderInt2 = new IntFieldCodec("myHeaderInt2");
        add(myHeaderInt2);
    }

    public ICodec newCopy() {
        return new MyBufferHeaderV2(getName());
    }

    void setMyHeaderInt(int myHeaderInt) {
        this.myHeaderInt.setValue(myHeaderInt);
    }

    int getMyHeaderInt() {
        return myHeaderInt.getValue();
    }

    void setMyHeaderInt2(int myHeaderInt) {
        this.myHeaderInt2.setValue(myHeaderInt);
    }

    int getMyHeaderInt2() {
        return myHeaderInt2.getValue();
    }
}