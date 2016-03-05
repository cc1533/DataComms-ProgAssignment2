TODO:
========
1. Fix the "EOFException" error in server.java, this should make the packets get to the server correctly.  
2. Implement window size correctly in client.java, currently just a static window for testing but it needs to be a dynamically changing window.  
3. Make server.java send acks and its other requirements by checking the packet type for new packets.  
  * I'm thinking this can be done easily enough with some if-else statements that check the packet.getType().  
4. Add ack support for client.java so it knows what server.java received or didn't receive.  
  * Resending packets can be added later.
5. Add a global timer to client.java that keeps track of the last unacked packet sent to server.java.  
6. Add a way for server.java to tell if it got a packet out of sequence order and drop it if it was wrong.  
7. Implement go-back-n protocol (oh boy...).  
8. Make sure program correctly handles missing packets.  
9. Hopefully the program is done by this point...  

To edit this file with the correct style:  https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet

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
Goals:
  1. Hopefully fix (1) on the TODO list.  
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
