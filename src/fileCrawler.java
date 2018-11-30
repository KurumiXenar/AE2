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
        if(args.length < 1){
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
            int numberOfProducers = 2;
            String pattern;
            switch (args.length) {
                case 1:
                    numberOfProducers = 1;
                    pattern = cvtPattern(args[0]);
                    break;
                case 2:
                    numberOfProducers = 1;
                    pattern = cvtPattern(args[0]);
                    break;
                default:
                    numberOfProducers = 2;
                    pattern = cvtPattern(args[0]);
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


            if(args.length < 2){
                Producer producer1 = new Producer(work, 1, start);
                producer1.nameAdd(".");
                producer[0] = new Thread(producer1);
                producer[0].start();
            }
            else if (args.length == 2){
                Producer producer1 = new Producer(work, 1, start);
                producer1.nameAdd(args[1]);
                producer[0] = new Thread(producer1);
                producer[0].start();

            }
            else {
                Producer producer1 = new Producer(work, 1, start);
                Producer producer2 = new Producer(work, 2, start);
                for(int i = 1; i < args.length; i+=2){
                    producer1.nameAdd(args[i]);
                    if(i+1 < args.length) {
                        producer2.nameAdd(args[i+1]);
                    }
                }
                producer[0] = new Thread(producer1);
                producer[1] = new Thread(producer2);
                producer[0].start();
                producer[1].start();

            }


            for (int i = 0; i < producer.length; i++) {
                try {
                    producer[i].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            long prodEnd = System.currentTimeMillis();

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

            // now exit gracefully
            long end = System.currentTimeMillis();
            System.out.println("\nProducer elapsed time: " + (prodEnd - start) + " milliseconds");
            System.out.println("\nElapsed time: " + (end - start) + " milliseconds");
            System.out.println("\n Difference in elapsed time: " + (end - prodEnd) + " milliseconds");

        }

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
