/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package filestatistics;

/**
 *
 * @author ahmed
 */
public class FileStatistics {


        public String name = "";
        public int nOfWords = 0;
        public int nOfIs = 0;
        public int nOfAre = 0;
        public int nOfYou = 0;
        public String longestWord = "";
        public String shortestWord = "";

        @Override
        public String toString() {

            return "name = " + name + "\nnOfWords = " + nOfWords + "\nnOfIs = " + nOfIs + "\nnOfAre = " + nOfAre + "\nnOfYou = " + nOfYou + "\nlongestWord = " + longestWord + "\nshortestWord = " + shortestWord + "\n";
        }
        
        public Object[] toObjArray(){
            return new Object[]{ name, nOfWords, nOfIs, nOfAre, nOfYou, longestWord, shortestWord};
        }
}
