public class NewSendingFile extends CanBeSerializable{
    private int parts;
    private int currentPart;
    private String fileName;
    private byte[] data;

    public NewSendingFile(String fileName, int parts, int currentPart, byte[] data) {
        this.fileName = fileName;
        this.parts = parts;
        this.currentPart = currentPart;
        this.data = data;
        System.out.println("Отправлен фаил " + fileName + ". Пакет " + currentPart + " из " + parts + " | " + data[0] + " - " + data[data.length - 1]);
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getData() {
        return data;
    }

    public int getCurrentPart() {
        return currentPart;
    }

    public int getParts() {
        return parts;
    }
}
