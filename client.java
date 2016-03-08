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
*	6) DONE -- Get the window working.
*	7) Set up timer for last packet with no ack.
*	8) Set up seqNum logs and Ack logs for client.
*	9) If there's time, clean everything up and try to use methods instead of having everything in main.
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

		// Create UDP connections between client and emulator, they say "toServer" and "fromServer" for simplicity.
		DatagramSocket toServer = new DatagramSocket();
		DatagramSocket fromServer = new DatagramSocket(receiveFromEmulator);



		// Setting up variables to be used in the while loop.
                String[] fileSubString = new String[8];
		int payloadSize = 30;
                int i = 0;
                boolean endOfFile = false;
		int exAckNum = 0;
		
		// Change this to work for final ack instead of endOfFile.
		while(!endOfFile)
		{
			exAckNum = 0;	// reset exAckNum every new window.
			//boolean ack = false;
			
			// This for-loop splits the fileString into segments of the given payloadSize.  As long as i is less than the length of
			// 	the fileString - the payloadSize, it is not the final packet of the transmission.
			// Else, it is the final packet and sets the endOfFile variable accordingly.
			int windowSize = 7;
			for(int j = 0; j <= windowSize; j++)
			{
				// for all packets before the end of the file.
				if(i < (fileString.length() - payloadSize))
				{
					fileSubString[j] = fileString.substring(i, i+payloadSize);
					i += payloadSize;
				}
				// For the final packet to the end of the file.
				else if(i >= (fileString.length() - payloadSize))
				{
					fileSubString[j] = fileString.substring(i, fileString.length());
					endOfFile = true;
				}
				// if the current packet is the end of file packet, all packets after this one don't contain any information.
				if(endOfFile)
				{
					System.out.println("End of File found.");
					for(int k = j + 1; k <= windowSize; k++)
					{
						fileSubString[k] = "";
						j++;
					}
				}
			}

			// Sends all the packets to the server one after another.
			for(int j = 0; j <= windowSize; j++)
			{
				packet payLoad = new packet(1,j,fileSubString[j].length(),fileSubString[j]);
				// Serialize packet
				ByteArrayOutputStream baStream = new ByteArrayOutputStream();
				ObjectOutputStream ooStream = new ObjectOutputStream(baStream);
				ooStream.writeObject(payLoad);
				ooStream.flush();
				byte[] sendData = baStream.toByteArray();
				System.out.println("Sending:  ");
				payLoad.printContents();
				DatagramPacket packet = new DatagramPacket(sendData, sendData.length, emulatorName, sendToEmulator);
				toServer.send(packet);
				// global timer should start here with a loop looking for the ack after the for loop
			}

			for(int j = 0; j <= windowSize; j++)
			{
				byte[] ackFromServer = new byte[1024];
				DatagramPacket recAck = new DatagramPacket(ackFromServer, ackFromServer.length);
				fromServer.receive(recAck);
				byte[] ackD = recAck.getData();
				ByteArrayInputStream bais = new ByteArrayInputStream(ackD);
				ObjectInputStream ois = new ObjectInputStream(bais);
				// obtained ack from server, test ack seqNums
				try
				{
					packet ackP = (packet) ois.readObject();
					ois.close();
					if(exAckNum == ackP.getSeqNum())
					{
						//reset timer for next packet
						exAckNum++;
					}
					else
					{
						System.out.println("Unexpected ack from server.");
						// resets i to start at the beginning of the missing packet *I think*.
						i = i - (payloadSize * (8 - ackP.getSeqNum()));
						// breaks out of the for-loop
						j = windowSize + 1;
					}
				} catch(ClassNotFoundException e)
				{
					e.printStackTrace();
				}
			}

			
			// If the endOfFile packet is sent, create and send the EOT packet
			if(endOfFile)
			{
				// This will basically be its own mini sender and receiver that uses a while-loop to wait for ack.
				//String endOfFileS = "XEOF";
				packet eofpayLoad = new packet(3,0,0,null);
				ByteArrayOutputStream eofStream = new ByteArrayOutputStream();
				ObjectOutputStream ooeofStream = new ObjectOutputStream(eofStream);
				ooeofStream.writeObject(eofpayLoad);
				ooeofStream.flush();
				byte[] eofSig = eofStream.toByteArray();
				DatagramPacket sendEOF = new DatagramPacket(eofSig, eofSig.length, emulatorName, sendToEmulator);
				toServer.send(sendEOF);
				// Start timer and look for ack of EOT packet from server

			}
		}//end while
		toServer.close();	// Connections terminated.
		fromServer.close();
	}//end main
}//end client
