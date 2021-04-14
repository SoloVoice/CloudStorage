public class FileRenamer extends CanBeSerializable {
    private String fileName;

    public FileRenamer(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
