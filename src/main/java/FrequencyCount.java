import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class FrequencyCount {
	//Method to count the frequency of attribute values within the JSON array and returns a Map where keys are strings and values are integers.
    public static Map<String, Integer> countInJson(JsonArray jsonArray, String attributeName) {
        if (jsonArray.isEmpty() || attributeName.isEmpty()) {
            throw new IllegalArgumentException("The provided JSON array is empty or the attribute name is empty.");
        }		// Checking whether the JSON array is  empty or the attribute name is empty, Checks for invalid input and throws error message.

        Map<String, Integer> countMap = new HashMap<>(); // HashMap instance to store the frequency count of attribute values present in the JSON array.
        for (JsonElement elementnJson : jsonArray) {
            if (elementnJson.isJsonObject()) {
                JsonObject objectnJson = elementnJson.getAsJsonObject();
                JsonElement attributename = objectnJson.get(attributeName);
                if (!attributename.isJsonNull()) {
                    String valueFAttribute = attributename.getAsString();
                    countMap.merge(valueFAttribute, 1, Integer::sum);
//                    if (atributcount == null) {
//                        countMap.put(valueFAttribute, 1);
//                    } else {
//                        countMap.put(valueFAttribute, atributcount + 1);
//                    }
                }
            }
        }
        return countMap;
    }
    
    // Method takes input of JsonArray and returns cityfrequencycount map and provincefrequencycount map
    public static Map<String, Integer> frequencyOfCityAndProvince(JsonArray ArrayOfJson) {
    	try {
            // Calls countInJson Method for city and province respectively
            Map<String, Integer> mapOfCityFrequencyCount = countInJson(ArrayOfJson, "city");
            Map<String, Integer> mapOfProvinceFrequencyCount = countInJson(ArrayOfJson, "province");

            // Merge the frequency counts of city and province
            return mergeMaps(mapOfCityFrequencyCount, mapOfProvinceFrequencyCount);
        } catch (IllegalArgumentException e) {
            // Print error message if exception occurs
            System.err.println("There is an error: " + e.getMessage());
            return new HashMap<>(); // Returning an empty map
        }
    }
    
    // Method to merge two maps
    private static Map<String, Integer> mergeMaps(Map<String, Integer> cityCountMap, Map<String, Integer> provinceCountMap) {
        Map<String, Integer> finalMap = new HashMap<>(cityCountMap); // HashMap instance with mapOfCityFrequencyCount contents
        for (Map.Entry<String, Integer> entry : provinceCountMap.entrySet()) { // Loop iterating over mapOfProvinceFrequencyCount
        	finalMap.merge(entry.getKey(), entry.getValue(), Integer::sum); // Each entry from the mapOfProvinceFrequencyCount is merged into the finalMap and if the key already exists in finalMap, the values are summed
        }
        return finalMap;
    }
}