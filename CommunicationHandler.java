package org.example;

import java.io.*;
import java.net.Socket;

public class CommunicationHandler implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private ServerGUI serverGUI;

    public CommunicationHandler(Socket socket, ServerGUI gui) {
        this.socket = socket;
        this.serverGUI = gui;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            serverGUI.appendToLog("Error: " + e.getMessage());
        }
    }

    public void run() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                serverGUI.appendToLog("Client: " + message);
                serverGUI.broadcastMessage("Client: " + message);
            }
        } catch (IOException e) {
            serverGUI.appendToLog("Connection error: " + e.getMessage());
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }
}
