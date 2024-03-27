package finalproject;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
			System.out.println("4. Search with Price Range");
			System.out.println("5. Exit");
			searchOption = inputReader.nextLine();

			switch (searchOption) {
			case "1":
				System.out.println("\nEnter city:");
				String searchedCity = inputReader.nextLine();
				boolean isValidInput = validateStringInput(searchedCity);
				if(isValidInput) 
					searchByProperty(propertyList, searchedCity, "city");
				else
					System.out.println("Invalid city name. Please enter a valid city with letters only.");
				break;
			case "2":
				System.out.println("\nEnter province:");
				String searchedProvince = inputReader.nextLine();
				boolean isValidInput2 = validateStringInput(searchedProvince);
				if(isValidInput2) 
					searchByProperty(propertyList, searchedProvince, "province");
				else
					System.out.println("Invalid province name. Please enter a valid province with letters only.");
				break;
			case "3":
				System.out.println("\nEnter pincode:");
				String searchedPincode = inputReader.nextLine();
				boolean isValidInput3 = validatePincodeInput(searchedPincode);
				if(isValidInput3) {
					searchedPincode = searchedPincode.replace(" ", "");
					searchByProperty(propertyList, searchedPincode, "pincode");
				}
				else
					System.out.println("Invalid Pincode. Please enter a valid Canadian postal code!");
				break;
			case "4":
				System.out.println("\nEnter the lower price range:");
				String lowerPriceString = inputReader.nextLine();
				boolean isValidInput4 = validatePriceInput(lowerPriceString);
				if(isValidInput4) {
					System.out.println("\nEnter the higher price range:");
					String higherPriceString = inputReader.nextLine();
					isValidInput4 = validatePriceInput(higherPriceString);
					if(isValidInput4) {
						BigDecimal lowerPropertyPrice = new BigDecimal(lowerPriceString);
						BigDecimal higherPropertyPrice = new BigDecimal(higherPriceString);
						searchByPriceRange(propertyList, lowerPropertyPrice, higherPropertyPrice);
					} else
						System.out.println("Invalid price format. Please enter a valid price in numbers only! (Decimal points can be used)");
				}
				else
					System.out.println("Invalid price format. Please enter a valid price in numbers only! (Decimal points can be used)");
				break;
			case "5":
				System.out.println("Exiting...");
				break;
			default:
				System.out.println("Invalid option. Please enter a valid option!");
			}
		} while (!searchOption.equals("5"));

		inputReader.close();

	}

	public static void searchByProperty(JsonArray propertyList, String searchedInput, String searchFactor) {
		int propertyCount = 0;
		for (JsonElement element : propertyList) {
			JsonObject property = element.getAsJsonObject();
			JsonElement cityElement = property.get(searchFactor);
			if (cityElement != null && cityElement.getAsString().equalsIgnoreCase(searchedInput)) {
				DisplayFormatter.printPropertyDetails(property);
				propertyCount++;
			} 
		}
		if(propertyCount == 0) {
			System.out.println("No property found with the provided "+searchFactor+"!");
		}
	}

	public static void searchByPriceRange(JsonArray propertyList, BigDecimal lowerPriceInput, BigDecimal higherPriceInput) {
		int propertyCount = 0;
		for (JsonElement element : propertyList) {
			JsonObject property = element.getAsJsonObject();
			JsonElement priceElement = property.get("price");
			if (priceElement != null && priceElement.isJsonPrimitive()) {
				BigDecimal propertyPrice = priceElement.getAsBigDecimal();
				if (propertyPrice.compareTo(lowerPriceInput) >= 0 && propertyPrice.compareTo(higherPriceInput) <= 0) {
					DisplayFormatter.printPropertyDetails(property);
					propertyCount++;
				}
			}
		}
		if (propertyCount == 0) {
			System.out.println("No property found in the provided price range!");
		}
	}

	public static boolean validateStringInput(String userInput) {
		String stringRegex = "^[a-zA-Z]+(?:[\\s-][a-zA-Z]+)*$";
		// Compile the regex pattern
		Pattern pattern = Pattern.compile(stringRegex);
		// Match the user input against the regex pattern
		Matcher matcher = pattern.matcher(userInput);
		if (matcher.matches()) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean validatePincodeInput(String userInput) {
		// Regular expression for validating Canadian postal codes
		String postalCodeRegex = "^[A-Za-z]\\d[A-Za-z]\\s?\\d[A-Za-z]\\d$";
		Pattern pattern = Pattern.compile(postalCodeRegex);
		Matcher matcher = pattern.matcher(userInput);
		if (matcher.matches()) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean validatePriceInput(String userInputPrice) {
		// Regular expression for validating price (allows positive decimal numbers)
		String priceRegex = "^\\d+(\\.\\d+)?$";
		Pattern pattern = Pattern.compile(priceRegex);
		Matcher matcher = pattern.matcher(userInputPrice);
		if (matcher.matches()) {
			return true;
		} else {
			return false;
		}
	}
}
