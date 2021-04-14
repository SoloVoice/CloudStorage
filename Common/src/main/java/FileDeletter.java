public class FileDeletter extends CanBeSerializable {
    private String fileName;

    public FileDeletter(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
