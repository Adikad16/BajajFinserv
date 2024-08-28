package com.example.hashgenerator;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class HashGenerator {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar DestinationHashGenerator.jar <PRN Number> <JSON File Path>");
            return;
        }

        String prnNumber = args[0]; 
        String jsonFilePath = args[1]; 

        String destinationValue = getDestinationValue(jsonFilePath);
        String randomString = generateRandomString();

        String concatenatedString = prnNumber + destinationValue + randomString;
        String hash = generateMD5Hash(concatenatedString);

        System.out.println(hash + ";" + randomString);
    }

    private static String getDestinationValue(String jsonFilePath) {
        try (FileReader reader = new FileReader(jsonFilePath)) {
            JsonElement jsonElement = JsonParser.parseReader(reader); // Updated line
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            return getDestinationValueRecursive(jsonObject);
        } catch (IOException e) {
            e.printStackTrace();
            return ""; 
        }
    }

    private static String getDestinationValueRecursive(JsonObject jsonObject) {
        for (String key : jsonObject.keySet()) {
            if (key.equals("destination")) {
                return jsonObject.get(key).getAsString(); // Return the value if "destination" is found
            }

            JsonElement value = jsonObject.get(key);
            if (value.isJsonObject()) {
                String destinationValue = getDestinationValueRecursive(value.getAsJsonObject());
                if (!destinationValue.isEmpty()) {
                    return destinationValue;
                }
            } else if (value.isJsonArray()) {
                for (JsonElement element : value.getAsJsonArray()) {
                    if (element.isJsonObject()) {
                        String destinationValue = getDestinationValueRecursive(element.getAsJsonObject());
                        if (!destinationValue.isEmpty()) {
                            return destinationValue;
                        }
                    }
                }
            }
        }

        return ""; 
    }

    private static String generateRandomString() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }

        return sb.toString(); 
    }

    private static String generateMD5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);

            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

            return hashtext; 
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e); 
        }
    }
}