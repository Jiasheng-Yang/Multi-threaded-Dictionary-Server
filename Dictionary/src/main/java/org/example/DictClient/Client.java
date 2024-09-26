package org.example.DictClient;

import org.example.Config;

import java.io.*;
import java.util.*;
import java.net.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jiasheng Yang
 * @studentID 1464801
 * @email jiasyang@student.unimelb.edu.au
 */
public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);
    private String serverAddress;
    private int port;

    public Client(String serverAddress, int port) {
        this.serverAddress = serverAddress;
        this.port = port;
        try (Socket socket = new Socket(serverAddress, port)) {
            System.out.println("Welcome to MultiThread Dictionary!");
            System.out.println("Congrats! Successfully connected the dictionary server!");
            sendMessage(socket);
        } catch (UnknownHostException e) {
            logger.error("A host error occurred: {}", e.getMessage());
        } catch (SocketException e) {
            logger.error("A socket error occurred: {}", e.getMessage());
        } catch (IOException e) {
            logger.error("An I/O error while connecting the server: {}", e.getMessage());
        }
    }

    public static void sendMessage(Socket socket) {
        try (BufferedReader isr = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
             BufferedWriter osw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
             PrintWriter msg = new PrintWriter(osw, true);
             Scanner scan = new Scanner(System.in)) {

            boolean flag = false;
            while(!flag) {
                System.out.print("Enter a command with <word> or <word,meaning> (type 'exit' to quit): ");
                String input = scan.nextLine();
                if (input.equals("exit")) {
                    flag = true;
                } else {
                    msg.println(input);
                    osw.flush();
                    String response = isr.readLine();
                    System.out.println("Server response: " + response);
                }
            }
            System.out.println("Thanks for using the multi-thread dictionary!");
        } catch (IOException e) {
            logger.error("A socket error occurred: {}", e.getMessage());
        }
    }

    public static void main(String[] args) {
        String serverAddress = Config.SERVER_ADDRESS;
        int port = Config.PORT;
        if (args.length > 1) {
            serverAddress = args[0];
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number. Using default port " + port);
            }
        }
        new Client(serverAddress, port);
    }
}
