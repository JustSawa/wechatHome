package com.wechat.bean;

/**
 */
public class ToMessage {
	//   avatar: "avatar.jpg"
//   ,id: "100001"
//   ,name: "贤心"
//   ,sign: "这些都是测试数据，实际使用请严格按照该格式返回"
//   ,type: "friend" //聊天类型，一般分friend和group两种，group即群聊
//   ,username: "贤心"
	private String avatar;
	private String id;
	private String name;
	private String sign;
	private String type;
	private String username;

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}