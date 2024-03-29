package features;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class PageRanking {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the city to search for properties: ");
        String city = scanner.nextLine();

        String jsonFilePath = "/Users/parvathijoshi/Documents/ACC/PropertyCost/src/main/java/features/RealtorProperties.json"; // Path to your JSON file

        try {
            // Parse JSON file
            JSONParser parser = new JSONParser();
            JSONArray properties = (JSONArray) parser.parse(new FileReader(jsonFilePath));

            // Filter properties by city
            List<JSONObject> cityProperties = new ArrayList<>();
            for (Object obj : properties) {
                JSONObject property = (JSONObject) obj;
                if (property.get("city").equals(city)) {
                    cityProperties.add(property);
                }
            }

            // Sort properties by search frequency and then by price
            cityProperties.sort((p1, p2) -> {
                int freqComparison = Long.compare((Long) p2.get("search_frequency"), (Long) p1.get("search_frequency"));
                if (freqComparison != 0) {
                    return freqComparison;
                } else {
                    return Long.compare((Long) p1.get("price"), (Long) p2.get("price"));
                }
            });

            // Output ranked properties
            if (!cityProperties.isEmpty()) {
                System.out.println("Ranked properties in " + city + ":");
                for (JSONObject property : cityProperties) {
                    System.out.println("Property Name: " + property.get("propertyName"));
                    System.out.println("Price: $" + property.get("price"));
                    System.out.println("Bedrooms: " + property.get("bedrooms"));
                    System.out.println("Bathrooms: " + property.get("bathrooms"));
                    System.out.println("Description: " + property.get("description"));
                    System.out.println("Search Frequency: " + property.get("search_frequency"));
                    System.out.println();
                }
            } else {
                System.out.println("No properties found in " + city);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}
