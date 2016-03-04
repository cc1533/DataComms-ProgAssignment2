//
//  client.cpp
//  Prog Assignment 1
//
//  Created by Joe Hall on 1/31/16.
//  Copyright © 2016 Joe Hall. All rights reserved.
//

/*
 ** client.c -- a stream socket client demo
 */

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <arpa/inet.h>
#include <sys/wait.h>
#include <signal.h>
#include <iostream>
#include <ctype.h>
#include <fstream>

using namespace std;

int main(int argc, char *argv[])
{
    
    //Declaring socket
    int mysocket = 0;
    mysocket = socket(AF_INET, SOCK_STREAM, 0);
    //Getting the IP address
    struct hostent *s;
    s = gethostbyname(argv[1]);
    
    //Setting Destination Info
    struct sockaddr_in server;
    memset((char *) &server, 0, sizeof(server));
    server.sin_family = AF_INET;
    server.sin_port = htons(atoi(argv[2]));
    bcopy((char *)s->h_addr, (char *)&server.sin_addr.s_addr, s->h_length);
    
    //Sending Data
    socklen_t slen = sizeof(server);
    connect(mysocket, (struct sockaddr *)&server, slen);
    //cout << "...";
    char payload[512];
    //char payload[] = "118";
    send(mysocket, "117", slen, 0);
    recv(mysocket, (void *)payload, 512, 0);
    //cout << "...";
    cout << "\nRandom Port: " << payload << "\n" << endl;
    //Closing socket
    close(mysocket);
    
    /*-------------UDP-----------*/
    
    //Declaring socket
    int mysocket2 = socket(AF_INET, SOCK_DGRAM, 0);
    if(mysocket2 < 0){
        cout << "error in socket creation\n";
    }
    
    //Getting the IP address
    s = gethostbyname(argv[1]);
    if(s == NULL){
        cout << "failed to set name\n";
    }
    slen = sizeof(server);
    memset((char *) &server, 0, sizeof(server));
    server.sin_family = AF_INET;
    bcopy((char *)s->h_addr, (char *)&server.sin_addr.s_addr, s->h_length);
    server.sin_port = htons(atoi(argv[2]));
    
    //Sending Data
    fstream fin("text.txt", fstream::in);
    int i = 0;

    ifstream myIstream(argv[3]);
    int k = 0;
    for (i = 0; i < 13;  i++){
        char joe[4];
        if(myIstream.is_open()){
        myIstream.read(joe, 4 + k);
            
        if (joe[0] == '!')
        {
            joe[1] = ' ';
            joe[2] = ' ';
            joe[3] = ' ';
        }
            
        if(sendto(mysocket2, joe, sizeof(joe), 0, (struct sockaddr *)&server, sizeof(server)) < 0){
            cout << "Failed to send payload.\n";
        }
    }
    }
    char payload2[512];
    i = 0;
    while(i < 13)
    {
        recvfrom(mysocket2, payload2, 5, 0, (struct sockaddr *)&server, &slen);
        cout << payload2 << endl;
        i++;
    }
    
    //Closing socket
    close(mysocket2);
    
    
}
