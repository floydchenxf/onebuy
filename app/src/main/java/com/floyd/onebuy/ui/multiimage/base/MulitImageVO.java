package com.floyd.onebuy.ui.multiimage.base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MulitImageVO implements Serializable {
	private static final long serialVersionUID = -3341635617943456631L;
	private int currentPage; // 当前页面
	private ArrayList<PicViewObject> picViewList = new ArrayList<PicViewObject>();// 多图片的列表

	public MulitImageVO(int currentPage, List<PicViewObject> picViewList) {
		this.currentPage = currentPage;
		this.picViewList.clear();
		this.picViewList.addAll(picViewList);
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public ArrayList<PicViewObject> getPicViewList() {
		return picViewList;
	}

	public void setPicViewList(ArrayList<PicViewObject> picViewList) {
		this.picViewList = picViewList;
	}

}
