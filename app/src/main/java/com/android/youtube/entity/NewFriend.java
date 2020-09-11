package com.android.youtube.entity;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Generated;


//  int64 friend_id = 1; // 好友id
//          string nickname = 2; // 昵称
//          string avatar_url = 3; // 头像
//          string description = 4; // 描述
@Entity(indexes = {
        @Index(value = "friend_id DESC", unique = true)
})
public class NewFriend {

    @Id
    private Long friend_id;
    private String nickname;
    private String avatar_url;
    private String description;
    private int newFriendStatus;
@Generated(hash = 1854630625)
public NewFriend(Long friend_id, String nickname, String avatar_url,
        String description, int newFriendStatus) {
    this.friend_id = friend_id;
    this.nickname = nickname;
    this.avatar_url = avatar_url;
    this.description = description;
    this.newFriendStatus = newFriendStatus;
}
@Generated(hash = 163516704)
public NewFriend() {
}
public Long getFriend_id() {
    return this.friend_id;
}
public void setFriend_id(Long friend_id) {
    this.friend_id = friend_id;
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
public String getDescription() {
    return this.description;
}
public void setDescription(String description) {
    this.description = description;
}
public int getNewFriendStatus() {
    return this.newFriendStatus;
}
public void setNewFriendStatus(int newFriendStatus) {
    this.newFriendStatus = newFriendStatus;
}
    
}
