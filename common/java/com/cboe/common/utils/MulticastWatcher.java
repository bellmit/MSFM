
package com.cboe.common.utils;

import java.io.*;
import java.util.*;
import java.net.*;
import java.text.*;

public class MulticastWatcher implements Runnable
{

   private PrintStream  myStream;
   private String       myGroupName;
   private InetAddress  myGroupAddress;
   private int          myPortNo;
   private String       myComment;
   private String       myHostName;
   private MulticastSocket mySocket;
   private Thread       runner = null;

   public MulticastWatcher(PrintStream stream, String groupAddr, int portNo, String comment)
   throws java.net.SocketException, java.io.IOException, java.net.UnknownHostException
   {
      myStream = stream;
      
      
      stream.println("Watcher: assigning port number...");
      myPortNo = portNo;
      stream.println("Watcher: looking for group address");
      myGroupName = groupAddr;
      myGroupAddress = InetAddress.getByName(groupAddr);
      if (myGroupAddress.isMulticastAddress() == false) {
         throw new java.net.UnknownHostException("BAD MULTICAST ADDRESS. The specified group address is not a valid multicast address. Valid addresses are 224.0.0.0 to 239.255.255.255");
      }

      stream.println("Watcher: creating multicast socket");
      mySocket = new MulticastSocket(portNo);
      stream.println("Watcher: joining multicast group");
      mySocket.joinGroup(myGroupAddress);
      stream.println("Watcher: Initialization completed.");

      myHostName = InetAddress.getLocalHost().getHostName();
      myComment = comment;
      banner(stream, myHostName, myComment);

   }
   
   public void banner (PrintStream stream, String hostname, String comment)
   {
      stream.println("");
      stream.println("MULTICAST TRAFFIC WATCHER V1.0 watching(Grp:"+myGroupName + ", Port:"+myPortNo+")");
      if (comment != null)
         stream.println("HOST("+hostname+")    <<<" + comment + ">>>");
      stream.println("-----------------------------------------------------------------------------");
   }

   public static void main (String[] argv)
   throws Exception
   {
      if ((argv.length == 3) && ("-Dsend".equals(argv[0]))) {
         int   port = Integer.parseInt(argv[2]);
         InetAddress grp = InetAddress.getByName(argv[1]);
         byte[] buffer = new byte[] { (byte)0xcd, (byte)0xcd, (byte)0xcd, (byte)0xcd };
         DatagramPacket dp = new DatagramPacket(buffer,4,grp,port);
         MulticastSocket ms = new MulticastSocket(port);
         ms.send(dp,(byte)100);
         return;   
      }

      if ((argv.length == 4) && ("-Dping".equals(argv[0]))) {
         int   port = Integer.parseInt(argv[3]);
         InetAddress grp = InetAddress.getByName(argv[2]);
         int   ping = Integer.parseInt(argv[1]);
         
         for (int i = ping; i >= 0; --i) {
            byte[] buffer = new byte[] { (byte)0xce, (byte) i, (byte)0xce, (byte)0xce };
            DatagramPacket dp = new DatagramPacket(buffer,4,grp,port);
            MulticastSocket ms = new MulticastSocket(port);
            ms.send(dp,(byte)i);
         }
         return;   
      }

      
      
      if (argv.length < 2) {
         System.out.println("Usage: MulticastWatcher <group address> <port number> [comment text]");
         return;
      }
      String comment;
      if (argv.length == 3)
         comment = argv[2];
      else
         comment = null;
         
      MulticastWatcher  mw = new MulticastWatcher(System.out, argv[0], Integer.parseInt(argv[1]), comment);
      mw.start();
   }


   public void start()
   {
      runner = new Thread(this, "MulticastWatcher");
      runner.start();
   }


   public void run()
   {
                                 // construct input packet to be filled
      byte[] buffer = new byte[4096];
      DatagramPacket inPack = new DatagramPacket(buffer, buffer.length);


      while(true)
      {
         try {
            inPack.setLength(buffer.length);
                              // This will block for multicast traffic
            mySocket.receive(inPack);
         }
         catch (java.io.IOException e) {
            
         }
         
         banner(myStream, myHostName, myComment);
         decodeDatagram(myStream, inPack);
      }
   }
   
   public void decodeDatagram(PrintStream stream, DatagramPacket myPack)
   {
      int len = myPack.getLength();
      byte[] buffer = myPack.getData();

      stream.println("Received at: " + DateFormat.getDateTimeInstance(DateFormat.FULL,DateFormat.FULL).format(new Date(System.currentTimeMillis())));
      
      if (len == 0)
      {
         stream.println("Received: Empty Packet.");
         return;
      }
      if ((len > 0) && (len < 4))
      {
         stream.println("Received: ["+buffer[0]+"]...");
      }
      if (len >= 4) {
         stream.println( "Received: ["
                         +(Integer.toHexString(0xff&buffer[0]))
                         +"] ["
                         +(Integer.toHexString(0xff&buffer[1]))
                         +"] ["
                         +(Integer.toHexString(0xff&buffer[2]))
                         +"] ["
                         +(Integer.toHexString(0xff&buffer[3]))
                         +"] ...");
      }
      
      stream.println("Received from: " + myPack.getAddress().toString() + ":" + myPack.getPort());
      stream.println("Received size:" + myPack.getLength());
      stream.println("Received begins at offset: " + myPack.getOffset());
   }


}


