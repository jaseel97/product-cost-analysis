import java.util.*;
import com.google.gson.*;
import java.io.*;

class TriNodeClass {
    Map<Character, TriNodeClass> childrenOfTri;
    boolean isWordEnd;

    public TriNodeClass() {
        this.childrenOfTri = new HashMap<>();
        this.isWordEnd = false;
    }
}

class Trie {
    final TriNodeClass rootTri;

    public Trie() {
    	rootTri = new TriNodeClass();
    }

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
        current.isWordEnd = true;
    }

    public boolean search(String word) {
    	TriNodeClass current = rootTri;
        word = word.toLowerCase(); // Convert word to lowercase
        for (int it2 = 0; it2 < word.length(); it2++) {
            char charInp = word.charAt(it2);
            TriNodeClass triNode = current.childrenOfTri.get(charInp);
            if (triNode == null) {
                return false;
            }
            current = triNode;
        }
        return current.isWordEnd;
    }

    public boolean startsWith(String prefix) {
    	TriNodeClass current = rootTri;
        prefix = prefix.toLowerCase(); // Convert prefix to lowercase
        for (int it3 = 0; it3 < prefix.length(); it3++) {
            char charInp = prefix.charAt(it3);
            TriNodeClass triNode = current.childrenOfTri.get(charInp);
            if (triNode == null) {
                return false;
            }
            current = triNode;
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
    	TriNodeClass currentTri = this.trie.rootTri;
        for (int i = 0; i < prefix.length(); i++) {
            char ch = prefix.charAt(i);
            TriNodeClass node = currentTri.childrenOfTri.get(ch);
            if (node == null) {
                return Collections.emptyList();
            }
            currentTri = node;
        }
        List<String> autoCompletedWords = new ArrayList<>();
        findWordsWithPrefix(currentTri, prefix, autoCompletedWords);
        return autoCompletedWords;
    }

    private void findWordsWithPrefix(TriNodeClass node, String prefix, List<String> result) {
        if (node.isWordEnd) {
            result.add(prefix);
        }
        for (char chars : node.childrenOfTri.keySet()) {
            findWordsWithPrefix(node.childrenOfTri.get(chars), prefix + chars, result);
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
