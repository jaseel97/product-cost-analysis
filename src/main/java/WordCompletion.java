import java.util.*;
import com.google.gson.*;
import java.io.*;

// Class representing a node in the Trie data structure
class TriNodeClass {
    Map<Character, TriNodeClass> childrenOfTri; // Map to store child nodes
    boolean isWordEnd; // Flag to mark the end of a word

    // Constructor to initialize the Trie node
    public TriNodeClass() {
        this.childrenOfTri = new HashMap<>();
        this.isWordEnd = false;
    }
}

// Trie data structure implementation
class Trie {
    final TriNodeClass rootTri; // Root node of the Trie

    // Constructor to initialize the Trie
    public Trie() {
    	rootTri = new TriNodeClass();
    }

    // Method to insert a word into the Trie
    public void insertIntoTrie(String inputWord) {
    	TriNodeClass current = rootTri;
    	inputWord = inputWord.toLowerCase(); // Convert word to lowercase
        for (int it1 = 0; it1 < inputWord.length(); it1++) {
            char charinp = inputWord.charAt(it1);
            TriNodeClass node = current.childrenOfTri.get(charinp);
            if (node == null) {
                node = new TriNodeClass();
                current.childrenOfTri.put(charinp, node);
            }
            current = node;
        }
        current.isWordEnd = true; // Mark the end of the inserted word
    }

    // Method to search for a word in the Trie
    public boolean search(String word) {
    	TriNodeClass current = rootTri;
        word = word.toLowerCase(); // Convert word to lowercase
        for (int it2 = 0; it2 < word.length(); it2++) {
            char charInp = word.charAt(it2);
            TriNodeClass triNode = current.childrenOfTri.get(charInp);
            if (triNode == null) {
                return false; // Word not found
            }
            current = triNode;
        }
        return current.isWordEnd; // Return true if the word exists in the Trie
    }

    // Method to check if there are words with a given prefix
    public boolean startsWith(String prefix) {
    	TriNodeClass current = rootTri;
        prefix = prefix.toLowerCase(); // Convert prefix to lowercase
        for (int it3 = 0; it3 < prefix.length(); it3++) {
            char charInp = prefix.charAt(it3);
            TriNodeClass triNode = current.childrenOfTri.get(charInp);
            if (triNode == null) {
                return false; // No words with the given prefix
            }
            current = triNode;
        }
        return true; // Prefix found
    }
}

// Class for word completion functionality
public class WordCompletion {

    Trie trie; // Trie object for word completion

    // Constructor to initialize WordCompletion object with a Trie
    public WordCompletion(){
        this.trie = new Trie();
    }

    // Main method to demonstrate word completion
    public static void main(String[] args) {
        WordCompletion wcTrie = new WordCompletion();
        System.out.println(wcTrie.autoCompletion("toronto")); // Example usage of auto-completion
    }

    // Method to perform auto-completion for a given prefix
    public List<String> autoCompletion(String prefix) {
    	TriNodeClass currentTri = this.trie.rootTri;
        for (int i = 0; i < prefix.length(); i++) {
            char ch = prefix.charAt(i);
            TriNodeClass node = currentTri.childrenOfTri.get(ch);
            if (node == null) {
                return Collections.emptyList(); // No words with the given prefix
            }
            currentTri = node;
        }
        List<String> autoCompletedWords = new ArrayList<>();
        findWordsWithPrefix(currentTri, prefix, autoCompletedWords);
        return autoCompletedWords; // Return list of auto-completed words
    }

    // Recursive method to find words with a given prefix
    private void findWordsWithPrefix(TriNodeClass node, String prefix, List<String> result) {
        if (node.isWordEnd) {
            result.add(prefix); // Add the word to the result list
        }
        for (char chars : node.childrenOfTri.keySet()) {
            findWordsWithPrefix(node.childrenOfTri.get(chars), prefix + chars, result);
        }
    }

    // Method to build word completion Trie from a JSON file
    public void buildWordCompletionTrie() {
        Gson gson = new Gson();
        try {
            // Parse JSON array of objects
            JsonArray jsonArray = gson.fromJson(new FileReader("src/main/resources/CombinedProperties.json"), JsonArray.class);
            // Iterate over JSON objects
            for (JsonElement element : jsonArray) {
                JsonObject jsonObject = element.getAsJsonObject();
                // Insert city, province, and pincode into the Trie
                JsonElement city = jsonObject.get("city");
                if (city != null) {
                    this.trie.insertIntoTrie(city.getAsString().toLowerCase());
                }
                JsonElement province = jsonObject.get("province");
                if(province != null) {
                    this.trie.insertIntoTrie(province.getAsString().toLowerCase());
                }
                JsonElement pincode = jsonObject.get("pincode");
                if(pincode != null) {
                    this.trie.insertIntoTrie(pincode.getAsString().toLowerCase());
                }
            }
        } catch (IOException e) {
            System.out.println("Error in creating word completion trie!");
        }
    }
}
