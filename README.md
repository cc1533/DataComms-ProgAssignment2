TODO:
========
1. DONE -- cc1533 -- Fix the "EOFException" error in server.java, this should make the packets get to the server correctly.  
2. Implement window size correctly in client.java, currently just a static window for testing but it needs to be a dynamically changing window.  
3. DONE -- cc1533 -- Make server.java send acks and its other requirements by checking the packet type for new packets.  
  * I'm thinking this can be done easily enough with some if-else statements that check the packet.getType().  
4. Add ack support for client.java so it knows what server.java received or didn't receive.  
  * Resending packets can be added later.
5. Add a global timer to client.java that keeps track of the last unacked packet sent to server.java.  
6. DONE -- cc1533 -- Add a way for server.java to tell if it got a packet out of sequence order and drop it if it was wrong.  
7. Implement go-back-n protocol (oh boy...).  
8. Make sure program correctly handles missing packets.  
9. Hopefully the program is done by this point...  

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

3/7/2016  
Chris:  

3/8/2016  
Chris:  

3/9/2016  
Chris:  

3/10/2016  
Chris:  

3/11/2016  
Chris:  

3/12/2016  
Chris:  

3/13/2016  
Chris:  

3/14/2016  
Chris:  
