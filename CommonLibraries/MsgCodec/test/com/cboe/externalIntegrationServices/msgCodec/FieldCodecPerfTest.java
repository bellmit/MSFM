/*
 * Created on Oct 15, 2008
 */
package com.cboe.externalIntegrationServices.msgCodec;

public class FieldCodecPerfTest
{

    /**
     * @param args
     */
    public static void main(String[] args) {
        ByteFieldCodecTest          byteTest    = new ByteFieldCodecTest();
        ShortFieldCodecTest         shortTest   = new ShortFieldCodecTest();
        IntFieldCodecTest           intTest     = new IntFieldCodecTest();
        LongFieldCodecTest          longTest    = new LongFieldCodecTest();
        CompressedIntFieldCodecTest compIntTest = new CompressedIntFieldCodecTest();
        ByteArrayFieldCodecTest     byteArrayTest   = new ByteArrayFieldCodecTest();
        
        int iters = 50000000;
        for(int i = 0; i < 2; i++) {
            System.out.println("\nPass: " + (i+1));
            byteTest.perfTest(iters,5);
            shortTest.perfTest(iters,5);
            intTest.perfTest(iters,5);
            longTest.perfTest(iters,5);
            compIntTest.perfTest(iters,5);
            byteArrayTest.perfTest(iters,5);
        }
    }

}
