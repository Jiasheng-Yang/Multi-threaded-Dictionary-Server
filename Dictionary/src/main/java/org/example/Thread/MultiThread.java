package org.example.Thread;

import org.example.DictClient.Client;
import org.example.DictServer.*;
import org.example.Config;

import java.io.*;
import java.net.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jiasheng Yang
 * @studentID 1464801
 * @email jiasyang@student.unimelb.edu.au
 */
public class MultiThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    Socket client;
    int id;
    Server server;

    public MultiThread(Socket client, int count, Server server) {
        this.client = client;
        this.server = server;
        this.id = count;
    }

    @Override
    public void run() {
        try (BufferedReader isr = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF-8"));
             PrintWriter msg = new PrintWriter(new OutputStreamWriter(client.getOutputStream(), "UTF-8"), true)) {

            String input;
            while ((input = isr.readLine()) != null) {
                System.out.println(this.id + " connection with client(" + this.client + ") with message: " + input);

                String[] tokens = input.split(",");
                String command = tokens.length > 0 ? tokens[0] : "";
                String response = "";

                switch (command) {
                    case "add":
                        if (tokens.length > 2) {
                            String word = tokens[1];
                            String meaning = tokens[2];
                            response = addNewWord(word, meaning);
                        } else {
                            response = "Invalid command: To add a word, please use the format <word,meaning>. The comma is missing in your input.";
                        }
                        break;
                    case "search":
                        if (tokens.length > 1) {
                            String word = tokens[1];
                            response = search(word);
                        }
                        break;
                    case "update":
                        if (tokens.length > 2) {
                            String word = tokens[1];
                            String newMeaning = tokens[2];
                            response = update(word, newMeaning);
                        } else {
                            response = "Invalid command: To update a word, please use the format <update,word,newMeaning>. The comma is missing in your input.";
                        }
                        break;
                    case "delete":
                        if (tokens.length > 1) {
                            String word = tokens[1];
                            response = delete(word);
                        }
                        break;
                    default:
                        response = "Invalid command";
                        break;
                }
                msg.println(response);
            }
        } catch (SocketException e) {
            logger.error("A socket error occurred: {}", e.getMessage());
        } catch (IOException e) {
            logger.error("An I/O error occurred: {}", e.getMessage());
        }
    }

    public synchronized JSONObject load(String filePath) throws IOException {
        JSONObject jsonObject = null;
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader(filePath));
            jsonObject = (JSONObject) obj;
        } catch (ParseException e) {
            logger.error("Failed to parse JSON file: " + e.getMessage());
        }
        return jsonObject;
    }

    public String search(String word) {
        try {
            JSONObject dictionary = load(Config.FILEPATH);

            Object item = dictionary.get(word);
            if (item != null) {

                if (item instanceof JSONArray) {
                    JSONArray meanings = (JSONArray) item;
                    StringBuilder allMeanings = new StringBuilder();
                    for (Object meaning : meanings) {
                        if (allMeanings.length() > 0) {
                            allMeanings.append("; ");
                        }
                        allMeanings.append(meaning.toString());
                    }
                    return allMeanings.toString();
                } else {
                    return item.toString();
                }
            } else {
                return "Word is not found.";
            }
        } catch (FileNotFoundException e) {
            String FileError = "File not found: " + e.getMessage();
            System.err.println(FileError);
            return FileError;
        } catch (IOException e) {
            String IOError = "An error occurred while loading the 'dictionary.json' file: " + e.getMessage();
            System.err.println(IOError);
            return IOError;
        }
    }

    public synchronized String addNewWord(String word, String meaning) {
        try {
            JSONObject dictionary = load(Config.FILEPATH);
            if (!dictionary.containsKey(word)) {
                dictionary.put(word, meaning);
                FileWriter file = new FileWriter(Config.FILEPATH, false);
                try {
                    file.write(dictionary.toJSONString());
                    file.flush();
                } catch (IOException e) {
                    logger.error("An error occurred while creating the 'dictionary.json' file: {}", e.getMessage(), e);
                    return ("An error occurred while creating the 'dictionary.json' file: " + e.getMessage());
                }
                System.out.println(dictionary);
                return ("Word, [" + word + "], added successfully.");
            } else {
                return "Duplicate: Word already exists. If you want to update its meaning or add more means, please click 'update' button.";
            }
        } catch (FileNotFoundException e) {
            String FileError = "File not found: " + e.getMessage();
            System.err.println(FileError);
            return FileError;
        } catch (IOException e) {
            String IOError = "An error occurred while loading the 'dictionary.json' file: " + e.getMessage();
            System.err.println(IOError);
            return IOError;
        }
    }

    public synchronized String update(String word, String newMeaning) {
        try {
            JSONObject dictionary = load(Config.FILEPATH);

            if (dictionary.containsKey(word)) {
                Object item = dictionary.get(word);
                JSONArray meanings;

                if (item instanceof JSONArray) {
                    meanings = (JSONArray) item;
                } else {
                    meanings = new JSONArray();
                    meanings.add(item);
                }

                if (!meanings.contains(newMeaning)) {
                    meanings.add(newMeaning);
                    dictionary.put(word, meanings);

                    try (FileWriter file = new FileWriter(Config.FILEPATH)) {
                        file.write(dictionary.toJSONString());
                    }
                    return word + "'s new meanings updated successfully.";
                } else {
                    return "Duplicate: the new meaning already exists for the word '" + word + "'.";
                }
            } else {
                return "Word '" + word + "' not found in the dictionary. No update performed.";
            }
        } catch (FileNotFoundException e) {
            String FileError = "File not found: " + e.getMessage();
            System.err.println(FileError);
            return FileError;
        } catch (IOException e) {
            String IOError = "An error occurred while loading the 'dictionary.json' file: " + e.getMessage();
            System.err.println(IOError);
            return IOError;
        }
    }

    public synchronized String delete(String word) {
        try {
            JSONObject dictionary = load(Config.FILEPATH);
            if (dictionary.containsKey(word)) {
                FileWriter file = new FileWriter(Config.FILEPATH, false);
                dictionary.remove(word);
                try {
                    file.write(dictionary.toJSONString());
                    file.flush();
                } catch (IOException e) {
                    logger.error("An error occurred while creating the 'dictionary.json' file: {}", e.getMessage(), e);
                    return ("An error occurred while creating the 'dictionary.json' file: " + e.getMessage());
                }
                System.out.println(dictionary);
                return "Word is deleted successfully.";
            } else {
                return "Word is not found.";
            }
        } catch (FileNotFoundException e) {
            String FileError = "File not found: " + e.getMessage();
            System.err.println(FileError);
            return FileError;
        } catch (IOException e) {
            String IOError = "An error occurred while loading the 'dictionary.json' file: " + e.getMessage();
            System.err.println(IOError);
            return IOError;
        }
    }
}