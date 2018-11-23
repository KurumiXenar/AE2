public class FileObject {

    private String fileName;
    private int safetyNumber;

    public FileObject(String fileName, int safetyNumber) {
        this.fileName = fileName;
        this.safetyNumber = safetyNumber;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getSafetyNumber() {
        return safetyNumber;
    }

    public void setSafetyNumber(int safetyNumber) {
        this.safetyNumber = safetyNumber;
    }

}
