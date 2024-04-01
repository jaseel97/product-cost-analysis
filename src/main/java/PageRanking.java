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
        Gson gson = new Gson();
        JsonArray propertyList = null;
        try (FileReader reader = new FileReader("src/main/resources/CombinedProperties.json")) {
            propertyList = gson.fromJson(reader, JsonArray.class);
        } catch (Exception e) {
            System.out.println("Error : Could not find json with scraped property data!");
            return;
        }
        InvertedIndex index = new InvertedIndex();
        index.buildIndexFromJSON(propertyList);

        JsonArray rankedProperties = PageRanking.rankProperties(index.get("toronto"));
        int ctr = 0;
        while (ctr < Math.min(20,rankedProperties.size())){
            System.out.println(rankedProperties.get(ctr).toString());
            ctr++;
        }
	}
	public static JsonArray rankProperties(JsonArray allProperties) {
        JsonArray rankedProperties = new JsonArray();
        PriorityQueue<JsonObject> heap = new PriorityQueue<>(allProperties.size(), (x, y) -> {
            int searchFreqX = x.get("search_frequency").getAsInt();
            int searchFreqY = y.get("search_frequency").getAsInt();
            if (searchFreqY != searchFreqX) {
                return searchFreqY - searchFreqX;
            }
            BigDecimal priceX = x.get("price").getAsBigDecimal();
            BigDecimal priceY = y.get("price").getAsBigDecimal();
            return priceX.compareTo(priceY);
        });
        for(JsonElement property : allProperties) {
            heap.add(property.getAsJsonObject());
        }
        while(!heap.isEmpty()){
            rankedProperties.add(heap.remove());
        }
        return rankedProperties;
    }
}