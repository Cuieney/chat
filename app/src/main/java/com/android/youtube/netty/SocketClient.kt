package com.xiaomeng.wxbridge.common.netty

//import Jubo.JuLiao.IM.Wx.Proto.DeviceAuthReq
//import Jubo.JuLiao.IM.Wx.Proto.TransportMessageOuterClass
//import com.xiaomeng.wxbridge.xpose.JuKe.Companion.log
//import io.netty.bootstrap.Bootstrap
//import io.netty.channel.*
//import io.netty.channel.nio.NioEventLoopGroup
//import io.netty.channel.socket.nio.NioSocketChannel
//import io.netty.handler.codec.protobuf.ProtobufDecoder
//import kotlinx.coroutines.GlobalScope
//import kotlinx.coroutines.async
//import java.net.InetSocketAddress
//import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by cuieney on 2019/4/24.
 *
 */
class SocketClient {
//
//    companion object {
//        var IMEI = ""
//
//        @JvmStatic
//        val instance: SocketClient by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
//            SocketClient()
//        }
//    }
//
//    var host = ""
//    var port = ""
//    var channel: Channel? = null
//    var authToken: String = ""
//    @Volatile
//    var isConnectSuccess = false
//    private val retryTimes = AtomicInteger()
//    private var maxRetryTimes: Int = 1000
//
//    init {
//        createClient()
//    }
//
//    public fun createClient() {
//        GlobalScope.async {
//            if(host=="" || port==""){
//                log("未获取到主机信息$host:$port")
//                return@async
//            }
//
//            val loopGroup = NioEventLoopGroup()
//
//            val bootStrap = Bootstrap()
//            bootStrap.channel(NioSocketChannel::class.java)
//            bootStrap.group(loopGroup)
//            bootStrap.option(ChannelOption.TCP_NODELAY, true) // 不延迟，直接发送
//                    .option(ChannelOption.SO_BACKLOG, 1024) // 设置tcp缓冲区
//                    .option(ChannelOption.SO_SNDBUF, 32 * 1024) // 设置发送缓冲大小
//                    .option(ChannelOption.SO_RCVBUF, 32 * 1024) // 这是接收缓冲大小
//                    .option(ChannelOption.SO_KEEPALIVE, true) // 保持连接
//            bootStrap.handler(object : ChannelInitializer<Channel>() {
//                /**
//                 * This method will be called once the [Channel] was registered. After the method returns this instance
//                 * will be removed from the [ChannelPipeline] of the [Channel].
//                 *
//                 * @param ch            the [Channel] which was registered.
//                 * @throws Exception    is thrown if an error occurs. In that case it will be handled by
//                 * [.exceptionCaught] which will by default close
//                 * the [Channel].
//                 */
//                override fun initChannel(ch: Channel?) {
//                    val pipeline = ch!!.pipeline()
//                    pipeline.addLast(SelfDecoder())
//                    pipeline.addLast(ProtobufDecoder(TransportMessageOuterClass.TransportMessage.getDefaultInstance()))
//                    pipeline.addLast(SelfEncoder())
//                    pipeline.addLast(SocketProtoHandler())
//
//                }
//            })
//
//            bootStrap.connect(InetSocketAddress(host, port.toInt()))
//                    .addListener {
//                        val futureListener = it as ChannelFuture
//                        val socketChannel = futureListener.channel() as Channel
//                        isConnectSuccess = it.isSuccess
//                        if (it.isSuccess) {
//                            log("connect netty server success")
//                            channel = socketChannel
//                            authReq()
//                        } else {
//                            log("connect netty server fail " + it.cause())
//                            socketChannel.close()
//                            Thread.sleep(3000)
//                            loopGroup.shutdownGracefully()
//                            createClient()
//
//                        }
//
//                    }
//        }
//    }
//
//    fun authReq() {
//        val deviceMsg = DeviceAuthReq.DeviceAuthReqMessage.newBuilder()
//                .setAuthType(DeviceAuthReq.DeviceAuthReqMessage.EnumAuthType.DeviceCode)
//                .setCredential(SocketClient.IMEI)
//                .build()
//
//
//        val message = TransportMessageOuterClass.TransportMessage.newBuilder()
//                .setMsgType(TransportMessageOuterClass.EnumMsgType.DeviceAuthReq)
//                .setContent(com.google.protobuf.Any.pack(deviceMsg))
//                .build()
//
//        channel!!.writeAndFlush(message)
//
//
//    }

}