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

    final private int MAX_BUFFER = 20;

    private File[] filesBuffer = new File[MAX_BUFFER];

    private int currFileIndex = -1;
    
    private File directory = null;

    private ReentrantLock mutex = new ReentrantLock();

    private Condition readerCondition = mutex.newCondition();

    private Condition writerCondition = mutex.newCondition();

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

                            while (currFileIndex == MAX_BUFFER - 1) {
                                try {
//                                    System.out.println(Thread.currentThread().getName() + " waiting to add file...");

                                    writerCondition.await();
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }

                            currFileIndex+=1;
                            
                            filesBuffer[currFileIndex] = new File(file.getAbsolutePath());
//                            System.out.println(Thread.currentThread().getName() + " added file, currFileIndex = " + currFileIndex);

                            readerCondition.signal();

                            mutex.unlock();

                        }
                    }
                }
            }
        }
    }

    public File getNextFile() {
        mutex.lock();

        while (currFileIndex == -1) { // All the files are processed or there is no files in the buffer
            try {
//                System.out.println(Thread.currentThread().getName() + " waiting for files...");

                readerCondition.await();

            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

//        System.out.println(filesBuffer[currFileIndex] + " With index: " + currFileIndex + " is being processed");

        File requiredFile = filesBuffer[currFileIndex];
        
        currFileIndex-=1;

        writerCondition.signal(); // informing the writer that there is an empty buffer to write in

        mutex.unlock();

        return requiredFile;
    }

}
