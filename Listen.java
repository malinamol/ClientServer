package edu.udo.cs.rvs.ssdp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.UUID;


public class Listen implements Runnable  {

    public java.util.UUID uuid;
    public static LinkedList<DatagramPacket> queue = new LinkedList<>();
    public static MulticastSocket multiSocket;
    public static InetAddress address;
    public boolean doStop = false;
    public static boolean flag = true;

    public static void stopRunning()
    {
        flag = false;
    }

    Listen()
    {
        try
        {
            multiSocket = new MulticastSocket(1900);
            address = InetAddress.getByName("239.255.255.250");
            multiSocket.joinGroup(address);
        }

        catch (IOException e)
        {
            System.out.print("Error at connecting");
        }
    }

    /**
     * The function creates a datagram and receives the content through the multisocket, adding it to the processing queue afterwards
     * If flag is false it means the user wants to exit the group
     */

    @Override
    public void run() {

        int count = 0;
        while( multiSocket != null && multiSocket.isBound() && !multiSocket.isClosed() && flag)
        {

            try
            {
                DatagramPacket dataPacket = new DatagramPacket(new byte[multiSocket.getReceiveBufferSize()], multiSocket.getReceiveBufferSize());

                synchronized(multiSocket)
                {
                    multiSocket.receive(dataPacket);
                }


                synchronized (dataPacket)
                {
                    this.queue.add(dataPacket);
                }

            } catch (IOException e ) {
                System.out.println("Problem with receiving datagram");
            }
        }

        if(flag == false) {
            try {
                multiSocket.leaveGroup(address);
                System.out.println("Left group\n");
            } catch (IOException e) {
                System.out.println("Couldn't leave group\n");
            }
        }

    }

    /**
     * Function that creates and sends a datagram
     *
     * @throws IOException
     */
    public static void sendDatagram() throws IOException {
        UUID uuid = UUID.randomUUID();
        String text = "M-SEARCH * HTTP/1.1\n" +
                "S: uuid:"+uuid+"\n" +
                "HOST: 239.255.255.250:1900\n" +
                "MAN: \"ssdp:discover\"\n" +
                "ST: ssdp:all\n";

        byte[] msgToSend = text.getBytes();
        DatagramPacket dataToSend = new DatagramPacket(msgToSend,msgToSend.length, address,1900);

        synchronized (multiSocket)
        {
            multiSocket.send(dataToSend);

        }

    }

}


