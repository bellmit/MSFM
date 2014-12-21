/*
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * TcpIpcTest.java
 * JUnit based test
 *
 * Created on May 17, 2002, 10:03 AM
 */

package com.cboe.lwt.interProcess.appl;

import java.net.URL;

import org.junit.Assert;


import com.cboe.lwt.byteUtil.ByteVector;
import com.cboe.lwt.eventLog.ConsoleLogger;
import com.cboe.lwt.interProcess.InterProcessConnection;
import com.cboe.lwt.interProcess.ServerSocketHandler;
import com.cboe.lwt.interProcess.TcpIpc;


/**
 *
 * @author dotyl
 */
public class ApplTest
{   
    
    private final int PORT_NUM = 47800;
    
    
    @org.junit.Test
    public void testMessages()
    {
        System.out.println("testMessages");
        try
        {
            TestMsgHandler.Factory servFactory = TestMsgHandler.getFactory();
            ServerSocketHandler serv = new ServerSocketHandler( "Serv",
                                                                PORT_NUM,
                                                                1024,
                                                                1024,
                                                                 servFactory );
            
            serv.go();
            
            URL clientUrl = new URL( "http://localhost:" + PORT_NUM );
            InterProcessConnection clientIpc = TcpIpc.getInstance( clientUrl, 4096, 4096 );

            TestClient client = new TestClient( clientIpc );
            
            client.go();
            
            client.waitForConnect();
            Assert.assertEquals( "handleConnectPrimary", servFactory.getCurMsgHandler().popCmd() );
           
            client.sendData( ByteVector.getInstance( "Test1" ) );
            Assert.assertEquals( "handleData", servFactory.getCurMsgHandler().popCmd() );
            Assert.assertTrue( servFactory.getCurMsgHandler().cmdsDone() );

            Assert.assertEquals( "handleConnectAccept", client.popCmd() );
            Assert.assertTrue( client.cmdsDone() );
            
            
            client.send( ApplMessage.DATA_WITH_CONFIRM, ByteVector.getInstance( "Test2" ) );
            Assert.assertEquals( "handleDataWithConfirm", servFactory.getCurMsgHandler().popCmd() );
            Assert.assertEquals( "handleConfirmResponse", client.popCmd() );
            Assert.assertTrue( servFactory.getCurMsgHandler().cmdsDone() );
            Assert.assertTrue( client.cmdsDone() );
            
            servFactory.getCurMsgHandler().sendData( ByteVector.getInstance( "Test3" ) );
            Assert.assertEquals( "handleData", client.popCmd() );
            Assert.assertTrue( client.cmdsDone() );
            
            servFactory.getCurMsgHandler().send( ApplMessage.DATA_WITH_CONFIRM, ByteVector.getInstance( "Test2" ) );
            Assert.assertEquals( "handleDataWithConfirm", client.popCmd() );
            Assert.assertEquals( "handleConfirmResponse", servFactory.getCurMsgHandler().popCmd() );
            Assert.assertTrue( client.cmdsDone() );
            Assert.assertTrue( servFactory.getCurMsgHandler().cmdsDone() );
            
            client.send( ApplMessage.DISCONNECT_PRIMARY, ByteVector.getInstance( "disco" ) );
            Assert.assertEquals( "handleDisconnectPrimary", servFactory.getCurMsgHandler().popCmd() );
            Assert.assertEquals( "handleDisconnectAccept", client.popCmd() );
            Assert.assertTrue( client.cmdsDone() );
            
            client.signalKill();
            client.waitForTermination();
            
            Assert.assertEquals( "handleSocketDisconnected", servFactory.getCurMsgHandler().popCmd() );
            Assert.assertTrue( servFactory.getCurMsgHandler().cmdsDone() );
            Assert.assertTrue( client.cmdsDone() );

            serv.signalKill();
        }
        catch( Exception ex )
        {
            ex.printStackTrace();
            System.exit( -1 );
        }
    }
    
    
    
}
