package org.example.DictServer;

import org.example.DictClient.Client;

import java.io.FileWriter;
import java.io.IOException;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jiasheng Yang
 * @studentID 1464801
 * @email jiasyang@student.unimelb.edu.au
 */
public class Dictionary {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);
    public static void creatDictionaryJSON() {
        JSONObject dictionary = new JSONObject();

        dictionary.put("abandon", "Give up completely");
        dictionary.put("ability", "The physical or mental power or skill needed to do something");
        dictionary.put("Apple", "Apple.Inc");
        dictionary.put("java", "An island in Indonesia south of Borneo");

        try (FileWriter fileWriter = new FileWriter("dictionary.json")) {
            fileWriter.write(dictionary.toString());
            System.out.println("Created 'dictionary.json' file successfully!");
        } catch (IOException e) {
            logger.error("An error occurred while creating the 'dictionary.json' file: {}", e.getMessage(), e);
        }
    }

}
