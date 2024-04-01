import com.google.gson.JsonElement;

import java.util.HashMap;
import java.util.HashSet;

public class Node {
    public final HashMap<Character, Node> children;
    public boolean endOfWord;
    public HashSet<JsonElement> properties;

    public Node(){
        this.children = new HashMap<>();
        this.endOfWord = false;
        this.properties = null;
    }
}
