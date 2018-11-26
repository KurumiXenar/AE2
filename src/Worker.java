import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Worker implements Runnable {

    //static function
    private final static int POISON_PILL_NUM = 0;

    private LinkedBlockingQueue<FileObject> work;
    private LinkedBlockingQueue<FileObject> harvest;
    Pattern p;
    int id;

    public Worker(LinkedBlockingQueue<FileObject> work, LinkedBlockingQueue<FileObject> harvest, int id, Pattern p) {
        this.work = work;
        this.harvest = harvest;
        this.id = id;
        this.p = p;
    }

    @Override
    public void run() {
        while(true){
            try {
                //Check if the next file is a poison pill
                FileObject obj = work.take();
                if(obj.getSafetyNumber() == POISON_PILL_NUM) {
                    work.put(new FileObject(Paths.get(""), POISON_PILL_NUM));
                    break;
                }

                DirectoryStream<Path> stream = Files.newDirectoryStream(obj.getFilePath());

                for(Path path : stream) {
                    if(path.toFile().isFile()) {
                        String str = path.getName(path.getNameCount() - 1).toString();
                        Matcher m = p.matcher(str);
                        if(m.matches()) {
                            harvest.put(new FileObject(path, 1));
                        }
                    }
                }

            } catch (InterruptedException | IOException e){
                e.printStackTrace();
            }
        }
    }

}
