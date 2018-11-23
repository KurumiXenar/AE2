import java.io.File;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

public class Producer implements  Runnable {

    //static function
    private final static int POISON_PILL_NUM = 0;
    private static int PRODUCERS_DONE = 0;
    public static int NUM_OF_PRODUCER = 0;

    private LinkedBlockingQueue<FileObject> work;
    private String name;
    int id;

    public Producer (LinkedBlockingQueue<FileObject> work, String name, int i) {
        this.work = work;
        this.name = name;
        this.id = i;
    }

    @Override
    public void run() {

        processDirectory(name);
        // Add Poison pill at end
        try {
            PRODUCERS_DONE++;

            if(PRODUCERS_DONE == NUM_OF_PRODUCER) {
                FileObject fileObj = new FileObject("empty", POISON_PILL_NUM);
                work.put(fileObj);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void processDirectory( String name ) {
        try {
            File file = new File(name); // create a File object
            if (file.isDirectory()) { // a directory - could be symlink
                String entries[] = file.list();
                if (entries != null) { // not a symlink
                    FileObject fileObj = new FileObject(name, 1);
                    work.put(fileObj);
                    for (String entry : entries ) {
                        if (entry.compareTo(".") == 0)
                            continue;
                        if (entry.compareTo("..") == 0)
                            continue;
                        processDirectory(name+"/"+entry);
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
