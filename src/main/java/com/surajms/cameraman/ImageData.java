package com.surajms.cameraman;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by surajshirvankar on 6/11/16.
 */
public class ImageData implements Parcelable {
    public String getOriginal() {
        return original;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    private String original;
    private String thumbnail;

    public ImageData(String original, String thumbnail) {
        this.original = original;
        this.thumbnail = thumbnail;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.original);
        dest.writeString(this.thumbnail);
    }

    protected ImageData(Parcel in) {
        this.original = in.readString();
        this.thumbnail = in.readString();
    }

    public static final Parcelable.Creator<ImageData> CREATOR = new Parcelable.Creator<ImageData>() {
        @Override
        public ImageData createFromParcel(Parcel source) {
            return new ImageData(source);
        }

        @Override
        public ImageData[] newArray(int size) {
            return new ImageData[size];
        }
    };
}