package com.example.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer implements Runnable {
    public static final int ServerPort = 8080;
    @Override
    public void run() {
        try {

            ServerSocket serverSocket = new ServerSocket(ServerPort);
            System.out.println("Connecting...");
            while (true) {
                Socket client = serverSocket.accept();
                try {

                    BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    String str = in.readLine();
                    String command= "";
                    if(str.equals("")||str==null) {
                        continue;
                    }
                    PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
                    out.println("Server Received : '" + str + "'");

                    if(str.equals("STOP")) {
                        command = "STOP";
                    }
                    else if(str.equals("BACK")) {
                        command = "BACK";
                    }
                    else {
                        continue;
                    }
                    try{
                        Process ps = Runtime.getRuntime().exec(command);
                        ps.waitFor();
                        ps.destroy();
                    }catch(Exception e){
                        e.printStackTrace();
                    }


                } catch (Exception e) {
                    System.out.println("Error");
                    e.printStackTrace();
                } finally {
                    client.close();
                }
            }

        } catch (Exception e) {
            System.out.println("S: Error");
            e.printStackTrace();
        }

    }
    public static void main(String[] args) {

        Thread ServerThread = new Thread(new SocketServer());
        ServerThread.start();

    }

}

