package com.example.app3_android;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Broadcast implements Parcelable {
    private int downloadedBytes;
    private int fileSize;
    private String status;
    private int progress;
    public Broadcast(int downloadedBytes, int fileSize, String status, int progress){
        this.downloadedBytes = downloadedBytes;
        this.fileSize = fileSize;
        this.status = status;
        this.progress = progress;
    }
    public Broadcast(Parcel parcel){
        this.downloadedBytes = parcel.readInt();
        this.fileSize = parcel.readInt();
        this.status = parcel.readString();
        this.progress = parcel.readInt();
    }
    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(downloadedBytes);
        parcel.writeInt(fileSize);
        parcel.writeString(status);
        parcel.writeInt(progress);
    }
    public static final Parcelable.Creator<Broadcast> CREATOR = new Parcelable.Creator<Broadcast>(){
        @Override
        public Broadcast createFromParcel(Parcel parcel){
            return new Broadcast(parcel);
        }
        @Override
        public Broadcast[] newArray(int size){
            return new Broadcast[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }
    public int getDownloadedBytes() {
        return downloadedBytes;
    }
    public int getProgress() {
        return progress;
    }
}
