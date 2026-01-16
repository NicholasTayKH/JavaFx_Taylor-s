package org.example.assignment;

public class UserItem {
    private final String text;
    private final String profileImage;
    private final String username;  // ✅ 新增字段

    // 原本的构造函数（兼容旧代码）
    public UserItem(String text, String profileImage) {
        this.text = text;
        this.profileImage = profileImage;
        this.username = "";  // 没传 username 就是空
    }


    public UserItem(String text, String profileImage, String username) {
        this.text = text;
        this.profileImage = profileImage;
        this.username = username;
    }

    public String getText() {
        return text;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getUsername() {
        return username;
    }
}
