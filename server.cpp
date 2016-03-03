//
//  server.cpp
//  Prog Assignment 1
//
//  Created by Joe Hall on 2/2/16.
//  Copyright © 2016 Joe Hall. All rights reserved.
//

/*
 ** server.c -- a stream socket server demo
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
    
    int num = rand() % 1024 + 64511;
    //Declare scoket
    int mysocket = 0;
    mysocket = socket(AF_INET, SOCK_STREAM, 0);
    int mysocket2 = 0;
    mysocket2 = socket(AF_INET, SOCK_STREAM, 0);
    
    //Declaring Destination
    struct sockaddr_in server;
    memset((char *) &server, 0, sizeof(server));
    server.sin_family = AF_INET;
    server.sin_port = htons(5000);
    server.sin_addr.s_addr = htonl(INADDR_ANY);
    
    //Bind it to the address
    bind(mysocket, (struct sockaddr *)&server, sizeof(server));
    listen(mysocket, 5);
    
    //Receiving data from client
    struct sockaddr_in client;
    socklen_t clen = sizeof(client);
    char udpPort[512];
    char payload[512];
    num = 1053;
    sprintf(udpPort, "%d", num);
    mysocket2 = accept(mysocket, (struct sockaddr *)&client, &clen);
    recv(mysocket2, payload, 512, 0);
    send(mysocket2, udpPort, sizeof(udpPort), 0);
    cout << "Output of payload is: " << payload << endl;
    cout << "\nNegotiation detected. Selected random port: " << num << "\n" << endl;
    
    close(mysocket);
    close(mysocket2);
    
    
    /*-------------UDP-----------*/
    
    //Declare scoket
    mysocket2 = 0;
    mysocket2 = socket(AF_INET, SOCK_DGRAM, 0);
    
    //Declaring Destination
    memset((char *) &server, 0, sizeof(server));
    server.sin_family = AF_INET;
    server.sin_port = htons(atoi(argv[1])); //changed to argv[1], stopped working
    server.sin_addr.s_addr = htonl(INADDR_ANY);
    
    //Added this, nothing changed:
    if(mysocket2 < 0){
        cout << "Error in trying to open datagram socket.\n";
        exit(EXIT_FAILURE);
    }
    
    //Bind it to the address
    bind(mysocket2, (struct sockaddr *)&server, sizeof(server));
    clen = sizeof(client);
    char payload2[6];
    memset(payload2, 0, sizeof(payload2));
    for (int i = 0; i < 13; i++)
    {
        recvfrom(mysocket2, payload2, 512, 0, (struct sockaddr *)&client, &clen);
        //cout  << "Received: " << payload2 << endl;
        for(int j = 0; j < clen; j++)
        {
            payload2[j] = toupper(payload2[j]);
            //cout << payload2;
        }
        sendto(mysocket2, payload2, 512, 0, (struct sockaddr *)&client, clen);
        //cout << "Sent" << payload2 << "\n" << endl;
    }
    /*
    int i = 0;
    while(i < 12)
    {
        recvfrom(mysocket2, payload2, 512, 0, (struct sockaddr *)&client, &clen);
        for(int j = 0; j < clen; j++)
        {
            payload2[j] = toupper(payload2[j]);
            cout << payload2;
        }
        sendto(mysocket2, payload2, 512, 0, (struct sockaddr *)&client, clen);
        cout << "Sent" << payload2 << "\n" << endl;
        i++;
    }*/
    
    //if (bind(mysocket2, (struct sockaddr *)&server, sizeof(server)) == -1)
    //    cout << "Error in binding.\n";
    
    /*
    //Receiving data from client
    struct sockaddr_in client;
    socklen_t clen = sizeof(client);
    clen = sizeof(client);
    char payload2[512];
    int i = 0;
    while(i < 12)
    {
        recvfrom(mysocket2, payload2, 512, 0, (struct sockaddr *)&client, &clen);
        for(int j = 0; j < clen; j++)
        {
            payload2[j] = toupper(payload2[j]);
            cout << payload2;
        }
        sendto(mysocket2, payload2, 512, 0, (struct sockaddr *)&client, clen);
        //cout << payload2 << "\n" << endl;
        i++;
    }
     */
    close(mysocket2);
}


