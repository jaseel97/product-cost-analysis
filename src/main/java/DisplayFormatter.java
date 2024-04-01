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
        String propertyName = propertyDetails.get("propertyName") != null ? propertyDetails.get("propertyName").getAsString() : "-";
        System.out.println("\nProperty Name: " + propertyName);
        String buildingType = propertyDetails.get("buildingType") != null ? propertyDetails.get("buildingType").getAsString(): "-";
        System.out.println("Building Type: " + buildingType);
        String propertyCity = propertyDetails.get("city") != null ? propertyDetails.get("city").getAsString(): "-";
        System.out.println("City: " + propertyCity);
        String province = propertyDetails.get("province") != null ? propertyDetails.get("province").getAsString(): "-";
        System.out.println("Province: " + province);
        String pincode = propertyDetails.get("pincode") != null ? propertyDetails.get("pincode").getAsString(): "-";
        System.out.println("Pincode: " + pincode);
        BigDecimal price = propertyDetails.get("price") != null ? propertyDetails.get("price").getAsBigDecimal() : null;
        System.out.println("Price: " + currencyFormatter.format(price));

        String bedrooms = (propertyDetails.get("bedrooms") == null || (propertyDetails.get("bedrooms").getAsString().equals("0"))) ? "-" : propertyDetails.get("bedrooms").getAsString();
        System.out.println("Bedrooms: " + bedrooms);
        String bathrooms = (propertyDetails.get("bathrooms") == null || (propertyDetails.get("bathrooms").getAsString().equals("0"))) ? "-" : propertyDetails.get("bathrooms").getAsString();
        System.out.println("Bathrooms: " + bathrooms);

        String description = propertyDetails.get("description") != null ? propertyDetails.get("description").getAsString(): "-";
        System.out.println("Description: " + description);
        System.out.println();

        String searchFrequency = propertyDetails.get("search_frequency") != null ? propertyDetails.get("search_frequency").getAsString(): "-";
        System.out.println("This property has been searched: " + searchFrequency+" time(s)!");
        System.out.println();
    }

}

