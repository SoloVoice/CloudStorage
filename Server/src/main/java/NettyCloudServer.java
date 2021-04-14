import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class NettyCloudServer {
    public void run() throws Exception{
        EventLoopGroup mainGroup = new NioEventLoopGroup(1);
        EventLoopGroup secondaryGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(mainGroup, secondaryGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(
                                    new ObjectEncoder(),
                                    new ObjectDecoder(150*1024*1024, ClassResolvers.cacheDisabled(null)),
                                    new ServerHandler()
                            );
                        }
                    });
            ChannelFuture f = serverBootstrap.bind(8189).sync();
            System.out.println("Сервер запущен");
            f.channel().closeFuture().sync();
            System.out.println("Сервер остановлен");
        } finally {
            mainGroup.shutdownGracefully();
            secondaryGroup.shutdownGracefully();
        }
    }
}
