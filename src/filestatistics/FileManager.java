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

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileManager {

    private ArrayList<File> matchedFiles = new ArrayList();

    private File directory = null;

    private int nextFile = 0;

    private ReentrantLock mutex = new ReentrantLock();
    private Condition reader = mutex.newCondition();

    public FileManager(File dir) throws FileNotFoundException, NotDirectoryException {
        if (dir.isFile()) {
            throw new NotDirectoryException("This is not a directory");
        }

        if (!dir.isDirectory()) {
            throw new FileNotFoundException("This directory doesn't exist");
        }
        directory = dir;
    }

    public void listFiles(String type, boolean deepSearch) {
        listFilesRecursive(directory, type, deepSearch);
    }

    private void listFilesRecursive(File dir, String type, boolean deepSearch) {
        if (dir.exists() && dir.isDirectory()) {
            // Get all files and subdirectories in the current directory
            File[] files = dir.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory() && deepSearch) {
                        // If it's a directory, recurse into it
                        listFilesRecursive(file, type, deepSearch);
                    } else {
                        // If it's a file, check if it matches the filter
                        if (file.getName().endsWith(type)) {
                            mutex.lock();

                            matchedFiles.add(new File(file.getAbsolutePath()));

                            reader.signal();

                            mutex.unlock();

                        }
                    }
                }
            }
        }
    }

    public File getNextFile() {
        mutex.lock();

        try {
            while (matchedFiles.isEmpty() || nextFile >= matchedFiles.size()) {
                reader.await();
            }

        } catch (InterruptedException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        File requiredFile = matchedFiles.get(nextFile++);

        mutex.unlock();

        return requiredFile;
    }

}
