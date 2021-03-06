package tts.moudle.huanxinapi.bean;

import java.io.Serializable;

public class GroupBean implements Serializable {
	private String groupId;// 群Id 唯一标示
	private String groupName;// 群昵称
	private String groupDesc;// 群介绍

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupDesc() {
		return groupDesc;
	}

	public void setGroupDesc(String groupDesc) {
		this.groupDesc = groupDesc;
	}

}
