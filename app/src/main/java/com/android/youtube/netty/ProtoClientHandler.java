package com.android.youtube.netty;

import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.android.youtube.App;
import com.android.youtube.entity.DaoMaster;
import com.android.youtube.entity.DaoSession;
import com.android.youtube.entity.Message;
import com.android.youtube.entity.MessageDao;
import com.android.youtube.utils.DBUtils;

import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import pb.ConnExt;

public class ProtoClientHandler extends SimpleChannelInboundHandler<ConnExt.Output> {
    private static final String TAG = "ProtoClientHandler";
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ConnExt.Output msg) throws Exception {
        Log.d(TAG, "收到服务器消息 "+msg+" ");
        if(msg.getType() == ConnExt.PackageType.PT_MESSAGE){
            ConnExt.Message message = ConnExt.Message.parseFrom(msg.getData());
            Log.i(TAG, "channelRead0: "+message);
            Message entity = new Message();
            ConnExt.MessageItem item = message.getMessage();
            entity.setMessage_content(ConnExt.Text.parseFrom(item.getMessageContent()).getText());
            entity.setReceiver_id((int) item.getReceiverId());
            entity.setMessage_type(item.getMessageTypeValue());
            entity.setReceiver_type(item.getReceiverTypeValue());
            entity.setSend_time(item.getSendTime());
            entity.setSender_device_id((int) item.getSenderDeviceId());
            entity.setSender_type(item.getSenderTypeValue());
            entity.setSeq(item.getSeq());
            entity.setStatus(item.getStatusValue());
            entity.setSender_type(item.getSenderTypeValue());
            entity.setSender_id((int) item.getSenderId());
            entity.setReceiver_id((int) item.getRequestId());
            DBUtils.getInstance().insertMessage(entity);
            Looper.prepare();
            try {
                Toast.makeText(App.app,"收到消息",Toast.LENGTH_SHORT).show();
            }catch (Exception e) {
                Log.e("error",e.toString());
            }
            Looper.loop();
        }
    }



    /**
     * 与服务端连接成功的回调
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        Log.d(TAG, "与服务端连接成功：" + ctx.toString());
        this.ctx = ctx;
        ConnExt.SignInInput.Builder builder = ConnExt.SignInInput.newBuilder();

        ConnExt.SignInInput signInInput = builder
                .setDeviceId(App.user.getDeviceId())
                .setToken(App.user.getToken())
                .setUserId(App.user.getUserId())
                .build();

        ConnExt.Input build = ConnExt.Input.newBuilder().setType(ConnExt.PackageType.PT_SIGN_IN)
                .setRequestId(System.currentTimeMillis())
                .setData(signInInput.toByteString()).build();
//        ChannelPromise promise = ctx.newPromise();
        ctx.writeAndFlush(build);

        Log.d(TAG, "与服务端连接成功："+App.user.getToken()+"  "+App.user.getUserId()+"  ");


//        ConnExt.SyncInput build = ConnExt.SyncInput.newBuilder().setSeq(1).build();
//        ctx.writeAndFlush(build);

    }

    private ChannelHandlerContext ctx;
    private ChannelPromise promise;

    public synchronized ChannelPromise sendMessage(Object message) {
        while (ctx == null) {
            try {
                TimeUnit.MILLISECONDS.sleep(1);
                //logger.error("等待ChannelHandlerContext实例化");
            } catch (InterruptedException e) {
            }
        }
        promise = ctx.newPromise();
        ctx.writeAndFlush(message);
        return promise;
    }


    /**
     * 与服务端断开的回调
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        Log.d(TAG, "与服务端断开连接：" + ctx.toString());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        Log.d(TAG, "与服务端断开连接exce：" + ctx.toString()+" "+cause.getMessage());

    }
}