package com.android.youtube.entity;

//message {
//        message_content: "xxxx"
//        message_type: MT_TEXT
//        message_type_value: 1
//        receiver_id: 3
//        receiver_type: RT_USER
//        receiver_type_value: 1
//        request_id: 1598532063071
//        send_time: 1598532062963
//        sender_device_id: 8
//        sender_id: 4
//        sender_type: ST_USER
//        sender_type_value: 2
//        seq: 27
//        status: MS_NORMAL
//        status_value: 1
//        }

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Generated;


//enum MessageType {
//    MT_UNKNOWN = 0; // 未知
//    MT_TEXT = 1; // 文本
//    MT_FACE = 2; // 表情
//    MT_VOICE = 3; // 语音消息
//    MT_IMAGE = 4; // 图片
//    MT_FILE = 5; // 文件
//    MT_LOCATION = 6; // 地理位置
//    MT_COMMAND = 7; // 指令推送
//    MT_CUSTOM = 8; // 自定义
//}

//enum SenderType {
//    ST_UNKNOWN = 0; // 未知的
//    ST_SYSTEM = 1; // IM系统
//    ST_USER = 2; // 用户
//    ST_BUSINESS = 3; // 业务方
//}

//enum ReceiverType {
//    RT_UNKNOWN = 0; // 未知
//    RT_USER = 1; // 用户
//    RT_NORMAL_GROUP = 2; // 普通群组
//    RT_LARGE_GROUP = 3; // 大群组
//}

//enum MessageStatus {
//    MS_UNKNOWN = 0; // 未知的
//    MS_NORMAL = 1; // 正常的
//    MS_RECALL = 2; // 撤回
//}

@Entity(indexes = {
        @Index(value = "id DESC", unique = true)
})
public class Message {
    @Id
    private Long id;
    private String message_content;
    private int message_type;
    private String receiver_id;
    private int receiver_type;
    private String request_id;
    private String send_time;
    private String sender_device_id;
    private int sender_type;
    private String seq;
    private int status;
@Generated(hash = 461733162)
public Message(Long id, String message_content, int message_type,
        String receiver_id, int receiver_type, String request_id,
        String send_time, String sender_device_id, int sender_type, String seq,
        int status) {
    this.id = id;
    this.message_content = message_content;
    this.message_type = message_type;
    this.receiver_id = receiver_id;
    this.receiver_type = receiver_type;
    this.request_id = request_id;
    this.send_time = send_time;
    this.sender_device_id = sender_device_id;
    this.sender_type = sender_type;
    this.seq = seq;
    this.status = status;
}
@Generated(hash = 637306882)
public Message() {
}
public Long getId() {
    return this.id;
}
public void setId(Long id) {
    this.id = id;
}
public String getMessage_content() {
    return this.message_content;
}
public void setMessage_content(String message_content) {
    this.message_content = message_content;
}
public int getMessage_type() {
    return this.message_type;
}
public void setMessage_type(int message_type) {
    this.message_type = message_type;
}
public String getReceiver_id() {
    return this.receiver_id;
}
public void setReceiver_id(String receiver_id) {
    this.receiver_id = receiver_id;
}
public int getReceiver_type() {
    return this.receiver_type;
}
public void setReceiver_type(int receiver_type) {
    this.receiver_type = receiver_type;
}
public String getRequest_id() {
    return this.request_id;
}
public void setRequest_id(String request_id) {
    this.request_id = request_id;
}
public String getSend_time() {
    return this.send_time;
}
public void setSend_time(String send_time) {
    this.send_time = send_time;
}
public String getSender_device_id() {
    return this.sender_device_id;
}
public void setSender_device_id(String sender_device_id) {
    this.sender_device_id = sender_device_id;
}
public int getSender_type() {
    return this.sender_type;
}
public void setSender_type(int sender_type) {
    this.sender_type = sender_type;
}
public String getSeq() {
    return this.seq;
}
public void setSeq(String seq) {
    this.seq = seq;
}
public int getStatus() {
    return this.status;
}
public void setStatus(int status) {
    this.status = status;
}
}
