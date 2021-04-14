public class FileRequest extends CanBeSerializable {
    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public FileRequest(String fileName) {
        this.fileName = fileName;
    }
}
