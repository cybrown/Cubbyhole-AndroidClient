package com.cubbyhole.android.parcelable;

import android.os.Parcel;
import android.os.Parcelable;

import com.cubbyhole.client.model.File;

public class ParcelableFile extends File implements Parcelable {

    public ParcelableFile() {

    }

    public ParcelableFile(File file) {
        setId(file.getId());
        setName(file.getName());
        setFolder(file.isFolder());
        setParent(file.getParent());
        setSize(file.getSize());
    }

    public ParcelableFile(Parcel in) {
        setId(in.readLong());
        setName(in.readString());
        setParent(in.readLong());
        setSize(in.readLong());
        boolean[] temp = new boolean[1];
        in.readBooleanArray(temp);
        setFolder(temp[0]);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(getId());
        dest.writeString(getName());
        dest.writeLong(getParent());
        dest.writeLong(getSize());
        dest.writeBooleanArray(new boolean[]{isFolder()});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ParcelableFile createFromParcel(Parcel in) {
            return new ParcelableFile(in);
        }

        public ParcelableFile[] newArray(int size) {
            return new ParcelableFile[size];
        }
    };
}
