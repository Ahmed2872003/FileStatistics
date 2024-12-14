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

    private String[] filesPathsBuffer = new String[MAX_BUFFER];

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

                                    writerCondition.await();
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }

                            currFileIndex += 1;

                            filesPathsBuffer[currFileIndex] = file.getAbsolutePath();

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
                readerCondition.await();

            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        File requiredFile = new File(filesPathsBuffer[currFileIndex]);

        currFileIndex -= 1;

        writerCondition.signal(); // informing the writer that there is an empty buffer to write in

        mutex.unlock();

        return requiredFile;
    }

}
