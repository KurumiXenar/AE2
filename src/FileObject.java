/*
Authorship statement
Name: Nigel Ng
Login: 2427257N
Title of Assignment: APH Exercise 2
This is my own work as defined in the Academic Ethics agreement I
have signed.
 */

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
