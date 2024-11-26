/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package filestatistics;

import filestatistics.Utilities.AdvancedString;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.NotDirectoryException;
import java.util.ArrayList;
import java.util.Scanner;

import filestatistics.GUI.interfaces.IUpdateGUI;

public class ThreadFilesAssigner implements Runnable {

    private FileManager fm = null;

    
    private IUpdateGUI GUIUpdater = null;

    public ThreadFilesAssigner(File dir, String type, boolean deepSearch, IUpdateGUI updateGUI) throws FileNotFoundException, NotDirectoryException {
        fm = new FileManager(dir);
        
        GUIUpdater = updateGUI;

        fm.listFiles(dir, type, deepSearch);
    }

    @Override
    public void run() {
        File f = null;

        f = fm.getNextFile(); // file to process

        if (f == null) {
            return; // no files to process
        }

       FileStatistics fileStatistics =  processFile(f);

       
       GUIUpdater.updateGUI(fileStatistics);
       
        run();
    }

    private FileStatistics processFile(File f) {

        FileStatistics res = new FileStatistics();
        AdvancedString aString = new AdvancedString();

        res.name = f.getName();

        try {
            Scanner sc = new Scanner(f);

            while (sc.hasNextLine()) {
                aString.set(sc.nextLine());

                String prevShortestWord = res.shortestWord;
                String prevLongestWord = res.longestWord;

                String currShortestWord = aString.shortestWord();
                String currLongestWord = aString.longestWord();

                res.nOfWords += aString.count(".", false);
                res.nOfIs += aString.count("\\bis\\b", false);
                res.nOfAre += aString.count("\\bare\\b", false);
                res.nOfYou += aString.count("\\byou\\b", false);
                res.shortestWord = prevShortestWord.length() > currShortestWord.length() || prevShortestWord.isEmpty() ? currShortestWord : prevShortestWord;
                res.longestWord = prevLongestWord.length() < currLongestWord.length() ? currLongestWord : prevLongestWord;
            }


        } catch (Exception exc) {
            exc.printStackTrace();
        }

        return res;
    }
}
