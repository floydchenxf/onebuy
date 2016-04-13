package com.floyd.onebuy.ui.multiimage.common;

import java.util.List;

/**
 * 一个目录的相册对象
 * 
 * 
 */
public class ImageBucket {
	private int count = 0;
	private String bucketName;
	private List<ImageItem> imageList;
	private String bucketId;

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public List<ImageItem> getImageList() {
		return imageList;
	}

	public void setImageList(List<ImageItem> imageList) {
		this.imageList = imageList;
	}

	public String getBucketId() {
		return bucketId;
	}

	public void setBucketId(String bucketId) {
		this.bucketId = bucketId;
	}

}
