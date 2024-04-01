

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

public class RoyalDataFormatter {
	public static void main(String[] args) {
		Gson gson = new Gson();

		try {
			//parse JSON array of objects
			JsonArray jsonArray = gson.fromJson(new FileReader("src/main/resources/royalle.json"), JsonArray.class);
			//create a list to hold PropertyDetails objects
			List<Property> propertyList = new ArrayList<>();
			//iterate over JSON objects
			for (JsonElement element : jsonArray) {
				JsonObject jsonObject = element.getAsJsonObject();
				//iterate through the different elements - this part. Now the whole properties per listing per city is formatted
				for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
					String jsonKeyValue = entry.getKey();
					JsonElement propertyValue = entry.getValue();
					String PropertyValueString = propertyValue.toString();
					String replacedPropertyValueString = PropertyValueString.replaceAll("\\\\n", "|");
					String propertyName = FindPropertyNameFromString(replacedPropertyValueString);
					BigDecimal propertyPrice = FindPropertyPriceFromString(replacedPropertyValueString);
					String pincode = FindPropertyPincodeFromString(replacedPropertyValueString);
					String mlsNumber = FindMLSNumberIdentifier(replacedPropertyValueString);
					int bedrooms = getBedroomsFromListing(replacedPropertyValueString);
					int bathrooms = getBathroomsFromListing(replacedPropertyValueString);
					String description = getPropertyDescription(replacedPropertyValueString);
					String buildingType = getPropertyBuildingType(replacedPropertyValueString);
					String propertyCity = getPropertyCity(jsonKeyValue.toString());
					String province = getPropertyProvince(jsonKeyValue.toString());
					// Create PropertyDetails object and add it to the list
					Property property = new Property(mlsNumber, propertyName, buildingType, propertyCity, province, pincode, propertyPrice, bedrooms, bathrooms, description, 0);
					propertyList.add(property);
				}
			}

			//Write propertyList to JSON file
			Gson gsonBuilder = new GsonBuilder().setPrettyPrinting().create();
			try (FileWriter writer = new FileWriter("src/main/resources/RoyalleProperties.json")) {
				gsonBuilder.toJson(propertyList, writer);
				System.out.println("Property details written to RoyalleProperties.json successfully.");
			} catch (IOException e) {
				System.out.println("Error writing royale data to json!");
			}
		} catch (IOException e) {
			System.out.println("Error reading data from royale sourec json!");
		}
	}

	private static String FindPropertyNameFromString(String jsonStringValue) {
		String propertyName = null;
		Pattern pattern = Pattern.compile("^\"?(.*?)\\,");
		// Create a matcher with the long string
		Matcher matcher = pattern.matcher(jsonStringValue);

		// Find the address
		if (matcher.find()) {
			propertyName = matcher.group(1).trim(); // Group 1 captures the address
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
		postalCode = postalCode.replace(" ", "");
		return postalCode;
	}

	private static String FindMLSNumberIdentifier(String jsonStringValue) {
		String patternString = "\\|MLS�\\s*#\\s*([A-Za-z0-9]+)\\|";
//		String patternString = "MLS�#\\|([A-Za-z0-9]+)\\|";
		Pattern mlsPattern = Pattern.compile(patternString);
		Matcher matcher = mlsPattern.matcher(jsonStringValue);
		String mlsNumber = null;

		if (matcher.find()) {
			mlsNumber = matcher.group(1);
		}
		return mlsNumber;
	}

	private static int getBedroomsFromListing(String jsonStringValue) {
		String extractedData = null;
		String pattern = "\\|(\\d+)\\s+Beds\\|"; // Regex pattern to match "1+1" followed by "bed" and space
		Pattern postalCodePattern = Pattern.compile(pattern);
		Matcher matcher = postalCodePattern.matcher(jsonStringValue);

		if(matcher.find()) {
			extractedData = matcher.group(1);
		}

		if (extractedData == null) {
			return 0; // Return 0 if extractedData is null
		}

		int sum = 0;
		if (extractedData.contains("+")) {
			String[] parts = extractedData.split("\\+");
			for (String part : parts) {
				sum += Integer.parseInt(part.trim());
			}
		} else {
			return Integer.parseInt(extractedData.trim());
		}

		return sum;
	}

	private static int getBathroomsFromListing(String jsonStringValue) {
		String extractedData = null;
		String pattern = "\\|(\\d+)\\s+Baths\\|";
		Pattern postalCodePattern = Pattern.compile(pattern);
		Matcher matcher = postalCodePattern.matcher(jsonStringValue);
		
		if(matcher.find()) {
			extractedData = matcher.group(1);
		}
		
		if (extractedData == null) {
			return 0; // Return 0 if extractedData is null
		}
		
		int sum = 0;
		if (extractedData.contains("+")) {
			String[] parts = extractedData.split("\\+");
			for (String part : parts) {
				sum += Integer.parseInt(part.trim());
			}
		} else {
			return Integer.parseInt(extractedData.trim());
		}

		return sum;
	}

	private static String getPropertyDescription(String jsonStringValue) {
		String propertyDescription = null;
		String patternString = "PROPERTY INFORMATION:\\|(.*?)\\|";
		Pattern pattern = Pattern.compile(patternString);
		Matcher matcher = pattern.matcher(jsonStringValue);

		if (matcher.find()) {
			propertyDescription = matcher.group(1).trim();
		}
		return propertyDescription;
	}

	private static String getPropertyBuildingType(String jsonStringValue) {
		String buildingType = null;
		String patternString = "Building Type:\\|(.*?)\\|";
		Pattern pattern = Pattern.compile(patternString);
		Matcher matcher = pattern.matcher(jsonStringValue);

		if (matcher.find()) {
			buildingType = matcher.group(1).trim();
			return buildingType;
		} else
			return "House";
	}

//	private static float getNumberOfStoreys(String jsonStringValue) {
//		float numberOfStoreys = 0;
//		Pattern pattern = Pattern.compile("Storeys\\\\n(.+?)\\\\n");
//		Matcher matcher = pattern.matcher(jsonStringValue);
//
//		if (matcher.find()) {
//			String storeys = matcher.group(1).trim();
//			numberOfStoreys = Float.parseFloat(storeys);
//		}
//		return numberOfStoreys;
//	}
//
//	private static double getSqftAreaOfProperty(String jsonStringValue) {
//		double propertyArea = 0;
//		Pattern pattern = Pattern.compile("\\\\n([0-9.]+)\\s*sqft");
//		Matcher matcher = pattern.matcher(jsonStringValue);
//		if (matcher.find()) {
//			String areaMeasurementString = matcher.group(1).trim();
//			propertyArea = Double.parseDouble(areaMeasurementString);
//		} 
//		return propertyArea;
//	}

	private static String getPropertyCity(String jsonStringValue) {
		String propertyCity = null;
		String[] partsOfString = jsonStringValue.split(",");
		if (partsOfString.length > 0) {
			propertyCity = partsOfString[0].trim();
		}
		return propertyCity;
	}

	public static String getPropertyProvince(String jsonStringValue) {
		String province = null;
        String pattern = ",\\s*([A-Z]{2}),"; // Match the province code consisting of two uppercase letters
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(jsonStringValue);

        if (matcher.find()) {
            province = matcher.group(1);
        } else {
            province = "Province code not found";
        }
        return mapProvinceCode(province);
    }
	
	private static String mapProvinceCode(String provinceCode) {
        // Mapping of province codes to province names
        return switch (provinceCode) {
            case "ON" -> "Ontario";
            case "QC" -> "Quebec";
            case "AB" -> "Alberta";
            case "BC" -> "British Columbia";
            case "NS" -> "Nova Scotia";
            case "MB" -> "Manitoba";
            default -> "Unknown"; // Default to "Unknown" if no mapping is found
        };
    }
}
