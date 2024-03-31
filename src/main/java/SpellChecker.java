import java.io.*;
import java.util.*;
import org.json.simple.*;
import org.json.simple.parser.*;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

class SplayNode {
    String key;
    int occurrences;
    SplayNode left, right;

    public SplayNode(String key) {
        this.key = key;
        this.occurrences = 1;
        this.left = this.right = null;
    }
}

class SplayTree {

    private SplayNode root;

    private SplayNode splay(SplayNode root, String k) {
        if (root == null || root.key.equals(k)) {
            return root;
        }

        if (k.compareTo(root.key) < 0) {
            if (root.left == null) {
                return root;
            }

            if (k.compareTo(root.left.key) < 0) {
                root.left.left = splay(root.left.left, k);
                root = rotateRight(root);
            } else if (k.compareTo(root.left.key) > 0) {
                root.left.right = splay(root.left.right, k);
                if (root.left.right != null) {
                    root.left = rotateLeft(root.left);
                }
            }

            return (root.left == null) ? root : rotateRight(root);

        } else {
            if (root.right == null) {
                return root;
            }

            if (k.compareTo(root.right.key) < 0) {
                root.right.left = splay(root.right.left, k);
                if (root.right.left != null) {
                    root.right = rotateRight(root.right);
                }
            } else if (k.compareTo(root.right.key) > 0) {
                root.right.right = splay(root.right.right, k);
                root = rotateLeft(root);
            }
            return (root.right == null) ? root : rotateLeft(root);
        }
    }

    private SplayNode rotateRight(SplayNode x) {
        SplayNode y = x.left;
        x.left = y.right;
        y.right = x;
        return y;
    }

    private SplayNode rotateLeft(SplayNode x) {
        SplayNode y = x.right;
        x.right = y.left;
        y.left = x;
        return y;
    }

    public void insert(String key) {
        root = insert(root, key);
        root = splay(root, key);
    }

    private SplayNode insert(SplayNode root, String key) {
        if (root == null) {
            return new SplayNode(key);
        }

        int cmp = key.compareTo(root.key);
        if (cmp < 0) {
            root.left = insert(root.left, key);
        } else if (cmp > 0) {
            root.right = insert(root.right, key);
        } else {
            root.occurrences++;
        }

        return root;
    }

    public boolean contains(String key) {
        root = splay(root, key);
        return root != null && root.key.equals(key);
    }

    public String suggestCorrection(String word, int threshold) {
        EditDistanceResult result = new EditDistanceResult();
        result.distance = Integer.MAX_VALUE;
        result.word = null;
        ForSuggestion(root, word, threshold, result);
        return result.word;
    }

    private void ForSuggestion(SplayNode root, String misspelledWord, int threshold, EditDistanceResult result) {
        if (root != null) {
        	
            ForSuggestion(root.left, misspelledWord, threshold, result);

            int distance = editDist(misspelledWord, root.key.toLowerCase());

            if (distance < result.distance && distance <= threshold) {
                result.word = root.key;
                result.distance = distance;
            }
            ForSuggestion(root.right, misspelledWord, threshold, result);
        }
    }

    private static class EditDistanceResult {
        String word;
        int distance;
    }

    public static int editDist(String w1, String w2) {
        int l1 = w1.length();
        int l2 = w2.length();

        int[][] dp = new int[l1 + 1][l2 + 1];

        for (int i = 0; i <= l1; i++) {
            dp[i][0] = i;
        }

        for (int j = 0; j <= l2; j++) {
            dp[0][j] = j;
        }

        for (int i = 0; i < l1; i++) {
            char c1 = w1.charAt(i);
            for (int j = 0; j < l2; j++) {
                char c2 = w2.charAt(j);

                if (c1 == c2) {
                    dp[i + 1][j + 1] = dp[i][j];
                } else {
                    int rp = dp[i][j] + 1;
                    int ins = dp[i][j + 1] + 1;
                    int dele = dp[i + 1][j] + 1;

                    int min = rp > ins ? ins : rp;
                    min = dele > min ? min : dele;
                    dp[i + 1][j + 1] = min;
                }
            }
        }

        return dp[l1][l2];
    }
}

public class SpellChecker {
   
    public static String call(String searchFactor, String inputtedString) {
    	SplayTree dicti = new SplayTree();
    	String fileName = "C:\\Users\\royce\\eclipse-workspace\\ACC_Final_Project\\RealtorProperties.json"; 

        // Load data from JSON file
        try {
            JSONParser parser = new JSONParser();
            JSONArray jsonProperties = (JSONArray) parser.parse(new FileReader(fileName));

            // Extract inputted search parameter and insert into the dictionary
            for (Object obj : jsonProperties) {
                JSONObject property = (JSONObject) obj;
                String inputSearchEntry = (String) property.get(searchFactor);
                if(!dicti.contains(inputSearchEntry.toLowerCase())) {
                	dicti.insert(inputSearchEntry.toLowerCase()); 
                }
            }
            
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
            
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        int threshold = 4; // Setting the threshold for spellchecker to 4
        String suggestion = dicti.suggestCorrection(inputtedString, threshold);
        return suggestion;
    }

    public static void main(String[] args) {
    }
}

