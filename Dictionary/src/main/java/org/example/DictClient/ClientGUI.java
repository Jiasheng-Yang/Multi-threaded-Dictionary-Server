/*
 * Created by JFormDesigner on Sat Apr 06 16:58:47 AWST 2024
 */

package org.example.DictClient;
import org.example.Config;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import javax.swing.*;
import javax.swing.border.*;

/**
 * @author Jiasheng Yang
 * @studentID 1464801
 * @email jiasyang@student.unimelb.edu.au
 */
public class ClientGUI extends JFrame implements ActionListener {
    private String serverAddress;
    private int port;

    public ClientGUI(String serverAddress, int port) {
        this.serverAddress = serverAddress;
        this.port = port;
        initComponents();
        connect();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeSocket();
                System.out.println("Thanks for using the multi-thread dictionary! \nSocket closed.");
            }
        });
    }

    private Socket socket;
    private BufferedReader isr;
    private BufferedWriter osw;

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        infoPanel = new JPanel();
        welcome = new JLabel();
        hintPane = new JScrollPane();
        hint = new JTextPane();
        instruction = new JLabel();
        searchPanel = new JPanel();
        word = new JTextArea();
        prompt = new JLabel();
        resultPanel = new JPanel();
        meaning = new JTextArea();
        buttonBar = new JPanel();
        search = new JButton();
        add = new JButton();
        update = new JButton();
        delete = new JButton();
        search = new JButton();

        //======== this ========
        setTitle("MultiThread Dictionary");
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setBackground(Color.white);
            dialogPane.setForeground(Color.white);
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setBackground(Color.white);
                contentPanel.setLayout(new GridLayout(3, 0));

                //======== infoPanel ========
                {
                    infoPanel.setBackground(Color.white);

                    //---- welcome ----
                    welcome.setText("Welcome to MultiThread Dictionary!");
                    welcome.setBackground(Color.white);
                    welcome.setForeground(Color.black);
                    welcome.setFont(new Font("Inter", Font.BOLD, 20));
                    welcome.setHorizontalAlignment(SwingConstants.CENTER);
                    welcome.setVerticalAlignment(SwingConstants.TOP);

                    //======== hintPane ========
                    {
                        hintPane.setBorder(null);
                        hintPane.setBackground(Color.white);
                        hintPane.setForeground(Color.black);

                        //---- hint ----
                        hint.setBackground(Color.white);
                        hint.setForeground(Color.black);
                        hint.setDisabledTextColor(Color.black);
                        hint.setBorder(null);
                        hint.setAlignmentY(0.0F);
                        hint.setAlignmentX(0.0F);
                        hint.setText("Hints: To delete or query a word already in the dictionary, enter the data in the input box below, then click the corresponding button." +
                                "For adding a new word to the dictionary, must type <word,meaning>, and then click the button. " +
                                "If you want to add one or more meanings for the same word, , must type <word,meaning>, and then click 'update' button.");
                        hintPane.setViewportView(hint);
                    }

                    //---- instruction ----
                    instruction.setText("Please enter the word or word,meaning in the following text area.");
                    instruction.setBackground(Color.white);
                    instruction.setForeground(Color.black);
                    instruction.setFont(new Font("Times New Roman", Font.ITALIC, 18));
                    instruction.setVerticalTextPosition(SwingConstants.BOTTOM);
                    instruction.setVerticalAlignment(SwingConstants.BOTTOM);
                    instruction.setHorizontalAlignment(SwingConstants.LEFT);

                    GroupLayout infoPanelLayout = new GroupLayout(infoPanel);
                    infoPanel.setLayout(infoPanelLayout);
                    infoPanelLayout.setHorizontalGroup(
                        infoPanelLayout.createParallelGroup()
                            .addComponent(hintPane, GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE)
                            .addGroup(infoPanelLayout.createSequentialGroup()
                                .addGroup(infoPanelLayout.createParallelGroup()
                                    .addComponent(instruction, GroupLayout.PREFERRED_SIZE, 494, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(welcome, GroupLayout.PREFERRED_SIZE, 494, GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))
                    );
                    infoPanelLayout.setVerticalGroup(
                        infoPanelLayout.createParallelGroup()
                            .addGroup(infoPanelLayout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(welcome, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(hintPane, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(instruction, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE))
                    );
                }
                contentPanel.add(infoPanel);

                //======== searchPanel ========
                {
                    searchPanel.setBackground(Color.white);
                    searchPanel.setForeground(Color.white);
                    searchPanel.setBorder(null);
                    searchPanel.setLayout(new BorderLayout());

                    //---- word ----
                    word.setRows(5);
                    word.setBackground(Color.white);
                    word.setForeground(Color.black);
                    word.setLineWrap(true);
                    word.setBorder(LineBorder.createBlackLineBorder());
                    searchPanel.add(word, BorderLayout.NORTH);

                    //---- prompt ----
                    prompt.setText("Output: Meaning(s) or Operation Status");
                    prompt.setBackground(Color.white);
                    prompt.setForeground(Color.black);
                    prompt.setFont(new Font("Times New Roman", Font.ITALIC, 18));
                    searchPanel.add(prompt, BorderLayout.SOUTH);
                }
                contentPanel.add(searchPanel);

                //======== resultPanel ========
                {
                    resultPanel.setBackground(Color.white);
                    resultPanel.setForeground(Color.white);
                    resultPanel.setLayout(new BorderLayout());

                    //---- meaning ----
                    meaning.setBackground(Color.white);
                    meaning.setForeground(Color.black);
                    meaning.setRows(6);
                    meaning.setBorder(LineBorder.createBlackLineBorder());
                    meaning.setDisabledTextColor(Color.black);
                    meaning.setCaretColor(Color.black);
                    meaning.setLineWrap(true);
                    meaning.setEditable(false);
                    resultPanel.add(meaning, BorderLayout.NORTH);
                }
                contentPanel.add(resultPanel);
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(null);
                buttonBar.setBackground(Color.white);
                buttonBar.setLayout(new GridLayout(1, 7));

                //---- search ----
                search.setText("Search");
                search.setBackground(new Color(0xcccccc));
                search.setForeground(new Color(0x333333));
                buttonBar.add(search);
                search.addActionListener(this);

                //---- add ----
                add.setText("Add");
                add.setBackground(new Color(0xcccccc));
                add.setForeground(new Color(0x333333));
                buttonBar.add(add);
                add.addActionListener(this);

                //---- update ----
                update.setText("Update");
                update.setBackground(new Color(0xcccccc));
                update.setForeground(new Color(0x333333));
                buttonBar.add(update);
                update.addActionListener(this);

                //---- delete ----
                delete.setText("Delete");
                delete.setBackground(new Color(0xcccccc));
                delete.setForeground(new Color(0x333333));
                buttonBar.add(delete);
                delete.addActionListener(this);
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JPanel infoPanel;
    private JLabel welcome;
    private JScrollPane hintPane;
    private JTextPane hint;
    private JLabel instruction;
    private JPanel searchPanel;
    private JTextArea word;
    private JLabel prompt;
    private JPanel resultPanel;
    private JTextArea meaning;
    private JPanel buttonBar;
    private JButton search;
    private JButton add;
    private JButton update;
    private JButton delete;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on

    private void connect() {
        try {
            socket = new Socket(this.serverAddress, this.port);
            isr = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            osw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
            SwingUtilities.invokeLater(() -> {
                meaning.setText("Connected to the server successfully!");
            });
        } catch (UnknownHostException e) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, "A host error occurred: " + e.getMessage(), "Host Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            });
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

    private void closeSocket() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error closing socket: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        try {
            String command = event.getActionCommand();
            String inputText = word.getText().trim();

            switch (command) {
                case "Search":
                    osw.write("search," + inputText + "\n");
                    break;
                case "Add":
                    osw.write("add," + inputText + "\n");
                    break;
                case "Update":
                    osw.write("update," + inputText + "\n");
                    break;
                case "Delete":
                    osw.write("delete," + inputText + "\n");
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "Invalid command: " + command, "Command Error", JOptionPane.ERROR_MESSAGE);
                    return;
            }

            osw.flush();
            String serverResponse = isr.readLine();
            SwingUtilities.invokeLater(() -> {
                meaning.setText(serverResponse);
            });
        } catch (IOException e) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, "Error communicating with server: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            });
        }
    }

    public static void main(String[] args) {
        String serverAddress = Config.SERVER_ADDRESS;
        int port = Config.PORT;

        if (args.length >= 2) {
            serverAddress = args[0];
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number provided. Using default port " + Config.PORT + ".");
            }
        } else {
            System.err.println("Not enough arguments provided. Using default server address and port.");
        }

        try {
            final String finalServerAddress = serverAddress;
            final int finalPort = port;
            SwingUtilities.invokeLater(() -> {
                ClientGUI clientGUI = new ClientGUI(finalServerAddress, finalPort);
                clientGUI.setVisible(true);
            });
        } catch (Exception e) {
            System.err.println("Failed to start the client application due to: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
