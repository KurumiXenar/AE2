import java.io.File;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Worker implements Runnable {

    private LinkedBlockingQueue<String> work;
    private LinkedBlockingQueue<String> harvest;
    private LinkedList<String> currentHarvest;
    Pattern p;
    int id;

    public Worker(LinkedBlockingQueue<String> work, LinkedBlockingQueue<String> harvest, int id, Pattern p) {
        this.work = work;
        this.harvest = harvest;
        this.id = id;
        this.p = p;
    }

    @Override
    public void run() {
        while(true){
            try {
                //Do sth
                File dir = new File(work.take());
                File files[] = dir.listFiles();

                for(File file : files) {
                    if(file.isFile()) {
                        String str = file.getName();
                        System.out.println("Worker " + id + " is printing file: " + str);
                        Matcher m = p.matcher(str);
                        if(m.matches()) {
                            harvest.add(dir.getAbsolutePath() + '/' + str);
                        }
                    }
                }

            } catch (InterruptedException e){

            }
        }
    }

}
