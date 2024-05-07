package io.netty.handler.damon;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.string.StringEncoder;

/*
 * Netty的Client
 * 功能:向server发送控制台输入的消息，并接收server发回的消息并显示
 * */
public class NettyServer {
    public void start(int port) throws Exception {
        ServerBootstrap strap = new ServerBootstrap();
        //主线程
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //从线程
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            strap.group(bossGroup, workerGroup)
                    //主线程监听通道
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    //定义从线程的handler链，责任链模式
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast("encoderByte", new ByteArrayEncoder());
                            ch.pipeline().addLast("encoderStr", new StringEncoder());
                            // 自定义handler
                            ch.pipeline().addLast();
                        }
                    });
            ChannelFuture future = strap.bind(port).sync();
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    public static void main(String[] args) throws Exception {
        System.out.println("start server");
        new NettyServer().start(8000);
    }
}