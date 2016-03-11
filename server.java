import java.io.*;
import java.net.*;
import java.util.Formatter;
import java.io.Serializable.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class server
{	// java server localhost 3000 3001 output.txt
    public static void main(String args[]) throws IOException
    {
        InetAddress emulatorName = InetAddress.getByName(args[0]);
        int receiveFromEmulator = Integer.parseInt(args[1]);
        int sendToEmulator = Integer.parseInt(args[2]);
        String fileName = args[3];
        ServerSocket nSocket = new ServerSocket(receiveFromEmulator);
        //int packSize = 37;
        boolean eot = false;
        
        // Arrival Log file
        Logger arrivalLog = Logger.getLogger("arrival");
        FileHandler fhArrival;
        
        try {
            
            // This block configure the logger with handler and formatter
            fhArrival = new FileHandler("%h/Downloads/arrival.log"); // save the log file
            arrivalLog.addHandler(fhArrival);
            SimpleFormatter formatter = new SimpleFormatter();
            fhArrival.setFormatter(formatter);

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        // Creating UDP connection with Emulator.
        DatagramSocket fromClient = new DatagramSocket(receiveFromEmulator);
        DatagramSocket toClient = new DatagramSocket();
        // Creating "received.txt" file.
        Formatter file = new Formatter(fileName);
        // using this byte array fixed the EOFException error I was getting.
        byte[] incomingData = new byte[1024];
        byte[] outgoingData = new byte[1024];
        // expected sequence number of the first packet is always 0.
        int exSeqNum = 0;
        while(!eot)
        {
            // Receive packet from Client.
            DatagramPacket recPack = new DatagramPacket(incomingData, incomingData.length);
            fromClient.receive(recPack);
            // Deserialize packet
            byte[] data = recPack.getData();
            ByteArrayInputStream baiStream = new ByteArrayInputStream(data);
            ObjectInputStream oiStream = new ObjectInputStream(baiStream);
            // Initialize the string to jump over any errors.
            String received = new String();
            try
            {
                packet packet = (packet) oiStream.readObject();
                oiStream.close();
                // if the packet is a data packet
                if(packet.getType() == 1)
                {
                    //System.out.println("Received packet, testing.");
                    // % 8 means the client doens't have to reset the seq #s every time.
                    if((exSeqNum % 8) == packet.getSeqNum())
                    {
                        //if (packet.getLength() != 0)
                        //{
                            // Update Arrival log file
                            arrivalLog.info(Integer.toString(packet.getSeqNum()%8));
                        //}
                        
                        // If the packet contained the expected seq #, send ack to server, inc exSeqNum
                        received = new String(packet.getData());
                        
                        // send ack
                        //packet.printContents();
                        packet ack = new packet(0,(packet.getSeqNum()),0,null);
                        ByteArrayOutputStream baoStream = new ByteArrayOutputStream();
                        ObjectOutputStream ooStream = new ObjectOutputStream(baoStream);
                        ooStream.writeObject(ack);
                        ooStream.flush();
                        outgoingData = baoStream.toByteArray();
                        DatagramPacket sendAck = new DatagramPacket(outgoingData, outgoingData.length, emulatorName, sendToEmulator);
                        toClient.send(sendAck);
                        // Write data received to the file.
                        file.format("%s", received);
                        exSeqNum++;
                    }
                    else // if the exSeqNum != packet.getSeqNum()
                    {
                        int ackNum = 0;
                        // fixes the issue where -1 was being sent as an ack #.
                        if(exSeqNum == 0){ackNum = 0;}
                        else{ackNum = (exSeqNum % 8) - 1;}
                        packet ack = new packet(0,ackNum,0,null);
                        ByteArrayOutputStream baoStream = new ByteArrayOutputStream();
                        ObjectOutputStream ooStream = new ObjectOutputStream(baoStream);
                        ooStream.writeObject(ack);
                        ooStream.flush();
                        outgoingData = baoStream.toByteArray();
                        DatagramPacket sendAck = new DatagramPacket(outgoingData, outgoingData.length, emulatorName, sendToEmulator);
                        toClient.send(sendAck);
                        exSeqNum = 0;
                    }
                }
                // packet is an EOT from the client
                else if(packet.getType() == 3)
                {
                    System.out.println("Eot received, closing file and connections.\n");
                    eot = true;
                    file.close();		// Close output file.
                    fromClient.close();	// Close UDP socket.
                    toClient.close();
                }
                else
                {
                    System.out.println("Error, unexpected packet type sent to server.\n");
                }
            } catch (ClassNotFoundException e)
            {
                e.printStackTrace();
            }
        }//end while
    }//end main
}//end server