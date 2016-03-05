/***********************************************************************************************************************************************
*	Created by Christopher Cole (cc1533) on 3/1/2016.
*	CSE 4153, Programming Assignment 2, client.java
*
* TODO for whole project
*	1) DONE -- Get basic UDP connection to server set up.
*	2) DONE -- Get basic file transfer working.
*	3) Figure out Go-Back-N protocol and get it working.
*	4) DONE -- Get serialization and deserialization working.
*	5) DONE -- Send file by packets class.
*	6) (A part of #3) Get the window working.
*	7) Set up timer for last packet with no ack.
*
************************************************************************************************************************************************/

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class client
{	// java client localhost 3000 3001 test.txt
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



		// Setting up variables to be used in the while loop.
                String[] fileSubString = new String[8];
		int payloadSize = 30;
                int i = 0;
                boolean endOfFile = false;    
		
		// Change this to work for final ack instead of endOfFile.
		while(!endOfFile)
		{
			i = 0;
			boolean ack = false;
			
			// create mock-up window
			// for now this just does the first 8 packets of the file
			int windowSize = 7;
			for(int j = 0; j <= windowSize; j++)
			{
				fileSubString[j] = fileString.substring(i, i+payloadSize);
				i += payloadSize;
			}

			// Sends all the packets to the server one after another.
			int sent = 0;
			for(int j = 0; j <= windowSize; j++)
			{
				packet payLoad = new packet(1,j,payloadSize,fileSubString[j]);
				// Serialize packet
				ByteArrayOutputStream baStream = new ByteArrayOutputStream();
				ObjectOutputStream ooStream = new ObjectOutputStream(baStream);
				ooStream.writeObject(payLoad);
				ooStream.flush();
				byte[] sendData = baStream.toByteArray();
				System.out.println("Sending:  ");
				payLoad.printContents();
				DatagramPacket packet = new DatagramPacket(sendData, sendData.length, emulatorName, sendToEmulator);
				clientSocketU.send(packet);
				// global timer should start here with a loop looking for the ack after the for loop
				// eof = true just for testing, should be taken out.
				endOfFile = true;
			}

			// Clean out the crap from recData.
			byte[] recData = new byte[1];

			
			// If the endOfFile packet is sent, create and send the EOT packet
			if(endOfFile)
			{
				// This will basically be its own mini sender and receiver that uses a while-loop to wait for ack.
				String endOfFileS = "XEOF";
				packet eofpayLoad = new packet(3,0,payloadSize,endOfFileS);
				ByteArrayOutputStream eofStream = new ByteArrayOutputStream();
				ObjectOutputStream ooeofStream = new ObjectOutputStream(eofStream);
				ooeofStream.writeObject(eofpayLoad);
				ooeofStream.flush();
				byte[] eofSig = eofStream.toByteArray();
				DatagramPacket sendEOF = new DatagramPacket(eofSig, eofSig.length, emulatorName, sendToEmulator);
				clientSocketU.send(sendEOF);
			}
		}//end while
		// Done with UDP connection, close the socket.
		clientSocketU.close();
	}//end main
}//end client
