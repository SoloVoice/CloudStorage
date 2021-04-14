public class ShowRepository extends CanBeSerializable {
    String[] filesList;

    public ShowRepository(String[] filesList) {
        this.filesList = filesList;
    }

    public String[] getFilesList() {
        return filesList;
    }
}
