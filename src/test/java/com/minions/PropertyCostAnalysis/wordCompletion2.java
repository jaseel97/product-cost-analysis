package finalproject;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

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
        Trie trie = new Trie();
        Gson gson = new Gson();
        
        try {
            // Parse JSON array of objects
            JsonArray jsonArray = gson.fromJson(new FileReader("PropertiesSample.json"), JsonArray.class);
            // Iterate over JSON objects
            for (JsonElement element : jsonArray) {
                JsonObject jsonObject = element.getAsJsonObject();
                // Assuming the property name is "city"
                String city = jsonObject.get("city").getAsString();
                trie.insert(city);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Prompt user for prefix input
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the prefix to find suggestions: ");
        String prefix = scanner.nextLine();

        // Test auto-completion
        System.out.println("Suggestions for '" + prefix + "':");
        autoCompletion(trie, prefix.toLowerCase());
    }

    public static void autoCompletion(Trie trie, String prefix) {
        TrieNode current = trie.root;
        for (int i = 0; i < prefix.length(); i++) {
            char ch = prefix.charAt(i);
            TrieNode node = current.children.get(ch);
            if (node == null) {
                System.out.println("No suggestions found for '" + prefix + "'");
                return;
            }
            current = node;
        }
        findWordsWithPrefix(current, prefix);
    }

    private static void findWordsWithPrefix(TrieNode node, String prefix) {
        if (node.isEndOfWord) {
            System.out.println(prefix);
        }
        for (char ch : node.children.keySet()) {
            findWordsWithPrefix(node.children.get(ch), prefix + ch);
        }
    }
}
