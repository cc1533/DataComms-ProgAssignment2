JFLAGS = -g
JCC = javac
default:  client.class server.class

client.class:  client.java
	$(JCC) $(JFLAGS) client.java

server.class:  server.java
	$(JCC) $(JFLAGS) server.java

packet.class:  packet.java
	$(JCC) $(JFLAGS) packet.java

clean:
	$(RM) client.class server.class output.txt
