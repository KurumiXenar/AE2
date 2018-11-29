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
            int numberOfDirPerProducer = (args.length - 1)/2;
            String pattern;
            String[] dir1;
            String[] dir2;
            switch (args.length) {
                case 1:
                    numberOfProducers = 1;
                    pattern = cvtPattern(args[0]);
                    dir1 = new String[1];
                    dir1[0] = ".";
                    dir2 = new String[0];
                    break;
                case 2:
                    numberOfProducers = 1;
                    pattern = cvtPattern(args[0]);
                    dir1 = new String[1];
                    dir1[0] = args[1];
                    dir2 = new String[0];
                    break;
                default:
                    pattern = cvtPattern(args[0]);
                    dir1 = new String[numberOfDirPerProducer];
                    for(int i = 0; i < numberOfDirPerProducer; i++){
                        dir1[i] = args[i+1];
                    }
                    if(args.length % 2 == 1){
                        dir2 = new String[numberOfDirPerProducer + 1];
                        for(int i = 0, j = numberOfDirPerProducer + 1; j < args.length; i++){
                            dir2[i] = args[j];
                            j++;
                        }
                    }
                    else{
                        dir2 = new String[numberOfDirPerProducer];
                        for(int i = 0, j = numberOfDirPerProducer + 1; j < args.length; i++){
                            dir2[i] = args[j];
                            j++;
                        }
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

            producer[0] = new Thread(new Producer(work, dir1, 1, start));
            producer[0].start();

            if(numberOfProducers == 2) {
                producer[1] = new Thread(new Producer(work, dir2, 2, start));
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
