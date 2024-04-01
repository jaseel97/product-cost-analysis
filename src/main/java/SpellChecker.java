import java.io.*;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.*;

class ImplementSplayNode {
    String spkey;
    int spkey_occurences;
    ImplementSplayNode spleftnode, sprightnode;
    

    public ImplementSplayNode(String spkey) {
        this.spkey = spkey;
        this.spkey_occurences = 1;
        this.spleftnode = this.sprightnode = null;
    }
}

class ImplementSplayTree {

    private ImplementSplayNode sproot;

    private ImplementSplayNode splay(ImplementSplayNode sproot, String wordk) {
        if (sproot == null || sproot.spkey.equals(wordk)) {
            return sproot;
        }

        if (wordk.compareTo(sproot.spkey) < 0) {
            if (sproot.spleftnode == null) {
                return sproot;
            }

            if (wordk.compareTo(sproot.spleftnode.spkey) < 0) {
                sproot.spleftnode.spleftnode = splay(sproot.spleftnode.spleftnode, wordk);
                sproot = rotatesprightnode(sproot);
            } else if (wordk.compareTo(sproot.spleftnode.spkey) > 0) {
                sproot.spleftnode.sprightnode = splay(sproot.spleftnode.sprightnode, wordk);
                if (sproot.spleftnode.sprightnode != null) {
                    sproot.spleftnode = rotatespleftnode(sproot.spleftnode);
                }
            }

            return (sproot.spleftnode == null) ? sproot : rotatesprightnode(sproot);

        } else {
            if (sproot.sprightnode == null) {
                return sproot;
            }

            if (wordk.compareTo(sproot.sprightnode.spkey) < 0) {
                sproot.sprightnode.spleftnode = splay(sproot.sprightnode.spleftnode, wordk);
                if (sproot.sprightnode.spleftnode != null) {
                    sproot.sprightnode = rotatesprightnode(sproot.sprightnode);
                }
            } else if (wordk.compareTo(sproot.sprightnode.spkey) > 0) {
                sproot.sprightnode.sprightnode = splay(sproot.sprightnode.sprightnode, wordk);
                sproot = rotatespleftnode(sproot);
            }
            return (sproot.sprightnode == null) ? sproot : rotatespleftnode(sproot);
        }
    }

    private ImplementSplayNode rotatesprightnode(ImplementSplayNode x) {
        ImplementSplayNode y = x.spleftnode;
        x.spleftnode = y.sprightnode;
        y.sprightnode = x;
        return y;
    }

    private ImplementSplayNode rotatespleftnode(ImplementSplayNode x) {
        ImplementSplayNode y = x.sprightnode;
        x.sprightnode = y.spleftnode;
        y.spleftnode = x;
        return y;
    }

    public void insert(String spkey) {
        sproot = insert(sproot, spkey);
        sproot = splay(sproot, spkey);
    }

    private ImplementSplayNode insert(ImplementSplayNode sproot, String spkey) {
        if (sproot == null) {
            return new ImplementSplayNode(spkey);
        }

        int cmp = spkey.compareTo(sproot.spkey);
        if (cmp < 0) {
            sproot.spleftnode = insert(sproot.spleftnode, spkey);
        } else if (cmp > 0) {
            sproot.sprightnode = insert(sproot.sprightnode, spkey);
        } else {
            sproot.spkey_occurences++;
        }

        return sproot;
    }

    public String suggestingCorrection(String searchWord, int EditDistThreshold) {
        EditDistResult result = new EditDistResult();
        result.dist = Integer.MAX_VALUE;
        result.searchword = null;
        ForSuggestingWords(sproot, searchWord, EditDistThreshold, result);
        return result.searchword;
    }

    private void ForSuggestingWords(ImplementSplayNode sproot, String WordMisspelled, int EditDistThreshold, EditDistResult wordresult) {
        if (sproot != null) {
        	
            ForSuggestingWords(sproot.spleftnode, WordMisspelled, EditDistThreshold, wordresult);

            int distance = calceditDistance(WordMisspelled, sproot.spkey.toLowerCase());

            if (distance < wordresult.dist && distance <= EditDistThreshold) {
                wordresult.searchword = sproot.spkey;
                wordresult.dist = distance;
            }
            ForSuggestingWords(sproot.sprightnode, WordMisspelled, EditDistThreshold, wordresult);
        }
    }

    private static class EditDistResult {
        String searchword;
        int dist;
    }

    public static int calceditDistance(String searchWord1, String searchWord2) {
        int searchWord1Len = searchWord1.length();
        int searchWord2Len = searchWord2.length();

        int[][] dp = new int[searchWord1Len + 1][searchWord2Len + 1];

        for (int itm = 0; itm <= searchWord1Len; itm++) {
            dp[itm][0] = itm;
        }

        for (int j = 0; j <= searchWord2Len; j++) {
            dp[0][j] = j;
        }

        for (int itm= 0; itm< searchWord1Len; itm++) {
            char c1 = searchWord1.charAt(itm);
            for (int j = 0; j < searchWord2Len; j++) {
                char c2 = searchWord2.charAt(j);

                if (c1 == c2) {
                    dp[itm+ 1][j + 1] = dp[itm][j];
                } else {
                    int replaceDistance = dp[itm][j] + 1;
                    int insertDistance = dp[itm][j + 1] + 1;
                    int deleteDistance = dp[itm+ 1][j] + 1;

                    int min = replaceDistance > insertDistance ? insertDistance : replaceDistance;
                    min = deleteDistance > min ? min : deleteDistance;
                    dp[itm+ 1][j + 1] = min;
                }
            }
        }

        return dp[searchWord1Len][searchWord2Len];
    }
}

public class SpellChecker {

    Map<String,ImplementSplayTree> spellings;

    public SpellChecker(){
        spellings = new HashMap<>();
        this.spellings.put("city",new ImplementSplayTree());
        this.spellings.put("province",new ImplementSplayTree());
        this.spellings.put("pincode",new ImplementSplayTree());
    }

    public String findCorrectedSpelling(String word, String searchFactor){
        return this.spellings.get(searchFactor).suggestingCorrection(word,3);
    }

    public void buildSpellCheckerSplayTree(){
        Gson gson = new Gson();
        try {
            // Parse JSON array of objects
            JsonArray jsonArray = gson.fromJson(new FileReader("src/main/resources/CombinedProperties.json"), JsonArray.class);
            // Iterate over JSON objects
            for (JsonElement element : jsonArray) {
                JsonObject jsonObject = element.getAsJsonObject();
                JsonElement city = jsonObject.get("city");
                if (city != null) {
                    this.spellings.get("city").insert(city.getAsString().toLowerCase());
                }
                JsonElement province = jsonObject.get("province");
                if(province != null) {
                    this.spellings.get("province").insert(province.getAsString().toLowerCase());
                }
                JsonElement pincode = jsonObject.get("pincode");
                if(pincode != null) {
                    this.spellings.get("pincode").insert(pincode.getAsString().toLowerCase());
                }
            }
        } 
        
        catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        }
        
        catch (IOException e) {
            System.out.println("Error in creating spell checker!");
        
            
        } catch (JsonParseException e) {
            System.out.println("Error parsing JSON: " + e.getMessage());
        }
        
        }
    }

