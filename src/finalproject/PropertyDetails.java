package finalproject;

import java.math.BigDecimal;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class PropertyDetails {
	public String id;
	public String propertyName;
	public String buildingType;
	public String city;
	public String province;
	public String pincode;
	public BigDecimal price;
	public int bedrooms;
	public int bathrooms;
	public float numberOfStoreys;
	public double sqft;
	public String description;

	public PropertyDetails(String mlsNumber, String propertyName, String buildingType, String city, String province,
			String pincode, BigDecimal propertyPrice, int bedrooms, int bathrooms, float numberOfStoreys, double propertyArea,
			String description) {
		
		this.id = mlsNumber;
		this.propertyName = propertyName;
		this.buildingType = buildingType;
		this.city = city;
		this.province = province;
		this.pincode = pincode;
		this.price = propertyPrice;
		this.bedrooms = bedrooms;
		this.bathrooms = bathrooms;
		this.numberOfStoreys = numberOfStoreys;
		this.sqft = propertyArea;
		this.description = description;
	}
}
