import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SearchFrequency {

    private static final String JSON_FILE_PATH = "src/main/resources/ZoloProperties.json";

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
            System.out.println("Error writing search frequency to json");
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
