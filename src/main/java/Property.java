import java.math.BigDecimal;

public class Property {
	public String id;
	public String propertyName;
	public String buildingType;
	public String city;
	public String province;
	public String pincode;
	public BigDecimal price;
	public int bedrooms;
	public int bathrooms;
//	public float numberOfStoreys;
//	public double sqft;
	public String description;
	public int search_frequency;

	public Property(String mlsNumber, String propertyName, String buildingType, String city, String province,
			String pincode, BigDecimal propertyPrice, int bedrooms, int bathrooms, String description, int search_frequency) {
		
		this.id = mlsNumber;
		this.propertyName = propertyName;
		this.buildingType = buildingType;
		this.city = city;
		this.province = province;
		this.pincode = pincode;
		this.price = propertyPrice;
		this.bedrooms = bedrooms;
		this.bathrooms = bathrooms;
//		this.numberOfStoreys = numberOfStoreys;
//		this.sqft = propertyArea;
		this.description = description;
		this.search_frequency = search_frequency;
	}
	
	public int getSearchFrequency() {
        return search_frequency;
    }

    public void setSearchFrequency(int searchFrequency) {
        this.search_frequency = searchFrequency;
    }
}
