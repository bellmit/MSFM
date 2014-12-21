package com.cboe.externalIntegrationServices.msgCodec.useCase;

import com.cboe.externalIntegrationServices.msgCodec.ICodec;
import com.cboe.externalIntegrationServices.msgCodec.IntFieldCodec;
import com.cboe.externalIntegrationServices.msgCodec.MsgCodec;

/**
 * 
 * BESTPRACTICE Your codec classes should always extend MsgCodec.
 * 
 * @author degreefc
 *
 */
public class MyCodec extends MsgCodec implements CodecConstants {
    /*
     * BESTPRACTICE Define MsgCodec fields as "final private" 
     */
    final private IntFieldCodec myInt1;
    final private IntFieldCodec myInt2;

    public MyCodec(String p_name) {
        super(p_name, MyCodecID);
        /*
         * BESTPRACTICE Assign MsgCodec fields to an instance variable and add() it. 
         *          
         * BESTPRACTICE MsgCodec fields are named only for documentational purposes. 
         */
        myInt1 = new IntFieldCodec("myInt1");
        this.add(myInt1);
        
        myInt2 = new IntFieldCodec("myInt2");
        this.add(myInt2);
    }

    public ICodec newCopy() {
        /*
         * BESTPRACTICE Always implement the newCopy returning the results ...
         * of your MsgCodec subclass name constructor
         */
        return new MyCodec(getName());
    }

    public void setMyInt1(int myInt1) {
        /*
         * BESTPRACTICE Always use setter methods for MsgCodec fields
         * 
         * BESTPRACTICE MsgCodec field setters always delegate to the Field
         * Codec.
         */
        this.myInt1.setValue(myInt1);
    }

    public int getMyInt1() {
        /*
         * BESTPRACTICE Always use getter methods for MsgCodec fields
         * 
         * BESTPRACTICE MsgCodec field getters always delegate to the Field
         * Codec.
         */
        return myInt1.getValue();
    }

    public void setMyInt2(int myInt2) {
        this.myInt2.setValue(myInt2);
    }

    public int getMyInt2() {
        return myInt2.getValue();
    }
}