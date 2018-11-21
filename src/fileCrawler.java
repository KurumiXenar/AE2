import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class fileCrawler {

    public static void main(String args[]) {
        LinkedBlockingQueue<String> work = new LinkedBlockingQueue<>();
        LinkedBlockingQueue<String> harvest = new LinkedBlockingQueue<>();
        Pattern p;

        final long startTime = System.currentTimeMillis();

        //number of default Worker Threads
        int numOfWorkers = 2;

        int numberOfProducers = 1;

        String pattern = cvtPattern("dun.h");
        p = Pattern.compile(pattern);

        // intialise Workers
        Thread[] workers = new Thread[numOfWorkers];
        Thread[] producer = new Thread[numberOfProducers];

        for(int i= 0; i < numOfWorkers; i++) {
            workers[i] = new Thread(new Worker(work, harvest, i, p));
        }

        // start the workers
        for (int i = 0; i < numOfWorkers; i++) {
            workers[i].start();
        }


       //if(args.length < 2) {
            producer[0] = new Thread(new Producer(work, ".", 1));
            producer[0].start();
            try {
                producer[0].join(); //Wait until producer is done
            } catch (Exception e) {
                System.exit(0);
            }
        //}
        //else {
        //    producer[0] = new Thread(new Producer(work, args[1], 1));
        //    producer[0].start();
       // }

        // now interrupt all workers and wait for them to finish
        for(int i = 0; i < workers.length; i++) {
            workers[i].interrupt();
            try {
                workers[i].join();
            } catch (Exception e) {

            }
        }

        for (String name : harvest) {
            System.out.println(name);
        }

        // now exit gracefully

    }


    public static String cvtPattern(String str) {
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
