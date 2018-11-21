import java.io.File;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

public class Producer implements  Runnable {

    private LinkedBlockingQueue<String> work;
    private String name;
    int id;

    public Producer (LinkedBlockingQueue<String> work, String name, int i) {
        this.work = work;
        this.name = name;
        this.id = i;
    }

    @Override
    public void run() {
            processDirectory(name);
    }

    private void processDirectory( String name ) {
        try {
            File file = new File(name); // create a File object
            if (file.isDirectory()) { // a directory - could be symlink
                String entries[] = file.list();
                if (entries != null) { // not a symlink
                    work.add(name);
                    System.out.println("Pro " + id + " is printing dir: " + name);
                    for (String entry : entries ) {
                        if (entry.compareTo(".") == 0)
                            continue;
                        if (entry.compareTo("..") == 0)
                            continue;
                        processDirectory(name+"/"+entry);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error processing "+name+": "+e);
        }
    }
}
