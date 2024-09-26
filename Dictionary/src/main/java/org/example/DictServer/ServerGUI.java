/*
 * Created by JFormDesigner on Fri Mar 29 13:49:36 AEDT 2024
 */

package org.example.DictServer;

import org.example.Config;
import org.example.Thread.MultiThread;
import org.example.DictServer.*;

import java.net.SocketException;
import java.util.List;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.*;
import javax.swing.border.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jiasheng Yang
 * @studentId 1464801
 * @email jiasyang@student.unimelb.edu.au
 */
public class ServerGUI extends JFrame implements ActionListener {

    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    private final ExecutorService pool;
    private int port;
    private int clientCount = 0;
    private Socket clientSocket;
    private List clientDetails = new ArrayList<>();

    public ServerGUI(int port) {
        initComponents();
        this.port = port;
        this.pool = Executors.newFixedThreadPool(Config.THREAD_POOL_SIZE);
        Thread thread = new Thread(() -> serverStart(new Server(port)));
        thread.start();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        general = new JPanel();
        welcome = new JLabel();
        hints = new JTextArea();
        buttonBar = new JPanel();
        clientButton = new JButton();
        detailsButton = new JButton();
        clients = new JPanel();
        clientInfo = new JTextArea();
        prompt = new JLabel();

        //======== this ========
        setTitle("MultiThread Dcitionary Server");
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(5, 5, 5, 5));
            dialogPane.setBackground(Color.white);
            dialogPane.setForeground(Color.white);
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setBackground(Color.white);
                contentPanel.setForeground(Color.white);
                contentPanel.setLayout(new BorderLayout());

                //======== general ========
                {
                    general.setBackground(Color.white);
                    general.setForeground(Color.white);
                    general.setLayout(new BorderLayout());

                    //---- welcome ----
                    welcome.setText("Welcome to MultiThread Dictionary Server!");
                    welcome.setBackground(Color.white);
                    welcome.setForeground(Color.black);
                    welcome.setHorizontalAlignment(SwingConstants.CENTER);
                    welcome.setFont(new Font("Inter", Font.BOLD, 19));
                    welcome.setVerticalAlignment(SwingConstants.BOTTOM);
                    general.add(welcome, BorderLayout.NORTH);

                    //---- hints ----
                    hints.setBackground(Color.white);
                    hints.setForeground(Color.black);
                    hints.setRows(4);
                    hints.setText("Hints: From this window, you can check the connection information or each clients including index, local port, remote port, and host.");
                    hints.setLineWrap(true);
                    hints.setFont(new Font("Inter", Font.PLAIN, 13));
                    hints.setEditable(false);
                    general.add(hints, BorderLayout.CENTER);

                    //======== buttonBar ========
                    {
                        buttonBar.setBorder(null);
                        buttonBar.setBackground(Color.white);
                        buttonBar.setForeground(Color.white);
                        buttonBar.setLayout(new GridLayout(1, 0, 280, 0));

                        //---- clientButton ----
                        clientButton.setText("Clients");
                        clientButton.setBackground(new Color(0xcccccc));
                        clientButton.setForeground(new Color(0x333333));
                        buttonBar.add(clientButton);
                        clientButton.addActionListener(this);

                        //---- detailsButton ----
                        detailsButton.setText("Details");
                        detailsButton.setBackground(new Color(0xcccccc));
                        detailsButton.setForeground(new Color(0x333333));
                        buttonBar.add(detailsButton);
                        detailsButton.addActionListener(this);
                    }
                    general.add(buttonBar, BorderLayout.SOUTH);
                }
                contentPanel.add(general, BorderLayout.PAGE_START);

                //======== clients ========
                {
                    clients.setLayout(new GridLayout());

                    //---- clientInfo ----
                    clientInfo.setBorder(LineBorder.createBlackLineBorder());
                    clientInfo.setRows(10);
                    clientInfo.setBackground(Color.white);
                    clientInfo.setLineWrap(true);
                    clientInfo.setForeground(Color.black);
                    clientInfo.setEditable(false);
                    clients.add(clientInfo);
                }
                contentPanel.add(clients, BorderLayout.PAGE_END);

                //---- prompt ----
                prompt.setText("Clients' connection information shown following ...");
                prompt.setBackground(Color.white);
                prompt.setForeground(Color.black);
                prompt.setVerticalAlignment(SwingConstants.BOTTOM);
                prompt.setFont(new Font("Times New Roman", Font.ITALIC, 16));
                contentPanel.add(prompt, BorderLayout.CENTER);
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JPanel general;
    private JLabel welcome;
    private JTextArea hints;
    private JPanel buttonBar;
    private JButton clientButton;
    private JButton detailsButton;
    private JPanel clients;
    private JTextArea clientInfo;
    private JLabel prompt;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on

    public void serverStart(Server server) {
        try (ServerSocket serverSocket = new ServerSocket(this.port)) {
            System.out.println("Server started. Listening on port " + this.port + " ...");

            while (true) {
                clientSocket = serverSocket.accept();
                ++clientCount;
                int remotePort = clientSocket.getPort();
                int localPort = clientSocket.getLocalPort();
                String hostName = clientSocket.getInetAddress().getHostName();
                String allClients = "Client "+ clientCount + " connected from " + hostName + "(" + localPort + "): Remote Port " + remotePort;

                synchronized (clientDetails) {
                    clientDetails.add(allClients);
                }

                pool.execute(new MultiThread(clientSocket, clientCount, server));
            }
        } catch (SocketException e) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, "A socket error occurred: " + e.getMessage(), "Socket Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            });
        } catch (IOException e) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, "An I/O error while connecting the server: " + e.getMessage(), "I/O Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            });
        }
    }


    @Override
    public void actionPerformed(ActionEvent event) {
        SwingUtilities.invokeLater(() -> {
            switch (event.getActionCommand()) {
                case "Clients":
                    clientInfo.setText(clientCount + " clients have connected.");
                    break;
                case "Details":
                    StringBuilder details = new StringBuilder();
                    synchronized (clientDetails) {
                        for(int i = 0; i < clientDetails.size(); i++) {
                            String detail = (String) clientDetails.get(i);
                            details.append(detail).append("\n");
                        }
                    }
                    clientInfo.setText(details.toString());
                    break;
            }
        });;
    }

    public static void main(String[] args) {
        int port = Config.PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number provided. Using default port " + Config.PORT);
            }
        } else {
            System.err.println("Not enough argument provided. Using default port.");
        }

        final int finalPort = port;
        SwingUtilities.invokeLater(() -> {
            ServerGUI gui = new ServerGUI(finalPort);
            gui.setVisible(true);
        });
    }
}
