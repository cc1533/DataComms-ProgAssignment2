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
		int packSize = 4;
		boolean eot = false;
		
		// Creating UDP connection with Client.
		DatagramSocket servSock = new DatagramSocket(receiveFromEmulator);
		// Creating "received.txt" file.
		Formatter file = new Formatter(fileName);
		
		while(!eot)
		{
			// Receive packet from Client.
			byte[] recData = new byte[packSize];
			DatagramPacket recPack = new DatagramPacket(recData, recData.length);
			servSock.receive(recPack);
			// Deserialize packet
			ByteArrayInputStream baStream = new ByteArrayInputStream(recData);
			//System.out.println(baStream);
			ObjectInputStream oiStream = new ObjectInputStream(baStream);
			// Initialize the string to jump over any errors.
			String received = new String();
			try
			{
				// I keep getting an "EOFException" here... idk what's going on.
				packet packet = (packet) oiStream.readObject();
				received = new String(packet.getData());
				packet.printContents();
			} catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			}
			

			// Test for eof signal
			if(received.contains("XEOF"))
                        {
				file.close();
				servSock.close();
				System.exit(0);
			}
			// Write data received to the file.
			file.format("%s", received);
			// Send Ack to Client.
			/*String ack = received.toUpperCase();
			byte[] sendAck = ack.getBytes();
			DatagramPacket sendPack = new DatagramPacket(sendAck, sendAck.length, recPack.getAddress(), recPack.getPort());
			servSock.send(sendPack);*/
		}
	}//end main
}//end server
