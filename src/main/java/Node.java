import com.google.gson.JsonArray;

import java.util.HashMap;

public class Node {
    public final HashMap<Character, Node> children;
    public boolean endOfWord;
    public JsonArray properties;

    public Node(){
        this.children = new HashMap<>();
        this.endOfWord = false;
        this.properties = null;
    }
}
