/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package filestatistics;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;


public class FileManager {
    
    private ArrayList<File> matchedFiles = new ArrayList();
    
    private File directory = null;
    
    private int nextFile = 0;
    
    public FileManager(File dir) throws FileNotFoundException, NotDirectoryException{
        if(dir.isFile()) throw new NotDirectoryException("This is not a directory");
        
        if(!dir.isDirectory()) throw new FileNotFoundException("This directory doesn't exist");
       
        
        
        directory = dir;
    }

    public void listFiles(File dir, String type, boolean deepSearch) {
        if (dir.exists() && dir.isDirectory()) {
            // Get all files and subdirectories in the current directory
            File[] files = dir.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory() && deepSearch) {
                        // If it's a directory, recurse into it
                        listFiles(file, type, deepSearch);
                    } else {
                        // If it's a file, check if it matches the filter
                        if (file.getName().endsWith(type)) {
                            matchedFiles.add(new File(file.getAbsolutePath()));
                        }
                    }
                }
            }
        }
    }

    public synchronized File getNextFile(){
        if(matchedFiles.isEmpty() || nextFile >= matchedFiles.size()) return null;
        
        return matchedFiles.get(nextFile++);
    }
    

}
