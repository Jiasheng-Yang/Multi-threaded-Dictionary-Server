package org.example.DictServer;

import org.example.Config;
import org.example.Thread.MultiThread;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jiasheng Yang
 * @studentID 1464801
 * @email jiasyang@student.unimelb.edu.au
 */
public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    private final ExecutorService pool;
    private int port;

    public Server(int port) {
        this.port = port;
        this.pool = Executors.newFixedThreadPool(Config.THREAD_POOL_SIZE);
        Dictionary.creatDictionaryJSON();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(this.port)) {
            System.out.println("Server started. Listening on port " + this.port + " ...");

            int clientCount = 0;
            while (true) {
                ++clientCount;
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection from " + clientSocket.getInetAddress().getHostName() + ": " + clientSocket.getPort());
                pool.execute(new MultiThread(clientSocket, clientCount, this));
            }

        } catch (SocketException e) {
            logger.error("A socket error occurred: {}", e.getMessage());
        } catch (IOException e) {
            logger.error("Server encountered an I/O error: {}", e.getMessage());
        } finally {
            pool.shutdown();
        }
    }

    public static void main(String[] args) {
        int port = Config.PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number. Using default port " + port);
            }
        } else {
            System.err.println("Not enough argument provided. Using default port.");
        }
        new Server(port).start();
    }
}

