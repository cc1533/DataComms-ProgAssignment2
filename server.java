import java.io.*;
import java.net.*;
import java.util.Formatter;

public class server
{
	public static void main(String args[]) throws IOException
	{
		InetAddress emulatorName = InetAddress.getByName(args[0]);
		int receiveFromEmulator = Integer.parseInt(args[1]);
		int sendToEmulator = Integer.parseInt(args[2]);
		String fileName = args[3];
        	ServerSocket nSocket = new ServerSocket(receiveFromEmulator);
		boolean eot = false;
		
		// Creating UDP connection with Client.
		DatagramSocket servSock = new DatagramSocket(receiveFromEmulator);
		// Creating "received.txt" file.
		Formatter file = new Formatter(fileName);
		
		while(!eot)
		{
			// Receive packet from Client.
			byte[] recData = new byte[30];
			DatagramPacket recPack = new DatagramPacket(recData, recData.length);
			servSock.receive(recPack);
			String received = new String(recPack.getData());

			if(received.contains("XEOF"))
                        {
				file.close();
				servSock.close();
				System.exit(0);
			}
			// Write data received to the file.
			file.format("%s", received);
			// Send Ack to Client.
			String ack = received.toUpperCase();
			byte[] sendAck = ack.getBytes();
			DatagramPacket sendPack = new DatagramPacket(sendAck, sendAck.length, recPack.getAddress(), recPack.getPort());
			servSock.send(sendPack);
		}
	}//end main
}//end server
