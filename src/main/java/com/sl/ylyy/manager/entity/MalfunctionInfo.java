package com.sl.ylyy.manager.entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;
/**
 * 故障台账
 * @author 李旭日
 *
 */
public class MalfunctionInfo implements Serializable{

	private int id;  //id
	private String location;  //故障位置
	private UserInfo fixUserInfo;  //维修人
	private int status;  //故障状态（1：未指派；2：已指派；3：已接受；4：维修失败；5：维修成功；6：已通过审核）
	private int type;  //故障类型（1：水；2：电；3：综合）
	private String type_name;  //故障类型（1：水；2：电；3：综合）
	private int source;  //故障来源（1：员工巡检发现；2：通过故障模块上报）
	private String question;  //问题
	private UserInfo createUserInfo;  //创建人
	private String text_desc;  //文字描述（储存文字描述id，多个以英文逗号隔开）
	private String image_desc;  //图片描述（储存图片描述id，多个以英文逗号隔开）
	private String audio_desc;  //语音描述（储存语音描述id，多个以英文逗号隔开）
	private long create_at;  //创建时间
	private long appoint_at;  //当前指派时间
	private long accept_at;  //当前接受时间
	private long fix_at;  //当前维修时间
	private long check_at;  //当前审核时间
	
	private String create_at1;  //创建日期
	
	
	private List<Image_Desc> imageList;  //图片描述
	private List<Audio_Desc> audioList;  //语音描述
	private List<Text_Desc> textList;  //文字描述
	
	
	
	
	public List<Image_Desc> getImageList() {
		return imageList;
	}
	public void setImageList(List<Image_Desc> imageList) {
		this.imageList = imageList;
	}
	public List<Audio_Desc> getAudioList() {
		return audioList;
	}
	public void setAudioList(List<Audio_Desc> audioList) {
		this.audioList = audioList;
	}
	public List<Text_Desc> getTextList() {
		return textList;
	}
	public void setTextList(List<Text_Desc> textList) {
		this.textList = textList;
	}
	private List<MaterialInfo> materialList;  //物资
	private List<SupportInfo> supportInfoList;  //支援
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public UserInfo getFixUserInfo() {
		return fixUserInfo;
	}
	public void setFixUserInfo(UserInfo fixUserInfo) {
		this.fixUserInfo = fixUserInfo;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getSource() {
		return source;
	}
	public void setSource(int source) {
		this.source = source;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public UserInfo getCreateUserInfo() {
		return createUserInfo;
	}
	public void setCreateUserInfo(UserInfo createUserInfo) {
		this.createUserInfo = createUserInfo;
	}
	public String getText_desc() {
		return text_desc;
	}
	public void setText_desc(String text_desc) {
		this.text_desc = text_desc;
	}
	public String getImage_desc() {
		return image_desc;
	}
	public void setImage_desc(String image_desc) {
		this.image_desc = image_desc;
	}
	public String getAudio_desc() {
		return audio_desc;
	}
	public void setAudio_desc(String audio_desc) {
		this.audio_desc = audio_desc;
	}
	public long getCreate_at() {
		return create_at;
	}
	public void setCreate_at(long create_at) {
		this.create_at = create_at;
	}
	public long getAppoint_at() {
		return appoint_at;
	}
	public void setAppoint_at(long appoint_at) {
		this.appoint_at = appoint_at;
	}
	
	public long getAccept_at() {
		return accept_at;
	}
	public void setAccept_at(long accept_at) {
		this.accept_at = accept_at;
	}
	public long getFix_at() {
		return fix_at;
	}
	public void setFix_at(long fix_at) {
		this.fix_at = fix_at;
	}
	public long getCheck_at() {
		return check_at;
	}
	public void setCheck_at(long check_at) {
		this.check_at = check_at;
	}
	public String getCreate_at1() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   //把时间转换成年月日
		return sdf.format(create_at*1000);
	}
	public void setCreate_at1(String create_at1) {
		this.create_at1 = create_at1;
	}
	public List<MaterialInfo> getMaterialList() {
		return materialList;
	}
	public void setMaterialList(List<MaterialInfo> materialList) {
		this.materialList = materialList;
	}
	public List<SupportInfo> getSupportInfoList() {
		return supportInfoList;
	}
	public void setSupportInfoList(List<SupportInfo> supportInfoList) {
		this.supportInfoList = supportInfoList;
	}

	public String getType_name() {
		return type_name;
	}

	public void setType_name(String type_name) {
		this.type_name = type_name;
	}
}
