import java.io.FileReader;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


public class Main {

	static final String BACK="!b";
	static final int DISPLAY_BATCH_SIZE = 4;

	public static void main(String[] args) {
		System.out.println("\n\n------------------------------------- Welcome to Property Lens -----------------------------------------------------");
		Gson gson = new Gson();

		// Read JSON data into a JsonArray
		JsonArray propertyList = null;
		try (FileReader reader = new FileReader("src/main/resources/CombinedProperties.json")) {
			propertyList = gson.fromJson(reader, JsonArray.class);
		} catch (Exception e) {
			System.out.println("Error : Could not find json with scraped property data!");
			return;
		}

		InvertedIndex index = new InvertedIndex();
		index.buildIndexFromJSON(propertyList);

		WordCompletion wcTrie = new WordCompletion();
		wcTrie.buildWordCompletionTrie();

		SpellChecker spellChecker = new SpellChecker();
		spellChecker.buildSpellCheckerSplayTree();

		PriceAnalyzer.analyzeAllListings();

		Scanner inputReader = new Scanner(System.in);
		String searchOption;
		do {
			System.out.println("\n\nSelect search option:");
			System.out.println("\n1. Search by City");
			System.out.println("2. Search by Province");
			System.out.println("3. Search by ZIP Code");
			System.out.println("4. Search with Price Range");
			System.out.println("5. Search with City, Price Range, Number of Bedrooms & Bathrooms");
			System.out.println("6. Exit");
			searchOption = inputReader.nextLine();

			switch (searchOption) {
				case "1":
					extractUniqueValues(propertyList, "city");
					boolean isValidCityEntered = false;
					do {
						System.out.println("\nEnter city: (Type "+BACK+" to go back to main menu)");
						String searchedCity = inputReader.nextLine().trim();
						if(searchedCity.equals(BACK))
							break;
						boolean isValidInput = validateStringInput(searchedCity);
						if(isValidInput) {
							isValidCityEntered = searchByProperty(index.get(searchedCity), searchedCity.toLowerCase(), "city", wcTrie, spellChecker);
						}
						else System.out.println("Invalid city name. Please enter a valid city with letters only.");
					}while(!isValidCityEntered);
					break;
				case "2":
					extractUniqueValues(propertyList, "province");
					boolean isValidProvinceEntered = false;
					do {
						System.out.println("\nEnter province:  (Type "+BACK+" to go back to main menu)");
						String searchedProvince = inputReader.nextLine().trim();
						if(searchedProvince.equals(BACK))
							break;
						boolean isValidInput2 = validateStringInput(searchedProvince);
						if(isValidInput2) {
							isValidProvinceEntered = searchByProperty(index.get(searchedProvince), searchedProvince.toLowerCase(), "province", wcTrie, spellChecker);
						}
						else System.out.println("Invalid province name. Please enter a valid province with letters only.");
					}while(!isValidProvinceEntered);
					break;
				case "3":
					boolean isValidPincodeEntered = false;
					do {
						System.out.println("\nExpected: Canadian Pincode Format");
						System.out.println("Example: R2M3Z4 / R2M 3Z4");
						System.out.println("\nEnter ZIP Code: (Type "+BACK+" to go back to main menu)");
						String searchedPincode = inputReader.nextLine().trim();;
						if(searchedPincode.equals(BACK))
							break;
						boolean isValidInput3 = validatePincodeInput(searchedPincode);
						if(isValidInput3) {
							isValidPincodeEntered = true;
							searchedPincode = searchedPincode.replace(" ", "");
							isValidPincodeEntered = searchByProperty(index.get(searchedPincode), searchedPincode.toLowerCase(), "pincode", wcTrie, spellChecker);
						}
						else System.out.println("Invalid Pincode. Please enter a valid Canadian postal code!");
					} while(!isValidPincodeEntered);
					break;
				case "4":
				    boolean isValidLowerPriceEntered = false;
				    do {
				        System.out.println("\nEnter the lower price range: (Type " + BACK + " to go back to the main menu)");
				        String lowerPriceString = inputReader.nextLine().trim();
				        if (lowerPriceString.equals(BACK))
				            break;
				        boolean isValidInput4 = validatePriceInput(lowerPriceString);
				        if (isValidInput4) {
				            isValidLowerPriceEntered = true;
				            boolean isValidHigherPriceEntered = false;
				            do {
				                System.out.println("\nEnter the higher price range:");
				                String higherPriceString = inputReader.nextLine().trim();;
				                isValidInput4 = validatePriceInput(higherPriceString);
				                if (isValidInput4) {
				                    BigDecimal lowerPropertyPrice = new BigDecimal(lowerPriceString);
				                    BigDecimal higherPropertyPrice = new BigDecimal(higherPriceString);
				                    if (higherPropertyPrice.compareTo(lowerPropertyPrice) > 0) {
				                        isValidHigherPriceEntered = true;
				                        searchByPriceRange(propertyList, lowerPropertyPrice, higherPropertyPrice);
				                    } else {
				                        System.out.println("Higher price must be greater than the lower price entered. Please enter a higher price.");
				                    }
				                } else
				                    System.out.println("Invalid price format. Please enter a valid price using positive numbers only! (Decimal points can be used)");
				            } while (!isValidHigherPriceEntered);
				        } else
				            System.out.println("Invalid price format. Please enter a valid price using positive numbers only! (Decimal points can be used)");
				    } while (!isValidLowerPriceEntered);
				    break;
				case "5":
					extractUniqueValues(propertyList, "city");
					boolean validCity = false ,validPrice = false, validBathrooms = false, validBedrooms = false;
					boolean goBack = false;
					String searchedCity2,lowerPriceString,higherPriceString,noOfBedrooms,noOfBathrooms;

					do{
						System.out.println("\nEnter city: (Type "+BACK+" to go back to main menu)");
						searchedCity2 = inputReader.nextLine().trim();
						if(searchedCity2.equals(BACK)) {
							goBack = true;
							break;
						}
						validCity = validateStringInput(searchedCity2);
						if(!validCity) System.out.println("Invalid City!");
					}while(!validCity);
					if(goBack) break;

					do{
						System.out.println("\nEnter the lower price range (Type "+BACK+" to go back to main menu):");
						lowerPriceString = inputReader.nextLine().trim();
						if(lowerPriceString.equals(BACK)) {
							goBack = true;
							break;
						}
						validPrice = validatePriceInput(lowerPriceString);
						if(!validPrice) System.out.println("Invalid price format. Please enter a valid price in numbers only! (Decimal points can be used)");
					}while(!validPrice);
					if(goBack) break;

					do{
						System.out.println("\nEnter the higher price range (Type "+BACK+" to go back to main menu):");
						higherPriceString = inputReader.nextLine().trim();
						if(higherPriceString.equals(BACK)) {
							goBack = true;
							break;
						}
						validPrice = validatePriceInput(higherPriceString);
						if(!validPrice) System.out.println("Invalid price format. Please enter a valid price in numbers only! (Decimal points can be used)");
						else{
							BigDecimal lowerPriceRangeValue = new BigDecimal(lowerPriceString);
							BigDecimal higherPriceRangeValue = new BigDecimal(higherPriceString);
							if(higherPriceRangeValue.compareTo(lowerPriceRangeValue) <= 0){
								validPrice = false;
								System.out.println("The max price should be greater than the min price (Min : "+lowerPriceString+")!");
							}
						}
					}while(!validPrice);
					if(goBack) break;

					do{
						System.out.println("\nEnter number of bedrooms (Type "+BACK+" to go back to main menu):");
						noOfBedrooms = inputReader.nextLine().trim();
						if(noOfBedrooms.equals(BACK)) {
							goBack = true;
							break;
						}
						validBedrooms = isValidInteger(noOfBedrooms);
						if(!validBedrooms) System.out.println("Invalid input. Please enter a valid positive number!");
					}while(!validBedrooms);
					if(goBack) break;

					do{
						System.out.println("\nEnter number of bathrooms (Type "+BACK+" to go back to main menu):");
						noOfBathrooms = inputReader.nextLine().trim();
						if(noOfBathrooms.equals(BACK)) {
							goBack = true;
							break;
						}
						validBathrooms = isValidInteger(noOfBathrooms);
						if(!validBathrooms) System.out.println("Invalid input. Please enter a valid positive number!");
					}while(!validBathrooms);
					if(goBack) break;

					searchByCityPriceBedroomsAndBathrooms(propertyList, searchedCity2, lowerPriceString, higherPriceString, noOfBedrooms, noOfBathrooms);
					break;
				case "6":
					System.out.println("Thank You for using Property Lens!");
					break;
				default:
					System.out.println("Invalid option. Please enter a valid option!");
				}
		} while (!searchOption.equals("6"));
		inputReader.close();
	}
	public static boolean searchByProperty(JsonArray propertyList, String searchedInput, String searchFactor, WordCompletion wcTrie, SpellChecker spellChecker) {
		if (propertyList != null) {
			Map<String, Integer> cityAndProvinceFrequencyCount = FrequencyCount.frequencyOfCityAndProvince(propertyList);
	        for (Map.Entry<String, Integer> entry : cityAndProvinceFrequencyCount.entrySet()) {
	            if (entry.getKey().equalsIgnoreCase(searchedInput)) {
	                System.out.println("\nThere are " + entry.getValue() + " listings in " + searchedInput.substring(0, 1).toUpperCase() + searchedInput.substring(1));
	            }
	        }
			Double[] metrics = PriceAnalyzer.priceMetrics.get(searchFactor).get(searchedInput);
			NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.CANADA);
			System.out.println("\nAverage Price of properties in "+searchedInput.substring(0, 1).toUpperCase() + searchedInput.substring(1)+": "+currencyFormatter.format(metrics[1]));
			System.out.println("Least expensive property in "+searchedInput.substring(0, 1).toUpperCase() + searchedInput.substring(1)+" costs: "+currencyFormatter.format(metrics[0]));
			System.out.println("Most expensive property in "+searchedInput.substring(0, 1).toUpperCase() + searchedInput.substring(1)+" costs: "+currencyFormatter.format(metrics[2]));
			JsonArray rankedList = PageRanking.rankProperties(propertyList);
			displayInBatches(rankedList);
			return true;
		} else {
			System.out.println("Could not find : "+searchedInput);
			List<String> autoCompletedWords = wcTrie.autoCompletion(searchedInput);
			if (autoCompletedWords.isEmpty()) {
				String suggestion = spellChecker.findCorrectedSpelling(searchedInput,searchFactor);
				if (suggestion != null) {
					if(searchFactor.equals("pincode")) System.out.println("Did you mean: " + suggestion.toUpperCase());
					else System.out.println("Did you mean: " + suggestion.substring(0, 1).toUpperCase() + suggestion.substring(1));
				} else
					System.out.println("No property found with the provided " + searchFactor + "!");
			} else {
				System.out.print("Did you mean: ");
				//To print word alone if single suggestion
				if (autoCompletedWords.size() == 1) {
					String completedWord = autoCompletedWords.getFirst();
					System.out.println(completedWord.substring(0, 1).toUpperCase() + completedWord.substring(1));
				} else {
					// to print words separated by a comma if multiple suggestions are present
					for (int i = 0; i < autoCompletedWords.size(); i++) {
						String completedWord = autoCompletedWords.get(i);
						System.out.print(completedWord.substring(0, 1).toUpperCase() + completedWord.substring(1));

						// If it's not the last word, print a comma
						if (i < autoCompletedWords.size() - 1) {
							System.out.print(", ");
						}
					}
				}
			}
		}
		return false;
	}

	public static void displayInBatches(JsonArray properties){
		Scanner sc = new Scanner(System.in);
		int batch_ctr = 0, shown_so_far = 0;
		for (JsonElement element : properties) {
			if(batch_ctr < DISPLAY_BATCH_SIZE) {
				JsonObject property = element.getAsJsonObject();
				SearchFrequency.updateSearchFrequency(property);
				DisplayFormatter.printPropertyDetails(property);
				batch_ctr++;
				shown_so_far++;
			}else {
				System.out.println((properties.size() - shown_so_far) + " properties left");
				System.out.print("Enter anything to list more entries, !b to go back to menu : ");
				String userInput = sc.nextLine();
				if(userInput.contains("!b")){
					return;
				}
				batch_ctr = 0;
			}
		}
	}
	public static void searchByCityPriceBedroomsAndBathrooms(JsonArray propertyList, String cityInput, String lowerPriceString2, String higherPriceString, String noOfBedrooms, String noOfBathrooms) {
		int propertyCount = 0;
		BigDecimal lowerPriceRangeValue = new BigDecimal(lowerPriceString2);
		BigDecimal higherPriceRangeValue = new BigDecimal(higherPriceString);
		int bedroomsInputValue = Integer.parseInt(noOfBedrooms);
		int bathroomsInputValue = Integer.parseInt(noOfBathrooms);
		for (JsonElement element : propertyList) {
			JsonObject property = element.getAsJsonObject();
			JsonElement cityElement = property.get("city");
			JsonElement priceElement = property.get("price");
			JsonElement bedroomsElement = property.get("bedrooms");
			JsonElement bathroomsElement = property.get("bathrooms");

			if (cityElement != null && cityElement.getAsString().equalsIgnoreCase(cityInput)) {
				BigDecimal price = priceElement.getAsBigDecimal();
				int bedrooms = bedroomsElement.getAsInt();
				int bathrooms = bathroomsElement.getAsInt();

				// Check if the price is within the specified range
				if (price.compareTo(lowerPriceRangeValue) >= 0 && price.compareTo(higherPriceRangeValue) <= 0) {
					// Check if the number of bedrooms and bathrooms match the specified criteria
					if (bedrooms == bedroomsInputValue && bathrooms == bathroomsInputValue) {
						SearchFrequency.updateSearchFrequency(property);
						DisplayFormatter.printPropertyDetails(property);
						propertyCount++;
					}
				}
			}
		}
		if(propertyCount == 0)
			System.out.println("No properties found with the provided criteria!");
	}

	public static void searchByPriceRange(JsonArray propertyList, BigDecimal lowerPriceInput, BigDecimal higherPriceInput) {
		int propertyCount = 0;
		for (JsonElement element : propertyList) {
			JsonObject property = element.getAsJsonObject();
			JsonElement priceElement = property.get("price");
			if (priceElement != null && priceElement.isJsonPrimitive()) {
				BigDecimal propertyPrice = priceElement.getAsBigDecimal();
				if (propertyPrice.compareTo(lowerPriceInput) >= 0 && propertyPrice.compareTo(higherPriceInput) <= 0) {
					SearchFrequency.updateSearchFrequency(property);
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
		return matcher.matches();
	}

	public static boolean validatePincodeInput(String userInput) {
		// Regular expression for validating Canadian postal codes
		String postalCodeRegex = "^[A-Za-z]\\d[A-Za-z]\\s?\\d[A-Za-z]\\d$";
		Pattern pattern = Pattern.compile(postalCodeRegex);
		Matcher matcher = pattern.matcher(userInput);
		return matcher.matches();
	}

	public static boolean validatePriceInput(String userInputPrice) {
		// Regular expression for validating price (allows positive decimal numbers)
		String priceRegex = "^\\d+(\\.\\d+)?$";
		Pattern pattern = Pattern.compile(priceRegex);
		Matcher matcher = pattern.matcher(userInputPrice);
		return matcher.matches();
	}

	public static boolean isValidInteger(String input) {
		// Regular expression to match an integer (positive or negative)
		String regex = "[-+]?\\d+";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);
		return matcher.matches();
	}

	public static void extractUniqueValues( JsonArray propertyList, String searchFactor) {
		Set<String> uniqueFactorValues = new HashSet<>();
		for (JsonElement element : propertyList) {
			JsonObject inpJsonObj = element.getAsJsonObject();
			String searchFactorValue = inpJsonObj.get(searchFactor).getAsString();
			uniqueFactorValues.add(searchFactorValue);
		}

		// Print unique cities
		if(Objects.equals(searchFactor, "city"))
			System.out.println("\nChoose from the provided list of cities:\n");
		else if(Objects.equals(searchFactor, "province"))
			System.out.println("\nChoose from the provided list of provinces:\n");
		for (String val : uniqueFactorValues) {
			System.out.println("- "+val);
		}
	}
}
