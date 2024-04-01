import com.google.gson.*;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PriceAnalyzer {

    static Map<String, Map<String,Double[]>> priceMetrics;
    public static void main(String[] args){
        analyzeAllListings();
        System.out.println(Arrays.toString(priceMetrics.get("city").get("toronto")));
    }
    public static void analyzeAllListings(){
        // searchfactor-searchvalue-metric
        priceMetrics = new HashMap<>();
        priceMetrics.put("city",new HashMap<String, Double[]>());
        priceMetrics.put("province",new HashMap<String,Double[]>());
        priceMetrics.put("pincode",new HashMap<String,Double[]>());

        Gson gson = new Gson();
        try {
            JsonArray properties = gson.fromJson(new FileReader("src/main/resources/CombinedProperties.json"), JsonArray.class);
            for (JsonElement propertyEle : properties) {
                propertyEle = propertyEle.getAsJsonObject();
                JsonObject property = propertyEle.getAsJsonObject();
                JsonElement price = property.get("price");
                if (price == null) continue;
                JsonElement city = property.get("city");
                double priceValue = price.getAsDouble();
                if (city != null) {
                    String value = city.getAsString().toLowerCase();
                    Double[] metricContainer = priceMetrics.get("city").get(value);
                    Double[] updatedMetrics = updateMetrics(metricContainer, priceValue);
                    priceMetrics.get("city").put(value,updatedMetrics);
                }
                JsonElement province = property.get("province");
                if(province != null) {
                    String value = province.getAsString().toLowerCase();
                    Double[] metricContainer = priceMetrics.get("city").get(value);
                    Double[] updatedMetrics = updateMetrics(metricContainer, priceValue);
                    priceMetrics.get("province").put(value,updatedMetrics);
                }
                JsonElement pincode = property.get("pincode");
                if(pincode != null) {
                    String value = pincode.getAsString().toLowerCase();
                    Double[] metricContainer = priceMetrics.get("city").get(value);
                    Double[] updatedMetrics = updateMetrics(metricContainer, priceValue);
                    priceMetrics.get("pincode").put(value,updatedMetrics);
                }
            }
        }catch (IOException e){
            System.out.println("Error reading property json for analysis.");
        }
    }

    public static Double[] updateMetrics(Double[] metricContainer, Double priceValue){
        if (metricContainer == null){
            metricContainer = new Double[4];
            metricContainer[0] = priceValue;
            metricContainer[1] = 0.0;
            metricContainer[2] = 0.0;
            metricContainer[3] = 0.0; // count so far
        }
        metricContainer[0] = Math.min(metricContainer[0],priceValue);
        metricContainer[1] = ((metricContainer[1]*metricContainer[3])+priceValue)/(metricContainer[3]+1);
        metricContainer[2] = Math.max(metricContainer[2],priceValue);
        metricContainer[3] += 1;

        return metricContainer;
    }
}
