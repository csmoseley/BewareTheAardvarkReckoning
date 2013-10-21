package com.ITCS4180.photogallery;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class ImageData implements Parcelable {
	String url;
	String title;
	int views;

	public ImageData(String atitle, String aurl, int theviews) {
		this.views = theviews;
		this.url = aurl;
		this.title = atitle;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getViews() {
		return views;
	}

	public void setViews(int views) {
		this.views = views;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) { 
		// Note to self(chris), read up on Parcelables
		out.writeString(getTitle());
		out.writeString(getUrl());
		out.writeInt(getViews());
	}

	public static final Parcelable.Creator<ImageData> CREATOR = new Parcelable.Creator<ImageData>() {
		public ImageData createFromParcel(Parcel in) {
			return new ImageData(in);
		}

		public ImageData[] newArray(int size) {
			Log.i("demo", "Creating ImageData Array");
			return new ImageData[size];
		}
	};

	private ImageData(Parcel in) {
		setTitle(in.readString());
		setUrl(in.readString());
		setViews(in.readInt());
	}

}