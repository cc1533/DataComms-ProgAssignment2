import java.io.*;
import java.net.*;
import java.util.Formatter;

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
					//System.out.println("Received Data Packet.");
					// % 8 means the client doens't have to reset the seq #s every time.
					if((exSeqNum % 8) != packet.getSeqNum())
					{
						// drop the packet and send the previous seqNum as an ack
						System.out.println("Packet did not contain the expected sequence #.");
						packet nack = new packet(0,(packet.getSeqNum()), 0, null);
						// copied from client.java
						ByteArrayOutputStream baoStream = new ByteArrayOutputStream();
						ObjectOutputStream ooStream = new ObjectOutputStream(baoStream);
						ooStream.writeObject(nack);
						ooStream.flush();
						outgoingData = baoStream.toByteArray();
						//System.out.println("Sending:  ");
						//nack.printContents();
						DatagramPacket sendNack = new DatagramPacket(outgoingData, outgoingData.length, emulatorName, sendToEmulator);
						toClient.send(sendNack);
						// so that any remaining packets in the window from the client will be dropped too.
						// the next packet seqNum from the client should be 0 because a new window started.
						exSeqNum = 0;
					}
					// if it is the expected seq num, increment seqnum, send ack, add data to file
					else
					{
						System.out.println("Packet contained the expected sequence #, sending ack.");
						received = new String(packet.getData());
						packet.printContents();
						// send ack
						packet ack = new packet(0,(packet.getSeqNum() % 8),0,null);
						ByteArrayOutputStream baoStream = new ByteArrayOutputStream();
						ObjectOutputStream ooStream = new ObjectOutputStream(baoStream);
						ooStream.writeObject(ack);
						ooStream.flush();
						outgoingData = baoStream.toByteArray();
						//System.out.println("Sending:  ");
						//ack.printContents();
						DatagramPacket sendAck = new DatagramPacket(outgoingData, outgoingData.length, emulatorName, sendToEmulator);
						toClient.send(sendAck);
						// Write data received to the file.
						file.format("%s", received);
						exSeqNum++;
					}
				}
				// packet is an EOT from the client
				else if(packet.getType() == 3)
				{
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
		}
	}//end main
}//end server
