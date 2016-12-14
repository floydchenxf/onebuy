package com.yyg365.interestbar.biz.vo;

import android.os.Parcel;
import android.os.Parcelable;

import com.yyg365.interestbar.utils.CommonUtil;

/**
 * Created by floyd on 15-12-3.
 */
public class AdvVO implements Parcelable, IKeepClassForProguard {
    public long id;
    public String title;
    public String imgUrl;
    public long createTime;
    public long updateTime;
    public int type;

    public AdvVO() {
    }

    public AdvVO(Parcel in) {
        id = in.readLong();
        title = in.readString();
        imgUrl = in.readString();
        createTime = in.readLong();
        updateTime = in.readLong();
        type = in.readInt();
    }

    public String getPreviewUrl() {
        return CommonUtil.getImage_400(this.imgUrl);
    }

    public String getImgUrl() {
        return CommonUtil.getImage_800(this.imgUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(imgUrl);
        dest.writeLong(createTime);
        dest.writeLong(updateTime);
        dest.writeInt(type);
    }

    public static final Creator<AdvVO> CREATOR = new Creator<AdvVO>() {
        public AdvVO createFromParcel(Parcel in) {
            return new AdvVO(in);
        }

        public AdvVO[] newArray(int size) {
            return new AdvVO[size];
        }
    };
}
