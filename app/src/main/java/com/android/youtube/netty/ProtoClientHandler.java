package com.android.youtube.netty;

import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.android.youtube.App;
import com.android.youtube.entity.Friend;
import com.android.youtube.entity.Message;
import com.android.youtube.entity.NewFriend;
import com.android.youtube.utils.DBUtils;
import com.google.protobuf.InvalidProtocolBufferException;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import pb.ConnExt;
import pb.Push;

public class ProtoClientHandler extends SimpleChannelInboundHandler<ConnExt.Output> {
    private static final String TAG = "ProtoClientHandler";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ConnExt.Output msg) throws Exception {
        Log.d(TAG, "收到服务器消息 " + msg + " ");
        if (msg.getType() == ConnExt.PackageType.PT_MESSAGE) {
            ConnExt.Message message = ConnExt.Message.parseFrom(msg.getData());

            Log.i(TAG, "channelRead0: " + message);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message entity = new Message();
                    ConnExt.MessageItem item = message.getMessage();
                    if (item.getMessageType() == ConnExt.MessageType.MT_TEXT) {
                        Log.i(TAG, "run: 收到文字消息");
                        String text = ConnExt.Text.newBuilder().setTextBytes(item.getMessageContent()).build().getText();
                        entity.setMessage_content(text);
                        entity.setReceiver_id(item.getReceiverId());
                        entity.setMessage_type(item.getMessageTypeValue());
                        entity.setReceiver_type(item.getReceiverTypeValue());
                        entity.setSend_time(item.getSendTime());
                        entity.setSender_device_id((int) item.getSenderDeviceId());
                        entity.setSeq(item.getSeq());
                        entity.setStatus(item.getStatusValue());
                        entity.setSender_type(item.getSenderTypeValue());
                        entity.setSender_id(item.getSenderId());
                        DBUtils.getInstance().insertMessage(entity);

                        Friend friend = new Friend();
                        friend.setUser_id((long) entity.getSender_id());
                        DBUtils.getInstance().insertFriends(friend);
                        EventBus.getDefault().post(entity);

                        toastTips("收到用户" + item.getSenderId() + "消息");
                    } else if (item.getMessageType() == ConnExt.MessageType.MT_COMMAND) {
                        Log.i(TAG, "run: 收到指令消息");
                        try {
                            ConnExt.Command command = ConnExt.Command.parseFrom(item.getMessageContent());
                            Log.i(TAG, "run: 收到指令消息:"+command.getCode());

                            if (command.getCode() == Push.PushCode.PC_ADD_FRIEND.getNumber()) {
                                Push.AddFriendPush addFriendPush = Push.AddFriendPush.parseFrom(command.getData());
                                NewFriend newFriend = new NewFriend();
                                newFriend.setFriend_id(addFriendPush.getFriendId());
                                newFriend.setAvatar_url(addFriendPush.getAvatarUrl());
                                newFriend.setNewFriendStatus(0);
                                newFriend.setDescription(addFriendPush.getDescription());
                                newFriend.setNickname(addFriendPush.getNickname());
                                DBUtils.getInstance().insertNewFriends(newFriend);
                                EventBus.getDefault().post(newFriend);
                                toastTips(addFriendPush.getFriendId()+"请求添加好友");
                            }else if(command.getCode() == Push.PushCode.PC_AGREE_ADD_FRIEND.getNumber()){
                                Push.AgreeAddFriendPush agreeAddFriendPush = Push.AgreeAddFriendPush.parseFrom(command.getData());
                                Friend friend = new Friend();
                                friend.setUser_id(agreeAddFriendPush.getFriendId());
                                friend.setNickname(agreeAddFriendPush.getNickname());
                                DBUtils.getInstance().insertFriends(friend);
                                EventBus.getDefault().post(friend);
                                toastTips(agreeAddFriendPush.getFriendId()+"同意好友请求");
                            }
                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }).start();

        }
    }

    public void toastTips(String msg){
        Looper.prepare();
        try {
            Toast.makeText(App.app, msg, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("error", e.toString());
        }
        Looper.loop();
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

        Log.d(TAG, "与服务端连接成功：" + App.user.getToken() + "  " + App.user.getUserId() + "  ");


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
        new Thread(new Runnable() {
            @Override
            public void run() {
                NettyClient.getInstance().connect();
            }
        }).start();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        Log.d(TAG, "与服务端断开连接exce：" + ctx.toString() + " " + cause.getMessage());

    }
}