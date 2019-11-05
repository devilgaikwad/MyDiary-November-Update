package com.ajaygaikwad.mydiary.pojo;

public class DetailsItem{
	public String srno;
	public String date;
	public String customerMobile;
	public String city;
	public String dealerName;
	public String description;
	public String propType;
	public String photo;
	public String onlytime;


	public DetailsItem(String srno,String date, String customerMobile, String city, String dealerName,
                       String description,  String propType, String photo, String onlytime) {
		this.srno = srno;
		this.date = date;
		this.customerMobile = customerMobile;
		this.city = city;
		this.dealerName = dealerName;
		this.description = description;
		this.propType = propType;
		this.photo = photo;
		this.onlytime = onlytime;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public void setSrno(String srno){
		this.srno = srno;
	}

	public String getSrno(){
		return srno;
	}

	public void setDate(String date){
		this.date = date;
	}

	public String getDate(){
		return date;
	}

	public void setCustomerMobile(String customerMobile){
		this.customerMobile = customerMobile;
	}

	public String getCustomerMobile(){
		return customerMobile;
	}

	public void setCity(String city){
		this.city = city;
	}

	public String getCity(){
		return city;
	}

	public void setDealerName(String dealerName){
		this.dealerName = dealerName;
	}

	public String getDealerName(){
		return dealerName;
	}

	public void setDescription(String description){
		this.description = description;
	}

	public String getDescription(){
		return description;
	}

	public void setPropType(String propType){
		this.propType = propType;
	}

	public String getPropType(){
		return propType;
	}

    public String getOnlytime() {
        return onlytime;
    }

    public void setOnlytime(String onlytime) {
        this.onlytime = onlytime;
    }
}
