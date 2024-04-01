import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SearchFrequency {

    private static final String JSON_FILE_PATH = "src/main/resources/CombinedProperties.json";
    private static final Map<String, JsonObject> propertyMap = new HashMap<>();

    static {
        loadPropertyDetails();
    }

    private static void loadPropertyDetails() {
        try (FileReader reader = new FileReader(JSON_FILE_PATH)) {
            JsonArray propertiesArray = JsonParser.parseReader(reader).getAsJsonArray();
            for (JsonElement element : propertiesArray) {
                JsonObject property = element.getAsJsonObject();
                String propertyName = property.get("propertyName").getAsString();
                propertyMap.put(propertyName, property);
            }
        } catch (IOException e) {
            System.out.println("Error loading property details from JSON file");
        }
    }

    private static void updateSearchFrequency(JsonObject propertyDetails) {
        int frequency = propertyDetails.get("search_frequency").getAsInt();
        propertyDetails.addProperty("search_frequency", frequency + 1);

        String propertyName = propertyDetails.get("propertyName").getAsString();
        propertyMap.put(propertyName, propertyDetails);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(JSON_FILE_PATH)) {
            JsonArray propertiesArray = new JsonArray();
            for (JsonObject property : propertyMap.values()) {
                propertiesArray.add(property);
            }
            gson.toJson(propertiesArray, writer);
            
        } catch (IOException e) {
            System.out.println("Error writing search frequency to JSON file");
        }
    }

    public static void call(JsonObject propertyDetail) {
        updateSearchFrequency(propertyDetail);
    }
}