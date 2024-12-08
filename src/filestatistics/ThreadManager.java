
package filestatistics;

import java.util.ArrayList;


public class ThreadManager {

    private ArrayList<Thread> threads = new ArrayList();

    public ThreadManager(int nOfThreads, Runnable r) {
        for (int i = 0; i < nOfThreads; i++) {
            threads.add(new Thread(r, "Thread " + i));
        }
    }

    public void startThreads() {
        if (threads.isEmpty()) {
            return;
        }

        for (Thread t : threads) {
//            if (!t.isAlive()) {
                t.start();
//            }
        }
    }
}
