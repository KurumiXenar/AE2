import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Pattern;

public class fileCrawler {

    private static LinkedBlockingQueue<FileObject> work = new LinkedBlockingQueue<>();
    private static LinkedBlockingQueue<FileObject> harvest = new LinkedBlockingQueue<>();
    private static Pattern p;

    public static void main(String args[]) {

        final long startTime = System.currentTimeMillis();

        //number of default Worker Threads
        int numOfWorkers = 4;

        //number of default Producer Threads
        int numberOfProducers = 2;
        if(args.length > 2) {
            numberOfProducers = args.length - 1;
        }
        Producer.NUM_OF_PRODUCER = numberOfProducers;

        String pattern = cvtPattern("dunh.tgz");
        p = Pattern.compile(pattern);

        // Initialise Workers
        Thread[] workers = new Thread[numOfWorkers];
        Thread[] producer = new Thread[numberOfProducers];

        for(int i= 0; i < numOfWorkers; i++) {
            workers[i] = new Thread(new Worker(work, harvest, i, p));
        }

        // start the workers
        for (int i = 0; i < numOfWorkers; i++) {
            workers[i].start();
        }

        for (int i = 0; i < numberOfProducers; i++) {
            producer[i] = new Thread(new Producer(work, args[1], 1));
            producer[i].start();
        }

        // now interrupt all workers and wait for them to finish
        for(int i = 0; i < workers.length; i++) {
            try {
                workers[i].join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (FileObject fileObj : harvest) {
            System.out.println(fileObj.getFileName());
        }

        // now exit gracefully
        System.out.println(startTime);

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
