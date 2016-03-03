/***********************************************************************************************************************************************
*	Created by Christopher Cole (cc1533) on 3/1/2016.
*	CSE 4153, Programming Assignment 2, client.java
*
* TODO for whole project
*	1) DONE -- Get basic UDP connection to server set up.
*	2) DONE -- Get basic file transfer working.
*	3) Figure out Go-Back-N protocol and get it working.
*	4) Get serialization and deserialization working.
*	5) Send file by packets class.
*	6) (A part of #3) Get the window working.
*	7) Set up timer for last packet with no ack.
*
************************************************************************************************************************************************/

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class client
{
	public static void main(String args[]) throws IOException
	{
		InetAddress emulatorName = InetAddress.getByName(args[0]);
		int sendToEmulator = Integer.parseInt(args[1]);
		int receiveFromEmulator = Integer.parseInt(args[2]);
		String fileName = args[3];

		// Set up File stuff.
		Scanner scanFile = new Scanner(new File(fileName));
		String fileString = "";			
		// Copy all the file's contents into a string, the string is what will be used and modified.
		while(scanFile.hasNext())
		{
			fileString += scanFile.nextLine() + "\n";   // "\n" added because .nextLine() leaves it off by default.
		}
		scanFile.close();

		// Create UDP connection with the received port # from the Server.
		DatagramSocket clientSocketU = new DatagramSocket();

		// Set up I/O stuff for the server.
		byte[] sendData;   // Blah, redundancy.
		byte[] recData;

		// Setting up variables to be used in the while loop.
                String[] fileSubString = new String[7];
		int payloadSize = 30;
                int i = 0;
                boolean endOfFile = false;    
	
		// Change this to work for final ack instead of endOfFile.
		while(!endOfFile)
		{
			boolean ack = false;
			int j = i;
/**********************************************************************************************************************************************
	//This whole block of code needs to be changed to accommodate the serialization of multiple packets.
			if(i < fileString.length() - payloadSize)
			{
				fileSubString = fileString.substring(i, i + payloadSize);
				i += payloadSize;
			}
			// This is for the final segment that includes the EOF.
			else if(i >= fileString.length() - payloadSize)
			{
				fileSubString = fileString.substring(j, fileString.length());
				fileSubString = fileSubString.replace("\n","");
				endOfFile = true;
			}
**********************************************************************************************************************************************/

			// Maybe start by trying to get just a window size # of packets sent to the server.
			// Have a timer set for the last unacked packet sent.
			// 

			packet payLoad = new packet(1,0,30,fileSubString[0]);

			// Send payload to server
			sendData = fileSubString[0].getBytes();
			DatagramPacket sendPack = new DatagramPacket(sendData, sendData.length, emulatorName, sendToEmulator);
			clientSocketU.send(sendPack);

			// Clean out the crap from recData.
			recData = new byte[1];

			
			// If the endOfFile packet is sent, create and send the EOT packet
			if(endOfFile)
			{
				// This will basically be its own mini sender and receiver that uses a while-loop to wait for ack.
				String endOfFileS = "XEOF";
				byte[] eofSig = endOfFileS.getBytes();
				DatagramPacket sendEOF = new DatagramPacket(eofSig, eofSig.length, emulatorName, sendToEmulator);
				clientSocketU.send(sendEOF);
			}
		}//end while
		// Done with UDP connection, close the socket.
		clientSocketU.close();
	}//end main
}//end client
