package com.yyg365.interestbar.ui.multiimage.base;

import java.io.Serializable;

public class PicViewObject implements Serializable {

	public static final int GIF = 0x04;
	public static final int IMAGE = 0x01;

	private static final long serialVersionUID = 3368847168029932356L;

	public static final int FROM_CHATTING_MSG = 0;

	public static final int FROM_YUNPAN = 1;

	private Long picId; // 图片的唯一id

	private int picType; // 图片类型

	private String picPreViewUrl; // 图片预览url

	private String picUrl; // 图片url

	private int from; // 来源

	private String extData; // 扩增数据字段

	public int getFrom() {
		return from;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public String getExtData() {
		return extData;
	}

	public void setExtData(String extData) {
		this.extData = extData;
	}

	public String getPicPreViewUrl() {
		return picPreViewUrl;
	}

	public void setPicPreViewUrl(String picPreViewUrl) {
		this.picPreViewUrl = picPreViewUrl;
	}

	public Long getPicId() {
		return picId;
	}

	public void setPicId(Long picId) {
		this.picId = picId;
	}

	public int getPicType() {
		return picType;
	}

	public void setPicType(int picType) {
		this.picType = picType;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}
	
	public boolean isFromYunpan() {
		return from == FROM_YUNPAN;
	}

}
