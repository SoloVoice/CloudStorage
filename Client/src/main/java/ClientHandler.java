import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ClientHandler extends ChannelInboundHandlerAdapter {
    private Path repositoryPath = Paths.get("ClientRepository");
    private int previousPart;
    private Controller controller;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof NewSendingFile) {
            if (((NewSendingFile) msg).getCurrentPart() == 1) {
                if (!Files.exists(repositoryPath.resolve(((NewSendingFile) msg).getFileName()))) {
                    Files.createFile(repositoryPath.resolve(((NewSendingFile) msg).getFileName()));
                    FileHandler.saveFile((NewSendingFile) msg, repositoryPath);
                    previousPart = ((NewSendingFile) msg).getCurrentPart();
                } else {
                    System.out.println("Файл уже существует!");
                }
            } else {
                if (previousPart == ((NewSendingFile) msg).getCurrentPart()-1) {
                    FileHandler.saveFile((NewSendingFile) msg, repositoryPath);
                    previousPart = ((NewSendingFile) msg).getCurrentPart();
                } else {
                    System.out.println("Ошибка последовательности пакетов!");
                }
            }
            controller.clientFiles.setText(FileHandler.showRepository(FileHandler.createRepositoryList(repositoryPath)));
        } else if (msg instanceof ShowRepository) {
                showFiles((ShowRepository) msg);
        }
    }
    public void setController(Controller controller) {
        this.controller = controller;
    }
    public void showFiles(ShowRepository msg) {
        controller.clientFiles.setText(FileHandler.showRepository(FileHandler.createRepositoryList(repositoryPath)));
        controller.serverFiles.setText(FileHandler.showRepository((ShowRepository) msg));
    }
}
