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

public class WordCompletion {

    Trie trie;

    public WordCompletion(){
        this.trie = new Trie();
    }
    public static void main(String[] args) {
        WordCompletion wcTrie = new WordCompletion();
        System.out.println(wcTrie.autoCompletion("toronto"));
    }

    public List<String> autoCompletion(String prefix) {
        TrieNode current = this.trie.root;
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

    private void findWordsWithPrefix(TrieNode node, String prefix, List<String> result) {
        if (node.isEndOfWord) {
            result.add(prefix);
        }
        for (char ch : node.children.keySet()) {
            findWordsWithPrefix(node.children.get(ch), prefix + ch, result);
        }
    }

    public void buildWordCompletionTrie() {
        Gson gson = new Gson();
        try {
            // Parse JSON array of objects
            JsonArray jsonArray = gson.fromJson(new FileReader("src/main/resources/CombinedProperties.json"), JsonArray.class);
            // Iterate over JSON objects
            for (JsonElement element : jsonArray) {
                JsonObject jsonObject = element.getAsJsonObject();
                JsonElement city = jsonObject.get("city");
                if (city != null) {
                    this.trie.insert(city.getAsString().toLowerCase());
                }
                JsonElement province = jsonObject.get("province");
                if(province != null) {
                    this.trie.insert(province.getAsString().toLowerCase());
                }
                JsonElement pincode = jsonObject.get("pincode");
                if(pincode != null) {
                    this.trie.insert(pincode.getAsString().toLowerCase());
                }
            }
        } catch (IOException e) {
            System.out.println("Error in creating word completion trie!");
        }
    }
}
