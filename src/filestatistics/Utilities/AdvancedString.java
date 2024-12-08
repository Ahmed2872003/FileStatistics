/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package filestatistics.Utilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdvancedString {

    private String str = null;

    public AdvancedString(String s) {
        str = s;
    }

    public AdvancedString() {
        str = "";
    }

    public String get() {
        return str;
    }

    public void set(String s) {
        str = s;
    }

    public int count(String patternStr, boolean sensitive) {
        int count = 0;

        Pattern pattern = Pattern.compile(patternStr, (sensitive? 0 : Pattern.CASE_INSENSITIVE));

        Matcher matcher = pattern.matcher(str);

        while(matcher.find()) count++;
            
        return count;
    }

    public String longestWord() {
        String lw = "";

        for (String word : str.trim().split("\\s+")) {
            if (lw.length() < word.length()) {
                lw = word;
            }
        }

        return lw;
    }
    
    public String shortestWord() {
        String[] splittedString = str.trim().split("\\s+");
        
        
        String sw = splittedString.length > 0? splittedString[0] : "";

        for (String word : splittedString) {
            if (sw.length() > word.length()) {
                sw = word;
            }
        }

        return sw;
    }

}
