import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import com.google.gson.JsonObject;

public class DisplayFormatter {

	public static void printPropertyDetails(JsonObject propertyDetails) {
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.CANADA);
        DecimalFormat decimalFormatter = new DecimalFormat("#.#");
        decimalFormatter.setMaximumFractionDigits(1);
        String propertyName = propertyDetails.get("propertyName") != null ? propertyDetails.get("propertyName").getAsString() : null
        System.out.println("\nProperty Name: " + propertyName);
        String buildingType = propertyDetails.get("buildingType") != null ? propertyDetails.get("buildingType").getAsString(): null;
        System.out.println("Building Type: " + buildingType);
        String propertyCity = propertyDetails.get("city") != null ? propertyDetails.get("city").getAsString(): null;
        System.out.println("City: " + propertyCity);
        String province = propertyDetails.get("province") != null ? propertyDetails.get("province").getAsString(): null;
        System.out.println("Province: " + province);
        String pincode = propertyDetails.get("pincode") != null ? propertyDetails.get("pincode").getAsString(): null;
        System.out.println("Pincode: " + pincode);
        BigDecimal price = propertyDetails.get("price") != null ? propertyDetails.get("price").getAsBigDecimal() : null;
        System.out.println("Price: " + currencyFormatter.format(price));
        int bedrooms = propertyDetails.get("bedrooms") != null ? propertyDetails.get("bedrooms").getAsInt() : null;
        System.out.println("Bedrooms: " + bedrooms);
        int bathrooms = propertyDetails.get("bathrooms") != null ? propertyDetails.get("bathrooms").getAsInt() : null;
        System.out.println("Bathrooms: " + bathrooms);
        System.out.print("Number of Storeys: ");
        double numberOfStoreys = propertyDetails.get("numberOfStoreys").getAsDouble();

        if (numberOfStoreys % 1 == 0) {
            // Whole number
            System.out.println((int) numberOfStoreys);
        } else {
            // Decimal with value
            System.out.println(decimalFormatter.format(numberOfStoreys));
        }
        System.out.println("Square Footage: " + propertyDetails.get("sqft").getAsDouble() + " sqft");

        String description = propertyDetails.get("description") != null ? propertyDetails.get("description").getAsString(): null;
        System.out.println("Description: " + description);
        System.out.println();

        int searchFrequency = propertyDetails.get("search_frequency") != null ? propertyDetails.get("search_frequency").getAsInt(): null;
        System.out.println("This property has been searched: " + searchFrequency+" time(s)!");
        System.out.println();
    }

}

