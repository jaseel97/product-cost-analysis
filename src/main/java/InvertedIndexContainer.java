import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


class InvertedIndex {
    private final Node root;
    public InvertedIndex(){
        this.root = new Node();
    }

    private void insert(String word) {
        Node current = root;
        for (int ctr = 0; ctr < word.length(); ctr++) {
            char ch = word.charAt(ctr);
            current = current.children.computeIfAbsent(ch, c -> new Node());
        }
        current.endOfWord = true;
    }

    private Node contains(String word) {
        Node current = root;
        for (int ctr = 0; ctr < word.length(); ctr++) {
            char ch = word.charAt(ctr);
            Node node = current.children.get(ch);
            if (node == null) return null;
            current = node;
        }
        return current;
    }

    public JsonArray get(String word) {
        word = word.toLowerCase();
        Node current = this.contains(word);
        if (current != null) {
            return current.properties;
        } else {
            return null;
        }
    }

    public void buildIndexFromJSON(JsonArray propertyList, String searchFactor){
        for(JsonElement element : propertyList){
            JsonObject property = element.getAsJsonObject();
            JsonElement field = property.get(searchFactor);
            if(field != null) this.insertProperty(field.getAsString().toLowerCase(),property);
//            JsonElement city = property.get("city");
//            if(city != null) this.insertProperty(city.getAsString().toLowerCase(),property);
//            JsonElement province = property.get("province");
//            if(province != null) this.insertProperty(province.getAsString().toLowerCase(),property);
//            JsonElement pincode = property.get("pincode");
//            if(pincode != null) this.insertProperty(pincode.getAsString().toLowerCase(),property);
        }
    }

    private void insertProperty(String key, JsonObject property){
        this.insert(key);
        Node node = this.contains(key);
        if (node.properties != null){
            node.properties.add(property);
        }else{
            node.properties = new JsonArray();
            node.properties.add(property);
        }
    }
}
public class InvertedIndexContainer{
    static InvertedIndex[] indices;

    public static void initIndices(JsonArray propertyList){
        indices = new InvertedIndex[3];
        indices[0] = new InvertedIndex();
        indices[0].buildIndexFromJSON(propertyList,"city");
        indices[1] = new InvertedIndex();
        indices[1].buildIndexFromJSON(propertyList,"province");
        indices[2] = new InvertedIndex();
        indices[2].buildIndexFromJSON(propertyList,"pincode");
    }
}

