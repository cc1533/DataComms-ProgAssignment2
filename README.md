TODO:
========
1. DONE -- cc1533 -- Fix the "EOFException" error in server.java, this should make the packets get to the server correctly.  
2. DONE -- cc1533 -- Implement window size correctly in client.java, currently just a static window for testing but it needs to be a dynamically changing window.  
3. DONE -- cc1533 -- Make server.java send acks and its other requirements by checking the packet type for new packets.  
  * I'm thinking this can be done easily enough with some if-else statements that check the packet.getType().  
4. DONE -- cc1533 -- Add ack support for client.java so it knows what server.java received or didn't receive.  
  * Resending packets can be added later.
5. Add a global timer to client.java that keeps track of the last unacked packet sent to server.java.  
6. DONE -- cc1533 -- Add a way for server.java to tell if it got a packet out of sequence order and drop it if it was wrong.  
7. IN PROGRESS -- Implement go-back-n protocol (oh boy...).  
8. DONE -- cc1533 -- Make sure program correctly handles missing packets.  
9. Add all the required .log files to client.java and server.java, shouldn't be too difficult.  
10. Hopefully the program is done by this point...  

To edit this file with the correct style:  https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet  
Need help learning Java code? try to find what you specifically need help with here: https://www.youtube.com/playlist?list=PL17E300C92CE0261A  
I'm currently coding with gedit on Ubuntu 15.10.  If you're on Windows, I suggest the IntelliJ Java IDE but you'll have to manually set the arguments when your program runs.  

Changelog:
=========

3/4/2016  
  Chris:  
1. Worked on both client.java and server.java  
2. added serialization to client.java and deserialization to server.java  
3. added support for packet class that Mr. Young included  
4. Encountered "EOFException" error in server.java, unsure of how to fix, quit for the night  
5. Tested client.java to make sure it was sending data correctly using 4 bytes, everything looked good from tests.  

3/5/2016  
  Chris:  
1. Solved the "EOFException" error in server.java, everything is now received by the server perfectly.  
2. Server.java now uses packet.getType() and packet.getSeqNum() to tell what type of packet it is receiving and whether or not that packet is the sequence number of the packet it is expecting.  
3. Server.java sends nacks and acks depending on whether or not it received the packet it was expecting.  
4. Client.java does not currently support handling acks or nacks, add this later.  
5. Made some minor changes to server.java that should fix a possible future issue when we start sending more than the 8 packets we're currently sending.  
Goals:
  1. DONE -- Hopefully fix (1) on the TODO list.  
  2. Work on (2) of the TODO list.  

3/6/2016  
Chris:  
Unfortunately, I was unable to work on anything today. Church, family, etc. I'll try to get some things done tomorrow.  

3/7/2016  
Chris:  
1. Got the sliding window working, it now sends the whole file and terminates correctly when the EOT packet is received by the server. This doesn't currently use any acks as that is a part of the GBN protocol.  
2. Will begin working on GBN and Acks.  
3. Tried testing .java files using the emulator program. Broke something. Now the server doesn't receive anything. I won't upload any code until this is resolved. I have begun the basic outline of how the GBN will work though.  
Goals:
  1. Work on Sliding Window (2). Maybe implement the client ack system (4) here?  May be necessary...  
  2. Begin thinking through how the GBN protocol will be implemented (7).  
  3. Completing these 2 will get the program down to just the timer for the GBN protocol left to be implemented and some intense testing to make sure that everything works as it's supposed to.  

3/8/2016  
Chris:  
1. Fixed the thing I broke yesterday. client.java and server.java now send across the emulator successfully.  
2. Working on getting GBN protocol working. So far the GBN (mostly) works. About 60% of the file gets sent successfully with the drop probability of the emulator set to .2 (20%). I'm not entirely sure why the rest of the file is missing, I'll figure this out later. But I'll go ahead and upload the code anyway.  
3. Unfortunately couldn't get back to working on the GBN protocol. I'll figure it out tomorrow.  
Goals:
  1. Fix server.java to receive packets correctly again.  
  2. Test client.java and server.java using the emulator to make sure the transfer works correctly.  
  3. Fully implement the GBN protocol.  
  4. Start thinking how the timer is going to work.  

3/9/2016  
Chris:  
1. Worked on GBN all day. The client now recognizes (based on acks) if the packet made it to the server or if an ack was dropped. However, there's some error in the way I'm handling missing packets. It's starting the new window at the wrong place and screwing up everything else. The program basically requires that the last window of a file drops no packets. The whole window must get to the server else the program will not shut down. This is required and makes perfect sense but also means that the last part of a file may be sent dozens of times and added to the file.  
Goals:
  1. Get GBN fully working.  
  2. Figure out how to get the timers implemented.  
  3. Log files?  

3/10/2016  
Chris:  
1. Worked extensively on both client.java and server.java. It will almost always terminate and save the output file. GBN is still not fully functional but I have an idea of what the problem could be. If I can figure out how to solve that problem, then it should be totally done.  

Joe:  
1. I edited the code to create the log files for the sequence number, acks, and arrival. However, I believe that the arrival and the ack log files are not implemented correctly. I'm going to try to fix those issues tomorrow and work on the timer portion if I get a chance.

2. Oh and you might have to change the path where you save the log files in your code (lines 58 and 75 in the client and line 29 for the server).

Goals:
  1. Finish GBN.  
  2. Timers?
  3. Log files?

3/11/2016  
Chris:  
Goals:
  1. Finish GBN.
  2. Make the client terminate from the EOT ack sent from the server.
  3. Get Log files working.
  4. Get timer working.  
  * Hopefully done. I'm going to Florida for Spring Break from Sat. - Mon. so I won't be able to work any.  

3/12/2016  
Chris:  

3/13/2016  
Chris:  

3/14/2016  
Chris:  
1. Got home at about 11:00PM from FL. Had to scamble to get the client working to where it exits via an EOT ack from the server. Couldn't work on it any more. Had to turn it in as it is.  
Goals:
  * Fix any remaining issues and turn in.  
