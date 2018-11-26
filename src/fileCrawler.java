/*
Authorship statement
Name: Nigel Ng
Login: 2427257N
Title of Assignment: APH Exercise 2
This is my own work as defined in the Academic Ethics agreement I
have signed.
 */

import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Pattern;

public class fileCrawler {

    private static LinkedBlockingQueue<FileObject> work = new LinkedBlockingQueue<>();
    private static LinkedBlockingQueue<FileObject> harvest = new LinkedBlockingQueue<>();
    private static Pattern p;

    public static void main(String args[]) {

        final long start = System.currentTimeMillis();
        if(args.length < 1 || args.length > 2){
            System.out.println("The usage string is : java -cp . fileCrawler pattern [directory]\n");
        }
        else {
            //number of default Worker Threads
            int numOfWorkers = 2;
            String threads = System.getenv("CRAWLER_THREADS");
            if (threads != null) {
                numOfWorkers = Integer.valueOf(threads);
            }


            //number of default Producer Threads
            int numberOfProducers = args.length - 1;
            String pattern;
            String[] dir = new String[numberOfProducers];
            switch (args.length) {
                case 1:
                    pattern = cvtPattern(args[0]);
                    dir[0] = ".";
                    break;
                case 2:
                    pattern = cvtPattern(args[0]);
                    dir[0] = args[1];
                    break;
                default:
                    pattern = cvtPattern(args[0]);
                    for(int i = 0; i < numberOfProducers; i++){
                        dir[i] = args[i+1];
                    }
                    break;
            }

            Producer.NUM_OF_PRODUCER = numberOfProducers;

            p = Pattern.compile(pattern);

            // Initialise Workers
            Thread[] workers = new Thread[numOfWorkers];
            Thread[] producer = new Thread[numberOfProducers];

            for (int i = 0; i < numOfWorkers; i++) {
                workers[i] = new Thread(new Worker(work, harvest, i, p));
            }

            // start the workers
            for (int i = 0; i < numOfWorkers; i++) {
                workers[i].start();
            }

            for (int i = 0; i < numberOfProducers; i++) {
                producer[i] = new Thread(new Producer(work, dir[i], i, start));
                producer[i].start();
            }

            for (int i = 0; i < producer.length; i++) {
                try {
                    producer[i].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // now interrupt all workers and wait for them to finish
            for (int i = 0; i < workers.length; i++) {
                try {
                    workers[i].join();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            for (FileObject fileObj : harvest) {
                System.out.println(fileObj.getFilePath());
            }
        }
        // now exit gracefully
        long end = System.currentTimeMillis();
        System.out.println("\nElapsed time: " + (end - start) + " milliseconds");

    }


    private static String cvtPattern(String str) {
        StringBuilder pat = new StringBuilder();
        int start, length;

        pat.append('^');
        if (str.charAt(0) == '\'') {	// double quoting on Windows
            start = 1;
            length = str.length() - 1;
        } else {
            start = 0;
            length = str.length();
        }
        for (int i = start; i < length; i++) {
            switch(str.charAt(i)) {
                case '*': pat.append('.'); pat.append('*'); break;
                case '.': pat.append('\\'); pat.append('.'); break;
                case '?': pat.append('.'); break;
                default:  pat.append(str.charAt(i)); break;
            }
        }
        pat.append('$');
        return new String(pat);
    }
}
