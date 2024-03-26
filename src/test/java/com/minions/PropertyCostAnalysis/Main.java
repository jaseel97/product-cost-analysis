package finalproject;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Main {

	public static void main(String[] args) {
		 System.out.println("Property Cost Analysis");
         System.out.println("------------------------------------------------------------------------------------------------");
		Gson gson = new Gson();
		
		// Read JSON data into a JsonArray
        JsonArray propertyList = null;
        try (FileReader reader = new FileReader("RealtorProperties.json")) {
            propertyList = gson.fromJson(reader, JsonArray.class);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        
        Scanner inputReader = new Scanner(System.in);
        String searchOption;
        do {
            System.out.println("\nSelect search option:");
            System.out.println("\n1. Search by City");
            System.out.println("2. Search by Province");
            System.out.println("3. Search by Pincode");
            System.out.println("4. Exit");
            searchOption = inputReader.nextLine();
            
            switch (searchOption) {
                case "1":
                    System.out.println("\nEnter city:");
                    String searchedCity = inputReader.nextLine();
                    searchByProperty(propertyList, searchedCity, "city");
                    break;
                case "2":
                    System.out.println("\nEnter province:");
                    String searchedProvince = inputReader.nextLine();
                    searchByProperty(propertyList, searchedProvince, "province");
                    break;
                case "3":
                    System.out.println("\nEnter pincode:");
                    String searchedPincode = inputReader.nextLine();
                    searchByProperty(propertyList, searchedPincode, "pincode");
                    break;
                case "4":
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        } while (!searchOption.equals("4"));
        
        inputReader.close();

	}

	public static void searchByProperty(JsonArray propertyList, String searchedInput, String searchFactor) {
        for (JsonElement element : propertyList) {
            JsonObject property = element.getAsJsonObject();
            JsonElement cityElement = property.get(searchFactor);
            if (cityElement != null && cityElement.getAsString().equalsIgnoreCase(searchedInput)) {
                DisplayFormatter.printPropertyDetails(property);
            }
        }
    }
}
