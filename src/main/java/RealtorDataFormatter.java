import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class RealtorDataFormatter {
	public static void call(JsonArray ScrapedDataFile) throws IOException {

		List<Property> propertyList = new ArrayList<>();
		//iterate over JSON objects
		for (JsonElement element : ScrapedDataFile) {
			JsonObject jsonObject = element.getAsJsonObject();
			//iterate through the different elements - this part. Now the whole properties per listing per city is formatted
			for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
				String jsonKeyValue = entry.getKey();
				JsonElement propertyValue = entry.getValue();
				String propertyName = FindPropertyNameFromString(propertyValue.toString());
				BigDecimal propertyPrice = FindPropertyPriceFromString(propertyValue.toString());
				String pincode = FindPropertyPincodeFromString(propertyValue.toString());
				String mlsNumber = FindMLSNumberIdentifier(propertyValue.toString());
				int bedrooms = getBedroomsAndBathrooms(propertyValue.toString(), "Bedrooms");
				int bathrooms = getBedroomsAndBathrooms(propertyValue.toString(), "Bathrooms");
				String description = getPropertyDescription(propertyValue.toString());
				String buildingType = getPropertyBuildingType(propertyValue.toString());
				//					float numberOfStoreys = getNumberOfStoreys(propertyValue.toString());
				//					double propertyArea = getSqftAreaOfProperty(propertyValue.toString());
				String city = getPropertyCity(jsonKeyValue);
				String province = getPropertyProvince(propertyValue.toString());

				// Create PropertyDetails object and add it to the list
				Property property = new Property(mlsNumber, propertyName, buildingType, city, province, pincode, propertyPrice, bedrooms, bathrooms, description, 0);
				propertyList.add(property);
			}
		}

		// Write propertyList to JSON file
		Gson gsonBuilder = new GsonBuilder().setPrettyPrinting().create();
		try (FileWriter writer = new FileWriter("src/main/resources/RealtorProperties.json")) {
			gsonBuilder.toJson(propertyList, writer);
			System.out.println("Property details written to RealtorProperties.json successfully.");
		} catch (IOException e) {
			System.out.println("Error writing data to json!");
		}
	}

	private static String FindPropertyNameFromString(String jsonStringValue) {
		String propertyName = null;
		Pattern pattern = Pattern.compile("\\\\n(.*?)\\\\nDirections");

		// Create a matcher with the long string
		Matcher matcher = pattern.matcher(jsonStringValue);

		// Find the address
		if (matcher.find()) {
			propertyName = matcher.group(1).trim(); // Group 1 captures the address
		}
		String replacedString = propertyName.replaceAll("\\\\n", "|");

		// Find the index of the first occurrence of the replaced character ('|') from the end of the string
		int lastIndex = replacedString.lastIndexOf('|');
		if (lastIndex != -1) {
			// Extract the substring between the first occurrence of the replaced character and the end of the string
			propertyName = replacedString.substring(lastIndex + 1).trim();
		}
		return propertyName;
	}

	private static BigDecimal FindPropertyPriceFromString(String jsonStringValue) {
		String patternString = "\\$\\s?\\d{1,3}(?:,\\d{3})*(?:\\.\\d{2})?";
		Pattern pattern = Pattern.compile(patternString); 
		Matcher matcher = pattern.matcher(jsonStringValue);
		BigDecimal price = null;
		// Find and print the first match (if any)
		if (matcher.find()) {
			String priceStr = matcher.group().replaceAll("\\$|,", "");;
			price = new BigDecimal(priceStr);
		} 
		return price;
	}

	private static String FindPropertyPincodeFromString(String jsonStringValue) {
		String pattern = "[A-Za-z]\\d[A-Za-z]\\s?\\d[A-Za-z]\\d";
		Pattern postalCodePattern = Pattern.compile(pattern);
		Matcher matcher = postalCodePattern.matcher(jsonStringValue);
		String postalCode = null;
		if(matcher.find()) {
			postalCode = matcher.group();
		}

		return postalCode;
	}

	private static String FindMLSNumberIdentifier(String jsonStringValue) {
		String pattern = "MLS� Number:\\s*([A-Za-z0-9]+)";
		Pattern mlsPattern = Pattern.compile(pattern);
		Matcher matcher = mlsPattern.matcher(jsonStringValue);
		String mlsNumber = null;

		if (matcher.find()) {
			mlsNumber = matcher.group(1);
		}
		return mlsNumber;
	}

	private static int getBedroomsAndBathrooms(String jsonStringValue, String keyword) {
		String extractedData = null;
		int sum = 0;
		int indexHash = jsonStringValue.indexOf("\\n"+keyword);
		if (indexHash != -1) { 
			int indexNewline = jsonStringValue.lastIndexOf('n', indexHash);
			if (indexNewline != -1) { 
				extractedData = jsonStringValue.substring(indexNewline + 1, indexHash);
				if (extractedData.contains("+")) {
					String[] parts = extractedData.split("\\+");
					for (String part : parts) {
						sum += Integer.parseInt(part.trim());
					}
				} else {
					return Integer.parseInt(extractedData.trim());
				}
			}
		}
		return sum;
	}


	private static String getPropertyDescription(String jsonStringValue) {
		String propertyDescription = null;
		Pattern pattern = Pattern.compile("Listing Description\\\\n(.*?)\\(");
		Matcher matcher = pattern.matcher(jsonStringValue);

		if (matcher.find()) {
			propertyDescription = matcher.group(1).trim();
		}
		return propertyDescription;
	}

	private static String getPropertyBuildingType(String jsonStringValue) {
		String buildingType = null;
		Pattern pattern = Pattern.compile("Building Type\\\\n(.+?)\\\\n");
		Matcher matcher = pattern.matcher(jsonStringValue);

		if (matcher.find()) {
			buildingType = matcher.group(1).trim();
		}
		return buildingType;
	}

	private static float getNumberOfStoreys(String jsonStringValue) {
		float numberOfStoreys = 0;
		Pattern pattern = Pattern.compile("Storeys\\\\n(.+?)\\\\n");
		Matcher matcher = pattern.matcher(jsonStringValue);

		if (matcher.find()) {
			String storeys = matcher.group(1).trim();
			numberOfStoreys = Float.parseFloat(storeys);
		}
		return numberOfStoreys;
	}

	private static double getSqftAreaOfProperty(String jsonStringValue) {
		double propertyArea = 0;
		Pattern pattern = Pattern.compile("\\\\n([0-9.]+)\\s*sqft");
		Matcher matcher = pattern.matcher(jsonStringValue);
		if (matcher.find()) {
			String areaMeasurementString = matcher.group(1).trim();
			propertyArea = Double.parseDouble(areaMeasurementString);
		} 
		return propertyArea;
	}

	private static String getPropertyCity(String jsonStringValue) {
		String propertyCity = null;
		String[] partsOfString = jsonStringValue.split(",");
		if (partsOfString.length > 0) {
			propertyCity = partsOfString[0].trim();
		}
		return propertyCity;
	}

	private static String getPropertyProvince(String jsonStringValue) {
		String province = null;
		Pattern pattern = Pattern.compile(",\\s*(\\w+(?:\\s+\\w+)*)\\s+(\\w{1,2}\\d{1,2}\\w{0,1}\\s*\\d{1}\\w{1}\\d{1})");
		Matcher matcher1 = pattern.matcher(jsonStringValue);
		if (matcher1.find()) {
			province = matcher1.group(1).trim();
		}
		return province;
	}
}
