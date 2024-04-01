import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PageRanking {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the city to search for properties: ");
        String city = scanner.nextLine();

        String jsonFilePath = "src/main/resources/CombinedProperties.json"; // Path to your JSON file

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

            // Convert list to array for heap sort
            JSONObject[] propertiesArray = new JSONObject[cityProperties.size()];
            cityProperties.toArray(propertiesArray);

            // Sort properties by search frequency and then by price using heap sort
            heapSort(propertiesArray);

            // Output ranked properties
            if (propertiesArray.length > 0) {
                System.out.println("Ranked properties in " + city + ":");
                for (JSONObject property : propertiesArray) {
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
            System.out.println("Error in page ranking.");
        } finally {
            scanner.close();
        }
    }

    // Heap sort implementation
    private static void heapSort(JSONObject[] arr) {
        int n = arr.length;

        // Build max heap
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(arr, n, i);
        }

        // Heap sort
        for (int i = n - 1; i > 0; i--) {
            // Swap root (max element) with the last element
            JSONObject temp = arr[0];
            arr[0] = arr[i];
            arr[i] = temp;

            // Heapify root element
            heapify(arr, i, 0);
        }
    }

    // To heapify a subtree rooted with node i which is an index in arr[]
    private static void heapify(JSONObject[] arr, int n, int i) {
        int largest = i; // Initialize largest as root
        int left = 2 * i + 1;
        int right = 2 * i + 2;

        // If left child is larger than root
        if (left < n && Long.parseLong(arr[left].get("search_frequency").toString()) > Long.parseLong(arr[largest].get("search_frequency").toString())) {
            largest = left;
        }

        // If right child is larger than largest so far
        if (right < n && Long.parseLong(arr[right].get("search_frequency").toString()) > Long.parseLong(arr[largest].get("search_frequency").toString())) {
            largest = right;
        }

        // If largest is not root
        if (largest != i) {
            JSONObject swap = arr[i];
            arr[i] = arr[largest];
            arr[largest] = swap;

            // Recursively heapify the affected sub-tree
            heapify(arr, n, largest);
        }
    }
}