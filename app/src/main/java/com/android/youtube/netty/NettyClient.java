package com.android.youtube.netty;

import android.util.Log;

import com.google.protobuf.ExtensionRegistryLite;

import java.net.InetSocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import pb.ConnExt;

public class NettyClient {
    private static final String TAG = "NettyClient";
    //连接的服务端ip地址
    private static NettyClient nettyClient;
    //与服务端的连接通道
    private Channel channel;

    public static NettyClient getInstance() {
        if (nettyClient == null) {
            nettyClient = new NettyClient();
        }
        return nettyClient;
    }
    /**
     *需要在子线程中发起连接
     */
    private NettyClient() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                connect();
            }
        }).start();
    }
    /**
     * 获取与服务端的连接
     */
    public static Channel getChannel() {
        if (nettyClient == null) {
            return null;
        }
        return nettyClient.channel;
    }
    /**
     * 连接服务端
     */
    public void connect() {
        try {
            NioEventLoopGroup group = new NioEventLoopGroup();
            Bootstrap bootstrap = new Bootstrap()
                    // 指定channel类型
                    .channel(NioSocketChannel.class)
                    // 指定EventLoopGroup
                    .group(group)
                    // 指定Handler
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            //解码器，通过Google Protocol Buffers序列化框架动态的切割接收到的ByteBuf
                            pipeline.addLast(new ProtobufVarint32FrameDecoder());
                            //服务器端接收的是客户端RequestUser对象，所以这边将接收对象进行解码生产实列
                            //Google Protocol Buffers编码器
                            pipeline.addLast(new ProtobufDecoder(ConnExt.Message.getDefaultInstance()));
                            pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
                            //Google Protocol Buffers编码器
                            pipeline.addLast(new ProtobufEncoder());
                            pipeline.addLast(new ProtoClientHandler());
                        }
                    });
            // 连接到服务端
            ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress(Const.MSG_SOCKET_HOST, Const.MSG_SOCKET_PORT));
            //获取连接通道
            channel = channelFuture.sync().channel();
        } catch (Exception e) {
            Log.e(TAG, "连接失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
}
