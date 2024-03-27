package features;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class SearchFrequency {

    private static final String JSON_FILE_PATH = "/Users/parvathijoshi/Documents/ACC/ACCProject/src/searchtrees/Realtor.json";

    public static void main(String[] args) {
        // Load JSON data
        JSONArray properties = loadJSONData(JSON_FILE_PATH);

        Scanner scanner = new Scanner(System.in);
        String searchTerm;
        
        do {
            System.out.print("Enter a city name (type 'exit' to exit): ");
            searchTerm = scanner.nextLine().trim().toLowerCase(); // Convert to lowercase
            if (!searchTerm.equalsIgnoreCase("exit")) {
                updateSearchFrequency(searchTerm, properties);
                saveJSONData(JSON_FILE_PATH, properties);
            }
        } while (!searchTerm.equalsIgnoreCase("exit"));
    }

    private static JSONArray loadJSONData(String filename) {
        JSONParser parser = new JSONParser();
        JSONArray jsonArray = new JSONArray();
        try (FileReader fileReader = new FileReader(filename)) {
            Object obj = parser.parse(fileReader);
            jsonArray = (JSONArray) obj;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    private static void updateSearchFrequency(String searchTerm, JSONArray properties) {
        boolean found = false;
        for (Object obj : properties) {
            JSONObject property = (JSONObject) obj;
            String city = ((String) property.get("city")).toLowerCase(); // Convert to lowercase
            if (city.equals(searchTerm)) {
                found = true;
                int frequency = property.containsKey("search_frequency") ?
                        ((Long) property.get("search_frequency")).intValue() : 0;
                property.put("search_frequency", frequency + 1);
                System.out.println("Frequency of " + searchTerm + ": " + (frequency + 1));
            }
        }
        if (!found) {
            System.out.println("City not found!");
        }
    }

    private static void saveJSONData(String filename, JSONArray jsonArray) {
        try (FileWriter fileWriter = new FileWriter(filename)) {
            jsonArray.writeJSONString(fileWriter);
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
