package com.floyd.onebuy.biz.vo.json;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by floyd on 16-5-8.
 */
public class GoodsAddressVO implements Parcelable {
    public long id;
    public String linkName;//收货人
    public String mobile;//收货人手机号码
    public String province;
    public long provinceId;
    public String city;
    public long cityId;
    public String area;
    public long areaId;
    public String detailAdr;
    public int isDefault;
    public long userId;

    public GoodsAddressVO() {
    }

    public GoodsAddressVO(Parcel in) {
        id = in.readLong();
        linkName = in.readString();
        mobile = in.readString();
        province = in.readString();
        provinceId = in.readLong();
        city = in.readString();
        cityId = in.readLong();
        area = in.readString();
        areaId = in.readLong();
        detailAdr = in.readString();
        isDefault = in.readInt();
        userId = in.readLong();
    }

    public String getFullAddress() {
        StringBuilder detailStr = new StringBuilder();
        detailStr.append(this.province).append(" ").append(this.city).append(" ").append(this.area).append(" ").append(this.detailAdr);
        return detailStr.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(linkName);
        dest.writeString(mobile);
        dest.writeString(province);
        dest.writeLong(provinceId);
        dest.writeString(city);
        dest.writeLong(cityId);
        dest.writeString(area);
        dest.writeLong(areaId);
        dest.writeString(detailAdr);
        dest.writeInt(isDefault);
        dest.writeLong(userId);
    }

    public static final Parcelable.Creator<GoodsAddressVO> CREATOR = new Parcelable.Creator<GoodsAddressVO>() {
        public GoodsAddressVO createFromParcel(Parcel in) {
            return new GoodsAddressVO(in);
        }

        public GoodsAddressVO[] newArray(int size) {
            return new GoodsAddressVO[size];
        }
    };
}
