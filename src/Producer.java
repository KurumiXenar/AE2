/*
Authorship statement
Name: Nigel Ng
Login: 2427257N
Title of Assignment: APH Exercise 2
This is my own work as defined in the Academic Ethics agreement I
have signed.
 */

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.LinkedBlockingQueue;

public class Producer implements  Runnable {

    //static function
    private final static int POISON_PILL_NUM = 0;
    private static int PRODUCERS_DONE = 0;
    public static int NUM_OF_PRODUCER = 0;

    private LinkedBlockingQueue<FileObject> work;
    private String[] name;
    int id;
    long start;

    public Producer (LinkedBlockingQueue<FileObject> work, String[] name, int i, long start) {
        this.work = work;
        this.name = name;
        this.id = i;
        this.start = start;
    }

    @Override
    public void run() {
        for(int i = 0; i < name.length; i++) {
            Path dir = FileSystems.getDefault().getPath(name[i]);
            try {
                processDirectory(dir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Add Poison pill at end
        try {
            PRODUCERS_DONE++;

            if(PRODUCERS_DONE == NUM_OF_PRODUCER) {
                FileObject fileObj = new FileObject( Paths.get("") ,POISON_PILL_NUM);
                work.put(fileObj);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void processDirectory( Path dir ) throws IOException {
        try {
            DirectoryStream<Path> stream = Files.newDirectoryStream(dir);
            FileObject fileObj = new FileObject(dir, 1);
            work.put(fileObj);
            for (Path path : stream ) {
                if(Files.isDirectory(path)){
                    processDirectory(Paths.get(path.toString()));
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
