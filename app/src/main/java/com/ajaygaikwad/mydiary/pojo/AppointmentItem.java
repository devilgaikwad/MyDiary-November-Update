package com.ajaygaikwad.mydiary.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class AppointmentItem implements Parcelable {
	public String date;
	public String city;
	public String custNo;
	public String dealerName;
	public String propType;
	public String desc;
	public String photo;
	public String onlytime;

	public AppointmentItem(String date, String dealerName, String custNo, String propType, String desc, String city, String photo, String onlytime) {
		this.date = date;
		this.city = city;
		this.custNo = custNo;
		this.dealerName = dealerName;
		this.propType = propType;
		this.desc = desc;
		this.photo = photo;
		this.onlytime = onlytime;
	}


	protected AppointmentItem(Parcel in) {
		date = in.readString();
		city = in.readString();
		custNo = in.readString();
		dealerName = in.readString();
		propType = in.readString();
		desc = in.readString();
		photo = in.readString();
		onlytime = in.readString();
	}

	public static final Creator<AppointmentItem> CREATOR = new Creator<AppointmentItem>() {
		@Override
		public AppointmentItem createFromParcel(Parcel in) {
			return new AppointmentItem(in);
		}

		@Override
		public AppointmentItem[] newArray(int size) {
			return new AppointmentItem[size];
		}
	};

	public void setDate(String date){
		this.date = date;
	}

	public String getDate(){
		return date;
	}

	public void setCity(String city){
		this.city = city;
	}

	public String getCity(){
		return city;
	}

	public void setCustNo(String custNo){
		this.custNo = custNo;
	}

	public String getCustNo(){
		return custNo;
	}

	public void setDealerName(String dealerName){
		this.dealerName = dealerName;
	}

	public String getDealerName(){
		return dealerName;
	}

	public void setPropType(String propType){
		this.propType = propType;
	}

	public String getPropType(){
		return propType;
	}

	public void setDesc(String desc){
		this.desc = desc;
	}

	public String getDesc(){
		return desc;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getOnlytime() {
		return onlytime;
	}

	public void setOnlytime(String onlytime) {
		this.onlytime = onlytime;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeString(date);
		parcel.writeString(city);
		parcel.writeString(custNo);
		parcel.writeString(dealerName);
		parcel.writeString(propType);
		parcel.writeString(desc);
		parcel.writeString(photo);
		parcel.writeString(onlytime);
	}
}
