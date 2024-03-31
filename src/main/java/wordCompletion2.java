import com.google.gson.*;
import java.io.*;
import java.util.*;

class TrieNode {
    Map<Character, TrieNode> children;
    boolean isEndOfWord;

    public TrieNode() {
        this.children = new HashMap<>();
        this.isEndOfWord = false;
    }
}

class Trie {
    final TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    public void insert(String word) {
        TrieNode current = root;
        word = word.toLowerCase(); // Convert word to lowercase
        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            TrieNode node = current.children.get(ch);
            if (node == null) {
                node = new TrieNode();
                current.children.put(ch, node);
            }
            current = node;
        }
        current.isEndOfWord = true;
    }

    public boolean search(String word) {
        TrieNode current = root;
        word = word.toLowerCase(); // Convert word to lowercase
        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            TrieNode node = current.children.get(ch);
            if (node == null) {
                return false;
            }
            current = node;
        }
        return current.isEndOfWord;
    }

    public boolean startsWith(String prefix) {
        TrieNode current = root;
        prefix = prefix.toLowerCase(); // Convert prefix to lowercase
        for (int i = 0; i < prefix.length(); i++) {
            char ch = prefix.charAt(i);
            TrieNode node = current.children.get(ch);
            if (node == null) {
                return false;
            }
            current = node;
        }
        return true;
    }
}

public class wordCompletion2 {
    public static void main(String[] args) {
        call("city", "to");
    }

    public static List<String> autoCompletion(Trie trie, String prefix) {
        TrieNode current = trie.root;
        for (int i = 0; i < prefix.length(); i++) {
            char ch = prefix.charAt(i);
            TrieNode node = current.children.get(ch);
            if (node == null) {
                return Collections.emptyList();
            }
            current = node;
        }
        List<String> autoCompletedWords = new ArrayList<>();
        findWordsWithPrefix(current, prefix, autoCompletedWords);
        return autoCompletedWords;
    }

    private static void findWordsWithPrefix(TrieNode node, String prefix, List<String> result) {
        if (node.isEndOfWord) {
            result.add(prefix);
        }
        for (char ch : node.children.keySet()) {
            findWordsWithPrefix(node.children.get(ch), prefix + ch, result);
        }
    }

    public static List<String> call(String searchFactor, String inputtedString) {
        Trie trie = new Trie();
        Gson gson = new Gson();

        try {
            // Parse JSON array of objects
            JsonArray jsonArray = gson.fromJson(new FileReader("src/main/resources/RealtorProperties2.json"), JsonArray.class);
            // Iterate over JSON objects
            for (JsonElement element : jsonArray) {
                JsonObject jsonObject = element.getAsJsonObject();
                // Assuming the property name is "city"
                String city = jsonObject.get(searchFactor).getAsString();
                trie.insert(city);
            }
        } catch (IOException e) {
            System.out.println("Error in word completion!");
        }

        List<String> suggestions = autoCompletion(trie, inputtedString.toLowerCase());
        return suggestions;
    }
}
