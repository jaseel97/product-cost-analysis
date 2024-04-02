import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.PriorityQueue;

public class PageRanking {
	public static void main(String [] args) throws Exception, JsonIOException, FileNotFoundException {
        Gson gson = new Gson();  // gson object for JSON parsing
        JsonArray propertyList = null; //variable JsonArray to hold property data
        try (FileReader reader = new FileReader("src/main/resources/CombinedProperties.json")) {
            propertyList = gson.fromJson(reader, JsonArray.class); // Parsing JSON file into a JsonArray using Gson
        } catch (Exception e) {
            System.out.println("Error : Could not find json with scraped property data!");
            return;
        }
//        InvertedIndex index = new InvertedIndex(); // InvertedIndex class instance
//        index.buildIndexFromJSON(propertyList); // Building inverted index from the parsed JSON property data
        InvertedIndexContainer.initIndices(propertyList);

        JsonArray rankedProperties = PageRanking.rankProperties(InvertedIndexContainer.indices[0].get("toronto"));
        int ctr = 0;
        while (ctr < Math.min(20,rankedProperties.size())){
            System.out.println(rankedProperties.get(ctr).toString());
            ctr++;
        }
	}
	
	// Method for ranking properties based on search frequency and price
	public static JsonArray rankProperties(JsonArray allProperties) {
        JsonArray rankedProperties = new JsonArray(); // JsonArray to keep ranked properties
        PriorityQueue<JsonObject> heap = new PriorityQueue<>(allProperties.size(), (x, y) -> { // Priority queue to prioritize properties
            int searchFreqX = x.get("search_frequency").getAsInt(); // Getting search frequency of property X
            int searchFreqY = y.get("search_frequency").getAsInt(); // Getting search frequency of property Y
            if (searchFreqY != searchFreqX) {
                return searchFreqY - searchFreqX;
            }
            BigDecimal priceX = x.get("price").getAsBigDecimal();  // Getting price of property X
            BigDecimal priceY = y.get("price").getAsBigDecimal();  // Getting price of property Y
            return priceX.compareTo(priceY);  // Comparing the prices of properties
        });
        for(JsonElement property : allProperties) {  // Looping through all properties and adding them to priority queue
            heap.add(property.getAsJsonObject());
        }
        while(!heap.isEmpty()){
            rankedProperties.add(heap.remove());
        }
        return rankedProperties;
    }
}