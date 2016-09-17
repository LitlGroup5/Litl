package com.litlgroup.litl.models;

/**
 * Created by monusurana on 9/17/16.
 */
import android.os.Parcel;
import android.os.Parcelable;

public class BottomSheetItem implements Parcelable {

    private int image;
    private String text;

    public BottomSheetItem(int image, String text) {
        this.image = image;
        this.text = text;
    }

    public int getImage() {
        return (this.image);
    }

    public String getText() {
        return text;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.image);
        dest.writeString(this.text);
    }

    protected BottomSheetItem(Parcel in) {
        this.image = in.readInt();
        this.text = in.readString();
    }

    public static final Creator CREATOR = new Creator() {
        public BottomSheetItem createFromParcel(Parcel source) {
            return new BottomSheetItem(source);
        }

        public BottomSheetItem[] newArray(int size) {
            return new BottomSheetItem[size];
        }
    };
}
