package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class ServerGUI extends JFrame {
    private JTextArea logArea;
    private JTextField inputField;
    private JButton sendButton;
    private JButton startButton;
    private ServerSocket serverSocket;
    private List<CommunicationHandler> clients = new ArrayList<>();

    public ServerGUI() {
        setTitle("Server GUI");
        setSize(400, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        logArea = new JTextArea();
        logArea.setEditable(false);
        inputField = new JTextField();
        sendButton = new JButton("Send");
        startButton = new JButton("Start Server");

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        add(new JScrollPane(logArea), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        add(startButton, BorderLayout.NORTH);

        sendButton.addActionListener(e -> {
            String message = inputField.getText();
            if (!message.isEmpty()) {
                appendToLog("Server: " + message);
                broadcastMessage("Server: " + message);
                inputField.setText("");
            }
        });

        startButton.addActionListener(e -> startServer());

        setVisible(true);
    }

    public void startServer() {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(8888);
                appendToLog("Server started on port 8888");

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    appendToLog("Client connected: " + clientSocket.getInetAddress());
                    CommunicationHandler handler = new CommunicationHandler(clientSocket, this);
                    clients.add(handler);
                    new Thread(handler).start();
                }
            } catch (Exception e) {
                appendToLog("Error: " + e.getMessage());
            }
        }).start();
    }

    public void appendToLog(String message) {
        SwingUtilities.invokeLater(() -> logArea.append(message + "\n"));
    }

    public void broadcastMessage(String message) {
        for (CommunicationHandler client : clients) {
            client.sendMessage(message);
        }
    }
}
