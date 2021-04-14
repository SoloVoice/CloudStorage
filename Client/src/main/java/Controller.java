import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    private ClientNetworkHandler client;
    private Path clientRepository = Paths.get("ClientRepository");

    @FXML
    TextField messageField;

    @FXML
    TextArea serverFiles;

    @FXML
    TextArea clientFiles;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Клиент запущен");
    }

//    Вот тут адский колбек, как мне кажется
    public void connectToServerAction (ActionEvent actionEvent) {
        client = new ClientNetworkHandler();
        client.getClientHandler().setController(this);
    }

    public void fileRequest (ActionEvent actionEvent) {
        FileHandler.fileRequest(client.getChannel(), messageField.getText());
        FileHandler.sendRepositoryList(clientRepository, client.getChannel());
        messageField.clear();
        messageField.requestFocus();
    }

    public void sendFile(ActionEvent actionEvent) {
        FileHandler.sendFileByParts(client.getRepPath().resolve(messageField.getText()), client.getChannel());
        messageField.clear();
        messageField.requestFocus();
    }

    public void renameFile(ActionEvent actionEvent) {
        FileHandler.renameRequest(client.getChannel(), messageField.getText());
        messageField.clear();
        messageField.requestFocus();
    }

    public void deleteFile(ActionEvent actionEvent) {
        FileHandler.deleteRequest(client.getChannel(), messageField.getText());
        messageField.clear();
        messageField.requestFocus();
    }

}
