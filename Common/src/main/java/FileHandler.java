import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.stream.Stream;

public interface FileHandler {
    static void fileRequest(SocketChannel channel, String fileName) {
        channel.writeAndFlush(new FileRequest(fileName));
    }

    static void renameRequest(Channel channel, String fileName) {
        channel.writeAndFlush(new FileRenamer(fileName));
    }

    static void deleteRequest(Channel channel, String fileName) {
        channel.writeAndFlush(new FileDeletter(fileName));
    }

    static void sendFileByParts(Path path, Channel channel) {
        new Thread(() -> {
            File file = new File(path.toString());
            int bufferSize = 1024 * 1024;
            byte[] data = new byte[1024 * 1024];
            int currentChunk = 1;
            int numbersOfChunks = (int) (file.length() / bufferSize);
            if (file.length() % bufferSize != 0) {
                numbersOfChunks += 1;
            }
            try {
                FileInputStream in = new FileInputStream(file);
                while (currentChunk <= numbersOfChunks) {
                    try {
                        if (in.available() >= bufferSize) {
                            in.read(data);
                        } else {
                            data = new byte[in.available()];
                            in.read(data);
                        }

                    } catch (IOException e) {
                        System.out.println("Невозможно прочитать фаил!");
                    }
                    channel.writeAndFlush(new NewSendingFile(path.getFileName().toString(), numbersOfChunks, currentChunk, data));
                    currentChunk += 1;
                    Thread.sleep(50);
                }
                in.close();
            } catch (FileNotFoundException e) {
                System.out.println("Файл не найден!");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    static void saveFile(NewSendingFile filePackage, Path path) {
        System.out.println(filePackage.getFileName() + " " + filePackage.getCurrentPart() + " / " + filePackage.getParts() + " | " + filePackage.getData()[0] + " - " + filePackage.getData()[filePackage.getData().length - 1]);
        try {
            Files.write(path.resolve(filePackage.getFileName()), filePackage.getData(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void rename(Path path, String oldNameNewName) {
        String[] names = oldNameNewName.split(" ");
        try {
            Files.move(path.resolve(names[0]), path.resolveSibling(path.resolve(names[1])));
            System.out.println("Фаил " + names[0] + " переименован в " + names[1]);
        } catch (IOException e) {
            System.out.println("Невозможно переименовать файл");
        }
    }

    static void delete(Path path, String fileName) {
        try {
            Files.delete(path.resolve(fileName));
            System.out.println("Фаил " + fileName + " удален!");
        } catch (IOException e) {
            System.out.println("Невозможно удалить фаил!");
        }
    }

    static ShowRepository createRepositoryList(Path path) {
        ArrayList<Path> listA = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(path)) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(listA::add);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] stringList = new String[listA.size()];
        for (int i = 0; i <= stringList.length-1; i++) {
            stringList[i] = String.valueOf(listA.get(i).getFileName());
        }
        return (new ShowRepository(stringList));
    }

    static void sendRepositoryList(Path path, Channel channel) {
        ShowRepository repository = createRepositoryList(path);
        channel.writeAndFlush(repository);
    }

    static String showRepository(ShowRepository repository) {
        StringBuilder sb = new StringBuilder();
        for (String i: repository.getFilesList()) {
            System.out.println(i);
            sb.append(i+"\n");
        }
        return sb.toString();
    }
}
