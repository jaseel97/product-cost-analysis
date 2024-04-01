import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class CombineJsonFiles {
    public static void main(String[] args) {
        // Paths to the JSON files
        String[] filePaths = {"src/main/resources/RealtorProperties.json", "src/main/resources/ZoloProperties.json", "src/main/resources/RoyalleProperties.json"};

        // Combined JSON array to hold all objects
        JSONArray combinedArray = new JSONArray();

        // Set to keep track of unique IDs
        Set<String> uniqueIds = new HashSet<>();

        // Read each JSON file, parse its contents, and append unique objects to combinedArray
        for (String filePath : filePaths) {
            try {
                JSONArray jsonArray = readJsonArrayFromFile(filePath);

                // Append only unique objects to combinedArray
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String id = jsonObject.getString("id");

                    if (!uniqueIds.contains(id)) {
                        combinedArray.put(jsonObject);
                        uniqueIds.add(id);
                    }
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }

        // Write combinedArray to a single JSON file
        writeJsonArrayToFile(combinedArray, "src/main/resources/CombinedProperties.json");
    }

    private static JSONArray readJsonArrayFromFile(String filePath) throws IOException, JSONException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        StringBuilder content = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            content.append(line);
        }
        reader.close();

        return new JSONArray(content.toString());
    }

    private static void writeJsonArrayToFile(JSONArray jsonArray, String filePath) {
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(jsonArray.toString(4)); // Indent with 4 spaces for better readability
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
