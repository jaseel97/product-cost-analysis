import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class CombineJsonFiles {
    public static void main(String[] args) {
    	Gson gson = new Gson();
    	
		try {
			//parse JSON array of scraped data from Realtor
			JsonArray realtorScrapedData = gson.fromJson(new FileReader("src/main/resources/realtor.json"), JsonArray.class);
			RealtorDataFormatter.call(realtorScrapedData);	
			
			//parse JSON array of scraped data from Zolo
			JsonArray zoloScrapedData = gson.fromJson(new FileReader("src/main/resources/zolo.json"), JsonArray.class);
			ZoloDataFormatter.call(zoloScrapedData);
			
			//parse JSON array of scraped data from Royallepage
			JsonArray royalleScrapedData = gson.fromJson(new FileReader("src/main/resources/royalle.json"), JsonArray.class);
			RoyalDataFormatter.call(royalleScrapedData);
			
		} catch (JsonSyntaxException e) {
			System.out.println("Error parsing JSON data!");
		} catch (JsonIOException e) {
			System.out.println("Error loading JSON file!");
		} catch (FileNotFoundException e) {
			System.out.println("Error in JSON file directory!");
		} catch (Exception e) {
			System.out.println("Unknown error!");
		}
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
            } catch (IOException e) {
            	System.out.println("Error reading from the file!");
            } catch (JSONException e) {
               System.out.println("Error in JSON parsing!");
            } catch (Exception e) {
                System.out.println("Unknown error!");
            }
        }

        // Write combinedArray to a single JSON file
        writeJsonArrayToFile(combinedArray);
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

    private static void writeJsonArrayToFile(JSONArray jsonArray) {
        try (FileWriter fileWriter = new FileWriter("src/main/resources/CombinedProperties.json")) {
            fileWriter.write(jsonArray.toString(4)); // Indent with 4 spaces for better readability
            fileWriter.flush();
        } catch (IOException e) {
            System.out.println("Error writing formatted data to JSON!");
        }
    }
}
