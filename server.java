import java.io.*;
import java.net.*;
import java.util.Formatter;
import java.io.Serializable.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class server
{	// java server localhost 6002 6000 output.txt
	public static void main(String args[]) throws IOException
	{
		InetAddress emulatorName = InetAddress.getByName(args[0]);
		int receiveFromEmulator = Integer.parseInt(args[1]);
		int sendToEmulator = Integer.parseInt(args[2]);
		String fileName = args[3];
        	ServerSocket nSocket = new ServerSocket(receiveFromEmulator);
		//int packSize = 37;
		boolean eot = false;

		/*// Arrival Log file
        	Logger arrivalLog = Logger.getLogger("arrival");
        	FileHandler fhArrival;
        	try
		{
            		// This block configure the logger with handler and formatter
            		fhArrival = new FileHandler("%h/Downloads/arrival.log"); // save the log file
            		arrivalLog.addHandler(fhArrival);
            		SimpleFormatter formatter = new SimpleFormatter();
            		fhArrival.setFormatter(formatter);
        	}
		catch (SecurityException e)
		{
            		e.printStackTrace();
        	}
		catch (IOException e) 
		{
            		e.printStackTrace();
        	}*/
		
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
		String[] fileStrings = new String[8];
		while(!eot)
		{
			//fileStrings[exSeqNum % 8] = "";
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
					System.out.println("Expected Seq #: " + (exSeqNum % 8) + " -- Received Seq #:  " + packet.getSeqNum());
					// % 8 means the client doens't have to reset the seq #s every time.
					if((exSeqNum % 8) == packet.getSeqNum())
					{
						//if (packet.getLength() != 0)
                        			//{
                            				// Update Arrival log file
                            				//arrivalLog.info(Integer.toString(packet.getSeqNum()%8));
                        			//}
						// If the packet contained the expected seq #, send ack to server, inc exSeqNum
						received = new String(packet.getData());
						// send ack
						//packet.printContents();
						packet ack = new packet(0,packet.getSeqNum(),0,null);
						ByteArrayOutputStream baoStream = new ByteArrayOutputStream();
						ObjectOutputStream ooStream = new ObjectOutputStream(baoStream);
						ooStream.writeObject(ack);
						ooStream.flush();
						outgoingData = baoStream.toByteArray();
						DatagramPacket sendAck = new DatagramPacket(outgoingData, outgoingData.length, emulatorName, sendToEmulator);
						//System.out.println("Sending Ack to Client.");
						toClient.send(sendAck);
						// Write data received to the file.
						fileStrings[exSeqNum % 8] = packet.getData();
						//file.format("%s", received);
						//exSeqNum++;
						if((exSeqNum % 8) == 7)
						{
							//System.out.println("All packets received, adding to file.");
							for(int j = 0; j <= (exSeqNum % 8); j++)
							{
								//System.out.println("Adding -- \"" + fileStrings[j] + "\" -- to file.");
								file.format("%s", fileStrings[j]);
							}
							exSeqNum = 0;
						}
						else{exSeqNum++;}
					}
					else // if the exSeqNum != packet.getSeqNum()
					{
						int ackNum = 0;
						// fixes the issue where -1 was being sent as an ack #.
						/*if(exSeqNum == 0){ackNum = 0;}
						else{ackNum = (exSeqNum % 8) - 1;}*/
						ackNum = (exSeqNum % 8) - 1;
						packet ack = new packet(0,ackNum,0,null);
						ByteArrayOutputStream baoStream = new ByteArrayOutputStream();
						ObjectOutputStream ooStream = new ObjectOutputStream(baoStream);
						ooStream.writeObject(ack);
						ooStream.flush();
						outgoingData = baoStream.toByteArray();
						DatagramPacket sendAck = new DatagramPacket(outgoingData, outgoingData.length, emulatorName, sendToEmulator);
						toClient.send(sendAck);
						exSeqNum = 0;
						/*
							I need to find a way to break out of this try-catch but not the while.
							I think the reason the output.txt is so different from the test file is because
							even after an error detection (this block) the server continues to receive packets
							and it assumes 
						*/
					}
				}
				// packet is an EOT from the client
				else if(packet.getType() == 3)
				{
					System.out.println("Eot received, closing file and connections.");
					eot = true;
					file.close();		// Close output file.
					fromClient.close();	// Close UDP socket.
					toClient.close();
				}
				else
				{
					System.out.println("Error, unexpected packet type sent to server.");
				}
			} catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			}
		}//end while
	}//end main
}//end server

