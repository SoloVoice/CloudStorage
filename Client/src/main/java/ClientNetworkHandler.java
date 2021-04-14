import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ClientNetworkHandler {
    private Path repPath = Paths.get("ClientRepository");
    private SocketChannel channel;
    private static final String HOST = "localhost";
    private static final int PORT = 8189;
    private ClientHandler clientHandler = new ClientHandler();

    public ClientNetworkHandler() {
        new Thread(() -> {
            EventLoopGroup mainG = new NioEventLoopGroup();
            try {
                Bootstrap b = new Bootstrap();
                b.group(mainG)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                channel = socketChannel;
                                socketChannel.pipeline().addLast(
                                        new ObjectEncoder(),
                                        new ObjectDecoder(150*1024*1024,ClassResolvers.cacheDisabled(null)),
                                        clientHandler
                                );
                            }
                        });
                ChannelFuture f = b.connect(HOST, PORT).sync();
                f.channel().closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                mainG.shutdownGracefully();
            }
        }).start();
    }

    public Path getRepPath() {
        return repPath;
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public ClientHandler getClientHandler() {
        return clientHandler;
    }
}
