package features;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class SearchFrequency {

    public static void main(String[] args) {
        // Load JSON data
        JSONArray properties = loadJSONData("/Users/parvathijoshi/Documents/ACC/PropertyCost/src/main/java/features/RealtorProperties.json");

        Scanner scanner = new Scanner(System.in);
        String searchTerm;
        do {
            System.out.print("Enter a city name (type 'terminate' to exit): ");
            searchTerm = scanner.nextLine().trim().toLowerCase(); // Convert to lowercase
            if (!searchTerm.equalsIgnoreCase("terminate")) {
                int updatedFrequency = updateJSONFile(properties, searchTerm);
                if (updatedFrequency != -1) {
                    System.out.println("Frequency of " + searchTerm + " in Toronto properties: " + updatedFrequency);
                } else {
                    System.out.println("City not found!");
                }
            }
        } while (!searchTerm.equalsIgnoreCase("terminate"));
    }

    private static JSONArray loadJSONData(String filename) {
        JSONParser parser = new JSONParser();
        JSONArray jsonArray = new JSONArray();
        try (FileReader reader = new FileReader(filename)) {
            Object obj = parser.parse(reader);
            jsonArray = (JSONArray) obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    private static int updateJSONFile(JSONArray properties, String searchTerm) {
        int totalFrequency = 0;
        for (Object obj : properties) {
            JSONObject property = (JSONObject) obj;
            String city = ((String) property.get("city")).toLowerCase();
            if (city.equals(searchTerm)) {
                long currentFrequency = property.containsKey("search_frequency") ? (long) property.get("search_frequency") : 0;
                property.put("search_frequency", currentFrequency + 1);
                totalFrequency++;
            }
        }
        if (totalFrequency > 0) {
            writeJSONFile(properties, "/Users/parvathijoshi/Documents/ACC/PropertyCost/src/main/java/features/RealtorProperties.json");
        }
        return totalFrequency;
    }

    private static void writeJSONFile(JSONArray properties, String filename) {
        try (FileWriter file = new FileWriter(filename)) {
            file.write(properties.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
