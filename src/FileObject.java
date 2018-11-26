import java.nio.file.*;

public class FileObject {

    private Path filePath;
    private int safetyNumber;

    public FileObject(Path filePath, int safetyNumber) {
        this.filePath = filePath;
        this.safetyNumber = safetyNumber;
    }

    public Path getFilePath() { return filePath; }

    public void setFilePath(Path filePath) {
        this.filePath = filePath;
    }

    public int getSafetyNumber() {
        return safetyNumber;
    }

    public void setSafetyNumber(int safetyNumber) {
        this.safetyNumber = safetyNumber;
    }

}
