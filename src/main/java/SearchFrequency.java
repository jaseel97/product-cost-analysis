import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class SearchFrequency {

    private static final String JSON_FILE_PATH = "C:\\Users\\royce\\eclipse-workspace\\ACC_Final_Project\\RealtorProperties.json";

    public static void main(String[] args) {
        // Load JSON data
//        JSONArray properties = loadJSONData(JSON_FILE_PATH);
//
//        Scanner scanner = new Scanner(System.in);
//        String searchTerm;
//        
//        do {
//            System.out.print("Enter a city name (type 'exit' to exit): ");
//            searchTerm = scanner.nextLine().trim().toLowerCase(); // Convert to lowercase
//            if (!searchTerm.equalsIgnoreCase("exit")) {
//                updateSearchFrequency(properties);
//                saveJSONData(JSON_FILE_PATH, properties);
//            }
//        } while (!searchTerm.equalsIgnoreCase("exit"));
    }

//    private static JSONArray loadJSONData(String filename) {
//        JSONParser parser = new JSONParser();
//        JSONArray jsonArray = new JSONArray();
//        try (FileReader fileReader = new FileReader(filename)) {
//            Object obj = parser.parse(fileReader);
//            jsonArray = (JSONArray) obj;
//        } catch (IOException | ParseException e) {
//            e.printStackTrace();
//        }
//        return jsonArray;
//    }

    private static void updateSearchFrequency(JsonObject propertyDetails) {
    	int frequency = propertyDetails.get("search_frequency").getAsInt();
        propertyDetails.addProperty("search_frequency", frequency + 1);
        
        // Get the name of the propertyDetails
        String propertyName = propertyDetails.get("propertyName").getAsString();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileReader reader = new FileReader(JSON_FILE_PATH)) {
            // Parse JSON file into JsonArray
            JsonArray propertiesArray = JsonParser.parseReader(reader).getAsJsonArray();
            if (propertiesArray != null) {
                // Iterate through each property in the array
                for (JsonElement element : propertiesArray) {
                    JsonObject property = element.getAsJsonObject();
                    // Check if the "propertyName" of the property matches the name of propertyDetails
                    if (property.get("propertyName").getAsString().equals(propertyName)) {
                        // Update search frequency in the JsonObject
                        property.addProperty("search_frequency", propertyDetails.get("search_frequency").getAsInt());
                        break; // Exit loop after updating
                    }
                }
            }

            // Write updated JsonArray back to the JSON file
            try (FileWriter writer = new FileWriter(JSON_FILE_PATH)) {
                gson.toJson(propertiesArray, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private static void saveJSONData(String filename, List <PropertyDetails> propertiesList) {
//        try (FileWriter fileWriter = new FileWriter(filename)) {
//        	((JSONArray) propertiesList).writeJSONString(fileWriter);
//            fileWriter.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
    
    public static void call(JsonObject propertyDetail) {
    	updateSearchFrequency(propertyDetail);
    }
}
