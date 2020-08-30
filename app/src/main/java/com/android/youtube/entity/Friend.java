package com.android.youtube.entity;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;


// int64 user_id = 1; // 用户id
//         string phone_number = 2; // 电话号码
//         string nickname = 3; // 昵称
//         int32 sex = 4; // 性别
//         string avatar_url = 5; // 头像地址
//         string user_extra = 6; // 用户附加字段
//         string remarks = 7; // 备注
//         string extra = 8; // 附加字段
@Entity(indexes = {
        @Index(value = "user_id DESC", unique = true)
})
public class Friend {

    @Id
    private Long user_id;
    private String phone_number;
    private String nickname;
    private String avatar_url;
    private String user_extra;
    private String remarks;
    private String extra;
@Generated(hash = 1569697638)
public Friend(Long user_id, String phone_number, String nickname,
        String avatar_url, String user_extra, String remarks, String extra) {
    this.user_id = user_id;
    this.phone_number = phone_number;
    this.nickname = nickname;
    this.avatar_url = avatar_url;
    this.user_extra = user_extra;
    this.remarks = remarks;
    this.extra = extra;
}
@Generated(hash = 287143722)
public Friend() {
}
public Long getUser_id() {
    return this.user_id;
}
public void setUser_id(Long user_id) {
    this.user_id = user_id;
}
public String getPhone_number() {
    return this.phone_number;
}
public void setPhone_number(String phone_number) {
    this.phone_number = phone_number;
}
public String getNickname() {
    return this.nickname;
}
public void setNickname(String nickname) {
    this.nickname = nickname;
}
public String getAvatar_url() {
    return this.avatar_url;
}
public void setAvatar_url(String avatar_url) {
    this.avatar_url = avatar_url;
}
public String getUser_extra() {
    return this.user_extra;
}
public void setUser_extra(String user_extra) {
    this.user_extra = user_extra;
}
public String getRemarks() {
    return this.remarks;
}
public void setRemarks(String remarks) {
    this.remarks = remarks;
}
public String getExtra() {
    return this.extra;
}
public void setExtra(String extra) {
    this.extra = extra;
}

   
}
