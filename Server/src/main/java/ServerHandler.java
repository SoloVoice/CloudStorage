import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    private Path repositoryPath = Paths.get("ServerRepository");
    private int previousPart;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        FileHandler.sendRepositoryList(repositoryPath, ctx.channel());
        System.out.println("Клиент подключился");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Клиент отключился");
    }

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
                if (previousPart == ((NewSendingFile) msg).getCurrentPart() - 1) {
                    FileHandler.saveFile((NewSendingFile) msg, repositoryPath);
                    previousPart = ((NewSendingFile) msg).getCurrentPart();
                } else {
                    System.out.println("Ошибка последовательности пакетов!");
                }
            }
            FileHandler.sendRepositoryList(repositoryPath, ctx.channel());
        } else if (msg instanceof FileRequest) {
            FileHandler.sendFileByParts(repositoryPath.resolve(((FileRequest) msg).getFileName()), ctx.channel());
        } else if (msg instanceof FileRenamer) {
            FileHandler.rename(repositoryPath, ((FileRenamer) msg).getFileName());
            FileHandler.sendRepositoryList(repositoryPath, ctx.channel());
        } else if (msg instanceof FileDeletter) {
            FileHandler.delete(repositoryPath, (((FileDeletter) msg).getFileName()));
            FileHandler.sendRepositoryList(repositoryPath, ctx.channel());
        } else {
            System.out.println("Неизвестная команда клинта!");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}